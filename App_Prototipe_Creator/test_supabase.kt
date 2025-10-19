import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val supabaseUrl = "https://ituecyuydkwefyrvnclo.supabase.co"
    val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml0dWVjeXV5ZGt3ZWZ5cnZuY2xvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTc5NTQ5NDcsImV4cCI6MjA3MzUzMDk0N30.sRwB2UYV3cRdQuNeKAj5Kx2ZbdsMmNTGEI_Zt1W2wBc"
    
    val client = HttpClient(CIO)
    
    try {
        println("🔍 Testing Supabase connection...")
        println("URL: $supabaseUrl/rest/v1/prototypes")
        
        val response = client.get("$supabaseUrl/rest/v1/prototypes") {
            header("apikey", supabaseKey)
            header("Authorization", "Bearer $supabaseKey")
        }
        
        println("✅ Status: ${response.status}")
        println("📦 Response: ${response.bodyAsText()}")
        
    } catch (e: Exception) {
        println("❌ Error: ${e.message}")
        e.printStackTrace()
    } finally {
        client.close()
    }
}
