package model;

import java.time.LocalDate;

/**
 * Artista — modelo que refleja exactamente la tabla PERFIL_ARTISTA de Oracle.
 *
 * Columnas mapeadas:
 *   ID_ARTISTA, ID_USUARIO, NOMBRE_ARTISTA, NOMBRE_REAL,
 *   FECHA_NACIMIENTO, GENERO, NACIONALIDAD, GENERO_MUSICAL,
 *   REDES_SOCIALES, FECHA_FIRMA, ESTADO_ARTISTA, TIPO_ARTISTA
 */
public class Artista {

    // ── Estados válidos según la BD ──────────────────────────────────
    public static final String ESTADO_ACTIVO   = "Activo";
    public static final String ESTADO_EN_PAUSA = "En Pausa";
    public static final String ESTADO_INACTIVO = "Inactivo";
    public static final String[] ESTADOS_VALIDOS = {
        ESTADO_ACTIVO, ESTADO_EN_PAUSA, ESTADO_INACTIVO
    };

    // ── Tipos válidos según la BD ────────────────────────────────────
    public static final String TIPO_SOLISTA = "Solista";
    public static final String TIPO_GRUPO   = "Grupo";
    public static final String[] TIPOS_VALIDOS = { TIPO_SOLISTA, TIPO_GRUPO };

    // ── Atributos (uno por columna) ──────────────────────────────────
    private int       idArtista;
    private Integer   idUsuario;        // puede ser null (enlace opcional)
    private String    nombreArtista;
    private String    nombreReal;
    private LocalDate fechaNacimiento;
    private String    genero;           // genero de persona (M/F/Otro)
    private String    nacionalidad;
    private String    generoMusical;
    private String    redesSociales;
    private LocalDate fechaFirma;
    private String    estadoArtista;
    private String    tipoArtista;

    // ── Constructor completo ─────────────────────────────────────────
    public Artista(int idArtista, Integer idUsuario,
                   String nombreArtista, String nombreReal,
                   LocalDate fechaNacimiento, String genero,
                   String nacionalidad, String generoMusical,
                   String redesSociales, LocalDate fechaFirma,
                   String estadoArtista, String tipoArtista) {
        this.idArtista       = idArtista;
        this.idUsuario       = idUsuario;
        this.nombreArtista   = nombreArtista;
        this.nombreReal      = nombreReal;
        this.fechaNacimiento = fechaNacimiento;
        this.genero          = genero;
        this.nacionalidad    = nacionalidad;
        this.generoMusical   = generoMusical;
        this.redesSociales   = redesSociales;
        this.fechaFirma      = fechaFirma;
        this.estadoArtista   = estadoArtista;
        this.tipoArtista     = tipoArtista;
    }

    // ── Constructor mínimo (para formularios de alta) ────────────────
    public Artista(String nombreArtista, String nombreReal,
                   LocalDate fechaNacimiento, String genero,
                   String nacionalidad, String generoMusical,
                   String redesSociales, LocalDate fechaFirma,
                   String estadoArtista, String tipoArtista) {
        this(0, null, nombreArtista, nombreReal, fechaNacimiento,
             genero, nacionalidad, generoMusical, redesSociales,
             fechaFirma, estadoArtista, tipoArtista);
    }

    // ── Getters y Setters ────────────────────────────────────────────
    public int       getIdArtista(){ 
        return idArtista; }
    public void      setIdArtista(int idArtista){
        this.idArtista = idArtista; }

    public Integer   getIdUsuario(){ 
        return idUsuario; }
    public void      setIdUsuario(Integer idUsuario)
    { this.idUsuario = idUsuario; }

    public String    getNombreArtista()
    { return nombreArtista; }
    public void      setNombreArtista(String nombreArtista)  
    { this.nombreArtista = nombreArtista; }

    public String    getNombreReal()
    { return nombreReal; }
    public void      setNombreReal(String nombreReal)
    { this.nombreReal = nombreReal; }

    public LocalDate getFechaNacimiento()
    { return fechaNacimiento; }
    public void      setFechaNacimiento(LocalDate f) 
    { this.fechaNacimiento = f; }

    public String    getGenero()
    { return genero; }
    public void      setGenero(String genero)
    { this.genero = genero; }

    public String    getNacionalidad()
    { return nacionalidad; }
    public void      setNacionalidad(String nacionalidad)   
    { this.nacionalidad = nacionalidad; }

    public String    getGeneroMusical()            
    { return generoMusical; }
    public void      setGeneroMusical(String generoMusical) 
    { this.generoMusical = generoMusical; }

    public String    getRedesSociales()              
    { return redesSociales; }
    public void      setRedesSociales(String redesSociales)  
    { this.redesSociales = redesSociales; }

    public LocalDate getFechaFirma()                        
    { return fechaFirma; }
    public void      setFechaFirma(LocalDate fechaFirma)    
    { this.fechaFirma = fechaFirma; }

    public String    getEstadoArtista()                     
    { return estadoArtista; }
    public void      setEstadoArtista(String estadoArtista) 
    { this.estadoArtista = estadoArtista; }

    public String    getTipoArtista()                      
    { return tipoArtista; }
    public void      setTipoArtista(String tipoArtista)    
    { this.tipoArtista = tipoArtista; }

    @Override
    public String toString() {
        return "Artista{id=" + idArtista
             + ", nombre='" + nombreArtista + '\''
             + ", nombreReal='" + nombreReal + '\''
             + ", generoMusical='" + generoMusical + '\''
             + ", nacionalidad='" + nacionalidad + '\''
             + ", estado='" + estadoArtista + '\''
             + ", tipo='" + tipoArtista + "'}";
    }
}
