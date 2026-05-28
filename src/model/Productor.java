package model;


public class Productor {

    private int    idProductor;
    private int    idUsuario;       // FK a usuarios (0 si no tiene cuenta)
    private String nombre;
    private String fechaNacimiento;
    private String genero;
    private String nacionalidad;
    private String especialidad;
    private String experiencia;
    private double tarifaHora;
    private String estadoProductor;
    private String fechaFirma;

    // Constructor completo — para mapear desde BD
    public Productor(int idProductor, int idUsuario, String nombre,
                     String fechaNacimiento, String genero, String nacionalidad,
                     String especialidad, String experiencia, double tarifaHora,
                     String estadoProductor, String fechaFirma) {
        this.idProductor    = idProductor;
        this.idUsuario      = idUsuario;
        this.nombre         = nombre;
        this.fechaNacimiento= fechaNacimiento;
        this.genero         = genero;
        this.nacionalidad   = nacionalidad;
        this.especialidad   = especialidad;
        this.experiencia    = experiencia;
        this.tarifaHora     = tarifaHora;
        this.estadoProductor= estadoProductor;
        this.fechaFirma     = fechaFirma;
    }

    // Constructor para registro nuevo desde la UI
    public Productor(String nombre, String especialidad,
                     String experiencia, double tarifaHora,
                     String nacionalidad) {
        this(0, 0, nombre, null, null, nacionalidad,
             especialidad, experiencia, tarifaHora, "Disponible", null);
    }

    // Constructor vacío
    public Productor() {}

    // Constructor de compatibilidad con código viejo
    // formProductor usaba: Productor(int, String, String, String, String, double)
    public Productor(int idProductor, String nombre, String correo,
                     String telefono, String especialidad, double tarifaHora) {
        this(idProductor, 0, nombre, null, null, null,
             especialidad, null, tarifaHora, "Disponible", null);
    }

    // ── Getters ───────────────────────────────────────────────────────
    public int    getIdProductor()     { return idProductor; }
    public int    getIdUsuario()       { return idUsuario; }
    public String getNombre()          { return nombre; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getGenero()          { return genero; }
    public String getNacionalidad()    { return nacionalidad; }
    public String getEspecialidad()    { return especialidad; }
    public String getExperiencia()     { return experiencia; }
    public double getTarifaHora()      { return tarifaHora; }
    public String getEstadoProductor() { return estadoProductor; }
    public String getFechaFirma()      { return fechaFirma; }

    // Alias para compatibilidad con código viejo
    public int    getIdentificacion()  { return idProductor; }

    // ── Setters ───────────────────────────────────────────────────────
    public void setIdProductor(int id)          { this.idProductor = id; }
    public void setIdentificacion(int id)       { this.idProductor = id; }
    public void setIdUsuario(int id)            { this.idUsuario = id; }
    public void setNombre(String n)             { this.nombre = n; }
    public void setEspecialidad(String e)       { this.especialidad = e; }
    public void setExperiencia(String e)        { this.experiencia = e; }
    public void setTarifaHora(double t)         { this.tarifaHora = t; }
    public void setNacionalidad(String n)       { this.nacionalidad = n; }
    public void setEstadoProductor(String e)    { this.estadoProductor = e; }
    public void setFechaNacimiento(String f)    { this.fechaNacimiento = f; }
    public void setGenero(String g)             { this.genero = g; }
    public void setFechaFirma(String f)         { this.fechaFirma = f; }

    @Override
    public String toString() {
        return "Productor{id=" + idProductor
             + ", nombre='" + nombre + "'"
             + ", especialidad='" + especialidad + "'"
             + ", tarifa=" + tarifaHora + "}";
    }
}