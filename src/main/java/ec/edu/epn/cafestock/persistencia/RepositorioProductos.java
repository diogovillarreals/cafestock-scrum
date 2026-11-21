package ec.edu.epn.cafestock.persistencia;

import ec.edu.epn.cafestock.modelo.Producto;
import java.util.List;

public interface RepositorioProductos {

    List<Producto> cargar();

    void guardar(List<Producto> productos);
}