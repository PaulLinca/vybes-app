package com.example.vybes.ui.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.vybes.R
import com.example.vybes.ui.feed.model.Vybe
import com.example.vybes.ui.feed.model.vybes
import com.example.vybes.ui.theme.Black
import com.example.vybes.ui.theme.SpotifyDarkGrey
import com.example.vybes.ui.theme.White
import com.example.vybes.ui.theme.artistsStyle
import com.example.vybes.ui.theme.songTitleStyle
import java.util.stream.Collectors

@Composable
fun VybePostScreen(vybe: Vybe, onGoBack: () -> Unit) {
    var text by remember {
        mutableStateOf("")
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
        ) {
            TopBar(vybe = vybe, onGoBack = onGoBack)
            SongBanner(vybe = vybe)
            VybeStatsBar()
            Divider(color = Color.DarkGray)
            CommentSection()
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            MultilineTextField(
                value = text,
                onValueChanged = { text = it },
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
                onClick = { },
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
fun MultilineTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    hintText: String = "",
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    maxLines: Int = 4
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChanged,
        textStyle = textStyle,
        modifier = modifier,
        maxLines = maxLines,
        cursorBrush = SolidColor(Color.White),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.padding(15.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        style = textStyle,
                        textAlign = TextAlign.Center,
                        text = hintText,
                        color = Color.Gray
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun TopBar(vybe: Vybe, onGoBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        IconButton(
            onClick = onGoBack,
            modifier = Modifier
                .size(35.dp)
                .clip(CircleShape)
                .align(Alignment.CenterStart)
        ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Icon",
                colorFilter = ColorFilter.tint(White),
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = vybe.vybesUser,
                color = White,
                textAlign = TextAlign.Center,
                style = songTitleStyle,
            )
            Text(
                text = vybe.postedDate,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                style = artistsStyle,
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
                text = vybe.songName,
                color = White,
                textAlign = TextAlign.Center,
                style = songTitleStyle,
            )
            Text(
                text = vybe.spotifyArtistNames.stream().collect(Collectors.joining(", ")),
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                style = artistsStyle,
            )
        }
    }
}

@Composable
fun VybeStatsBar() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(horizontal = 1.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.thumb_up),
                    contentDescription = "Icon",
                    colorFilter = ColorFilter.tint(White),
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "1",
                    textAlign = TextAlign.Start,
                    color = White,
                    style = artistsStyle
                )
            }
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(5.dp),
//                modifier = Modifier.padding(horizontal = 1.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.ThumbUp,
//                    contentDescription = "Button",
//                    tint = Color.White,
//                    modifier = Modifier
//                        .rotate(180f)
//                        .size(25.dp)
//                )
//                Text(
//                    text = "0",
//                    textAlign = TextAlign.Start,
//                    color = White,
//                    style = artistsStyle
//                )
//            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(horizontal = 1.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.comment),
                    contentDescription = "Icon",
                    colorFilter = ColorFilter.tint(White),
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "3",
                    textAlign = TextAlign.Start,
                    color = White,
                    style = artistsStyle
                )
            }
            Image(
                painter = painterResource(id = R.drawable.spotify),
                contentDescription = "Spotify",
                colorFilter = ColorFilter.tint(White),
                modifier = Modifier
                    .size(23.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun CommentSection() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Comment("This is a comment. It could be super long or it could be super short but this one is testing long comments to see how they fit on the screen and how it looks")
        Comment("This is a short one")
        Comment("Another one for you ")
    }
}

@Composable
fun Comment(commentText: String) {
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
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "Icon",
                    colorFilter = ColorFilter.tint(White),
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "Sir Cumalot",
                    textAlign = TextAlign.Start,
                    color = White,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "12:45:42",
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
                    text = commentText,
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
            Image(
                painter = painterResource(id = R.drawable.heart),
                contentDescription = "Icon",
                colorFilter = ColorFilter.tint(White),
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "3",
                textAlign = TextAlign.Start,
                color = White,
                fontSize = 10.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}


@Preview
@Composable
fun Preview() {
    VybePostScreen(vybe = vybes.get(1), {})
}