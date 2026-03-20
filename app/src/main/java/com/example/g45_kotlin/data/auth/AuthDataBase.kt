package com.example.g45_kotlin.data.auth

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserDto::class], version = 1)
abstract class AuthDataBase : RoomDatabase() {
    abstract fun userDao(): AuthDaoInterface

    companion object {
        @Volatile
        private var INSTANCE: AuthDataBase? = null

        fun getInstance(context: Context): AuthDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AuthDataBase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}