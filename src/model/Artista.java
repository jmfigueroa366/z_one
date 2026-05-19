/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author alvar
 */
public class Artista extends Persona {
    
    private String genero;

    public Artista() {
    }

    public Artista(String genero, int identificacion, String nombre, String correo, String telefono) {
        super(identificacion, nombre, correo, telefono);
        this.genero = genero;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
    
    @Override
    public String toString() {
        return "ID: " + getIdentificacion() + 
               ", Nombre: " + getNombre() + 
               ", Correo: " + getCorreo() + 
               ", Telefono: " + getTelefono() + 
               ", Genero: " + genero;
    }
    
}
