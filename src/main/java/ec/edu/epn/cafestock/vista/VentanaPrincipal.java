package ec.edu.epn.cafestock.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal(
            PanelRegistroProducto panelRegistro,
            PanelRegistroVenta panelRegistroVenta,
            PanelProductos panelProductos) {

        super("CaféStock - Inventario");

        Color crema = new Color(250, 248, 245);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Crear botones para seleccionar los formularios
        JButton btnRegistrarProducto = new JButton("Registrar producto");
        JButton btnRegistrarVenta = new JButton("Registrar venta");

        // Panel que contiene los dos botones
        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        panelBotones.add(btnRegistrarProducto);
        panelBotones.add(btnRegistrarVenta);

        // Contenedor para alternar entre formularios
        CardLayout cardLayout = new CardLayout();
        JPanel panelFormularios = new JPanel(cardLayout);

        // Contenedor del formulario de productos
        JPanel contenedorProducto = new JPanel(new BorderLayout());
        contenedorProducto.setBackground(crema);
        contenedorProducto.add(panelRegistro, BorderLayout.NORTH);

        // Contenedor del formulario de ventas
        JPanel contenedorVenta = new JPanel(new BorderLayout());
        contenedorVenta.setBackground(crema);
        contenedorVenta.add(panelRegistroVenta, BorderLayout.NORTH);

        // Agregar los contenedores al CardLayout
        panelFormularios.add(contenedorProducto, "PRODUCTO");
        panelFormularios.add(contenedorVenta, "VENTA");
        // Acciones de los botones
        btnRegistrarProducto.addActionListener(e -> {
            cardLayout.show(panelFormularios, "PRODUCTO");
        });

        btnRegistrarVenta.addActionListener(e -> {
            cardLayout.show(panelFormularios, "VENTA");
            panelRegistroVenta.refrescarProductos();
        });
        
        // Mantiene el formulario arriba, sin estirarlo verticalmente.
        JPanel contenedorFormulario = new JPanel(new BorderLayout());
        contenedorFormulario.setBackground(crema);

        contenedorFormulario.add(panelBotones, BorderLayout.NORTH);
        contenedorFormulario.add(panelFormularios, BorderLayout.CENTER);

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
        
        int anchoFormulario = Math.max(
                panelRegistro.getPreferredSize().width,
                panelRegistroVenta.getPreferredSize().width
        );

        division.setDividerLocation(anchoFormulario + 20);

        add(division, BorderLayout.CENTER);

        setSize(1200, 700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}