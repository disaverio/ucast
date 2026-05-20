package dev.disaverio.ucast.extensions

import tools.jackson.databind.ObjectMapper
import dev.disaverio.ucast.extensions.ConciseSyntaxUtils.toConciseJsonNode
import dev.disaverio.ucast.models.UcastExpression

fun UcastExpression.normalize(): UcastExpression =
    NormalizationUtils.toDNF(this)

fun UcastExpression.toConciseJson(): String =
    ObjectMapper().writeValueAsString(toConciseJsonNode(this))
