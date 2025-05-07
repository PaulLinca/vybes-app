package com.example.vybes.add

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
object SearchTrackScreen

@Composable
fun SearchTrackScreen(
    navController: NavController,
    viewModel: SearchTrackViewModel = hiltViewModel()
) {
    GenericSearchScreen(
        navController = navController,
        viewModel = viewModel,
        hint = "Search tracks...",
        onItemClick = { track -> navController.navigate(track) }
    )
}