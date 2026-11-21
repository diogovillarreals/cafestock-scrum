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

        // 1. Buscar "Café" devuelve ambos cafés
        verificar("Buscar por parte del nombre ('Café')",
                BuscadorProductos.buscar(inventario, "Café"), "CAF-01", "CAF-02");

        // 2. Buscar por código exacto
        verificar("Buscar por código ('CAF-01')",
                BuscadorProductos.buscar(inventario, "CAF-01"), "CAF-01");

        // 3. Sin tilde y en minúsculas
        verificar("Buscar sin tilde y en minúsculas ('  cafe  ')",
                BuscadorProductos.buscar(inventario, "  cafe  "), "CAF-01", "CAF-02");

        verificar("Buscar código en minúsculas ('caf-02')",
                BuscadorProductos.buscar(inventario, "caf-02"), "CAF-02");

        // 4. Sin coincidencias (sin pasar códigos)
        verificar("Buscar producto inexistente ('Chocolate')",
                BuscadorProductos.buscar(inventario, "Chocolate"));

        // 5. Criterio vacío, espacios y null (devuelve los 3)
        verificar("Criterio vacío ('')",
                BuscadorProductos.buscar(inventario, ""), "CAF-01", "CAF-02", "TE-01");

        verificar("Criterio solo espacios ('   ')",
                BuscadorProductos.buscar(inventario, "   "), "CAF-01", "CAF-02", "TE-01");

        verificar("Criterio null",
                BuscadorProductos.buscar(inventario, null), "CAF-01", "CAF-02", "TE-01");

        // 6. Lista vacía y lista null
        verificar("Lista vacía",
                BuscadorProductos.buscar(new ArrayList<>(), "Café"));

        verificar("Lista null",
                BuscadorProductos.buscar(null, "Café"));
    }

    private static void verificar(
            String nombreCaso,
            List<Producto> resultado,
            String... codigosEsperados) {

        List<String> encontrados = resultado.stream()
                .map(Producto::getCodigo)
                .toList();

        List<String> esperados = List.of(codigosEsperados);

        boolean paso = encontrados.size() == esperados.size()
                && encontrados.containsAll(esperados)
                && esperados.containsAll(encontrados);

        String estado = paso ? "OK" : "FALLÓ";

        System.out.println("[" + estado + "] " + nombreCaso);
        System.out.println("Encontrados: " + encontrados);
        System.out.println("Esperados: " + esperados);

        if (!paso) {
            throw new AssertionError(
                    "Prueba fallida: " + nombreCaso
            );
        }
    }
}
