package ec.edu.utmachala.titulacion.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Conexion {

	private static Conexion conexion;
	private static Connection connection;
	public static Conexion getConexion() {
		return Conexion.conexion;
	}
	public static Connection getConnection() {
		return Conexion.connection;
	}
	public static void iniciarConeccion(final String db, final String driver, final String server, final String port,
			final String database, final String user, final String password) {
		final Properties props = new Properties();
		if (Conexion.conexion == null) {
			try {
				Conexion.conexion = new Conexion(db, driver, server, port, database, user, password);
				final String url = "jdbc:" + db + "://" + server + ":" + port + "/" + database;
				Class.forName(driver);
				props.setProperty("user", user);
				props.setProperty("password", password);
				Conexion.connection = DriverManager.getConnection(url, props);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException sql) {
				sql.printStackTrace();
			}
		}
	}
	private String db;
	private String driver;
	private String server;
	private String port;

	private String database;

	private String user;

	private String password;

	private Conexion(final String db, final String driver, final String server, final String port,
			final String database, final String user, final String password) {
		this.db = db;
		this.driver = driver;
		this.server = server;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
	}

	public String getDatabase() {
		return this.database;
	}

	public String getDb() {
		return this.db;
	}

	public String getDriver() {
		return this.driver;
	}

	public String getPassword() {
		return this.password;
	}

	public String getPort() {
		return this.port;
	}

	public String getServer() {
		return this.server;
	}

	public String getUser() {
		return this.user;
	}

	public void setDatabase(final String database) {
		this.database = database;
	}

	public void setDb(final String db) {
		this.db = db;
	}

	public void setDriver(final String driver) {
		this.driver = driver;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public void setPort(final String port) {
		this.port = port;
	}

	public void setServer(final String server) {
		this.server = server;
	}

	public void setUser(final String user) {
		this.user = user;
	}
}