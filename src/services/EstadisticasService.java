package services;

import util.ConexionDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para obtener estadisticas en tiempo real desde Oracle.
 * Centraliza todas las consultas de conteo y agregaciones para el Dashboard.
 */
public class EstadisticasService {

    // ════════════════════════════════════════════════════════════════
    //  CONTEOS GLOBALES PARA STAT CARDS
    // ════════════════════════════════════════════════════════════════

    public int totalArtistas() {
        return contar("SELECT COUNT(*) FROM artistas");
    }

    public int totalProductores() {
        return contar("SELECT COUNT(*) FROM productores");
    }

    public int totalCanciones() {
        return contar("SELECT COUNT(*) FROM canciones");
    }

    public int totalCabinas() {
        return contar("SELECT COUNT(*) FROM cabinas");
    }

    public int totalSesiones() {
        return contar("SELECT COUNT(*) FROM sesion_grabaciones");
    }

    public int totalEventos() {
        return contar("SELECT COUNT(*) FROM eventos_agendados");
    }

    public int totalColaboraciones() {
        return contar("SELECT COUNT(*) FROM colaboraciones");
    }

    // ── Conteos calificados ──
    public int artistasActivos() {
        return contar("SELECT COUNT(*) FROM artistas a " +
                "JOIN estados_art_pro e ON a.id_estado = e.id_estado " +
                "WHERE UPPER(e.nombre) = 'ACTIVO'");
    }

    public int productoresActivos() {
        return contar("SELECT COUNT(*) FROM productores p " +
                "JOIN estados_art_pro e ON p.id_estado = e.id_estado " +
                "WHERE UPPER(e.nombre) = 'ACTIVO'");
    }

    public int cancionesPublicadas() {
        return contar("SELECT COUNT(*) FROM canciones c " +
                "JOIN estados_canciones e ON c.id_estado_cancion = e.id_estado_cancion " +
                "WHERE UPPER(e.nombre) = 'PUBLICADA'");
    }

    public int cabinasDisponibles() {
        return contar("SELECT COUNT(*) FROM cabinas c " +
                "JOIN estado_cabina e ON c.id_estado_cabina = e.id_estado_cabina " +
                "WHERE UPPER(e.nombre) = 'DISPONIBLE'");
    }

    public int sesionesEsteMes() {
        return contar("SELECT COUNT(*) FROM sesion_grabaciones " +
                "WHERE TRUNC(fecha, 'MM') = TRUNC(SYSDATE, 'MM')");
    }

    public int artistasNuevosEsteMes() {
        return contar("SELECT COUNT(*) FROM artistas " +
                "WHERE TRUNC(fecha_firma, 'MM') = TRUNC(SYSDATE, 'MM')");
    }

    public int cancionesPublicadasEsteMes() {
        return contar("SELECT COUNT(*) FROM canciones c " +
                "JOIN estados_canciones e ON c.id_estado_cancion = e.id_estado_cancion " +
                "WHERE UPPER(e.nombre) = 'PUBLICADA' " +
                "AND TRUNC(c.fecha_compilacion, 'MM') = TRUNC(SYSDATE, 'MM')");
    }

    // ════════════════════════════════════════════════════════════════
    //  ACTIVIDAD SEMANAL (gráfico de barras)
    // ════════════════════════════════════════════════════════════════

    /**
     * Devuelve un mapa con 3 arreglos de 7 elementos (Lun→Dom):
     * "sesiones", "canciones", "artistas"
     */
    public Map<String, int[]> actividadSemanal() {
        Map<String, int[]> data = new HashMap<>();
        data.put("sesiones",  new int[7]);
        data.put("canciones", new int[7]);
        data.put("artistas",  new int[7]);

        // Sesiones por día (últimos 7 días)
        llenarPorDia(data.get("sesiones"),
                "SELECT TO_CHAR(fecha, 'DY') AS dia, COUNT(*) AS total " +
                "FROM sesion_grabaciones " +
                "WHERE fecha >= TRUNC(SYSDATE) - 6 " +
                "GROUP BY TO_CHAR(fecha, 'DY')");

        // Canciones compiladas por día
        llenarPorDia(data.get("canciones"),
                "SELECT TO_CHAR(fecha_compilacion, 'DY') AS dia, COUNT(*) AS total " +
                "FROM canciones " +
                "WHERE fecha_compilacion >= TRUNC(SYSDATE) - 6 " +
                "GROUP BY TO_CHAR(fecha_compilacion, 'DY')");

        // Artistas firmados por día
        llenarPorDia(data.get("artistas"),
                "SELECT TO_CHAR(fecha_firma, 'DY') AS dia, COUNT(*) AS total " +
                "FROM artistas " +
                "WHERE fecha_firma >= TRUNC(SYSDATE) - 6 " +
                "GROUP BY TO_CHAR(fecha_firma, 'DY')");

        return data;
    }

