package com.example.vybes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vybes.auth.login.LoginScreen
import com.example.vybes.auth.register.RegisterScreen
import com.example.vybes.common.theme.VybesTheme
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        if(SharedPreferencesManager.isLoggedIn()) {
            startMainActivity()
        }

        setContent {
            VybesTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = LoginScreen) {
                    composable<RegisterScreen> {
                        RegisterScreen(
                            onRegisterSuccess = { navController.navigate(LoginScreen) },
                            onLoginClick = { navController.navigate(LoginScreen) }
                        )
                    }
                    composable<LoginScreen> {
                        LoginScreen(
                            onLoginSuccess = { startMainActivity() },
                            onRegisterClick = { navController.navigate(RegisterScreen) })
                    }
                }
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}