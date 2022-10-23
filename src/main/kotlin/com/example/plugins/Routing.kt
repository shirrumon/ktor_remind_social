package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.dao.user.userDao
import com.example.factory.user.AuthTokenFactory
import com.example.models.serializtion.UserLoginSerializationModel
import com.example.plugins.websockets.WebSocketConnection
import com.example.models.serializtion.UserSerializationModel
import com.google.gson.GsonBuilder
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.LinkedHashSet

fun Application.configureRouting() {
    routing {
        post("register"){
            val gsonPretty = GsonBuilder().setPrettyPrinting().create()

            val formParameters = call.receive<UserSerializationModel>()
            val user = userDao.createUser(
                formParameters.username,
                formParameters.password,
                formParameters.name,
                formParameters.surname,
                "",
                "",
            )
            call.respond(gsonPretty.toJson(user))
        }

        post("/login") {
            val userFromRequest = call.receive<UserLoginSerializationModel>()

            val userFromDb = userDao.getUserByUsername(userFromRequest.getUsername())

            if(userFromDb !== null){
                if (!BCrypt.checkpw(userFromRequest.getPassword(), userFromDb.password)){
                    call.respond("password is wrong")
                } else {
                    val token = AuthTokenFactory().createToken(userFromRequest.getUsername())
                    call.respond(hashMapOf("token" to token))
                }
            } else {
                call.respond("user not exist")
            }
        }

        authenticate("auth-jwt") {
            delete("account/delete"){
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()

                userDao.deleteUserByUsername(username)
                call.respondText("Your account was successfully deleted")
            }

            get("/") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val id = principal!!.payload.getClaim("id").asInt()
                call.respondText("Hello, ${id}!")
            }

            val connections = Collections.synchronizedSet<WebSocketConnection?>(LinkedHashSet())
            val sessionsById = ConcurrentHashMap<UserId, WebSocketConnection>()
            webSocket("/chat") {
                println("Adding user!")
                val thisConnection = WebSocketConnection(this)
                connections += thisConnection

                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("id").asInt()
                val currentUser = userDao.getUser(username)

                val userId = currentUser!!.id
                sessionsById[userId] = thisConnection

                val targetSession = call.request.headers.get("target")
                try {
                    send("You are connected! There are ${connections.count()} users here.")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        val firstUserSession = sessionsById[userId]
                        val secondUserSession = sessionsById[targetSession!!.toInt()]
                        val textWithUsername = "[${thisConnection.name}]: $receivedText"
                        if(!targetSession.isEmpty()){
                            firstUserSession!!.session.send(textWithUsername)
                            secondUserSession!!.session.send(textWithUsername)
                        }
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage)
                } finally {
                    println("Removing $thisConnection!")
                    connections -= thisConnection
                }
            }
        }
    }
}

typealias UserId = Int
