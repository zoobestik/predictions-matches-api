package app

import app.handlers.userHandler
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import org.jetbrains.ktor.application.ApplicationCallPipeline
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.content.TextContent
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.routing.Routing
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.route
import org.jetbrains.ktor.sessions.SessionCookieTransformerMessageAuthentication
import org.jetbrains.ktor.sessions.SessionCookiesSettings
import org.jetbrains.ktor.sessions.withCookieByValue
import org.jetbrains.ktor.sessions.withSessions
import org.jetbrains.ktor.transform.transform
import org.jetbrains.ktor.util.hex
import javax.crypto.Mac.getInstance
import javax.crypto.spec.SecretKeySpec

val hashAlg = "HmacSHA256"

fun timestamp(): Long = System.currentTimeMillis()

fun log(message: String) {
    println("${timestamp()}: $message")
}

class JsonResponse(val data: Any)
data class Session(val userId: String)

fun main(args: Array<String>) {
    embeddedServer(Netty, 8088) {
        log("Starting server...")

        val hashKey = hex("6819b57a326945c1968f45236589")
        val hmacKey = SecretKeySpec(hashKey, hashAlg)

        val gson = GsonBuilder()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .create()

        install(DefaultHeaders)
        install(CallLogging)

        withSessions<Session> {
            withCookieByValue {
                settings = SessionCookiesSettings(transformers = listOf(SessionCookieTransformerMessageAuthentication(hashKey)))
            }
        }

        intercept(ApplicationCallPipeline.Infrastructure) { call ->
//            if (call.request.acceptItems().any { it.value == "application/json" }) {
                call.transform.register<JsonResponse> { value ->
                    TextContent(gson.toJson(value.data), ContentType.Application.Json)
                }
//            }
        }

        install(Routing) {
            route("/api") {
                userHandler()
            }

            post("/login") {

            }
        }

        fun hash(password: String): String {
            val hmac = getInstance(hashAlg)
            hmac.init(hmacKey)
            return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
        }

        log("Server is running...")
    }.start(wait = true)
}
