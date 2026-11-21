package ec.edu.epn.cafestock.vista;

import ec.edu.epn.cafestock.modelo.Producto;
import ec.edu.epn.cafestock.validacion.ValidarEdicionProducto;
import java.awt.Component;
import java.util.Optional;
import java.util.function.Consumer;
import javax.swing.JOptionPane;

public final class ValidacionFormularioEdicion {

    private ValidacionFormularioEdicion() {
    }
    
    public static Optional<Producto> validar(Component formulario, Producto original,
                                             String nombre, String precio,
                                             String stockActual, String stockMinimo) {
        return validar(original, nombre, precio, stockActual, stockMinimo, mensaje ->
                JOptionPane.showMessageDialog(formulario, mensaje, "Datos inválidos",
                        JOptionPane.WARNING_MESSAGE));
    }

    static Optional<Producto> validar(Producto original, String nombre, String precio,
                                      String stockActual, String stockMinimo,
                                      Consumer<String> mostrarError) {
        try {
            return Optional.of(ValidarEdicionProducto.validar(original, nombre, precio,
                    stockActual, stockMinimo));
        } catch (IllegalArgumentException error) {
            mostrarError.accept(error.getMessage());
            return Optional.empty();
        }
    }
}
