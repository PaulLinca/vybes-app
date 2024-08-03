package com.example.vybes.ui.feed

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.vybes.R
import com.example.vybes.ui.feed.model.Vybe
import com.example.vybes.ui.feed.model.vybes
import com.example.vybes.ui.theme.SpotifyDarkGrey
import com.example.vybes.ui.theme.SpotifyLighterGrey
import com.example.vybes.ui.theme.White
import com.example.vybes.ui.theme.artistsStyle
import com.example.vybes.ui.theme.songTitleStyle
import java.util.stream.Collectors

@Composable
fun VybePost(vybe: Vybe, onClickCard: () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Row {
            val context = LocalContext.current
            IconButton(
                onClick = {
                    Toast.makeText(context, "Go to user profile", Toast.LENGTH_SHORT).show()
                },
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
                    text = vybe.vybesUser,
                    color = White,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = vybe.postedDate,
                    color = Color.LightGray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        VybeCard(vybe, onClickCard)
        StatsBar(vybe = vybe, onClickComment = onClickCard, modifier = Modifier.padding(top = 5.dp))
    }
}

@Composable
fun StatsBar(
    vybe: Vybe,
    onClickComment: () -> Unit = {},
    onClickSpotify: () -> Unit = {},
    onClickThumbsUp: () -> Unit = {},
    modifier: Modifier,
    iconSize: Dp = 20.dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(SpotifyDarkGrey)
                .padding(horizontal = 10.dp, vertical = 3.dp)
                .clickable(onClick = onClickThumbsUp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.thumb_up),
                contentDescription = "Like this vybe",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .size(iconSize)
            )
            Text(
                text = vybe.likes.toString(),
                color = White,
                style = artistsStyle,
            )
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(SpotifyDarkGrey)
                .padding(horizontal = 10.dp, vertical = 3.dp)
                .clickable(onClick = onClickComment),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.comment),
                contentDescription = "See comments",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .size(iconSize)
            )
            Text(
                text = "3",
                color = White,
                style = artistsStyle,
            )
        }
        val context = LocalContext.current
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(SpotifyDarkGrey)
                .padding(horizontal = 10.dp, vertical = 3.dp)
                .clickable(onClick = {
                    val urlIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://open.spotify.com/track/" + vybe.spotifyTrackId)
                    )
                    context.startActivity(urlIntent)
                }),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Listen on",
                color = White,
                style = artistsStyle,
            )
            Image(
                painter = painterResource(id = R.drawable.spotify),
                contentDescription = "Go to user profile",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .size(iconSize)
            )
        }
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
                    .padding(2.dp)
                    .aspectRatio(1f)
            )
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
                    text = vybe.spotifyArtistNames.stream().collect(Collectors.joining(", ")),
                    modifier = Modifier.padding(top = 3.dp, bottom = 7.dp),
                    style = artistsStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

    }
}

@Preview
@Composable
fun PreviewPost() {
    VybePost(vybe = vybes.get(4), {})
}