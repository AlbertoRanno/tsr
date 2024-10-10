package ar.com.tresimplesrazones.tsr.service.impl;

import ar.com.tresimplesrazones.tsr.TsrApplication;
import ar.com.tresimplesrazones.tsr.enums.TipoProducto;
import ar.com.tresimplesrazones.tsr.model.Compra;
import ar.com.tresimplesrazones.tsr.model.Producto;
import ar.com.tresimplesrazones.tsr.model.Venta;
import ar.com.tresimplesrazones.tsr.repository.ICompraRepository;
import ar.com.tresimplesrazones.tsr.repository.IProductoRepository;
import ar.com.tresimplesrazones.tsr.repository.IVentaRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//Por buena práctica, cree una clase aparte para los reportes, y mantengo limpia la clase producto. (=> También tendré que crear un nuevo controlador.)
//Y en la capa de serviceImpl, que es donde se alojan los cálculos extras, como los reportes, u operaciones complejas que requieren datos de varias entidades
@Service
public class ReporteService {

    private static Logger LOG = LoggerFactory.getLogger(TsrApplication.class);

    @Autowired
    IVentaRepository ventaRepo;

    @Autowired
    ICompraRepository compraRepo;

    @Autowired
    IProductoRepository productoRepo;

    public Long calcularRentabilidadTotal() { //Considera el costo de los productos no vendidos aún, por lo que la ganancia arrojada será menor
        // A la suma de todos los importes de ventas realizadas, se le resta la suma de todos los importes de compras (considerando productos en stock y vendidos).
        List<Venta> ventas = ventaRepo.findAll();
        List<Compra> compras = compraRepo.findAll();

        //Calculo los ingresos totales por las ventas
        Long totalVentas = 0L;
        //Recorro todas las ventas
        for (Venta venta : ventas) {
            //Calculo el total en efvo ingresado POR venta (Precio * Cantidad)
            Long totalPorVenta = venta.getPrecioDeVenta() * venta.getCantidadVendida();
            //Voy actualizando el total
            totalVentas += totalPorVenta;
        }
        LOG.info("Total de todas las ventas: " + totalVentas.toString());

        Long totalCompras = 0L;
        for (Compra compra : compras) {
            Long totalPorCompra = compra.getPrecioDeCompra() * compra.getCantidadComprada();
            totalCompras += totalPorCompra;
        }
        LOG.info("Total de todas las compras - incluido lo que sigue en stock -: " + totalCompras.toString());

        return totalVentas - totalCompras;
    }

    /*Voy a aplicar, para el cálculo de la rentabilidad, el método de "Costo de Productos Vendidos" (CPV).
    De modo que se calculen las ganancias únicamente en base al costo de los productos que efectivamente fueron vendidos, ignorando los que aún están en stock.
    Esto me parece que da un margen de ganancia más real.  
    Para calcular el CPV, usaré las compras asociadas a cada producto, con una estrategia FIFO (First In, First Out), es decir que las primeras compras que hizo,
    las considero las primeras que vendió.
    Para esto voy a:
    -tomar cada venta,
    -revisar las compras en orden cronológico para encontrar el costo de las unidades vendidas en esa venta
    -calcular el acumulado del CPV  */
    public Long calcularGananciaTotalCPV() {
        //Obtengo todas las ventas
        List<Venta> ventas = ventaRepo.findAll();

        //Defino la variable gananciaTotal
        Long gananciaTotal = 0L;

        //Bucles anidados, recorro primero las ventas
        for (Venta venta : ventas) {
            //Por cada venta me guardo en variables:
            Producto producto = venta.getProducto(); //El producto que vendí
            int cantidadRestante = venta.getCantidadVendida(); //La cantidad del mismo que vendí (la llamo Restante, porque es la cantidad que me falta ver cuanto costó)
            Long costoTotalVenta = 0L; //El costo total que tuvieron esa cantidad de prod. (Por ej: de 7 que vendí, compré 4 a 100 y 3 a 120)

            /*Para calcular el costo total, necesito ordenar las compras del producto en cuestión por fecha. De modo de usar el criterio FIFO.
            Pero si uso de forma directa: producto.getCompras() me trae una lista que No puedo modificar.
            Por lo tanto, creo una Lista que Sí pueda modificar, con todas las compras que tiene ese producto*/
            List<Compra> comprasOrdenadas = new ArrayList<>(producto.getCompras());

            /*Para ordenar la lista, uso Collections.sort(), el cual toma 2 argumentos: la lista a ordenar, y un comparador.
            El comparador compara las fechas de compra para ordenar en orden ascendente. */
            Collections.sort(comprasOrdenadas, new Comparator<Compra>() { //Al importar el Comparator, por default pide sobreescribir posibles metodos y sugiere el siguiente (*)
                @Override
                public int compare(Compra c1, Compra c2) { // (*)
                    return c1.getFechaDeCompra().compareTo(c2.getFechaDeCompra()); //Esto si se completa
                }
            });
            //Luego de esto, comprasOrdenadas, realmente contiene las compras del producto en orden cronológico

            //LOG.info() puede recibir objetos y convertirlos internamente a texto.
            LOG.info("\nProcesando la venta del producto: " + producto.getNombre() + ", Cantidad vendida: " + cantidadRestante);

            //Ahora recorro esta lista de comprasOrdenadas
            for (Compra compra : comprasOrdenadas) {
                if (cantidadRestante <= 0) {
                    break; //Calcula cuántas unidades de la compra se necesitan para la venta actual (bucle externo)
                }
                /*Me fijo que, cantidadUtilizada, no sea mayor a cantidadRestante (la cant. que aún se necesita para el cálculo del CPV),
                ni a compra.getCantidadComprada (la cant comprada en esta compra).
                Math.min() devuelve el valor menor entre ambos, asegurando que la cantidad utilizada no exceda ni la compra ni la venta.*/
                int cantidadUtilizada = Math.min(compra.getCantidadComprada(), cantidadRestante);
                /*Ejemplo: Supongo una compra de 10 unidades (compra.getCantidadComprada() = 10), pero cantidadRestante = 7.
                Entonces Math.min(10, 7) devolverá 7, ya que esta cantidad es suficiente y no es necesario usar más de lo que queda.
                Entonces, cantidadUtilizada = 7.*/

                costoTotalVenta += cantidadUtilizada * compra.getPrecioDeCompra();
                LOG.info("Compra considerada - Fecha: " + compra.getFechaDeCompra() + ", Cantidad Usada: " + cantidadUtilizada
                        + ", Precio Unitario: " + compra.getPrecioDeCompra() + ", Costo Acumulado: " + costoTotalVenta);

                //Reduzco la cantidad restante de la venta:
                cantidadRestante -= cantidadUtilizada;
            }

            Long ingresoVenta = venta.getPrecioDeVenta() * venta.getCantidadVendida();

            Long gananciaVenta = ingresoVenta - costoTotalVenta;
            gananciaTotal += gananciaVenta;

            LOG.info("Venta procesada - Ingreso Total Venta: " + ingresoVenta + ", Costo Total Venta: " + costoTotalVenta
                    + ", Ganancia Venta: " + gananciaVenta + "\n");
        }

        LOG.info("Ganancia Total CPV: " + gananciaTotal);
        return gananciaTotal;

        /* Recapitulando: 
        Obtengo Todas las ventas
        Por cada venta, obtengo el producto que se vendió y la cantidad del mismo
        Del producto, obtengo las compras del mismo (producto. getCompras)
        Las guardo en un ArrayList y las ordeno por fecha
        Por cada compra, me voy fijando la cantidad que se vendió. Partiendo de la 1er compra.
        Y el en base a lo que pagó cada producto voy actualizando el costo total de esa venta.
        Hasta cubrir exacto la cantidad que se vendió (primero las compradas a precio más viejo)
        Una vez llegado a eso, se ejecuta el break del bucle de compras.
        Se calcula el dinero recibido por la venta que estaba recorriendo el bucle externo,
        Y ahora conociendo cuando costaron esas unidades vendidas, que fueron las más viejas compradas, se sabe la ganancia real de la venta.
        Esto se va acumulando por cada venta que hubo, y así se obtiene la ganancia total,
        considerando todas las ventas, y las compras SOLO de lo vendido hasta el momento (A diferencia del método anterior) */
    }

