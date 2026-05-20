package dev.disaverio.ucast.serializers

import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer
import dev.disaverio.ucast.models.FieldValue

class FieldValueSerializer : ValueSerializer<FieldValue>() {

    override fun serialize(value: FieldValue, gen: JsonGenerator, serializers: SerializationContext) {
        with(gen) {
            when (value) {
                is FieldValue.StringValue -> writeString(value.value)
                is FieldValue.NumberValue -> writeNumber(value.value)
                is FieldValue.BooleanValue -> writeBoolean(value.value)
                is FieldValue.NullValue -> writeNull()
                is FieldValue.ArrayValue -> {
                    writeStartArray()
                    value.value.forEach { serializers.writeValue(this, it) }
                    writeEndArray()
                }
                is FieldValue.FieldReference -> {
                    writeStartObject()
                    writeStringProperty("field", value.field)
                    writeEndObject()
                }
            }
        }
    }
}
