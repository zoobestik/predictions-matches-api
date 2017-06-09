package app.lib.responses

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.content.TextContent
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.request.acceptItems
import org.jetbrains.ktor.transform.transform

class JsonResponse(val data: Any)

fun jsonResponse(
        call: ApplicationCall,
        gson: Gson = GsonBuilder()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .create()
) {
//    if (call.request.acceptItems().any { it.value == "application/json" }) {
        call.transform.register<JsonResponse> { value ->
            TextContent(gson.toJson(value.data), ContentType.Application.Json)
        }
//    }
}

fun jsonResponse(gson: Gson): (call: ApplicationCall) -> Unit = { call -> jsonResponse(call, gson) }

