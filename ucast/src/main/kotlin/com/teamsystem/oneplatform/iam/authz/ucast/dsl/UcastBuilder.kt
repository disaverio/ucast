package dev.disaverio.ucast.dsl

import dev.disaverio.ucast.models.UcastExpression
import dev.disaverio.ucast.dsl.and as _and
import dev.disaverio.ucast.dsl.contains as _contains
import dev.disaverio.ucast.dsl.endsWith as _endsWith
import dev.disaverio.ucast.dsl.eq as _eq
import dev.disaverio.ucast.dsl.gt as _gt
import dev.disaverio.ucast.dsl.gte as _gte
import dev.disaverio.ucast.dsl.`in` as _in
import dev.disaverio.ucast.dsl.lt as _lt
import dev.disaverio.ucast.dsl.lte as _lte
import dev.disaverio.ucast.dsl.ne as _ne
import dev.disaverio.ucast.dsl.nin as _nin
import dev.disaverio.ucast.dsl.or as _or
import dev.disaverio.ucast.dsl.startsWith as _startsWith

class UcastBuilder() {

    val conditions = mutableListOf<UcastExpression>()

    fun and(initializer: UcastBuilder.() -> Unit) { conditions += _and(initializer) }

    fun or(initializer: UcastBuilder.() -> Unit) { conditions += _or(initializer) }

    infix fun String.eq(value: Any?) { conditions += this _eq value }

    infix fun String.ne(value: Any?) { conditions += this _ne value }

    infix fun String.lt(value: Any?) { conditions += this _lt value }

    infix fun String.lte(value: Any?) { conditions += this _lte value }

    infix fun String.gt(value: Any?) { conditions += this _gt value }

    infix fun String.gte(value: Any?) { conditions += this _gte value }

    infix fun String.`in`(value: Any?) { conditions += this _in value }

    infix fun String.nin(value: Any?) { conditions += this _nin value }

    infix fun String.contains(value: Any?) { conditions += this _contains value }

    infix fun String.startsWith(value: Any?) { conditions += this _startsWith value }

    infix fun String.endsWith(value: Any?) { conditions += this _endsWith value }
}
