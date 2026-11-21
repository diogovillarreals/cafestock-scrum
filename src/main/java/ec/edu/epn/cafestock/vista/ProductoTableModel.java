package ec.edu.epn.cafestock.vista;

import ec.edu.epn.cafestock.modelo.Producto;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class ProductoTableModel extends AbstractTableModel {

    private final String[] columnas = {
        "Código",
        "Nombre",
        "Precio (USD)",
        "Stock actual",
        "Stock mínimo"
    };

    private List<Producto> productos = List.of();

    public Producto obtenerProductoEn(int fila) {
    if (fila < 0 || fila >= productos.size()) {
        return null;
    }
    return productos.get(fila);
}
    
    @Override
    public int getRowCount() {
        return productos.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int columna) {
        return columnas[columna];
    }

    @Override
    public Class<?> getColumnClass(int columna) {
        return switch (columna) {
            case 0, 1 -> String.class;
            case 2 -> BigDecimal.class;
            case 3, 4 -> Integer.class;
            default -> throw new IndexOutOfBoundsException(
                    "Columna inexistente: " + columna
            );
        };
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Producto producto = productos.get(fila);

        return switch (columna) {
            case 0 -> producto.getCodigo();
            case 1 -> producto.getNombre();
            case 2 -> producto.getPrecio();
            case 3 -> producto.getStockActual();
            case 4 -> producto.getStockMinimo();
            default -> throw new IndexOutOfBoundsException(
                    "Columna inexistente: " + columna
            );
        };
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }

    public void actualizarProductos(List<Producto> nuevosProductos) {
        productos = List.copyOf(nuevosProductos);
        fireTableDataChanged();
    }
}