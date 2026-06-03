package dao;

import util.ConexionDB;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class FaseDAO {

    /**
     * Devuelve ID_FASE → etiqueta legible.
     * La etiqueta combina el nombre del TIPO_FASE con el id de la fase,
     * porque FASE_PRODUCCION no tiene nombre propio.
     */
    public Map<Integer, String> listarMapa() throws SQLException {
        Map<Integer, String> mapa = new LinkedHashMap<>();
        String sql =
            "SELECT f.id_fase, tf.nombre AS tipo_fase, f.id_cancion " +
            "FROM fase_produccion f " +
            "LEFT JOIN tipo_fase tf ON f.id_tipo_fase = tf.id_tipo_fase " +
            "ORDER BY f.id_fase";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idFase   = rs.getInt("id_fase");
                String tipo  = rs.getString("tipo_fase");
                int idCancion= rs.getInt("id_cancion");
                String etiqueta = (tipo != null ? tipo : "Fase")
                                + " (canción #" + idCancion + ")";
                mapa.put(idFase, etiqueta);
            }
        }
        return mapa;
    }
}
