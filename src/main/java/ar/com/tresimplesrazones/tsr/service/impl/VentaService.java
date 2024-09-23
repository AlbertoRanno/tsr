package ar.com.tresimplesrazones.tsr.service.impl;

import ar.com.tresimplesrazones.tsr.model.Venta;
import ar.com.tresimplesrazones.tsr.repository.IVentaRepository;
import ar.com.tresimplesrazones.tsr.service.IVentaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
//@Qualifier("VentaServiceImpl")
public class VentaService implements IVentaService {

    @Autowired
    IVentaRepository repo;

    @Override
    public List<Venta> listarVentas() {
        return repo.findAll();
    }

    @Override
    public void cargarVenta(Venta venta) {
        repo.save(venta);
    }

    @Override
    public boolean modificarVenta(Long id, Venta venta) {
        if (repo.existsById(id)) {
            venta.setId(id);
            repo.save(venta);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean eliminarVenta(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
