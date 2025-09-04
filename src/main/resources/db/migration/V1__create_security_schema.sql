-- =================================================================================
-- ESQUEMA DEL MÓDULO DE SEGURIDAD - VERSIÓN PARA ARQUITECTURA FEDERADA (COGNITO + AD)
--
-- Principios de Diseño:
-- - SIN ALMACENAMIENTO DE CONTRASEÑAS: La autenticación se delega a Cognito/AD.
-- - VINCULACIÓN POR IDENTIFICADOR EXTERNO: La tabla `auth_user` se vincula al
--   usuario de Cognito/AD a través del campo `cognito_sub` (del JWT).
-- - AUTORIZACIÓN EFICIENTE: Modelo RBAC consolidado para consultas de permisos rápidas.
-- - STATELESS: Diseñado para un backend que valida JWTs en cada petición.
-- =================================================================================

-- Habilitar la extensión para generar UUIDs si es necesario en otras partes.
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =============================================================================
-- SECCIÓN 1: GESTIÓN DE PERFILES DE USUARIO Y VINCULACIÓN DE IDENTIDAD
-- =============================================================================

-- Tabla: auth_user
-- Almacena el perfil de la aplicación. NO CONTIENE CONTRASEÑAS.
CREATE TABLE auth_user (
                           user_id BIGSERIAL PRIMARY KEY,
                           cognito_sub VARCHAR(255) UNIQUE NOT NULL, -- Identificador único de Cognito/AD. Vínculo principal.
                           user_principal_name VARCHAR(255),
                           username VARCHAR(100) NOT NULL,
                           email VARCHAR(255) NOT NULL,
                           first_name VARCHAR(100),
                           last_name VARCHAR(100),
                           phone VARCHAR(50),
                           country_code VARCHAR(5),
                           profile_data JSONB,
                           enabled BOOLEAN NOT NULL DEFAULT TRUE,
                           account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
                           account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
                           last_login TIMESTAMP,
                           created_by BIGINT,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_by BIGINT,
                           updated_at TIMESTAMP,
                           CONSTRAINT uq_username_country UNIQUE (country_code, username),
                           CONSTRAINT uq_email_country UNIQUE (country_code, email)
);

COMMENT ON TABLE auth_user IS 'Almacena perfiles de usuario de la aplicación. NO contiene credenciales.';
COMMENT ON COLUMN auth_user.cognito_sub IS 'Identificador inmutable (subject) del JWT de Cognito. Vínculo principal a la identidad externa.';
COMMENT ON COLUMN auth_user.user_principal_name IS 'UPN del usuario en AD, obtenido del JWT. Útil para referencia.';


-- Tabla: auth_user_session
-- Opcional: Rastrea JWTs de acceso para permitir la revocación (lista de denegación).
CREATE TABLE auth_user_session (
                                   session_id BIGSERIAL PRIMARY KEY,
                                   user_id BIGINT NOT NULL,
                                   token_jti VARCHAR(255) NOT NULL UNIQUE, -- 'jti' (JWT ID) claim del token de acceso.
                                   issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   expires_at TIMESTAMP NOT NULL,
                                   ip_address VARCHAR(45),
                                   user_agent TEXT,
                                   revoked BOOLEAN DEFAULT FALSE,
                                   revoked_at TIMESTAMP,
                                   CONSTRAINT fk_user_session_user FOREIGN KEY (user_id) REFERENCES auth_user(user_id) ON DELETE CASCADE
);

COMMENT ON TABLE auth_user_session IS 'Rastrea JWTs de acceso emitidos para permitir su revocación antes de la expiración.';
COMMENT ON COLUMN auth_user_session.token_jti IS 'El claim "jti" (JWT ID) del token de acceso, usado para identificarlo unívocamente.';


-- =============================================================================
-- SECCIÓN 2: AUTORIZACIÓN (MODELO RBAC CONSOLIDADO)
-- (Esta sección permanece sin cambios lógicos, ya que es la implementación ideal)
-- =============================================================================

-- Tabla: auth_module
CREATE TABLE auth_module (
                             module_id BIGSERIAL PRIMARY KEY,
                             module_name VARCHAR(100) NOT NULL UNIQUE,
                             description TEXT,
                             created_by BIGINT,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_by BIGINT,
                             updated_at TIMESTAMP
);

