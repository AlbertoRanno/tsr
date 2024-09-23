package ar.com.tresimplesrazones.tsr.service.impl;

import ar.com.tresimplesrazones.tsr.model.Producto;
import ar.com.tresimplesrazones.tsr.repository.IProductoRepository;
import ar.com.tresimplesrazones.tsr.service.IProductoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
//@Qualifier("ProductoServiceImpl")
public class ProductoService implements IProductoService {

    @Autowired
    IProductoRepository repo;

    @Override
    public List<Producto> listarProductos() {
        return repo.findAll();
    }

    @Override
    public void agregarProducto(Producto producto) {
        repo.save(producto);
    }

    @Override
    public Producto encontrarProducto(String nombre) {
        return repo.findByNombre(nombre);
    }

    @Override
    public boolean modificarProducto(Long id, Producto producto) {
        if (repo.existsById(id)) {
            producto.setId(id);
            repo.save(producto);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean eliminarProducto(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
