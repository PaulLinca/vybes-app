package com.example.vybes.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.vybes.R
import com.example.vybes.network.response.MediaItem
import com.example.vybes.network.response.UserResponse
import com.example.vybes.common.composables.DebouncedIconButton
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.posts.PostFilter
import com.example.vybes.common.posts.PostFilterTabs
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.SecondaryTextColor
import com.example.vybes.common.theme.SubtleBorderColor
import com.example.vybes.common.theme.TryoutBlue
import com.example.vybes.common.theme.TryoutGreen
import com.example.vybes.common.theme.TryoutRed
import com.example.vybes.common.theme.VybesVeryLightGray
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.common.util.DateUtils
import com.example.vybes.feedback.FeedbackScreen
import com.example.vybes.post.feed.AlbumReviewCard
import com.example.vybes.post.feed.VybeCard
import com.example.vybes.model.AlbumReview
import com.example.vybes.model.AlbumReviewScreen
import com.example.vybes.model.Post
import com.example.vybes.model.User
import com.example.vybes.model.Vybe
import com.example.vybes.model.VybeScreen
import com.example.vybes.profile.favourites.EditFavouritesScreen
import com.example.vybes.profile.favourites.FavoriteType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

enum class ProfileMenuItem {
    FEEDBACK, LOGOUT
}

@Composable
fun ProfileScreen(
    user: User,
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.uploadSuccessMessage) {
        uiState.uploadSuccessMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            profileViewModel.clearSuccessMessage()
        }
    }

    LaunchedEffect(user.username) {
        profileViewModel.loadUser(user.username)
    }

    val isCurrentUser = profileViewModel.isCurrentUser(user)
    val showMenu = remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val imagePart = profileViewModel.createMultipartFromUri(context, it)
            profileViewModel.imagePart = imagePart
            profileViewModel.uploadProfilePicture()
        }
    }

    Scaffold(
        topBar = {
            ProfileTopBar(
                navController = navController,
                isCurrentUser = isCurrentUser,
                showMenu = showMenu,
                onMenuItemClick = { menuItem ->
                    when (menuItem) {
                        ProfileMenuItem.FEEDBACK -> navController.navigate(FeedbackScreen)
                        ProfileMenuItem.LOGOUT -> profileViewModel.logout()
                    }
                }
            )
        },
        backgroundColor = BackgroundColor
    ) { paddingValues ->
        ProfileContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            user = user,
            isCurrentUser = isCurrentUser,
            profileViewModel = profileViewModel,
            onUploadProfilePicture = { imagePickerLauncher.launch("image/*") },
            onRetryLoad = { profileViewModel.loadUser(user.username) },
            navController = navController
        )
    }
}

@Composable
private fun ProfileTopBar(
    isCurrentUser: Boolean,
    showMenu: MutableState<Boolean>,
    onMenuItemClick: (ProfileMenuItem) -> Unit,
    navController: NavController
) {
    TopBarWithBackButton(
        onGoBack = { navController.popBackStack() },
        rightButtonComposable = {
            if (isCurrentUser) {
                DebouncedIconButton(
                    onClick = { showMenu.value = true },
                    modifier = Modifier.size(35.dp),
                    contentDescription = "Menu",
                    iconResId = R.drawable.more
                )
                DropdownMenu(
                    expanded = showMenu.value,
                    onDismissRequest = { showMenu.value = false },
                    modifier = Modifier.background(ElevatedBackgroundColor)
                ) {
                    DropdownMenuItem(
                        onClick = {
                            onMenuItemClick(ProfileMenuItem.FEEDBACK)
                            showMenu.value = false
                        },
                        text = { Text("Send feedback", color = PrimaryTextColor) }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onMenuItemClick(ProfileMenuItem.LOGOUT)
                            showMenu.value = false
                        },
                        text = { Text("Logout", color = TryoutRed) }
                    )
                }
            }
        }
    )
}

@Composable
private fun ProfileContent(
    modifier: Modifier = Modifier,
    uiState: ProfileUiState,
    user: User,
    isCurrentUser: Boolean,
    profileViewModel: ProfileViewModel,
    onUploadProfilePicture: () -> Unit,
    onRetryLoad: () -> Unit,
    navController: NavController
) {
    when {
        uiState.isLoading -> LoadingIndicator()
        uiState.error != null -> ErrorView(
            message = uiState.error,
            onRetry = onRetryLoad
        )

        else -> ProfileLoadedContent(
            modifier = modifier,
            user = user,
            userState = uiState.user,
            isCurrentUser = isCurrentUser,
            profileViewModel = profileViewModel,
            onUploadProfilePicture = onUploadProfilePicture,
            navController = navController
        )
    }
}

