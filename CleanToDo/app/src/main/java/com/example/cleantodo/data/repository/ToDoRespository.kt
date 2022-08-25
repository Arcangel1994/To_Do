package com.example.cleantodo.data.repository

import androidx.lifecycle.LiveData
import com.example.cleantodo.data.ToDoDao
import com.example.cleantodo.data.models.ToDoData

class ToDoRespository(private val toDoDao: ToDoDao) {

    val getAllData: LiveData<List<ToDoData>> = toDoDao.getAllData()

    suspend fun insertData(toDoData: ToDoData) {
        toDoDao.insertData(toDoData)
    }

    suspend fun updateData(toDoData: ToDoData) {
        toDoDao.updateData(toDoData)
    }

    suspend fun deleteData(toDoData: ToDoData) {
        toDoDao.deleteData(toDoData)
    }

    suspend fun deleteAllData() {
        toDoDao.deleteAllData()
    }

    fun searchDatabase(searchQuery: String) : LiveData<List<ToDoData>> {
        return toDoDao.searchDatabase(searchQuery)
    }

    fun sortByHighPriority() : LiveData<List<ToDoData>> {
        return toDoDao.sortByHighPriority()
    }

    fun sortByLowPriority() : LiveData<List<ToDoData>> {
        return toDoDao.sortByLowPriority()
    }

}