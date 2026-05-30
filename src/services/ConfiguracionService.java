package services;

import util.ConexionDB;
import util.PasswordUtil;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de configuracion: tema, perfil, password y backup de BD.
 */
public class ConfiguracionService {

    // ════════════════════════════════════════════════════════════════
    //  PERFIL Y PASSWORD
    // ════════════════════════════════════════════════════════════════

    /** Cambia la contrasena de un usuario validando primero la actual. */
    public boolean cambiarPassword(int idUsuario, String passActual, String passNueva)
            throws SQLException {
        // 1. Verificar la contrasena actual
        String hashActual = PasswordUtil.sha256(passActual);
        String sqlCheck = "SELECT contrasena FROM usuarios WHERE id_usuarios = ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sqlCheck)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    throw new SQLException("Usuario no encontrado");
                String hashBD = rs.getString("contrasena");
                if (!hashActual.equals(hashBD))
                    throw new SQLException("La contrasena actual es incorrecta");
            }
        }

        // 2. Actualizar a la nueva
        String hashNuevo = PasswordUtil.sha256(passNueva);
        String sqlUpd = "UPDATE usuarios SET contrasena = ? WHERE id_usuarios = ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sqlUpd)) {
            ps.setString(1, hashNuevo);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    /** Actualiza datos del perfil (correo y nombre completo). */
    public boolean actualizarPerfil(int idUsuario, String correo, String nombreCompleto)
            throws SQLException {
        String sql = "UPDATE usuarios SET correo = ?, nombre_completo = ? " +
                     "WHERE id_usuarios = ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, nombreCompleto);
            ps.setInt(3, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  BACKUP / EXPORTAR
    // ════════════════════════════════════════════════════════════════

    /** Tablas exportables. */
    public static final String[] TABLAS_BACKUP = {
        "usuarios", "artistas", "productores", "canciones",
        "sesion_grabaciones", "cabinas", "eventos_agendados", "colaboraciones",
        "genero_musicales", "nacionalidades", "estados_canciones"
    };

    /**
     * Exporta todas las tablas a archivos CSV en una carpeta con timestamp.
     * Devuelve la ruta de la carpeta creada.
     */
    public String exportarCSV() throws Exception {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path carpeta = Paths.get("backup", "csv_" + timestamp);
        Files.createDirectories(carpeta);

        for (String tabla : TABLAS_BACKUP) {
            try {
                exportarTablaCSV(tabla, carpeta.resolve(tabla + ".csv").toFile());
            } catch (SQLException ex) {
                System.err.println("Tabla " + tabla + " no exportada: " + ex.getMessage());
            }
        }
        return carpeta.toAbsolutePath().toString();
    }

    private void exportarTablaCSV(String tabla, File archivo) throws Exception {
        try (Connection c = ConexionDB.getConexion();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + tabla);
             PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                     new FileOutputStream(archivo), java.nio.charset.StandardCharsets.UTF_8))) {

            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();

            // Cabecera
            StringBuilder cab = new StringBuilder();
            for (int i = 1; i <= cols; i++) {
                if (i > 1) cab.append(",");
                cab.append(md.getColumnName(i));
            }
            pw.println(cab);

            // Filas
            while (rs.next()) {
                StringBuilder fila = new StringBuilder();
                for (int i = 1; i <= cols; i++) {
                    if (i > 1) fila.append(",");
                    Object v = rs.getObject(i);
                    if (v != null) {
                        String s = v.toString().replace("\"", "\"\"");
                        if (s.contains(",") || s.contains("\n") || s.contains("\""))
                            fila.append("\"").append(s).append("\"");
                        else
                            fila.append(s);
                    }
                }
                pw.println(fila);
            }
        }
    }

    /**
     * Exporta un informe en formato HTML que se puede imprimir/guardar como PDF
     * desde el navegador.
     */
    public String exportarInformeHTML() throws Exception {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path carpeta = Paths.get("backup");
        Files.createDirectories(carpeta);
        File archivo = carpeta.resolve("informe_zone_" + timestamp + ".html").toFile();

        EstadisticasService stats = new EstadisticasService();
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'>");
        html.append("<title>Informe Z-One Music</title>");
        html.append("<style>");
        html.append("body{font-family:Arial,sans-serif;margin:40px;color:#222;}");
        html.append("h1{color:#1A6EBE;border-bottom:3px solid #1A6EBE;padding-bottom:8px;}");
        html.append("h2{color:#00BCD4;margin-top:30px;}");
        html.append(".grid{display:grid;grid-template-columns:repeat(3,1fr);gap:14px;margin:20px 0;}");
        html.append(".card{border:1px solid #ddd;border-radius:8px;padding:14px;background:#f9fbff;}");
        html.append(".card .num{font-size:32px;font-weight:bold;color:#1A6EBE;}");
        html.append(".card .lbl{font-size:11px;color:#666;text-transform:uppercase;}");
        html.append("table{width:100%;border-collapse:collapse;margin:10px 0;}");
        html.append("th{background:#1A6EBE;color:white;padding:8px;text-align:left;}");
        html.append("td{border-bottom:1px solid #eee;padding:6px 8px;}");
        html.append(".footer{margin-top:40px;font-size:11px;color:#888;text-align:center;}");
        html.append("</style></head><body>");

        html.append("<h1>🎵 Z-One Music — Informe General</h1>");
        html.append("<p><b>Generado:</b> ")
            .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
            .append("</p>");

        // Cards
        html.append("<h2>📊 Estadísticas generales</h2><div class='grid'>");
        appendCard(html, "Artistas",       stats.totalArtistas());
        appendCard(html, "Productores",    stats.totalProductores());
        appendCard(html, "Canciones",      stats.totalCanciones());
        appendCard(html, "Sesiones",       stats.totalSesiones());
        appendCard(html, "Cabinas",        stats.totalCabinas());
        appendCard(html, "Eventos",        stats.totalEventos());
        appendCard(html, "Colaboraciones", stats.totalColaboraciones());
        appendCard(html, "Canciones publicadas", stats.cancionesPublicadas());
        appendCard(html, "Cabinas disponibles",  stats.cabinasDisponibles());
        html.append("</div>");

        // Top artistas
        html.append("<h2>🎤 Listado de artistas</h2>");
        appendTabla(html,
                "SELECT a.nombre_artista AS \"Nombre\", " +
                "       n.nombre AS \"Nacionalidad\", " +
                "       gm.nombre AS \"Genero\", " +
                "       e.nombre AS \"Estado\" " +
                "FROM artistas a " +
                "LEFT JOIN nacionalidades n ON a.id_nacionalidad = n.id_nacionalidad " +
                "LEFT JOIN genero_musicales gm ON a.id_genero_musical = gm.id_genero " +
                "LEFT JOIN estados_art_pro e ON a.id_estado = e.id_estado " +
                "ORDER BY a.nombre_artista");

        // Productores
        html.append("<h2>🎚 Listado de productores</h2>");
        appendTabla(html,
                "SELECT p.nombre AS \"Nombre\", " +
                "       p.especialidad AS \"Especialidad\", " +
                "       p.tarifa_hora AS \"Tarifa/h\", " +
                "       n.nombre AS \"Nacionalidad\" " +
                "FROM productores p " +
                "LEFT JOIN nacionalidades n ON p.id_nacionalidad = n.id_nacionalidad " +
                "ORDER BY p.nombre");

        html.append("<div class='footer'>Z-One Music v1.0 — Informe automatizado</div>");
        html.append("</body></html>");

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(archivo), java.nio.charset.StandardCharsets.UTF_8))) {
            pw.print(html);
        }
        return archivo.getAbsolutePath();
    }

    private void appendCard(StringBuilder html, String etiqueta, int valor) {
        html.append("<div class='card'>")
            .append("<div class='lbl'>").append(etiqueta).append("</div>")
            .append("<div class='num'>").append(valor).append("</div>")
            .append("</div>");
    }

    private void appendTabla(StringBuilder html, String sql) {
        try (Connection c = ConexionDB.getConexion();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            html.append("<table><tr>");
            for (int i = 1; i <= cols; i++)
                html.append("<th>").append(md.getColumnLabel(i)).append("</th>");
            html.append("</tr>");
            while (rs.next()) {
                html.append("<tr>");
                for (int i = 1; i <= cols; i++) {
                    Object v = rs.getObject(i);
                    html.append("<td>").append(v != null ? v.toString() : "—").append("</td>");
                }
                html.append("</tr>");
            }
            html.append("</table>");
        } catch (SQLException ex) {
            html.append("<p><i>Error al cargar: ").append(ex.getMessage()).append("</i></p>");
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  TEMA (preferencia local persistida en archivo)
    // ════════════════════════════════════════════════════════════════

    private static final Path RUTA_TEMA = Paths.get("config", "tema.txt");

    public String temaActual() {
        try {
            if (Files.exists(RUTA_TEMA))
                return Files.readString(RUTA_TEMA).trim();
        } catch (IOException ignore) {}
        return "oscuro";  // por defecto
    }

    public void guardarTema(String tema) {
        try {
            Files.createDirectories(RUTA_TEMA.getParent());
            Files.writeString(RUTA_TEMA, tema);
        } catch (IOException ignore) {}
    }
}