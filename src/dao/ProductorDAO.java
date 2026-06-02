package dao;

import model.Productor;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductorDAO {

    private static final String SCHEMA = "PRODUCTORA_BD.";

    private static final String SELECT_BASE =
        "SELECT p.id_productor, p.num_identificacion, p.nombre, " +
        "       p.fecha_nacimiento, p.fecha_firma, p.especialidad, " +
        "       gp.descripcion AS genero_persona, " +
        "       n.nombre       AS nacionalidad, " +
        "       gm.nombre      AS genero_musical, " +
        "       es.nombre      AS estado " +
        "FROM "      + SCHEMA + "PRODUCTOR p " +
        "LEFT JOIN " + SCHEMA + "GENERO_PERSONA  gp ON p.id_genero_persona  = gp.id_genero_persona " +
        "LEFT JOIN " + SCHEMA + "NACIONALIDAD    n  ON p.id_nacionalidad    = n.id_nacionalidad " +
        "LEFT JOIN " + SCHEMA + "GENERO_MUSICAL  gm ON p.id_genero_musical  = gm.id_genero_musical " +
        "LEFT JOIN " + SCHEMA + "ESTADO_ART_PRO  es ON p.id_estado_art_pro  = es.id_estado_art_pro ";

    // ── LISTAR / BUSCAR ──────────────────────────────────────────────

    public List<Productor> listarTodos() throws SQLException {
        return ejecutar(SELECT_BASE + "ORDER BY p.nombre", null);
    }

    public Productor buscarPorId(int id) throws SQLException {
        List<Productor> r = ejecutar(SELECT_BASE + "WHERE p.id_productor = ?", new Object[]{id});
        return r.isEmpty() ? null : r.get(0);
    }

    public List<Productor> buscar(String texto) throws SQLException {
        if (texto == null || texto.isBlank()) return listarTodos();
        String q = "%" + texto.toLowerCase() + "%";
        String sql = SELECT_BASE +
                "WHERE LOWER(p.nombre) LIKE ? OR LOWER(p.especialidad) LIKE ? " +
                "ORDER BY p.nombre";
        return ejecutar(sql, new Object[]{q, q});
    }

    // ── INSERTAR ─────────────────────────────────────────────────────

    public int crear(Productor p) throws SQLException {
        Integer idNac    = resolverId("NACIONALIDAD",   "id_nacionalidad",   "nombre",      p.getNacionalidad(),  "Nacionalidad");
        Integer idGenPer = resolverId("GENERO_PERSONA", "id_genero_persona", "descripcion", p.getGeneroPersona(), "Genero persona");
        Integer idGenMus = resolverId("GENERO_MUSICAL", "id_genero_musical", "nombre",      p.getGeneroMusical(), "Genero musical");
        Integer idEstado = resolverId("ESTADO_ART_PRO", "id_estado_art_pro", "nombre",      p.getEstado(),        "Estado");

        String sql =
            "INSERT INTO " + SCHEMA + "PRODUCTOR " +
            "(num_identificacion, nombre, fecha_nacimiento, fecha_firma, especialidad, " +
            " id_genero_persona, id_nacionalidad, id_genero_musical, id_estado_art_pro) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_PRODUCTOR"})) {

            ps.setString(1, p.getNumIdentificacion());
            ps.setString(2, p.getNombre());
            ps.setDate  (3, p.getFechaNacimiento() != null ? Date.valueOf(p.getFechaNacimiento()) : null);
            ps.setDate  (4, p.getFechaFirma()      != null ? Date.valueOf(p.getFechaFirma())      : null);
            ps.setString(5, p.getEspecialidad());
            setIntOrNull(ps, 6, idGenPer);
            setIntOrNull(ps, 7, idNac);
            setIntOrNull(ps, 8, idGenMus);
            setIntOrNull(ps, 9, idEstado);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // ── ACTUALIZAR ───────────────────────────────────────────────────

    public boolean actualizar(Productor p) throws SQLException {
        Integer idNac    = resolverId("NACIONALIDAD",   "id_nacionalidad",   "nombre",      p.getNacionalidad(),  "Nacionalidad");
        Integer idGenPer = resolverId("GENERO_PERSONA", "id_genero_persona", "descripcion", p.getGeneroPersona(), "Genero persona");
        Integer idGenMus = resolverId("GENERO_MUSICAL", "id_genero_musical", "nombre",      p.getGeneroMusical(), "Genero musical");
        Integer idEstado = resolverId("ESTADO_ART_PRO", "id_estado_art_pro", "nombre",      p.getEstado(),        "Estado");

        String sql =
            "UPDATE " + SCHEMA + "PRODUCTOR SET " +
            "num_identificacion = ?, nombre = ?, fecha_nacimiento = ?, fecha_firma = ?, " +
            "especialidad = ?, id_genero_persona = ?, id_nacionalidad = ?, " +
            "id_genero_musical = ?, id_estado_art_pro = ? " +
            "WHERE id_productor = ?";

        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getNumIdentificacion());
            ps.setString(2, p.getNombre());
            ps.setDate  (3, p.getFechaNacimiento() != null ? Date.valueOf(p.getFechaNacimiento()) : null);
            ps.setDate  (4, p.getFechaFirma()      != null ? Date.valueOf(p.getFechaFirma())      : null);
            ps.setString(5, p.getEspecialidad());
            setIntOrNull(ps, 6,  idGenPer);
            setIntOrNull(ps, 7,  idNac);
            setIntOrNull(ps, 8,  idGenMus);
            setIntOrNull(ps, 9,  idEstado);
            ps.setInt   (10, p.getIdProductor());

            return ps.executeUpdate() > 0;
        }
    }

    // ── ELIMINAR ─────────────────────────────────────────────────────

    public boolean eliminar(int idProductor) throws SQLException {
        String sql = "DELETE FROM " + SCHEMA + "PRODUCTOR WHERE id_productor = ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProductor);
            return ps.executeUpdate() > 0;
        }
    }

    // ── CATÁLOGOS ────────────────────────────────────────────────────

    public List<String> listarNacionalidades() throws SQLException {
        List<String> out = new ArrayList<>();
        String sql = "SELECT nombre FROM " + SCHEMA + "NACIONALIDAD ORDER BY nombre";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(rs.getString(1));
        }
        return out;
    }

    public List<String> listarGenerosMusicales() throws SQLException {
        List<String> out = new ArrayList<>();
        String sql = "SELECT nombre FROM " + SCHEMA + "GENERO_MUSICAL ORDER BY nombre";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(rs.getString(1));
        }
        return out;
    }

    public List<String> listarEstados() throws SQLException {
        List<String> out = new ArrayList<>();
        String sql = "SELECT nombre FROM " + SCHEMA + "ESTADO_ART_PRO ORDER BY nombre";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(rs.getString(1));
        }
        return out;
    }

    // ── HELPERS ──────────────────────────────────────────────────────

    private Integer resolverId(String tabla, String colId, String colNombre,
                                String valor, String etiqueta) throws SQLException {
        if (valor == null || valor.isBlank()) return null;
        String sql = "SELECT " + colId + " FROM " + SCHEMA + tabla +
                     " WHERE UPPER(TRIM(" + colNombre + ")) = UPPER(TRIM(?))";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException(etiqueta + " '" + valor + "' no existe en la BD. " +
                "Verifica que esté registrado en la tabla " + SCHEMA + tabla + ".");
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
        p.setIdProductor      (rs.getInt("id_productor"));
        p.setNumIdentificacion(rs.getString("num_identificacion"));
        p.setNombre           (rs.getString("nombre"));
        Date fn = rs.getDate("fecha_nacimiento");
        p.setFechaNacimiento  (fn != null ? fn.toLocalDate() : null);
        Date ff = rs.getDate("fecha_firma");
        p.setFechaFirma       (ff != null ? ff.toLocalDate() : null);
        p.setEspecialidad     (rs.getString("especialidad"));
        p.setGeneroPersona    (rs.getString("genero_persona"));
        p.setNacionalidad     (rs.getString("nacionalidad"));
        p.setGeneroMusical    (rs.getString("genero_musical"));
        p.setEstado           (rs.getString("estado"));
        return p;
    }
}