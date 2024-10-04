package ar.com.tresimplesrazones.tsr.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
//Este es un manejador global de excepciones (para no repetir en cada uno de los controladores el try-catch)
// Esta clase maneja globalmente las excepciones lanzadas por los controladores, evitando el manejo por defecto de Spring
// que incluye mostrar un stack trace (traza de la pila) extenso en la consola. Esto mejora la claridad del log, 
// y muestra solo el mensaje personalizado de la excepción. (***)


@ControllerAdvice // Esta anotación permite que la clase actúe como manejador global de excepciones. Intercepta las excepciones lanzadas en cualquier controlador
// de la aplicación y permite generar una respuesta HTTP personalizada según el tipo de excepción.
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class) // Este método manejará todas las excepciones del tipo ResourceNotFoundException. 
    // Cuando se lanza esta excepción, Spring llamará a este método y devolverá una respuesta personalizada.
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}

/* (***) Explicación más clara:
Cuando lanzaba una excepción, como ResourceNotFoundException, antes de implementar el manejador global, el framework Spring también generaba un rastreo 
completo de la pila (stack trace) en la consola. Este rastreo mostraba todo el camino que recorrió la excepción hasta llegar al controlador, lo que incluía
muchos mensajes técnicos y trazas del sistema.

Con el uso de @ControllerAdvice, intercepto las excepciones antes de que lleguen al manejador por defecto de Spring.
Ahora yo controlo cómo se responde ante las excepciones, devolviendo respuestas limpias y personalizadas. 
Esto evita que Spring aplique su propio manejo, y por lo tanto, no genera esos mensajes de error largos en la consola (que incluyen el stack trace completo).

En resumen: El stack trace completo ya no se muestra porque estoy manejando la excepción de forma personalizada antes de que Spring la procese.

---

Es importante entender que el uso de un manejador global no solo oculta el stack trace en la consola, sino que también mejora la experiencia del cliente 
(o consumidor de la API). En lugar de recibir un error genérico de Spring con toda la traza, reciben un mensaje limpio y claro, que yo mismo defino.

Si en algún momento necesito ver el stack trace en producción o desarrollo para debugging (aunque no quiera que los usuarios finales lo vean), podría
considerar registrarlo en un archivo de logs usando una herramienta como Logback o SLF4J. Esto te permitirá ver detalles más específicos de los errores 
sin afectar las respuestas de la API.

En resumen:
El manejo de excepciones personalizado evita que Spring genere un stack trace largo.
@ControllerAdvice centraliza el manejo de excepciones, lo que mejora la mantenibilidad.
Si necesito más información para depuración, puedo usar un logger para registrar el stack trace en archivos de log, sin mostrárselo al usuario final.
*/