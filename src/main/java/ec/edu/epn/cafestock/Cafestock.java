package ec.edu.epn.cafestock;

import ec.edu.epn.cafestock.modelo.Producto;
import ec.edu.epn.cafestock.persistencia.GestorInventarioArchivo;
import ec.edu.epn.cafestock.persistencia.GestorInventarioArchivo.PersistenciaInventarioException;
import ec.edu.epn.cafestock.servicio.ServicioProductos;
import ec.edu.epn.cafestock.vista.PanelProductos;
import ec.edu.epn.cafestock.vista.PanelRegistroProducto;
import ec.edu.epn.cafestock.vista.VentanaPrincipal;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Cafestock {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Cafestock::iniciarAplicacion);
    }

    private static void iniciarAplicacion() {
        GestorInventarioArchivo repositorio =
                new GestorInventarioArchivo();

        ServicioProductos servicio =
                new ServicioProductos(repositorio);

        PanelProductos panelProductos = new PanelProductos();

        try {
            panelProductos.mostrarProductos(
                    servicio.listarProductos()
            );
        } catch (PersistenciaInventarioException error) {
            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo cargar el inventario.\n"
                            + error.getMessage(),
                    "Error de apertura",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        PanelRegistroProducto panelRegistro =
                new PanelRegistroProducto(producto -> {

                    List<Producto> productosActualizados;

                    try {
                        productosActualizados =
                                servicio.registrarProducto(producto);

                    } catch (PersistenciaInventarioException error) {
                        throw new IllegalStateException(
                                "No se pudo guardar el producto.\n"
                                        + error.getMessage(),
                                error
                        );
                    }

                    panelProductos.mostrarProductos(
                            productosActualizados
                    );
                });

        VentanaPrincipal ventana = new VentanaPrincipal(
                panelRegistro,
                panelProductos
        );

        ventana.setVisible(true);
    }
}