package com.example.vybes.post

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.vybes.R
import com.example.vybes.common.composables.DebouncedIconButton
import com.example.vybes.common.composables.MultilineTextField
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.SecondaryTextColor
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.common.util.DateUtils
import com.example.vybes.post.feed.StatsBar
import com.example.vybes.post.model.Comment
import com.example.vybes.post.model.Vybe
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

    when (val state = uiState) {
        is VybeViewModel.VybeUiState.Loading -> {
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

        is VybeViewModel.VybeUiState.LoadingCall -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                VybePostContent(
                    vybe = state.vybe,
                    isLikedByUser = state.isLikedByCurrentUser,
                    commentText = commentText,
                    onGoBack = onGoBack,
                    onTextChanged = { vybeViewModel.updateText(it) },
                    onSendComment = { vybeViewModel.addComment() },
                    onLikeVybe = { vybeViewModel.likeVybe() },
                    onUnlikeVybe = { vybeViewModel.unlikeVybe() },
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

        is VybeViewModel.VybeUiState.Error -> {
            ErrorScreen(
                errorMessage = state.message,
                onRetry = { vybeViewModel.refreshVybe() },
                onGoBack = onGoBack
            )
        }

        is VybeViewModel.VybeUiState.Success -> {
            VybePostContent(
                vybe = state.vybe,
                isLikedByUser = state.isLikedByCurrentUser,
                commentText = commentText,
                onGoBack = onGoBack,
                onTextChanged = { vybeViewModel.updateText(it) },
                onSendComment = { vybeViewModel.addComment() },
                onLikeVybe = { vybeViewModel.likeVybe() },
                onUnlikeVybe = { vybeViewModel.unlikeVybe() },
                onLikeComment = { vybeViewModel.likeComment(it) },
                onUnlikeComment = { vybeViewModel.unlikeComment(it) },
                navController = navController
            )
        }
    }
}

@Composable
fun ErrorScreen(
    errorMessage: String,
    onRetry: () -> Unit,
    onGoBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryTextColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryTextColor,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onGoBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElevatedBackgroundColor
                )
            ) {
                Text("Go Back", color = SecondaryTextColor)
            }

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Retry")
            }
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

            SongBanner(vybe = vybe)

            if (vybe.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = vybe.description, style = artistsStyle, color = Color.LightGray)
                }
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                StatsBar(
                    vybe = vybe,
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
fun CommentInputBar(
    commentText: String,
    onTextChanged: (String) -> Unit,
    onSendComment: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        MultilineTextField(
            value = commentText,
            onValueChanged = onTextChanged,
            hintText = "Add a comment...",
            textStyle = artistsStyle,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(25.dp))
                .background(BackgroundColor, shape = RoundedCornerShape(25.dp))
                .border(1.dp, PrimaryTextColor, RoundedCornerShape(25.dp))
        )

        DebouncedIconButton(
            onClick = onSendComment,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentDescription = "Send Button",
            iconResId = R.drawable.send
        )
    }
}

@Composable
fun CommentSection(
    comments: List<Comment>,
    onLikeComment: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        if (comments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No comments yet. Be the first to comment!",
                    color = Color.LightGray,
                    style = artistsStyle,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            comments.forEach { comment ->
                CommentItem(
                    comment = comment,
                    onLikeComment = onLikeComment,
                    onUserClick = { navController.navigate(comment.user) }
                )
                Divider(
                    color = Color.DarkGray.copy(alpha = 0.5f),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    onLikeComment: (Long, Boolean) -> Unit,
    onUserClick: () -> Unit
) {
    val currentUserId = SharedPreferencesManager.getUserId()
    val isLikedByCurrentUser = comment.likes.orEmpty().any { it.userId == currentUserId }

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                DebouncedIconButton(
                    onClick = { onUserClick() },
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape),
                    contentDescription = "Go to user profile",
                    iconResId = R.drawable.user
                )
                Text(
                    text = comment.user.username,
                    textAlign = TextAlign.Start,
                    color = PrimaryTextColor,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .clickable { onUserClick }
                )

                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = DateUtils.formatPostedDate(comment.timestamp),
                        textAlign = TextAlign.Start,
                        color = Color.LightGray,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(25.dp))
                Text(
                    text = comment.text,
                    textAlign = TextAlign.Start,
                    color = PrimaryTextColor,
                    style = artistsStyle,
                )
            }
        }

        Column(
            Modifier.padding(horizontal = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DebouncedIconButton(
                onClick = { onLikeComment(comment.id, isLikedByCurrentUser) },
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterHorizontally),
                contentDescription = if (isLikedByCurrentUser) "Unlike comment" else "Like comment",
                iconResId = if (isLikedByCurrentUser) R.drawable.heart_filled else R.drawable.heart
            )
            Text(
                text = comment.likes.orEmpty().size.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = PrimaryTextColor,
                    fontSize = 10.sp
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
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
                text = vybe.songName,
                color = PrimaryTextColor,
                textAlign = TextAlign.Center,
                style = songTitleStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

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