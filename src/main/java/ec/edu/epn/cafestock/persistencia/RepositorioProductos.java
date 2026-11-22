package ec.edu.epn.cafestock.persistencia;

import ec.edu.epn.cafestock.modelo.Producto;
import ec.edu.epn.cafestock.modelo.Venta;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

public interface RepositorioProductos {

    List<Producto> cargar();

    void guardar(List<Producto> productos);

    default void guardarVenta(List<Producto> productosActualizados, Venta venta, BigDecimal total) {
        throw new UnsupportedOperationException("El repositorio no admite el registro de ventas.");
    }

    default <T> T conBloqueoDeEscritura(Supplier<T> operacion) {
        synchronized (this) {
            return operacion.get();
        }
    }
}
