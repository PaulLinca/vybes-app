package com.linca.vybes.post

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.linca.vybes.R
import com.linca.vybes.common.composables.DebouncedIconButton
import com.linca.vybes.common.composables.DebouncedImageButton
import com.linca.vybes.common.composables.MultilineTextField
import com.linca.vybes.common.theme.BackgroundColor
import com.linca.vybes.common.theme.ElevatedBackgroundColor
import com.linca.vybes.common.theme.PrimaryTextColor
import com.linca.vybes.common.theme.SecondaryTextColor
import com.linca.vybes.common.theme.TryoutRed
import com.linca.vybes.common.theme.artistsStyle
import com.linca.vybes.common.util.DateUtils
import com.linca.vybes.model.Comment
import com.linca.vybes.sharedpreferences.SharedPreferencesManager

@Composable
fun CommentInputBar(
    commentText: String,
    remainingCharacters: Int,
    onTextChanged: (String) -> Unit,
    onSendComment: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .height(56.dp)
                .weight(1f)
        ) {

            MultilineTextField(
                enabled = true,
                value = commentText,
                onValueChanged = onTextChanged,
                hintText = "Add a comment...",
                textStyle = artistsStyle,
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            )

            Text(
                text = "$remainingCharacters",
                color = if (remainingCharacters < 50) TryoutRed else SecondaryTextColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 8.dp)
            )
        }


        DebouncedIconButton(
            onClick = onSendComment,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentDescription = "Send Button",
            iconResId = R.drawable.send
        )
    }
}

@Composable
fun CommentSection(
    comments: List<Comment>,
    onLikeComment: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (comments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No comments yet. Be the first to comment!",
                    color = Color.LightGray,
                    style = artistsStyle,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            comments.forEach { comment ->
                CommentItem(
                    comment = comment,
                    onLikeComment = onLikeComment,
                    onUserClick = { navController.navigate(comment.user) }
                )
                Divider(
                    color = Color.DarkGray.copy(alpha = 0.5f),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    onLikeComment: (Long, Boolean) -> Unit,
    onUserClick: () -> Unit
) {
    val currentUserId = SharedPreferencesManager.getUserId()
    val isLikedByCurrentUser = comment.likes.orEmpty().any { it.userId == currentUserId }

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                DebouncedImageButton(
                    onClick = { onUserClick() },
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape),
                    contentDescription = "Go to user profile",
                    pictureUrl = comment.user.profilePictureUrl
                )
                Text(
                    text = comment.user.username,
                    textAlign = TextAlign.Start,
                    color = PrimaryTextColor,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .clickable { onUserClick }
                )

                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = DateUtils.formatPostedDate(comment.postedDate),
                        textAlign = TextAlign.Start,
                        color = Color.LightGray,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(25.dp))
                Text(
                    text = comment.text,
                    textAlign = TextAlign.Start,
                    color = PrimaryTextColor,
                    style = artistsStyle,
                )
            }
        }

        Column(
            Modifier.padding(horizontal = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DebouncedIconButton(
                onClick = { onLikeComment(comment.id, isLikedByCurrentUser) },
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterHorizontally),
                contentDescription = if (isLikedByCurrentUser) "Unlike comment" else "Like comment",
                iconResId = if (isLikedByCurrentUser) R.drawable.heart_filled else R.drawable.heart
            )
            Text(
                text = comment.likes.orEmpty().size.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = PrimaryTextColor,
                    fontSize = 10.sp
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ErrorScreen(
    errorMessage: String,
    onRetry: () -> Unit,
    onGoBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryTextColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryTextColor,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onGoBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElevatedBackgroundColor
                )
            ) {
                Text("Go Back", color = SecondaryTextColor)
            }

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Retry")
            }
        }
    }
}