package com.example.vybes.add

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.vybes.R
import com.example.vybes.auth.model.Album
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.SubtleBorderColor
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.post.VybeViewModel
import com.example.vybes.post.model.Vybe
import kotlinx.serialization.Serializable

@Serializable
data class AddAlbumReviewScreen(val spotifyId: String) {
    companion object {
        fun from(savedStateHandle: SavedStateHandle) =
            savedStateHandle.toRoute<AddAlbumReviewScreen>()
    }
}

@Composable
fun AddAlbumReviewScreen(
    onGoBack: () -> Unit,
    onSubmitSuccess: () -> Unit,
    viewModel: AddAlbumViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val descriptionText = viewModel.descriptionText


    when (val state = uiState) {
        is AddAlbumViewModel.ReviewUiState.Success ->  {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
            ) {
                TopBarWithBackButton(onGoBack = onGoBack) {
                    Text(
                        text = stringResource(R.string.review_album),
                        color = PrimaryTextColor,
                        textAlign = TextAlign.Center,
                        style = songTitleStyle,
                    )
                }
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(state.album)
                        .size(Size.ORIGINAL)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.FillWidth,
                )

            }
        }
        else -> {

        }
    }


}

@Composable
fun AlbumCard(album: Album, onClickCard: () -> Unit) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(album.imageUrl)
            .size(Size.ORIGINAL)
            .build(),
        contentScale = ContentScale.FillWidth,
    )

    Box(
        modifier = Modifier
            .clickable(onClick = onClickCard)
            .fillMaxWidth()
            .border(1.dp, SubtleBorderColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
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
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painter,
                contentDescription = "Button",
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(0.dp)
                    .height(90.dp)
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
                    text = album.name,
                    style = songTitleStyle,
                    maxLines = 3
                )
                Text(
                    text = album.artist.joinToString(", ") { it.name },
                    modifier = Modifier.padding(top = 3.dp, bottom = 7.dp),
                    style = artistsStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}