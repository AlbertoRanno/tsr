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
    private int precioDeCompra;
    @Column(name = "fecha_de_compra", columnDefinition = "DATE")
    private LocalDate fechaDeCompra;
    
    @ManyToOne(targetEntity = Producto.class)
    @JsonBackReference("producto-compra")
    private Producto producto;
    

}
