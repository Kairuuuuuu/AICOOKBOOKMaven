package com.cookbook.backend

import com.cookbook.data.model.BudgetStatus
import com.cookbook.data.model.PantryItem
import com.cookbook.data.model.ParsedResponse
import com.cookbook.data.model.RecipeAnalysisResult

object Chatbackend {

    fun analyzeRecipe(aiResponse: ParsedResponse, currentBudgetStr: String): RecipeAnalysisResult {
        var currentBudget = 0.0
        try {
            if (currentBudgetStr.isNotBlank()) {
                val budgetStr = currentBudgetStr.replace("Php", "")
                    .replace(",", "").trim()
                currentBudget = budgetStr.toDouble()
            }
        } catch (_: Exception) {
            currentBudget = 0.0
        }

        if (currentBudget <= 0) {
            return RecipeAnalysisResult(BudgetStatus.NO_BUDGET, aiResponse.totalEstimatedCost, 0.0)
        }
        if (aiResponse.totalEstimatedCost > currentBudget) {
            return RecipeAnalysisResult(
                BudgetStatus.INSUFFICIENT_FUNDS,
                aiResponse.totalEstimatedCost,
                currentBudget
            )
        }
        return RecipeAnalysisResult(BudgetStatus.OK, aiResponse.totalEstimatedCost, currentBudget)
    }

    fun saveRecipeToMenu(
        aiResponse: ParsedResponse,
        onUpdate: (recipeName: String, ingredients: List<String>, fullIngredients: List<String>,
                   checked: List<Boolean>, calories: String, protein: String, totalCost: Double) -> Unit
    ) {
        val ingredients = aiResponse.ingredients.toList()
        val checked = ingredients.map { false }

        onUpdate(
            aiResponse.recipeName,
            ingredients,
            ingredients,
            checked,
            aiResponse.calories,
            aiResponse.protein,
            aiResponse.totalEstimatedCost
        )
    }

    fun generatePromptFromPantry(pantryItems: List<PantryItem>): String? {
        val validItems = pantryItems.filter { it.expDate.isNotBlank() }
        if (validItems.isEmpty()) return null

        val ingredientList = validItems.joinToString("\n- ") { "${it.name} (Qty: ${it.qty})" }
        return "I have these ingredients in my pantry:\n- $ingredientList\n\nSuggest a recipe I can make with them."
    }

    fun deductIngredientsAndClearState(
        fullIngredients: List<String>,
        checkedIngredients: List<Boolean>,
        pantryItems: List<PantryItem>
    ): List<PantryItem> {
        val updated = pantryItems.toMutableList()
        for (i in fullIngredients.indices) {
            if (i < checkedIngredients.size && checkedIngredients[i]) {
                val usedName = fullIngredients[i].split("(")[0].trim().lowercase()
                updated.removeAll { it.name.lowercase().contains(usedName) }
            }
        }
        return updated
    }

    fun computeMissingCount(
        ingredients: List<String>,
        checkedState: List<Boolean>
    ): Int {
        if (ingredients.isEmpty()) return 0
        return checkedState.count { !it }
    }
}
