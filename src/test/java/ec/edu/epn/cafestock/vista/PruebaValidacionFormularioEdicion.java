package ec.edu.epn.cafestock.vista;

import ec.edu.epn.cafestock.modelo.Producto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PruebaValidacionFormularioEdicion {

    public static void main(String[] args) {
        Producto original = new Producto("CAF-01", "Café", new BigDecimal("1.25"), 10, 3);
        List<Producto> inventario = new ArrayList<>(List.of(original));
        List<String> mensajes = new ArrayList<>();

        String[][] casosInvalidos = {
            {"   ", "2,50", "20", "5", "Nombre"},
            {"Café grande", "", "20", "5", "Precio"},
            {"Café grande", "abc", "20", "5", "precio"},
            {"Café grande", "0", "20", "5", "precio"},
            {"Café grande", "-1", "20", "5", "precio"},
            {"Café grande", "2,50", "", "5", "Stock actual"},
            {"Café grande", "2,50", "abc", "5", "Stock actual"},
            {"Café grande", "2,50", "-1", "5", "Stock actual"},
            {"Café grande", "2,50", "1,5", "5", "Stock actual"},
            {"Café grande", "2,50", "2147483648", "5", "Stock actual"},
            {"Café grande", "2,50", "20", "", "Stock mínimo"},
            {"Café grande", "2,50", "20", "-1", "Stock mínimo"},
            {"Café grande", "2,50", "20", "1.5", "Stock mínimo"}
        };

        for (String[] caso : casosInvalidos) {
            mensajes.clear();
            ValidacionFormularioEdicion.validar(original, caso[0], caso[1], caso[2], caso[3],
                    mensajes::add).ifPresent(editar -> inventario.set(0, editar));
            comprobar(mensajes.size() == 1 && mensajes.get(0).contains(caso[4]),
                    "Se esperaba un aviso para " + caso[4] + ".");
            comprobar(inventario.get(0) == original,
                    "Una edición inválida debe conservar el producto anterior.");
        }

        mensajes.clear();
        ValidacionFormularioEdicion.validar(original, "  Café grande  ", "2,50", "0", "0",
                mensajes::add).ifPresent(editar -> inventario.set(0, editar));
        Producto editado = inventario.get(0);
        comprobar(mensajes.isEmpty(), "Una edición válida no debe mostrar avisos.");
        comprobar(editado.getCodigo().equals("CAF-01") && editado.getNombre().equals("Café grande")
                && editado.getPrecio().equals(new BigDecimal("2.50"))
                && editado.getStockActual() == 0 && editado.getStockMinimo() == 0,
                "La edición debe conservar el código y aceptar precio decimal y stock cero.");

        Producto limite = ValidacionFormularioEdicion.validar(editado, "Café grande", "0.01",
                "2147483647", "2147483647", mensajes::add).orElseThrow();
        comprobar(limite.getStockActual() == Integer.MAX_VALUE
                && limite.getStockMinimo() == Integer.MAX_VALUE,
                "Los dos campos de stock deben aceptar el mayor entero representable.");
        System.out.println("HU-A2: " + (casosInvalidos.length + 2) + " casos superados.");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}
