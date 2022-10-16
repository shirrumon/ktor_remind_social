package com.example.dao

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.*
import com.example.models.Users.userName
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*

class DAOFacadeImpl : DAOFacade {
    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        userName = row[Users.userName],
        password = row[Users.password],
        chatSessionId = row[Users.chatSessionId],
    )

    override suspend fun allUsers(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun user(userName: String): User? = dbQuery {
        Users
            .select { Users.userName eq userName }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun createNewUser(userName: String, password: String, chatSessionId: String): User? =
        dbQuery {
            val insertStatement = Users.insert {
                it[Users.userName] = userName
                it[Users.password] = password
                it[Users.chatSessionId] = chatSessionId
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
        }

    override suspend fun editUserSingle(userName: String, password: String, chatSessionId: String): Boolean =
        dbQuery {
            Users.update({ Users.userName eq userName }) {
                it[Users.userName] = userName
                it[Users.password] = password
                it[Users.chatSessionId] = chatSessionId
            } > 0
        }

    override suspend fun deleteUser(id: Int): Boolean {
        TODO("Not yet implemented")
    }
//
//    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
//        UserModels.deleteWhere { UserModels.id eq id } > 0
//    }
}

val dao: DAOFacade = DAOFacadeImpl().apply {
    runBlocking {
//        if (allUsers().isEmpty()) {
//            createNewUser("test", "test", "asdasdasdasdasd")
//        }
    }
}