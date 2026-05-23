package model;

public class Artista extends Persona {

    // ── Atributos propios del artista ────────────────────────────────
    private String genero;
    private String pais;
    private int    cantidadCanciones;
    private String estado;

    // ── Estados válidos (evita magic strings dispersos) ──────────────
    public static final String ESTADO_ACTIVO   = "Activo";
    public static final String ESTADO_INACTIVO = "Inactivo";
    public static final String ESTADO_EN_GIRA  = "En gira";
    public static final String ESTADO_HIATUS   = "Hiatus";
    public static final String[] ESTADOS_VALIDOS = {
        ESTADO_ACTIVO, ESTADO_INACTIVO, ESTADO_EN_GIRA, ESTADO_HIATUS
    };

    // ── Constructor (el que usan Service y DAO) ──────────────────────
    public Artista(int identificacion, String nombre, String correo, String telefono,
                   String genero, String pais, int cantidadCanciones, String estado) {
        super(identificacion, nombre, correo, telefono);
        this.genero            = genero;
        this.pais              = pais;
        this.cantidadCanciones = cantidadCanciones;
        this.estado            = estado;
    }

    // ── Getters / Setters ────────────────────────────────────────────
    public String getGenero()                        { return genero; }
    public void   setGenero(String genero)           { this.genero = genero; }
    public String getPais()                          { return pais; }
    public void   setPais(String pais)               { this.pais = pais; }
    public int    getCantidadCanciones()             { return cantidadCanciones; }
    public void   setCantidadCanciones(int cantidad) { this.cantidadCanciones = cantidad; }
    public String getEstado()                        { return estado; }
    public void   setEstado(String estado)           { this.estado = estado; }

    @Override
    public String toString() {
        return "Artista{id=" + getIdentificacion()
             + ", nombre='" + getNombre() + '\''
             + ", genero='" + genero + '\''
             + ", pais='" + pais + '\''
             + ", canciones=" + cantidadCanciones
             + ", estado='" + estado + "'}";
    }
}
