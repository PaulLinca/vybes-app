package com.example.vybes.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vybes.R
import com.example.vybes.auth.shared.AuthButton
import com.example.vybes.auth.shared.AuthErrorMessages
import com.example.vybes.auth.shared.LoadingOverlay
import com.example.vybes.auth.shared.RegistrationSection
import com.example.vybes.common.composables.MultilineTextField
import com.example.vybes.common.composables.PasswordTextField
import com.example.vybes.common.theme.AccentBorderColor
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.logoStyle
import kotlinx.coroutines.delay
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

    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }

    val emailKeyboardActions = KeyboardActions(
        onNext = { passwordFocusRequester.requestFocus() }
    )

    val passwordKeyboardActions = KeyboardActions(
        onDone = {
            focusManager.clearFocus()
            if (!isLoading) viewModel.login()
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically(),
                modifier = Modifier.padding(bottom = 30.dp)
            ) {
                Text(
                    text = "vybes",
                    color = PrimaryTextColor,
                    style = logoStyle,
                    fontSize = 50.sp
                )
            }

            AuthErrorMessages(
                networkError = networkError,
                emailError = emailError,
                passwordError = passwordError
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                EmailField(
                    viewModel = viewModel,
                    isLoading = isLoading,
                    keyboardActions = emailKeyboardActions
                )

                Spacer(modifier = Modifier.size(20.dp))

                PasswordField(
                    viewModel = viewModel,
                    isLoading = isLoading,
                    keyboardActions = passwordKeyboardActions,
                    focusRequester = passwordFocusRequester
                )

                Spacer(modifier = Modifier.size(24.dp))

                AuthButton(
                    isLoading = isLoading,
                    onClick = { viewModel.login() },
                    text = stringResource(R.string.login)
                )
            }

            // Set initial focus with a slight delay to ensure composition is complete
            LaunchedEffect(Unit) {
                delay(300)
            }

            RegistrationSection(
                isLoading = isLoading,
                onRegisterClick = onRegisterClick
            )
        }

        if (isLoading) {
            LoadingOverlay()
        }
    }
}

@Composable
fun EmailField(
    viewModel: LoginViewModel,
    isLoading: Boolean,
    keyboardActions: KeyboardActions
) {
    val focusRequester = remember { FocusRequester() }

    MultilineTextField(
        enabled = !isLoading,
        value = viewModel.emailText,
        onValueChanged = { viewModel.updateEmailText(it) },
        textStyle = artistsStyle,
        hintText = stringResource(R.string.email),
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = keyboardActions,
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .focusRequester(focusRequester)
    )


    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }
}

@Composable
fun PasswordField(
    viewModel: LoginViewModel,
    isLoading: Boolean,
    keyboardActions: KeyboardActions,
    focusRequester: FocusRequester
) {
    PasswordTextField(
        enabled = !isLoading,
        value = viewModel.passwordText,
        onValueChanged = { viewModel.updatePasswordText(it) },
        textStyle = artistsStyle,
        hintText = stringResource(R.string.password),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = keyboardActions,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .focusRequester(focusRequester)
    )
}