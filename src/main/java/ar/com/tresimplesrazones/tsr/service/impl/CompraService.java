package ar.com.tresimplesrazones.tsr.service.impl;

import ar.com.tresimplesrazones.tsr.model.Compra;
import ar.com.tresimplesrazones.tsr.repository.ICompraRepository;
import ar.com.tresimplesrazones.tsr.service.ICompraService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
//@Qualifier("CompraServiceImpl")
public class CompraService implements ICompraService {

    @Autowired
    ICompraRepository repo;

    @Override
    public List<Compra> listarCompras() {
        return repo.findAll();
    }

    @Override
    public void cargarCompra(Compra compra) {
        repo.save(compra);
    }

    @Override
    public boolean modificarCompra(Long Id, Compra compra) {
        if (repo.existsById(Id)) {
            compra.setId(Id);
            repo.save(compra);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean eliminarCompra(Long Id) {
        if (repo.existsById(Id)) {
            repo.deleteById(Id);
            return true;
        } else {
            return false;
        }
    }

}
