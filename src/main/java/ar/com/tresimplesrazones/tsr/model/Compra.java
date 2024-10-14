package ar.com.tresimplesrazones.tsr.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
@Table(name = "compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cantidad_comprada")
    private int cantidadComprada;
    @Column(name = "precio_de_compra")
    private Long precioDeCompra;
    @Column(name = "fecha_de_compra", columnDefinition = "DATE")
    private LocalDate fechaDeCompra;
    /* LocalDate es una clase en Java que representa una fecha sin hora. Formato: YYYY-MM-DD (año-mes-día)*/

    @ManyToOne(targetEntity = Producto.class)
    @JoinColumn(name = "product_id", nullable = false) //Siempre tiene que estar el producto que se compró
    @JsonBackReference("producto-compra")
    private Producto producto;

}
