package com.example.vybes.post

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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
import com.example.vybes.R
import com.example.vybes.common.composables.DebouncedIconButton
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.SecondaryTextColor
import com.example.vybes.common.theme.TryoutBlue
import com.example.vybes.common.theme.TryoutGreen
import com.example.vybes.common.theme.TryoutOrange
import com.example.vybes.common.theme.TryoutRed
import com.example.vybes.common.theme.TryoutYellow
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.common.util.DateUtils
import com.example.vybes.model.AlbumReview
import com.example.vybes.model.TrackReviewDTO
import com.example.vybes.post.feed.FeedScreen
import com.example.vybes.post.feed.StatsBar
import com.example.vybes.sharedpreferences.SharedPreferencesManager

@Composable
fun AlbumReviewScreen(
    albumReviewViewModel: AlbumReviewViewModel = hiltViewModel(),
    onGoBack: () -> Unit,
    navController: NavController
) {

    val uiState by albumReviewViewModel.uiState.collectAsState()
    val commentText = albumReviewViewModel.commentText
    val remainingCharacters = albumReviewViewModel.remainingCharacters

    val navigationEvents = albumReviewViewModel.navigationEvents

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
                AlbumReviewPostContent(
                    albumReview = state.post,
                    isLikedByUser = state.isLikedByCurrentUser,
                    commentText = commentText,
                    onGoBack = onGoBack,
                    onDeletePost = { albumReviewViewModel.deletePost(state.post.id) },
                    onTextChanged = { albumReviewViewModel.updateText(it) },
                    onSendComment = { albumReviewViewModel.addComment() },
                    onLikeAlbumReview = { albumReviewViewModel.likeAlbumReview() },
                    onUnlikeAlbumReview = { albumReviewViewModel.unlikeAlbumReview() },
                    onLikeComment = { albumReviewViewModel.likeComment(it) },
                    onUnlikeComment = { albumReviewViewModel.unlikeComment(it) },
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
                onDeletePost = { albumReviewViewModel.deletePost(state.post.id) },
                onTextChanged = { albumReviewViewModel.updateText(it) },
                onSendComment = { albumReviewViewModel.addComment() },
                onLikeAlbumReview = { albumReviewViewModel.likeAlbumReview() },
                onUnlikeAlbumReview = { albumReviewViewModel.unlikeAlbumReview() },
                onLikeComment = { albumReviewViewModel.likeComment(it) },
                onUnlikeComment = { albumReviewViewModel.unlikeComment(it) },
                navController = navController,
                remainingCharacters = remainingCharacters
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
    navController: NavController,
    remainingCharacters: Int,
    onDeletePost: () -> Unit
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
                if (albumReview.user.userId == SharedPreferencesManager.getUserId()) {
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
                        onLikeClicked = {
                            if (isLikedByUser) onUnlikeAlbumReview() else onLikeAlbumReview()
                        },
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp, bottom = 8.dp),
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
                    remainingCharacters = remainingCharacters,
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
                    .size(180.dp)
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (score != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                when {
                                    score >= 8 -> TryoutGreen.copy(alpha = 0.15f)
                                    score >= 6 -> TryoutBlue.copy(alpha = 0.15f)
                                    score >= 4 -> TryoutYellow.copy(alpha = 0.15f)
                                    score >= 2 -> TryoutOrange.copy(alpha = 0.15f)
                                    else -> TryoutRed.copy(alpha = 0.15f)
                                },
                                ElevatedBackgroundColor.copy(alpha = 0.8f)
                            ),
                            start = Offset(500f, 0f),
                            end = Offset(500f, 200f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = score.toString(),
                        style = TextStyle(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp
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
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = SecondaryTextColor,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                ElevatedBackgroundColor.copy(alpha = 0.6f),
                                ElevatedBackgroundColor.copy(alpha = 0.3f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Rating",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    ),
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(20.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(TryoutBlue, TryoutGreen)
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "THOUGHTS",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = PrimaryTextColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Transparent
                    )
            ) {
                Column {
                    Text(
                        text = description,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 28.sp,
                            letterSpacing = 0.2.sp
                        ),
                        color = PrimaryTextColor.copy(alpha = 0.9f),
                        maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                        overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis
                    )

                    if (description.length > 200) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .clickable { isExpanded = !isExpanded }
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            PrimaryTextColor.copy(alpha = 0.05f),
                                            PrimaryTextColor.copy(alpha = 0.1f),
                                            PrimaryTextColor.copy(alpha = 0.05f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isExpanded) "SHOW LESS" else "READ MORE",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = PrimaryTextColor.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = PrimaryTextColor.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackRatingsSection(trackReviews: List<TrackReviewDTO>) {
    if (trackReviews.isNotEmpty()) {
        val favorites = trackReviews.filter { it.isFavorite }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(20.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(TryoutYellow, TryoutOrange)
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "TRACKS",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = PrimaryTextColor
                    )
                    if (favorites.isNotEmpty()) {
                        Text(
                            text = "${favorites.size} favorites • ${trackReviews.size} total",
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            ),
                            color = SecondaryTextColor.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (trackReviews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    PrimaryTextColor.copy(alpha = 0.03f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            1.dp,
                            PrimaryTextColor.copy(alpha = 0.05f),
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = PrimaryTextColor.copy(alpha = 0.05f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = SecondaryTextColor.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "NO TRACKS REVIEWED",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = SecondaryTextColor.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trackReviews.forEachIndexed { index, track ->
                        TrackRatingItem(
                            track = track,
                            index = index + 1,
                            isHighlighted = track.isFavorite
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackRatingItem(
    track: TrackReviewDTO,
    index: Int,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        if (isHighlighted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = 2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                TryoutYellow.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (isHighlighted)
                        ElevatedBackgroundColor.copy(alpha = 0.8f)
                    else
                        ElevatedBackgroundColor.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = if (isHighlighted) 1.dp else 0.dp,
                    color = if (isHighlighted)
                        TryoutYellow.copy(alpha = 0.3f)
                    else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Track indicator
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (isHighlighted)
                                TryoutYellow.copy(alpha = 0.2f)
                            else
                                PrimaryTextColor.copy(alpha = 0.08f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isHighlighted) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorite",
                            tint = TryoutYellow,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = index.toString().padStart(2, '0'),
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = PrimaryTextColor.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = track.name,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Normal,
                        letterSpacing = 0.1.sp
                    ),
                    color = PrimaryTextColor.copy(
                        alpha = if (isHighlighted) 1f else 0.8f
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                track.rating?.let { rating ->
                    Box(
                        modifier = Modifier
                            .background(
                                color = rating.color.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                rating.color.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = rating.displayName,
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = rating.color
                        )
                    }
                }
            }
        }
    }
}