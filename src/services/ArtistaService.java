package services;

import dao.ArtistaDAO;
import model.Artista;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * ArtistaService — lógica de negocio para PERFIL_ARTISTA.
 * Solo valida reglas; delega la persistencia al DAO.
 */
public class ArtistaService {

    private final ArtistaDAO dao;

    public ArtistaService() {
        this.dao = new ArtistaDAO();
    }

    // ── Consultas ────────────────────────────────────────────────────

    public List<Artista> obtenerTodos() {
        return dao.listarTodos();
    }

    /** Si el texto está vacío retorna todos los artistas. */
    public List<Artista> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) return dao.listarTodos();
        return dao.buscarPorTexto(texto.trim());
    }

    // ── Operaciones de negocio ───────────────────────────────────────

    /**
     * Registra un nuevo artista validando las reglas de negocio.
     *
     * @throws IllegalArgumentException si algún campo obligatorio falla.
     */
    public Artista registrar(String nombreArtista, String nombreReal,
                              LocalDate fechaNacimiento, String genero,
                              String nacionalidad, String generoMusical,
                              String redesSociales, LocalDate fechaFirma,
                              String estadoArtista, String tipoArtista) {

        validarNombreNoVacio(nombreArtista);
        validarGeneroMusicalNoVacio(generoMusical);
        validarEstadoPermitido(estadoArtista);
        validarTipoPermitido(tipoArtista);

        Artista artista = new Artista(
            nombreArtista.trim(), nombreReal,
            fechaNacimiento, genero,
            nacionalidad, generoMusical.trim(),
            redesSociales, fechaFirma,
            estadoArtista, tipoArtista
        );
        return dao.insertar(artista);
    }

    /**
     * Modifica los datos de un artista existente.
     *
     * @throws IllegalArgumentException si algún campo obligatorio falla.
     */
    public void modificar(int idArtista, String nombreArtista, String nombreReal,
                           LocalDate fechaNacimiento, String genero,
                           String nacionalidad, String generoMusical,
                           String redesSociales, LocalDate fechaFirma,
                           String estadoArtista, String tipoArtista) {

        validarNombreNoVacio(nombreArtista);
        validarGeneroMusicalNoVacio(generoMusical);
        validarEstadoPermitido(estadoArtista);
        validarTipoPermitido(tipoArtista);

        Artista artista = new Artista(
            idArtista, null,
            nombreArtista.trim(), nombreReal,
            fechaNacimiento, genero,
            nacionalidad, generoMusical.trim(),
            redesSociales, fechaFirma,
            estadoArtista, tipoArtista
        );
        dao.actualizar(artista);
    }

    /** Elimina permanentemente un artista. */
    public void darDeBaja(int idArtista) {
        dao.eliminar(idArtista);
    }

    // ── Validaciones privadas ────────────────────────────────────────

    private void validarNombreNoVacio(String nombre) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre artístico es obligatorio.");
    }

    private void validarGeneroMusicalNoVacio(String generoMusical) {
        if (generoMusical == null || generoMusical.trim().isEmpty())
            throw new IllegalArgumentException("El género musical es obligatorio.");
    }

    private void validarEstadoPermitido(String estado) {
        if (!Arrays.asList(Artista.ESTADOS_VALIDOS).contains(estado))
            throw new IllegalArgumentException("Estado no válido: " + estado);
    }

    private void validarTipoPermitido(String tipo) {
        if (!Arrays.asList(Artista.TIPOS_VALIDOS).contains(tipo))
            throw new IllegalArgumentException("Tipo de artista no válido: " + tipo);
    }
}