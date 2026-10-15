# Pruebas de HU-01

## Entorno

- JDK: Amazon Corretto 26.0.2 (`java -version`).
- Maven: 3.9.16.
- MySQL de integración: 8.4.11, ejecutado en contenedor Docker efímero.
- POM: JUnit Jupiter 5.13.4, JavaFX 26.0.1, `maven.compiler.release=26`.

## Línea base del proyecto original

Se ejecutó `mvn -f /workspaces/JavaFxMySQL/examen2_Soto/pom.xml test` antes de implementar HU-01, con Java 25 y sin servidor MySQL. Resultado: 11 pruebas, 0 fallos de aserción, 6 errores de conexión (`Connection refused`) y 0 omitidas. Las 5 unitarias pasaron; las 6 pruebas de integración dependían de MySQL externo.

## Resultados posteriores

| Comando/alcance | Ejecutadas | Aprobadas | Fallidas | Errores | Omitidas |
|---|---:|---:|---:|---:|---:|
| `-Dtest=TestTarjeta test` | 8 | 8 | 0 | 0 | 0 |
| `-Dtest=TestFormularioTarjeta test` | 1 | 1 | 0 | 0 | 0 |
| `-Dtest=TestDAOTarjeta test` con MySQL | 6 | 6 | 0 | 0 | 0 |
| `clean verify` con MySQL | 18 | 18 | 0 | 0 | 0 |
| `clean test` sin MySQL | 18 | 9 | 0 | 0 | 9 |

La ejecución completa se realizó con JDK 26.0.2 y MySQL 8.4.11. La instancia era efímera y los valores de conexión se pasaron solo en el entorno del proceso Maven; no se guardaron credenciales en el repositorio. Incluyó los tres estados, la escritura y consulta del enum, listado DAO y el rechazo MySQL de un valor inválido.

También se ejecutó el script de migración sobre una tabla legada aislada con dos registros (`1` y `0`). Resultado observado: `L0001 → ACTIVA`, `L0002 → BLOQUEADA`; ambos números y fechas permanecieron.

## Ejecución reproducible

Sin servicio de base de datos, ejecutar desde la raíz:

```sh
mvn -f examen2_Soto/pom.xml clean test
```

Las pruebas unitarias no requieren MySQL. Las pruebas de integración tienen condiciones explícitas sobre `DB_URL`, `DB_USER` y `DB_PASSWORD`; sin ellas se omiten.

Con MySQL inicializado y esas variables exportadas en una base desechable:

```sh
mvn -f examen2_Soto/pom.xml clean verify
```

Para la ejecución completa se levantó `mysql:8.4`, se cargó `ScriptDB.sql` y Maven apuntó a `jdbc:mysql://127.0.0.1:33060/examen2_soto`. La contraseña temporal no se incluye en esta documentación.

## Cobertura y límites

- Dominio: enum exacto, representaciones, transiciones y rechazo de estado nulo/desconocido.
- Persistencia real MySQL: crear/leer los tres estados, `readall` y rechazo del valor `INVALIDA` por `CHECK`.
- Pruebas existentes de titulares, ejecutadas también contra el MySQL temporal.
- No se ejecutó una prueba JavaFX/FXML interactiva: el contenedor no tiene `DISPLAY`, `xvfb-run` ni servidor gráfico. La interfaz está compilada, pero el lanzamiento visual queda por verificar en escritorio.
- Sí se validó sin abrir una ventana el XML del FXML, el nombre de su controlador y la presencia del selector `cmbEstado` con prompt obligatorio.
- No se probaron actualización/eliminación: esas operaciones no existen en el código original.