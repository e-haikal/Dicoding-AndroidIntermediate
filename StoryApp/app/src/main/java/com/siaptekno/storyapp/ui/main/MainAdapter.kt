package com.siaptekno.storyapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.siaptekno.storyapp.data.remote.response.ListStoryItem
import com.siaptekno.storyapp.databinding.ItemStoryBinding


class MainAdapter(private val onItemClick: (ListStoryItem) -> Unit) : PagingDataAdapter<ListStoryItem, MainAdapter.MainViewHolder>(DIFF_CALLBACK) {

    inner class MainViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.tvItemName.text = story.name
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)

            binding.root.setOnClickListener {
                onItemClick(story)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
