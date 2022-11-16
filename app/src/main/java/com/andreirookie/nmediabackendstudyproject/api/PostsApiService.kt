package com.andreirookie.nmediabackendstudyproject.api

import com.andreirookie.nmediabackendstudyproject.BuildConfig
import com.andreirookie.nmediabackendstudyproject.dto.Post
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.create
import retrofit2.http.*

private const val BASE_URL = "http://10.0.2.2:9999/api/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create()) // //converter-gson
    .baseUrl(BASE_URL)
    .build()

interface PostsApiService {
    @GET(value = "posts")
    fun getAll(): Call<List<Post>>

    @GET("posts/{postId}")
    fun getById(@Path("postId") id: Long): Call<Post>

    //Для отправки тела запроса есть маркерная аннотация (значит не имеющая элементов) @Body
    @POST(value = "posts")
    fun save(@Body post: Post): Call<Post>

    //если мы не ждём ответа от сервиса, то стоит указывать именно Call<Unit>
    // (тогда сработает Converter, который закроет тело ответа)
    @DELETE(value = "posts/{postId}")
    fun removeById(@Path("postId") id: Long): Call<Unit>

    @POST("posts/{id}/likes")
    fun likeById(@Path("id") id: Long): Call<Post>

    @DELETE("posts/{id}/likes")
    fun dislikeById(@Path("id") id: Long): Call<Post>
}

object PostsApi {
//     val retrofitService : PostsApiService by lazy {
    val retrofitService by lazy {
//        retrofit.create(PostsApiService::class.java)
    retrofit.create<PostsApiService>() //см. import ..create; inline fun <reified T> Retrofit.create(): T = create(T::class.java)
    }
}