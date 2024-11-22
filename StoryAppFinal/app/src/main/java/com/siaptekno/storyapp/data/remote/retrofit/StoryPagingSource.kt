package com.siaptekno.storyapp.data.remote.retrofit

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.siaptekno.storyapp.data.remote.response.ListStoryItem

class StoryPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, ListStoryItem>() {

    // Determines the key for the next or previous page to be loaded
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    // Loads a page of data
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX // Default to the initial page
            val responseData = apiService.getStories(
                "Bearer $token",
                page = position,
                size = params.loadSize
            )

            LoadResult.Page(
                data = responseData.listStory, // Fetched data
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1, // Previous page key
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1 // Next page key
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception) // Handle errors
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1 // Default initial page index
    }
}
