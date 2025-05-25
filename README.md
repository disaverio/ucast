# ucast

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A Kotlin library that models, builds, serializes and normalizes
[UCAST](https://github.com/stalniy/ucast) boolean expressions — the JSON-based format
also adopted in OPA - Open Policy Agent, [see docs](https://www.openpolicyagent.org/docs/filtering/ucast-syntax).

`ucast-lib` provides:

- A type-safe model of UCAST expressions (`CompoundExpression`, `FieldExpression`).
- A Kotlin DSL for building expressions ergonomically.
- JSON serialization in two dialects: **expanded** and **concise**.
- A normalization primitive that converts an arbitrary expression into [Disjunctive Normal Form](https://en.wikipedia.org/wiki/Disjunctive_normal_form).

## Install

### Maven

```xml
<dependency>
    <groupId>dev.disaverio</groupId>
    <artifactId>ucast</artifactId>
    <version>0.0.0</version>
</dependency>
```

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("dev.disaverio:ucast-lib:0.0.0")
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