    private void llenarPorDia(int[] arr, String sql) {
        // Mapeo de abreviaturas en español de Oracle → índice (0=Lun..6=Dom)
        Map<String, Integer> idxDia = new HashMap<>();
        idxDia.put("LUN", 0); idxDia.put("LU.", 0);
        idxDia.put("MAR", 1); idxDia.put("MA.", 1);
        idxDia.put("MIÉ", 2); idxDia.put("MI.", 2); idxDia.put("MIE", 2);
        idxDia.put("JUE", 3); idxDia.put("JU.", 3);
        idxDia.put("VIE", 4); idxDia.put("VI.", 4);
        idxDia.put("SÁB", 5); idxDia.put("SA.", 5); idxDia.put("SAB", 5);
        idxDia.put("DOM", 6); idxDia.put("DO.", 6);

        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String dia = rs.getString("dia");
                if (dia == null) continue;
                String key = dia.trim().toUpperCase();
                Integer idx = idxDia.get(key);
                if (idx != null) arr[idx] = rs.getInt("total");
            }
        } catch (SQLException e) {
            // Si falla, devuelve ceros — no rompe el dashboard
            System.err.println("Error en actividad semanal: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  ACTIVIDAD RECIENTE (últimos eventos)
    // ════════════════════════════════════════════════════════════════

    /**
     * Devuelve los últimos eventos del sistema (canciones nuevas, sesiones, artistas).
     * Cada entrada: [iniciales, texto, tiempo_relativo, tipo]
     */
    public List<String[]> actividadReciente() {
        List<String[]> lista = new ArrayList<>();

        // Últimas 3 canciones
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM (" +
                "  SELECT c.titulo, c.fecha_compilacion AS fecha, " +
                "         p.nombre AS productor " +
                "  FROM canciones c " +
                "  LEFT JOIN productores p ON c.id_productor = p.id_productor " +
                "  WHERE c.fecha_compilacion IS NOT NULL " +
                "  ORDER BY c.fecha_compilacion DESC" +
                ") WHERE ROWNUM <= 3");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String titulo = rs.getString("titulo");
                String prod   = rs.getString("productor");
                Date f        = rs.getDate("fecha");
                lista.add(new String[]{
                    iniciales(prod != null ? prod : titulo),
                    (prod != null ? prod : "Productor") + " compiló " + titulo,
                    tiempoRelativo(f != null ? f.toLocalDate() : null),
                    "cancion"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Últimas 2 sesiones
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM (" +
                "  SELECT s.nombre_sesion, s.fecha, a.nombre_artista AS artista " +
                "  FROM sesion_grabaciones s " +
                "  LEFT JOIN artistas a ON s.id_artista = a.id_artista " +
                "  ORDER BY s.fecha DESC" +
                ") WHERE ROWNUM <= 2");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nombre  = rs.getString("nombre_sesion");
                String artista = rs.getString("artista");
                Date f         = rs.getDate("fecha");
                lista.add(new String[]{
                    iniciales(artista != null ? artista : "SE"),
                    "Sesión: " + (nombre != null ? nombre : "(sin nombre)"),
                    tiempoRelativo(f != null ? f.toLocalDate() : null),
                    "sesion"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Últimos 2 artistas
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM (" +
                "  SELECT nombre_artista, fecha_firma " +
                "  FROM artistas " +
                "  WHERE fecha_firma IS NOT NULL " +
                "  ORDER BY fecha_firma DESC" +
                ") WHERE ROWNUM <= 2");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nom = rs.getString("nombre_artista");
                Date f     = rs.getDate("fecha_firma");
                lista.add(new String[]{
                    iniciales(nom),
                    "Nuevo artista: " + nom,
                    tiempoRelativo(f != null ? f.toLocalDate() : null),
                    "artista"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return lista;
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════

    private int contar(String sql) {
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error contando: " + sql + " → " + e.getMessage());
        }
        return 0;
    }

    private String iniciales(String texto) {
        if (texto == null || texto.isBlank()) return "??";
        String[] partes = texto.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].length() >= 2
                    ? partes[0].substring(0, 2).toUpperCase()
                    : partes[0].toUpperCase();
        }
        return ("" + partes[0].charAt(0) + partes[1].charAt(0)).toUpperCase();
    }

    private String tiempoRelativo(LocalDate fecha) {
        if (fecha == null) return "—";
        long dias = java.time.temporal.ChronoUnit.DAYS.between(fecha, LocalDate.now());
        if (dias == 0) return "hoy";
        if (dias == 1) return "ayer";
        if (dias < 7)  return "hace " + dias + "d";
        if (dias < 30) return "hace " + (dias / 7) + "sem";
        return "hace " + (dias / 30) + "mes";
    }
}