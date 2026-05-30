package util;

import model.Usuario;

/**
 * Singleton que guarda el usuario actualmente autenticado en memoria.
 * Se inicializa tras un login exitoso y se limpia al cerrar sesion.
 *
 * Uso:
 *   SesionUsuario.iniciar(usuario);
 *   Usuario actual = SesionUsuario.get();
 *   if (SesionUsuario.esAdmin()) { ... }
 *   SesionUsuario.cerrar();
 */
public class SesionUsuario {

    private static Usuario usuarioActual;

    private SesionUsuario() {}

    public static void iniciar(Usuario u) {
        usuarioActual = u;
    }

    public static Usuario get() {
        return usuarioActual;
    }

    public static boolean hayLogin() {
        return usuarioActual != null;
    }

    public static void cerrar() {
        usuarioActual = null;
    }

    // ── Utilidades rapidas de rol ──
    public static boolean esAdmin() {
        return usuarioActual != null && usuarioActual.esAdmin();
    }
    public static boolean esArtista() {
        return usuarioActual != null && usuarioActual.esArtista();
    }
    public static boolean esProductor() {
        return usuarioActual != null && usuarioActual.esProductor();
    }

    public static String nombre() {
        return usuarioActual != null ? usuarioActual.getNombreCompleto() : "Invitado";
    }

    public static Integer id() {
        return usuarioActual != null ? usuarioActual.getIdUsuario() : null;
    }

    public static int rol() {
        return usuarioActual != null ? usuarioActual.getIdRol() : -1;
    }
}