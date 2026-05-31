package dao;

import model.Cancion;
import util.ConexionDB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CancionDao {

    private static final String SELECT_BASE =
        "SELECT c.id_cancion, c.titulo, c.bpm, c.fecha_composicion, c.fecha_compilacion, " +
        "       c.id_productor, p.nombre AS nombre_productor, " +
        "       c.id_formato, f.titulo AS nombre_formato, " +
        "       c.id_idioma, i.nombre AS nombre_idioma, " +
        "       c.id_version_cancion, " +
        "       c.id_estado_cancion, ec.nombre AS nombre_estado, " +
        "       c.id_genero_musical, gm.nombre AS nombre_genero, " +
        "       c.ruta_archivo " +
        "FROM canciones c " +
        "LEFT JOIN productores       p  ON c.id_productor       = p.id_productor " +
        "LEFT JOIN formatos          f  ON c.id_formato         = f.id_formato " +
        "LEFT JOIN idiomas           i  ON c.id_idioma          = i.id_idiomas " +
        "LEFT JOIN estados_canciones ec ON c.id_estado_cancion  = ec.id_estado_cancion " +
        "LEFT JOIN genero_musicales  gm ON c.id_genero_musical  = gm.id_genero ";

    // ── LISTAR / BUSCAR ──
    public List<Cancion> listarTodos() throws SQLException {
        return ejecutar(SELECT_BASE + "ORDER BY c.titulo", null);
    }

    /** Solo trae canciones que tengan archivo de audio (para el reproductor). */
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

    // ── INSERTAR ──
    public int crear(Cancion c) throws SQLException {
        Integer idIdioma  = resolverId("idiomas",           "id_idiomas",        "nombre", c.getNombreIdioma(),  "Idioma");
        Integer idFormato = resolverId("formatos",          "id_formato",        "titulo", c.getNombreFormato(), "Formato");
        Integer idEstado  = resolverId("estados_canciones", "id_estado_cancion", "nombre", c.getNombreEstado(),  "Estado cancion");
        Integer idGenero  = resolverId("genero_musicales",  "id_genero",         "nombre", c.getNombreGenero(),  "Genero musical");

        String sql = "INSERT INTO canciones (titulo, bpm, fecha_composicion, fecha_compilacion, " +
                     "id_productor, id_formato, id_idioma, id_version_cancion, " +
                     "id_estado_cancion, id_genero_musical, ruta_archivo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"id_cancion"})) {
            ps.setString(1, c.getTitulo());
            setIntOrNull(ps, 2, c.getBpm());
            ps.setTimestamp(3, c.getFechaComposicion() != null
                    ? Timestamp.valueOf(c.getFechaComposicion()) : null);
            ps.setDate(4, c.getFechaCompilacion() != null
                    ? Date.valueOf(c.getFechaCompilacion()) : null);
            setIntOrNull(ps, 5,  c.getIdProductor());
            setIntOrNull(ps, 6,  idFormato);
            setIntOrNull(ps, 7,  idIdioma);
            setIntOrNull(ps, 8,  c.getIdVersionCancion());
            setIntOrNull(ps, 9,  idEstado);
            setIntOrNull(ps, 10, idGenero);
            ps.setString(11, c.getRutaArchivo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // ── ACTUALIZAR ──
    public boolean actualizar(Cancion c) throws SQLException {
        Integer idIdioma  = resolverId("idiomas",           "id_idiomas",        "nombre", c.getNombreIdioma(),  "Idioma");
        Integer idFormato = resolverId("formatos",          "id_formato",        "titulo", c.getNombreFormato(), "Formato");
        Integer idEstado  = resolverId("estados_canciones", "id_estado_cancion", "nombre", c.getNombreEstado(),  "Estado cancion");
        Integer idGenero  = resolverId("genero_musicales",  "id_genero",         "nombre", c.getNombreGenero(),  "Genero musical");

        String sql = "UPDATE canciones SET titulo = ?, bpm = ?, fecha_composicion = ?, " +
                     "fecha_compilacion = ?, id_productor = ?, id_formato = ?, id_idioma = ?, " +
                     "id_version_cancion = ?, id_estado_cancion = ?, id_genero_musical = ?, " +
                     "ruta_archivo = ? " +
                     "WHERE id_cancion = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getTitulo());
            setIntOrNull(ps, 2, c.getBpm());
            ps.setTimestamp(3, c.getFechaComposicion() != null
                    ? Timestamp.valueOf(c.getFechaComposicion()) : null);
            ps.setDate(4, c.getFechaCompilacion() != null
                    ? Date.valueOf(c.getFechaCompilacion()) : null);
            setIntOrNull(ps, 5,  c.getIdProductor());
            setIntOrNull(ps, 6,  idFormato);
            setIntOrNull(ps, 7,  idIdioma);
            setIntOrNull(ps, 8,  c.getIdVersionCancion());
            setIntOrNull(ps, 9,  idEstado);
            setIntOrNull(ps, 10, idGenero);
            ps.setString(11, c.getRutaArchivo());
            ps.setInt(12, c.getIdCancion());
            return ps.executeUpdate() > 0;
        }
    }

    // ── ELIMINAR ──
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM canciones WHERE id_cancion = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════

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
        Timestamp fc = rs.getTimestamp("fecha_composicion");
        c.setFechaComposicion(fc != null ? fc.toLocalDateTime() : null);
        Date fcomp = rs.getDate("fecha_compilacion");
        c.setFechaCompilacion(fcomp != null ? fcomp.toLocalDate() : null);
        int idPro = rs.getInt("id_productor");
        c.setIdProductor(rs.wasNull() ? null : idPro);
        c.setNombreProductor(rs.getString("nombre_productor"));
        int idFor = rs.getInt("id_formato");
        c.setIdFormato(rs.wasNull() ? null : idFor);
        c.setNombreFormato(rs.getString("nombre_formato"));
        int idIdi = rs.getInt("id_idioma");
        c.setIdIdioma(rs.wasNull() ? null : idIdi);
        c.setNombreIdioma(rs.getString("nombre_idioma"));
        int idVer = rs.getInt("id_version_cancion");
        c.setIdVersionCancion(rs.wasNull() ? null : idVer);
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