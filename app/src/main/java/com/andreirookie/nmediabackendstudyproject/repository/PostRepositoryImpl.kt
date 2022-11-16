package com.andreirookie.nmediabackendstudyproject.repository


import com.andreirookie.nmediabackendstudyproject.api.PostsApi
import com.andreirookie.nmediabackendstudyproject.dto.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PostRepositoryImpl : PostRepository {

    override fun getAll(callback: PostRepository.PostCallback<List<Post>>) {

        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(
                call: Call<List<Post>>,
                response: Response<List<Post>>
            ) {
                if (!response.isSuccessful) {
                    println("статус сообщения ответа: ${response.message()}")
                    println("код ответа: ${response.code()}")
                    println("raw body ответа: ${response.errorBody()}")

                    callback.onError(RuntimeException(response.message()))
                    return
                }
                println("заголовки ответа: ${response.headers()}")
                println("необработанное тело ответа: ${response.raw()}")
                println("приведённое с помощью конвертера к типу List<Post>: ${response.body()}")

                callback.onSuccess(response.body().orEmpty() /*?: throw RuntimeException("body is null")*/)
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(RuntimeException(t))
            }

        })
    }

    override fun save(post: Post, callback: PostRepository.PostCallback<Post>) {
        PostsApi.retrofitService.save(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                    return
                }
                callback.onError(RuntimeException(response.message()))
            }
            override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
            }
        })
    }

    override fun likeById(id: Long, callback: PostRepository.PostCallback<Long>) {
            PostsApi.retrofitService.likeById(id).enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        callback.onSuccess(response.body()?.id ?: throw RuntimeException("body is null"))
                        return
                    }
                    callback.onError(RuntimeException(response.message()))
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }
            })
    }

    override fun dislikeById(id: Long, callback: PostRepository.PostCallback<Long>) {
        PostsApi.retrofitService.dislikeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()?.id ?: throw RuntimeException("body is null"))
                    return
                }
                callback.onError(RuntimeException(response.message()))
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(RuntimeException(t))
            }

        })
    }

    override fun removeById(id: Long, callback: PostRepository.PostCallback<Unit>) {
        PostsApi.retrofitService.removeById(id).enqueue(object  : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    callback.onSuccess(Unit)
                    return
                }
                callback.onError(RuntimeException(response.message()))
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(RuntimeException(t))
            }

        })
    }
}