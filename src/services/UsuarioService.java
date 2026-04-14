package services;

import dao.UsuarioDAO;
import model.Usuario;
import util.PasswordUtil;

import java.sql.SQLException;

public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario login(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty())
            throw new Exception("El usuario no puede estar vacio.");
        if (password == null || password.isEmpty())
            throw new Exception("La contrasena no puede estar vacia.");

        String hash = PasswordUtil.hashPassword(password);
        Usuario usuario = dao.buscarPorCredenciales(username.trim(), hash);

        if (usuario == null)
            throw new Exception("Usuario o contrasena incorrectos.");
        if (!usuario.isActivo())
            throw new Exception("Tu cuenta esta desactivada. Contacta al administrador.");

        return usuario;
    }

    public Usuario registrar(String username, String password,
                              String nombre, String correo, String rol) throws Exception {
        if (username == null || username.trim().length() < 3)
            throw new Exception("El usuario debe tener al menos 3 caracteres.");
        if (password == null || password.length() < 4)
            throw new Exception("La contrasena debe tener al menos 4 caracteres.");
        if (nombre == null || nombre.trim().isEmpty())
            throw new Exception("El nombre no puede estar vacio.");
        if (correo == null || !correo.contains("@"))
            throw new Exception("El correo debe tener formato valido.");
        if (dao.existeUsername(username.trim()))
            throw new Exception("El usuario '" + username + "' ya esta registrado.");

        String hash = PasswordUtil.hashPassword(password);
        Usuario nuevo = new Usuario(username.trim(), hash, nombre.trim(), correo.trim(), rol);
        dao.guardar(nuevo);
        return dao.buscarPorUsername(username.trim());
    }

    public boolean hayConexion() {
        try {
            dao.listarTodos();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
