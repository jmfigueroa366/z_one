package dao;

import model.Cancion;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CancionDao {

    private static final String SELECT_BASE =
        "SELECT c.id_cancion, c.titulo, c.bpm, c.fecha_registro, c.fecha_publicacion, " +
        "       c.id_productor, p.nombre AS nombre_productor, " +
        "       c.id_formato, f.titulo AS nombre_formato, " +
        "       c.id_idioma, i.nombre AS nombre_idioma, " +
        "       c.id_estado_cancion, ec.nombre AS nombre_estado, " +
        "       c.id_genero_musical, gm.nombre AS nombre_genero, " +
        "       c.ruta_archivo " +
        "FROM cancion c " +
        "LEFT JOIN productor      p  ON c.id_productor      = p.id_productor " +
        "LEFT JOIN formato        f  ON c.id_formato        = f.id_formato " +
        "LEFT JOIN idioma         i  ON c.id_idioma         = i.id_idioma " +
        "LEFT JOIN estado_cancion ec ON c.id_estado_cancion = ec.id_estado_cancion " +
        "LEFT JOIN genero_musical gm ON c.id_genero_musical = gm.id_genero_musical ";
    // ── LISTAR / BUSCAR ──────────────────────────────────────────────

    public List<Cancion> listarTodos() throws SQLException {
        return ejecutar(SELECT_BASE + "ORDER BY c.titulo", null);
    }

    public List<Cancion> listarConArchivo() throws SQLException {
        return ejecutar(SELECT_BASE
            + "WHERE c.ruta_archivo IS NOT NULL "
            + "ORDER BY c.titulo", null);
    }

    public Cancion buscarPorId(int id) throws SQLException {
        List<Cancion> r = ejecutar(SELECT_BASE + "WHERE c.id_cancion = ?", new Object[]{id});
        return r.isEmpty() ? null : r.get(0);
    }

    public List<Cancion> buscar(String texto) throws SQLException {
        if (texto == null || texto.isBlank()) return listarTodos();
        String q = "%" + texto.toLowerCase() + "%";
        String sql = SELECT_BASE +
                "WHERE LOWER(c.titulo) LIKE ? OR LOWER(p.nombre) LIKE ? " +
                "ORDER BY c.titulo";
        return ejecutar(sql, new Object[]{q, q});
    }

    // ── INSERTAR ─────────────────────────────────────────────────────

    public int crear(Cancion c) throws SQLException {
        Integer idIdioma  = resolverId("idioma",        "id_idioma",         "nombre", c.getNombreIdioma(),  "Idioma");
        Integer idFormato = resolverId("formato",        "id_formato",        "titulo", c.getNombreFormato(), "Formato");
        Integer idEstado  = resolverId("estado_cancion", "id_estado_cancion", "nombre", c.getNombreEstado(),  "Estado cancion");
        Integer idGenero = resolverId("genero_musical", "id_genero_musical", "nombre", c.getNombreGenero(), "Genero musical");

        String sql = "INSERT INTO cancion (titulo, bpm, fecha_registro, fecha_publicacion, " +
                     "id_productor, id_formato, id_idioma, " +
                     "id_estado_cancion, id_genero_musical, ruta_archivo) " +
                     "VALUES (?, ?, SYSDATE, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"id_cancion"})) {
            ps.setString(1, c.getTitulo());
            setIntOrNull(ps, 2, c.getBpm());
            ps.setDate(3, c.getFechaCompilacion() != null
                    ? Date.valueOf(c.getFechaCompilacion()) : null);
            setIntOrNull(ps, 4, c.getIdProductor());
            setIntOrNull(ps, 5, idFormato);
            setIntOrNull(ps, 6, idIdioma);
            setIntOrNull(ps, 7, idEstado);
            setIntOrNull(ps, 8, idGenero);
            ps.setString(9, c.getRutaArchivo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // ── ACTUALIZAR ───────────────────────────────────────────────────

    public boolean actualizar(Cancion c) throws SQLException {
        Integer idIdioma  = resolverId("idioma",        "id_idioma",         "nombre", c.getNombreIdioma(),  "Idioma");
        Integer idFormato = resolverId("formato",        "id_formato",        "titulo", c.getNombreFormato(), "Formato");
        Integer idEstado  = resolverId("estado_cancion", "id_estado_cancion", "nombre", c.getNombreEstado(),  "Estado cancion");
        Integer idGenero  = resolverId("genero_musical", "id_genero",         "nombre", c.getNombreGenero(),  "Genero musical");

        String sql = "UPDATE cancion SET titulo = ?, bpm = ?, fecha_publicacion = ?, " +
                     "id_productor = ?, id_formato = ?, id_idioma = ?, " +
                     "id_estado_cancion = ?, id_genero_musical = ?, ruta_archivo = ? " +
                     "WHERE id_cancion = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getTitulo());
            setIntOrNull(ps, 2, c.getBpm());
            ps.setDate(3, c.getFechaCompilacion() != null
                    ? Date.valueOf(c.getFechaCompilacion()) : null);
            setIntOrNull(ps, 4, c.getIdProductor());
            setIntOrNull(ps, 5, idFormato);
            setIntOrNull(ps, 6, idIdioma);
            setIntOrNull(ps, 7, idEstado);
            setIntOrNull(ps, 8, idGenero);
            ps.setString(9, c.getRutaArchivo());
            ps.setInt(10, c.getIdCancion());
            return ps.executeUpdate() > 0;
        }
    }

    // ── ELIMINAR ─────────────────────────────────────────────────────

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cancion WHERE id_cancion = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── HELPERS ──────────────────────────────────────────────────────

    private Integer resolverId(String tabla, String colId, String colNombre,
                                String valor, String etiqueta) throws SQLException {
        if (valor == null || valor.isBlank()) return null;
        String sql = "SELECT " + colId + " FROM " + tabla +
                     " WHERE UPPER(TRIM(" + colNombre + ")) = UPPER(TRIM(?))";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException(etiqueta + " '" + valor + "' no existe en la BD.");
    }

    private void setIntOrNull(PreparedStatement ps, int idx, Integer val) throws SQLException {
        if (val != null) ps.setInt(idx, val);
        else             ps.setNull(idx, Types.NUMERIC);
    }

    private List<Cancion> ejecutar(String sql, Object[] params) throws SQLException {
        List<Cancion> out = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (params != null)
                for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapear(rs));
            }
        }
        return out;
    }

    private Cancion mapear(ResultSet rs) throws SQLException {
        Cancion c = new Cancion();
        c.setIdCancion(rs.getInt("id_cancion"));
        c.setTitulo(rs.getString("titulo"));
        int bpm = rs.getInt("bpm");
        c.setBpm(rs.wasNull() ? null : bpm);
        java.sql.Date fr = rs.getDate("fecha_registro");
        c.setFechaComposicion(fr != null ? fr.toLocalDate().atStartOfDay() : null);
        java.sql.Date fp = rs.getDate("fecha_publicacion");
        c.setFechaCompilacion(fp != null ? fp.toLocalDate() : null);
        int idPro = rs.getInt("id_productor");
        c.setIdProductor(rs.wasNull() ? null : idPro);
        c.setNombreProductor(rs.getString("nombre_productor"));
        int idFor = rs.getInt("id_formato");
        c.setIdFormato(rs.wasNull() ? null : idFor);
        c.setNombreFormato(rs.getString("nombre_formato"));
        int idIdi = rs.getInt("id_idioma");
        c.setIdIdioma(rs.wasNull() ? null : idIdi);
        c.setNombreIdioma(rs.getString("nombre_idioma"));
        int idEst = rs.getInt("id_estado_cancion");
        c.setIdEstadoCancion(rs.wasNull() ? null : idEst);
        c.setNombreEstado(rs.getString("nombre_estado"));
        int idGen = rs.getInt("id_genero_musical");
        c.setIdGeneroMusical(rs.wasNull() ? null : idGen);
        c.setNombreGenero(rs.getString("nombre_genero"));
        c.setRutaArchivo(rs.getString("ruta_archivo"));
        return c;
    }
}