package com.linca.vybes.feedback

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.linca.vybes.R
import com.linca.vybes.common.composables.MultilineTextField
import com.linca.vybes.common.composables.TopBarWithBackButton
import com.linca.vybes.common.theme.AccentBorderColor
import com.linca.vybes.common.theme.BackgroundColor
import com.linca.vybes.common.theme.ElevatedBackgroundColor
import com.linca.vybes.common.theme.PrimaryTextColor
import com.linca.vybes.common.theme.SecondaryTextColor
import com.linca.vybes.common.theme.SuccessGreen
import com.linca.vybes.common.theme.TryoutRed
import com.linca.vybes.common.theme.White
import com.linca.vybes.common.theme.artistsStyle
import com.linca.vybes.common.theme.disabledStyle
import com.linca.vybes.common.theme.songTitleStyle
import kotlinx.serialization.Serializable

@Serializable
object FeedbackScreen

@Composable
fun FeedbackScreen(
    feedbackViewModel: FeedbackViewModel = hiltViewModel(),
    onGoBack: () -> Unit
) {
    val uiState by feedbackViewModel.uiState.collectAsState()

    BackHandler(enabled = uiState.isSubmitted) {
        feedbackViewModel.resetForm()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        TopBarWithBackButton(
            onGoBack = if (uiState.isSubmitted) {
                { feedbackViewModel.resetForm(); onGoBack() }
            } else {
                onGoBack
            }
        ) {
            Text(
                text = stringResource(R.string.feedback),
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
            Text(
                text = stringResource(R.string.feedback_info_text),
                textAlign = TextAlign.Center,
                color = SecondaryTextColor,
                style = artistsStyle,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            AnimatedVisibility(
                visible = uiState.alertText.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(
                        text = uiState.alertText,
                        textAlign = TextAlign.Center,
                        color = when {
                            uiState.isSubmitted -> SuccessGreen
                            uiState.isTextInvalid -> TryoutRed
                            else -> SecondaryTextColor
                        },
                        style = artistsStyle,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Spacer(modifier = Modifier.size(20.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                val fieldTextStyle = if (uiState.isSubmitted) disabledStyle else artistsStyle
                val borderColor = if (uiState.isTextInvalid) TryoutRed else AccentBorderColor

                MultilineTextField(
                    enabled = !uiState.isSubmitted && !uiState.isLoading,
                    value = uiState.text,
                    onValueChanged = { feedbackViewModel.updateText(it) },
                    hintText = stringResource(R.string.feedback_field_hint),
                    textStyle = fieldTextStyle,
                    maxLines = 10,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(BackgroundColor, shape = RoundedCornerShape(25.dp))
                        .border(
                            1.dp,
                            borderColor,
                            RoundedCornerShape(25.dp)
                        )
                        .animateContentSize()
                )

                Text(
                    text = "${uiState.charactersRemaining}",
                    color = if (uiState.charactersRemaining < 50) TryoutRed else SecondaryTextColor,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.size(20.dp))

            Button(
                enabled = !uiState.isSubmitted && !uiState.isLoading,
                onClick = { feedbackViewModel.submitFeedback() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElevatedBackgroundColor),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = PrimaryTextColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text =
                            if (uiState.isSubmitted) "Submitted" else "Submit",
                        color = if (uiState.isSubmitted) SecondaryTextColor else PrimaryTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.size(20.dp))

            AnimatedVisibility(
                visible = uiState.isSubmitted,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                OutlinedButton(
                    onClick = {
                        feedbackViewModel.resetForm()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryTextColor
                    ),
                    border = BorderStroke(1.dp, AccentBorderColor)
                ) {
                    Text(text = "Submit another")
                }
            }
        }
    }
}