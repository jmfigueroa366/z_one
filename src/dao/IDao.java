/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import java.util.List;

/**
 *
 * @author alvar
 */
public interface IDao<T> {
    
    public boolean agregar(T objeto);
    public List<T> listar();
    public T buscarPorId(int ID);
    public boolean actualizar(T objeto);
    public boolean eliminar(int ID);
    
}
