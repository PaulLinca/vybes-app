package com.linca.vybes.auth.login

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.linca.vybes.R
import com.linca.vybes.auth.shared.AuthButton
import com.linca.vybes.auth.shared.LoadingOverlay
import com.linca.vybes.common.theme.BackgroundColor
import com.linca.vybes.common.theme.PrimaryTextColor
import com.linca.vybes.common.theme.logoStyle
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

@Serializable
object LoginScreen

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onUsernameSetupRequired: () -> Unit
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoginSuccess by viewModel.isLoginSuccess.collectAsState()
    val requiresUsernameSetup by viewModel.requiresUsernameSetup.collectAsState()
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
                    text = "vybes",
                    color = PrimaryTextColor,
                    style = logoStyle,
                    fontSize = 50.sp
                )
            }

            if (networkError != null) {
                Text(
                    text = networkError ?: "",
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            AuthButton(
                isLoading = isLoading,
                onClick = { if (!isLoading) viewModel.signIn(context as ComponentActivity) },
                text = "Sign in with Google",
                icon = painterResource(id = R.drawable.google_logo),
                iconContentDescription = "Sign in with Google"
            )

            LaunchedEffect(Unit) { delay(300) }
        }

        if (isLoading) LoadingOverlay()
    }
}
