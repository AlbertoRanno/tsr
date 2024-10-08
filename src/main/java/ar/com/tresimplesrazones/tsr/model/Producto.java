package ar.com.tresimplesrazones.tsr.model;

import ar.com.tresimplesrazones.tsr.enums.TipoProducto;
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
    @Column(name = "stock", nullable = true)
    private int stock;
    @Enumerated(EnumType.STRING)//Esto permite que el nombre del enum se guarde en la base de datos
    @Column(name = "tipo")
    private TipoProducto tipo;
    
    //Un producto específico va a estar en muchas compras
    @OneToMany(targetEntity = Compra.class, fetch = FetchType.EAGER, mappedBy = "producto")
    @JsonManagedReference("producto-compra")
    private List<Compra> compras;
    
    //Un producto específico va a estar en muchas ventas
    @OneToMany(targetEntity = Venta.class, fetch = FetchType.EAGER, mappedBy = "producto")
    @JsonManagedReference("producto-venta")
    private List<Venta> ventas;

}