    public Long calcularGananciaPorTipoProductoCPV(TipoProducto tipo) {
        //Simil al método anterior, pero quiero saber las ganancias por un determinado tipo: SAHUMERIO,TE,JUGUETE
        List<Producto> productosDeEsteTipo = productoRepo.findAllByTipo(tipo); //Todos los productos del tipo que vino en la url

        List<Venta> ventas = ventaRepo.findAll(); // No quiero todas las ventas, solo las relacionadas (debajo) pero para filtrarlas, recorro todas en el bucle
        List<Venta> ventasDeProdDeEsteTipo = new ArrayList<>();

        Long gananciaTotal = 0L;

        for (Venta venta : ventas) {
            if (productosDeEsteTipo.contains(venta.getProducto())) {
                ventasDeProdDeEsteTipo.add(venta);
            }
        }
        for (Venta venta : ventasDeProdDeEsteTipo) {
            Producto producto = venta.getProducto();
            int cantidadRestante = venta.getCantidadVendida();
            Long costoTotalVenta = 0L;

            List<Compra> comprasOrdenadas = new ArrayList<>(producto.getCompras());

            Collections.sort(comprasOrdenadas, new Comparator<Compra>() {
                @Override
                public int compare(Compra c1, Compra c2) {
                    return c1.getFechaDeCompra().compareTo(c2.getFechaDeCompra());
                }
            });

            LOG.info("\nProcesando la venta del producto: " + producto.getNombre() + ", Cantidad vendida: " + cantidadRestante);

            for (Compra compra : comprasOrdenadas) {
                if (cantidadRestante <= 0) {
                    break;
                }
                int cantidadUtilizada = Math.min(compra.getCantidadComprada(), cantidadRestante);

                costoTotalVenta += cantidadUtilizada * compra.getPrecioDeCompra();
                LOG.info("Compra considerada - Fecha: " + compra.getFechaDeCompra() + ", Cantidad Usada: " + cantidadUtilizada
                        + ", Precio Unitario: " + compra.getPrecioDeCompra() + ", Costo Acumulado: " + costoTotalVenta);

                cantidadRestante -= cantidadUtilizada;
            }

            Long ingresoVenta = venta.getPrecioDeVenta() * venta.getCantidadVendida();

            Long gananciaVenta = ingresoVenta - costoTotalVenta;
            gananciaTotal += gananciaVenta;

            LOG.info("Venta procesada - Ingreso Total Venta: " + ingresoVenta + ", Costo Total Venta: " + costoTotalVenta
                    + ", Ganancia Venta: " + gananciaVenta + "\n");
        }

        LOG.info("Ganancia Total CPV: " + gananciaTotal);
        return gananciaTotal;
    }
}
