package dao;

import model.Artista;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistaDAO {

    private static final String SCHEMA = "PRODUCTORA_BD.";

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

    public List<Artista> listarTodos() throws SQLException {
        List<Artista> artistas = ejecutar(SELECT_BASE + "ORDER BY a.nombre_artista", null);
        for (Artista artista : artistas) {
            artista.setGenerosMusicales(listarGenerosDe(artista.getIdArtista()));
        }
        return artistas;
    }

    public Artista buscarPorId(int id) throws SQLException {
        List<Artista> resultado = ejecutar(SELECT_BASE + "WHERE a.id_artista = ?", new Object[]{id});
        if (resultado.isEmpty()) return null;
        Artista artista = resultado.get(0);
        artista.setGenerosMusicales(listarGenerosDe(artista.getIdArtista()));
        return artista;
    }

    public int crear(Artista artista) throws SQLException {
        IdsArtista ids = resolverIdsArtista(artista);

        String sql =
            "INSERT INTO " + SCHEMA + "ARTISTA " +
            "(nombre_artista, nombre_real, fecha_nacimiento, redes_sociales, fecha_firma, " +
            " num_identificacion, id_nacionalidad, id_genero_persona, " +
            " id_tipo_artista, id_estado_art_pro) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int idGenerado = -1;
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_ARTISTA"})) {

            asignarParametrosArtista(ps, artista, ids);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) idGenerado = rs.getInt(1);
            }
        }

        // Los géneros musicales se guardan en la tabla intermedia, no en ARTISTA
        if (idGenerado > 0 && artista.getGenerosMusicales() != null) {
            insertarGenerosMusicales(idGenerado, artista.getGenerosMusicales());
        }
        return idGenerado;
    }

    public boolean actualizar(Artista artista) throws SQLException {
        IdsArtista ids = resolverIdsArtista(artista);

        String sql =
            "UPDATE " + SCHEMA + "ARTISTA SET " +
            "nombre_artista = ?, nombre_real = ?, fecha_nacimiento = ?, " +
            "redes_sociales = ?, fecha_firma = ?, num_identificacion = ?, " +
            "id_nacionalidad = ?, id_genero_persona = ?, " +
            "id_tipo_artista = ?, id_estado_art_pro = ? " +
            "WHERE id_artista = ?";

        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            asignarParametrosArtista(ps, artista, ids);
            ps.setInt(11, artista.getIdArtista());

            boolean actualizado = ps.executeUpdate() > 0;

            if (artista.getGenerosMusicales() != null) {
                eliminarGenerosMusicales(artista.getIdArtista());
                insertarGenerosMusicales(artista.getIdArtista(), artista.getGenerosMusicales());
            }
            return actualizado;
        }
    }

    public boolean eliminar(int idArtista) throws SQLException {
        // Los géneros de la tabla intermedia deben borrarse antes que el artista
        eliminarGenerosMusicales(idArtista);
        String sql = "DELETE FROM " + SCHEMA + "ARTISTA WHERE id_artista = ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idArtista);
            return ps.executeUpdate() > 0;
        }
    }

    private void insertarGenerosMusicales(int idArtista, List<String> generos) throws SQLException {
        for (String genero : generos) {
            if (genero == null || genero.isBlank()) continue;
            Integer idGenero = resolverId("GENERO_MUSICAL", "id_genero_musical", "nombre", genero, "Genero musical");
            if (idGenero == null) continue;
            String sql = "INSERT INTO " + SCHEMA + "ARTISTA_GENERO_MUSICAL (id_artista, id_genero_musical) VALUES (?, ?)";
            try (Connection c = ConexionDB.getConexion();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idArtista);
                ps.setInt(2, idGenero);
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
        List<String> generos = new ArrayList<>();
        String sql =
            "SELECT gm.nombre FROM " + SCHEMA + "ARTISTA_GENERO_MUSICAL ag " +
            "JOIN " + SCHEMA + "GENERO_MUSICAL gm ON ag.id_genero_musical = gm.id_genero_musical " +
            "WHERE ag.id_artista = ? ORDER BY gm.nombre";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idArtista);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) generos.add(rs.getString(1));
            }
        }
        return generos;
    }

    public List<String> listarNacionalidades() throws SQLException {
        List<String> nacionalidades = new ArrayList<>();
        String sql = "SELECT nombre FROM " + SCHEMA + "NACIONALIDAD ORDER BY nombre";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) nacionalidades.add(rs.getString(1));
        }
        return nacionalidades;
    }

    public List<String> listarGenerosMusicales() throws SQLException {
        List<String> generos = new ArrayList<>();
        String sql = "SELECT nombre FROM " + SCHEMA + "GENERO_MUSICAL ORDER BY nombre";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) generos.add(rs.getString(1));
        }
        return generos;
    }

    /**
     * Resuelve todos los IDs de catálogo necesarios para insertar o actualizar un artista.
     */
    private IdsArtista resolverIdsArtista(Artista artista) throws SQLException {
        IdsArtista ids = new IdsArtista();
        ids.nacionalidad  = resolverId("NACIONALIDAD",   "id_nacionalidad",   "nombre",      artista.getNacionalidad(),  "Nacionalidad");
        ids.generoPersona = resolverId("GENERO_PERSONA", "id_genero_persona", "descripcion", artista.getGeneroPersona(), "Genero persona");
        ids.tipoArtista   = resolverId("TIPO_ARTISTA",   "id_tipo_artista",   "nombre",      artista.getTipoArtista(),   "Tipo artista");
        ids.estado        = resolverId("ESTADO_ART_PRO", "id_estado_art_pro", "nombre",      artista.getEstadoArtista(), "Estado");
        return ids;
    }

    /**
     * Asigna los parámetros 1-10 del PreparedStatement para INSERT o UPDATE de artista.
     */
    private void asignarParametrosArtista(PreparedStatement ps, Artista artista, IdsArtista ids) throws SQLException {
        ps.setString(1, artista.getNombreArtista());
        ps.setString(2, artista.getNombreReal());
        ps.setDate  (3, artista.getFechaNacimiento() != null ? Date.valueOf(artista.getFechaNacimiento()) : null);
        ps.setString(4, artista.getRedesSociales());
        ps.setDate  (5, artista.getFechaFirma()      != null ? Date.valueOf(artista.getFechaFirma())      : null);
        ps.setString(6, artista.getNumIdentificacion());
        setIntOrNull(ps, 7,  ids.nacionalidad);
        setIntOrNull(ps, 8,  ids.generoPersona);
        setIntOrNull(ps, 9,  ids.tipoArtista);
        setIntOrNull(ps, 10, ids.estado);
    }

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

    private void setIntOrNull(PreparedStatement ps, int indice, Integer valor) throws SQLException {
        if (valor != null) ps.setInt(indice, valor);
        else               ps.setNull(indice, Types.NUMERIC);
    }

    private List<Artista> ejecutar(String sql, Object[] params) throws SQLException {
        List<Artista> artistas = new ArrayList<>();
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (params != null)
                for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) artistas.add(mapear(rs));
            }
        }
        return artistas;
    }

    private Artista mapear(ResultSet rs) throws SQLException {
        Artista artista = new Artista();
        artista.setIdArtista        (rs.getInt("id_artista"));
        artista.setNombreArtista    (rs.getString("nombre_artista"));
        artista.setNombreReal       (rs.getString("nombre_real"));
        artista.setNumIdentificacion(rs.getString("num_identificacion"));
        Date fechaNacimiento = rs.getDate("fecha_nacimiento");
        artista.setFechaNacimiento  (fechaNacimiento != null ? fechaNacimiento.toLocalDate() : null);
        Date fechaFirma = rs.getDate("fecha_firma");
        artista.setFechaFirma       (fechaFirma != null ? fechaFirma.toLocalDate() : null);
        artista.setRedesSociales    (rs.getString("redes_sociales"));
        artista.setGeneroPersona    (rs.getString("genero_persona"));
        artista.setNacionalidad     (rs.getString("nacionalidad"));
        artista.setTipoArtista      (rs.getString("tipo_artista"));
        artista.setEstadoArtista    (rs.getString("estado"));
        // Los géneros musicales se cargan aparte en listarTodos() y buscarPorId()
        return artista;
    }

    /**
     * Agrupa los IDs de catálogo resueltos para un artista,
     * evitando repetir el bloque de resolución en crear() y actualizar().
     */
    private static class IdsArtista {
        Integer nacionalidad;
        Integer generoPersona;
        Integer tipoArtista;
        Integer estado;
    }
}