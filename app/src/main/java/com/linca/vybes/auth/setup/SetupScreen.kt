package com.linca.vybes.auth.setup

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.linca.vybes.R
import com.linca.vybes.auth.shared.AuthButton
import com.linca.vybes.auth.shared.LoadingOverlay
import com.linca.vybes.auth.shared.SingleErrorMessage
import com.linca.vybes.common.composables.MultilineTextField
import com.linca.vybes.common.theme.AccentBorderColor
import com.linca.vybes.common.theme.BackgroundColor
import com.linca.vybes.common.theme.PrimaryTextColor
import com.linca.vybes.common.theme.SecondaryTextColor
import com.linca.vybes.common.theme.artistsStyle
import com.linca.vybes.common.theme.logoStyle
import com.linca.vybes.common.theme.songTitleStyle
import kotlinx.coroutines.delay
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
    val usernameError by viewModel.usernameError.collectAsState()

    LaunchedEffect(isSetupSuccess) {
        if (isSetupSuccess) {
            onSetupSuccess()
        }
    }

    val focusManager = LocalFocusManager.current
    val usernameFocusRequester = remember { FocusRequester() }

    val usernameKeyboardActions = KeyboardActions(
        onDone = {
            focusManager.clearFocus()
            if (!isLoading) viewModel.setUsername()
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

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = stringResource(R.string.choose_a_username),
                color = PrimaryTextColor,
                style = songTitleStyle,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = stringResource(R.string.username_setup_subtitle),
                color = SecondaryTextColor,
                style = artistsStyle,
                textAlign = TextAlign.Center
            )

            SingleErrorMessage(errorMessage = usernameError)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                UsernameField(
                    viewModel = viewModel,
                    isLoading = isLoading,
                    keyboardActions = usernameKeyboardActions,
                    focusRequester = usernameFocusRequester
                )

                Spacer(modifier = Modifier.size(24.dp))

                AuthButton(
                    isLoading = isLoading,
                    onClick = { viewModel.setUsername() },
                    text = stringResource(R.string.continue_text)
                )
            }

            LaunchedEffect(Unit) {
                delay(300)
                usernameFocusRequester.requestFocus()
            }
        }

        if (isLoading) {
            LoadingOverlay()
        }
    }
}

@Composable
fun UsernameField(
    viewModel: SetupViewModel,
    isLoading: Boolean,
    keyboardActions: KeyboardActions,
    focusRequester: FocusRequester
) {
    MultilineTextField(
        enabled = !isLoading,
        value = viewModel.usernameText,
        onValueChanged = { viewModel.updateUsernameText(it) },
        textStyle = artistsStyle,
        hintText = stringResource(R.string.username_setup_hint),
        maxLines = 1,
        contentAlignment = Alignment.CenterStart,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = keyboardActions,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(BackgroundColor, shape = RoundedCornerShape(20.dp))
            .border(1.dp, AccentBorderColor, RoundedCornerShape(20.dp))
            .focusRequester(focusRequester)
    )
}