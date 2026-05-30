package dao;

import model.Grabacion;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GrabacionDAO {


private static final String SELECT_BASE =
    "SELECT g.id_grabacion_audio, g.id_sesion, s.nombre_sesion, " +
    "       g.nombre_archivo, g.ruta_archivo, g.duracion_segundos, " +
    "       g.tamano_kb, g.fecha_grabacion, g.observaciones " +
    "FROM grabaciones g " +
    "LEFT JOIN sesion_grabaciones s ON g.id_sesion = s.id_sesion "; // id_session -> id_sesion

    public List<Grabacion> listarTodos() throws SQLException {
        return ejecutar(SELECT_BASE + "ORDER BY g.fecha_grabacion DESC", null);
    }

    public List<Grabacion> listarPorSesion(int idSesion) throws SQLException {
        return ejecutar(SELECT_BASE +
                "WHERE g.id_sesion = ? ORDER BY g.fecha_grabacion DESC",
                new Object[]{idSesion});
    }

public Grabacion buscarPorId(int id) throws SQLException {
    List<Grabacion> r = ejecutar(SELECT_BASE + "WHERE g.id_grabacion_audio = ?",
            new Object[]{id});
    return r.isEmpty() ? null : r.get(0);
}
 public int crear(Grabacion g) throws SQLException {
    String sqlSeq = "SELECT SEQ_GRABACIONES.NEXTVAL FROM DUAL";
    String sqlInsert = "INSERT INTO grabaciones (id_grabacion_audio, id_sesion, nombre_archivo, " +
                       "ruta_archivo, duracion_segundos, tamano_kb, observaciones) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection c = ConexionDB.getConexion()) {
        int nuevoId;
        try (PreparedStatement ps = c.prepareStatement(sqlSeq);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            nuevoId = rs.getInt(1);
        }
        try (PreparedStatement ps = c.prepareStatement(sqlInsert)) {
            ps.setInt(1, nuevoId);
            ps.setInt(2, g.getIdSesion());
            ps.setString(3, g.getNombreArchivo());
            ps.setString(4, g.getRutaArchivo());
            ps.setInt(5, g.getDuracionSegundos());
            ps.setLong(6, g.getTamanoKb());
            ps.setString(7, g.getObservaciones());
            ps.executeUpdate();
        }
        return nuevoId;
    }
}

 public boolean eliminar(int id) throws SQLException {
    String sql = "DELETE FROM grabaciones WHERE id_grabacion_audio = ?";
    try (Connection c = ConexionDB.getConexion();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, id);
        return ps.executeUpdate() > 0;
    }
}

    private List<Grabacion> ejecutar(String sql, Object[] params) throws SQLException {
        List<Grabacion> out = new ArrayList<>();
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

  private Grabacion mapear(ResultSet rs) throws SQLException {
    Grabacion g = new Grabacion();
    g.setIdGrabacion(rs.getInt("id_grabacion_audio"));
        g.setIdSesion(rs.getInt("id_sesion"));
        g.setNombreSesion(rs.getString("nombre_sesion"));
        g.setNombreArchivo(rs.getString("nombre_archivo"));
        g.setRutaArchivo(rs.getString("ruta_archivo"));
        g.setDuracionSegundos(rs.getInt("duracion_segundos"));
        g.setTamanoKb(rs.getLong("tamano_kb"));
        Timestamp ts = rs.getTimestamp("fecha_grabacion");
        g.setFechaGrabacion(ts != null ? ts.toLocalDateTime() : null);
        g.setObservaciones(rs.getString("observaciones"));
        return g;
    }
}