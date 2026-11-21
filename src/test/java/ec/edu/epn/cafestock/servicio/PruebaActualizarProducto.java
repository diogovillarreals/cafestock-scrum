package ec.edu.epn.cafestock.servicio;

import ec.edu.epn.cafestock.modelo.Producto;
import ec.edu.epn.cafestock.persistencia.GestorInventarioArchivo;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import ec.edu.epn.cafestock.validacion.ValidarEdicionProducto;

public class PruebaActualizarProducto {

    public static void main(String[] args) throws Exception {
        Path carpeta = Files.createTempDirectory("cafestock-edicion-");
        Path archivo = carpeta.resolve("inventario.tsv");

        GestorInventarioArchivo repositorio =
                new GestorInventarioArchivo(archivo);

        ServicioProductos servicio = new ServicioProductos(repositorio);

        Producto cafe = new Producto(
                "CAF-01", "Café", new BigDecimal("1.50"), 10, 2
        );

        Producto pan = new Producto(
                "PAN-01", "Pan", new BigDecimal("0.50"), 8, 3
        );

        repositorio.guardar(List.of(cafe, pan));

        Producto editado = ValidarEdicionProducto.validar(
        cafe,
        "Café grande",
        "2,50",
        "0",
        "0"
);

        List<Producto> resultado = servicio.actualizarProducto(editado);

        comprobar(resultado.size() == 2,
                "Editar no debe añadir otra fila.");

        comprobar(resultado.get(0).getNombre().equals("Café grande"),
                "Debe actualizar el nombre.");

        // Simula volver a abrir el inventario desde otra instancia.
        List<Producto> recuperados =
                new GestorInventarioArchivo(archivo).cargar();

        Producto guardado = recuperados.get(0);

        comprobar(guardado.getCodigo().equals("CAF-01")
                && guardado.getNombre().equals("Café grande")
                && guardado.getPrecio().compareTo(
                        new BigDecimal("2.50")) == 0
                && guardado.getStockActual() == 0
                && guardado.getStockMinimo() == 0,
                "Los cambios deben conservarse al reabrir.");

        Producto otro = recuperados.get(1);

        comprobar(otro.getCodigo().equals("PAN-01")
                && otro.getNombre().equals("Pan")
                && otro.getPrecio().compareTo(
                        new BigDecimal("0.50")) == 0
                && otro.getStockActual() == 8
                && otro.getStockMinimo() == 3,
                "El otro producto debe permanecer intacto.");

        String contenidoAnterior = Files.readString(archivo);
        
        try {
    Producto invalido = ValidarEdicionProducto.validar(
            guardado,
            "Café grande",
            "abc",
            "0",
            "0"
    );

    servicio.actualizarProducto(invalido);

    throw new AssertionError(
            "El precio con letras debía rechazarse."
    );

} catch (IllegalArgumentException esperado) {
    System.out.println(
            "OK: la validación rechazó la edición inválida."
    );
}

comprobar(
        Files.readString(archivo).equals(contenidoAnterior),
        "La edición rechazada no debe modificar el archivo."
);

        comprobarRechazo(servicio, new Producto(
                "NO-EXISTE", "Otro", BigDecimal.ONE, 0, 0
        ));

        comprobar(Files.readString(archivo).equals(contenidoAnterior),
                "Un código inexistente no debe modificar el archivo.");

        comprobarRechazo(servicio, new Producto(
                "CAF-01", "Café", new BigDecimal("-1"), 0, 0
        ));

        comprobar(Files.readString(archivo).equals(contenidoAnterior),
                "Una edición inválida no debe modificar el archivo.");

        System.out.println("HU-A2: pruebas de actualización correctas.");
        System.out.println("Archivo temporal de prueba: " + archivo);
    }

    private static void comprobarRechazo(
            ServicioProductos servicio, Producto producto) {

        try {
            servicio.actualizarProducto(producto);
        } catch (IllegalArgumentException esperado) {
            return;
        }

        throw new AssertionError("La actualización debía rechazarse.");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}