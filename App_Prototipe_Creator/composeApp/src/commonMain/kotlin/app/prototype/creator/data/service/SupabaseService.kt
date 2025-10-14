package app.prototype.creator.data.service

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import app.prototype.creator.data.model.Prototype
import app.prototype.creator.data.local.FavoritesRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow

/**
 * Service interface for interacting with Supabase
 */
interface SupabaseService {
    /**
     * Get a single prototype by ID
     */
    suspend fun getPrototype(id: String): Result<Prototype>

    /**
     * Save or update a prototype
     */
    suspend fun savePrototype(prototype: Prototype): Result<Prototype>

    /**
     * Delete a prototype by ID
     */
    suspend fun deletePrototype(id: String): Result<Boolean>

    /**
     * List all prototypes
     */
    suspend fun listPrototypes(): Result<List<Prototype>>

    /**
     * Toggle favorite status for a prototype (stored locally)
     */
    suspend fun toggleFavorite(prototypeId: String)

    /**
     * Check if a prototype is in favorites
     */
    suspend fun isFavorite(prototypeId: String): Boolean
}

/**
 * Implementation of SupabaseService
 */
class SupabaseServiceImpl(
    private val client: HttpClient,
    private val favoritesRepository: FavoritesRepository,
    private val supabaseUrl: String,
    private val supabaseKey: String
) : SupabaseService, CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val baseUrl = supabaseUrl
    private val json = Json { ignoreUnknownKeys = true }
    
    private suspend inline fun <reified T> executeRequest(
        path: String,
        crossinline block: HttpRequestBuilder.() -> Unit
    ): Result<T> {
        return try {
            val fullUrl = "${baseUrl.trimEnd('/')}$path"
            Napier.d("🌐 Making request to: $fullUrl")
            
            val response = client.request {
                url(fullUrl)
                block()
                
                // Add common headers
                header("apikey", supabaseKey)
                header("Authorization", "Bearer $supabaseKey")
                header("Content-Type", "application/json")
                header("Prefer", "return=representation")
            }
            
            Napier.d("📡 Response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                val jsonString = response.bodyAsText()
                Napier.d("📦 Response body length: ${jsonString.length}")
                Napier.d("📦 Response body: $jsonString")
                val result = json.decodeFromString<T>(jsonString)
                Result.success(result)
            } else {
                val errorBody = try { response.bodyAsText() } catch (e: Exception) { "Unable to read error body" }
                val errorMsg = "Request failed with status: ${response.status}, body: $errorBody"
                Napier.e(errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            val errorMsg = "Network request failed: ${e.message}"
            Napier.e(errorMsg, e)
            e.printStackTrace()
            Result.failure(Exception("Error al cargar los prototipos: ${e.message}", e))
        }
    }
    
    override suspend fun getPrototype(id: String): Result<Prototype> {
        return executeRequest<Array<SupabasePrototypeDTO>>("/rest/v1/prototypes?short_id=eq.$id") {
            method = HttpMethod.Get
        }.map { dtos ->
            if (dtos.isEmpty()) {
                throw NoSuchElementException("Prototype with id $id not found")
            }
            dtos.first().toDomain(isFavorite = isFavorite(id))
        }
    }
    
    override suspend fun savePrototype(prototype: Prototype): Result<Prototype> {
        val dto = SupabasePrototypeDTO.fromDomain(prototype)
        val path = if (prototype.id.isNotEmpty()) "/rest/v1/prototypes?short_id=eq.${prototype.id}" 
                  else "/rest/v1/prototypes"
        return executeRequest<Array<SupabasePrototypeDTO>>(path) {
            method = if (prototype.id.isNotEmpty()) HttpMethod.Patch else HttpMethod.Post
            setBody(dto)
        }.map { dtos ->
            if (dtos.isEmpty()) {
                throw IllegalStateException("Failed to save prototype: empty response")
            }
            dtos.first().toDomain(isFavorite = isFavorite(prototype.id))
        }
    }
    
    override suspend fun deletePrototype(id: String): Result<Boolean> {
        return executeRequest<Unit>("/rest/v1/prototypes?short_id=eq.$id") {
            method = HttpMethod.Delete
        }.map { true }
    }
    
    override suspend fun listPrototypes(): Result<List<Prototype>> {
        return try {
            Napier.d("🔍 Fetching prototypes from Supabase at $baseUrl/rest/v1/prototypes")
            
            val result = executeRequest<List<SupabasePrototypeDTO>>("/rest/v1/prototypes?select=*&order=created_at.desc") {
                method = HttpMethod.Get
            }
            
            result.map { dtos ->
                Napier.d("📦 Received ${dtos.size} DTOs from Supabase")
                dtos.forEachIndexed { index, dto ->
                    Napier.d("DTO[$index]: id=${dto.id}, short_id=${dto.short_id}, app_name=${dto.app_name}, html_content_length=${dto.html_content?.length}")
                }
                
                val favorites = favoritesRepository.getFavorites()
                dtos.map { dto ->
                    val prototypeId = dto.short_id ?: dto.id?.toString() ?: ""
                    dto.toDomain(favorites.contains(prototypeId))
                }.also { 
                    Napier.d("✅ Successfully loaded ${it.size} prototypes from Supabase")
                }
            }.onFailure { error ->
                Napier.e("❌ Error loading prototypes: ${error.message}", error)
            }
        } catch (e: Exception) {
            Napier.e("❌ Exception in listPrototypes: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    override suspend fun toggleFavorite(prototypeId: String) {
        favoritesRepository.toggleFavorite(prototypeId)
    }
    
    override suspend fun isFavorite(prototypeId: String): Boolean {
        return favoritesRepository.isFavorite(prototypeId)
    }
    
    private fun isNetworkAvailable(): Boolean {
        // Implement platform-specific network availability check
        return true
    }
}

/**
 * Data Transfer Object for Supabase Prototype
 */
@Serializable
data class SupabasePrototypeDTO(
    val id: Int? = null,
    val short_id: String? = null,
    val user_id: String? = null,
    val app_name: String? = null,
    val user_idea: String? = null,
    val html_content: String? = null,
    val created_at: String? = null,
    val validated_description: String? = null,
    val validation_notes: String? = null,
    val json_schema: JsonElement? = null
) {
    companion object {
        fun fromDomain(prototype: Prototype): SupabasePrototypeDTO {
            return SupabasePrototypeDTO(
                id = prototype.id.toIntOrNull() ?: 0,
                short_id = prototype.id,
                user_id = "",
                app_name = prototype.name,
                html_content = prototype.htmlContent ?: "",
                created_at = prototype.createdAt.toString(),
                validated_description = prototype.description.takeIf { it.isNotEmpty() }
            )
        }
    }
    
    fun toDomain(isFavorite: Boolean = false): Prototype {
        val timestamp = parseTimestamp(created_at)
        return Prototype(
            id = short_id ?: id?.toString() ?: "",
            name = app_name ?: short_id ?: id?.toString() ?: "Unknown",
            description = validated_description ?: "",
            previewUrl = "",
            htmlContent = html_content,
            createdAt = timestamp,
            updatedAt = timestamp,  // Usar la misma fecha para ambos
            isFavorite = isFavorite,
            userIdea = user_idea,
            validationNotes = validation_notes
        )
    }
    
    private fun parseTimestamp(timestamp: String?): Long {
        if (timestamp == null) {
            Napier.w("Timestamp is null, using current time")
            return System.currentTimeMillis()
        }
        
        return try {
            Napier.d("Parsing timestamp: $timestamp")
            
            // Supabase devuelve timestamps en formato ISO 8601: "2024-10-14T23:59:00+00:00" o "2024-10-14T23:59:00.000Z"
            val cleanedTimestamp = timestamp
                .replace("Z", "+00:00")  // Convertir Z a +00:00
                .substringBefore(".")    // Remover milisegundos si existen
                .plus(if (!timestamp.contains("+") && !timestamp.contains("Z")) "+00:00" else "")
            
            // Parsear manualmente
            val parts = timestamp.split("T")
            if (parts.size == 2) {
                val dateParts = parts[0].split("-")
                val timePart = parts[1].replace("Z", "").replace("+00:00", "").substringBefore(".")
                val timeParts = timePart.split(":")
                
                if (dateParts.size == 3 && timeParts.size >= 2) {
                    val year = dateParts[0].toInt()
                    val month = dateParts[1].toInt()
                    val day = dateParts[2].toInt()
                    val hour = timeParts[0].toInt()
                    val minute = timeParts[1].toInt()
                    val second = timeParts.getOrNull(2)?.toIntOrNull() ?: 0
                    
                    val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
                    calendar.set(year, month - 1, day, hour, minute, second)
                    calendar.set(java.util.Calendar.MILLISECOND, 0)
                    
                    val result = calendar.timeInMillis
                    Napier.d("Parsed timestamp: $timestamp -> $result")
                    return result
                }
            }
            
            // Fallback
            Napier.w("Could not parse timestamp format, using current time: $timestamp")
            System.currentTimeMillis()
        } catch (e: Exception) {
            Napier.e("Error parsing timestamp: $timestamp", e)
            System.currentTimeMillis()
        }
    }
}
