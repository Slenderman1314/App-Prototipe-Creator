package app.prototype.creator.data.repository

import app.prototype.creator.data.model.Language
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository to manage the current language selection
 */
class LanguageRepository {
    private val _currentLanguage = MutableStateFlow(Language.SPANISH)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()
    
    /**
     * Changes the current language
     */
    fun setLanguage(language: Language) {
        Napier.d("🌐 LanguageRepository.setLanguage() called with: $language")
        Napier.d("🌐 Previous language: ${_currentLanguage.value}")
        _currentLanguage.value = language
        Napier.d("🌐 New language set: ${_currentLanguage.value}")
    }
    
    /**
     * Gets the current language value
     */
    fun getCurrentLanguage(): Language {
        return _currentLanguage.value
    }
}
