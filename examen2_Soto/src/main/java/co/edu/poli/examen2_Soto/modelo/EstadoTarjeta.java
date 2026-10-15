package co.edu.poli.examen2_Soto.modelo;

import java.util.Objects;

/**
 * Estados que puede tener una tarjeta y que se persisten usando su nombre.
 */
public enum EstadoTarjeta {
	ACTIVA,
	BLOQUEADA,
	CANCELADA;

	/**
	 * Convierte el valor persistido a un estado del dominio.
	 *
	 * @param valor valor almacenado en la base de datos
	 * @return estado correspondiente
	 * @throws NullPointerException si el valor es nulo
	 * @throws IllegalArgumentException si el valor no es uno de los estados definidos
	 */
	public static EstadoTarjeta desdeBaseDeDatos(String valor) {
		return EstadoTarjeta.valueOf(Objects.requireNonNull(valor, "El estado de la tarjeta es obligatorio"));
	}
}