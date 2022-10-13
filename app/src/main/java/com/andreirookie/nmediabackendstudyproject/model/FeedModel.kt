package com.andreirookie.nmediabackendstudyproject.model

import com.andreirookie.nmediabackendstudyproject.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
    val empty: Boolean = false,
    val refreshing: Boolean = false,
)