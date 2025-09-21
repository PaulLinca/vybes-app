package com.example.vybes.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.vybes.R
import com.example.vybes.common.composables.DebouncedIconButton
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.TryoutRed
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.common.util.DateUtils
import com.example.vybes.model.Vybe
import com.example.vybes.post.feed.FeedScreen
import com.example.vybes.post.feed.StatsBar
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import java.util.stream.Collectors

@Composable
fun VybePostScreen(
    vybeViewModel: VybeViewModel = hiltViewModel(),
    onGoBack: () -> Unit,
    navController: NavController
) {
    val uiState by vybeViewModel.uiState.collectAsState()
    val commentText = vybeViewModel.commentText
    val remainingCharacters = vybeViewModel.remainingCharacters

    val navigationEvents = vybeViewModel.navigationEvents

    LaunchedEffect(Unit) {
        navigationEvents.collect { event ->
            when (event) {
                is PostViewModel.NavigationEvent.NavigateToHomeClearingBackStack -> {
                    navController.navigate(FeedScreen) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    when (val state = uiState) {
        is PostViewModel.PostUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = PrimaryTextColor
                )
            }
        }

        is PostViewModel.PostUiState.LoadingCall -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                VybePostContent(
                    vybe = state.post,
                    isLikedByUser = state.isLikedByCurrentUser,
                    commentText = commentText,
                    onGoBack = onGoBack,
                    onDeletePost = { vybeViewModel.deletePost(state.post.id) },
                    onTextChanged = { vybeViewModel.updateText(it) },
                    onSendComment = { vybeViewModel.addComment() },
                    onLikeVybe = { vybeViewModel.likeVybe() },
                    onUnlikeVybe = { vybeViewModel.unlikeVybe() },
                    onLikeComment = { vybeViewModel.likeComment(it) },
                    onUnlikeComment = { vybeViewModel.unlikeComment(it) },
                    navController = navController,
                    remainingCharacters = remainingCharacters
                )
                Box(
                    modifier = Modifier
                        .background(BackgroundColor.copy(alpha = 0.8f))
                        .fillMaxSize(),
                )
                Text(text = "Loading...", color = PrimaryTextColor)
            }
        }

        is PostViewModel.PostUiState.Error -> {
            ErrorScreen(
                errorMessage = state.message,
                onRetry = { vybeViewModel.refreshVybe() },
                onGoBack = onGoBack
            )
        }

        is PostViewModel.PostUiState.Success -> {
            VybePostContent(
                vybe = state.post,
                isLikedByUser = state.isLikedByCurrentUser,
                commentText = commentText,
                onGoBack = onGoBack,
                onTextChanged = { vybeViewModel.updateText(it) },
                onSendComment = { vybeViewModel.addComment() },
                onDeletePost = { vybeViewModel.deletePost(state.post.id) },
                onLikeVybe = { vybeViewModel.likeVybe() },
                onUnlikeVybe = { vybeViewModel.unlikeVybe() },
                onLikeComment = { vybeViewModel.likeComment(it) },
                onUnlikeComment = { vybeViewModel.unlikeComment(it) },
                navController = navController,
                remainingCharacters = remainingCharacters
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VybePostContent(
    vybe: Vybe,
    isLikedByUser: Boolean,
    commentText: String,
    onGoBack: () -> Unit,
    onTextChanged: (String) -> Unit,
    onSendComment: () -> Unit,
    onDeletePost: () -> Unit,
    onLikeVybe: () -> Unit,
    onUnlikeVybe: () -> Unit,
    onLikeComment: (Long) -> Unit,
    onUnlikeComment: (Long) -> Unit,
    navController: NavController,
    remainingCharacters: Int
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = { /* Should we refresh? */ }
    )
    val showMenu = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            TopBarWithBackButton(onGoBack = onGoBack, rightButtonComposable = {
                if (vybe.user.userId == SharedPreferencesManager.getUserId()) {
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
                                onDeletePost()
                                showMenu.value = false
                            },
                            text = { Text("Delete post", color = TryoutRed) }
                        )
                    }
                }
            }) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = vybe.user.username,
                        color = PrimaryTextColor,
                        textAlign = TextAlign.Center,
                        style = songTitleStyle,
                    )
                    Text(
                        text = DateUtils.formatPostedDate(vybe.postedDate),
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        style = artistsStyle,
                    )
                }
            }

            SongBanner(vybe = vybe)

            if (vybe.description.orEmpty().isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = vybe.description.orEmpty(),
                        style = artistsStyle,
                        color = Color.LightGray
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                StatsBar(
                    post = vybe,
                    onLikeClicked = {
                        if (isLikedByUser) onUnlikeVybe() else onLikeVybe()
                    },
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp, bottom = 8.dp),
                    iconSize = 23.dp,
                    isLiked = isLikedByUser
                )
            }

            CommentSection(
                comments = vybe.comments.orEmpty(),
                onLikeComment = { commentId, isLiked ->
                    if (isLiked) onUnlikeComment(commentId) else onLikeComment(commentId)
                },
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                navController = navController
            )

            CommentInputBar(
                commentText = commentText,
                remainingCharacters = remainingCharacters,
                onTextChanged = onTextChanged,
                onSendComment = onSendComment
            )
        }

        PullRefreshIndicator(
            refreshing = false,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = ElevatedBackgroundColor,
            contentColor = PrimaryTextColor
        )
    }
}

@Composable
fun SongBanner(vybe: Vybe) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(vybe.imageUrl)
            .size(Size.ORIGINAL)
            .crossfade(true)
            .build(),
        contentScale = ContentScale.FillWidth,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .defaultMinSize(minHeight = 200.dp)
            .background(color = ElevatedBackgroundColor)
    ) {

        Box(
            modifier = Modifier
                .blur(5.dp)
                .paint(painter, contentScale = ContentScale.FillWidth)
                .matchParentSize(),
        )

        Box(
            modifier = Modifier
                .background(BackgroundColor.copy(alpha = 0.6f))
                .matchParentSize()
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(TryoutRed, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "NOW PLAYING",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .shadow(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Color.Black,
                            CircleShape
                        )
                        .border(2.dp, Color(0xFF333333), CircleShape)
                        .fillMaxSize()
                ) {
//                    repeat(4) { index ->
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding((12 + index * 8).dp)
//                                .border(
//                                    0.5.dp,
//                                    Color.White.copy(alpha = 0.1f),
//                                    CircleShape
//                                )
//                        )
//                    }
                }

                Image(
                    painter = painter,
                    contentDescription = "Album cover",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color.Black, CircleShape)
                        .border(1.dp, Color(0xFF444444), CircleShape)
                )
            }

            if (painter.state is AsyncImagePainter.State.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = PrimaryTextColor,
                        strokeWidth = 2.dp
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = vybe.songName,
                color = PrimaryTextColor,
                textAlign = TextAlign.Center,
                style = songTitleStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = vybe.spotifyArtists.stream()
                    .map { a -> a.name }
                    .collect(Collectors.joining(", ")),
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                style = artistsStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}