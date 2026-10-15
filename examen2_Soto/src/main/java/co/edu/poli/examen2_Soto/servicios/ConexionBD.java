package co.edu.poli.examen2_Soto.servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Proporciona una conexion JDBC MySQL usando configuracion externa al codigo. */
public class ConexionBD {

	private static ConexionBD instancia;
	private Connection conexion;

	private ConexionBD() throws Exception {
		String url = configuracion("DB_URL");
		String user = configuracion("DB_USER");
		String pass = configuracion("DB_PASSWORD");

		if (url == null || user == null || pass == null) {
			throw new IllegalStateException("Configure DB_URL, DB_USER y DB_PASSWORD como variables de entorno o propiedades JVM.");
		}

		conexion = DriverManager.getConnection(url, user, pass);
	}

	private static String configuracion(String nombre) {
		String valor = System.getProperty(nombre);
		return valor != null ? valor : System.getenv(nombre);
	}

	/**
	 * Obtiene la instancia de conexion, creandola cuando hace falta.
	 *
	 * @return instancia conectada
	 * @throws Exception si falta configuracion o falla la conexion
	 */
	public static synchronized ConexionBD getInstancia() throws Exception {
		if (instancia == null) {
			instancia = new ConexionBD();
		}
		return instancia;
	}

	/**
	 * Devuelve la conexion JDBC activa.
	 *
	 * @return conexion a MySQL
	 * @throws Exception si no puede establecerse la conexion
	 */
	public Connection getConexion() throws Exception {
		if (conexion == null || conexion.isClosed()) {
			instancia = null;
			return getInstancia().conexion;
		}
		return conexion;
	}
}
