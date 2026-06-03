package dao;

import model.Evento;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

    private static final String SCHEMA = "PRODUCTORA_BD.";

    private static final String SELECT_BASE =
        "SELECT e.id_evento, e.id_tipo_evento, te.nombre AS nombre_tipo_evento, " +
        "       e.id_artista, a.nombre_artista, " +
        "       e.id_productor, p.nombre AS nombre_productor, " +
        "       e.id_formato, e.fecha, e.hora_inicio, e.hora_final, e.descripcion " +
        "FROM " + SCHEMA + "evento_agenda e " +
        "JOIN " + SCHEMA + "tipo_evento    te ON e.id_tipo_evento = te.id_tipo_evento " +
        "LEFT JOIN " + SCHEMA + "artista    a  ON e.id_artista   = a.id_artista " +
        "LEFT JOIN " + SCHEMA + "productor  p  ON e.id_productor = p.id_productor ";

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
        Integer idTipo = resolverIdTipo(e.getNombreTipoEvento());
        e.setIdTipoEvento(idTipo);

        String sql = "INSERT INTO " + SCHEMA + "evento_agenda " +
                     "(id_tipo_evento, id_artista, id_productor, id_formato, " +
                     " fecha, hora_inicio, hora_final, descripcion) " +
                     "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID_EVENTO"})) {
            ps.setInt(1, e.getIdTipoEvento());
            setIntOrNull(ps, 2, e.getIdArtista());
            setIntOrNull(ps, 3, e.getIdProductor());
            setIntOrNull(ps, 4, e.getIdFormato());
            ps.setDate(5, e.getFecha() != null ? Date.valueOf(e.getFecha()) : null);
            ps.setTimestamp(6, e.getHoraInicio() != null ? Timestamp.valueOf(e.getHoraInicio()) : null);
            ps.setTimestamp(7, e.getHoraFin()    != null ? Timestamp.valueOf(e.getHoraFin())    : null);
            ps.setString(8, e.getDescripcion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean actualizar(Evento e) throws SQLException {
        Integer idTipo = resolverIdTipo(e.getNombreTipoEvento());
        e.setIdTipoEvento(idTipo);

        String sql = "UPDATE " + SCHEMA + "evento_agenda SET " +
                     "id_tipo_evento=?, id_artista=?, id_productor=?, id_formato=?, " +
                     "fecha=?, hora_inicio=?, hora_final=?, descripcion=? " +
                     "WHERE id_evento=?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getIdTipoEvento());
            setIntOrNull(ps, 2, e.getIdArtista());
            setIntOrNull(ps, 3, e.getIdProductor());
            setIntOrNull(ps, 4, e.getIdFormato());
            ps.setDate(5, e.getFecha() != null ? Date.valueOf(e.getFecha()) : null);
            ps.setTimestamp(6, e.getHoraInicio() != null ? Timestamp.valueOf(e.getHoraInicio()) : null);
            ps.setTimestamp(7, e.getHoraFin()    != null ? Timestamp.valueOf(e.getHoraFin())    : null);
            ps.setString(8, e.getDescripcion());
            ps.setInt(9, e.getIdEvento());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM " + SCHEMA + "evento_agenda WHERE id_evento=?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
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
        e.setIdTipoEvento(rs.getInt("id_tipo_evento"));
        e.setNombreTipoEvento(rs.getString("nombre_tipo_evento"));
        int idA = rs.getInt("id_artista");
        e.setIdArtista(rs.wasNull() ? null : idA);
        e.setNombreArtista(rs.getString("nombre_artista"));
        int idP = rs.getInt("id_productor");
        e.setIdProductor(rs.wasNull() ? null : idP);
        e.setNombreProductor(rs.getString("nombre_productor"));
        int idF = rs.getInt("id_formato");
        e.setIdFormato(rs.wasNull() ? null : idF);
        Date f = rs.getDate("fecha");
        e.setFecha(f != null ? f.toLocalDate() : null);
        Timestamp hi = rs.getTimestamp("hora_inicio");
        e.setHoraInicio(hi != null ? hi.toLocalDateTime() : null);
        Timestamp hf = rs.getTimestamp("hora_final");
        e.setHoraFin(hf != null ? hf.toLocalDateTime() : null);
        e.setDescripcion(rs.getString("descripcion"));
        return e;
    }

    private Integer resolverIdTipo(String nombre) throws SQLException {
        if (nombre == null || nombre.isBlank())
            throw new SQLException("El tipo de evento es obligatorio");
        String sql = "SELECT id_tipo_evento FROM " + SCHEMA +
                     "tipo_evento WHERE UPPER(TRIM(nombre)) = UPPER(TRIM(?))";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Tipo de evento '" + nombre + "' no existe en la BD.");
    }
}