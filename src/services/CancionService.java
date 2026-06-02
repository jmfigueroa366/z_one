package services;

import dao.CancionDao;
import model.Cancion;

import java.sql.SQLException;
import java.util.List;

public class CancionService {

    private final CancionDao dao = new CancionDao();

    public List<Cancion> listar() throws SQLException {
        return dao.listarTodos();
    }

    public List<Cancion> buscar(String texto) throws SQLException {
        return dao.buscar(texto);
    }

    public int crear(Cancion c) throws SQLException {
        String error = validar(c);
        if (error != null) throw new IllegalArgumentException(error);
        return dao.crear(c);
    }

    public boolean actualizar(Cancion c) throws SQLException {
        String error = validar(c);
        if (error != null) throw new IllegalArgumentException(error);
        return dao.actualizar(c);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }

    private String validar(Cancion c) {
        if (c.getTitulo() == null || c.getTitulo().isBlank())
            return "El titulo de la cancion es obligatorio";
        if (c.getBpm() != null && (c.getBpm() <= 0 || c.getBpm() > 300))
            return "El BPM debe estar entre 1 y 300";
        if (c.getIdProductor() == null)
            return "Selecciona un productor";
        return null;
    }
}   