package com.example.vybes.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vybes.common.theme.AccentBorderColor
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.HintTextColor
import com.example.vybes.common.theme.PrimaryTextColor

@Composable
fun MultilineTextField(
    modifier: Modifier = Modifier,
    weightModifier: Modifier = Modifier,
    enabled: Boolean = true,
    value: String,
    onValueChanged: (String) -> Unit,
    hintText: String = "",
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    maxLines: Int = 4,
    contentAlignment: Alignment = Alignment.TopStart,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    BasicTextField(
        enabled = enabled,
        value = value,
        onValueChange = onValueChanged,
        textStyle = textStyle,
        modifier = weightModifier,
        maxLines = maxLines,
        cursorBrush = SolidColor(PrimaryTextColor),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        decorationBox = { innerTextField ->
            Box(
                modifier = modifier
                    .background(BackgroundColor, shape = RoundedCornerShape(20.dp))
                    .border(1.dp, AccentBorderColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = contentAlignment
            ) {
                if (value.isEmpty()) {
                    Text(
                        style = textStyle,
                        textAlign = TextAlign.Center,
                        text = hintText,
                        color = HintTextColor
                    )
                }
                innerTextField()
            }
        }
    )
}