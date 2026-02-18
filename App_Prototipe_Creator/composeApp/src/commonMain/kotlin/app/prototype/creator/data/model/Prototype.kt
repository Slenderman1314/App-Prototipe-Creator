@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package app.prototype.creator.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Prototype(
    val id: String,
    val name: String,
    val description: String,
    val previewUrl: String,
    val htmlContent: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val tags: List<String>,
    val isFavorite: Boolean,
    val userIdea: String?,
    val validationNotes: String?
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
        htmlContent = null,
        createdAt = 0L,
        updatedAt = 0L,
        tags = listOf("ecommerce", "shopping", "mobile"),
        isFavorite = false,
        userIdea = null,
        validationNotes = null
    ),
    Prototype(
        id = "2",
        name = "Social Network",
        description = "A social media platform",
        previewUrl = "https://example.com/prototypes/social",
        htmlContent = null,
        createdAt = 0L,
        updatedAt = 0L,
        tags = listOf("social", "networking", "web"),
        isFavorite = false,
        userIdea = null,
        validationNotes = null
    ),
    Prototype(
        id = "3",
        name = "Task Manager",
        description = "Productivity app for managing tasks",
        previewUrl = "https://example.com/prototypes/tasks",
        htmlContent = null,
        createdAt = 0L,
        updatedAt = 0L,
        tags = listOf("productivity", "tasks", "mobile"),
        isFavorite = false,
        userIdea = null,
        validationNotes = null
    )
)
