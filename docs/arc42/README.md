# SamsungHealthReporter — Architecture Documentation (arc42)

This set describes the architecture of SamsungHealthReporter following the [arc42](https://arc42.org) template.
It is derived from the code in `library/`, `samsung-health-data-stub/`, `app/` and `.github/workflows/`, and from
running the Example app on a Pixel 7a with Samsung Health 7.00.6 (09.10.2026). Where the code and these pages
disagree, the code wins and the page is a bug.

| # | Chapter | Content |
| :--- | :--- | :--- |
| 1 | [Introduction and Goals](01-introduction-and-goals.md) | Purpose, quality goals, stakeholders |
| 2 | [Architecture Constraints](02-architecture-constraints.md) | Platform, SDK licensing, partner approval, conventions |
| 3 | [Context and Scope](03-context-and-scope.md) | Business and technical context |
| 4 | [Solution Strategy](04-solution-strategy.md) | The few decisions that shape everything else |
| 5 | [Building Block View](05-building-block-view.md) | Facade, services, types, payloads, decorators, SDK stub, Example app |
| 6 | [Runtime View](06-runtime-view.md) | Connect and authorize, read, aggregate, write, observe |
| 7 | [Deployment View](07-deployment-view.md) | JitPack distribution, consumer apps, CI and release |
| 8 | [Crosscutting Concepts](08-crosscutting-concepts.md) | Identity, time, units, serialization, errors, concurrency, testing |
| 9 | [Architecture Decisions](09-architecture-decisions.md) | Index of the ADRs in `docs/adr/` |
| 10 | [Quality Requirements](10-quality-requirements.md) | Quality tree and scenarios |
| 11 | [Risks and Technical Debt](11-risks-and-technical-debt.md) | Known risks and debt |
| 12 | [Glossary](12-glossary.md) | Ubiquitous language |

Diagrams are [Mermaid](https://mermaid.js.org) (flowcharts, sequence and class diagrams), which GitHub renders
inline.
