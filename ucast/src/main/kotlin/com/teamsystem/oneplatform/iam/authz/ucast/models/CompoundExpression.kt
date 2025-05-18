package dev.disaverio.ucast.models

import com.fasterxml.jackson.annotation.*

data class CompoundExpression @JsonCreator constructor(
    @JsonProperty("operator") val operator: CompoundOperator,
    @JsonProperty("value") val value: List<UcastExpression>
) : UcastExpression() {

    @get:JsonIgnore
    override val type = "compound"

    init { validate(this) }

    companion object {
        fun validate(exp: CompoundExpression) {
            when (exp.operator) {
                CompoundOperator.AND ->
                    require(exp.value.size >= 2) { "AND operator requires at least two operands" }
                CompoundOperator.OR ->
                    require(exp.value.size >= 2) { "OR operator requires at least two operands" }
                CompoundOperator.NOT ->
                    require(exp.value.size == 1) { "NOT operator requires exactly one operand" }
            }
        }
    }
}
