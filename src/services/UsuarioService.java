package services;

import dao.UsuarioDAO;
import model.Usuario;
import util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;

public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();

    /** Inicia sesion validando username + password contra Oracle. */
    public Usuario login(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty())
            throw new Exception("El usuario no puede estar vacio.");
        if (password == null || password.isEmpty())
            throw new Exception("La contrasena no puede estar vacia.");

        String hash = PasswordUtil.hashPassword(password);
        Usuario u = dao.buscarPorCredenciales(username.trim(), hash);

        if (u == null)
            throw new Exception("Usuario o contrasena incorrectos.");
        if (!u.isActivo())
            throw new Exception("Tu cuenta esta desactivada. Contacta al administrador.");

        return u;
    }

    /**
     * Registra un nuevo usuario.
     *
     * @param idRol 1=ADMIN, 2=ARTISTA, 3=PRODUCTOR, 4=USUARIO
     */
    public Usuario registrar(String username, String password, String correo,
                             String nombreCompleto, int idRol) throws Exception {
        if (username == null || username.trim().length() < 3)
            throw new Exception("El usuario debe tener al menos 3 caracteres.");
        if (password == null || password.length() < 4)
            throw new Exception("La contrasena debe tener al menos 4 caracteres.");
        if (correo == null || !correo.contains("@"))
            throw new Exception("El correo debe tener formato valido.");
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty())
            throw new Exception("El nombre completo no puede estar vacio.");
        if (idRol < 1 || idRol > 4)
            throw new Exception("Rol invalido (1-4).");

        if (dao.existeUsername(username.trim()))
            throw new Exception("El usuario '" + username + "' ya esta registrado.");
        if (dao.existeCorreo(correo.trim()))
            throw new Exception("El correo '" + correo + "' ya esta registrado.");

        String hash = PasswordUtil.hashPassword(password);
        Usuario nuevo = new Usuario(username.trim(), hash, correo.trim(),
                                    nombreCompleto.trim(), idRol);
        int id = dao.guardar(nuevo);
        return dao.buscarPorId(id);
    }

    public List<Usuario> listar() throws SQLException {
        return dao.listarTodos();
    }

    public boolean actualizar(Usuario u) throws SQLException {
        return dao.actualizar(u);
    }

    public boolean desactivar(int idUsuario) throws SQLException {
        return dao.desactivar(idUsuario);
    }

    public boolean cambiarPassword(int idUsuario, String passwordActual,
                                    String passwordNuevo) throws Exception {
        if (passwordNuevo == null || passwordNuevo.length() < 4)
            throw new Exception("La nueva contrasena debe tener al menos 4 caracteres.");
        Usuario u = dao.buscarPorId(idUsuario);
        if (u == null) throw new Exception("Usuario no encontrado.");
        if (!PasswordUtil.verificar(passwordActual, u.getPasswordHash()))
            throw new Exception("La contrasena actual es incorrecta.");
        return dao.cambiarPassword(idUsuario, PasswordUtil.hashPassword(passwordNuevo));
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