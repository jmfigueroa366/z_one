/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author alvar
 */
public class Productor extends Persona{
    
    private String especialidad;
    private double tarifaHora;

    public Productor() {
    }

    public Productor(String especialidad, double tarifaHora, int identificacion, String nombre, String correo, String telefono) {
        super(identificacion, nombre, correo, telefono);
        this.especialidad = especialidad;
        this.tarifaHora = tarifaHora;
    }

    @Override
    public String toString() {
        return "ID: " + getIdentificacion() + 
               ", Nombre: " + getNombre() + 
               ", Correo: " + getCorreo() + 
               ", Telefono: " + getTelefono() + 
               ", Especialidad: " + especialidad;
    }
    
    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public double getTarifaHora() {
        return tarifaHora;
    }

    public void setTarifaHora(double tarifaHora) {
        this.tarifaHora = tarifaHora;
    }
    
    
    
}
