# Android Native Agents (Refactored)

This repository is equipped with the **Android Native Agent Pack**, a set of specialized AI instructions to maintain code quality and consistency.

## 🤖 Active Agents

The following agents are available in `.android-native-agents/`.
All agents are orchestrated via this file to ensure the right specialist is chosen.

| Agent | Responsibilities |
| :--- | :--- |
| **`master`** | **Orchestrator**. Start here. Analyzes context, plans, and routes to specialists. |
| **`arch`** | **Architecture**. Boundaries, Contracts, DI, State Models. |
| **`ui`** | **User Interface**. Compose Screens, Components, Aesthetics. |
| **`data`** | **Data Layer**. Repositories, Database, Network, Sync. |
| **`test`** | **Testing**. Unit, Integration, UI Tests, Fakes. |
| **`android`** | **Platform**. Lifecycle, Manifest, Permissions, Services. |
| **`kotlin`** | **Language**. Refactoring, Coroutines, Safety. |
| **`design`** | **Design System**. Tokens, Theme, Accessibility, Material 3. |
| **`build`** | **Build/CI**. Gradle, Dependencies, Lint/Detekt. |
| **`release`** | **Distribution**. Signing, Versioning, Play Store Pre-check. |
| **`security`**| **Security**. Privacy, Encryption, Permissions Audit. |
| **`perf`** | **Performance**. Profiling, Optimization, Benchmarks. |
| **`observe`** | **Observability**. Logs, Analytics, Crash Reporting. |

## 🛑 Protocol: Context First
**CRITICAL INSTRUCTION FOR AI:**
Before generating any code or plan, you **MUST**:
1.  **Analyze Structure**: Run `list_dir` to understand the current directory layout.
2.  **Read Existing Code**: Run `view_file` on relevant files to match the existing coding style and architecture.
3.  **Avoid Assumptions**: Do not guess file paths or content. Verify them first.

## 🚀 How to Use

Invoke `master` for complex tasks or directly call a specialist if the scope is narrow.

> **User**: `@master We need to migrate the database to use a new schema.`
> **AI**: *Reads `AGENTS.md`, sees `master` is the orchestrator, invokes `master` to plan the migration.*

## 📂 Structure
- `SKILL.md`: Main entry point and definition of the skill pack.
- `skills.md`: detailed command reference and prompt patterns.
- `workflows.md`: Standard Operating Procedures (Playbooks) for common tasks.
- `agents/*.md`: Individual agent instructions and personas.
