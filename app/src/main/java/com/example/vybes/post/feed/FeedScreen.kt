package com.example.vybes.post.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.vybes.R
import com.example.vybes.add.SearchTrackScreen
import com.example.vybes.common.theme.SpotifyDarkGrey
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.logoStyle
import com.example.vybes.feedback.FeedbackScreen
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.model.VybeScreen
import com.example.vybes.profile.ProfileScreen
import com.example.vybes.sharedpreferences.SharedPreferencesManager
import kotlinx.serialization.Serializable

@Serializable
object FeedScreen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FeedScreen(
    navController: NavController,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pullRefresh(pullRefreshState)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            TopBar(navController)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                val vybes by viewModel.vybes.collectAsState()

                if (vybes.isEmpty()) {
                    EmptyFeedState()
                } else {
                    PopulatedFeedState(vybes, navController, viewModel)
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = SpotifyDarkGrey,
            contentColor = White
        )

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
private fun PopulatedFeedState(
    vybes: List<Vybe>,
    navController: NavController,
    viewModel: FeedViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        vybes.forEach { v ->
            val currentUserId = SharedPreferencesManager.getUserId()
            val isLikedByCurrentUser = v.likes.any { it.userId == currentUserId }

            VybePost(
                vybe = v,
                onClickCard = { navController.navigate(VybeScreen(v.id)) },
                onLikeClicked = {
                    viewModel.clickLikeButton(
                        v.id,
                        isLikedByCurrentUser
                    )
                }
            )
        }
    }
}

@Composable
private fun EmptyFeedState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Image(
            painter = painterResource(id = R.drawable.empty_feed_art),
            contentDescription = "Empty Feed",
            modifier = Modifier
                .size(160.dp)
                .padding(bottom = 16.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )

        Text(
            text = "It's quiet here...",
            style = MaterialTheme.typography.h6,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Share a vybe to add some music to the mix!",
            style = MaterialTheme.typography.body1,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
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