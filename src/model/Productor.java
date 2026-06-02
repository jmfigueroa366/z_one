package model;

import java.time.LocalDate;

/** Entidad Productor alineada con la tabla PRODUCTORA_BD.PRODUCTOR */
public class Productor {

    // ── Atributos — solo los que existen en la tabla ─────────────────
    private Integer   idProductor;
    private String    numIdentificacion;
    private String    nombre;
    private LocalDate fechaNacimiento;
    private LocalDate fechaFirma;
    private String    especialidad;
    private String    generoPersona;
    private String    nacionalidad;
    private String    generoMusical;
    private String    estado;

    // Campos que NO están en la tabla PRODUCTOR pero pueden usarse en UI
    // (no se persisten en BD)
    private Integer   idUsuario;
    private String    correo;
    private String    telefono;
    private double    tarifaHora;

    // ── Constructores ────────────────────────────────────────────────
    public Productor() {}

    public Productor(Integer idProductor, String nombre, String especialidad) {
        this.idProductor  = idProductor;
        this.nombre       = nombre;
        this.especialidad = especialidad;
    }

    // ── Getters ──────────────────────────────────────────────────────
    public Integer   getIdProductor()       { return idProductor; }
    public String    getNumIdentificacion() { return numIdentificacion; }
    public String    getNombre()            { return nombre; }
    public LocalDate getFechaNacimiento()   { return fechaNacimiento; }
    public LocalDate getFechaFirma()        { return fechaFirma; }
    public String    getEspecialidad()      { return especialidad; }
    public String    getGeneroPersona()     { return generoPersona; }
    public String    getNacionalidad()      { return nacionalidad; }
    public String    getGeneroMusical()     { return generoMusical; }
    public String    getEstado()            { return estado; }
    public Integer   getIdUsuario()         { return idUsuario; }
    public String    getCorreo()            { return correo; }
    public String    getTelefono()          { return telefono; }
    public double    getTarifaHora()        { return tarifaHora; }

    // ── Setters ──────────────────────────────────────────────────────
    public void setIdProductor(Integer id)          { this.idProductor = id; }
    public void setNumIdentificacion(String s)      { this.numIdentificacion = s; }
    public void setNombre(String s)                 { this.nombre = s; }
    public void setFechaNacimiento(LocalDate f)     { this.fechaNacimiento = f; }
    public void setFechaFirma(LocalDate f)          { this.fechaFirma = f; }
    public void setEspecialidad(String s)           { this.especialidad = s; }
    public void setGeneroPersona(String s)          { this.generoPersona = s; }
    public void setNacionalidad(String s)           { this.nacionalidad = s; }
    public void setGeneroMusical(String s)          { this.generoMusical = s; }
    public void setEstado(String s)                 { this.estado = s; }
    public void setIdUsuario(Integer id)            { this.idUsuario = id; }
    public void setCorreo(String s)                 { this.correo = s; }
    public void setTelefono(String s)               { this.telefono = s; }
    public void setTarifaHora(double v)             { this.tarifaHora = v; }

    @Override
    public String toString() {
        return nombre != null ? nombre : "Productor#" + idProductor;
    }
}