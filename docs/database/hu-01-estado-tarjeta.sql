-- Migracion de una sola ejecucion para el esquema original examen2_soto.
-- Requiere MySQL 8.0.16 o posterior (CHECK aplicado por el servidor).
-- Haga una copia de seguridad antes de ejecutarlo. No elimina ni reemplaza filas.
--
-- Conserva la semantica documentada en ScriptDB.sql:
--   1 / TRUE -> ACTIVA
--   0 / FALSE -> BLOQUEADA
-- No existe una decision de negocio verificable para CANCELADA en registros
-- heredados, por lo que no se asigna ese valor automaticamente.

USE examen2_soto;

DELIMITER //
DROP PROCEDURE IF EXISTS migrar_estado_tarjeta_hu01//
CREATE PROCEDURE migrar_estado_tarjeta_hu01()
BEGIN
    IF EXISTS (SELECT 1 FROM tarjeta WHERE estado NOT IN (0, 1)) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Migracion detenida: hay estados heredados distintos de 0 y 1.';
    END IF;

    ALTER TABLE tarjeta MODIFY COLUMN estado VARCHAR(10) NOT NULL;
    ALTER TABLE tarjeta MODIFY COLUMN fecha_exp VARCHAR(10) NOT NULL;

    UPDATE tarjeta
       SET estado = CASE estado
           WHEN '1' THEN 'ACTIVA'
           WHEN '0' THEN 'BLOQUEADA'
           ELSE estado
       END;

    ALTER TABLE tarjeta
        ADD CONSTRAINT chk_tarjeta_estado
        CHECK (estado IN ('ACTIVA', 'BLOQUEADA', 'CANCELADA'));
END//

CALL migrar_estado_tarjeta_hu01()//
DROP PROCEDURE migrar_estado_tarjeta_hu01//
DELIMITER ;

-- Verificacion posterior: debe devolver cero filas.
SELECT numero, estado
  FROM tarjeta
 WHERE estado NOT IN ('ACTIVA', 'BLOQUEADA', 'CANCELADA');