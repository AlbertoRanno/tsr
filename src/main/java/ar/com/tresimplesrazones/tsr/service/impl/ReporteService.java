package ar.com.tresimplesrazones.tsr.service.impl;

import ar.com.tresimplesrazones.tsr.TsrApplication;
import ar.com.tresimplesrazones.tsr.model.Compra;
import ar.com.tresimplesrazones.tsr.model.Venta;
import ar.com.tresimplesrazones.tsr.repository.ICompraRepository;
import ar.com.tresimplesrazones.tsr.repository.IVentaRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//Por buena práctica, cree una clase aparte para los reportes, y mantengo limpia la clase producto. (=> También tendré que crear un nuevo controlador.)
//Y en la capa de serviceImpl, que es donde se alojan los cálculos extras, como los reportes, u operaciones complejas que requieren datos de varias entidades
@Service
public class ReporteService {

    private static Logger LOG = LoggerFactory.getLogger(TsrApplication.class);

    @Autowired
    IVentaRepository ventaRepo;

    @Autowired
    ICompraRepository compraRepo;

    public Long calcularRentabilidadTotal() { //Considera el costo de los productos no vendidos aún, por lo que la ganancia arrojada será menor
        //A la suma de todo los importes de ventas, le resto la suma de todos los importes de compra
        List<Venta> ventas = ventaRepo.findAll();
        List<Compra> compras = compraRepo.findAll();

        //Calculo los ingresos totales por las ventas
        Long totalVentas = 0L;
        //Recorro todas las ventas
        for (Venta venta : ventas) {
            //Calculo el total en efvo ingresado POR venta (Precio * Cantidad)
            Long totalPorVenta = venta.getPrecioDeVenta() * venta.getCantidadVendida();
            //Voy actualizando el total
            totalVentas += totalPorVenta;
        }
        LOG.info("Total ventas: " + totalVentas.toString());

        Long totalCompras = 0L;
        for (Compra compra : compras) {
            Long totalPorCompra = compra.getPrecioDeCompra() * compra.getCantidadComprada();
            totalCompras += totalPorCompra;
        }
        LOG.info("Total compras: " + totalCompras.toString());
        
        return totalVentas - totalCompras;
    }

    /*Voy a aplicar, para el cálculo de la rentabilidad, el método de "Costo de los Productos Vendidos" (CPV).
    De modo que se calculen las ganancias únicamente en base al costro de los productos que efectivamente fueron vendidos, ignorando los que aún están en stock.
    Esto me parece que da un margen de ganancia más real.  */
}
