package dao;

import util.ConexionDB;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class EstadoGrabacionDAO {

    /** Devuelve ID_ESTADO_GRABACION → NOMBRE, ordenado por id. */
    public Map<Integer, String> listarMapa() throws SQLException {
        Map<Integer, String> mapa = new LinkedHashMap<>();
        String sql = "SELECT id_estado_grabacion, nombre " +
                     "FROM estado_grabacion ORDER BY id_estado_grabacion";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mapa.put(rs.getInt("id_estado_grabacion"), rs.getString("nombre"));
            }
        }
        return mapa;
    }
}