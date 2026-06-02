package service;

import dao.SesionDAO;
import model.Sesion;
import java.sql.SQLException;
import java.util.List;

public class SesionServicio {

    private final SesionDAO dao = new SesionDAO();

    // ── Lectura ──
    public List<Sesion> listar() throws SQLException {
        return dao.listarTodas();
    }

    // ── Alta ──
    public int crear(Sesion s) throws SQLException {
        String error = validar(s);
        if (error != null) throw new IllegalArgumentException(error);
        return dao.insertar(s);
    }

    // ── Modificación ──
    public boolean actualizar(Sesion s) throws SQLException {
        String error = validar(s);
        if (error != null) throw new IllegalArgumentException(error);
        return dao.actualizar(s);
    }

    // ── Baja ──
    public boolean eliminar(int idGrabacion) throws SQLException {
        return dao.eliminar(idGrabacion);
    }

    public String validar(Sesion s) {
        if (s.getNombreSesion() == null || s.getNombreSesion().isBlank())
            return "El nombre de sesion es obligatorio";
        if (s.getFechaGrabacion() == null)
            return "La fecha es obligatoria";
        if (s.getIdArtista() == null)
            return "Selecciona un artista";
        if (s.getIdProductor() == null)
            return "Selecciona un productor";
        if (s.getIdCancion() == null)
            return "Selecciona una cancion";
        if (s.getIdFase() == null)
            return "Selecciona una fase de produccion";
        if (s.getIdEstadoGrabacion() == null)
            return "Selecciona un estado de grabacion";
        if (s.getHoraInicio() != null && s.getHoraFin() != null
                && !s.getHoraFin().isAfter(s.getHoraInicio()))
            return "La hora de inicio debe ser anterior a la hora de fin";
        return null;
    }
}