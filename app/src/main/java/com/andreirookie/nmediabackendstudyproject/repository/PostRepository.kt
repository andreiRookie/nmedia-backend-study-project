package com.andreirookie.nmediabackendstudyproject.repository


import com.andreirookie.nmediabackendstudyproject.dto.Post

interface PostRepository {
    fun likeById(id: Long): Post
    fun save(post: Post)
    fun removeById(id: Long)
    fun dislikeById(id: Long): Post

    fun getAllAsync(callback: GetAllPostsCallback)

    interface GetAllPostsCallback {

        fun onSuccess(posts: List<Post>)
        fun onError(e: Exception)
    }
}
