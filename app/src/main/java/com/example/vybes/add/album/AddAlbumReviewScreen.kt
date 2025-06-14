package com.example.vybes.add.album

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.vybes.R
import com.example.vybes.auth.model.Track
import com.example.vybes.common.composables.MultilineTextField
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.AccentBorderColor
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.IconColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.SecondaryTextColor
import com.example.vybes.common.theme.TryoutBlue
import com.example.vybes.common.theme.TryoutRed
import com.example.vybes.common.theme.TryoutYellow
import com.example.vybes.common.theme.VybesVeryDarkGray
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.post.model.AlbumReviewScreen
import com.example.vybes.post.model.network.TrackRating
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
data class AddAlbumReviewScreen(val spotifyId: String) {
    companion object {
        fun from(savedStateHandle: SavedStateHandle) =
            savedStateHandle.toRoute<AddAlbumReviewScreen>()
    }
}

@Composable
fun AddAlbumReviewScreen(
    onGoBack: () -> Unit,
    onSubmitSuccess: () -> Unit,
    onSeeReview: (AlbumReviewScreen) -> Unit,
    viewModel: AddAlbumViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val descriptionText = viewModel.descriptionText
    val albumRating = viewModel.albumRating
    val trackRatings = viewModel.trackRatings
    val favoriteTrackIds = viewModel.favoriteTrackIds
    val remainingCharacters = viewModel.remainingCharacters

    when (val state = uiState) {
        is AddAlbumViewModel.ReviewUiState.Success -> {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundColor),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        TopBarWithBackButton(onGoBack = onGoBack) {
                            Text(
                                text = stringResource(R.string.review_album),
                                color = PrimaryTextColor,
                                textAlign = TextAlign.Center,
                                style = songTitleStyle,
                            )
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(state.album.imageUrl)
                                    .size(Size.ORIGINAL)
                                    .crossfade(true)
                                    .build(),
                                contentScale = ContentScale.Crop,
                            )

                            Image(
                                painter = painter,
                                contentDescription = "Album cover",
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = state.album.name,
                                style = songTitleStyle,
                                color = PrimaryTextColor,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = state.album.releaseDate?.year.toString(),
                                style = artistsStyle,
                                color = SecondaryTextColor,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = state.album.artists.joinToString(", ") { it.name },
                                style = artistsStyle,
                                color = SecondaryTextColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    item {
                        AlbumRatingSection(
                            rating = albumRating,
                            onRatingChanged = viewModel::updateAlbumRating,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Write your review",
                                style = MaterialTheme.typography.titleMedium,
                                color = PrimaryTextColor,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Box(modifier = Modifier.fillMaxWidth()) {

                                MultilineTextField(
                                    enabled = true,
                                    value = descriptionText,
                                    onValueChanged = viewModel::updateText,
                                    hintText = "Write your review here...",
                                    textStyle = artistsStyle,
                                    maxLines = 10,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(25.dp))
                                        .background(
                                            BackgroundColor,
                                            shape = RoundedCornerShape(25.dp)
                                        )
                                        .border(
                                            1.dp,
                                            AccentBorderColor,
                                            RoundedCornerShape(25.dp)
                                        )
                                        .animateContentSize()
                                )

                                Text(
                                    text = "$remainingCharacters",
                                    color = if (remainingCharacters < 50) TryoutRed else SecondaryTextColor,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(end = 16.dp, bottom = 8.dp)
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Rate the tracks",
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryTextColor,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                        )
                    }

                    item {
                        Text(
                            text = "Select up to 3 favorites",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryTextColor,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    items(state.album.tracks) { track ->
                        TrackRatingItem(
                            track = track,
                            rating = trackRatings[track.spotifyId],
                            isFavorite = favoriteTrackIds.contains(track.spotifyId),
                            onRatingChange = { rating ->
                                viewModel.updateTrackRating(track.spotifyId, rating)
                            },
                            onFavoriteToggle = {
                                viewModel.toggleFavoriteTrack(track.spotifyId)
                            },
                            canAddMoreFavorites = favoriteTrackIds.size < 3,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(48.dp),
                        )
                    }
                }
                Button(
                    onClick = { viewModel.submit { onSubmitSuccess() } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(48.dp)
                        .align(Alignment.BottomCenter),
                    enabled = !descriptionText.isBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VybesVeryDarkGray,
                        contentColor = PrimaryTextColor
                    )
                ) {
                    Text("Submit Review", color = PrimaryTextColor)
                }
            }

        }

        is AddAlbumViewModel.ReviewUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is AddAlbumViewModel.ReviewUiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error: ${state.message}",
                    color = TryoutRed,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onGoBack) {
                    Text("Go Back")
                }
            }
        }

        is AddAlbumViewModel.ReviewUiState.AlbumReviewExists -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
            ) {
                TopBarWithBackButton(onGoBack = onGoBack) {
                    Text(
                        text = stringResource(R.string.review_album),
                        color = PrimaryTextColor,
                        textAlign = TextAlign.Center,
                        style = songTitleStyle,
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(state.album.imageUrl)
                            .size(Size.ORIGINAL)
                            .crossfade(true)
                            .build(),
                        contentScale = ContentScale.Crop,
                    )

                    Image(
                        painter = painter,
                        contentDescription = "Album cover",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "You've already reviewed ${state.album.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = PrimaryTextColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = ElevatedBackgroundColor),
                        onClick = { onSeeReview(AlbumReviewScreen(state.album.reviewId!!)) }) {
                        Text("See Review", color = PrimaryTextColor)
                    }
                }
            }
        }

        else -> {

        }
    }
}

@Composable
fun AlbumRatingSection(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Rate this album",
            style = MaterialTheme.typography.titleMedium,
            color = PrimaryTextColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )


        Text(
            text = "Rating: $rating/10",
            style = MaterialTheme.typography.bodyMedium,
            color = PrimaryTextColor,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )

        Slider(
            value = rating.toFloat(),
            onValueChange = { onRatingChanged(it.roundToInt()) },
            valueRange = 0f..10f,
            steps = 9,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            colors = SliderDefaults.colors(
                thumbColor = TryoutBlue,
                activeTrackColor = TryoutBlue,
                activeTickColor = TryoutBlue
            )
        )
    }
}

@Composable
fun TrackRatingItem(
    track: Track,
    rating: TrackRating?,
    isFavorite: Boolean,
    onRatingChange: (TrackRating) -> Unit,
    onFavoriteToggle: () -> Unit,
    canAddMoreFavorites: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ElevatedBackgroundColor),
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.name,
                    style = songTitleStyle,
                    color = PrimaryTextColor,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 8.dp)
                )

                IconButton(
                    onClick = onFavoriteToggle,
                    enabled = isFavorite || canAddMoreFavorites
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) TryoutYellow else IconColor
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TrackRating.values().forEach { trackRating ->
                    TrackRatingChip(
                        rating = trackRating,
                        isSelected = rating == trackRating,
                        onClick = { onRatingChange(trackRating) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackRatingChip(
    rating: TrackRating,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = rating.displayName,
                fontSize = 12.sp,
                color = if (isSelected) rating.color else MaterialTheme.colorScheme.onSurface
            )
        },
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.Transparent,
            selectedContainerColor = Color.Transparent,
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedLabelColor = MaterialTheme.colorScheme.primary
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = if (isSelected) rating.color else Color.Gray
        )
    )
}