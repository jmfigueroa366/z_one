
package dao;

import model.ItemCatalogo;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CatalogoDAO {

    private final String tabla;
    private final String colId;
    private final String colNombre;
    private final String colDescripcion;

    /**
     * @param tabla          nombre fisico de la tabla
     * @param colId          columna PK
     * @param colNombre      columna del nombre/etiqueta
     * @param colDescripcion columna de descripcion (puede ser null si no existe)
     */
    public CatalogoDAO(String tabla, String colId, String colNombre, String colDescripcion) {
        this.tabla          = tabla;
        this.colId          = colId;
        this.colNombre      = colNombre;
        this.colDescripcion = colDescripcion;
    }

    public List<ItemCatalogo> listar() throws SQLException {
        String sql = "SELECT " + colId + ", " + colNombre +
                     (colDescripcion != null ? ", " + colDescripcion : "") +
                     " FROM " + tabla + " ORDER BY " + colNombre;
        List<ItemCatalogo> out = new ArrayList<>();
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ItemCatalogo it = new ItemCatalogo(
                    rs.getInt(1),
                    rs.getString(2),
                    colDescripcion != null ? rs.getString(3) : null
                );
                out.add(it);
            }
        }
        return out;
    }

    public ItemCatalogo buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + colId + ", " + colNombre +
                     (colDescripcion != null ? ", " + colDescripcion : "") +
                     " FROM " + tabla + " WHERE " + colId + " = ?";
        try (Connection c = ConexionDB.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new ItemCatalogo(
                    rs.getInt(1), rs.getString(2),
                    colDescripcion != null ? rs.getString(3) : null);
            }
        }
        return null;
    }

    // ── Factories listas ─────────────────────────────────────────────
    public static CatalogoDAO generosMusicales() {
        return new CatalogoDAO("genero_musicales", "id_genero", "nombre", "descripcion");
    }
    public static CatalogoDAO nacionalidades() {
        return new CatalogoDAO("nacionalidades", "id_nacionalidad", "nombre", null);
    }
    public static CatalogoDAO generosPersona() {
        return new CatalogoDAO("genero_persona", "id_genero_persona", "descripcion", null);
    }
    public static CatalogoDAO idiomas() {
        return new CatalogoDAO("idiomas", "id_idiomas", "nombre", null);
    }
    public static CatalogoDAO estadosArtPro() {
        return new CatalogoDAO("estados_art_pro", "id_estado", "nombre", null);
    }
    public static CatalogoDAO tiposArtista() {
        return new CatalogoDAO("tipo_artista", "id_tipo_artista", "nombre", "descripcion");
    }
    public static CatalogoDAO tiposEvento() {
        return new CatalogoDAO("tipos_eventos", "id_tipo_evento", "nombre", "descripcion");
    }
    public static CatalogoDAO tiposVersion() {
        return new CatalogoDAO("tipo_version", "id_tipo_version", "nombre", "descripcion");
    }
    public static CatalogoDAO roles() {
        return new CatalogoDAO("roles", "id_rol", "nombre_rol", null);
    }
}