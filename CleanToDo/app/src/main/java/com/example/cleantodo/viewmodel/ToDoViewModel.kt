package com.example.cleantodo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cleantodo.data.ToDoDao
import com.example.cleantodo.data.ToDoDatabase
import com.example.cleantodo.data.models.ToDoData
import com.example.cleantodo.data.repository.ToDoRespository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoViewModel(application: Application): AndroidViewModel(application) {

    private val toDoDao: ToDoDao = ToDoDatabase.getDatabase(application).toDoDao()

    private val repository: ToDoRespository = ToDoRespository(toDoDao)

    val getAllData: LiveData<List<ToDoData>> = repository.getAllData

    val sortByHighPriority: LiveData<List<ToDoData>> = repository.sortByHighPriority()
    val sortByLowPriority: LiveData<List<ToDoData>> = repository.sortByLowPriority()

    fun insert(toDoData: ToDoData){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(toDoData)
        }
    }

    fun updateData(toDoData: ToDoData){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateData(toDoData)
        }
    }

    fun deleteData(toDoData: ToDoData){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteData(toDoData)
        }
    }

    fun deleteAllData(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllData()
        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<ToDoData>> {
        return repository.searchDatabase(searchQuery)
    }

}