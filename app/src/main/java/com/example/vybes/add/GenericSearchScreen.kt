package com.example.vybes.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vybes.R
import com.example.vybes.model.SearchResultItem
import com.example.vybes.common.composables.MultilineTextField
import com.example.vybes.common.composables.TopBarWithBackButton
import com.example.vybes.common.theme.BackgroundColor
import com.example.vybes.common.theme.ErrorRed
import com.example.vybes.common.theme.PrimaryTextColor
import com.example.vybes.common.theme.artistsStyle
import com.example.vybes.common.theme.songTitleStyle

@Composable
fun <T : SearchResultItem> GenericSearchScreen(
    navController: NavController,
    viewModel: GenericSearchViewModel<T>,
    hint: String,
    onItemClick: (T) -> Unit,
    itemContent: @Composable (T) -> Unit = { item ->
        SearchResultItemRow(
            item = item,
            onClick = { onItemClick(item) }
        )
    }
) {
    val searchQuery = viewModel.searchQuery
    val searchResults = viewModel.searchResults
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundColor)
    ) {
        TopBarWithBackButton(onGoBack = { navController.popBackStack() }) {
            Text(
                text = stringResource(R.string.share_prompt),
                color = PrimaryTextColor,
                textAlign = TextAlign.Center,
                style = songTitleStyle,
            )
        }
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            MultilineTextField(
                value = searchQuery,
                onValueChanged = { viewModel.updateSearchQuery(it) },
                hintText = hint,
                textStyle = artistsStyle,
                maxLines = 1,
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = PrimaryTextColor
                    )
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage,
                        color = ErrorRed,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                searchResults.isNotEmpty() -> {
                    LazyColumn {
                        items(searchResults) { item ->
                            itemContent(item)
                        }
                    }
                }
            }
        }
    }
}