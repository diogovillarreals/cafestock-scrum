package ec.edu.epn.cafestock.validacion;

import java.math.BigDecimal;

public class PruebaValidarCampos {

    public static void main(String[] args) {
        try {
            String codigo = ValidarCampos.validarObligatorio(
                "CAF-01", "Código"
            );

            String nombre = ValidarCampos.validarObligatorio(
                "Café americano", "Nombre"
            );

            BigDecimal precio = ValidarCampos.validarPrecio("das");

            int stockActual = ValidarCampos.validarStock(
                "10", "Stock actual"
            );

            int stockMinimo = ValidarCampos.validarStock(
                "5", "Stock mínimo"
            );

            System.out.println("Datos válidos");
            System.out.println("Código: " + codigo);
            System.out.println("Nombre: " + nombre);
            System.out.println("Precio: " + precio);
            System.out.println("Stock actual: " + stockActual);
            System.out.println("Stock mínimo: " + stockMinimo);

        } catch (IllegalArgumentException error) {
            System.out.println("Error: " + error.getMessage());
        }
    }
}