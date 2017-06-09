package app

import app.routes.userHandler
import app.lib.log
import app.lib.responses.jsonResponse
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import org.jetbrains.ktor.application.ApplicationCallPipeline
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.host.ApplicationHost
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.netty.Netty
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

class App constructor(val conf: Configuration) {
    val hashAlg = "HmacSHA256"
    val hashKey = hex(conf.hash)
    val hmacKey = SecretKeySpec(hashKey, hashAlg)

    val gson = GsonBuilder()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create()

    val json = jsonResponse(gson)

    val hmac = Mac.getInstance(hashAlg)

    init {
        hmac.init(hmacKey)
    }

    fun hash(string: String): String {
        return hex(hmac.doFinal(string.toByteArray(Charsets.UTF_8)))
    }

    fun configure(): ApplicationHost {
        return embeddedServer(Netty, conf.port) {
            log("Starting server...")

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

            log("Server is running...")
        }
    }
}