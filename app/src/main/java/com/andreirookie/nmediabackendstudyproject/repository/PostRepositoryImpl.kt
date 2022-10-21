package com.andreirookie.nmediabackendstudyproject.repository


import com.andreirookie.nmediabackendstudyproject.dto.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit


class PostRepositoryImpl: PostRepository {
    // инстанс класса ОкХттпКлиент, делается через спецкласс Билдер
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        // ip адрес эмулятора на компе - http://10.0.2.2
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
                // api/posts - адрес запроса
                //вставка slow - задержка 5 сек для тестиррвоания методов соединения
            //посмотреть как работает прилоожение, если медленный интернет
            // запрос get - по умолчанию, не пишется
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)//ньюКолл формирует запрос
            .execute()//запускает запрос
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    // Лучший подход, если к серверу единовременно
    // обращается сразу несколько клиентов
    override fun likeById(id: Long): Post {
        val request: Request = Request.Builder()
            .post("".toRequestBody())
            .url("$BASE_URL/api/slow/posts/$id/likes")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            // можно без тайптокена,т.к. Post обычный класс,
            // а не интерфейс/сложная коллекция
            .let {
                gson.fromJson(it, Post::class.java)
            }
    }

    override fun dislikeById(id: Long): Post {
        val request: Request = Request.Builder()
            .delete()
            .url("$BASE_URL/api/slow/posts/$id/likes")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, Post::class.java)
            }
    }


    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("$BASE_URL/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("$BASE_URL/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}