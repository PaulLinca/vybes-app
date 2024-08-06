package com.example.vybes.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vybes.R
import com.example.vybes.ui.feedback.FeedbackScreen
import com.example.vybes.ui.theme.SpotifyDarkGrey
import com.example.vybes.ui.theme.White
import com.example.vybes.ui.theme.songTitleStyle
import kotlinx.serialization.Serializable

@Serializable
object ProfileScreen

@Composable
fun ProfileScreen(onGoBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            IconButton(
                onClick = onGoBack,
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Go back",
                    colorFilter = ColorFilter.tint(White),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = "Icon",
                colorFilter = ColorFilter.tint(White),
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.size(10.dp).fillMaxWidth())
            Text(
                text = "Current User", color = White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                style = songTitleStyle,
            )
            Spacer(modifier = Modifier.size(30.dp).fillMaxWidth())
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.CenterHorizontally)
                    .background(SpotifyDarkGrey)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clickable(onClick = { }),
            ) {
                Text(
                    text = "Clear cache", color = White,
                )
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    ProfileScreen({})
}