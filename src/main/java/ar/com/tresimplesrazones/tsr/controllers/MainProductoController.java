package ar.com.tresimplesrazones.tsr.controllers;

import ar.com.tresimplesrazones.tsr.TsrApplication;
import ar.com.tresimplesrazones.tsr.model.Producto;
import ar.com.tresimplesrazones.tsr.service.IProductoService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static Logger LOG = LoggerFactory.getLogger(TsrApplication.class);

    @Autowired
    //@Qualifier("ProductoServiceImpl")
    IProductoService service;

    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        LOG.info("entrando en listarProductos");
        return ResponseEntity.status(HttpStatus.OK).body(service.listarProductos());
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<?> encontrarProducto(@PathVariable("nombre") String nombre) {
        LOG.info("entrando en encontrarProducto");
        Producto producto = service.encontrarProducto(nombre);
        if (producto != null) {
            return ResponseEntity.status(HttpStatus.OK).body(producto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el producto");
        }
    }

    @PostMapping
    public ResponseEntity<String> agregarProducto(@RequestBody Producto producto) {
        LOG.info("entrando en agregarProducto");
        service.agregarProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Producto agregado");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> modificarProducto(@PathVariable("id") Long id, @RequestBody Producto producto) {
        LOG.info("entrando en modificarProducto");
        if (service.modificarProducto(id, producto)) {
            return ResponseEntity.status(HttpStatus.OK).body("Producto modificado");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProducto(@PathVariable("id") Long id) {
        LOG.info("entrando en eliminarProducto");
        if (service.eliminarProducto(id)) {
            return ResponseEntity.status(HttpStatus.OK).body("Producto eliminado");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        }
    }
}
