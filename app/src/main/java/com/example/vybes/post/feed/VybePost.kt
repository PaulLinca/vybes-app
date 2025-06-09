package com.example.vybes.post.feed

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.vybes.R
import com.example.vybes.common.composables.DebouncedIconButton
import com.example.vybes.common.composables.IconTextButton
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.SecondaryTextColor
import com.example.vybes.common.theme.SubtleBorderColor
import com.example.vybes.common.theme.TryoutBlue
import com.example.vybes.common.theme.TryoutDarkGreen
import com.example.vybes.common.theme.TryoutGreen
import com.example.vybes.common.theme.TryoutOrange
import com.example.vybes.common.theme.TryoutRed
import com.example.vybes.common.theme.TryoutYellow
import com.example.vybes.common.theme.VybesVeryDarkGray
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.common.util.DateUtils
import com.example.vybes.post.model.AlbumReview
import com.example.vybes.post.model.Post
import com.example.vybes.post.model.User
import com.example.vybes.post.model.Vybe
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import java.time.ZonedDateTime

@Composable
fun VybePost(
    vybe: Vybe,
    onClickCard: () -> Unit,
    onLikeClicked: () -> Unit = {},
    navController: NavController
) {
    val currentUserId = SharedPreferencesManager.getUserId()
    val isLikedByCurrentUser = vybe.likes.any { it.userId == currentUserId }

    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        TopBar(user = vybe.user, postedDate = vybe.postedDate, navController = navController)
        VybeCard(vybe, onClickCard)
        if (vybe.description.isNotBlank()) {
            Row(modifier = Modifier.padding(top = 5.dp)) {
                Text(
                    text = vybe.description,
                    style = artistsStyle,
                    color = Color.LightGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        StatsBar(
            post = vybe,
            onClickComment = onClickCard,
            onLikeClicked = onLikeClicked,
            modifier = Modifier.padding(top = 5.dp),
            isLiked = isLikedByCurrentUser
        )
    }
}

@Composable
fun TopBar(user: User, postedDate: ZonedDateTime, navController: NavController) {
    Row {
        DebouncedIconButton(
            onClick = { navController.navigate(user) },
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .align(Alignment.CenterVertically),
            contentDescription = "Go to user profile",
            iconResId = R.drawable.user
        )
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(start = 5.dp, top = 3.dp, bottom = 3.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = user.username,
                color = White,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = DateUtils.formatPostedDate(postedDate),
                color = Color.LightGray,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun StatsBar(
    post: Post,
    onClickComment: () -> Unit = {},
    onLikeClicked: () -> Unit = {},
    modifier: Modifier,
    iconSize: Dp = 20.dp,
    isLiked: Boolean = false
) {
    val type = if (post.type.equals("VYBE")) "track" else "album"
    val context = LocalContext.current
    val onClickSpotify = {
        val urlIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://open.spotify.com/${type}/${post.spotifyId}")
        )
        context.startActivity(urlIntent)
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        IconTextButton(
            description = "Like this vybe",
            onClick = onLikeClicked,
            text = post.likes?.size.toString(),
            drawableId = if (isLiked) R.drawable.thumb_up_filled else R.drawable.thumb_up,
            iconSize = iconSize,
            iconColor = White
        )
        IconTextButton(
            description = "Opening comments...",
            onClick = onClickComment,
            text = post.comments.orEmpty().size.toString(),
            drawableId = R.drawable.comment,
            iconSize = iconSize
        )
        IconTextButton(
            description = "Going to spotify...",
            onClick = onClickSpotify,
            text = stringResource(R.string.listen_on),
            drawableId = R.drawable.spotify,
            iconSize = iconSize,
            reversed = true
        )
    }
}

@Composable
fun VybeCard(vybe: Vybe, onClickCard: () -> Unit) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(vybe.imageUrl)
            .size(Size.ORIGINAL)
            .build(),
        contentScale = ContentScale.Crop,
    )

    // Extract colors from album art using Palette API
    var dominantColor by remember { mutableStateOf(Color.Black) }
    var vibrantColor by remember { mutableStateOf(Color.Gray) }

    LaunchedEffect(vybe.imageUrl) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(vybe.imageUrl)
            .allowHardware(false)
            .build()

        val drawable = loader.execute(request).drawable
        if (drawable is BitmapDrawable) {
            val palette = Palette.from(drawable.bitmap).generate()
            dominantColor = Color(palette.getDominantColor(Color.Black.toArgb()))
            vibrantColor = Color(palette.getVibrantColor(Color.Gray.toArgb()))
        }
    }

    Card(
        modifier = Modifier
            .clickable(onClick = onClickCard)
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, vibrantColor.copy(alpha = 0.2f)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            VybesVeryDarkGray.copy(alpha = 0.9f),
                            dominantColor.copy(alpha = 0.6f)
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(88.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(
                            Color.Black,
                            CircleShape
                        )
                        .border(2.dp, Color(0xFF333333), CircleShape)
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding((12 + index * 8).dp)
                                .border(
                                    0.5.dp,
                                    Color.White.copy(alpha = 0.1f),
                                    CircleShape
                                )
                        )
                    }
                }

                Image(
                    painter = painter,
                    contentDescription = "Album cover",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.Black, CircleShape)
                        .border(1.dp, Color(0xFF444444), CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
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

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = vybe.songName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = vybe.spotifyArtists.joinToString(", ") { it.name },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AlbumReviewPost(
    albumReview: AlbumReview,
    onClickCard: () -> Unit,
    onLikeClicked: () -> Unit = {},
    navController: NavController
) {
    val currentUserId = SharedPreferencesManager.getUserId()
    val isLikedByCurrentUser = albumReview.likes?.any { it.userId == currentUserId } == true

    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        TopBar(
            user = albumReview.user,
            postedDate = albumReview.postedDate,
            navController = navController
        )
        AlbumReviewCard(albumReview, onClickCard)
        StatsBar(
            post = albumReview,
            onClickComment = onClickCard,
            onLikeClicked = onLikeClicked,
            modifier = Modifier.padding(top = 5.dp),
            isLiked = isLikedByCurrentUser
        )
    }
}
@Composable
fun AlbumReviewCard(
    albumReview: AlbumReview,
    onClickCard: () -> Unit
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(albumReview.imageUrl)
            .size(Size.ORIGINAL)
            .build(),
        contentScale = ContentScale.FillWidth,
    )

    val favoriteTrackReviews = albumReview.trackReviews.filter { it.isFavorite }.take(3)

    Card(
        modifier = Modifier
            .clickable(onClick = onClickCard)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SubtleBorderColor),
        colors = CardDefaults.cardColors(containerColor = ElevatedBackgroundColor)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .blur(5.dp)
                    .paint(painter, contentScale = ContentScale.FillWidth)
                    .matchParentSize(),
            )
            Box(
                modifier = Modifier
                    .background(BackgroundColor.copy(alpha = 0.7f))
                    .matchParentSize(),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Header row with album info and score
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Album cover
                    Image(
                        painter = painter,
                        contentDescription = "Album cover",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Album info
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Album type indicator
                        Text(
                            text = "ALBUM REVIEW",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryTextColor.copy(alpha = 0.7f),
                            letterSpacing = 1.2.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = albumReview.albumName,
                            style = songTitleStyle,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = albumReview.artists.joinToString(", ") { it.name },
                            style = artistsStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Text(
                            text = albumReview.releaseDate.year.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryTextColor,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    albumReview.score?.let { score ->
                        ScoreBadge(score = score)
                    }
                }

                albumReview.description?.let { description ->
                    if (description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryTextColor.copy(alpha = 0.8f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 16.sp
                        )
                    }
                }

                if (favoriteTrackReviews.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Favorite tracks",
                            tint = TryoutYellow,
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Favorites: ${favoriteTrackReviews.joinToString(", ") { it.name }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryTextColor.copy(alpha = 0.9f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreBadge(score: Int) {
    val backgroundColor = when {
        score >= 8 -> TryoutDarkGreen
        score >= 6 -> TryoutBlue
        score >= 4 -> TryoutOrange
        else -> TryoutRed
    }

    Box(
        modifier = Modifier
            .background(
                backgroundColor,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$score/10",
            style = MaterialTheme.typography.labelMedium,
            color = PrimaryTextColor,
            fontWeight = FontWeight.Bold
        )
    }
}