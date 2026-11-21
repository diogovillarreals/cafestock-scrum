package ec.edu.epn.cafestock.servicio;

import ec.edu.epn.cafestock.modelo.Producto;
import ec.edu.epn.cafestock.persistencia.GestorInventarioArchivo;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PruebaRegistroProductos {

    public static void main(String[] args) throws Exception {
        Path directorio = Files.createTempDirectory("cafestock-hu-a1-");
        Path inventario = directorio.resolve("inventario.tsv");
        Path temporal = directorio.resolve("inventario.tsv.tmp");

        try {
            comprobarRegistro(inventario);
            System.out.println("HU-A1: pruebas de registro y persistencia correctas.");
        } finally {
            Files.deleteIfExists(temporal);
            Files.deleteIfExists(inventario);
            Files.deleteIfExists(directorio);
        }
    }

    private static void comprobarRegistro(Path inventario) throws Exception {
        GestorInventarioArchivo gestor = new GestorInventarioArchivo(inventario);
        RegistroProductos registro = new RegistroProductos(gestor);

        registro.registrar(new Producto("CAF-01", "Café americano", new BigDecimal("2.50"), 10, 5));
        String contenidoOriginal = Files.readString(inventario);

        comprobarRechazo(registro, "CAF-01", "El código CAF-01 ya existe.");
        comprobarRechazo(registro, "   ", "Completa el campo Código.");

        // Una nueva instancia debe detectar también los códigos guardados antes de abrirla.
        RegistroProductos registroReabierto = new RegistroProductos(new GestorInventarioArchivo(inventario));
        comprobarRechazo(registroReabierto, " caf-01 ", "El código caf-01 ya existe.");
        comprobar(Files.readString(inventario).equals(contenidoOriginal), "El archivo original cambió.");

        List<Producto> productos = gestor.cargar();
        comprobar(productos.size() == 1, "Debe quedar una sola fila.");
        Producto original = productos.get(0);
        comprobar(original.getCodigo().equals("CAF-01")
                && original.getNombre().equals("Café americano")
                && original.getPrecio().equals(new BigDecimal("2.50"))
                && original.getStockActual() == 10 && original.getStockMinimo() == 5,
                "El producto original perdió datos.");

        registroReabierto.registrar(new Producto("CAF-02", "Café filtrado", new BigDecimal("1.25"), 0, 0));
        productos = gestor.cargar();
        comprobar(productos.size() == 2 && productos.get(1).getCodigo().equals("CAF-02")
                && productos.get(1).getStockActual() == 0 && productos.get(1).getStockMinimo() == 0,
                "Debe aceptarse otro código con stock actual y mínimo en cero.");
    }

    private static void comprobarRechazo(RegistroProductos registro, String codigo, String mensajeEsperado) {
        try {
            registro.registrar(new Producto(codigo, "Otro producto", new BigDecimal("9.90"), 20, 8));
        } catch (IllegalArgumentException error) {
            comprobar(mensajeEsperado.equals(error.getMessage()), "Mensaje inesperado: " + error.getMessage());
            return;
        }
        throw new AssertionError("El código debía rechazarse: " + codigo);
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}
