package com.example.vybes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.vybes.common.theme.VybesTheme
import com.example.vybes.feed.FeedScreen
import com.example.vybes.feed.VybePostScreen
import com.example.vybes.feed.model.VybeScreen
import com.example.vybes.feed.model.vybes
import com.example.vybes.feedback.FeedbackScreen
import com.example.vybes.profile.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        setContent {
            VybesTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = FeedScreen) {
                    composable<FeedScreen> {
                        FeedScreen(navController)
                    }
                    composable<ProfileScreen> {
                        ProfileScreen(onGoBack = { navController.popBackStack() })
                    }
                    composable<VybeScreen>()
                    { backStackEntry ->
                        val vybeScreen: VybeScreen = backStackEntry.toRoute()
                        VybePostScreen(vybes[vybeScreen.id], onGoBack = { navController.popBackStack() })
                    }
                    composable<FeedbackScreen> {
                        FeedbackScreen(onGoBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}