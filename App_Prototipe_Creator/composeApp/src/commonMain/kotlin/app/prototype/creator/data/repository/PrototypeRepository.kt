package app.prototype.creator.data.repository

import app.prototype.creator.data.model.Prototype
import kotlinx.coroutines.flow.Flow

interface PrototypeRepository {
    /**
     * Get all prototypes
     * @return Flow of list of prototypes
     */
    fun getPrototypes(): Flow<List<Prototype>>
    
    /**
     * Get a prototype by ID
     * @param id The ID of the prototype to retrieve
     * @return Flow of the requested prototype
     */
    fun getPrototypeById(id: String): Flow<Prototype>
    
    /**
     * Create a new prototype
     * @param prototype The prototype to create
     */
    suspend fun createPrototype(prototype: Prototype)
    
    /**
     * Update an existing prototype
     * @param prototype The prototype with updated values
     */
    suspend fun updatePrototype(prototype: Prototype)
    
    /**
     * Delete a prototype by ID
     * @param id The ID of the prototype to delete
     */
    suspend fun deletePrototype(id: String)
}

/**
 * In-memory implementation of PrototypeRepository for demo purposes
 * In a real app, this would be replaced with a database implementation
 */
class InMemoryPrototypeRepository : PrototypeRepository {
    private val prototypes = mutableListOf<Prototype>()
    
    override fun getPrototypes(): Flow<List<Prototype>> {
        return kotlinx.coroutines.flow.flow { emit(prototypes.sortedByDescending { it.updatedAt }) }
    }
    
    override fun getPrototypeById(id: String): Flow<Prototype> {
        return kotlinx.coroutines.flow.flow { 
            val prototype = prototypes.find { it.id == id }
            if (prototype != null) {
                emit(prototype)
            }
        }
    }
    
    override suspend fun createPrototype(prototype: Prototype) {
        prototypes.add(prototype.copy(
            updatedAt = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis()
        ))
    }
    
    override suspend fun updatePrototype(prototype: Prototype) {
        val existingIndex = prototypes.indexOfFirst { it.id == prototype.id }
        if (existingIndex != -1) {
            prototypes[existingIndex] = prototype.copy(updatedAt = System.currentTimeMillis())
        }
    }
    
    override suspend fun deletePrototype(id: String) {
        prototypes.removeIf { it.id == id }
    }
}
