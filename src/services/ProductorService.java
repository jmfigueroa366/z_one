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

    public List<String> listarNacionalidades() {
        try { return dao.listarNacionalidades(); }
        catch (SQLException e) { throw new RuntimeException("Error al cargar nacionalidades.", e); }
    }

    public List<String> listarGenerosMusicales() {
        try { return dao.listarGenerosMusicales(); }
        catch (SQLException e) { throw new RuntimeException("Error al cargar géneros musicales.", e); }
    }

    public List<String> listarEstados() {
        try { return dao.listarEstados(); }
        catch (SQLException e) { throw new RuntimeException("Error al cargar estados.", e); }
    }

    // ── Crear ─────────────────────────────────────────────────────────

    public Productor registrar(String nombre,
                                String especialidad,
                                String numIdentificacion,
                                LocalDate fechaNacimiento,
                                LocalDate fechaFirma,
                                String nacionalidad,
                                String generoPersona,
                                String generoMusical,
                                String estado) {
        validarNombreNoVacio(nombre);
        validarEspecialidadNoVacia(especialidad);

        Productor p = new Productor();
        p.setNombre           (nombre.trim());
        p.setEspecialidad     (especialidad.trim());
        p.setNumIdentificacion(numIdentificacion);
        p.setFechaNacimiento  (fechaNacimiento);
        p.setFechaFirma       (fechaFirma);
        p.setNacionalidad     (nacionalidad);
        p.setGeneroPersona    (generoPersona);
        p.setGeneroMusical    (generoMusical);
        p.setEstado           (estado);

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
                           String numIdentificacion,
                           LocalDate fechaNacimiento,
                           LocalDate fechaFirma,
                           String nacionalidad,
                           String generoPersona,
                           String generoMusical,
                           String estado) {
        validarNombreNoVacio(nombre);
        validarEspecialidadNoVacia(especialidad);

        Productor p = new Productor();
        p.setIdProductor      (id);
        p.setNombre           (nombre.trim());
        p.setEspecialidad     (especialidad.trim());
        p.setNumIdentificacion(numIdentificacion);
        p.setFechaNacimiento  (fechaNacimiento);
        p.setFechaFirma       (fechaFirma);
        p.setNacionalidad     (nacionalidad);
        p.setGeneroPersona    (generoPersona);
        p.setGeneroMusical    (generoMusical);
        p.setEstado           (estado);

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
}