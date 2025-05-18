package dev.disaverio.ucast.dsl

import dev.disaverio.ucast.models.*

fun and(initializer: UcastBuilder.() -> Unit): CompoundExpression =
    CompoundExpression(
        CompoundOperator.AND,
        UcastBuilder().apply(initializer).conditions
    )

fun or(initializer: UcastBuilder.() -> Unit): CompoundExpression =
    CompoundExpression(
        CompoundOperator.OR,
        UcastBuilder().apply(initializer).conditions
    )

fun not(initializer: UcastBuilder.() -> Unit): CompoundExpression =
    CompoundExpression(
        CompoundOperator.NOT,
        UcastBuilder().apply(initializer).conditions
    )

infix fun String.eq(value: Any?): FieldExpression =
    FieldExpression(this, FieldOperator.EQ, getFieldValue(value))

infix fun String.ne(value: Any?): FieldExpression =
    FieldExpression(this, FieldOperator.NE, getFieldValue(value))

infix fun String.lt(value: Any?): FieldExpression =
    FieldExpression(this, FieldOperator.LT, getFieldValue(value))

infix fun String.lte(value: Any?): FieldExpression =
    FieldExpression(this, FieldOperator.LTE, getFieldValue(value))

infix fun String.gt(value: Any?): FieldExpression =
    FieldExpression(this, FieldOperator.GT, getFieldValue(value))

infix fun String.gte(value: Any?): FieldExpression =
    FieldExpression(this, FieldOperator.GTE, getFieldValue(value))

infix fun String.`in`(value: Any?): FieldExpression =
    FieldExpression(this, FieldOperator.IN, getFieldValue(value))

infix fun String.nin(value: Any?): FieldExpression =
    FieldExpression(this, FieldOperator.NIN, getFieldValue(value))

infix fun String.contains(value: Any?): FieldExpression =
    FieldExpression(this, FieldOperator.CONTAINS, getFieldValue(value))

infix fun String.startsWith(value: Any?): FieldExpression =
    FieldExpression(this, FieldOperator.STARTS_WITH, getFieldValue(value))

infix fun String.endsWith(value: Any?): FieldExpression =
    FieldExpression(this, FieldOperator.ENDS_WITH, getFieldValue(value))

fun otherField(field: String) =
    OtherField(field)

private fun getFieldValue(value: Any?): FieldValue {
    return when (value) {
        is String -> FieldValue.StringValue(value)
        is Number -> FieldValue.NumberValue(value.toDouble())
        is Boolean -> FieldValue.BooleanValue(value)
        is List<*> -> FieldValue.ArrayValue(value.map { getFieldValue(it) })
        is OtherField -> FieldValue.FieldReference(value.field)
        null -> FieldValue.NullValue
        else -> throw IllegalArgumentException("Unsupported value type: ${value::class.simpleName}")
    }
}

data class OtherField(val field: String)
