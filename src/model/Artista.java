package model;

import java.time.LocalDate;

/**
 * Entidad Artista alineada con la tabla ARTISTAS.
 */
public class Artista {

    public static final String ESTADO_ACTIVO   = "Activo";
    public static final String ESTADO_EN_PAUSA = "En pausa";
    public static final String ESTADO_RETIRADO = "Retirado";
    public static final String[] ESTADOS_VALIDOS = {ESTADO_ACTIVO, ESTADO_EN_PAUSA, ESTADO_RETIRADO};

    public static final String TIPO_SOLISTA = "Solista";
    public static final String TIPO_BANDA   = "Banda";
    public static final String TIPO_DUO     = "Duo";
    public static final String[] TIPOS_VALIDOS = {TIPO_SOLISTA, TIPO_BANDA, TIPO_DUO};

    private Integer   idArtista;
    private Integer   idUsuario;
    private String    nombreArtista;
    private String    nombreReal;
    private LocalDate fechaNacimiento;
    private String    generoPersona;
    private String    nacionalidad;
    private String    generoMusical;
    private String    redesSociales;
    private LocalDate fechaFirma;
    private String    estadoArtista;
    private String    tipoArtista;
    private String    numIdentificacion;

    public Artista() {}

    public Artista(Integer idArtista, Integer idUsuario,
                   String nombreArtista, String nombreReal,
                   LocalDate fechaNacimiento, String generoPersona,
                   String nacionalidad, String generoMusical,
                   String redesSociales, LocalDate fechaFirma,
                   String estadoArtista, String tipoArtista) {
        this.idArtista       = idArtista;
        this.idUsuario       = idUsuario;
        this.nombreArtista   = nombreArtista;
        this.nombreReal      = nombreReal;
        this.fechaNacimiento = fechaNacimiento;
        this.generoPersona   = generoPersona;
        this.nacionalidad    = nacionalidad;
        this.generoMusical   = generoMusical;
        this.redesSociales   = redesSociales;
        this.fechaFirma      = fechaFirma;
        this.estadoArtista   = estadoArtista;
        this.tipoArtista     = tipoArtista;
    }

    // ── Getters ──
    public Integer   getIdArtista()         { return idArtista; }
    public Integer   getIdUsuario()         { return idUsuario; }
    public String    getNombreArtista()     { return nombreArtista; }
    public String    getNombreReal()        { return nombreReal; }
    public LocalDate getFechaNacimiento()   { return fechaNacimiento; }
    public String    getGeneroPersona()     { return generoPersona; }
    public String    getNacionalidad()      { return nacionalidad; }
    public String    getGeneroMusical()     { return generoMusical; }
    public String    getRedesSociales()     { return redesSociales; }
    public LocalDate getFechaFirma()        { return fechaFirma; }
    public String    getEstadoArtista()     { return estadoArtista; }
    public String    getTipoArtista()       { return tipoArtista; }
    public String    getNumIdentificacion() { return numIdentificacion; }

    // ── Setters ──
    public void setIdArtista(Integer id)             { this.idArtista = id; }
    public void setIdUsuario(Integer id)             { this.idUsuario = id; }
    public void setNombreArtista(String s)           { this.nombreArtista = s; }
    public void setNombreReal(String s)              { this.nombreReal = s; }
    public void setFechaNacimiento(LocalDate f)      { this.fechaNacimiento = f; }
    public void setGeneroPersona(String s)           { this.generoPersona = s; }
    public void setNacionalidad(String s)            { this.nacionalidad = s; }
    public void setGeneroMusical(String s)           { this.generoMusical = s; }
    public void setRedesSociales(String s)           { this.redesSociales = s; }
    public void setFechaFirma(LocalDate f)           { this.fechaFirma = f; }
    public void setEstadoArtista(String s)           { this.estadoArtista = s; }
    public void setTipoArtista(String s)             { this.tipoArtista = s; }
    public void setNumIdentificacion(String s)       { this.numIdentificacion = s; }

    @Override
    public String toString() {
        return nombreArtista != null ? nombreArtista : "Artista#" + idArtista;
    }
}