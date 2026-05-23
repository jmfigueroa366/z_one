package view;

import model.Productor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static view.ModernUI.*;

public class formProductor extends JPanel {

    private static final String[] COLUMNAS = {
        "ID", "Nombre", "Especialidad", "Correo", "Teléfono", "Tarifa/h"
    };

    private static final Color BG_DARK  = new Color(18, 18, 40);
    private static final Color BG_FIELD = new Color(24, 24, 52);
    private static final Color PURPLE   = new Color(139, 92, 246);

    private final List<Productor> productoresData = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable            tabla;
    private JTextField        txtBuscar;
    private int               nextId = 4;

    public formProductor() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 0, 0));
        inicializarDatos();
        construirUI();
    }

    // ─── Datos iniciales ─────────────────────────────────────────────────────────

    private void inicializarDatos() {
        productoresData.add(new Productor("Mezcla",        120.0, 1, "Carlos Vives",    "cvives@mail.com",   "3001234567"));
        productoresData.add(new Productor("Masterización", 95.0,  2, "Andrés Torres",   "atorres@mail.com",  "3109876543"));
        productoresData.add(new Productor("Composición",   150.0, 3, "Mauricio Rengifo","mrengifo@mail.com", "3154561234"));
    }

    // ─── Construcción de la UI ────────────────────────────────────────────────────

    private void construirUI() {
        add(crearTopPanel(),  BorderLayout.NORTH);
        add(crearCardTabla(), BorderLayout.CENTER);
    }

    private JPanel crearTopPanel() {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(crearHeader());
        top.add(crearToolbar());
        return top;
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titulo = label("Productores", new Font("Segoe UI", Font.BOLD, 26), TEXT_PRIMARY);
        JLabel sub    = label("Gestión de productores, equipo técnico y especialidades",
                              new Font("Segoe UI", Font.PLAIN, 13), TEXT_MUTED);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        header.add(titulo);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        return header;
    }

    private JPanel crearToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(16, 0, 12, 0));

        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(280, 36));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setForeground(TEXT_PRIMARY);
        txtBuscar.setBackground(new Color(20, 20, 45));
        txtBuscar.setCaretColor(TEXT_PRIMARY);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 92, 246, 80), 1, true),
            new EmptyBorder(6, 12, 6, 12)
        ));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar productor...");
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrarTabla(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrarTabla(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarTabla(); }
        });

        ModernUI.RoundedButton btnNuevo = new ModernUI.RoundedButton("+ Nuevo productor", true);
        btnNuevo.setPreferredSize(new Dimension(170, 36));
        btnNuevo.addActionListener(e -> abrirFormulario(null));

        toolbar.add(txtBuscar, BorderLayout.WEST);
        toolbar.add(btnNuevo,  BorderLayout.EAST);
        return toolbar;
    }

    private ModernUI.CardPanel crearCardTabla() {
        tableModel = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        refrescarTabla(productoresData);

        tabla = new JTable(tableModel);
        configurarTabla();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBackground(BG_DARK);

        ModernUI.CardPanel card = new ModernUI.CardPanel(16);
        card.setLayout(new BorderLayout());
        card.add(scroll,          BorderLayout.CENTER);
        card.add(crearAcciones(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearAcciones() {
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        acciones.setOpaque(false);
        acciones.setBorder(new EmptyBorder(12, 0, 0, 0));

        ModernUI.RoundedButton btnEditar   = new ModernUI.RoundedButton("Editar",   false);
        ModernUI.RoundedButton btnEliminar = new ModernUI.RoundedButton("Eliminar", false);
        btnEliminar.setForeground(new Color(255, 80, 120));

        btnEditar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { toast("Selecciona un productor primero", MainFrame.ToastType.INFO); return; }
            abrirFormulario(fila);
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { toast("Selecciona un productor primero", MainFrame.ToastType.INFO); return; }
            String nombre = (String) tableModel.getValueAt(fila, 1);
            int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a \"" + nombre + "\"?", "Z-One — Confirmar", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) eliminarProductor(fila);
        });

        ModernUI.RoundedButton btnRefrescar = new ModernUI.RoundedButton("↻ Refrescar", false);
        btnRefrescar.addActionListener(e -> {
            txtBuscar.setText("");
            refrescarTabla(productoresData);
            toast("Lista actualizada", MainFrame.ToastType.INFO);
        });

        acciones.add(btnEditar);
        acciones.add(btnEliminar);
        acciones.add(btnRefrescar);
        return acciones;
    }

    // ─── Tabla ───────────────────────────────────────────────────────────────────

    private void configurarTabla() {
        tabla.setOpaque(false);
        tabla.setBackground(BG_DARK);
        tabla.setForeground(TEXT_PRIMARY);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(40);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(new Color(139, 92, 246, 60));
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFocusable(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader head = tabla.getTableHeader();
        head.setBackground(new Color(13, 13, 30));
        head.setForeground(TEXT_MUTED);
        head.setFont(new Font("Segoe UI", Font.BOLD, 11));
        head.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(139, 92, 246, 50)));
        head.setReorderingAllowed(false);

        int[] anchos = {50, 160, 130, 180, 110, 90};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                lbl.setBorder(new EmptyBorder(0, 14, 0, 14));
                lbl.setOpaque(true);
                Color base = (row % 2 == 0) ? BG_DARK : new Color(22, 22, 50);
                lbl.setBackground(sel ? new Color(139, 92, 246, 60) : base);
                lbl.setForeground(TEXT_PRIMARY);
                // Tarifa/h en verde
                if (col == 5) {
                    lbl.setForeground(new Color(34, 197, 94));
                    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                }
                if (col == 0) lbl.setForeground(TEXT_MUTED);
                return lbl;
            }
        });
    }

    private void refrescarTabla(List<Productor> datos) {
        tableModel.setRowCount(0);
        int id = 1;
        for (Productor p : datos) {
            tableModel.addRow(new Object[]{
                String.format("%03d", p.getIdentificacion()),
                p.getNombre(),
                p.getEspecialidad(),
                p.getCorreo(),
                p.getTelefono(),
                String.format("$%.0f", p.getTarifaHora())
            });
            id++;
        }
    }

    private void filtrarTabla() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        if (texto.isEmpty()) { refrescarTabla(productoresData); return; }
        List<Productor> filtrado = new ArrayList<>();
        for (Productor p : productoresData) {
            if (p.getNombre().toLowerCase().contains(texto)
                || p.getEspecialidad().toLowerCase().contains(texto)
                || p.getCorreo().toLowerCase().contains(texto)) {
                filtrado.add(p);
            }
        }
        refrescarTabla(filtrado);
    }

    private void eliminarProductor(int filaTabla) {
        int id = Integer.parseInt(((String) tableModel.getValueAt(filaTabla, 0)));
        productoresData.removeIf(p -> p.getIdentificacion() == id);
        refrescarTabla(productoresData);
        toast("Productor eliminado correctamente", MainFrame.ToastType.SUCCESS);
    }

    // ─── Diálogo crear / editar ──────────────────────────────────────────────────

    // Método público requerido por el diagrama de clase
    public void guardar()  { /* integración con servicio — pendiente */ }
    public void editar()   { /* carga el productor seleccionado — pendiente */ }

    private void abrirFormulario(Integer filaEditar) {
        boolean esEdicion = (filaEditar != null);

        String nombreVal      = esEdicion ? (String) tableModel.getValueAt(filaEditar, 1) : "";
        String especialidadVal= esEdicion ? (String) tableModel.getValueAt(filaEditar, 2) : "";
        String correoVal      = esEdicion ? (String) tableModel.getValueAt(filaEditar, 3) : "";
        String telefonoVal    = esEdicion ? (String) tableModel.getValueAt(filaEditar, 4) : "";
        String tarifaVal      = esEdicion
            ? tableModel.getValueAt(filaEditar, 5).toString().replace("$", "")
            : "0";

        JDialog dlg = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            esEdicion ? "Editar productor" : "Nuevo productor", true);
        dlg.setResizable(false);

        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel lblTitulo = label(esEdicion ? "Editar productor" : "Nuevo productor",
                                 new Font("Segoe UI", Font.BOLD, 20), TEXT_PRIMARY);
        lblTitulo.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(24));

        JTextField fNombre       = campoTexto(nombreVal);
        JTextField fEspecialidad = campoTexto(especialidadVal);
        JTextField fCorreo       = campoTexto(correoVal);
        JTextField fTelefono     = campoTexto(telefonoVal);
        JTextField fTarifa       = campoTexto(tarifaVal);

        panel.add(fila("Nombre completo *",  fNombre));       panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Especialidad *",     fEspecialidad)); panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Correo electrónico", fCorreo));       panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Teléfono",           fTelefono));     panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Tarifa por hora ($)",fTarifa));       panel.add(Box.createVerticalStrut(28));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        ModernUI.RoundedButton btnCancelar = new ModernUI.RoundedButton("Cancelar", false);
        ModernUI.RoundedButton btnGuardar  = new ModernUI.RoundedButton(
            esEdicion ? "Guardar cambios" : "Crear productor", true);

        btnCancelar.addActionListener(e -> dlg.dispose());

        btnGuardar.addActionListener(e -> {
            String nombre       = fNombre.getText().trim();
            String especialidad = fEspecialidad.getText().trim();
            String correo       = fCorreo.getText().trim();
            String telefono     = fTelefono.getText().trim();
            String tarifaStr    = fTarifa.getText().trim();

            if (nombre.isEmpty())       { toast("El nombre es obligatorio",       MainFrame.ToastType.ERROR); return; }
            if (especialidad.isEmpty()) { toast("La especialidad es obligatoria", MainFrame.ToastType.ERROR); return; }

            double tarifa = 0;
            try { if (!tarifaStr.isEmpty()) tarifa = Double.parseDouble(tarifaStr); }
            catch (NumberFormatException ex) {
                toast("La tarifa debe ser un número", MainFrame.ToastType.ERROR); return;
            }

            if (esEdicion) {
                int id = Integer.parseInt(((String) tableModel.getValueAt(filaEditar, 0)));
                for (Productor p : productoresData) {
                    if (p.getIdentificacion() == id) {
                        p.setNombre(nombre);
                        p.setEspecialidad(especialidad);
                        p.setCorreo(correo);
                        p.setTelefono(telefono);
                        p.setTarifaHora(tarifa);
                        break;
                    }
                }
                toast("Productor actualizado correctamente", MainFrame.ToastType.SUCCESS);
            } else {
                productoresData.add(new Productor(especialidad, tarifa, nextId++, nombre, correo, telefono));
                toast("Productor creado: " + nombre, MainFrame.ToastType.SUCCESS);
            }

            refrescarTabla(productoresData);
            dlg.dispose();
        });

        btnRow.add(btnCancelar);
        btnRow.add(btnGuardar);
        panel.add(btnRow);

        dlg.setContentPane(panel);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(460, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ─── Helpers de UI ───────────────────────────────────────────────────────────

    private JLabel label(String texto, Font font, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    private JTextField campoTexto(String valor) {
        JTextField tf = new JTextField(valor);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(BG_FIELD);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 92, 246, 70), 1, true),
            new EmptyBorder(7, 12, 7, 12)
        ));
        return tf;
    }

    private JPanel fila(String etiqueta, JComponent campo) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_MUTED);
        row.add(lbl,   BorderLayout.NORTH);
        row.add(campo, BorderLayout.CENTER);
        return row;
    }

    private void toast(String msg, MainFrame.ToastType type) {
        MainFrame.showToast(msg, type);
    }
}