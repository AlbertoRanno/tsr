package ar.com.tresimplesrazones.tsr.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre", unique = true)
    private String nombre;
    @Column(name = "precio_compra")
    private int precioCompra;
    @Column(name = "precio_venta")
    private int precioVenta;
    private int stock;
    
    //Producto debería llevar el control de las cantidades compradas y vendidas a través de las relaciones con Compra y Venta
    
    //Un producto específico va a estar en muchas compras
    @OneToMany(targetEntity = Compra.class, fetch = FetchType.EAGER, mappedBy = "producto")
    @JsonManagedReference("producto-compra")
    private List<Compra> compras;
    
    //Un producto específico va a estar en muchas ventas
    @OneToMany(targetEntity = Venta.class, fetch = FetchType.EAGER, mappedBy = "producto")
    @JsonManagedReference("producto-venta")
    private List<Venta> ventas;

}
