package model;

import java.time.LocalDate;

/** Entidad Sesion (alineada con SESION_GRABACIONES). */
public class Sesion {

    public static final String ESTADO_PROGRAMADA = "Programada";
    public static final String ESTADO_EN_CURSO   = "En curso";
    public static final String ESTADO_FINALIZADA = "Finalizada";
    public static final String ESTADO_CANCELADA  = "Cancelada";
    public static final String[] ESTADOS_VALIDOS = {
        ESTADO_PROGRAMADA, ESTADO_EN_CURSO, ESTADO_FINALIZADA, ESTADO_CANCELADA
    };

    private Integer   idSesion;
    private Artista   artista;
    private Productor productor;
    private Integer   idCabina;
    private String    nombreSesion;
    private LocalDate fecha;
    private String    horaInicio;
    private String    horaFin;
    private double    duracion;
    private String    estadoSesion;
    private String    observaciones;
    private Integer   idCancion;

    public Sesion() {}

    public Sesion(Integer idSesion, Artista artista, Productor productor,
                  Integer idCabina, String nombreSesion, LocalDate fecha,
                  String horaInicio, String horaFin, double duracion,
                  String estadoSesion, String observaciones) {
        this.idSesion      = idSesion;
        this.artista       = artista;
        this.productor     = productor;
        this.idCabina      = idCabina;
        this.nombreSesion  = nombreSesion;
        this.fecha         = fecha;
        this.horaInicio    = horaInicio;
        this.horaFin       = horaFin;
        this.duracion      = duracion;
        this.estadoSesion  = estadoSesion;
        this.observaciones = observaciones;
    }

    /** Costo total = duracion * tarifa del productor. */
    public double getCostoTotal() {
        if (productor == null) return 0;
        return duracion * productor.getTarifaHora();
    }

    // ── Getters / Setters ──
    public Integer   getIdSesion()      { return idSesion != null ? idSesion : 0; }
    public Artista   getArtista()       { return artista; }
    public Productor getProductor()     { return productor; }
    public Integer   getIdCabina()      { return idCabina; }
    public String    getNombreSesion()  { return nombreSesion; }
    public LocalDate getFecha()         { return fecha; }
    public String    getHoraInicio()    { return horaInicio; }
    public String    getHoraFin()       { return horaFin; }
    public double    getDuracion()      { return duracion; }
    public String    getEstadoSesion()  { return estadoSesion; }
    public String    getObservaciones() { return observaciones; }
    public Integer   getIdCancion()     { return idCancion; }

    public void setIdSesion(Integer id)         { this.idSesion = id; }
    public void setArtista(Artista a)            { this.artista = a; }
    public void setProductor(Productor p)        { this.productor = p; }
    public void setIdCabina(Integer id)          { this.idCabina = id; }
    public void setNombreSesion(String s)        { this.nombreSesion = s; }
    public void setFecha(LocalDate f)            { this.fecha = f; }
    public void setHoraInicio(String s)          { this.horaInicio = s; }
    public void setHoraFin(String s)             { this.horaFin = s; }
    public void setDuracion(double v)            { this.duracion = v; }
    public void setEstadoSesion(String s)        { this.estadoSesion = s; }
    public void setObservaciones(String s)       { this.observaciones = s; }
    public void setIdCancion(Integer id)         { this.idCancion = id; }
}