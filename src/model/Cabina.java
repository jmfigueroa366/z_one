package model;


/** Entidad Cabina alineada con la tabla CABINAS. */
public class Cabina {

    private Integer idCabina;
    private String  nombreCabina;
    private Integer idEstadoCabina;
    private String  nombreEstado;

    public Cabina() {}

    public Cabina(Integer idCabina, String nombreCabina) {
        this.idCabina     = idCabina;
        this.nombreCabina = nombreCabina;
    }

    public Cabina(Integer idCabina, String nombreCabina,
                  Integer idEstadoCabina, String nombreEstado) {
        this.idCabina       = idCabina;
        this.nombreCabina   = nombreCabina;
        this.idEstadoCabina = idEstadoCabina;
        this.nombreEstado   = nombreEstado;
    }

    public Integer getIdCabina()        { return idCabina; }
    public String  getNombreCabina()    { return nombreCabina; }
    public Integer getIdEstadoCabina()  { return idEstadoCabina; }
    public String  getNombreEstado()    { return nombreEstado; }

    public void setIdCabina(Integer id)        { this.idCabina = id; }
    public void setNombreCabina(String s)      { this.nombreCabina = s; }
    public void setIdEstadoCabina(Integer id)  { this.idEstadoCabina = id; }
    public void setNombreEstado(String s)      { this.nombreEstado = s; }

    @Override
    public String toString() { return nombreCabina != null ? nombreCabina : "Cabina#" + idCabina; }
}