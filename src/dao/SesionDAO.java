package dao;

import model.Sesion;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SesionDAO {

    private static final String SCHEMA = "";

    //SELECT el cual consulta los registros de las sesiones actuales
   private static final String SELECT_BASE =
        "SELECT s.id_grabacion, s.id_cancion, s.id_fase, s.id_artista, s.id_productor, " +
        "       s.id_cabina, s.id_estado_grabacion, s.nombre_sesion, s.numero_sesion, " +
        "       s.fecha_grabacion, s.hora_inicio, s.hora_fin, s.notas, " +
        "       a.nombre_artista, p.nombre AS nombre_productor " +
        "FROM " + SCHEMA + "sesion_grabacion s " +
        "LEFT JOIN " + SCHEMA + "artista   a ON a.id_artista   = s.id_artista " +
        "LEFT JOIN " + SCHEMA + "productor p ON p.id_productor = s.id_productor ";

    public List<Sesion> listarTodas() throws SQLException {
        List<Sesion> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY s.fecha_grabacion DESC";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Sesion buscarPorId(int idGrabacion) throws SQLException {
        String sql = SELECT_BASE + "WHERE s.id_grabacion = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idGrabacion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public int insertar(Sesion s) throws SQLException {
        String sql = "INSERT INTO " + SCHEMA + "sesion_grabacion " +
                     "(id_cancion, id_fase, id_artista, id_productor, id_cabina, " +
                     " id_estado_grabacion, nombre_sesion, numero_sesion, " +
                     " fecha_grabacion, hora_inicio, hora_fin, notas) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID_GRABACION"})) {
            ps.setInt(1, s.getIdCancion());
            ps.setInt(2, s.getIdFase());
            ps.setInt(3, s.getIdArtista());
            ps.setInt(4, s.getIdProductor());
            ps.setInt(5, s.getIdCabina());
            ps.setInt(6, s.getIdEstadoGrabacion());
            ps.setString(7, s.getNombreSesion());
            ps.setObject(8, s.getNumeroSesion(), Types.NUMERIC);
            ps.setDate(9,      s.getFechaGrabacion() != null ? Date.valueOf(s.getFechaGrabacion()) : null);
            ps.setTimestamp(10, s.getHoraInicio()    != null ? Timestamp.valueOf(s.getHoraInicio()) : null);
            ps.setTimestamp(11, s.getHoraFin()       != null ? Timestamp.valueOf(s.getHoraFin())    : null);
            ps.setString(12, s.getNotas());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean actualizar(Sesion s) throws SQLException {
        String sql = "UPDATE " + SCHEMA + "sesion_grabacion SET " +
                     "id_cancion=?, id_fase=?, id_artista=?, id_productor=?, id_cabina=?, " +
                     "id_estado_grabacion=?, nombre_sesion=?, numero_sesion=?, " +
                     "fecha_grabacion=?, hora_inicio=?, hora_fin=?, notas=? " +
                     "WHERE id_grabacion=?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, s.getIdCancion());
            ps.setInt(2, s.getIdFase());
            ps.setInt(3, s.getIdArtista());
            ps.setInt(4, s.getIdProductor());
            ps.setInt(5, s.getIdCabina());
            ps.setInt(6, s.getIdEstadoGrabacion());
            ps.setString(7, s.getNombreSesion());
            ps.setObject(8, s.getNumeroSesion(), Types.NUMERIC);
            ps.setDate(9,      s.getFechaGrabacion() != null ? Date.valueOf(s.getFechaGrabacion()) : null);
            ps.setTimestamp(10, s.getHoraInicio()    != null ? Timestamp.valueOf(s.getHoraInicio()) : null);
            ps.setTimestamp(11, s.getHoraFin()       != null ? Timestamp.valueOf(s.getHoraFin())    : null);
            ps.setString(12, s.getNotas());
            ps.setInt(13, s.getIdGrabacion());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idGrabacion) throws SQLException {
        String sql = "DELETE FROM " + SCHEMA + "sesion_grabacion WHERE id_grabacion=?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idGrabacion);
            return ps.executeUpdate() > 0;
        }
    }

    private Sesion mapear(ResultSet rs) throws SQLException {
        Sesion s = new Sesion();
        s.setIdGrabacion(rs.getInt("id_grabacion"));
        s.setIdCancion(rs.getInt("id_cancion"));
        s.setIdFase(rs.getInt("id_fase"));
        s.setIdArtista(rs.getInt("id_artista"));
        s.setNombreArtista(rs.getString("nombre_artista"));
        s.setIdProductor(rs.getInt("id_productor"));
        s.setNombreProductor(rs.getString("nombre_productor"));
        s.setIdCabina(rs.getInt("id_cabina"));
        s.setIdEstadoGrabacion(rs.getInt("id_estado_grabacion"));
        s.setNombreSesion(rs.getString("nombre_sesion"));
        s.setNumeroSesion(rs.getInt("numero_sesion"));
        Date f = rs.getDate("fecha_grabacion");
        s.setFechaGrabacion(f != null ? f.toLocalDate() : null);
        Timestamp hi = rs.getTimestamp("hora_inicio");
        s.setHoraInicio(hi != null ? hi.toLocalDateTime() : null);
        Timestamp hf = rs.getTimestamp("hora_fin");
        s.setHoraFin(hf != null ? hf.toLocalDateTime() : null);
        s.setNotas(rs.getString("notas"));
        return s;
    }
}