package dev.disaverio.ucast.serializers

import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.module.SimpleModule
import dev.disaverio.ucast.models.FieldValue
import org.junit.jupiter.api.*

class FieldValueDeserializerTest {

    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        val module = SimpleModule()
            .addDeserializer(FieldValue::class.java, FieldValueDeserializer())
        objectMapper = JsonMapper.builder().addModule(module).build()
    }

    @Test
    fun `test deserialize StringValue`() {
        val json = "\"hello\""
        val fieldValue = objectMapper.readValue(json, FieldValue::class.java)
        Assertions.assertEquals(FieldValue.StringValue("hello"), fieldValue)
    }

    @Test
    fun `test deserialize BooleanValue`() {
        val json = "false"
        val fieldValue = objectMapper.readValue(json, FieldValue::class.java)
        Assertions.assertEquals(FieldValue.BooleanValue(false), fieldValue)
    }

    @Test
    fun `test deserialize NumberValue integer`() {
        val json = "123"
        val fieldValue = objectMapper.readValue(json, FieldValue::class.java)
        Assertions.assertEquals(FieldValue.NumberValue(123.0), fieldValue)
    }

    @Test
    fun `test deserialize NumberValue floating point`() {
        val json = "123.45"
        val fieldValue = objectMapper.readValue(json, FieldValue::class.java)
        Assertions.assertEquals(FieldValue.NumberValue(123.45), fieldValue)
    }

    @Test
    fun `test deserialize NullValue`() {
        val json = "null"
        val fieldValue = objectMapper.readValue(json, FieldValue::class.java)
        Assertions.assertEquals(FieldValue.NullValue, fieldValue)
    }

    @Test
    fun `test deserialize FieldReference`() {
        val json = "{\"field\":\"reference_field\"}"
        val fieldValue = objectMapper.readValue(json, FieldValue::class.java)
        Assertions.assertEquals(FieldValue.FieldReference("reference_field"), fieldValue)
    }

    @Test
    fun `test deserialize ArrayValue`() {
        val json = "[\"test\", 42, true, null]"
        val fieldValue = objectMapper.readValue(json, FieldValue::class.java)
        Assertions.assertEquals(FieldValue.ArrayValue(listOf(FieldValue.StringValue("test"), FieldValue.NumberValue(42.0), FieldValue.BooleanValue(true), FieldValue.NullValue)), fieldValue)
    }

    @Test
    fun `test deserialize nested arrays`() {
        val json = "[[\"nested\", 42], {\"field\":\"reference_field\"}, [true, null]]"
        val fieldValue = objectMapper.readValue(json, FieldValue::class.java)
        Assertions.assertEquals(
            FieldValue.ArrayValue(
                listOf(
                    FieldValue.ArrayValue(listOf(FieldValue.StringValue("nested"), FieldValue.NumberValue(42.0))),
                    FieldValue.FieldReference("reference_field"),
                    FieldValue.ArrayValue(listOf(FieldValue.BooleanValue(true), FieldValue.NullValue))
                )
            ),
            fieldValue
        )
    }

    @Test
    fun `test deserialize empty string`() {
        val json = "\"\""
        val fieldValue = objectMapper.readValue(json, FieldValue::class.java)
        Assertions.assertEquals(FieldValue.StringValue(""), fieldValue)
    }

    @Test
    fun `test deserialize invalid field type`() {
        val json = "{\"field\":46}"
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            objectMapper.readValue(json, FieldValue::class.java)
        }
    }

    @Test
    fun `test deserialize invalid object shape`() {
        val json = "{\"unknown\":\"value\"}"
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            objectMapper.readValue(json, FieldValue::class.java)
        }
    }

    @Test
    fun `test deserialize invalid json throws exception`() {
        val json = "{invalid_json"
        Assertions.assertThrows(Exception::class.java) {
            objectMapper.readValue(json, FieldValue::class.java)
        }
    }
}
