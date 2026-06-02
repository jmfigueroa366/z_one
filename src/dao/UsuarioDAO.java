package dao;

import model.Usuario;
import util.ConexionDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Usuario alineado con la tabla USUARIOS de Oracle.
 * Hace JOIN con la tabla ROLES para traer el nombre del rol.
 */
public class UsuarioDAO {

    private static final String SELECT_BASE =
        "SELECT u.id_usuario, u.username, u.contrasena, u.correo, " +
        "       u.nombre_completo, u.rol, r.nombre_rol, u.activo, " +
        "       u.fecha_registro, u.ultimo_login " +
        "FROM usuarios u " +
        "LEFT JOIN roles r ON u.rol = r.id_rol ";

    // ── INSERT ───────────────────────────────────────────────────────

    public int guardar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (username, contrasena, correo, nombre_completo, rol, activo, fecha_registro) " +
                     "VALUES (?, ?, ?, ?, ?, ?, SYSDATE)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_usuario"})) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getNombreCompleto());
            ps.setInt   (5, u.getIdRol());
            ps.setInt   (6, u.isActivo() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // ── LOGIN ────────────────────────────────────────────────────────

    public Usuario buscarPorCredenciales(String username, String passwordHash) throws SQLException {
        String sql = SELECT_BASE +
                "WHERE u.username = ? AND u.contrasena = ? AND u.activo = 1";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = mapear(rs);
                    actualizarUltimoLogin(u.getIdUsuario());
                    return u;
                }
            }
        }
        return null;
    }

    // ── BUSCAR / EXISTE ──────────────────────────────────────────────

    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = SELECT_BASE + "WHERE u.username = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = SELECT_BASE + "WHERE u.id_usuario = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public boolean existeUsername(String username) throws SQLException {
        return buscarPorUsername(username) != null;
    }

    public boolean existeCorreo(String correo) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE correo = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    // ── LISTAR ───────────────────────────────────────────────────────

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY u.fecha_registro DESC";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── UPDATE ───────────────────────────────────────────────────────

    public boolean actualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuarios SET username=?, correo=?, nombre_completo=?, rol=?, activo=? " +
                     "WHERE id_usuario=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getNombreCompleto());
            ps.setInt   (4, u.getIdRol());
            ps.setInt   (5, u.isActivo() ? 1 : 0);
            ps.setInt   (6, u.getIdUsuario());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarPassword(int idUsuario, String nuevoHash) throws SQLException {
        String sql = "UPDATE usuarios SET contrasena=? WHERE id_usuario=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoHash);
            ps.setInt   (2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    public void actualizarUltimoLogin(int idUsuario) throws SQLException {
        String sql = "UPDATE usuarios SET ultimo_login = SYSTIMESTAMP WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    // ── DELETE (baja logica) ─────────────────────────────────────────

    public boolean desactivar(int idUsuario) throws SQLException {
        String sql = "UPDATE usuarios SET activo = 0 WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    // ── MAPPER ───────────────────────────────────────────────────────

    private Usuario mapear(ResultSet rs) throws SQLException {
        Date    fr  = rs.getDate("fecha_registro");
        Timestamp ul = rs.getTimestamp("ultimo_login");
        return new Usuario(
            rs.getInt("id_usuario"),
            rs.getString("username"),
            rs.getString("contrasena"),
            rs.getString("correo"),
            rs.getString("nombre_completo"),
            rs.getInt("rol"),
            rs.getString("nombre_rol"),
            rs.getInt("activo") == 1,
            fr  != null ? fr.toLocalDate() : null,
            ul  != null ? ul.toLocalDateTime() : null
        );
    }
}