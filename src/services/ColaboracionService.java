package services;

import dao.ColaboracionDAO;
import model.Colaboracion;
import java.sql.SQLException;
import java.util.List;

public class ColaboracionService {

    private final ColaboracionDAO dao = new ColaboracionDAO();

    public List<Colaboracion> listar() throws SQLException {
        return dao.listarTodos();
    }

    public List<Colaboracion> listarPorCancion(int idCancion) throws SQLException {
        return dao.listarPorCancion(idCancion);
    }

    public int crear(Colaboracion c) throws SQLException {
        String error = validar(c);
        if (error != null) throw new IllegalArgumentException(error);
        return dao.crear(c);
    }

    public boolean actualizar(Colaboracion c) throws SQLException {
        String error = validar(c);
        if (error != null) throw new IllegalArgumentException(error);
        return dao.actualizar(c);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }

    private String validar(Colaboracion c) {
        if (c.getNombreColaborador() == null || c.getNombreColaborador().isBlank())
            return "El nombre del colaborador es obligatorio";
        if (c.getIdCancion() == null)
            return "Selecciona una cancion";
        if (c.getFechaColaboracion() == null)
            return "La fecha de colaboracion es obligatoria";
        return null;
    }
}