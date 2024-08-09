package com.example.vybes.ui.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vybes.ui.elements.TopBarWithBackButton
import com.example.vybes.ui.elements.MultilineTextField
import com.example.vybes.ui.theme.ErrorRed
import com.example.vybes.ui.theme.SpotifyDarkGrey
import com.example.vybes.ui.theme.SuccessGreen
import com.example.vybes.ui.theme.White
import com.example.vybes.ui.theme.artistsStyle
import com.example.vybes.ui.theme.disabledStyle
import com.example.vybes.ui.theme.songTitleStyle
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
        TopBarWithBackButton(onGoBack = onGoBack) {
            Text(
                text = "Feedback",
                color = White,
                textAlign = TextAlign.Center,
                style = songTitleStyle,
            )
        }
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            val isSubmitted by feedbackViewModel.isSubmitted.collectAsState()
            val textColor = if (isSubmitted) Color.DarkGray else White
            val fieldTextStyle = if (isSubmitted) disabledStyle else artistsStyle

            val isTextInvalid by feedbackViewModel.isTextInvalid.collectAsState()
            val borderColor = if (isTextInvalid) ErrorRed else White

            val alertText by feedbackViewModel.alertText.collectAsState()
            val alertTextColor =
                if (isSubmitted) SuccessGreen else if (isTextInvalid) ErrorRed else White

            val infoText =
                "We value your input! Please use the text field below to share any feedback, suggest new features, or report bugs. Your comments help us improve the app and make it better for you. Thank you for your support!"

            Text(
                text = infoText,
                textAlign = TextAlign.Center,
                color = Color.LightGray,
                style = artistsStyle,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            if (isTextInvalid || isSubmitted) {
                Spacer(modifier = Modifier.size(20.dp))
                Text(
                    text = alertText,
                    textAlign = TextAlign.Center,
                    color = alertTextColor,
                    style = artistsStyle,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.size(20.dp))
            MultilineTextField(
                enabled = !isSubmitted,
                value = feedbackViewModel.text,
                onValueChanged = {
                    feedbackViewModel.updateText(it)
                    feedbackViewModel.resetTextValidity()
                },
                hintText = "Type your feedback here...",
                textStyle = fieldTextStyle,
                maxLines = 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.Black, shape = RoundedCornerShape(25.dp))
                    .border(
                        1.dp, borderColor, RoundedCornerShape(25.dp)
                    )
            )
            Spacer(modifier = Modifier.size(20.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.CenterHorizontally)
                    .background(SpotifyDarkGrey)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clickable(
                        enabled = !isSubmitted,
                        onClick = {
                            feedbackViewModel.submitFeedback()
                        }),
            ) {
                Text(
                    text = "Submit", color = textColor,
                )
            }
        }
    }
}