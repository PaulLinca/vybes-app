package com.example.vybes.common.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vybes.R

@Composable
fun TopBarWithSideButtons(
    modifier: Modifier = Modifier,
    leftButtonComposable: @Composable () -> Unit,
    rightButtonComposable: @Composable () -> Unit,
    centerComposable: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            leftButtonComposable()
        }

        Box(
            modifier = Modifier.align(Alignment.Center)
        ) {
            centerComposable()
        }

        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            rightButtonComposable()
        }
    }
}

@Composable
fun TopBarWithBackButton(
    onGoBack: () -> Unit,
    rightButtonComposable: @Composable () -> Unit = {},
    titleComposable: @Composable () -> Unit = {}
) {
    TopBarWithSideButtons(
        modifier = Modifier.padding(10.dp),
        leftButtonComposable = {
            DebouncedIconButton(
                onClick = onGoBack,
                modifier = Modifier.size(35.dp),
                contentDescription = "Go back",
                iconResId = R.drawable.back
            )
        },
        centerComposable = {
            titleComposable()
        },
        rightButtonComposable = {
            rightButtonComposable()
        }
    )
}