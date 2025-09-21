package com.linca.vybes.common.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.linca.vybes.R
import com.linca.vybes.common.theme.IconColor
import com.linca.vybes.common.theme.SubtleBorderColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DebouncedIconButton(
    onClick: () -> Unit,
    iconResId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    var isEnabled by remember { mutableStateOf(true) }

    IconButton(
        onClick = {
            if (isEnabled) {
                isEnabled = false

                onClick.invoke()

                CoroutineScope(Dispatchers.Main).launch {
                    delay(300)
                    isEnabled = true
                }
            }
        },
        enabled = isEnabled,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(IconColor),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DebouncedImageButton(
    onClick: () -> Unit,
    pictureUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    var isEnabled by remember { mutableStateOf(true) }

    IconButton(
        onClick = {
            if (isEnabled) {
                isEnabled = false

                onClick.invoke()

                CoroutineScope(Dispatchers.Main).launch {
                    delay(300)
                    isEnabled = true
                }
            }
        },
        enabled = isEnabled,
        modifier = modifier
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = pictureUrl,
                error = painterResource(id = R.drawable.user)
            ),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = SubtleBorderColor,
                    shape = CircleShape
                )
        )
    }
}