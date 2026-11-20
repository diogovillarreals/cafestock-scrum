package ec.edu.epn.cafestock.modelo;

import java.math.BigDecimal;

public class Producto {

    private final String codigo;
    private final String nombre;
    private final BigDecimal precio;
    private final int stockActual;
    private final int stockMinimo;

    public Producto(String codigo, String nombre, BigDecimal precio,
                    int stockActual, int stockMinimo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
    }
    public String getCodigo() {
    return codigo;
}

public String getNombre() {
    return nombre;
}

public BigDecimal getPrecio() {
    return precio;
}

public int getStockActual() {
    return stockActual;
}

public int getStockMinimo() {
    return stockMinimo;
}
}