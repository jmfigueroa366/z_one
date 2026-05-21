package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class ConexionDB {
    private static final String HOST     = "localhost";
    private static final String PORT     = "1521";
    private static final String SID      = "XE"; 
    private static final String DB_USER  = "System";  
    private static final String DB_PASS  = "oracle123"; 
    private static final String URL =
        "jdbc:oracle:thin:@" + HOST + ":" + PORT + ":" + SID;
    private static Connection instancia = null;
    private ConexionDB() {}
    public static Connection getConexion() {
        try {
            if (instancia == null || instancia.isClosed()) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                instancia = DriverManager.getConnection(URL, DB_USER, DB_PASS);
                System.out.println("Conexión a Oracle establecida correctamente.");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "Driver Oracle no encontrado. Agrega ojdbc11.jar al classpath.\n" + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(
                "Error al conectar con Oracle: " + e.getMessage() +
                "\nVerifica HOST, PORT, SID, usuario y contraseña en ConexionDB.java");
        }
        return instancia;
    }
    public static void cerrarConexion() {
        try {
            if (instancia != null && !instancia.isClosed()) {
                instancia.close();
                instancia = null;
                System.out.println("Conexión a Oracle cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
    public static boolean probarConexion() {
        try {
            Connection conn = getConexion();
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
}
