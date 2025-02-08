package com.example.vybes.post.feed

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.vybes.R
import com.example.vybes.common.composables.IconTextButton
import com.example.vybes.common.theme.SpotifyDarkGrey
import com.example.vybes.common.theme.SpotifyLighterGrey
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.common.util.DateUtils
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
            vybe = vybe,
            onClickComment = onClickCard,
            onLikeClicked = onLikeClicked,
            modifier = Modifier.padding(top = 5.dp),
            isLiked = isLikedByCurrentUser
        )
    }
}

@Composable
fun TopBar(user: User, postedDate: ZonedDateTime, navController: NavController) {
    val context = LocalContext.current
    Row {
        IconButton(
            onClick = { navController.navigate(user) },
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .align(Alignment.CenterVertically)
        ) {
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = "Go to user profile",
                colorFilter = ColorFilter.tint(Color.Gray),
                modifier = Modifier.fillMaxSize()
            )
        }
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
    vybe: Vybe,
    onClickComment: () -> Unit = {},
    onLikeClicked: () -> Unit = {},
    modifier: Modifier,
    iconSize: Dp = 20.dp,
    isLiked: Boolean = false
) {
    val context = LocalContext.current
    val onClickSpotify = {
        val urlIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://open.spotify.com/track/${vybe.spotifyTrackId}")
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
            text = vybe.likes.size.toString(),
            drawableId = if (isLiked) R.drawable.thumb_up_filled else R.drawable.thumb_up,
            iconSize = iconSize,
            iconColor = White
        )
        IconTextButton(
            description = "Opening comments...",
            onClick = onClickComment,
            text = vybe.comments.size.toString(),
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
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(vybe.imageUrl)
            .size(Size.ORIGINAL)
            .build(),
        contentScale = ContentScale.FillWidth,
    )

    Box(
        modifier = Modifier
            .clickable(onClick = onClickCard)
            .fillMaxWidth()
            .height(90.dp)
            .border(1.dp, SpotifyLighterGrey, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
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
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painter,
                contentDescription = "Button",
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(0.dp)
                    .aspectRatio(1f)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = vybe.songName,
                    style = songTitleStyle,
                    maxLines = 3
                )
                Text(
                    text = vybe.spotifyArtists.joinToString(", ") { it.name },
                    modifier = Modifier.padding(top = 3.dp, bottom = 7.dp),
                    style = artistsStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

    }
}