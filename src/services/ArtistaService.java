package services;
import dao.ArtistaDAO;
import java.util.List;
import model.Artista;

import java.util.Arrays;

/**
 * ArtistaService — lógica de negocio para la gestión de artistas.
 *
 * Principio 1 (Nombres significativos): métodos con verbos de
 *   dominio (registrar, modificar, dar de baja) en lugar de
 *   términos técnicos de BD (insert, update, delete).
 * Principio 2 (SRP): solo valida reglas de negocio y delega
 *   la persistencia al DAO; no toca Swing ni SQL directamente.
 * Principio 4 (Funciones pequeñas): cada validación es un
 *   método privado con nombre que explica la regla.
 * Principio 7 (Separación de capas): la vista llama al service,
 *   el service llama al DAO; nunca la vista al DAO directamente.
 */
public class ArtistaService {

    private final ArtistaDAO dao;

    // ── Constructor con inyección del DAO ────────────────────────────
    public ArtistaService() {
        this.dao = new ArtistaDAO();
    }

    // ── Consultas ────────────────────────────────────────────────────

    /**
     * Retorna todos los artistas registrados en la BD.
     */
    public List<Artista> obtenerTodos() {
        return dao.listarTodos();
    }

    /**
     * Filtra artistas por nombre, género o país.
     * Si el texto está vacío retorna todos.
     */
    public List<Artista> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return dao.listarTodos();
        }
        return dao.buscarPorTexto(texto.trim());
    }

    // ── Operaciones de negocio ───────────────────────────────────────

    /**
     * Registra un nuevo artista después de validar las reglas de negocio.
     *
     * @throws IllegalArgumentException si algún campo obligatorio falla.
     */
    public Artista registrar(String nombre, String correo, String telefono,
                              String genero, String pais,
                              int cantidadCanciones, String estado) {
        validarNombreNoVacio(nombre);
        validarGeneroNoVacio(genero);
        validarCancionesNoNegativas(cantidadCanciones);
        validarEstadoPermitido(estado);

        Artista artista = new Artista(
            0, nombre.trim(), correo, telefono,
            genero.trim(), pais, cantidadCanciones, estado
        );
        return dao.insertar(artista);
    }

    /**
     * Modifica los datos de un artista existente.
     *
     * @throws IllegalArgumentException si algún campo obligatorio falla.
     */
    public void modificar(int id, String nombre, String correo, String telefono,
                           String genero, String pais,
                           int cantidadCanciones, String estado) {
        validarNombreNoVacio(nombre);
        validarGeneroNoVacio(genero);
        validarCancionesNoNegativas(cantidadCanciones);
        validarEstadoPermitido(estado);

        Artista artista = new Artista(
            id, nombre.trim(), correo, telefono,
            genero.trim(), pais, cantidadCanciones, estado
        );
        dao.actualizar(artista);
    }

    /**
     * Elimina permanentemente un artista de la BD.
     */
    public void darDeBaja(int id) {
        dao.eliminar(id);
    }

    // ── Validaciones privadas (Principio 4 — funciones pequeñas) ────

    private void validarNombreNoVacio(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre artístico es obligatorio.");
        }
    }

    private void validarGeneroNoVacio(String genero) {
        if (genero == null || genero.trim().isEmpty()) {
            throw new IllegalArgumentException("El género musical es obligatorio.");
        }
    }

    private void validarCancionesNoNegativas(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("El número de canciones no puede ser negativo.");
        }
    }

    private void validarEstadoPermitido(String estado) {
        boolean esValido = Arrays.asList(Artista.ESTADOS_VALIDOS).contains(estado);
        if (!esValido) {
            throw new IllegalArgumentException("Estado no válido: " + estado);
        }
    }
}
