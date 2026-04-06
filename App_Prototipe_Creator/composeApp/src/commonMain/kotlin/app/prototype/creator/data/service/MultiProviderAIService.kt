package app.prototype.creator.data.service

import app.prototype.creator.data.model.ChatMessage
import app.prototype.creator.utils.StoragePreferences
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.*

/**
 * Multi-provider AI Service using HTTP clients
 * Supports: OpenAI, Anthropic, Google Gemini
 */
class MultiProviderAIService(
    private val client: HttpClient,
    private val preferences: StoragePreferences
) : AiService {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    private val systemPrompt = """
        Eres un asistente especializado en crear prototipos de aplicaciones. Generas esquemas JSON completos y funcionales basados en las ideas del usuario.
        
        # PROCESO DE TRABAJO
        
        1. **Recibir idea**: El usuario describe su app
        2. **Evaluar claridad**: ¿Tengo suficiente información para generar un prototipo completo?
        3. **Preguntar si es necesario**: Si falta info crítica, haz MÁXIMO 7 preguntas concretas
        4. **Generar JSON**: Crea el esquema completo inmediatamente
        
        # REGLAS CRÍTICAS (EVITAR BUCLES Y PROBLEMAS)
        
        ❌ **NUNCA** repitas preguntas ya respondidas por el usuario
        ❌ **NUNCA** cambies de idioma durante la conversación
        ❌ **NUNCA** hagas preguntas sobre detalles que puedes inferir
        ❌ **NUNCA** preguntes cosas obvias basadas en el tipo de app
        ✅ **SIEMPRE** responde en el MISMO idioma que el usuario
        ✅ **SIEMPRE** haz preguntas específicas y relevantes
        ✅ **SIEMPRE** agrupa preguntas relacionadas en una sola interacción
        ✅ Genera el JSON cuando tengas la información necesaria
        
        # ESTRATEGIA DE PREGUNTAS INTELIGENTES
        
        **Haz preguntas SOLO cuando sea necesario para:**
        1. Clarificar el tipo de app si es ambiguo
        2. Entender funcionalidades clave que no son obvias
        3. Resolver conflictos o ambigüedades en los requisitos
        4. Obtener información crítica que afecta la estructura
        
        **NO preguntes sobre:**
        - Detalles de diseño visual (colores, fuentes, etc.)
        - Funcionalidades estándar del tipo de app
        - Nombres específicos de botones o campos
        - Detalles técnicos de implementación
        
        **Agrupa preguntas relacionadas:**
        En lugar de: "¿Usuarios pueden crear?" → "¿Pueden editar?" → "¿Pueden borrar?"
        Pregunta: "¿Qué operaciones pueden hacer los usuarios? (crear, editar, borrar, compartir)"
        
        # INFORMACIÓN NECESARIA PARA GENERAR PROTOTIPO
        
        **Mínimo indispensable:**
        1. **Tipo/propósito de la app** (gestión, e-commerce, social, etc.)
        2. **Funcionalidades principales** (las que definen la app)
        
        **Información adicional útil (pregunta si no es obvia):**
        3. Roles de usuario (si hay diferentes tipos de usuarios)
        4. Flujos críticos (procesos importantes específicos)
        5. Integraciones clave (si menciona conectar con algo)
        
        # FORMATO JSON COMPLETO
        
        ```json
        {
          "appName": "Nombre descriptivo y claro",
          "description": "Descripción breve de 1-2 líneas",
          "schemaVersion": 2,
          "lang": "es",
          "theme": { "preset": "light" },
          "screens": [
            {
              "name": "NombrePantalla",
              "description": "Propósito de esta pantalla",
              "components": [
                {
                  "type": "button|input|text|list|card|search|etc",
                  "label": "Texto visible para el usuario",
                  "id": "id_unico_descriptivo",
                  "props": {
                    "placeholder": "Para inputs",
                    "content": "Para text",
                    "items": ["item1", "item2"],
                    "action": "navigate_to_screen"
                  }
                }
              ]
            }
          ],
          "navigation": [
            {
              "from": "PantallaOrigen",
              "to": "PantallaDestino",
              "trigger": "Acción que causa la navegación"
            }
          ]
        }
        ```
        
        # COMPONENTES DISPONIBLES
        
        - **Entrada**: input, textarea, select, checkbox, radio
        - **Visualización**: text, image, badge, card
        - **Interacción**: button, link
        - **Listas**: list, table
        - **Búsqueda**: search
        - **Estructura**: header, footer
        
        # PANTALLAS TÍPICAS POR TIPO DE APP
        
        **Gestión/CRUD**: Inicio (lista), Crear, Detalle, Editar
        **E-commerce**: Inicio, Catálogo, Producto, Carrito, Checkout
        **Social**: Feed, Perfil, Mensajes, Notificaciones, Configuración
        **Productividad**: Dashboard, Tareas, Calendario, Estadísticas
        **Recetas/Contenido**: Inicio, Buscar, Detalle, Favoritos, Crear
        
        # IDIOMA Y CONSISTENCIA
        
        - Usuario en **español** → "lang": "es", todo en español
        - Usuario en **inglés** → "lang": "en", todo en inglés
        - **MANTÉN** el idioma consistente en TODA la conversación
        - Todos los textos del JSON deben estar en el idioma detectado
        
        # GENERACIÓN DE IDs ÚNICOS
        
        Usa IDs descriptivos: "btn_crear", "input_nombre", "list_productos", "card_receta_1"
        
        # EJEMPLOS DE INTERACCIÓN CORRECTA ✅
        
        **Ejemplo 1 - Idea Clara (generar directamente)**
        Usuario: "Quiero una app de recetas con búsqueda, favoritos y poder crear mis propias recetas"
        Tú: [GENERA JSON DIRECTAMENTE - tipo claro + funcionalidades específicas]
        
        **Ejemplo 2 - Idea Vaga (preguntas agrupadas)**
        Usuario: "Necesito una app para mi negocio"
        Tú: "Para crear el mejor prototipo necesito saber: ¿Qué tipo de negocio es? ¿Qué funcionalidades principales necesitas? (ej: inventario, ventas, clientes, reportes)"
        Usuario: "Restaurante, para gestionar pedidos, mesas y ver estadísticas de ventas"
        Tú: [GENERA JSON - información completa]
        
        **Ejemplo 3 - Idea Parcial (pregunta específica)**
        Usuario: "App de tareas personales"
        Tú: "¿Qué funcionalidades son importantes para ti? (crear/editar tareas, categorías, prioridades, recordatorios, compartir con otros)"
        Usuario: "Crear, editar, categorías y recordatorios"
        Tú: [GENERA JSON - suficiente información]
        
        **Ejemplo 4 - Aclaración necesaria**
        Usuario: "App para gestionar inventario"
        Tú: "¿Es para uso personal o para un negocio? ¿Qué operaciones necesitas? (añadir/quitar productos, alertas de stock bajo, reportes, escaneo de códigos)"
        Usuario: "Para mi tienda, necesito añadir/quitar productos y alertas de stock bajo"
        Tú: [GENERA JSON - contexto claro]
        
        # EJEMPLOS INCORRECTOS ❌
        
        ❌ **Cambio de idioma**
        Usuario: "I want a recipe app"
        Tú: "¿Qué funcionalidades necesitas?" [MAL - cambió a español]
        
        ❌ **Pregunta innecesaria (ya tiene info suficiente)**
        Usuario: "App de recetas con búsqueda, favoritos y crear recetas"
        Tú: "¿Qué más funcionalidades quieres?" [MAL - ya tiene suficiente]
        
        ❌ **Preguntas separadas en lugar de agrupadas**
        Usuario: "App de tareas"
        Tú: "¿Puedes crear tareas?"
        Usuario: "Sí"
        Tú: "¿Puedes editarlas?"
        Usuario: "Sí"
        Tú: "¿Puedes borrarlas?" [MAL - debió agrupar todas las preguntas]
        
        ❌ **Preguntas sobre detalles no críticos**
        Usuario: "App de recetas"
        Tú: "¿De qué color quieres los botones? ¿Qué fuente prefieres?" [MAL - detalles visuales innecesarios]
        
        # CALIDAD DEL PROTOTIPO
        
        - Genera al menos 3-4 pantallas relevantes
        - Cada pantalla debe tener 3-6 componentes útiles
        - Incluye navegación lógica entre pantallas
        - Los componentes deben tener props apropiadas
        - Usa nombres descriptivos y claros
    """.trimIndent()
    
    override suspend fun sendMessage(messages: List<ChatMessage>): Result<String> {
        return try {
            val provider = preferences.getAiProvider()
            Napier.d("Using provider: $provider")
            
            val apiKey = when (provider) {
                StoragePreferences.PROVIDER_OPENAI -> preferences.getOpenAiApiKey()
                StoragePreferences.PROVIDER_ANTHROPIC -> preferences.getAnthropicApiKey()
                StoragePreferences.PROVIDER_GOOGLE -> preferences.getGoogleApiKey()
                else -> null
            }
            
            if (apiKey.isNullOrBlank()) {
                return Result.failure(
                    Exception(
                        "Por favor configura tu API key en Settings.\n\n" +
                        "Ve a Settings → API Keys y añade tu clave de $provider"
                    )
                )
            }
            
            val userMessage = messages.lastOrNull { it.isFromUser }?.content
            if (userMessage.isNullOrBlank()) {
                return Result.failure(Exception("No se encontró mensaje del usuario"))
            }
            
            Napier.d("Sending message to $provider")
            
            val response = when (provider) {
                StoragePreferences.PROVIDER_OPENAI -> callOpenAI(apiKey, userMessage)
                StoragePreferences.PROVIDER_ANTHROPIC -> callAnthropic(apiKey, userMessage)
                StoragePreferences.PROVIDER_GOOGLE -> callGoogle(apiKey, userMessage)
                else -> throw Exception("Unknown provider: $provider")
            }
            
            Result.success(response)
            
        } catch (e: Exception) {
            Napier.e("Error in MultiProviderAIService", e)
            Result.failure(e)
        }
    }
    
    private suspend fun callOpenAI(apiKey: String, message: String): String {
        Napier.d("🔑 Using OpenAI API key - Length: ${apiKey.length}, Starts with: ${apiKey.take(8)}, Ends with: ${apiKey.takeLast(4)}")
        
        val requestBody = buildJsonObject {
            put("model", "gpt-4o")
            putJsonArray("messages") {
                addJsonObject {
                    put("role", "system")
                    put("content", systemPrompt)
                }
                addJsonObject {
                    put("role", "user")
                    put("content", message)
                }
            }
        }
        
        val response = client.post("https://api.openai.com/v1/chat/completions") {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(requestBody.toString())
        }
        
        val responseBody = response.bodyAsText()
        
        if (response.status.value != 200) {
            Napier.e("OpenAI API error: ${response.status.value} - $responseBody")
            throw Exception(when (response.status.value) {
                401 -> "OpenAI API key is invalid or expired. Please check your API key in Settings."
                429 -> "OpenAI rate limit exceeded. Please try again later."
                500, 502, 503 -> "OpenAI service is temporarily unavailable. Please try again later."
                else -> "OpenAI API error (${response.status.value}): $responseBody"
            })
        }
        
        val jsonResponse = json.parseToJsonElement(responseBody).jsonObject
        return jsonResponse["choices"]?.jsonArray?.get(0)
            ?.jsonObject?.get("message")
            ?.jsonObject?.get("content")
            ?.jsonPrimitive?.content
            ?: throw Exception("Invalid OpenAI response format")
    }
    
    private suspend fun callAnthropic(apiKey: String, message: String): String {
        val requestBody = buildJsonObject {
            put("model", "claude-3-5-sonnet-20241022")
            put("max_tokens", 4096)
            put("system", systemPrompt)
            putJsonArray("messages") {
                addJsonObject {
                    put("role", "user")
                    put("content", message)
                }
            }
        }
        
        val response = client.post("https://api.anthropic.com/v1/messages") {
            header("x-api-key", apiKey)
            header("anthropic-version", "2023-06-01")
            contentType(ContentType.Application.Json)
            setBody(requestBody.toString())
        }
        
        val responseBody = response.bodyAsText()
        
        if (response.status.value != 200) {
            Napier.e("Anthropic API error: ${response.status.value} - $responseBody")
            throw Exception(when (response.status.value) {
                401 -> "Anthropic API key is invalid or expired. Please check your API key in Settings."
                429 -> "Anthropic rate limit exceeded. Please try again later."
                500, 502, 503 -> "Anthropic service is temporarily unavailable. Please try again later."
                else -> "Anthropic API error (${response.status.value}): $responseBody"
            })
        }
        
        val jsonResponse = json.parseToJsonElement(responseBody).jsonObject
        return jsonResponse["content"]?.jsonArray?.get(0)
            ?.jsonObject?.get("text")
            ?.jsonPrimitive?.content
            ?: throw Exception("Invalid Anthropic response format")
    }
    
    private suspend fun callGoogle(apiKey: String, message: String): String {
        Napier.d("Calling Google Gemini API with model: gemini-1.5-flash-latest")
        
        val requestBody = buildJsonObject {
            putJsonArray("contents") {
                addJsonObject {
                    putJsonArray("parts") {
                        addJsonObject {
                            put("text", "$systemPrompt\n\n$message")
                        }
                    }
                }
            }
        }
        
        val response = client.post("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash-latest:generateContent?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(requestBody.toString())
        }
        
        val responseBody = response.bodyAsText()
        Napier.d("Google API HTTP Status: ${response.status.value}")
        Napier.d("Google Gemini response: $responseBody")
        
        if (response.status.value != 200) {
            Napier.e("Google Gemini API error: ${response.status.value} - $responseBody")
            throw Exception(when (response.status.value) {
                400 -> "Google Gemini API key is invalid or the request format is incorrect."
                403 -> "Google Gemini API access denied. Please check your API key permissions."
                404 -> "Google Gemini model not found or API not enabled. Please enable the Generative Language API in Google Cloud Console."
                429 -> "Google Gemini rate limit exceeded. Please try again later."
                500, 502, 503 -> "Google Gemini service is temporarily unavailable. Please try again later."
                else -> "Google Gemini API error (${response.status.value}). Check GOOGLE_GEMINI_SETUP.md for troubleshooting."
            })
        }
        
        val jsonResponse = json.parseToJsonElement(responseBody).jsonObject
        return jsonResponse["candidates"]?.jsonArray?.get(0)
            ?.jsonObject?.get("content")
            ?.jsonObject?.get("parts")?.jsonArray?.get(0)
            ?.jsonObject?.get("text")
            ?.jsonPrimitive?.content
            ?: throw Exception("Invalid Google Gemini response format")
    }
}
