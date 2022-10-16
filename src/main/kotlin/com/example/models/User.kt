package com.example.models

import org.jetbrains.exposed.sql.*

data class User(val id: Int, val userName: String, val password: String, val chatSessionId: String)

object Users : Table() {
    val id = integer("id").autoIncrement()
    val userName = varchar("username", 128)
    val password = varchar("password", 256)
    val chatSessionId = varchar("chat_session_id", 512)

    override val primaryKey = PrimaryKey(userName)
}