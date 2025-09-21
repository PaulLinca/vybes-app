package com.linca.vybes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.linca.vybes.add.album.AddAlbumReviewScreen
import com.linca.vybes.add.album.SearchAlbumScreen
import com.linca.vybes.add.vybe.AddPostScreen
import com.linca.vybes.add.vybe.SearchTrackScreen
import com.linca.vybes.auth.AuthEvent
import com.linca.vybes.auth.AuthEventBus
import com.linca.vybes.common.theme.VybesTheme
import com.linca.vybes.feedback.FeedbackScreen
import com.linca.vybes.model.AlbumReviewScreen
import com.linca.vybes.model.ArtistSearchResult
import com.linca.vybes.model.ArtistSearchResultNavType
import com.linca.vybes.model.TrackSearchResult
import com.linca.vybes.model.User
import com.linca.vybes.model.VybeScreen
import com.linca.vybes.post.AlbumReviewScreen
import com.linca.vybes.post.VybePostScreen
import com.linca.vybes.post.feed.FeedScreen
import com.linca.vybes.profile.ProfileScreen
import com.linca.vybes.profile.favourites.EditFavouritesScreen
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
                            }
                        )
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

