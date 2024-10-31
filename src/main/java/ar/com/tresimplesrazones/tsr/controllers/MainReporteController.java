package ar.com.tresimplesrazones.tsr.controllers;

import ar.com.tresimplesrazones.tsr.enums.TipoProducto;
import ar.com.tresimplesrazones.tsr.service.impl.ReporteService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
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

    // Considera el stock sin vender como costo ya incurrido, restándolo de la ganancia total
    @GetMapping("/rentabilidad-total")
    public ResponseEntity<Long> obtenerRentabilidadTotal() {
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.calcularRentabilidadTotal());
    }

    @GetMapping("/rentabilidad-total-usd")
    public ResponseEntity<Double> obtenerRentabilidadTotalEnDolares() {
        //Mantengo la precisión de todos los decimales en el servicio, pero limito a 2 decimales en la presentación de la respuesta:
        Double gananciaTotalUSD = reporteService.calcularRentabilidadTotalEnDolares();
        Double gananciaRedondeada = BigDecimal.valueOf(gananciaTotalUSD).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return ResponseEntity.status(HttpStatus.OK).body(gananciaRedondeada);
    }

    // Considera ganancias solo sobre el stock vendido (el stock sin vender aún no se considera costo)
    @GetMapping("/rentabilidad-cpv")
    public ResponseEntity<Long> obtenerRentabilidadCPV() {
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.calcularGananciaTotalCPV());
    }

    @GetMapping("/rentabilidad-cpv-usd")
    public ResponseEntity<Double> obtenerRentabilidadCPVEnDolares() {
        Double gananciaTotalUSD = reporteService.calcularGananciaTotalCPVEnDolares();
        Double gananciaRedondeada = BigDecimal.valueOf(gananciaTotalUSD).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return ResponseEntity.status(HttpStatus.OK).body(gananciaRedondeada);
    }

    // Igual que el anterior, usando criterios CPV y FIFO, pero permite individualizar la ganancia por tipo de producto
    @GetMapping("/rentabilidad/{tipo}")
    public ResponseEntity<Long> obtenerRentabilidadPorTipo(@PathVariable("tipo") TipoProducto tipo) {
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.calcularGananciaPorTipoProductoCPV(tipo));
    }

    @GetMapping("/rentabilidad-usd/{tipo}")
    public ResponseEntity<Double> obtenerRentabilidadPorTipoEnDolares(@PathVariable("tipo") TipoProducto tipo) {
        Double gananciaTotalUSD = reporteService.calcularGananciaPorTipoProductoCPVEnDolares(tipo);
        Double gananciaRedondeada = BigDecimal.valueOf(gananciaTotalUSD).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return ResponseEntity.status(HttpStatus.OK).body(gananciaRedondeada);
    }

    // Igual que el anterior, pero permite filtrar también por periodo (fecha de inicio y fin)
    @GetMapping("rentabilidad/{tipo}/{fechaInicio}/{fechaFin}")
    public ResponseEntity<Long> obtenerRentabilidadPorTipoEnPeriodo(@PathVariable TipoProducto tipo, @PathVariable LocalDate fechaInicio, @PathVariable LocalDate fechaFin) {
        return ResponseEntity.status(HttpStatus.OK).body(reporteService.calcularGananciaPorTipoProductoCPVEnPeriodo(tipo, fechaInicio, fechaFin));
    }

    @GetMapping("rentabilidad-usd/{tipo}/{fechaInicio}/{fechaFin}")
    public ResponseEntity<Double> obtenerRentabilidadPorTipoEnPeriodoEnDolares(@PathVariable TipoProducto tipo, @PathVariable LocalDate fechaInicio, @PathVariable LocalDate fechaFin) {
        Double gananciaTotalUSD = reporteService.calcularGananciaPorTipoProductoCPVEnPeriodoEnDolares(tipo, fechaInicio, fechaFin);
        Double gananciaRedondeada = BigDecimal.valueOf(gananciaTotalUSD).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return ResponseEntity.status(HttpStatus.OK).body(gananciaRedondeada);
    }

    /*Endpoints combinados:
    Eficiencia: Permite obtener los datos en una sola llamada en lugar de realizar múltiples solicitudes.
    Claridad: Devuelve ambos valores en pesos y dólares, facilitando la comparación sin duplicar la lógica del servicio.
    Flexibilidad: Mantiene los endpoints individuales y ofrece una opción combinada para obtener ambos valores de manera práctica.*/
    // Endpoint combinado para rentabilidadTotal en pesos y en dólares
    @GetMapping("/rentabilidad-total-combinado")
    public ResponseEntity<Map<String, Object>> obtenerRentabilidadTotalCombinado() {
        Long rentabilidadPesos = reporteService.calcularRentabilidadTotal();
        Double rentabilidadDolares = BigDecimal.valueOf(reporteService.calcularRentabilidadTotalEnDolares())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("rentabilidadPesos", rentabilidadPesos);
        respuesta.put("rentabilidadDolares", rentabilidadDolares);

        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    // Endpoint combinado para rentabilidad CPV en pesos y en dólares
    @GetMapping("/rentabilidad-cpv-combinado")
    public ResponseEntity<Map<String, Object>> obtenerRentabilidadCPVCombinado() {
        Long rentabilidadCPVPesos = reporteService.calcularGananciaTotalCPV();
        Double rentabilidadCPVDolares = BigDecimal.valueOf(reporteService.calcularGananciaTotalCPVEnDolares())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("rentabilidadCPVPesos", rentabilidadCPVPesos);
        respuesta.put("rentabilidadCPVDolares", rentabilidadCPVDolares);

        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    // Endpoint combinado para rentabilidad por tipo de producto en pesos y en dólares
    @GetMapping("/rentabilidad-combinado/{tipo}")
    public ResponseEntity<Map<String, Object>> obtenerRentabilidadPorTipoCombinado(@PathVariable("tipo") TipoProducto tipo) {
        Long rentabilidadPorTipoPesos = reporteService.calcularGananciaPorTipoProductoCPV(tipo);
        Double rentabilidadPorTipoDolares = BigDecimal.valueOf(reporteService.calcularGananciaPorTipoProductoCPVEnDolares(tipo))
                .setScale(2, RoundingMode.HALF_UP).doubleValue();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("rentabilidadPorTipoPesos", rentabilidadPorTipoPesos);
        respuesta.put("rentabilidadPorTipoDolares", rentabilidadPorTipoDolares);

        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    // Endpoint combinado para rentabilidad por tipo de producto en un periodo específico en pesos y en dólares
    @GetMapping("/rentabilidad-combinado/{tipo}/{fechaInicio}/{fechaFin}")
    public ResponseEntity<Map<String, Object>> obtenerRentabilidadPorTipoEnPeriodoCombinado(
            @PathVariable("tipo") TipoProducto tipo,
            @PathVariable("fechaInicio") LocalDate fechaInicio,
            @PathVariable("fechaFin") LocalDate fechaFin) {

        Long rentabilidadPorTipoPeriodoPesos = reporteService.calcularGananciaPorTipoProductoCPVEnPeriodo(tipo, fechaInicio, fechaFin);
        Double rentabilidadPorTipoPeriodoDolares = BigDecimal.valueOf(reporteService.calcularGananciaPorTipoProductoCPVEnPeriodoEnDolares(tipo, fechaInicio, fechaFin))
                .setScale(2, RoundingMode.HALF_UP).doubleValue();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("rentabilidadPorTipoPeriodoPesos", rentabilidadPorTipoPeriodoPesos);
        respuesta.put("rentabilidadPorTipoPeriodoDolares", rentabilidadPorTipoPeriodoDolares);

        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    /* ResponseEntity: Es una envoltura (wrapper) que se configura para devolver un objeto clave-valor al cliente.
    Las claves son las etiquetas (String) que se ven en consola y los valores son los resultados de los métodos (las variables numéricas).

    Map: Este objeto se usa para estructurar la respuesta en pares clave-valor, donde:
    Claves (String): Describen el valor que se está enviando, como "rentabilidadPesos" o "rentabilidadDolares".
    Valores (Long o Double): Son los resultados de los métodos del servicio.
    
    HashMap y put:
    HashMap es una implementación de Map para crear la estructura de clave-valor.
    put agrega cada par clave-valor al Map, asignando la etiqueta correcta (String) a cada valor (Long o Double).
    
    Entonces, en resumen:
    ResponseEntity envuelve el Map y lo envía al cliente.
    Map es la estructura clave-valor que contiene la respuesta.
    HashMap y put permiten crear y manipular esa estructura antes de enviarla.*/
    
    // Endpoint combinado más abarcativo:
    @GetMapping("/informe-total")
    public ResponseEntity<Map<String, Object>> informeTotal() {
        Long rentabilidadPesos = reporteService.calcularRentabilidadTotal();
        Double rentabilidadDolares = BigDecimal.valueOf(reporteService.calcularRentabilidadTotalEnDolares())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
        Long rentabilidadCPVPesos = reporteService.calcularGananciaTotalCPV();
        Double rentabilidadCPVDolares = BigDecimal.valueOf(reporteService.calcularGananciaTotalCPVEnDolares())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("Rentabilidad en Pesos (considerando el costo del stock no vendido)", rentabilidadPesos);
        respuesta.put("Rentabilidad en Dolares (considerando el costo del stock no vendido)", rentabilidadDolares);
        respuesta.put("Rentabilidad en Pesos (sin considerar el stock no vendido)", rentabilidadCPVPesos);
        respuesta.put("Rentabilidad en Dolares (sin considerar el stock no vendido)", rentabilidadCPVDolares);
        
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }
}
