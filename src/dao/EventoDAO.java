package dao;

import model.Evento;
import util.ConexionDB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

    private static final String SELECT_BASE =
        "SELECT e.id_evento, e.tipo_evento, e.fecha, e.hora_inicio, e.hora_fin, " +
        "       e.descripcion, e.id_artista, a.nombre_artista, " +
        "       e.id_productor, p.nombre AS nombre_productor, " +
        "       e.id_formato, " +
        "       e.id_tipo_evento, te.nombre AS nombre_tipo_evento " +
        "FROM eventos_agendados e " +
        "LEFT JOIN artistas       a  ON e.id_artista     = a.id_artista " +
        "LEFT JOIN productores    p  ON e.id_productor   = p.id_productor " +
        "LEFT JOIN tipos_eventos  te ON e.id_tipo_evento = te.id_tipo_evento ";

    public List<Evento> listarTodos() throws SQLException {
        return ejecutar(SELECT_BASE + "ORDER BY e.fecha DESC, e.hora_inicio", null);
    }

    public List<Evento> listarProximos() throws SQLException {
        return ejecutar(SELECT_BASE +
                "WHERE e.fecha >= TRUNC(SYSDATE) ORDER BY e.fecha, e.hora_inicio", null);
    }

    public Evento buscarPorId(int id) throws SQLException {
        List<Evento> r = ejecutar(SELECT_BASE + "WHERE e.id_evento = ?", new Object[]{id});
        return r.isEmpty() ? null : r.get(0);
    }

    public int crear(Evento e) throws SQLException {
        Integer idTipo = resolverId("tipos_eventos", "id_tipo_evento", "nombre",
                e.getNombreTipoEvento(), "Tipo evento");

        String sql = "INSERT INTO eventos_agendados (tipo_evento, fecha, hora_inicio, hora_fin, " +
                     "descripcion, id_artista, id_productor, id_formato, id_tipo_evento) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"id_evento"})) {
            ps.setString(1, e.getTipoEvento());
            ps.setDate(2, e.getFecha() != null ? Date.valueOf(e.getFecha()) : null);
            ps.setTimestamp(3, e.getFecha() != null && e.getHoraInicio() != null
                    ? Timestamp.valueOf(LocalDateTime.of(e.getFecha(), e.getHoraInicio())) : null);
            ps.setTimestamp(4, e.getFecha() != null && e.getHoraFin() != null
                    ? Timestamp.valueOf(LocalDateTime.of(e.getFecha(), e.getHoraFin())) : null);
            ps.setString(5, e.getDescripcion());
            setIntOrNull(ps, 6, e.getIdArtista());
            setIntOrNull(ps, 7, e.getIdProductor());
            setIntOrNull(ps, 8, e.getIdFormato());
            setIntOrNull(ps, 9, idTipo);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean actualizar(Evento e) throws SQLException {
        Integer idTipo = resolverId("tipos_eventos", "id_tipo_evento", "nombre",
                e.getNombreTipoEvento(), "Tipo evento");

        String sql = "UPDATE eventos_agendados SET tipo_evento = ?, fecha = ?, " +
                     "hora_inicio = ?, hora_fin = ?, descripcion = ?, " +
                     "id_artista = ?, id_productor = ?, id_formato = ?, id_tipo_evento = ? " +
                     "WHERE id_evento = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getTipoEvento());
            ps.setDate(2, e.getFecha() != null ? Date.valueOf(e.getFecha()) : null);
            ps.setTimestamp(3, e.getFecha() != null && e.getHoraInicio() != null
                    ? Timestamp.valueOf(LocalDateTime.of(e.getFecha(), e.getHoraInicio())) : null);
            ps.setTimestamp(4, e.getFecha() != null && e.getHoraFin() != null
                    ? Timestamp.valueOf(LocalDateTime.of(e.getFecha(), e.getHoraFin())) : null);
            ps.setString(5, e.getDescripcion());
            setIntOrNull(ps, 6, e.getIdArtista());
            setIntOrNull(ps, 7, e.getIdProductor());
            setIntOrNull(ps, 8, e.getIdFormato());
            setIntOrNull(ps, 9, idTipo);
            ps.setInt(10, e.getIdEvento());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM eventos_agendados WHERE id_evento = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Helpers ──
    private Integer resolverId(String tabla, String colId, String colNombre,
                                String valor, String etiqueta) throws SQLException {
        if (valor == null || valor.isBlank()) return null;
        String sql = "SELECT " + colId + " FROM " + tabla +
                     " WHERE UPPER(TRIM(" + colNombre + ")) = UPPER(TRIM(?))";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException(etiqueta + " '" + valor + "' no existe en la BD.");
    }

    private void setIntOrNull(PreparedStatement ps, int idx, Integer val) throws SQLException {
        if (val != null) ps.setInt(idx, val);
        else             ps.setNull(idx, Types.NUMERIC);
    }

    private List<Evento> ejecutar(String sql, Object[] params) throws SQLException {
        List<Evento> out = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (params != null)
                for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapear(rs));
            }
        }
        return out;
    }

    private Evento mapear(ResultSet rs) throws SQLException {
        Evento e = new Evento();
        e.setIdEvento(rs.getInt("id_evento"));
        e.setTipoEvento(rs.getString("tipo_evento"));
        Date f = rs.getDate("fecha");
        e.setFecha(f != null ? f.toLocalDate() : null);
        Timestamp hi = rs.getTimestamp("hora_inicio");
        e.setHoraInicio(hi != null ? hi.toLocalDateTime().toLocalTime() : null);
        Timestamp hf = rs.getTimestamp("hora_fin");
        e.setHoraFin(hf != null ? hf.toLocalDateTime().toLocalTime() : null);
        e.setDescripcion(rs.getString("descripcion"));
        int idA = rs.getInt("id_artista");
        e.setIdArtista(rs.wasNull() ? null : idA);
        e.setNombreArtista(rs.getString("nombre_artista"));
        int idP = rs.getInt("id_productor");
        e.setIdProductor(rs.wasNull() ? null : idP);
        e.setNombreProductor(rs.getString("nombre_productor"));
        int idF = rs.getInt("id_formato");
        e.setIdFormato(rs.wasNull() ? null : idF);
        int idT = rs.getInt("id_tipo_evento");
        e.setIdTipoEvento(rs.wasNull() ? null : idT);
        e.setNombreTipoEvento(rs.getString("nombre_tipo_evento"));
        return e;
    }
}