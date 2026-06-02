package dao;

import model.Usuario;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
private static final String SELECT_BASE =
    "SELECT u.id_usuario, u.username, u.password_hash, " +
    "       u.id_rol, r.nombre AS nombre_rol, u.estado_usuario, " +
    "       u.fecha_registro, u.ultimo_acceso " +
    "FROM usuario u " +
    "LEFT JOIN rol_usuario r ON u.id_rol = r.id_rol ";


public int guardar(Usuario u) throws SQLException {
    String sql = "INSERT INTO usuario (username, password_hash, id_rol, estado_usuario, fecha_registro) " +
                 "VALUES (?, ?, ?, 'Activo', SYSDATE)";
    try (Connection conn = ConexionDB.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_usuario"})) {
        ps.setString(1, u.getUsername());
        ps.setString(2, u.getPasswordHash());
        ps.setInt   (3, u.getIdRol());
        ps.executeUpdate();
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) return rs.getInt(1);
        }
    }
    return -1;
}

    public Usuario buscarPorCredenciales(String username, String passwordHash) throws SQLException {
        String sql = SELECT_BASE +
                "WHERE u.username = ? AND u.password_hash = ? AND u.estado_usuario = 'Activo'";
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
        // La tabla USUARIO no tiene columna correo, busca por username
        return existeUsername(correo);
    }

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

    public boolean actualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET username=?, id_rol=?, estado_usuario=? " +
                     "WHERE id_usuario=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setInt   (2, u.getIdRol());
            ps.setString(3, u.isActivo() ? "Activo" : "Inactivo");
            ps.setInt   (4, u.getIdUsuario());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarPassword(int idUsuario, String nuevoHash) throws SQLException {
        String sql = "UPDATE usuario SET password_hash=? WHERE id_usuario=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoHash);
            ps.setInt   (2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    public void actualizarUltimoLogin(int idUsuario) throws SQLException {
        String sql = "UPDATE usuario SET ultimo_acceso = SYSDATE WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    public boolean desactivar(int idUsuario) throws SQLException {
        String sql = "UPDATE usuario SET estado_usuario = 'Inactivo' WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

private Usuario mapear(ResultSet rs) throws SQLException {
    Date fr = rs.getDate("fecha_registro");
    Date ul = rs.getDate("ultimo_acceso");

    return new Usuario(
        rs.getInt("id_usuario"),
        rs.getString("username"),
        rs.getString("password_hash"),
        null,
        rs.getString("username"), // <-- usa username como nombreCompleto
        rs.getInt("id_rol"),
        rs.getString("nombre_rol"),
        "Activo".equals(rs.getString("estado_usuario")),
        fr != null ? fr.toLocalDate() : null,
        ul != null ? ul.toLocalDate().atStartOfDay() : null
    );
}
}