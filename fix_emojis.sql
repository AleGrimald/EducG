-- Corrige los emoji de `cursos` que quedaron como '?' en el dump original
-- (se perdieron por una conexión/mysqldump que no usó utf8mb4 al exportar).
-- Ejecutar con: mysql --default-character-set=utf8mb4 -u root -p educg_db < fix_emojis.sql

SET NAMES utf8mb4;

UPDATE cursos SET emoji = '🐍'  WHERE titulo = 'Python para Principiantes';
UPDATE cursos SET emoji = '🌐'  WHERE titulo = 'Desarrollo Web Full Stack';
UPDATE cursos SET emoji = '🗄️' WHERE titulo = 'SQL y Bases de Datos';
UPDATE cursos SET emoji = '🔧'  WHERE titulo = 'Git y GitHub';
UPDATE cursos SET emoji = '📊'  WHERE titulo = 'Algoritmos y Estructuras de Datos';

SELECT id, emoji, titulo FROM cursos ORDER BY id;
