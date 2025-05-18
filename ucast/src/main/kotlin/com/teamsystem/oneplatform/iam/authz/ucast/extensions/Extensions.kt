package dev.disaverio.ucast.extensions

import dev.disaverio.ucast.models.UcastExpression

fun UcastExpression.normalize(): UcastExpression =
    NormalizationUtils.toDNF(this)

