package com.example.vybes.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.vybes.R
import com.example.vybes.auth.model.MediaItem
import com.example.vybes.auth.model.UserResponse
import com.example.vybes.common.composables.DebouncedIconButton
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.SecondaryTextColor
import com.example.vybes.common.theme.TryoutBlue
import com.example.vybes.common.theme.TryoutGreen
import com.example.vybes.common.theme.TryoutRed
import com.example.vybes.common.theme.VybesVeryLightGray
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.feedback.FeedbackScreen
import com.example.vybes.post.model.User
import com.example.vybes.profile.favourites.EditFavouritesScreen
import com.example.vybes.profile.favourites.FavoriteType

@Composable
fun ProfileScreen(
    user: User,
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = user.username) {
        profileViewModel.loadUser(user.username)
    }

    val userState by profileViewModel.user.collectAsState()
    val isCurrentUser = profileViewModel.isCurrentUser(user)
    val uiState = profileViewModel.uiState.collectAsState()
    val showMenu = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBarWithBackButton(
                onGoBack = { navController.popBackStack() },
                rightButtonComposable = {
                    if (isCurrentUser) {
                        DebouncedIconButton(
                            onClick = { showMenu.value = true },
                            modifier = Modifier.size(35.dp),
                            contentDescription = "Go back",
                            iconResId = R.drawable.more
                        )
                        DropdownMenu(
                            expanded = showMenu.value,
                            onDismissRequest = { showMenu.value = false },
                            modifier = Modifier.background(ElevatedBackgroundColor)
                        ) {
                            DropdownMenuItem(
                                onClick = { navController.navigate(FeedbackScreen) },
                                text = {
                                    Text("Send feedback", color = PrimaryTextColor)
                                })
                            DropdownMenuItem(onClick = { profileViewModel.logout() }, text = {
                                Text("Logout", color = TryoutRed)
                            })
                        }
                    }
                })
        },
        backgroundColor = BackgroundColor
    ) { paddingValues ->
        when {
            uiState.value.isLoading -> {
                LoadingIndicator()
            }

            uiState.value.error != null -> {
                ErrorView(message = uiState.value.error!!) {
                    profileViewModel.loadUser(user.username)
                }
            }

            else -> {
                ProfileContent(
                    modifier = Modifier.padding(paddingValues),
                    user = user,
                    userState = userState,
                    isCurrentUser = isCurrentUser,
                    onEditFavoriteArtists = {
                        navController.navigate(
                            EditFavouritesScreen(
                                FavoriteType.ARTISTS.name
                            )
                        )
                    },
                    onEditFavoriteAlbums = {
                        navController.navigate(
                            EditFavouritesScreen(
                                FavoriteType.ALBUMS.name
                            )
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    modifier: Modifier = Modifier,
    user: User,
    userState: UserResponse?,
    isCurrentUser: Boolean,
    onEditFavoriteArtists: () -> Unit,
    onEditFavoriteAlbums: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        ProfileHeader(username = user.username)

        Spacer(modifier = Modifier.height(30.dp))

        FavoriteSection(
            title = "Favourite artists",
            items = userState?.favoriteArtists.orEmpty(),
            emptyMessage = if (isCurrentUser) {
                "You haven't set your favourite artists yet"
            } else {
                "${user.username} hasn't set their favourite artists"
            },
            isCurrentUser = isCurrentUser,
            isCircular = true,
            onSectionClick = onEditFavoriteArtists
        )

        Spacer(modifier = Modifier.height(30.dp))

        FavoriteSection(
            title = "Favourite albums",
            items = userState?.favoriteAlbums.orEmpty(),
            emptyMessage = if (isCurrentUser) {
                "You haven't set your favourite albums yet"
            } else {
                "${user.username} hasn't set their favourite albums"
            },
            isCurrentUser = isCurrentUser,
            isCircular = false,
            onSectionClick = onEditFavoriteAlbums
        )
    }
}

@Composable
private fun ProfileHeader(username: String) {
    Column(
        modifier = Modifier
            .padding(top = 40.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.user),
            contentDescription = "Profile picture of $username",
            colorFilter = ColorFilter.tint(White),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = username,
            color = White,
            style = songTitleStyle
        )
    }
}

@Composable
private fun <T : MediaItem> FavoriteSection(
    title: String,
    items: List<T>,
    emptyMessage: String,
    isCurrentUser: Boolean,
    isCircular: Boolean,
    onSectionClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(ElevatedBackgroundColor, shape = RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = { if (isCurrentUser) onSectionClick() }
            )
            .padding(16.dp)
    ) {
        Column {
            Text(text = title, color = White)

            Spacer(modifier = Modifier.height(16.dp))

            if (items.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items) { item ->
                        MediaItemThumbnail(
                            item = item,
                            isCircular = isCircular
                        )
                    }
                }
            } else {
                EmptyStateMessage(
                    message = emptyMessage,
                    isCurrentUser = isCurrentUser
                )
            }
        }
    }
}

@Composable
private fun MediaItemThumbnail(
    item: MediaItem,
    isCircular: Boolean
) {
    val shape = if (isCircular) CircleShape else RoundedCornerShape(12.dp)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = rememberAsyncImagePainter(
                model = item.imageUrl,
                error = painterResource(id = R.drawable.spotify)
            ),
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(shape)
                .border(1.dp, TryoutGreen, shape)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.name,
            color = SecondaryTextColor,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(72.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyStateMessage(
    message: String,
    isCurrentUser: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.body2,
            color = PrimaryTextColor.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        if (isCurrentUser) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tap to edit",
                style = MaterialTheme.typography.body2,
                color = TryoutBlue,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = VybesVeryLightGray)
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: $message",
            color = TryoutRed,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(backgroundColor = ElevatedBackgroundColor)
        ) {
            Text("Retry", color = PrimaryTextColor)
        }
    }
}
