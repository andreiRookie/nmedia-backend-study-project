package com.andreirookie.nmediabackendstudyproject.repository


import com.andreirookie.nmediabackendstudyproject.dto.Post

interface PostRepository {
    fun getAllAsync(callback: PostCallback<List<Post>>)

    interface PostCallback<T> {
        fun onSuccess(value: T)
        fun onError(e: Exception)
    }

    fun removeByIdAsync(id: Long, callback: PostCallback<Unit>)

    fun saveAsync(post: Post, callback: PostCallback<Unit>)

    fun likeByIdAsync(id: Long, callback: PostCallback<Long>)
    fun dislikeByIdAsync(id: Long, callback: PostCallback<Long>)

//    fun likeByIdAsync(id: Long, callback: PostCallback<Post>)
//    fun dislikeByIdAsync(id: Long, callback: PostCallback<Post>)
}
