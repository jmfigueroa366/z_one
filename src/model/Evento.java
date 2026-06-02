package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Evento {

    private Integer       idEvento;
    private Integer       idTipoEvento;
    private String        nombreTipoEvento;
    private Integer       idArtista;
    private String        nombreArtista;
    private Integer       idProductor;
    private String        nombreProductor;
    private Integer       idFormato;
    private LocalDate     fecha;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private String        descripcion;

    public Evento() {}

    public Integer       getIdEvento()         { return idEvento; }
    public Integer       getIdTipoEvento()     { return idTipoEvento; }
    public String        getNombreTipoEvento() { return nombreTipoEvento; }
    public Integer       getIdArtista()        { return idArtista; }
    public String        getNombreArtista()    { return nombreArtista; }
    public Integer       getIdProductor()      { return idProductor; }
    public String        getNombreProductor()  { return nombreProductor; }
    public Integer       getIdFormato()        { return idFormato; }
    public LocalDate     getFecha()            { return fecha; }
    public LocalDateTime getHoraInicio()       { return horaInicio; }
    public LocalDateTime getHoraFin()          { return horaFin; }
    public String        getDescripcion()      { return descripcion; }

    public void setIdEvento(Integer id)           { this.idEvento = id; }
    public void setIdTipoEvento(Integer id)       { this.idTipoEvento = id; }
    public void setNombreTipoEvento(String s)     { this.nombreTipoEvento = s; }
    public void setIdArtista(Integer id)          { this.idArtista = id; }
    public void setNombreArtista(String s)        { this.nombreArtista = s; }
    public void setIdProductor(Integer id)        { this.idProductor = id; }
    public void setNombreProductor(String s)      { this.nombreProductor = s; }
    public void setIdFormato(Integer id)          { this.idFormato = id; }
    public void setFecha(LocalDate f)             { this.fecha = f; }
    public void setHoraInicio(LocalDateTime t)    { this.horaInicio = t; }
    public void setHoraFin(LocalDateTime t)       { this.horaFin = t; }
    public void setDescripcion(String s)          { this.descripcion = s; }
}