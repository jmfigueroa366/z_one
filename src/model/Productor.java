package model;

/**
 * Productor — modelo de dominio. Hereda de Persona los datos básicos
 * (identificacion, nombre, correo, telefono) y agrega especialidad y tarifa.
 */
public class Productor extends Persona {

    private String especialidad;
    private double tarifaHora;

    public Productor() {
    }

    public Productor(int identificacion, String nombre, String correo, String telefono,
                     String especialidad, double tarifaHora) {
        super(identificacion, nombre, correo, telefono);
        this.especialidad = especialidad;
        this.tarifaHora   = tarifaHora;
    }

    public String getEspecialidad()                  { return especialidad; }
    public void   setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public double getTarifaHora()                    { return tarifaHora; }
    public void   setTarifaHora(double tarifaHora)   { this.tarifaHora = tarifaHora; }

    @Override
    public String toString() {
        return "Productor{id=" + getIdentificacion()
             + ", nombre='" + getNombre() + '\''
             + ", especialidad='" + especialidad + '\''
             + ", tarifa=" + tarifaHora + "}";
    }
}