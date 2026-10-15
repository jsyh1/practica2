package co.edu.poli.examen2_Soto.modelo;

/** Tarjeta de debito con saldo asociado. */
public class Debito extends Tarjeta {

	private double saldo;

	/**
	 * Crea una tarjeta de debito.
	 *
	 * @param numero identificador de tarjeta
	 * @param fechaExp fecha elegida en el formulario
	 * @param estado estado inicial obligatorio
	 * @param titular titular asociado
	 * @param saldo saldo disponible
	 */
	public Debito(String numero, String fechaExp, EstadoTarjeta estado, Titular titular, double saldo) {
		super(numero, fechaExp, estado, titular);
		this.saldo = saldo;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	@Override
	public String toString() {
		return "Debito [" + super.toString() + ", saldo=" + saldo + "]";
	}
}
