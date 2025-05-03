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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.vybes.R
import com.example.vybes.add.SearchTrackScreen
import com.example.vybes.common.composables.DebouncedIconButton
import com.example.vybes.common.composables.TopBarWithSideButtons
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.IconColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.TryoutRed
import com.example.vybes.common.theme.VybesVeryLightGray
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.logoStyle
import com.example.vybes.post.model.User
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
    val vybes by viewModel.vybes.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val hasMoreContent by viewModel.hasMoreContent.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .distinctUntilChanged { old, new ->
                old.size == new.size && old.lastOrNull()?.index == new.lastOrNull()?.index
            }
            .collectLatest { visibleItems ->
                if (!isLoading && !isLoadingMore && hasMoreContent) {
                    val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: 0
                    val totalItems = vybes.size

                    if (lastVisibleItemIndex >= totalItems - 3) {
                        viewModel.loadMorePosts()
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
            TopBar(navController)

            // Error state banner
            errorState?.let { error ->
                ErrorBanner(
                    errorMessage = error,
                    onDismiss = { viewModel.clearError() }
                )
            }

            when {
                isLoading && vybes.isEmpty() -> {
                    LoadingState()
                }

                vybes.isEmpty() -> {
                    EmptyFeedState(navController)
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(vybes) { vybe ->
                            val currentUserId = SharedPreferencesManager.getUserId()
                            val isLikedByCurrentUser = vybe.likes.any { it.userId == currentUserId }

                            VybePost(
                                vybe = vybe,
                                onClickCard = { navController.navigate(VybeScreen(vybe.id)) },
                                onLikeClicked = {
                                    viewModel.clickLikeButton(
                                        vybe.id,
                                        isLikedByCurrentUser
                                    )
                                },
                                navController = navController
                            )
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
private fun EmptyFeedState(navController: NavController) {
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
fun TopBar(navController: NavController) {
    TopBarWithSideButtons(
        leftButtonComposable = {
            DebouncedIconButton(
                onClick = { navController.navigate(SearchTrackScreen) },
                modifier = Modifier
                    .size(35.dp),
                contentDescription = "Add New Vybe Button",
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