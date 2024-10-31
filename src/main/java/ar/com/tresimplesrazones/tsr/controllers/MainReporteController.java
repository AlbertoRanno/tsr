package ar.com.tresimplesrazones.tsr.controllers;

import ar.com.tresimplesrazones.tsr.enums.TipoProducto;
import ar.com.tresimplesrazones.tsr.service.impl.ReporteService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reportes")
public class MainReporteController {

    @Autowired
    ReporteService reporteService;

    @GetMapping("/rentabilidad-total")
    public ResponseEntity<Long> obtenerRentabilidadTotal() {
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.calcularRentabilidadTotal());
    }
    @GetMapping("/rentabilidad-total-usd")
    public ResponseEntity<Double> obtenerRentabilidadTotalEnDolares() {
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.calcularRentabilidadTotalEnDolares());
    }

    @GetMapping("/rentabilidad-cpv")
    public ResponseEntity<Long> obtenerRentabilidadCPV() {
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.calcularGananciaTotalCPV());
    }
    @GetMapping("/rentabilidad-cpv-usd")
    public ResponseEntity<Double> obtenerRentabilidadCPVEnDolares() {
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.calcularGananciaTotalCPVEnDolares());
    }

    @GetMapping("/rentabilidad/{tipo}")
    public ResponseEntity<Long> obtenerRentabilidadPorTipo(@PathVariable("tipo") TipoProducto tipo) {
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.calcularGananciaPorTipoProductoCPV(tipo));
    }
    @GetMapping("/rentabilidad-usd/{tipo}")
    public ResponseEntity<Double> obtenerRentabilidadPorTipoEnDolares(@PathVariable("tipo") TipoProducto tipo) {
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.calcularGananciaPorTipoProductoCPVEnDolares(tipo));
    }

    @GetMapping("rentabilidad/{tipo}/{fechaInicio}/{fechaFin}")
    public ResponseEntity<Long> obtenerRentabilidadPorTipoEnPeriodo(@PathVariable TipoProducto tipo, @PathVariable LocalDate fechaInicio, @PathVariable LocalDate fechaFin) {
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.calcularGananciaPorTipoProductoCPVEnPeriodo(tipo, fechaInicio, fechaFin));
    }
    @GetMapping("rentabilidad-usd/{tipo}/{fechaInicio}/{fechaFin}")
    public ResponseEntity<Double> obtenerRentabilidadPorTipoEnPeriodoEnDolares(@PathVariable TipoProducto tipo, @PathVariable LocalDate fechaInicio, @PathVariable LocalDate fechaFin) {
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.calcularGananciaPorTipoProductoCPVEnPeriodoEnDolares(tipo, fechaInicio, fechaFin));
    }
}
