package com.example.vybes.ui.feedback

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vybes.R
import com.example.vybes.ui.elements.MultilineTextField
import com.example.vybes.ui.theme.SpotifyDarkGrey
import com.example.vybes.ui.theme.White
import com.example.vybes.ui.theme.artistsStyle
import kotlinx.serialization.Serializable

@Serializable
object FeedbackScreen

@Composable
fun FeedbackScreen(
    feedbackViewModel: FeedbackViewModel = hiltViewModel(),
    onGoBack: () -> Unit
) {
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
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "We value your input! Please use the text field below to share any feedback, suggest new features, or report bugs. Your comments help us improve the app and make it better for you. Thank you for your support!",
                textAlign = TextAlign.Center,
                color = Color.LightGray,
                style = artistsStyle,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.size(20.dp))
            MultilineTextField(
                value = feedbackViewModel.text,
                onValueChanged = { feedbackViewModel.updateText(it) },
                hintText = "Type your feedback here...",
                textStyle = artistsStyle,
                maxLines = 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.Black, shape = RoundedCornerShape(25.dp))
                    .border(
                        1.dp, Color.White, RoundedCornerShape(25.dp)
                    )
            )
            Spacer(modifier = Modifier.size(20.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.CenterHorizontally)
                    .background(SpotifyDarkGrey)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clickable(onClick = {feedbackViewModel.submitFeedback()}),
            ) {
                Text(
                    text = "Submit", color = White,
                )
            }
        }
    }
}

//@Preview
//@Composable
//fun Preview() {
//    FeedbackScreen({})
//}