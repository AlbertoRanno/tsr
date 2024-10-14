package ar.com.tresimplesrazones.tsr.service;

import ar.com.tresimplesrazones.tsr.model.Venta;
import java.time.LocalDate;
import java.util.List;

public interface IVentaService {
    
    List<Venta> listarVentas();
    
    void cargarVenta(Venta venta);
    
    boolean modificarVenta(Long id, Venta venta);
    
    boolean eliminarVenta(Long id);
    
    List<Venta> listarVentasDelPeriodo(LocalDate fechaInicio, LocalDate fechaFin);
    
}
