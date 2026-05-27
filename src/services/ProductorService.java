package services;

import dao.ProductorDAO;
import model.Productor;

import java.util.List;

/**
 * ProductorService — lógica de negocio para la gestión de productores.
 *
 * Principio SRP : solo valida reglas de negocio y delega al DAO.
 * Principio separación de capas: la vista nunca llama al DAO directamente.
 */
public class ProductorService {

    private final ProductorDAO dao;

    public ProductorService() {
        this.dao = new ProductorDAO();
    }

    // ── Consultas ─────────────────────────────────────────────────────

    public List<Productor> obtenerTodos() {
        return dao.listarTodos();
    }

    public List<Productor> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return dao.listarTodos();
        }
        return dao.buscarPorTexto(texto.trim());
    }

    // ── Operaciones de negocio ────────────────────────────────────────

    /**
     * Registra un nuevo productor.
     * Parámetros alineados con los campos reales de Productor.java:
     * nombre, especialidad, experiencia, tarifaHora, nacionalidad
     */
    public Productor registrar(String nombre, String especialidad,
                                String experiencia, double tarifaHora,
                                String nacionalidad) {
        validarNombreNoVacio(nombre);
        validarEspecialidadNoVacia(especialidad);
        validarTarifaNoNegativa(tarifaHora);

        Productor p = new Productor(nombre, especialidad, experiencia,
                                    tarifaHora, nacionalidad);
        return dao.insertar(p);
    }

    /**
     * Modifica un productor existente.
     */
    public void modificar(int id, String nombre, String especialidad,
                           String experiencia, double tarifaHora,
                           String nacionalidad, String estado) {
        validarNombreNoVacio(nombre);
        validarEspecialidadNoVacia(especialidad);
        validarTarifaNoNegativa(tarifaHora);

        Productor p = new Productor();
        p.setIdProductor(id);
        p.setNombre(nombre);
        p.setEspecialidad(especialidad);
        p.setExperiencia(experiencia);
        p.setTarifaHora(tarifaHora);
        p.setNacionalidad(nacionalidad);
        p.setEstadoProductor(estado);
        dao.actualizar(p);
    }

    /**
     * Elimina un productor por su ID.
     */
    public void darDeBaja(int id) {
        dao.eliminar(id);
    }

    // ── Validaciones privadas ─────────────────────────────────────────

    private void validarNombreNoVacio(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del productor es obligatorio.");
        }
    }

    private void validarEspecialidadNoVacia(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La especialidad es obligatoria.");
        }
    }

    private void validarTarifaNoNegativa(double tarifa) {
        if (tarifa < 0) {
            throw new IllegalArgumentException("La tarifa no puede ser negativa.");
        }
    }
}