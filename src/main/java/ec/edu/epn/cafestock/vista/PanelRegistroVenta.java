/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ec.edu.epn.cafestock.vista;

import ec.edu.epn.cafestock.modelo.Producto;
import ec.edu.epn.cafestock.modelo.Venta;
import ec.edu.epn.cafestock.servicio.ServicioProductos;
import ec.edu.epn.cafestock.validacion.ValidadorVenta;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import java.util.Date;
import java.util.Calendar;
import java.time.ZoneId;
import java.text.ParseException;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

/**
 *
 * @author Dell G15
 */
public class PanelRegistroVenta extends javax.swing.JPanel {
    private ServicioProductos servicioProductos;
    private Consumer<Venta> alRegistrar;

    private List<Producto> productosDisponibles = List.of();

    /**
     * Creates new form PanelRegistroVenta
     */
    public PanelRegistroVenta() {
        initComponents();
        configurarFormulario();
    }

    public PanelRegistroVenta(
            ServicioProductos servicioProductos,
            Consumer<Venta> alRegistrar) {

        this();

        this.servicioProductos = Objects.requireNonNull(
                servicioProductos,
                "El servicio de productos no puede ser nulo."
        );

        this.alRegistrar = alRegistrar;

        cargarProductos();
    }
    
    private void configurarFormulario() {    
        
        configurarSelectorFecha();
    }
    
    private void configurarSelectorFecha() {

        // Obtener la fecha actual
        Date hoy = new Date();

        // Establecer el límite máximo: hoy a las 23:59:59
        Calendar limite = Calendar.getInstance();
        limite.setTime(hoy);
        limite.set(Calendar.HOUR_OF_DAY, 23);
        limite.set(Calendar.MINUTE, 59);
        limite.set(Calendar.SECOND, 59);
        limite.set(Calendar.MILLISECOND, 999);

        // Crear el modelo del Spinner
        SpinnerDateModel modelo = new SpinnerDateModel(
                hoy,
                null,
                limite.getTime(),
                Calendar.DAY_OF_MONTH
        );

        spnFechaVenta.setModel(modelo);

        // Establecer el formato de fecha
        JSpinner.DateEditor editor
                = new JSpinner.DateEditor(spnFechaVenta, "dd/MM/yyyy");

        editor.getFormat().setLenient(false);

        spnFechaVenta.setEditor(editor);
    }

