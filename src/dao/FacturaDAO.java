package dao;

import model.Factura;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAO {

    private static final String SELECT_BASE =
        "SELECT f.id_factura, f.numero_factura, f.id_sesion, " +
        "       f.correo_destino, f.monto_total, f.estado, " +
        "       f.fecha_emision, f.fecha_envio, f.ruta_pdf, f.observaciones, " +
        "       s.nombre_sesion, a.nombre_artista " +
        "FROM facturas f " +
        "LEFT JOIN sesion_grabaciones s ON f.id_sesion = s.id_sesion " +
        "LEFT JOIN artistas a ON s.id_artista = a.id_artista ";

    public List<Factura> listarTodos() throws SQLException {
        return ejecutar(SELECT_BASE + "ORDER BY f.fecha_emision DESC", null);
    }

    public Factura buscarPorId(int id) throws SQLException {
        List<Factura> r = ejecutar(SELECT_BASE + "WHERE f.id_factura = ?", new Object[]{id});
        return r.isEmpty() ? null : r.get(0);
    }

    public int crear(Factura f) throws SQLException {
        String sql = "INSERT INTO facturas (numero_factura, id_sesion, correo_destino, " +
                     "monto_total, estado, ruta_pdf, observaciones) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"id_factura"})) {
            ps.setString(1, f.getNumeroFactura());
            ps.setInt(2, f.getIdSesion());
            ps.setString(3, f.getCorreoDestino());
            ps.setDouble(4, f.getMontoTotal());
            ps.setString(5, f.getEstado() != null ? f.getEstado() : "EMITIDA");
            ps.setString(6, f.getRutaPdf());
            ps.setString(7, f.getObservaciones());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean marcarEnviada(int idFactura) throws SQLException {
        String sql = "UPDATE facturas SET estado = 'ENVIADA', fecha_envio = SYSTIMESTAMP " +
                     "WHERE id_factura = ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            return ps.executeUpdate() > 0;
        }
    }

    public String generarNumeroFactura() throws SQLException {
        String sql = "SELECT seq_facturas.NEXTVAL FROM dual";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return "FAC-" + String.format("%06d", rs.getInt(1));
        }
        return "FAC-" + System.currentTimeMillis();
    }

    private List<Factura> ejecutar(String sql, Object[] params) throws SQLException {
        List<Factura> out = new ArrayList<>();
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

    private Factura mapear(ResultSet rs) throws SQLException {
        Factura f = new Factura();
        f.setIdFactura(rs.getInt("id_factura"));
        f.setNumeroFactura(rs.getString("numero_factura"));
        f.setIdSesion(rs.getInt("id_sesion"));
        f.setCorreoDestino(rs.getString("correo_destino"));
        f.setMontoTotal(rs.getDouble("monto_total"));
        f.setEstado(rs.getString("estado"));
        Timestamp t1 = rs.getTimestamp("fecha_emision");
        f.setFechaEmision(t1 != null ? t1.toLocalDateTime() : null);
        Timestamp t2 = rs.getTimestamp("fecha_envio");
        f.setFechaEnvio(t2 != null ? t2.toLocalDateTime() : null);
        f.setRutaPdf(rs.getString("ruta_pdf"));
        f.setObservaciones(rs.getString("observaciones"));
        f.setNombreSesion(rs.getString("nombre_sesion"));
        f.setNombreArtista(rs.getString("nombre_artista"));
        return f;
    }
}