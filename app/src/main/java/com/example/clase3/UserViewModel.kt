package com.example.clase3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    fun insert(user: User) = viewModelScope.launch {
        userDao.insertUser(user)
    }

    fun update(user: User) = viewModelScope.launch {
        userDao.updateUser(user)
    }

    fun delete(user: User) = viewModelScope.launch {
        userDao.deleteUser(user)
    }

    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }
}
