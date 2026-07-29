
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `educg_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `educg_db`;
DROP TABLE IF EXISTS `auditoria_cambios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria_cambios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_admin_id` int DEFAULT NULL,
  `tabla_afectada` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `registro_id` int NOT NULL,
  `accion` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `datos_anteriores` json DEFAULT NULL,
  `datos_nuevos` json DEFAULT NULL,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  `ip_origen` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `usuario_admin_id` (`usuario_admin_id`),
  KEY `tabla_afectada_idx` (`tabla_afectada`),
  KEY `fecha_idx` (`fecha`),
  CONSTRAINT `fk_auditoria_usuario` FOREIGN KEY (`usuario_admin_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `auditoria_cambios` WRITE;
/*!40000 ALTER TABLE `auditoria_cambios` DISABLE KEYS */;
/*!40000 ALTER TABLE `auditoria_cambios` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `certificados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `certificados` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `curso_id` int NOT NULL,
  `test_resultado_id` int NOT NULL,
  `puntaje` int NOT NULL,
  `fecha_emision` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_certificado` (`usuario_id`,`curso_id`),
  KEY `fecha_emision_idx` (`fecha_emision`),
  KEY `fk_certificados_curso` (`curso_id`),
  KEY `fk_certificados_resultado` (`test_resultado_id`),
  CONSTRAINT `fk_certificados_curso` FOREIGN KEY (`curso_id`) REFERENCES `cursos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_certificados_resultado` FOREIGN KEY (`test_resultado_id`) REFERENCES `test_resultados` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_certificados_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `certificados` WRITE;
/*!40000 ALTER TABLE `certificados` DISABLE KEYS */;
INSERT INTO `certificados` VALUES (1,2,5,2,90,'2026-07-28 19:52:36');
/*!40000 ALTER TABLE `certificados` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `curso_contenidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `curso_contenidos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `curso_id` int NOT NULL,
  `orden` tinyint NOT NULL,
  `topico` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `contenido` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `ejercicio_propuesto` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `respuesta_esperada` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_curso_orden` (`curso_id`,`orden`),
  KEY `activo_idx` (`activo`),
  CONSTRAINT `fk_contenidos_curso` FOREIGN KEY (`curso_id`) REFERENCES `cursos` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `curso_contenidos` WRITE;
/*!40000 ALTER TABLE `curso_contenidos` DISABLE KEYS */;
INSERT INTO `curso_contenidos` VALUES (1,1,0,'Introducción','Java es un lenguaje de propósito general, fuertemente tipado y orientado a objetos, creado por Sun Microsystems (hoy Oracle) y lanzado en 1995. Su lema \"Write Once, Run Anywhere\" se cumple gracias a la JVM (Java Virtual Machine): el compilador `javac` traduce tu código fuente (.java) a bytecode independiente de la plataforma, guardado en archivos `.class`, y la JVM interpreta (o compila justo a tiempo) ese bytecode en la máquina donde se ejecuta — la misma pieza corre igual en Windows, Linux o macOS sin recompilar. Esta portabilidad, sumada a su tipado fuerte y su recolector de basura automático (que libera memoria que ya no se usa), lo convirtió en el lenguaje dominante de aplicaciones empresariales durante más de 25 años.<br><br>Hoy Java se usa en aplicaciones de escritorio (como esta misma app Educ G, hecha en Swing), backends empresariales (Spring Boot es el framework más usado), apps Android (que corren sobre una variante de la JVM) y sistemas bancarios y de gran escala, donde la estabilidad y el tipado estricto son más valiosos que la velocidad de desarrollo. En este curso vas a instalar el JDK (Java Development Kit, que incluye el compilador y la JVM), entender la diferencia entre `javac` (compila) y `java` (ejecuta), y escribir y correr tu primer programa \"Hola Mundo\".',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(2,1,1,'Variables y tipos de datos','Java es un lenguaje de tipado estático: cada variable debe declarar su tipo antes de usarse (`int edad = 25;`, `String nombre = \"Ana\";`, `double promedio = 8.5;`). Los ocho tipos primitivos (`int`, `long`, `double`, `float`, `boolean`, `char`, `byte`, `short`) se guardan directamente en memoria y son livianos y rápidos; `double` es el tipo estándar para números con decimales de doble precisión, mientras que `int` cubre números enteros dentro de un rango de aproximadamente ±2.100 millones. Los tipos objeto o \"wrapper\" (`Integer`, `Double`, `String`, `Boolean`) envuelven a los primitivos en un objeto, lo que les permite ser `null` y participar en colecciones genéricas (`List<Integer>`, por ejemplo, no acepta `int` directamente).<br><br>Vas a practicar conversiones (casting) entre tipos numéricos — por ejemplo, convertir un `double` a `int` trunca la parte decimal, y hacerlo al revés puede perder precisión si el número es muy grande. También vas a distinguir claramente `==` de `.equals()`: `==` compara si dos referencias apuntan al mismo objeto en memoria (o el valor exacto en primitivos), mientras que `.equals()` compara el contenido — por eso comparar dos `String` con `==` es un error clásico de principiante. Por último, la palabra clave `final` marca que una variable no puede reasignarse después de su primera asignación, la forma estándar de declarar constantes en Java.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(3,1,2,'Control de flujo','Las decisiones se manejan con `if/else` y con `switch` (incluida la versión moderna con flechas `->` desde Java 14, que evita el \"fall-through\" accidental de la sintaxis clásica con `case`). Las repeticiones usan tres tipos de bucles: `for` (cuando sabés de antemano cuántas veces repetir), `while` (repite mientras se cumpla una condición, evaluada antes de cada vuelta) y `do-while` (igual que `while`, pero ejecuta el bloque al menos una vez antes de evaluar la condición). También vas a usar el `for-each` (`for (int n : numeros)`) para recorrer colecciones y arrays sin manejar índices manualmente, reduciendo errores de \"off-by-one\".<br><br>Dentro de cualquier bucle, `break` corta la iteración por completo y sale del bucle, mientras que `continue` salta únicamente el resto de la vuelta actual y sigue con la siguiente — una distinción que suele confundir al principio pero que es clave para escribir bucles legibles en vez de anidar `if` innecesarios. El objetivo de esta clase es que puedas traducir un problema descripto en español (\"repetir hasta que...\", \"para cada elemento...\", \"saltear los que no cumplan tal condición\") a la estructura de control correcta, en vez de memorizar sintaxis sin entender cuándo usar cada una.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(4,1,3,'POO: clases y objetos','La Programación Orientada a Objetos organiza el código en clases (planos) que generan objetos (instancias). Una clase define atributos (el estado del objeto), constructores (cómo se crea una instancia) y métodos (su comportamiento). Vas a practicar los tres pilares clásicos: encapsulamiento, herencia y polimorfismo.<br><br>El encapsulamiento oculta el estado interno marcando los atributos como `private` — visibles solo dentro de la propia clase — y exponiendo el acceso controlado mediante métodos públicos (getters y setters), en vez de dejar que cualquier código externo modifique el atributo directamente sin validación. La herencia (`extends`) permite que una clase reutilice atributos y métodos de otra: por ejemplo, en esta misma aplicación Educ G, `VentanaLogin`, `VentanaRegistro` y `VentanaCursos` heredan de una clase base común (`VentanaBase`) que centraliza el título, el cierre y el tamaño de la ventana, evitando repetir ese código en cada una. El polimorfismo permite que una subclase redefina el comportamiento de un método heredado usando la anotación `@Override`, que además le pide al compilador que verifique que realmente estás sobrescribiendo un método existente (y no creando uno nuevo por error de tipeo).',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(5,1,4,'Colecciones y generics','El paquete `java.util` ofrece estructuras listas para usar: `ArrayList` (lista dinámica, ordenada, permite duplicados), `HashMap` (asocia claves a valores, ideal para búsquedas rápidas por clave) y `HashSet` (como una lista, pero rechaza automáticamente los elementos duplicados). Elegir la estructura correcta según lo que necesitás — ¿accedo por posición?, ¿por clave?, ¿me importa el orden?, ¿pueden repetirse los elementos? — es una de las decisiones de diseño más frecuentes en cualquier programa.<br><br>Los generics (`List<String>`, `Map<Integer, Usuario>`) le dicen al compilador qué tipo de dato vive adentro de la colección, evitando errores de casteo en tiempo de ejecución que antes de Java 5 eran comunes. Vas a practicar recorrer, filtrar y transformar colecciones, incluyendo una introducción a Streams: `.stream().filter(u -> u.isActivo()).map(Usuario::getNombre).collect(Collectors.toList())` encadena una condición de filtrado, una transformación (`map`, que aplica una función a cada elemento y arma una nueva colección con los resultados) y una recolección final, todo de forma declarativa — describiendo QUÉ querés obtener en vez de escribir manualmente el bucle que lo calcula.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(6,1,5,'Excepciones y archivos','Java distingue dos familias de excepciones: las checked (como `SQLException` o `IOException`), que el compilador te OBLIGA a capturar con `try/catch` o a declarar con `throws` en la firma del método porque representan fallas externas esperables (la base de datos no responde, el archivo no existe); y las unchecked (subclases de `RuntimeException`, como `NullPointerException` o `ArithmeticException`), que no exigen ser declaradas porque suelen representar errores de programación que deberías prevenir con validaciones, no capturar sistemáticamente.<br><br>El bloque `try/catch/finally` maneja estos errores: el código dentro de `finally` se ejecuta siempre, haya ocurrido una excepción o no — típicamente para liberar recursos. Así trabaja, por ejemplo, `SQLException` en toda la capa de acceso a datos de esta app. También vas a leer y escribir archivos de texto con `java.nio.file`, y a entender el patrón try-with-resources (`try (Connection conn = ...) { ... }`), que reemplaza al `finally` manual y cierra automáticamente conexiones, archivos y streams aunque ocurra un error en el medio — la forma moderna y más segura de manejar recursos que deben cerrarse siempre.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(7,2,0,'Introducción','Python es un lenguaje interpretado, de tipado dinámico y sintaxis minimalista (usa indentación en vez de llaves `{}`), diseñado por Guido van Rossum a fines de los 80 con un objetivo explícito: que el código se lea casi como texto en inglés. Esa simplicidad lo hace ideal para aprender a pensar como programador sin pelearte con el compilador, y a la vez es una de las razones por las que hoy es el lenguaje dominante en ciencia de datos, automatización, scripting e inteligencia artificial (librerías como pandas, NumPy, scikit-learn y PyTorch son estándares de la industria).<br><br>En este curso instalás Python y el gestor de paquetes `pip`, y escribís tu primer programa con la función `print(\"Hola Mundo\")`, que envía texto a la consola — el punto de partida de prácticamente cualquier script, útil tanto para mostrar resultados como para depurar (\"debuggear\") mientras programás. A diferencia de Java, Python no requiere compilar el código antes de ejecutarlo: el intérprete lee y corre el archivo `.py` directamente, lo que acelera muchísimo el ciclo de prueba y error mientras aprendés.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(8,2,1,'Sintaxis y estructuras básicas','En Python no declarás el tipo de una variable (`nombre = \"Ana\"` alcanza), pero el tipo sí existe en tiempo de ejecución y podés consultarlo con `type(nombre)`. Los tipos básicos son `int` (enteros), `float` (decimales), `str` (texto) y `bool` (verdadero/falso). Los comentarios de una línea se escriben con `#` — todo lo que sigue en esa línea es ignorado por el intérprete, útil para explicar el porqué de una decisión sin afectar la ejecución.<br><br>Entre los operadores aritméticos, `/` siempre devuelve un `float` (incluso `10 / 2` da `5.0`), mientras que `//` (división entera) descarta la parte decimal y devuelve un entero (`10 // 3` da `3`). Las estructuras de control `if/elif/else` y los bucles `for`/`while` se delimitan exclusivamente por indentación (típicamente 4 espacios) en vez de llaves — un bloque mal indentado es un error de sintaxis en Python, a diferencia de Java donde la indentación es solo una convención visual. Esta regla, que al principio parece rígida, obliga a que todo el código Python tenga una estructura visual consistente sin necesidad de un formateador externo.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(9,2,2,'Funciones y módulos','Las funciones se definen con `def nombre(parametros):` y devuelven un valor con `return` — si una función no tiene `return` explícito, devuelve `None` por defecto. Podés darle valores por defecto a los parámetros (`def saludar(nombre=\"invitado\"):`), lo que permite llamar a la función sin especificar todos los argumentos, y también usar argumentos nombrados (`saludar(nombre=\"Ana\")`) para mayor claridad en llamadas con muchos parámetros.<br><br>Vas a aprender a organizar el código en módulos: cualquier archivo `.py` es automáticamente un módulo importable con `import nombre_archivo`, y un paquete es una carpeta con un archivo `__init__.py` que agrupa varios módulos relacionados. Esta organización es la base para separar un proyecto grande en piezas reutilizables y testeables por separado, en vez de un único script gigante donde todo está mezclado — el mismo principio de \"separación de responsabilidades\" que en Java se logra con paquetes y clases.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(10,2,3,'Listas, dicts y sets','Las listas (`[1, 2, 3]`) son mutables y ordenadas — podés agregar un elemento al final con `.append(x)`, o insertarlo en una posición específica con `.insert(i, x)`. Los diccionarios (`{\"clave\": \"valor\"}`) asocian claves a valores y se acceden con `diccionario[\"clave\"]`, similar a un `HashMap` en Java pero con sintaxis mucho más directa. Los sets (`{1, 2, 3}`) se comportan como una lista pero rechazan automáticamente los duplicados — insertar un valor que ya existe simplemente no hace nada.<br><br>Vas a practicar la comprensión de listas (`[x*2 for x in numeros if x > 0]`), una de las herramientas más idiomáticas de Python: en una sola línea legible, filtra los elementos que cumplen una condición y los transforma, reemplazando lo que en otros lenguajes requeriría un bucle `for` de varias líneas con una lista auxiliar. Dominar esta sintaxis es una de las señales más claras de que estás empezando a \"pensar en Python\" en vez de traducir mentalmente desde otro lenguaje.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(11,2,4,'Archivos y excepciones','El manejo de archivos usa `with open(\"archivo.txt\") as f:`, que abre el archivo y lo cierra automáticamente al salir del bloque `with` — el equivalente conceptual al try-with-resources de Java, pero con una sintaxis más corta. Adentro del bloque, `f.read()` devuelve todo el contenido como un string, y `f.readlines()` devuelve una lista con cada línea.<br><br>Para errores esperables se usa `try/except/finally`, de forma similar a Java pero sin obligar a declarar qué excepciones puede lanzar una función (Python no distingue entre excepciones checked y unchecked). El bloque `finally` se ejecuta siempre, haya ocurrido una excepción o no. Vas a practicar capturar errores específicos (`except FileNotFoundError:`, `except ValueError:`) en vez de un `except:` genérico que atrapa absolutamente todo — una mala práctica muy común entre principiantes, porque oculta errores de programación reales (como un typo en el nombre de una variable) detrás de un manejo de excepciones demasiado amplio.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(12,2,5,'Introducción a pip','`pip` es el gestor de paquetes de Python: `pip install requests` descarga e instala una librería desde PyPI (Python Package Index), el repositorio oficial con cientos de miles de paquetes de código abierto. El comando `pip list` muestra todos los paquetes instalados en el entorno actual junto a su versión, útil para verificar qué tenés disponible antes de importar algo.<br><br>Vas a aprender a usar entornos virtuales (`python -m venv venv`) para que las dependencias de cada proyecto no choquen entre sí — sin esto, instalar una versión de una librería para un proyecto podría romper otro proyecto que necesita una versión distinta. Por último, vas a generar un archivo `requirements.txt` (`pip freeze > requirements.txt`) que lista las dependencias exactas de tu proyecto con sus versiones, para que otra persona (o vos mismo en otra máquina) pueda reinstalar exactamente el mismo entorno con `pip install -r requirements.txt`.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(13,3,0,'Introducción','La web se construye sobre tres tecnologías que cumplen roles distintos y se combinan en toda página moderna: HTML define la estructura del contenido (qué hay: títulos, párrafos, imágenes, formularios), CSS define su apariencia (colores, tipografía, espaciado, layout), y JavaScript agrega comportamiento e interactividad — es el único de los tres que puede reaccionar a acciones del usuario, modificar la página en tiempo real y comunicarse con un servidor sin recargar.<br><br>Vas a entender cómo el navegador arma el DOM (Document Object Model) a partir del HTML, cómo aplica en cascada las reglas de CSS sobre ese árbol de elementos, y por qué separar estas tres capas — en vez de mezclar estilos y lógica directamente en el HTML — facilita mantener un sitio a medida que crece: un diseñador puede tocar el CSS sin arriesgar la lógica, y un desarrollador puede tocar el JavaScript sin romper el diseño. Este curso recorre las tres capas y cierra con una introducción a React, el framework más usado para construir interfaces complejas.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(14,3,1,'HTML5 semántico','En vez de armar todo con `<div>` genéricos, HTML5 aporta etiquetas con significado propio: `<header>` (encabezado de la página o de una sección), `<nav>` (bloque de navegación con enlaces principales), `<main>` (el contenido principal, único por página), `<article>` (una pieza de contenido autocontenida, como un post de blog) y `<footer>` (el pie de página, con información de contacto o copyright). Usar la etiqueta correcta en vez de un `<div>` sin significado mejora la accesibilidad (los lectores de pantalla que usan personas con discapacidad visual navegan la página por estas regiones) y el SEO (los motores de búsqueda entienden mejor la estructura del contenido).<br><br>También vas a practicar atributos que mejoran la accesibilidad, como `alt` en las imágenes (`<img src=\"logo.png\" alt=\"Logo de Educ G\">`), que describe la imagen para quien no puede verla y se muestra como texto alternativo si la imagen no carga. Vas a armar la estructura completa de una página usando estas etiquetas semánticas en vez de contenedores genéricos sin significado, el primer paso hacia un sitio profesional y accesible.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(15,3,2,'CSS3 y Flexbox/Grid','Flexbox (`display: flex`) resuelve alineación y distribución de elementos en una dimensión (una fila o una columna): centrar verticalmente un botón, distribuir tarjetas con espacio parejo entre ellas, o hacer que un elemento ocupe todo el espacio sobrante. Grid (`display: grid`) resuelve layouts bidimensionales con filas y columnas simultáneas, ideal para el diseño general de una página (encabezado, barra lateral, contenido, pie). Ambos reemplazan trucos antiguos y frágiles como floats para lograr layouts que antes requerían mucho código.<br><br>Entre las propiedades de espaciado, `padding` define el espacio interno entre el borde de un elemento y su contenido (a diferencia de `margin`, que es el espacio externo respecto a otros elementos) — confundir ambos es un error clásico al empezar con CSS. Vas a construir layouts responsivos con media queries (`@media (max-width: 600px) { ... }`), reglas que aplican estilos distintos según el tamaño de pantalla, para que el mismo sitio se vea bien en celular, tablet y escritorio sin necesitar versiones separadas.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(16,3,3,'JavaScript ES6+','Desde ES6 (2015), JavaScript incorporó `let` y `const` (en vez de `var`, que tenía un comportamiento de alcance confuso), arrow functions (`(x) => x * 2`, una sintaxis más corta para funciones), template literals (`` `Hola ${nombre}` ``, que insertan variables directamente en un string sin concatenar con `+`), y destructuring (`const { nombre, email } = usuario;`), que extrae valores de un objeto o array directamente en variables individuales sin acceder campo por campo.<br><br>Para trabajar con datos que tardan en llegar (como la respuesta de una API), JavaScript usa el objeto `Promise`, que representa una operación asíncrona pendiente que eventualmente se resuelve (éxito) o se rechaza (error). La sintaxis moderna `async/await` permite escribir código asíncrono con la misma legibilidad que código síncrono, evitando el anidamiento de callbacks (\"callback hell\") que caracterizaba al JavaScript más antiguo. Vas a practicar todas estas herramientas modernas, que hoy son estándar en cualquier código JavaScript profesional.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(17,3,4,'DOM y eventos','El DOM (Document Object Model) es la representación en memoria del HTML que JavaScript puede leer y modificar: cada etiqueta se convierte en un objeto navegable y editable desde código. El método `document.querySelector(\"#boton\")` selecciona el primer elemento que coincide con un selector CSS (por id, clase, etiqueta o combinaciones), y `document.querySelectorAll(...)` selecciona todos los que coincidan.<br><br>Para reaccionar a una interacción del usuario, `.addEventListener(\"click\", funcion)` registra una función que se ejecuta cuando ocurre ese evento sobre el elemento — click, escritura en un campo, envío de un formulario, y muchos más. Vas a aprender a actualizar la página dinámicamente sin recargarla: cambiar el texto de un elemento (`elemento.textContent = \"nuevo texto\"`), agregar o quitar clases CSS para mostrar/ocultar contenido, o crear elementos nuevos desde código — la base de cualquier formulario interactivo, menú desplegable o botón que cambia el contenido en vivo.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(18,3,5,'Intro a React','React organiza la interfaz en componentes reutilizables: funciones de JavaScript que devuelven la descripción de cómo debe verse una porción de la pantalla, escrita en JSX (una sintaxis que mezcla HTML y JavaScript directamente, por ejemplo `<h1>Hola {nombre}</h1>`). Cada componente recibe datos desde afuera a través de `props` (propiedades, similar a los parámetros de una función) y puede manejar su propio estado interno con el hook `useState`, que le permite \"recordar\" valores entre renderizados (como el texto escrito en un input).<br><br>El hook `useEffect` ejecuta código después de que el componente se renderiza — típicamente para pedir datos a una API apenas se muestra el componente, o para reaccionar cuando cambia algún valor. En vez de manipular el DOM manualmente como en JavaScript puro, en React describís declarativamente cómo se ve la interfaz para cada estado posible, y React se encarga de calcular y aplicar solo los cambios necesarios en el DOM real. Vas a armar tu primer componente con props y estado, y entender por qué este modelo escala mejor que JavaScript puro en aplicaciones grandes con muchas partes interactivas.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(19,4,0,'Introducción','Casi toda aplicación necesita guardar datos de forma persistente — que sobrevivan a un reinicio del programa o del servidor — y las bases de datos relacionales (MySQL, PostgreSQL, SQL Server) son el estándar más usado para eso desde hace más de 40 años. Esta misma app, Educ G, guarda usuarios, cursos e inscripciones en tablas relacionadas por claves foráneas, siguiendo exactamente los principios que vas a aprender en este curso.<br><br>Vas a entender qué es una tabla (una colección de filas con la misma estructura de columnas), una fila (un registro individual) y una columna (un atributo con un tipo de dato definido), y sobre todo por qué normalizar los datos — organizarlos siguiendo reglas formales (las \"formas normales\") que eliminan la duplicación y las inconsistencias — es la diferencia entre una base de datos mantenible a largo plazo y una que acumula errores silenciosos a medida que crece.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(20,4,1,'Modelo relacional y DDL','El DDL (Data Definition Language) define la estructura de la base: `CREATE TABLE` crea una tabla nueva, `ALTER TABLE` modifica una existente (agregar o quitar columnas, cambiar tipos), y `DROP TABLE` la elimina por completo junto con todos sus datos — un comando irreversible que hay que usar con mucho cuidado. Vas a aprender a elegir el tipo de dato correcto para cada columna (`INT` para enteros, `VARCHAR(n)` para texto de longitud acotada, `TEXT` para texto largo, `DATETIME` para fechas y horas, `BOOLEAN`/`TINYINT(1)` para verdadero/falso).<br><br>Cada tabla necesita una clave primaria (`PRIMARY KEY`), la columna (o combinación de columnas) que identifica de forma única cada fila — típicamente un `id` autoincremental. Las claves foráneas (`FOREIGN KEY`) conectan una tabla con otra, garantizando que, por ejemplo, no pueda existir una inscripción para un `usuario_id` que no exista realmente en la tabla `usuarios`. Las restricciones `NOT NULL` (obliga a que la columna siempre tenga un valor) y `UNIQUE` (impide valores repetidos, como dos usuarios con el mismo email) son la primera línea de defensa contra datos corruptos, antes incluso de que el código de la aplicación tenga que validar nada.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(21,4,2,'SELECT, WHERE, JOIN','El DML de consulta empieza con `SELECT columnas FROM tabla WHERE condición`, donde `WHERE` filtra qué filas se incluyen en el resultado según una o más condiciones (`WHERE activo = 1 AND fecha > \"2024-01-01\"`). Los `JOIN` combinan filas de varias tablas relacionadas: un `INNER JOIN` entre `usuarios` e `inscripciones` devuelve solo las combinaciones donde existe coincidencia en ambas tablas (usuarios que SÍ tienen al menos una inscripción), mientras que un `LEFT JOIN` devuelve además todas las filas de la tabla izquierda aunque no haya coincidencia en la derecha (todos los usuarios, tengan o no inscripciones, mostrando `NULL` donde no las hay).<br><br>Vas a practicar ambos tipos de JOIN, además de ordenar resultados con `ORDER BY columna [ASC|DESC]` y limitar cuántas filas devolver con `LIMIT n` (esencial para paginar resultados grandes en vez de traer toda la tabla de una vez). Combinar `WHERE`, `JOIN`, `ORDER BY` y `LIMIT` correctamente es la habilidad más usada en el día a día de trabajar con bases de datos relacionales.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(22,4,3,'Subconsultas y funciones','Una subconsulta es un `SELECT` dentro de otro `SELECT`, útil para filtrar por un valor calculado dinámicamente (por ejemplo, \"usuarios con más tests que el promedio general\", donde el promedio se calcula con una subconsulta). Vas a combinar esto con funciones de agregación, que resumen múltiples filas en un solo valor: `COUNT(*)` cuenta filas, `AVG(columna)` calcula el promedio, `SUM(columna)` suma todos los valores, y `MAX`/`MIN` devuelven el mayor y menor valor respectivamente.<br><br>`GROUP BY` agrupa las filas según el valor de una o más columnas para aplicar estas funciones POR GRUPO en vez de sobre toda la tabla (por ejemplo, el promedio de puntaje POR curso, no un único promedio general). `HAVING` filtra esos grupos ya calculados — a diferencia de `WHERE`, que filtra filas individuales ANTES de agrupar, `HAVING` filtra los resultados agregados DESPUÉS de agrupar (por ejemplo, \"mostrar solo los cursos con más de 10 inscriptos\"). Vas a generar reportes y estadísticas con estas herramientas, como los que muestra el panel de \"Estadísticas\" de esta misma aplicación.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(23,4,4,'Índices y optimización','Un índice acelera las búsquedas por una columna, igual que el índice de un libro evita leerlo entero para encontrar un tema — en vez de recorrer fila por fila (\"full table scan\"), la base usa una estructura ordenada (típicamente un árbol B) para encontrar directamente las filas que coinciden. Pero cada índice también hace más lentas las escrituras (`INSERT`/`UPDATE`/`DELETE`), porque la base tiene que mantener esa estructura auxiliar actualizada además de la tabla misma — por eso indexar TODAS las columnas \"por las dudas\" es contraproducente.<br><br>Hay que elegir con criterio qué columnas indexar: típicamente las que usás con frecuencia en `WHERE`, `JOIN` o `ORDER BY`, no columnas que rara vez se consultan. Vas a aprender a leer un plan de ejecución con `EXPLAIN SELECT ...`, que muestra cómo MySQL va a resolver una consulta (si usa un índice o si escanea la tabla completa, cuántas filas estima revisar), la herramienta principal para detectar y corregir consultas lentas antes de que se vuelvan un problema en producción.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(24,4,5,'MySQL en la práctica','Vas a cerrar el curso escribiendo procedimientos almacenados (`CREATE PROCEDURE`) como los que usa el backend de Educ G (`sp_alta_usuario`, `sp_listar_inscripciones_usuario`, etc.): funciones que viven directamente en la base de datos, reciben parámetros, y encapsulan la lógica de acceso a datos en un solo lugar en vez de armar SQL a mano desde cada punto de la aplicación que lo necesite — más seguro (previene inyección SQL por diseño) y más fácil de mantener.<br><br>También vas a ver transacciones: `START TRANSACTION` marca el inicio de un grupo de operaciones que deben aplicarse todas juntas o ninguna (por ejemplo, transferir puntos entre dos usuarios: restar de uno y sumar al otro no puede quedar a mitad de camino si algo falla). `COMMIT` confirma la transacción de forma permanente una vez que todo salió bien, y `ROLLBACK` deshace todos los cambios de la transacción si algo falló en el medio, devolviendo la base exactamente al estado anterior — la garantía que hace que las transacciones sean seguras incluso ante errores o caídas del sistema.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(25,5,0,'Introducción','Git es un sistema de control de versiones distribuido: guarda el historial completo de cambios de un proyecto, permite volver atrás a cualquier punto anterior, y hace posible que varias personas trabajen sobre el mismo código sin pisarse el trabajo mutuamente. A diferencia de sistemas más antiguos (centralizados), cada copia local de un repositorio Git tiene el historial completo, no solo una copia de trabajo — podés hacer commits, ver el historial y crear ramas incluso sin conexión a internet.<br><br>GitHub es un servicio online que aloja repositorios Git y agrega herramientas de colaboración por encima: issues (para trackear tareas y bugs), pull requests (para proponer y revisar cambios) y GitHub Actions (para automatizar tareas). Vas a instalar Git, configurar tu identidad (`git config --global user.name \"Tu Nombre\"`) y crear tu primer repositorio con `git init`, el comando que convierte cualquier carpeta en un repositorio Git trackeado desde cero.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(26,5,1,'Repositorios y commits','Un repositorio (`git init` para uno nuevo, o `git clone url` para descargar uno remoto ya existente completo, con todo su historial) trackea el estado de una carpeta a lo largo del tiempo. En cualquier momento, `git status` te muestra qué archivos fueron modificados, cuáles están preparados para el próximo commit y cuáles no están siendo trackeados todavía — el comando que más vas a usar en tu día a día con Git.<br><br>Un commit es una \"foto\" de los cambios en un momento dado, acompañada de un mensaje que explica el porqué (no el qué, que ya se ve en el diff). Vas a practicar el flujo básico: `git add archivo` (preparar cambios específicos), `git commit -m \"mensaje\"` (guardarlos con un mensaje) y `git log` (ver el historial completo de commits, con autor y fecha), y a escribir mensajes de commit claros y útiles para tu futuro yo — o para cualquier compañero que necesite entender por qué se hizo un cambio meses después.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(27,5,2,'Branches y merges','Una branch (rama) es una línea de desarrollo independiente — típicamente creás una por feature o bugfix (`git branch nombre-rama`) para no tocar directamente la rama principal (`main`) mientras trabajás en algo que todavía no está terminado o probado. Un merge (`git merge nombre-rama`) junta los cambios de una rama en otra, integrando el trabajo una vez que está listo.<br><br>Vas a practicar `git branch` (crear y listar ramas), `git checkout`/`git switch` (cambiar entre ramas existentes) y `git merge`, entendiendo cuándo Git puede combinar automáticamente los cambios (cuando las modificaciones no se solapan) y cuándo necesita tu ayuda para decidir manualmente (cuando dos ramas cambiaron exactamente la misma línea de formas distintas). Trabajar con ramas es lo que permite que un equipo entero desarrolle features en paralelo sin bloquearse mutuamente.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(28,5,3,'Resolución de conflictos','Un conflicto ocurre cuando dos ramas modificaron la misma línea (o líneas muy cercanas) de forma distinta, y Git no puede decidir solo cuál versión priorizar — a diferencia de cambios en partes distintas del archivo, que Git combina automáticamente sin problema. Vas a aprender a leer los marcadores que Git deja directamente en el archivo en conflicto: `<<<<<<< HEAD` marca el inicio de tu versión, `=======` separa ambas versiones, y `>>>>>>> nombre-rama` marca el final de la versión entrante.<br><br>Resolver un conflicto significa editar el archivo a mano: elegir una de las dos versiones, combinar ambas, o escribir una tercera que las reconcilie, y luego borrar los marcadores. Una vez resuelto, el flujo para completar el merge es el mismo que un commit normal: `git add archivo` (marcar el conflicto como resuelto) seguido de `git commit` (que por defecto genera un mensaje indicando que fue un merge). Los conflictos dan miedo al principio, pero con práctica se vuelven un trámite rutinario más que un problema grave.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(29,5,4,'Pull requests y code review','Un pull request propone fusionar una rama a otra y abre un espacio para que el equipo revise el código antes de aceptarlo: comentarios en líneas específicas del diff, pedidos de cambios (\"acá falta manejar el caso null\"), y aprobaciones explícitas antes de poder mergear — muchos equipos configuran GitHub para exigir al menos una aprobación antes de permitir el merge. El code review no es solo buscar errores: también difunde conocimiento del código entre el equipo y mantiene un estilo consistente.<br><br>Vas a practicar el flujo completo en GitHub: crear una rama, subir cambios con `git push`, abrir el pull request describiendo qué cambia y por qué, responder a los comentarios de revisión (haciendo más commits en la misma rama, que se agregan automáticamente al PR), y finalmente mergear una vez aprobado. Este flujo — rama, cambios, PR, revisión, merge — es el estándar de facto en el desarrollo de software profesional en equipo.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(30,5,5,'GitHub Actions básico','GitHub Actions permite automatizar tareas cuando pasa algo en el repositorio (un push, un pull request, una etiqueta nueva): correr los tests automáticamente en cada cambio, compilar el proyecto, verificar el formato del código, o desplegarlo a un servidor. Los workflows se definen en archivos YAML dentro de la carpeta `.github/workflows/` de tu repositorio — por convención, ese es el único lugar donde GitHub busca automáticamente estas configuraciones.<br><br>Vas a escribir tu primer workflow (por ejemplo `.github/workflows/ci.yml`) que corre en cada push, una introducción práctica a la integración continua (CI): la idea de que cada cambio se valida automáticamente (tests, build) antes de llegar a la rama principal, detectando errores apenas se introducen en vez de descubrirlos días después. Esta automatización es hoy un estándar en cualquier proyecto serio, chico o grande.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(31,6,0,'Introducción','Un algoritmo es una secuencia de pasos bien definida para resolver un problema, y una estructura de datos es la forma en que organizás la información para que esos pasos sean eficientes. Elegir bien entre las estructuras disponibles — según qué operaciones necesitás hacer con más frecuencia: ¿buscar?, ¿insertar?, ¿recorrer en orden?, ¿acceder por posición? — puede ser la diferencia entre un programa que responde en milisegundos y uno que tarda horas con exactamente los mismos datos, simplemente por haber elegido la estructura equivocada.<br><br>Este curso es también la preparación clásica para entrevistas técnicas de programación, donde estos temas (complejidad, estructuras, algoritmos de búsqueda y ordenamiento) son el contenido más evaluado en la industria. Vas a aprender no solo a implementar estas estructuras, sino sobre todo a razonar sobre CUÁNDO conviene usar cada una.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(32,6,1,'Complejidad algorítmica','La notación Big O (`O(1)`, `O(log n)`, `O(n)`, `O(n log n)`, `O(n²)`) describe cómo crece el tiempo (o la memoria) que necesita un algoritmo a medida que crecen los datos de entrada, ignorando constantes y detalles de implementación para enfocarse en la tendencia general. Un algoritmo `O(1)` (tiempo constante) tarda lo mismo sin importar cuántos datos haya; uno `O(n)` (lineal) revisa cada elemento una sola vez, así que duplicar los datos duplica el tiempo; uno `O(n²)` (cuadrático) — típico de bucles anidados que recorren los mismos n elementos dos veces — hace que duplicar los datos CUADRUPLIQUE el tiempo.<br><br>Vas a aprender a analizar un fragmento de código contando cuántas veces se ejecuta su operación más costosa en función del tamaño de la entrada, y a estimar su complejidad de memoria de la misma forma. Entender esto es crucial porque un algoritmo `O(n²)` que funciona perfectamente bien con 100 elementos puede volverse completamente inutilizable con un millón — un error de diseño que no se nota hasta que el sistema ya está en producción con datos reales.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(33,6,2,'Arrays, listas y pilas','Un array tiene acceso instantáneo por índice (`O(1)`) porque todos sus elementos están en posiciones de memoria contiguas y calculables, pero tiene tamaño fijo; una lista enlazada crece dinámicamente (cada elemento apunta al siguiente) pero accede secuencialmente (`O(n)`, hay que recorrer desde el principio para llegar a una posición). Una pila (stack) sigue el orden LIFO (\"last in, first out\": el último elemento en entrar es el primero en salir) — la operación para agregar un elemento se llama `push` y para quitarlo `pop` — e ideal para casos como deshacer acciones (el último cambio hecho es el primero en deshacerse) o el manejo de llamadas a funciones anidadas.<br><br>Una cola (queue) sigue el orden opuesto, FIFO (\"first in, first out\": el primero en entrar es el primero en salir), ideal para procesar tareas en el mismo orden en que llegaron — como una fila de impresión o una cola de mensajes. Vas a implementar ambas estructuras y reconocer en qué situaciones cada una modela mejor el problema que estás resolviendo.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(34,6,3,'Árboles y grafos','Un árbol binario de búsqueda organiza datos jerárquicamente: cada nodo tiene como máximo dos hijos, y mantiene la propiedad de que todo lo menor que un nodo está en su subárbol izquierdo y todo lo mayor en el derecho — eso permite buscar, insertar y borrar en `O(log n)` en el caso promedio, descartando la mitad de las opciones restantes en cada paso, igual que una búsqueda binaria. Un grafo generaliza esta idea de relaciones: está compuesto por nodos (o vértices, las entidades) y aristas (las conexiones entre ellos) — pensá en una red social (nodos = personas, aristas = amistades) o un mapa de rutas (nodos = ciudades, aristas = caminos).<br><br>Vas a implementar los dos recorridos clásicos de grafos y árboles: BFS (Breadth-First Search, explora nivel por nivel, ideal para encontrar el camino más corto en pasos) y DFS (Depth-First Search, se mete lo más profundo posible por una rama antes de retroceder, ideal para explorar todas las posibilidades o detectar ciclos). Estos dos recorridos son la base de una enorme cantidad de algoritmos más avanzados sobre grafos.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(35,6,4,'Búsqueda y ordenamiento','La búsqueda binaria (`O(log n)`) requiere datos ordenados de antemano y descarta la mitad del espacio de búsqueda restante en cada paso, comparando el elemento buscado contra el del medio — muchísimo más rápida que revisar elemento por elemento (`O(n)`) cuando los datos ya están ordenados. Vas a comparar algoritmos de ordenamiento clásicos: bubble sort (compara pares adyacentes y los intercambia repetidamente, simple de entender pero `O(n²)` en el peor caso — impráctico para datasets grandes), merge sort y quicksort (ambos `O(n log n)` en promedio, mucho más eficientes, la base de las funciones de ordenamiento de la mayoría de los lenguajes modernos).<br><br>Vas a entender POR QUÉ unos algoritmos son `O(n²)` y otros `O(n log n)` analizando su estructura (cuántas comparaciones hacen, cómo dividen el problema), y por qué esa diferencia importa muchísimo cuando el dataset crece de miles a millones de elementos: la diferencia entre segundos y horas de procesamiento.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38'),(36,6,5,'Algoritmos greedy y DP','Un algoritmo greedy toma en cada paso la decisión que parece mejor localmente, sin reconsiderarla después ni mirar el problema completo — funciona bien y es muy rápido para algunos problemas (como dar vuelto con la menor cantidad de monedas, con las denominaciones típicas), pero NO garantiza encontrar la solución óptima global para todos los problemas: a veces la mejor decisión local lleva a un resultado peor a largo plazo que una decisión aparentemente peor al principio.<br><br>La programación dinámica resuelve problemas complejos dividiéndolos en subproblemas más chicos y superpuestos, y guardando (\"memoizando\") los resultados ya calculados para no repetir el mismo trabajo una y otra vez — la diferencia entre un algoritmo que recalcula lo mismo exponencialmente muchas veces y uno que lo calcula una sola vez y lo reutiliza. Es la técnica detrás de problemas clásicos como la mochila (knapsack: elegir qué objetos llevar para maximizar valor sin exceder un peso límite) o la subsecuencia común más larga entre dos strings — problemas que a fuerza bruta serían intratables, pero que con programación dinámica se resuelven en tiempo razonable.',NULL,NULL,1,'2026-07-28 19:28:38','2026-07-28 19:28:38');
/*!40000 ALTER TABLE `curso_contenidos` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `cursos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cursos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `emoji` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `titulo` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `duracion` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `titulo_unique` (`titulo`),
  KEY `activo_idx` (`activo`),
  KEY `idx_cursos_titulo_activo` (`titulo`,`activo`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `cursos` WRITE;
/*!40000 ALTER TABLE `cursos` DISABLE KEYS */;
INSERT INTO `cursos` VALUES (1,'☕','Java desde Cero','Aprendé programación orientada a objetos con el lenguaje más usado en la industria.','8 semanas',1,'2026-07-28 19:27:49','2026-07-28 19:27:49'),(2,'🐍','Python para Principiantes','El lenguaje más amigable para comenzar. Ideal para automatización, datos y web.','6 semanas',1,'2026-07-28 19:27:49','2026-07-28 19:27:49'),(3,'🌐','Desarrollo Web Full Stack','Construí sitios modernos con HTML, CSS, JavaScript y una intro a frameworks.','10 semanas',1,'2026-07-28 19:27:49','2026-07-28 19:27:49'),(4,'🗄️','SQL y Bases de Datos','Diseñá y consultá bases de datos relacionales. Fundamento de toda aplicación.','5 semanas',1,'2026-07-28 19:27:49','2026-07-28 19:27:49'),(5,'🔧','Git y GitHub','Control de versiones profesional. Trabajá en equipo sin perder ningún cambio.','3 semanas',1,'2026-07-28 19:27:49','2026-07-28 19:27:49'),(6,'📊','Algoritmos y Estructuras de Datos','El corazón de la programación eficiente. Preparate para entrevistas técnicas.','7 semanas',1,'2026-07-28 19:27:49','2026-07-28 19:27:49');
/*!40000 ALTER TABLE `cursos` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `inscripciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inscripciones` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `curso_id` int NOT NULL,
  `fecha_inscripcion` datetime DEFAULT CURRENT_TIMESTAMP,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `leccion_actual` tinyint NOT NULL DEFAULT '0',
  `fecha_modificacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_inscripcion` (`usuario_id`,`curso_id`),
  KEY `curso_id` (`curso_id`),
  KEY `activo_idx` (`activo`),
  CONSTRAINT `fk_inscripciones_curso` FOREIGN KEY (`curso_id`) REFERENCES `cursos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_inscripciones_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `inscripciones` WRITE;
/*!40000 ALTER TABLE `inscripciones` DISABLE KEYS */;
INSERT INTO `inscripciones` VALUES (1,2,6,'2026-07-28 19:47:04',0,0,'2026-07-28 19:50:30'),(2,2,3,'2026-07-28 19:50:05',1,5,'2026-07-28 19:55:31'),(3,2,5,'2026-07-28 19:50:10',1,0,'2026-07-28 19:50:10'),(4,2,2,'2026-07-28 19:50:14',1,0,'2026-07-28 19:58:19'),(5,2,1,'2026-07-28 19:56:05',1,5,'2026-07-28 19:56:10');
/*!40000 ALTER TABLE `inscripciones` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `test_opciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test_opciones` (
  `id` int NOT NULL AUTO_INCREMENT,
  `pregunta_id` int NOT NULL,
  `texto` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `es_correcta` tinyint(1) NOT NULL DEFAULT '0',
  `orden` tinyint NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `pregunta_id` (`pregunta_id`),
  KEY `es_correcta_idx` (`es_correcta`),
  CONSTRAINT `fk_opciones_pregunta` FOREIGN KEY (`pregunta_id`) REFERENCES `test_preguntas` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=512 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `test_opciones` WRITE;
/*!40000 ALTER TABLE `test_opciones` DISABLE KEYS */;
INSERT INTO `test_opciones` VALUES (1,1,'Un compilador de Python',0,1,'2026-07-28 19:28:38'),(2,1,'Una máquina virtual que ejecuta el bytecode de Java',1,2,'2026-07-28 19:28:38'),(3,1,'Un editor de texto',0,3,'2026-07-28 19:28:38'),(4,1,'Una base de datos',0,4,'2026-07-28 19:28:38'),(5,2,'String',0,1,'2026-07-28 19:28:38'),(6,2,'int',1,2,'2026-07-28 19:28:38'),(7,2,'ArrayList',0,3,'2026-07-28 19:28:38'),(8,2,'Object',0,4,'2026-07-28 19:28:38'),(9,3,'java',0,1,'2026-07-28 19:28:38'),(10,3,'javac',1,2,'2026-07-28 19:28:38'),(11,3,'javap',0,3,'2026-07-28 19:28:38'),(12,3,'jar',0,4,'2026-07-28 19:28:38'),(13,4,'No hay diferencia',0,1,'2026-07-28 19:28:38'),(14,4,'== compara referencias, .equals() compara contenido',1,2,'2026-07-28 19:28:38'),(15,4,'== solo funciona con int',0,3,'2026-07-28 19:28:38'),(16,4,'.equals() siempre es más rápido',0,4,'2026-07-28 19:28:38'),(17,5,'if',0,1,'2026-07-28 19:28:38'),(18,5,'while',1,2,'2026-07-28 19:28:38'),(19,5,'class',0,3,'2026-07-28 19:28:38'),(20,5,'try',0,4,'2026-07-28 19:28:38'),(21,6,'Variables, funciones, clases',0,1,'2026-07-28 19:28:38'),(22,6,'Encapsulamiento, herencia, polimorfismo',1,2,'2026-07-28 19:28:38'),(23,6,'Compilación, ejecución, depuración',0,3,'2026-07-28 19:28:38'),(24,6,'Arrays, listas, mapas',0,4,'2026-07-28 19:28:38'),(25,7,'HashMap',0,1,'2026-07-28 19:28:38'),(26,7,'ArrayList',1,2,'2026-07-28 19:28:38'),(27,7,'HashSet',0,3,'2026-07-28 19:28:38'),(28,7,'Scanner',0,4,'2026-07-28 19:28:38'),(29,8,'Para acelerar el programa',0,1,'2026-07-28 19:28:38'),(30,8,'Para indicarle al compilador qué tipo de dato contiene la colección',1,2,'2026-07-28 19:28:38'),(31,8,'Para ordenar automáticamente los elementos',0,3,'2026-07-28 19:28:38'),(32,8,'Para conectar con la base de datos',0,4,'2026-07-28 19:28:38'),(33,9,'try/catch',1,1,'2026-07-28 19:28:38'),(34,9,'if/else',0,2,'2026-07-28 19:28:38'),(35,9,'switch/case',0,3,'2026-07-28 19:28:38'),(36,9,'for/each',0,4,'2026-07-28 19:28:38'),(37,10,'try-with-resources',1,1,'2026-07-28 19:28:38'),(38,10,'finally-only',0,2,'2026-07-28 19:28:38'),(39,10,'catch-all',0,3,'2026-07-28 19:28:38'),(40,10,'auto-close manual',0,4,'2026-07-28 19:28:38'),(41,11,'.java',0,1,'2026-07-28 19:28:38'),(42,11,'.class',1,2,'2026-07-28 19:28:38'),(43,11,'.jar',0,3,'2026-07-28 19:28:38'),(44,11,'.exe',0,4,'2026-07-28 19:28:38'),(45,12,'int',0,1,'2026-07-28 19:28:38'),(46,12,'double',1,2,'2026-07-28 19:28:38'),(47,12,'boolean',0,3,'2026-07-28 19:28:38'),(48,12,'char',0,4,'2026-07-28 19:28:38'),(49,13,'break',0,1,'2026-07-28 19:28:38'),(50,13,'continue',1,2,'2026-07-28 19:28:38'),(51,13,'return',0,3,'2026-07-28 19:28:38'),(52,13,'skip',0,4,'2026-07-28 19:28:38'),(53,14,'switch',1,1,'2026-07-28 19:28:38'),(54,14,'while',0,2,'2026-07-28 19:28:38'),(55,14,'try',0,3,'2026-07-28 19:28:38'),(56,14,'do',0,4,'2026-07-28 19:28:38'),(57,15,'public',0,1,'2026-07-28 19:28:38'),(58,15,'private',1,2,'2026-07-28 19:28:38'),(59,15,'static',0,3,'2026-07-28 19:28:38'),(60,15,'final',0,4,'2026-07-28 19:28:38'),(61,16,'@Override',1,1,'2026-07-28 19:28:38'),(62,16,'@Deprecated',0,2,'2026-07-28 19:28:38'),(63,16,'@FunctionalInterface',0,3,'2026-07-28 19:28:38'),(64,16,'@SuppressWarnings',0,4,'2026-07-28 19:28:38'),(65,17,'ArrayList',0,1,'2026-07-28 19:28:38'),(66,17,'HashSet',1,2,'2026-07-28 19:28:38'),(67,17,'LinkedList',0,3,'2026-07-28 19:28:38'),(68,17,'Stack',0,4,'2026-07-28 19:28:38'),(69,18,'filter()',0,1,'2026-07-28 19:28:38'),(70,18,'map()',1,2,'2026-07-28 19:28:38'),(71,18,'sort()',0,3,'2026-07-28 19:28:38'),(72,18,'collect()',0,4,'2026-07-28 19:28:38'),(73,19,'catch',0,1,'2026-07-28 19:28:38'),(74,19,'finally',1,2,'2026-07-28 19:28:38'),(75,19,'throw',0,3,'2026-07-28 19:28:38'),(76,19,'try',0,4,'2026-07-28 19:28:38'),(77,20,'Una excepción checked como SQLException',0,1,'2026-07-28 19:28:38'),(78,20,'Una excepción unchecked (RuntimeException)',1,2,'2026-07-28 19:28:38'),(79,20,'IOException',0,3,'2026-07-28 19:28:38'),(80,20,'Cualquier excepción declarada con throws',0,4,'2026-07-28 19:28:38'),(81,21,'Con llaves {}',0,1,'2026-07-28 19:28:38'),(82,21,'Con indentación',1,2,'2026-07-28 19:28:38'),(83,21,'Con paréntesis',0,3,'2026-07-28 19:28:38'),(84,21,'Con punto y coma',0,4,'2026-07-28 19:28:38'),(85,22,'type()',1,1,'2026-07-28 19:28:38'),(86,22,'typeof()',0,2,'2026-07-28 19:28:38'),(87,22,'class()',0,3,'2026-07-28 19:28:38'),(88,22,'kind()',0,4,'2026-07-28 19:28:38'),(89,23,'function nombre():',0,1,'2026-07-28 19:28:38'),(90,23,'def nombre():',1,2,'2026-07-28 19:28:38'),(91,23,'func nombre():',0,3,'2026-07-28 19:28:38'),(92,23,'void nombre():',0,4,'2026-07-28 19:28:38'),(93,24,'Lista',0,1,'2026-07-28 19:28:38'),(94,24,'Set',1,2,'2026-07-28 19:28:38'),(95,24,'Tupla',0,3,'2026-07-28 19:28:38'),(96,24,'Diccionario',0,4,'2026-07-28 19:28:38'),(97,25,'Es una forma compacta de crear una lista transformando otra',1,1,'2026-07-28 19:28:38'),(98,25,'Solo funciona con números',0,2,'2026-07-28 19:28:38'),(99,25,'Reemplaza a los diccionarios',0,3,'2026-07-28 19:28:38'),(100,25,'Es exclusiva de Python 2',0,4,'2026-07-28 19:28:38'),(101,26,'open() sin cierre',0,1,'2026-07-28 19:28:38'),(102,26,'with open(...) as f:',1,2,'2026-07-28 19:28:38'),(103,26,'file.read()',0,3,'2026-07-28 19:28:38'),(104,26,'import file',0,4,'2026-07-28 19:28:38'),(105,27,'npm',0,1,'2026-07-28 19:28:38'),(106,27,'pip',1,2,'2026-07-28 19:28:38'),(107,27,'maven',0,3,'2026-07-28 19:28:38'),(108,27,'composer',0,4,'2026-07-28 19:28:38'),(109,28,'Para aislar las dependencias de cada proyecto',1,1,'2026-07-28 19:28:38'),(110,28,'Para acelerar la ejecución del script',0,2,'2026-07-28 19:28:38'),(111,28,'Para compilar el código a bytecode',0,3,'2026-07-28 19:28:38'),(112,28,'Para conectar con GitHub',0,4,'2026-07-28 19:28:38'),(113,29,'list',0,1,'2026-07-28 19:28:38'),(114,29,'dict',1,2,'2026-07-28 19:28:38'),(115,29,'set',0,3,'2026-07-28 19:28:38'),(116,29,'tuple',0,4,'2026-07-28 19:28:38'),(117,30,'try/except',1,1,'2026-07-28 19:28:38'),(118,30,'try/catch',0,2,'2026-07-28 19:28:38'),(119,30,'on/error',0,3,'2026-07-28 19:28:38'),(120,30,'rescue/end',0,4,'2026-07-28 19:28:38'),(121,31,'echo()',0,1,'2026-07-28 19:28:38'),(122,31,'print()',1,2,'2026-07-28 19:28:38'),(123,31,'console.log()',0,3,'2026-07-28 19:28:38'),(124,31,'write()',0,4,'2026-07-28 19:28:38'),(125,32,'Con //',0,1,'2026-07-28 19:28:38'),(126,32,'Con #',1,2,'2026-07-28 19:28:38'),(127,32,'Con /* */',0,3,'2026-07-28 19:28:38'),(128,32,'Con --',0,4,'2026-07-28 19:28:38'),(129,33,'/',0,1,'2026-07-28 19:28:38'),(130,33,'//',1,2,'2026-07-28 19:28:38'),(131,33,'%',0,3,'2026-07-28 19:28:38'),(132,33,'**',0,4,'2026-07-28 19:28:38'),(133,34,'return',1,1,'2026-07-28 19:28:38'),(134,34,'yield solamente',0,2,'2026-07-28 19:28:38'),(135,34,'give',0,3,'2026-07-28 19:28:38'),(136,34,'back',0,4,'2026-07-28 19:28:38'),(137,35,'def f(x=10):',1,1,'2026-07-28 19:28:38'),(138,35,'def f(x default 10):',0,2,'2026-07-28 19:28:38'),(139,35,'def f(x: 10):',0,3,'2026-07-28 19:28:38'),(140,35,'def f(x -> 10):',0,4,'2026-07-28 19:28:38'),(141,36,'add()',0,1,'2026-07-28 19:28:38'),(142,36,'append()',1,2,'2026-07-28 19:28:38'),(143,36,'push()',0,3,'2026-07-28 19:28:38'),(144,36,'insert()',0,4,'2026-07-28 19:28:38'),(145,37,'diccionario[\"clave\"]',1,1,'2026-07-28 19:28:38'),(146,37,'diccionario.clave',0,2,'2026-07-28 19:28:38'),(147,37,'diccionario->clave',0,3,'2026-07-28 19:28:38'),(148,37,'diccionario(clave)',0,4,'2026-07-28 19:28:38'),(149,38,'except',0,1,'2026-07-28 19:28:38'),(150,38,'finally',1,2,'2026-07-28 19:28:38'),(151,38,'raise',0,3,'2026-07-28 19:28:38'),(152,38,'else',0,4,'2026-07-28 19:28:38'),(153,39,'package.json',0,1,'2026-07-28 19:28:38'),(154,39,'requirements.txt',1,2,'2026-07-28 19:28:38'),(155,39,'pom.xml',0,3,'2026-07-28 19:28:38'),(156,39,'Gemfile',0,4,'2026-07-28 19:28:38'),(157,40,'pip list',1,1,'2026-07-28 19:28:38'),(158,40,'pip show-all',0,2,'2026-07-28 19:28:38'),(159,40,'pip packages',0,3,'2026-07-28 19:28:38'),(160,40,'python -m list',0,4,'2026-07-28 19:28:38'),(161,41,'CSS',0,1,'2026-07-28 19:28:38'),(162,41,'HTML',1,2,'2026-07-28 19:28:38'),(163,41,'JavaScript',0,3,'2026-07-28 19:28:38'),(164,41,'SQL',0,4,'2026-07-28 19:28:38'),(165,42,'<bottom>',0,1,'2026-07-28 19:28:38'),(166,42,'<footer>',1,2,'2026-07-28 19:28:38'),(167,42,'<end>',0,3,'2026-07-28 19:28:38'),(168,42,'<section>',0,4,'2026-07-28 19:28:38'),(169,43,'display: flex',1,1,'2026-07-28 19:28:38'),(170,43,'position: flex',0,2,'2026-07-28 19:28:38'),(171,43,'layout: flex',0,3,'2026-07-28 19:28:38'),(172,43,'float: flex',0,4,'2026-07-28 19:28:38'),(173,44,'let/const',1,1,'2026-07-28 19:28:38'),(174,44,'def',0,2,'2026-07-28 19:28:38'),(175,44,'dim',0,3,'2026-07-28 19:28:38'),(176,44,'auto',0,4,'2026-07-28 19:28:38'),(177,45,'Un lenguaje de programación',0,1,'2026-07-28 19:28:38'),(178,45,'La representación en memoria del HTML que JavaScript puede leer y modificar',1,2,'2026-07-28 19:28:38'),(179,45,'Un framework de CSS',0,3,'2026-07-28 19:28:38'),(180,45,'Un tipo de base de datos',0,4,'2026-07-28 19:28:38'),(181,46,'.addEventListener(\"click\", ...)',1,1,'2026-07-28 19:28:38'),(182,46,'.onPress()',0,2,'2026-07-28 19:28:38'),(183,46,'.clickNow()',0,3,'2026-07-28 19:28:38'),(184,46,'.trigger()',0,4,'2026-07-28 19:28:38'),(185,47,'useEffect',0,1,'2026-07-28 19:28:38'),(186,47,'useState',1,2,'2026-07-28 19:28:38'),(187,47,'useRef',0,3,'2026-07-28 19:28:38'),(188,47,'useMemo',0,4,'2026-07-28 19:28:38'),(189,48,'Flexbox',0,1,'2026-07-28 19:28:38'),(190,48,'Grid',1,2,'2026-07-28 19:28:38'),(191,48,'Float',0,3,'2026-07-28 19:28:38'),(192,48,'Table',0,4,'2026-07-28 19:28:38'),(193,49,'async/await',1,1,'2026-07-28 19:28:38'),(194,49,'sync/wait',0,2,'2026-07-28 19:28:38'),(195,49,'defer/now',0,3,'2026-07-28 19:28:38'),(196,49,'thread/join',0,4,'2026-07-28 19:28:38'),(197,50,'props',1,1,'2026-07-28 19:28:38'),(198,50,'tags',0,2,'2026-07-28 19:28:38'),(199,50,'sockets',0,3,'2026-07-28 19:28:38'),(200,50,'queries',0,4,'2026-07-28 19:28:38'),(201,51,'HTML',0,1,'2026-07-28 19:28:38'),(202,51,'CSS',0,2,'2026-07-28 19:28:38'),(203,51,'JavaScript',1,3,'2026-07-28 19:28:38'),(204,51,'SQL',0,4,'2026-07-28 19:28:38'),(205,52,'<nav>',1,1,'2026-07-28 19:28:38'),(206,52,'<menu-bar>',0,2,'2026-07-28 19:28:38'),(207,52,'<links>',0,3,'2026-07-28 19:28:38'),(208,52,'<header-nav>',0,4,'2026-07-28 19:28:38'),(209,53,'src',0,1,'2026-07-28 19:28:38'),(210,53,'alt',1,2,'2026-07-28 19:28:38'),(211,53,'title solamente',0,3,'2026-07-28 19:28:38'),(212,53,'name',0,4,'2026-07-28 19:28:38'),(213,54,'margin',0,1,'2026-07-28 19:28:38'),(214,54,'padding',1,2,'2026-07-28 19:28:38'),(215,54,'border',0,3,'2026-07-28 19:28:38'),(216,54,'gap',0,4,'2026-07-28 19:28:38'),(217,55,'Media queries',1,1,'2026-07-28 19:28:38'),(218,55,'Pseudo-clases',0,2,'2026-07-28 19:28:38'),(219,55,'Variables CSS',0,3,'2026-07-28 19:28:38'),(220,55,'Floats',0,4,'2026-07-28 19:28:38'),(221,56,'Destructuring',1,1,'2026-07-28 19:28:38'),(222,56,'Hoisting',0,2,'2026-07-28 19:28:38'),(223,56,'Currying',0,3,'2026-07-28 19:28:38'),(224,56,'Bubbling',0,4,'2026-07-28 19:28:38'),(225,57,'Promise',1,1,'2026-07-28 19:28:38'),(226,57,'Callback',0,2,'2026-07-28 19:28:38'),(227,57,'Observer',0,3,'2026-07-28 19:28:38'),(228,57,'Thread',0,4,'2026-07-28 19:28:38'),(229,58,'querySelector()',1,1,'2026-07-28 19:28:38'),(230,58,'getElement()',0,2,'2026-07-28 19:28:38'),(231,58,'findById()',0,3,'2026-07-28 19:28:38'),(232,58,'selectOne()',0,4,'2026-07-28 19:28:38'),(233,59,'useState',0,1,'2026-07-28 19:28:38'),(234,59,'useEffect',1,2,'2026-07-28 19:28:38'),(235,59,'useContext',0,3,'2026-07-28 19:28:38'),(236,59,'useReducer',0,4,'2026-07-28 19:28:38'),(237,60,'JSX',1,1,'2026-07-28 19:28:38'),(238,60,'HTMLX',0,2,'2026-07-28 19:28:38'),(239,60,'TSX puro',0,3,'2026-07-28 19:28:38'),(240,60,'Markup',0,4,'2026-07-28 19:28:38'),(241,61,'MAKE TABLE',0,1,'2026-07-28 19:28:38'),(242,61,'CREATE TABLE',1,2,'2026-07-28 19:28:38'),(243,61,'NEW TABLE',0,3,'2026-07-28 19:28:38'),(244,61,'BUILD TABLE',0,4,'2026-07-28 19:28:38'),(245,62,'WHERE',1,1,'2026-07-28 19:28:38'),(246,62,'FILTER',0,2,'2026-07-28 19:28:38'),(247,62,'HAVING solamente',0,3,'2026-07-28 19:28:38'),(248,62,'ORDER BY',0,4,'2026-07-28 19:28:38'),(249,63,'LEFT JOIN',0,1,'2026-07-28 19:28:38'),(250,63,'INNER JOIN',1,2,'2026-07-28 19:28:38'),(251,63,'FULL JOIN',0,3,'2026-07-28 19:28:38'),(252,63,'CROSS JOIN',0,4,'2026-07-28 19:28:38'),(253,64,'COUNT()',1,1,'2026-07-28 19:28:38'),(254,64,'SUM()',0,2,'2026-07-28 19:28:38'),(255,64,'TOTAL()',0,3,'2026-07-28 19:28:38'),(256,64,'LEN()',0,4,'2026-07-28 19:28:38'),(257,65,'Para acelerar búsquedas por una columna',1,1,'2026-07-28 19:28:38'),(258,65,'Para borrar duplicados automáticamente',0,2,'2026-07-28 19:28:38'),(259,65,'Para encriptar los datos',0,3,'2026-07-28 19:28:38'),(260,65,'Para definir el charset de la tabla',0,4,'2026-07-28 19:28:38'),(261,66,'EXPLAIN',1,1,'2026-07-28 19:28:38'),(262,66,'DESCRIBE PLAN',0,2,'2026-07-28 19:28:38'),(263,66,'SHOW EXECUTION',0,3,'2026-07-28 19:28:38'),(264,66,'ANALYZE ONLY',0,4,'2026-07-28 19:28:38'),(265,67,'FOREIGN KEY',0,1,'2026-07-28 19:28:38'),(266,67,'PRIMARY KEY',1,2,'2026-07-28 19:28:38'),(267,67,'UNIQUE INDEX',0,3,'2026-07-28 19:28:38'),(268,67,'CHECK KEY',0,4,'2026-07-28 19:28:38'),(269,68,'GROUP BY',1,1,'2026-07-28 19:28:38'),(270,68,'ORDER BY',0,2,'2026-07-28 19:28:38'),(271,68,'JOIN BY',0,3,'2026-07-28 19:28:38'),(272,68,'PARTITION ONLY',0,4,'2026-07-28 19:28:38'),(273,69,'Un stored procedure',1,1,'2026-07-28 19:28:38'),(274,69,'Un índice',0,2,'2026-07-28 19:28:38'),(275,69,'Una vista materializada',0,3,'2026-07-28 19:28:38'),(276,69,'Un trigger obligatorio',0,4,'2026-07-28 19:28:38'),(277,70,'START TRANSACTION / COMMIT / ROLLBACK',1,1,'2026-07-28 19:28:38'),(278,70,'BEGIN / END',0,2,'2026-07-28 19:28:38'),(279,70,'LOCK / UNLOCK',0,3,'2026-07-28 19:28:38'),(280,70,'OPEN / CLOSE',0,4,'2026-07-28 19:28:38'),(281,71,'La normalización',1,1,'2026-07-28 19:28:38'),(282,71,'La indexación total',0,2,'2026-07-28 19:28:38'),(283,71,'El uso de stored procedures',0,3,'2026-07-28 19:28:38'),(284,71,'El uso de transacciones',0,4,'2026-07-28 19:28:38'),(285,72,'DELETE TABLE',0,1,'2026-07-28 19:28:38'),(286,72,'DROP TABLE',1,2,'2026-07-28 19:28:38'),(287,72,'REMOVE TABLE',0,3,'2026-07-28 19:28:38'),(288,72,'TRUNCATE ROW',0,4,'2026-07-28 19:28:38'),(289,73,'NOT NULL',0,1,'2026-07-28 19:28:38'),(290,73,'UNIQUE',1,2,'2026-07-28 19:28:38'),(291,73,'DEFAULT',0,3,'2026-07-28 19:28:38'),(292,73,'CHECK',0,4,'2026-07-28 19:28:38'),(293,74,'INNER JOIN',0,1,'2026-07-28 19:28:38'),(294,74,'LEFT JOIN',1,2,'2026-07-28 19:28:38'),(295,74,'CROSS JOIN',0,3,'2026-07-28 19:28:38'),(296,74,'SELF JOIN',0,4,'2026-07-28 19:28:38'),(297,75,'LIMIT',1,1,'2026-07-28 19:28:38'),(298,75,'TOP ONLY',0,2,'2026-07-28 19:28:38'),(299,75,'MAX ROWS',0,3,'2026-07-28 19:28:38'),(300,75,'CAP',0,4,'2026-07-28 19:28:38'),(301,76,'AVG()',1,1,'2026-07-28 19:28:38'),(302,76,'MEAN()',0,2,'2026-07-28 19:28:38'),(303,76,'SUM()/COUNT() manual únicamente',0,3,'2026-07-28 19:28:38'),(304,76,'MID()',0,4,'2026-07-28 19:28:38'),(305,77,'WHERE',0,1,'2026-07-28 19:28:38'),(306,77,'HAVING',1,2,'2026-07-28 19:28:38'),(307,77,'FILTER BY',0,3,'2026-07-28 19:28:38'),(308,77,'ON',0,4,'2026-07-28 19:28:38'),(309,78,'Las lecturas se vuelven más lentas',0,1,'2026-07-28 19:28:38'),(310,78,'Las escrituras (INSERT/UPDATE/DELETE) se vuelven más lentas',1,2,'2026-07-28 19:28:38'),(311,78,'La tabla deja de aceptar claves foráneas',0,3,'2026-07-28 19:28:38'),(312,78,'No tiene ningún efecto secundario',0,4,'2026-07-28 19:28:38'),(313,79,'COMMIT',1,1,'2026-07-28 19:28:38'),(314,79,'ROLLBACK',0,2,'2026-07-28 19:28:38'),(315,79,'SAVE',0,3,'2026-07-28 19:28:38'),(316,79,'CONFIRM',0,4,'2026-07-28 19:28:38'),(317,80,'ROLLBACK',1,1,'2026-07-28 19:28:38'),(318,80,'COMMIT',0,2,'2026-07-28 19:28:38'),(319,80,'UNDO',0,3,'2026-07-28 19:28:38'),(320,80,'RESET',0,4,'2026-07-28 19:28:38'),(321,81,'git save',0,1,'2026-07-28 19:28:38'),(322,81,'git commit',1,2,'2026-07-28 19:28:38'),(323,81,'git push',0,3,'2026-07-28 19:28:38'),(324,81,'git add',0,4,'2026-07-28 19:28:38'),(325,82,'git add',1,1,'2026-07-28 19:28:38'),(326,82,'git stage',0,2,'2026-07-28 19:28:38'),(327,82,'git commit',0,3,'2026-07-28 19:28:38'),(328,82,'git track',0,4,'2026-07-28 19:28:38'),(329,83,'Una copia de seguridad del repositorio',0,1,'2026-07-28 19:28:38'),(330,83,'Una línea de desarrollo independiente',1,2,'2026-07-28 19:28:38'),(331,83,'Un archivo de configuración',0,3,'2026-07-28 19:28:38'),(332,83,'Un tipo de commit especial',0,4,'2026-07-28 19:28:38'),(333,84,'Git lo resuelve siempre solo',0,1,'2026-07-28 19:28:38'),(334,84,'Se genera un conflicto que hay que resolver a mano',1,2,'2026-07-28 19:28:38'),(335,84,'Se pierden los cambios de una rama automáticamente',0,3,'2026-07-28 19:28:38'),(336,84,'El merge queda cancelado para siempre',0,4,'2026-07-28 19:28:38'),(337,85,'Un comando de Git',0,1,'2026-07-28 19:28:38'),(338,85,'Una propuesta para fusionar una rama, abierta a revisión del equipo',1,2,'2026-07-28 19:28:38'),(339,85,'Una forma de borrar una rama',0,3,'2026-07-28 19:28:38'),(340,85,'Un tipo de commit',0,4,'2026-07-28 19:28:38'),(341,86,'GitHub Actions',1,1,'2026-07-28 19:28:38'),(342,86,'Git Desktop',0,2,'2026-07-28 19:28:38'),(343,86,'GitHub Pages',0,3,'2026-07-28 19:28:38'),(344,86,'Git LFS',0,4,'2026-07-28 19:28:38'),(345,87,'git log',1,1,'2026-07-28 19:28:38'),(346,87,'git history',0,2,'2026-07-28 19:28:38'),(347,87,'git show-all',0,3,'2026-07-28 19:28:38'),(348,87,'git commits',0,4,'2026-07-28 19:28:38'),(349,88,'<<<<<<<',0,1,'2026-07-28 19:28:38'),(350,88,'=======',0,2,'2026-07-28 19:28:38'),(351,88,'>>>>>>>',0,3,'2026-07-28 19:28:38'),(352,88,'#########',1,4,'2026-07-28 19:28:38'),(353,89,'git switch',1,1,'2026-07-28 19:28:38'),(354,89,'git move',0,2,'2026-07-28 19:28:38'),(355,89,'git jump',0,3,'2026-07-28 19:28:38'),(356,89,'git go',0,4,'2026-07-28 19:28:38'),(357,90,'Es lo mismo que Git',0,1,'2026-07-28 19:28:38'),(358,90,'Un servicio online que aloja repositorios Git y agrega colaboración',1,2,'2026-07-28 19:28:38'),(359,90,'Un lenguaje de programación',0,3,'2026-07-28 19:28:38'),(360,90,'Una base de datos',0,4,'2026-07-28 19:28:38'),(361,91,'git init',1,1,'2026-07-28 19:28:38'),(362,91,'git start',0,2,'2026-07-28 19:28:38'),(363,91,'git new',0,3,'2026-07-28 19:28:38'),(364,91,'git create',0,4,'2026-07-28 19:28:38'),(365,92,'git clone',1,1,'2026-07-28 19:28:38'),(366,92,'git download',0,2,'2026-07-28 19:28:38'),(367,92,'git copy',0,3,'2026-07-28 19:28:38'),(368,92,'git fork',0,4,'2026-07-28 19:28:38'),(369,93,'git status',1,1,'2026-07-28 19:28:38'),(370,93,'git diff --files',0,2,'2026-07-28 19:28:38'),(371,93,'git info',0,3,'2026-07-28 19:28:38'),(372,93,'git changes',0,4,'2026-07-28 19:28:38'),(373,94,'git branch nombre-rama',1,1,'2026-07-28 19:28:38'),(374,94,'git new-branch nombre-rama',0,2,'2026-07-28 19:28:38'),(375,94,'git create branch nombre-rama',0,3,'2026-07-28 19:28:38'),(376,94,'git checkout new nombre-rama',0,4,'2026-07-28 19:28:38'),(377,95,'git merge nombre-rama',1,1,'2026-07-28 19:28:38'),(378,95,'git join nombre-rama',0,2,'2026-07-28 19:28:38'),(379,95,'git combine nombre-rama',0,3,'2026-07-28 19:28:38'),(380,95,'git sync nombre-rama',0,4,'2026-07-28 19:28:38'),(381,96,'git add archivo y luego git commit',1,1,'2026-07-28 19:28:38'),(382,96,'Borrar la rama en conflicto',0,2,'2026-07-28 19:28:38'),(383,96,'git reset --hard inmediatamente',0,3,'2026-07-28 19:28:38'),(384,96,'No hace falta hacer nada más',0,4,'2026-07-28 19:28:38'),(385,97,'Comentarios y pedidos de cambios sobre el código propuesto',1,1,'2026-07-28 19:28:38'),(386,97,'Borrar automáticamente commits antiguos',0,2,'2026-07-28 19:28:38'),(387,97,'Fusionar ramas sin revisión',0,3,'2026-07-28 19:28:38'),(388,97,'Encriptar el repositorio',0,4,'2026-07-28 19:28:38'),(389,98,'.github/workflows',1,1,'2026-07-28 19:28:38'),(390,98,'.git/actions',0,2,'2026-07-28 19:28:38'),(391,98,'ci/',0,3,'2026-07-28 19:28:38'),(392,98,'.actions/',0,4,'2026-07-28 19:28:38'),(393,99,'git push',1,1,'2026-07-28 19:28:38'),(394,99,'git upload',0,2,'2026-07-28 19:28:38'),(395,99,'git send',0,3,'2026-07-28 19:28:38'),(396,99,'git commit --remote',0,4,'2026-07-28 19:28:38'),(397,100,'git fetch',1,1,'2026-07-28 19:28:38'),(398,100,'git pull --no-merge',0,2,'2026-07-28 19:28:38'),(399,100,'git sync',0,3,'2026-07-28 19:28:38'),(400,100,'git download',0,4,'2026-07-28 19:28:38'),(401,101,'Notación Big O',1,1,'2026-07-28 19:28:38'),(402,101,'Notación binaria',0,2,'2026-07-28 19:28:38'),(403,101,'Notación UML',0,3,'2026-07-28 19:28:38'),(404,101,'Notación SQL',0,4,'2026-07-28 19:28:38'),(405,102,'O(n)',0,1,'2026-07-28 19:28:38'),(406,102,'O(1)',1,2,'2026-07-28 19:28:38'),(407,102,'O(log n)',0,3,'2026-07-28 19:28:38'),(408,102,'O(n²)',0,4,'2026-07-28 19:28:38'),(409,103,'Cola (queue)',0,1,'2026-07-28 19:28:38'),(410,103,'Pila (stack)',1,2,'2026-07-28 19:28:38'),(411,103,'Árbol',0,3,'2026-07-28 19:28:38'),(412,103,'Grafo',0,4,'2026-07-28 19:28:38'),(413,104,'DFS',0,1,'2026-07-28 19:28:38'),(414,104,'BFS',1,2,'2026-07-28 19:28:38'),(415,104,'Backtracking',0,3,'2026-07-28 19:28:38'),(416,104,'Greedy',0,4,'2026-07-28 19:28:38'),(417,105,'Que los datos estén ordenados',1,1,'2026-07-28 19:28:38'),(418,105,'Que los datos sean todos números',0,2,'2026-07-28 19:28:38'),(419,105,'Que la lista tenga menos de 100 elementos',0,3,'2026-07-28 19:28:38'),(420,105,'Que no haya duplicados',0,4,'2026-07-28 19:28:38'),(421,106,'O(n²)',0,1,'2026-07-28 19:28:38'),(422,106,'O(n log n)',1,2,'2026-07-28 19:28:38'),(423,106,'O(2^n)',0,3,'2026-07-28 19:28:38'),(424,106,'O(1)',0,4,'2026-07-28 19:28:38'),(425,107,'Programación dinámica',1,1,'2026-07-28 19:28:38'),(426,107,'Recursión simple',0,2,'2026-07-28 19:28:38'),(427,107,'Fuerza bruta',0,3,'2026-07-28 19:28:38'),(428,107,'Ordenamiento burbuja',0,4,'2026-07-28 19:28:38'),(429,108,'Prueba todas las combinaciones posibles',0,1,'2026-07-28 19:28:38'),(430,108,'Toma en cada paso la decisión que parece mejor localmente',1,2,'2026-07-28 19:28:38'),(431,108,'Siempre encuentra la solución óptima global',0,3,'2026-07-28 19:28:38'),(432,108,'Solo funciona con grafos',0,4,'2026-07-28 19:28:38'),(433,109,'Lista enlazada',0,1,'2026-07-28 19:28:38'),(434,109,'Árbol binario de búsqueda',1,2,'2026-07-28 19:28:38'),(435,109,'Array sin ordenar',0,3,'2026-07-28 19:28:38'),(436,109,'Pila',0,4,'2026-07-28 19:28:38'),(437,110,'La mochila (knapsack)',1,1,'2026-07-28 19:28:38'),(438,110,'Ordenar un array ya ordenado',0,2,'2026-07-28 19:28:38'),(439,110,'Imprimir \"Hola Mundo\"',0,3,'2026-07-28 19:28:38'),(440,110,'Sumar dos números',0,4,'2026-07-28 19:28:38'),(441,111,'La eficiencia de las operaciones que necesitás hacer con más frecuencia',1,1,'2026-07-28 19:28:38'),(442,111,'El nombre de la variable',0,2,'2026-07-28 19:28:38'),(443,111,'El lenguaje de programación usado',0,3,'2026-07-28 19:28:38'),(444,111,'La cantidad de comentarios en el código',0,4,'2026-07-28 19:28:38'),(445,112,'O(1)',0,1,'2026-07-28 19:28:38'),(446,112,'O(n)',1,2,'2026-07-28 19:28:38'),(447,112,'O(n²)',0,3,'2026-07-28 19:28:38'),(448,112,'O(2^n)',0,4,'2026-07-28 19:28:38'),(449,113,'O(log n)',0,1,'2026-07-28 19:28:38'),(450,113,'O(n²)',1,2,'2026-07-28 19:28:38'),(451,113,'O(n)',0,3,'2026-07-28 19:28:38'),(452,113,'O(1)',0,4,'2026-07-28 19:28:38'),(453,114,'Pila (stack)',0,1,'2026-07-28 19:28:38'),(454,114,'Cola (queue)',1,2,'2026-07-28 19:28:38'),(455,114,'Árbol',0,3,'2026-07-28 19:28:38'),(456,114,'Grafo',0,4,'2026-07-28 19:28:38'),(457,115,'push',1,1,'2026-07-28 19:28:38'),(458,115,'pop',0,2,'2026-07-28 19:28:38'),(459,115,'peek',0,3,'2026-07-28 19:28:38'),(460,115,'enqueue',0,4,'2026-07-28 19:28:38'),(461,116,'BFS',0,1,'2026-07-28 19:28:38'),(462,116,'DFS',1,2,'2026-07-28 19:28:38'),(463,116,'Ordenamiento topológico',0,3,'2026-07-28 19:28:38'),(464,116,'Búsqueda binaria',0,4,'2026-07-28 19:28:38'),(465,117,'Filas y columnas',0,1,'2026-07-28 19:28:38'),(466,117,'Nodos (vértices) y aristas',1,2,'2026-07-28 19:28:38'),(467,117,'Claves y valores',0,3,'2026-07-28 19:28:38'),(468,117,'Índices y punteros únicamente',0,4,'2026-07-28 19:28:38'),(469,118,'Merge sort',0,1,'2026-07-28 19:28:38'),(470,118,'Bubble sort',1,2,'2026-07-28 19:28:38'),(471,118,'Quicksort en el caso promedio',0,3,'2026-07-28 19:28:38'),(472,118,'Búsqueda binaria',0,4,'2026-07-28 19:28:38'),(473,119,'Programación dinámica',0,1,'2026-07-28 19:28:38'),(474,119,'Algoritmo greedy',1,2,'2026-07-28 19:28:38'),(475,119,'Búsqueda binaria',0,3,'2026-07-28 19:28:38'),(476,119,'BFS',0,4,'2026-07-28 19:28:38'),(477,120,'Programación dinámica (memoización)',1,1,'2026-07-28 19:28:38'),(478,120,'Ordenamiento burbuja',0,2,'2026-07-28 19:28:38'),(479,120,'Búsqueda lineal',0,3,'2026-07-28 19:28:38'),(480,120,'Algoritmo greedy',0,4,'2026-07-28 19:28:38');
/*!40000 ALTER TABLE `test_opciones` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `test_preguntas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test_preguntas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `curso_id` int NOT NULL,
  `enunciado` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `orden` tinyint NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_pregunta` (`curso_id`,`orden`),
  KEY `activo_idx` (`activo`),
  CONSTRAINT `fk_preguntas_curso` FOREIGN KEY (`curso_id`) REFERENCES `cursos` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `test_preguntas` WRITE;
/*!40000 ALTER TABLE `test_preguntas` DISABLE KEYS */;
INSERT INTO `test_preguntas` VALUES (1,1,'¿Qué es la JVM?',1,1,'2026-07-28 19:28:38'),(2,1,'¿Cuál de los siguientes es un tipo primitivo en Java?',2,1,'2026-07-28 19:28:38'),(3,1,'¿Qué comando compila un archivo Java?',3,1,'2026-07-28 19:28:38'),(4,1,'¿Cuál es la diferencia principal entre == y .equals() al comparar objetos?',4,1,'2026-07-28 19:28:38'),(5,1,'¿Qué estructura se usa para repetir un bloque de código mientras se cumpla una condición?',5,1,'2026-07-28 19:28:38'),(6,1,'¿Cuáles son los tres pilares clásicos de la Programación Orientada a Objetos?',6,1,'2026-07-28 19:28:38'),(7,1,'¿Qué clase de java.util se usa para una lista dinámica?',7,1,'2026-07-28 19:28:38'),(8,1,'¿Para qué sirven los generics (por ejemplo List<String>)?',8,1,'2026-07-28 19:28:38'),(9,1,'¿Qué bloque se usa para manejar errores esperables en Java?',9,1,'2026-07-28 19:28:38'),(10,1,'¿Qué patrón cierra automáticamente un recurso aunque ocurra un error?',10,1,'2026-07-28 19:28:38'),(11,1,'¿Qué extensión tienen los archivos que genera javac al compilar?',11,1,'2026-07-28 19:28:38'),(12,1,'¿Qué tipo de dato es el estándar en Java para números con decimales de doble precisión?',12,1,'2026-07-28 19:28:38'),(13,1,'¿Qué palabra clave salta el resto de la vuelta actual de un bucle y continúa con la siguiente?',13,1,'2026-07-28 19:28:38'),(14,1,'¿Qué instrucción permite elegir entre múltiples casos según el valor de una variable?',14,1,'2026-07-28 19:28:38'),(15,1,'¿Qué modificador de acceso hace que un atributo solo sea visible dentro de la propia clase?',15,1,'2026-07-28 19:28:38'),(16,1,'¿Qué anotación se usa para indicar que un método sobrescribe uno de la superclase?',16,1,'2026-07-28 19:28:38'),(17,1,'¿Qué colección de java.util no permite elementos duplicados?',17,1,'2026-07-28 19:28:38'),(18,1,'¿Qué operación de Streams se usa para transformar cada elemento de una colección?',18,1,'2026-07-28 19:28:38'),(19,1,'¿Qué bloque se ejecuta siempre, haya o no una excepción?',19,1,'2026-07-28 19:28:38'),(20,1,'¿Qué tipo de excepción NO obliga a declararla con throws ni a capturarla?',20,1,'2026-07-28 19:28:38'),(21,2,'¿Cómo delimita Python los bloques de código?',1,1,'2026-07-28 19:28:38'),(22,2,'¿Qué función se usa para saber el tipo de una variable?',2,1,'2026-07-28 19:28:38'),(23,2,'¿Cómo se define una función en Python?',3,1,'2026-07-28 19:28:38'),(24,2,'¿Qué estructura de datos no permite elementos duplicados?',4,1,'2026-07-28 19:28:38'),(25,2,'¿Qué caracteriza a una comprensión de listas (list comprehension)?',5,1,'2026-07-28 19:28:38'),(26,2,'¿Qué instrucción abre un archivo y lo cierra automáticamente al salir del bloque?',6,1,'2026-07-28 19:28:38'),(27,2,'¿Qué gestor de paquetes se usa para instalar librerías en Python?',7,1,'2026-07-28 19:28:38'),(28,2,'¿Para qué sirve un entorno virtual (venv)?',8,1,'2026-07-28 19:28:38'),(29,2,'¿Qué tipo de dato representa clave-valor en Python?',9,1,'2026-07-28 19:28:38'),(30,2,'¿Qué bloque se usa para capturar errores en Python?',10,1,'2026-07-28 19:28:38'),(31,2,'¿Qué función se usa para imprimir texto en la consola?',11,1,'2026-07-28 19:28:38'),(32,2,'¿Cómo se escribe un comentario de una línea en Python?',12,1,'2026-07-28 19:28:38'),(33,2,'¿Qué operador se usa para la división entera en Python?',13,1,'2026-07-28 19:28:38'),(34,2,'¿Qué palabra clave se usa para devolver un valor desde una función?',14,1,'2026-07-28 19:28:38'),(35,2,'¿Cómo se le da un valor por defecto a un parámetro de función?',15,1,'2026-07-28 19:28:38'),(36,2,'¿Qué método agrega un elemento al final de una lista?',16,1,'2026-07-28 19:28:38'),(37,2,'¿Cómo se accede al valor de una clave en un diccionario?',17,1,'2026-07-28 19:28:38'),(38,2,'¿Qué bloque se ejecuta siempre, haya o no una excepción?',18,1,'2026-07-28 19:28:38'),(39,2,'¿Qué archivo lista las dependencias exactas de un proyecto Python?',19,1,'2026-07-28 19:28:38'),(40,2,'¿Qué comando lista los paquetes instalados con pip?',20,1,'2026-07-28 19:28:38'),(41,3,'¿Qué lenguaje define la estructura del contenido en la web?',1,1,'2026-07-28 19:28:38'),(42,3,'¿Qué etiqueta HTML5 semántica se usa para el pie de página?',2,1,'2026-07-28 19:28:38'),(43,3,'¿Qué propiedad CSS activa el modelo de layout Flexbox?',3,1,'2026-07-28 19:28:38'),(44,3,'Desde ES6, ¿qué palabras clave reemplazan a var para declarar variables?',4,1,'2026-07-28 19:28:38'),(45,3,'¿Qué es el DOM?',5,1,'2026-07-28 19:28:38'),(46,3,'¿Qué método se usa para reaccionar a un click en JavaScript?',6,1,'2026-07-28 19:28:38'),(47,3,'En React, ¿qué hook se usa para manejar estado en un componente?',7,1,'2026-07-28 19:28:38'),(48,3,'¿Qué modelo de layout CSS resuelve distribuciones bidimensionales (filas y columnas a la vez)?',8,1,'2026-07-28 19:28:38'),(49,3,'¿Qué palabras clave modernas de JavaScript permiten trabajar con asincronía de forma más legible?',9,1,'2026-07-28 19:28:38'),(50,3,'¿Qué reciben los componentes de React para configurarse desde afuera?',10,1,'2026-07-28 19:28:38'),(51,3,'¿Qué lenguaje agrega comportamiento e interactividad a una página web?',11,1,'2026-07-28 19:28:38'),(52,3,'¿Qué etiqueta HTML5 semántica se usa para la barra de navegación?',12,1,'2026-07-28 19:28:38'),(53,3,'¿Qué atributo HTML mejora la accesibilidad de una imagen?',13,1,'2026-07-28 19:28:38'),(54,3,'¿Qué propiedad CSS define el espacio interno de un elemento?',14,1,'2026-07-28 19:28:38'),(55,3,'¿Qué técnica CSS permite adaptar el diseño a distintos tamaños de pantalla?',15,1,'2026-07-28 19:28:38'),(56,3,'¿Qué sintaxis de ES6+ permite extraer valores de un objeto o array en variables individuales?',16,1,'2026-07-28 19:28:38'),(57,3,'¿Qué objeto de JavaScript representa una operación asíncrona pendiente?',17,1,'2026-07-28 19:28:38'),(58,3,'¿Qué método selecciona un único elemento del DOM por su selector CSS?',18,1,'2026-07-28 19:28:38'),(59,3,'¿Qué hook de React se usa para ejecutar código después de que un componente se renderiza?',19,1,'2026-07-28 19:28:38'),(60,3,'¿Cómo se llama la sintaxis que mezcla HTML y JavaScript en React?',20,1,'2026-07-28 19:28:38'),(61,4,'¿Qué instrucción SQL se usa para crear una tabla?',1,1,'2026-07-28 19:28:38'),(62,4,'¿Qué cláusula filtra filas en un SELECT?',2,1,'2026-07-28 19:28:38'),(63,4,'¿Qué tipo de JOIN devuelve solo las filas que coinciden en ambas tablas?',3,1,'2026-07-28 19:28:38'),(64,4,'¿Qué función SQL cuenta filas?',4,1,'2026-07-28 19:28:38'),(65,4,'¿Para qué sirve un índice en una tabla?',5,1,'2026-07-28 19:28:38'),(66,4,'¿Qué instrucción MySQL muestra el plan de ejecución de una consulta?',6,1,'2026-07-28 19:28:38'),(67,4,'¿Qué tipo de clave identifica de forma única cada fila de una tabla?',7,1,'2026-07-28 19:28:38'),(68,4,'¿Qué cláusula agrupa filas para aplicar funciones de agregación como AVG o SUM por grupo?',8,1,'2026-07-28 19:28:38'),(69,4,'¿Qué encapsula lógica de acceso a datos directamente en la base de datos, como hace el backend de Educ G?',9,1,'2026-07-28 19:28:38'),(70,4,'¿Qué instrucciones permiten que un conjunto de operaciones se aplique todas juntas o ninguna?',10,1,'2026-07-28 19:28:38'),(71,4,'¿Qué característica de un buen diseño de base de datos evita inconsistencias y duplicación de datos?',11,1,'2026-07-28 19:28:38'),(72,4,'¿Qué instrucción elimina una tabla completa junto con sus datos?',12,1,'2026-07-28 19:28:38'),(73,4,'¿Qué restricción impide que una columna tenga valores repetidos?',13,1,'2026-07-28 19:28:38'),(74,4,'¿Qué tipo de JOIN devuelve todas las filas de la tabla izquierda aunque no haya coincidencia en la derecha?',14,1,'2026-07-28 19:28:38'),(75,4,'¿Qué cláusula limita la cantidad de filas devueltas por una consulta?',15,1,'2026-07-28 19:28:38'),(76,4,'¿Qué función SQL calcula el promedio de una columna?',16,1,'2026-07-28 19:28:38'),(77,4,'¿Qué cláusula filtra grupos ya calculados después de un GROUP BY?',17,1,'2026-07-28 19:28:38'),(78,4,'¿Qué efecto secundario tiene agregar demasiados índices a una tabla?',18,1,'2026-07-28 19:28:38'),(79,4,'¿Qué instrucción confirma una transacción de forma permanente?',19,1,'2026-07-28 19:28:38'),(80,4,'¿Qué instrucción deshace todos los cambios de una transacción en curso?',20,1,'2026-07-28 19:28:38'),(81,5,'¿Qué comando guarda los cambios preparados en un nuevo commit?',1,1,'2026-07-28 19:28:38'),(82,5,'¿Qué comando prepara los cambios para el próximo commit?',2,1,'2026-07-28 19:28:38'),(83,5,'¿Qué es una branch (rama)?',3,1,'2026-07-28 19:28:38'),(84,5,'¿Qué ocurre cuando dos ramas modificaron la misma línea de forma distinta al hacer merge?',4,1,'2026-07-28 19:28:38'),(85,5,'¿Qué es un pull request?',5,1,'2026-07-28 19:28:38'),(86,5,'¿Qué plataforma permite automatizar tareas como tests o despliegues ante eventos del repositorio?',6,1,'2026-07-28 19:28:38'),(87,5,'¿Qué comando muestra el historial de commits?',7,1,'2026-07-28 19:28:38'),(88,5,'¿Qué marcador NO aparece en un conflicto de Git?',8,1,'2026-07-28 19:28:38'),(89,5,'¿Qué comando cambia a otra rama existente?',9,1,'2026-07-28 19:28:38'),(90,5,'¿Qué es GitHub, a diferencia de Git?',10,1,'2026-07-28 19:28:38'),(91,5,'¿Qué comando inicializa un nuevo repositorio Git en la carpeta actual?',11,1,'2026-07-28 19:28:38'),(92,5,'¿Qué comando descarga un repositorio remoto completo, con todo su historial?',12,1,'2026-07-28 19:28:38'),(93,5,'¿Qué comando muestra qué archivos fueron modificados, preparados o no trackeados?',13,1,'2026-07-28 19:28:38'),(94,5,'¿Qué comando crea una nueva rama?',14,1,'2026-07-28 19:28:38'),(95,5,'¿Qué comando combina los cambios de una rama con la rama actual?',15,1,'2026-07-28 19:28:38'),(96,5,'¿Qué hay que hacer después de resolver a mano un conflicto de merge?',16,1,'2026-07-28 19:28:38'),(97,5,'¿Qué permite un code review antes de aceptar un pull request?',17,1,'2026-07-28 19:28:38'),(98,5,'¿En qué carpeta se ubican los workflows de GitHub Actions?',18,1,'2026-07-28 19:28:38'),(99,5,'¿Qué comando sube commits locales al repositorio remoto?',19,1,'2026-07-28 19:28:38'),(100,5,'¿Qué comando trae los cambios del remoto sin fusionarlos automáticamente en tu rama?',20,1,'2026-07-28 19:28:38'),(101,6,'¿Qué notación describe cómo crece el tiempo de un algoritmo según el tamaño de la entrada?',1,1,'2026-07-28 19:28:38'),(102,6,'¿Cuál es la complejidad de acceder a un elemento de un array por índice?',2,1,'2026-07-28 19:28:38'),(103,6,'¿Qué estructura sigue el orden LIFO (último en entrar, primero en salir)?',3,1,'2026-07-28 19:28:38'),(104,6,'¿Qué recorrido de grafos explora nivel por nivel?',4,1,'2026-07-28 19:28:38'),(105,6,'¿Qué requiere la búsqueda binaria para funcionar correctamente?',5,1,'2026-07-28 19:28:38'),(106,6,'¿Cuál es la complejidad temporal típica de un buen algoritmo de ordenamiento como merge sort?',6,1,'2026-07-28 19:28:38'),(107,6,'¿Qué técnica resuelve problemas dividiéndolos en subproblemas y guardando resultados ya calculados?',7,1,'2026-07-28 19:28:38'),(108,6,'¿Qué caracteriza a un algoritmo greedy?',8,1,'2026-07-28 19:28:38'),(109,6,'¿Qué estructura organiza datos para buscar, insertar y borrar en O(log n) en el caso promedio?',9,1,'2026-07-28 19:28:38'),(110,6,'¿Qué problema clásico resuelve la programación dinámica combinando subproblemas superpuestos?',10,1,'2026-07-28 19:28:38'),(111,6,'¿Qué determina si conviene usar una estructura de datos u otra para resolver un problema?',11,1,'2026-07-28 19:28:38'),(112,6,'¿Qué complejidad tiene un algoritmo que revisa cada elemento de la entrada una sola vez?',12,1,'2026-07-28 19:28:38'),(113,6,'¿Cuál es la complejidad típica de un bucle anidado simple que recorre dos veces los mismos n elementos?',13,1,'2026-07-28 19:28:38'),(114,6,'¿Qué estructura sigue el orden FIFO (primero en entrar, primero en salir)?',14,1,'2026-07-28 19:28:38'),(115,6,'¿Qué operación de una pila agrega un elemento al tope?',15,1,'2026-07-28 19:28:38'),(116,6,'¿Qué recorrido de árbol o grafo explora primero en profundidad, antes de retroceder?',16,1,'2026-07-28 19:28:38'),(117,6,'¿Qué elementos componen un grafo?',17,1,'2026-07-28 19:28:38'),(118,6,'¿Qué algoritmo de ordenamiento es O(n²) en el peor caso, comparando e intercambiando pares adyacentes?',18,1,'2026-07-28 19:28:38'),(119,6,'¿Qué técnica toma la decisión que parece mejor en cada paso, pero no garantiza la solución óptima global?',19,1,'2026-07-28 19:28:38'),(120,6,'¿Qué técnica evita recalcular subproblemas ya resueltos, guardando sus resultados?',20,1,'2026-07-28 19:28:38');
/*!40000 ALTER TABLE `test_preguntas` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `test_respuestas_usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test_respuestas_usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `test_resultado_id` int NOT NULL,
  `pregunta_id` int NOT NULL,
  `opcion_elegida_id` int NOT NULL,
  `es_correcta` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `test_resultado_id` (`test_resultado_id`),
  KEY `pregunta_id` (`pregunta_id`),
  KEY `opcion_elegida_id` (`opcion_elegida_id`),
  CONSTRAINT `fk_respuestas_intento` FOREIGN KEY (`test_resultado_id`) REFERENCES `test_resultados` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_respuestas_opcion` FOREIGN KEY (`opcion_elegida_id`) REFERENCES `test_opciones` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_respuestas_pregunta` FOREIGN KEY (`pregunta_id`) REFERENCES `test_preguntas` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `test_respuestas_usuario` WRITE;
/*!40000 ALTER TABLE `test_respuestas_usuario` DISABLE KEYS */;
INSERT INTO `test_respuestas_usuario` VALUES (1,1,102,405,0),(2,1,103,410,1),(3,1,104,416,0),(4,1,108,430,1),(5,1,109,433,0),(6,1,112,447,0),(7,1,113,449,0),(8,1,114,454,1),(9,1,116,463,0),(10,1,117,466,1),(11,2,82,325,1),(12,2,83,330,1),(13,2,84,334,1),(14,2,86,341,1),(15,2,89,353,1),(16,2,94,373,1),(17,2,95,377,1),(18,2,97,385,1),(19,2,98,390,0),(20,2,99,393,1);
/*!40000 ALTER TABLE `test_respuestas_usuario` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `test_resultados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test_resultados` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `curso_id` int NOT NULL,
  `puntaje` int NOT NULL,
  `aprobado` tinyint(1) NOT NULL,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `curso_id` (`curso_id`),
  KEY `aprobado_idx` (`aprobado`),
  KEY `fecha_idx` (`fecha`),
  CONSTRAINT `fk_resultados_curso` FOREIGN KEY (`curso_id`) REFERENCES `cursos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_resultados_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_puntaje` CHECK ((`puntaje` between 0 and 100))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `test_resultados` WRITE;
/*!40000 ALTER TABLE `test_resultados` DISABLE KEYS */;
INSERT INTO `test_resultados` VALUES (1,2,6,40,0,'2026-07-28 19:49:37'),(2,2,5,90,1,'2026-07-28 19:52:36');
/*!40000 ALTER TABLE `test_resultados` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellido` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `dni` bigint NOT NULL,
  `telefono` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `es_admin` tinyint(1) NOT NULL DEFAULT '0',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_unique` (`email`),
  KEY `idx_usuarios_activo` (`activo`),
  KEY `idx_usuarios_es_admin` (`es_admin`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'admin@educg.com','eac4abd424df667225df26e7d2beca8f:d962ca97cb581e5edbc2e1732fb320217189ec9f877e4536079ecd5ae8f1d9c6','Admin','EducG',10000000,'0000000000',1,1,'2026-07-28 19:27:49','2026-07-28 19:27:49'),(2,'ale@g.com','20188bb2aeb04feb816c72129001f63c:389b024e5056f4dd86ae287b049e742d4ea8b29812b666ca454064a17a6248de','Oscar Alejandro','Grimaldi',36420807,'3816699521',1,0,'2026-07-28 19:46:12','2026-07-28 19:46:12');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 DROP PROCEDURE IF EXISTS `sp_activar_curso` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_activar_curso`(
  IN p_curso_id INT,
  OUT p_resultado TINYINT
)
BEGIN
  UPDATE cursos SET activo = 1 WHERE id = p_curso_id;
  SET p_resultado = ROW_COUNT();

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_activar_usuario` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_activar_usuario`(
    IN p_usuario_id INT,
    OUT p_resultado TINYINT
)
BEGIN
    UPDATE usuarios SET activo = 1 WHERE id = p_usuario_id;
    SET p_resultado = ROW_COUNT();

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_alta_inscripcion` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_alta_inscripcion`(
  IN p_email VARCHAR(255),
  IN p_curso_id INT,
  OUT p_resultado TINYINT
)
BEGIN
  DECLARE v_usuario_id INT;

  SELECT id INTO v_usuario_id FROM usuarios WHERE email = p_email LIMIT 1;

  IF v_usuario_id IS NULL THEN
    SET p_resultado = -1;
  ELSEIF EXISTS (SELECT 1 FROM inscripciones WHERE usuario_id = v_usuario_id AND curso_id = p_curso_id) THEN
    
    UPDATE inscripciones SET activo = 1 WHERE usuario_id = v_usuario_id AND curso_id = p_curso_id;
    SET p_resultado = ROW_COUNT();
  ELSE
    INSERT INTO inscripciones (usuario_id, curso_id) VALUES (v_usuario_id, p_curso_id);
    SET p_resultado = 1;
  END IF;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_alta_respuesta_test` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_alta_respuesta_test`(
  IN p_test_resultado_id INT,
  IN p_pregunta_id INT,
  IN p_opcion_elegida_id INT,
  OUT p_resultado TINYINT
)
BEGIN
  DECLARE v_es_correcta TINYINT;

  SELECT es_correcta INTO v_es_correcta FROM test_opciones WHERE id = p_opcion_elegida_id LIMIT 1;

  INSERT INTO test_respuestas_usuario (test_resultado_id, pregunta_id, opcion_elegida_id, es_correcta)
  VALUES (p_test_resultado_id, p_pregunta_id, p_opcion_elegida_id, v_es_correcta);

  SET p_resultado = ROW_COUNT();

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_alta_resultado_test` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_alta_resultado_test`(
  IN p_email VARCHAR(255),
  IN p_curso_id INT,
  IN p_puntaje INT,
  OUT p_resultado_id INT
)
BEGIN
  DECLARE v_usuario_id INT;
  DECLARE v_aprobado TINYINT;

  SELECT id INTO v_usuario_id FROM usuarios WHERE email = p_email LIMIT 1;
  SET v_aprobado = IF(p_puntaje >= 60, 1, 0);

  INSERT INTO test_resultados (usuario_id, curso_id, puntaje, aprobado)
  VALUES (v_usuario_id, p_curso_id, p_puntaje, v_aprobado);

  SET p_resultado_id = LAST_INSERT_ID();

  
  IF v_aprobado = 1 THEN
    INSERT IGNORE INTO certificados (usuario_id, curso_id, test_resultado_id, puntaje)
    VALUES (v_usuario_id, p_curso_id, p_resultado_id, p_puntaje);
  END IF;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_alta_usuario` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_alta_usuario`(
  IN p_email VARCHAR(255),
  IN p_password_hash VARCHAR(255),
  IN p_nombre VARCHAR(100),
  IN p_apellido VARCHAR(100),
  IN p_dni BIGINT,
  IN p_telefono VARCHAR(20),
  OUT p_resultado TINYINT,
  OUT p_usuario_id INT
)
BEGIN
  IF EXISTS (SELECT 1 FROM usuarios WHERE email = p_email) THEN
    SET p_resultado = 0;
    SET p_usuario_id = -1;
  ELSE
    INSERT INTO usuarios (email, password_hash, nombre, apellido, dni, telefono)
    VALUES (p_email, p_password_hash, p_nombre, p_apellido, p_dni, p_telefono);
    SET p_resultado = 1;
    SET p_usuario_id = LAST_INSERT_ID();
  END IF;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_baja_inscripcion` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_baja_inscripcion`(IN p_email VARCHAR(255), IN p_curso_id INT, OUT p_resultado TINYINT)
BEGIN
  DECLARE v_usuario_id INT;
  SELECT id INTO v_usuario_id FROM usuarios WHERE email = p_email LIMIT 1;
  UPDATE inscripciones SET activo = 0 WHERE usuario_id = v_usuario_id AND curso_id = p_curso_id;
  SET p_resultado = ROW_COUNT();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_buscar_cursos` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_buscar_cursos`(IN p_busqueda VARCHAR(255))
BEGIN
  SELECT id, emoji, titulo, descripcion, duracion, activo, fecha_creacion
  FROM cursos
  WHERE (titulo LIKE CONCAT('%', p_busqueda, '%')
      OR descripcion LIKE CONCAT('%', p_busqueda, '%')
      OR emoji LIKE CONCAT('%', p_busqueda, '%'))
    AND activo = 1
  ORDER BY titulo ASC;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_buscar_todos_cursos` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_buscar_todos_cursos`(IN p_busqueda VARCHAR(255))
BEGIN
  SELECT id, emoji, titulo, descripcion, duracion, activo
  FROM cursos
  WHERE titulo LIKE CONCAT('%', p_busqueda, '%')
  ORDER BY titulo ASC;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_buscar_usuarios` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_buscar_usuarios`(IN p_busqueda VARCHAR(255))
BEGIN
  SELECT id, email, nombre, apellido, dni, telefono, activo, es_admin, fecha_creacion
  FROM usuarios
  WHERE (email LIKE CONCAT('%', p_busqueda, '%')
     OR CONCAT(nombre, ' ', apellido) LIKE CONCAT('%', p_busqueda, '%')
     OR dni LIKE CONCAT('%', p_busqueda, '%'))
    AND activo = 1
  ORDER BY fecha_creacion DESC;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_buscar_usuario_por_dni` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_buscar_usuario_por_dni`(IN p_dni BIGINT)
BEGIN
  SELECT id, email, nombre, apellido, dni, telefono, fecha_creacion, activo, es_admin
  FROM usuarios
  WHERE dni = p_dni AND es_admin = 0;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_contar_cursos` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_contar_cursos`(OUT p_count INT)
BEGIN
    SELECT COUNT(*) INTO p_count FROM cursos;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_contar_inscripciones` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_contar_inscripciones`(OUT p_count INT)
BEGIN
    SELECT COUNT(*) INTO p_count FROM inscripciones WHERE activo = 1;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_contar_test_resultados` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_contar_test_resultados`(OUT p_count INT)
BEGIN
    SELECT COUNT(*) INTO p_count FROM test_resultados;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_contar_usuarios` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_contar_usuarios`(OUT p_count INT)
BEGIN
    SELECT COUNT(*) INTO p_count FROM usuarios;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_crear_curso` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_crear_curso`(
    IN p_emoji VARCHAR(10),
    IN p_titulo VARCHAR(200),
    IN p_descripcion TEXT,
    IN p_duracion VARCHAR(50),
    OUT p_curso_id INT
)
BEGIN
    INSERT INTO cursos (emoji, titulo, descripcion, duracion)
    VALUES (p_emoji, p_titulo, p_descripcion, p_duracion);
    SET p_curso_id = LAST_INSERT_ID();

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_crear_leccion` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_crear_leccion`(
  IN p_curso_id INT,
  IN p_orden TINYINT,
  IN p_topico VARCHAR(200),
  IN p_contenido LONGTEXT,
  IN p_ejercicio_propuesto TEXT,
  IN p_respuesta_esperada VARCHAR(255),
  OUT p_leccion_id INT
)
BEGIN
  INSERT INTO curso_contenidos (curso_id, orden, topico, contenido, ejercicio_propuesto, respuesta_esperada)
  VALUES (p_curso_id, p_orden, p_topico, p_contenido, p_ejercicio_propuesto, p_respuesta_esperada);
  SET p_leccion_id = LAST_INSERT_ID();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_crear_opcion_pregunta` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_crear_opcion_pregunta`(
  IN p_pregunta_id INT,
  IN p_texto VARCHAR(255),
  IN p_es_correcta TINYINT(1),
  IN p_orden TINYINT
)
BEGIN
  INSERT INTO test_opciones (pregunta_id, texto, es_correcta, orden) VALUES (p_pregunta_id, p_texto, p_es_correcta, p_orden);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_crear_pregunta` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_crear_pregunta`(
  IN p_curso_id INT,
  IN p_enunciado TEXT,
  IN p_orden TINYINT,
  OUT p_pregunta_id INT
)
BEGIN
  INSERT INTO test_preguntas (curso_id, enunciado, orden) VALUES (p_curso_id, p_enunciado, p_orden);
  SET p_pregunta_id = LAST_INSERT_ID();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_desactivar_curso` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_desactivar_curso`(
  IN p_curso_id INT,
  OUT p_resultado TINYINT
)
BEGIN
  UPDATE cursos SET activo = 0 WHERE id = p_curso_id;
  SET p_resultado = ROW_COUNT();

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_desactivar_leccion` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_desactivar_leccion`(
  IN p_leccion_id INT,
  OUT p_resultado TINYINT
)
BEGIN
  UPDATE curso_contenidos SET activo = 0 WHERE id = p_leccion_id;
  SET p_resultado = ROW_COUNT();

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_desactivar_usuario` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_desactivar_usuario`(
    IN p_usuario_id INT,
    OUT p_resultado TINYINT
)
BEGIN
    UPDATE usuarios SET activo = 0 WHERE id = p_usuario_id;
    SET p_resultado = ROW_COUNT();

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_eliminar_curso` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_eliminar_curso`(IN p_curso_id INT, OUT p_resultado TINYINT)
BEGIN
  DELETE FROM cursos WHERE id = p_curso_id;
  SET p_resultado = ROW_COUNT();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_eliminar_usuario` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_eliminar_usuario`(IN p_usuario_id INT, OUT p_resultado TINYINT)
BEGIN
  DELETE FROM usuarios WHERE id = p_usuario_id;
  SET p_resultado = ROW_COUNT();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_estadisticas_generales` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_estadisticas_generales`()
BEGIN
    SELECT
        (SELECT COUNT(*) FROM usuarios WHERE es_admin = 0 AND activo = 1) AS alumnos_activos,
        (SELECT COUNT(*) FROM usuarios WHERE es_admin = 0 AND activo = 0) AS alumnos_inactivos,
        (SELECT COUNT(*) FROM cursos WHERE activo = 1) AS cursos_activos,
        (SELECT COUNT(*) FROM inscripciones WHERE activo = 1) AS inscripciones_activas,
        (SELECT COUNT(*) FROM (SELECT DISTINCT usuario_id, curso_id FROM test_resultados WHERE aprobado = 1) t) AS aprobados_totales;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_estadisticas_por_curso` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_estadisticas_por_curso`()
BEGIN
    SELECT
        c.id AS curso_id,
        c.titulo,
        (SELECT COUNT(*) FROM inscripciones i WHERE i.curso_id = c.id AND i.activo = 1) AS inscriptos,
        IFNULL((SELECT AVG(tr.puntaje) FROM test_resultados tr WHERE tr.curso_id = c.id), 0) AS promedio,
        IFNULL((
            (SELECT COUNT(DISTINCT usuario_id) FROM test_resultados WHERE curso_id = c.id AND aprobado = 1)
            / NULLIF((SELECT COUNT(DISTINCT usuario_id) FROM test_resultados WHERE curso_id = c.id), 0) * 100
        ), 0) AS tasa_aprobacion
    FROM cursos c
    WHERE c.activo = 1
    ORDER BY c.titulo;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_estadisticas_registros_mensuales` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_estadisticas_registros_mensuales`()
BEGIN
    SELECT DATE_FORMAT(fecha_creacion, '%Y-%m') AS mes, COUNT(*) AS cantidad
    FROM usuarios
    WHERE es_admin = 0 AND fecha_creacion >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
    GROUP BY mes
    ORDER BY mes;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_listar_certificados_emitidos` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_listar_certificados_emitidos`()
BEGIN
  SELECT c.id, u.id as usuario_id, u.nombre, u.apellido, u.email,
         cr.id as curso_id, cr.titulo, cr.emoji, c.puntaje, c.fecha_emision
  FROM certificados c
  INNER JOIN usuarios u ON c.usuario_id = u.id
  INNER JOIN cursos cr ON c.curso_id = cr.id
  ORDER BY c.fecha_emision DESC;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_listar_certificados_usuario` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_listar_certificados_usuario`(IN p_email VARCHAR(255))
BEGIN
  SELECT c.id, c.usuario_id, c.curso_id, cr.titulo, cr.emoji, c.puntaje, c.fecha_emision
  FROM certificados c
  INNER JOIN usuarios u ON c.usuario_id = u.id
  INNER JOIN cursos cr ON c.curso_id = cr.id
  WHERE u.email = p_email
  ORDER BY c.fecha_emision DESC;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_listar_contenidos_curso` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_listar_contenidos_curso`(IN p_curso_id INT)
BEGIN
  SELECT id, curso_id, orden, topico, contenido, ejercicio_propuesto, respuesta_esperada, activo, fecha_creacion
  FROM curso_contenidos
  WHERE curso_id = p_curso_id
  ORDER BY orden ASC;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_listar_cursos_catalogo` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_listar_cursos_catalogo`()
BEGIN
  SELECT id, emoji, titulo, descripcion, duracion, activo, fecha_creacion
  FROM cursos
  WHERE activo = 1
  ORDER BY titulo ASC;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_listar_inscripciones_usuario` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_listar_inscripciones_usuario`(IN p_email VARCHAR(255))
BEGIN
  SELECT i.id, i.usuario_id, i.curso_id, c.titulo, c.emoji, i.fecha_inscripcion, i.leccion_actual, i.activo
  FROM inscripciones i
  INNER JOIN usuarios u ON i.usuario_id = u.id
  INNER JOIN cursos c ON i.curso_id = c.id
  WHERE u.email = p_email AND i.activo = 1
  ORDER BY i.fecha_inscripcion DESC;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_listar_preguntas_curso` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_listar_preguntas_curso`(IN p_curso_id INT)
BEGIN
  SELECT tp.id AS pregunta_id, tp.enunciado, topt.id AS opcion_id, topt.texto AS opcion_texto, topt.es_correcta
  FROM (
    SELECT id, enunciado
    FROM test_preguntas
    WHERE curso_id = p_curso_id AND activo = 1
    ORDER BY RAND()
    LIMIT 10
  ) tp
  JOIN test_opciones topt ON topt.pregunta_id = tp.id
  ORDER BY tp.id, topt.orden;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_listar_resultados_test_usuario` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_listar_resultados_test_usuario`(IN p_email VARCHAR(255))
BEGIN
  SELECT tr.id, tr.usuario_id, tr.curso_id, c.titulo, c.emoji, tr.puntaje, tr.aprobado, tr.fecha
  FROM test_resultados tr
  INNER JOIN usuarios u ON tr.usuario_id = u.id
  INNER JOIN cursos c ON tr.curso_id = c.id
  WHERE u.email = p_email
  ORDER BY tr.fecha DESC;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_listar_todos_cursos` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_listar_todos_cursos`()
BEGIN
    SELECT id, emoji, titulo, descripcion, duracion, activo
    FROM cursos
    ORDER BY titulo ASC;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_listar_todos_usuarios` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_listar_todos_usuarios`()
BEGIN
    SELECT id, email, nombre, apellido, dni, telefono, fecha_creacion, activo, es_admin
    FROM usuarios
    WHERE es_admin = 0
    ORDER BY fecha_creacion DESC;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_listar_usuarios` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_listar_usuarios`()
BEGIN
  SELECT id, email, nombre, apellido, dni, telefono, activo, es_admin, fecha_creacion, fecha_modificacion
  FROM usuarios
  WHERE activo = 1
  ORDER BY fecha_creacion DESC;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_modificar_curso` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_modificar_curso`(
    IN p_curso_id INT,
    IN p_emoji VARCHAR(10),
    IN p_titulo VARCHAR(200),
    IN p_descripcion TEXT,
    IN p_duracion VARCHAR(50),
    OUT p_resultado TINYINT
)
BEGIN
    UPDATE cursos
    SET emoji = p_emoji, titulo = p_titulo, descripcion = p_descripcion, duracion = p_duracion
    WHERE id = p_curso_id;
    SET p_resultado = ROW_COUNT();

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_modificar_leccion` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_modificar_leccion`(
  IN p_leccion_id INT,
  IN p_topico VARCHAR(200),
  IN p_contenido LONGTEXT,
  OUT p_resultado TINYINT
)
BEGIN
  UPDATE curso_contenidos
  SET topico = p_topico, contenido = p_contenido
  WHERE id = p_leccion_id;
  SET p_resultado = ROW_COUNT();

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_modificar_password_usuario` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_modificar_password_usuario`(
  IN p_email VARCHAR(255),
  IN p_password_hash VARCHAR(255),
  OUT p_resultado TINYINT
)
BEGIN
  UPDATE usuarios
  SET password_hash = p_password_hash
  WHERE email = p_email;
  SET p_resultado = ROW_COUNT();

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_modificar_progreso_inscripcion` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_modificar_progreso_inscripcion`(
  IN p_email VARCHAR(255),
  IN p_curso_id INT,
  IN p_leccion_actual TINYINT,
  OUT p_resultado TINYINT
)
BEGIN
  UPDATE inscripciones i
  INNER JOIN usuarios u ON i.usuario_id = u.id
  SET i.leccion_actual = p_leccion_actual
  WHERE u.email = p_email AND i.curso_id = p_curso_id;
  SET p_resultado = ROW_COUNT();

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_modificar_usuario` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_modificar_usuario`(
  IN p_usuario_id INT,
  IN p_email VARCHAR(255),
  IN p_nombre VARCHAR(100),
  IN p_apellido VARCHAR(100),
  IN p_dni BIGINT,
  IN p_telefono VARCHAR(20),
  OUT p_resultado TINYINT
)
BEGIN
  UPDATE usuarios
  SET email = p_email, nombre = p_nombre, apellido = p_apellido, dni = p_dni, telefono = p_telefono
  WHERE id = p_usuario_id;
  SET p_resultado = ROW_COUNT();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_obtener_curso` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obtener_curso`(IN p_curso_id INT)
BEGIN
  SELECT id, emoji, titulo, descripcion, duracion, activo, fecha_creacion
  FROM cursos
  WHERE id = p_curso_id;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_obtener_estadisticas_usuario` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obtener_estadisticas_usuario`(IN p_email VARCHAR(255))
BEGIN
  SELECT
    COUNT(DISTINCT tr.curso_id) as cursos_completados,
    COUNT(tr.id) as tests_realizados,
    AVG(tr.puntaje) as promedio_puntaje,
    COUNT(CASE WHEN tr.aprobado = 1 THEN 1 END) as tests_aprobados
  FROM test_resultados tr
  INNER JOIN usuarios u ON tr.usuario_id = u.id
  WHERE u.email = p_email;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_obtener_hash_password` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obtener_hash_password`(IN p_email VARCHAR(255), OUT p_hash VARCHAR(255))
BEGIN
  SELECT password_hash INTO p_hash FROM usuarios WHERE email = p_email AND activo = 1 LIMIT 1;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_obtener_inscripcion` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obtener_inscripcion`(IN p_email VARCHAR(255), IN p_curso_id INT, OUT p_existe TINYINT)
BEGIN
  SELECT COUNT(*) INTO p_existe
  FROM inscripciones i
  INNER JOIN usuarios u ON i.usuario_id = u.id
  WHERE u.email = p_email AND i.curso_id = p_curso_id AND i.activo = 1;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_obtener_leccion` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obtener_leccion`(IN p_contenido_id INT)
BEGIN
  SELECT id, curso_id, orden, topico, contenido, activo, fecha_creacion
  FROM curso_contenidos
  WHERE id = p_contenido_id;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_obtener_mejor_puntaje_curso` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obtener_mejor_puntaje_curso`(IN p_email VARCHAR(255), IN p_curso_id INT, OUT p_puntaje INT)
BEGIN
  SELECT MAX(tr.puntaje) INTO p_puntaje
  FROM test_resultados tr
  INNER JOIN usuarios u ON tr.usuario_id = u.id
  WHERE u.email = p_email AND tr.curso_id = p_curso_id;
  IF p_puntaje IS NULL THEN
    SET p_puntaje = -1;
  END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_obtener_progreso_inscripcion` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obtener_progreso_inscripcion`(
  IN p_email VARCHAR(255),
  IN p_curso_id INT,
  OUT p_leccion_actual TINYINT
)
BEGIN
  SELECT leccion_actual INTO p_leccion_actual
  FROM inscripciones i
  INNER JOIN usuarios u ON i.usuario_id = u.id
  WHERE u.email = p_email AND i.curso_id = p_curso_id AND i.activo = 1
  LIMIT 1;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_obtener_usuario` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obtener_usuario`(IN p_email VARCHAR(255))
BEGIN
  SELECT id, email, password_hash, nombre, apellido, dni, telefono, activo, es_admin, fecha_creacion
  FROM usuarios
  WHERE email = p_email AND activo = 1;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_obtener_usuario_por_id` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_obtener_usuario_por_id`(IN p_usuario_id INT)
BEGIN
  SELECT id, email, nombre, apellido, dni, telefono, activo, es_admin, fecha_creacion
  FROM usuarios
  WHERE id = p_usuario_id;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_promedio_calificaciones` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_promedio_calificaciones`(OUT p_promedio DOUBLE)
BEGIN
    SELECT AVG(puntaje) INTO p_promedio FROM test_resultados;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

