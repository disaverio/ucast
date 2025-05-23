package dev.disaverio.ucast.extensions

import dev.disaverio.ucast.models.*

internal object NormalizationUtils {

    /**
     * Converts a UcastExpression to Disjunctive Normal Form, DNF
     * https://en.wikipedia.org/wiki/Disjunctive_normal_form
     */
    fun toDNF(expr: UcastExpression): UcastExpression =
        when (expr) {
            is FieldExpression -> expr
            is CompoundExpression -> when (expr.operator) {
                CompoundOperator.AND -> distributeAndOverOr(expr.value.map { toDNF(it) })
                CompoundOperator.OR -> CompoundExpression(CompoundOperator.OR, flatten(expr.operator, expr.value.map { toDNF(it) }))
                CompoundOperator.NOT -> expr.value.single().let {
                    if (it is FieldExpression) expr else toDNF(negate(it))
                }
            }
        }

    private fun distributeAndOverOr(operands: List<UcastExpression>): UcastExpression {
        if (operands.isEmpty()) return CompoundExpression(CompoundOperator.AND, emptyList())
        if (operands.size == 1) return operands.single()

        val firstOrIdx = operands.indexOfFirst { it is CompoundExpression && it.operator == CompoundOperator.OR }
        if (firstOrIdx == -1) {
            return CompoundExpression(CompoundOperator.AND, flatten(CompoundOperator.AND, operands)) // A ∧ (B ∧ C) → A ∧ B ∧ C
        }

        val orExpr = operands[firstOrIdx] as CompoundExpression
        val left = operands.take(firstOrIdx)
        val right = operands.drop(firstOrIdx + 1)
        val distributed = orExpr.value.map { distributeAndOverOr(left + it + right) }

        return CompoundExpression(CompoundOperator.OR, flatten(CompoundOperator.OR, distributed)) // A ∧ (B ∨ C) → (A ∧ B) ∨ (A ∧ C)
    }

    private fun negate(expr: UcastExpression): UcastExpression =
        when (expr) {
            is FieldExpression -> CompoundExpression(CompoundOperator.NOT, listOf(expr))
            is CompoundExpression -> when (expr.operator) {
                CompoundOperator.NOT -> expr.value.single() // ¬(¬A) → A
                CompoundOperator.AND -> CompoundExpression(CompoundOperator.OR, expr.value.map { negate(it) }) // ¬(A ∧ B) → ¬A ∨ ¬B
                CompoundOperator.OR -> CompoundExpression(CompoundOperator.AND, expr.value.map { negate(it) }) // ¬(A ∨ B) → ¬A ∧ ¬B
            }
        }

    private fun flatten(operator: CompoundOperator, exprs: List<UcastExpression>): List<UcastExpression> =
        exprs.flatMap {
            if (it is CompoundExpression && it.operator == operator) flatten(operator, it.value)
            else listOf(it)
        }
}
