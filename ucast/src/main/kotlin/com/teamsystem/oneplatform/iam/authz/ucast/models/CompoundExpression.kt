package dev.disaverio.ucast.models

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class CompoundExpression @JsonCreator constructor(
    @JsonProperty("operator") val operator: CompoundOperator,
    @JsonProperty("value") val value: List<UcastExpression>
) : UcastExpression() {
    override val type = "compound"
}
