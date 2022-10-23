package com.example.models.serializtion

import kotlinx.serialization.Serializable

@Serializable
data class UserSerializationModel(
    val username: String,
    val password: String,
    val name: String,
    val surname: String)