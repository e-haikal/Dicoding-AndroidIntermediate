package com.siaptekno.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.siaptekno.storyapp.DataDummy
import com.siaptekno.storyapp.MainDispatcherRule
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.data.remote.response.ListStoryItem
import com.siaptekno.storyapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import androidx.recyclerview.widget.ListUpdateCallback

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    // Rule to force LiveData to execute tasks synchronously
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Rule to manage coroutine dispatcher for testing
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: Repository // Mock repository for dependency injection

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        // Generate dummy data and wrap it in PagingData
        val dummyStories = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStories)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data

        // Mock the repository's behavior
        Mockito.`when`(storyRepository.getStoriesPaging("token")).thenReturn(expectedStories)

        // Initialize ViewModel with mocked repository
        val mainViewModel = MainViewModel(storyRepository)
        val actualStories: PagingData<ListStoryItem> =
            mainViewModel.stories("token").getOrAwaitValue()

        // Use AsyncPagingDataDiffer to handle PagingData in tests
        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK, // Callback for detecting changes in data
            updateCallback = noopListUpdateCallback, // No-op implementation for list updates
            workerDispatcher = Dispatchers.Main, // Dispatcher for running tasks
        )
        differ.submitData(actualStories) // Submit data to differ

        // Verify the data is not null and matches expectations
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        // Test for empty PagingData
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data

        // Mock repository response
        Mockito.`when`(storyRepository.getStoriesPaging("token")).thenReturn(expectedStories)

        // Initialize ViewModel and retrieve data
        val mainViewModel = MainViewModel(storyRepository)
        val actualStories: PagingData<ListStoryItem> = mainViewModel.stories("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories) // Submit data to differ

        // Verify no data is returned
        Assert.assertEquals(0, differ.snapshot().size)
    }

    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}

        override fun onRemoved(position: Int, count: Int) {}

        override fun onMoved(fromPosition: Int, toPosition: Int) {}

        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}

// Mock PagingSource for testing purposes
class StoryPagingSource : PagingSource<Int, ListStoryItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return LoadResult.Page(emptyList(), prevKey = null, nextKey = null) // Empty data for testing
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return null
    }

    // Helper function to create a PagingData snapshot
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items) // Create PagingData from a list
        }
    }
}
