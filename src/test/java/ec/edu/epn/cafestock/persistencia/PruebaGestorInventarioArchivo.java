/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.epn.cafestock.persistencia;

import ec.edu.epn.cafestock.persistencia.GestorInventarioArchivo.PersistenciaInventarioException;
import ec.edu.epn.cafestock.modelo.Producto;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
/**
 *
 * @author Dell G15
 */
public class PruebaGestorInventarioArchivo {

    private static final Path BASE_PRUEBAS = Paths.get("target");

    public static void main(String[] args) throws Exception {
        ejecutarBateriaCompleta("ejecucion-1");
        // Caso 10: Ejecuciones repetidas sin restos que alteren archivo inexistente
        ejecutarBateriaCompleta("ejecucion-2");

        System.out.println("OK: Todas las pruebas de PruebaGestorInventarioArchivo (casos 01-11) pasaron.");
        System.out.println("Nota caso 11 (Movimiento atomico): Se verifico reemplazo de archivo existente mediante "
                + "archivo temporal + ATOMIC_MOVE; simular de forma determinista un sistema de archivos sin soporte "
                + "ATOMIC_MOVE requiere un proveedor/entorno de archivos específico o fallo de SO no forzado en prueba estándar.");
    }

    private static void ejecutarBateriaCompleta(String etiquetaEjecucion) throws IOException {
        Path directorioEjecucion = BASE_PRUEBAS.resolve(
                "prueba-gestor-" + etiquetaEjecucion + "-" + UUID.randomUUID()
        );
        Files.createDirectories(directorioEjecucion);

        caso01PrimerInicio(directorioEjecucion);
        caso02GuardarYRecuperar(directorioEjecucion);
        caso03CaracteresYDecimales(directorioEjecucion);
        caso04StocksCero(directorioEjecucion);
        caso05SustituirInventario(directorioEjecucion);
        caso06ProductoInvalidoNoAlteraAnterior(directorioEjecucion);
        caso07CsvInvalidoConUbicacion(directorioEjecucion);
        caso08CorrupcionIntermediaSinCargaParcial(directorioEjecucion);
        caso09FalloDeEscrituraControlado(directorioEjecucion);
        caso11ReemplazoArchivoExistente(directorioEjecucion);
    }

    private static void caso01PrimerInicio(Path dir) {
        Path archivo = dir.resolve("01-inexistente").resolve("productos.csv");
        GestorInventarioArchivo gestor = new GestorInventarioArchivo(archivo);

        List<Producto> cargados = gestor.cargar();
        assertTrue(cargados != null, "Caso 01: cargar() no debe devolver null.");
        assertTrue(cargados.isEmpty(), "Caso 01: ruta inexistente debe devolver lista vacía.");
    }

    private static void caso02GuardarYRecuperar(Path dir) {
        Path archivo = dir.resolve("02-guardar-recuperar").resolve("productos.csv");
        GestorInventarioArchivo escritor = new GestorInventarioArchivo(archivo);

        Producto p1 = new Producto("CAF-01", "Cafe Americano", new BigDecimal("2.50"), 12, 4);
        Producto p2 = new Producto("PAN-02", "Croissant", new BigDecimal("1.75"), 8, 2);
        Producto p3 = new Producto("JUG-03", "Jugo Natural", new BigDecimal("3.00"), 15, 5);

        escritor.guardar(List.of(p1, p2, p3));

        GestorInventarioArchivo lector = new GestorInventarioArchivo(archivo);
        List<Producto> recuperados = lector.cargar();

        assertTrue(recuperados.size() == 3, "Caso 02: se esperaban 3 productos cargados.");
        assertProductoIgual(p1, recuperados.get(0), "Caso 02 (producto 1)");
        assertProductoIgual(p2, recuperados.get(1), "Caso 02 (producto 2)");
        assertProductoIgual(p3, recuperados.get(2), "Caso 02 (producto 3)");
    }

    private static void caso03CaracteresYDecimales(Path dir) {
        Path archivo = dir.resolve("03-caracteres-decimales").resolve("productos.csv");
        GestorInventarioArchivo gestor = new GestorInventarioArchivo(archivo);

        String nombreEspecial = "Café \"Especial\", con coma\ny salto \\ barra áéíóú Ñ";
        Producto especial = new Producto("ESP-01", nombreEspecial, new BigDecimal("12.3450"), 7, 3);

        gestor.guardar(List.of(especial));
        List<Producto> cargados = new GestorInventarioArchivo(archivo).cargar();

        assertTrue(cargados.size() == 1, "Caso 03: se esperaba 1 producto.");
        Producto recuperado = cargados.get(0);
        assertTrue("ESP-01".equals(recuperado.getCodigo()), "Caso 03: código no coincide.");
        assertTrue(nombreEspecial.equals(recuperado.getNombre()),
                "Caso 03: caracteres especiales/comas/comillas/saltos/barras no se conservaron.");
        assertTrue(recuperado.getPrecio().compareTo(new BigDecimal("12.345")) == 0,
                "Caso 03: precio no coincide usando BigDecimal.compareTo.");
        assertTrue(recuperado.getStockActual() == 7, "Caso 03: stockActual no coincide.");
        assertTrue(recuperado.getStockMinimo() == 3, "Caso 03: stockMinimo no coincide.");
    }

