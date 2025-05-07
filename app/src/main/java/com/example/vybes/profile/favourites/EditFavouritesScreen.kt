package com.example.vybes.profile.favourites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.vybes.R
import com.example.vybes.auth.model.MediaItem
import com.example.vybes.common.composables.MultilineTextField
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.IconColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.SubtleBorderColor
import com.example.vybes.common.theme.TryoutGreen
import com.example.vybes.common.theme.TryoutRed
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.post.feed.FeedScreen
import com.example.vybes.post.model.User
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import kotlinx.serialization.Serializable

enum class FavoriteType {
    ARTISTS, ALBUMS
}

@Serializable
data class EditFavouritesScreen(val favoriteType: String)

@Composable
fun EditFavouritesScreen(
    navController: NavController,
    editFavouritesViewModel: EditFavouritesViewModel = hiltViewModel()
) {
    val uiState by editFavouritesViewModel.uiState.collectAsState()
    val currentFavorites by editFavouritesViewModel.currentFavorites.collectAsState()
    val selectedIndex by editFavouritesViewModel.selectedIndex.collectAsState()
    val searchQuery by editFavouritesViewModel.searchQuery.collectAsState()
    val searchResults by editFavouritesViewModel.searchResults.collectAsState()
    val favoriteType = editFavouritesViewModel.favoriteType

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            editFavouritesViewModel.resetSaveSuccess()
            navController.popBackStack()
        }
    }
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            editFavouritesViewModel.resetSaveSuccess()

            navController.popBackStack(FeedScreen, false)
            navController.navigate(User(1, SharedPreferencesManager.getUsername().orEmpty()))
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            // TODO Show something on error
            editFavouritesViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopBarWithBackButton(onGoBack = { navController.popBackStack() }) {
                Text(
                    text = "Edit favorites",
                    color = PrimaryTextColor,
                    textAlign = TextAlign.Center,
                    style = songTitleStyle,
                )
            }
        },
        containerColor = BackgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editFavouritesViewModel.saveFavorites() },
                containerColor = ElevatedBackgroundColor
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.save),
                    contentDescription = "Save favorites",
                    tint = IconColor,
                    modifier = Modifier.size(35.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                currentFavorites.forEachIndexed { index, item ->
                    FavoriteItem(
                        type = favoriteType,
                        item = item,
                        isSelected = selectedIndex == index,
                        onClick = { editFavouritesViewModel.selectFavoriteToReplace(index) },
                        onDelete = { editFavouritesViewModel.deleteFavorite(index) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            MultilineTextField(
                value = searchQuery,
                onValueChanged = { editFavouritesViewModel.updateSearchQuery(it) },
                hintText = deriveHintText(selectedIndex, favoriteType),
                textStyle = artistsStyle,
                maxLines = 1,
                enabled = selectedIndex != null,
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isSearching) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TryoutGreen)
                }
            } else if (searchQuery.length >= 2 && searchResults.isEmpty()) {
                Text(
                    text = "No results found",
                    color = PrimaryTextColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults) { item ->
                        SearchResultItem(
                            type = favoriteType,
                            item = item,
                            onClick = {
                                if (selectedIndex != null) {
                                    editFavouritesViewModel.replaceSelectedFavorite(item)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = TryoutGreen)
        }
    }
}

@Composable
fun FavoriteItem(
    type: FavoriteType,
    item: MediaItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val shape = if (type == FavoriteType.ARTISTS) CircleShape else RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier
            .size(100.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .border(
                    width = 2.dp,
                    color = if (isSelected) TryoutGreen else SubtleBorderColor,
                    shape = shape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (item.isEmpty) {
                Image(
                    painter = painterResource(id = R.drawable.add_icon_transparent),
                    contentDescription = "Add favorite",
                    colorFilter = ColorFilter.tint(IconColor),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = item.imageUrl,
                        error = painterResource(id = R.drawable.add_icon_transparent_colored)
                    ),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape)
                )
            }
        }

        if (!item.isEmpty) {
            val offset = if (type == FavoriteType.ALBUMS) 12 else 0
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = offset.dp, y = (-offset).dp)
                    .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove favorite",
                    tint = TryoutRed,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun SearchResultItem(
    type: FavoriteType,
    item: MediaItem,
    onClick: () -> Unit
) {
    val shape = if (type == FavoriteType.ARTISTS) CircleShape else RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ElevatedBackgroundColor)
            .clickable(onClick = onClick)
            .padding(top = 4.dp, bottom = 4.dp, start = 4.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = item.imageUrl),
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(shape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.name,
                color = PrimaryTextColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Select",
            tint = IconColor
        )
    }
}

private fun deriveHintText(
    selectedIndex: Int?, favoriteType: FavoriteType
) = if (selectedIndex == null) {
    "Select a slot to add your favorite"
} else "Search for " + (if (favoriteType == FavoriteType.ARTISTS) "artists" else "albums") + "..."