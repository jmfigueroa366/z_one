package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria Singleton para gestionar la conexión a Oracle SQL Developer.
 * Proporciona una única instancia de conexión reutilizable en todo el sistema.
 *
 * CONFIGURACIÓN: Cambiar los valores de HOST, PORT, SID, USER y PASSWORD
 * según tu instalación local de Oracle.
 *
 * @author Equipo Z-One
 */
public class ConexionDB {

    // ── Configuración de conexión ── ¡AJUSTA ESTOS VALORES! ──────────────
    private static final String HOST     = "localhost";
    private static final String PORT     = "1521";
    private static final String SID      = "XE";           // o el nombre de tu BD
    private static final String DB_USER  = "system";       // tu usuario Oracle
    private static final String DB_PASS  = "Jesusrey25*";       // tu contraseña Oracle
    // ─────────────────────────────────────────────────────────────────────

    private static final String URL =
        "jdbc:oracle:thin:@" + HOST + ":" + PORT + ":" + SID;

    private static Connection instancia = null;

    /** Constructor privado — impide instanciación externa */
    private ConexionDB() {}

    /**
     * Retorna la conexión activa. Si está cerrada o es nula, la crea de nuevo.
     *
     * @return Connection activa con la base de datos Oracle
     * @throws RuntimeException si no se puede establecer la conexión
     */
    public static Connection getConexion() {
        try {
            if (instancia == null || instancia.isClosed()) {
                // Cargar driver JDBC de Oracle
                Class.forName("oracle.jdbc.driver.OracleDriver");
                instancia = DriverManager.getConnection(URL, DB_USER, DB_PASS);
                System.out.println("✅ Conexión a Oracle establecida correctamente.");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "❌ Driver Oracle no encontrado. Agrega ojdbc11.jar al classpath.\n" + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(
                "❌ Error al conectar con Oracle: " + e.getMessage() +
                "\nVerifica HOST, PORT, SID, usuario y contraseña en ConexionDB.java");
        }
        return instancia;
    }

    /**
     * Cierra la conexión activa si existe y está abierta.
     * Llamar al cerrar la aplicación.
     */
    public static void cerrarConexion() {
        try {
            if (instancia != null && !instancia.isClosed()) {
                instancia.close();
                instancia = null;
                System.out.println("🔌 Conexión a Oracle cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("⚠ Error al cerrar la conexión: " + e.getMessage());
        }
    }

    /**
     * Prueba rápida de conectividad. Útil para verificar la configuración.
     *
     * @return true si la conexión está activa, false en caso contrario
     */
    public static boolean probarConexion() {
        try {
            Connection conn = getConexion();
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
}
