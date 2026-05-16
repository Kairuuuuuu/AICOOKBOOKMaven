package com.cookbook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookbook.backend.AIChatBot
import com.cookbook.backend.AuthenticationService
import com.cookbook.backend.BudgetService
import com.cookbook.backend.ChangePasswordService
import com.cookbook.backend.Chatbackend
import com.cookbook.backend.EmailAuthenticationService
import com.cookbook.backend.ForgotPasswordBackend
import com.cookbook.backend.PantryBackend
import com.cookbook.backend.ShoppingListBackend
import com.cookbook.backend.SignUpBackend
import com.cookbook.backend.UserProfileBackend
import com.cookbook.backend.VerificationBackend
import com.cookbook.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CookbookViewModel : ViewModel() {

    private val _state = MutableStateFlow(CookbookState())
    val state: StateFlow<CookbookState> = _state.asStateFlow()

    fun clearError() = _state.update { it.copy(errorMessage = null) }
    fun clearToast() = _state.update { it.copy(toastMessage = null) }


    // --- Auth ---

    fun login(email: String, password: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = AuthenticationService.attemptLogin(email, password)) {
                is AuthenticationService.LoginResult.Success -> {
                    UserProfileBackend.email = result.email
                    UserProfileBackend.firstName = result.firstName
                    _state.update {
                        it.copy(
                            isLoading = false,
                            userEmail = result.email,
                            firstName = result.firstName
                        )
                    }
                }
                is AuthenticationService.LoginResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun signUp(email: String, password: String, confirmPassword: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = SignUpBackend.attemptSignUp(email, password, confirmPassword)) {
                is SignUpBackend.SignUpResult.Success -> {
                    _state.update { it.copy(isLoading = false, userEmail = email, signUpSuccess = true) }
                }
                is SignUpBackend.SignUpResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun changePassword(email: String, currentPassword: String,
                       newPassword: String, confirmPassword: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = ChangePasswordService.updatePassword(
                email, currentPassword, newPassword, confirmPassword)) {
                is ChangePasswordService.PasswordUpdateResult.Success -> {
                    _state.update { it.copy(isLoading = false, passwordChangeSuccess = true) }
                }
                is ChangePasswordService.PasswordUpdateResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is ChangePasswordService.PasswordUpdateResult.RequiresReauth -> {
                    _state.update {
                        it.copy(isLoading = false,
                            errorMessage = "Please log out and log in again to change your password.")
                    }
                }
            }
        }
    }

    fun forgotPasswordChange(email: String, newPassword: String, confirmPassword: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = ForgotPasswordBackend.processPasswordChange(
                email, newPassword, confirmPassword)) {
                is ForgotPasswordBackend.PasswordChangeResult.Success -> {
                    _state.update { it.copy(isLoading = false, passwordChangeSuccess = true) }
                }
                is ForgotPasswordBackend.PasswordChangeResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun logout() {
        UserProfileBackend.performLogout()
        _state.update {
            CookbookState()
        }
    }

    // --- Email / OTP ---

    suspend fun sendOTP(email: String): OTPResult {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            val result = EmailAuthenticationService.processEmailForOTP(email)
            if (result.status == OTPStatus.SUCCESS) {
                _state.update { it.copy(userEmail = email, errorMessage = null) }
            } else if (result.status == OTPStatus.INVALID_EMAIL) {
                _state.update { it.copy(errorMessage = "Please enter a valid email address.") }
            } else {
                _state.update { it.copy(errorMessage = "Failed to send code. Check your connection.") }
            }
            result
        }
    }

    // --- Budget ---

    fun setBudget(input: String) {
        val result = BudgetService.validateBudget(input, _state.value.currentTotalCost)
        if (result.isValid) {
            _state.update {
                it.copy(
                    currentBudget = result.formattedBudget,
                    errorMessage = null,
                    toastMessage = "Budget successfully updated!"
                )
            }
        } else {
            _state.update { it.copy(errorMessage = result.errorMessage) }
        }
    }

    // --- Pantry ---

    fun addPantryItem(name: String, qty: String, expDate: String) {
        val displayName = name.ifBlank { "New Food" }
        PantryBackend.savedPantryItems.add(
            PantryItem(name = displayName, qty = qty, expDate = expDate)
        )
        _state.update { it.copy(pantryItems = PantryBackend.savedPantryItems.toList()) }
    }

    fun refreshPantry() {
        _state.update { it.copy(pantryItems = PantryBackend.savedPantryItems.toList()) }
    }

    // --- AI Chat ---

    fun generateFromPantry(): String? {
        val prompt = Chatbackend.generatePromptFromPantry(PantryBackend.savedPantryItems)
        if (prompt == null && PantryBackend.savedPantryItems.isEmpty()) {
            _state.update { it.copy(errorMessage = "Your pantry is empty. Add items first!") }
        } else if (prompt == null) {
            _state.update { it.copy(errorMessage = "All items in your pantry have expired.") }
        } else {
            _state.update { it.copy(pendingPantryPrompt = prompt, isFromPantry = true) }
        }
        return prompt
    }

    // --- Recipe Management ---

    fun saveRecipeToMenu(aiResponse: ParsedResponse) {
        Chatbackend.saveRecipeToMenu(aiResponse) { recipeName, ingredients, fullIngredients,
                                                     checked, calories, protein, totalCost ->
            val missingLabel = ShoppingListBackend.computeMissingCount(ingredients, checked)
            _state.update {
                it.copy(
                    currentRecipeName = recipeName,
                    currentIngredients = ingredients,
                    fullRecipeIngredients = fullIngredients,
                    checkedIngredients = checked,
                    currentCalories = calories,
                    currentProtein = protein,
                    currentTotalCost = totalCost,
                    savedMissingIngredients = missingLabel
                )
            }
        }
    }

    fun toggleIngredientCheck(index: Int, isChecked: Boolean) {
        val newChecked = _state.value.checkedIngredients.toMutableList()
        ShoppingListBackend.updateCheckedState(newChecked, index, isChecked)
        val missingLabel = ShoppingListBackend.computeMissingCount(
            _state.value.currentIngredients, newChecked)
        _state.update {
            it.copy(checkedIngredients = newChecked, savedMissingIngredients = missingLabel)
        }
    }

    fun completeShoppingList() {
        val updatedPantry = Chatbackend.deductIngredientsAndClearState(
            _state.value.fullRecipeIngredients,
            _state.value.checkedIngredients,
            PantryBackend.savedPantryItems
        )
        PantryBackend.savedPantryItems.clear()
        PantryBackend.savedPantryItems.addAll(updatedPantry)
        _state.update {
            it.copy(
                currentRecipeName = "No meal selected",
                currentIngredients = emptyList(),
                fullRecipeIngredients = emptyList(),
                checkedIngredients = emptyList(),
                currentCalories = "N/A",
                currentProtein = "N/A",
                currentTotalCost = 0.0,
                savedMissingIngredients = "",
                isFromPantry = false,
                pendingPantryPrompt = null,
                toastMessage = "Shopping list completed! Ingredients deducted from pantry.",
                pantryItems = PantryBackend.savedPantryItems.toList()
            )
        }
    }

    fun clearRecipe() {
        _state.update {
            it.copy(
                currentRecipeName = "No meal selected",
                currentIngredients = emptyList(),
                fullRecipeIngredients = emptyList(),
                checkedIngredients = emptyList(),
                currentCalories = "N/A",
                currentProtein = "N/A",
                currentTotalCost = 0.0,
                savedMissingIngredients = "",
                isFromPantry = false,
                pendingPantryPrompt = null
            )
        }
    }
}
