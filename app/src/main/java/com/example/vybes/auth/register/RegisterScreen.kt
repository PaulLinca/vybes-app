package com.example.vybes.auth.register

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
import com.example.vybes.auth.shared.ErrorMessages
import com.example.vybes.auth.shared.LoadingOverlay
import com.example.vybes.auth.shared.RegistrationSection
import com.example.vybes.common.composables.MultilineTextField
import com.example.vybes.common.composables.PasswordTextField
import com.example.vybes.common.theme.AccentBorderColor
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.PrimaryTextColor
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
    val isRegisterSuccess by viewModel.isRegisterSuccess.collectAsState()

    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val repeatPasswordError by viewModel.repeatPasswordError.collectAsState()
    val networkError by viewModel.networkError.collectAsState()

    LaunchedEffect(isRegisterSuccess) {
        if (isRegisterSuccess) {
            onRegisterSuccess()
        }
    }

    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }
    val repeatPasswordFocusRequester = remember { FocusRequester() }

    val emailKeyboardActions = KeyboardActions(
        onNext = { passwordFocusRequester.requestFocus() }
    )
    val passwordKeyboardActions = KeyboardActions(
        onNext = { repeatPasswordFocusRequester.requestFocus() }
    )
    val repeatPasswordKeyboardActions = KeyboardActions(
        onDone = {
            focusManager.clearFocus()
            if (!isLoading) viewModel.register()
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
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically(),
                modifier = Modifier.padding(bottom = 30.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    color = PrimaryTextColor,
                    style = logoStyle,
                    fontSize = 50.sp
                )
            }

            ErrorMessages(
                networkError = networkError,
                emailError = emailError,
                passwordError = passwordError,
                repeatPasswordError = repeatPasswordError
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                val emailFocusRequester = remember { FocusRequester() }

                MultilineTextField(
                    enabled = !isLoading,
                    value = viewModel.emailText,
                    onValueChanged = { viewModel.updateEmailText(it) },
                    textStyle = artistsStyle,
                    hintText = stringResource(R.string.email),
                    maxLines = 1,
                    contentAlignment = Alignment.CenterStart,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = emailKeyboardActions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(BackgroundColor)
                        .border(1.dp, AccentBorderColor, RoundedCornerShape(20.dp))
                        .focusRequester(emailFocusRequester)
                )

                Spacer(modifier = Modifier.size(20.dp))

                PasswordTextField(
                    enabled = !isLoading,
                    value = viewModel.passwordText,
                    onValueChanged = { viewModel.updatePasswordText(it) },
                    textStyle = artistsStyle,
                    hintText = stringResource(R.string.password),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = passwordKeyboardActions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .focusRequester(passwordFocusRequester)
                )

                Spacer(modifier = Modifier.size(20.dp))

                PasswordTextField(
                    enabled = !isLoading,
                    value = viewModel.repeatPasswordText,
                    onValueChanged = { viewModel.updateRepeatPasswordText(it) },
                    textStyle = artistsStyle,
                    hintText = stringResource(R.string.repeat_password),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = repeatPasswordKeyboardActions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .focusRequester(repeatPasswordFocusRequester)
                )

                Spacer(modifier = Modifier.size(24.dp))

                AuthButton(
                    isLoading = isLoading,
                    onClick = { viewModel.register() },
                    text = stringResource(R.string.register)
                )
            }

            RegistrationSection(
                isLoading = isLoading,
                onRegisterClick = onLoginClick,
                isLogin = true
            )
        }

        if (isLoading) {
            LoadingOverlay()
        }
    }
}
