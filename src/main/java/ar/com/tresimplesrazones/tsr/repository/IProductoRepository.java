package ar.com.tresimplesrazones.tsr.repository;

import ar.com.tresimplesrazones.tsr.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductoRepository extends JpaRepository<Producto, Long> {
    
    Producto findByNombre(String nombre);

}
