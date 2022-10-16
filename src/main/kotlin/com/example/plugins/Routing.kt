package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.WebSocketConnection
import com.example.dao.dao
import com.example.models.User
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.LinkedHashSet

fun Application.configureRouting() {

    routing {
        post("register"){
            val formParameters = call.receive<UserSerializationModel>()
            val user = dao.createNewUser(formParameters.username, formParameters.password, "")
            call.respond("ok")
        }

        post("/login") {
            val user = call.receive<UserSerializationModel>()
            val token = JWT.create()
                .withAudience( "http://0.0.0.0:8080/")
                .withIssuer("http://0.0.0.0:8080/hello")
                .withClaim("username", user.username)
                .withExpiresAt(Date(System.currentTimeMillis() + 999999999999999999))
                .sign(Algorithm.HMAC256("secret"))
            call.respond(hashMapOf("token" to token))
        }

        authenticate("auth-jwt") {
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
                val username = principal!!.payload.getClaim("username").asString()
                val currentUser = dao.user(username)

                val userId = currentUser!!.id
                sessionsById[userId] = thisConnection

                dao.editUserSingle(currentUser.userName, currentUser.password, connections.size.toString())

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
