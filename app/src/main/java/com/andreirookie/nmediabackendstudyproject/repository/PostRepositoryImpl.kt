package com.andreirookie.nmediabackendstudyproject.repository


import com.andreirookie.nmediabackendstudyproject.dto.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    // инстанс класса ОкХттпКлиент, делается через спецкласс Билдер
    private val client = OkHttpClient.Builder()
        .connectTimeout(7, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        // ip адрес эмулятора на компе - http://10.0.2.2
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAllAsync(callback: PostRepository.PostCallback<List<Post>>) {
        val request: Request = Request.Builder()
            // api/posts - адрес запроса
            //вставка slow - задержка 5 сек для тестиррвоания методов соединения
            //посмотреть как работает прилоожение, если медленный интернет
            // запрос get - по умолчанию, не пишется
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)//ньюКолл формирует запрос
            .enqueue(object : Callback { // добавляем запрос в очередь
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseBody =
                            response.body?.string() ?: throw RuntimeException("body is null")
                        callback.onSuccess(gson.fromJson(responseBody, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    // Лучший подход, если к серверу единовременно
    // обращается сразу несколько клиентов
//    override fun likeById(id: Long): Post {
//        val request: Request = Request.Builder()
//            .post("".toRequestBody())
//            .url("$BASE_URL/api/slow/posts/$id/likes")
//            .build()
//
//        return client.newCall(request)
//            .execute()
//            .let { it.body?.string() ?: throw RuntimeException("body is null") }
//            // можно без тайптокена,т.к. Post обычный класс,
//            // а не интерфейс/сложная коллекция
//            .let {
//                gson.fromJson(it, Post::class.java)
//            }
//    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.PostCallback<Long>) {
        val request: Request = Request.Builder()
            .post("".toRequestBody())
            .url("$BASE_URL/api/slow/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object  : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: throw RuntimeException("body is null")
                        callback.onSuccess(gson.fromJson(body, Post::class.java).id)
                    } catch (e: Exception){
                        callback.onError(e)
                    }
                }
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun dislikeByIdAsync(id: Long, callback: PostRepository.PostCallback<Long>) {
        val request: Request = Request.Builder()
            .delete()
            .url("$BASE_URL/api/slow/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object  : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: throw RuntimeException("body is null")
                        callback.onSuccess(gson.fromJson(body, Post::class.java).id)
                    } catch (e: Exception){
                        callback.onError(e)
                    }
                }
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }


    //    override fun save(post: Post) {
//        val request: Request = Request.Builder()
//            .post(gson.toJson(post).toRequestBody(jsonType))
//            .url("$BASE_URL/api/slow/posts")
//            .build()
//
//        client.newCall(request)
//            .execute()
//            .close()
//    }
    override fun saveAsync(post: Post, callback: PostRepository.PostCallback<Unit>) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("$BASE_URL/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        response.body?.string() ?: throw RuntimeException("body is null")
                        callback.onSuccess(Unit)
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.PostCallback<Unit>) {
        val request: Request = Request.Builder()
            .delete()
            .url("$BASE_URL/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        response.body?.string() ?: throw RuntimeException("body is null")
                        callback.onSuccess(Unit)
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

            })
    }
}