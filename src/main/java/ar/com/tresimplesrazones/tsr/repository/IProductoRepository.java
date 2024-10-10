package ar.com.tresimplesrazones.tsr.repository;

import ar.com.tresimplesrazones.tsr.enums.TipoProducto;
import ar.com.tresimplesrazones.tsr.model.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductoRepository extends JpaRepository<Producto, Long> {
    
    Producto findByNombre(String nombre);
    
    List<Producto> findAllByTipo(TipoProducto tipo);//Ojo No es String sino que es TipoProducto porque es la Enum
    //Al ser un enum en swagger aparece un desplegable para las opciones del PathVariable

}
