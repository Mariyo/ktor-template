# Agent conventions for this repo

- Hexagonal layering (`domain` / `application` / `adapter.http`) is enforced by
  `HexagonalArchitectureTest` (Konsist) — see `ARCHITECTURE.md` for the layer diagram.
- Comment only to explain _why_ a setup/config decision is the way it is (e.g. a gated flag,
  a chosen port, a fallback) — not what the next line does. Redundant comments are noise for
  humans and agents alike.
- `application.yaml` declares Ktor modules by fully-qualified name; never wire a module by
  calling it from code instead — auto-reload and `InfoBrowserTest` both depend on the YAML list.
- Dev-only flags (JDWP, `-Dio.ktor.development=true`) are gated behind the `-Pdev` Gradle
  property. Never make them unconditional on the `run` task; a plain `./gradlew run` must stay
  production-like.
- Test packages are `unit`, `arch`, `browser`. `test` excludes
  `com/example/browser/**`; browser tests only run via the `browserTest` task. Kover's
  `disabledForTestTasks("browserTest")` must stay in sync with that exclusion. Browser tests
  reach the app through `AbstractBrowserTest.url` (`BASE_URL` env var, else an embedded server
  booted from `application.yaml`) — don't start a server ad hoc in a test.
- Any architectural change (new/removed component, changed data/control flow, a new or
  reworked use case) must also update `ARCHITECTURE.md` — its diagrams are the source of truth.
- This repo is a template, not a product: a new/changed DevEx capability (dev loop, debugging,
  test tiers, quality gates, logging, packaging, etc.) must be documented in `README.md`; a
  new/changed HTTP route must be added to `src/main/resources/openapi/documentation.yaml`.
- Never run `git commit` on the user's behalf. Stage or prepare changes if useful, but leave
  the actual commit to the user.
- use Accepatance Test Driven Development when doing changes in `src`. Work iteratively,
  create test, verify it with human developer and after approval implement/refactor
  agains the test.
