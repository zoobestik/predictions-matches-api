package app.routes

import app.Session
import app.lib.exceptions.JsonApiArgumentMissing
import app.lib.responses.JsonResponse
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.sessions.sessionOrNull

data class UserInfo(val id: String)

fun userHandler(router: Route) {
    router.apply {
        get("/user/{id?}") {
            val queryId = call.parameters["id"]

            val id: String = when {
                queryId != null -> queryId
                else -> call.sessionOrNull<Session>()?.userId ?: throw JsonApiArgumentMissing("userId")
            }

            call.respond(JsonResponse(UserInfo(id)))
        }
    }
}
