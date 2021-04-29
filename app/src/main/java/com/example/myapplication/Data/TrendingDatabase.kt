package com.example.mvvmtask.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.exampleproject.responsemodel.TrendingResponseItem

@Database(entities = [TrendingResponseItem::class], version = 1, exportSchema = false)
abstract class TrendingDatabase : RoomDatabase() {

    abstract fun trendingDao(): TrendingDao

    companion object {
        private var instance: TrendingDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): TrendingDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, TrendingDatabase::class.java,
                    "note_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()

            return instance!!

        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                populateDatabase(instance!!)
            }
        }

        private fun populateDatabase(db: TrendingDatabase) {
            val trendingDao = db.trendingDao()
        }

    }

}