package ar.com.tresimplesrazones.tsr.enums;

public enum TipoProducto {
    SAHUMERIO,
    TE,
    JUGUETE,
    VARIOS
}

/* Al agregar otro tipo, como pasó con VARIOS, solo hace falta agregarlo acá en el back, y en el front, en los optionals de los select en cada caso.
Pero además de eso, hay que actualizar la bbdd desde el workbench, para que se actualice que internamente también habrá otro tipo de datos (caso contrario daba error).
Ejecutando:
USE tsr;
ALTER TABLE producto MODIFY COLUMN tipo ENUM('SAHUMERIO', 'TE', 'JUGUETE', 'VARIOS');
Y luego reiniciando la app.
*/