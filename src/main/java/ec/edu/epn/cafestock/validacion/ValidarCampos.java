/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.epn.cafestock.validacion;

import java.math.BigDecimal;

/**
 *
 * @author diogo
 */
public class ValidarCampos {

    public static String validarObligatorio(String valor, String nombreCampo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "Completa el campo " + nombreCampo + "."
            );
        }

        return valor.strip();
    }

    public static BigDecimal validarPrecio(String valor) {
        String texto = validarObligatorio(valor, "Precio");
        texto = texto.replace(',', '.');

        BigDecimal precio;

        try {
            precio = new BigDecimal(texto);
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException(
                    "El precio debe ser un número, por ejemplo 1,50."
            );
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser mayor que cero."
            );
        }

        return precio;
    }

    public static int validarStock(String valor, String nombreCampo) {
        String texto = validarObligatorio(valor, nombreCampo);
        int stock;

        try {
            stock = Integer.parseInt(texto);
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException(
                    nombreCampo + " debe ser un número entero válido."
            );
        }

        if (stock < 0) {
            throw new IllegalArgumentException(
                    nombreCampo + " debe ser mayor o igual que cero."
            );
        }

        return stock;
    }
}
