package com.example.vybes.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.vybes.R
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.ErrorRed
import com.example.vybes.common.theme.SpotifyDarkGrey
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.feedback.FeedbackScreen
import com.example.vybes.post.model.User

@Composable
fun ProfileScreen(
    user: User,
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val isCurrentUser = profileViewModel.isCurrentUser(user)

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
                text = user.username,
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
            if (isCurrentUser) {
                Button(
                    onClick = { navController.navigate(FeedbackScreen) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SpotifyDarkGrey),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.give_feedback),
                        color = White
                    )
                }
                Button(
                    onClick = { profileViewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SpotifyDarkGrey),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.logout),
                        color = ErrorRed
                    )
                }
            }
        }
    }
}