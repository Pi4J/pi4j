#!/usr/bin/env python3
"""
Generate sample JMH benchmark result files for local development / demo of the
static-jmh dashboard.

JMH (with `-rf json`) writes one JSON file per run containing an array of
benchmark records. The weekly CI job is expected to drop one such file per run
into this folder and append an entry to `manifest.json`.

This script fabricates a handful of historical runs with a believable trend so
the dashboard can be viewed without a real CI history. Re-running it is
idempotent: it rewrites the sample files and the manifest.

Usage:
    python3 generate-sample-data.py
"""

import json
import math
import random
from datetime import datetime, timedelta, timezone

random.seed(42)

# All Pi4J FFM benchmarks are @BenchmarkMode(AverageTime) @OutputTimeUnit(MILLISECONDS),
# so the score is "ms/op" and LOWER IS BETTER.
BENCHMARKS = {
    "com.pi4j.plugin.jmh.GPIOInputPerformanceTest.testFFMInputRoundTrip": 0.42,
    "com.pi4j.plugin.jmh.GPIOInputPerformanceTest.testFFMInputWithListenerRoundTrip": 0.61,
    "com.pi4j.plugin.jmh.GPIOInputPerformanceTest.testFFMOutputRoundTrip": 0.38,
    "com.pi4j.plugin.jmh.I2CSMBusPerformanceTest.testSMBusRoundTrip": 1.05,
    "com.pi4j.plugin.jmh.I2CSMBusPerformanceTest.testI2CDirectRoundTrip": 0.74,
    "com.pi4j.plugin.jmh.I2CSMBusPerformanceTest.testI2CFileRoundTrip": 0.91,
    "com.pi4j.plugin.jmh.PWMPerformanceTest.testPWMRoundTrip": 2.13,
    "com.pi4j.plugin.jmh.SPIPerformanceTest.testFFMWriteReadRoundTrip": 0.56,
}

# Per-run multiplicative trend applied to every benchmark. < 1.0 means the run
# got faster than the baseline. This produces a gentle improvement with one
# regression in the middle so all dashboard views have something to show.
RUN_TRENDS = [1.00, 0.97, 1.04, 0.95, 0.92, 0.90]

COMMITS = ["ab0acd4", "edecb07", "7888d2c", "9f68286", "923e4c6", "5f1c0aa"]


def make_record(benchmark: str, base: float, trend: float):
    """Build one JMH primaryMetric record for a benchmark."""
    score = base * trend * random.uniform(0.98, 1.02)
    error = score * random.uniform(0.02, 0.06)  # ~2-6% measurement error
    iterations = [round(score * random.uniform(0.95, 1.05), 4) for _ in range(5)]
    return {
        "jmhVersion": "1.37",
        "benchmark": benchmark,
        "mode": "avgt",
        "threads": 1,
        "forks": 1,
        "jvmArgs": [],
        "jdkVersion": "21.0.2",
        "vmName": "OpenJDK 64-Bit Server VM",
        "vmVersion": "21.0.2+13",
        "warmupIterations": 3,
        "warmupTime": "10 s",
        "measurementIterations": 5,
        "measurementTime": "10 s",
        "primaryMetric": {
            "score": round(score, 4),
            "scoreError": round(error, 4),
            "scoreConfidence": [round(score - error, 4), round(score + error, 4)],
            "scorePercentiles": {
                "0.0": round(min(iterations), 4),
                "50.0": round(score, 4),
                "100.0": round(max(iterations), 4),
            },
            "scoreUnit": "ms/op",
            "rawData": [iterations],
        },
        "secondaryMetrics": {},
    }


def main():
    # Weekly runs, oldest first. Newest run is "today" relative to the demo.
    first = datetime(2026, 5, 4, 2, 0, tzinfo=timezone.utc)
    manifest_runs = []

    for i, trend in enumerate(RUN_TRENDS):
        run_date = first + timedelta(weeks=i)
        records = [make_record(b, base, trend) for b, base in BENCHMARKS.items()]
        filename = f"jmh-{run_date.strftime('%Y-%m-%d')}.json"
        with open(filename, "w") as f:
            json.dump(records, f, indent=2)
        manifest_runs.append({
            "file": filename,
            "date": run_date.isoformat(),
            "commit": COMMITS[i],
            "label": f"Weekly run {run_date.strftime('%Y-%m-%d')}",
        })
        print(f"wrote {filename} ({len(records)} benchmarks)")

    manifest = {
        "title": "Pi4J FFM — JMH Performance Benchmarks",
        "generated": datetime.now(timezone.utc).isoformat(),
        "runs": manifest_runs,
    }
    with open("manifest.json", "w") as f:
        json.dump(manifest, f, indent=2)
    print(f"wrote manifest.json ({len(manifest_runs)} runs)")


if __name__ == "__main__":
    main()
