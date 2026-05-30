package services;

import util.ConexionDB;

import java.awt.Color;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Trae sesiones, eventos y colaboraciones agrupados por dia para mostrar en el calendario.
 */
public class CalendarioService {

    /** Un item del calendario (sesion, evento o colaboracion). */
    public static class ItemCalendario {
        public int       id;
        public String    tipo;        // "SESION" | "EVENTO" | "COLABORACION"
        public String    titulo;
        public String    subtitulo;
        public LocalDate fecha;
        public LocalTime horaInicio;
        public LocalTime horaFin;
        public Color     color;

        public ItemCalendario(int id, String tipo, String titulo, String subtitulo,
                              LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Color color) {
            this.id = id;
            this.tipo = tipo;
            this.titulo = titulo;
            this.subtitulo = subtitulo;
            this.fecha = fecha;
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
            this.color = color;
        }
    }

    private static final Color C_SESION       = new Color(0x42A5F5);   // azul
    private static final Color C_EVENTO       = new Color(0xFFA726);   // ambar
    private static final Color C_COLABORACION = new Color(0xEC4899);   // rosa

    /** Trae todos los items de una semana (lun a dom). */
    public List<ItemCalendario> itemsDeSemana(LocalDate lunes) {
        List<ItemCalendario> out = new ArrayList<>();
        LocalDate domingo = lunes.plusDays(6);

        // ── SESIONES ──
        String sqlSes = "SELECT s.id_sesion, s.nombre_sesion, s.fecha, " +
                "       s.hora_inicio, s.hora_fin, " +
                "       a.nombre_artista, p.nombre AS productor " +
                "FROM sesion_grabaciones s " +
                "LEFT JOIN artistas    a ON s.id_artista   = a.id_artista " +
                "LEFT JOIN productores p ON s.id_productor = p.id_productor " +
                "WHERE s.fecha BETWEEN ? AND ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sqlSes)) {
            ps.setDate(1, Date.valueOf(lunes));
            ps.setDate(2, Date.valueOf(domingo));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalTime hi = parseHora(rs.getString("hora_inicio"));
                    LocalTime hf = parseHora(rs.getString("hora_fin"));
                    String sub  = (rs.getString("nombre_artista") != null
                            ? rs.getString("nombre_artista") : "")
                            + (rs.getString("productor") != null
                            ? " · " + rs.getString("productor") : "");
                    out.add(new ItemCalendario(
                            rs.getInt("id_sesion"),
                            "SESION",
                            rs.getString("nombre_sesion"),
                            sub,
                            rs.getDate("fecha").toLocalDate(),
                            hi, hf, C_SESION));
                }
            }
        } catch (SQLException e) {
            System.err.println("Calendario sesiones: " + e.getMessage());
        }

        // ── EVENTOS ──
        String sqlEv = "SELECT e.id_evento, e.descripcion, e.fecha, " +
                "       e.hora_inicio, e.hora_fin, " +
                "       te.nombre AS tipo, a.nombre_artista " +
                "FROM eventos_agendados e " +
                "LEFT JOIN tipos_eventos te ON e.id_tipo_evento = te.id_tipo_evento " +
                "LEFT JOIN artistas      a  ON e.id_artista     = a.id_artista " +
                "WHERE e.fecha BETWEEN ? AND ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sqlEv)) {
            ps.setDate(1, Date.valueOf(lunes));
            ps.setDate(2, Date.valueOf(domingo));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp tsi = rs.getTimestamp("hora_inicio");
                    Timestamp tsf = rs.getTimestamp("hora_fin");
                    LocalTime hi = tsi != null ? tsi.toLocalDateTime().toLocalTime() : LocalTime.of(12, 0);
                    LocalTime hf = tsf != null ? tsf.toLocalDateTime().toLocalTime() : hi.plusHours(1);
                    String sub = (rs.getString("tipo") != null ? rs.getString("tipo") : "")
                            + (rs.getString("nombre_artista") != null
                            ? " · " + rs.getString("nombre_artista") : "");
                    out.add(new ItemCalendario(
                            rs.getInt("id_evento"),
                            "EVENTO",
                            rs.getString("descripcion"),
                            sub,
                            rs.getDate("fecha").toLocalDate(),
                            hi, hf, C_EVENTO));
                }
            }
        } catch (SQLException e) {
            System.err.println("Calendario eventos: " + e.getMessage());
        }

        // ── COLABORACIONES ──
        String sqlCol = "SELECT co.id_colaboracion, co.colaboracion_artista, " +
                "       co.fecha_colaboracion, c.titulo " +
                "FROM colaboraciones co " +
                "LEFT JOIN canciones c ON co.id_cancion = c.id_cancion " +
                "WHERE co.fecha_colaboracion BETWEEN ? AND ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sqlCol)) {
            ps.setDate(1, Date.valueOf(lunes));
            ps.setDate(2, Date.valueOf(domingo));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new ItemCalendario(
                            rs.getInt("id_colaboracion"),
                            "COLABORACION",
                            rs.getString("colaboracion_artista"),
                            rs.getString("titulo") != null
                                    ? "en " + rs.getString("titulo") : "",
                            rs.getDate("fecha_colaboracion").toLocalDate(),
                            LocalTime.of(15, 0),   // hora por defecto
                            LocalTime.of(16, 0),
                            C_COLABORACION));
                }
            }
        } catch (SQLException e) {
            System.err.println("Calendario colaboraciones: " + e.getMessage());
        }

        return out;
    }

    /** Parsea "HH:mm" o "HH:mm:ss" tolerante a nulos. Devuelve 09:00 si falla. */
    private LocalTime parseHora(String s) {
        if (s == null || s.isBlank()) return LocalTime.of(9, 0);
        try {
            s = s.trim();
            if (s.length() >= 5) return LocalTime.parse(s.substring(0, 5));
        } catch (Exception ignore) {}
        return LocalTime.of(9, 0);
    }
}