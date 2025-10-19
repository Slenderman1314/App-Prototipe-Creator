-- Tabla de prototipos para Supabase
-- Esta tabla almacena los prototipos generados por la aplicación

CREATE TABLE IF NOT EXISTS prototypes (
    -- ID autoincremental (clave primaria)
    id BIGSERIAL PRIMARY KEY,
    
    -- ID corto para uso en la aplicación (único)
    short_id TEXT UNIQUE NOT NULL,
    
    -- ID del usuario (para futuras implementaciones de autenticación)
    user_id TEXT,
    
    -- Nombre de la aplicación
    app_name TEXT NOT NULL,
    
    -- Idea original del usuario
    user_idea TEXT,
    
    -- Contenido HTML del prototipo
    html_content TEXT,
    
    -- Descripción validada del prototipo
    validated_description TEXT,
    
    -- Notas de validación
    validation_notes TEXT,
    
    -- Schema JSON (para futuras extensiones)
    json_schema JSONB,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_prototypes_short_id ON prototypes(short_id);
CREATE INDEX IF NOT EXISTS idx_prototypes_user_id ON prototypes(user_id);
CREATE INDEX IF NOT EXISTS idx_prototypes_created_at ON prototypes(created_at DESC);

-- Trigger para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_prototypes_updated_at BEFORE UPDATE ON prototypes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Habilitar Row Level Security (RLS) - Opcional
-- ALTER TABLE prototypes ENABLE ROW LEVEL SECURITY;

-- Política de acceso público para lectura (ajustar según necesidades)
-- CREATE POLICY "Allow public read access" ON prototypes FOR SELECT USING (true);
-- CREATE POLICY "Allow authenticated insert" ON prototypes FOR INSERT WITH CHECK (auth.role() = 'authenticated');
-- CREATE POLICY "Allow authenticated update" ON prototypes FOR UPDATE USING (auth.role() = 'authenticated');
-- CREATE POLICY "Allow authenticated delete" ON prototypes FOR DELETE USING (auth.role() = 'authenticated');

-- Datos de ejemplo (opcional)
INSERT INTO prototypes (short_id, app_name, user_idea, html_content, validated_description, validation_notes)
VALUES 
    ('demo-1', 'E-commerce App', 'Una app para vender productos online', '<h1>E-commerce App</h1><p>Prototipo de aplicación de comercio electrónico</p>', 'Aplicación moderna de comercio electrónico con carrito de compras', 'Validado para MVP'),
    ('demo-2', 'Social Network', 'Red social para conectar personas', '<h1>Social Network</h1><p>Plataforma de redes sociales</p>', 'Plataforma de redes sociales con perfiles y mensajería', 'Requiere autenticación'),
    ('demo-3', 'Task Manager', 'App para gestionar tareas diarias', '<h1>Task Manager</h1><p>Gestor de tareas y productividad</p>', 'Aplicación de productividad para gestión de tareas', 'Listo para desarrollo')
ON CONFLICT (short_id) DO NOTHING;
