package ec.edu.epn.cafestock.negocio;

import java.math.BigDecimal;

public final class CalculadoraTotalVenta {

    private CalculadoraTotalVenta() {
    }

    /**
     * Multiplica el precio unitario por una cantidad ya validada.
     */
    public static BigDecimal calcular(BigDecimal precioUnitario, int cantidad) {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
