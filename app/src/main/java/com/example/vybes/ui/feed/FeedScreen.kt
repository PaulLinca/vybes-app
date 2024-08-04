package com.example.vybes.ui.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.vybes.R
import com.example.vybes.ui.feed.feedback.FeedbackScreen
import com.example.vybes.ui.feed.model.vybes
import com.example.vybes.ui.profile.ProfileScreen
import com.example.vybes.ui.theme.White
import com.example.vybes.ui.theme.logoStyle
import kotlinx.serialization.Serializable

@Serializable
object FeedScreen

@Composable
fun FeedScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
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
            vybes.forEach { v ->
                VybePost(vybe = v, onClickCard = {
                    navController.navigate(v)
                })
            }
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
            text = "vybes",
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