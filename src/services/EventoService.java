package services;

import dao.EventoDAO;
import model.Evento;
import java.sql.SQLException;
import java.util.List;

public class EventoService {

    private final EventoDAO dao = new EventoDAO();

    public List<Evento> listar() throws SQLException {
        return dao.listarTodos();
    }

    public List<Evento> listarProximos() throws SQLException {
        return dao.listarProximos();
    }

    public int crear(Evento e) throws SQLException {
        String error = validar(e);
        if (error != null) throw new IllegalArgumentException(error);
        return dao.crear(e);
    }

    public boolean actualizar(Evento e) throws SQLException {
        String error = validar(e);
        if (error != null) throw new IllegalArgumentException(error);
        return dao.actualizar(e);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }

    private String validar(Evento e) {
       
        if (e.getFecha() == null)
            return "La fecha del evento es obligatoria";
        if (e.getHoraInicio() != null && e.getHoraFin() != null
                && !e.getHoraFin().isAfter(e.getHoraInicio()))
            return "La hora fin debe ser posterior a la hora inicio";
        if (e.getDescripcion() == null || e.getDescripcion().isBlank())
            return "La descripcion del evento es obligatoria";
        return null;
    }
}