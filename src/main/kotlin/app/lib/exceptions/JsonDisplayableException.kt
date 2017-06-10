package app.lib.exceptions

open class JsonDisplayableException() : Exception()

class JsonApiArgumentMissing(message: String) : JsonDisplayableException()
