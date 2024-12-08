package com.example.vybes.post.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.vybes.R
import com.example.vybes.add.SearchTrackScreen
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.logoStyle
import com.example.vybes.feedback.FeedbackScreen
import com.example.vybes.post.model.VybeScreen
import com.example.vybes.profile.ProfileScreen
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import kotlinx.serialization.Serializable

@Serializable
object FeedScreen

@Composable
fun FeedScreen(
    navController: NavController,
    viewModel: FeedViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopBar(navController)
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val vybes by viewModel.vybes.collectAsState()
                vybes.forEach { v ->
                    val currentUserId = SharedPreferencesManager.getUserId(LocalContext.current)
                    val isLikedByCurrentUser = v.likes.any { it.userId == currentUserId }

                    VybePost(
                        vybe = v,
                        onClickCard = { navController.navigate(VybeScreen(v.id)) },
                        onLikeClicked = { viewModel.clickLikeButton(v.id, isLikedByCurrentUser) })
                }
            }
        }
        Button(
            onClick = { navController.navigate(SearchTrackScreen) },
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(4.dp, Color.White, CircleShape)
                .align(Alignment.BottomCenter),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            // empty
        }
    }
}

@Composable
fun TopBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        IconButton(
            onClick = { navController.navigate(FeedbackScreen) },
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .align(Alignment.CenterStart)
        ) {
            Image(
                painter = painterResource(id = R.drawable.lightbulb),
                contentDescription = "Feedback Button",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = stringResource(R.string.app_name),
            color = White,
            style = logoStyle,
            modifier = Modifier.align(Alignment.Center)
        )
        IconButton(
            onClick = { navController.navigate(ProfileScreen) },
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .align(Alignment.CenterEnd)
        ) {
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = "Profile Button",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun preview() {
    val navController = rememberNavController()
    FeedScreen(navController)
}