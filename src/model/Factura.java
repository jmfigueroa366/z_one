package model;

import java.time.LocalDateTime;

public class Factura {
    private Integer       idFactura;
    private String        numeroFactura;
    private Integer       idSesion;
    private String        correoDestino;
    private double        montoTotal;
    private String        estado;
    private LocalDateTime fechaEmision;
    private LocalDateTime fechaEnvio;
    private String        rutaPdf;
    private String        observaciones;
    private String        nombreSesion;     // para mostrar (JOIN)
    private String        nombreArtista;    // para mostrar (JOIN)

    public Factura() {}

    public Integer getIdFactura()        { return idFactura; }
    public String getNumeroFactura()     { return numeroFactura; }
    public Integer getIdSesion()         { return idSesion; }
    public String getCorreoDestino()     { return correoDestino; }
    public double getMontoTotal()        { return montoTotal; }
    public String getEstado()            { return estado; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public LocalDateTime getFechaEnvio()   { return fechaEnvio; }
    public String getRutaPdf()           { return rutaPdf; }
    public String getObservaciones()     { return observaciones; }
    public String getNombreSesion()      { return nombreSesion; }
    public String getNombreArtista()     { return nombreArtista; }

    public void setIdFactura(Integer id)         { this.idFactura = id; }
    public void setNumeroFactura(String s)       { this.numeroFactura = s; }
    public void setIdSesion(Integer id)          { this.idSesion = id; }
    public void setCorreoDestino(String s)       { this.correoDestino = s; }
    public void setMontoTotal(double m)          { this.montoTotal = m; }
    public void setEstado(String s)              { this.estado = s; }
    public void setFechaEmision(LocalDateTime f) { this.fechaEmision = f; }
    public void setFechaEnvio(LocalDateTime f)   { this.fechaEnvio = f; }
    public void setRutaPdf(String s)             { this.rutaPdf = s; }
    public void setObservaciones(String s)       { this.observaciones = s; }
    public void setNombreSesion(String s)        { this.nombreSesion = s; }
    public void setNombreArtista(String s)       { this.nombreArtista = s; }
}