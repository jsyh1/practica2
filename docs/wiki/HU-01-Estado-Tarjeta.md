# HU-01: Estado de tarjeta

## Historia

Como analista de crédito, quiero asignar un estado a una tarjeta para controlar si puede ser utilizada.

Estados admitidos: `ACTIVA`, `BLOQUEADA` y `CANCELADA`. El selector FXML es obligatorio; el modelo y el DAO rechazan un estado ausente/inválido, y MySQL lo restringe mediante `NOT NULL` y `CHECK`. El DAO persiste el nombre del enum y lo restaura al consultar.

## Arquitectura afectada

`ControlFormCard` valida el selector `cmbEstado` en `formCard.fxml`. `Tarjeta` contiene el enum `EstadoTarjeta`; `Debito` y `Credito` conservan la herencia original. `DAOTarjeta` escribe estado y datos de subtipo con JDBC y lee los tres estados. No se introdujo un servicio porque la arquitectura existente usa DAO directamente.

## Persistencia

Para instalaciones nuevas, consultar `ScriptDB.sql`. Para migrar la tabla original, ejecutar una copia de seguridad y después `docs/database/hu-01-estado-tarjeta.sql` una sola vez. La migración conserva el significado antiguo (`1 → ACTIVA`, `0 → BLOQUEADA`) y no asigna arbitrariamente `CANCELADA` a registros históricos.

## Verificación

La evidencia reproducible está en `docs/pruebas-hu-01.md`. Incluye resultados medidos de JDK 26 y MySQL 8.4.11, junto con la limitación de no haber ejecutado una prueba gráfica por falta de servidor de pantalla.

## Publicación manual

El repositorio remoto de destino no expone una Wiki clonable (`git ls-remote` devolvió `Repository not found`), por lo que este contenido queda preparado localmente y no se publicó. Si la Wiki se habilita y se conceden permisos, crear/actualizar la página `HU-01-Estado-Tarjeta` con este contenido y enlazar el diagrama de clases y los documentos del repositorio.