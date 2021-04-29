package com.example.mvvmtask.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.exampleproject.responsemodel.TrendingResponseItem
import com.example.mvvmtask.Repository.HomeRepository
import com.example.mvvmtask.Util.Resource
import kotlinx.coroutines.Dispatchers

class HomeViewModel(private val mainRepository: HomeRepository) : ViewModel() {
    var trendingResponseItem: LiveData<List<TrendingResponseItem>>? = null

    fun insertData(context: Context, list: List<TrendingResponseItem>) {
        mainRepository.insertData(context, list)
    }

    fun getList() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            Log.d("afadfasf", "try");
            emit(Resource.success(data = mainRepository.getList()))
        } catch (exception: Exception) {
            Log.d("afadfasf", "catch");
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getLocalData(context: Context): LiveData<List<TrendingResponseItem>>? {
        trendingResponseItem = mainRepository.getLocalData(context)
        return trendingResponseItem
    }
}