package com.linca.vybes.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.linca.vybes.model.SearchResultItem
import com.linca.vybes.post.service.VybesPostService

abstract class GenericSearchViewModel<T : SearchResultItem>(
    private val vybesPostService: VybesPostService
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    var searchResults by mutableStateOf<List<T>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun updateSearchQuery(query: String) {
        searchQuery = query
        if (query.length >= 2) {
            performSearch(query)
        } else {
            searchResults = emptyList()
        }
    }

    abstract fun performSearch(query: String)

    protected fun updateLoadingState(loading: Boolean) {
        isLoading = loading
    }

    protected fun updateErrorState(message: String?) {
        errorMessage = message
    }

    protected fun updateResults(results: List<T>) {
        searchResults = results
    }
}
