package ar.com.tresimplesrazones.tsr.controllers;

import ar.com.tresimplesrazones.tsr.model.Venta;
import ar.com.tresimplesrazones.tsr.service.IVentaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tsr/venta")
public class MainVentaController {

    @Autowired
    //@Qualifier("VentaServiceImpl")
    IVentaService service;

    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas() {
        return ResponseEntity.status(HttpStatus.OK).body(service.listarVentas());
    }

    @PostMapping
    public ResponseEntity<String> cargarVenta(@RequestBody Venta venta) {
        service.cargarVenta(venta);
        return ResponseEntity.status(HttpStatus.CREATED).body("venta cargada");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> modificarVenta(@PathVariable("id") Long id, @RequestBody Venta venta) {
        if (service.modificarVenta(id, venta)) {
            return ResponseEntity.status(HttpStatus.OK).body("venta modificada");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("venta no encontrada");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarVenta(@PathVariable("id") Long id) {
        if (service.eliminarVenta(id)) {
            return ResponseEntity.status(HttpStatus.OK).body("venta eliminada");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("venta no encontrada");
        }
    }
}
