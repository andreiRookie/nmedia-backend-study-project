package com.andreirookie.nmediabackendstudyproject.adapter

import androidx.recyclerview.widget.DiffUtil
import com.andreirookie.nmediabackendstudyproject.dto.Post


class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}