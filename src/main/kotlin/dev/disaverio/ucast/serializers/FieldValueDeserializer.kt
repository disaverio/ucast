package dev.disaverio.ucast.serializers

import tools.jackson.core.JsonParser
import tools.jackson.databind.*
import dev.disaverio.ucast.models.FieldValue

class FieldValueDeserializer : ValueDeserializer<FieldValue>() {

    override fun getNullValue(ctx: DeserializationContext): FieldValue =
        FieldValue.NullValue

    override fun deserialize(parser: JsonParser, ctx: DeserializationContext): FieldValue {
        val node = ctx.readTree(parser)
        return when {
            node.isNull -> FieldValue.NullValue
            node.isArray -> FieldValue.ArrayValue(
                (node as Iterable<JsonNode>).map { element ->
                    element.traverse(ctx).run {
                        nextToken()
                        deserialize(this, ctx)
                    }
                }
            )
            node.isObject -> node.get("field")?.takeIf { it.isString }?.let {
                FieldValue.FieldReference(it.stringValue())
            } ?: throw IllegalArgumentException("Unknown object shape for FieldValue: $node")
            node.isString -> FieldValue.StringValue(node.stringValue())
            node.isBoolean -> FieldValue.BooleanValue(node.booleanValue())
            node.isNumber -> FieldValue.NumberValue(node.doubleValue())
            else -> throw IllegalArgumentException("Cannot deserialize FieldValue from: $node")
        }
    }
}