    private void cargarProductos() {

        try {

            productosDisponibles = List.copyOf(
                    servicioProductos.listarProductos()
            );

            DefaultComboBoxModel<String> modelo
                    = new DefaultComboBoxModel<>();

            modelo.addElement("Selecciona un producto...");

            for (Producto producto : productosDisponibles) {

                modelo.addElement(
                        producto.getNombre()
                        + " (" + producto.getCodigo() + ")"
                );
            }

            cboProducto.setModel(modelo);
            cboProducto.setSelectedIndex(0);


        } catch (RuntimeException error) {

            productosDisponibles = List.of();

            cboProducto.setModel(new DefaultComboBoxModel<>());


            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar los productos:\n" + error.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private Producto obtenerProductoSeleccionado() {

        int posicion = cboProducto.getSelectedIndex();

        if (posicion <= 0 || posicion > productosDisponibles.size()) {
            return null;
        }

        return productosDisponibles.get(posicion - 1);
    }
   
    
    public void refrescarProductos() {

        if (servicioProductos != null) {
            cargarProductos();
        }
    }
    
    private LocalDate obtenerFechaVenta() {

        try {

            // Aplicar el valor escrito en el Spinner
            spnFechaVenta.commitEdit();

            // Obtener la fecha seleccionada
            Date fechaSeleccionada
                    = (Date) spnFechaVenta.getValue();

            // Convertir Date a LocalDate
            LocalDate fecha = fechaSeleccionada.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Comprobar que no sea una fecha futura
            if (fecha.isAfter(LocalDate.now())) {

                throw new IllegalArgumentException(
                        "La fecha de venta no puede ser futura."
                );
            }

            return fecha;

        } catch (ParseException error) {

            throw new IllegalArgumentException(
                    "Selecciona una fecha válida."
            );
        }
    }
    
    private void registrarVenta() {

        try {

            Producto producto = obtenerProductoSeleccionado();

            int cantidad = ValidadorVenta.validarCantidadContraStock(
                    txtCantidad.getText(),
                    producto
            );

            LocalDate fecha = obtenerFechaVenta();

            Venta venta = new Venta(
                    producto,
                    cantidad,
                    fecha
            );

            if (alRegistrar == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "El formulario no está conectado al registro de ventas.\n"
                        + "No se modificó el inventario.",
                        "Registro no disponible",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            alRegistrar.accept(venta);
            refrescarProductos();

            JOptionPane.showMessageDialog(
                    this,
                    "Venta registrada correctamente.\n"
                    + "El stock del producto ha sido actualizado.",
                    "Venta exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

            limpiarCampos();

        } catch (IllegalArgumentException error) {

            JOptionPane.showMessageDialog(
                    this,
                    error.getMessage(),
                    "Datos inválidos",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException error) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo procesar la venta:\n" + error.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void limpiarCampos() {
        
        if (cboProducto.getItemCount() > 0) {
            cboProducto.setSelectedIndex(0);
        }

        txtCantidad.setText("");

        configurarSelectorFecha();

        txtCantidad.requestFocusInWindow();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlContenido = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblEncabezado = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        lblProducto = new javax.swing.JLabel();
        lblFechaVenta = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        btnRegistrarVenta = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        cboProducto = new javax.swing.JComboBox<>();
        lblCantidad = new javax.swing.JLabel();
        spnFechaVenta = new javax.swing.JSpinner();

        jPanel1.setBackground(new java.awt.Color(250, 248, 245));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(232, 223, 216), 1, true));
        jPanel2.setPreferredSize(new java.awt.Dimension(600, 460));

        lblEncabezado.setBackground(new java.awt.Color(252, 236, 228));
        lblEncabezado.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblEncabezado.setForeground(new java.awt.Color(84, 53, 28));
        lblEncabezado.setText("• CaféStock • Ventas");
        lblEncabezado.setOpaque(true);

        jSeparator1.setForeground(new java.awt.Color(242, 236, 231));

        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(35, 23, 16));
        lblTitulo.setText("Registrar Venta");

        lblSubtitulo.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        lblSubtitulo.setForeground(new java.awt.Color(153, 153, 153));
        lblSubtitulo.setText("\tSelecciona un producto y completa los datos.");

        lblProducto.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblProducto.setForeground(new java.awt.Color(84, 53, 28));
        lblProducto.setText("Producto");

        lblFechaVenta.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblFechaVenta.setForeground(new java.awt.Color(84, 53, 28));
        lblFechaVenta.setText("Fecha de Venta (dd/MM/yyyy)");

        txtCantidad.addActionListener(this::txtCantidadActionPerformed);

        jSeparator2.setForeground(new java.awt.Color(242, 236, 231));

        btnRegistrarVenta.setBackground(new java.awt.Color(238, 218, 201));
        btnRegistrarVenta.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        btnRegistrarVenta.setForeground(new java.awt.Color(35, 23, 16));
        btnRegistrarVenta.setText("Registrar venta");
        btnRegistrarVenta.setOpaque(true);
        btnRegistrarVenta.addActionListener(this::btnRegistrarVentaActionPerformed);

        btnLimpiar.setBackground(new java.awt.Color(238, 218, 201));
        btnLimpiar.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(35, 23, 16));
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        lblCantidad.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblCantidad.setForeground(new java.awt.Color(84, 53, 28));
        lblCantidad.setText("Cantidad");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEncabezado)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cboProducto, 0, 554, Short.MAX_VALUE)
                                .addComponent(lblFechaVenta)
                                .addComponent(lblCantidad)
                                .addComponent(lblProducto)
                                .addComponent(spnFechaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtCantidad))
                            .addComponent(lblTitulo)
                            .addComponent(lblSubtitulo)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 576, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnLimpiar)
                                .addGap(18, 18, 18)
                                .addComponent(btnRegistrarVenta)
                                .addGap(57, 57, 57))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 576, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblEncabezado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubtitulo)
                .addGap(16, 16, 16)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblProducto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(lblCantidad)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblFechaVenta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnFechaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRegistrarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(64, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(76, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(40, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        javax.swing.GroupLayout pnlContenidoLayout = new javax.swing.GroupLayout(pnlContenido);
        pnlContenido.setLayout(pnlContenidoLayout);
        pnlContenidoLayout.setHorizontalGroup(
            pnlContenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenidoLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlContenidoLayout.setVerticalGroup(
            pnlContenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenidoLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContenido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContenido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegistrarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarVentaActionPerformed
      registrarVenta();
    }//GEN-LAST:event_btnRegistrarVentaActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
      limpiarCampos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void txtCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRegistrarVenta;
    private javax.swing.JComboBox<String> cboProducto;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblEncabezado;
    private javax.swing.JLabel lblFechaVenta;
    private javax.swing.JLabel lblProducto;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JSpinner spnFechaVenta;
    private javax.swing.JTextField txtCantidad;
    // End of variables declaration//GEN-END:variables
}
