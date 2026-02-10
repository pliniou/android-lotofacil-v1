---
name: android-native-agent-pack
description: Reusable Android native skill pack for Kotlin and Jetpack projects, with specialist agents and workflows for architecture, data, UI/Compose, Android framework, testing, build/CI, release, security, performance, and observability. Use when planning, implementing, reviewing, or fixing Android features and when routing work across multiple Android concerns.
---

# Android Native Agent Pack

Use this skill pack as the default operating model for this repository.

## Execute
- Start with `agents/master.md` when the task crosses multiple areas or scope is unclear.
- Jump directly to a specialist in `agents/*.md` when the user scope is explicit.
- Apply a playbook in `workflows.md` for multi-step delivery.
- Reuse templates in `templates/*.md` instead of rewriting standard artifacts.

## Route
- Use `arch` for boundaries, contracts, DI, and UI state/event models.
- Use `data` for repository, network, persistence, cache, sync, and migrations.
- Use `ui` for Compose screens/components, previews, and accessibility in UI.
- Use `android` for lifecycle, permissions, intents, background work, and manifest concerns.
- Use `kotlin` for language-level refactors, coroutines, and readability/safety improvements.
- Use `design` for design system, tokens, Material 3, and WCAG concerns.
- Use `test` for strategy and implementation of unit/instrumented/Compose tests.
- Use `build` for Gradle/AGP, dependencies, modularization, lint, and CI.
- Use `release` for versioning, signing, shrinker, and release readiness.
- Use `security` for privacy, secrets, permission minimization, and PII controls.
- Use `perf` for startup, jank, memory, network, and battery optimization.
- Use `observe` for logs, crashes, metrics, analytics, and tracing.

## Deliver
- Keep hand-offs explicit: include context, constraints, expected artifacts, and done criteria.
- Keep implementation aligned with existing repository conventions.
- Keep quality gates active: critical tests, error states, and minimal observability.
