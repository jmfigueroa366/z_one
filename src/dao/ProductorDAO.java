package dao;

import model.Productor;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductorDAO {

    private static final String SELECT_BASE =
        "SELECT p.id_productor, p.id_usuario, p.nombre, p.especialidad, " +
        "       p.fecha_firma, p.fecha_nacimiento, p.num_identificacion, " +
        "       p.tarifa_hora, " +
        "       gp.descripcion AS genero_persona, " +
        "       n.nombre       AS nacionalidad, " +
        "       gm.nombre      AS genero_musical, " +
        "       es.nombre      AS estado " +
        "FROM productores p " +
        "LEFT JOIN genero_persona     gp ON p.id_genero_persona = gp.id_genero_persona " +
        "LEFT JOIN nacionalidades     n  ON p.id_nacionalidad   = n.id_nacionalidad " +
        "LEFT JOIN genero_musicales   gm ON p.id_genero_musical = gm.id_genero " +
        "LEFT JOIN estados_art_pro    es ON p.id_estado         = es.id_estado ";

    public List<Productor> listarTodos() throws SQLException {
        return ejecutar(SELECT_BASE + "ORDER BY p.nombre", null);
    }

    public Productor buscarPorId(int id) throws SQLException {
        List<Productor> r = ejecutar(SELECT_BASE + "WHERE p.id_productor = ?", new Object[]{id});
        return r.isEmpty() ? null : r.get(0);
    }

    // ── INSERTAR ──
    public int crear(Productor p) throws SQLException {
        Integer idNac    = resolverId("nacionalidades",   "id_nacionalidad",   "nombre",      p.getNacionalidad(),  "Nacionalidad");
        Integer idGenPer = resolverId("genero_persona",   "id_genero_persona", "descripcion", p.getGeneroPersona(), "Genero persona");
        Integer idGenMus = resolverId("genero_musicales", "id_genero",         "nombre",      p.getGeneroMusical(), "Genero musical");
        Integer idEstado = resolverId("estados_art_pro",  "id_estado",         "nombre",      p.getEstado(),        "Estado");

        String sql = "INSERT INTO productores (nombre, especialidad, fecha_firma, fecha_nacimiento, " +
                     "num_identificacion, tarifa_hora, id_nacionalidad, id_genero_persona, " +
                     "id_genero_musical, id_estado, id_usuario) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"id_productor"})) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getEspecialidad());
            ps.setDate  (3, p.getFechaFirma() != null ? Date.valueOf(p.getFechaFirma()) : null);
            ps.setDate  (4, p.getFechaNacimiento() != null ? Date.valueOf(p.getFechaNacimiento()) : null);
            ps.setString(5, p.getNumIdentificacion());
            ps.setDouble(6, p.getTarifaHora());
            setIntOrNull(ps, 7,  idNac);
            setIntOrNull(ps, 8,  idGenPer);
            setIntOrNull(ps, 9,  idGenMus);
            setIntOrNull(ps, 10, idEstado);
            setIntOrNull(ps, 11, p.getIdUsuario());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // ── ACTUALIZAR ──
    public boolean actualizar(Productor p) throws SQLException {
        Integer idNac    = resolverId("nacionalidades",   "id_nacionalidad",   "nombre",      p.getNacionalidad(),  "Nacionalidad");
        Integer idGenPer = resolverId("genero_persona",   "id_genero_persona", "descripcion", p.getGeneroPersona(), "Genero persona");
        Integer idGenMus = resolverId("genero_musicales", "id_genero",         "nombre",      p.getGeneroMusical(), "Genero musical");
        Integer idEstado = resolverId("estados_art_pro",  "id_estado",         "nombre",      p.getEstado(),        "Estado");

        String sql = "UPDATE productores SET nombre=?, especialidad=?, fecha_firma=?, " +
                     "fecha_nacimiento=?, num_identificacion=?, tarifa_hora=?, " +
                     "id_nacionalidad=?, id_genero_persona=?, id_genero_musical=?, id_estado=? " +
                     "WHERE id_productor = ?";

        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getEspecialidad());
            ps.setDate  (3, p.getFechaFirma() != null ? Date.valueOf(p.getFechaFirma()) : null);
            ps.setDate  (4, p.getFechaNacimiento() != null ? Date.valueOf(p.getFechaNacimiento()) : null);
            ps.setString(5, p.getNumIdentificacion());
            ps.setDouble(6, p.getTarifaHora());
            setIntOrNull(ps, 7,  idNac);
            setIntOrNull(ps, 8,  idGenPer);
            setIntOrNull(ps, 9,  idGenMus);
            setIntOrNull(ps, 10, idEstado);
            ps.setInt(11, p.getIdProductor());
            return ps.executeUpdate() > 0;
        }
    }

    // ── ELIMINAR ──
    public boolean eliminar(int idProductor) throws SQLException {
        String sql = "DELETE FROM productores WHERE id_productor = ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProductor);
            return ps.executeUpdate() > 0;
        }
    }

    // ── BUSCAR por texto ──
    public List<Productor> buscar(String texto) throws SQLException {
        if (texto == null || texto.isBlank()) return listarTodos();
        String q = "%" + texto.toLowerCase() + "%";
        String sql = SELECT_BASE +
                "WHERE LOWER(p.nombre) LIKE ? OR LOWER(p.especialidad) LIKE ? " +
                "ORDER BY p.nombre";
        return ejecutar(sql, new Object[]{q, q});
    }

    // ══════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════

    /**
     * Busca el ID de un catalogo por su nombre, tolerante a tildes/mayusculas.
     * - Si el valor viene vacio/null: devuelve null (campo opcional, queda NULL en BD).
     * - Si el valor viene con datos pero NO existe en el catalogo: LANZA error claro.
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

    private List<Productor> ejecutar(String sql, Object[] params) throws SQLException {
        List<Productor> out = new ArrayList<>();
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

    private Productor mapear(ResultSet rs) throws SQLException {
        Productor p = new Productor();
        p.setIdProductor(rs.getInt("id_productor"));
        int idU = rs.getInt("id_usuario");
        p.setIdUsuario(rs.wasNull() ? null : idU);
        p.setNombre(rs.getString("nombre"));
        p.setEspecialidad(rs.getString("especialidad"));
        Date ff = rs.getDate("fecha_firma");
        p.setFechaFirma(ff != null ? ff.toLocalDate() : null);
        Date fn = rs.getDate("fecha_nacimiento");
        p.setFechaNacimiento(fn != null ? fn.toLocalDate() : null);
        p.setNumIdentificacion(rs.getString("num_identificacion"));
        p.setTarifaHora(rs.getDouble("tarifa_hora"));
        p.setGeneroPersona(rs.getString("genero_persona"));
        p.setNacionalidad(rs.getString("nacionalidad"));
        p.setGeneroMusical(rs.getString("genero_musical"));
        p.setEstado(rs.getString("estado"));
        return p;
    }
}