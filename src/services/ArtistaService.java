package services;

import dao.ArtistaDAO;
import model.Artista;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ArtistaService {

    private final ArtistaDAO dao;

    public ArtistaService() {
        this.dao = new ArtistaDAO();
    }

    // ── Consultas ────────────────────────────────────────────────────

    public List<Artista> obtenerTodos() {
        try {
            return dao.listarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener artistas.", e);
        }
    }

    public List<Artista> buscar(String texto) {
        try {
            if (texto == null || texto.trim().isEmpty()) return dao.listarTodos();
            // Filtra en memoria sobre todos los artistas
            String q = texto.trim().toLowerCase();
            return dao.listarTodos().stream()
                    .filter(a ->
                        (a.getNombreArtista() != null && a.getNombreArtista().toLowerCase().contains(q)) ||
                        (a.getNombreReal()    != null && a.getNombreReal().toLowerCase().contains(q))    ||
                        (a.getGeneroMusical() != null && a.getGeneroMusical().toLowerCase().contains(q)) ||
                        (a.getNacionalidad()  != null && a.getNacionalidad().toLowerCase().contains(q))
                    )
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar artistas.", e);
        }
    }

    // ── Operaciones de negocio ───────────────────────────────────────

public Artista registrar(String nombreArtista, String nombreReal,
                         LocalDate fechaNacimiento, String genero,
                         String nacionalidad, String generoMusical,
                         String redesSociales, LocalDate fechaFirma,
                         String estadoArtista, String tipoArtista) {
    // Las validaciones están FUERA del try — si fallan, el IllegalArgumentException
    // sube limpio a la UI con su mensaje descriptivo
    validarNombreNoVacio(nombreArtista);
    validarGeneroMusicalNoVacio(generoMusical);
    validarEstadoPermitido(estadoArtista);
    validarTipoPermitido(tipoArtista);

    try {
        Artista artista = new Artista();
        artista.setNombreArtista(nombreArtista.trim());
        artista.setNombreReal(nombreReal);
        artista.setFechaNacimiento(fechaNacimiento);
        artista.setGeneroPersona(genero);
        artista.setNacionalidad(nacionalidad);
        artista.setGenerosMusicales(
    Arrays.stream(generoMusical.split(","))
          .map(String::trim)
          .filter(s -> !s.isEmpty())
          .collect(java.util.stream.Collectors.toList())
);
        artista.setRedesSociales(redesSociales);
        artista.setFechaFirma(fechaFirma);
        artista.setEstadoArtista(estadoArtista);
        artista.setTipoArtista(tipoArtista);

        int idGenerado = dao.crear(artista);
        artista.setIdArtista(idGenerado);
        return artista;

    } catch (SQLException e) {
        throw new RuntimeException("Error al registrar artista: " + e.getMessage(), e);
    }
}

public void modificar(int idArtista, String nombreArtista, String nombreReal,
                      LocalDate fechaNacimiento, String genero,
                      String nacionalidad, String generoMusical,
                      String redesSociales, LocalDate fechaFirma,
                      String estadoArtista, String tipoArtista) {
    validarNombreNoVacio(nombreArtista);
    validarGeneroMusicalNoVacio(generoMusical);
    validarEstadoPermitido(estadoArtista);
    validarTipoPermitido(tipoArtista);

    try {
        Artista artista = new Artista();
        artista.setIdArtista(idArtista);
        artista.setNombreArtista(nombreArtista.trim());
        artista.setNombreReal(nombreReal);
        artista.setFechaNacimiento(fechaNacimiento);
        artista.setGeneroPersona(genero);
        artista.setNacionalidad(nacionalidad);
        artista.setGenerosMusicales(
    Arrays.stream(generoMusical.split(","))
          .map(String::trim)
          .filter(s -> !s.isEmpty())
          .collect(java.util.stream.Collectors.toList())
);
        artista.setRedesSociales(redesSociales);
        artista.setFechaFirma(fechaFirma);
        artista.setEstadoArtista(estadoArtista);
        artista.setTipoArtista(tipoArtista);

        dao.actualizar(artista);

    } catch (SQLException e) {
        throw new RuntimeException("Error al modificar artista: " + e.getMessage(), e);
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
public void eliminar(int idArtista) {
    try {
        dao.eliminar(idArtista);
    } catch (SQLException e) {
        throw new RuntimeException("Error al eliminar el artista: " + e.getMessage(), e);
    }
}

public void darDeBaja(int idArtista) {
    try {
        Artista a = dao.buscarPorId(idArtista);
        if (a == null)
            throw new IllegalArgumentException("Artista no encontrado: " + idArtista);
        a.setEstadoArtista(Artista.ESTADO_RETIRADO);
        dao.actualizar(a);
    } catch (SQLException e) {
        throw new RuntimeException("Error al dar de baja al artista: " + e.getMessage(), e);
    }
}

    // ── Validaciones privadas ────────────────────────────────────────

    private void validarNombreNoVacio(String nombre) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre artistico es obligatorio.");
    }

    private void validarGeneroMusicalNoVacio(String generoMusical) {
        if (generoMusical == null || generoMusical.trim().isEmpty())
            throw new IllegalArgumentException("El genero musical es obligatorio.");
    }

    private void validarEstadoPermitido(String estado) {
        if (!Arrays.asList(Artista.ESTADOS_VALIDOS).contains(estado))
            throw new IllegalArgumentException("Estado no valido: " + estado);
    }

    private void validarTipoPermitido(String tipo) {
        if (!Arrays.asList(Artista.TIPOS_VALIDOS).contains(tipo))
            throw new IllegalArgumentException("Tipo de artista no valido: " + tipo);
    }
}