-- Tabla: auth_resource
CREATE TABLE auth_resource (
                               resource_id BIGSERIAL PRIMARY KEY,
                               resource_name VARCHAR(150) NOT NULL,
                               description TEXT,
                               module_id BIGINT NOT NULL,
                               created_by BIGINT,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_by BIGINT,
                               updated_at TIMESTAMP,
                               CONSTRAINT uq_resource_module UNIQUE (module_id, resource_name),
                               CONSTRAINT fk_resource_module FOREIGN KEY (module_id) REFERENCES auth_module(module_id) ON DELETE RESTRICT
);

-- Tabla: auth_action
CREATE TABLE auth_action (
                             action_id BIGSERIAL PRIMARY KEY,
                             action_name VARCHAR(100) NOT NULL UNIQUE,
                             description TEXT,
                             created_by BIGINT,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_by BIGINT,
                             updated_at TIMESTAMP
);

-- Tabla: auth_role
CREATE TABLE auth_role (
                           role_id BIGSERIAL PRIMARY KEY,
                           role_name VARCHAR(100) NOT NULL,
                           description TEXT,
                           country_code VARCHAR(5),
                           created_by BIGINT,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_by BIGINT,
                           updated_at TIMESTAMP,
                           CONSTRAINT uq_role_country UNIQUE (country_code, role_name)
);

-- Tabla: auth_user_role
CREATE TABLE auth_user_role (
                                user_id BIGINT NOT NULL,
                                role_id BIGINT NOT NULL,
                                assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                assigned_by BIGINT,
                                PRIMARY KEY (user_id, role_id),
                                CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES auth_user(user_id) ON DELETE CASCADE,
                                CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES auth_role(role_id) ON DELETE CASCADE
);

-- Tabla: auth_permission (CONSOLIDADA)
CREATE TABLE auth_permission (
                                 role_id BIGINT NOT NULL,
                                 resource_id BIGINT NOT NULL,
                                 action_id BIGINT NOT NULL,
                                 granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 granted_by BIGINT,
                                 PRIMARY KEY (role_id, resource_id, action_id),
                                 CONSTRAINT fk_permission_role FOREIGN KEY (role_id) REFERENCES auth_role(role_id) ON DELETE CASCADE,
                                 CONSTRAINT fk_permission_resource FOREIGN KEY (resource_id) REFERENCES auth_resource(resource_id) ON DELETE CASCADE,
                                 CONSTRAINT fk_permission_action FOREIGN KEY (action_id) REFERENCES auth_action(action_id) ON DELETE CASCADE
);

COMMENT ON TABLE auth_permission IS 'Tabla de concesión de permisos directa y consolidada. El núcleo del modelo RBAC.';

-- Tabla: auth_role_hierarchy
CREATE TABLE auth_role_hierarchy (
                                     parent_role_id BIGINT NOT NULL,
                                     child_role_id BIGINT NOT NULL,
                                     PRIMARY KEY (parent_role_id, child_role_id),
                                     CONSTRAINT fk_role_hier_parent FOREIGN KEY (parent_role_id) REFERENCES auth_role(role_id) ON DELETE CASCADE,
                                     CONSTRAINT fk_role_hier_child FOREIGN KEY (child_role_id) REFERENCES auth_role(role_id) ON DELETE CASCADE
);

-- =============================================================================
-- SECCIÓN 3: AUDITORÍA
-- =============================================================================

-- Tabla: auth_audit_log
CREATE TABLE auth_audit_log (
                                audit_id BIGSERIAL PRIMARY KEY,
                                user_id BIGINT,
                                action VARCHAR(100) NOT NULL,
                                resource VARCHAR(255),
                                resource_id BIGINT,
                                details JSONB,
                                description TEXT,
                                ip_address VARCHAR(45),
                                user_agent TEXT,
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT fk_audit_log_user FOREIGN KEY (user_id) REFERENCES auth_user(user_id) ON DELETE SET NULL
);

COMMENT ON TABLE auth_audit_log IS 'Registra eventos importantes para fines de auditoría y forenses.';
COMMENT ON COLUMN auth_audit_log.user_id IS 'Nulable para permitir eventos del sistema no asociados a un usuario.';
COMMENT ON COLUMN auth_audit_log.details IS 'Almacena contexto detallado (ej. valores antiguos/nuevos, parámetros de solicitud).';