package dao;

import model.Cabina;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CabinaDAO {

    private static final String SELECT_BASE =
        "SELECT c.id_cabina, c.nombre_cabina, c.id_estado_cabina, " +
        "       ec.nombre AS nombre_estado " +
        "FROM cabina c " +
        "LEFT JOIN estado_cabina ec ON c.id_estado_cabina = ec.id_estado_cabina ";

    public List<Cabina> listarTodos() throws SQLException {
        return ejecutar(SELECT_BASE + "ORDER BY c.nombre_cabina", null);
    }

    public Cabina buscarPorId(int id) throws SQLException {
        List<Cabina> r = ejecutar(SELECT_BASE + "WHERE c.id_cabina = ?", new Object[]{id});
        return r.isEmpty() ? null : r.get(0);
    }

    public int crear(Cabina c) throws SQLException {
        Integer idEstado = resolverId("estado_cabina", "id_estado_cabina", "nombre",
                c.getNombreEstado(), "Estado cabina");

        String sql = "INSERT INTO cabina (nombre_cabina, id_estado_cabina) VALUES (?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"id_cabina"})) {
            ps.setString(1, c.getNombreCabina());
            setIntOrNull(ps, 2, idEstado);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean actualizar(Cabina c) throws SQLException {
        Integer idEstado = resolverId("estado_cabina", "id_estado_cabina", "nombre",
                c.getNombreEstado(), "Estado cabina");

        String sql = "UPDATE cabina SET nombre_cabina = ?, id_estado_cabina = ? " +
                     "WHERE id_cabina = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombreCabina());
            setIntOrNull(ps, 2, idEstado);
            ps.setInt(3, c.getIdCabina());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cabina WHERE id_cabina = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Cambia solo el estado de una cabina (Disponible / Ocupada / Mantenimiento / Reservada). */
    public boolean cambiarEstado(int idCabina, String nuevoEstado) throws SQLException {
        Integer idEstado = resolverId("estado_cabina", "id_estado_cabina", "nombre",
                nuevoEstado, "Estado cabina");
        String sql = "UPDATE cabina SET id_estado_cabina = ? WHERE id_cabina = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setIntOrNull(ps, 1, idEstado);
            ps.setInt(2, idCabina);
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

    private List<Cabina> ejecutar(String sql, Object[] params) throws SQLException {
        List<Cabina> out = new ArrayList<>();
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

    private Cabina mapear(ResultSet rs) throws SQLException {
        Cabina c = new Cabina();
        c.setIdCabina(rs.getInt("id_cabina"));
        c.setNombreCabina(rs.getString("nombre_cabina"));
        int idEst = rs.getInt("id_estado_cabina");
        c.setIdEstadoCabina(rs.wasNull() ? null : idEst);
        c.setNombreEstado(rs.getString("nombre_estado"));
        return c;
    }
}