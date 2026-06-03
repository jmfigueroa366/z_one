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
/** Trae las sesiones de una semana (lun a dom). Solo sesiones. */
    public List<ItemCalendario> itemsDeSemana(LocalDate lunes) {
        List<ItemCalendario> out = new ArrayList<>();
        LocalDate domingo = lunes.plusDays(6);

        // ── SESIONES (única fuente) ──
        String sqlSes =
                "SELECT s.id_grabacion, s.nombre_sesion, s.fecha_grabacion, " +
                "       s.hora_inicio, s.hora_fin, " +
                "       a.nombre_artista, p.nombre AS productor " +
                "FROM sesion_grabacion s " +
                "LEFT JOIN artista   a ON s.id_artista   = a.id_artista " +
                "LEFT JOIN productor p ON s.id_productor = p.id_productor " +
                "WHERE s.fecha_grabacion BETWEEN ? AND ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sqlSes)) {
            ps.setDate(1, Date.valueOf(lunes));
            ps.setDate(2, Date.valueOf(domingo));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // hora_inicio / hora_fin son TIMESTAMP en la BD
                    Timestamp tsi = rs.getTimestamp("hora_inicio");
                    Timestamp tsf = rs.getTimestamp("hora_fin");
                    LocalTime hi = tsi != null ? tsi.toLocalDateTime().toLocalTime() : LocalTime.of(9, 0);
                    LocalTime hf = tsf != null ? tsf.toLocalDateTime().toLocalTime() : hi.plusHours(1);

                    String art  = rs.getString("nombre_artista");
                    String prod = rs.getString("productor");
                    String sub  = (art != null ? art : "")
                                + (prod != null ? " · " + prod : "");

                    out.add(new ItemCalendario(
                            rs.getInt("id_grabacion"),
                            "SESION",
                            rs.getString("nombre_sesion"),
                            sub,
                            rs.getDate("fecha_grabacion").toLocalDate(),
                            hi, hf, C_SESION));
                }
            }
        } catch (SQLException e) {
            System.err.println("Calendario sesiones: " + e.getMessage());
        }

        return out;
    }
    /** Devuelve la fecha de la sesión más próxima a hoy (futura o pasada). Null si no hay. */
    public LocalDate fechaSesionMasCercana() {
        String sql = "SELECT fecha_grabacion FROM sesion_grabacion " +
                     "ORDER BY ABS(fecha_grabacion - SYSDATE) FETCH FIRST 1 ROWS ONLY";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Date f = rs.getDate("fecha_grabacion");
                if (f != null) return f.toLocalDate();
            }
        } catch (SQLException e) {
            System.err.println("Fecha sesión cercana: " + e.getMessage());
        }
        return null;
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