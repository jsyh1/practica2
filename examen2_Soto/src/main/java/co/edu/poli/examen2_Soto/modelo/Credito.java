package co.edu.poli.examen2_Soto.modelo;

/** Tarjeta de credito con limite asociado. */
public class Credito extends Tarjeta {

	private double limite;

	/**
	 * Crea una tarjeta de credito.
	 *
	 * @param numero identificador de tarjeta
	 * @param fechaExp fecha elegida en el formulario
	 * @param estado estado inicial obligatorio
	 * @param titular titular asociado
	 * @param limite limite de credito
	 */
	public Credito(String numero, String fechaExp, EstadoTarjeta estado, Titular titular, double limite) {
		super(numero, fechaExp, estado, titular);
		this.limite = limite;
	}

	public double getLimite() {
		return limite;
	}

	public void setLimite(double limite) {
		this.limite = limite;
	}

	@Override
	public String toString() {
		return "Credito [" + super.toString() + ", limite=" + limite + "]";
	}
}
