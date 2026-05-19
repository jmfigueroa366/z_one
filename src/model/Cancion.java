/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author alvar
 */
public class Cancion {
    
   private int identificador;
   private String titulo;
   private double duracionMinutos;
   private String genero;
   private Artista artista;

    public Cancion() {
    }

    public Cancion(int identificador, String titulo, double duracionMinutos, String genero, Artista artista) {
        this.identificador = identificador;
        this.titulo = titulo;
        this.duracionMinutos = duracionMinutos;
        this.genero = genero;
        this.artista = artista;
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

    public double getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(double duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Artista getArtista() {
        return artista;
    }

    public void setArtista(Artista artista) {
        this.artista = artista;
    }

    @Override
    public String toString() {
        return "Cancion{" + "identificador=" + identificador + ", titulo=" + titulo + ", duracionMinutos=" + duracionMinutos + ", genero=" + genero + ", artista=" + artista + '}';
    }
    
}
