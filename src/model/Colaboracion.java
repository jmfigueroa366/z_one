package model;

import java.time.LocalDate;

public class Colaboracion {

    private Integer   idColaboracion;
    private Integer   idCancion;
    private String    nombreColaborador;
    private LocalDate fechaColaboracion;
    private String    nombreCancion;

    public Colaboracion() {}

    public Colaboracion(Integer idColaboracion, Integer idCancion,
                        String nombreColaborador, LocalDate fechaColaboracion,
                        String nombreCancion) {
        this.idColaboracion    = idColaboracion;
        this.idCancion         = idCancion;
        this.nombreColaborador = nombreColaborador;
        this.fechaColaboracion = fechaColaboracion;
        this.nombreCancion     = nombreCancion;
    }

    public Integer   getIdColaboracion()    { return idColaboracion; }
    public Integer   getIdCancion()         { return idCancion; }
    public String    getNombreColaborador() { return nombreColaborador; }
    public LocalDate getFechaColaboracion() { return fechaColaboracion; }
    public String    getNombreCancion()     { return nombreCancion; }

    public void setIdColaboracion(Integer id)       { this.idColaboracion = id; }
    public void setIdCancion(Integer id)            { this.idCancion = id; }
    public void setNombreColaborador(String s)      { this.nombreColaborador = s; }
    public void setFechaColaboracion(LocalDate f)   { this.fechaColaboracion = f; }
    public void setNombreCancion(String s)          { this.nombreCancion = s; }
}