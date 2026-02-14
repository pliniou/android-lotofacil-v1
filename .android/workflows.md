# Workflows (Playbooks)

These workflows define standard procedures for common development tasks. Follow them to ensure consistency and quality.

## 📦 WF.Feature: New Feature End-to-End
*Goal: Deliver a robust, production-ready feature.*

1.  **`arch`**: **Blueprint & Contracts**
    - Define domain models, repository interfaces, and UseCases.
    - Define UI State and Events.
    - Setup Dependency Injection (Hilt).
2.  **`design`**: **Visual Specs**
    - Define design tokens (if new) or identify existing ones.
    - Clarify layout rules and accessibility requirements.
3.  **`data`**: **Data Layer Implementation**
    - Implement DataSources (API service / DAO).
    - Implement Repository with mapping logic and error handling.
    - Handle caching/offline support if required.
4.  **`ui`**: **UI Implementation**
    - Build stateless Components with Previews.
    - implementations Screen/Page Composable.
    - Connect ViewModel (manage state/events).
    - Implement Navigation.
5.  **`test`**: **Verification**
    - Unit Tests for UseCases and ViewModels.
    - Integration Tests for Repository (optional but recommended).
    - UI/Screenshot tests for Screens.
6.  **`observe`**: **Instrumentation**
    - Add structured logging for critical flows.
    - Define and track analytics events.

**Done Criteria (DoD):**
- [ ] Compiles without warnings.
- [ ] Runs on target API levels (minSdk to targetSdk).
- [ ] Unit tests pass.
- [ ] UI has Loading, Content, Empty, and Error states.
- [ ] Accessibility support (ContentDescriptions, touch targets).

---

## 🐛 WF.Bug: Standardized Bug Fix
*Goal: Fix bugs with regression safety.*

1.  **`observe`**: **Diagnosis**
    - Analyze logs, stack traces, or crash reports.
    - Reproduce the issue locally (create a reproduction test case if possible).
2.  **`arch`**: **Root Cause Analysis**
    - Identify the broken contract, state management issue, or logic error.
3.  **`code`**: **Fix Implementation**
    - Apply the fix in the appropriate layer (`android`, `ui`, `data`, or `domain`).
    - *Avoid workaround/hacks unless documented as temporary.*
4.  **`test`**: **Regression Testing**
    - Add a test case that fails without the fix and passes with it.
    - Run related test suites to ensure no side effects.

---

## 🗄️ WF.Schema: specific Data/Database Change
*Goal: Modify data structure without data loss.*

1.  **`arch`**: **Impact Analysis**
    - Identify all affected Models, DTOs, and Mappers.
2.  **`data`**: **Migration Strategy**
    - Modify Entity/Table classes.
    - Create a Room Migration or API version handling strategy.
    - Update Mappers and Tests.
3.  **`test`**: **Verify Migration**
    - Write a migration test to verify data integrity before/after.

---

## 🚀 WF.Release: Release Preparation
*Goal: Safe and compliant release.*

1.  **`build`**: **Pre-flight Check**
    - Clean build.
    - Check for dependency updates/vulnerabilities.
    - Run Lint/Detekt.
2.  **`test`**: **Full Suite**
    - Run all Unit and Instrumented tests.
3.  **`release`**: **Artifact Generation**
    - Bump version code/name.
    - Update `CHANGELOG.md`.
    - Generate Signed Bundle/APK.
    - Verify R8/Obfuscation mappings.
4.  **`test`**: **Sanity Check**
    - Install release build on a physical device.
    - Perform a manual smoke test of critical paths.

---

## ⚡ WF.Perf: Performance Optimization
*Goal: Measurable performance improvement.*

1.  **`perf`**: **Baseline Measurement**
    - Use Profiler/Macrobenchmark to record current state.
    - Identify bottleneck (CPU, Memory, GPU, I/O).
2.  **`code`**: **Optimization**
    - Apply specific fix (e.g., flatten layout, optimize query, debounce inputs).
3.  **`perf`**: **Validation**
    - Re-run benchmark to confirm improvement.
    - Ensure no functional regression.

---

## 🔒 WF.Security: Security Audit
*Goal: Harden application security.*

1.  **`security`**: **Threat Model**
    - Review data flow and storage.
    - Check permissions usage.
2.  **`build`**: **Dependency Scan**
    - Check for known vulnerabilities in libraries.
3.  **`android`**: **Manifest & Config**
    - Verify `exported` flags.
    - Check Network Security Config.
    - Verify Proguard rules.
