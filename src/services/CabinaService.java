package services;

import dao.CabinaDAO;
import model.Cabina;

import java.sql.SQLException;
import java.util.List;

public class CabinaService {

    private final CabinaDAO dao = new CabinaDAO();

    public List<Cabina> listar() throws SQLException {
        return dao.listarTodos();
    }

    public int crear(Cabina c) throws SQLException {
        String error = validar(c);
        if (error != null) throw new IllegalArgumentException(error);

        // Validar nombre duplicado ANTES de insertar
        if (existeNombre(c.getNombreCabina(), null)) {
            throw new IllegalArgumentException(
                "Ya existe una cabina con el nombre '" + c.getNombreCabina() + "'");
        }
        return dao.crear(c);
    }

    public boolean actualizar(Cabina c) throws SQLException {
        String error = validar(c);
        if (error != null) throw new IllegalArgumentException(error);

        // Validar nombre duplicado (excepto la cabina actual)
        if (existeNombre(c.getNombreCabina(), c.getIdCabina())) {
            throw new IllegalArgumentException(
                "Ya existe otra cabina con el nombre '" + c.getNombreCabina() + "'");
        }
        return dao.actualizar(c);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }

    public boolean cambiarEstado(int idCabina, String nuevoEstado) throws SQLException {
        return dao.cambiarEstado(idCabina, nuevoEstado);
    }

    /** Verifica si ya existe una cabina con ese nombre. Si idExcluir != null, ignora esa cabina. */
    private boolean existeNombre(String nombre, Integer idExcluir) throws SQLException {
        if (nombre == null) return false;
        List<Cabina> todas = dao.listarTodos();
        for (Cabina c : todas) {
            if (c.getNombreCabina() != null
                && c.getNombreCabina().trim().equalsIgnoreCase(nombre.trim())
                && (idExcluir == null || c.getIdCabina() != idExcluir)) {
                return true;
            }
        }
        return false;
    }

    private String validar(Cabina c) {
        if (c.getNombreCabina() == null || c.getNombreCabina().isBlank())
            return "El nombre de la cabina es obligatorio";
        return null;
    }
}