/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.epn.cafestock.negocio;

import ec.edu.epn.cafestock.modelo.Producto;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author pablo.vizuete
 */
public class BuscadorProductos {

    private BuscadorProductos() {

    }

    public static List<Producto> buscar(List<Producto> productos, String criterio) {
        List<Producto> resultados = new ArrayList<>();

        if (productos == null || productos.isEmpty()) {
            return resultados;
        }

        if (criterio == null || criterio.isBlank()) {
            for (Producto producto : productos) {
                if (producto != null) {
                    resultados.add(producto);
                }
            }
            return resultados;
        }

        String textoBuscado = normalizar(criterio);

        for (Producto producto : productos) {
            if (producto == null) {
                continue;
            }

            String codigo = normalizar(producto.getCodigo());
            String nombre = normalizar(producto.getNombre());

            if (codigo.contains(textoBuscado) || nombre.contains(textoBuscado)) {
                resultados.add(producto);
            }
        }

        return resultados;
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinEspacios = texto.strip().toLowerCase(Locale.ROOT);
        String descompuesto = Normalizer.normalize(sinEspacios, Normalizer.Form.NFD);
        return descompuesto.replaceAll("\\p{M}", "");
    }
}
