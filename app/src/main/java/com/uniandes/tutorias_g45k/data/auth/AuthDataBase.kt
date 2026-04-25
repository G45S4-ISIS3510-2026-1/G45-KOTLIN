package com.uniandes.tutorias_g45k.data.auth

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.uniandes.tutorias_g45k.data.catalog.CachedTutorEntity
import com.uniandes.tutorias_g45k.data.reservation.CachedSessionEntity

@Database(entities = [UserDto::class, CachedTutorEntity::class, CachedSessionEntity::class], version = 5, exportSchema = false)
@TypeConverters(DataConverters::class)
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
                )
                    .fallbackToDestructiveMigration() // Esto evita el crash al cambiar la versión de la DB
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}
