package com.example.vybes.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.SecondaryTextColor
import com.example.vybes.common.theme.SubtleBorderColor
import com.example.vybes.common.theme.TryoutBlue
import com.example.vybes.common.theme.TryoutGreen
import com.example.vybes.common.theme.TryoutOrange
import com.example.vybes.common.theme.TryoutRed
import com.example.vybes.common.theme.TryoutYellow
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.common.util.DateUtils
import com.example.vybes.post.feed.StatsBar
import com.example.vybes.post.model.AlbumReview
import com.example.vybes.post.model.TrackReviewDTO

@Composable
fun AlbumReviewScreen(
    albumReviewViewModel: AlbumReviewViewModel = hiltViewModel(),
    onGoBack: () -> Unit,
    navController: NavController
) {
    val uiState by albumReviewViewModel.uiState.collectAsState()
    val commentText = albumReviewViewModel.commentText

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
                    albumReview = state.post,
                    isLikedByUser = state.isLikedByCurrentUser,
                    commentText = commentText,
                    onGoBack = onGoBack,
                    onTextChanged = { albumReviewViewModel.updateText(it) },
                    onSendComment = { albumReviewViewModel.addComment() },
                    onLikeAlbumReview = { albumReviewViewModel.likeAlbumReview() },
                    onUnlikeAlbumReview = { albumReviewViewModel.unlikeAlbumReview() },
                    onLikeComment = { albumReviewViewModel.likeComment(it) },
                    onUnlikeComment = { albumReviewViewModel.unlikeComment(it) },
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
                onRetry = { albumReviewViewModel.refreshAlbumReview() },
                onGoBack = onGoBack
            )
        }

        is PostViewModel.PostUiState.Success -> {
            AlbumReviewPostContent(
                albumReview = state.post,
                isLikedByUser = state.isLikedByCurrentUser,
                commentText = commentText,
                onGoBack = onGoBack,
                onTextChanged = { albumReviewViewModel.updateText(it) },
                onSendComment = { albumReviewViewModel.addComment() },
                onLikeAlbumReview = { albumReviewViewModel.likeAlbumReview() },
                onUnlikeAlbumReview = { albumReviewViewModel.unlikeAlbumReview() },
                onLikeComment = { albumReviewViewModel.likeComment(it) },
                onUnlikeComment = { albumReviewViewModel.unlikeComment(it) },
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlbumReviewPostContent(
    albumReview: AlbumReview,
    isLikedByUser: Boolean,
    commentText: String,
    onGoBack: () -> Unit,
    onTextChanged: (String) -> Unit,
    onSendComment: () -> Unit,
    onLikeAlbumReview: () -> Unit,
    onUnlikeAlbumReview: () -> Unit,
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
                        text = albumReview.user.username,
                        color = PrimaryTextColor,
                        textAlign = TextAlign.Center,
                        style = songTitleStyle,
                    )
                    Text(
                        text = DateUtils.formatPostedDate(albumReview.postedDate),
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        style = artistsStyle,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                AlbumBanner(albumReview = albumReview)

                AlbumRatingSection(albumReview.score)

                AlbumDescriptionSection(albumReview.description)

                TrackRatingsSection(albumReview.trackReviews)

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    StatsBar(
                        post = albumReview,
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp, bottom = 8.dp),
                        onLikeClicked = {
                            if (isLikedByUser) onUnlikeAlbumReview() else onLikeAlbumReview()
                        },
                        iconSize = 23.dp,
                        isLiked = isLikedByUser
                    )
                }

                CommentSection(
                    comments = albumReview.comments.orEmpty(),
                    onLikeComment = { commentId, isLiked ->
                        if (isLiked) onUnlikeComment(commentId) else onLikeComment(commentId)
                    },
                    navController = navController
                )

                CommentInputBar(
                    commentText = commentText,
                    onTextChanged = onTextChanged,
                    onSendComment = onSendComment
                )
            }
        }
    }
}

