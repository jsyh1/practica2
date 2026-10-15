package co.edu.poli.examen2_Soto.servicios;

import co.edu.poli.examen2_Soto.modelo.Credito;
import co.edu.poli.examen2_Soto.modelo.Debito;
import co.edu.poli.examen2_Soto.modelo.EstadoTarjeta;
import co.edu.poli.examen2_Soto.modelo.Tarjeta;
import co.edu.poli.examen2_Soto.modelo.Titular;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Acceso JDBC a tarjetas y sus tablas de subtipos. */
public class DAOTarjeta implements CRUD<Tarjeta> {

	/**
	 * Persiste una tarjeta y su subtipo dentro de una transaccion.
	 *
	 * @param t tarjeta con estado y titular obligatorios
	 * @return confirmacion de la tarjeta creada
	 * @throws Exception si los datos son invalidos o falla la persistencia
	 */
	@Override
	public String create(Tarjeta t) throws Exception {
		Objects.requireNonNull(t, "La tarjeta es obligatoria");
		Objects.requireNonNull(t.getEstado(), "El estado de la tarjeta es obligatorio");
		Objects.requireNonNull(t.getTitular(), "El titular de la tarjeta es obligatorio");
		if (!(t instanceof Debito) && !(t instanceof Credito)) {
			throw new IllegalArgumentException("Tipo de tarjeta no soportado: " + t.getClass().getName());
		}
		Connection con = ConexionBD.getInstancia().getConexion();
		boolean autoCommitOriginal = con.getAutoCommit();
		try {
			con.setAutoCommit(false);
			String insertTarjeta = "INSERT INTO tarjeta (numero, fecha_exp, estado, titular_id) VALUES (?, ?, ?, ?)";
			try (PreparedStatement statement = con.prepareStatement(insertTarjeta)) {
				statement.setString(1, t.getNumero());
				statement.setString(2, t.getFechaExp());
				statement.setString(3, t.getEstado().name());
				statement.setString(4, t.getTitular().getId());
				statement.executeUpdate();
			}

			String insertSubtype = t instanceof Debito
					? "INSERT INTO tarjeta_debito (numero, saldo) VALUES (?, ?)"
					: "INSERT INTO tarjeta_credito (numero, limite) VALUES (?, ?)";
			try (PreparedStatement statement = con.prepareStatement(insertSubtype)) {
				statement.setString(1, t.getNumero());
				if (t instanceof Debito) {
					statement.setDouble(2, ((Debito) t).getSaldo());
				} else {
					statement.setDouble(2, ((Credito) t).getLimite());
				}
				statement.executeUpdate();
			}
			con.commit();
			return "✔ " + t.getClass().getSimpleName() + " [" + t.getNumero() + "] guardada correctamente. Estado: "
					+ t.getEstado() + ".";
		} catch (Exception exception) {
			try {
				con.rollback();
			} catch (SQLException rollbackException) {
				exception.addSuppressed(rollbackException);
			}
			throw exception;
		} finally {
			con.setAutoCommit(autoCommitOriginal);
		}
	}

	/**
	 * Recupera una tarjeta por numero y convierte su estado persistido al enum.
	 *
	 * @param num numero de tarjeta
	 * @param <K> tipo de la clave recibido por la interfaz CRUD
	 * @return tarjeta encontrada, o {@code null} si no existe
	 * @throws Exception si falla la consulta o el estado almacenado es invalido
	 */
	@Override
	public <K> Tarjeta readone(K num) throws Exception {

		Connection con = ConexionBD.getInstancia().getConexion();

		String SQL_SELECT_DEBITO = "SELECT  t.numero, t.fecha_exp, t.estado, "
				+ "        ti.id AS titular_id, ti.nombre AS titular_nombre, " + "        d.saldo "
				+ "FROM    tarjeta_debito d " + "INNER JOIN tarjeta  t  ON d.numero     = t.numero "
				+ "INNER JOIN titular  ti ON t.titular_id = ti.id " + "WHERE   d.numero = ?";

		try (PreparedStatement statement = con.prepareStatement(SQL_SELECT_DEBITO)) {
			statement.setString(1, (String) num);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					return new Debito(result.getString("numero"), result.getString("fecha_exp"),
							EstadoTarjeta.desdeBaseDeDatos(result.getString("estado")),
							new Titular(result.getString("titular_id"), result.getString("titular_nombre")),
							result.getDouble("saldo"));
				}
			}
		}

		String SQL_SELECT_CREDITO = "SELECT  t.numero, t.fecha_exp, t.estado, "
				+ "        ti.id AS titular_id, ti.nombre AS titular_nombre, " + "        c.limite "
				+ "FROM    tarjeta_credito c " + "INNER JOIN tarjeta  t  ON c.numero     = t.numero "
				+ "INNER JOIN titular  ti ON t.titular_id = ti.id " + "WHERE   c.numero = ?";

		try (PreparedStatement statement = con.prepareStatement(SQL_SELECT_CREDITO)) {
			statement.setString(1, (String) num);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					return new Credito(result.getString("numero"), result.getString("fecha_exp"),
							EstadoTarjeta.desdeBaseDeDatos(result.getString("estado")),
							new Titular(result.getString("titular_id"), result.getString("titular_nombre")),
							result.getDouble("limite"));
				}
			}
		}

		return null;
	}

	/**
	 * Recupera las tarjetas de debito y credito.
	 *
	 * @return lista de tarjetas; vacia si no hay registros
	 * @throws Exception si falla la consulta o un estado persistido no es valido
	 */
	@Override
	public List<Tarjeta> readall() throws Exception {
		String SQL_SELECT_DEBITOS = "SELECT t.numero, t.fecha_exp, t.estado, ti.id AS titular_id, "
				+ "ti.nombre AS titular_nombre, d.saldo FROM tarjeta_debito d "
				+ "INNER JOIN tarjeta t ON d.numero = t.numero INNER JOIN titular ti ON t.titular_id = ti.id";
		String SQL_SELECT_CREDITOS = "SELECT t.numero, t.fecha_exp, t.estado, ti.id AS titular_id, "
				+ "ti.nombre AS titular_nombre, c.limite FROM tarjeta_credito c "
				+ "INNER JOIN tarjeta t ON c.numero = t.numero INNER JOIN titular ti ON t.titular_id = ti.id";
		List<Tarjeta> tarjetas = new ArrayList<>();
		Connection con = ConexionBD.getInstancia().getConexion();
		try (PreparedStatement statement = con.prepareStatement(SQL_SELECT_DEBITOS);
				ResultSet result = statement.executeQuery()) {
			while (result.next()) {
				tarjetas.add(new Debito(result.getString("numero"), result.getString("fecha_exp"),
						EstadoTarjeta.desdeBaseDeDatos(result.getString("estado")),
						new Titular(result.getString("titular_id"), result.getString("titular_nombre")),
						result.getDouble("saldo")));
			}
		}
		try (PreparedStatement statement = con.prepareStatement(SQL_SELECT_CREDITOS);
				ResultSet result = statement.executeQuery()) {
			while (result.next()) {
				tarjetas.add(new Credito(result.getString("numero"), result.getString("fecha_exp"),
						EstadoTarjeta.desdeBaseDeDatos(result.getString("estado")),
						new Titular(result.getString("titular_id"), result.getString("titular_nombre")),
						result.getDouble("limite")));
			}
		}
		return tarjetas;
	}
}
