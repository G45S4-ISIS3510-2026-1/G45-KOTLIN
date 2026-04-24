package com.example.g45_kotlin.data.recommendation

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TutorSummaryDto::class], version = 1)
abstract class RecommendationDatabase : RoomDatabase() {
    abstract fun recommendationDao(): RecommendedTutorDao

    companion object {
        @Volatile
        private var INSTANCE: RecommendationDatabase? = null

        fun getInstance(context: Context?=null): RecommendationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context!!.applicationContext,
                    RecommendationDatabase::class.java,
                    "recommended_tutors"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}