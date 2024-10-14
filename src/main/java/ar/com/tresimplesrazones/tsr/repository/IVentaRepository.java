package ar.com.tresimplesrazones.tsr.repository;

import ar.com.tresimplesrazones.tsr.model.Venta;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVentaRepository extends JpaRepository<Venta, Long> {
    
    List<Venta> findAllByFechaDeVentaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
}
