package com.example.vybes.auth.login

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vybes.R
import com.example.vybes.common.composables.MultilineTextField
import com.example.vybes.common.composables.PasswordTextField
import com.example.vybes.common.theme.AccentBorderColor
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.LinkBlue
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.ErrorRed
import com.example.vybes.common.theme.HintTextColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.logoStyle
import kotlinx.serialization.Serializable

@Serializable
object LoginScreen

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onUsernameSetupRequired: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoginSuccess by viewModel.isLoginSuccess.collectAsState()
    val requiresUsernameSetup by viewModel.requiresUsernameSetup.collectAsState()

    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val networkError by viewModel.loginError.collectAsState()

    LaunchedEffect(isLoginSuccess, requiresUsernameSetup) {
        if (isLoginSuccess) {
            if (requiresUsernameSetup) {
                onUsernameSetupRequired()
            } else {
                onLoginSuccess()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "vybes",
            color = PrimaryTextColor,
            style = logoStyle,
            fontSize = 50.sp
        )
        Spacer(modifier = Modifier.size(30.dp))

        networkError?.let { error ->
            Text(
                text = error,
                textAlign = TextAlign.Center,
                color = ErrorRed,
                style = artistsStyle
            )
            Spacer(modifier = Modifier.size(10.dp))
        }

        emailError?.let { error ->
            Text(
                text = error,
                textAlign = TextAlign.Center,
                color = ErrorRed,
                style = artistsStyle
            )
            Spacer(modifier = Modifier.size(10.dp))
        }

        passwordError?.let { error ->
            Text(
                text = error,
                textAlign = TextAlign.Center,
                color = ErrorRed,
                style = artistsStyle
            )
            Spacer(modifier = Modifier.size(10.dp))
        }

        EmailField(viewModel, isLoading)
        Spacer(modifier = Modifier.size(20.dp))
        PasswordField(viewModel, isLoading)
        Spacer(modifier = Modifier.size(20.dp))

        LoginButton(viewModel, isLoading)

        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = stringResource(R.string.need_an_account),
            color = HintTextColor,
            style = artistsStyle
        )
        Text(
            text = stringResource(R.string.register),
            color = LinkBlue,
            modifier = Modifier.clickable(
                onClick = {
                    if (!isLoading) {
                        onRegisterClick()
                    }
                }
            ),
            style = artistsStyle
        )
    }
}

@Composable
fun EmailField(viewModel: LoginViewModel, isLoading: Boolean) {
    MultilineTextField(
        enabled = !isLoading,
        value = viewModel.emailText,
        onValueChanged = { viewModel.updateEmailText(it) },
        textStyle = artistsStyle,
        hintText = stringResource(R.string.email),
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BackgroundColor, shape = RoundedCornerShape(20.dp))
            .border(1.dp, AccentBorderColor, RoundedCornerShape(20.dp))
    )
}

@Composable
fun PasswordField(viewModel: LoginViewModel, isLoading: Boolean) {
    PasswordTextField(
        enabled = !isLoading,
        value = viewModel.passwordText,
        onValueChanged = { viewModel.updatePasswordText(it) },
        textStyle = artistsStyle,
        hintText = stringResource(R.string.password),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BackgroundColor, shape = RoundedCornerShape(20.dp))
            .border(1.dp, AccentBorderColor, RoundedCornerShape(20.dp))
    )
}

@Composable
fun LoginButton(viewModel: LoginViewModel, isLoading: Boolean) {
    Button(
        enabled = !isLoading,
        onClick = { viewModel.login() },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ElevatedBackgroundColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = ElevatedBackgroundColor,
            contentColor = PrimaryTextColor,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
        } else {
            Text(text = stringResource(R.string.login), color = PrimaryTextColor)
        }
    }
}