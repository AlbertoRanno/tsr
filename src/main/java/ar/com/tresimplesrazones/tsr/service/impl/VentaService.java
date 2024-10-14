package ar.com.tresimplesrazones.tsr.service.impl;

import ar.com.tresimplesrazones.tsr.TsrApplication;
import ar.com.tresimplesrazones.tsr.exceptions.ResourceNotFoundException;
import ar.com.tresimplesrazones.tsr.model.Producto;
import ar.com.tresimplesrazones.tsr.model.Venta;
import ar.com.tresimplesrazones.tsr.repository.IProductoRepository;
import ar.com.tresimplesrazones.tsr.repository.IVentaRepository;
import ar.com.tresimplesrazones.tsr.service.IVentaService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
//@Qualifier("VentaServiceImpl")
public class VentaService implements IVentaService {

    private static Logger LOG = LoggerFactory.getLogger(TsrApplication.class);

    @Autowired
    IVentaRepository repoVenta;

    @Autowired
    IProductoRepository repoProducto;

    @Override
    public List<Venta> listarVentas() {
        return repoVenta.findAll();
    }

    @Transactional
    @Override
    public void cargarVenta(Venta venta) {
        LOG.info("entrando en cargarVenta");
        Producto producto = repoProducto.findById(venta.getProducto().getId()).orElseThrow();
        if (producto.getStock() >= venta.getCantidadVendida()) {
            producto.setStock(producto.getStock() - venta.getCantidadVendida());
        } else {
            //LOG.warn("Stock insuficiente para realizar la venta");
            throw new ResourceNotFoundException("Stock insuficiente para realizar la venta");
        }
        repoVenta.save(venta);
        repoProducto.save(producto);
    }

    @Transactional
    @Override
    public boolean modificarVenta(Long id, Venta venta) {
        if (repoVenta.existsById(id)) {
            venta.setId(id);
            Venta ventaOrig = repoVenta.findById(id).orElseThrow();
            int cantVendidaOrig = ventaOrig.getCantidadVendida();
            Producto producto = repoProducto.findById(venta.getProducto().getId()).orElseThrow();
            if (venta.getCantidadVendida() <= producto.getStock()) {
                producto.setStock(producto.getStock() + cantVendidaOrig - venta.getCantidadVendida());
                repoVenta.save(venta);
                repoProducto.save(producto);
                return true;
            } else {
                throw new ResourceNotFoundException("Stock insuficiente para realizar la venta");
            }
        } else {
            //return false;
            throw new ResourceNotFoundException("Exception - venta no encontrada");
        }
    }

    @Transactional
    @Override
    public boolean eliminarVenta(Long id) {
        if (repoVenta.existsById(id)) {
            Venta ventaOrig = repoVenta.findById(id).orElseThrow();
            int cantVendidaOrig = ventaOrig.getCantidadVendida();
            Producto producto = repoProducto.findById(ventaOrig.getProducto().getId()).orElseThrow();
            producto.setStock(producto.getStock() + cantVendidaOrig);
            repoProducto.save(producto);
            repoVenta.deleteById(id);
            return true;
        } else {
            //return false;
            throw new ResourceNotFoundException("Exception - venta no encontrada");
        }
    }

    @Override
    public List<Venta> listarVentasDelPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Venta> ventasDelPeriodo = repoVenta.findAllByFechaDeVentaBetween(fechaInicio, fechaFin);
        return ventasDelPeriodo;
    }

}
