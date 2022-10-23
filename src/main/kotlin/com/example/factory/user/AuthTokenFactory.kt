package com.example.factory.user

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class AuthTokenFactory {
    fun createToken(username: String): Any {
        return JWT.create()
            .withAudience( "http://0.0.0.0:8080/")
            .withIssuer("http://0.0.0.0:8080/hello")
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + 999999999999999999))
            .sign(Algorithm.HMAC256("e845d28de1b5488fbb82e064ee7d0b40"))
    }
}