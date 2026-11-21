/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.epn.cafestock.negocio;
import ec.edu.epn.cafestock.modelo.Producto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author pablo.vizuete
 */
public class PruebaBuscadorProductos {
    public static void main(String[] args) {
        List<Producto> inventario = List.of(
                new Producto("CAF-01", "Café molido", new BigDecimal("2.50"), 15, 5),
                new Producto("CAF-02", "Café frío", new BigDecimal("3.00"), 10, 3),
                new Producto("TE-01", "Té verde", new BigDecimal("1.75"), 20, 4)
        );

        // 1. Criterio de aceptación: buscar "Café" devuelve ambos cafés
        verificar("Buscar por parte del nombre ('Café')",
                BuscadorProductos.buscar(inventario, "Café"), 2);

        // 2. Criterio de aceptación: buscar por código exacto
        verificar("Buscar por código ('CAF-01')",
                BuscadorProductos.buscar(inventario, "CAF-01"), 1);

        // 3. Insensible a mayúsculas, tildes y espacios extra
        verificar("Buscar sin tilde y en minúsculas ('  cafe  ')",
                BuscadorProductos.buscar(inventario, "  cafe  "), 2);

        verificar("Buscar código en minúsculas ('caf-02')",
                BuscadorProductos.buscar(inventario, "caf-02"), 1);

        // 4. Sin coincidencias
        verificar("Buscar producto inexistente ('Chocolate')",
                BuscadorProductos.buscar(inventario, "Chocolate"), 0);

        // 5. Casos límite de criterio: vacío, espacios y null 
        verificar("Criterio vacío ('')",
                BuscadorProductos.buscar(inventario, ""), 3);

        verificar("Criterio solo espacios ('   ')",
                BuscadorProductos.buscar(inventario, "   "), 3);

        verificar("Criterio null",
                BuscadorProductos.buscar(inventario, null), 3);

        // 6. Casos límite de lista: lista vacía y lista null
        verificar("Lista vacía",
                BuscadorProductos.buscar(new ArrayList<>(), "Café"), 0);

        verificar("Lista null",
                BuscadorProductos.buscar(null, "Café"), 0);
    }

    private static void verificar(String nombreCaso, List<Producto> resultado, int esperados) {
        boolean paso = resultado.size() == esperados;
        String estado = paso ? "OK" : "FALLÓ";
        System.out.println("[" + estado + "] " + nombreCaso
                + " -> encontrados: " + resultado.size()
                + " (esperados: " + esperados + ")");
    }
}

