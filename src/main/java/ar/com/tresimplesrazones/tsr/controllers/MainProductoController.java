package ar.com.tresimplesrazones.tsr.controllers;

import ar.com.tresimplesrazones.tsr.model.Producto;
import ar.com.tresimplesrazones.tsr.service.IProductoService;
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
@RequestMapping("/tsr/producto")
public class MainProductoController {

    @Autowired
    //@Qualifier("ProductoServiceImpl")
    IProductoService service;

    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.status(HttpStatus.OK).body(service.listarProductos());
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<?> encontrarProducto(@PathVariable("nombre") String nombre) {
        Producto producto = service.encontrarProducto(nombre);
        if (producto != null) {
            return ResponseEntity.status(HttpStatus.OK).body(producto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el producto");
        }
    }

    @PostMapping
    public ResponseEntity<String> agregarProducto(@RequestBody Producto producto) {
        service.agregarProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Producto agregado");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> modificarProducto(@PathVariable("id") Long id, @RequestBody Producto producto) {
        if (service.modificarProducto(id, producto)) {
            return ResponseEntity.status(HttpStatus.OK).body("Producto modificado");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProducto(@PathVariable("id") Long id) {
        if (service.eliminarProducto(id)) {
            return ResponseEntity.status(HttpStatus.OK).body("Producto eliminado");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        }
    }
}
