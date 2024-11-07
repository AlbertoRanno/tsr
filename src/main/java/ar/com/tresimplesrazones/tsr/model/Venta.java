package ar.com.tresimplesrazones.tsr.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cantidad_vendida")
    private int cantidadVendida;
    @Column(name = "precio_de_venta")
    private Long precioDeVenta;
    @Column(name = "fecha_de_Venta", columnDefinition = "DATE")
    private LocalDate fechaDeVenta;
    
    @Column(name= "tipo_cambio", nullable = false)
    private Double tipoCambio;
    
    @Column(name= "info_adicional", nullable = true)
    private String infoAdicional;
    /*  Atributo Agregado de forma tardía, para no volver a crear la base de datos que ya tiene muchos valores:
    USE tsr;  
    ALTER TABLE venta ADD COLUMN info_adicional VARCHAR(255) NULL;
    Esto crea la columna info_adicional en la tabla venta como un campo opcional (NULL), permitiendo texto de hasta 255 caracteres. */

    @ManyToOne(targetEntity = Producto.class)
    @JoinColumn(name = "producto_id", nullable = false) // Siempre tiene que estar el producto
    private Producto producto;
    //Evito el fetch Eager y JsonRefence, y lo manejo solo con un JsonIgnore del lado del producto. Con esto se ve limpio en Swagger y se accede al IdProd en el Front
}
