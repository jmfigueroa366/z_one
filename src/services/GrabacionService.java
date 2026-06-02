package services;

import dao.GrabacionDAO;
import model.Grabacion;

import java.sql.SQLException;
import java.util.List;

public class GrabacionService {

    private final GrabacionDAO dao = new GrabacionDAO();

    public List<Grabacion> listar() throws SQLException {
        return dao.listarTodos();
    }

    public List<Grabacion> listarPorSesion(int idSesion) throws SQLException {
        return dao.listarPorSesion(idSesion);
    }

    public int crear(Grabacion g) throws SQLException {
        if (g.getIdSesion() == null)
            throw new IllegalArgumentException("La grabacion debe estar asociada a una sesion");
        if (g.getNombreArchivo() == null || g.getNombreArchivo().isBlank())
            throw new IllegalArgumentException("Nombre de archivo obligatorio");
        if (g.getRutaArchivo() == null || g.getRutaArchivo().isBlank())
            throw new IllegalArgumentException("Ruta de archivo obligatoria");
        return dao.crear(g);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }
}