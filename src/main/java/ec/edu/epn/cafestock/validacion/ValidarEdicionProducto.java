package ec.edu.epn.cafestock.validacion;

import ec.edu.epn.cafestock.modelo.Producto;
import java.math.BigDecimal;

public final class ValidarEdicionProducto {

    private ValidarEdicionProducto() {
    }

    public static Producto validar(Producto original, String nombre, String precio,
                                   String stockActual, String stockMinimo) {
        String nombreValido = ValidarCampos.validarObligatorio(nombre, "Nombre");
        BigDecimal precioValido = ValidarCampos.validarPrecio(precio);
        int stockActualValido = ValidarCampos.validarStock(stockActual, "Stock actual");
        int stockMinimoValido = ValidarCampos.validarStock(stockMinimo, "Stock mínimo");

        return new Producto(original.getCodigo(), nombreValido, precioValido,
                stockActualValido, stockMinimoValido);
    }
}
