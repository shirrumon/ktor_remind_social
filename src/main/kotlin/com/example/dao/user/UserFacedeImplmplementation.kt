package com.example.dao.user

import com.example.dao.DatabaseFactory
import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.User
import com.example.models.Users
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.mindrot.jbcrypt.BCrypt

class UserFacadeImplementation: UserFacadeInterface {
    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        userName = row[Users.username],
        password = row[Users.password],
        name = row[Users.name],
        surname = row[Users.surname],
        email = row[Users.email],
        phoneNumber = row[Users.phoneNumber]
    )

    override suspend fun getAllUsers(): List<User> = DatabaseFactory.dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun getUser(id: Int): User? = DatabaseFactory.dbQuery {
        Users
            .select { Users.id eq id }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun createUser(
        username: String,
        password: String,
        name: String,
        surname: String,
        email: String,
        phoneNumber: String
    ): User? =
        DatabaseFactory.dbQuery {
            val insertStatement = Users.insert {
                it[Users.username] = username
                it[Users.name] = name
                it[Users.surname] = surname
                it[Users.email] = email
                it[Users.phoneNumber] = phoneNumber
                it[Users.password] = BCrypt.hashpw(password, BCrypt.gensalt())
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
        }

    override suspend fun editUser(
        username: String,
        name: String,
        surname: String,
        email: String,
        phoneNumber: String
    ): Boolean =
        DatabaseFactory.dbQuery {
            Users.update({ Users.username eq username }) {
                it[Users.name] = name
                it[Users.surname] = surname
                it[Users.email] = email
                it[Users.phoneNumber] = phoneNumber
            } > 0
        }

    override suspend fun getUserByUsername(username: String): User? = DatabaseFactory.dbQuery {
        Users
            .select { Users.username eq username }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun deleteUserByUsername(username: String): Boolean = dbQuery {
        Users.deleteWhere { Users.username eq username } > 0
    }
}

val userDao: UserFacadeInterface = UserFacadeImplementation().apply {
    runBlocking {
//        if (allUsers().isEmpty()) {
//            createNewUser("test", "test", "asdasdasdasdasd")
//        }
    }
}