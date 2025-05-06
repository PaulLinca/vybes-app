package com.example.vybes.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.vybes.R
import com.example.vybes.add.model.network.TrackSearchResult
import com.example.vybes.common.composables.MultilineTextField
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.ErrorRed
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.SecondaryTextColor
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.songTitleStyle
import kotlinx.serialization.Serializable

@Serializable
object SearchTrackScreen

@Composable
fun SearchTrackScreen(
    navController: NavController,
    viewModel: SearchTrackViewModel = hiltViewModel()
) {
    val searchQuery = viewModel.searchQuery
    val searchResults = viewModel.searchResults
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundColor)
    ) {
        TopBarWithBackButton(onGoBack = { navController.popBackStack() }) {
            androidx.compose.material3.Text(
                text = stringResource(R.string.share_prompt),
                color = PrimaryTextColor,
                textAlign = TextAlign.Center,
                style = songTitleStyle,
            )
        }
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            MultilineTextField(
                value = searchQuery,
                onValueChanged = { viewModel.updateSearchQuery(it) },
                hintText = "Search for tracks...",
                textStyle = artistsStyle,
                maxLines = 1,
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = PrimaryTextColor
                    )
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage,
                        color = ErrorRed,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                searchResults.isNotEmpty() -> {
                    LazyColumn {
                        items(searchResults) { track ->
                            TrackItem(navController, track = track)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackItem(navController: NavController, track: TrackSearchResult) {
    Row(
        modifier = Modifier
            .clickable { navController.navigate(track) }
            .fillMaxWidth()
            .padding(8.dp)
            .background(ElevatedBackgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        AsyncImage(
            model = track.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = track.name, style = MaterialTheme.typography.h6, color = PrimaryTextColor)
            Text(
                text = track.artists.joinToString(", ") { it.name },
                style = MaterialTheme.typography.body2,
                color = SecondaryTextColor
            )
        }
    }
}