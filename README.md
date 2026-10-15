# Tarjetas JavaFX + MySQL

Aplicación de escritorio Maven para consultar y crear tarjetas de débito y crédito, asociadas a titulares y persistidas en MySQL. La implementación de HU-01 incorpora un estado de dominio obligatorio y tipado.

## Objetivo de la práctica

Integrar HU-01 en la arquitectura existente JavaFX/FXML, modelo orientado a objetos y DAO JDBC, manteniendo la jerarquía `Tarjeta`/`Debito`/`Credito` y el esquema de herencia JOINED que la aplicación implementa manualmente.

## Historia de Usuario HU-01

**Como** analista de crédito, **quiero** asignar un estado a una tarjeta, **para** controlar si puede ser utilizada.

### Criterios de aceptación

- CA-01: una tarjeta solo admite `ACTIVA`, `BLOQUEADA` o `CANCELADA`.
- CA-02: el estado se almacena en MySQL.
- CA-03: el estado se presenta en la consulta de la tarjeta y en la confirmación de creación.
- CA-04: el estado es obligatorio al crear.
- CA-05: no se guarda una tarjeta con estado vacío, nulo o inválido.
- CA-06: la consulta recupera el estado persistido.
- CA-07: se mantienen las operaciones que existen en el original; crear, consultar una tarjeta y listar están implementadas. El original no implementa actualización ni eliminación.
- CA-08: se conserva la interfaz de pestañas JavaFX/FXML existente.

## Estimación de desarrollo

Estimación del trabajo de desarrollo, no un registro de tiempo trabajado: **8 puntos** (aprox. **10,5 horas**).

| Tarea | Descripción | Estimación |
|---|---|---:|
| T1 | Inspección del proyecto, historial y Wiki | 1 h |
| T2 | Diseño y modificación del modelo | 1,5 h |
| T3 | Actualización de MySQL y migración | 1,5 h |
| T4 | FXML y controlador | 1,5 h |
| T5 | Pruebas unitarias y de integración | 2,5 h |
| T6 | UML y JavaDoc | 1 h |
| T7 | README, Wiki y verificación final | 1,5 h |
| **Total estimado** |  | **10,5 h** |

## Tecnologías

- Amazon Corretto OpenJDK 26.0.2; compilación Maven con `--release 26`.
- JavaFX Controls/FXML 26.0.1.
- Maven 3.9.16 y Maven Compiler Plugin 3.14.1.
- MySQL 8.4.11 (versión usada en las pruebas de integración) y MySQL Connector/J 8.4.0.
- JUnit Jupiter 5.13.4; Surefire 3.5.4.
- Eclipse con soporte Java 26 y m2e, usando el proyecto Maven existente.

## Arquitectura y estructura

El proyecto Maven está en `examen2_Soto/`; se conserva esa estructura original.

- `modelo`: `Tarjeta` abstracta, `Debito`, `Credito`, `Titular` y `EstadoTarjeta`.
- `controlador`: `ControlFormCard`, controlador de las pestañas definidas en FXML.
- `servicios`: interfaz genérica `CRUD`, `DAOTarjeta`, `DAOTitular` y `ConexionBD`.
- `src/main/resources/.../formCard.fxml`: vistas de consulta y creación.
- `ScriptDB.sql`: esquema para una instalación nueva y datos de demostración.
- `docs/database/hu-01-estado-tarjeta.sql`: migración de una instalación original.

No se añadió una capa de servicio que no existía en el diseño original. El DAO conserva `PreparedStatement`, trata la creación de las filas base/subtipo como una transacción y traduce el estado con `EstadoTarjeta.name()`/`desdeBaseDeDatos()`.

## Funcionalidad

El formulario de creación muestra el selector requerido `Estado *`, inicialmente sin selección, con los tres estados válidos. El controlador valida el estado antes de llamar al DAO. El modelo rechaza `null`; el DAO valida la entrada y propaga errores de escritura; MySQL impone `NOT NULL` y `CHECK`. La vista de consulta muestra el estado recuperado. El listado del DAO devuelve tarjetas de ambos subtipos.

## Requisitos y configuración

- JDK 26, Maven 3.9 o posterior y MySQL 8.0.16 o posterior.
- No se versionan credenciales ni `.env`. Configure `DB_URL`, `DB_USER` y `DB_PASSWORD` en el entorno, o como propiedades de la JVM. Por ejemplo, para una base local:

