package dao;

import model.Usuario;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;

public class UsuarioDAO {

    public void guardar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (username, password_hash, nombre, correo, rol) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPasswordHash());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getCorreo());
            ps.setString(5, usuario.getRol());
            ps.executeUpdate();
        }
    }

    public Usuario buscarPorCredenciales(String username, String passwordHash) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password_hash = ? AND activo = 1";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    actualizarUltimoLogin(username);
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public ArrayList<Usuario> listarTodos() throws SQLException {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY fecha_registro DESC";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public void actualizarUltimoLogin(String username) throws SQLException {
        String sql = "UPDATE usuarios SET ultimo_login = CURRENT_TIMESTAMP WHERE username = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }

    public boolean existeUsername(String username) throws SQLException {
        return buscarPorUsername(username) != null;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getString("id_usuario"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("nombre"),
            rs.getString("correo"),
            rs.getString("rol"),
            rs.getInt("activo") == 1
        );
    }
}
