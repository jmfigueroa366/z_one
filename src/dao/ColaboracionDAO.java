package dao;

import model.Colaboracion;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColaboracionDAO {

    private static final String SELECT_BASE =
        "SELECT c.id_colaboracion, c.fecha_colaboracion, c.colaboracion_artista, " +
        "       c.id_cancion, can.titulo AS nombre_cancion " +
        "FROM colaboraciones c " +
        "LEFT JOIN canciones can ON c.id_cancion = can.id_cancion ";

    public List<Colaboracion> listarTodos() throws SQLException {
        return ejecutar(SELECT_BASE + "ORDER BY c.fecha_colaboracion DESC", null);
    }

    public List<Colaboracion> listarPorCancion(int idCancion) throws SQLException {
        return ejecutar(SELECT_BASE + "WHERE c.id_cancion = ? ORDER BY c.fecha_colaboracion DESC",
                new Object[]{idCancion});
    }

    public Colaboracion buscarPorId(int id) throws SQLException {
        List<Colaboracion> r = ejecutar(SELECT_BASE + "WHERE c.id_colaboracion = ?", new Object[]{id});
        return r.isEmpty() ? null : r.get(0);
    }

    public int crear(Colaboracion c) throws SQLException {
        String sql = "INSERT INTO colaboraciones (fecha_colaboracion, colaboracion_artista, id_cancion) " +
                     "VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"id_colaboracion"})) {
            ps.setDate(1, c.getFechaColaboracion() != null
                    ? Date.valueOf(c.getFechaColaboracion()) : null);
            ps.setString(2, c.getColaboracionArtista());
            setIntOrNull(ps, 3, c.getIdCancion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean actualizar(Colaboracion c) throws SQLException {
        String sql = "UPDATE colaboraciones SET fecha_colaboracion = ?, " +
                     "colaboracion_artista = ?, id_cancion = ? WHERE id_colaboracion = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, c.getFechaColaboracion() != null
                    ? Date.valueOf(c.getFechaColaboracion()) : null);
            ps.setString(2, c.getColaboracionArtista());
            setIntOrNull(ps, 3, c.getIdCancion());
            ps.setInt(4, c.getIdColaboracion());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM colaboraciones WHERE id_colaboracion = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Helpers ──
    private void setIntOrNull(PreparedStatement ps, int idx, Integer val) throws SQLException {
        if (val != null) ps.setInt(idx, val);
        else             ps.setNull(idx, Types.NUMERIC);
    }

    private List<Colaboracion> ejecutar(String sql, Object[] params) throws SQLException {
        List<Colaboracion> out = new ArrayList<>();
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

    private Colaboracion mapear(ResultSet rs) throws SQLException {
        Colaboracion c = new Colaboracion();
        c.setIdColaboracion(rs.getInt("id_colaboracion"));
        Date f = rs.getDate("fecha_colaboracion");
        c.setFechaColaboracion(f != null ? f.toLocalDate() : null);
        c.setColaboracionArtista(rs.getString("colaboracion_artista"));
        int idCan = rs.getInt("id_cancion");
        c.setIdCancion(rs.wasNull() ? null : idCan);
        c.setNombreCancion(rs.getString("nombre_cancion"));
        return c;
    }
}