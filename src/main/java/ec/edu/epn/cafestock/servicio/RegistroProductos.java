package ec.edu.epn.cafestock.servicio;

import ec.edu.epn.cafestock.modelo.Producto;
import ec.edu.epn.cafestock.persistencia.GestorInventarioArchivo;
import ec.edu.epn.cafestock.validacion.ValidarCampos;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RegistroProductos {

    private final GestorInventarioArchivo gestorInventario;

    public RegistroProductos(GestorInventarioArchivo gestorInventario) {
        this.gestorInventario = Objects.requireNonNull(gestorInventario);
    }

    public synchronized void registrar(Producto producto) {
        String codigo = ValidarCampos.validarObligatorio(producto.getCodigo(), "Código");
        String codigoNormalizado = codigo.toUpperCase(Locale.ROOT);
        List<Producto> productos = new ArrayList<>(gestorInventario.cargar());

        for (Producto registrado : productos) {
            String codigoRegistrado = registrado.getCodigo().strip().toUpperCase(Locale.ROOT);
            if (codigoNormalizado.equals(codigoRegistrado)) {
                throw new IllegalArgumentException("El código " + codigo + " ya existe.");
            }
        }

        productos.add(producto);
        gestorInventario.guardar(productos);
    }
}
