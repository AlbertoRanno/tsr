package ar.com.tresimplesrazones.tsr.service;

import ar.com.tresimplesrazones.tsr.model.Producto;
import java.util.List;

public interface IProductoService {
    
    List<Producto> listarProductos();
    
    void agregarProducto(Producto producto);
    
    Producto encontrarProducto(String nombre);
    
    boolean modificarProducto(Long id, Producto producto);
    
    boolean eliminarProducto(Long id);
    
}
