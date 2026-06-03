package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class ConexionDB { 
    private static final String DB_USER  = "PRODUCTORA_BD";  
    private static final String DB_PASS  = "productora123"; 
  private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
  static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver Oracle no encontrado", e);
        }
    }

    // Cada llamada devuelve una conexión NUEVA
public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASS);
    }

    // Solo para verificar conectividad, abre y cierra rápido
    public static boolean probarConexion() {
        try (Connection c = getConexion()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}