package model;

public class Usuario {

    private String  idUsuario;
    private String  username;
    private String  passwordHash;
    private String  nombre;
    private String  correo;
    private String  rol;
    private boolean activo;

    public Usuario(String idUsuario, String username, String passwordHash,
                   String nombre, String correo, String rol, boolean activo) {
        this.idUsuario    = idUsuario;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.nombre       = nombre;
        this.correo       = correo;
        this.rol          = rol;
        this.activo       = activo;
    }

    public Usuario(String username, String passwordHash,
                   String nombre, String correo, String rol) {
        this(null, username, passwordHash, nombre, correo, rol, true);
    }

    // Getters
    public String  getIdUsuario()    { return idUsuario; }
    public String  getUsername()     { return username; }
    public String  getPasswordHash() { return passwordHash; }
    public String  getNombre()       { return nombre; }
    public String  getCorreo()       { return correo; }
    public String  getRol()          { return rol; }
    public boolean isActivo()        { return activo; }

    // Setters
    public void setIdUsuario(String idUsuario)   { this.idUsuario = idUsuario; }
    public void setPasswordHash(String hash)     { this.passwordHash = hash; }
    public void setActivo(boolean activo)        { this.activo = activo; }

    public void setUsername(String username) {
        if (username == null || username.trim().length() < 3)
            throw new IllegalArgumentException("El usuario debe tener al menos 3 caracteres.");
        this.username = username.trim();
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre no puede estar vacio.");
        this.nombre = nombre.trim();
    }

    public void setCorreo(String correo) {
        if (correo == null || !correo.contains("@"))
            throw new IllegalArgumentException("El correo debe contener @.");
        this.correo = correo.trim();
    }

    public void setRol(String rol) {
        String[] validos = {"ADMIN", "ARTISTA", "PRODUCTOR", "USUARIO"};
        for (String r : validos)
            if (r.equals(rol)) { this.rol = rol; return; }
        throw new IllegalArgumentException("Rol invalido. Usar: ADMIN, ARTISTA, PRODUCTOR o USUARIO.");
    }

    @Override
    public String toString() {
        return "Usuario{id='" + idUsuario + "', user='" + username +
               "', nombre='" + nombre + "', rol='" + rol + "'}";
    }
}
