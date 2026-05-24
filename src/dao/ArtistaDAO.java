package dao;

import model.Artista;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ArtistaDAO — acceso a datos para PERFIL_ARTISTA en Oracle.
 * Tabla real: PERFIL_ARTISTA  |  PK: ID_ARTISTA
 */
public class ArtistaDAO {

    // ── Columnas a seleccionar (evita SELECT *) ──────────────────────
    private static final String COLS =
        "ID_ARTISTA, ID_USUARIO, NOMBRE_ARTISTA, NOMBRE_REAL, " +
        "FECHA_NACIMIENTO, GENERO, NACIONALIDAD, GENERO_MUSICAL, " +
        "REDES_SOCIALES, FECHA_FIRMA, ESTADO_ARTISTA, TIPO_ARTISTA";

    private static final String SQL_LISTAR_TODOS =
        "SELECT " + COLS + " FROM PERFIL_ARTISTA ORDER BY ID_ARTISTA";

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT " + COLS + " FROM PERFIL_ARTISTA WHERE ID_ARTISTA = ?";

    private static final String SQL_INSERTAR =
        "INSERT INTO PERFIL_ARTISTA " +
        "(ID_USUARIO, NOMBRE_ARTISTA, NOMBRE_REAL, FECHA_NACIMIENTO, " +
        " GENERO, NACIONALIDAD, GENERO_MUSICAL, REDES_SOCIALES, " +
        " FECHA_FIRMA, ESTADO_ARTISTA, TIPO_ARTISTA) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
        "UPDATE PERFIL_ARTISTA SET " +
        "ID_USUARIO=?, NOMBRE_ARTISTA=?, NOMBRE_REAL=?, FECHA_NACIMIENTO=?, " +
        "GENERO=?, NACIONALIDAD=?, GENERO_MUSICAL=?, REDES_SOCIALES=?, " +
        "FECHA_FIRMA=?, ESTADO_ARTISTA=?, TIPO_ARTISTA=? " +
        "WHERE ID_ARTISTA=?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM PERFIL_ARTISTA WHERE ID_ARTISTA = ?";

    private static final String SQL_BUSCAR_POR_TEXTO =
        "SELECT " + COLS + " FROM PERFIL_ARTISTA " +
        "WHERE LOWER(NOMBRE_ARTISTA) LIKE ? " +
        "   OR LOWER(GENERO_MUSICAL) LIKE ? " +
        "   OR LOWER(NACIONALIDAD)   LIKE ? " +
        "ORDER BY ID_ARTISTA";

    // ── CRUD ─────────────────────────────────────────────────────────

    public List<Artista> listarTodos() {
        List<Artista> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapearFila(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar artistas: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Artista> buscarPorTexto(String texto) {
        List<Artista> lista = new ArrayList<>();
        String patron = "%" + texto.toLowerCase() + "%";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_TEXTO)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearFila(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar artistas: " + e.getMessage(), e);
        }
        return lista;
    }

    public Artista insertar(Artista a) {
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(
                 SQL_INSERTAR, new String[]{"ID_ARTISTA"})) {
            asignarParametros(ps, a);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setIdArtista(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar artista: " + e.getMessage(), e);
        }
        return a;
    }

    public void actualizar(Artista a) {
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {
            asignarParametros(ps, a);
            ps.setInt(12, a.getIdArtista());   // WHERE ID_ARTISTA = ?
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar artista: " + e.getMessage(), e);
        }
    }

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

    /** Convierte una fila del ResultSet en un objeto Artista. */
    private Artista mapearFila(ResultSet rs) throws SQLException {
        // FECHA_NACIMIENTO y FECHA_FIRMA pueden ser null en la BD
        Date dbFechaNac  = rs.getDate("FECHA_NACIMIENTO");
        Date dbFechaFirma = rs.getDate("FECHA_FIRMA");

        return new Artista(
            rs.getInt("ID_ARTISTA"),
            rs.getObject("ID_USUARIO") != null ? rs.getInt("ID_USUARIO") : null,
            rs.getString("NOMBRE_ARTISTA"),
            rs.getString("NOMBRE_REAL"),
            dbFechaNac  != null ? dbFechaNac.toLocalDate()  : null,
            rs.getString("GENERO"),
            rs.getString("NACIONALIDAD"),
            rs.getString("GENERO_MUSICAL"),
            rs.getString("REDES_SOCIALES"),
            dbFechaFirma != null ? dbFechaFirma.toLocalDate() : null,
            rs.getString("ESTADO_ARTISTA"),
            rs.getString("TIPO_ARTISTA")
        );
    }

    /**
     * Asigna los 11 parámetros de INSERT/UPDATE al PreparedStatement.
     * El parámetro 12 (ID_ARTISTA para el WHERE) lo pone actualizar().
     */
    private void asignarParametros(PreparedStatement ps, Artista a) throws SQLException {
        if (a.getIdUsuario() != null) ps.setInt(1, a.getIdUsuario());
        else                          ps.setNull(1, Types.NUMERIC);

        ps.setString(2, a.getNombreArtista());
        ps.setString(3, a.getNombreReal());

        ps.setDate(4, a.getFechaNacimiento() != null
            ? Date.valueOf(a.getFechaNacimiento()) : null);

        ps.setString(5,  a.getGenero());
        ps.setString(6,  a.getNacionalidad());
        ps.setString(7,  a.getGeneroMusical());
        ps.setString(8,  a.getRedesSociales());

        ps.setDate(9, a.getFechaFirma() != null
            ? Date.valueOf(a.getFechaFirma()) : null);

        ps.setString(10, a.getEstadoArtista());
        ps.setString(11, a.getTipoArtista());
    }
}