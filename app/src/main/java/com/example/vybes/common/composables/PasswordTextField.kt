package com.example.vybes.common.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vybes.R
import com.example.vybes.common.theme.White

@Composable
fun PasswordTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = TextStyle(),
    hintText: String = ""
) {
    var passwordVisibility by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = onValueChanged,
        enabled = enabled,
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        textStyle = textStyle.copy(color = Color.White),
        decorationBox = { innerTextField ->
            Box(
                modifier = modifier
                    .background(Color.Black, shape = RoundedCornerShape(20.dp))
                    .border(1.dp, White, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = hintText,
                        textAlign = TextAlign.Center,
                        style = textStyle.copy(color = Color.Gray),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        innerTextField()
                    }
                    Image(
                        painter = painterResource(if (passwordVisibility) R.drawable.eye_filled else R.drawable.eye_empty),
                        contentDescription = if (passwordVisibility) "Hide password" else "Show password",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { passwordVisibility = !passwordVisibility },
                        colorFilter = ColorFilter.tint(Color.White),
                    )
                }
            }
        },
        singleLine = true
    )
}