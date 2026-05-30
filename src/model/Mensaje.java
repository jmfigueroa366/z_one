package model;

public class Mensaje {
    private String rol;   // "user" o "model"
    private String texto;

    public Mensaje(String rol, String texto) {
        this.rol = rol;
        this.texto = texto;
    }

    public String getRol()   { return rol; }
    public String getTexto() { return texto; }
}