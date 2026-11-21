package ec.edu.epn.cafestock.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal(
            PanelRegistroProducto panelRegistro,
            PanelProductos panelProductos) {

        super("CaféStock - Inventario");

        Color crema = new Color(250, 248, 245);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Mantiene el formulario arriba, sin estirarlo verticalmente.
        JPanel contenedorFormulario = new JPanel(new BorderLayout());
        contenedorFormulario.setBackground(crema);
        contenedorFormulario.add(panelRegistro, BorderLayout.NORTH);

        JScrollPane scrollFormulario =
                new JScrollPane(contenedorFormulario);

        scrollFormulario.setBorder(null);
        scrollFormulario.getViewport().setBackground(crema);
        scrollFormulario.getVerticalScrollBar().setUnitIncrement(16);

        JSplitPane division = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                scrollFormulario,
                panelProductos
        );

        division.setBorder(null);
        division.setBackground(crema);
        division.setDividerSize(8);
        division.setContinuousLayout(true);

        // El formulario conserva su ancho y la tabla recibe
        // el espacio adicional al ampliar la ventana.
        division.setResizeWeight(0.0);
        division.setDividerLocation(
                panelRegistro.getPreferredSize().width + 20
        );

        add(division, BorderLayout.CENTER);

        setSize(1200, 700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}