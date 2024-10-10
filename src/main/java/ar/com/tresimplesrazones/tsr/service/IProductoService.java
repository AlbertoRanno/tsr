package ar.com.tresimplesrazones.tsr.service;

import ar.com.tresimplesrazones.tsr.enums.TipoProducto;
import ar.com.tresimplesrazones.tsr.model.Producto;
import java.util.List;

public interface IProductoService {
    
    List<Producto> listarProductos();
    
    void agregarProducto(Producto producto);
    
    Producto encontrarProducto(String nombre);
    
    List<Producto> encontrarPorTipo(TipoProducto tipo);
    
    boolean modificarProducto(Long id, Producto producto);
    
    boolean eliminarProducto(Long id);
    
}
