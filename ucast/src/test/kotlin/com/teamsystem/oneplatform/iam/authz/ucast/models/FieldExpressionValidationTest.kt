package dev.disaverio.ucast.models

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FieldExpressionValidationTest {

    @Nested
    inner class EqOperatorValidation {
        @Test
        fun `EQ operator accepts string value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.EQ, FieldValue.StringValue("test"))
            }
        }

        @Test
        fun `EQ operator accepts boolean value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.EQ, FieldValue.BooleanValue(true))
            }
        }

        @Test
        fun `EQ operator accepts number value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.EQ, FieldValue.NumberValue(1.0))
            }
        }

        @Test
        fun `EQ operator accepts null value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.EQ, FieldValue.NullValue)
            }
        }

        @Test
        fun `EQ operator accepts field reference value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.EQ, FieldValue.FieldReference("other"))
            }
        }

        @Test
        fun `EQ operator rejects array value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.EQ, FieldValue.ArrayValue(listOf()))
            }
        }
    }

    @Nested
    inner class NeOperatorValidation {
        @Test
        fun `NE operator accepts string value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.NE, FieldValue.StringValue("abc"))
            }
        }

        @Test
        fun `NE operator accepts boolean value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.NE, FieldValue.BooleanValue(false))
            }
        }

        @Test
        fun `NE operator accepts number value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.NE, FieldValue.NumberValue(42.0))
            }
        }

        @Test
        fun `NE operator accepts null value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.NE, FieldValue.NullValue)
            }
        }

        @Test
        fun `NE operator accepts field reference value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.NE, FieldValue.FieldReference("other"))
            }
        }

        @Test
        fun `NE operator rejects array value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.NE, FieldValue.ArrayValue(listOf()))
            }
        }
    }

    @Nested
    inner class InOperatorValidation {
        @Test
        fun `IN operator rejects string value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.IN, FieldValue.StringValue("not-an-array"))
            }
        }

        @Test
        fun `IN operator rejects boolean value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.IN, FieldValue.BooleanValue(true))
            }
        }

        @Test
        fun `IN operator rejects number value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.IN, FieldValue.NumberValue(1.0))
            }
        }

        @Test
        fun `IN operator rejects null value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.IN, FieldValue.NullValue)
            }
        }

        @Test
        fun `IN operator accepts field reference value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.IN, FieldValue.FieldReference("other"))
            }
        }

        @Test
        fun `IN operator accepts array value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.IN, FieldValue.ArrayValue(listOf(FieldValue.StringValue("a"), FieldValue.StringValue("b"))))
            }
        }
    }

    @Nested
    inner class NinOperatorValidation {
        @Test
        fun `NIN operator rejects string value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.NIN, FieldValue.StringValue("abc"))
            }
        }

        @Test
        fun `NIN operator rejects boolean value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.NIN, FieldValue.BooleanValue(true))
            }
        }

        @Test
        fun `NIN operator rejects number value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.NIN, FieldValue.NumberValue(1.0))
            }
        }

        @Test
        fun `NIN operator rejects null value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.NIN, FieldValue.NullValue)
            }
        }

        @Test
        fun `NIN operator accepts field reference value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.NIN, FieldValue.FieldReference("other"))
            }
        }

        @Test
        fun `NIN operator accepts array value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.NIN, FieldValue.ArrayValue(listOf(FieldValue.NumberValue(1.0))))
            }
        }
    }

    @Nested
    inner class LtOperatorValidation {
        @Test
        fun `LT operator rejects string value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.LT, FieldValue.StringValue("not-a-number"))
            }
        }

        @Test
        fun `LT operator rejects boolean value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.LT, FieldValue.BooleanValue(true))
            }
        }

        @Test
        fun `LT operator accepts number value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.LT, FieldValue.NumberValue(10.0))
            }
        }

        @Test
        fun `LT operator rejects null value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.LT, FieldValue.NullValue)
            }
        }

        @Test
        fun `LT operator accepts field reference value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.LT, FieldValue.FieldReference("other"))
            }
        }

        @Test
        fun `LT operator rejects array value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.LT, FieldValue.ArrayValue(listOf()))
            }
        }
    }

    @Nested
    inner class LteOperatorValidation {
        @Test
        fun `LTE operator rejects string value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.LTE, FieldValue.StringValue("not-a-number"))
            }
        }

        @Test
        fun `LTE operator rejects boolean value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.LTE, FieldValue.BooleanValue(false))
            }
        }

        @Test
        fun `LTE operator accepts number value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.LTE, FieldValue.NumberValue(10.0))
            }
        }

        @Test
        fun `LTE operator rejects null value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.LTE, FieldValue.NullValue)
            }
        }

        @Test
        fun `LTE operator accepts field reference value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.LTE, FieldValue.FieldReference("other"))
            }
        }

        @Test
        fun `LTE operator rejects array value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.LTE, FieldValue.ArrayValue(listOf()))
            }
        }
    }

    @Nested
    inner class GtOperatorValidation {
        @Test
        fun `GT operator rejects string value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.GT, FieldValue.StringValue("not-a-number"))
            }
        }

        @Test
        fun `GT operator rejects boolean value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.GT, FieldValue.BooleanValue(true))
            }
        }

        @Test
        fun `GT operator accepts number value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.GT, FieldValue.NumberValue(10.0))
            }
        }

        @Test
        fun `GT operator rejects null value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.GT, FieldValue.NullValue)
            }
        }

        @Test
        fun `GT operator accepts field reference value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.GT, FieldValue.FieldReference("other"))
            }
        }

        @Test
        fun `GT operator rejects array value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.GT, FieldValue.ArrayValue(listOf()))
            }
        }
    }

    @Nested
    inner class GteOperatorValidation {
        @Test
        fun `GTE operator rejects string value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.GTE, FieldValue.StringValue("not-a-number"))
            }
        }

        @Test
        fun `GTE operator rejects boolean value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.GTE, FieldValue.BooleanValue(false))
            }
        }

        @Test
        fun `GTE operator accepts number value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.GTE, FieldValue.NumberValue(10.0))
            }
        }

        @Test
        fun `GTE operator rejects null value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.GTE, FieldValue.NullValue)
            }
        }

        @Test
        fun `GTE operator accepts field reference value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.GTE, FieldValue.FieldReference("other"))
            }
        }

        @Test
        fun `GTE operator rejects array value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.GTE, FieldValue.ArrayValue(listOf()))
            }
        }
    }

    @Nested
    inner class ContainsOperatorValidation {
        @Test
        fun `CONTAINS operator accepts string value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.CONTAINS, FieldValue.StringValue("substring"))
            }
        }

        @Test
        fun `CONTAINS operator accepts boolean value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.CONTAINS, FieldValue.BooleanValue(true))
            }
        }

        @Test
        fun `CONTAINS operator accepts number value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.CONTAINS, FieldValue.NumberValue(123.0))
            }
        }

        @Test
        fun `CONTAINS operator accepts null value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.CONTAINS, FieldValue.NullValue)
            }
        }

        @Test
        fun `CONTAINS operator accepts field reference value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.CONTAINS, FieldValue.FieldReference("other"))
            }
        }

        @Test
        fun `CONTAINS operator rejects array value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.CONTAINS, FieldValue.ArrayValue(listOf()))
            }
        }
    }

    @Nested
    inner class StartsWithOperatorValidation {
        @Test
        fun `STARTS_WITH operator accepts string value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.STARTS_WITH, FieldValue.StringValue("prefix"))
            }
        }

        @Test
        fun `STARTS_WITH operator rejects boolean value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.STARTS_WITH, FieldValue.BooleanValue(true))
            }
        }

        @Test
        fun `STARTS_WITH operator rejects number value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.STARTS_WITH, FieldValue.NumberValue(1.0))
            }
        }

        @Test
        fun `STARTS_WITH operator rejects null value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.STARTS_WITH, FieldValue.NullValue)
            }
        }

        @Test
        fun `STARTS_WITH operator accepts field reference value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.STARTS_WITH, FieldValue.FieldReference("other"))
            }
        }

        @Test
        fun `STARTS_WITH operator rejects array value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.STARTS_WITH, FieldValue.ArrayValue(listOf()))
            }
        }
    }

    @Nested
    inner class EndsWithOperatorValidation {
        @Test
        fun `ENDS_WITH operator accepts string value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.ENDS_WITH, FieldValue.StringValue("suffix"))
            }
        }

        @Test
        fun `ENDS_WITH operator rejects boolean value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.ENDS_WITH, FieldValue.BooleanValue(true))
            }
        }

        @Test
        fun `ENDS_WITH operator rejects number value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.ENDS_WITH, FieldValue.NumberValue(1.0))
            }
        }

        @Test
        fun `ENDS_WITH operator rejects null value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.ENDS_WITH, FieldValue.NullValue)
            }
        }

        @Test
        fun `ENDS_WITH operator accepts field reference value`() {
            assertDoesNotThrow {
                FieldExpression("field", FieldOperator.ENDS_WITH, FieldValue.FieldReference("other"))
            }
        }

        @Test
        fun `ENDS_WITH operator rejects array value`() {
            assertThrows(IllegalArgumentException::class.java) {
                FieldExpression("field", FieldOperator.ENDS_WITH, FieldValue.ArrayValue(listOf()))
            }
        }
    }
}

