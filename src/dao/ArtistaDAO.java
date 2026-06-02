package dao;

import model.Artista;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistaDAO {

    private static final String SCHEMA = "PRODUCTORA_BD.";

    // ── Ya NO incluye genero_musical en el SELECT ni en el JOIN ──────
    private static final String SELECT_BASE =
        "SELECT a.id_artista, a.nombre_artista, a.nombre_real, " +
        "       a.fecha_nacimiento, a.redes_sociales, a.fecha_firma, a.num_identificacion, " +
        "       gp.descripcion AS genero_persona, " +
        "       n.nombre       AS nacionalidad, " +
        "       ta.nombre      AS tipo_artista, " +
        "       es.nombre      AS estado " +
        "FROM "      + SCHEMA + "ARTISTA a " +
        "LEFT JOIN " + SCHEMA + "GENERO_PERSONA  gp ON a.id_genero_persona  = gp.id_genero_persona " +
        "LEFT JOIN " + SCHEMA + "NACIONALIDAD    n  ON a.id_nacionalidad    = n.id_nacionalidad " +
        "LEFT JOIN " + SCHEMA + "TIPO_ARTISTA    ta ON a.id_tipo_artista    = ta.id_tipo_artista " +
        "LEFT JOIN " + SCHEMA + "ESTADO_ART_PRO  es ON a.id_estado_art_pro  = es.id_estado_art_pro ";

    // ── LISTAR / BUSCAR ──────────────────────────────────────────────

    public List<Artista> listarTodos() throws SQLException {
        List<Artista> lista = ejecutar(SELECT_BASE + "ORDER BY a.nombre_artista", null);
        // Cargar géneros de cada artista desde la tabla intermedia
        for (Artista a : lista) {
            a.setGenerosMusicales(listarGenerosDe(a.getIdArtista()));
        }
        return lista;
    }

    public Artista buscarPorId(int id) throws SQLException {
        List<Artista> r = ejecutar(SELECT_BASE + "WHERE a.id_artista = ?", new Object[]{id});
        if (r.isEmpty()) return null;
        Artista a = r.get(0);
        a.setGenerosMusicales(listarGenerosDe(a.getIdArtista()));
        return a;
    }

    // ── INSERTAR ─────────────────────────────────────────────────────

    public int crear(Artista a) throws SQLException {
        Integer idNac    = resolverId("NACIONALIDAD",   "id_nacionalidad",   "nombre",      a.getNacionalidad(),  "Nacionalidad");
        Integer idGenPer = resolverId("GENERO_PERSONA", "id_genero_persona", "descripcion", a.getGeneroPersona(), "Genero persona");
        Integer idTipo   = resolverId("TIPO_ARTISTA",   "id_tipo_artista",   "nombre",      a.getTipoArtista(),   "Tipo artista");
        Integer idEstado = resolverId("ESTADO_ART_PRO", "id_estado_art_pro", "nombre",      a.getEstadoArtista(), "Estado");

        // id_genero_musical NO va aquí — va en ARTISTA_GENERO_MUSICAL
        String sql =
            "INSERT INTO " + SCHEMA + "ARTISTA " +
            "(nombre_artista, nombre_real, fecha_nacimiento, redes_sociales, fecha_firma, " +
            " num_identificacion, id_nacionalidad, id_genero_persona, " +
            " id_tipo_artista, id_estado_art_pro) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int idGenerado = -1;
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_ARTISTA"})) {

            ps.setString(1, a.getNombreArtista());
            ps.setString(2, a.getNombreReal());
            ps.setDate  (3, a.getFechaNacimiento() != null ? Date.valueOf(a.getFechaNacimiento()) : null);
            ps.setString(4, a.getRedesSociales());
            ps.setDate  (5, a.getFechaFirma()      != null ? Date.valueOf(a.getFechaFirma())      : null);
            ps.setString(6, a.getNumIdentificacion());
            setIntOrNull(ps, 7,  idNac);
            setIntOrNull(ps, 8,  idGenPer);
            setIntOrNull(ps, 9,  idTipo);
            setIntOrNull(ps, 10, idEstado);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) idGenerado = rs.getInt(1);
            }
        }

        // Insertar géneros en la tabla intermedia
        if (idGenerado > 0 && a.getGenerosMusicales() != null) {
            insertarGenerosMusicales(idGenerado, a.getGenerosMusicales());
        }
        return idGenerado;
    }

    // ── ACTUALIZAR ───────────────────────────────────────────────────

    public boolean actualizar(Artista a) throws SQLException {
        Integer idNac    = resolverId("NACIONALIDAD",   "id_nacionalidad",   "nombre",      a.getNacionalidad(),  "Nacionalidad");
        Integer idGenPer = resolverId("GENERO_PERSONA", "id_genero_persona", "descripcion", a.getGeneroPersona(), "Genero persona");
        Integer idTipo   = resolverId("TIPO_ARTISTA",   "id_tipo_artista",   "nombre",      a.getTipoArtista(),   "Tipo artista");
        Integer idEstado = resolverId("ESTADO_ART_PRO", "id_estado_art_pro", "nombre",      a.getEstadoArtista(), "Estado");

        String sql =
            "UPDATE " + SCHEMA + "ARTISTA SET " +
            "nombre_artista = ?, nombre_real = ?, fecha_nacimiento = ?, " +
            "redes_sociales = ?, fecha_firma = ?, num_identificacion = ?, " +
            "id_nacionalidad = ?, id_genero_persona = ?, " +
            "id_tipo_artista = ?, id_estado_art_pro = ? " +
            "WHERE id_artista = ?";

        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, a.getNombreArtista());
            ps.setString(2, a.getNombreReal());
            ps.setDate  (3, a.getFechaNacimiento() != null ? Date.valueOf(a.getFechaNacimiento()) : null);
            ps.setString(4, a.getRedesSociales());
            ps.setDate  (5, a.getFechaFirma()      != null ? Date.valueOf(a.getFechaFirma())      : null);
            ps.setString(6, a.getNumIdentificacion());
            setIntOrNull(ps, 7,  idNac);
            setIntOrNull(ps, 8,  idGenPer);
            setIntOrNull(ps, 9,  idTipo);
            setIntOrNull(ps, 10, idEstado);
            ps.setInt   (11, a.getIdArtista());

            boolean ok = ps.executeUpdate() > 0;

            // Reemplazar géneros: borrar los viejos e insertar los nuevos
            if (a.getGenerosMusicales() != null) {
                eliminarGenerosMusicales(a.getIdArtista());
                insertarGenerosMusicales(a.getIdArtista(), a.getGenerosMusicales());
            }
            return ok;
        }
    }

    // ── ELIMINAR ─────────────────────────────────────────────────────

    public boolean eliminar(int idArtista) throws SQLException {
        // Primero borrar géneros de la tabla intermedia
        eliminarGenerosMusicales(idArtista);
        String sql = "DELETE FROM " + SCHEMA + "ARTISTA WHERE id_artista = ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idArtista);
            return ps.executeUpdate() > 0;
        }
    }

    // ── TABLA INTERMEDIA ARTISTA_GENERO_MUSICAL ──────────────────────

    private void insertarGenerosMusicales(int idArtista, List<String> generos) throws SQLException {
        for (String gen : generos) {
            if (gen == null || gen.isBlank()) continue;
            Integer idGen = resolverId("GENERO_MUSICAL", "id_genero_musical", "nombre", gen, "Genero musical");
            if (idGen == null) continue;
            String sql = "INSERT INTO " + SCHEMA + "ARTISTA_GENERO_MUSICAL (id_artista, id_genero_musical) VALUES (?, ?)";
            try (Connection c = ConexionDB.getConexion();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idArtista);
                ps.setInt(2, idGen);
                ps.executeUpdate();
            }
        }
    }

    private void eliminarGenerosMusicales(int idArtista) throws SQLException {
        String sql = "DELETE FROM " + SCHEMA + "ARTISTA_GENERO_MUSICAL WHERE id_artista = ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idArtista);
            ps.executeUpdate();
        }
    }

    public List<String> listarGenerosDe(int idArtista) throws SQLException {
        List<String> out = new ArrayList<>();
        String sql =
            "SELECT gm.nombre FROM " + SCHEMA + "ARTISTA_GENERO_MUSICAL ag " +
            "JOIN " + SCHEMA + "GENERO_MUSICAL gm ON ag.id_genero_musical = gm.id_genero_musical " +
            "WHERE ag.id_artista = ? ORDER BY gm.nombre";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idArtista);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(rs.getString(1));
            }
        }
        return out;
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
        a.setIdArtista        (rs.getInt("id_artista"));
        a.setNombreArtista    (rs.getString("nombre_artista"));
        a.setNombreReal       (rs.getString("nombre_real"));
        a.setNumIdentificacion(rs.getString("num_identificacion"));
        Date fn = rs.getDate("fecha_nacimiento");
        a.setFechaNacimiento  (fn != null ? fn.toLocalDate() : null);
        Date ff = rs.getDate("fecha_firma");
        a.setFechaFirma       (ff != null ? ff.toLocalDate() : null);
        a.setRedesSociales    (rs.getString("redes_sociales"));
        a.setGeneroPersona    (rs.getString("genero_persona"));
        a.setNacionalidad     (rs.getString("nacionalidad"));
        a.setTipoArtista      (rs.getString("tipo_artista"));
        a.setEstadoArtista    (rs.getString("estado"));
        // Los géneros se cargan aparte en listarTodos() / buscarPorId()
        return a;
    }
}