package ar.com.tresimplesrazones.tsr.controllers;

import ar.com.tresimplesrazones.tsr.enums.TipoProducto;
import ar.com.tresimplesrazones.tsr.service.impl.ReporteService;
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

    @GetMapping("/rentabilidad-cpv")
    public ResponseEntity<Long> obtenerRentabilidadCPV() {
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.calcularGananciaTotalCPV());
    }

    @GetMapping("/rentabilidad/{tipo}")
    public ResponseEntity<Long> obtenerRentabilidadPorTipo(@PathVariable("tipo") TipoProducto tipo) {
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.calcularGananciaPorTipoProductoCPV(tipo));
    }
}
