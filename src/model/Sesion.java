package model;

import java.time.LocalDate;

/**
 * Sesion — modelo que refleja exactamente la tabla SESION_GRABACION de Oracle.
 *
 * Columnas mapeadas:
 *   ID_SESION, ID_ARTISTA, ID_PRODUCTOR, ID_CABINA, NOMBRE_SESION,
 *   FECHA, HORA_INICIO, HORA_FIN, DURACION, ESTADO_SESION, OBSERVACIONES
 *
 * id_artista / id_productor se navegan como objetos (Artista / Productor).
 * id_cabina se mantiene como Integer porque aún no existe un modelo Cabina.
 * El costo NO es columna: se calcula (duración × tarifa), igual que fn_costo_sesion.
 */
public class Sesion {

    // ── Estados válidos ──────────────────────────────────────────────
    public static final String ESTADO_PROGRAMADA = "Programada";
    public static final String ESTADO_EN_CURSO   = "En curso";
    public static final String ESTADO_FINALIZADA = "Finalizada";
    public static final String ESTADO_CANCELADA  = "Cancelada";
    public static final String[] ESTADOS_VALIDOS = {
        ESTADO_PROGRAMADA, ESTADO_EN_CURSO, ESTADO_FINALIZADA, ESTADO_CANCELADA
    };

    // ── Atributos (uno por columna) ──────────────────────────────────
    private int       idSesion;
    private Artista   artista;        // id_artista
    private Productor productor;      // id_productor
    private Integer   idCabina;       // id_cabina (puede ser null)
    private String    nombreSesion;
    private LocalDate fecha;
    private String    horaInicio;
    private String    horaFin;
    private double    duracion;       // en horas
    private String    estadoSesion;
    private String    observaciones;

    // ── Constructor completo ─────────────────────────────────────────
    public Sesion(int idSesion, Artista artista, Productor productor,
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

    // ── Constructor mínimo (para formularios de alta) ────────────────
    public Sesion(Artista artista, Productor productor, Integer idCabina,
                  String nombreSesion, LocalDate fecha, String horaInicio,
                  String horaFin, double duracion, String estadoSesion,
                  String observaciones) {
        this(0, artista, productor, idCabina, nombreSesion, fecha,
             horaInicio, horaFin, duracion, estadoSesion, observaciones);
    }

    // ── Costo calculado (equivale a fn_costo_sesion) ─────────────────
    public double getCostoTotal() {
        if (productor == null) return 0;
        return duracion * productor.getTarifaHora();
    }

    // ── Getters y Setters ────────────────────────────────────────────
    public int       getIdSesion()      { return idSesion; }
    public void      setIdSesion(int v) { this.idSesion = v; }

    public Artista   getArtista()        { return artista; }
    public void      setArtista(Artista v){ this.artista = v; }

    public Productor getProductor()         { return productor; }
    public void      setProductor(Productor v){ this.productor = v; }

    public Integer   getIdCabina()        { return idCabina; }
    public void      setIdCabina(Integer v){ this.idCabina = v; }

    public String    getNombreSesion()       { return nombreSesion; }
    public void      setNombreSesion(String v){ this.nombreSesion = v; }

    public LocalDate getFecha()            { return fecha; }
    public void      setFecha(LocalDate v) { this.fecha = v; }

    public String    getHoraInicio()       { return horaInicio; }
    public void      setHoraInicio(String v){ this.horaInicio = v; }

    public String    getHoraFin()        { return horaFin; }
    public void      setHoraFin(String v){ this.horaFin = v; }

    public double    getDuracion()       { return duracion; }
    public void      setDuracion(double v){ this.duracion = v; }

    public String    getEstadoSesion()       { return estadoSesion; }
    public void      setEstadoSesion(String v){ this.estadoSesion = v; }

    public String    getObservaciones()       { return observaciones; }
    public void      setObservaciones(String v){ this.observaciones = v; }

    @Override
    public String toString() {
        return "Sesion{id=" + idSesion
             + ", nombre='" + nombreSesion + '\''
             + ", fecha=" + fecha
             + ", duracion=" + duracion
             + ", estado='" + estadoSesion + "'}";
    }
}