# Skills Reference (Short Commands)

Use these short commands (prefixes) to quickly invoke specific capabilities in the IDE chat.

## 🚀 Command Reference

| Command | Responsibility | Keywords |
| :--- | :--- | :--- |
| **`master`** | **Routing & Strategy** | Plan, Refactor, Overview, "How to", Coordination |
| **`arch`** | **Architecture & DI** | Structure, Hilt, Module, Interface, UseCase, State |
| **`data`** | **Data Operations** | API, Room, SQL, JSON, Repository, Cache, Offline |
| **`ui`** | **User Interface** | Compose, Screen, Modifier, Animation, Theme, Layout |
| **`android`** | **Platform APIs** | Context, Intent, Service, Permission, Manifest, Res |
| **`kotlin`** | **Language & Logic** | Coroutine, Flow, Extension, Generic, Refactor, Clean |
| **`design`** | **Visual System** | Color, Type, Shape, Token, Material3, Icon, Assets |
| **`test`** | **Testing** | JUnit, Mockk, Espresso, Robot, verify, assert |
| **`build`** | **Build & CI** | Gradle, KTS, Dependency, Version, Plugin, CI/CD |
| **`release`** | **Publication** | Store, Bundle, Signing, Proguard, R8, Changelog |
| **`security`** | **Sec & Privacy** | Encrypt, Hash, Token, Auth, PII, Obfuscate |
| **`perf`** | **Performance** | Profile, Trace, Memory, Frame, Startup, Latency |
| **`observe`** | **Telemetry** | Log, Crash, Event, Analytics, Trace, Monitor |

## 🗣️ Effective Prompting Patterns

**Standard Pattern:**
> `<skill>: <task_description> | context: <constraints/details> | output: <expected_artifacts>`

**Examples:**

- **New Feature:**
  > `master: Plan "User Profile" feature | context: MVVM, Offline-first, Edit profile | output: Implementation Plan + Task List`

- **UI Component:**
  > `ui: Create "PrimaryButton" component | context: Material3, support loading state | output: Composable + Preview`

- **Database Change:**
  > `data: Add "lastLogin" field to User entity | context: Room migration required | output: Entity update + Migration script`

- **Bug Fix:**
  > `android: Fix "PermissionDenied" crash on Camera | context: Android 14 changes | output: Permission logic fix`

- **Unit Test:**
  > `test: Test "LoginUseCase" | context: Mockk, verify success/error flows | output: Test class`
