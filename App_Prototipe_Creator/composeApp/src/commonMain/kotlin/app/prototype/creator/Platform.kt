package app.prototype.creator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform