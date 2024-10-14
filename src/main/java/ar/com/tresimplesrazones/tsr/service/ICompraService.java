package ar.com.tresimplesrazones.tsr.service;

import ar.com.tresimplesrazones.tsr.model.Compra;
import java.time.LocalDate;
import java.util.List;

public interface ICompraService {
    
    List<Compra> listarCompras();
    
    void cargarCompra(Compra compra);
    
    boolean modificarCompra(Long Id, Compra compra);
    
    boolean eliminarCompra(Long Id);
    
    List<Compra> comprasEnPeriodo(LocalDate fechaInicio, LocalDate fechaFin);
}
