package ar.com.tresimplesrazones.tsr.controllers;

import ar.com.tresimplesrazones.tsr.model.Compra;
import ar.com.tresimplesrazones.tsr.service.ICompraService;
import java.time.LocalDate;
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
@RequestMapping("/tsr/compra")
public class MainCompraController {

    @Autowired
    //@Qualifier("CompraServiceImpl")
    ICompraService service;

    @GetMapping
    public ResponseEntity<List<Compra>> listarCompras() {
        return ResponseEntity.status(HttpStatus.OK).body(service.listarCompras());
    }

    /* @GetMapping("/periodos")
    public ResponseEntity<List<Compra>> listarComprasEntreFechas(@RequestBody LocalDate fechaInicio, LocalDate fechaFin){
        return ResponseEntity.status(HttpStatus.OK).body(service.comprasEnPeriodo(fechaInicio, fechaFin));
    }
    Al usar @RequestBody, Spring espera un solo objeto en el cuerpo de la solicitud. Dado que fechaInicio y fechaFin son 
    parámetros separados, la forma actual no funcionará y es la razón del error de Required request body is missing.    */
    @GetMapping("/periodo/{fechaInicio}/{fechaFin}") //Formato en Swagger: 2024-10-07
    public ResponseEntity<List<Compra>> listarComprasEntreFechas(@PathVariable LocalDate fechaInicio, @PathVariable LocalDate fechaFin) {
        return ResponseEntity.status(HttpStatus.OK).body(service.comprasEnPeriodo(fechaInicio, fechaFin));
    }

    @PostMapping
    public ResponseEntity<String> cargarCompra(@RequestBody Compra compra) {
        service.cargarCompra(compra);
        return ResponseEntity.status(HttpStatus.CREATED).body("compra cargada");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> modificarCompra(@PathVariable("id") Long id, @RequestBody Compra compra) {
        if (service.modificarCompra(id, compra)) {
            return ResponseEntity.status(HttpStatus.OK).body("compra modificada");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("compra no encontrada");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCompra(@PathVariable("id") Long id) {
        if (service.eliminarCompra(id)) {
            return ResponseEntity.status(HttpStatus.OK).body("compra eliminada");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("compra no encontrada");
        }
    }

}
