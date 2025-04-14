package com.example.vybes.common.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.example.vybes.common.theme.IconColor
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