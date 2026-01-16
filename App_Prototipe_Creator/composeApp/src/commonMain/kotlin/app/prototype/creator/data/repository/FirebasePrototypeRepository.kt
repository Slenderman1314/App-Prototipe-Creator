package app.prototype.creator.data.repository

import app.prototype.creator.data.model.Prototype
import app.prototype.creator.data.service.FirebaseService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Firebase implementation of PrototypeRepository
 * 
 * This repository uses Firebase Firestore as the backend for cloud storage.
 * All operations are performed against the cloud database.
 */
class FirebasePrototypeRepository(
    private val firebaseService: FirebaseService
) : PrototypeRepository {

    override fun getPrototypes(): Flow<List<Prototype>> = flow {
        try {
            // Emit empty list initially (loading state)
            emit(emptyList())
            
            val result = firebaseService.listPrototypes()
            
            result.fold(
                onSuccess = { prototypes ->
                    emit(prototypes)
                    Napier.d("✅ Successfully loaded ${prototypes.size} prototypes from Firebase")
                },
                onFailure = { error ->
                    Napier.e("❌ Error loading prototypes from Firebase", error)
                    emit(emptyList()) // Emit empty list on error
                }
            )
            
        } catch (e: Exception) {
            Napier.e("❌ Exception loading prototypes from Firebase: ${e.message}", e)
            emit(emptyList())
        }
    }

    override fun getPrototypeById(id: String): Flow<Prototype> = flow {
        try {
            val result = firebaseService.getPrototype(id)
            result.fold(
                onSuccess = { prototype ->
                    emit(prototype)
                    Napier.d("✅ Successfully loaded prototype: ${prototype.name}")
                },
                onFailure = { error ->
                    Napier.e("❌ Failed to load prototype with id: $id", error)
                    throw error
                }
            )
        } catch (e: Exception) {
            Napier.e("❌ Exception while loading prototype with id: $id", e)
            throw e
        }
    }

    override suspend fun createPrototype(prototype: Prototype) {
        try {
            val result = firebaseService.savePrototype(prototype)
            result.fold(
                onSuccess = { savedPrototype ->
                    Napier.d("✅ Successfully created prototype: ${savedPrototype.name}")
                },
                onFailure = { error ->
                    Napier.e("❌ Failed to create prototype", error)
                    throw error
                }
            )
        } catch (e: Exception) {
            Napier.e("❌ Exception while creating prototype", e)
            throw e
        }
    }

    override suspend fun updatePrototype(prototype: Prototype) {
        try {
            val result = firebaseService.savePrototype(prototype)
            result.fold(
                onSuccess = { updatedPrototype ->
                    Napier.d("✅ Successfully updated prototype: ${updatedPrototype.name}")
                },
                onFailure = { error ->
                    Napier.e("❌ Failed to update prototype: ${prototype.name}", error)
                    throw error
                }
            )
        } catch (e: Exception) {
            Napier.e("❌ Exception while updating prototype: ${prototype.name}", e)
            throw e
        }
    }

    override suspend fun deletePrototype(id: String) {
        try {
            val result = firebaseService.deletePrototype(id)
            result.fold(
                onSuccess = {
                    Napier.d("✅ Successfully deleted prototype with id: $id")
                },
                onFailure = { error ->
                    Napier.e("❌ Failed to delete prototype with id: $id", error)
                    throw error
                }
            )
        } catch (e: Exception) {
            Napier.e("❌ Exception while deleting prototype with id: $id", e)
            throw e
        }
    }
}
