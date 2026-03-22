package com.example.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.presentation.component.Loader
import com.example.presentation.screen.login.LoginScreen
import com.example.presentation.screen.profile.ProfileScreen
import com.example.presentation.screen.register.RegisterScreen

@Composable
fun NavGraph(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.state.collectAsStateWithLifecycle()

    when (authState) {
        AuthState.Loading -> Loader()

        else -> {
            val startDestination = when (authState) {
                AuthState.Authenticated -> Screen.Profile.route
                else -> Screen.Login.route
            }

            LaunchedEffect(authState) {
                val target = when (authState) {
                    AuthState.Authenticated -> Screen.Profile.route
                    else -> Screen.Login.route
                }
                navController.navigate(target) {
                    popUpTo(0) { inclusive = true }
                }
            }

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        onRegister = { navController.navigate(Screen.Register.route) }
                    )
                }

                composable(Screen.Register.route) {
                    RegisterScreen(
                        onLogin = { navController.navigate(Screen.Login.route) }
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen()
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Login : Screen("login")

    data object Register : Screen("register")

    data object Profile : Screen("profile")
}
