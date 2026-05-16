package com.cookbook.backend

object BudgetService {

    data class BudgetResult(
        val isValid: Boolean,
        val formattedBudget: String = "",
        val errorMessage: String = ""
    )

    fun validateBudget(input: String, currentTotalCost: Double = 0.0): BudgetResult {
        if (input.isBlank()) {
            return BudgetResult(false, errorMessage = "Please enter a budget.")
        }
        try {
            val cleaned = input.replace(Regex("[PhpPHPphp\\s,]"), "").trim()
            val amount = cleaned.toDouble()
            if (amount < 0) {
                return BudgetResult(false, errorMessage = "Budget cannot be negative.")
            }
            val formatted = "Php %.2f".format(amount)
            return BudgetResult(true, formattedBudget = formatted)
        } catch (_: NumberFormatException) {
            return BudgetResult(false, errorMessage = "Please enter a valid number.")
        }
    }
}
