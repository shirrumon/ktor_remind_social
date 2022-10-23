package com.example.models

import org.jetbrains.exposed.sql.*

data class User(
    val id: Int,
    val userName: String,
    val password: String,
    val name: String,
    val surname: String,
    val email: String,
    val phoneNumber: String
    )

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 128)
    val name = varchar("name", 256)
    val surname = varchar("surname", 256)
    val email = varchar("email", 256)
    val phoneNumber = varchar("phone_number", 60)
    val password = varchar("password", 256)

    override val primaryKey = PrimaryKey(username)
}