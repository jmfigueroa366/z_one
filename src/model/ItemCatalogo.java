package model;

/** Item generico de catalogo (id + nombre + descripcion). */
public class ItemCatalogo {

    private Integer id;
    private String  nombre;
    private String  descripcion;

    public ItemCatalogo() {}

    public ItemCatalogo(Integer id, String nombre) {
        this(id, nombre, null);
    }

    public ItemCatalogo(Integer id, String nombre, String descripcion) {
        this.id          = id;
        this.nombre      = nombre;
        this.descripcion = descripcion;
    }

    public Integer getId()           { return id; }
    public String  getNombre()       { return nombre; }
    public String  getDescripcion()  { return descripcion; }

    public void setId(Integer id)            { this.id = id; }
    public void setNombre(String s)          { this.nombre = s; }
    public void setDescripcion(String s)     { this.descripcion = s; }

    @Override
    public String toString() { return nombre != null ? nombre : "#" + id; }
}