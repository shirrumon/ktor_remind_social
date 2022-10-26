package com.example.models.serializtion

import kotlinx.serialization.Serializable

@Serializable
data class UserEditSerializationModel(
    private val name: String,
    private val surname: String,
    private val email: String,
    private val phoneNumber: String
) {
    fun getName(): String{
        return this.name
    }

    fun getSurname(): String{
        return this.surname
    }

    fun getEmail(): String{
        return this.email
    }

    fun getPhoneNumber(): String{
        return this.phoneNumber
    }
}