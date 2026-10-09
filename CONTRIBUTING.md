# Contributing

When contributing to this repository, please first discuss the change you wish to make via an issue
with the owners of this repository before making a change.

Please note we have a code of conduct, please follow it in all your interactions with the project.

The binding engineering rules are in [AGENTS.md](AGENTS.md); this page is the short version.

## Pull Request Process

1. Branch off a freshly pulled `master` as `<initials>/issue-<number>` and keep one issue per pull request.
2. Write the tests first: every payload round-trips through `json` and `make(from:)`, every writable payload
   through Samsung Health data points, and new service behavior gets a test against a mocked `HealthDataStore`.
3. Update `README.md` with a usage snippet for new public API, and add a demo row for it to the Example app.
4. Use a [Conventional Commit](https://www.conventionalcommits.org) title (`feat:`, `fix:`, `docs:`, …;
   `!` for breaking changes). release-please derives the version and the `CHANGELOG.md` entry from it,
   so don't bump versions or edit released changelog entries by hand.
5. You may merge the Pull Request once you have the sign-off of another developer, or if you do not
   have permission to do that, you may request the second reviewer to merge it for you.

## Samsung Health Data SDK

Samsung's license doesn't allow committing the SDK. Download `samsung-health-data-api.aar` (1.1.0 or later)
with a Samsung account and put it into `library/libs/` (git-ignored). Without it the build uses
`samsung-health-data-stub/`, written from the SDK's API reference; keep the stub's signatures identical to the
reference when you use a new SDK API, and check both builds: `./gradlew :library:testDebugUnitTest` with the AAR
and again with `-PsamsungHealthDataStub`.

## Kotlin

We follow the [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html) and the
[library authors' guidelines](https://kotlinlang.org/docs/api-guidelines-introduction.html). ktlint and detekt
enforce the style (`.editorconfig`, `detekt.yml`, lines ≤ 120 characters); every violation fails CI.

## Checks

CI runs these on every pull request; run them locally before pushing:

```bash
# Lint
./gradlew ktlintCheck detekt :library:lintDebug

# Unit tests with the coverage gate (minBound in library/build.gradle.kts)
./gradlew :library:testDebugUnitTest :library:koverVerify

# Public API check; after a deliberate API change run ./gradlew :library:apiDump and commit library/api
./gradlew :library:apiCheck

# Build the library and the Example app
./gradlew :library:assembleRelease :app:assembleDebug
```
