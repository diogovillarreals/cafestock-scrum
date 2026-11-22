/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.epn.cafestock.servicio;

import ec.edu.epn.cafestock.modelo.Producto;
import ec.edu.epn.cafestock.modelo.Venta;
import ec.edu.epn.cafestock.modelo.VentaRegistrada;
import ec.edu.epn.cafestock.persistencia.GestorInventarioArchivo;
import ec.edu.epn.cafestock.persistencia.GestorInventarioArchivo.PersistenciaInventarioException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class PruebaRegistroVenta {

    public static void main(String[] args) throws Exception {
        Path directorio = Files.createTempDirectory("prueba-cafestock-");
        Path archivo = directorio.resolve("productos.csv");

        try {
            comprobarPersistencia(archivo);
            comprobarVentasConcurrentes(directorio.resolve("concurrente.csv"));
            System.out.println("HU-A3: pruebas de registro, persistencia y stock correctas.");
        } finally {
            Files.deleteIfExists(directorio.resolve("concurrente.csv"));
            Files.deleteIfExists(archivo);
            Files.deleteIfExists(directorio);
        }
    }

    private static void comprobarPersistencia(Path archivo) throws Exception {
        Files.writeString(archivo, "\"CAF,01\",\"Café \"\"especial\"\"\",1.50,10,2\n");
        GestorInventarioArchivo repositorio = new GestorInventarioArchivo(archivo);
        ServicioProductos servicio = new ServicioProductos(repositorio);
        Producto cafe = servicio.listarProductos().get(0);
        LocalDate fechaElegida = LocalDate.of(2025, 3, 12);

        List<Producto> inventario = servicio.registrarVenta(new Venta(cafe, 3, fechaElegida));
        comprobar(inventario.get(0).getStockActual() == 7, "El stock debe bajar de 10 a 7.");

        GestorInventarioArchivo reabierto = new GestorInventarioArchivo(archivo);
        ServicioProductos siguienteSesion = new ServicioProductos(reabierto);
        comprobar(siguienteSesion.listarProductos().get(0).getStockActual() == 7,
                "Al reabrir, el stock debe seguir en 7.");
        VentaRegistrada primeraVenta = new VentaRegistrada(
                1, fechaElegida, cafe.getCodigo(), cafe.getNombre(), 3, new BigDecimal("4.50"));
        comprobar(reabierto.cargarVentas().equals(List.of(primeraVenta)),
                "La venta debe conservar número, fecha elegida, producto, cantidad y total.");

        String estadoAnterior = Files.readString(archivo);
        esperarRechazo(() -> siguienteSesion.registrarVenta(new Venta(cafe, 8, fechaElegida)));
        comprobar(Files.readString(archivo).equals(estadoAnterior),
                "Si falta stock, el inventario y las ventas deben quedar intactos.");

        Producto stockInvalido = new Producto(cafe.getCodigo(), cafe.getNombre(), cafe.getPrecio(), -1, 2);
        try {
            reabierto.guardarVenta(List.of(stockInvalido), new Venta(cafe, 1, fechaElegida),
                    new BigDecimal("1.50"));
            throw new AssertionError("Un error al guardar el inventario debe rechazar la venta.");
        } catch (PersistenciaInventarioException error) {
            comprobar(Files.readString(archivo).equals(estadoAnterior),
                    "Un fallo en la escritura debe dejar intacto el archivo previo.");
        }

        Producto editado = new Producto(cafe.getCodigo(), "Café renombrado", new BigDecimal("2.00"), 7, 2);
        siguienteSesion.actualizarProducto(editado);
        siguienteSesion.registrarProducto(new Producto("VENTA", "Té", BigDecimal.ONE, 4, 1));
        comprobar(reabierto.cargarVentas().equals(List.of(primeraVenta)),
                "Editar o agregar productos debe conservar el historial de ventas.");

        estadoAnterior = Files.readString(archivo);
        esperarRechazo(() -> siguienteSesion.registrarVenta(new Venta(cafe, 1, fechaElegida)));
        comprobar(Files.readString(archivo).equals(estadoAnterior),
                "Si cambió el precio, debe pedirse una nueva confirmación.");

        siguienteSesion.registrarVenta(new Venta(editado, 2, fechaElegida.plusDays(1)));
        VentaRegistrada segundaVenta = new VentaRegistrada(
                2, fechaElegida.plusDays(1), editado.getCodigo(), editado.getNombre(), 2, new BigDecimal("4.00"));
        comprobar(reabierto.cargarVentas().equals(List.of(primeraVenta, segundaVenta)),
                "La numeración debe continuar tras reabrir y conservar el primer total.");
        comprobar(reabierto.cargar().get(0).getStockActual() == 5, "La segunda venta debe dejar 5 unidades.");
    }

    private static void comprobarVentasConcurrentes(Path archivo) throws Exception {
        Producto cafe = new Producto("CAF", "Café", new BigDecimal("1.50"), 10, 2);
        GestorInventarioArchivo repositorio = new GestorInventarioArchivo(archivo);
        repositorio.guardar(List.of(cafe));
        ServicioProductos primero = new ServicioProductos(repositorio);
        ServicioProductos segundo = new ServicioProductos(new GestorInventarioArchivo(archivo));
        Venta venta = new Venta(cafe, 3, LocalDate.of(2025, 3, 12));
        CountDownLatch comenzar = new CountDownLatch(1);
        var ejecutor = Executors.newFixedThreadPool(2);
        try {
            var primeraVenta = ejecutor.submit(() -> {
                comenzar.await();
                return primero.registrarVenta(venta);
            });
            var segundaVenta = ejecutor.submit(() -> {
                comenzar.await();
                return segundo.registrarVenta(venta);
            });
            comenzar.countDown();
            primeraVenta.get();
            segundaVenta.get();
        } finally {
            ejecutor.shutdownNow();
        }

        comprobar(repositorio.cargar().get(0).getStockActual() == 4,
                "Dos ventas simultáneas deben descontar 6 unidades en total.");
        comprobar(repositorio.cargarVentas().stream().map(VentaRegistrada::numero).toList().equals(List.of(1L, 2L)),
                "Dos ventas simultáneas deben recibir números distintos.");
    }

    private static void esperarRechazo(Runnable operacion) {
        try {
            operacion.run();
            throw new AssertionError("La venta debía rechazarse.");
        } catch (IllegalArgumentException error) {
            comprobar(!error.getMessage().isBlank(), "El rechazo debe indicar su causa.");
        }
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}
