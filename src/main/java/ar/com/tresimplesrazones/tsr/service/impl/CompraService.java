package ar.com.tresimplesrazones.tsr.service.impl;

import ar.com.tresimplesrazones.tsr.model.Compra;
import ar.com.tresimplesrazones.tsr.model.Producto;
import ar.com.tresimplesrazones.tsr.repository.ICompraRepository;
import ar.com.tresimplesrazones.tsr.repository.IProductoRepository;
import ar.com.tresimplesrazones.tsr.service.ICompraService;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
//@Qualifier("CompraServiceImpl")
public class CompraService implements ICompraService {

    @Autowired
    ICompraRepository repoCompra;

    @Autowired
    IProductoRepository repoProducto;

    @Override
    public List<Compra> listarCompras() {
        return repoCompra.findAll();
    }

    @Transactional
    @Override
    public void cargarCompra(Compra compra) {
        Producto producto = repoProducto.findById(compra.getProducto().getId()).orElseThrow();
        producto.setStock(producto.getStock() + compra.getCantidadComprada());
        repoCompra.save(compra);
        repoProducto.save(producto);
    }

    @Transactional
    @Override
    public boolean modificarCompra(Long Id, Compra compra) {
        if (repoCompra.existsById(Id)) {
            compra.setId(Id);
            Compra compraOrig = repoCompra.findById(Id).orElseThrow();
            int cantCompradaOrig = compraOrig.getCantidadComprada();
            Producto producto = repoProducto.findById(compra.getProducto().getId()).orElseThrow();
            producto.setStock(producto.getStock() -cantCompradaOrig + compra.getCantidadComprada());
            repoCompra.save(compra);
            repoProducto.save(producto);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    @Override
    public boolean eliminarCompra(Long Id) {
        if (repoCompra.existsById(Id)) {
            Compra compraOrig = repoCompra.findById(Id).orElseThrow();
            int cantCompradaOrig = compraOrig.getCantidadComprada();
            Producto producto = repoProducto.findById(compraOrig.getProducto().getId()).orElseThrow();
            producto.setStock(producto.getStock() -cantCompradaOrig);
            repoProducto.save(producto);
            repoCompra.deleteById(Id);
            return true;
        } else {
            return false;
        }
    }

}
