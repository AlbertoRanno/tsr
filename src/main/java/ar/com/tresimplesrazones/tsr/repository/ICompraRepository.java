package ar.com.tresimplesrazones.tsr.repository;

import ar.com.tresimplesrazones.tsr.model.Compra;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICompraRepository extends JpaRepository<Compra, Long> {

    // Método que recupera el listado de compras entre dos fechas ingresadas
    List<Compra> findAllByFechaDeCompraBetween(LocalDate fechaInicio, LocalDate fechaFin);
}

