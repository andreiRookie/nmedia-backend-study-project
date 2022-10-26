package com.andreirookie.nmediabackendstudyproject.viewModel


import android.app.Application
import androidx.lifecycle.*
import com.andreirookie.nmediabackendstudyproject.dto.Post
import com.andreirookie.nmediabackendstudyproject.model.FeedModel
import com.andreirookie.nmediabackendstudyproject.repository.*
import com.andreirookie.nmediabackendstudyproject.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        // Начинаем загрузку
        _data.postValue(FeedModel(loading = true))

        repository.getAllAsync(object : PostRepository.GetAllPostsCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }

        })
        // прокидываем значения в FeedModel в _data
        //Мы используем postValue для записи в LiveData
        // с фонового потока, т.к. этот метод выполняет
        // доставку данных в главный поток,
        // в то время как обычный вызов setValue(только на главном потоке)
        // таких преобразований не делает.
    }

    fun save() {
        edited.value?.let {
            thread {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    // Лучший подход, если к серверу единовременно
    // обращается сразу несколько клиентов
    fun likeById(id: Long) {
        thread {
            val oldPosts = _data.value?.posts.orEmpty()

            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .map {
                        if (it.id != id) it else it.copy(
                            likedByMe = !it.likedByMe,
                            likes = it.likes + 1
                        )
                    })
            )

            try {
                val updatedPost = repository.likeById(id)
                val currentPosts = _data.value?.posts.orEmpty()
                    .map { if (it.id == id) updatedPost else it }
                _data.postValue(FeedModel(posts = currentPosts, empty = currentPosts.isEmpty()))
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = oldPosts))
            }
        }
    }

    fun dislikeById(id: Long) {
        thread {
            val oldPosts = _data.value?.posts.orEmpty()

            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .map {
                        if (it.id != id) it else it.copy(
                            likedByMe = !it.likedByMe,
                            likes = it.likes - 1
                        )
                    })
            )

            try {
                val updatedPost = repository.dislikeById(id)
                val currentPosts = _data.value?.posts.orEmpty()
                    .map { if (it.id == id) updatedPost else it }
                _data.postValue(FeedModel(posts = currentPosts, empty = currentPosts.isEmpty()))
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = oldPosts))
            }
        }
    }

    fun removeById(id: Long) {
        thread {
            // Оптимистичная модель (подразумеваем, что на сервере всё пройдет хорошо)

            // сохраняем состояние постов до удаления
            val old = _data.value?.posts.orEmpty()

            //моделируем удаление (для пользователя)
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            // на сервере удаляется в фоновом режиме(с задержкой n сек)
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }
}