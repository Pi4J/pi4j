#!/usr/bin/env python3
"""Builds the Groma architecture comparison comment for a pull request.

Reads the comparison.json summary and the snapshot.js written next to it by the
groma.md action, and prints a Markdown comment with the change counts, a Mermaid
diagram of the changed components (rendered by GitHub, so no image hosting is
needed) and a table of the changed components with their files.

Usage: groma-comparison-comment.py <comparison.json> <run-url>
"""

import json
import re
import sys
from pathlib import Path

MARKER = "<!-- groma-comparison -->"
# Beyond this many nodes or edges the diagram is no longer readable, so only the table is shown.
MAX_DIAGRAM_NODES = 60
MAX_DIAGRAM_EDGES = 80
# GitHub rejects comments over 65,536 characters; keep a margin for the marker and links.
MAX_COMMENT_CHARS = 60_000
CHANGED = ("added", "modified", "removed")
STATUS_LABEL = {"added": "🟢 added", "modified": "🟡 modified", "removed": "🔴 removed"}
# Colours that stay readable in both GitHub light and dark mode.
CLASS_DEFS = {
    "added": "fill:#dafbe1,stroke:#1a7f37,color:#1f2328",
    "modified": "fill:#fff8c5,stroke:#9a6700,color:#1f2328",
    "removed": "fill:#ffebe9,stroke:#cf222e,color:#1f2328,stroke-dasharray:4 3",
    "context": "fill:#f6f8fa,stroke:#8c959f,color:#57606a",
}
EDGE_COLOURS = {"added": "#1a7f37", "modified": "#9a6700", "removed": "#cf222e"}


def load_snapshot(path):
    """Extracts the JSON payload from the groma:published event in snapshot.js."""
    text = path.read_text(encoding="utf-8")
    start = text.index("detail: ") + len("detail: ")
    payload, _ = json.JSONDecoder().raw_decode(text[start:])
    return payload


def status_of(value):
    """Comparison entries are either a status string or an object with a status."""
    return value if isinstance(value, str) else value.get("status", "unchanged")


def label(text):
    """Makes text safe inside a quoted Mermaid label."""
    text = re.sub(r"[\r\n`]", " ", str(text))
    return text.replace("&", "#amp;").replace('"', "#quot;").replace("<", "#lt;").replace(">", "#gt;").replace("|", "#124;")


def cell(text):
    """Makes text safe inside a Markdown table cell, without triggering mentions."""
    text = re.sub(r"[\r\n]", " ", str(text))
    text = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    return text.replace("|", "\\|").replace("@", "@\u200b")


class Diagram:
    def __init__(self, elements, zones):
        self.elements = elements
        self.zones = zones
        self.node_ids = {}
        self.nodes = {}  # element id -> class
        self.edges = []  # (source, target, status, description)

    def node_id(self, element_id):
        if element_id not in self.node_ids:
            self.node_ids[element_id] = f"n{len(self.node_ids)}_{re.sub(r'[^A-Za-z0-9_]', '_', element_id)}"
        return self.node_ids[element_id]

    def add_node(self, element_id, css_class):
        # a changed status wins over the context class of a relationship endpoint
        if self.nodes.get(element_id) not in CHANGED:
            self.nodes[element_id] = css_class

    def container_of(self, element_id):
        """Returns the chain of (key, title) groups the element is drawn in, outermost first."""
        element = self.elements.get(element_id, {})
        chain = []
        parent = element.get("parent")
        while parent and parent in self.elements and self.elements[parent].get("kind") != "system":
            chain.insert(0, (parent, self.elements[parent].get("title", parent)))
            parent = self.elements[parent].get("parent")
        zone = self.zones.get(element_id)
        if zone:
            chain.append((zone["key"], zone["name"]))
        return tuple(chain)

    def render(self):
        tree = {}
        for element_id in self.nodes:
            branch = tree
            for group in self.container_of(element_id):
                branch = branch.setdefault(group, {})
            branch.setdefault(None, []).append(element_id)

        # top to bottom places unconnected groups side by side; within a group, invisible
        # links stack the components so the diagram stays readable at comment width
        lines = ["```mermaid", "flowchart TB"]
        invisible_links = self._render_branch(tree, lines, 1)
        for source, target, status, description in self.edges:
            arrow = "-.->" if status == "removed" else "-->"
            text = f'|"{label(description)}"|' if description else ""
            lines.append(f"  {self.node_id(source)} {arrow}{text} {self.node_id(target)}")
        for css_class, style in CLASS_DEFS.items():
            lines.append(f"  classDef {css_class} {style}")
        for index, (_, _, status, _) in enumerate(self.edges, start=invisible_links):
            lines.append(f"  linkStyle {index} stroke:{EDGE_COLOURS[status]},stroke-width:2px")
        lines.append("```")
        return "\n".join(lines)

    def _render_branch(self, branch, lines, depth):
        """Renders the nodes and nested groups of a branch; returns the number of invisible links."""
        indent = "  " * depth
        element_ids = sorted(branch.get(None, []), key=lambda e: self.title(e).lower())
        for element_id in element_ids:
            lines.append(f'{indent}{self.node_id(element_id)}["{label(self.title(element_id))}"]:::{self.nodes[element_id]}')
        if len(element_ids) > 1:
            lines.append(indent + " ~~~ ".join(self.node_id(e) for e in element_ids))
        invisible_links = max(len(element_ids) - 1, 0)
        for group in sorted((g for g in branch if g is not None), key=lambda g: g[1].lower()):
            key, title = group
            lines.append(f'{indent}subgraph {self.node_id("group:" + key)}["{label(title)}"]')
            invisible_links += self._render_branch(branch[group], lines, depth + 1)
            lines.append(f"{indent}end")
        return invisible_links

    def title(self, element_id):
        return self.elements.get(element_id, {}).get("title", element_id)


