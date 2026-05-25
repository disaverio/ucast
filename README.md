# ucast

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/dev.disaverio/ucast.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/dev.disaverio/ucast)

A Kotlin library that models, builds, serializes and normalizes
[UCAST](https://github.com/stalniy/ucast) boolean expressions — the JSON-based format
also adopted in OPA - Open Policy Agent, [see docs](https://www.openpolicyagent.org/docs/filtering/ucast-syntax).

`ucast-lib` provides:

- A type-safe model of UCAST expressions (`CompoundExpression`, `FieldExpression`).
- A Kotlin DSL for building expressions ergonomically.
- JSON serialization in two dialects: **expanded** and **concise**.
- A normalization primitive that converts an arbitrary expression into [Disjunctive Normal Form](https://en.wikipedia.org/wiki/Disjunctive_normal_form).

## Install

Choose the version that matches the Jackson major version used in your project.

| Your Jackson dependency | `ucast` version to use |
|------------------------|------------------------|
| `com.fasterxml.jackson` (Jackson 2.x) | `0.0.0` |
| `tools.jackson` (Jackson 3.x) | `1.0.0` |

### Maven

**Jackson 3.x projects (`tools.jackson`)**

```xml
<dependency>
    <groupId>dev.disaverio</groupId>
    <artifactId>ucast</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Jackson 2.x projects (`com.fasterxml.jackson`)**

```xml
<dependency>
    <groupId>dev.disaverio</groupId>
    <artifactId>ucast</artifactId>
    <version>0.0.0</version>
</dependency>
```

### Gradle (Kotlin DSL)

**Jackson 3.x projects (`tools.jackson`)**

```kotlin
dependencies {
    implementation("dev.disaverio:ucast:1.0.0")
}
```

**Jackson 2.x projects (`com.fasterxml.jackson`)**

```kotlin
dependencies {
    implementation("dev.disaverio:ucast:0.0.0")
}
```

## Quick example

```kotlin
import dev.disaverio.ucast.dsl.*

val expr = and {
    "age" gte 18
    or {
        "role" eq "admin"
        "department" `in` listOf("eng", "sec")
    }
    not {
        "status" eq "banned"
    }
}
```

## Grammar

The two supported syntaxes are documented under [`src/main/resources/definitions`](src/main/resources/definitions) as resources:

- [Expanded grammar](src/main/resources/definitions/ucast-expanded-grammar.txt) — JSON Schema available in [`ucast-grammar.json`](src/main/resources/definitions/ucast-grammar.json).
- [Concise grammar](src/main/resources/definitions/ucast-concise-grammar.txt) — sugared form with implicit-AND objects and field shorthand.

Note: the concise dialect is currently emit-only; deserialization supports the expanded form only.

## Build

Requires JDK 17+.

```bash
mvn clean test
```

## License

Apache License 2.0 — see [LICENSE](LICENSE).
