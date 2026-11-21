package ec.edu.epn.cafestock.servicio;

import ec.edu.epn.cafestock.modelo.Producto;
import ec.edu.epn.cafestock.persistencia.RepositorioProductos;
import java.util.List;
import java.util.Objects;
import ec.edu.epn.cafestock.validacion.ValidarCampos;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ServicioProductos {

    private final RepositorioProductos repositorio;
    private final RegistroProductos registro;

    public ServicioProductos(RepositorioProductos repositorio) {
        this.repositorio = Objects.requireNonNull(
                repositorio,
                "El repositorio no puede ser nulo."
        );

        this.registro = new RegistroProductos(this.repositorio);
    }

    public List<Producto> listarProductos() {
        return List.copyOf(repositorio.cargar());
    }

    public List<Producto> registrarProducto(Producto producto) {
        Objects.requireNonNull(
                producto,
                "El producto no puede ser nulo."
        );

        return registro.registrar(producto);
    }
    public List<Producto> actualizarProducto(Producto editado) {
    if (editado == null) {
        throw new IllegalArgumentException(
                "Selecciona un producto para editar."
        );
    }

    String codigo = ValidarCampos.validarObligatorio(
            editado.getCodigo(), "Código"
    );

    String nombre = ValidarCampos.validarObligatorio(
            editado.getNombre(), "Nombre"
    );

    BigDecimal precio = ValidarCampos.validarPrecio(
            editado.getPrecio() == null
                    ? null
                    : editado.getPrecio().toPlainString()
    );

    int stockActual = ValidarCampos.validarStock(
            Integer.toString(editado.getStockActual()),
            "Stock actual"
    );

    int stockMinimo = ValidarCampos.validarStock(
            Integer.toString(editado.getStockMinimo()),
            "Stock mínimo"
    );

    List<Producto> productos =
            new ArrayList<>(repositorio.cargar());

    int posicion = -1;

    for (int i = 0; i < productos.size(); i++) {
        String codigoGuardado =
                productos.get(i).getCodigo().strip();

        if (codigoGuardado.equalsIgnoreCase(codigo)) {
            posicion = i;
            break;
        }
    }

    if (posicion == -1) {
        throw new IllegalArgumentException(
                "El producto ya no existe en el inventario."
        );
    }

    Producto original = productos.get(posicion);

    Producto actualizado = new Producto(
            original.getCodigo(),
            nombre,
            precio,
            stockActual,
            stockMinimo
    );

    productos.set(posicion, actualizado);

    repositorio.guardar(productos);

    return List.copyOf(productos);
}
}