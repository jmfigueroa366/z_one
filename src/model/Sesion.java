/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;

/**
 *
 * @author alvar
 */
public class Sesion {
    
    private int identificador;
    private LocalDate fechaRealizacion;
    private double duracionHoras;
    private Artista artista;
    private Productor productor;
    private double costoTotal;

    public Sesion() {
    }

    public Sesion(int identificador, LocalDate fechaRealizacion, double duracionHoras, Artista artista, Productor productor, double costoTotal) {
        this.identificador = identificador;
        this.fechaRealizacion = fechaRealizacion;
        this.duracionHoras = duracionHoras;
        this.artista = artista;
        this.productor = productor;
        this.costoTotal = costoTotal;
    }

    public double calcularCosto(){
        costoTotal=duracionHoras*productor.getTarifaHora();
        return costoTotal;
    }
    
    public int getIdentificador() {
        return identificador;
    }

    public void setIdentificador(int identificador) {
        this.identificador = identificador;
    }

    public LocalDate getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(LocalDate fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    public double getDuracionHoras() {
        return duracionHoras;
    }

    public void setDuracionHoras(double duracionHoras) {
        this.duracionHoras = duracionHoras;
    }

    public Artista getArtista() {
        return artista;
    }

    public void setArtista(Artista artista) {
        this.artista = artista;
    }

    public Productor getProductor() {
        return productor;
    }

    public void setProductor(Productor productor) {
        this.productor = productor;
    }

    public double getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(double costoTotal) {
        this.costoTotal = costoTotal;
    }

    @Override
    public String toString() {
        return "Sesion{" + "identificador=" + identificador + ", fechaRealizacion=" + fechaRealizacion + ", duracionHoras=" + duracionHoras + ", artista=" + artista + ", productor=" + productor + ", costoTotal=" + costoTotal + '}';
    }
    
    
    
}
