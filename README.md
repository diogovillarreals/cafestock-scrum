# CaféStock — Sprint 1

Aplicación de escritorio para registrar productos, consultar y editar el inventario y registrar ventas con actualización automática del stock.

## Equipo

**Nombre:** Equipo CaféStock
**Escenario:** A — Inventario y ventas de la cafetería.

Integrante y Rol
Pablo Vizuete  Product Owner y desarrollador
Diogo Villarreal  Scrum Master y desarrollador
Steven Arauz  Desarrollador
Erick Guachagmira  Desarrollador

## Funcionalidades

- **HU-A1:** registrar productos, validar sus datos e impedir códigos duplicados.
- **HU-A2:** buscar productos por código o nombre y editar sus datos conservando el código.
- **HU-A3:** registrar ventas con fecha editable, calcular el total y descontar las unidades vendidas.

Los datos se guardan en archivos CSV y se recuperan al volver a abrir la aplicación.

## Requisitos

Java 17 o superior.

Para comprobar la instalación de Java, ejecutar:

java -version


## Ejecutar la aplicación

Descargar el proyecto y extraerlo si está comprimido. Abrir una terminal en la carpeta del proyecto y ejecutar:

```bash
cd dist
java -jar cafestock-1.0-SNAPSHOT.jar
```

La carpeta `dist` contiene el ejecutable y la subcarpeta `data` con los datos de prueba. Se debe ejecutar el comando desde `dist` para cargar esos datos.

Los registros, modificaciones y ventas realizados durante el uso actualizan el archivo `data/productos.csv`.

## Tablero del proyecto

https://trello.com/b/FAkNTyRm

## Repositorio

https://github.com/diogovillarreals/cafestock-scrum
