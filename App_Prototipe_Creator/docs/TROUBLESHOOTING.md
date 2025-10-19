# Guía de Solución de Problemas - Carga de Prototipos

## Problema: Los prototipos no se cargan en la aplicación

### Posibles Causas y Soluciones

#### 1. Variables de Entorno No Configuradas

**Síntomas:**
- La aplicación muestra un error al iniciar
- Logs muestran: "property is not set or is empty"

**Solución:**
1. Verifica que el archivo `.env` existe en la raíz del proyecto
2. Asegúrate de que contiene las siguientes variables:
   ```
   SUPABASE_URL=https://tu-proyecto.supabase.co
   SUPABASE_ANON_KEY=tu_clave_anon_aqui
   N8N_BASE_URL=tu_url_n8n
   N8N_WEBHOOK_PATH=tu_webhook_path
   N8N_API_KEY=tu_api_key
   ```

#### 2. Tabla de Supabase No Existe o Está Vacía

**Síntomas:**
- La aplicación carga pero muestra "No prototypes yet"
- Logs muestran: "Successfully loaded 0 prototypes"

**Solución:**
1. Accede a tu panel de Supabase: https://app.supabase.com
2. Ve a "SQL Editor"
3. Ejecuta el script en `docs/SUPABASE_SCHEMA.sql`
4. Verifica que la tabla `prototypes` tiene datos:
   ```sql
   SELECT * FROM prototypes;
   ```

#### 3. Permisos de Supabase (RLS)

**Síntomas:**
- Error 403 o "Forbidden" en los logs
- La tabla existe pero no se pueden leer los datos

**Solución:**
1. En Supabase, ve a "Authentication" > "Policies"
2. Para la tabla `prototypes`, asegúrate de tener una política de lectura pública:
   ```sql
   CREATE POLICY "Allow public read access" 
   ON prototypes FOR SELECT 
   USING (true);
   ```

#### 4. URL de Supabase Incorrecta

**Síntomas:**
- Error de conexión en los logs
- "Network request failed"

**Solución:**
1. Verifica que `SUPABASE_URL` en `.env` tiene el formato correcto:
   - Debe empezar con `https://`
   - Debe terminar con `.supabase.co`
   - Ejemplo: `https://ituecyuydkwefyrvnclo.supabase.co`

#### 5. Clave de API Incorrecta

**Síntomas:**
- Error 401 "Unauthorized"
- "Invalid API key"

**Solución:**
1. En Supabase, ve a "Settings" > "API"
2. Copia la clave "anon public" (no la clave "service_role")
3. Actualiza `SUPABASE_ANON_KEY` en `.env`

### Verificación Paso a Paso

#### Para Desktop:

1. **Verifica que el archivo .env se carga:**
   ```
   Busca en los logs: "📄 Loading .env file from:"
   Debe mostrar: "✅ .env file loaded successfully"
   ```

2. **Verifica la inicialización de Koin:**
   ```
   Busca: "✅ Koin initialized successfully"
   ```

3. **Verifica la conexión a Supabase:**
   ```
   Busca: "🔍 Fetching prototypes from Supabase"
   Debe mostrar: "✅ Successfully loaded X prototypes"
   ```

#### Para Android:

1. **Reconstruye el proyecto:**
   ```bash
   ./gradlew clean
   ./gradlew :composeApp:assembleDebug
   ```

2. **Verifica los logs en Logcat:**
   - Filtra por "Napier" o "Supabase"
   - Busca mensajes de error

### Comandos Útiles

**Limpiar y reconstruir:**
```bash
./gradlew clean build
```

**Ejecutar Desktop:**
```bash
./gradlew :composeApp:run
```

**Ejecutar Android:**
```bash
./gradlew :composeApp:installDebug
```

**Ver logs detallados:**
```bash
./gradlew :composeApp:run --info
```

### Logs Importantes a Buscar

✅ **Éxito:**
- `✅ .env file loaded successfully`
- `✅ Koin initialized successfully`
- `✅ Successfully loaded X prototypes from Supabase`
- `📦 Received X DTOs from Supabase`

❌ **Error:**
- `❌ Error loading .env file`
- `❌ Error initializing Koin`
- `❌ Error loading prototypes`
- `property is not set or is empty`

### Contacto y Soporte

Si después de seguir estos pasos el problema persiste:
1. Revisa los logs completos de la aplicación
2. Verifica que Supabase está funcionando: https://status.supabase.com
3. Comprueba tu conexión a internet

### Cambios Recientes Implementados

- ✅ Soporte multiplataforma para variables de entorno
- ✅ Desktop usa `System.getProperty()`
- ✅ Android usa `BuildConfig` generado desde `.env`
- ✅ Actualizado `compileSdk` a 35 para compatibilidad con Compose 1.8.0
