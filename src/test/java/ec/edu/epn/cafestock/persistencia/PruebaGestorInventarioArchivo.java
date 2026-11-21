/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.epn.cafestock.persistencia;

import ec.edu.epn.cafestock.modelo.Producto;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
/**
 *
 * @author Dell G15
 */
public class PruebaGestorInventarioArchivo {

    public static void main(String[] args) {
        Path rutaPrueba = Paths.get("target/prueba-inventario.tsv");
        Path rutaCorrupta = Paths.get("target/prueba-inventario-invalido.tsv");
        GestorInventarioArchivo gestor = new GestorInventarioArchivo(rutaPrueba);

        try {
            List<Producto> inicial = gestor.cargar();
            System.out.println("1. Lista inicial (archivo inexistente): " + inicial.size());

            Producto p1 = new Producto("CAF-01", "Café americano", new BigDecimal("2.50"), 10, 5);
            Producto p2 = new Producto("PAN-02", "Croissant", new BigDecimal("1.75"), 0, 0);

            gestor.guardar(List.of(p1, p2));
            List<Producto> cargados = gestor.cargar();

            System.out.println("2. Productos guardados y cargados: " + cargados.size());
            for (Producto p : cargados) {
                System.out.println("   - Código: " + p.getCodigo()
                        + " | Nombre: " + p.getNombre()
                        + " | Precio: " + p.getPrecio()
                        + " | Stock actual: " + p.getStockActual()
                        + " | Stock mínimo: " + p.getStockMinimo());
            }

            gestor.guardar(List.of());
            System.out.println("3. Lista vacía cargada: " + gestor.cargar().size());

            Files.createDirectories(rutaCorrupta.getParent());
            Files.writeString(
                    rutaCorrupta,
                    "CAF-03\tLatte\tNO_NUMERICO\t4\t2\n",
                    StandardCharsets.UTF_8
            );
            GestorInventarioArchivo gestorInvalido = new GestorInventarioArchivo(rutaCorrupta);

            try {
                gestorInvalido.cargar();
                System.out.println("4. Aviso: no se lanzó error ante archivo inválido.");
            } catch (RuntimeException errorInvalido) {
                System.out.println("4. Error esperado ante dato inválido: " + errorInvalido.getMessage());
            }

            System.out.println("Pruebas de persistencia finalizadas.");
        } catch (Exception error) {
            System.out.println("Error inesperado en la prueba: " + error.getMessage());
        }
    }
}
