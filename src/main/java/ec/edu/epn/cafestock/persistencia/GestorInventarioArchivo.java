/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.epn.cafestock.persistencia;

import ec.edu.epn.cafestock.modelo.Producto;
import ec.edu.epn.cafestock.validacion.ValidarCampos;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
/**
 *
 * @author Dell G15
 */
public class GestorInventarioArchivo implements RepositorioProductos {

    private static final char SEPARADOR = ',';
    private static final char COMILLA = '"';
    private static final int CANTIDAD_CAMPOS = 5;
    private static final Path RUTA_POR_DEFECTO = Paths.get("data", "productos.csv");

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

    @Override
    public void guardar(List<Producto> productos) {
        Objects.requireNonNull(productos, "La lista de productos no puede ser nula.");

        List<String> lineasCsv = new ArrayList<>(productos.size());
        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            validarProductoParaGuardar(producto, i + 1);
            lineasCsv.add(formatearProductoCsv(producto));
        }

        Path archivoTemporal = null;
        try {
            Path directorioPadre = rutaArchivo.getParent();
            if (directorioPadre != null) {
                Files.createDirectories(directorioPadre);
            }

            String nombreBase = rutaArchivo.getFileName() != null
                    ? rutaArchivo.getFileName().toString()
                    : "productos.csv";
            archivoTemporal = rutaArchivo.resolveSibling(nombreBase + "." + UUID.randomUUID() + ".tmp");

            try (BufferedWriter writer = Files.newBufferedWriter(archivoTemporal, StandardCharsets.UTF_8)) {
                for (String linea : lineasCsv) {
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
        } catch (IOException | RuntimeException e) {
            limpiarTemporalSinOcultarError(archivoTemporal, e);
            if (e instanceof PersistenciaInventarioException pie) {
                throw pie;
            }
            throw new PersistenciaInventarioException(
                    "Error al guardar el inventario en '" + rutaArchivo + "': " + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public List<Producto> cargar() {
        if (Files.notExists(rutaArchivo)) {
            return new ArrayList<>();
        }

        try (BufferedReader reader = Files.newBufferedReader(rutaArchivo, StandardCharsets.UTF_8)) {
            LectorCsv lector = new LectorCsv(reader, rutaArchivo);
            List<RegistroCsv> registros = lector.leerTodosLosRegistros();
            List<Producto> productos = new ArrayList<>(registros.size());

            for (RegistroCsv registro : registros) {
                productos.add(convertirRegistroAProducto(registro));
            }

            return productos;
        } catch (IOException e) {
            throw new PersistenciaInventarioException(
                    "Error al cargar el inventario desde '" + rutaArchivo + "': " + e.getMessage(),
                    e
            );
        }
    }

    private void validarProductoParaGuardar(Producto producto, int posicion) {
        if (producto == null) {
            throw new PersistenciaInventarioException(
                    "No se puede guardar un producto nulo en '" + rutaArchivo + "' (elemento #" + posicion + ")."
            );
        }

        try {
            ValidarCampos.validarObligatorio(producto.getCodigo(), "Código");
            ValidarCampos.validarObligatorio(producto.getNombre(), "Nombre");
        } catch (IllegalArgumentException ex) {
            throw new PersistenciaInventarioException(
                    "Producto inválido en '" + rutaArchivo + "' (elemento #" + posicion + "): " + ex.getMessage(),
                    ex
            );
        }

        BigDecimal precio = producto.getPrecio();
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PersistenciaInventarioException(
                    "Precio inválido en '" + rutaArchivo + "' (elemento #" + posicion
                    + "): debe ser un número mayor que cero."
            );
        }

        if (producto.getStockActual() < 0) {
            throw new PersistenciaInventarioException(
                    "Stock actual inválido en '" + rutaArchivo + "' (elemento #" + posicion
                    + "): debe ser mayor o igual que cero."
            );
        }

        if (producto.getStockMinimo() < 0) {
            throw new PersistenciaInventarioException(
                    "Stock mínimo inválido en '" + rutaArchivo + "' (elemento #" + posicion
                    + "): debe ser mayor o igual que cero."
            );
        }
    }

    private String formatearProductoCsv(Producto producto) {
        String codigo = escaparCampoCsv(producto.getCodigo().strip());
        String nombre = escaparCampoCsv(producto.getNombre().strip());
        String precio = escaparCampoCsv(producto.getPrecio().toPlainString());
        String stockActual = escaparCampoCsv(Integer.toString(producto.getStockActual()));
        String stockMinimo = escaparCampoCsv(Integer.toString(producto.getStockMinimo()));

        return codigo + SEPARADOR
                + nombre + SEPARADOR
                + precio + SEPARADOR
                + stockActual + SEPARADOR
                + stockMinimo;
    }

    private String escaparCampoCsv(String valor) {
        String texto = valor != null ? valor : "";
        boolean requiereComillas = false;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == SEPARADOR || c == COMILLA || c == '\n' || c == '\r') {
                requiereComillas = true;
                break;
            }
        }

        if (!requiereComillas) {
            return texto;
        }

        StringBuilder sb = new StringBuilder(texto.length() + 4);
        sb.append(COMILLA);
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == COMILLA) {
                sb.append(COMILLA).append(COMILLA);
            } else {
                sb.append(c);
            }
        }
        sb.append(COMILLA);
        return sb.toString();
    }

    private Producto convertirRegistroAProducto(RegistroCsv registro) {
        List<String> campos = registro.campos;
        int lineaInicio = registro.lineaInicio;

        if (campos.size() != CANTIDAD_CAMPOS) {
            throw new PersistenciaInventarioException(
                    "Error de formato CSV en '" + rutaArchivo + "' (registro iniciado en línea " + lineaInicio
                    + "): se esperaban " + CANTIDAD_CAMPOS + " campos y se encontraron " + campos.size() + "."
            );
        }

        String codigoRaw = campos.get(0);
        String nombreRaw = campos.get(1);
        String precioRaw = campos.get(2).strip();
        String stockActualRaw = campos.get(3).strip();
        String stockMinimoRaw = campos.get(4).strip();

        String codigo;
        String nombre;
        try {
            codigo = ValidarCampos.validarObligatorio(codigoRaw, "Código (campo 1)");
            nombre = ValidarCampos.validarObligatorio(nombreRaw, "Nombre (campo 2)");
        } catch (IllegalArgumentException ex) {
            throw new PersistenciaInventarioException(
                    "Dato obligatorio inválido en '" + rutaArchivo + "' (registro iniciado en línea " + lineaInicio
                    + "): " + ex.getMessage(),
                    ex
            );
        }

        if (precioRaw.isEmpty()) {
            throw new PersistenciaInventarioException(
                    "Precio vacío en '" + rutaArchivo + "' (registro iniciado en línea " + lineaInicio
                    + ", campo 3)."
            );
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(precioRaw);
        } catch (NumberFormatException ex) {
            throw new PersistenciaInventarioException(
                    "Precio inválido '" + precioRaw + "' en '" + rutaArchivo
                    + "' (registro iniciado en línea " + lineaInicio + ", campo 3).",
                    ex
            );
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PersistenciaInventarioException(
                    "Precio fuera de rango (" + precio.toPlainString() + ") en '" + rutaArchivo
                    + "' (registro iniciado en línea " + lineaInicio + ", campo 3): debe ser mayor que cero."
            );
        }

        int stockActual = parsearEnteroEstricto(stockActualRaw, "Stock actual (campo 4)", lineaInicio);
        int stockMinimo = parsearEnteroEstricto(stockMinimoRaw, "Stock mínimo (campo 5)", lineaInicio);

        return new Producto(codigo, nombre, precio, stockActual, stockMinimo);
    }

    private int parsearEnteroEstricto(String texto, String nombreCampo, int lineaInicio) {
        if (texto.isEmpty()) {
            throw new PersistenciaInventarioException(
                    nombreCampo + " vacío en '" + rutaArchivo + "' (registro iniciado en línea " + lineaInicio + ")."
            );
        }

        int valor;
        try {
            valor = Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            throw new PersistenciaInventarioException(
                    nombreCampo + " inválido '" + texto + "' en '" + rutaArchivo
                    + "' (registro iniciado en línea " + lineaInicio + ").",
                    ex
            );
        }

        if (valor < 0) {
            throw new PersistenciaInventarioException(
                    nombreCampo + " negativo (" + valor + ") en '" + rutaArchivo
                    + "' (registro iniciado en línea " + lineaInicio + "): debe ser mayor o igual que cero."
            );
        }

        return valor;
    }

    private void limpiarTemporalSinOcultarError(Path archivoTemporal, Throwable causaOriginal) {
        if (archivoTemporal == null) {
            return;
        }
        try {
            Files.deleteIfExists(archivoTemporal);
        } catch (IOException exBorrado) {
            causaOriginal.addSuppressed(exBorrado);
        }
    }

    private static final class RegistroCsv {
        private final int lineaInicio;
        private final List<String> campos;

        private RegistroCsv(int lineaInicio, List<String> campos) {
            this.lineaInicio = lineaInicio;
            this.campos = campos;
        }
    }

    private static final class LectorCsv {
        private final Reader reader;
        private final Path ruta;
        private int lineaActual = 1;
        private boolean ultimoCaracterEraCr = false;

        private LectorCsv(Reader reader, Path ruta) {
            this.reader = reader;
            this.ruta = ruta;
        }

        private List<RegistroCsv> leerTodosLosRegistros() throws IOException {
            List<RegistroCsv> registros = new ArrayList<>();

            while (true) {
                int primerCaracter = leerSiguienteCaracter();
                if (primerCaracter == -1) {
                    break;
                }

                char c = (char) primerCaracter;
                if (c == '\n') {
                    if (ultimoCaracterEraCr) {
                        ultimoCaracterEraCr = false;
                        continue;
                    }
                    lineaActual++;
                    continue;
                }
                if (c == '\r') {
                    ultimoCaracterEraCr = true;
                    lineaActual++;
                    continue;
                }
                ultimoCaracterEraCr = false;

                int lineaInicioRegistro = lineaActual;
                List<String> campos = leerRegistroEmpezandoCon(c, lineaInicioRegistro);
                registros.add(new RegistroCsv(lineaInicioRegistro, campos));
            }

            return registros;
        }

        private List<String> leerRegistroEmpezandoCon(char primerChar, int lineaInicioRegistro) throws IOException {
            List<String> campos = new ArrayList<>(CANTIDAD_CAMPOS);
            StringBuilder campoActual = new StringBuilder();
            boolean enComillas = false;
            boolean campoEntreComillas = false;
            boolean esperabaSeparadorOFin = false;
            char caracterActual = primerChar;

            while (true) {
                if (esperabaSeparadorOFin) {
                    if (caracterActual == SEPARADOR) {
                        campos.add(campoActual.toString());
                        campoActual.setLength(0);
                        esperabaSeparadorOFin = false;
                        campoEntreComillas = false;
                    } else if (caracterActual == '\n') {
                        if (!ultimoCaracterEraCr) {
                            lineaActual++;
                        }
                        ultimoCaracterEraCr = false;
                        campos.add(campoActual.toString());
                        return campos;
                    } else if (caracterActual == '\r') {
                        ultimoCaracterEraCr = true;
                        lineaActual++;
                        campos.add(campoActual.toString());
                        consumirLfSiSigue();
                        return campos;
                    } else {
                        throw new PersistenciaInventarioException(
                                "Carácter inesperado '" + caracterActual + "' tras comilla de cierre en '"
                                + ruta + "' (registro iniciado en línea " + lineaInicioRegistro
                                + ", línea actual " + lineaActual + ")."
                        );
                    }
                } else if (enComillas) {
                    if (caracterActual == COMILLA) {
                        int siguiente = leerSiguienteCaracter();
                        if (siguiente == -1) {
                            throw new PersistenciaInventarioException(
                                    "Comillas sin cerrar en '" + ruta + "' (registro iniciado en línea "
                                    + lineaInicioRegistro + ", línea actual " + lineaActual + ")."
                            );
                        }
                        if ((char) siguiente == COMILLA) {
                            campoActual.append(COMILLA);
                        } else {
                            enComillas = false;
                            esperabaSeparadorOFin = true;
                            caracterActual = (char) siguiente;
                            continue;
                        }
                    } else {
                        if (caracterActual == '\n') {
                            if (!ultimoCaracterEraCr) {
                                lineaActual++;
                            }
                            ultimoCaracterEraCr = false;
                            campoActual.append('\n');
                        } else if (caracterActual == '\r') {
                            ultimoCaracterEraCr = true;
                            lineaActual++;
                            consumirLfSiSigue();
                            campoActual.append('\n');
                        } else {
                            ultimoCaracterEraCr = false;
                            campoActual.append(caracterActual);
                        }
                    }
                } else {
                    if (campoActual.length() == 0 && !campoEntreComillas && caracterActual == COMILLA) {
                        enComillas = true;
                        campoEntreComillas = true;
                    } else if (caracterActual == COMILLA) {
                        throw new PersistenciaInventarioException(
                                "Comilla doble sin escapar dentro de campo no entrecomillado en '"
                                + ruta + "' (registro iniciado en línea " + lineaInicioRegistro
                                + ", línea actual " + lineaActual + ")."
                        );
                    } else if (caracterActual == SEPARADOR) {
                        campos.add(campoActual.toString());
                        campoActual.setLength(0);
                        campoEntreComillas = false;
                    } else if (caracterActual == '\n') {
                        if (!ultimoCaracterEraCr) {
                            lineaActual++;
                        }
                        ultimoCaracterEraCr = false;
                        campos.add(campoActual.toString());
                        return campos;
                    } else if (caracterActual == '\r') {
                        ultimoCaracterEraCr = true;
                        lineaActual++;
                        campos.add(campoActual.toString());
                        consumirLfSiSigue();
                        return campos;
                    } else {
                        ultimoCaracterEraCr = false;
                        campoActual.append(caracterActual);
                    }
                }

                int siguiente = leerSiguienteCaracter();
                if (siguiente == -1) {
                    if (enComillas) {
                        throw new PersistenciaInventarioException(
                                "Comillas sin cerrar al final del archivo '" + ruta
                                + "' (registro iniciado en línea " + lineaInicioRegistro
                                + ", línea actual " + lineaActual + ")."
                        );
                    }
                    campos.add(campoActual.toString());
                    return campos;
                }
                caracterActual = (char) siguiente;
            }
        }

        private void consumirLfSiSigue() throws IOException {
            reader.mark(1);
            int siguiente = reader.read();
            if (siguiente == '\n') {
                ultimoCaracterEraCr = false;
            } else {
                reader.reset();
            }
        }

        private int leerSiguienteCaracter() throws IOException {
            return reader.read();
        }
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
