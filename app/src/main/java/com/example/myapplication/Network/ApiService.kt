package com.example.mvvmtask.Network

import com.example.exampleproject.responsemodel.TrendingResponseItem
import retrofit2.http.GET

interface ApiService {
    @GET("repositories")
    suspend fun getList(): List<TrendingResponseItem>
}