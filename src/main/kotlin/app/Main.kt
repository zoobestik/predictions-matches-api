package app

import app.lib.getenv
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.netty.Netty

fun Application.module() {
    App(this)
}

fun main(args: Array<String>) {
    val port = getenv("port")?.toInt() ?: 8088

    embeddedServer(Netty, port, reloadPackages = listOf("app"), module = Application::module)
            .start(wait = true)
}
