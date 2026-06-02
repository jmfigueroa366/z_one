package services;

import dao.ProductorDAO;
import model.Productor;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ProductorService {

    private final ProductorDAO dao;

    public ProductorService() {
        this.dao = new ProductorDAO();
    }

    // ── Consultas ─────────────────────────────────────────────────────

    public List<Productor> obtenerTodos() {
        try {
            return dao.listarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener productores: " + e.getMessage(), e);
        }
    }

    public List<Productor> buscar(String texto) {
        try {
            // dao.buscar() ya maneja el caso null/vacío internamente
            return dao.buscar(texto == null ? "" : texto.trim());
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar productores: " + e.getMessage(), e);
        }
    }

    public Productor buscarPorId(int id) {
        try {
            return dao.buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar productor: " + e.getMessage(), e);
        }
    }

    // ── Crear ─────────────────────────────────────────────────────────

    /**
     * Registra un nuevo productor con los campos que maneja el DAO.
     * Los campos correo/telefono no están en la tabla PRODUCTORES
     * (no los inserta el DAO) pero se pueden setear en el objeto
     * si tu BD los tiene; por ahora se deja como en el modelo.
     */
    public Productor registrar(String nombre,
                                String especialidad,
                                double tarifaHora,
                                LocalDate fechaFirma,
                                LocalDate fechaNacimiento,
                                String numIdentificacion,
                                String nacionalidad,
                                String generoPersona,
                                String generoMusical,
                                String estado,
                                Integer idUsuario) {
        validarNombreNoVacio(nombre);
        validarEspecialidadNoVacia(especialidad);
        validarTarifaNoNegativa(tarifaHora);

        Productor p = new Productor();
        p.setNombre(nombre);
        p.setEspecialidad(especialidad);
        p.setTarifaHora(tarifaHora);
        p.setFechaFirma(fechaFirma);
        p.setFechaNacimiento(fechaNacimiento);
        p.setNumIdentificacion(numIdentificacion);
        p.setNacionalidad(nacionalidad);
        p.setGeneroPersona(generoPersona);
        p.setGeneroMusical(generoMusical);
        p.setEstado(estado);
        p.setIdUsuario(idUsuario);

        try {
            int nuevoId = dao.crear(p);
            if (nuevoId > 0) p.setIdProductor(nuevoId);
            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar productor: " + e.getMessage(), e);
        }
    }

    // ── Modificar ─────────────────────────────────────────────────────

    public void modificar(int id,
                           String nombre,
                           String especialidad,
                           double tarifaHora,
                           LocalDate fechaFirma,
                           LocalDate fechaNacimiento,
                           String numIdentificacion,
                           String nacionalidad,
                           String generoPersona,
                           String generoMusical,
                           String estado) {
        validarNombreNoVacio(nombre);
        validarEspecialidadNoVacia(especialidad);
        validarTarifaNoNegativa(tarifaHora);

        Productor p = new Productor();
        p.setIdProductor(id);
        p.setNombre(nombre);
        p.setEspecialidad(especialidad);
        p.setTarifaHora(tarifaHora);
        p.setFechaFirma(fechaFirma);
        p.setFechaNacimiento(fechaNacimiento);
        p.setNumIdentificacion(numIdentificacion);
        p.setNacionalidad(nacionalidad);
        p.setGeneroPersona(generoPersona);
        p.setGeneroMusical(generoMusical);
        p.setEstado(estado);

        try {
            dao.actualizar(p);
        } catch (SQLException e) {
            throw new RuntimeException("Error al modificar productor: " + e.getMessage(), e);
        }
    }

    // ── Eliminar ──────────────────────────────────────────────────────

    public void darDeBaja(int id) {
        try {
            dao.eliminar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar productor: " + e.getMessage(), e);
        }
    }

    // ── Validaciones privadas ─────────────────────────────────────────

    private void validarNombreNoVacio(String nombre) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre del productor es obligatorio.");
    }

    private void validarEspecialidadNoVacia(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty())
            throw new IllegalArgumentException("La especialidad es obligatoria.");
    }

    private void validarTarifaNoNegativa(double tarifa) {
        if (tarifa < 0)
            throw new IllegalArgumentException("La tarifa no puede ser negativa.");
    }
}