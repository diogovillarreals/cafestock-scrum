/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.epn.cafestock.modelo;

import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author Dell G15
 */
public class Venta {

    private final Producto producto;
    private final int cantidad;
    private final LocalDate fechaVenta;

    public Venta(Producto producto, int cantidad, LocalDate fechaVenta) {

        this.producto = Objects.requireNonNull(
                producto,
                "El producto no puede ser nulo."
        );

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor a cero."
            );
        }

        this.cantidad = cantidad;

        this.fechaVenta = Objects.requireNonNull(
                fechaVenta,
                "La fecha de venta es obligatoria."
        );
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }
}