package com.cookbook.data.model

data class CookbookState(
    val currentRecipeName: String = "No meal selected",
    val currentBudget: String = "Php 0",
    val currentTotalCost: Double = 0.0,
    val currentCalories: String = "N/A",
    val currentProtein: String = "N/A",
    val currentIngredients: List<String> = emptyList(),
    val fullRecipeIngredients: List<String> = emptyList(),
    val checkedIngredients: List<Boolean> = emptyList(),
    val savedMissingIngredients: String = "",
    val isFromPantry: Boolean = false,
    val pendingPantryPrompt: String? = null,
    val pantryItems: List<PantryItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val toastMessage: String? = null,
    val passwordChangeSuccess: Boolean = false,
    val signUpSuccess: Boolean = false,
    val userEmail: String = "",
    val firstName: String = "Guest",
    val lastName: String = ""
)
