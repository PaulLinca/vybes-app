package com.linca.vybes.auth.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linca.vybes.common.theme.ElevatedBackgroundColor
import com.linca.vybes.common.theme.PrimaryTextColor
import com.linca.vybes.common.theme.TryoutRed
import com.linca.vybes.common.theme.VybesLightGray
import com.linca.vybes.common.theme.artistsStyle

@Composable
fun AuthErrorMessages(
    networkError: String?,
    emailError: String?,
    passwordError: String?,
    repeatPasswordError: String? = null
) {
    val hasErrors =
        listOfNotNull(networkError, emailError, passwordError, repeatPasswordError).isNotEmpty()

    AnimatedVisibility(
        visible = hasErrors,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = TryoutRed),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                networkError?.let { ErrorText(it) }
                emailError?.let { ErrorText(it) }
                passwordError?.let { ErrorText(it) }
                repeatPasswordError?.let { ErrorText(it) }
            }
        }
    }
}

@Composable
fun SingleErrorMessage(
    errorMessage: String?
) {
    val hasError =
        listOfNotNull(errorMessage).isNotEmpty()

    AnimatedVisibility(
        visible = hasError,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = TryoutRed),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                errorMessage?.let { ErrorText(it) }
            }
        }
    }
}

@Composable
fun ErrorText(text: String) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        color = PrimaryTextColor,
        style = artistsStyle,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
@Composable
fun AuthButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    text: String,
    icon: Painter? = null,
    iconContentDescription: String? = null
) {
    Button(
        enabled = !isLoading,
        onClick = { if (!isLoading) onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ElevatedBackgroundColor,
            contentColor = PrimaryTextColor,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = PrimaryTextColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        painter = icon,
                        contentDescription = iconContentDescription,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(24.dp)
                    )
                    Spacer(Modifier.size(12.dp))
                }
                Text(
                    text = text,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                    color = PrimaryTextColor
                )
            }
        }
    }
}
@Composable
fun LoadingOverlay() {
    if (LocalInspectionMode.current) return

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        CircularProgressIndicator(
            color = VybesLightGray,
            modifier = Modifier.size(48.dp)
        )
    }
}