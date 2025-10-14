package app.prototype.creator.data.repository

import app.prototype.creator.data.model.Prototype
import app.prototype.creator.data.service.SupabaseService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SupabasePrototypeRepository(
    private val supabaseService: SupabaseService
) : PrototypeRepository {

    override fun getPrototypes(): Flow<List<Prototype>> = flow {
        try {
            emit(emptyList()) // Show loading state
            
            val result = supabaseService.listPrototypes()
            
            result.fold(
                onSuccess = { prototypes ->
                    // Check favorites for each prototype
                    val prototypesWithFavorites = prototypes.map { prototype ->
                        val isFavorite = supabaseService.isFavorite(prototype.id)
                        prototype.copy(isFavorite = isFavorite)
                    }
                    
                    emit(prototypesWithFavorites)
                    Napier.d("✅ Successfully loaded ${prototypesWithFavorites.size} prototypes from Supabase")
                },
                onFailure = { error ->
                    Napier.e("❌ Error loading prototypes from Supabase", error)
                    emit(emptyList()) // Emit empty list on error
                }
            )
            
        } catch (e: Exception) {
            val errorMsg = "❌ Error loading test prototypes: ${e.message}"
            Napier.e(errorMsg, e)
            emit(emptyList())
        }
    }

    override fun getPrototypeById(id: String): Flow<Prototype> = flow {
        try {
            val result = supabaseService.getPrototype(id)
            result.fold(
                onSuccess = { prototype ->
                    emit(prototype)
                },
                onFailure = { error ->
                    Napier.e("Failed to load prototype with id: $id", error)
                    throw error
                }
            )
        } catch (e: Exception) {
            Napier.e("Exception while loading prototype with id: $id", e)
            throw e
        }
    }

    override suspend fun createPrototype(prototype: Prototype) {
        try {
            val result = supabaseService.savePrototype(prototype)
            result.fold(
                onSuccess = { savedPrototype ->
                    Napier.d("Successfully created prototype with id: ${savedPrototype.id}")
                },
                onFailure = { error ->
                    Napier.e("Failed to create prototype", error)
                    throw error
                }
            )
        } catch (e: Exception) {
            Napier.e("Exception while creating prototype", e)
            throw e
        }
    }

    override suspend fun updatePrototype(prototype: Prototype) {
        try {
            val result = supabaseService.savePrototype(prototype)
            result.fold(
                onSuccess = { updatedPrototype ->
                    Napier.d("Successfully updated prototype with id: ${updatedPrototype.id}")
                },
                onFailure = { error ->
                    Napier.e("Failed to update prototype with id: ${prototype.id}", error)
                    throw error
                }
            )
        } catch (e: Exception) {
            Napier.e("Exception while updating prototype with id: ${prototype.id}", e)
            throw e
        }
    }

    override suspend fun deletePrototype(id: String) {
        try {
            val result = supabaseService.deletePrototype(id)
            result.fold(
                onSuccess = {
                    Napier.d("Successfully deleted prototype with id: $id")
                },
                onFailure = { error ->
                    Napier.e("Failed to delete prototype with id: $id", error)
                    throw error
                }
            )
        } catch (e: Exception) {
            Napier.e("Exception while deleting prototype with id: $id", e)
            throw e
        }
    }
}