@Composable
private fun ProfileLoadedContent(
    modifier: Modifier = Modifier,
    user: User,
    userState: UserResponse?,
    isCurrentUser: Boolean,
    profileViewModel: ProfileViewModel,
    onUploadProfilePicture: () -> Unit,
    navController: NavController
) {
    val filteredPosts by profileViewModel.filteredPosts.collectAsState()
    val selectedPostFilter by profileViewModel.selectedPostFilter.collectAsState()
    val isLoadingPosts by profileViewModel.isLoadingPosts.collectAsState()
    val isLoadingMorePosts by profileViewModel.isLoadingMorePosts.collectAsState()
    val hasMorePosts by profileViewModel.hasMorePosts.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .distinctUntilChanged { old, new ->
                old.size == new.size && old.lastOrNull()?.index == new.lastOrNull()?.index
            }
            .collectLatest { visibleItems ->
                if (!isLoadingPosts && !isLoadingMorePosts && hasMorePosts) {
                    val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: 0
                    val totalItems = listState.layoutInfo.totalItemsCount

                    if (lastVisibleItemIndex >= totalItems - 3) {
                        profileViewModel.loadMorePosts()
                    }
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ProfileHeader(
                username = user.username,
                profilePictureUrl = userState?.profilePictureUrl,
                onUploadProfilePicture = onUploadProfilePicture,
                isCurrentUser = isCurrentUser
            )
        }

        item {
            FavoritesSection(
                user = user,
                userState = userState,
                isCurrentUser = isCurrentUser,
                onEditFavoriteArtists = {
                    navController.navigate(EditFavouritesScreen(FavoriteType.ARTISTS.name))
                },
                onEditFavoriteAlbums = {
                    navController.navigate(EditFavouritesScreen(FavoriteType.ALBUMS.name))
                }
            )
        }

        item {
            PostsHeader(
                selectedPostFilter = selectedPostFilter,
                onFilterSelected = profileViewModel::setPostFilter
            )
        }

        postsContent(
            posts = filteredPosts,
            isLoadingPosts = isLoadingPosts,
            isLoadingMorePosts = isLoadingMorePosts,
            user = user,
            isCurrentUser = isCurrentUser,
            navController = navController
        )
    }
}

private fun LazyListScope.postsContent(
    posts: List<Post>,
    isLoadingPosts: Boolean,
    isLoadingMorePosts: Boolean,
    user: User,
    isCurrentUser: Boolean,
    navController: NavController
) {
    when {
        isLoadingPosts && posts.isEmpty() -> {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = White)
                }
            }
        }

        posts.isEmpty() -> {
            item {
                EmptyPostsMessage(
                    user = user,
                    isCurrentUser = isCurrentUser
                )
            }
        }

        else -> {
            items(posts) { post ->
                PostItem(
                    post = post,
                    navController = navController
                )
            }

            if (isLoadingMorePosts) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    username: String,
    profilePictureUrl: String?,
    onUploadProfilePicture: () -> Unit,
    isCurrentUser: Boolean
) {
    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfilePicture(
            profilePictureUrl = profilePictureUrl,
            username = username,
            onUploadProfilePicture = if (isCurrentUser) onUploadProfilePicture else null
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
private fun ProfilePicture(
    profilePictureUrl: String?,
    username: String,
    onUploadProfilePicture: (() -> Unit)?
) {
    val clickModifier = if (onUploadProfilePicture != null) {
        Modifier.clickable { onUploadProfilePicture() }
    } else Modifier

    if (profilePictureUrl == null) {
        Image(
            painter = painterResource(id = R.drawable.user),
            contentDescription = "Profile picture of $username",
            colorFilter = ColorFilter.tint(White),
            modifier = Modifier
                .size(64.dp)
                .then(clickModifier)
        )
    } else {
        Image(
            painter = rememberAsyncImagePainter(
                model = profilePictureUrl,
                error = painterResource(id = R.drawable.user)
            ),
            contentDescription = "Profile picture of $username",
            modifier = Modifier
                .size(96.dp)
                .border(
                    width = 2.dp,
                    color = SubtleBorderColor,
                    shape = CircleShape
                )
                .padding(2.dp)
                .clip(CircleShape)
                .then(clickModifier)
        )
    }
}

@Composable
private fun FavoritesSection(
    user: User,
    userState: UserResponse?,
    isCurrentUser: Boolean,
    onEditFavoriteArtists: () -> Unit,
    onEditFavoriteAlbums: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            color = PrimaryTextColor
        )

        FavoriteSection(
            title = "Favorite artists",
            items = userState?.favoriteArtists.orEmpty(),
            emptyMessage = if (isCurrentUser) {
                "You haven't set your favorite artists yet"
            } else {
                "${user.username} hasn't set their favorite artists"
            },
            isCurrentUser = isCurrentUser,
            isCircular = true,
            onSectionClick = onEditFavoriteArtists
        )

        FavoriteSection(
            title = "Favorite albums",
            items = userState?.favoriteAlbums.orEmpty(),
            emptyMessage = if (isCurrentUser) {
                "You haven't set your favorite albums yet"
            } else {
                "${user.username} hasn't set their favorite albums"
            },
            isCurrentUser = isCurrentUser,
            isCircular = false,
            onSectionClick = onEditFavoriteAlbums
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
                error = painterResource(id = R.drawable.user)
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
private fun PostsHeader(
    selectedPostFilter: PostFilter,
    onFilterSelected: (PostFilter) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Posts",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            color = PrimaryTextColor
        )
        PostFilterTabs(
            selectedFilter = selectedPostFilter,
            onFilterSelected = onFilterSelected,
            modifier = Modifier.padding(horizontal = 16.dp)
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

@Composable
private fun PostItem(
    post: Post,
    navController: NavController
) {
    Column {
        Text(
            text = DateUtils.formatPostedDate(post.postedDate),
            color = Color.LightGray,
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(4.dp))

        when (post) {
            is Vybe -> {
                VybeCard(
                    vybe = post,
                    onClickCard = { navController.navigate(VybeScreen(post.id)) }
                )
            }

            is AlbumReview -> {
                AlbumReviewCard(
                    albumReview = post,
                    onClickCard = { navController.navigate(AlbumReviewScreen(post.id)) }
                )
            }
        }
    }
}

@Composable
private fun EmptyPostsMessage(
    user: User,
    isCurrentUser: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isCurrentUser) {
                "You haven't posted anything yet"
            } else {
                "${user.username} hasn't posted anything yet"
            },
            color = SecondaryTextColor,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center
        )
    }
}