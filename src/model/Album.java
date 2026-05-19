/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author alvar
 */
public class Album {
    
    private int identificador;
    private String titulo;
    private int año;
    private Artista artista;
    private List<Cancion> canciones;

    public Album() {
    }

    public Album(int identificador, String titulo, int año, Artista artista, List<Cancion> canciones) {
        this.identificador = identificador;
        this.titulo = titulo;
        this.año = año;
        this.artista = artista;
        this.canciones = canciones;
    }

    public void agregarCancion(Cancion cancion){
        canciones.add(cancion);
    }
    
    public void removerCancion(Cancion cancion){
        canciones.remove(cancion);
    }
    
    public int getIdentificador() {
        return identificador;
    }

    public void setIdentificador(int identificador) {
        this.identificador = identificador;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public Artista getArtista() {
        return artista;
    }

    public void setArtista(Artista artista) {
        this.artista = artista;
    }

    public List<Cancion> getCanciones() {
        return canciones;
    }

    public void setCanciones(List<Cancion> canciones) {
        this.canciones = canciones;
    }
    
    
    
}
