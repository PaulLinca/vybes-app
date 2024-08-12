package com.example.vybes.post

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.vybes.R
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.composables.MultilineTextField
import com.example.vybes.post.model.Vybe
import com.example.vybes.common.theme.Black
import com.example.vybes.common.theme.SpotifyDarkGrey
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.post.feed.StatsBar
import com.example.vybes.post.model.Comment
import java.util.stream.Collectors

@Composable
fun VybePostScreen(vybeId: Int, onGoBack: () -> Unit) {

//    var text by remember {
//        mutableStateOf("")
//    }
//    val context = LocalContext.current
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Black)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Black)
//        ) {
//            TopBarWithBackButton(onGoBack = onGoBack) {
//                Text(
//                    text = vybe.vybesUser,
//                    color = White,
//                    textAlign = TextAlign.Center,
//                    style = songTitleStyle,
//                )
//                Text(
//                    text = vybe.postedDate,
//                    color = Color.LightGray,
//                    textAlign = TextAlign.Center,
//                    style = artistsStyle,
//                )
//            }
//            SongBanner(vybe = vybe)
//            StatsBar(
//                vybe = vybe,
//                modifier = Modifier.padding(top = 8.dp, start = 8.dp),
//                onClickThumbsUp = {
//                    Toast.makeText(context, "Liking vybe...", Toast.LENGTH_SHORT).show()
//                },
//                iconSize = 23.dp
//            )
//            CommentSection(vybe)
//        }
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(8.dp)
//        ) {
//            MultilineTextField(
//                value = text,
//                onValueChanged = { text = it },
//                hintText = "Add a comment...",
//                textStyle = artistsStyle,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(25.dp))
//                    .weight(1f)
//                    .background(Color.Black, shape = RoundedCornerShape(25.dp))
//                    .border(
//                        1.dp, Color.White, RoundedCornerShape(25.dp)
//                    )
//            )
//            IconButton(
//                onClick = {
//                    Toast.makeText(context, "Adding comment", Toast.LENGTH_SHORT).show()
//                },
//                modifier = Modifier
//                    .size(40.dp)
//                    .clip(CircleShape)
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.send),
//                    contentDescription = "Send Button",
//                    colorFilter = ColorFilter.tint(White),
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//        }
//    }
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
fun CommentSection(vybe: Vybe) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        vybe.comments.forEach { c ->
            Comment(c)
        }
    }
}

@Composable
fun Comment(comment: Comment) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        val context = LocalContext.current

        Column(Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                IconButton(
                    onClick = {
                        Toast.makeText(context, "Go to user profile", Toast.LENGTH_SHORT).show()
                    },
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
                    text = comment.user.name,
                    textAlign = TextAlign.Start,
                    color = White,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            Toast
                                .makeText(context, "Go to user profile", Toast.LENGTH_SHORT)
                                .show()
                        }
                )
                Text(
                    text = comment.postedDate,
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
            IconButton(
                onClick = {
                    Toast.makeText(context, "Liking comment", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.heart),
                    contentDescription = "Go to user profile",
                    colorFilter = ColorFilter.tint(White),
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

//@Preview
//@Composable
//fun Preview() {
//    VybePostScreen(vybe = vybes.get(1), {})
//}