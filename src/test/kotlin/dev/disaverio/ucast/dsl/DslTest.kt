package dev.disaverio.ucast.dsl

import dev.disaverio.ucast.models.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class DslTest {

    @Nested
    inner class FieldExpressionDslTest {

        @Test
        fun `test EQ field expression dsl constructor`() {

            val exp = "field1" eq "value1"

            assertEquals("field1", exp.field)
            assertEquals(FieldOperator.EQ, exp.operator)
            assertEquals(FieldValue.StringValue("value1"), exp.value)
        }

        @Test
        fun `test NE field expression dsl constructor`() {

            val exp = "field4" ne "value2"

            assertEquals("field4", exp.field)
            assertEquals(FieldOperator.NE, exp.operator)
            assertEquals(FieldValue.StringValue("value2"), exp.value)
        }

        @Test
        fun `test LT field expression dsl constructor`() {

            val exp = "field3" lt 20.5

            assertEquals("field3", exp.field)
            assertEquals(FieldOperator.LT, exp.operator)
            assertEquals(FieldValue.NumberValue(20.5), exp.value)
        }

        @Test
        fun `test LTE field expression dsl constructor`() {

            val exp = "field2" lte 15

            assertEquals("field2", exp.field)
            assertEquals(FieldOperator.LTE, exp.operator)
            assertEquals(FieldValue.NumberValue(15.0), exp.value)
        }

        @Test
        fun `test GT field expression dsl constructor`() {

            val exp = "field2" gt 10

            assertEquals("field2", exp.field)
            assertEquals(FieldOperator.GT, exp.operator)
            assertEquals(FieldValue.NumberValue(10.0), exp.value)
        }

        @Test
        fun `test GTE field expression dsl constructor`() {

            val exp = "field3" gte 5.5

            assertEquals("field3", exp.field)
            assertEquals(FieldOperator.GTE, exp.operator)
            assertEquals(FieldValue.NumberValue(5.5), exp.value)
        }

        @Test
        fun `test IN field expression dsl constructor`() {

            val exp = "field5" `in` listOf("value3", "value4")

            assertEquals("field5", exp.field)
            assertEquals(FieldOperator.IN, exp.operator)
            assertEquals(FieldValue.ArrayValue(listOf(FieldValue.StringValue("value3"), FieldValue.StringValue("value4"))), exp.value)
        }

        @Test
        fun `test NIN field expression dsl constructor`() {

            val exp = "field6" nin listOf("value5", "value6")

            assertEquals("field6", exp.field)
            assertEquals(FieldOperator.NIN, exp.operator)
            assertEquals(FieldValue.ArrayValue(listOf(FieldValue.StringValue("value5"), FieldValue.StringValue("value6"))), exp.value)
        }

        @Test
        fun `test CONTAINS field expression dsl constructor`() {

            val exp = "field7" contains "value7"

            assertEquals("field7", exp.field)
            assertEquals(FieldOperator.CONTAINS, exp.operator)
            assertEquals(FieldValue.StringValue("value7"), exp.value)
        }

        @Test
        fun `test STARTS_WITH field expression dsl constructor`() {

            val exp = "field8".startsWith("value8")

            assertEquals("field8", exp.field)
            assertEquals(FieldOperator.STARTS_WITH, exp.operator)
            assertEquals(FieldValue.StringValue("value8"), exp.value)
        }

        @Test
        fun `test ENDS_WITH field expression dsl constructor`() {

            val exp = "field9".endsWith("value9")

            assertEquals("field9", exp.field)
            assertEquals(FieldOperator.ENDS_WITH, exp.operator)
            assertEquals(FieldValue.StringValue("value9"), exp.value)
        }
    }

    @Nested
    inner class CompoundExpressionDslTest {

        @Test
        fun `test AND conditions`() {

            val exp = and {
                "field1" eq "value1"
                "field2" gt 10
                "field3" lt 20.5
            }

            assertEquals(CompoundOperator.AND, exp.operator)
            assertEquals(3, exp.value.size)

            val firstCondition = exp.value[0] as FieldExpression
            assertEquals("field1", firstCondition.field)
            assertEquals(FieldValue.StringValue("value1"), firstCondition.value)

            val secondCondition = exp.value[1] as FieldExpression
            assertEquals("field2", secondCondition.field)
            assertEquals(FieldValue.NumberValue(10.0), secondCondition.value)

            val thirdCondition = exp.value[2] as FieldExpression
            assertEquals("field3", thirdCondition.field)
            assertEquals(FieldValue.NumberValue(20.5), thirdCondition.value)
        }

        @Test
        fun `test OR conditions`() {

            val exp = or {
                "field1" eq "value1"
                "field2" gt 10
                "field3" lt 20.5
            }

            assertEquals(CompoundOperator.OR, exp.operator)
            assertEquals(3, exp.value.size)

            val firstCondition = exp.value[0] as FieldExpression
            assertEquals("field1", firstCondition.field)
            assertEquals(FieldValue.StringValue("value1"), firstCondition.value)

            val secondCondition = exp.value[1] as FieldExpression
            assertEquals("field2", secondCondition.field)
            assertEquals(FieldValue.NumberValue(10.0), secondCondition.value)

            val thirdCondition = exp.value[2] as FieldExpression
            assertEquals("field3", thirdCondition.field)
            assertEquals(FieldValue.NumberValue(20.5), thirdCondition.value)
        }

        @Test
        fun `test NOT conditions`() {

            val exp = not { "field1" eq "value1" }

            assertEquals(CompoundOperator.NOT, exp.operator)
            assertEquals(1, exp.value.size)

            val condition = exp.value.single() as FieldExpression
            assertEquals("field1", condition.field)
            assertEquals(FieldValue.StringValue("value1"), condition.value)
        }
    }

    @Nested
    inner class MixinDslTest {

        @Test
        fun `test a full expression with mixin DSL, starting with OR`() {

            val exp = or {
                "field0" eq true
                "field1" ne null
                "field2" lt 10
                and {
                    "field3" lte otherField("resource.other_field")
                    "field4" gt 12
                    "field5" gte 13
                    "field6" `in` listOf("value1", "value2", "value3")
                    "field7" nin listOf("value4", "value5", "value6")
                    not {
                        and {
                            "field8" contains "pluto"
                            "field9".startsWith("starter")
                            "field10".endsWith("ender")
                        }
                    }
                    or {
                        "field11" contains "pluto"
                        "field12".startsWith("starter")
                        "field13".endsWith("ender")
                        not {
                            or {
                                "field14" eq "value11"
                                "field15" ne "value12"
                            }
                        }
                    }
                }
                not { "field2" lt 10 }
            }

            assertEquals(CompoundOperator.OR, exp.operator)
            assertEquals(5, exp.value.size)

            // Primo livello: controlla i FieldExpression e il CompoundExpression interno
            val first = exp.value[0] as FieldExpression
            assertEquals("field0", first.field)
            assertEquals(FieldValue.BooleanValue(true), first.value)
            assertEquals(FieldOperator.EQ, first.operator)

            val second = exp.value[1] as FieldExpression
            assertEquals("field1", second.field)
            assertEquals(FieldValue.NullValue, second.value)
            assertEquals(FieldOperator.NE, second.operator)

            val third = exp.value[2] as FieldExpression
            assertEquals("field2", third.field)
            assertEquals(FieldValue.NumberValue(10.0), third.value)
            assertEquals(FieldOperator.LT, third.operator)

            // Quarto elemento: CompoundExpression AND
            val andExp = exp.value[3] as CompoundExpression
            assertEquals(CompoundOperator.AND, andExp.operator)
            assertEquals(7, andExp.value.size)

            // Verifica i FieldExpression dentro l'AND
            val andFirst = andExp.value[0] as FieldExpression
            assertEquals("field3", andFirst.field)
            assertEquals(FieldValue.FieldReference("resource.other_field"), andFirst.value)
            assertEquals(FieldOperator.LTE, andFirst.operator)

            val andSecond = andExp.value[1] as FieldExpression
            assertEquals("field4", andSecond.field)
            assertEquals(FieldValue.NumberValue(12.0), andSecond.value)
            assertEquals(FieldOperator.GT, andSecond.operator)

            val andThird = andExp.value[2] as FieldExpression
            assertEquals("field5", andThird.field)
            assertEquals(FieldValue.NumberValue(13.0), andThird.value)
            assertEquals(FieldOperator.GTE, andThird.operator)

            val andFourth = andExp.value[3] as FieldExpression
            assertEquals("field6", andFourth.field)
            assertEquals(
                FieldValue.ArrayValue(listOf(
                FieldValue.StringValue("value1"),
                FieldValue.StringValue("value2"),
                FieldValue.StringValue("value3")
            )), andFourth.value)
            assertEquals(FieldOperator.IN, andFourth.operator)

            val andFifth = andExp.value[4] as FieldExpression
            assertEquals("field7", andFifth.field)
            assertEquals(
                FieldValue.ArrayValue(listOf(
                FieldValue.StringValue("value4"),
                FieldValue.StringValue("value5"),
                FieldValue.StringValue("value6")
            )), andFifth.value)
            assertEquals(FieldOperator.NIN, andFifth.operator)

            // Sesto elemento dell'AND: CompoundExpression NOT
            val innerNot = andExp.value[5] as CompoundExpression
            assertEquals(CompoundOperator.NOT, innerNot.operator)
            assertEquals(1, innerNot.value.size)

            val notInnerAnd = innerNot.value[0] as CompoundExpression
            assertEquals(CompoundOperator.AND, notInnerAnd.operator)
            assertEquals(3, notInnerAnd.value.size)

            val notAndFirst = notInnerAnd.value[0] as FieldExpression
            assertEquals("field8", notAndFirst.field)
            assertEquals(FieldValue.StringValue("pluto"), notAndFirst.value)
            assertEquals(FieldOperator.CONTAINS, notAndFirst.operator)

            val notAndSecond = notInnerAnd.value[1] as FieldExpression
            assertEquals("field9", notAndSecond.field)
            assertEquals(FieldValue.StringValue("starter"), notAndSecond.value)
            assertEquals(FieldOperator.STARTS_WITH, notAndSecond.operator)

            val notAndThird = notInnerAnd.value[2] as FieldExpression
            assertEquals("field10", notAndThird.field)
            assertEquals(FieldValue.StringValue("ender"), notAndThird.value)
            assertEquals(FieldOperator.ENDS_WITH, notAndThird.operator)

            // Settimo elemento dell'AND: CompoundExpression OR
            val innerOr = andExp.value[6] as CompoundExpression
            assertEquals(CompoundOperator.OR, innerOr.operator)
            assertEquals(4, innerOr.value.size)

            val orFirst = innerOr.value[0] as FieldExpression
            assertEquals("field11", orFirst.field)
            assertEquals(FieldValue.StringValue("pluto"), orFirst.value)
            assertEquals(FieldOperator.CONTAINS, orFirst.operator)

            val orSecond = innerOr.value[1] as FieldExpression
            assertEquals("field12", orSecond.field)
            assertEquals(FieldValue.StringValue("starter"), orSecond.value)
            assertEquals(FieldOperator.STARTS_WITH, orSecond.operator)

            val orThird = innerOr.value[2] as FieldExpression
            assertEquals("field13", orThird.field)
            assertEquals(FieldValue.StringValue("ender"), orThird.value)
            assertEquals(FieldOperator.ENDS_WITH, orThird.operator)

            // Ottavo elemento dell'OR: CompoundExpression NOT
            val innerNotOr = innerOr.value[3] as CompoundExpression
            assertEquals(CompoundOperator.NOT, innerNotOr.operator)
            assertEquals(1, innerNotOr.value.size)

            val notInnerOr = innerNotOr.value[0] as CompoundExpression
            assertEquals(CompoundOperator.OR, notInnerOr.operator)
            assertEquals(2, notInnerOr.value.size)

            val notOrFirst = notInnerOr.value[0] as FieldExpression
            assertEquals("field14", notOrFirst.field)
            assertEquals(FieldValue.StringValue("value11"), notOrFirst.value)
            assertEquals(FieldOperator.EQ, notOrFirst.operator)

            val notOrSecond = notInnerOr.value[1] as FieldExpression
            assertEquals("field15", notOrSecond.field)
            assertEquals(FieldValue.StringValue("value12"), notOrSecond.value)
            assertEquals(FieldOperator.NE, notOrSecond.operator)

            // Nono elemento: NOT field2 lt 10
            val notField2 = exp.value[4] as CompoundExpression
            assertEquals(CompoundOperator.NOT, notField2.operator)
            assertEquals(1, notField2.value.size)

            val notField2Condition = notField2.value[0] as FieldExpression
            assertEquals("field2", notField2Condition.field)
            assertEquals(FieldValue.NumberValue(10.0), notField2Condition.value)
            assertEquals(FieldOperator.LT, notField2Condition.operator)

        }

        @Test
        fun `test a full expression with mixin DSL, starting with AND`() {

            val exp = and {
                "field0" eq true
                "field1" ne null
                "field2" lt 10
                or {
                    "field3" lte otherField("resource.other_field")
                    "field4" gt 12
                    "field5" gte 13
                    "field6" `in` listOf("value1", "value2", "value3")
                    "field7" nin listOf("value4", "value5", "value6")
                    and {
                        "field8" contains "pluto"
                        "field9".startsWith("starter")
                        "field10".endsWith("ender")
                    }
                }
            }

            // Verifica il tipo e l'operatore principale
            assertEquals(CompoundOperator.AND, exp.operator)
            assertEquals(4, exp.value.size)

            // Verifica i primi tre FieldExpression
            val first = exp.value[0] as FieldExpression
            assertEquals("field0", first.field)
            assertEquals(FieldValue.BooleanValue(true), first.value)
            assertEquals(FieldOperator.EQ, first.operator)

            val second = exp.value[1] as FieldExpression
            assertEquals("field1", second.field)
            assertEquals(FieldValue.NullValue, second.value)
            assertEquals(FieldOperator.NE, second.operator)

            val third = exp.value[2] as FieldExpression
            assertEquals("field2", third.field)
            assertEquals(FieldValue.NumberValue(10.0), third.value)
            assertEquals(FieldOperator.LT, third.operator)

            // Verifica il CompoundExpression OR interno
            val innerOr = exp.value[3] as CompoundExpression
            assertEquals(CompoundOperator.OR, innerOr.operator)
            assertEquals(6, innerOr.value.size)

            // Verifica i FieldExpression dentro l'OR
            val orFirst = innerOr.value[0] as FieldExpression
            assertEquals("field3", orFirst.field)
            assertEquals(FieldValue.FieldReference("resource.other_field"), orFirst.value)
            assertEquals(FieldOperator.LTE, orFirst.operator)

            val orSecond = innerOr.value[1] as FieldExpression
            assertEquals("field4", orSecond.field)
            assertEquals(FieldValue.NumberValue(12.0), orSecond.value)
            assertEquals(FieldOperator.GT, orSecond.operator)

            val orThird = innerOr.value[2] as FieldExpression
            assertEquals("field5", orThird.field)
            assertEquals(FieldValue.NumberValue(13.0), orThird.value)
            assertEquals(FieldOperator.GTE, orThird.operator)

            val orFourth = innerOr.value[3] as FieldExpression
            assertEquals("field6", orFourth.field)
            assertEquals(
                FieldValue.ArrayValue(listOf(
                FieldValue.StringValue("value1"),
                FieldValue.StringValue("value2"),
                FieldValue.StringValue("value3")
            )), orFourth.value)
            assertEquals(FieldOperator.IN, orFourth.operator)

            val orFifth = innerOr.value[4] as FieldExpression
            assertEquals("field7", orFifth.field)
            assertEquals(
                FieldValue.ArrayValue(listOf(
                FieldValue.StringValue("value4"),
                FieldValue.StringValue("value5"),
                FieldValue.StringValue("value6")
            )), orFifth.value)
            assertEquals(FieldOperator.NIN, orFifth.operator)

            // Verifica l'AND interno all'OR
            val innerAnd = innerOr.value[5] as CompoundExpression
            assertEquals(CompoundOperator.AND, innerAnd.operator)
            assertEquals(3, innerAnd.value.size)

            val andFirst = innerAnd.value[0] as FieldExpression
            assertEquals("field8", andFirst.field)
            assertEquals(FieldValue.StringValue("pluto"), andFirst.value)
            assertEquals(FieldOperator.CONTAINS, andFirst.operator)

            val andSecond = innerAnd.value[1] as FieldExpression
            assertEquals("field9", andSecond.field)
            assertEquals(FieldValue.StringValue("starter"), andSecond.value)
            assertEquals(FieldOperator.STARTS_WITH, andSecond.operator)

            val andThird = innerAnd.value[2] as FieldExpression
            assertEquals("field10", andThird.field)
            assertEquals(FieldValue.StringValue("ender"), andThird.value)
            assertEquals(FieldOperator.ENDS_WITH, andThird.operator)
        }

        @Test
        fun `test a AND of ORs`() {

            val exp = and {
                or {
                    "field0" eq true
                    "field1" ne null
                }
                or {
                    "field2" lt 10
                    "field3" gt 20
                }
            }

            // Verifica il tipo e l'operatore principale
            assertEquals(CompoundOperator.AND, exp.operator)
            assertEquals(2, exp.value.size)

            // Verifica i CompoundExpression OR
            val firstOr = exp.value[0] as CompoundExpression
            assertEquals(CompoundOperator.OR, firstOr.operator)
            assertEquals(2, firstOr.value.size)

            val firstOrFirst = firstOr.value[0] as FieldExpression
            assertEquals("field0", firstOrFirst.field)
            assertEquals(FieldValue.BooleanValue(true), firstOrFirst.value)
            assertEquals(FieldOperator.EQ, firstOrFirst.operator)

            val firstOrSecond = firstOr.value[1] as FieldExpression
            assertEquals("field1", firstOrSecond.field)
            assertEquals(FieldValue.NullValue, firstOrSecond.value)
            assertEquals(FieldOperator.NE, firstOrSecond.operator)

            val secondOr = exp.value[1] as CompoundExpression
            assertEquals(CompoundOperator.OR, secondOr.operator)
            assertEquals(2, secondOr.value.size)

            val secondOrFirst = secondOr.value[0] as FieldExpression
            assertEquals("field2", secondOrFirst.field)
            assertEquals(FieldValue.NumberValue(10.0), secondOrFirst.value)
            assertEquals(FieldOperator.LT, secondOrFirst.operator)

            val secondOrSecond = secondOr.value[1] as FieldExpression
            assertEquals("field3", secondOrSecond.field)
            assertEquals(FieldValue.NumberValue(20.0), secondOrSecond.value)
            assertEquals(FieldOperator.GT, secondOrSecond.operator)
        }

        @Test
        fun `test a OR of ANDs`() {

            val exp = or {
                and {
                    "field0" eq true
                    "field1" ne null
                }
                and {
                    "field2" lt 10
                    "field3" gt 20
                }
            }

            // Verifica il tipo e l'operatore principale
            assertEquals(CompoundOperator.OR, exp.operator)
            assertEquals(2, exp.value.size)

            // Verifica i CompoundExpression AND
            val firstAnd = exp.value[0] as CompoundExpression
            assertEquals(CompoundOperator.AND, firstAnd.operator)
            assertEquals(2, firstAnd.value.size)

            val firstAndFirst = firstAnd.value[0] as FieldExpression
            assertEquals("field0", firstAndFirst.field)
            assertEquals(FieldValue.BooleanValue(true), firstAndFirst.value)
            assertEquals(FieldOperator.EQ, firstAndFirst.operator)

            val firstAndSecond = firstAnd.value[1] as FieldExpression
            assertEquals("field1", firstAndSecond.field)
            assertEquals(FieldValue.NullValue, firstAndSecond.value)
            assertEquals(FieldOperator.NE, firstAndSecond.operator)

            val secondAnd = exp.value[1] as CompoundExpression
            assertEquals(CompoundOperator.AND, secondAnd.operator)
            assertEquals(2, secondAnd.value.size)

            val secondAndFirst = secondAnd.value[0] as FieldExpression
            assertEquals("field2", secondAndFirst.field)
            assertEquals(FieldValue.NumberValue(10.0), secondAndFirst.value)
            assertEquals(FieldOperator.LT, secondAndFirst.operator)

            val secondAndSecond = secondAnd.value[1] as FieldExpression
            assertEquals("field3", secondAndSecond.field)
            assertEquals(FieldValue.NumberValue(20.0), secondAndSecond.value)
            assertEquals(FieldOperator.GT, secondAndSecond.operator)
        }

        @Test
        fun `test a dynamic creation`() {

            val elements = listOf(
                Pair("field0", true),
                Pair("field1", null),
                Pair("field2", 10)
            )

            val exp = and {
                for ((field, value) in elements) {
                    field eq value
                }
                or {
                    for ((field, value) in elements) {
                        field ne value
                    }
                }
            }

            // Verifica il tipo e l'operatore principale
            assertEquals(CompoundOperator.AND, exp.operator)
            assertEquals(4, exp.value.size)

            // Verifica i primi tre FieldExpression
            val first = exp.value[0] as FieldExpression
            assertEquals("field0", first.field)
            assertEquals(FieldValue.BooleanValue(true), first.value)
            assertEquals(FieldOperator.EQ, first.operator)

            val second = exp.value[1] as FieldExpression
            assertEquals("field1", second.field)
            assertEquals(FieldValue.NullValue, second.value)
            assertEquals(FieldOperator.EQ, second.operator)

            val third = exp.value[2] as FieldExpression
            assertEquals("field2", third.field)
            assertEquals(FieldValue.NumberValue(10.0), third.value)
            assertEquals(FieldOperator.EQ, third.operator)

            // Verifica il CompoundExpression OR interno
            val innerOr = exp.value[3] as CompoundExpression
            assertEquals(CompoundOperator.OR, innerOr.operator)
            assertEquals(3, innerOr.value.size)

            // Verifica i FieldExpression dentro l'OR
            val orFirst = innerOr.value[0] as FieldExpression
            assertEquals("field0", orFirst.field)
            assertEquals(FieldValue.BooleanValue(true), orFirst.value)
            assertEquals(FieldOperator.NE, orFirst.operator)

            val orSecond = innerOr.value[1] as FieldExpression
            assertEquals("field1", orSecond.field)
            assertEquals(FieldValue.NullValue, orSecond.value)
            assertEquals(FieldOperator.NE, orSecond.operator)

            val orThird = innerOr.value[2] as FieldExpression
            assertEquals("field2", orThird.field)
            assertEquals(FieldValue.NumberValue(10.0), orThird.value)
            assertEquals(FieldOperator.NE, orThird.operator)
        }
    }
}
