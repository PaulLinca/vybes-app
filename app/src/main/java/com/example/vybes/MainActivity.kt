package com.example.vybes

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vybes.auth.login.LoginScreen
import com.example.vybes.auth.register.RegisterScreen
import com.example.vybes.common.theme.VybesTheme
import com.example.vybes.feedback.FeedbackScreen
import com.example.vybes.post.VybePostScreen
import com.example.vybes.post.feed.FeedScreen
import com.example.vybes.post.model.VybeScreen
import com.example.vybes.profile.ProfileScreen
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        setContent {
            VybesTheme {

                val isLoggedIn = remember { mutableStateOf(checkIfUserIsLoggedIn(this)) }
                val startDestination = if (isLoggedIn.value) FeedScreen else RegisterScreen
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = startDestination) {
                    composable<RegisterScreen> {
                        RegisterScreen(
                            onRegisterSuccess = { navController.navigate(LoginScreen) },
                            onLoginClick = { navController.navigate(LoginScreen) }
                        )
                    }
                    composable<LoginScreen> {
                        LoginScreen(
                            navController = navController,
                            onRegisterClick = { navController.navigate(RegisterScreen) })
                    }
                    composable<FeedScreen> {
                        FeedScreen(navController)
                    }
                    composable<ProfileScreen> {
                        ProfileScreen(onGoBack = { navController.popBackStack() })
                    }
                    composable<VybeScreen> {
                        VybePostScreen(onGoBack = { navController.popBackStack() })
                    }
                    composable<FeedbackScreen> {
                        FeedbackScreen(onGoBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

fun checkIfUserIsLoggedIn(context: Context): Boolean {
    val jwt = SharedPreferencesManager.getJwt(context)
    return !jwt.isNullOrEmpty()
}