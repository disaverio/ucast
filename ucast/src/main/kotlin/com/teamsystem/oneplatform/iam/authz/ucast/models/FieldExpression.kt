package dev.disaverio.ucast.models

import com.fasterxml.jackson.annotation.*

data class FieldExpression @JsonCreator constructor(
    @JsonProperty("field") val field: String,
    @JsonProperty("operator") val operator: FieldOperator,
    @JsonProperty("value") val value: FieldValue
) : UcastExpression() {

    @get:JsonIgnore
    override val type = "field"

    init { validate(this) }

    companion object {
        fun validate(exp: FieldExpression) {
            when (exp.operator) {
                FieldOperator.EQ, FieldOperator.NE, FieldOperator.CONTAINS ->
                    require(
                        exp.value is FieldValue.StringValue ||
                        exp.value is FieldValue.NumberValue ||
                        exp.value is FieldValue.BooleanValue ||
                        exp.value is FieldValue.NullValue ||
                        exp.value is FieldValue.FieldReference
                    ) { "For operator ${exp.operator}, only values of type string, number, boolean, null or field references are allowed" }
                FieldOperator.LT, FieldOperator.LTE, FieldOperator.GT, FieldOperator.GTE ->
                    require(
                        exp.value is FieldValue.NumberValue ||
                        exp.value is FieldValue.FieldReference
                    ) { "For operator ${exp.operator}, only numeric values or field references are allowed" }
                FieldOperator.IN, FieldOperator.NIN ->
                    require(
                        exp.value is FieldValue.ArrayValue ||
                        exp.value is FieldValue.FieldReference
                    ) { "For operator ${exp.operator}, only arrays or field references are allowed" }
                FieldOperator.STARTS_WITH, FieldOperator.ENDS_WITH ->
                    require(
                        exp.value is FieldValue.StringValue ||
                        exp.value is FieldValue.FieldReference
                    ) { "For operator ${exp.operator}, only values of type string or field references are allowed" }
            }
        }
    }
}
