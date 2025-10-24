package app.prototype.creator.data.i18n

import app.prototype.creator.data.model.Language

/**
 * Centralized strings for internationalization
 */
object Strings {
    // Gallery Screen
    val galleryTitle = mapOf(
        Language.SPANISH to "Galería de Prototipos",
        Language.ENGLISH to "Prototype Gallery"
    )
    
    val noPrototypes = mapOf(
        Language.SPANISH to "No hay prototipos disponibles",
        Language.ENGLISH to "No prototypes available"
    )
    
    val createFirst = mapOf(
        Language.SPANISH to "Crea tu primer prototipo en el chat",
        Language.ENGLISH to "Create your first prototype in the chat"
    )
    
    val loading = mapOf(
        Language.SPANISH to "Cargando...",
        Language.ENGLISH to "Loading..."
    )
    
    val error = mapOf(
        Language.SPANISH to "Error",
        Language.ENGLISH to "Error"
    )
    
    val retry = mapOf(
        Language.SPANISH to "Reintentar",
        Language.ENGLISH to "Retry"
    )
    
    val cancel = mapOf(
        Language.SPANISH to "Cancelar",
        Language.ENGLISH to "Cancel"
    )
    
    val goToChat = mapOf(
        Language.SPANISH to "Ir al Chat",
        Language.ENGLISH to "Go to Chat"
    )
    
    val searchPrototypes = mapOf(
        Language.SPANISH to "Buscar prototipos...",
        Language.ENGLISH to "Search prototypes..."
    )
    
    val searchByNameOrId = mapOf(
        Language.SPANISH to "Buscar por nombre o ID",
        Language.ENGLISH to "Search by name or ID"
    )
    
    val noResultsFound = mapOf(
        Language.SPANISH to "No se encontraron resultados",
        Language.ENGLISH to "No results found"
    )
    
    val tryDifferentSearch = mapOf(
        Language.SPANISH to "Intenta con otros términos de búsqueda",
        Language.ENGLISH to "Try different search terms"
    )
    
    val sortBy = mapOf(
        Language.SPANISH to "Ordenar por",
        Language.ENGLISH to "Sort by"
    )
    
    val sortNewestFirst = mapOf(
        Language.SPANISH to "Más reciente primero",
        Language.ENGLISH to "Newest first"
    )
    
    val sortOldestFirst = mapOf(
        Language.SPANISH to "Más antiguo primero",
        Language.ENGLISH to "Oldest first"
    )
    
    val created = mapOf(
        Language.SPANISH to "Creado",
        Language.ENGLISH to "Created"
    )
    
    val favorite = mapOf(
        Language.SPANISH to "Favorito",
        Language.ENGLISH to "Favorite"
    )
    
    val unfavorite = mapOf(
        Language.SPANISH to "Quitar de favoritos",
        Language.ENGLISH to "Remove from favorites"
    )
    
    val delete = mapOf(
        Language.SPANISH to "Eliminar",
        Language.ENGLISH to "Delete"
    )
    
    val confirmDelete = mapOf(
        Language.SPANISH to "¿Eliminar prototipo?",
        Language.ENGLISH to "Delete prototype?"
    )
    val confirmDeleteMessage = mapOf(
        Language.SPANISH to "Esta acción no se puede deshacer",
        Language.ENGLISH to "This action cannot be undone"
    )
    
    val back = mapOf(
        Language.SPANISH to "Atrás",
        Language.ENGLISH to "Back"
    )
    
    val chatTitle = mapOf(
        Language.SPANISH to "Chat de Prototipos",
        Language.ENGLISH to "Prototype Chat"
    )
    
    val typeMessage = mapOf(
        Language.SPANISH to "Escribe un mensaje...",
        Language.ENGLISH to "Type a message..."
    )
    
    val send = mapOf(
        Language.SPANISH to "Enviar",
        Language.ENGLISH to "Send"
    )
    
    val generating = mapOf(
        Language.SPANISH to "Generando prototipo...",
        Language.ENGLISH to "Generating prototype..."
    )
    
    val openPrototype = mapOf(
        Language.SPANISH to "Abrir Prototipo",
        Language.ENGLISH to "Open Prototype"
    )
    
