package dev.disaverio.ucast.extensions

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*
import dev.disaverio.ucast.models.*
import dev.disaverio.ucast.models.FieldValue.*

internal object ConciseSyntaxUtils {

    fun toConciseJsonNode(expr: UcastExpression): JsonNode {
        val mapper = ObjectMapper()
        return when (expr) {
            is FieldExpression -> getFieldExpression(expr, mapper)
            is CompoundExpression -> {
                if (expr.operator == CompoundOperator.AND && expr.value.all { it is FieldExpression }) { // ok let's go with the compact FIELD_NAME ':' VALUE syntax
                    mapper.createObjectNode().apply {
                        for (expr in expr.value) {
                            getFieldExpression(expr as FieldExpression, mapper)
                                .properties()
                                .iterator()
                                .forEachRemaining { (k, v) -> set<JsonNode>(k, v) }
                        }
                    }
                } else {
                    getCompoundExpression(expr, mapper)
                }
            }
        }
    }

    // named in the grammar definition as FIELD_EXPR
    private fun getFieldExpression(expr: FieldExpression, mapper: ObjectMapper): ObjectNode =
        mapper.createObjectNode().apply {
            when (expr.operator) {
                FieldOperator.EQ -> putPOJO(expr.field, getFieldValue(expr.value, mapper)) // FIELD_NAME ':' VALUE
                else -> set( // FIELD_NAME ':' '{' FIELD_OP_NAME ':' VALUE '}'
                    expr.field, // FIELD_NAME
                    mapper.createObjectNode().apply { set<JsonNode>(
                        expr.operator.value, // FIELD_OP_NAME
                        getFieldValue(expr.value, mapper) // VALUE
                    ) }
                )
            }
        }

    // named in the grammar definition as COMPOUND_EXPR
    private fun getCompoundExpression(expr: CompoundExpression, mapper: ObjectMapper): ObjectNode =
        mapper.createObjectNode().apply { // COMPOUND_OP_NAME ':' [EXPRS...]
            set<ArrayNode>(
                expr.operator.value, // COMPOUND_OP_NAME
                mapper.createArrayNode().apply { addAll(expr.value.map { toConciseJsonNode(it) }) } // [EXPRS...]
            )
        }

    // named in the grammar definition as VALUE
    private fun getFieldValue(value: FieldValue, mapper: ObjectMapper): JsonNode =
        when (value) {
            is StringValue -> TextNode(value.value)
            is NumberValue -> DoubleNode(value.value)
            is BooleanValue -> BooleanNode.valueOf(value.value)
            is NullValue -> NullNode.instance
            is ArrayValue -> mapper.createArrayNode().apply { addAll(value.value.map { getFieldValue(it, mapper) }) }
            is FieldReference -> mapper.createObjectNode().apply { put( // {"field":COMPARED_FIELD_NAME}
                "field",
                value.field // COMPARED_FIELD_NAME
            ) }
        }
}
