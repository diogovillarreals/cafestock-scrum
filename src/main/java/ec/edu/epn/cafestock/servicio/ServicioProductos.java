package ec.edu.epn.cafestock.servicio;

import ec.edu.epn.cafestock.modelo.Producto;
import ec.edu.epn.cafestock.persistencia.RepositorioProductos;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServicioProductos {

    private final RepositorioProductos repositorio;

    public ServicioProductos(RepositorioProductos repositorio) {
        this.repositorio = Objects.requireNonNull(
                repositorio,
                "El repositorio no puede ser nulo."
        );
    }

    public List<Producto> listarProductos() {
        return List.copyOf(repositorio.cargar());
    }

    public List<Producto> registrarProducto(Producto producto) {
        Objects.requireNonNull(
                producto,
                "El producto no puede ser nulo."
        );

        List<Producto> productos =
                new ArrayList<>(repositorio.cargar());

        productos.add(producto);

        repositorio.guardar(productos);

        return List.copyOf(productos);
    }
}