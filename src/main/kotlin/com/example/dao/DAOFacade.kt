package com.example.dao

import com.example.models.*

interface DAOFacade {
    suspend fun allUsers(): List<User>
    suspend fun user(userName: String): User?
    suspend fun createNewUser(userName: String, password: String, chatSessionId: String): User?
    suspend fun editUserSingle(userName: String, password: String, chatSessionId: String): Boolean
    suspend fun deleteUser(id: Int): Boolean
}