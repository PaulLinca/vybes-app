package com.example.vybes.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.common.util.DateUtils
import com.example.vybes.post.feed.StatsBar
import com.example.vybes.post.model.AlbumReview
import java.util.stream.Collectors

@Composable
fun AlbumReviewScreen(
    vybeViewModel: AlbumReviewViewModel = hiltViewModel(),
    onGoBack: () -> Unit,
    navController: NavController
) {
    val uiState by vybeViewModel.uiState.collectAsState()
    val commentText = vybeViewModel.commentText

    when (val state = uiState) {
        is PostViewModel.PostUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryTextColor),
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
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AlbumReviewPostContent(
                    vybe = state.post,
                    isLikedByUser = state.isLikedByCurrentUser,
                    commentText = commentText,
                    onGoBack = onGoBack,
                    onTextChanged = { vybeViewModel.updateText(it) },
                    onSendComment = { vybeViewModel.addComment() },
                    onLikeVybe = { vybeViewModel.likeAlbumReview() },
                    onUnlikeVybe = { vybeViewModel.unlikeAlbumReview() },
                    onLikeComment = { vybeViewModel.likeComment(it) },
                    onUnlikeComment = { vybeViewModel.unlikeComment(it) },
                    navController = navController
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
                onRetry = { vybeViewModel.refreshAlbumReview() },
                onGoBack = onGoBack
            )
        }

        is PostViewModel.PostUiState.Success -> {
            AlbumReviewPostContent(
                vybe = state.post,
                isLikedByUser = state.isLikedByCurrentUser,
                commentText = commentText,
                onGoBack = onGoBack,
                onTextChanged = { vybeViewModel.updateText(it) },
                onSendComment = { vybeViewModel.addComment() },
                onLikeVybe = { vybeViewModel.likeAlbumReview() },
                onUnlikeVybe = { vybeViewModel.unlikeAlbumReview() },
                onLikeComment = { vybeViewModel.likeComment(it) },
                onUnlikeComment = { vybeViewModel.unlikeComment(it) },
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlbumReviewPostContent(
    vybe: AlbumReview,
    isLikedByUser: Boolean,
    commentText: String,
    onGoBack: () -> Unit,
    onTextChanged: (String) -> Unit,
    onSendComment: () -> Unit,
    onLikeVybe: () -> Unit,
    onUnlikeVybe: () -> Unit,
    onLikeComment: (Long) -> Unit,
    onUnlikeComment: (Long) -> Unit,
    navController: NavController
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = { /* Should we refresh? */ }
    )

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
            TopBarWithBackButton(onGoBack = onGoBack) {
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

            AlbumBanner(vybe = vybe)

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
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp, bottom = 8.dp),
                    onLikeClicked = {
                        if (isLikedByUser) onUnlikeVybe() else onLikeVybe()
                    },
                    iconSize = 23.dp,
                    isLiked = isLikedByUser
                )
            }

            CommentSection(
                comments = vybe.comments.orEmpty(),
                onLikeComment = { commentId, isLiked ->
                    if (isLiked) onUnlikeComment(commentId) else onLikeComment(commentId)
                },
                modifier = Modifier.weight(1f),
                navController = navController
            )

            CommentInputBar(
                commentText = commentText,
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
fun AlbumBanner(vybe: AlbumReview) {
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
            .height(250.dp)
            .background(color = ElevatedBackgroundColor)
    ) {
        Box(
            modifier = Modifier
                .blur(5.dp)
                .paint(painter, contentScale = ContentScale.FillWidth)
                .fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .background(BackgroundColor.copy(alpha = 0.6f))
                .fillMaxSize(),
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shadow(8.dp)
            ) {
                Image(
                    painter = painter,
                    contentDescription = "Album Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

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
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = vybe.albumName,
                color = PrimaryTextColor,
                textAlign = TextAlign.Center,
                style = songTitleStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = vybe.artists.stream()
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