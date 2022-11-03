package com.andreirookie.nmediabackendstudyproject.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.andreirookie.nmediabackendstudyproject.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

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

    private val client = OkHttpClient.Builder()
        .connectTimeout(10,TimeUnit.SECONDS)
        .build()

    fun download(url: String) {
        println("passed to queue: $url")

        val message = handler.obtainMessage().apply {
            this.data = bundleOf("url" to url)
        }
        handler.sendMessage(message)
    }

    override fun run() {
        Looper.prepare()

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