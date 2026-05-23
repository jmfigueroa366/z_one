package dao;

import model.Productor;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductorDAO — acceso a datos para la tabla PRODUCTORES en Oracle.
 */
public class ProductorDAO {

    private static final String SQL_LISTAR_TODOS =
        "SELECT ID, NOMBRE, CORREO, TELEFONO, ESPECIALIDAD, TARIFA_HORA " +
        "FROM PRODUCTORES ORDER BY ID";

    private static final String SQL_INSERTAR =
        "INSERT INTO PRODUCTORES (NOMBRE, CORREO, TELEFONO, ESPECIALIDAD, TARIFA_HORA) " +
        "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
        "UPDATE PRODUCTORES SET NOMBRE=?, CORREO=?, TELEFONO=?, " +
        "ESPECIALIDAD=?, TARIFA_HORA=? WHERE ID=?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM PRODUCTORES WHERE ID = ?";

    private static final String SQL_BUSCAR_POR_TEXTO =
        "SELECT ID, NOMBRE, CORREO, TELEFONO, ESPECIALIDAD, TARIFA_HORA " +
        "FROM PRODUCTORES WHERE LOWER(NOMBRE) LIKE ? OR LOWER(ESPECIALIDAD) LIKE ? " +
        "OR LOWER(CORREO) LIKE ? ORDER BY ID";

    /** Retorna todos los productores ordenados por ID. */
    public List<Productor> listarTodos() {
        List<Productor> productores = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productores.add(mapearFila(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar productores: " + e.getMessage(), e);
        }
        return productores;
    }

    /** Busca productores por nombre, especialidad o correo. */
    public List<Productor> buscarPorTexto(String texto) {
        List<Productor> productores = new ArrayList<>();
        String patron = "%" + texto.toLowerCase() + "%";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_TEXTO)) {

            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productores.add(mapearFila(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar productores: " + e.getMessage(), e);
        }
        return productores;
    }

    /** Inserta un nuevo productor y retorna el objeto con el ID generado. */
    public Productor insertar(Productor productor) {
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(
                 SQL_INSERTAR, new String[]{"ID"})) {

            asignarParametros(ps, productor);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    productor.setIdentificacion(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar productor: " + e.getMessage(), e);
        }
        return productor;
    }

    /** Actualiza todos los campos de un productor existente. */
    public void actualizar(Productor productor) {
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {

            asignarParametros(ps, productor);
            ps.setInt(6, productor.getIdentificacion());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar productor: " + e.getMessage(), e);
        }
    }

    /** Elimina un productor por su ID. */
    public void eliminar(int id) {
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar productor: " + e.getMessage(), e);
        }
    }

    // ── Helpers privados ─────────────────────────────────────────────

    /** Mapea una fila del ResultSet a un objeto Productor (DRY). */
    private Productor mapearFila(ResultSet rs) throws SQLException {
        return new Productor(
            rs.getInt("ID"),
            rs.getString("NOMBRE"),
            rs.getString("CORREO"),
            rs.getString("TELEFONO"),
            rs.getString("ESPECIALIDAD"),
            rs.getDouble("TARIFA_HORA")
        );
    }

    /** Asigna los parámetros comunes al PreparedStatement. */
    private void asignarParametros(PreparedStatement ps, Productor p) throws SQLException {
        ps.setString(1, p.getNombre());
        ps.setString(2, p.getCorreo());
        ps.setString(3, p.getTelefono());
        ps.setString(4, p.getEspecialidad());
        ps.setDouble(5, p.getTarifaHora());
    }
}