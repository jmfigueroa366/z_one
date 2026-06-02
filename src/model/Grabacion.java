
package model;

import java.time.LocalDateTime;

public class Grabacion {
    private Integer       idGrabacion;
    private Integer       idSesion;
    private String        nombreSesion;       // para mostrar en lista
    private String        nombreArchivo;
    private String        rutaArchivo;
    private int           duracionSegundos;
    private long          tamanoKb;
    private LocalDateTime fechaGrabacion;
    private String        observaciones;

    public Grabacion() {}

    public Integer       getIdGrabacion()       { return idGrabacion; }
    public Integer       getIdSesion()          { return idSesion; }
    public String        getNombreSesion()      { return nombreSesion; }
    public String        getNombreArchivo()     { return nombreArchivo; }
    public String        getRutaArchivo()       { return rutaArchivo; }
    public int           getDuracionSegundos()  { return duracionSegundos; }
    public long          getTamanoKb()          { return tamanoKb; }
    public LocalDateTime getFechaGrabacion()    { return fechaGrabacion; }
    public String        getObservaciones()     { return observaciones; }

    public void setIdGrabacion(Integer id)        { this.idGrabacion = id; }
    public void setIdSesion(Integer id)           { this.idSesion = id; }
    public void setNombreSesion(String s)         { this.nombreSesion = s; }
    public void setNombreArchivo(String s)        { this.nombreArchivo = s; }
    public void setRutaArchivo(String s)          { this.rutaArchivo = s; }
    public void setDuracionSegundos(int d)        { this.duracionSegundos = d; }
    public void setTamanoKb(long t)               { this.tamanoKb = t; }
    public void setFechaGrabacion(LocalDateTime f){ this.fechaGrabacion = f; }
    public void setObservaciones(String s)        { this.observaciones = s; }

    public String getDuracionFormato() {
        int m = duracionSegundos / 60;
        int s = duracionSegundos % 60;
        return String.format("%d:%02d", m, s);
    }

    @Override
    public String toString() {
        return nombreArchivo != null ? nombreArchivo : "Grabacion#" + idGrabacion;
    }
}