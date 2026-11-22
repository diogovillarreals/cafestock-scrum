/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.epn.cafestock.validacion;

import ec.edu.epn.cafestock.modelo.Producto;

/**
 *
 * @author pablo.vizuete
 */
public class ValidadorVenta {

    private ValidadorVenta() {
    }

    public static int validarCantidadContraStock(String textoCantidad, Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("Debe seleccionar un producto.");
        }

        if (textoCantidad == null || textoCantidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La cantidad es obligatoria.");
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(textoCantidad.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La cantidad debe ser un número entero válido.");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        if (cantidad > producto.getStockActual()) {
            throw new IllegalArgumentException(
                    "Stock insuficiente. Stock disponible: " + producto.getStockActual() + "."
            );
        }

        return cantidad;
    }
}
