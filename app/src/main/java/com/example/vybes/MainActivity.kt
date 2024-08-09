package com.example.vybes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.vybes.feed.FeedScreen
import com.example.vybes.feed.VybePostScreen
import com.example.vybes.feed.model.Vybe
import com.example.vybes.feedback.FeedbackScreen
import com.example.vybes.profile.ProfileScreen
import com.example.vybes.common.theme.VybesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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