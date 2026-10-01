# CI Secrets & Token Maintenance

This file exists because a silently-expired GitHub token broke snapshot
publishing from 2026-07-02 until 2026-10-01 without anyone noticing — every
CI run reported green the whole time. See the incident note at the bottom
before you assume "the workflow is green" means "the thing actually happened."

## Secrets used by this repo's workflows

| Secret | Used in | Purpose | Type |
|---|---|---|---|
| `OSSRH_USERNAME` / `OSSRH_TOKEN` | `ci-main-snapshot.yml` | Deploy snapshot artifacts to the Sonatype Central snapshots repository (`sonatype-oss-snapshots` server id) | Org secret (Pi4J org settings → Secrets and variables → Actions) |
| `API_TOKEN_GITHUB` | `ci-main-snapshot.yml` (`DeployPackages` job) | Push the built `.zip`/`.deb` bundle to [Pi4J/download](https://github.com/Pi4J/download) | Org secret, must have **write access to `Pi4J/download`** |

`Pi4J/download` itself (not this repo) also has `PI4J_BOT_GITHUB_PAT`, used
by its own `rebuild-repo.yml` to trigger that repo's `dynamic-readme.yml`
via `workflow_dispatch`. It's a separate token from `API_TOKEN_GITHUB` and
can expire independently — if the download page's dates look stale even
after `API_TOKEN_GITHUB` works, check that one too (in `Pi4J/download`'s
own secrets, not here).

## How to regenerate `API_TOKEN_GITHUB`

1. Create a **fine-grained PAT**: https://github.com/settings/tokens?type=beta
   - Resource owner: `Pi4J`
   - Repository access: only `Pi4J/download`
   - Permissions: Repository → **Contents: Read and write**
   - Set an expiration, and **note the expiry date below** when you do this.
2. Update the org secret: https://github.com/organizations/Pi4J/settings/secrets/actions
   → `API_TOKEN_GITHUB` → Update → paste the new token.
3. Trigger a snapshot build (push to `main`, or re-run
   `ci-main-snapshot.yml` manually) and confirm `Pi4J/download` actually
   receives a new commit — don't just trust the job going green (see
   incident note below for why).

## Expiry log

Keep this updated every time the token is rotated, so the next person
knows whether it's about to lapse instead of finding out from a silent
failure again.

| Token | Rotated on | Expires | Rotated by |
|---|---|---|---|
| `API_TOKEN_GITHUB` | 2026-10-01 | 2027-09-30 | @fdelporte |

## Incident note (2026-07-02 → 2026-10-01)

`API_TOKEN_GITHUB` expired/became invalid around 2026-07-02. The
`DeployPackages` job's push-retry loop (`git push origin main && break`)
didn't check whether all 3 retries had actually failed — when they did, the
script fell through to the loop's last command (`git pull --rebase`, which
exits 0 even when there's nothing to rebase), so the step and the whole job
kept reporting **success** for three months while silently pushing nothing.
This was fixed in
[#772](https://github.com/Pi4J/pi4j/pull/772) — the job now exits non-zero
if all push attempts fail. The underlying token was also separately broken
in a way that had nothing to do with `pi4j-drivers`' CI failures that
surfaced it (see [#770](https://github.com/Pi4J/pi4j/pull/770) and
[#771](https://github.com/Pi4J/pi4j/pull/771) for that unrelated
`central-publishing-maven-plugin` issue, found while investigating this).
