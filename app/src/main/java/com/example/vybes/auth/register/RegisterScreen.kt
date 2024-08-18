package com.example.vybes.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vybes.R
import com.example.vybes.auth.service.DummyAuthService
import com.example.vybes.common.composables.MultilineTextField
import com.example.vybes.common.theme.Blue
import com.example.vybes.common.theme.ErrorRed
import com.example.vybes.common.theme.SpotifyDarkGrey
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.logoStyle
import kotlinx.serialization.Serializable

@Serializable
object RegisterScreen

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {

    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            color = White,
            style = logoStyle,
            fontSize = 50.sp
        )
        val isRegisterInfoInvalid by viewModel.isRegisterInfoInvalid.collectAsState()
        Spacer(modifier = Modifier.size(30.dp))
        if (isRegisterInfoInvalid) {
            Text(
                text = stringResource(R.string.invalid_input),
                textAlign = TextAlign.Center,
                color = ErrorRed,
                style = artistsStyle
            )
            Spacer(modifier = Modifier.size(20.dp))
        }
        MultilineTextField(
            enabled = !isLoading,
            value = viewModel.usernameText,
            onValueChanged = { viewModel.updateUsernameText(it) },
            textStyle = artistsStyle,
            hintText = stringResource(R.string.username),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black, shape = RoundedCornerShape(20.dp))
                .border(
                    1.dp, White, RoundedCornerShape(20.dp)
                )
        )
        Spacer(modifier = Modifier.size(20.dp))
        MultilineTextField(
            enabled = !isLoading,
            value = viewModel.passwordText,
            onValueChanged = { viewModel.updatePasswordText(it) },
            textStyle = artistsStyle,
            hintText = stringResource(R.string.password),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black, shape = RoundedCornerShape(20.dp))
                .border(
                    1.dp, White, RoundedCornerShape(20.dp)
                )
        )
        Spacer(modifier = Modifier.size(20.dp))
        MultilineTextField(
            enabled = !isLoading,
            value = viewModel.repeatPasswordText,
            onValueChanged = { viewModel.updateRepeatPasswordText(it) },
            textStyle = artistsStyle,
            hintText = stringResource(R.string.repeat_password),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black, shape = RoundedCornerShape(20.dp))
                .border(
                    1.dp, White, RoundedCornerShape(20.dp)
                )
        )
        Spacer(modifier = Modifier.size(20.dp))
        Button(
            enabled = !isLoading,
            onClick = {
                viewModel.register(onRegisterSuccess)
            },
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
                Text(text = stringResource(R.string.register))
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = stringResource(R.string.already_have_an_account),
            color = Color.LightGray,
            style = artistsStyle
        )
        Text(
            text = stringResource(R.string.login),
            color = Blue,
            modifier = Modifier.clickable(
                onClick = {
                    if (!isLoading) {
                        onLoginClick()
                    }
                }
            ),
            style = artistsStyle,
        )
    }
}

@Preview
@Composable
fun Preview() {
    RegisterScreen(viewModel = RegisterViewModel(DummyAuthService()), {}, {})
}