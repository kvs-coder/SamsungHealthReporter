# 2. Architecture Constraints

## 2.1 Technical Constraints

| Constraint | Consequence |
| :--- | :--- |
| Samsung Health Data SDK 1.1.0 (`com.samsung.android.sdk.health.data`) | The only supported way to Samsung Health; suspend API; data types and fields are fixed by Samsung. |
| Android 10 (API 29)+, Samsung Health app 6.30.2+, Java 17 | `minSdk 29`; the Samsung Health app must be installed on Samsung and non-Samsung phones alike. |
| No emulator support | The SDK refuses emulators; end-to-end checks need a physical phone (verified on a Pixel 7a). |
| The SDK AAR is login-gated and may not be redistributed | It is `compileOnly`, git-ignored and never published; consumers add it themselves; CI fetches it from a private repository; without it the build uses `samsung-health-data-stub` (ADR 0003). |
| The SDK needs `kotlin-parcelize` runtime and Gson at runtime | Consumer apps apply the parcelize plugin and add Gson; library tests add both. |
| SDK write request builders parcel data points | Writer unit tests run under Robolectric. |
| Kotlin 2.2, AGP 8.13, Gradle 8.14, `explicitApi()` | Every public declaration states its visibility and has KDoc. |

## 2.2 Organizational Constraints

| Constraint | Consequence |
| :--- | :--- |
| **Partner approval** | Developer mode grants reads only. Writes fail with SDK error 2003 (`ERR_ACCESS_CONTROL`) until Samsung issues an access code through a partnership; distribution also needs approval. |
| Single maintainer, open source (MIT) | Automation over process: CI, release-please, lint gates. |
| Distribution through JitPack | Tags `X.Y.Z` (no `v`); JitPack builds the library without the SDK AAR, i.e. against the stub. |

## 2.3 Conventions

* Engineering rules, prohibitions and the commit format live in [`AGENTS.md`](../../AGENTS.md) (symlinked as
  `CLAUDE.md`); `CONTRIBUTING.md` is the short version.
* Kotlin coding conventions, ktlint (`ktlint_official`, 120 columns), detekt (`detekt.yml`), Android lint.
* Conventional Commits drive versions and `CHANGELOG.md` (release-please); breaking public API or JSON contract
  changes are major releases.
* Architecture decisions are recorded as ADRs (Nygard) in `docs/adr/`.
