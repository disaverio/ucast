package dev.disaverio.ucast.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import dev.disaverio.ucast.models.FieldValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FieldValueSerializerTest {

    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
        val module = SimpleModule()
        module.addSerializer(FieldValue::class.java, FieldValueSerializer())
        module.addDeserializer(FieldValue::class.java, FieldValueDeserializer())
        objectMapper.registerModule(module)
    }

    @Test
    fun `test serialize StringValue`() {
        val fieldValue = FieldValue.StringValue("test")
        val json = objectMapper.writeValueAsString(fieldValue)
        assertEquals("\"test\"", json)
    }

    @Test
    fun `test serialize BooleanValue`() {
        val fieldValue = FieldValue.BooleanValue(true)
        val json = objectMapper.writeValueAsString(fieldValue)
        assertEquals("true", json)
    }

    @Test
    fun `test serialize NumberValue`() {
        val fieldValue = FieldValue.NumberValue(42.5)
        val json = objectMapper.writeValueAsString(fieldValue)
        assertEquals("42.5", json)
    }

    @Test
    fun `test serialize NullValue`() {
        val fieldValue = FieldValue.NullValue
        val json = objectMapper.writeValueAsString(fieldValue)
        assertEquals("null", json)
    }

    @Test
    fun `test serialize FieldReference`() {
        val fieldValue = FieldValue.FieldReference("other_field")
        val json = objectMapper.writeValueAsString(fieldValue)
        assertEquals("{\"field\":\"other_field\"}", json)
    }

    @Test
    fun `test serialize ArrayValue`() {
        val fieldValue =
            FieldValue.ArrayValue(
                listOf(
                    FieldValue.StringValue("one"),
                    FieldValue.BooleanValue(true),
                    FieldValue.NumberValue(2.3),
                    FieldValue.NullValue,
                    FieldValue.FieldReference("other_field"),
                    FieldValue.ArrayValue(
                        listOf(
                            FieldValue.FieldReference("other_again_field"),
                            FieldValue.StringValue("two"),
                            FieldValue.BooleanValue(false),
                            FieldValue.NumberValue(3.4),
                            FieldValue.NullValue
                        )
                    )
                )
            )
        val json = objectMapper.writeValueAsString(fieldValue)
        assertEquals("[\"one\",true,2.3,null,{\"field\":\"other_field\"},[{\"field\":\"other_again_field\"},\"two\",false,3.4,null]]", json)
    }
}

