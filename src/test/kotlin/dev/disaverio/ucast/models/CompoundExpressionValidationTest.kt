package dev.disaverio.ucast.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CompoundExpressionValidationTest {

    @Nested
    inner class AndOperatorValidation {
        @Test
        fun `AND operator accepts exactly two operands`() {
            val fieldExp1 = FieldExpression("field1", FieldOperator.EQ, FieldValue.StringValue("value1"))
            val fieldExp2 = FieldExpression("field2", FieldOperator.EQ, FieldValue.StringValue("value2"))

            assertDoesNotThrow {
                CompoundExpression(CompoundOperator.AND, listOf(fieldExp1, fieldExp2))
            }
        }

        @Test
        fun `AND operator accepts more than two operands`() {
            val fieldExp1 = FieldExpression("field1", FieldOperator.EQ, FieldValue.StringValue("value1"))
            val fieldExp2 = FieldExpression("field2", FieldOperator.EQ, FieldValue.StringValue("value2"))
            val fieldExp3 = FieldExpression("field3", FieldOperator.EQ, FieldValue.StringValue("value3"))

            assertDoesNotThrow {
                CompoundExpression(CompoundOperator.AND, listOf(fieldExp1, fieldExp2, fieldExp3))
            }
        }

        @Test
        fun `AND operator rejects less than two operands`() {
            val fieldExp1 = FieldExpression("field1", FieldOperator.EQ, FieldValue.StringValue("value1"))

            val exception = assertThrows(IllegalArgumentException::class.java) {
                CompoundExpression(CompoundOperator.AND, listOf(fieldExp1))
            }

            assertEquals("AND operator requires at least two operands", exception.message)
        }
    }

    @Nested
    inner class OrOperatorValidation {
        @Test
        fun `OR operator accepts exactly two operands`() {
            val fieldExp1 = FieldExpression("field1", FieldOperator.EQ, FieldValue.StringValue("value1"))
            val fieldExp2 = FieldExpression("field2", FieldOperator.EQ, FieldValue.StringValue("value2"))

            assertDoesNotThrow {
                CompoundExpression(CompoundOperator.OR, listOf(fieldExp1, fieldExp2))
            }
        }

        @Test
        fun `OR operator accepts more than two operands`() {
            val fieldExp1 = FieldExpression("field1", FieldOperator.EQ, FieldValue.StringValue("value1"))
            val fieldExp2 = FieldExpression("field2", FieldOperator.EQ, FieldValue.StringValue("value2"))
            val fieldExp3 = FieldExpression("field3", FieldOperator.EQ, FieldValue.StringValue("value3"))

            assertDoesNotThrow {
                CompoundExpression(CompoundOperator.OR, listOf(fieldExp1, fieldExp2, fieldExp3))
            }
        }

        @Test
        fun `OR operator rejects less than two operands`() {
            val fieldExp1 = FieldExpression("field1", FieldOperator.EQ, FieldValue.StringValue("value1"))

            val exception = assertThrows(IllegalArgumentException::class.java) {
                CompoundExpression(CompoundOperator.OR, listOf(fieldExp1))
            }

            assertEquals("OR operator requires at least two operands", exception.message)
        }
    }

    @Nested
    inner class NotOperatorValidation {
        @Test
        fun `NOT operator accepts exactly one operand`() {
            val fieldExp1 = FieldExpression("field1", FieldOperator.EQ, FieldValue.StringValue("value1"))

            assertDoesNotThrow {
                CompoundExpression(CompoundOperator.NOT, listOf(fieldExp1))
            }
        }

        @Test
        fun `NOT operator rejects more than one operand`() {
            val fieldExp1 = FieldExpression("field1", FieldOperator.EQ, FieldValue.StringValue("value1"))
            val fieldExp2 = FieldExpression("field2", FieldOperator.EQ, FieldValue.StringValue("value2"))

            val exception = assertThrows(IllegalArgumentException::class.java) {
                CompoundExpression(CompoundOperator.NOT, listOf(fieldExp1, fieldExp2))
            }

            assertEquals("NOT operator requires exactly one operand", exception.message)
        }

        @Test
        fun `NOT operator rejects empty operand list`() {
            val exception = assertThrows(IllegalArgumentException::class.java) {
                CompoundExpression(CompoundOperator.NOT, emptyList())
            }

            assertEquals("NOT operator requires exactly one operand", exception.message)
        }
    }
}
