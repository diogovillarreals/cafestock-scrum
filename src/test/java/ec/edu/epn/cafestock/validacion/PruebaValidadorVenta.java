/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.epn.cafestock.validacion;

import ec.edu.epn.cafestock.modelo.Producto;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 *
 * @author pablo.vizuete
 */
public class PruebaValidadorVenta {

    public static void main(String[] args) {
        Producto cafe = new Producto("CAF-01", "Café pasado", new BigDecimal("2.50"), 10, 2);
        Producto sinStock = new Producto("TE-01", "Té verde", new BigDecimal("1.75"), 0, 1);
        List<String> ventas = new ArrayList<>();
        ventas.add("Venta inicial");

        // Casos inválidos (deben lanzar mensaje y conservar ventas y stock)
        verificarRechazo("Cantidad vacía", "", cafe, ventas);
        verificarRechazo("Cantidad solo espacios", "   ", cafe, ventas);
        verificarRechazo("Cantidad null", null, cafe, ventas);
        verificarRechazo("Cantidad con letras", "abc", cafe, ventas);
        verificarRechazo("Cantidad decimal con punto", "2.5", cafe, ventas);
        verificarRechazo("Cantidad decimal con coma", "1,5", cafe, ventas);
        verificarRechazo("Cantidad igual a cero", "0", cafe, ventas);
        verificarRechazo("Cantidad negativa", "-3", cafe, ventas);
        verificarRechazo("Cantidad superior al stock", "11", cafe, ventas);
        verificarRechazo("Producto sin stock", "1", sinStock, ventas);
        verificarRechazo("Producto nulo", "2", null, ventas);

        // Casos válidos
        verificarAceptacion("Cantidad válida menor al stock", "4", cafe, 4);
        verificarAceptacion("Cantidad válida igual al stock", "10", cafe, 10);

        System.out.println("Todas las pruebas de ValidadorVenta pasaron correctamente.");
    }

    private static void verificarRechazo(
            String nombreCaso,
            String textoCantidad,
            Producto producto,
            List<String> ventas) {

        int stockAnterior = (producto != null) ? producto.getStockActual() : -1;
        int ventasAnteriores = ventas.size();

        try {
            ValidadorVenta.validarCantidadContraStock(textoCantidad, producto);
            throw new AssertionError("[FALLÓ] " + nombreCaso + ": debió rechazar la cantidad.");
        } catch (IllegalArgumentException e) {
            boolean stockIntacto = (producto == null) || (producto.getStockActual() == stockAnterior);
            boolean ventasIntactas = ventas.size() == ventasAnteriores;

            if (!stockIntacto || !ventasIntactas || e.getMessage() == null || e.getMessage().isBlank()) {
                throw new AssertionError("[FALLÓ] " + nombreCaso + ": se alteró el estado o faltó el mensaje.");
            }
            System.out.println("[OK] " + nombreCaso + " -> Mensaje: " + e.getMessage());
        }
    }

    private static void verificarAceptacion(
            String nombreCaso,
            String textoCantidad,
            Producto producto,
            int esperado) {

        int resultado = ValidadorVenta.validarCantidadContraStock(textoCantidad, producto);
        if (resultado != esperado) {
            throw new AssertionError("[FALLÓ] " + nombreCaso + ": esperado " + esperado + ", obtenido " + resultado);
        }
        System.out.println("[OK] " + nombreCaso + " -> Cantidad validada: " + resultado);
    }
}