@Composable
fun AlbumBanner(albumReview: AlbumReview) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(albumReview.imageUrl)
            .size(Size.ORIGINAL)
            .crossfade(true)
            .build(),
        contentScale = ContentScale.FillWidth,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .defaultMinSize(minHeight = 200.dp) // Minimum height to ensure background shows
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
            Text(
                text = "ALBUM REVIEW",
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = PrimaryTextColor.copy(alpha = 0.7f),
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                text = albumReview.albumName,
                style = songTitleStyle,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = albumReview.releaseDate.year.toString(),
                style = MaterialTheme.typography.body2,
                color = SecondaryTextColor,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = albumReview.artists.joinToString(", ") { it.name },
                style = artistsStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun AlbumRatingSection(score: Int?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        backgroundColor = ElevatedBackgroundColor,
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Overall Rating",
                style = MaterialTheme.typography.subtitle2,
                color = PrimaryTextColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (score != null) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = score.toString(),
                        style = MaterialTheme.typography.body1.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = when {
                            score >= 8 -> TryoutGreen
                            score >= 6 -> TryoutBlue
                            score >= 4 -> TryoutYellow
                            score >= 2 -> TryoutOrange
                            else -> TryoutRed
                        }
                    )
                    Text(
                        text = "/10",
                        style = MaterialTheme.typography.body1,
                        color = SecondaryTextColor
                    )
                }
            } else {
                Text(
                    text = "No rating provided",
                    style = MaterialTheme.typography.body2,
                    color = SecondaryTextColor
                )
            }
        }
    }
}

@Composable
fun AlbumDescriptionSection(description: String?) {
    if (!description.isNullOrBlank()) {
        var isExpanded by remember { mutableStateOf(false) }
        val maxLines = if (isExpanded) Int.MAX_VALUE else 3

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable {
                    if (description.length > 150) {
                        isExpanded = !isExpanded
                    }
                },
            backgroundColor = ElevatedBackgroundColor,
            elevation = 2.dp,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Review",
                    style = MaterialTheme.typography.subtitle2,
                    color = PrimaryTextColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.body2,
                    color = SecondaryTextColor,
                    maxLines = maxLines,
                    overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis,
                )

                if (description.length > 150) {
                    Text(
                        text = if (isExpanded) "Show less" else "Show more",
                        style = MaterialTheme.typography.caption,
                        color = PrimaryTextColor,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable { isExpanded = !isExpanded }
                    )
                }
            }
        }
    }
}

@Composable
fun TrackRatingsSection(trackReviews: List<TrackReviewDTO>) {
    if (trackReviews.isNotEmpty()) {
        var isExpanded by remember { mutableStateOf(false) }
        val favorites = trackReviews.filter { it.isFavorite }.take(3)
        val displayTracks = if (isExpanded) trackReviews else favorites

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(onClick = { isExpanded = !isExpanded }),
            backgroundColor = ElevatedBackgroundColor,
            elevation = 2.dp,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tracks",
                        style = MaterialTheme.typography.subtitle2,
                        color = PrimaryTextColor,
                    )

                    if (trackReviews.size > 3) {
                        Text(
                            text = if (isExpanded) "Show favorites" else "Show all",
                            color = PrimaryTextColor,
                            style = MaterialTheme.typography.caption,
                        )

                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                displayTracks.forEach { track ->
                    TrackRatingItem(
                        track = track,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Divider(color = SubtleBorderColor)
                }

                if (displayTracks.isEmpty() && !isExpanded) {
                    Text(
                        text = "No favorite tracks selected",
                        style = MaterialTheme.typography.body2,
                        color = SecondaryTextColor,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TrackRatingItem(
    track: TrackReviewDTO,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (track.isFavorite) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Favorite",
                    tint = TryoutYellow,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 8.dp)
                )
            }

            Text(
                text = track.name,
                style = MaterialTheme.typography.body2,
                color = PrimaryTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        track.rating?.let {
            Text(
                text = it.displayName,
                style = MaterialTheme.typography.caption,
                color = it.color,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}