package com.example.vybes.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vybes.R
import com.example.vybes.auth.login.LoginScreen
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.SpotifyDarkGrey
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import kotlinx.serialization.Serializable

@Serializable
object ProfileScreen

@Composable
fun ProfileScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TopBarWithBackButton(onGoBack = { navController.popBackStack() }) {}
        Column(
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = "Icon",
                colorFilter = ColorFilter.tint(White),
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(
                modifier = Modifier
                    .size(10.dp)
                    .fillMaxWidth()
            )
            Text(
                text = getCurrentUsername(),
                color = White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                style = songTitleStyle,
            )
            Spacer(
                modifier = Modifier
                    .size(30.dp)
                    .fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.CenterHorizontally)
                    .background(SpotifyDarkGrey)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clickable(onClick = onClearCache(navController)),
            ) {
                Text(
                    text = "Clear cache", color = White,
                )
            }
        }
    }
}

private fun getCurrentUsername() =
    SharedPreferencesManager.getUsername().orEmpty()

private fun onClearCache(
    navController: NavController
): () -> Unit = {
    SharedPreferencesManager.clearUserData()
    navController.navigate(LoginScreen) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}