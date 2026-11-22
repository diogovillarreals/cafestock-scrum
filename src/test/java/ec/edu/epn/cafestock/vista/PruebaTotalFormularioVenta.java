package ec.edu.epn.cafestock.vista;

import ec.edu.epn.cafestock.modelo.Producto;
import ec.edu.epn.cafestock.persistencia.RepositorioProductos;
import ec.edu.epn.cafestock.servicio.ServicioProductos;
import java.awt.Component;
import java.awt.Container;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class PruebaTotalFormularioVenta {

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(PruebaTotalFormularioVenta::verificar);
    }

    private static void verificar() {
        String sinTotal = "Total: —";
        Producto cafe = new Producto("CAF-01", "Café", new BigDecimal("1.50"), 10, 2);
        Producto te = new Producto("TE-01", "Té", new BigDecimal("0.1"), 3, 1);
        RepositorioProductos repositorio = new RepositorioProductos() {
            @Override
            public List<Producto> cargar() {
                return List.of(cafe, te);
            }

            @Override
            public void guardar(List<Producto> productos) {
                throw new AssertionError("La vista previa no debe escribir el inventario.");
            }
        };
        PanelRegistroVenta formulario = new PanelRegistroVenta(
                new ServicioProductos(repositorio),
                venta -> {
                    throw new AssertionError("La vista previa no debe registrar la venta.");
                });

        JComboBox<?> selector = buscar(formulario, JComboBox.class, combo -> true).orElseThrow();
        JTextField cantidad = buscar(formulario, JTextField.class,
                campo -> campo.getClass() == JTextField.class).orElseThrow();
        JLabel total = buscar(formulario, JLabel.class,
                etiqueta -> sinTotal.equals(etiqueta.getText())).orElseThrow();
        JButton limpiar = buscar(formulario, JButton.class,
                boton -> "Limpiar".equals(boton.getText())).orElseThrow();

        selector.setSelectedIndex(1);
        comprobarTotal(total, sinTotal);
        cantidad.setText("3");
        comprobarTotal(total, "Total: USD 4,50");
        cantidad.setText("2");
        comprobarTotal(total, "Total: USD 3,00");
        selector.setSelectedIndex(2);
        comprobarTotal(total, "Total: USD 0,20");

        for (String valor : List.of("", "0", "-1", "abc", "1,5", "4")) {
            cantidad.setText("3");
            comprobarTotal(total, "Total: USD 0,30");
            cantidad.setText(valor);
            comprobarTotal(total, sinTotal);
        }

        cantidad.setText("2");
        selector.setSelectedIndex(0);
        comprobarTotal(total, sinTotal);
        selector.setSelectedIndex(1);
        comprobarTotal(total, "Total: USD 3,00");
        formulario.refrescarProductos();
        comprobarTotal(total, sinTotal);
        selector.setSelectedIndex(1);
        comprobarTotal(total, "Total: USD 3,00");
        limpiar.doClick();
        comprobarTotal(total, sinTotal);
        System.out.println("HU-A3: pruebas del total en el formulario de venta superadas.");
    }

    private static void comprobarTotal(JLabel total, String esperado) {
        if (!esperado.equals(total.getText())) {
            throw new AssertionError("Esperado: " + esperado + " | Obtenido: " + total.getText());
        }
    }

    private static <T extends Component> Optional<T> buscar(
            Container contenedor, Class<T> tipo, Predicate<T> filtro) {
        for (Component componente : contenedor.getComponents()) {
            if (tipo.isInstance(componente) && filtro.test(tipo.cast(componente))) {
                return Optional.of(tipo.cast(componente));
            }
            if (componente instanceof Container hijo) {
                Optional<T> encontrado = buscar(hijo, tipo, filtro);
                if (encontrado.isPresent()) {
                    return encontrado;
                }
            }
        }
        return Optional.empty();
    }
}
