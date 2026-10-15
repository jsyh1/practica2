# Guion de demostración (8:30 aprox.)

El video no se ha grabado ni se considera producido. Este guion sirve para una grabación real de entre 7 y 9 minutos; los resultados que se muestran deben ser los archivos y la salida actuales del proyecto.

| Tiempo | Demostración y narración |
|---|---|
| 0:00–0:45 | Presentar la aplicación y leer HU-01: asignar un estado permitido para controlar el uso de una tarjeta. |
| 0:45–1:30 | Mostrar en VS Code la sesión real de GitHub Copilot usada para inspeccionar el proyecto, aplicar los cambios y ejecutar verificaciones. No mostrar credenciales ni afirmar evidencia que no se vea en pantalla. |
| 1:30–2:25 | Explicar el diseño original: `Tarjeta` abstracta, subclases `Debito`/`Credito`, `Titular`, FXML, DAO y tablas relacionadas. Comparar con el análisis UML de `Examen2.jpg`. |
| 2:25–3:20 | Abrir `formCard.fxml`; enseñar el selector sin estado predeterminado y las opciones `ACTIVA`, `BLOQUEADA`, `CANCELADA`. Mostrar la validación del controlador. |
| 3:20–4:20 | Crear tres tarjetas de prueba, una con cada estado. Leer en voz alta la confirmación y comprobar que no hay selección automática. |
| 4:20–4:55 | Intentar crear sin elegir estado; mostrar el mensaje y verificar que la operación no llega al DAO. |
| 4:55–5:50 | Consultar una tarjeta creada y enseñar el estado recuperado. En MySQL, ejecutar `SELECT numero, estado FROM tarjeta WHERE numero = ...` sobre la base de demostración. |
| 5:50–6:50 | Ejecutar `mvn -f examen2_Soto/pom.xml clean verify` con variables de un MySQL de pruebas. Mostrar la salida real documentada en `docs/pruebas-hu-01.md`. |
| 6:50–7:40 | Abrir el `.puml` y el SVG del UML actualizado; explicar la relación `Tarjeta`/`Titular` como asociación y el enum de estado. |
| 7:40–8:30 | Cerrar con decisiones y límites: migración SQL, Java 26 verificado, Wiki no publicada porque no está disponible y video aún no grabado. Mostrar README y estructura final. |

## Preparación de la grabación

1. Usar una base de pruebas aislada y no grabar variables o contraseñas.
2. Configurar JDK 26, MySQL 8.4+ y la base con `ScriptDB.sql` o una migración de prueba.
3. Confirmar antes de grabar la salida de `mvn clean verify`; narrar el número que aparezca realmente.
4. Mostrar una tarjeta por cada estado y una creación rechazada sin selección.
5. Grabar pantalla y audio; revisar que no aparezcan secretos. No presentar este guion como un archivo de video.