package com.andreirookie.nmediabackendstudyproject.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.andreirookie.nmediabackendstudyproject.dao.PostDao
import com.andreirookie.nmediabackendstudyproject.entity.PostEntity


@Database(entities = [PostEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        // ленивая инициализация
        // double-checked locking (многопоточная инициализация синглтона)
//      1.  Сначала проверяется, инициализирована ли переменная (без получения блокировки).
//      Если она инициализирована, её значение возвращается немедленно.  return instance
//      2.  Получение блокировки.  ?: synchronized(this) {
//      3.  Повторно проверяется, инициализирована ли переменная,
//      так как вполне возможно, что после первой проверки другой поток инициализировал переменную.
//      Если она инициализирована, её значение возвращается. instance ?:
//      4.  В противном случае, переменная инициализируется и возвращается. buildDatabase(context).also { instance = it }
//       !! корреткно работмает только с модификатором\аннотацией volatile на инициализируемую переменную,
//        чтобы переменная не кешировалась в потоке!!
        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
    }
}