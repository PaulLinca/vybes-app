package com.example.vybes.post.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.example.vybes.common.composables.DebouncedIconButton
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.composables.TopBarWithSideButtons
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ElevatedBackgroundColor
import com.example.vybes.common.theme.IconColor
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.SubtleBorderColor
import com.example.vybes.common.theme.VybesVeryLightGray
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.logoStyle
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.post.model.User
import com.example.vybes.post.model.Vybe
import com.example.vybes.post.model.VybeScreen
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
            .background(BackgroundColor)
            .padding(horizontal = 10.dp)
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
                val isLoading by viewModel.isLoading.collectAsState()

                when {
                    isLoading -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = White)
                        }
                    }

                    vybes.isEmpty() -> {
                        EmptyFeedState()
                    }

                    else -> {
                        PopulatedFeedState(vybes, navController, viewModel)
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = VybesVeryLightGray,
            contentColor = White
        )
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
            .fillMaxWidth(),
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
                },
                navController
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
            colorFilter = ColorFilter.tint(IconColor)
        )

        Text(
            text = "It's quiet here...",
            style = MaterialTheme.typography.h6,
            color = PrimaryTextColor,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Share a vybe to add some music to the mix!",
            style = MaterialTheme.typography.body1,
            color = PrimaryTextColor.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun TopBar(navController: NavController) {
    TopBarWithSideButtons(
        leftButtonComposable = {
            DebouncedIconButton(
                onClick = { navController.navigate(SearchTrackScreen) },
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
                contentDescription = "Add New Vybe Button",
                iconResId = R.drawable.add_icon_square
            )
        },
        centerComposable = {
            Text(
                text = stringResource(R.string.app_name),
                color = White,
                style = logoStyle
            )
        },
        rightButtonComposable = {
            DebouncedIconButton(
                onClick = {
                    navController.navigate(
                        User(
                            userId = SharedPreferencesManager.getUserId(),
                            username = SharedPreferencesManager.getUsername().orEmpty()
                        )
                    )
                },
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
                contentDescription = "Profile Button",
                iconResId = R.drawable.user
            )
        }
    )
}

@Composable
fun AddVybeButton(navController: NavController) {
    Box(
        modifier = Modifier
            .clickable(onClick = { navController.navigate(SearchTrackScreen) })
            .fillMaxWidth()
            .height(90.dp)
            .border(1.dp, SubtleBorderColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(color = ElevatedBackgroundColor)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add icon",
                    tint = IconColor,
                    modifier = Modifier.size(45.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 5.dp, horizontal = 2.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(text = "Share a vybe", color = Color.Gray, style = songTitleStyle)
            }
        }
    }
}