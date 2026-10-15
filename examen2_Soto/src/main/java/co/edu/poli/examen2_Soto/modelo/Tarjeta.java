package co.edu.poli.examen2_Soto.modelo;

import java.util.Objects;

/**
 * Datos comunes de una tarjeta de débito o crédito.
 */
public abstract class Tarjeta {

	private String numero;

	private String fechaExp;

	private EstadoTarjeta estado;

	private Titular titular;

	/**
	 * Construye una tarjeta con un estado perteneciente al dominio.
	 *
	 * @param numero identificador de tarjeta
	 * @param fechaExp fecha indicada en el formulario
	 * @param estado estado inicial obligatorio
	 * @param titular titular asociado
	 * @throws NullPointerException si estado es nulo
	 */
	public Tarjeta(String numero, String fechaExp, EstadoTarjeta estado, Titular titular) {
		this.numero = numero;
		this.fechaExp = fechaExp;
		this.estado = Objects.requireNonNull(estado, "El estado de la tarjeta es obligatorio");
		this.titular = titular;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getFechaExp() {
		return fechaExp;
	}

	public void setFechaExp(String fechaExp) {
		this.fechaExp = fechaExp;
	}

	/** @return estado actual de la tarjeta */
	public EstadoTarjeta getEstado() {
		return estado;
	}

	/**
	 * Cambia el estado sin admitir valores fuera del enum.
	 *
	 * @param estado nuevo estado obligatorio
	 * @throws NullPointerException si estado es nulo
	 */
	public void setEstado(EstadoTarjeta estado) {
		this.estado = Objects.requireNonNull(estado, "El estado de la tarjeta es obligatorio");
	}

	public Titular getTitular() {
		return titular;
	}

	public void setTitular(Titular titular) {
		this.titular = titular;
	}

	/** @return mensaje que identifica el bloqueo aplicado */
	public String bloquear() {
		this.estado = EstadoTarjeta.BLOQUEADA;
		return "Tarjeta " + numero + " BLOQUEADA.";
	}

	/** @return mensaje que identifica la activacion aplicada */
	public String activar() {
		this.estado = EstadoTarjeta.ACTIVA;
		return "Tarjeta " + numero + " ACTIVADA.";
	}

	/** @return mensaje que identifica la cancelacion aplicada */
	public String cancelar() {
		this.estado = EstadoTarjeta.CANCELADA;
		return "Tarjeta " + numero + " CANCELADA.";
	}

	@Override
	public String toString() {
		return "numero=" + numero + ", fechaExp=" + fechaExp + ", estado=" + estado + ", titular=" + titular;
	}
}
