package com.example.mvvmtask.Repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.exampleproject.responsemodel.TrendingResponseItem
import com.example.mvvmtask.Data.TrendingDatabase
import com.example.mvvmtask.Network.ApiHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class HomeRepository(private val apiHelper: ApiHelper) {

    var trendingDatabase: TrendingDatabase? = null
    var trendingResponseItem: LiveData<List<TrendingResponseItem>>? = null

    fun initializeDB(context: Context): TrendingDatabase {
        return TrendingDatabase.getInstance(context)
    }

    suspend fun getList() = apiHelper.getList()

    fun insertData(context: Context, list: List<TrendingResponseItem>) {
        trendingDatabase = initializeDB(context)
        CoroutineScope(IO).launch {
            trendingDatabase!!.trendingDao().insertTrendingList(list)
        }

    }

    fun getLocalData(context: Context): LiveData<List<TrendingResponseItem>>? {

        trendingDatabase = initializeDB(context)

        trendingResponseItem = trendingDatabase!!.trendingDao().getAllTrendingList()

        return trendingResponseItem
    }
}