package ar.com.tresimplesrazones.tsr.service.impl;

import ar.com.tresimplesrazones.tsr.TsrApplication;
import ar.com.tresimplesrazones.tsr.enums.TipoProducto;
import ar.com.tresimplesrazones.tsr.model.Compra;
import ar.com.tresimplesrazones.tsr.model.Producto;
import ar.com.tresimplesrazones.tsr.model.Venta;
import ar.com.tresimplesrazones.tsr.repository.ICompraRepository;
import ar.com.tresimplesrazones.tsr.repository.IProductoRepository;
import ar.com.tresimplesrazones.tsr.repository.IVentaRepository;
import java.time.LocalDate;
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

//    public Long calcularGananciaPorTipoProductoCPVEnPeriodo(TipoProducto tipo, LocalDate fechaInicio, LocalDate fechaFin) {
//        //Simil al método anterior, pero quiero saber las ganancias por un determinado tipo: SAHUMERIO,TE,JUGUETE en un periodo determinado
//        List<Producto> productosDeEsteTipo = productoRepo.findAllByTipo(tipo); //Todos los productos del tipo que vino en la url
//
//        //List<Venta> ventas = ventaRepo.findAll(); // No quiero todas las ventas, solo las relacionadas (debajo) pero para filtrarlas, recorro todas en el bucle
//        List<Venta> ventas = ventaRepo.findAllByFechaDeVentaBetween(fechaInicio, fechaFin); //Solo las del periodo
//
//        Long gananciaTotal = 0L;
//
//        //Filtro las ventas, según el tipo de producto.
//        List<Venta> ventasDeProdDeEsteTipo = new ArrayList<>();
//        for (Venta venta : ventas) {
//            if (productosDeEsteTipo.contains(venta.getProducto())) {
//                ventasDeProdDeEsteTipo.add(venta);
//            }
//        }
//
//        // Recorro cada venta del tipo indicado
//        for (Venta venta : ventasDeProdDeEsteTipo) {
//            Producto producto = venta.getProducto();
//            int cantidadRestante = venta.getCantidadVendida();
//            Long costoTotalVenta = 0L;
//
//            /* A continuación, filtro las compras de cada producto hasta la fecha de esta venta.
//            Esto asegura que No se usen compras con fechas posteriores a la venta actual.
//            Uso el arrayList al igual que antes, para poder modificar su contenido y ordenarlo. */
//            List<Compra> comprasHastaFechaVenta = new ArrayList<>(producto.getCompras());
//            for (Compra compra : producto.getCompras()) {
//                if (!compra.getFechaDeCompra().isAfter(venta.getFechaDeVenta())) {
//                    //Si es verdadero, que la fecha de la compra, NO es posterior, a la fecha de la venta, entonces, añado la compra al array:
//                    comprasHastaFechaVenta.add(compra);
//                }
//            }
//
//            //Ordeno las compras hasta la fecha de la venta actual, usando el criterio FIFO
//            Collections.sort(comprasHastaFechaVenta, new Comparator<Compra>() {
//                @Override
//                public int compare(Compra c1, Compra c2) {
//                    return c1.getFechaDeCompra().compareTo(c2.getFechaDeCompra());
//                }
//            });
//
//            LOG.info("\nProcesando la venta del producto: " + producto.getNombre() + ", Cantidad vendida: " + cantidadRestante);
//
//            //Calculo el CPV (Costo Productos Vendidos) en base a las compras hasta la venta
//            for (Compra compra : comprasHastaFechaVenta) {
//                if (cantidadRestante <= 0) {
//                    break;
//                }
//                
//                //Calculo la cantidad usada para la venta actual (uso Math.min para no sobrepasar el inventario)
//                int cantidadUtilizada = Math.min(compra.getCantidadComprada(), cantidadRestante);
//
//                // Multiplico por el precio de la compra para obtener el costo parcial de esta venta
//                costoTotalVenta += cantidadUtilizada * compra.getPrecioDeCompra();
//                LOG.info("Compra considerada - Fecha: " + compra.getFechaDeCompra() + ", Cantidad Usada: " + cantidadUtilizada
//                        + ", Precio Unitario: " + compra.getPrecioDeCompra() + ", Costo Acumulado: " + costoTotalVenta);
//
//                // Reduzco la cantidad restante en base a la cantidad ya calculada
//                cantidadRestante -= cantidadUtilizada;
//            }
//
//            // Calculo los ingresos totales por la venta y la ganancia restando los costos
//            Long ingresoVenta = venta.getPrecioDeVenta() * venta.getCantidadVendida();
//            Long gananciaVenta = ingresoVenta - costoTotalVenta;
//            gananciaTotal += gananciaVenta;
//
//            LOG.info("Venta procesada - Ingreso Total Venta: " + ingresoVenta + ", Costo Total Venta: " + costoTotalVenta
//                    + ", Ganancia Venta: " + gananciaVenta + "\n");
//        }
//
//        LOG.info("Ganancia Total CPV: " + gananciaTotal);
//        return gananciaTotal;
//    }
    /* Este metodo no corrige el hecho de que si yo quiero saber las ganancias en el periodo que va de A a B, el costo de los productos vendidos allí,
    se calcularán como si fueran los primeros productos que compré.... despreciando, la MUY POSIBLE REALIDAD, de que los productos que yo compré al inicio de todo,
    con sus costos (más bajos que el resto), todos, o en parte, ya los he vendido en un periodo previo a 'A', que está fuera de esta consulta, pero creo que,
    para dar una respuesta exacta, habría que considerarlo... Por lo tanto en el siguiente metodo llevo un seguimiento de las compras “usadas” hasta el inicio del periodo 'A'.
    Esto implica ajustar el inventario de manera que solo cuente lo que queda disponible al inicio del periodo de consulta.

    Modificación en la Lógica:
    Cargar Inventario Inicial: Al iniciar el cálculo para el periodo de 'A' a 'B', se itera sobre todas las ventas anteriores a 'A' y sw descuentan del inventario de compras
    (ordenado por fecha) el costo de las unidades ya vendidas.
    Calcular Ganancia dentro del Periodo: Luego, con el inventario ajustado, ahora sí, se procesan las ventas de 'A' a 'B' siguiendo el criterio FIFO.*/
    public Long calcularGananciaPorTipoProductoCPVEnPeriodo(TipoProducto tipo, LocalDate fechaInicio, LocalDate fechaFin) {
        LOG.info("Calculando ganancia para tipo de producto: " + tipo + " entre las fechas: " + fechaInicio + " y " + fechaFin);

        List<Producto> productosDeEsteTipo = productoRepo.findAllByTipo(tipo);

        List<Venta> ventas = ventaRepo.findAllByFechaDeVentaBetween(fechaInicio, fechaFin); //Ventas del periodo 'A' a 'B'
        List<Venta> ventasAnteriores = ventaRepo.findAllByFechaDeVentaBetween(LocalDate.MIN, fechaInicio.minusDays(1)); //Ventas previas a 'A'

        Long gananciaTotal = 0L;

        for (Producto producto : productosDeEsteTipo) {
            LOG.info("Procesando producto: " + producto.getNombre());

            List<Compra> comprasOrdenadas = new ArrayList<>(producto.getCompras()); //Todas las compras de este producto
            Collections.sort(comprasOrdenadas, Comparator.comparing(Compra::getFechaDeCompra)); //Ordenadas por fecha

            // FALTABA ESTO => Descontar las compras ya usadas en ventas anteriores a 'A'
            for (Venta ventaAnterior : ventasAnteriores) {
                if (!ventaAnterior.getProducto().equals(producto)) {
                    continue;
                }
                LOG.info("Descontando inventario usado en venta anterior a periodo. Venta anterior: " + ventaAnterior.getFechaDeVenta()
                        + ", Cantidad vendida: " + ventaAnterior.getCantidadVendida());

                int cantidadRestante = ventaAnterior.getCantidadVendida();
                for (Compra compra : comprasOrdenadas) {
                    if (cantidadRestante <= 0) {
                        break;
                    }

                    int cantidadUtilizada = Math.min(compra.getCantidadComprada(), cantidadRestante);
                    LOG.info("Compra descontada: Fecha compra: " + compra.getFechaDeCompra() + ", Cantidad usada: " + cantidadUtilizada
                            + ", Cantidad restante en compra antes de descontar: " + compra.getCantidadComprada());

                    compra.setCantidadComprada(compra.getCantidadComprada() - cantidadUtilizada);
                    cantidadRestante -= cantidadUtilizada;

                    LOG.info("Cantidad restante en compra después de descontar: " + compra.getCantidadComprada()
                            + ", Cantidad restante en venta anterior: " + cantidadRestante);
                }
            }

            // Ya descartadas las primeras compras de productos ya vendidos, ahora sí => Calculo la ganancia para las ventas en el periodo de 'A' a 'B'
            for (Venta venta : ventas) {
                if (!venta.getProducto().equals(producto)) {
                    continue;
                }
                LOG.info("Calculando ganancia de venta en periodo. Fecha de venta: " + venta.getFechaDeVenta()
                        + ", Cantidad vendida: " + venta.getCantidadVendida());

                int cantidadRestante = venta.getCantidadVendida();
                Long costoTotalVenta = 0L;

                for (Compra compra : comprasOrdenadas) {
                    if (cantidadRestante <= 0) {
                        break;
                    }

                    int cantidadUtilizada = Math.min(compra.getCantidadComprada(), cantidadRestante);
                    LOG.info("Usando inventario para venta en periodo. Fecha compra: " + compra.getFechaDeCompra()
                            + ", Cantidad usada: " + cantidadUtilizada + ", Precio unitario: " + compra.getPrecioDeCompra());

                    costoTotalVenta += cantidadUtilizada * compra.getPrecioDeCompra();
                    compra.setCantidadComprada(compra.getCantidadComprada() - cantidadUtilizada);
                    cantidadRestante -= cantidadUtilizada;

                    LOG.info("Costo acumulado para esta venta: " + costoTotalVenta
                            + ", Cantidad restante en compra después de uso: " + compra.getCantidadComprada()
                            + ", Cantidad restante en venta: " + cantidadRestante);
                }

                Long ingresoVenta = venta.getPrecioDeVenta() * venta.getCantidadVendida();
                Long gananciaVenta = ingresoVenta - costoTotalVenta;
                gananciaTotal += gananciaVenta;

                LOG.info("Ganancia de esta venta: " + gananciaVenta + ", Ingreso total venta: " + ingresoVenta
                        + ", Costo total venta: " + costoTotalVenta);
            }
        }

        LOG.info("Ganancia Total CPV en periodo: " + gananciaTotal);
        return gananciaTotal;
    }

    /*A continuación, cómo funciona la lógica de este ajuste de inventario virtual, siguiendo el proceso desde la “preparación del inventario”,
    hasta el cálculo de la ganancia en el periodo deseado.

    Explicación Teórica:
    El objetivo de esta lógica es mantener un “registro virtual” del inventario, donde se simulan las compras ya usadas para ventas anteriores.
    Esto permite que, al calcular la ganancia en el periodo de consulta, solo se usen las unidades de compra que realmente quedan en inventario para ese momento.

    Paso 1: Cargar todas las Compras en Orden Cronológico
    Primero, se obtienen todas las compras de cada producto y se ordenan por fecha, en orden ascendente (de la compra más antigua a la más reciente). 
    Este orden es esencial porque el criterio FIFO indica que las unidades más antiguas deben usarse primero.

    Paso 2: Simular el Consumo de Inventario en Ventas Previas
    Se recorren todas las ventas anteriores al inicio del periodo de consulta, y “consumimos” las compras en orden cronológico (FIFO) para estas ventas. 
    Esto se hace ajustando, virtualmente, la cantidad disponible en cada compra.
    No afecta la base de datos ni el valor real de la compra en el sistema, solo es un ajuste para el cálculo.

    Para cada venta previa:
    Se calcula la cantidad vendida en esa venta.
    Se descuenta esta cantidad de las compras más antiguas.
    Una vez que una compra queda en cero (toda la cantidad fue consumida), se pasa a la siguiente compra.
    
    Paso 3: Calcular la Ganancia en el Periodo de Consulta con el Inventario Ajustado
    Ahora que se han descontado las compras utilizadas en ventas previas, el inventario está listo para usarse en el periodo de consulta.

    Para cada venta en el periodo deseado:
    Se aplica el mismo proceso FIFO para calcular el costo de las unidades vendidas, pero ahora, solo se usa el inventario ajustado (compras que no fueron
    totalmente consumidas por ventas anteriores).
    Se calcula la ganancia de cada venta en el periodo restando los costos acumulados del ingreso de esa venta.
    
    Ejemplo Paso a Paso:

    Compras Realizadas:
    Compra de 100 unidades a $10 (01-01-2023)
    Compra de 50 unidades a $12 (15-01-2023)
    Compra de 80 unidades a $15 (01-02-2023)
    
    Ventas Previas al Periodo Consultado (Antes del 01-03-2023):
    Venta de 70 unidades (10-01-2023)
    Venta de 40 unidades (20-01-2023)
    
    Ventas en el Periodo Consultado (01-03-2023 a 31-03-2023):
    Venta de 50 unidades (05-03-2023)
    Venta de 40 unidades (20-03-2023)
    
    Paso 1: Ordenar las Compras
    
    Compras en orden:
    100 unidades a $10 (01-01-2023)
    50 unidades a $12 (15-01-2023)
    80 unidades a $15 (01-02-2023)
    
    Paso 2: Simular Consumo en Ventas Previas (antes de 01-03-2023)
    
    Primera Venta Anterior (70 unidades el 10-01-2023):
    Se restan las 70 unidades de las primeras compras:
    Se usa la primer compra (100 unidades a $10).
    Después de esta venta, la primera compra queda con 30 unidades (100 - 70 = 30).
    
    Segunda Venta Anterior (40 unidades el 20-01-2023):
    Se sigue descontando con el criterio FIFO:
    Se usan las 30 unidades restantes de la primera compra (30 unidades a $10), dejándola en 0.
    Se necesitan 10 unidades más, entonces se toman de la segunda compra:
    10 unidades de la segunda compra (50 unidades a $12), dejando 40 unidades en esa compra.
    
    Inventario Virtual al Inicio del Periodo (después de descontar ventas anteriores):
    Compra de 50 unidades a $10 → Consumida totalmente (0 unidades).
    Compra de 50 unidades a $12 → Quedan 40 unidades.
    Compra de 80 unidades a $15 → Completa, quedan 80 unidades.
    
    Paso 3: Calcular Ganancia en el Periodo Consultado (01-03-2023 a 31-03-2023)
    
    Primera Venta en el Periodo (50 unidades el 05-03-2023):
    Se calcula el costo usando el inventario ajustado:
    Se usan las 40 unidades restantes de la segunda compra a $12 (quedan 0 unidades).
    Luego, se usan 10 unidades de la tercera compra a $15 (80 - 10 = 70).
    Costo Total de esta Venta:
    (40 unidades x $12) + (10 unidades x $15) = $480 + $150 = $630.
    
    Segunda Venta en el Periodo (40 unidades el 20-03-2023):

    Se sigue usando el inventario restante en orden:
    Se toman 40 unidades de la tercera compra a $15 (quedan 30).
    Costo Total de esta Venta:
    (40 unidades x $15) = $600.
    
    Resumen de Resultados
    
    Ganancia en el Periodo (01-03-2023 a 31-03-2023):

    Primera Venta (05-03-2023):
    Ingreso: Supongo que fue vendida a $20 por unidad → 50 x $20 = $1,000.
    Costo: $630.
    Ganancia de esta Venta: $1,000 - $630 = $370.
    
    Segunda Venta (20-03-2023):
    Ingreso: Supongo que fue vendida a $20 por unidad → 40 x $20 = $800.
    Costo: $600.
    Ganancia de esta Venta: $800 - $600 = $200.
    
    Ganancia Total en el Periodo: $370 + $200 = $570.

    Resumen Final
    Este método permite calcular las ganancias dentro del periodo de manera precisa al ajustar el inventario consumido en ventas anteriores. 
    
    La clave es:
    -Ajustar el inventario al inicio del periodo restando las unidades ya vendidas.
    -Aplicar el cálculo del costo usando este inventario ajustado y el criterio FIFO dentro del periodo de consulta.
    -Este enfoque garantiza que solo se utilicen las unidades realmente disponibles en cada venta del periodo y evita contar productos que ya fueron usados en ventas previas.*/
}
