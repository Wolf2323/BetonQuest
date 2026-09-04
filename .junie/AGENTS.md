# BetonQuest Developer & Agent Guide

This document contains project-specific guidelines, build and configuration instructions, testing workflows, and code
conventions for development on BetonQuest.

---

## 1. Build and Configuration Instructions

### Prerequisites & Environment
- **Java**: Java 25 (minimum Java 17/21 bytecode target compatibility across modules, but development and CI target Java
  25 LTS).
- **Build Tool**: Apache Maven Wrapper (`./mvnw` on Unix/macOS, `.\mvnw.cmd` on Windows).
- **Operating System / Shell**: Cross-platform (commands below use Maven wrapper).

### Multi-Module Project Structure
- `code/api`: Public API definitions, events, and interfaces.
- `code/lib`: Shared core utility classes, config management, and parsers.
- `code/core`: Main BetonQuest logic, command handling, quests, menus, and scheduling.
- `code/mc_1_20_6`, `code/mc_1_21_4`, `code/mc_1_21_8`: Version-specific Minecraft / Paper server adapters.
- `code/compatibility`: Third-party plugin integrations (e.g., AuraSkills, MythicMobs, Vault, etc.).
- `code/build`: Shading, packaging, and producing the final `BetonQuest.jar`.
- `docs/`: MkDocs documentation and developer wiki.

### Common Build Commands

- **Full Build & Verification (CI replica)**:
  ```bash
  ./mvnw clean verify
  ```
  Runs compilation, all linters/checks (PMD, SpotBugs, CheckStyle, EditorConfig, NullAway), and unit tests.

- **Fast Build (Skip Tests & Verification)**:
  ```bash
  ./mvnw package -P Test-None,Skip-Verification
  # or simply:
  ./mvnw package -P Test-None
  ```
  Useful for quick iteration and generating the shaded plugin JAR in `target/code/BetonQuest.jar`.

- **Build with All Tests**:
  ```bash
  ./mvnw verify -P Test-All
  ```

- **Compile Specific Submodule**:
  ```bash
  ./mvnw test-compile -pl code/core
  ```

- **Reformat Code with EditorConfig**:
  ```bash
  ./mvnw editorconfig:format
  ```

---

## 2. Testing Information

### Test Frameworks & Libraries
- **JUnit 5 (Jupiter)**: Standard test framework (`org.junit.jupiter.api.*`).
- **Mockito**: Mocking framework for interfaces and classes.
- **MockBukkit / BukkitSchedulerMock**: Custom and Bukkit mocking helpers for tick-based scheduler testing.
- **BetonQuest Logger Extensions**: `BetonQuestLoggerExtension` and `LogValidator` for verifying logged messages.

### Test Execution Commands

- **Run all unit tests**:
  ```bash
  ./mvnw test
  ```

- **Run tests in a single module**:
  ```bash
  ./mvnw test -pl code/core
  ./mvnw test -pl code/lib
  ```

- **Run a specific test class**:
  ```bash
  ./mvnw test -pl code/core -Dtest=SlotsTest
  ./mvnw test -pl code/lib -Dtest=BetonQuestVersionTest
  ```

- **Run a specific test method**:
  ```bash
  ./mvnw test -pl code/lib -Dtest=BetonQuestVersionTest#dev_versions_are_higher_than_stable_versions
  ```

### Guidelines for Writing New Tests

1. **Naming Conventions**:

- Use `snake_case` for test method names.
- Example: `void parse_valid_version_string()`, `void get_items_with_offset()`.
- Test class names should end in `Test` (e.g. `MyFeatureTest`).

2. **Nullability**:

- The project uses **NullAway** with `@NotNull` by default.
- Any parameter or return value that can be null must be explicitly annotated with `@Nullable`.

3. **Visibility for Testing**:

- When package-private visibility is used solely for tests, annotate the member with `@VisibleForTesting`
  (`org.jetbrains.annotations.VisibleForTesting`) to suppress PMD `CommentDefaultAccessModifier` warnings.

4. **Testing Scheduler / Async Tasks**:

- Use `BukkitSchedulerMock` with try-with-resources:
  ```java
  @Test
  void test_scheduled_task() {
      try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
          // schedule task
          scheduler.performTick();
          // assert expectations
      }
  }
  ```

5. **Testing Logging Output**:

- Annotate test class with `@ExtendWith(BetonQuestLoggerExtension.class)` and inject `LogValidator validator`:
  ```java
@ExtendWith (BetonQuestLoggerExtension.class)
class MyServiceTest {
@Test
void logs_warning_on_missing_config (final LogValidator validator) {
// trigger action
validator.assertLogEntry (Level.WARN, "Expected warning message");
}
}
```

### Example Test Template

```java
package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.Version;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExampleDemonstrationTest {

    @Test
    void parse_valid_version_string() {
        final Version version = BetonQuestVersion.parse("3.3.0");
        assertNotNull(version, "Parsed version should not be null");
        assertEquals("3.3.0", version.toString(), "Version string representation should match");
    }
}
```

---

## 3. Additional Development & Code Style Information

### Static Analysis & Quality Requirements

BetonQuest enforces strict automated checks during the `verify` lifecycle:

1. **NullAway**:

- Disallows unhandled `null`s. All fields, parameters, and methods default to `@NotNull`.
- Explicitly annotate nullable fields and parameters with `org.jetbrains.annotations.Nullable`.

2. **PMD**:

- Enforces clean code rules, naming standards, and catches code smells.
- Rule suppressions should be used sparingly via `@SuppressWarnings("PMD.RuleName")`.

3. **SpotBugs**:

- Catches potential bugs, incorrect synchronization, and null dereferences.
- Annotate with `@SuppressFBWarnings` if a false positive occurs.

4. **CheckStyle**:

- Validates import ordering: imports must be organized cleanly without unused imports.
- No star (`*`) imports (except specific JUnit imports).

5. **EditorConfig**:

- Indentation (spaces), charset (UTF-8), trailing whitespaces, and final newlines.
- Fix violations automatically using `./mvnw editorconfig:format`.

### Changelog Management
- When implementing a user-facing or API change:
  - Add notes to `CHANGELOG.md` under `## [Unreleased]`.
  - For API modifications, update `API-CHANGELOG.md` accordingly.
