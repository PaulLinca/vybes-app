package com.example.vybes.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.vybes.R
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.SpotifyDarkGrey
import com.example.vybes.common.theme.TryoutGreen
import com.example.vybes.common.theme.White
import com.example.vybes.common.theme.songTitleStyle
import com.example.vybes.post.model.User

@Composable
fun ProfileScreen(
    user: User,
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {

    LaunchedEffect(user.username) {
        profileViewModel.loadUser(user.username)
    }
    val userState by profileViewModel.user.collectAsState()
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
                    .size(64.dp)
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(SpotifyDarkGrey, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Favourite artists",
                        color = White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        userState?.favoriteArtists.orEmpty().forEach { artist ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = rememberAsyncImagePainter(artist.imageUrl),
                                    contentDescription = artist.name,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, TryoutGreen, CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier
                    .size(30.dp)
                    .fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(SpotifyDarkGrey, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Favourite albums",
                        color = White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        userState?.favoriteAlbums.orEmpty().forEach { artist ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = rememberAsyncImagePainter(artist.imageUrl),
                                    contentDescription = artist.name,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, TryoutGreen, RoundedCornerShape(12.dp))
                                )
                            }
                        }
                    }
                }
            }


            if (isCurrentUser) {
//                Button(
//                    onClick = { navController.navigate(FeedbackScreen) },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .align(Alignment.CenterHorizontally),
//                    shape = RoundedCornerShape(16.dp),
//                    colors = ButtonDefaults.buttonColors(containerColor = SpotifyDarkGrey),
//                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
//                ) {
//                    Text(
//                        text = stringResource(R.string.give_feedback),
//                        color = White
//                    )
//                }
//                Button(
//                    onClick = { profileViewModel.logout() },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .align(Alignment.CenterHorizontally),
//                    shape = RoundedCornerShape(16.dp),
//                    colors = ButtonDefaults.buttonColors(containerColor = SpotifyDarkGrey),
//                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
//                ) {
//                    Text(
//                        text = stringResource(R.string.logout),
//                        color = ErrorRed
//                    )
//                }
            }
        }
    }
}