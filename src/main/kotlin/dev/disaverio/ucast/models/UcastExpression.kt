package dev.disaverio.ucast.models

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = CompoundExpression::class, name = "compound"),
    JsonSubTypes.Type(value = FieldExpression::class, name = "field")
)
sealed class UcastExpression {
    abstract val type: String
}
