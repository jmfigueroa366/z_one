package services;

import util.ConexionDB;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class EstadisticasService {

    public int totalArtistas()       { return contar("SELECT COUNT(*) FROM artista"); }
    public int totalProductores()    { return contar("SELECT COUNT(*) FROM productor"); }
    public int totalCanciones()      { return contar("SELECT COUNT(*) FROM cancion"); }
    public int totalCabinas()        { return contar("SELECT COUNT(*) FROM cabina"); }
    public int totalSesiones()       { return contar("SELECT COUNT(*) FROM sesion_grabacion"); }
    public int totalEventos()        { return contar("SELECT COUNT(*) FROM evento_agenda"); }
    public int totalColaboraciones() { return contar("SELECT COUNT(*) FROM colaboracion"); }

    public int artistasActivos() {
        return contar("SELECT COUNT(*) FROM artista a " +
                "JOIN estado_art_pro e ON a.id_estado_art_pro = e.id_estado_art_pro " +
                "WHERE UPPER(e.nombre) = 'ACTIVO'");
    }
    public int productoresActivos() {
        return contar("SELECT COUNT(*) FROM productor p " +
                "JOIN estado_art_pro e ON p.id_estado_art_pro = e.id_estado_art_pro " +
                "WHERE UPPER(e.nombre) = 'ACTIVO'");
    }
    public int cancionesPublicadas() {
        return contar("SELECT COUNT(*) FROM cancion c " +
                "JOIN estado_cancion e ON c.id_estado_cancion = e.id_estado_cancion " +
                "WHERE UPPER(e.nombre) = 'PUBLICADA'");
    }
    public int cabinasDisponibles() {
        return contar("SELECT COUNT(*) FROM cabina c " +
                "JOIN estado_cabina e ON c.id_estado_cabina = e.id_estado_cabina " +
                "WHERE UPPER(e.nombre) = 'DISPONIBLE'");
    }
    public int sesionesEsteMes() {
        return contar("SELECT COUNT(*) FROM sesion_grabacion " +
                "WHERE TRUNC(fecha_grabacion, 'MM') = TRUNC(SYSDATE, 'MM')");
    }
    public int artistasNuevosEsteMes() {
        return contar("SELECT COUNT(*) FROM artista " +
                "WHERE TRUNC(fecha_firma, 'MM') = TRUNC(SYSDATE, 'MM')");
    }
    public int cancionesPublicadasEsteMes() {
        return contar("SELECT COUNT(*) FROM cancion c " +
                "JOIN estado_cancion e ON c.id_estado_cancion = e.id_estado_cancion " +
                "WHERE UPPER(e.nombre) = 'PUBLICADA' " +
                "AND TRUNC(c.fecha_publicacion, 'MM') = TRUNC(SYSDATE, 'MM')");
    }

    public Map<String, int[]> actividadSemanal() {
        Map<String, int[]> data = new HashMap<>();
        data.put("sesiones",  new int[7]);
        data.put("canciones", new int[7]);
        data.put("artistas",  new int[7]);

        llenarPorDia(data.get("sesiones"),
                "SELECT TO_CHAR(fecha_grabacion, 'DY') AS dia, COUNT(*) AS total " +
                "FROM sesion_grabacion " +
                "WHERE fecha_grabacion >= TRUNC(SYSDATE) - 6 " +
                "GROUP BY TO_CHAR(fecha_grabacion, 'DY')");
        llenarPorDia(data.get("canciones"),
                "SELECT TO_CHAR(fecha_publicacion, 'DY') AS dia, COUNT(*) AS total " +
                "FROM cancion WHERE fecha_publicacion >= TRUNC(SYSDATE) - 6 " +
                "GROUP BY TO_CHAR(fecha_publicacion, 'DY')");
        llenarPorDia(data.get("artistas"),
                "SELECT TO_CHAR(fecha_firma, 'DY') AS dia, COUNT(*) AS total " +
                "FROM artista WHERE fecha_firma >= TRUNC(SYSDATE) - 6 " +
                "GROUP BY TO_CHAR(fecha_firma, 'DY')");
        return data;
    }

    private void llenarPorDia(int[] arr, String sql) {
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
                Integer idx = idxDia.get(dia.trim().toUpperCase());
                if (idx != null) arr[idx] = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error actividad semanal: " + e.getMessage());
        }
    }

    public List<String[]> actividadReciente() {
        List<String[]> lista = new ArrayList<>();

        // Últimas 3 canciones
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM (" +
                "  SELECT c.titulo, c.fecha_publicacion AS fecha, p.nombre AS productor " +
                "  FROM cancion c LEFT JOIN productor p ON c.id_productor = p.id_productor " +
                "  WHERE c.fecha_publicacion IS NOT NULL ORDER BY c.fecha_publicacion DESC" +
                ") WHERE ROWNUM <= 3");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String titulo = rs.getString("titulo");
                String prod   = rs.getString("productor");
                java.sql.Date f = rs.getDate("fecha");
                lista.add(new String[]{
                    iniciales(prod != null ? prod : titulo),
                    (prod != null ? prod : "Productor") + " publicó " + titulo,
                    tiempoRelativo(f != null ? f.toLocalDate() : null),
                    "cancion"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Últimas 2 sesiones
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM (" +
                "  SELECT s.nombre_sesion, s.fecha_grabacion, a.nombre_artista " +
                "  FROM sesion_grabacion s LEFT JOIN artista a ON s.id_artista = a.id_artista " +
                "  ORDER BY s.fecha_grabacion DESC" +
                ") WHERE ROWNUM <= 2");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nombre  = rs.getString("nombre_sesion");
                String artista = rs.getString("nombre_artista");
                java.sql.Date f = rs.getDate("fecha_grabacion");
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
                "  SELECT nombre_artista, fecha_firma FROM artista " +
                "  WHERE fecha_firma IS NOT NULL ORDER BY fecha_firma DESC" +
                ") WHERE ROWNUM <= 2");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nom = rs.getString("nombre_artista");
                java.sql.Date f = rs.getDate("fecha_firma");
                lista.add(new String[]{
                    iniciales(nom), "Nuevo artista: " + nom,
                    tiempoRelativo(f != null ? f.toLocalDate() : null), "artista"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return lista;
    }

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
        String[] p = texto.trim().split("\\s+");
        if (p.length == 1) return p[0].length() >= 2 ? p[0].substring(0, 2).toUpperCase() : p[0].toUpperCase();
        return ("" + p[0].charAt(0) + p[1].charAt(0)).toUpperCase();
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