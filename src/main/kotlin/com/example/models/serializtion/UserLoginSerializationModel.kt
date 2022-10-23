package com.example.models.serializtion

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginSerializationModel(
    private val username: String,
    private val password: String
    ) {
    fun getUsername(): String {
        return this.username
    }

    fun getPassword(): String {
        return this.password
    }
}