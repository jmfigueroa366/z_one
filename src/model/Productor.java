package model;

import java.time.LocalDate;

/** Entidad Productor alineada con la tabla PRODUCTORES. */
public class Productor {

    private Integer   idProductor;
    private Integer   idUsuario;
    private String    nombre;
    private String    correo;
    private String    telefono;
    private String    especialidad;
    private double    tarifaHora;
    private LocalDate fechaFirma;
    private LocalDate fechaNacimiento;
    private String    nacionalidad;
    private String    generoPersona;
    private String    generoMusical;
    private String    estado;
    private String    numIdentificacion;

    public Productor() {}

    public Productor(Integer idProductor, String nombre, String correo,
                     String telefono, String especialidad, double tarifaHora) {
        this.idProductor  = idProductor;
        this.nombre       = nombre;
        this.correo       = correo;
        this.telefono     = telefono;
        this.especialidad = especialidad;
        this.tarifaHora   = tarifaHora;
    }

    // ── Getters / Setters ──
    public Integer   getIdProductor()       { return idProductor; }
    public Integer   getIdUsuario()         { return idUsuario; }
    public String    getNombre()            { return nombre; }
    public String    getCorreo()            { return correo; }
    public String    getTelefono()          { return telefono; }
    public String    getEspecialidad()      { return especialidad; }
    public double    getTarifaHora()        { return tarifaHora; }
    public LocalDate getFechaFirma()        { return fechaFirma; }
    public LocalDate getFechaNacimiento()   { return fechaNacimiento; }
    public String    getNacionalidad()      { return nacionalidad; }
    public String    getGeneroPersona()     { return generoPersona; }
    public String    getGeneroMusical()     { return generoMusical; }
    public String    getEstado()            { return estado; }
    public String    getNumIdentificacion() { return numIdentificacion; }

    public void setIdProductor(Integer id)         { this.idProductor = id; }
    public void setIdUsuario(Integer id)            { this.idUsuario = id; }
    public void setNombre(String s)                 { this.nombre = s; }
    public void setCorreo(String s)                 { this.correo = s; }
    public void setTelefono(String s)               { this.telefono = s; }
    public void setEspecialidad(String s)           { this.especialidad = s; }
    public void setTarifaHora(double v)             { this.tarifaHora = v; }
    public void setFechaFirma(LocalDate f)          { this.fechaFirma = f; }
    public void setFechaNacimiento(LocalDate f)     { this.fechaNacimiento = f; }
    public void setNacionalidad(String s)           { this.nacionalidad = s; }
    public void setGeneroPersona(String s)          { this.generoPersona = s; }
    public void setGeneroMusical(String s)          { this.generoMusical = s; }
    public void setEstado(String s)                 { this.estado = s; }
    public void setNumIdentificacion(String s)      { this.numIdentificacion = s; }

    @Override
    public String toString() {
        return nombre != null ? nombre : "Productor#" + idProductor;
    }
}