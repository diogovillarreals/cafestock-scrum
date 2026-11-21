/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.epn.cafestock.vista;

import ec.edu.epn.cafestock.modelo.Producto;
import ec.edu.epn.cafestock.validacion.ValidarCampos;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
/**
 *
 * @author Dell G15
 */
public class PruebaDialogoEditarProducto {

    public static void main(String[] args) {
        probarProductoTableModel();
        probarValidacionYConstruccionProductoEditado();
        probarCallbackAlGuardarEnPanelProductos();
        System.out.println("Todas las pruebas de la Etapa 4 pasaron correctamente.");
    }

    private static void probarProductoTableModel() {
        Producto p1 = new Producto("CAF-01", "Café Molido", new BigDecimal("4.50"), 20, 5);
        Producto p2 = new Producto("TE-02", "Té Verde", new BigDecimal("3.00"), 10, 3);

        ProductoTableModel modelo = new ProductoTableModel();
        modelo.actualizarProductos(List.of(p1, p2));

        verificar(modelo.getRowCount() == 2, "Debe haber 2 filas en el modelo.");
        verificar("CAF-01".equals(modelo.obtenerProductoEn(0).getCodigo()), "Fila 0 debe ser CAF-01.");
        verificar("TE-02".equals(modelo.obtenerProductoEn(1).getCodigo()), "Fila 1 debe ser TE-02.");
        verificar(modelo.obtenerProductoEn(-1) == null, "Índice negativo debe retornar null.");
        verificar(modelo.obtenerProductoEn(2) == null, "Índice fuera de rango debe retornar null.");
    }

    private static void probarValidacionYConstruccionProductoEditado() {
        Producto original = new Producto("CAF-01", "Café Original", new BigDecimal("2.50"), 15, 5);

        String nombreValidado = ValidarCampos.validarObligatorio("Café Especial", "Nombre");
        BigDecimal precioValidado = ValidarCampos.validarPrecio("3.75");
        int stockActualValidado = ValidarCampos.validarStock("25", "Stock actual");
        int stockMinimoValidado = ValidarCampos.validarStock("6", "Stock mínimo");

        Producto editado = new Producto(
                original.getCodigo(),
                nombreValidado,
                precioValidado,
                stockActualValidado,
                stockMinimoValidado
        );

        verificar("CAF-01".equals(editado.getCodigo()), "El código del producto debe conservarse.");
        verificar("Café Especial".equals(editado.getNombre()), "El nombre debe actualizarse.");
        verificar(new BigDecimal("3.75").compareTo(editado.getPrecio()) == 0, "El precio debe ser 3.75.");
        verificar(editado.getStockActual() == 25, "El stock actual debe ser 25.");
        verificar(editado.getStockMinimo() == 6, "El stock mínimo debe ser 6.");

        boolean lanzoErrorPrecio = false;
        try {
            ValidarCampos.validarPrecio("-1.00");
        } catch (IllegalArgumentException ex) {
            lanzoErrorPrecio = true;
        }
        verificar(lanzoErrorPrecio, "Precio negativo debe lanzar IllegalArgumentException.");
    }

    private static void probarCallbackAlGuardarEnPanelProductos() {
        Producto p1 = new Producto("CAF-01", "Café Molido", new BigDecimal("4.50"), 20, 5);
        Producto p2 = new Producto("PAN-03", "Croissant", new BigDecimal("1.75"), 12, 4);

        PanelProductos panel = new PanelProductos();
        panel.mostrarProductos(List.of(p1, p2));

        AtomicReference<Producto> recibido = new AtomicReference<>();
        java.util.function.Consumer<Producto> callback = recibido::set;
        panel.setAlGuardarEdicion(callback);

        Producto editado = new Producto("CAF-01", "Café Molido Premium", new BigDecimal("5.00"), 18, 5);
        callback.accept(editado);

        verificar(recibido.get() != null, "El callback alGuardarEdicion debe recibir el producto editado.");
        verificar("Café Molido Premium".equals(recibido.get().getNombre()), "Debe conservar el nuevo nombre.");
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new IllegalStateException("Fallo en prueba: " + mensaje);
        }
    }
}
