package dev.disaverio.ucast.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import dev.disaverio.ucast.models.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UcastExpressionTest {

    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
    }

    @Test
    fun `test serialize and deserialize simple field expression`() {
        val fieldExpression = FieldExpression(
            field = "username",
            operator = FieldOperator.EQ,
            value = FieldValue.StringValue("test_user")
        )

        val json = objectMapper.writeValueAsString(fieldExpression)
        assertEquals("""{"type":"field","field":"username","operator":"eq","value":"test_user"}""", json)

        val deserializedExpression = objectMapper.readValue(json, UcastExpression::class.java)

        assert(deserializedExpression is FieldExpression)

        deserializedExpression as FieldExpression
        assertEquals("username", deserializedExpression.field)
        assertEquals(FieldOperator.EQ, deserializedExpression.operator)
        assert(deserializedExpression.value is FieldValue.StringValue)
        assertEquals("test_user", (deserializedExpression.value as FieldValue.StringValue).value)
    }

    @Test
    fun `test serialize and deserialize compound expression`() {
        val fieldExpression1 = FieldExpression(
            field = "age",
            operator = FieldOperator.GTE,
            value = FieldValue.NumberValue(18.0)
        )

        val fieldExpression2 = FieldExpression(
            field = "active",
            operator = FieldOperator.EQ,
            value = FieldValue.BooleanValue(true)
        )

        val compoundExpression = CompoundExpression(
            operator = CompoundOperator.AND,
            value = listOf(fieldExpression1, fieldExpression2)
        )

        val json = objectMapper.writeValueAsString(compoundExpression)
        assertEquals("""{"type":"compound","operator":"and","value":[{"type":"field","field":"age","operator":"gte","value":18.0},{"type":"field","field":"active","operator":"eq","value":true}]}""", json)

        val deserializedExpression = objectMapper.readValue(json, UcastExpression::class.java)

        assert(deserializedExpression is CompoundExpression)

        deserializedExpression as CompoundExpression
        assertEquals(CompoundOperator.AND, deserializedExpression.operator)
        assertEquals(2, deserializedExpression.value.size)

        val subExp1 = deserializedExpression.value[0] as FieldExpression
        assertEquals("age", subExp1.field)
        assertEquals(FieldOperator.GTE, subExp1.operator)
        assert(subExp1.value is FieldValue.NumberValue)
        assertEquals(18.0, (subExp1.value as FieldValue.NumberValue).value)

        val subExp2 = deserializedExpression.value[1] as FieldExpression
        assertEquals("active", subExp2.field)
        assertEquals(FieldOperator.EQ, subExp2.operator)
        assert(subExp2.value is FieldValue.BooleanValue)
        assertEquals(true, (subExp2.value as FieldValue.BooleanValue).value)
    }

    @Test
    fun `test complex nested expression with multiple operators and values`() {
        val usernameCondition = FieldExpression(
            field = "username",
            operator = FieldOperator.CONTAINS,
            value = FieldValue.StringValue("admin")
        )

        val roleCondition = FieldExpression(
            field = "role",
            operator = FieldOperator.IN,
            value = FieldValue.ArrayValue(listOf(FieldValue.StringValue("ADMIN"), FieldValue.StringValue("SUPER_ADMIN")))
        )

        val userOrRoleGroup = CompoundExpression(
            operator = CompoundOperator.OR,
            value = listOf(usernameCondition, roleCondition)
        )

        val activeCondition = FieldExpression(
            field = "active",
            operator = FieldOperator.EQ,
            value = FieldValue.BooleanValue(true)
        )

        val lastLoginCondition = FieldExpression(
            field = "lastLoginDays",
            operator = FieldOperator.LT,
            value = FieldValue.NumberValue(30.0)
        )

        val finalExpression = CompoundExpression(
            operator = CompoundOperator.AND,
            value = listOf(userOrRoleGroup, activeCondition, lastLoginCondition)
        )

        val json = objectMapper.writeValueAsString(finalExpression)
        assertEquals("""{"type":"compound","operator":"and","value":[{"type":"compound","operator":"or","value":[{"type":"field","field":"username","operator":"contains","value":"admin"},{"type":"field","field":"role","operator":"in","value":["ADMIN","SUPER_ADMIN"]}]},{"type":"field","field":"active","operator":"eq","value":true},{"type":"field","field":"lastLoginDays","operator":"lt","value":30.0}]}""", json)

        val deserializedExpression = objectMapper.readValue(json, UcastExpression::class.java)

        assert(deserializedExpression is CompoundExpression)
        val resultExpression = deserializedExpression as CompoundExpression
        assertEquals(CompoundOperator.AND, resultExpression.operator)
        assertEquals(3, resultExpression.value.size)

        val orGroup = resultExpression.value[0] as CompoundExpression
        assertEquals(CompoundOperator.OR, orGroup.operator)
        assertEquals(2, orGroup.value.size)

        val usernameResult = orGroup.value[0] as FieldExpression
        assertEquals("username", usernameResult.field)
        assertEquals(FieldOperator.CONTAINS, usernameResult.operator)
        assertEquals("admin", (usernameResult.value as FieldValue.StringValue).value)

        val roleResult = orGroup.value[1] as FieldExpression
        assertEquals("role", roleResult.field)
        assertEquals(FieldOperator.IN, roleResult.operator)
        val roleValues: List<FieldValue> = (roleResult.value as FieldValue.ArrayValue).value
        assertEquals(2, roleValues.size)
        assertEquals("ADMIN", (roleValues[0] as FieldValue.StringValue).value)
        assertEquals("SUPER_ADMIN", (roleValues[1] as FieldValue.StringValue).value)

        val activeResult = resultExpression.value[1] as FieldExpression
        assertEquals("active", activeResult.field)
        assertEquals(FieldOperator.EQ, activeResult.operator)
        assertEquals(true, (activeResult.value as FieldValue.BooleanValue).value)

        val loginResult = resultExpression.value[2] as FieldExpression
        assertEquals("lastLoginDays", loginResult.field)
        assertEquals(FieldOperator.LT, loginResult.operator)
        assertEquals(30.0, (loginResult.value as FieldValue.NumberValue).value)
    }

    @Test
    fun `test deserialization from JSON string`() {
        val jsonString = """
            {
                "type": "compound",
                "operator": "and",
                "value": [
                    {
                        "type": "field",
                        "field": "category",
                        "operator": "eq",
                        "value": "electronics"
                    },
                    {
                        "type": "field",
                        "field": "price",
                        "operator": "lt",
                        "value": 1000
                    },
                    {
                        "type": "compound",
                        "operator": "or",
                        "value": [
                            {
                                "type": "field",
                                "field": "subcategory",
                                "operator": "in",
                                "value": ["safe", "children"]
                            },
                            {
                                "type": "field",
                                "field": "min_age",
                                "operator": "lt",
                                "value": { "field": "input.identity.age" }
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()

        val expression = objectMapper.readValue(jsonString, UcastExpression::class.java)

        // Verifica che expression sia un CompoundExpression con operatore AND
        assertTrue(expression is CompoundExpression)
        val compound = expression as CompoundExpression
        assertEquals(CompoundOperator.AND, compound.operator)
        assertEquals(3, compound.value.size)

        // Primo elemento: field "category" eq "electronics"
        val categoryExpr = compound.value[0] as FieldExpression
        assertEquals("category", categoryExpr.field)
        assertEquals(FieldOperator.EQ, categoryExpr.operator)
        assertTrue(categoryExpr.value is FieldValue.StringValue)
        assertEquals("electronics", (categoryExpr.value as FieldValue.StringValue).value)

        // Secondo elemento: field "price" lt 1000
        val priceExpr = compound.value[1] as FieldExpression
        assertEquals("price", priceExpr.field)
        assertEquals(FieldOperator.LT, priceExpr.operator)
        assertTrue(priceExpr.value is FieldValue.NumberValue)
        assertEquals(1000.0, (priceExpr.value as FieldValue.NumberValue).value)

        // Terzo elemento: compound OR
        val orExpr = compound.value[2] as CompoundExpression
        assertEquals(CompoundOperator.OR, orExpr.operator)
        assertEquals(2, orExpr.value.size)

        // Primo elemento OR: field "subcategory" in ["safe", "children"]
        val subcatExpr = orExpr.value[0] as FieldExpression
        assertEquals("subcategory", subcatExpr.field)
        assertEquals(FieldOperator.IN, subcatExpr.operator)
        assertTrue(subcatExpr.value is FieldValue.ArrayValue)
        val subcatValues = (subcatExpr.value as FieldValue.ArrayValue).value
        assertEquals(2, subcatValues.size)
        assertEquals("safe", (subcatValues[0] as FieldValue.StringValue).value)
        assertEquals("children", (subcatValues[1] as FieldValue.StringValue).value)

        // Secondo elemento OR: field "min_age" lt { field: "input.identity.age" }
        val minAgeExpr = orExpr.value[1] as FieldExpression
        assertEquals("min_age", minAgeExpr.field)
        assertEquals(FieldOperator.LT, minAgeExpr.operator)
        assertTrue(minAgeExpr.value is FieldValue.FieldReference)
        assertEquals("input.identity.age", (minAgeExpr.value as FieldValue.FieldReference).field)
    }
}
