package com.linca.vybes.common.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linca.vybes.common.theme.BackgroundColor
import com.linca.vybes.common.theme.ElevatedBackgroundColor
import com.linca.vybes.common.theme.SecondaryTextColor
import com.linca.vybes.common.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostFilterTabs(
    selectedFilter: PostFilter,
    onFilterSelected: (PostFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PostFilter.entries.forEach { filter ->
            val isSelected = selectedFilter == filter
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.getDisplayName(),
                        color = if (isSelected) White else SecondaryTextColor
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ElevatedBackgroundColor,
                    containerColor = BackgroundColor
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}