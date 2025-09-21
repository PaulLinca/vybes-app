package com.linca.vybes.post.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.linca.vybes.R
import com.linca.vybes.common.composables.DebouncedImageButton
import com.linca.vybes.common.composables.debouncedClickable
import com.linca.vybes.common.theme.ElevatedBackgroundColor
import com.linca.vybes.common.theme.PrimaryTextColor
import com.linca.vybes.common.theme.SecondaryTextColor
import com.linca.vybes.common.theme.TryoutBlue
import com.linca.vybes.common.theme.TryoutYellow
import com.linca.vybes.common.theme.VybesVeryDarkGray
import com.linca.vybes.model.Challenge
import com.linca.vybes.model.ChallengeOption

@Composable
fun FeaturedChallengeCard(
    challenge: Challenge,
    onVoteOption: (Long) -> Unit = {},
    onNavigateToSubmission: () -> Unit = {},
    isVoting: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .debouncedClickable {
                when (challenge.type) {
                    "POLL" -> isExpanded = !isExpanded
                    "USER_SUBMISSIONS" -> onNavigateToSubmission()
                }
            },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, TryoutYellow.copy(alpha = 0.3f)),
        colors = CardDefaults.cardColors(containerColor = ElevatedBackgroundColor)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                TryoutYellow.copy(alpha = 0.08f),
                                Color.Transparent,
                                TryoutYellow.copy(alpha = 0.05f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Featured",
                            tint = TryoutYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "FEATURED ${challenge.type}",
                            style = MaterialTheme.typography.labelMedium,
                            color = TryoutYellow,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (isVoting) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = TryoutYellow
                            )
                        }
                    }

                    challenge.createdBy?.let {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            challenge.createdBy.profilePictureUrl?.let { profileUrl ->
                                DebouncedImageButton(
                                    onClick = { /* Navigate to user profile */ },
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape),
                                    contentDescription = "Creator profile",
                                    pictureUrl = profileUrl
                                )
                            } ?: run {
                                Icon(
                                    painter = painterResource(R.drawable.user),
                                    contentDescription = "Creator",
                                    tint = SecondaryTextColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "by ${challenge.createdBy.username}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryTextColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = challenge.question,
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryTextColor,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (challenge.type) {
                        "POLL" -> {
                            Text(
                                text = "${challenge.options.orEmpty().size} options",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SecondaryTextColor,
                                fontWeight = FontWeight.Normal,
                            )
                        }

                        "USER_SUBMISSIONS" -> {
                            Text(
                                text = "Submit your answer",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SecondaryTextColor,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                    }

                    when (challenge.type) {
                        "POLL" -> {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = SecondaryTextColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        "USER_SUBMISSIONS" -> {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Go to submission",
                                tint = SecondaryTextColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isExpanded && challenge.type == "POLL",
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300)),
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                ) {
                    Column(
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        ChallengeOptions(
                            options = challenge.options.orEmpty(),
                            onVoteOption = onVoteOption,
                            isVoting = isVoting
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChallengeOptions(
    options: List<ChallengeOption>,
    onVoteOption: (Long) -> Unit,
    isVoting: Boolean = false
) {
    val totalVotes = options.sumOf { it.votesCount }
    val hasUserVoted = options.any { it.votedByUser }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { option ->
            ChallengeOptionItem(
                option = option,
                totalVotes = totalVotes,
                onClick = { onVoteOption(option.id) },
                isVoting = isVoting,
                hasUserVoted = hasUserVoted
            )
        }
    }
}

@Composable
private fun ChallengeOptionItem(
    option: ChallengeOption,
    totalVotes: Int,
    onClick: () -> Unit,
    isVoting: Boolean = false,
    hasUserVoted: Boolean = false
) {
    val percentage = if (totalVotes > 0) (option.votesCount.toFloat() / totalVotes) * 100 else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .debouncedClickable(enabled = !isVoting && !hasUserVoted) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (option.votedByUser) {
                TryoutBlue.copy(alpha = 0.2f)
            } else {
                VybesVeryDarkGray.copy(alpha = 0.3f)
            }
        ),
        border = if (option.votedByUser) {
            BorderStroke(1.dp, TryoutBlue.copy(alpha = 0.5f))
        } else null
    ) {
        Box {
            if (hasUserVoted && totalVotes > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percentage / 100f)
                        .height(72.dp)
                        .background(
                            TryoutBlue.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    option.album != null -> {
                        MediaOptionContent(
                            imageUrl = option.album.imageUrl,
                            title = option.album.name,
                            subtitle = option.album.artists.joinToString(", ") { it.name }
                        )
                    }

                    option.track != null -> {
                        MediaOptionContent(
                            imageUrl = option.track.imageUrl,
                            title = option.track.name,
                            subtitle = option.track.artists.joinToString(", ") { it.name }
                        )
                    }

                    option.artist != null -> {
                        MediaOptionContent(
                            imageUrl = option.artist.imageUrl,
                            title = option.artist.name,
                            subtitle = null
                        )
                    }

                    else -> {
                        Text(
                            text = option.customText.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrimaryTextColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                if (hasUserVoted) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (option.votedByUser) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Voted",
                                    tint = TryoutBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }

                            Text(
                                text = "${option.votesCount} votes",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (option.votedByUser) TryoutBlue else SecondaryTextColor,
                                fontWeight = if (option.votedByUser) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }

                        if (totalVotes > 0) {
                            Text(
                                text = "${percentage.toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryTextColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaOptionContent(
    imageUrl: String?,
    title: String,
    subtitle: String?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imageUrl != null) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .size(Size.ORIGINAL)
                    .build(),
                contentScale = ContentScale.Crop,
            )

            Image(
                painter = painter,
                contentDescription = "Media cover",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))
        }

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryTextColor,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}