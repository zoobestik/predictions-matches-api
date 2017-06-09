package app.lib

fun timestamp() = System.currentTimeMillis()

fun log(message: String) = println("${timestamp()}: $message")

fun getenv(name: String): String? = System.getenv("predictions-matches-api-$name")