package com.siaptekno.storyapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.siaptekno.storyapp.data.remote.response.ListStoryItem
import com.siaptekno.storyapp.databinding.ItemStoryBinding

// Adapter for RecyclerView to display a paginated list of stories
class MainAdapter(private val onItemClick: (ListStoryItem) -> Unit) :
    PagingDataAdapter<ListStoryItem, MainAdapter.MainViewHolder>(DIFF_CALLBACK) {

    // ViewHolder class for binding story item views
    inner class MainViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            // Set story data to UI components
            binding.tvItemName.text = story.name
            Glide.with(binding.root.context)
                .load(story.photoUrl) // Load story image using Glide
                .into(binding.ivItemPhoto)

            // Handle item click to navigate to detail screen
            binding.root.setOnClickListener {
                onItemClick(story)
            }
        }
    }

    // Inflate the layout for each story item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    // Bind the story data to the corresponding ViewHolder
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    // DiffUtil for efficiently updating the RecyclerView
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id // Compare items by unique ID
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem // Compare full content for equality
            }
        }
    }
}