def main():
    summary_path = Path(sys.argv[1])
    run_url = sys.argv[2]
    summary = json.loads(summary_path.read_text(encoding="utf-8"))
    components, relationships = summary["components"], summary["relationships"]

    counts = [
        MARKER,
        "### Groma architecture comparison",
        "",
        "| Changes | Added | Modified | Removed |",
        "| --- | ---: | ---: | ---: |",
        f"| Components | {components['added']} | {components['modified']} | {components['removed']} |",
        f"| Relationships | {relationships['added']} | {relationships['modified']} | {relationships['removed']} |",
        "",
    ]
    download = [
        f"[Download the full before/after architecture map]({run_url}) from this run's artifacts (kept 14 days). "
        "The current map is also published at https://www.pi4j.com/groma/."
    ]

    diagram, table = [], []
    snapshot_path = summary_path.with_name("snapshot.js")
    try:
        snapshot = load_snapshot(snapshot_path)
        diagram, table = describe_changes(snapshot)
    except (OSError, ValueError, KeyError) as e:
        # the counts above are still useful, so never fail the comment on the details
        print(f"Cannot read changes from {snapshot_path}: {e}", file=sys.stderr)

    # drop the diagram, then the table, until the comment fits within GitHub's limit
    too_long = ["_The list of changes is too long for a comment; download the map below._", ""]
    for body in (counts + diagram + table + download, counts + table + download, counts + too_long + download):
        text = "\n".join(body)
        if len(text) <= MAX_COMMENT_CHARS:
            break
    print(text)


def describe_changes(snapshot):
    comparison = snapshot["comparison"]
    world = snapshot["world"]
    elements = {e["id"]: e for e in world["elements"]}
    zones = {}
    for zone in snapshot.get("sheet", {}).get("zones", []):
        for member in zone.get("members", []):
            zones[member] = zone

    changed = {}
    for element_id, entry in comparison["components"].items():
        status = status_of(entry)
        if status not in CHANGED:
            continue
        changed[element_id] = (status, entry if isinstance(entry, dict) else {})
        # removed components are only described by their state before the change
        if element_id not in elements and isinstance(entry, dict) and entry.get("before"):
            elements[element_id] = entry["before"]

    relationships = {r["id"]: r for r in world.get("relationships", [])}
    changed_relationships = []
    for relationship_id, entry in comparison.get("relationships", {}).items():
        status = status_of(entry)
        if status not in CHANGED:
            continue
        relationship = relationships.get(relationship_id)
        if relationship is None and isinstance(entry, dict):
            relationship = entry.get("after") or entry.get("before")
        if relationship and relationship.get("source") and relationship.get("target"):
            changed_relationships.append((relationship, status))

    if not changed and not changed_relationships:
        return ["No components or relationships changed.", ""], []

    out = []
    diagram = Diagram(elements, zones)
    for element_id, (status, _) in changed.items():
        diagram.add_node(element_id, status)
    for relationship, status in changed_relationships:
        diagram.add_node(relationship["source"], "context")
        diagram.add_node(relationship["target"], "context")
        diagram.edges.append((relationship["source"], relationship["target"], status, relationship.get("description", "")))

    if len(diagram.nodes) <= MAX_DIAGRAM_NODES and len(diagram.edges) <= MAX_DIAGRAM_EDGES:
        out += [diagram.render(), ""]
        out += ["<sub>🟢 added · 🟡 modified · 🔴 removed · grey: unchanged component with a changed relationship</sub>", ""]
    else:
        out += [f"_{len(diagram.nodes)} components and {len(diagram.edges)} relationships are involved, "
                "too many to draw; see the table below._", ""]

    table = []
    if changed:
        rows = []
        for element_id, (status, entry) in sorted(changed.items(), key=lambda item: diagram.title(item[0]).lower()):
            groups = " › ".join(title for _, title in diagram.container_of(element_id)) or "—"
            files = entry.get("files", [])
            additions = sum(f.get("additions", 0) for f in files)
            deletions = sum(f.get("deletions", 0) for f in files)
            names = "<br>".join(cell(f["file"].rsplit("/", 1)[-1]) for f in files) or "—"
            rows.append(f"| {cell(diagram.title(element_id))} | {cell(groups)} | {STATUS_LABEL[status]} "
                        f"| {names} | +{additions} −{deletions} |")
        table = [
            "<details><summary>Changed components</summary>",
            "",
            "| Component | Location | Change | Files | Lines |",
            "| --- | --- | --- | --- | ---: |",
            *rows,
            "",
            "</details>",
            "",
        ]
    return out, table


if __name__ == "__main__":
    main()
