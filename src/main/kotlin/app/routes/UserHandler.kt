package app.routes

import app.lib.responses.JsonResponse
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.get

data class UserInfo(val id: String)

fun getUserId(): String {
    return "0"
}

fun userHandler(router: Route) {
    router.apply {
        get("/user/{id?}") {
            val id = call.parameters["id"] ?: getUserId()
            val user = UserInfo(id)
            call.respond(JsonResponse(user))
        }
    }
}
