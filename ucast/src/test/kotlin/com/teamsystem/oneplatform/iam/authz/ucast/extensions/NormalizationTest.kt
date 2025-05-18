package dev.disaverio.ucast.extensions

import dev.disaverio.ucast.dsl.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NormalizationTest {

    @Nested
    inner class BasicExpressions {
        @Test
        fun `test normalization of field expression 1`() {
            val fe = "field1" gt 24
            assertEquals(fe, fe.normalize())
        }

        @Test
        fun `test normalization of field expression 2`() {
            val fe = "field2" startsWith "pluto"
            assertEquals(fe, fe.normalize())
        }

        @Test
        fun `test normalization of field expression 3`() {
            val fe = "field3" ne true
            assertEquals(fe, fe.normalize())
        }

        @Test
        fun `test normalization of not expression 1`() {
            val not = not { "field1" gt 24 }
            assertEquals(not, not.normalize())
        }

        @Test
        fun `test normalization of not expression 5`() {
            val not = not { "field2" startsWith "pluto" }
            assertEquals(not, not.normalize())
        }

        @Test
        fun `test normalization of not expression 6`() {
            val not = not { "field3" ne true }
            assertEquals(not, not.normalize())
        }
    }

    @Nested
    inner class CompoundNOTExpressions {
        @Test
        fun `test normalization of compound NOT expressions 1`() {
            val Not_Not = not { not { "field1" gt 24 } }
            assertEquals("field1" gt 24, Not_Not.normalize())
        }

        @Test
        fun `test normalization of compound NOT expressions 2`() {
            val Not_Or = not {
                or {
                    "field1" gt 24
                    "field2" startsWith "pluto"
                }
            }
            val normalized_Not_Or = and {
                not { "field1" gt 24 }
                not { "field2" startsWith "pluto" }
            }
            assertEquals(normalized_Not_Or, Not_Or.normalize())
        }

        @Test
        fun `test normalization of compound NOT expressions 3`() {
            val Not_And = not { and {
                "field1" gt 24
                "field2" startsWith "pluto"
            } }
            val normalized_Not_And = or {
                not { "field1" gt 24 }
                not {  "field2" startsWith "pluto" }
            }
            assertEquals(normalized_Not_And, Not_And.normalize())
        }
    }

    @Nested
    inner class SimpleCompoundORExpressions {
        @Test
        fun `test normalization of simple OR expressions 1`() {
            val Or_Not1Fe1 = or {
                not { "field1" gt 24 }
                "field1" gt 24
            }
            assertEquals(Or_Not1Fe1, Or_Not1Fe1.normalize())
        }

        @Test
        fun `test normalization of simple OR expressions 2`() {
            val Or_Not1Not2 = or {
                not { "field1" gt 24 }
                not { "field2" startsWith "pluto" }
            }
            assertEquals(Or_Not1Not2, Or_Not1Not2.normalize())
        }

        @Test
        fun `test normalization of simple OR expressions 3`() {
            val Or_Or1Fe1 = or {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                "field1" gt 24
            }
            val normalized_Or_Or1Fe1 = or { "field1" gt 24; "field2" startsWith "pluto"; "field1" gt 24 }
            assertEquals(normalized_Or_Or1Fe1, Or_Or1Fe1.normalize())
        }

        @Test
        fun `test normalization of simple OR expressions 4`() {
            val Or_Or1Not1 = or {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                not { "field1" gt 24 }
            }
            val normalized_Or_Or1Not1 = or { "field1" gt 24; "field2" startsWith "pluto"; not { "field1" gt 24 } }
            assertEquals(normalized_Or_Or1Not1, Or_Or1Not1.normalize())
        }

        @Test
        fun `test normalization of simple OR expressions 5`() {
            val Or_Or1Or2 = or {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
            }
            val normalized_Or_Or1Or2 = or { "field1" gt 24; "field2" startsWith "pluto"; "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
            assertEquals(normalized_Or_Or1Or2, Or_Or1Or2.normalize())
        }
    }

    @Nested
    inner class CompoundORWithANDExpressions {
        @Test
        fun `test normalization of complex OR expressions with AND subexpressions 1`() {
            val Or_And1Fe1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                "field1" gt 24
            }
            assertEquals(Or_And1Fe1, Or_And1Fe1.normalize())
        }

        @Test
        fun `test normalization of complex OR expressions with AND subexpressions 2`() {
            val Or_And1Not1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                not { "field1" gt 24 }
            }
            assertEquals(Or_And1Not1, Or_And1Not1.normalize())
        }

        @Test
        fun `test normalization of complex OR expressions with AND subexpressions 3`() {
            val Or_And1And2 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                and { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
            }
            assertEquals(Or_And1And2, Or_And1And2.normalize())
        }

        @Test
        fun `test normalization of complex OR expressions with AND subexpressions 4`() {
            val Or_And1Or1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field1" gt 24; "field2" startsWith "pluto" }
            }
            val normalized_Or_And1Or1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                "field1" gt 24
                "field2" startsWith "pluto"
            }
            assertEquals(normalized_Or_And1Or1, Or_And1Or1.normalize())
        }
    }

    @Nested
    inner class CompundORWithMultipleSubexpressions {
        @Test
        fun `test normalization of complex OR expressions with multiple subexpressions 1`() {
            val Or_Fe1Fe2Fe3 = or {
                "field1" gt 24
                "field2" startsWith "pluto"
                "field3" ne true
            }
            assertEquals(Or_Fe1Fe2Fe3, Or_Fe1Fe2Fe3.normalize())
        }
        @Test
        fun `test normalization of complex OR expressions with multiple subexpressions 2`() {
            val Or_Not1Fe1Fe2 = or {
                not { "field1" gt 24 }
                "field1" gt 24
                "field2" startsWith "pluto"
            }
            assertEquals(Or_Not1Fe1Fe2, Or_Not1Fe1Fe2.normalize())
        }

        @Test
        fun `test normalization of complex OR expressions with multiple subexpressions 3`() {
            val Or_Not1Not2Fe1 = or {
                not { "field1" gt 24 }
                not { "field2" startsWith "pluto" }
                "field1" gt 24
            }
            assertEquals(Or_Not1Not2Fe1, Or_Not1Not2Fe1.normalize())
        }

        @Test
        fun `test normalization of complex OR expressions with multiple subexpressions 4`() {
            val Or_Not1Not2Not3 = or {
                not { "field1" gt 24 }
                not { "field2" startsWith "pluto" }
                not { "field3" ne true }
            }
            assertEquals(Or_Not1Not2Not3, Or_Not1Not2Not3.normalize())
        }

        @Test
        fun `test normalization of complex OR expressions with multiple subexpressions 5`() {
            val Or_Or1Fe1Fe2 = or {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                "field1" gt 24
                "field2" startsWith "pluto"
            }
            val normalized_Or_Or1Fe1Fe2 = or {
                "field1" gt 24
                "field2" startsWith "pluto"
                "field1" gt 24
                "field2" startsWith "pluto"
            }
            assertEquals(normalized_Or_Or1Fe1Fe2, Or_Or1Fe1Fe2.normalize())
        }

        @Test
        fun `test normalization of complex OR expressions with multiple subexpressions 6`() {
            val Or_Or1Not1Fe1 = or {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                not { "field1" gt 24 }
                "field1" gt 24
            }
            val normalized_Or_Or1Not1Fe1 = or {
                "field1" gt 24
                "field2" startsWith "pluto"
                not { "field1" gt 24 }
                "field1" gt 24
            }
            assertEquals(normalized_Or_Or1Not1Fe1, Or_Or1Not1Fe1.normalize())
        }

        @Test
        fun `test normalization of complex OR expressions with multiple subexpressions 7`() {
            val Or_Or1Not1Not2 = or {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                not { "field1" gt 24 }
                not { "field2" startsWith "pluto" }
            }
            val normalized_Or_Or1Not1Not2 = or {
                "field1" gt 24
                "field2" startsWith "pluto"
                not { "field1" gt 24 }
                not { "field2" startsWith "pluto" }
            }
            assertEquals(normalized_Or_Or1Not1Not2, Or_Or1Not1Not2.normalize())
        }

        @Test
        fun `test normalization of complex OR expressions with multiple subexpressions 8`() {
            val Or_Or1Or2Fe1 = or {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                "field1" gt 24
            }
            val normalized_Or_Or1Or2Fe1 = or {
                "field1" gt 24
                "field2" startsWith "pluto"
                "field3" ne true
                "field4" `in` listOf("pippo", "paperino")
                "field1" gt 24
            }
            assertEquals(normalized_Or_Or1Or2Fe1, Or_Or1Or2Fe1.normalize())
        }

        @Test
        fun `test normalization of complex OR expressions with multiple subexpressions 9`() {
            val Or_Or1Or2Not1 = or {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                not { "field1" gt 24 }
            }
            val normalized_Or_Or1Or2Not1 = or {
                "field1" gt 24
                "field2" startsWith "pluto"
                "field3" ne true
                "field4" `in` listOf("pippo", "paperino")
                not { "field1" gt 24 }
            }
            assertEquals(normalized_Or_Or1Or2Not1, Or_Or1Or2Not1.normalize())
        }

        @Test
        fun `test normalization of complex OR expressions with multiple subexpressions 10`() {
            val Or_Or1Or2Or3 = or {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                or { "field5" eq null; "field6" lt otherField("otherField") }
            }
            val normalized_Or_Or1Or2Or3 = or {
                "field1" gt 24
                "field2" startsWith "pluto"
                "field3" ne true
                "field4" `in` listOf("pippo", "paperino")
                "field5" eq null
                "field6" lt otherField("otherField")
            }
            assertEquals(normalized_Or_Or1Or2Or3, Or_Or1Or2Or3.normalize())
        }
    }

    @Nested
    inner class CompundORWithANDAndOtherSubexpressions {
        @Test
        fun `test normalization of OR expressions with AND and other subexpressions 1`() {
            val Or_And1Fe1Fe2 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                "field1" gt 24
                "field2" startsWith "pluto"
            }
            assertEquals(Or_And1Fe1Fe2, Or_And1Fe1Fe2.normalize())
        }

        @Test
        fun `test normalization of OR expressions with AND and other subexpressions 2`() {
            val Or_And1Not1Fe1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                not { "field1" gt 24 }
                "field1" gt 24
            }
            assertEquals(Or_And1Not1Fe1, Or_And1Not1Fe1.normalize())
        }

        @Test
        fun `test normalization of OR expressions with AND and other subexpressions 3`() {
            val Or_And1Not1Not2 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                not { "field1" gt 24 }
                not { "field2" startsWith "pluto" }
            }
            assertEquals(Or_And1Not1Not2, Or_And1Not1Not2.normalize())
        }

        @Test
        fun `test normalization of OR expressions with AND and other subexpressions 4`() {
            val Or_And1Or1Fe1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field1" gt 24; "field2" startsWith "pluto" }
                "field1" gt 24
            }
            val normalized_Or_And1Or1Fe1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                "field1" gt 24
                "field2" startsWith "pluto"
                "field1" gt 24
            }
            assertEquals(normalized_Or_And1Or1Fe1, Or_And1Or1Fe1.normalize())
        }

        @Test
        fun `test normalization of OR expressions with AND and other subexpressions 5`() {
            val Or_And1Or1Not1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field1" gt 24; "field2" startsWith "pluto" }
                not { "field1" gt 24 }
            }
            val normalized_Or_And1Or1Not1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                "field1" gt 24
                "field2" startsWith "pluto"
                not { "field1" gt 24 }
            }
            assertEquals(normalized_Or_And1Or1Not1, Or_And1Or1Not1.normalize())
        }

        @Test
        fun `test normalization of OR expressions with AND and other subexpressions 6`() {
            val Or_And1Or1Or2 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
            }
            val normalized_Or_And1Or1Or2 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                "field1" gt 24
                "field2" startsWith "pluto"
                "field3" ne true
                "field4" `in` listOf("pippo", "paperino")
            }
            assertEquals(normalized_Or_And1Or1Or2, Or_And1Or1Or2.normalize())
        }

        @Test
        fun `test normalization of OR expressions with AND and other subexpressions 7`() {
            val Or_And1And2Fe1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                and { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                "field1" gt 24
            }
            val normalized_Or_And1And2Fe1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                and { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                "field1" gt 24
            }
            assertEquals(normalized_Or_And1And2Fe1, Or_And1And2Fe1.normalize())
        }

        @Test
        fun `test normalization of OR expressions with AND and other subexpressions 8`() {
            val Or_And1And2Not1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                and { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                not { "field1" gt 24 }
            }
            val normalized_Or_And1And2Not1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                and { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                not { "field1" gt 24 }
            }
            assertEquals(normalized_Or_And1And2Not1, Or_And1And2Not1.normalize())
        }

        @Test
        fun `test normalization of OR expressions with AND and other subexpressions 9`() {
            val Or_And1And2Or1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                and { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                or { "field1" gt 24; "field2" startsWith "pluto" }
            }
            val normalized_Or_And1And2Or1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                and { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                "field1" gt 24
                "field2" startsWith "pluto"
            }
            assertEquals(normalized_Or_And1And2Or1, Or_And1And2Or1.normalize())
        }

        @Test
        fun `test normalization of OR expressions with AND and other subexpressions 10`() {
            val Or_And1And2And3 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                and { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                and { "field5" eq null; "field6" lt otherField("otherField") }
            }
            val normalized_Or_And1And2And3 = or {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                and { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                and { "field5" eq null; "field6" lt otherField("otherField") }
            }
            assertEquals(normalized_Or_And1And2And3, Or_And1And2And3.normalize())
        }
    }

    @Nested
    inner class SimpleCompoundANDExpressions {
        @Test
        fun `test normalization of simple AND expressions 1`() {
            val And_Not1Fe1 = and {
                not { "field1" gt 24 }
                "field1" gt 24
            }
            assertEquals(And_Not1Fe1, And_Not1Fe1.normalize())
        }

        @Test
        fun `test normalization of simple AND expressions 2`() {
            val And_Not1Not2 = and {
                not { "field1" gt 24 }
                not { "field2" startsWith "pluto" }
            }
            assertEquals(And_Not1Not2, And_Not1Not2.normalize())
        }

        @Test
        fun `test normalization of simple AND expressions 3`() {
            val And_And1Fe1 = and {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                "field1" gt 24
            }
            val normalized_And_And1Fe1 = and {
                "field1" gt 24
                "field2" startsWith "pluto"
                "field1" gt 24
            }
            assertEquals(normalized_And_And1Fe1, And_And1Fe1.normalize())
        }

        @Test
        fun `test normalization of simple AND expressions 4`() {
            val And_And1Not1 = and {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                not { "field1" gt 24 }
            }
            val normalized_And_And1Not1 = and {
                "field1" gt 24
                "field2" startsWith "pluto"
                not { "field1" gt 24 }
            }
            assertEquals(normalized_And_And1Not1, And_And1Not1.normalize())
        }

        @Test
        fun `test normalization of simple AND expressions 5`() {
            val And_And1And2 = and {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                and { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
            }
            val normalized_And_And1And2 = and {
                "field1" gt 24
                "field2" startsWith "pluto"
                "field3" ne true
                "field4" `in` listOf("pippo", "paperino")
            }
            assertEquals(normalized_And_And1And2, And_And1And2.normalize())
        }
    }

    @Nested
    inner class CompoundANDWithORExpressions {
        @Test
        fun `test normalization of AND expressions with OR subexpressions 1`() {
            val And_Or1Fe1 = and {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                "field1" gt 24
            }
            val normalized_And_Or1Fe1 = or {
                and { "field1" gt 24; "field1" gt 24 }
                and { "field2" startsWith "pluto"; "field1" gt 24 }
            }
            assertEquals(normalized_And_Or1Fe1, And_Or1Fe1.normalize())
        }

        @Test
        fun `test normalization of AND expressions with OR subexpressions 2`() {
            val And_Or1Not1 = and {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                not { "field1" gt 24 }
            }
            val normalized_And_Or1Not1 = or {
                and { "field1" gt 24; not { "field1" gt 24 } }
                and { "field2" startsWith "pluto"; not { "field1" gt 24 } }
            }
            assertEquals(normalized_And_Or1Not1, And_Or1Not1.normalize())
        }

        @Test
        fun `test normalization of AND expressions with OR subexpressions 3`() {
            val And_Or1Or2 = and {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
            }
            val normalized_And_Or1Or2 = or {
                and { "field1" gt 24; "field3" ne true }
                and { "field1" gt 24; "field4" `in` listOf("pippo", "paperino") }
                and { "field2" startsWith "pluto"; "field3" ne true }
                and { "field2" startsWith "pluto"; "field4" `in` listOf("pippo", "paperino") }
            }
            assertEquals(normalized_And_Or1Or2, And_Or1Or2.normalize())
        }

        @Test
        fun `test normalization of AND expressions with OR subexpressions 4`() {
            val And_And1Or1 = and {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field1" gt 24; "field2" startsWith "pluto" }
            }
            val normalized_And_And1Or1 = or {
                and { "field1" gt 24; "field2" startsWith "pluto"; "field1" gt 24 }
                and { "field1" gt 24; "field2" startsWith "pluto"; "field2" startsWith "pluto" }
            }
            assertEquals(normalized_And_And1Or1, And_And1Or1.normalize())
        }
    }

    @Nested
    inner class CompundANDWithMultipleSubexpressions {
        @Test
        fun `test normalization of complex AND expressions 1`() {
            val And_Fe1Fe2Fe3 = and {
                "field1" gt 24
                "field2" startsWith "pluto"
                "field3" ne true
            }
            assertEquals(And_Fe1Fe2Fe3, And_Fe1Fe2Fe3.normalize())
        }

        @Test
        fun `test normalization of complex AND expressions 2`() {
            val And_Not1Fe1Fe2 = and {
                not { "field1" gt 24 }
                "field1" gt 24
                "field2" startsWith "pluto"
            }
            assertEquals(And_Not1Fe1Fe2, And_Not1Fe1Fe2.normalize())
        }

        @Test
        fun `test normalization of complex AND expressions 3`() {
            val And_Not1Not2Fe1 = and {
                not { "field1" gt 24 }
                not { "field2" startsWith "pluto" }
                "field1" gt 24
            }
            assertEquals(And_Not1Not2Fe1, And_Not1Not2Fe1.normalize())
        }

        @Test
        fun `test normalization of complex AND expressions 4`() {
            val And_Not1Not2Not3 = and {
                not { "field1" gt 24 }
                not { "field2" startsWith "pluto" }
                not { "field3" ne true }
            }
            assertEquals(And_Not1Not2Not3, And_Not1Not2Not3.normalize())
        }

        @Test
        fun `test normalization of complex AND expressions 5`() {
            val And_Or1Fe1Fe2 = and {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                "field1" gt 24
                "field2" startsWith "pluto"
            }
            val normalized_And_Or1Fe1Fe2 = And_Or1Fe1Fe2.normalize()
            val expected_And_Or1Fe1Fe2 = or {
                and { "field1" gt 24; "field1" gt 24; "field2" startsWith "pluto" }
                and { "field2" startsWith "pluto"; "field1" gt 24; "field2" startsWith "pluto" }
            }
            assertEquals(expected_And_Or1Fe1Fe2, normalized_And_Or1Fe1Fe2)
        }

        @Test
        fun `test normalization of complex AND expressions 6`() {
            val And_Or1Not1Fe1 = and {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                not { "field1" gt 24 }
                "field1" gt 24
            }
            val normalized_And_Or1Not1Fe1 = And_Or1Not1Fe1.normalize()
            val expected_And_Or1Not1Fe1 = or {
                and { "field1" gt 24; not { "field1" gt 24 }; "field1" gt 24; "field2" }
                and { "field2" startsWith "pluto"; not { "field1" gt 24 }; "field1" gt 24 }
            }
            assertEquals(expected_And_Or1Not1Fe1, normalized_And_Or1Not1Fe1)
        }

        @Test
        fun `test normalization of complex AND expressions 7`() {
            val And_Or1Not1Not2 = and {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                not { "field1" gt 24 }
                not { "field2" startsWith "pluto" }
            }
            val normalized_And_Or1Not1Not2 = And_Or1Not1Not2.normalize()
            val expected_And_Or1Not1Not2 = or {
                and { "field1" gt 24; not { "field1" gt 24 }; not { "field2" startsWith "pluto" } }
                and { "field2" startsWith "pluto"; not { "field1" gt 24 }; not { "field2" startsWith "pluto" } }
            }
            assertEquals(expected_And_Or1Not1Not2, normalized_And_Or1Not1Not2)
        }
    }

    @Nested
    inner class CompundANDWithMultipleORSubexpressions {
        @Test
        fun `test normalization of complex AND expressions with multiple OR subexpressions 1`() {
            val And_Or1Or2Fe1 = and {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                "field1" gt 24
            }
            val normalized_And_Or1Or2Fe1 = or {
                and { "field1" gt 24; "field3" ne true; "field1" gt 24 };
                and { "field1" gt 24; "field4" `in` listOf("pippo", "paperino"); "field1" gt 24 };
                and { "field2" startsWith "pluto"; "field3" ne true; "field1" gt 24 };
                and { "field2" startsWith "pluto"; "field4" `in` listOf("pippo", "paperino"); "field1" gt 24 }
            }
            assertEquals(normalized_And_Or1Or2Fe1, And_Or1Or2Fe1.normalize())
        }

        @Test
        fun `test normalization of complex AND expressions with multiple OR subexpressions 2`() {
            val And_Or1Or2Not1 = and {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                not { "field1" gt 24 }
            }
            val normalized_And_Or1Or2Not1 = or {
                and { "field1" gt 24; "field3" ne true; not { "field1" gt 24 } };
                and { "field1" gt 24; "field4" `in` listOf("pippo", "paperino"); not { "field1" gt 24 } };
                and { "field2" startsWith "pluto"; "field3" ne true; not { "field1" gt 24 } };
                and { "field2" startsWith "pluto"; "field4" `in` listOf("pippo", "paperino"); not { "field1" gt 24 } }
            }
            assertEquals(normalized_And_Or1Or2Not1, And_Or1Or2Not1.normalize())
        }

        @Test
        fun `test normalization of complex AND expressions with multiple OR subexpressions 3`() {
            val And_Or1Or2Or3 = and {
                or { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                or { "field5" eq null; "field6" lt otherField("otherField") }
            }
            val normalized_And_Or1Or2Or3 = or {
                and { "field1" gt 24; "field3" ne true; "field5" eq null };
                and { "field1" gt 24; "field3" ne true; "field6" lt otherField("otherField") };
                and { "field1" gt 24; "field4" `in` listOf("pippo", "paperino"); "field5" eq null };
                and { "field1" gt 24; "field4" `in` listOf("pippo", "paperino"); "field6" lt otherField("otherField") };
                and { "field2" startsWith "pluto"; "field3" ne true; "field5" eq null };
                and { "field2" startsWith "pluto"; "field3" ne true; "field6" lt otherField("otherField") };
                and { "field2" startsWith "pluto"; "field4" `in` listOf("pippo", "paperino"); "field5" eq null };
                and { "field2" startsWith "pluto"; "field4" `in` listOf("pippo", "paperino"); "field6" lt otherField("otherField") }
            }
            assertEquals(normalized_And_Or1Or2Or3, And_Or1Or2Or3.normalize())
        }
    }

    @Nested
    inner class  CompundANDWithANDSubexpressions {
        @Test
        fun `test normalization of AND expressions with AND subexpressions 1`() {
            val And_And1Fe1Fe2 = and {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                "field1" gt 24
                "field2" startsWith "pluto"
            }
            val normalized_And_And1Fe1Fe2 = and {
                "field1" gt 24
                "field2" startsWith "pluto"
                "field1" gt 24
                "field2" startsWith "pluto"
            }
            assertEquals(normalized_And_And1Fe1Fe2, And_And1Fe1Fe2.normalize())
        }

        @Test
        fun `test normalization of AND expressions with AND subexpressions 2`() {
            val And_And1Not1Fe1 = and {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                not { "field1" gt 24 }
                "field1" gt 24
            }
            val normalized_And_And1Not1Fe1 = and {
                "field1" gt 24
                "field2" startsWith "pluto"
                not { "field1" gt 24 }
                "field1" gt 24
            }
            assertEquals(normalized_And_And1Not1Fe1, And_And1Not1Fe1.normalize())
        }

        @Test
        fun `test normalization of AND expressions with AND subexpressions 3`() {
            val And_And1Not1Not2 = and {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                not { "field1" gt 24 }
                not { "field2" startsWith "pluto" }
            }
            val normalized_And_And1Not1Not2 = and {
                "field1" gt 24
                "field2" startsWith "pluto"
                not { "field1" gt 24 }
                not { "field2" startsWith "pluto" }
            }
            assertEquals(normalized_And_And1Not1Not2, And_And1Not1Not2.normalize())
        }
    }

    @Nested
    inner class CompundANDWithMixedSubexpressions {
        @Test
        fun `test normalization of complex AND expressions with mixed subexpressions 1`() {
            val And_And1Or1Fe1 = and {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field1" gt 24; "field2" startsWith "pluto" }
                "field1" gt 24
            }
            val normalized_And_And1Or1Fe1 = or {
                and {
                    "field1" gt 24
                    "field2" startsWith "pluto"
                    "field1" gt 24
                    "field1" gt 24
                }
                and {
                    "field1" gt 24
                    "field2" startsWith "pluto"
                    "field2" startsWith "pluto"
                    "field1" gt 24
                }
            }
            assertEquals(normalized_And_And1Or1Fe1, And_And1Or1Fe1.normalize())
        }

        @Test
        fun `test normalization of complex AND expressions with mixed subexpressions 2`() {
            val And_And1Or1Not1 = and {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field1" gt 24; "field2" startsWith "pluto" }
                not { "field1" gt 24 }
            }
            val normalized_And_And1Or1Not1 = or {
                and {
                    "field1" gt 24
                    "field2" startsWith "pluto"
                    "field1" gt 24
                    not { "field1" gt 24 }
                }
                and {
                    "field1" gt 24
                    "field2" startsWith "pluto"
                    "field2" startsWith "pluto"
                    not { "field1" gt 24 }
                }
            }
            assertEquals(normalized_And_And1Or1Not1, And_And1Or1Not1.normalize())
        }

        @Test
        fun `test normalization of complex AND expressions with mixed subexpressions 3`() {
            val And_And1Or1Or2 = and {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field1" gt 24; "field2" startsWith "pluto" }
                or { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
            }
            val normalized_And_And1Or1Or2 = or {
                and {
                    "field1" gt 24
                    "field2" startsWith "pluto"
                    "field1" gt 24
                    "field3" ne true
                }
                and {
                    "field1" gt 24
                    "field2" startsWith "pluto"
                    "field1" gt 24
                    "field4" `in` listOf("pippo", "paperino")
                }
                and {
                    "field1" gt 24
                    "field2" startsWith "pluto"
                    "field2" startsWith "pluto"
                    "field3" ne true
                }
                and {
                    "field1" gt 24
                    "field2" startsWith "pluto"
                    "field2" startsWith "pluto"
                    "field4" `in` listOf("pippo", "paperino")
                }
            }
            assertEquals(normalized_And_And1Or1Or2, And_And1Or1Or2.normalize())
        }
    }

    @Nested
    inner class CompundANDWithMultipleANDSubexpressions {
        @Test
        fun `test normalization of complex AND expressions with multiple AND subexpressions 1`() {
            val And_And1And2Fe1 = and {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                and { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                "field1" gt 24
            }
            val normalized_And_And1And2Fe1 = and {
                "field1" gt 24
                "field2" startsWith "pluto"
                "field3" ne true
                "field4" `in` listOf("pippo", "paperino")
                "field1" gt 24
            }
            assertEquals(normalized_And_And1And2Fe1, And_And1And2Fe1.normalize())
        }

        @Test
        fun `test normalization of complex AND expressions with multiple AND subexpressions 2`() {
            val And_And1And2Not1 = and {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                and { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                not { "field1" gt 24 }
            }
            val normalized_And_And1And2Not1 = and {
                "field1" gt 24
                "field2" startsWith "pluto"
                "field3" ne true
                "field4" `in` listOf("pippo", "paperino")
                not { "field1" gt 24 }
            }
            assertEquals(normalized_And_And1And2Not1, And_And1And2Not1.normalize())
        }

        @Test
        fun `test normalization of complex AND expressions with multiple AND subexpressions 3`() {
            val And_And1And2Or1 = and {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                and { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                or { "field1" gt 24; "field2" startsWith "pluto" }
            }
            val normalized_And_And1And2Or1 = or {
                and {
                    "field1" gt 24
                    "field2" startsWith "pluto"
                    "field3" ne true
                    "field4" `in` listOf("pippo", "paperino")
                    "field1" gt 24
                }
                and {
                    "field1" gt 24
                    "field2" startsWith "pluto"
                    "field3" ne true
                    "field4" `in` listOf("pippo", "paperino")
                    "field2" startsWith "pluto"
                }
            }
            assertEquals(normalized_And_And1And2Or1, And_And1And2Or1.normalize())
        }

        @Test
        fun `test normalization of complex AND expressions with multiple AND subexpressions 4`() {
            val And_And1And2And3 = and {
                and { "field1" gt 24; "field2" startsWith "pluto" }
                and { "field3" ne true; "field4" `in` listOf("pippo", "paperino") }
                and { "field5" eq null; "field6" lt otherField("otherField") }
            }
            val normalized_And_And1And2And3 = and {
                "field1" gt 24
                "field2" startsWith "pluto"
                "field3" ne true
                "field4" `in` listOf("pippo", "paperino")
                "field5" eq null
                "field6" lt otherField("otherField")
            }
            assertEquals(normalized_And_And1And2And3, And_And1And2And3.normalize())
        }
    }

    @Nested
    inner class ComplexNestedExpressions {

        val A: UcastBuilder.() -> Unit = { "field1" gt 24 }
        val B: UcastBuilder.() -> Unit = { "field2" startsWith "pluto" }
        val C: UcastBuilder.() -> Unit = { "field3" ne true }
        val D: UcastBuilder.() -> Unit = { "field4" `in` listOf("pippo", "paperino") }
        val E: UcastBuilder.() -> Unit = { "field5" eq null }
        val F: UcastBuilder.() -> Unit = { "field6" lt otherField("otherField") }
        val G: UcastBuilder.() -> Unit = { "field7" nin listOf(3, 7) }
        val H: UcastBuilder.() -> Unit = { "field8" gte 19 }
        val I: UcastBuilder.() -> Unit = { "field9" lte 51 }
        val J: UcastBuilder.() -> Unit = { "field10" contains "test" }
        val K: UcastBuilder.() -> Unit = { "field11" endsWith "example" }
        val L: UcastBuilder.() -> Unit = { "field12" eq "something" }
        val M: UcastBuilder.() -> Unit = { "field13" ne "else" }
        // Definizione alternativa possibile:
        // fun UcastBuilder.A() { "field1" gt 24 }

        @Test
        fun `test normalization of complex expressions 1`() {
            // ¬(A ∧ (B ∨ ¬C)) ∨ (D ∧ ¬E)
            val exp = or {
                not { and { A(); or { B(); not { C() } } } }
                and { D(); not { E() } }
            }
            // normalized version: ¬A ∨ (¬B ∧ C) ∨ (D ∧ ¬E)
            val normalizedExp = or {
                not { A() }
                and { not { B() }; C() }
                and { D(); not { E() } }
            }
            assertEquals(normalizedExp, exp.normalize())
        }

        @Test
        fun `test normalization of complex expressions 2`() {
            // (¬A ∨ (B ∧ C)) ∧ (D ∨ (¬E ∧ F))
            val exp = and {
                or { not { A() }; and { B(); C() } }
                or { D(); and { not { E() }; F() } }
            }
            // normalized version: (¬A ∧ D) ∨ (¬A ∧ ¬E ∧ F) ∨ (B ∧ C ∧ D) ∨ (B ∧ C ∧ ¬E ∧ F)
            val normalizedExp = or {
                and { not { A() }; D() }
                and { not { A() }; not { E() }; F() }
                and { B(); C(); D() }
                and { B(); C(); not { E() }; F() }
            }
            assertEquals(normalizedExp, exp.normalize())
        }

        @Test
        fun `test normalization of complex expressions 3`() {
            // ¬((A ∨ B) ∧ ¬(C ∨ (D ∧ ¬E))) ∧ F
            val exp = and {
                not { and {
                    or { A(); B() }
                    not { or { C(); and { D(); not { E() } } } }
                } }
                F()
            }
            // normalized version:
            //   (¬A ∧ ¬B ∧ F) ∨
            //   (C ∧ F) ∨
            //   (D ∧ ¬E ∧ F)
            val normalizedExp = or {
                and { not { A() }; not { B() }; F() }
                and { C(); F() }
                and { D(); not { E() }; F() }
            }
            assertEquals(normalizedExp, exp.normalize())
        }

        @Test
        fun `test normalization of complex expressions 4`() {
            // (A ∧ ¬(B ∨ (C ∧ ¬D))) ∨ (¬E ∧ (F ∨ G))
            val exp = or {
                and { A(); not { or { B(); and { C(); not { D() } } } } }
                and { not { E() }; or { F(); G() } }
            }
            // normalized version:
            //   (A ∧ ¬B ∧ ¬C) ∨
            //   (A ∧ ¬B ∧ D) ∨
            //   (¬E ∧ F) ∨
            //   (¬E ∧ G)
            val normalizedExp = or {
                and { A(); not { B() }; not { C() } }
                and { A(); not { B() }; D() }
                and { not { E() }; F() }
                and { not { E() }; G() }
            }
            assertEquals(normalizedExp, exp.normalize())
        }

        @Test
        fun `test normalization of complex expressions 5`() {
            // ¬A ∨ ((B ∧ ¬(C ∨ D)) ∧ (¬E ∨ (F ∧ ¬G)))
            val exp = or {
                not { A() }
                and {
                    and { B(); not { or { C(); D() } } }
                    or { not { E() }; and { F(); not { G() } } }
                }
            }
            // normalized version:
            //   ¬A ∨
            //   (B ∧ ¬C ∧ ¬D ∧ ¬E) ∨
            //   (B ∧ ¬C ∧ ¬D ∧ F ∧ ¬G)
            val normalizedExp = or {
                not { A() }
                and { B(); not { C() }; not { D() }; not { E() } }
                and { B(); not { C() }; not { D() }; F(); not { G() } }
            }
            assertEquals(normalizedExp, exp.normalize())
        }

        @Test
        fun `test normalization of complex expressions 6`() {
            // ¬(A ∨ (¬B ∧ (C ∨ ¬(D ∧ E)))) ∧ (F ∨ (¬G ∧ (H ∨ ¬I)))
            val exp = and {
                not { or {
                    A()
                    and { not { B() }; or { C(); not { and { D(); E() } } } }
                } }
                or {
                    F()
                    and { not { G() }; or { H(); not { I() } } }
                }
            }
            // normalized version:
            //   (¬A ∧ B ∧ F) ∨
            //   (¬A ∧ B ∧ ¬G ∧ H) ∨
            //   (¬A ∧ B ∧ ¬G ∧ ¬I) ∨
            //   (¬A ∧ ¬C ∧ D ∧ E ∧ F) ∨
            //   (¬A ∧ ¬C ∧ D ∧ E ∧ ¬G ∧ H) ∨
            //   (¬A ∧ ¬C ∧ D ∧ E ∧ ¬G ∧ ¬I)
            val normalizedExp = or {
                and { not { A() }; B(); F() }
                and { not { A() }; B(); not { G() }; H() }
                and { not { A() }; B(); not { G() }; not { I() } }
                and { not { A() }; not { C() }; D(); E(); F() }
                and { not { A() }; not { C() }; D(); E(); not { G() }; H() }
                and { not { A() }; not { C() }; D(); E(); not { G() }; not { I() } }
            }
            assertEquals(normalizedExp, exp.normalize())
        }

        @Test
        fun `test normalization of complex expressions 7`() {
            // (¬(A ∧ ¬(B ∨ (C ∧ ¬D))) ∨ (E ∧ (¬F ∨ (G ∧ ¬(H ∨ I))))) ∧ J
            val exp = and {
                or {
                    not { and {
                        A()
                        not { or { B(); and { C(); not { D() } } } }
                    } }
                    and {
                        E()
                        or { not { F() }; and { G(); not { or { H(); I() } } } }
                    }
                }
                J()
            }
            // normalized version:
            //   (¬A ∧ J) ∨
            //   (B ∧ J) ∨
            //   (C ∧ ¬D ∧ J) ∨
            //   (E ∧ ¬F ∧ J) ∨
            //   (E ∧ G ∧ ¬H ∧ ¬I ∧ J)
            val normalizedExp = or {
                and { not { A() }; J() }
                and { B(); J() }
                and { C(); not { D() }; J() }
                and { E(); not { F() }; J() }
                and { E(); G(); not { H() }; not { I() }; J() }
            }
            assertEquals(normalizedExp, exp.normalize())
        }

        @Test
        fun `test normalization of complex expressions 8`() {
            // ¬((A ∧ (¬B ∨ (C ∧ ¬(D ∧ E)))) ∧ (¬F ∨ (G ∧ (¬H ∨ I)))) ∨ (J ∧ ¬K)
            val exp = or {
                not { and {
                    and { A(); or { not { B() }; and { C(); not { and { D(); E() } } } } }
                    or { not { F() }; and { G(); or { not { H() }; I() } } }
                } }
                and { J(); not { K() } }
            }
            // normalized version:
            //   ¬A ∨
            //   (B ∧ ¬C) ∨
            //   (B ∧ D ∧ E) ∨
            //   (F ∧ ¬G) ∨
            //   (F ∧ H ∧ ¬I) ∨
            //   (J ∧ ¬K)
            val normalizedExp = or {
                not { A() }
                and { B(); not { C() } }
                and { B(); D(); E() }
                and { F(); not { G() } }
                and { F(); H(); not { I() } }
                and { J(); not { K() } }
            }
            assertEquals(normalizedExp, exp.normalize())
        }

        @Test
        fun `test normalization of complex expressions 9`() {
            // ((¬A ∨ (B ∧ ¬(C ∨ (D ∧ ¬E)))) ∧ (F ∨ ¬(G ∧ (H ∨ ¬I)))) ∨ (¬J ∧ (K ∨ ¬L))
            val exp = or {
                and {
                    or { not { A() }; and { B(); not { or { C(); and { D(); not { E() } } } } } }
                    or { F(); not { and { G(); or { H(); not { I() } } } } }
                }
                and { not { J() }; or { K(); not { L() } } }
            }
            // normalized version:
            //   (¬A ∧ F) ∨
            //   (¬A ∧ ¬G) ∨
            //   (¬A ∧ ¬H ∧ I) ∨
            //   (B ∧ ¬C ∧ ¬D ∧ F) ∨
            //   (B ∧ ¬C ∧ ¬D ∧ ¬G) ∨
            //   (B ∧ ¬C ∧ ¬D ∧ ¬H ∧ I) ∨
            //   (B ∧ ¬C ∧ E ∧ F) ∨
            //   (B ∧ ¬C ∧ E ∧ ¬G) ∨
            //   (B ∧ ¬C ∧ E ∧ ¬H ∧ I) ∨
            //   (¬J ∧ K) ∨ (¬J ∧ ¬L)
            val normalizedExp = or {
                and { not { A() }; F() }
                and { not { A() }; not { G() } }
                and { not { A() }; not { H() }; I() }
                and { B(); not { C() }; not { D() }; F() }
                and { B(); not { C() }; not { D() }; not { G() } }
                and { B(); not { C() }; not { D() }; not { H() }; I() }
                and { B(); not { C() }; E(); F() }
                and { B(); not { C() }; E(); not { G() } }
                and { B(); not { C() }; E(); not { H() }; I() }
                and { not { J() }; K() }
                and { not { J() }; not { L() } }
            }
            assertEquals(normalizedExp, exp.normalize())
        }

        @Test
        fun `test normalization of complex expressions 10`() {
            // ¬(A ∧ (¬B ∨ ¬(¬(C ∧ ¬(D ∨ (E ∧ ¬F))))))) ∧ ((G ∨ ¬(H ∧ (I ∨ ¬J))) ∧ (K ∨ (¬L ∧ M)))
            val exp = and {
                not { and {
                    A()
                    or { not { B() }; not { not { and { C(); not { or { D(); and { E(); not { F() } } } } } } } }
                } }
                and {
                    or { G(); not { and { H(); or { I(); not { J() } } } } }
                    or { K(); and { not { L() }; M() } }
                }
            }
            // normalized version:
            //   (¬A ∧ G ∧ K) ∨
            //   (¬A ∧ G ∧ ¬L ∧ M) ∨
            //   (¬A ∧ ¬H ∧ K) ∨
            //   (¬A ∧ ¬H ∧ ¬L ∧ M) ∨
            //   (¬A ∧ ¬I ∧ J ∧ K) ∨
            //   (¬A ∧ ¬I ∧ J ∧ ¬L ∧ M) ∨
            //   (B ∧ ¬C ∧ G ∧ K) ∨
            //   (B ∧ ¬C ∧ G ∧ ¬L ∧ M) ∨
            //   (B ∧ ¬C ∧ ¬H ∧ K) ∨
            //   (B ∧ ¬C ∧ ¬H ∧ ¬L ∧ M) ∨
            //   (B ∧ ¬C ∧ ¬I ∧ J ∧ K) ∨
            //   (B ∧ ¬C ∧ ¬I ∧ J ∧ ¬L ∧ M) ∨
            //   (B ∧ D ∧ G ∧ K) ∨
            //   (B ∧ D ∧ G ∧ ¬L ∧ M) ∨
            //   (B ∧ D ∧ ¬H ∧ K) ∨
            //   (B ∧ D ∧ ¬H ∧ ¬L ∧ M) ∨
            //   (B ∧ D ∧ ¬I ∧ J ∧ K) ∨
            //   (B ∧ D ∧ ¬I ∧ J ∧ ¬L ∧ M) ∨
            //   (B ∧ E ∧ ¬F ∧ G ∧ K) ∨
            //   (B ∧ E ∧ ¬F ∧ G ∧ ¬L ∧ M) ∨
            //   (B ∧ E ∧ ¬F ∧ ¬H ∧ K) ∨
            //   (B ∧ E ∧ ¬F ∧ ¬H ∧ ¬L ∧ M) ∨
            //   (B ∧ E ∧ ¬F ∧ ¬I ∧ J ∧ K) ∨
            //   (B ∧ E ∧ ¬F ∧ ¬I ∧ J ∧ ¬L ∧ M)
            val normalizedExp = or {
                and { not { A() }; G(); K() }
                and { not { A() }; G(); not { L() }; M() }
                and { not { A() }; not { H() }; K() }
                and { not { A() }; not { H() }; not { L() }; M() }
                and { not { A() }; not { I() }; J(); K() }
                and { not { A() }; not { I() }; J(); not { L() }; M() }
                and { B(); not { C() }; G(); K() }
                and { B(); not { C() }; G(); not { L() }; M() }
                and { B(); not { C() }; not { H() }; K() }
                and { B(); not { C() }; not { H() }; not { L() }; M() }
                and { B(); not { C() }; not { I() }; J(); K() }
                and { B(); not { C() }; not { I() }; J(); not { L() }; M() }
                and { B(); D(); G(); K() }
                and { B(); D(); G(); not { L() }; M() }
                and { B(); D(); not { H() }; K() }
                and { B(); D(); not { H() }; not { L() }; M() }
                and { B(); D(); not { I() }; J(); K() }
                and { B(); D(); not { I() }; J(); not { L() }; M() }
                and { B(); E(); not { F() }; G(); K() }
                and { B(); E(); not { F() }; G(); not { L() }; M() }
                and { B(); E(); not { F() }; not { H() }; K() }
                and { B(); E(); not { F() }; not { H() }; not { L() }; M() }
                and { B(); E(); not { F() }; not { I() }; J(); K() }
                and { B(); E(); not { F() }; not { I() }; J(); not { L() }; M() }
            }
            assertEquals(normalizedExp, exp.normalize())
        }
    }
}




