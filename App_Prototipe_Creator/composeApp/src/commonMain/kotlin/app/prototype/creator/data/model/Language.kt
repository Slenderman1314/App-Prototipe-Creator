package app.prototype.creator.data.model

/**
 * Represents a supported language in the application
 */
enum class Language(val code: String, val displayName: String, val nativeName: String) {
    SPANISH("es", "Spanish", "Español"),
    ENGLISH("en", "English", "English");
    
    companion object {
        fun fromCode(code: String): Language {
            return entries.find { it.code == code } ?: SPANISH
        }
    }
}
