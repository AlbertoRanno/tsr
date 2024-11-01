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
    
    //Campo para almacenar el tipo de cambio al momento de la compra
    @Column(name = "tipo_cambio", nullable = false) //Al poner nullable false => obligo a un valor => tendré que modificar la bbdd
    private Double tipoCambio; //A priori, el usuario ingresará el valor al registrar la compra (la idea es que después se consuma de una API)
    /*Agregar esta propiedad:
    -no afecta los repo
    -con solo agregarla, aparece en Swagger, como parte del objeto Compra que recibe el body en el controlador en cada caso
    -por lo que podría no modificar más nada, y esto funcionaría
    -pero como medidas de seguridad, puse 2: la primera, un condicional en el servicio, que lance un error, en caso de que esta propiedad venga en cero en el objeto
    compra que recibe el body. La segunda, si por algún motivo pasa ese condicional, la propiedad nullable en false, hará que No se guarde un valor nulo en la bbdd.*/

    @ManyToOne(targetEntity = Producto.class)
    @JoinColumn(name = "product_id", nullable = false) //Siempre tiene que estar el producto que se compró
    //Evito el fetch Eager y JsonRefence, y lo manejo solo con un JsonIgnore del lado del producto. Con esto se ve limpio en Swagger y se accede al IdProd en el Front
    private Producto producto;

}
