package com.example.plugins

import kotlinx.serialization.Serializable

@Serializable
data class UserSerializationModel(val username: String, val password: String, val chatSessionId: String)