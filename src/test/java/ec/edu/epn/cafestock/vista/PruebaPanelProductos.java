package ec.edu.epn.cafestock.vista;

import ec.edu.epn.cafestock.modelo.Producto;
import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class PruebaPanelProductos {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Producto cafe = new Producto(
                    "CAF-01", "Café americano",
                    new BigDecimal("1.50"), 10, 5
            );

            Producto sandwich = new Producto(
                    "SAN-01", "Sándwich",
                    new BigDecimal("2.75"), 8, 3
            );

            Producto jugo = new Producto(
                    "JUG-01", "Jugo de naranja",
                    new BigDecimal("1.25"), 0, 2
            );

            PanelProductos panel = new PanelProductos();
            panel.mostrarProductos(List.of(cafe, sandwich));

            JButton botonActualizar = new JButton(
                    "Probar actualización con 3 productos"
            );

            botonActualizar.addActionListener(evento -> {
                panel.mostrarProductos(List.of(cafe, sandwich, jugo));
            });

            JFrame ventana = new JFrame("Prueba de la lista de productos");
            ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ventana.add(panel, BorderLayout.CENTER);
            ventana.add(botonActualizar, BorderLayout.SOUTH);
            ventana.setSize(800, 450);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}