    private static void caso04StocksCero(Path dir) {
        Path archivo = dir.resolve("04-stocks-cero").resolve("productos.csv");
        GestorInventarioArchivo gestor = new GestorInventarioArchivo(archivo);

        Producto agotado = new Producto("AG-01", "Te Verde", new BigDecimal("1.25"), 0, 0);
        gestor.guardar(List.of(agotado));

        List<Producto> cargados = new GestorInventarioArchivo(archivo).cargar();
        assertTrue(cargados.size() == 1, "Caso 04: se esperaba 1 producto.");
        assertTrue(cargados.get(0).getStockActual() == 0, "Caso 04: stockActual debe ser 0.");
        assertTrue(cargados.get(0).getStockMinimo() == 0, "Caso 04: stockMinimo debe ser 0.");
    }

    private static void caso05SustituirInventario(Path dir) {
        Path archivo = dir.resolve("05-sustituir").resolve("productos.csv");
        GestorInventarioArchivo gestor = new GestorInventarioArchivo(archivo);

        Producto a = new Producto("A-01", "Producto A", new BigDecimal("1.00"), 5, 1);
        Producto b = new Producto("B-01", "Producto B", new BigDecimal("2.00"), 6, 2);

        gestor.guardar(List.of(a, b));
        assertTrue(gestor.cargar().size() == 2, "Caso 05: inicialmente debe haber 2 productos.");

        gestor.guardar(List.of(b));
        List<Producto> soloB = gestor.cargar();
        assertTrue(soloB.size() == 1, "Caso 05: tras guardar solo B debe haber 1 producto.");
        assertProductoIgual(b, soloB.get(0), "Caso 05 (solo B)");

        gestor.guardar(List.of());
        List<Producto> vacio = gestor.cargar();
        assertTrue(vacio.isEmpty(), "Caso 05: tras guardar lista vacía debe cargarse lista vacía.");
    }

    private static void caso06ProductoInvalidoNoAlteraAnterior(Path dir) {
        Path archivo = dir.resolve("06-producto-invalido").resolve("productos.csv");
        GestorInventarioArchivo gestor = new GestorInventarioArchivo(archivo);

        Producto valido = new Producto("VAL-01", "Cafe Valido", new BigDecimal("2.00"), 4, 1);
        gestor.guardar(List.of(valido));
        String contenidoAntes = leerTexto(archivo);

        List<List<Producto>> lotesInvalidos = Arrays.asList(
                Arrays.asList((Producto) null),
                List.of(new Producto("", "Nombre", new BigDecimal("1.00"), 1, 0)),
                List.of(new Producto("COD-1", "   ", new BigDecimal("1.00"), 1, 0)),
                List.of(new Producto("COD-2", "Nombre", null, 1, 0)),
                List.of(new Producto("COD-3", "Nombre", BigDecimal.ZERO, 1, 0)),
                List.of(new Producto("COD-4", "Nombre", new BigDecimal("-0.50"), 1, 0)),
                List.of(new Producto("COD-5", "Nombre", new BigDecimal("1.50"), -1, 0)),
                List.of(new Producto("COD-6", "Nombre", new BigDecimal("1.50"), 1, -2))
        );

        for (int i = 0; i < lotesInvalidos.size(); i++) {
            final int indice = i;
            List<Producto> lote = lotesInvalidos.get(i);
            assertLanzaPersistenciaException(
                    () -> gestor.guardar(lote),
                    "Caso 06 (lote #" + (indice + 1) + "): debe rechazar producto inválido."
            );
            assertTrue(contenidoAntes.equals(leerTexto(archivo)),
                    "Caso 06 (lote #" + (indice + 1) + "): no debe modificar el archivo anterior.");
        }
    }

