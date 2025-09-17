package com.example.vybes.common.composables

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vybes.R
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.artistsStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun IconTextButton(
    onClick: () -> Unit,
    text: String,
    iconSize: Dp,
    description: String,
    drawableId: Int,
    reversed: Boolean = false,
    iconColor: Color = White
) {
    var isEnabled by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ElevatedBackgroundColor)
            .padding(horizontal = 10.dp, vertical = 3.dp)
            .clickable(onClick = {
                if (isEnabled) {
                    isEnabled = false

                    onClick.invoke()

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(300)
                        isEnabled = true
                    }
                }
            }),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (reversed) {
            Text(
                text = text,
                color = PrimaryTextColor,
                style = artistsStyle,
            )
        }
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = description,
            colorFilter = ColorFilter.tint(iconColor),
            modifier = Modifier
                .size(iconSize)
        )
        if (!reversed) {
            Text(
                text = text,
                color = PrimaryTextColor,
                style = artistsStyle,
            )
        }
    }
}

@Composable
fun LoadingLikeButton(
    onClick: () -> Unit,
    likeCount: Int,
    isLiked: Boolean,
    isLoading: Boolean = false,
    iconSize: Dp = 20.dp,
    iconColor: Color = White
) {
    var isEnabled by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ElevatedBackgroundColor)
            .padding(horizontal = 10.dp, vertical = 3.dp)
            .clickable(
                enabled = isEnabled && !isLoading,
                onClick = {
                    if (isEnabled && !isLoading) {
                        isEnabled = false
                        onClick.invoke()

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(300)
                            isEnabled = true
                        }
                    }
                }
            ),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isLoading) {
            PulsingLikeIcon(
                iconRes = if (isLiked) R.drawable.thumb_up_filled else R.drawable.thumb_up,
                iconSize = iconSize,
                iconColor = iconColor,
                contentDescription = "Like this vybe"
            )
        } else {
            Image(
                painter = painterResource(
                    id = if (isLiked) R.drawable.thumb_up_filled else R.drawable.thumb_up
                ),
                contentDescription = "Like this vybe",
                colorFilter = ColorFilter.tint(iconColor),
                modifier = Modifier.size(iconSize)
            )
        }

        Text(
            text = likeCount.toString(),
            color = PrimaryTextColor,
            style = artistsStyle,
        )
    }
}

@Composable
fun PulsingLikeIcon(
    iconRes: Int,
    iconSize: Dp,
    iconColor: Color,
    contentDescription: String
) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        )
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        )
    )

    Image(
        painter = painterResource(id = iconRes),
        contentDescription = contentDescription,
        colorFilter = ColorFilter.tint(iconColor),
        modifier = Modifier
            .size(iconSize)
            .scale(scale)
            .alpha(alpha)
    )
}