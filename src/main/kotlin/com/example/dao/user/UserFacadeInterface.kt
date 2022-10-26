package com.example.dao.user

import com.example.models.*

interface UserFacadeInterface {
    suspend fun getAllUsers(): List<User>
    suspend fun getUser(id: Int): User?
    suspend fun createUser(
        username: String,
        password: String,
        name: String,
        surname: String,
        email: String,
        phoneNumber: String
    ): User?
    suspend fun editUser(
        username: String,
        name: String,
        surname: String,
        email: String,
        phoneNumber: String
    ): Boolean
    suspend fun deleteUserByUsername(username: String): Boolean
    suspend fun getUserByUsername(username: String): User?
}