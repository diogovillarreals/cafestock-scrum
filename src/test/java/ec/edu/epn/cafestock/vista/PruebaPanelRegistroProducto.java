/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.epn.cafestock.vista;
import ec.edu.epn.cafestock.modelo.Producto;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
/**
 *
 * @author pablo.vizuete
 */
public class PruebaPanelRegistroProducto {
   public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            Set<String> codigosRegistrados = new HashSet<>();

            PanelRegistroProducto panel = new PanelRegistroProducto((Producto producto) -> {
                String codigoNormalizado = producto.getCodigo().toUpperCase();
                if (!codigosRegistrados.add(codigoNormalizado)) {
                    throw new IllegalArgumentException(
                            "El código " + producto.getCodigo() + " ya existe."
                    );
                }

                System.out.println("Producto validado y enviado:");
                System.out.println("  Código: " + producto.getCodigo());
                System.out.println("  Nombre: " + producto.getNombre());
                System.out.println("  Precio: $" + producto.getPrecio());
                System.out.println("  Stock actual: " + producto.getStockActual());
                System.out.println("  Stock mínimo: " + producto.getStockMinimo());
                System.out.println("-----------------------------------");
            });

            JFrame ventana = new JFrame("Prueba - Registro de Productos");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setContentPane(panel);
            ventana.pack();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}