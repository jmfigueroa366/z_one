package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Entidad Cancion alineada con la tabla CANCIONES. */
public class Cancion {
    private Integer       idCancion;
    private String        titulo;
    private Integer       bpm;
    private LocalDateTime fechaComposicion;
    private LocalDate     fechaCompilacion;
    private Integer       idProductor;
    private String        nombreProductor;
    private Integer       idFormato;
    private String        nombreFormato;
    private Integer       idIdioma;
    private String        nombreIdioma;
    private Integer       idVersionCancion;
    private Integer       idEstadoCancion;
    private String        nombreEstado;
    private Integer       idGeneroMusical;
    private String        nombreGenero;
    private String        rutaArchivo;     // ⬅ NUEVO: ruta del .wav/.mp3 para el reproductor

    public Cancion() {}

    public Cancion(Integer idCancion, String titulo, Integer bpm,
                   LocalDateTime fechaComposicion, LocalDate fechaCompilacion) {
        this.idCancion        = idCancion;
        this.titulo           = titulo;
        this.bpm              = bpm;
        this.fechaComposicion = fechaComposicion;
        this.fechaCompilacion = fechaCompilacion;
    }

    // ── Getters ──
    public Integer       getIdCancion()        { return idCancion; }
    public String        getTitulo()           { return titulo; }
    public Integer       getBpm()              { return bpm; }
    public LocalDateTime getFechaComposicion() { return fechaComposicion; }
    public LocalDate     getFechaCompilacion() { return fechaCompilacion; }
    public Integer       getIdProductor()      { return idProductor; }
    public String        getNombreProductor()  { return nombreProductor; }
    public Integer       getIdFormato()        { return idFormato; }
    public String        getNombreFormato()    { return nombreFormato; }
    public Integer       getIdIdioma()         { return idIdioma; }
    public String        getNombreIdioma()     { return nombreIdioma; }
    public Integer       getIdVersionCancion() { return idVersionCancion; }
    public Integer       getIdEstadoCancion()  { return idEstadoCancion; }
    public String        getNombreEstado()     { return nombreEstado; }
    public Integer       getIdGeneroMusical()  { return idGeneroMusical; }
    public String        getNombreGenero()     { return nombreGenero; }
    public String        getRutaArchivo()      { return rutaArchivo; }      // ⬅ NUEVO

    // ── Setters ──
    public void setIdCancion(Integer id)             { this.idCancion = id; }
    public void setTitulo(String s)                   { this.titulo = s; }
    public void setBpm(Integer b)                     { this.bpm = b; }
    public void setFechaComposicion(LocalDateTime f)  { this.fechaComposicion = f; }
    public void setFechaCompilacion(LocalDate f)      { this.fechaCompilacion = f; }
    public void setIdProductor(Integer id)            { this.idProductor = id; }
    public void setNombreProductor(String s)          { this.nombreProductor = s; }
    public void setIdFormato(Integer id)              { this.idFormato = id; }
    public void setNombreFormato(String s)            { this.nombreFormato = s; }
    public void setIdIdioma(Integer id)               { this.idIdioma = id; }
    public void setNombreIdioma(String s)             { this.nombreIdioma = s; }
    public void setIdVersionCancion(Integer id)       { this.idVersionCancion = id; }
    public void setIdEstadoCancion(Integer id)        { this.idEstadoCancion = id; }
    public void setNombreEstado(String s)             { this.nombreEstado = s; }
    public void setIdGeneroMusical(Integer id)        { this.idGeneroMusical = id; }
    public void setNombreGenero(String s)             { this.nombreGenero = s; }
    public void setRutaArchivo(String s)              { this.rutaArchivo = s; }    // ⬅ NUEVO

    /** Útil para saber si la canción tiene audio disponible para reproducir. */
    public boolean tieneAudio() {
        return rutaArchivo != null && !rutaArchivo.isBlank();
    }

    @Override
    public String toString() { return titulo != null ? titulo : "Cancion#" + idCancion; }
}