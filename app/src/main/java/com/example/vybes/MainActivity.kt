package com.example.vybes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.vybes.add.AddAlbumReviewScreen
import com.example.vybes.add.AddPostScreen
import com.example.vybes.add.SearchAlbumScreen
import com.example.vybes.add.SearchTrackScreen
import com.example.vybes.add.model.network.ArtistSearchResult
import com.example.vybes.add.model.network.ArtistSearchResultNavType
import com.example.vybes.add.model.network.TrackSearchResult
import com.example.vybes.auth.AuthEvent
import com.example.vybes.auth.AuthEventBus
import com.example.vybes.common.theme.VybesTheme
import com.example.vybes.feedback.FeedbackScreen
import com.example.vybes.post.AlbumReviewScreen
import com.example.vybes.post.VybePostScreen
import com.example.vybes.post.feed.FeedScreen
import com.example.vybes.post.model.AlbumReviewScreen
import com.example.vybes.post.model.User
import com.example.vybes.post.model.VybeScreen
import com.example.vybes.profile.ProfileScreen
import com.example.vybes.profile.favourites.EditFavouritesScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            AuthEventBus.authEvents.collect { event ->
                when (event) {
                    is AuthEvent.TokenExpired, AuthEvent.TokenCleared -> {
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
                        SearchTrackScreen(navController)
                    }
                    composable<SearchAlbumScreen> {
                        SearchAlbumScreen(navController)
                    }
                    composable<TrackSearchResult>(typeMap = mapOf(typeOf<List<ArtistSearchResult>>() to ArtistSearchResultNavType))
                    { backStackEntry ->
                        val searchResult: TrackSearchResult = backStackEntry.toRoute()
                        AddPostScreen(
                            searchResult,
                            onGoBack = { navController.popBackStack() },
                            onSubmitSuccess = {
                                navController.navigate(FeedScreen) {
                                    popUpTo(0)
                                }
                            })
                    }
                    composable<AddAlbumReviewScreen> {
                        AddAlbumReviewScreen(
                            onGoBack = { navController.popBackStack() },
                            onSubmitSuccess = {
                                navController.navigate(FeedScreen) {
                                    popUpTo(0)
                                }
                            })
                    }
                    composable<User> { backStackEntry ->
                        val user: User = backStackEntry.toRoute()
                        ProfileScreen(user, navController)
                    }
                    composable<EditFavouritesScreen> {
                        EditFavouritesScreen(navController)
                    }
                    composable<VybeScreen> {
                        VybePostScreen(
                            onGoBack = { navController.popBackStack() },
                            navController = navController
                        )
                    }
                    composable<AlbumReviewScreen> {
                        AlbumReviewScreen(
                            onGoBack = { navController.popBackStack() },
                            navController = navController
                        )
                    }
                    composable<FeedbackScreen> {
                        FeedbackScreen(onGoBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

