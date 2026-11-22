package ec.edu.epn.cafestock.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public record VentaRegistrada(long numero, LocalDate fechaVenta, String codigoProducto,
                               String nombreProducto, int cantidad, BigDecimal total) {

    public VentaRegistrada {
        Objects.requireNonNull(fechaVenta, "La fecha de venta es obligatoria.");
        Objects.requireNonNull(total, "El total de venta es obligatorio.");
        if (numero <= 0 || cantidad <= 0 || total.signum() <= 0) {
            throw new IllegalArgumentException("El número, la cantidad y el total deben ser positivos.");
        }
        if (codigoProducto == null || codigoProducto.isBlank()
                || nombreProducto == null || nombreProducto.isBlank()) {
            throw new IllegalArgumentException("Indica el código y el nombre del producto vendido.");
        }
    }
}
