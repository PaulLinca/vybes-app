package com.linca.vybes.add.album

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.linca.vybes.add.GenericSearchScreen
import kotlinx.serialization.Serializable

@Serializable
object SearchAlbumScreen

@Composable
fun SearchAlbumScreen(
    navController: NavController,
    viewModel: SearchAlbumViewModel = hiltViewModel()
) {
    GenericSearchScreen(
        navController = navController,
        viewModel = viewModel,
        hint = "Search albums...",
        onItemClick = { album -> navController.navigate(AddAlbumReviewScreen(album.spotifyId)) }
    )
}