package com.cookbook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cookbook.ui.screens.*
import com.cookbook.ui.viewmodel.CookbookViewModel

object Routes {
    const val LOGIN = "login"
    const val ENTER_EMAIL = "enter_email/{isForgotPassword}"
    const val VERIFICATION_CODE = "verification_code/{isForgotPassword}/{email}"
    const val SIGN_UP = "sign_up/{email}"
    const val FORGOT_PASSWORD = "forgot_password/{email}"
    const val MAIN_MENU = "main_menu"
    const val CHAT = "chat"
    const val PANTRY = "pantry"
    const val CHANGE_PASSWORD = "change_password"
}

@Composable
fun NavGraph(viewModel: CookbookViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { navController.navigate(Routes.MAIN_MENU) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onForgotPassword = { navController.navigate("enter_email/true") },
                onCreateAccount = { navController.navigate("enter_email/false") }
            )
        }

        composable(
            "enter_email/{isForgotPassword}",
            arguments = listOf(navArgument("isForgotPassword") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isForgot = backStackEntry.arguments?.getBoolean("isForgotPassword") ?: false
            EnterEmailScreen(
                isForgotPassword = isForgot,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNext = { email, sentCode ->
                    navController.navigate("verification_code/$isForgot/$email/$sentCode")
                },
                onLogin = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(
            "verification_code/{isForgotPassword}/{email}/{sentCode}",
            arguments = listOf(
                navArgument("isForgotPassword") { type = NavType.BoolType },
                navArgument("email") { type = NavType.StringType },
                navArgument("sentCode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val isForgot = backStackEntry.arguments?.getBoolean("isForgotPassword") ?: false
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val sentCode = backStackEntry.arguments?.getString("sentCode") ?: ""
            VerificationCodeScreen(
                isForgotPassword = isForgot,
                userEmail = email,
                correctCode = sentCode,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    if (isForgot) {
                        navController.navigate("forgot_password/$email")
                    } else {
                        navController.navigate("sign_up/$email")
                    }
                },
                onLogin = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(
            "sign_up/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            SignUpScreen(
                email = backStackEntry.arguments?.getString("email") ?: "",
                viewModel = viewModel,
                onSignUpSuccess = {
                    navController.navigate(Routes.LOGIN) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onLogin = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(
            "forgot_password/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            ForgotPasswordScreen(
                email = backStackEntry.arguments?.getString("email") ?: "",
                viewModel = viewModel,
                onSuccess = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                },
                onLogin = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Routes.MAIN_MENU) {
            MainMenuScreen(
                viewModel = viewModel,
                onNavigateToChat = { navController.navigate(Routes.CHAT) },
                onNavigateToPantry = { navController.navigate(Routes.PANTRY) },
                onChangePassword = { navController.navigate(Routes.CHANGE_PASSWORD) },
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Routes.CHAT) {
            ChatScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToMainMenu = {
                    navController.navigate(Routes.MAIN_MENU) { popUpTo(Routes.CHAT) { inclusive = true } }
                },
                onNavigateToPantry = {
                    navController.navigate(Routes.PANTRY) { popUpTo(Routes.CHAT) { inclusive = true } }
                }
            )
        }

        composable(Routes.PANTRY) {
            PantryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToMainMenu = {
                    navController.navigate(Routes.MAIN_MENU) { popUpTo(Routes.PANTRY) { inclusive = true } }
                },
                onNavigateToChat = {
                    navController.navigate(Routes.CHAT) { popUpTo(Routes.PANTRY) { inclusive = true } }
                }
            )
        }

        composable(Routes.CHANGE_PASSWORD) {
            ChangePasswordScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    viewModel.logout()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }
    }
}
