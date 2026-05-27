package dao;

import model.Productor;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductorDAO — acceso a datos para la tabla PRODUCTOR en Oracle.
 * Columnas reales: id_productor, id_usuario, nombre, fecha_nacimiento,
 * genero, nacionalidad, especialidad, experiencia, tarifa_hora,
 * estado_productor, fecha_firma
 */
public class ProductorDAO {

    private static final String SQL_LISTAR =
        "SELECT ID_PRODUCTOR, ID_USUARIO, NOMBRE, FECHA_NACIMIENTO, GENERO, " +
        "NACIONALIDAD, ESPECIALIDAD, EXPERIENCIA, TARIFA_HORA, " +
        "ESTADO_PRODUCTOR, FECHA_FIRMA " +
        "FROM PRODUCTOR ORDER BY ID_PRODUCTOR";

    private static final String SQL_BUSCAR =
        "SELECT ID_PRODUCTOR, ID_USUARIO, NOMBRE, FECHA_NACIMIENTO, GENERO, " +
        "NACIONALIDAD, ESPECIALIDAD, EXPERIENCIA, TARIFA_HORA, " +
        "ESTADO_PRODUCTOR, FECHA_FIRMA " +
        "FROM PRODUCTOR WHERE LOWER(NOMBRE) LIKE ? " +
        "OR LOWER(ESPECIALIDAD) LIKE ? OR LOWER(NACIONALIDAD) LIKE ? " +
        "ORDER BY ID_PRODUCTOR";

    private static final String SQL_INSERTAR =
        "INSERT INTO PRODUCTOR (NOMBRE, ESPECIALIDAD, EXPERIENCIA, " +
        "TARIFA_HORA, NACIONALIDAD, ESTADO_PRODUCTOR) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
        "UPDATE PRODUCTOR SET NOMBRE=?, ESPECIALIDAD=?, EXPERIENCIA=?, " +
        "TARIFA_HORA=?, NACIONALIDAD=?, ESTADO_PRODUCTOR=? " +
        "WHERE ID_PRODUCTOR=?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM PRODUCTOR WHERE ID_PRODUCTOR=?";

    // ── CRUD ──────────────────────────────────────────────────────────

    public List<Productor> listarTodos() {
        List<Productor> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar productores: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Productor> buscarPorTexto(String texto) {
        List<Productor> lista = new ArrayList<>();
        String patron = "%" + texto.toLowerCase() + "%";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar productores: " + e.getMessage(), e);
        }
        return lista;
    }

    public Productor insertar(Productor p) {
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(
                 SQL_INSERTAR, new String[]{"ID_PRODUCTOR"})) {
            asignarParams(ps, p);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setIdProductor(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar productor: " + e.getMessage(), e);
        }
        return p;
    }

    public void actualizar(Productor p) {
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {
            asignarParams(ps, p);
            ps.setInt(7, p.getIdProductor());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar productor: " + e.getMessage(), e);
        }
    }

    public void eliminar(int id) {
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar productor: " + e.getMessage(), e);
        }
    }

    // ── Helpers privados ──────────────────────────────────────────────

    private Productor mapear(ResultSet rs) throws SQLException {
        return new Productor(
            rs.getInt("ID_PRODUCTOR"),
            rs.getInt("ID_USUARIO"),
            rs.getString("NOMBRE"),
            rs.getString("FECHA_NACIMIENTO"),
            rs.getString("GENERO"),
            rs.getString("NACIONALIDAD"),
            rs.getString("ESPECIALIDAD"),
            rs.getString("EXPERIENCIA"),
            rs.getDouble("TARIFA_HORA"),
            rs.getString("ESTADO_PRODUCTOR"),
            rs.getString("FECHA_FIRMA")
        );
    }

    private void asignarParams(PreparedStatement ps, Productor p) throws SQLException {
        ps.setString(1, p.getNombre());
        ps.setString(2, p.getEspecialidad());
        ps.setString(3, p.getExperiencia());
        ps.setDouble(4, p.getTarifaHora());
        ps.setString(5, p.getNacionalidad());
        ps.setString(6, p.getEstadoProductor() != null ? p.getEstadoProductor() : "Disponible");
    }
}