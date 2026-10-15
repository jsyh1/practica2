# Análisis del proyecto y HU-01

## Procedencia e inspección

- Repositorio original clonado: [WolframDev/JavaFxMySQL](https://github.com/WolframDev/JavaFxMySQL), rama `main`, HEAD inspeccionado `023a7cad1757ac55cba2b83aeafeca4fc322875f`; el clon local conserva el historial completo. Las referencias locales incluían `main` y ocho commits (`023a7ca`, `19b7ae1`, `054d320`, `4f2292d`, `3183d27`, `94cfc27`, `56fb8da`, `b4a1c47`).
- La única rama original disponible en el remoto era `main`; no se encontraron ramas adicionales.
- El repositorio de destino ya existía y contenía un commit inicial y un README mínimo. Se trabajó en `feature/hu-01-estado-tarjeta`, preservando el historial del destino.
- Se inspeccionaron fuentes Java, FXML, SQL, `pom.xml`, `module-info.java`, configuración de Eclipse, pruebas, `.gitignore`, commits y la imagen original `Examen2.jpg`.
- La Wiki original se clonó; su único commit (`304214bed52e9e062d11fb1f691d0422142a25f4`) contiene `Home.md` con una línea de bienvenida, sin páginas UML ni arquitectura. La Wiki del destino no se pudo consultar: el endpoint Git devolvió `Repository not found`.
- El `.env` original estaba rastreado por Git. No se copió al destino ni se reprodujeron credenciales; la conexión ahora lee variables de entorno o propiedades JVM.

## Arquitectura observada

Maven está dentro de `examen2_Soto/`. La aplicación modular usa JavaFX, una vista `formCard.fxml` con pestañas **Consultar** y **Crear**, y `ControlFormCard`. El paquete `modelo` contiene `Tarjeta` abstracta, `Debito`, `Credito` y `Titular`; `servicios` contiene `CRUD<T>`, `DAOTarjeta`, `DAOTitular` y el singleton `ConexionBD`. Los DAO usan JDBC y el esquema mantiene datos comunes en `tarjeta` y detalles en `tarjeta_debito`/`tarjeta_credito`.

El POM original declaraba Java 11 y JavaFX 13; Eclipse también estaba en JavaSE-11. No había README del proyecto original ni pruebas de interfaz. Las pruebas JUnit existentes mezclaban unidades con pruebas que requerían una instancia MySQL externa.

## Contraste entre UML, código y SQL

La imagen [Examen2.jpg](../Examen2.jpg) confirma las clases y herencia del modelo, los DAO y la conexión. El atributo del diagrama, el modelo Java y SQL coincidían en un booleano (`boolean` / `TINYINT(1)`), insuficiente para HU-01.

Discrepancias verificadas:

- La imagen dibuja composición entre `Tarjeta` y `Titular`; el modelo solo mantiene una referencia y SQL una FK `tarjeta.titular_id` con `ON DELETE RESTRICT`. El diagrama HU-01 representa una asociación muchos-a-uno, no propiedad del ciclo de vida.
- Comentarios SQL describen anotaciones ORM (`@Entity`, `JOINED`), pero las clases no usan JPA; la herencia se persiste manualmente en los DAO.
- `fecha_exp` era `VARCHAR(7)`, mientras el controlador envía `dd/MM/yyyy` (10 caracteres). Se amplió a `VARCHAR(10)` en el esquema nuevo y en la migración.
- `DAOTarjeta.readall()` devolvía `null`; actualización y eliminación no existen en `CRUD<T>` ni en los DAO. Se implementó `readall`; no se añadieron `update`/`delete`.
- El POM apuntaba a una clase principal que no coincidía con `vista.App`; se corrigió para el plugin JavaFX.

## Flujo afectado

1. `formCard.fxml` presenta un `ComboBox<EstadoTarjeta>` sin preselección y marca el campo como obligatorio.
2. `ControlFormCard` inicializa las tres opciones y verifica la selección antes de instanciar `Debito` o `Credito`.
3. `Tarjeta` valida el enum no nulo; `bloquear`, `activar` y `cancelar` usan las constantes del dominio.
4. `DAOTarjeta.create` escribe las tablas base y subtipo dentro de una transacción con `PreparedStatement`; propaga errores y no devuelve errores como si fueran confirmaciones.
5. Las consultas reconstruyen el estado mediante `EstadoTarjeta.desdeBaseDeDatos`; `toString` y la confirmación lo muestran.
6. MySQL restringe el campo con `NOT NULL` y `CHECK`.

## Tecnologías encontradas y objetivo

Original: Java 11, JavaFX 13, Maven, MySQL Connector/J 8.4.0, dotenv-java 3.0.0, JUnit 5.10.0. Implementado: JDK Corretto 26.0.2 con `--release 26`, JavaFX 26.0.1, Maven Compiler Plugin 3.14.1, JavaFX Maven Plugin 0.0.8, Connector/J 8.4.0 y JUnit 5.13.4. Se retiró dotenv para no requerir ni rastrear un archivo con credenciales.

## Plan y estado

- Modelo: enumeración de tres valores y validación no nula.
- Persistencia: columna `VARCHAR(10) NOT NULL`, restricción `CHECK`, conversión legada segura de `0/1` y cobertura de escritura/lectura.
- Interfaz: selector FXML obligatorio, validación y estado visible en consulta/confirmación.
- Compatibilidad: Maven, JPMS y Eclipse actualizados a Java 26.
- Entrega: pruebas, documentación, diagrama PlantUML y guion de video.

No se añadió una capa de servicio ni una entidad duplicada: el DAO y `Tarjeta` existentes son los puntos de integración verificados.