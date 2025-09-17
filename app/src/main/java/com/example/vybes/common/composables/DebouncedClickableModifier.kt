package com.example.vybes.common.composables

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun Modifier.debouncedClickable(
    debounceTime: Long = 300L,
    onClick: () -> Unit
): Modifier {
    var lastClickTime by remember { mutableStateOf(0L) }

    return this.clickable {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > debounceTime) {
            lastClickTime = currentTime
            onClick()
        }
    }
}