package ec.edu.epn.cafestock.vista;

import ec.edu.epn.cafestock.persistencia.GestorInventarioArchivo;
import ec.edu.epn.cafestock.servicio.RegistroProductos;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PruebaPanelRegistroDuplicados {

    public static void main(String[] args) throws IOException {
        Path directorio = Files.createTempDirectory("cafestock-hu-a1-swing-");
        Path inventario = directorio.resolve("inventario.tsv");
        directorio.toFile().deleteOnExit();
        inventario.toFile().deleteOnExit();
        directorio.resolve("inventario.tsv.tmp").toFile().deleteOnExit();
        RegistroProductos registro = new RegistroProductos(new GestorInventarioArchivo(inventario));
        SwingUtilities.invokeLater(() -> mostrarFormulario(registro));
    }

    private static void mostrarFormulario(RegistroProductos registro) {
        String[] columnas = {"Código", "Nombre", "Precio", "Stock actual", "Stock mínimo"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modelo);
        tabla.setDefaultEditor(Object.class, null);
        JScrollPane tablaPrueba = new JScrollPane(tabla);
        tablaPrueba.setPreferredSize(new Dimension(600, 130));

        PanelRegistroProducto panel = new PanelRegistroProducto(producto -> {
            registro.registrar(producto);
            modelo.addRow(new Object[]{producto.getCodigo(), producto.getNombre(), producto.getPrecio(),
                producto.getStockActual(), producto.getStockMinimo()});
        });

        JFrame ventana = new JFrame("HU-A1: prueba de códigos duplicados");
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.add(panel, BorderLayout.CENTER);
        ventana.add(tablaPrueba, BorderLayout.SOUTH);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