    val errorGenerating = mapOf(
        Language.SPANISH to "Error al generar el prototipo",
        Language.ENGLISH to "Error generating prototype"
    )
    
    val chatWelcomeMessage = mapOf(
        Language.SPANISH to "¡Hola! Soy tu asistente de IA. Describe la aplicación que quieres crear y te ayudaré a generar un prototipo.",
        Language.ENGLISH to "Hello! I'm your AI assistant. Describe the application you want to create and I'll help you generate a prototype."
    )
    
    val yesContinue = mapOf(
        Language.SPANISH to "Sí, continúa",
        Language.ENGLISH to "Yes, continue"
    )
    
    // Prototype Detail Screen
    val prototypeDetails = mapOf(
        Language.SPANISH to "Detalles del Prototipo",
        Language.ENGLISH to "Prototype Details"
    )
    
    val errorLoading = mapOf(
        Language.SPANISH to "Error al cargar el prototipo",
        Language.ENGLISH to "Error loading prototype"
    )
    
    val url = mapOf(
        Language.SPANISH to "URL",
        Language.ENGLISH to "URL"
    )
    
    val notAvailable = mapOf(
        Language.SPANISH to "No disponible",
        Language.ENGLISH to "Not available"
    )
    
    // App Settings
    val darkMode = mapOf(
        Language.SPANISH to "Modo Oscuro",
        Language.ENGLISH to "Dark Mode"
    )
    
    val lightMode = mapOf(
        Language.SPANISH to "Modo Claro",
        Language.ENGLISH to "Light Mode"
    )
    
    val language = mapOf(
        Language.SPANISH to "Idioma",
        Language.ENGLISH to "Language"
    )
    
    val selectLanguage = mapOf(
        Language.SPANISH to "Seleccionar idioma",
        Language.ENGLISH to "Select language"
    )
    
    // General
    val initializingApp = mapOf(
        Language.SPANISH to "Inicializando aplicación...",
        Language.ENGLISH to "Initializing application..."
    )
    
    val tryAgain = mapOf(
        Language.SPANISH to "Intentar de nuevo",
        Language.ENGLISH to "Try Again"
    )
    
    // Export functionality
    val export = mapOf(
        Language.SPANISH to "Exportar",
        Language.ENGLISH to "Export"
    )
    
    val exportPrototype = mapOf(
        Language.SPANISH to "Exportar Prototipo",
        Language.ENGLISH to "Export Prototype"
    )
    
    val selectExportFormat = mapOf(
        Language.SPANISH to "Seleccionar formato de exportación",
        Language.ENGLISH to "Select export format"
    )
    
    val exportAsHtml = mapOf(
        Language.SPANISH to "Exportar como HTML",
        Language.ENGLISH to "Export as HTML"
    )
    
    val exportAsMhtml = mapOf(
        Language.SPANISH to "Exportar como MHTML",
        Language.ENGLISH to "Export as MHTML"
    )
    
    val exportAsPdf = mapOf(
        Language.SPANISH to "Exportar como PDF",
        Language.ENGLISH to "Export as PDF"
    )
    
    val exportSuccess = mapOf(
        Language.SPANISH to "Prototipo exportado exitosamente",
        Language.ENGLISH to "Prototype exported successfully"
    )
    
    val exportError = mapOf(
        Language.SPANISH to "Error al exportar el prototipo",
        Language.ENGLISH to "Error exporting prototype"
    )
    
    val selectLocation = mapOf(
        Language.SPANISH to "Seleccionar ubicación",
        Language.ENGLISH to "Select location"
    )
    
    val exportCancelled = mapOf(
        Language.SPANISH to "Exportación cancelada",
        Language.ENGLISH to "Export cancelled"
    )
    
    val saveFile = mapOf(
        Language.SPANISH to "Guardar archivo",
        Language.ENGLISH to "Save file"
    )
    
    val htmlFiles = mapOf(
        Language.SPANISH to "Archivos HTML",
        Language.ENGLISH to "HTML Files"
    )
    
    val pdfFiles = mapOf(
        Language.SPANISH to "Archivos PDF",
        Language.ENGLISH to "PDF Files"
    )
}

/**
 * Extension function to get a string for the current language
 */
fun Map<Language, String>.localized(language: Language): String {
    return this[language] ?: this[Language.SPANISH] ?: ""
}
