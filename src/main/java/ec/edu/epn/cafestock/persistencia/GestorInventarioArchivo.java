/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.epn.cafestock.persistencia;

import ec.edu.epn.cafestock.modelo.Producto;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Dell G15
 */
public class GestorInventarioArchivo {

    private static final String SEPARADOR = "\t";
    private static final int CANTIDAD_CAMPOS = 5;
    private static final Path RUTA_POR_DEFECTO = Paths.get("data", "inventario.tsv");

    private final Path rutaArchivo;

    public GestorInventarioArchivo() {
        this(RUTA_POR_DEFECTO);
    }

    public GestorInventarioArchivo(Path rutaArchivo) {
        this.rutaArchivo = Objects.requireNonNull(rutaArchivo, "La ruta del archivo no puede ser nula.");
    }

    public Path getRutaArchivo() {
        return rutaArchivo;
    }

    public void guardar(List<Producto> productos) {
        Objects.requireNonNull(productos, "La lista de productos no puede ser nula.");

        for (Producto producto : productos) {
            if (producto == null) {
                throw new PersistenciaInventarioException("No se puede guardar un producto nulo en el inventario.");
            }
        }

        try {
            Path directorioPadre = rutaArchivo.getParent();
            if (directorioPadre != null) {
                Files.createDirectories(directorioPadre);
            }

            Path archivoTemporal = rutaArchivo.resolveSibling(rutaArchivo.getFileName().toString() + ".tmp");

            try (BufferedWriter writer = Files.newBufferedWriter(archivoTemporal, StandardCharsets.UTF_8)) {
                for (Producto producto : productos) {
                    String linea = formatearProducto(producto);
                    writer.write(linea);
                    writer.newLine();
                }
            }

            Files.move(
                    archivoTemporal,
                    rutaArchivo,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (IOException e) {
            throw new PersistenciaInventarioException("Error al guardar el inventario en: " + rutaArchivo, e);
        }
    }

    public List<Producto> cargar() {
        if (Files.notExists(rutaArchivo)) {
            return new ArrayList<>();
        }

        List<Producto> productos = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(rutaArchivo, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;

            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                if (linea.isEmpty()) {
                    continue;
                }
                productos.add(parsearProducto(linea, numeroLinea));
            }

            return productos;
        } catch (IOException e) {
            throw new PersistenciaInventarioException("Error al cargar el inventario desde: " + rutaArchivo, e);
        }
    }

    private String formatearProducto(Producto producto) {
        String codigo = escaparCampo(producto.getCodigo());
        String nombre = escaparCampo(producto.getNombre());
        String precio = producto.getPrecio() != null ? producto.getPrecio().toPlainString() : "";
        String stockActual = Integer.toString(producto.getStockActual());
        String stockMinimo = Integer.toString(producto.getStockMinimo());

        return String.join(SEPARADOR, codigo, nombre, precio, stockActual, stockMinimo);
    }

    private Producto parsearProducto(String linea, int numeroLinea) {
        String[] partes = linea.split(SEPARADOR, -1);
        if (partes.length != CANTIDAD_CAMPOS) {
            throw new PersistenciaInventarioException(
                    "Formato inválido en la línea " + numeroLinea + ": se esperaban " + CANTIDAD_CAMPOS + " campos."
            );
        }

        try {
            String codigo = desescaparCampo(partes[0]);
            String nombre = desescaparCampo(partes[1]);
            if (partes[2].isBlank()) {
                throw new PersistenciaInventarioException("Precio vacío en la línea " + numeroLinea + ".");
            }
            BigDecimal precio = new BigDecimal(partes[2]);
            int stockActual = Integer.parseInt(partes[3]);
            int stockMinimo = Integer.parseInt(partes[4]);

            return new Producto(codigo, nombre, precio, stockActual, stockMinimo);
        } catch (NumberFormatException e) {
            throw new PersistenciaInventarioException(
                    "Valor numérico inválido en la línea " + numeroLinea + ".", e
            );
        }
    }

    private String escaparCampo(String valor) {
        if (valor == null) {
            return "";
        }
        return valor
                .replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    private String desescaparCampo(String valor) {
        StringBuilder sb = new StringBuilder(valor.length());
        boolean escapado = false;

        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            if (escapado) {
                switch (c) {
                    case '\\' -> sb.append('\\');
                    case 't' -> sb.append('\t');
                    case 'r' -> sb.append('\r');
                    case 'n' -> sb.append('\n');
                    default -> sb.append(c);
                }
                escapado = false;
            } else if (c == '\\') {
                escapado = true;
            } else {
                sb.append(c);
            }
        }

        if (escapado) {
            throw new PersistenciaInventarioException("Secuencia de escape incompleta en el archivo de inventario.");
        }

        return sb.toString();
    }

    public static class PersistenciaInventarioException extends RuntimeException {
        public PersistenciaInventarioException(String mensaje) {
            super(mensaje);
        }

        public PersistenciaInventarioException(String mensaje, Throwable causa) {
            super(mensaje, causa);
        }
    }
}
