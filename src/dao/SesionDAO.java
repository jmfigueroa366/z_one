package dao;

import model.Artista;
import model.Productor;
import model.Sesion;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SesionDAO — alineado con el schema Oracle (tablas: sesion_grabaciones,
 * artistas, productores y catalogos de nacionalidades/generos/etc).
 */
public class SesionDAO {

    private static final String SELECT_BASE =
        "SELECT s.id_sesion, s.id_cabina, s.nombre_sesion, s.fecha, " +
        "       s.hora_inicio, s.hora_fin, s.duracion, s.estado_sesion, s.observaciones, " +
        "       a.id_artista, a.id_usuario, a.nombre_artista, a.nombre_real, " +
        "       a.fecha_nacimiento, a.redes_sociales, a.fecha_firma, " +
        "       gp.descripcion AS genero_persona, " +
        "       n.nombre        AS nacionalidad, " +
        "       gm.nombre       AS genero_musical, " +
        "       ta.nombre       AS tipo_artista, " +
        "       es.nombre       AS estado_artista, " +
        "       p.id_productor, p.nombre AS prod_nombre, p.especialidad AS prod_esp, " +
        "       p.tarifa_hora " +
        "  FROM sesion_grabaciones s " +
        "  JOIN artistas    a ON a.id_artista   = s.id_artista " +
        "  JOIN productores p ON p.id_productor = s.id_productor " +
        "  LEFT JOIN nacionalidades   n  ON a.id_nacionalidad   = n.id_nacionalidad " +
        "  LEFT JOIN genero_persona   gp ON a.id_genero_persona = gp.id_genero_persona " +
        "  LEFT JOIN genero_musicales gm ON a.id_genero_musical = gm.id_genero " +
        "  LEFT JOIN tipo_artista     ta ON a.id_tipo_artista   = ta.id_tipo_artista " +
        "  LEFT JOIN estados_art_pro  es ON a.id_estado         = es.id_estado ";

    // ── LISTAR ──
    public List<Sesion> listarTodas() throws SQLException {
        List<Sesion> lista = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY s.id_sesion";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── BUSCAR POR ID ──
    public Sesion buscarPorId(int idSesion) throws SQLException {
        String sql = SELECT_BASE + " WHERE s.id_sesion = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSesion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // ── INSERTAR ──
    public int insertar(Sesion s) throws SQLException {
        String sql = "INSERT INTO sesion_grabaciones " +
                     "(id_artista, id_productor, id_cabina, nombre_sesion, fecha, " +
                     " hora_inicio, hora_fin, duracion, estado_sesion, observaciones) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID_SESION"})) {
            llenarParametros(ps, s);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    s.setIdSesion(idGenerado);
                    return idGenerado;
                }
            }
        }
        return 0;
    }

    // ── ACTUALIZAR ──
    public boolean actualizar(Sesion s) throws SQLException {
        String sql = "UPDATE sesion_grabaciones SET " +
                     "id_artista = ?, id_productor = ?, id_cabina = ?, nombre_sesion = ?, " +
                     "fecha = ?, hora_inicio = ?, hora_fin = ?, duracion = ?, " +
                     "estado_sesion = ?, observaciones = ? " +
                     "WHERE id_sesion = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            llenarParametros(ps, s);
            ps.setInt(11, s.getIdSesion());
            return ps.executeUpdate() > 0;
        }
    }

    // ── ELIMINAR ──
    public boolean eliminar(int idSesion) throws SQLException {
        String sql = "DELETE FROM sesion_grabaciones WHERE id_sesion = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSesion);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Helpers ──

    private void llenarParametros(PreparedStatement ps, Sesion s) throws SQLException {
        ps.setInt(1, s.getArtista().getIdArtista());
        ps.setInt(2, s.getProductor().getIdProductor());
        if (s.getIdCabina() != null) ps.setInt(3, s.getIdCabina());
        else                         ps.setNull(3, Types.NUMERIC);
        ps.setString(4, s.getNombreSesion());
        ps.setDate  (5, s.getFecha() != null ? Date.valueOf(s.getFecha()) : null);
        ps.setString(6, s.getHoraInicio());
        ps.setString(7, s.getHoraFin());
        ps.setDouble(8, s.getDuracion());
        ps.setString(9, s.getEstadoSesion());
        ps.setString(10, s.getObservaciones());
    }

    private Sesion mapear(ResultSet rs) throws SQLException {
        int idCab = rs.getInt("id_cabina");
        Integer idCabina = rs.wasNull() ? null : idCab;
        Date fecha = rs.getDate("fecha");
        return new Sesion(
            rs.getInt("id_sesion"),
            mapearArtista(rs),
            mapearProductor(rs),
            idCabina,
            rs.getString("nombre_sesion"),
            fecha != null ? fecha.toLocalDate() : null,
            rs.getString("hora_inicio"),
            rs.getString("hora_fin"),
            rs.getDouble("duracion"),
            rs.getString("estado_sesion"),
            rs.getString("observaciones"));
    }

    private Artista mapearArtista(ResultSet rs) throws SQLException {
        int idU = rs.getInt("id_usuario");
        Integer idUsuario = rs.wasNull() ? null : idU;
        Date fNac  = rs.getDate("fecha_nacimiento");
        Date fFirm = rs.getDate("fecha_firma");
        return new Artista(
            rs.getInt("id_artista"), idUsuario,
            rs.getString("nombre_artista"), rs.getString("nombre_real"),
            fNac != null ? fNac.toLocalDate() : null,
            rs.getString("genero_persona"),
            rs.getString("nacionalidad"),
            rs.getString("genero_musical"),
            rs.getString("redes_sociales"),
            fFirm != null ? fFirm.toLocalDate() : null,
            rs.getString("estado_artista"),
            rs.getString("tipo_artista"));
    }

    private Productor mapearProductor(ResultSet rs) throws SQLException {
        return new Productor(
            rs.getInt("id_productor"),
            rs.getString("prod_nombre"),
            "", "",
            rs.getString("prod_esp"),
            rs.getDouble("tarifa_hora"));
    }
}