package com.example.plugins

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginSerializationModel(val username: String, val password: String)