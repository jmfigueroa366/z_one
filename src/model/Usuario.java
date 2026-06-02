package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
public class Usuario {

    // Constantes de rol (id_rol en la BD)
    public static final int ROL_ADMIN     = 1;
    public static final int ROL_ARTISTA   = 2;
    public static final int ROL_PRODUCTOR = 3;
    public static final int ROL_USUARIO   = 4;

    private Integer       idUsuario;
    private String        username;
    private String        passwordHash;   // mapea a contrasena
    private String        correo;
    private String        nombreCompleto;
    private int           idRol;
    private String        nombreRol;      // se rellena con JOIN
    private boolean       activo;
    private LocalDate     fechaRegistro;
    private LocalDateTime ultimoLogin;

    public Usuario() {}

    /** Constructor completo (al cargar desde BD). */
    public Usuario(Integer idUsuario, String username, String passwordHash,
                   String correo, String nombreCompleto,
                   int idRol, String nombreRol, boolean activo,
                   LocalDate fechaRegistro, LocalDateTime ultimoLogin) {
        this.idUsuario      = idUsuario;
        this.username       = username;
        this.passwordHash   = passwordHash;
        this.correo         = correo;
        this.nombreCompleto = nombreCompleto;
        this.idRol          = idRol;
        this.nombreRol      = nombreRol;
        this.activo         = activo;
        this.fechaRegistro  = fechaRegistro;
        this.ultimoLogin    = ultimoLogin;
    }

    /** Constructor para crear un nuevo usuario antes de insertarlo. */
    public Usuario(String username, String passwordHash, String correo,
                   String nombreCompleto, int idRol) {
        this(null, username, passwordHash, correo, nombreCompleto,
             idRol, null, true, LocalDate.now(), null);
    }

    // ── Getters ──
    public Integer       getIdUsuario()      { return idUsuario; }
    public String        getUsername()       { return username; }
    public String        getPasswordHash()   { return passwordHash; }
    public String        getCorreo()         { return correo; }
    public String        getNombreCompleto() { return nombreCompleto; }
    public int           getIdRol()          { return idRol; }
    public String        getNombreRol()      { return nombreRol; }
    public boolean       isActivo()          { return activo; }
    public LocalDate     getFechaRegistro()  { return fechaRegistro; }
    public LocalDateTime getUltimoLogin()    { return ultimoLogin; }

    // ── Setters con validación ──
    public void setIdUsuario(Integer id) { this.idUsuario = id; }

    public void setUsername(String username) {
        if (username == null || username.trim().length() < 3)
            throw new IllegalArgumentException("El usuario debe tener al menos 3 caracteres.");
        this.username = username.trim();
    }

    public void setPasswordHash(String hash) { this.passwordHash = hash; }

    public void setCorreo(String correo) {
        if (correo == null || !correo.contains("@"))
            throw new IllegalArgumentException("El correo debe contener @.");
        this.correo = correo.trim();
    }

    public void setNombreCompleto(String nombre) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre no puede estar vacio.");
        this.nombreCompleto = nombre.trim();
    }

    public void setIdRol(int idRol) {
        if (idRol < 1 || idRol > 4)
            throw new IllegalArgumentException("Rol invalido. Debe ser 1=ADMIN, 2=ARTISTA, 3=PRODUCTOR o 4=USUARIO.");
        this.idRol = idRol;
    }

    public void setNombreRol(String nombreRol)        { this.nombreRol = nombreRol; }
    public void setActivo(boolean activo)              { this.activo = activo; }
    public void setFechaRegistro(LocalDate f)          { this.fechaRegistro = f; }
    public void setUltimoLogin(LocalDateTime u)        { this.ultimoLogin = u; }

    // ── Utilidades de rol ──
    public boolean esAdmin()      { return idRol == ROL_ADMIN; }
    public boolean esArtista()    { return idRol == ROL_ARTISTA; }
    public boolean esProductor()  { return idRol == ROL_PRODUCTOR; }

@Override
    public String toString() {
        return "Usuario{id=" + idUsuario + ", user='" + username +
               "', nombre='" + nombreCompleto + "', rol=" + idRol + "}";
    }

    // ── Compatibilidad con código viejo ──
    /** @deprecated usa getNombreCompleto() */
    @Deprecated
    public String getNombre() {
        return nombreCompleto;
    }

    /** @deprecated usa getNombreRol() */
    @Deprecated
    public String getRol() {
        return nombreRol != null ? nombreRol : "USUARIO";
    }
}
