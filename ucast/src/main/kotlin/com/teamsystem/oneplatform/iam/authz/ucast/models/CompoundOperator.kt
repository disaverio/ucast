package dev.disaverio.ucast.models

import com.fasterxml.jackson.annotation.JsonValue

enum class CompoundOperator(@JsonValue val value: String) {
    AND("and"),
    OR("or"),
    NOT("not")
}
