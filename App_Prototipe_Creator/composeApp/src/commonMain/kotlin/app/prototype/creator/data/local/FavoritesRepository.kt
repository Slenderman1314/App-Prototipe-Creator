package app.prototype.creator.data.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import io.github.aakira.napier.Napier

/**
 * Repository for managing favorite prototypes
 */
interface FavoritesRepository {
    /**
     * Toggle favorite status for a prototype
     */
    fun toggleFavorite(prototypeId: String)
    
    /**
     * Check if a prototype is in favorites
     */
    fun isFavorite(prototypeId: String): Boolean
    
    /**
     * Get all favorite prototype IDs
     */
    fun getFavorites(): Set<String>
    
    companion object {
        /**
         * Create a FavoritesRepository instance
         */
        fun create(settings: Settings): FavoritesRepository {
            return FavoritesRepositoryImpl(settings)
        }
    }
}

/**
 * Implementation of FavoritesRepository using Settings
 */
private class FavoritesRepositoryImpl(
    private val settings: Settings
) : FavoritesRepository {
    private val favoritesKey = "favorite_prototypes"
    
    override fun toggleFavorite(prototypeId: String) {
        val favorites = getFavorites().toMutableSet()
        if (favorites.contains(prototypeId)) {
            favorites.remove(prototypeId)
            Napier.d("Removed from favorites: $prototypeId")
        } else {
            favorites.add(prototypeId)
            Napier.d("Added to favorites: $prototypeId")
        }
        settings[favoritesKey] = Json.encodeToString(SetSerializer(String.serializer()), favorites)
    }
    
    override fun isFavorite(prototypeId: String): Boolean {
        return getFavorites().contains(prototypeId)
    }
    
    override fun getFavorites(): Set<String> {
        return try {
            val json = settings.getStringOrNull(favoritesKey) ?: return emptySet()
            Json.decodeFromString(SetSerializer(String.serializer()), json)
        } catch (e: Exception) {
            Napier.e("Error reading favorites", e)
            emptySet()
        }
    }
    
    private fun Settings.getStringOrNull(key: String): String? {
        return try {
            this[key, ""]
        } catch (e: Exception) {
            null
        }.takeIf { !it.isNullOrEmpty() }
    }
}
