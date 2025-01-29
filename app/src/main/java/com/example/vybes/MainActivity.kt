package com.example.vybes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vybes.add.SearchTrackScreen
import com.example.vybes.auth.AuthEvent
import com.example.vybes.auth.AuthEventBus
import com.example.vybes.common.theme.VybesTheme
import com.example.vybes.feedback.FeedbackScreen
import com.example.vybes.post.VybePostScreen
import com.example.vybes.post.feed.FeedScreen
import com.example.vybes.post.model.VybeScreen
import com.example.vybes.profile.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            AuthEventBus.authEvents.collect { event ->
                when (event) {
                    is AuthEvent.TokenExpired -> {
                        startActivity(
                            Intent(this@MainActivity, AuthActivity::class.java)
                                .apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                                })
                        finish()
                    }
                }
            }
        }

        setContent {
            VybesTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = FeedScreen) {
                    composable<FeedScreen> {
                        FeedScreen(navController)
                    }
                    composable<SearchTrackScreen> {
                        SearchTrackScreen()
                    }
                    composable<ProfileScreen> {
                        ProfileScreen(navController)
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

