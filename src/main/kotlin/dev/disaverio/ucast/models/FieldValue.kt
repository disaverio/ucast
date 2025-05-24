package dev.disaverio.ucast.models

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import dev.disaverio.ucast.serializers.FieldValueDeserializer
import dev.disaverio.ucast.serializers.FieldValueSerializer

@JsonSerialize(using = FieldValueSerializer::class)
@JsonDeserialize(using = FieldValueDeserializer::class)
sealed class FieldValue {
    data class StringValue(val value: String) : FieldValue()
    data class NumberValue(val value: Double) : FieldValue()
    data class BooleanValue(val value: Boolean) : FieldValue()
    data class ArrayValue(val value: List<FieldValue>) : FieldValue()
    data class FieldReference(val field: String) : FieldValue()
    object NullValue : FieldValue()
}
