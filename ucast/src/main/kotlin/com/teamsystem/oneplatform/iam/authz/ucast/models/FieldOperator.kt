package dev.disaverio.ucast.models

import com.fasterxml.jackson.annotation.JsonValue

enum class FieldOperator(@JsonValue val value: String) {
    EQ("eq"),
    NE("ne"),
    LT("lt"),
    LTE("lte"),
    GT("gt"),
    GTE("gte"),
    IN("in"),
    NIN("nin"),
    CONTAINS("contains"),
    STARTS_WITH("startswith"),
    ENDS_WITH("endswith")
}
