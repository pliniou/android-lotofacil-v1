---
name: android-native-agent-pack
description: A comprehensive, professional Android native skill pack for Kotlin and Jetpack Compose projects. Includes specialist agents and workflows for architecture, data, UI, testing, build, release, security, performance, and observability.
---

# Android Native Agent Pack

This skill pack serves as the **Standard Operating Procedure (SOP)** for this Android repository. It is designed to ensure high-quality, maintainable, and scalable code.

## 🎯 Core Objectives
- **Consistency**: Enforce architectural patterns (Clean Architecture, MVVM) and coding standards.
- **Quality**: Prioritize testing, error handling, and observability in every change.
- **Efficiency**: Use specialized agents to handle specific domains effectively.

## 🚦 Routing & Execution
1.  **Start with `master`**: Use the `master` agent for complex tasks, uncertain scope, or when coordination across multiple domains is required. It will route you to the appropriate specialist.
2.  **Direct Specialist Access**: If the task is clearly within a specific domain (e.g., "create a new UI component"), invoke the specialist agent directly (e.g., `ui`).
3.  **Workflows**: For multi-step processes (e.g., "new feature", "release"), strictly follow the playbooks defined in `workflows.md`.
4.  **Templates**: Use the provided templates in `templates/` for creating new files (UseCases, ViewModels, Repositories, etc.) to maintain consistency.

## 🧠 Skill Specialists
| Skill | Description |
| :--- | :--- |
| **`master`** | **Orchestrator & Strategist.** Analyzes requests, breaks them down, and delegates to specialists. Use for general inquiries or complex, multi-domain tasks. |
| **`arch`** | **System Architect.** Defines boundaries, contracts, dependency injection (Hilt), UI state modeling, and error handling strategies. Ensures Clean Architecture compliance. |
| **`data`** | **Data Layer Specialist.** Manages repositories, data sources, network (Retrofit), persistence (Room/DataStore), caching, synchronization, and migrations. |
| **`ui`** | **UI/UX Engineer.** Specializes in Jetpack Compose, screens, reusable components, navigation, previews, animations, and accessibility (a11y). |
| **`android`** | **Platform Specialist.** Handles Android framework specifics: Lifecycle, Permissions, Intents, Services, BroadcastReceivers, WorkManager, and Manifest configurations. |
| **`kotlin`** | **Language Expert.** Focuses on Kotlin idioms, Coroutines/Flow best practices, refactoring for readability, type safety, and functional programming patterns. |
| **`design`** | **Design System Guardian.** Manages design tokens (colors, typography, shapes), Material 3 implementation, theme handling, and responsive layout guidelines. |
| **`test`** | **Quality Assurance.** defines testing strategy and implementation for Unit Tests (JUnit), Integration Tests, and UI Tests (Compose/Espresso). |
| **`build`** | **Build Engineer.** Manages Gradle configurations (kts), dependencies (Catalog), plugins, CI/CD pipelines, static analysis (Lint, Detekt), and modularization. |
| **`release`** | **Release Manager.** Handles app versioning, signing, obfuscation (R8/Proguard), App Bundle generation, and Google Play Store compliance. |
| **`security`** | **Security Analyst.** Focuses on data privacy, secure storage, encryption, API security, permission minimization, and threat modeling. |
| **`perf`** | **Performance Engineer.** Optimizes startup time, UI rendering (jank), memory usage, battery consumption, and network efficiency. |
| **`observe`** | **Observability Engineer.** Implements logging (Timber), crash reporting, remote config, analytics, and performance tracing. |

## 📝 Output Guidelines
- **Actionable Code**: Deliver complete, compilable code snippets. Avoid placeholders unless absolutely necessary.
- **Contextual Awareness**: Respect existing project patterns. Do not introduce new libraries or patterns without a strong justification.
- **Safety First**: Always consider edge cases, error states, and thread safety.
- **Documentation**: Comment complex logic and update relevant documentation (README, KDoc) as part of the task.
