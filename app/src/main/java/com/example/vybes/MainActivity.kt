package com.example.vybes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.vybes.ui.feed.FeedScreen
import com.example.vybes.ui.feed.VybePostScreen
import com.example.vybes.ui.feed.model.Vybe
import com.example.vybes.ui.feedback.FeedbackScreen
import com.example.vybes.ui.profile.ProfileScreen
import com.example.vybes.ui.theme.VybesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    composable<Vybe> { backStackEntry ->
                        val vybe: Vybe = backStackEntry.toRoute()
                        VybePostScreen(vybe, onGoBack = { navController.popBackStack() })
                    }
                    composable<FeedbackScreen> {
                        FeedbackScreen(onGoBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}



