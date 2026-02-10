# Android Native Agents (Default)

Use this project agent pack by default for Android tasks:
- `.android-native-agents/SKILL.md`
- `.android-native-agents/skills.md`
- `.android-native-agents/workflows.md`
- `.android-native-agents/agents/*.md`
- `.android-native-agents/templates/*.md`

## Trigger Rules
- Activate this pack for any request involving Android native development, Kotlin, Jetpack/Compose, Gradle, testing, observability, release, security, or performance.
- Prefer routing through `master` when the request spans multiple concerns or the entry point is unclear.
- If the user explicitly picks a specialist (`arch`, `data`, `ui`, `android`, `kotlin`, `design`, `test`, `build`, `release`, `security`, `perf`, `observe`), use that specialist first.

## Default Execution Order
1. `master` (routing and hand-off plan when needed)
2. Relevant specialist agents from `.android-native-agents/agents/`
3. Matching workflow from `.android-native-agents/workflows.md`
4. Matching template from `.android-native-agents/templates/`

## Output Expectations
- Keep outputs actionable and implementation-ready.
- Respect existing project patterns before proposing new architecture.
- Include tests, observability, and risk checks in all non-trivial changes.
