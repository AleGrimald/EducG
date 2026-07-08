-- ============================================================
--  Educ G – Script de base de datos
--  Ejecutar con: mysql -u <usuario> -p < schema.sql
-- ============================================================
--
--  Diseño normalizado hasta 4FN
--  ─────────────────────────────────────────────────────────
--  1FN : todos los atributos son atómicos y de un solo valor;
--        los tópicos de cada curso se extraen a curso_contenidos.
--  2FN : todas las tablas usan PK surrogate (id INT); no existen
--        dependencias parciales sobre claves compuestas.
--  3FN : ningún atributo no-clave depende de otro no-clave;
--        curso_titulo se elimina de inscripciones/test_resultados
--        y se reemplaza por FK a cursos(id).
--  4FN : no existen dependencias multivaluadas independientes
--        en una misma tabla; la relación usuario↔curso y la
--        relación usuario↔test se mantienen en tablas separadas.
-- ============================================================

CREATE DATABASE IF NOT EXISTS educg_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE educg_db;

-- ============================================================
--  TABLAS
-- ============================================================

-- ── 1. usuarios ──────────────────────────────────────────────
--  Cada atributo describe únicamente al usuario identificado
--  por su PK (id).  email es clave candidata (UNIQUE).
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuarios (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    email          VARCHAR(255) NOT NULL UNIQUE,
    password_hash  VARCHAR(512) NOT NULL,          -- <saltHex>:<sha256Hex>
    nombre         VARCHAR(100) NOT NULL,
    apellido       VARCHAR(100) NOT NULL,
    fecha_registro DATETIME     DEFAULT CURRENT_TIMESTAMP,
    activo         TINYINT(1)   DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 2. cursos ────────────────────────────────────────────────
--  Entidad independiente.  Elimina la repetición de
--  curso_titulo VARCHAR en inscripciones y test_resultados
--  (violación 3FN del esquema anterior).
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cursos (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    emoji       VARCHAR(10)  NOT NULL,
    titulo      VARCHAR(200) NOT NULL UNIQUE,
    descripcion TEXT         NOT NULL,
    duracion    VARCHAR(50)  NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 3. curso_contenidos ──────────────────────────────────────
--  Resuelve la violación de 1FN: los tópicos de un curso son
--  un grupo repetitivo (array en Java).  Cada fila es un
--  tópico atómico con su orden dentro del curso.
--  Dependencia funcional: {curso_id, orden} → topico
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS curso_contenidos (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    curso_id  INT          NOT NULL,
    orden     TINYINT      NOT NULL,
    topico    VARCHAR(200) NOT NULL,
    UNIQUE KEY uq_contenido (curso_id, orden),
    FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 4. inscripciones ─────────────────────────────────────────
--  Relación N:M entre usuarios y cursos con atributo propio
--  (fecha_inscripcion, activo).
--  3FN: ya no almacena curso_titulo; usa FK a cursos(id).
--  4FN: la relación usuario↔curso es independiente de la
--       relación usuario↔test → tablas separadas.
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS inscripciones (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id        INT        NOT NULL,
    curso_id          INT        NOT NULL,
    fecha_inscripcion DATETIME   DEFAULT CURRENT_TIMESTAMP,
    activo            TINYINT(1) DEFAULT 1,
    UNIQUE KEY uq_inscripcion (usuario_id, curso_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (curso_id)   REFERENCES cursos(id)   ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 5. test_resultados ───────────────────────────────────────
--  Cada fila representa un resultado único de un test para un
--  usuario en un curso.
--  3FN: usa FK curso_id en vez de curso_titulo VARCHAR.
--  DF: id → {usuario_id, curso_id, test_nombre, puntaje, fecha}
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS test_resultados (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id  INT         NOT NULL,
    curso_id    INT         NOT NULL,
    test_nombre VARCHAR(200) NOT NULL,
    puntaje     INT          NOT NULL CHECK (puntaje BETWEEN 0 AND 100),
    fecha       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (curso_id)   REFERENCES cursos(id)   ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
--  DATOS INICIALES – Catálogo de cursos
-- ============================================================

INSERT IGNORE INTO cursos (emoji, titulo, descripcion, duracion) VALUES
('☕', 'Java desde Cero',
 'Aprendé programación orientada a objetos con el lenguaje más usado en la industria.',
 '8 semanas'),
('🐍', 'Python para Principiantes',
 'El lenguaje más amigable para comenzar. Ideal para automatización, datos y web.',
 '6 semanas'),
('🌐', 'Desarrollo Web Full Stack',
 'Construí sitios modernos con HTML, CSS, JavaScript y una intro a frameworks.',
 '10 semanas'),
('🗄️', 'SQL y Bases de Datos',
 'Diseñá y consultá bases de datos relacionales. Fundamento de toda aplicación.',
 '5 semanas'),
('🔧', 'Git y GitHub',
 'Control de versiones profesional. Trabajá en equipo sin perder ningún cambio.',
 '3 semanas'),
('📊', 'Algoritmos y Estructuras de Datos',
 'El corazón de la programación eficiente. Preparate para entrevistas técnicas.',
 '7 semanas');

INSERT IGNORE INTO curso_contenidos (curso_id, orden, topico)
SELECT c.id, v.orden, v.topico FROM cursos c
JOIN (
    SELECT 'Java desde Cero' AS titulo, 1 AS orden, 'Variables y tipos de datos'   AS topico UNION ALL
    SELECT 'Java desde Cero', 2, 'Control de flujo'                                           UNION ALL
    SELECT 'Java desde Cero', 3, 'POO: clases y objetos'                                      UNION ALL
    SELECT 'Java desde Cero', 4, 'Colecciones y generics'                                     UNION ALL
    SELECT 'Java desde Cero', 5, 'Excepciones y archivos'                                     UNION ALL
    SELECT 'Python para Principiantes', 1, 'Sintaxis y estructuras básicas'                   UNION ALL
    SELECT 'Python para Principiantes', 2, 'Funciones y módulos'                              UNION ALL
    SELECT 'Python para Principiantes', 3, 'Listas, dicts y sets'                             UNION ALL
    SELECT 'Python para Principiantes', 4, 'Archivos y excepciones'                           UNION ALL
    SELECT 'Python para Principiantes', 5, 'Introducción a pip'                               UNION ALL
    SELECT 'Desarrollo Web Full Stack', 1, 'HTML5 semántico'                                  UNION ALL
    SELECT 'Desarrollo Web Full Stack', 2, 'CSS3 y Flexbox/Grid'                              UNION ALL
    SELECT 'Desarrollo Web Full Stack', 3, 'JavaScript ES6+'                                  UNION ALL
    SELECT 'Desarrollo Web Full Stack', 4, 'DOM y eventos'                                    UNION ALL
    SELECT 'Desarrollo Web Full Stack', 5, 'Intro a React'                                    UNION ALL
    SELECT 'SQL y Bases de Datos', 1, 'Modelo relacional y DDL'                               UNION ALL
    SELECT 'SQL y Bases de Datos', 2, 'SELECT, WHERE, JOIN'                                   UNION ALL
    SELECT 'SQL y Bases de Datos', 3, 'Subconsultas y funciones'                              UNION ALL
    SELECT 'SQL y Bases de Datos', 4, 'Índices y optimización'                                UNION ALL
    SELECT 'SQL y Bases de Datos', 5, 'MySQL en la práctica'                                  UNION ALL
    SELECT 'Git y GitHub', 1, 'Repositorios y commits'                                        UNION ALL
    SELECT 'Git y GitHub', 2, 'Branches y merges'                                             UNION ALL
    SELECT 'Git y GitHub', 3, 'Resolución de conflictos'                                      UNION ALL
    SELECT 'Git y GitHub', 4, 'Pull requests y code review'                                   UNION ALL
    SELECT 'Git y GitHub', 5, 'GitHub Actions básico'                                         UNION ALL
    SELECT 'Algoritmos y Estructuras de Datos', 1, 'Complejidad algorítmica'                  UNION ALL
    SELECT 'Algoritmos y Estructuras de Datos', 2, 'Arrays, listas y pilas'                   UNION ALL
    SELECT 'Algoritmos y Estructuras de Datos', 3, 'Árboles y grafos'                         UNION ALL
    SELECT 'Algoritmos y Estructuras de Datos', 4, 'Búsqueda y ordenamiento'                  UNION ALL
    SELECT 'Algoritmos y Estructuras de Datos', 5, 'Algoritmos greedy y DP'
) v ON c.titulo = v.titulo;

-- ============================================================
--  STORED PROCEDURES
-- ============================================================

DELIMITER $$

-- ── Usuarios ─────────────────────────────────────────────────

DROP PROCEDURE IF EXISTS sp_get_usuario $$
CREATE PROCEDURE sp_get_usuario(IN p_email VARCHAR(255))
BEGIN
    SELECT id, nombre, apellido, email, fecha_registro
    FROM   usuarios
    WHERE  email = p_email AND activo = 1;
END $$

DROP PROCEDURE IF EXISTS sp_update_datos_personales $$
CREATE PROCEDURE sp_update_datos_personales(
    IN p_email    VARCHAR(255),
    IN p_nombre   VARCHAR(100),
    IN p_apellido VARCHAR(100)
)
BEGIN
    UPDATE usuarios
    SET    nombre = p_nombre, apellido = p_apellido
    WHERE  email = p_email AND activo = 1;
    SELECT ROW_COUNT() AS filas_afectadas;
END $$

DROP PROCEDURE IF EXISTS sp_get_password_hash $$
CREATE PROCEDURE sp_get_password_hash(IN p_email VARCHAR(255))
BEGIN
    SELECT password_hash FROM usuarios WHERE email = p_email AND activo = 1;
END $$

DROP PROCEDURE IF EXISTS sp_update_password $$
CREATE PROCEDURE sp_update_password(
    IN p_email      VARCHAR(255),
    IN p_nuevo_hash VARCHAR(512)
)
BEGIN
    UPDATE usuarios SET password_hash = p_nuevo_hash
    WHERE  email = p_email AND activo = 1;
    SELECT ROW_COUNT() AS filas_afectadas;
END $$

-- ── Cursos ────────────────────────────────────────────────────

-- Listar todo el catálogo con sus contenidos
DROP PROCEDURE IF EXISTS sp_listar_catalogo $$
CREATE PROCEDURE sp_listar_catalogo()
BEGIN
    SELECT c.id, c.emoji, c.titulo, c.descripcion, c.duracion,
           cc.orden, cc.topico
    FROM   cursos c
    LEFT JOIN curso_contenidos cc ON cc.curso_id = c.id
    ORDER  BY c.id, cc.orden;
END $$

-- Obtener curso por título (resuelve titulo → id para el código Java)
DROP PROCEDURE IF EXISTS sp_get_curso_por_titulo $$
CREATE PROCEDURE sp_get_curso_por_titulo(IN p_titulo VARCHAR(200))
BEGIN
    SELECT id, emoji, titulo, descripcion, duracion
    FROM   cursos
    WHERE  titulo = p_titulo;
END $$

-- ── Inscripciones ─────────────────────────────────────────────

-- Inscribir o reactivar (OUT: 1=OK, 0=ya activo, -1=usuario/curso no existe)
DROP PROCEDURE IF EXISTS sp_inscribir_curso $$
CREATE PROCEDURE sp_inscribir_curso(
    IN  p_email        VARCHAR(255),
    IN  p_curso_titulo VARCHAR(200),
    OUT p_resultado    TINYINT
)
BEGIN
    DECLARE v_usuario_id INT DEFAULT NULL;
    DECLARE v_curso_id   INT DEFAULT NULL;
    DECLARE v_insc_id    INT DEFAULT NULL;
    DECLARE v_activo     TINYINT DEFAULT NULL;

    SELECT id INTO v_usuario_id FROM usuarios WHERE email = p_email AND activo = 1;
    SELECT id INTO v_curso_id   FROM cursos   WHERE titulo = p_curso_titulo;

    IF v_usuario_id IS NULL OR v_curso_id IS NULL THEN
        SET p_resultado = -1;
    ELSE
        SELECT id, activo INTO v_insc_id, v_activo
        FROM   inscripciones
        WHERE  usuario_id = v_usuario_id AND curso_id = v_curso_id;

        IF v_insc_id IS NULL THEN
            INSERT INTO inscripciones (usuario_id, curso_id) VALUES (v_usuario_id, v_curso_id);
            SET p_resultado = 1;
        ELSEIF v_activo = 0 THEN
            UPDATE inscripciones SET activo = 1, fecha_inscripcion = NOW() WHERE id = v_insc_id;
            SET p_resultado = 1;
        ELSE
            SET p_resultado = 0;
        END IF;
    END IF;
END $$

-- Baja lógica
DROP PROCEDURE IF EXISTS sp_baja_curso $$
CREATE PROCEDURE sp_baja_curso(
    IN p_email        VARCHAR(255),
    IN p_curso_titulo VARCHAR(200)
)
BEGIN
    UPDATE inscripciones i
    INNER JOIN usuarios u ON u.id = i.usuario_id
    INNER JOIN cursos   c ON c.id = i.curso_id
    SET    i.activo = 0
    WHERE  u.email = p_email AND c.titulo = p_curso_titulo;
END $$

-- Listar cursos activos del usuario con datos del curso
DROP PROCEDURE IF EXISTS sp_listar_cursos_usuario $$
CREATE PROCEDURE sp_listar_cursos_usuario(IN p_email VARCHAR(255))
BEGIN
    SELECT c.titulo, c.emoji, c.duracion, i.fecha_inscripcion
    FROM   inscripciones i
    INNER JOIN usuarios u ON u.id = i.usuario_id
    INNER JOIN cursos   c ON c.id = i.curso_id
    WHERE  u.email = p_email AND i.activo = 1
    ORDER  BY i.fecha_inscripcion DESC;
END $$

-- Verificar inscripción activa (OUT: 1=sí, 0=no)
DROP PROCEDURE IF EXISTS sp_esta_inscripto $$
CREATE PROCEDURE sp_esta_inscripto(
    IN  p_email        VARCHAR(255),
    IN  p_curso_titulo VARCHAR(200),
    OUT p_resultado    TINYINT
)
BEGIN
    SELECT COUNT(*) INTO p_resultado
    FROM   inscripciones i
    INNER JOIN usuarios u ON u.id = i.usuario_id
    INNER JOIN cursos   c ON c.id = i.curso_id
    WHERE  u.email = p_email AND c.titulo = p_curso_titulo AND i.activo = 1;
END $$

-- ── Test Resultados ───────────────────────────────────────────

-- Registrar resultado de un test
DROP PROCEDURE IF EXISTS sp_registrar_test $$
CREATE PROCEDURE sp_registrar_test(
    IN p_email        VARCHAR(255),
    IN p_curso_titulo VARCHAR(200),
    IN p_test_nombre  VARCHAR(200),
    IN p_puntaje      INT
)
BEGIN
    DECLARE v_usuario_id INT DEFAULT NULL;
    DECLARE v_curso_id   INT DEFAULT NULL;

    SELECT id INTO v_usuario_id FROM usuarios WHERE email = p_email AND activo = 1;
    SELECT id INTO v_curso_id   FROM cursos   WHERE titulo = p_curso_titulo;

    IF v_usuario_id IS NOT NULL AND v_curso_id IS NOT NULL THEN
        INSERT INTO test_resultados (usuario_id, curso_id, test_nombre, puntaje)
        VALUES (v_usuario_id, v_curso_id, p_test_nombre, p_puntaje);
    END IF;
END $$

-- Listar historial de tests del usuario
DROP PROCEDURE IF EXISTS sp_listar_tests_usuario $$
CREATE PROCEDURE sp_listar_tests_usuario(IN p_email VARCHAR(255))
BEGIN
    SELECT c.titulo AS curso_titulo, t.test_nombre, t.puntaje, t.fecha
    FROM   test_resultados t
    INNER JOIN usuarios u ON u.id = t.usuario_id
    INNER JOIN cursos   c ON c.id = t.curso_id
    WHERE  u.email = p_email
    ORDER  BY t.fecha DESC;
END $$

-- ── Estadísticas ──────────────────────────────────────────────

-- Resumen general del usuario
DROP PROCEDURE IF EXISTS sp_estadisticas_usuario $$
CREATE PROCEDURE sp_estadisticas_usuario(IN p_email VARCHAR(255))
BEGIN
    DECLARE v_uid INT DEFAULT NULL;
    SELECT id INTO v_uid FROM usuarios WHERE email = p_email AND activo = 1;

    SELECT
        (SELECT COUNT(*) FROM inscripciones  WHERE usuario_id = v_uid AND activo = 1)            AS cursos_inscriptos,
        (SELECT COUNT(*) FROM test_resultados WHERE usuario_id = v_uid)                           AS tests_realizados,
        (SELECT COALESCE(ROUND(AVG(puntaje)), 0) FROM test_resultados WHERE usuario_id = v_uid)  AS promedio_puntaje,
        (SELECT COALESCE(MAX(puntaje), 0)        FROM test_resultados WHERE usuario_id = v_uid)  AS puntaje_maximo,
        (SELECT COALESCE(MIN(puntaje), 0)        FROM test_resultados WHERE usuario_id = v_uid)  AS puntaje_minimo;
END $$

-- Promedio y mejor puntaje agrupado por curso
DROP PROCEDURE IF EXISTS sp_progreso_por_curso $$
CREATE PROCEDURE sp_progreso_por_curso(IN p_email VARCHAR(255))
BEGIN
    SELECT c.titulo                      AS curso_titulo,
           COUNT(*)                      AS tests_realizados,
           ROUND(AVG(t.puntaje), 1)      AS promedio,
           MAX(t.puntaje)                AS mejor_puntaje
    FROM   test_resultados t
    INNER JOIN usuarios u ON u.id = t.usuario_id
    INNER JOIN cursos   c ON c.id = t.curso_id
    WHERE  u.email = p_email
    GROUP  BY c.id, c.titulo
    ORDER  BY promedio DESC;
END $$

DELIMITER ;


-- ============================================================
--  STORED PROCEDURES
-- ============================================================

DELIMITER $$

-- ── Usuarios ─────────────────────────────────────────────────────────────────

-- Obtener datos personales de un usuario por email
DROP PROCEDURE IF EXISTS sp_get_usuario $$
CREATE PROCEDURE sp_get_usuario(IN p_email VARCHAR(255))
BEGIN
    SELECT id, nombre, apellido, email, fecha_registro
    FROM   usuarios
    WHERE  email = p_email AND activo = 1;
END $$

-- Actualizar nombre y apellido
DROP PROCEDURE IF EXISTS sp_update_datos_personales $$
CREATE PROCEDURE sp_update_datos_personales(
    IN p_email    VARCHAR(255),
    IN p_nombre   VARCHAR(100),
    IN p_apellido VARCHAR(100)
)
BEGIN
    UPDATE usuarios
    SET    nombre   = p_nombre,
           apellido = p_apellido
    WHERE  email = p_email AND activo = 1;
    SELECT ROW_COUNT() AS filas_afectadas;
END $$

-- Actualizar contraseña (recibe el hash ya calculado desde Java)
DROP PROCEDURE IF EXISTS sp_update_password $$
CREATE PROCEDURE sp_update_password(
    IN p_email        VARCHAR(255),
    IN p_nuevo_hash   VARCHAR(512)
)
BEGIN
    UPDATE usuarios
    SET    password_hash = p_nuevo_hash
    WHERE  email = p_email AND activo = 1;
    SELECT ROW_COUNT() AS filas_afectadas;
END $$

-- Obtener hash de contraseña almacenado (para verificar antes de cambiar)
DROP PROCEDURE IF EXISTS sp_get_password_hash $$
CREATE PROCEDURE sp_get_password_hash(IN p_email VARCHAR(255))
BEGIN
    SELECT password_hash
    FROM   usuarios
    WHERE  email = p_email AND activo = 1;
END $$

-- ── Inscripciones ─────────────────────────────────────────────────────────────

-- Inscribir (crea o reactiva)
DROP PROCEDURE IF EXISTS sp_inscribir_curso $$
CREATE PROCEDURE sp_inscribir_curso(
    IN  p_email        VARCHAR(255),
    IN  p_curso_titulo VARCHAR(200),
    OUT p_resultado    TINYINT   -- 1=inscripto OK, 0=ya estaba activo, -1=usuario no existe
)
BEGIN
    DECLARE v_usuario_id INT DEFAULT NULL;
    DECLARE v_insc_id    INT DEFAULT NULL;
    DECLARE v_activo     TINYINT DEFAULT NULL;

    SELECT id INTO v_usuario_id FROM usuarios WHERE email = p_email AND activo = 1;

    IF v_usuario_id IS NULL THEN
        SET p_resultado = -1;
    ELSE
        SELECT id, activo INTO v_insc_id, v_activo
        FROM   inscripciones
        WHERE  usuario_id = v_usuario_id AND curso_titulo = p_curso_titulo;

        IF v_insc_id IS NULL THEN
            INSERT INTO inscripciones (usuario_id, curso_titulo) VALUES (v_usuario_id, p_curso_titulo);
            SET p_resultado = 1;
        ELSEIF v_activo = 0 THEN
            UPDATE inscripciones
            SET    activo = 1, fecha_inscripcion = NOW()
            WHERE  id = v_insc_id;
            SET p_resultado = 1;
        ELSE
            SET p_resultado = 0; -- ya estaba inscripto
        END IF;
    END IF;
END $$

-- Darse de baja de un curso (baja lógica)
DROP PROCEDURE IF EXISTS sp_baja_curso $$
CREATE PROCEDURE sp_baja_curso(
    IN p_email        VARCHAR(255),
    IN p_curso_titulo VARCHAR(200)
)
BEGIN
    UPDATE inscripciones i
    INNER JOIN usuarios u ON u.id = i.usuario_id
    SET    i.activo = 0
    WHERE  u.email = p_email AND i.curso_titulo = p_curso_titulo;
END $$

-- Listar cursos activos de un usuario
DROP PROCEDURE IF EXISTS sp_listar_cursos_usuario $$
CREATE PROCEDURE sp_listar_cursos_usuario(IN p_email VARCHAR(255))
BEGIN
    SELECT i.curso_titulo, i.fecha_inscripcion
    FROM   inscripciones i
    INNER JOIN usuarios u ON u.id = i.usuario_id
    WHERE  u.email = p_email AND i.activo = 1
    ORDER  BY i.fecha_inscripcion DESC;
END $$

-- Verificar si un usuario está inscripto en un curso
DROP PROCEDURE IF EXISTS sp_esta_inscripto $$
CREATE PROCEDURE sp_esta_inscripto(
    IN  p_email        VARCHAR(255),
    IN  p_curso_titulo VARCHAR(200),
    OUT p_resultado    TINYINT   -- 1=sí, 0=no
)
BEGIN
    SELECT COUNT(*) INTO p_resultado
    FROM   inscripciones i
    INNER JOIN usuarios u ON u.id = i.usuario_id
    WHERE  u.email = p_email AND i.curso_titulo = p_curso_titulo AND i.activo = 1;
END $$

-- ── Test Resultados ───────────────────────────────────────────────────────────

-- Registrar resultado de un test
DROP PROCEDURE IF EXISTS sp_registrar_test $$
CREATE PROCEDURE sp_registrar_test(
    IN p_email        VARCHAR(255),
    IN p_curso_titulo VARCHAR(200),
    IN p_test_nombre  VARCHAR(200),
    IN p_puntaje      INT
)
BEGIN
    DECLARE v_usuario_id INT DEFAULT NULL;
    SELECT id INTO v_usuario_id FROM usuarios WHERE email = p_email AND activo = 1;
    IF v_usuario_id IS NOT NULL THEN
        INSERT INTO test_resultados (usuario_id, curso_titulo, test_nombre, puntaje)
        VALUES (v_usuario_id, p_curso_titulo, p_test_nombre, p_puntaje);
    END IF;
END $$

-- Listar todos los resultados de tests de un usuario
DROP PROCEDURE IF EXISTS sp_listar_tests_usuario $$
CREATE PROCEDURE sp_listar_tests_usuario(IN p_email VARCHAR(255))
BEGIN
    SELECT t.curso_titulo, t.test_nombre, t.puntaje, t.fecha
    FROM   test_resultados t
    INNER JOIN usuarios u ON u.id = t.usuario_id
    WHERE  u.email = p_email
    ORDER  BY t.fecha DESC;
END $$

-- ── Estadísticas ──────────────────────────────────────────────────────────────

-- Resumen de progreso del usuario
DROP PROCEDURE IF EXISTS sp_estadisticas_usuario $$
CREATE PROCEDURE sp_estadisticas_usuario(IN p_email VARCHAR(255))
BEGIN
    DECLARE v_usuario_id INT DEFAULT NULL;
    SELECT id INTO v_usuario_id FROM usuarios WHERE email = p_email AND activo = 1;

    SELECT
        (SELECT COUNT(*) FROM inscripciones  WHERE usuario_id = v_usuario_id AND activo = 1) AS cursos_inscriptos,
        (SELECT COUNT(*) FROM test_resultados WHERE usuario_id = v_usuario_id)                AS tests_realizados,
        (SELECT COALESCE(ROUND(AVG(puntaje)), 0) FROM test_resultados WHERE usuario_id = v_usuario_id) AS promedio_puntaje,
        (SELECT COALESCE(MAX(puntaje), 0)        FROM test_resultados WHERE usuario_id = v_usuario_id) AS puntaje_maximo,
        (SELECT COALESCE(MIN(puntaje), 0)        FROM test_resultados WHERE usuario_id = v_usuario_id) AS puntaje_minimo;
END $$

-- Promedio de puntaje por curso para un usuario
DROP PROCEDURE IF EXISTS sp_progreso_por_curso $$
CREATE PROCEDURE sp_progreso_por_curso(IN p_email VARCHAR(255))
BEGIN
    SELECT t.curso_titulo,
           COUNT(*)                     AS tests_realizados,
           ROUND(AVG(t.puntaje), 1)     AS promedio,
           MAX(t.puntaje)               AS mejor_puntaje
    FROM   test_resultados t
    INNER JOIN usuarios u ON u.id = t.usuario_id
    WHERE  u.email = p_email
    GROUP  BY t.curso_titulo
    ORDER  BY promedio DESC;
END $$

DELIMITER ;
