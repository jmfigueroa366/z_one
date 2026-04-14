-- ============================================================
--  PLATAFORMA MUSICAL Z-ONE
--  Script SQL para Oracle SQL Developer
--  Ejecutar este script primero antes de correr el proyecto
-- ============================================================

-- 1. Crear tabla de usuarios del sistema
CREATE TABLE usuarios (
    id_usuario    VARCHAR2(20)  PRIMARY KEY,
    username      VARCHAR2(50)  NOT NULL UNIQUE,
    password_hash VARCHAR2(64)  NOT NULL,   -- SHA-256 en hex
    nombre        VARCHAR2(100) NOT NULL,
    correo        VARCHAR2(100) NOT NULL UNIQUE,
    rol           VARCHAR2(20)  DEFAULT 'USUARIO'
                                CHECK (rol IN ('ADMIN','ARTISTA','PRODUCTOR','USUARIO')),
    activo        NUMBER(1)     DEFAULT 1 CHECK (activo IN (0,1)),
    fecha_registro TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    ultimo_login  TIMESTAMP
);

-- 2. Secuencia para IDs automáticos
CREATE SEQUENCE seq_usuario
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

-- 3. Trigger para generar el ID automáticamente
CREATE OR REPLACE TRIGGER trg_usuario_id
BEFORE INSERT ON usuarios
FOR EACH ROW
BEGIN
    IF :NEW.id_usuario IS NULL THEN
        :NEW.id_usuario := 'USR-' || LPAD(seq_usuario.NEXTVAL, 4, '0');
    END IF;
END;
/

-- 4. Insertar usuario ADMIN por defecto
-- Password: admin123  (hash SHA-256)
INSERT INTO usuarios (username, password_hash, nombre, correo, rol)
VALUES (
    'admin',
    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
    'Administrador Z-One',
    'admin@zonemusic.com',
    'ADMIN'
);

-- 5. Insertar usuario de prueba
-- Password: 1234
INSERT INTO usuarios (username, password_hash, nombre, correo, rol)
VALUES (
    'artista1',
    '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4',
    'DJ Lucho',
    'lucho@zonemusic.com',
    'ARTISTA'
);

COMMIT;

-- 6. Verificar datos insertados
SELECT id_usuario, username, nombre, rol, activo FROM usuarios;
