package model;

import java.time.LocalDate;

/** Entidad Colaboracion alineada con COLABORACIONES. */
public class Colaboracion {

    private Integer   idColaboracion;
    private LocalDate fechaColaboracion;
    private String    colaboracionArtista;
    private Integer   idCancion;
    private String    nombreCancion;

    public Colaboracion() {}

    public Colaboracion(Integer id, LocalDate fecha, String colaborador,
                        Integer idCancion, String nombreCancion) {
        this.idColaboracion      = id;
        this.fechaColaboracion   = fecha;
        this.colaboracionArtista = colaborador;
        this.idCancion           = idCancion;
        this.nombreCancion       = nombreCancion;
    }

    public Integer   getIdColaboracion()      { return idColaboracion; }
    public LocalDate getFechaColaboracion()   { return fechaColaboracion; }
    public String    getColaboracionArtista() { return colaboracionArtista; }
    public Integer   getIdCancion()           { return idCancion; }
    public String    getNombreCancion()       { return nombreCancion; }

    public void setIdColaboracion(Integer id)        { this.idColaboracion = id; }
    public void setFechaColaboracion(LocalDate f)    { this.fechaColaboracion = f; }
    public void setColaboracionArtista(String s)     { this.colaboracionArtista = s; }
    public void setIdCancion(Integer id)             { this.idCancion = id; }
    public void setNombreCancion(String s)           { this.nombreCancion = s; }
}