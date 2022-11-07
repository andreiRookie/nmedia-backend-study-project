package com.andreirookie.nmediabackendstudyproject.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.andreirookie.nmediabackendstudyproject.R
import com.andreirookie.nmediabackendstudyproject.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit



// Third version: via Glide

class MainActivity : AppCompatActivity() {

    private val urls = listOf("netology.jpg", "sber.jpg", "tcs.jpg", "404.png")
    private var index = 0

    private val client = OkHttpClient.Builder()
        .connectTimeout(10,TimeUnit.SECONDS)
        .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.load.setOnClickListener {
            if (index == urls.size) index = 0

            val url = "http://10.0.2.2:9999/avatars/${urls[index++]}"
            println("Loading: $url")

            Picasso.get()
                .load(url)
                    // vector placeholders not supported
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .into(binding.image)



            //указываем Context, с которым работаем
          //  Glide.with(binding.image) - из view сам вытащит контекст
//            Glide.with(this)
//                .load(url) // передаем адрес картинки
//                .placeholder(R.drawable.ic_loading_100dp) //пикча в момент загрузки
//                .error(R.drawable.ic_error_100dp)
//                // glide по умолчанию не ждёт, поэтому нужно прописать timeout
//                .timeout(10_000)
//                .into(binding.image)

                //картинки кешируются

        }

    }

}

// Second vers: метод не оч хороший, размер сообщения ограничен ~ 1Mb
// в Bundle'ы картинки записывать нельзя
//class MainActivity : AppCompatActivity() {
//
//    private val urls = listOf("netology.jpg", "sber.jpg", "tcs.jpg", "404.png")
//    private var index = 0
//
//    private val client = OkHttpClient.Builder()
//        .connectTimeout(10,TimeUnit.SECONDS)
//        .build()
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // handler для looper'а главного потока
//        val handler = Handler(Looper.getMainLooper()) { message ->
//            //demo
//            val bitmap = message.data["image"] as Bitmap?
//            bitmap.let { binding.image.setImageBitmap(bitmap)}
//            println("Handler(Looper.getMainLooper()){}: image set")
//
//            return@Handler true
//        }
//
//        binding.load.setOnClickListener {
//            if (index == urls.size) {
//                index = 0
//            }
//
//
//            val url = "http://10.0.2.2:9999/avatars/${urls[index++]}"
//            println("Loading: $url")
//            // запрос на сервер
//            val request = Request.Builder()
//                .url(url)
//                .build()
//
//            client.newCall(request)
//           // тут из главного уходим в фоновый поток через enqueue()
//                .enqueue(object : Callback {
//                    override fun onResponse(call: Call, response: Response) {
//                        response.body?.use { responseBody ->
//
//                            println("loaded: $url")
//                            println(responseBody.contentType())
//                            println(responseBody.contentLength())
//
//                            val bitmap = BitmapFactory.decodeStream(responseBody.byteStream())
//
//                            // более простой способ отправить в UiThread
////                            this@MainActivity.runOnUiThread {
////                                bitmap?.let {binding.image.setImageBitmap(bitmap)}
////                                println("this@MainActivity.runOnUiThread: image set")
////                            }
//
//                            val message = handler.obtainMessage().apply {
//                                data = bundleOf("image" to bitmap)
//                            }
//                            //отправляем картинку в handler нашей activity, главный поток
//                            handler.sendMessage(message)
//                        }
//                    }
//
//                    override fun onFailure(call: Call, e: IOException) {
//                        e.printStackTrace()
//                    }
//                })
//        }
//    }
//}



/*  First version: getting images without imageView bindings

class MainActivity : AppCompatActivity() {

    private val urls = listOf("netology.jpg", "sber.jpg", "tcs.jpg", "404.png")
    private var index = 0

    private val worker = WorkerThread().apply { start() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.load.setOnClickListener {
            if (index == urls.size) {
                index = 0
            }

            worker.download("http://10.0.2.2:9999/avatars/${urls[index++]}")
        }
    }
}

class WorkerThread : Thread() {

    private lateinit var handler: Handler

    private val client = OkHttpClient.Builder().build()

    fun download(url: String) {
        println("passed to queue: $url")

        // вариант создания Message
        val message = Message.obtain(handler).apply { data = bungleOf("url" to url)}

        // вариант создания Message
        val message = handler.obtainMessage().apply {
            this.data = bundleOf("url" to url)
        }
        handler.sendMessage(message)
    }

    override fun run() {
        Looper.prepare()
        // prepare() записываетэкземпляр Looper в sThreadLocal
       // static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>()
       //ThreadLocal — это переменные, копии которых уникальны для каждого потока.
       //Т.е. несмотря на то, что поля static final, у каждого потока будет своя,
      // независимая от других потоков копия переменной
       //(они не будут пересекаться, это не Shared State).

        // just for demo: dont use !! & as
        handler = Handler(Looper.myLooper()!!) { message ->

            //Looper.myLooper() returns the Looper obj associated with the current thread
            // or null if thread isn't associated with a looper

            try {
                val url = message.data["url"] as String

                println("loaading: $url")

                val request = Request.Builder()
                    .url(url)
                    .build()

                client.newCall(request)
                // уже в фоновом потоке, поэтому enqueue необязательно
                    .execute()
                    .body?.use { responseBody ->
                        println("loaded: $url")
                        println(responseBody.contentType())
                        println(responseBody.contentLength())
                    }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return@Handler true
        }
        Looper.loop()
    }

}
*/