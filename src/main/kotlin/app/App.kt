package app

import app.lib.exceptions.HashIllegalConfiguration
import app.lib.getenv
import app.lib.responses.jsonResponse
import app.routes.userHandler
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.ApplicationCallPipeline
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.routing.Routing
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.route
import org.jetbrains.ktor.sessions.SessionCookieTransformerMessageAuthentication
import org.jetbrains.ktor.sessions.SessionCookiesSettings
import org.jetbrains.ktor.sessions.withCookieByValue
import org.jetbrains.ktor.sessions.withSessions
import org.jetbrains.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

data class Session(val userId: String)

class App(val application: Application) {
    val hashAlg = "HmacSHA256"
    val hash = getenv("hash") ?: throw HashIllegalConfiguration()
    val hashKey = hex(hash)
    val hmacKey = SecretKeySpec(hashKey, hashAlg)

    val gson = GsonBuilder()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create()

    val json = jsonResponse(gson)

    val hmac = Mac.getInstance(hashAlg)

    fun hash(string: String): String {
        return hex(hmac.doFinal(string.toByteArray(Charsets.UTF_8)))
    }

    init {
        hmac.init(hmacKey)

        application.apply {
            install(DefaultHeaders)
            install(CallLogging)

            withSessions<Session> {
                withCookieByValue {
                    settings = SessionCookiesSettings(transformers = listOf(SessionCookieTransformerMessageAuthentication(hashKey)))
                }
            }

            intercept(ApplicationCallPipeline.Infrastructure) { call -> json(call) }

            install(Routing) {
                route("/api") {
                    userHandler(this)
                }

                route("/site") {
                    post("/login") {
                        throw Exception("Not Implemented")
                    }
                }
            }
        }
    }
}