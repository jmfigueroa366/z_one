
package dao;

import java.util.List;

/**
 *
 * @author alvar
 */
public interface IDao<T> {
    //**
    //metodos a implementar en las clases DAO
    public boolean agregar(T objeto);
    public List<T> listar();
    public T buscarPorId(int ID);
    public boolean actualizar(T objeto);
    public boolean eliminar(int ID);
    
}
