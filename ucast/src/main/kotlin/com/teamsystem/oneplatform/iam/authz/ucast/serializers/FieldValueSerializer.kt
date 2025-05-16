package dev.disaverio.ucast.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import dev.disaverio.ucast.models.FieldValue

class FieldValueSerializer : JsonSerializer<FieldValue>() {

    override fun serialize(value: FieldValue, gen: JsonGenerator, serializers: SerializerProvider) = with(gen) {
        when (value) {
            is FieldValue.StringValue -> writeString(value.value)
            is FieldValue.NumberValue -> writeNumber(value.value)
            is FieldValue.BooleanValue -> writeBoolean(value.value)
            is FieldValue.NullValue -> writeNull()
            is FieldValue.ArrayValue -> {
                writeStartArray()
                value.value.forEach { serializers.defaultSerializeValue(it, this) }
                writeEndArray()
            }
            is FieldValue.FieldReference -> {
                writeStartObject()
                writeStringField("field", value.field)
                writeEndObject()
            }
        }
    }
}

