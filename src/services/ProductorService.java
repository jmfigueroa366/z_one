package services;

import dao.ProductorDAO;
import model.Productor;

import java.util.List;

/**
 * ProductorService — lógica de negocio para la gestión de productores.
 * Valida reglas de negocio y delega la persistencia al DAO.
 */
public class ProductorService {

    private final ProductorDAO dao;

    public ProductorService() {
        this.dao = new ProductorDAO();
    }

    /** Retorna todos los productores registrados. */
    public List<Productor> obtenerTodos() {
        return dao.listarTodos();
    }

    /** Filtra productores por nombre, especialidad o correo. */
    public List<Productor> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return dao.listarTodos();
        }
        return dao.buscarPorTexto(texto.trim());
    }

    /** Registra un nuevo productor tras validar las reglas de negocio. */
    public Productor registrar(String nombre, String correo, String telefono,
                               String especialidad, double tarifaHora) {
        validarNombreNoVacio(nombre);
        validarEspecialidadNoVacia(especialidad);
        validarTarifaNoNegativa(tarifaHora);

        Productor productor = new Productor(
            0, nombre.trim(), correo, telefono,
            especialidad.trim(), tarifaHora
        );
        return dao.insertar(productor);
    }

    /** Modifica los datos de un productor existente. */
    public void modificar(int id, String nombre, String correo, String telefono,
                          String especialidad, double tarifaHora) {
        validarNombreNoVacio(nombre);
        validarEspecialidadNoVacia(especialidad);
        validarTarifaNoNegativa(tarifaHora);

        Productor productor = new Productor(
            id, nombre.trim(), correo, telefono,
            especialidad.trim(), tarifaHora
        );
        dao.actualizar(productor);
    }

    /** Elimina permanentemente un productor de la BD. */
    public void darDeBaja(int id) {
        dao.eliminar(id);
    }

    // ── Validaciones privadas ────────────────────────────────────────

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
            throw new IllegalArgumentException("La tarifa por hora no puede ser negativa.");
        }
    }
}
