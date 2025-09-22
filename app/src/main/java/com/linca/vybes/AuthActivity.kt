package com.linca.vybes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.linca.vybes.auth.login.LoginScreen
import com.linca.vybes.auth.setup.SetupScreen
import com.linca.vybes.common.theme.VybesTheme
import com.linca.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        // Check if user is already authenticated with Firebase
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Check if user has completed username setup
            if (SharedPreferencesManager.getUsername() != null) {
                startMainActivity()
                return
            }
        }

        setContent {
            VybesTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = LoginScreen) {
                    composable<LoginScreen> {
                        LoginScreen(
                            onLoginSuccess = { startMainActivity() },
                            onUsernameSetupRequired = { navController.navigate(SetupScreen) })
                    }
                    composable<SetupScreen> {
                        SetupScreen(
                            onSetupSuccess = { startMainActivity() }
                        )
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
