# 🔒 Seguridad de API Keys

## ⚠️ ADVERTENCIAS IMPORTANTES

### **Protección de tus API Keys**

Las API keys son credenciales sensibles que dan acceso a servicios de IA de pago. **Debes protegerlas como si fueran contraseñas**.

#### ❌ **NUNCA hagas esto:**
- ❌ Compartir tu API key con nadie
- ❌ Publicar tu API key en redes sociales, foros o GitHub
- ❌ Enviar tu API key por email o mensajería
- ❌ Guardar tu API key en archivos de texto sin cifrar
- ❌ Usar la misma API key en múltiples dispositivos no confiables

#### ✅ **SÍ debes hacer esto:**
- ✅ Mantener tu API key privada y segura
- ✅ Configurar límites de gasto en tu cuenta del proveedor
- ✅ Monitorear el uso regularmente en el dashboard del proveedor
- ✅ Revocar y regenerar keys si sospechas que fueron comprometidas
- ✅ Usar diferentes API keys para desarrollo y producción

---

## 🛡️ Cómo protegemos tus API Keys

### **Almacenamiento Seguro**

#### **Android**
- Usamos `EncryptedSharedPreferences` con cifrado AES-256
- Las keys se cifran usando el hardware del dispositivo
- Solo accesibles cuando el dispositivo está desbloqueado
- Protegidas por el sistema de seguridad de Android

#### **Desktop**
- Almacenadas en el directorio local del usuario
- Protegidas por los permisos del sistema operativo
- Nunca se envían a servidores externos

### **Comunicación**
- ✅ Todas las comunicaciones usan HTTPS (TLS 1.3)
- ✅ Conexión directa con el proveedor (sin intermediarios)
- ✅ Las keys nunca se registran en logs
- ✅ Las keys solo existen en memoria durante la ejecución

### **Validación**
- ✅ Formato de keys validado antes de guardar
- ✅ Longitud mínima requerida
- ✅ Prefijos específicos verificados por proveedor

---

## 📋 Configuración de Límites de Gasto

**Es ALTAMENTE RECOMENDABLE** configurar límites de gasto en tu cuenta del proveedor:

### **OpenAI**
1. Ve a https://platform.openai.com/account/billing/limits
2. Configura "Hard limit" (límite máximo mensual)
3. Configura "Soft limit" (alerta de gasto)

### **Anthropic**
1. Ve a https://console.anthropic.com/settings/limits
2. Configura límites de uso mensual
3. Activa notificaciones de uso

### **Google (Gemini)**
1. Ve a https://aistudio.google.com/app/apikey
2. Configura cuotas y límites
3. Activa alertas de facturación

---

## 🚨 Si tu API Key fue comprometida

### **Pasos inmediatos:**

1. **Revoca la key comprometida INMEDIATAMENTE**
   - OpenAI: https://platform.openai.com/api-keys
   - Anthropic: https://console.anthropic.com/settings/keys
   - Google: https://aistudio.google.com/app/apikey

2. **Genera una nueva API key**

3. **Revisa el historial de uso**
   - Verifica si hubo uso no autorizado
   - Contacta al soporte del proveedor si detectas cargos fraudulentos

4. **Actualiza la key en la app**
   - Ve a Settings → API Keys
   - Elimina la key antigua
   - Configura la nueva key

---

## 🔍 Monitoreo de Uso

### **Revisa regularmente:**

- **Dashboard del proveedor**: Verifica uso diario/mensual
- **Facturas**: Revisa cargos mensuales
- **Alertas**: Configura notificaciones de uso anómalo

### **Señales de alerta:**
- ⚠️ Uso inesperadamente alto
- ⚠️ Peticiones desde ubicaciones desconocidas
- ⚠️ Picos de uso fuera de tu horario habitual

---

## 📖 Obtener API Keys

### **OpenAI (GPT-4)**
- **Costo**: Desde $0.03 por 1K tokens
- **Obtener key**: https://platform.openai.com/api-keys
- **Documentación**: https://platform.openai.com/docs

### **Anthropic (Claude)**
- **Costo**: Desde $0.015 por 1K tokens
- **Obtener key**: https://console.anthropic.com/settings/keys
- **Documentación**: https://docs.anthropic.com

### **Google (Gemini)**
- **Costo**: Plan gratuito disponible
- **Obtener key**: https://aistudio.google.com/app/apikey
- **Documentación**: https://ai.google.dev/gemini-api/docs

---

## ❓ Preguntas Frecuentes

### **¿Mis API keys se envían a algún servidor de la app?**
**NO**. Las keys se almacenan solo en tu dispositivo y se usan únicamente para comunicación directa con el proveedor de IA.

### **¿Puedo usar la app sin API key?**
**NO**. Necesitas al menos una API key de algún proveedor para usar las funciones de IA.

### **¿Qué pasa si alguien accede a mi dispositivo?**
En Android, las keys están cifradas y protegidas. En Desktop, protege tu cuenta de usuario del sistema operativo.

### **¿Puedo cambiar de proveedor?**
**SÍ**. Puedes configurar múltiples API keys y cambiar entre proveedores desde el menú de ChatScreen.

### **¿Las keys expiran?**
Depende del proveedor. Revisa la documentación de cada uno. Puedes revocar y regenerar keys cuando quieras.

---

## 📞 Soporte

Si tienes problemas con tus API keys:

1. **Problemas de facturación**: Contacta al soporte del proveedor
2. **Problemas técnicos de la app**: Abre un issue en GitHub
3. **Seguridad comprometida**: Revoca la key inmediatamente y contacta al proveedor

---

## ✅ Checklist de Seguridad

Antes de empezar a usar la app:

- [ ] He leído y entendido esta guía de seguridad
- [ ] He configurado límites de gasto en mi cuenta del proveedor
- [ ] He activado alertas de uso
- [ ] Entiendo que soy responsable de proteger mis API keys
- [ ] Sé cómo revocar una key si es comprometida
- [ ] He guardado mis credenciales de acceso al proveedor en un lugar seguro

---

**Última actualización**: Marzo 2026
