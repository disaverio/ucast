package dev.disaverio.ucast.serializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.node.ArrayNode
import dev.disaverio.ucast.models.FieldValue

class FieldValueDeserializer : JsonDeserializer<FieldValue>() {

    override fun getNullValue(ctx: DeserializationContext?): FieldValue =
        FieldValue.NullValue

    override fun deserialize(parser: JsonParser, ctx: DeserializationContext): FieldValue {
        val node = parser.codec.readTree<JsonNode>(parser)
        return when {
            node.isNull -> FieldValue.NullValue
            node.isArray -> FieldValue.ArrayValue(
                (node as ArrayNode).map { element ->
                    element.traverse(parser.codec).run {
                        nextToken()
                        deserialize(this, ctx)
                    }
                }
            )
            node.isObject -> node.get("field")?.takeIf { it.isTextual }?.let {
                FieldValue.FieldReference(it.textValue())
            } ?: throw IllegalArgumentException("Unknown object shape for FieldValue: $node")
            node.isTextual -> FieldValue.StringValue(node.textValue())
            node.isBoolean -> FieldValue.BooleanValue(node.booleanValue())
            node.isNumber -> FieldValue.NumberValue(node.doubleValue())
            else -> throw IllegalArgumentException("Cannot deserialize FieldValue from: $node")
        }
    }
}
