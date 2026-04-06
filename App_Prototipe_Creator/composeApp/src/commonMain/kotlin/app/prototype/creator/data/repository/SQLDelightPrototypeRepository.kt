package app.prototype.creator.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.prototype.creator.data.model.Prototype
import app.prototype.creator.db.AppDatabase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * SQLDelight implementation of PrototypeRepository
 * Stores prototypes in local SQLite database
 */
class SQLDelightPrototypeRepository(
    private val database: AppDatabase
) : PrototypeRepository {
    
    private val queries = database.prototypeQueries
    
    override fun getPrototypes(): Flow<List<Prototype>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbPrototypes ->
                dbPrototypes.map { it.toDomain() }
            }
            .catch { e ->
                Napier.e("Error loading prototypes from database", e)
                emit(emptyList())
            }
    }
    
    override fun getPrototypeById(id: String): Flow<Prototype> {
        return queries.selectById(id)
            .asFlow()
            .mapToOne(Dispatchers.IO)
            .map { it.toDomain() }
            .catch { e ->
                Napier.e("Error loading prototype $id from database", e)
                throw e
            }
    }
    
    override suspend fun createPrototype(prototype: Prototype) {
        withContext(Dispatchers.IO) {
            try {
                queries.insert(
                    id = prototype.id,
                    name = prototype.name,
                    description = prototype.description,
                    previewUrl = prototype.previewUrl,
                    htmlContent = prototype.htmlContent,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    tags = prototype.tags.joinToString(","),
                    isFavorite = if (prototype.isFavorite) 1L else 0L,
                    userIdea = prototype.userIdea,
                    validationNotes = prototype.validationNotes,
                    language = prototype.language
                )
                Napier.d("✅ Prototype created: ${prototype.name}")
            } catch (e: Exception) {
                Napier.e("❌ Error creating prototype", e)
                throw e
            }
        }
    }
    
    override suspend fun updatePrototype(prototype: Prototype) {
        withContext(Dispatchers.IO) {
            try {
                queries.update(
                    name = prototype.name,
                    description = prototype.description,
                    previewUrl = prototype.previewUrl,
                    htmlContent = prototype.htmlContent,
                    updatedAt = System.currentTimeMillis(),
                    tags = prototype.tags.joinToString(","),
                    isFavorite = if (prototype.isFavorite) 1L else 0L,
                    userIdea = prototype.userIdea,
                    validationNotes = prototype.validationNotes,
                    language = prototype.language,
                    id = prototype.id
                )
                Napier.d("✅ Prototype updated: ${prototype.name}")
            } catch (e: Exception) {
                Napier.e("❌ Error updating prototype", e)
                throw e
            }
        }
    }
    
    override suspend fun deletePrototype(id: String) {
        withContext(Dispatchers.IO) {
            try {
                queries.deleteById(id)
                Napier.d("✅ Prototype deleted: $id")
            } catch (e: Exception) {
                Napier.e("❌ Error deleting prototype", e)
                throw e
            }
        }
    }
    
    /**
     * Toggle favorite status of a prototype
     */
    suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                queries.updateFavorite(
                    isFavorite = if (isFavorite) 1L else 0L,
                    updatedAt = System.currentTimeMillis(),
                    id = id
                )
                Napier.d("✅ Prototype favorite toggled: $id -> $isFavorite")
            } catch (e: Exception) {
                Napier.e("❌ Error toggling favorite", e)
                throw e
            }
        }
    }
    
    /**
     * Get all favorite prototypes
     */
    fun getFavorites(): Flow<List<Prototype>> {
        return queries.selectFavorites()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbPrototypes ->
                dbPrototypes.map { it.toDomain() }
            }
            .catch { e ->
                Napier.e("Error loading favorites from database", e)
                emit(emptyList())
            }
    }
    
    /**
     * Search prototypes by name
     */
    fun searchByName(query: String): Flow<List<Prototype>> {
        return queries.selectByName(query)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbPrototypes ->
                dbPrototypes.map { it.toDomain() }
            }
            .catch { e ->
                Napier.e("Error searching prototypes", e)
                emit(emptyList())
            }
    }
}

/**
 * Extension function to convert database model to domain model
 */
private fun app.prototype.creator.db.Prototype.toDomain(): Prototype {
    return Prototype(
        id = id,
        name = name,
        description = description,
        previewUrl = previewUrl,
        htmlContent = htmlContent,
        createdAt = createdAt,
        updatedAt = updatedAt,
        tags = if (tags.isBlank()) emptyList() else tags.split(","),
        isFavorite = isFavorite == 1L,
        userIdea = userIdea,
        validationNotes = validationNotes,
        language = language
    )
}
