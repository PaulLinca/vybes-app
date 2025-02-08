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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.vybes.R
import com.example.vybes.common.composables.MultilineTextField
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.Black
import com.example.vybes.common.theme.ErrorRed
import com.example.vybes.common.theme.SpotifyDarkGrey
import com.example.vybes.common.theme.White
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
    val vybe by vybeViewModel.vybe.collectAsState()
    val isLikedByUser by vybeViewModel.isLikedByCurrentUser.collectAsState()
    if (vybe != null) {
        VybePostScreen(onGoBack, vybe!!, isLikedByUser, vybeViewModel, navController)
    } else {
        CircularProgressIndicator(modifier = Modifier.size(20.dp))
    }
}

@Composable
fun VybePostScreen(
    onGoBack: () -> Unit,
    vybe: Vybe,
    isLikedByUser: Boolean,
    vybeViewModel: VybeViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        TopBarWithBackButton(onGoBack = onGoBack) {
            Text(
                text = vybe.user.username,
                color = White,
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
                    if (isLikedByUser) {
                        vybeViewModel.unlikeVybe()
                    } else {
                        vybeViewModel.likeVybe()
                    }
                },
                iconSize = 23.dp,
                isLiked = isLikedByUser
            )
        }
        CommentSection(vybe, vybeViewModel, Modifier.weight(1f), navController)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
        ) {
            MultilineTextField(
                value = vybeViewModel.commentText,
                onValueChanged = { vybeViewModel.updateText(it) },
                hintText = "Add a comment...",
                textStyle = artistsStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(25.dp))
                    .weight(1f)
                    .background(Color.Black, shape = RoundedCornerShape(25.dp))
                    .border(
                        1.dp, Color.White, RoundedCornerShape(25.dp)
                    )
            )
            IconButton(
                onClick = {
                    vybeViewModel.addComment()
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.send),
                    contentDescription = "Send Button",
                    colorFilter = ColorFilter.tint(White),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun SongBanner(vybe: Vybe?) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(vybe?.imageUrl.orEmpty())
            .size(Size.ORIGINAL)
            .build(),
        contentScale = ContentScale.FillWidth,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(color = SpotifyDarkGrey)
    ) {
        Box(
            modifier = Modifier
                .blur(5.dp)
                .paint(painter, contentScale = ContentScale.FillWidth)
                .fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.6f))
                .fillMaxSize(),
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(
                painter = painter,
                contentDescription = "Button",
                modifier = Modifier
                    .size(130.dp)
                    .padding(2.dp)
                    .aspectRatio(1f)
            )
            Text(
                text = vybe?.songName.orEmpty(),
                color = White,
                textAlign = TextAlign.Center,
                style = songTitleStyle,
            )
            Text(
                text = vybe?.spotifyArtists.orEmpty().stream()
                    .map { a -> a.name }
                    .collect(Collectors.joining(", ")),
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                style = artistsStyle,
            )
        }
    }
}

@Composable
fun CommentSection(
    vybe: Vybe,
    vybeViewModel: VybeViewModel,
    modifier: Modifier,
    navController: NavController
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        vybe.comments.forEach { c ->
            Comment(c, vybeViewModel, navController)
        }
    }
}

@Composable
fun Comment(comment: Comment, vybeViewModel: VybeViewModel, navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                IconButton(
                    onClick = { navController.navigate(comment.user) },
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterVertically)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "Go to user profile",
                        colorFilter = ColorFilter.tint(White),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Text(
                    text = comment.user.username,
                    textAlign = TextAlign.Start,
                    color = White,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { navController.navigate(comment.user) }
                )
                Text(
                    text = DateUtils.formatPostedDate(comment.timestamp),
                    textAlign = TextAlign.Start,
                    color = Color.LightGray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(top = 1.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Spacer(modifier = Modifier.size(25.dp))
                Text(
                    text = comment.text,
                    textAlign = TextAlign.Start,
                    color = White,
                    style = artistsStyle,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        }
        Column(
            Modifier.padding(horizontal = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isLikedByCurrentUser =
                comment.likes.any { it.userId == SharedPreferencesManager.getUserId() }
            IconButton(
                onClick = {
                    if (isLikedByCurrentUser) {
                        vybeViewModel.unlikeComment(comment.id)
                    } else {
                        vybeViewModel.likeComment(comment.id)
                    }
                },
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Image(
                    painter = painterResource(id = if (isLikedByCurrentUser) R.drawable.heart_filled else R.drawable.heart),
                    contentDescription = "Go to user profile",
                    colorFilter = ColorFilter.tint(if (isLikedByCurrentUser) ErrorRed else White),
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                text = comment.likes.count().toString(),
                textAlign = TextAlign.Start,
                color = White,
                fontSize = 10.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}