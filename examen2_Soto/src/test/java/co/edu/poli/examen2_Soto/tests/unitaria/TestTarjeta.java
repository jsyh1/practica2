package co.edu.poli.examen2_Soto.tests.unitaria;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import co.edu.poli.examen2_Soto.modelo.Credito;
import co.edu.poli.examen2_Soto.modelo.Debito;
import co.edu.poli.examen2_Soto.modelo.EstadoTarjeta;
import co.edu.poli.examen2_Soto.modelo.Tarjeta;
import co.edu.poli.examen2_Soto.modelo.Titular;
import co.edu.poli.examen2_Soto.servicios.DAOTarjeta;

public class TestTarjeta {

	@Test
	void bloquear_cambiaEstadoAFalso() {
		Titular titular = new Titular("1", "Test");

		Tarjeta t = new Debito("123", "2025-12-25", EstadoTarjeta.ACTIVA, titular, 1000);

		String mensaje = t.bloquear();

		assertEquals(EstadoTarjeta.BLOQUEADA, t.getEstado());
		assertTrue(mensaje.contains("BLOQUEADA"));
	}

	@Test
	void activar_cambiaEstadoAVerdadero() {
		Titular titular = new Titular("1", "Test");

		Tarjeta t = new Debito("123", "2025-12-25", EstadoTarjeta.BLOQUEADA, titular, 1000);

		String mensaje = t.activar();

		assertEquals(EstadoTarjeta.ACTIVA, t.getEstado());
		assertTrue(mensaje.contains("ACTIVADA"));
	}

	@Test
	void cancelar_cambiaEstadoACancelada() {
		Tarjeta tarjeta = new Debito("123", "2025-12-25", EstadoTarjeta.ACTIVA, new Titular("1", "Test"), 1000);

		String mensaje = tarjeta.cancelar();

		assertEquals(EstadoTarjeta.CANCELADA, tarjeta.getEstado());
		assertTrue(mensaje.contains("CANCELADA"));
	}

	@Test
	void getters_retornaValoresCorrectos() {
		Titular titular = new Titular("1", "Test");

		Tarjeta t = new Debito("123", "2025-12-25", EstadoTarjeta.ACTIVA, titular, 1000);

		assertEquals("123", t.getNumero());
		assertEquals("2025-12-25", t.getFechaExp());
		assertEquals(EstadoTarjeta.ACTIVA, t.getEstado());
		assertEquals(titular, t.getTitular());
	}

	@Test
	void setters_modificanValores() {
		Titular titular = new Titular("1", "Test");
		Titular nuevo = new Titular("2", "Nuevo");

		Tarjeta t = new Debito("123", "2025-12-25", EstadoTarjeta.ACTIVA, titular, 1000);

		t.setNumero("999");
		t.setFechaExp("2030-01-01");
		t.setEstado(EstadoTarjeta.CANCELADA);
		t.setTitular(nuevo);

		assertEquals("999", t.getNumero());
		assertEquals("2030-01-01", t.getFechaExp());
		assertEquals(EstadoTarjeta.CANCELADA, t.getEstado());
		assertEquals(nuevo, t.getTitular());
	}

	@Test
	void credito_conservaYActualizaLimite() {
		Credito tarjeta = new Credito("456", "2026-10-15", EstadoTarjeta.ACTIVA, new Titular("2", "Nuevo"), 5000);

		assertEquals(5000, tarjeta.getLimite());

		tarjeta.setLimite(7500);

		assertEquals(7500, tarjeta.getLimite());
	}

	@Test
	void toString_contieneDatos() {
		Titular titular = new Titular("1", "Test");

		Tarjeta t = new Debito("123", "2025-12-25", EstadoTarjeta.ACTIVA, titular, 1000);

		String texto = t.toString();

		assertTrue(texto.contains("123"));
		assertTrue(texto.contains("2025-12-25"));
	}

	@Test
	void enum_contieneSoloLosEstadosPermitidos() {
		assertArrayEquals(new EstadoTarjeta[] { EstadoTarjeta.ACTIVA, EstadoTarjeta.BLOQUEADA,
				EstadoTarjeta.CANCELADA }, EstadoTarjeta.values());
		assertEquals("ACTIVA", EstadoTarjeta.ACTIVA.name());
	}

	@Test
	void estadoPersistido_invalidoONuloSeRechaza() {
		assertThrows(IllegalArgumentException.class, () -> EstadoTarjeta.desdeBaseDeDatos("DESCONOCIDA"));
		assertThrows(NullPointerException.class, () -> EstadoTarjeta.desdeBaseDeDatos(null));
		assertThrows(NullPointerException.class,
				() -> new Debito("123", "2025-12-25", null, new Titular("1", "Test"), 1000));
	}

	@Test
	void dao_rechazaTarjetaNulaAntesDeConectarse() {
		assertThrows(NullPointerException.class, () -> new DAOTarjeta().create(null));
	}

	@Test
	void dao_rechazaTitularNuloAntesDeConectarse() {
		Tarjeta tarjeta = new Debito("123", "2025-12-25", EstadoTarjeta.ACTIVA, null, 1000);

		assertThrows(NullPointerException.class, () -> new DAOTarjeta().create(tarjeta));
	}
}
