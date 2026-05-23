package model;

/**
 * Persona — clase base para entidades con datos personales.
 *
 * Principio 1 (Nombres significativos): cada campo describe
 *   claramente qué representa.
 * Principio 2 (Responsabilidad única — SRP): solo almacena
 *   datos personales comunes; no tiene lógica de negocio.
 */
public class Persona {

    // ── Atributos ────────────────────────────────────────────────────
    private int    identificacion;
    private String nombre;
    private String correo;
    private String telefono;

    // ── Constructores ────────────────────────────────────────────────
    public Persona() {}

    public Persona(int identificacion, String nombre, String correo, String telefono) {
        this.identificacion = identificacion;
        this.nombre         = nombre;
        this.correo         = correo;
        this.telefono       = telefono;
    }

    // ── Getters / Setters ────────────────────────────────────────────
    public int    getIdentificacion()              { return identificacion; }
    public void   setIdentificacion(int id)        { this.identificacion = id; }

    public String getNombre()                      { return nombre; }
    public void   setNombre(String nombre)         { this.nombre = nombre; }

    public String getCorreo()                      { return correo; }
    public void   setCorreo(String correo)         { this.correo = correo; }

    public String getTelefono()                    { return telefono; }
    public void   setTelefono(String telefono)     { this.telefono = telefono; }

    @Override
    public String toString() {
        return "Persona{id=" + identificacion
             + ", nombre='" + nombre + '\''
             + ", correo='" + correo + '\''
             + ", telefono='" + telefono + "'}";
    }
}