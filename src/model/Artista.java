package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Artista {

    // ── Constantes catálogo ──────────────────────────────────────────
    public static final String ESTADO_ACTIVO         = "Activo";
    public static final String ESTADO_EN_NEGOCIACION = "En Negociacion";
    public static final String ESTADO_EN_PAUSA       = "En Pausa";
    public static final String ESTADO_RETIRADO       = "Retirado";
    public static final String[] ESTADOS_VALIDOS = {
        ESTADO_ACTIVO, ESTADO_EN_NEGOCIACION, ESTADO_EN_PAUSA, ESTADO_RETIRADO
    };

    public static final String TIPO_SOLISTA = "Solista";
    public static final String TIPO_BANDA   = "Banda";
    public static final String[] TIPOS_VALIDOS = { TIPO_SOLISTA, TIPO_BANDA };

    public static final String[] GENEROS_PERSONA = {
        "Masculino", "Femenino", "Prefiero no decir"
    };

    // ── Atributos ────────────────────────────────────────────────────
    private Integer   idArtista;
    private Integer   idUsuario;
    private String    nombreArtista;
    private String    nombreReal;
    private LocalDate fechaNacimiento;
    private String    generoPersona;
    private String    nacionalidad;
    private String    redesSociales;
    private LocalDate fechaFirma;
    private String    estadoArtista;
    private String    tipoArtista;
    private String    numIdentificacion;

    // Ahora es una lista porque un artista puede tener varios géneros
    private List<String> generosMusicales = new ArrayList<>();

    // ── Constructores ────────────────────────────────────────────────
    public Artista() {}

    public Artista(Integer idArtista, Integer idUsuario,
                   String nombreArtista, String nombreReal,
                   LocalDate fechaNacimiento, String generoPersona,
                   String nacionalidad, List<String> generosMusicales,
                   String redesSociales, LocalDate fechaFirma,
                   String estadoArtista, String tipoArtista) {
        this.idArtista        = idArtista;
        this.idUsuario        = idUsuario;
        this.nombreArtista    = nombreArtista;
        this.nombreReal       = nombreReal;
        this.fechaNacimiento  = fechaNacimiento;
        this.generoPersona    = generoPersona;
        this.nacionalidad     = nacionalidad;
        this.generosMusicales = generosMusicales != null ? generosMusicales : new ArrayList<>();
        this.redesSociales    = redesSociales;
        this.fechaFirma       = fechaFirma;
        this.estadoArtista    = estadoArtista;
        this.tipoArtista      = tipoArtista;
    }

    // ── Getters ──────────────────────────────────────────────────────
    public Integer   getIdArtista()          { return idArtista; }
    public Integer   getIdUsuario()          { return idUsuario; }
    public String    getNombreArtista()      { return nombreArtista; }
    public String    getNombreReal()         { return nombreReal; }
    public LocalDate getFechaNacimiento()    { return fechaNacimiento; }
    public String    getGeneroPersona()      { return generoPersona; }
    public String    getNacionalidad()       { return nacionalidad; }
    public String    getRedesSociales()      { return redesSociales; }
    public LocalDate getFechaFirma()         { return fechaFirma; }
    public String    getEstadoArtista()      { return estadoArtista; }
    public String    getTipoArtista()        { return tipoArtista; }
    public String    getNumIdentificacion()  { return numIdentificacion; }
    public List<String> getGenerosMusicales() { return generosMusicales; }

    /**
     * Devuelve el primer género musical (o null si no tiene ninguno).
     * Útil para mostrar en la tabla sin romper el código existente.
     */
    public String getGeneroMusical() {
        return (generosMusicales != null && !generosMusicales.isEmpty())
               ? generosMusicales.get(0) : null;
    }

    /**
     * Devuelve todos los géneros separados por coma.
     * Útil para mostrar en la tabla o en etiquetas.
     */
    public String getGenerosMusicalesTexto() {
        return (generosMusicales != null) ? String.join(", ", generosMusicales) : "";
    }

    // ── Setters ──────────────────────────────────────────────────────
    public void setIdArtista(Integer id)              { this.idArtista = id; }
    public void setIdUsuario(Integer id)              { this.idUsuario = id; }
    public void setNombreArtista(String s)            { this.nombreArtista = s; }
    public void setNombreReal(String s)               { this.nombreReal = s; }
    public void setFechaNacimiento(LocalDate f)       { this.fechaNacimiento = f; }
    public void setGeneroPersona(String s)            { this.generoPersona = s; }
    public void setNacionalidad(String s)             { this.nacionalidad = s; }
    public void setRedesSociales(String s)            { this.redesSociales = s; }
    public void setFechaFirma(LocalDate f)            { this.fechaFirma = f; }
    public void setEstadoArtista(String s)            { this.estadoArtista = s; }
    public void setTipoArtista(String s)              { this.tipoArtista = s; }
    public void setNumIdentificacion(String s)        { this.numIdentificacion = s; }
    public void setGenerosMusicales(List<String> list){ this.generosMusicales = list != null ? list : new ArrayList<>(); }

    /**
     * Setter de compatibilidad: recibe un String y lo convierte en lista de uno.
     * Así el código existente que llame setGeneroMusical() no rompe.
     */
    public void setGeneroMusical(String s) {
        this.generosMusicales = new ArrayList<>();
        if (s != null && !s.isBlank()) this.generosMusicales.add(s.trim());
    }

    @Override
    public String toString() {
        return nombreArtista != null ? nombreArtista : "Artista#" + idArtista;
    }
}