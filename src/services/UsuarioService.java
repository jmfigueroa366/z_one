package services;

import dao.UsuarioDAO;
import model.Usuario;
import java.security.MessageDigest;
import java.sql.SQLException;

public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();

    // ── SHA-256 ───────────────────────────────────────────────────────
    public static String sha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al hashear", e);
        }
    }

    // ── Login ─────────────────────────────────────────────────────────
    public Usuario login(String username, String contrasena) throws Exception {
        if (username == null || username.isBlank())
            throw new Exception("El usuario no puede estar vacío.");
        if (contrasena == null || contrasena.isBlank())
            throw new Exception("La contraseña no puede estar vacía.");

        Usuario u = dao.buscarPorCredenciales(
                username.trim(), sha256(contrasena));

        if (u == null)
            throw new Exception("Usuario o contraseña incorrectos.");
        return u;
    }

    // ── Registro ──────────────────────────────────────────────────────
    public void registrar(String username, String contrasena,
                          String nombre, String correo,
                          String rol) throws Exception {

        if (username == null || username.trim().length() < 3)
            throw new Exception("El usuario debe tener al menos 3 caracteres.");
        if (contrasena == null || contrasena.length() < 4)
            throw new Exception("La contraseña debe tener al menos 4 caracteres.");
        if (correo == null || !correo.contains("@"))
            throw new Exception("El correo no es válido.");
        if (nombre == null || nombre.isBlank())
            throw new Exception("El nombre es requerido.");

        Usuario u = new Usuario(
            username.trim(),
            sha256(contrasena),
            correo.trim().toLowerCase(),
            rol.toUpperCase()
        );

        try {
            dao.guardar(u);
        } catch (SQLException e) {
            String msg = e.getMessage().toUpperCase();
            if (msg.contains("USERNAME") || msg.contains("UK_USER"))
                throw new Exception("Ese usuario ya existe.");
            if (msg.contains("CORREO") || msg.contains("UK_MAIL"))
                throw new Exception("Ese correo ya está registrado.");
            throw new Exception("Error al registrar: " + e.getMessage());
        }
    }

    // ── Conexión ──────────────────────────────────────────────────────
    public boolean hayConexion() {
        return dao.hayConexion();
    }
}
