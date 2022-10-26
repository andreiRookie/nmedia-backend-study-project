package com.andreirookie.nmediabackendstudyproject.util


import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T> : MutableLiveData<T>() {

    // флаг - значение было изменено
    private var pending = AtomicBoolean(false)

    @MainThread                           //тут observer - FeedFragment
    override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {
        require (!hasActiveObservers()) {
            error("Multiple observers registered but only one will be notified of changes.")
        }

        //подписываемся на родительский класс(MutableLiveData)
        // нижняя лямбда уйдет на выполнение в базовый super
        // и нет гарантий, что она будет вызвана в главном потоке
        super.observe(owner) {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }
}