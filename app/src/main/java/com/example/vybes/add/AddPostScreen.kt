package com.example.vybes.add

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.vybes.R
import com.example.vybes.add.model.network.TrackSearchResult
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.SpotifyDarkGrey
import com.example.vybes.common.theme.SpotifyLighterGrey
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.songTitleStyle

@Composable
fun AddPostScreen(
    searchResult: TrackSearchResult,
    onGoBack: () -> Unit,
    onSubmitSuccess: () -> Unit,
    viewModel: AddPostViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { success ->
            if (success) {
                Log.e("SDS", "NAVIGATIING")
                onSubmitSuccess()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TopBarWithBackButton(onGoBack = onGoBack) {
            Text(
                text = stringResource(R.string.share_song),
                color = White,
                textAlign = TextAlign.Center,
                style = songTitleStyle,
            )
        }
        VybeCard(searchResult = searchResult) {}
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .align(Alignment.CenterHorizontally)
                .background(SpotifyDarkGrey)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clickable(
                    onClick = {
                        viewModel.submit(searchResult.id)
                    }),
        ) {
            Text(
                text = stringResource(R.string.submit), color = White,
            )
        }
    }
}

@Composable
fun VybeCard(searchResult: TrackSearchResult, onClickCard: () -> Unit) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(searchResult.imageUrl)
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
                    text = searchResult.name,
                    style = songTitleStyle,
                    maxLines = 3
                )
                Text(
                    text = searchResult.artist,
                    modifier = Modifier.padding(top = 3.dp, bottom = 7.dp),
                    style = artistsStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

    }
}

