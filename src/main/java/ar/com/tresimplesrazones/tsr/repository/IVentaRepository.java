package ar.com.tresimplesrazones.tsr.repository;

import ar.com.tresimplesrazones.tsr.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVentaRepository extends JpaRepository<Venta, Long> {
    
}
