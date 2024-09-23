package ar.com.tresimplesrazones.tsr.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private int precioDeVenta;
    @Column(name = "fecha_de_Venta", columnDefinition = "DATE")
    private LocalDate fechaDeVenta;

    @ManyToOne(targetEntity = Producto.class)
    @JsonBackReference("producto-venta")
    private Producto producto;
}
