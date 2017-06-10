package app.lib

fun getenv(name: String): String? = System.getenv("predictions-matches-api-$name")