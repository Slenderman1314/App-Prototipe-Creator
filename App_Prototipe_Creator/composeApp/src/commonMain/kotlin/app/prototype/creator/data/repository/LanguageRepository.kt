package app.prototype.creator.data.repository

import app.prototype.creator.data.model.Language
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
        _currentLanguage.value = language
    }
    
    /**
     * Gets the current language value
     */
    fun getCurrentLanguage(): Language {
        return _currentLanguage.value
    }
}
