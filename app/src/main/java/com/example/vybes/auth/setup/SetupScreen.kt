package com.example.vybes.auth.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vybes.R
import com.example.vybes.common.composables.MultilineTextField
import com.example.vybes.common.theme.SpotifyDarkGrey
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.logoStyle
import com.example.vybes.common.theme.songTitleStyle
import kotlinx.serialization.Serializable


@Serializable
object SetupScreen

@Composable
fun SetupScreen(
    viewModel: SetupViewModel = hiltViewModel(),
    onSetupSuccess: () -> Unit,
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isSetupSuccess by viewModel.isSetupSuccess.collectAsState()

    LaunchedEffect(isSetupSuccess) {
        if (isSetupSuccess) {
            onSetupSuccess.invoke()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "vybes",
            color = White,
            style = logoStyle,
            fontSize = 50.sp
        )
        Spacer(modifier = Modifier.size(30.dp))
        Text(
            text = stringResource(R.string.choose_a_username),
            color = Color.White,
            style = songTitleStyle
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = stringResource(R.string.username_setup_subtitle),
            color = Color.LightGray,
            style = artistsStyle
        )
        Spacer(modifier = Modifier.size(30.dp))
        MultilineTextField(
            enabled = !isLoading,
            value = viewModel.usernameText,
            onValueChanged = { viewModel.updateUsernameText(it) },
            textStyle = artistsStyle,
            hintText = stringResource(R.string.username_setup_hint),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black, shape = RoundedCornerShape(20.dp))
                .border(1.dp, White, RoundedCornerShape(20.dp))
        )
        Spacer(modifier = Modifier.size(30.dp))
        Button(
            enabled = !isLoading,
            onClick = { viewModel.login() },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SpotifyDarkGrey),
            colors = ButtonDefaults.buttonColors(
                containerColor = SpotifyDarkGrey,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.LightGray
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text(text = stringResource(R.string.continue_text))
            }
        }
    }
}