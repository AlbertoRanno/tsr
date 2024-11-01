package ar.com.tresimplesrazones.tsr.model;

import ar.com.tresimplesrazones.tsr.enums.TipoProducto;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore //Tuve que quitar los JsonRefence, y agregar este aquí, para que desde el front se pueda acceder al producto relacionado con la venta.
    private List<Compra> compras;
    
    //Un producto específico va a estar en muchas ventas
    @OneToMany(targetEntity = Venta.class, fetch = FetchType.EAGER, mappedBy = "producto")
    //Esto carga todas las ventas de un producto cada vez que se carga el producto, lo cual puede ser útil si se necesita acceder a todas las ventas de un producto inmediatamente
    @JsonIgnore //Tuve que quitar los JsonRefence, y agregar este aquí, para que desde el front se pueda acceder al producto relacionado con la venta.
    private List<Venta> ventas;

}
