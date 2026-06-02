package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Sesion {

    // ── Campos de SESION_GRABACION ──
    private Integer       idGrabacion;
    private Integer       idCancion;
    private Integer       idFase;
    private Integer       idArtista;
    private String        nombreArtista;
    private Integer       idProductor;
    private String        nombreProductor;
    private Integer       idCabina;
    private Integer       idEstadoGrabacion;
    private String        nombreSesion;
    private Integer       numeroSesion;
    private LocalDate     fechaGrabacion;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private String        notas;

    public Sesion() {}

    public Sesion(Integer idGrabacion, Integer idCancion, Integer idFase,
                  Integer idArtista, String nombreArtista,
                  Integer idProductor, String nombreProductor,
                  Integer idCabina, Integer idEstadoGrabacion,
                  String nombreSesion, Integer numeroSesion,
                  LocalDate fechaGrabacion, LocalDateTime horaInicio,
                  LocalDateTime horaFin, String notas) {
        this.idGrabacion       = idGrabacion;
        this.idCancion         = idCancion;
        this.idFase            = idFase;
        this.idArtista         = idArtista;
        this.nombreArtista     = nombreArtista;
        this.idProductor       = idProductor;
        this.nombreProductor   = nombreProductor;
        this.idCabina          = idCabina;
        this.idEstadoGrabacion = idEstadoGrabacion;
        this.nombreSesion      = nombreSesion;
        this.numeroSesion      = numeroSesion;
        this.fechaGrabacion    = fechaGrabacion;
        this.horaInicio        = horaInicio;
        this.horaFin           = horaFin;
        this.notas             = notas;
    }

    // ── Getters ──
    public Integer       getIdGrabacion()       { return idGrabacion; }
    public Integer       getIdCancion()         { return idCancion; }
    public Integer       getIdFase()            { return idFase; }
    public Integer       getIdArtista()         { return idArtista; }
    public String        getNombreArtista()     { return nombreArtista; }
    public Integer       getIdProductor()       { return idProductor; }
    public String        getNombreProductor()   { return nombreProductor; }
    public Integer       getIdCabina()          { return idCabina; }
    public Integer       getIdEstadoGrabacion() { return idEstadoGrabacion; }
    public String        getNombreSesion()      { return nombreSesion; }
    public Integer       getNumeroSesion()      { return numeroSesion; }
    public LocalDate     getFechaGrabacion()    { return fechaGrabacion; }
    public LocalDateTime getHoraInicio()        { return horaInicio; }
    public LocalDateTime getHoraFin()           { return horaFin; }
    public String        getNotas()             { return notas; }

    // ── Setters ──
    public void setIdGrabacion(Integer id)          { this.idGrabacion = id; }
    public void setIdCancion(Integer id)            { this.idCancion = id; }
    public void setIdFase(Integer id)               { this.idFase = id; }
    public void setIdArtista(Integer id)            { this.idArtista = id; }
    public void setNombreArtista(String s)          { this.nombreArtista = s; }
    public void setIdProductor(Integer id)          { this.idProductor = id; }
    public void setNombreProductor(String s)        { this.nombreProductor = s; }
    public void setIdCabina(Integer id)             { this.idCabina = id; }
    public void setIdEstadoGrabacion(Integer id)    { this.idEstadoGrabacion = id; }
    public void setNombreSesion(String s)           { this.nombreSesion = s; }
    public void setNumeroSesion(Integer n)          { this.numeroSesion = n; }
    public void setFechaGrabacion(LocalDate f)      { this.fechaGrabacion = f; }
    public void setHoraInicio(LocalDateTime t)      { this.horaInicio = t; }
    public void setHoraFin(LocalDateTime t)         { this.horaFin = t; }
    public void setNotas(String s)                  { this.notas = s; }
}