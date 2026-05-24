package model;

public class Usuario {
    private int     idUsuario;
    private String  username;
    private String  contrasena;
    private String  correo;
    private String  rol;
    private boolean activo;

    // Constructor completo
    public Usuario(int idUsuario, String username, String contrasena,
                   String correo, String rol, boolean activo) {
        this.idUsuario  = idUsuario;
        this.username   = username;
        this.contrasena = contrasena;
        this.correo     = correo;
        this.rol        = rol;
        this.activo     = activo;
    }

    // Constructor para registro (sin id todavía)
    public Usuario(String username, String contrasena,
                   String correo, String rol) {
        this(0, username, contrasena, correo, rol, true);
    }

    // Getters
    public int     getIdUsuario()  { return idUsuario; }
    public String  getUsername()   { return username; }
    public String  getNombre()     { return username; } // compatibilidad con LoginFrame
    public String  getContrasena() { return contrasena; }
    public String  getPasswordHash(){ return contrasena; }
    public String  getCorreo()     { return correo; }
    public String  getRol()        { return rol; }
    public boolean isActivo()      { return activo; }

    // Helpers de rol
    public boolean esAdmin()     { return "ADMIN".equalsIgnoreCase(rol); }
    public boolean esArtista()   { return "ARTISTA".equalsIgnoreCase(rol); }
    public boolean esProductor() { return "PRODUCTOR".equalsIgnoreCase(rol); }

    @Override
    public String toString() {
        return "Usuario{id=" + idUsuario + ", username='" + username
             + "', rol='" + rol + "'}";
    }
}
