package com.example.vybes.common.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vybes.R

@Composable
fun TopBarWithBackButton(
    onGoBack: () -> Unit,
    titleComposable: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        DebouncedIconButton(
            onClick = onGoBack,
            modifier = Modifier
                .size(35.dp)
                .align(Alignment.CenterStart),
            contentDescription = "Go back",
            iconResId = R.drawable.back
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            titleComposable()
        }
    }
}