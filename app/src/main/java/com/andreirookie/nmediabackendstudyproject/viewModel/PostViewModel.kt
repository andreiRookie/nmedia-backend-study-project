package com.andreirookie.nmediabackendstudyproject.viewModel


import android.app.Application
import androidx.lifecycle.*
import com.andreirookie.nmediabackendstudyproject.dto.Post
import com.andreirookie.nmediabackendstudyproject.model.FeedModel
import com.andreirookie.nmediabackendstudyproject.repository.*
import com.andreirookie.nmediabackendstudyproject.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "Username",
    likedByMe = false,
    likes = 0,
    published = "",
    authorAvatar = "ava.jpeg",
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty) // private ??
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {

        _data.postValue(FeedModel(loading = true))

        repository.getAll(object : PostRepository.PostCallback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }

        })
    }

    fun save() {
        edited.value?.let {

                repository.save(it,
                object : PostRepository.PostCallback<Post> {
                    override fun onSuccess(value: Post) {
                        _postCreated.postValue(Unit)
                    }

                    override fun onError(e: Exception) {
                        _postCreated.postValue(Unit)
                    }

                })
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
//    fun likeById(id: Long) {
//        thread {
//            val oldPosts = _data.value?.posts.orEmpty()
//
//            _data.postValue(
//                _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                    .map {
//                        if (it.id != id) it else it.copy(
//                            likedByMe = !it.likedByMe,
//                            likes = it.likes + 1
//                        )
//                    })
//            )
//
//            try {
//                val updatedPost = repository.likeById(id)
//                val currentPosts = _data.value?.posts.orEmpty()
//                    .map { if (it.id == id) updatedPost else it }
//                _data.postValue(FeedModel(posts = currentPosts, empty = currentPosts.isEmpty()))
//            } catch (e: IOException) {
//                _data.postValue(_data.value?.copy(posts = oldPosts))
//            }
//        }
//    }
    fun likeById(id: Long) {

            val oldPosts = _data.value?.posts.orEmpty()

//            _data.postValue(
//                _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                    .map {
//                        if (it.id != id) it else it.copy(
//                            likedByMe = !it.likedByMe,
//                            likes = it.likes + 1
//                        )
//                    })
//            )
        repository.likeById(id, object  : PostRepository.PostCallback<Long> {
            override fun onSuccess(id: Long) {
                            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .map {
                        if (it.id != id) it else it.copy(
                            likedByMe = !it.likedByMe,
                            likes = it.likes + 1
                        )
                    })
            )
//                _data.postValue(FeedModel(posts = _data.value?.posts.orEmpty()
//                        .map {
//                            if (it.id != post.id) it else post
//                        })
//                )
            }
            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = oldPosts))
            }
        })
    }

//    fun dislikeById(id: Long) {
//        thread {
//            val oldPosts = _data.value?.posts.orEmpty()
//
//            _data.postValue(
//                _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                    .map {
//                        if (it.id != id) it else it.copy(
//                            likedByMe = !it.likedByMe,
//                            likes = it.likes - 1
//                        )
//                    })
//            )
//
//            try {
//                val updatedPost = repository.dislikeById(id)
//                val currentPosts = _data.value?.posts.orEmpty()
//                    .map { if (it.id == id) updatedPost else it }
//                _data.postValue(FeedModel(posts = currentPosts, empty = currentPosts.isEmpty()))
//            } catch (e: IOException) {
//                _data.postValue(_data.value?.copy(posts = oldPosts))
//            }
//        }
//    }

    fun dislikeById(id: Long) {
        val oldPosts = _data.value?.posts.orEmpty()

//        _data.postValue(
//            _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                .map {
//                    if (it.id != id) it else it.copy(
//                        likedByMe = !it.likedByMe,
//                        likes = it.likes - 1
//                    )
//                })
//        )

        repository.dislikeById(id, object  : PostRepository.PostCallback<Long> {
            override fun onSuccess(id: Long) {
                        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .map {
                    if (it.id != id) it else it.copy(
                        likedByMe = !it.likedByMe,
                        likes = it.likes - 1
                    )
                })
        )




//                _data.postValue(data.value?.copy(posts = _data.value?.posts.orEmpty()
//                    .map {
//                        if (it.id != post.id) it else post
//                    }))
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = oldPosts))            }

        })

}

//    fun removeById(id: Long) {
//        thread {
//            // Оптимистичная модель (подразумеваем, что на сервере всё пройдет хорошо)
//
//            // сохраняем состояние постов до удаления
//            val old = _data.value?.posts.orEmpty()
//
//            //моделируем удаление (для пользователя)
//            _data.postValue(
//                _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                    .filter { it.id != id }
//                )
//            )
//            // на сервере удаляется в фоновом режиме(с задержкой n сек)
//            try {
//                repository.removeById(id)
//            } catch (e: IOException) {
//                _data.postValue(_data.value?.copy(posts = old))
//            }
//        }
//    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        val posts = _data.value?.posts.orEmpty().filter { it.id != id }
        val new = _data.value?.copy(posts = posts, empty = posts.isNullOrEmpty())

        repository.removeById(id,
            object  : PostRepository.PostCallback<Unit> {
            override fun onSuccess(value: Unit) {
                _data.postValue(new)

            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })

    }
}