package com.andreirookie.nmediabackendstudyproject.repository


import com.andreirookie.nmediabackendstudyproject.dto.Post

interface PostRepository {
    fun getAll(callback: PostCallback<List<Post>>)

    interface PostCallback<T> {
        fun onSuccess(value: T)
        fun onError(e: Exception)
    }

    fun removeById(id: Long, callback: PostCallback<Unit>)

    fun save(post: Post, callback: PostCallback<Post>)

    fun likeById(id: Long, callback: PostCallback<Long>)
    fun dislikeById(id: Long, callback: PostCallback<Long>)

//    fun likeByIdAsync(id: Long, callback: PostCallback<Post>)
//    fun dislikeByIdAsync(id: Long, callback: PostCallback<Post>)
}