    private static void caso07CsvInvalidoConUbicacion(Path dir) throws IOException {
        Path base = dir.resolve("07-csv-invalido");
        Files.createDirectories(base);

        Path pocosCampos = base.resolve("pocos-campos.csv");
        Files.writeString(pocosCampos, "C-01,Cafe,1.50,10\n", StandardCharsets.UTF_8);
        PersistenciaInventarioException exCampos = assertLanzaPersistenciaException(
                () -> new GestorInventarioArchivo(pocosCampos).cargar(),
                "Caso 07: debe rechazar cantidad incorrecta de campos."
        );
        assertTrue(exCampos.getMessage().contains("1"),
                "Caso 07: debe indicar la ubicación/línea del error de campos.");

        Path comillasSinCerrar = base.resolve("comillas-sin-cerrar.csv");
        Files.writeString(comillasSinCerrar, "C-02,\"Cafe sin cerrar,1.50,10,2\n", StandardCharsets.UTF_8);
        PersistenciaInventarioException exComillas = assertLanzaPersistenciaException(
                () -> new GestorInventarioArchivo(comillasSinCerrar).cargar(),
                "Caso 07: debe rechazar comillas sin cerrar."
        );
        assertTrue(exComillas.getMessage().contains("1"),
                "Caso 07: debe indicar la ubicación/línea de comillas sin cerrar.");

        Path numeroInvalido = base.resolve("numero-invalido.csv");
        Files.writeString(numeroInvalido, "C-03,Cafe,ABC,10,2\n", StandardCharsets.UTF_8);
        PersistenciaInventarioException exNumero = assertLanzaPersistenciaException(
                () -> new GestorInventarioArchivo(numeroInvalido).cargar(),
                "Caso 07: debe rechazar número inválido."
        );
        assertTrue(exNumero.getMessage().contains("1"),
                "Caso 07: debe indicar la ubicación/línea del número inválido.");
    }

    private static void caso08CorrupcionIntermediaSinCargaParcial(Path dir) throws IOException {
        Path archivo = dir.resolve("08-corrupcion-intermedia").resolve("productos.csv");
        Files.createDirectories(archivo.getParent());

        String contenido = "OK-01,Producto Valido,2.50,10,2\n"
                + "BAD-02,Producto Malo,-1.00,5,1\n";
        Files.writeString(archivo, contenido, StandardCharsets.UTF_8);

        PersistenciaInventarioException ex = assertLanzaPersistenciaException(
                () -> new GestorInventarioArchivo(archivo).cargar(),
                "Caso 08: corrupción intermedia debe lanzar PersistenciaInventarioException y no cargar parcialmente."
        );
        assertTrue(ex.getMessage().contains("2"),
                "Caso 08: el mensaje debe ubicar el registro corrupto en la línea 2.");
    }

    private static void caso09FalloDeEscrituraControlado(Path dir) throws IOException {
        Path rutaQueEsDirectorio = dir.resolve("09-fallo-escritura").resolve("destino-como-carpeta.csv");
        Files.createDirectories(rutaQueEsDirectorio);

        GestorInventarioArchivo gestor = new GestorInventarioArchivo(rutaQueEsDirectorio);
        Producto p = new Producto("ERR-01", "Prueba Error", new BigDecimal("1.00"), 1, 0);

        assertLanzaPersistenciaException(
                () -> gestor.guardar(List.of(p)),
                "Caso 09: fallo controlado de escritura debe lanzar PersistenciaInventarioException."
        );
    }

    private static void caso11ReemplazoArchivoExistente(Path dir) {
        Path archivo = dir.resolve("11-movimiento-atomico").resolve("productos.csv");
        GestorInventarioArchivo gestor = new GestorInventarioArchivo(archivo);

        Producto inicial = new Producto("INI-01", "Inicial", new BigDecimal("1.50"), 3, 1);
        Producto reemplazo = new Producto("REP-01", "Reemplazo", new BigDecimal("4.25"), 9, 2);

        gestor.guardar(List.of(inicial));
        gestor.guardar(List.of(reemplazo));

        List<Producto> cargados = gestor.cargar();
        assertTrue(cargados.size() == 1, "Caso 11: debe quedar solo el producto de reemplazo.");
        assertProductoIgual(reemplazo, cargados.get(0), "Caso 11 (reemplazo)");
    }

    private static void assertProductoIgual(Producto esperado, Producto actual, String contexto) {
        assertTrue(actual != null, contexto + ": producto actual es null.");
        assertTrue(esperado.getCodigo().equals(actual.getCodigo()), contexto + ": código distinto.");
        assertTrue(esperado.getNombre().equals(actual.getNombre()), contexto + ": nombre distinto.");
        assertTrue(esperado.getPrecio().compareTo(actual.getPrecio()) == 0, contexto + ": precio distinto.");
        assertTrue(esperado.getStockActual() == actual.getStockActual(), contexto + ": stockActual distinto.");
        assertTrue(esperado.getStockMinimo() == actual.getStockMinimo(), contexto + ": stockMinimo distinto.");
    }

    private static PersistenciaInventarioException assertLanzaPersistenciaException(
            Runnable accion, String mensajeFallo) {
        try {
            accion.run();
        } catch (PersistenciaInventarioException ex) {
            return ex;
        }
        throw new AssertionError(mensajeFallo + " (no se lanzó PersistenciaInventarioException)");
    }

    private static void assertTrue(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    private static String leerTexto(Path archivo) {
        try {
            return Files.readString(archivo, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AssertionError("No se pudo leer archivo de prueba: " + archivo, e);
        }
    }
}
