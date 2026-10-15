package co.edu.poli.examen2_Soto.tests.integracion;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.Test;

import co.edu.poli.examen2_Soto.modelo.Credito;
import co.edu.poli.examen2_Soto.modelo.Debito;
import co.edu.poli.examen2_Soto.modelo.EstadoTarjeta;
import co.edu.poli.examen2_Soto.modelo.Tarjeta;
import co.edu.poli.examen2_Soto.modelo.Titular;
import co.edu.poli.examen2_Soto.servicios.DAOTarjeta;
import co.edu.poli.examen2_Soto.servicios.ConexionBD;

@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
@EnabledIfEnvironmentVariable(named = "DB_USER", matches = ".+")
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".*")
public class TestDAOTarjeta {

    private static final AtomicLong NUMEROS = new AtomicLong(System.currentTimeMillis());
    DAOTarjeta dao = new DAOTarjeta();

    @Test
    void create_debito_y_readone() throws Exception {

        Titular titular = new Titular("T001", "Test");

        String numero = nuevoNumero();
        Debito debito = new Debito(
            numero,
                "2025-12-25",
            EstadoTarjeta.BLOQUEADA,
                titular,
                5000.0
        );

        String result = dao.create(debito);

        assertTrue(result.contains("guardada"));

        Tarjeta t = dao.readone(numero);

        assertNotNull(t);
        assertTrue(t instanceof Debito);

        Debito d = (Debito) t;
        assertEquals(5000.0, d.getSaldo());
        assertEquals(EstadoTarjeta.BLOQUEADA, d.getEstado());
    }

    @Test
    void create_credito_y_readone() throws Exception {

        Titular titular = new Titular("T001", "Test");

        String numero = nuevoNumero();
        Credito credito = new Credito(
            numero,
                "2025-12-25",
            EstadoTarjeta.CANCELADA,
                titular,
                10000.0
        );

        String result = dao.create(credito);

        assertTrue(result.contains("guardada"));

        Tarjeta t = dao.readone(numero);

        assertNotNull(t);
        assertTrue(t instanceof Credito);

        Credito c = (Credito) t;
        assertEquals(10000.0, c.getLimite());
        assertEquals(EstadoTarjeta.CANCELADA, c.getEstado());
    }

    @Test
    void create_y_readone_persistenEstadoActivo() throws Exception {
        String numero = nuevoNumero();
        Debito debito = new Debito(numero, "2025-12-25", EstadoTarjeta.ACTIVA,
                new Titular("T001", "Test"), 100.0);

        assertTrue(dao.create(debito).contains("guardada"));
        assertEquals(EstadoTarjeta.ACTIVA, dao.readone(numero).getEstado());
    }

    @Test
    void mysqlRechazaEstadoNoPermitido() throws Exception {
        String insert = "INSERT INTO tarjeta (numero, fecha_exp, estado, titular_id) VALUES (?, ?, ?, ?)";
        Connection connection = ConexionBD.getInstancia().getConexion();
        try (PreparedStatement statement = connection.prepareStatement(insert)) {
            statement.setString(1, nuevoNumero());
            statement.setString(2, "12/2030");
            statement.setString(3, "INVALIDA");
            statement.setString(4, "T001");
            assertThrows(SQLException.class, statement::executeUpdate);
        }
    }

        @Test
        void readall_incluyeLasTarjetasPersistidas() throws Exception {
        String numero = nuevoNumero();
        Debito debito = new Debito(numero, "25/12/2028", EstadoTarjeta.ACTIVA,
            new Titular("T001", "Test"), 200.0);
        dao.create(debito);

        List<Tarjeta> tarjetas = dao.readall();

        assertTrue(tarjetas.stream().anyMatch(t -> numero.equals(t.getNumero())
            && t.getEstado() == EstadoTarjeta.ACTIVA));
        }

    @Test
    void readone_noExiste() throws Exception {

        Tarjeta t = dao.readone("000000");

        assertNull(t);
    }

    private static String nuevoNumero() {
        return Long.toString(NUMEROS.incrementAndGet());
    }
}
