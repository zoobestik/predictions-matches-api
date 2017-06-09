package app.handlers

import app.JsonResponse
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.get

data class UserInfo(val id: String)

fun getUserId(): String {
    return "0"
}

fun Route.userHandler() {
    get("/user/{id?}") {
        val id = call.parameters["id"] ?: getUserId()
        val user = UserInfo(id)
        call.respond(JsonResponse(user))
    }
}
