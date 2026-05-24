package dao;

import model.Usuario;
import util.ConexionDB;
import java.sql.*;

public class UsuarioDAO {

    // ── Login ─────────────────────────────────────────────────────────
    public Usuario buscarPorCredenciales(String username, String hashContrasena)
            throws SQLException {
        String sql =
            "SELECT u.id_usuario, u.username, u.correo, u.contrasena, " +
            "       u.activo, r.nombre_rol " +
            "FROM   usuarios u " +
            "JOIN   roles    r ON r.id_rol = u.id_rol " +
            "WHERE  u.username  = ? " +
            "  AND  u.contrasena = ? " +
            "  AND  u.activo    = 1";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hashContrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // ── Registrar usuario nuevo ───────────────────────────────────────
    public void guardar(Usuario u) throws SQLException {
        Connection conn = ConexionDB.getConexion();
        try {
            conn.setAutoCommit(false);

            // 1. Obtener id_rol
            int idRol = obtenerIdRol(conn, u.getRol());

            // 2. Insertar en usuarios
            String sqlUser =
                "INSERT INTO usuarios (username, correo, contrasena, id_rol) " +
                "VALUES (?, ?, ?, ?)";

            int idUsuario;
            try (PreparedStatement ps = conn.prepareStatement(
                    sqlUser, new String[]{"id_usuario"})) {
                ps.setString(1, u.getUsername());
                ps.setString(2, u.getCorreo());
                ps.setString(3, u.getContrasena()); // ya hasheada
                ps.setInt(4, idRol);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next())
                        throw new SQLException("No se obtuvo el ID generado.");
                    idUsuario = keys.getInt(1);
                }
            }

            // 3. Insertar perfil según rol
            insertarPerfil(conn, idUsuario, u);

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ── Helpers privados ──────────────────────────────────────────────
    private int obtenerIdRol(Connection conn, String nombreRol)
            throws SQLException {
        String sql = "SELECT id_rol FROM roles WHERE nombre_rol = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreRol.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Rol no encontrado: " + nombreRol);
    }

    private void insertarPerfil(Connection conn, int idUsuario, Usuario u)
            throws SQLException {
        switch (u.getRol().toUpperCase()) {
            case "ARTISTA": {
                String sql =
                    "INSERT INTO perfil_artista " +
                    "    (id_usuario, nombre_artista, estado_artista, tipo_artista) " +
                    "VALUES (?, ?, 'Activo', 'Solista')";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idUsuario);
                    ps.setString(2, u.getNombre());
                    ps.executeUpdate();
                }
                break;
            }
            case "PRODUCTOR": {
                String sql =
                    "INSERT INTO productor " +
                    "    (id_usuario, nombre, especialidad, estado_productor) " +
                    "VALUES (?, ?, 'General', 'Disponible')";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idUsuario);
                    ps.setString(2, u.getNombre());
                    ps.executeUpdate();
                }
                break;
            }
            // ADMIN y USUARIO no necesitan perfil extra
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id_usuario"),
            rs.getString("username"),
            rs.getString("contrasena"),
            rs.getString("correo"),
            rs.getString("nombre_rol"),
            rs.getInt("activo") == 1
        );
    }

    public boolean hayConexion() {
        return ConexionDB.probarConexion();
    }
}