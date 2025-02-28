package com.example.vybes.common.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vybes.common.theme.SpotifyDarkGrey
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
            .background(SpotifyDarkGrey)
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
                color = White,
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
                color = White,
                style = artistsStyle,
            )
        }
    }
}