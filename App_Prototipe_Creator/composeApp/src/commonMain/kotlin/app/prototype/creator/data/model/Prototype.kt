package app.prototype.creator.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Prototype(
    val id: String,
    val name: String,
    val description: String = "",
    val previewUrl: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false
) {
    val createdDate: String
        get() = createdAt.toString()
        
    val updatedDate: String
        get() = updatedAt.toString()
}

// Sample data for previews and testing
val samplePrototypes = listOf(
    Prototype(
        id = "1",
        name = "E-commerce App",
        description = "A modern e-commerce application",
        previewUrl = "https://example.com/prototypes/ecommerce",
        tags = listOf("ecommerce", "shopping", "mobile")
    ),
    Prototype(
        id = "2",
        name = "Social Network",
        description = "A social media platform",
        previewUrl = "https://example.com/prototypes/social",
        tags = listOf("social", "networking", "web")
    ),
    Prototype(
        id = "3",
        name = "Task Manager",
        description = "Productivity app for managing tasks",
        previewUrl = "https://example.com/prototypes/tasks",
        tags = listOf("productivity", "tasks", "mobile")
    )
)
