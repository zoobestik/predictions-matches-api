package app

import app.lib.exceptions.HashIllegalConfiguration
import app.lib.getenv

data class Configuration(val port: Int, val hash: String)
data class Session(val userId: String)

fun main(args: Array<String>) {
    val conf = Configuration(
            port = getenv("port")?.toInt() ?: 8088,
            hash = getenv("hash") ?: throw HashIllegalConfiguration()
    )

    App(conf)
            .configure()
            .start(wait = true)
}
