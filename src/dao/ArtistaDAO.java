package dao;

import model.Artista;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ArtistaDAO — acceso a datos para la tabla ARTISTAS en Oracle.
 *
 * Principio 1 (Nombres significativos): métodos con verbos
 *   que describen exactamente la operación SQL que realizan.
 * Principio 2 (SRP): solo se ocupa de persistencia; no tiene
 *   lógica de negocio ni código de interfaz.
 * Principio 4 (Funciones pequeñas): cada método hace una sola
 *   cosa y delega la construcción del objeto a mapearFila().
 * Principio 5 (No repetir — DRY): mapearFila() centraliza el
 *   mapeo ResultSet → Artista para no duplicarlo en cada query.
 * Principio 6 (Manejo de errores): todas las excepciones SQL
 *   se propagan como RuntimeException con mensaje descriptivo.
 */
public class ArtistaDAO {

    // ── Queries SQL como constantes (evita magic strings) ────────────
    private static final String SQL_LISTAR_TODOS =
        "SELECT ID, NOMBRE, CORREO, TELEFONO, GENERO, PAIS, CANCIONES, ESTADO " +
        "FROM ARTISTAS ORDER BY ID";

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT ID, NOMBRE, CORREO, TELEFONO, GENERO, PAIS, CANCIONES, ESTADO " +
        "FROM ARTISTAS WHERE ID = ?";

    private static final String SQL_INSERTAR =
        "INSERT INTO ARTISTAS (NOMBRE, CORREO, TELEFONO, GENERO, PAIS, CANCIONES, ESTADO) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
        "UPDATE ARTISTAS SET NOMBRE=?, CORREO=?, TELEFONO=?, GENERO=?, " +
        "PAIS=?, CANCIONES=?, ESTADO=? WHERE ID=?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM ARTISTAS WHERE ID = ?";

    private static final String SQL_BUSCAR_POR_TEXTO =
        "SELECT ID, NOMBRE, CORREO, TELEFONO, GENERO, PAIS, CANCIONES, ESTADO " +
        "FROM ARTISTAS WHERE LOWER(NOMBRE) LIKE ? OR LOWER(GENERO) LIKE ? " +
        "OR LOWER(PAIS) LIKE ? ORDER BY ID";

    // ── Operaciones CRUD ─────────────────────────────────────────────

    /**
     * Retorna todos los artistas ordenados por ID.
     */
    public List<Artista> listarTodos() {
        List<Artista> artistas = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                artistas.add(mapearFila(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar artistas: " + e.getMessage(), e);
        }
        return artistas;
    }

    /**
     * Busca artistas cuyo nombre, género o país contengan el texto dado.
     */
    public List<Artista> buscarPorTexto(String texto) {
        List<Artista> artistas = new ArrayList<>();
        String patron = "%" + texto.toLowerCase() + "%";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_TEXTO)) {

            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    artistas.add(mapearFila(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar artistas: " + e.getMessage(), e);
        }
        return artistas;
    }

    /**
     * Inserta un nuevo artista y retorna el objeto con el ID generado por Oracle.
     */
    public Artista insertar(Artista artista) {
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(
                 SQL_INSERTAR, new String[]{"ID"})) {

            asignarParametros(ps, artista);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    artista.setIdentificacion(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar artista: " + e.getMessage(), e);
        }
        return artista;
    }

    /**
     * Actualiza todos los campos de un artista existente.
     */
    public void actualizar(Artista artista) {
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {

            asignarParametros(ps, artista);
            ps.setInt(8, artista.getIdentificacion());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar artista: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un artista por su ID.
     */
    public void eliminar(int id) {
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar artista: " + e.getMessage(), e);
        }
    }

    // ── Helpers privados ─────────────────────────────────────────────

    /**
     * Mapea una fila del ResultSet a un objeto Artista.
     * Centraliza el mapeo para no repetirlo en cada query (DRY).
     */
    private Artista mapearFila(ResultSet rs) throws SQLException {
        return new Artista(
            rs.getInt("ID"),
            rs.getString("NOMBRE"),
            rs.getString("CORREO"),
            rs.getString("TELEFONO"),
            rs.getString("GENERO"),
            rs.getString("PAIS"),
            rs.getInt("CANCIONES"),
            rs.getString("ESTADO")
        );
    }

    /**
     * Asigna los parámetros comunes del artista al PreparedStatement.
     * Evita repetir los setString/setInt en insertar() y actualizar().
     */
    private void asignarParametros(PreparedStatement ps, Artista artista) throws SQLException {
        ps.setString(1, artista.getNombre());
        ps.setString(2, artista.getCorreo());
        ps.setString(3, artista.getTelefono());
        ps.setString(4, artista.getGenero());
        ps.setString(5, artista.getPais());
        ps.setInt   (6, artista.getCantidadCanciones());
        ps.setString(7, artista.getEstado());
    }
}