```sh
export DB_URL='jdbc:mysql://localhost:3306/examen2_soto?serverTimezone=UTC'
export DB_USER='usuario_mysql'
export DB_PASSWORD='contraseña_local'
```

Mantenga la contraseña fuera del repositorio, del historial de shell compartido y de capturas públicas. El proyecto original tenía un `.env` rastreado; se excluyó de esta copia y las reglas `.gitignore` cubren archivos `.env` y carpetas `target/`.

### Base de datos

Para una instalación nueva, ejecute desde la raíz del repositorio:

```sh
mysql -u usuario_mysql -p < ScriptDB.sql
```

Para una base original con `tarjeta.estado TINYINT(1)`, haga primero un respaldo y ejecute una sola vez:

```sh
mysql -u usuario_mysql -p < docs/database/hu-01-estado-tarjeta.sql
```

La migración conserva `1 → ACTIVA` y `0 → BLOQUEADA`, amplía `fecha_exp` a 10 caracteres para admitir el formato del DatePicker y se detiene si encuentra estados heredados distintos de 0/1. No asigna `CANCELADA` a registros existentes. Requiere MySQL 8.0.16+ para aplicar `CHECK`.

### Ejecutar

Desde terminal, con las variables de base configuradas:

```sh
mvn -f examen2_Soto/pom.xml clean javafx:run
```

En Eclipse: importar `examen2_Soto/` como **Existing Maven Project**, seleccionar un JDK 26 para el proyecto y la ejecución, confirmar la biblioteca JavaSE-26 y lanzar `co.edu.poli.examen2_Soto.vista.App` como Java Application. En Run Configurations, defina las mismas tres variables de entorno. Maven también puede iniciar la interfaz con el goal `javafx:run`.

### Pruebas

Sin base configurada, las pruebas unitarias se ejecutan y las clases de integración se omiten automáticamente:

```sh
mvn -f examen2_Soto/pom.xml clean test
```

Con MySQL de pruebas inicializado mediante `ScriptDB.sql` y las tres variables definidas, ejecute:

```sh
mvn -f examen2_Soto/pom.xml clean verify
```

Los resultados medidos y las limitaciones de la prueba de interfaz se registran en [docs/pruebas-hu-01.md](docs/pruebas-hu-01.md).

## UML y Wiki

- Diagrama editable: [docs/uml/diagrama-clases-hu-01.puml](docs/uml/diagrama-clases-hu-01.puml).
- Imágenes generadas: [PNG](docs/uml/diagrama-clases-hu-01.png) y [SVG](docs/uml/diagrama-clases-hu-01.svg).
- Análisis contrastado con la imagen UML original: [docs/analisis-proyecto.md](docs/analisis-proyecto.md).
- La Wiki original solo contiene `Home.md`, sin diagramas ni documentación funcional. La Wiki del repositorio destino no está disponible (`git ls-remote` respondió “Repository not found”); no se publicó nada. La página preparada y los pasos manuales están en [docs/wiki/HU-01-Estado-Tarjeta.md](docs/wiki/HU-01-Estado-Tarjeta.md).

## Limitaciones y decisiones

- El diseño original no tenía actualización ni eliminación de tarjetas; no se inventaron esas operaciones.
- El diagrama original muestra una composición con `Titular`, pero el código y la FK expresan una asociación: una tarjeta referencia un titular existente. El UML actualizado no afirma propiedad del ciclo de vida.
- La integración MySQL se verificó en un contenedor efímero MySQL 8.4.11. El FXML se parseó y se comprobó su controlador y selector; la interfaz gráfica no se pudo abrir en este contenedor por falta de `DISPLAY`/Xvfb.
- El guion de video está preparado en [docs/guion-video.md](docs/guion-video.md); no existe una grabación producida.
- No se ha hecho push ni se ha actualizado una Wiki remota.

## Referencias

- Repositorio original: [WolframDev/JavaFxMySQL](https://github.com/WolframDev/JavaFxMySQL).
- Wiki original: [JavaFxMySQL Wiki](https://github.com/WolframDev/JavaFxMySQL/wiki).
- Imagen UML original: [Examen2.jpg](Examen2.jpg).