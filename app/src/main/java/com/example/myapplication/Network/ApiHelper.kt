package com.example.mvvmtask.Network

class ApiHelper(private val apiService: ApiService) {

    suspend fun getList() = apiService.getList()
}