package com.siaptekno.storyapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.siaptekno.storyapp.databinding.ItemLoadingBinding

class LoadingStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {

    // Bind the load state to the view holder to update the UI accordingly
    override fun onBindViewHolder(
        holder: LoadingStateViewHolder,
        loadState: LoadState
    ) {
        holder.bind(loadState) // Call the bind method to update the view based on load state
    }

    // Create the view holder for the loading state with its layout and retry callback
    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, retry) // Return a new view holder instance
    }

    // ViewHolder class to bind loading/error states and handle retry logic
    class LoadingStateViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Set a listener on the retry button to trigger retry action on failure
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        // Bind the load state to the UI components (progress bar, retry button, error message)
        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage // Display error message
            }
            // Show/hide progress bar and error message based on loading or error state
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }
    }
}
