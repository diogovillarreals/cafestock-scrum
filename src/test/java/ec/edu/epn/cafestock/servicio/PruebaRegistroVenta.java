/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.epn.cafestock.servicio;

import ec.edu.epn.cafestock.modelo.Producto;
import ec.edu.epn.cafestock.modelo.Venta;
import ec.edu.epn.cafestock.persistencia.GestorInventarioArchivo;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Dell G15
 */
public class PruebaRegistroVenta {

    public static void main(String[] args) throws Exception {

        Path directorio = Files.createTempDirectory("prueba-cafestock-");
        Path archivo = directorio.resolve("productos.csv");

        try {

            GestorInventarioArchivo repositorio =
                    new GestorInventarioArchivo(archivo);

            ServicioProductos servicio =
                    new ServicioProductos(repositorio);

            Producto cafe = new Producto(
                    "CAF-TEST",
                    "Café de prueba",
                    new BigDecimal("2.50"),
                    10,
                    2
            );

            repositorio.guardar(List.of(cafe));

            System.out.println("=== PRUEBAS DE REGISTRO DE VENTA ===");

            Venta venta = new Venta(
                    cafe,
                    3,
                    LocalDate.now()
            );

            List<Producto> resultado =
                    servicio.registrarVenta(venta);

            comprobar(
                    resultado.get(0).getStockActual() == 7,
                    "Venta válida: el stock disminuyó de 10 a 7."
            );

            List<Producto> inventarioGuardado =
                    servicio.listarProductos();

            comprobar(
                    inventarioGuardado.get(0).getStockActual() == 7,
                    "Persistencia: el stock guardado es 7."
            );

            try {

                Venta ventaInvalida = new Venta(
                        cafe,
                        8,
                        LocalDate.now()
                );

                servicio.registrarVenta(ventaInvalida);

                throw new AssertionError(
                        "La venta superior al stock debió rechazarse."
                );

            } catch (IllegalArgumentException error) {

                System.out.println(
                        "[OK] Venta superior al stock: "
                        + error.getMessage()
                );
            }

            List<Producto> inventarioFinal =
                    servicio.listarProductos();

            comprobar(
                    inventarioFinal.get(0).getStockActual() == 7,
                    "Venta rechazada: el stock permanece en 7."
            );

            System.out.println();
            System.out.println(
                    "Todas las pruebas finalizaron correctamente."
            );

        } finally {

            Files.deleteIfExists(archivo);
            Files.deleteIfExists(directorio);
        }
    }

    private static void comprobar(
            boolean condicion,
            String mensaje) {

        if (!condicion) {
            throw new AssertionError("[FALLÓ] " + mensaje);
        }

        System.out.println("[OK] " + mensaje);
    }
}
