package model;

import java.time.LocalDate;
import java.time.LocalTime;

/** Entidad Evento alineada con EVENTOS_AGENDADOS. */
public class Evento {

    private Integer   idEvento;
    private String    tipoEvento;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String    descripcion;
    private Integer   idArtista;
    private String    nombreArtista;
    private Integer   idProductor;
    private String    nombreProductor;
    private Integer   idFormato;
    private Integer   idTipoEvento;
    private String    nombreTipoEvento;

    public Evento() {}

    public Integer   getIdEvento()         { return idEvento; }
    public String    getTipoEvento()       { return tipoEvento; }
    public LocalDate getFecha()            { return fecha; }
    public LocalTime getHoraInicio()       { return horaInicio; }
    public LocalTime getHoraFin()          { return horaFin; }
    public String    getDescripcion()      { return descripcion; }
    public Integer   getIdArtista()        { return idArtista; }
    public String    getNombreArtista()    { return nombreArtista; }
    public Integer   getIdProductor()      { return idProductor; }
    public String    getNombreProductor()  { return nombreProductor; }
    public Integer   getIdFormato()        { return idFormato; }
    public Integer   getIdTipoEvento()     { return idTipoEvento; }
    public String    getNombreTipoEvento() { return nombreTipoEvento; }

    public void setIdEvento(Integer id)         { this.idEvento = id; }
    public void setTipoEvento(String s)         { this.tipoEvento = s; }
    public void setFecha(LocalDate f)           { this.fecha = f; }
    public void setHoraInicio(LocalTime t)      { this.horaInicio = t; }
    public void setHoraFin(LocalTime t)         { this.horaFin = t; }
    public void setDescripcion(String s)        { this.descripcion = s; }
    public void setIdArtista(Integer id)        { this.idArtista = id; }
    public void setNombreArtista(String s)      { this.nombreArtista = s; }
    public void setIdProductor(Integer id)      { this.idProductor = id; }
    public void setNombreProductor(String s)    { this.nombreProductor = s; }
    public void setIdFormato(Integer id)        { this.idFormato = id; }
    public void setIdTipoEvento(Integer id)     { this.idTipoEvento = id; }
    public void setNombreTipoEvento(String s)   { this.nombreTipoEvento = s; }
}