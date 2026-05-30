package dao;

import model.Artista;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistaDAO {

    private static final String SELECT_BASE =
        "SELECT a.id_artista, a.id_usuario, a.nombre_artista, a.nombre_real, " +
        "       a.fecha_nacimiento, a.redes_sociales, a.fecha_firma, a.num_identificacion, " +
        "       gp.descripcion AS genero_persona, " +
        "       n.nombre        AS nacionalidad, " +
        "       gm.nombre       AS genero_musical, " +
        "       ta.nombre       AS tipo_artista, " +
        "       es.nombre       AS estado " +
        "FROM artistas a " +
        "LEFT JOIN genero_persona     gp ON a.id_genero_persona = gp.id_genero_persona " +
        "LEFT JOIN nacionalidades     n  ON a.id_nacionalidad   = n.id_nacionalidad " +
        "LEFT JOIN genero_musicales   gm ON a.id_genero_musical = gm.id_genero " +
        "LEFT JOIN tipo_artista       ta ON a.id_tipo_artista   = ta.id_tipo_artista " +
        "LEFT JOIN estados_art_pro    es ON a.id_estado         = es.id_estado ";

    // ── LISTAR / BUSCAR ──
    public List<Artista> listarTodos() throws SQLException {
        return ejecutar(SELECT_BASE + "ORDER BY a.nombre_artista", null);
    }

    public Artista buscarPorId(int id) throws SQLException {
        List<Artista> r = ejecutar(SELECT_BASE + "WHERE a.id_artista = ?", new Object[]{id});
        return r.isEmpty() ? null : r.get(0);
    }

    // ── INSERTAR ──
    public int crear(Artista a) throws SQLException {
        // Resolver IDs ANTES de insertar (falla ruidoso si no existe)
        Integer idNac    = resolverId("nacionalidades",   "id_nacionalidad",   "nombre",      a.getNacionalidad(),  "Nacionalidad");
        Integer idGenPer = resolverId("genero_persona",   "id_genero_persona", "descripcion", a.getGeneroPersona(), "Genero persona");
        Integer idGenMus = resolverId("genero_musicales", "id_genero",         "nombre",      a.getGeneroMusical(), "Genero musical");
        Integer idTipo   = resolverId("tipo_artista",     "id_tipo_artista",   "nombre",      a.getTipoArtista(),   "Tipo artista");
        Integer idEstado = resolverId("estados_art_pro",  "id_estado",         "nombre",      a.getEstadoArtista(), "Estado");

        String sql = "INSERT INTO artistas (nombre_artista, nombre_real, fecha_nacimiento, " +
                     "redes_sociales, fecha_firma, num_identificacion, id_nacionalidad, " +
                     "id_genero_persona, id_genero_musical, id_tipo_artista, id_estado, id_usuario) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"id_artista"})) {

            ps.setString(1, a.getNombreArtista());
            ps.setString(2, a.getNombreReal());
            ps.setDate  (3, a.getFechaNacimiento() != null ? Date.valueOf(a.getFechaNacimiento()) : null);
            ps.setString(4, a.getRedesSociales());
            ps.setDate  (5, a.getFechaFirma() != null ? Date.valueOf(a.getFechaFirma()) : null);
            ps.setString(6, a.getNumIdentificacion());
            setIntOrNull(ps, 7,  idNac);
            setIntOrNull(ps, 8,  idGenPer);
            setIntOrNull(ps, 9,  idGenMus);
            setIntOrNull(ps, 10, idTipo);
            setIntOrNull(ps, 11, idEstado);
            setIntOrNull(ps, 12, a.getIdUsuario());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // ── ACTUALIZAR ──
    public boolean actualizar(Artista a) throws SQLException {
        Integer idNac    = resolverId("nacionalidades",   "id_nacionalidad",   "nombre",      a.getNacionalidad(),  "Nacionalidad");
        Integer idGenPer = resolverId("genero_persona",   "id_genero_persona", "descripcion", a.getGeneroPersona(), "Genero persona");
        Integer idGenMus = resolverId("genero_musicales", "id_genero",         "nombre",      a.getGeneroMusical(), "Genero musical");
        Integer idTipo   = resolverId("tipo_artista",     "id_tipo_artista",   "nombre",      a.getTipoArtista(),   "Tipo artista");
        Integer idEstado = resolverId("estados_art_pro",  "id_estado",         "nombre",      a.getEstadoArtista(), "Estado");

        String sql = "UPDATE artistas SET " +
                     "nombre_artista = ?, nombre_real = ?, fecha_nacimiento = ?, " +
                     "redes_sociales = ?, fecha_firma = ?, num_identificacion = ?, " +
                     "id_nacionalidad = ?, id_genero_persona = ?, id_genero_musical = ?, " +
                     "id_tipo_artista = ?, id_estado = ? " +
                     "WHERE id_artista = ?";

        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, a.getNombreArtista());
            ps.setString(2, a.getNombreReal());
            ps.setDate  (3, a.getFechaNacimiento() != null ? Date.valueOf(a.getFechaNacimiento()) : null);
            ps.setString(4, a.getRedesSociales());
            ps.setDate  (5, a.getFechaFirma() != null ? Date.valueOf(a.getFechaFirma()) : null);
            ps.setString(6, a.getNumIdentificacion());
            setIntOrNull(ps, 7,  idNac);
            setIntOrNull(ps, 8,  idGenPer);
            setIntOrNull(ps, 9,  idGenMus);
            setIntOrNull(ps, 10, idTipo);
            setIntOrNull(ps, 11, idEstado);
            ps.setInt(12, a.getIdArtista());
            return ps.executeUpdate() > 0;
        }
    }

    // ── ELIMINAR ──
    public boolean eliminar(int idArtista) throws SQLException {
        String sql = "DELETE FROM artistas WHERE id_artista = ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idArtista);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Helpers ──

    /**
     * Busca el ID de un catalogo por nombre. Normaliza tildes y mayusculas.
     * Si el nombre viene vacio, devuelve null (campo opcional).
     * Si el nombre viene con valor pero no se encuentra, LANZA error claro.
     */
    private Integer resolverId(String tabla, String colId, String colNombre,
                                String valor, String etiqueta) throws SQLException {
        if (valor == null || valor.isBlank()) return null;
        String sql = "SELECT " + colId + " FROM " + tabla +
                     " WHERE UPPER(TRIM(" + colNombre + ")) = UPPER(TRIM(?))";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException(etiqueta + " '" + valor + "' no existe en la BD. " +
                "Verifica que este registrado en la tabla " + tabla + ".");
    }

    private void setIntOrNull(PreparedStatement ps, int idx, Integer val) throws SQLException {
        if (val != null) ps.setInt(idx, val);
        else             ps.setNull(idx, Types.NUMERIC);
    }

    private List<Artista> ejecutar(String sql, Object[] params) throws SQLException {
        List<Artista> out = new ArrayList<>();
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (params != null)
                for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapear(rs));
            }
        }
        return out;
    }

    private Artista mapear(ResultSet rs) throws SQLException {
        Artista a = new Artista();
        a.setIdArtista(rs.getInt("id_artista"));
        int idU = rs.getInt("id_usuario");
        a.setIdUsuario(rs.wasNull() ? null : idU);
        a.setNombreArtista(rs.getString("nombre_artista"));
        a.setNombreReal(rs.getString("nombre_real"));
        Date fn = rs.getDate("fecha_nacimiento");
        a.setFechaNacimiento(fn != null ? fn.toLocalDate() : null);
        a.setRedesSociales(rs.getString("redes_sociales"));
        Date ff = rs.getDate("fecha_firma");
        a.setFechaFirma(ff != null ? ff.toLocalDate() : null);
        a.setNumIdentificacion(rs.getString("num_identificacion"));
        a.setGeneroPersona(rs.getString("genero_persona"));
        a.setNacionalidad(rs.getString("nacionalidad"));
        a.setGeneroMusical(rs.getString("genero_musical"));
        a.setTipoArtista(rs.getString("tipo_artista"));
        a.setEstadoArtista(rs.getString("estado"));
        return a;
    }
}