package com.example.vybes.post.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.vybes.R
import com.example.vybes.add.SearchAlbumScreen
import com.example.vybes.add.SearchTrackScreen
import com.example.vybes.common.composables.DebouncedIconButton
import com.example.vybes.common.composables.TopBarWithSideButtons
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.IconColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.SecondaryTextColor
import com.example.vybes.common.theme.TryoutRed
import com.example.vybes.common.theme.VybesVeryLightGray
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.logoStyle
import com.example.vybes.post.model.AlbumReview
import com.example.vybes.post.model.AlbumReviewScreen
import com.example.vybes.post.model.User
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.model.VybeScreen
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.serialization.Serializable

@Serializable
object FeedScreen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FeedScreen(
    navController: NavController,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )
    val errorState by viewModel.errorState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val posts by viewModel.posts.collectAsState()

    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val hasMoreContent by viewModel.hasMoreContent.collectAsState()

    val listState = rememberLazyListState()

    var showOptionsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .distinctUntilChanged { old, new ->
                old.size == new.size && old.lastOrNull()?.index == new.lastOrNull()?.index
            }
            .collectLatest { visibleItems ->
                if (!isLoading && !isLoadingMore && hasMoreContent) {
                    val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: 0
                    val totalItems = posts.size

                    if (lastVisibleItemIndex >= totalItems - 3) {
                        viewModel.loadMorePosts()
                    }
                }
            }
    }

    if (showOptionsDialog) {
        Dialog(
            onDismissRequest = { showOptionsDialog = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = BackgroundColor
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "New Post",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = PrimaryTextColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OptionButton(
                            icon = R.drawable.music_note,
                            text = "Song",
                            onClick = {
                                showOptionsDialog = false
                                navController.navigate(SearchTrackScreen)
                            }
                        )
                        OptionButton(
                            icon = R.drawable.album,
                            text = "Album review",
                            onClick = {
                                showOptionsDialog = false
                                navController.navigate(SearchAlbumScreen)
                            }
                        )
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 10.dp)
            .pullRefresh(pullRefreshState)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                navController = navController,
                onShowOptionsDialogChange = { showOptionsDialog = it }
            )

            errorState?.let { error ->
                ErrorBanner(
                    errorMessage = error,
                    onDismiss = { viewModel.clearError() }
                )
            }

            when {
                isLoading && posts.isEmpty() -> {
                    LoadingState()
                }

                posts.isEmpty() -> {
                    EmptyFeedState()
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(posts) { post ->
                            when (post) {
                                is Vybe -> {
                                    val currentUserId = SharedPreferencesManager.getUserId()
                                    val isLikedByCurrentUser = post.likes?.any { it.userId == currentUserId } == true
                                    VybePost(
                                        vybe = post,
                                        onClickCard = { navController.navigate(VybeScreen(post.id)) },
                                        onLikeClicked = {
                                            viewModel.clickLikeButton(
                                                post.id,
                                                isLikedByCurrentUser
                                            )
                                        },
                                        navController = navController
                                    )
                                }

                                is AlbumReview -> {
                                    val currentUserId = SharedPreferencesManager.getUserId()
                                    val isLikedByCurrentUser = post.likes?.any { it.userId == currentUserId } == true
                                    AlbumReviewPost(
                                        albumReview = post,
                                        onClickCard = { navController.navigate(AlbumReviewScreen(post.id)) },
                                        onLikeClicked = {
                                            viewModel.clickLikeButton(
                                                post.id,
                                                isLikedByCurrentUser
                                            )
                                        },
                                        navController = navController
                                    )
                                }
                            }
                        }

                        // Loading indicator at bottom when loading more items
                        if (isLoadingMore) {
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
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = VybesVeryLightGray,
            contentColor = White
        )
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = White)
    }
}

@Composable
fun ErrorBanner(errorMessage: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(TryoutRed)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = errorMessage,
                color = White,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.body2
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onDismiss() }
            )
        }
    }
}

@Composable
private fun EmptyFeedState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(70.dp))
        Image(
            painter = painterResource(id = R.drawable.empty_feed_art),
            contentDescription = "Empty Feed",
            modifier = Modifier
                .size(160.dp)
                .padding(bottom = 16.dp),
            colorFilter = ColorFilter.tint(IconColor)
        )

        Text(
            text = "It's quiet here...",
            style = MaterialTheme.typography.h6,
            color = PrimaryTextColor,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Share a vybe to add some music to the mix!",
            style = MaterialTheme.typography.body1,
            color = PrimaryTextColor.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )
    }
}

@Composable
fun TopBar(
    navController: NavController,
    onShowOptionsDialogChange: (Boolean) -> Unit
) {
    TopBarWithSideButtons(
        leftButtonComposable = {
            DebouncedIconButton(
                onClick = { onShowOptionsDialogChange(true) },
                modifier = Modifier
                    .size(35.dp),
                contentDescription = "Add New Post Button",
                iconResId = R.drawable.add_icon_square
            )
        },
        centerComposable = {
            Text(
                text = stringResource(R.string.app_name),
                color = White,
                style = logoStyle
            )
        },
        rightButtonComposable = {
            DebouncedIconButton(
                onClick = {
                    navController.navigate(
                        User(
                            userId = SharedPreferencesManager.getUserId(),
                            username = SharedPreferencesManager.getUsername().orEmpty()
                        )
                    )
                },
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
                contentDescription = "Profile Button",
                iconResId = R.drawable.user
            )
        }
    )
}

@Composable
fun OptionButton(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = ElevatedBackgroundColor,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                tint = IconColor,
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            color = SecondaryTextColor,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}