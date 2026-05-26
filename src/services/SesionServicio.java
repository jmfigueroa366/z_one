package service;

import dao.SesionDAO;
import model.Sesion;

import java.sql.SQLException;
import java.util.List;

/**
 * SesionServicio — lógica de negocio del módulo Sesiones.
 * Valida y delega la persistencia en SesionDAO.
 */
public class SesionServicio {

    private final SesionDAO dao = new SesionDAO();

    // ── Lectura ──────────────────────────────────────────────────────
    public List<Sesion> listar() throws SQLException {
        return dao.listarTodas();
    }

    // ── Alta ─────────────────────────────────────────────────────────
    public int crear(Sesion s) throws SQLException {
        String error = validar(s);
        if (error != null) throw new IllegalArgumentException(error);
        return dao.insertar(s);
    }

    // ── Modificación ─────────────────────────────────────────────────
    public boolean actualizar(Sesion s) throws SQLException {
        String error = validar(s);
        if (error != null) throw new IllegalArgumentException(error);
        return dao.actualizar(s);
    }

    // ── Baja ─────────────────────────────────────────────────────────
    public boolean eliminar(int idSesion) throws SQLException {
        return dao.eliminar(idSesion);
    }

    /**
     * Valida una sesión. Devuelve null si todo está bien,
     * o un mensaje de error listo para mostrar al usuario.
     */
    public String validar(Sesion s) {
        if (s.getNombreSesion() == null || s.getNombreSesion().isBlank())
            return "El nombre de sesión es obligatorio";
        if (s.getFecha() == null)
            return "La fecha es obligatoria";
        if (s.getArtista() == null)
            return "Selecciona un artista";
        if (s.getProductor() == null)
            return "Selecciona un productor";
        if (s.getDuracion() <= 0)
            return "La duración debe ser un número positivo";
        // hora inicio debe ser anterior a hora fin (si ambas vienen)
        String hi = s.getHoraInicio(), hf = s.getHoraFin();
        if (hi != null && hf != null && !hi.isBlank() && !hf.isBlank()
                && hi.compareTo(hf) >= 0)
            return "La hora de inicio debe ser anterior a la hora de fin";
        return null;
    }
}