package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static view.ModernUI.*;

public class formArtista extends JPanel {

    private static final String[] COLUMNAS = {"ID", "Nombre artístico", "Género", "País", "Canciones", "Estado"};
    private static final Color    BG_DARK   = new Color(18, 18, 40);
    private static final Color    BG_FIELD  = new Color(24, 24, 52);
    private static final Color    PURPLE    = new Color(139, 92, 246);

    private final List<Object[]> artistasData = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable            tabla;
    private JTextField        txtBuscar;
    private int nextId = 4;

    public formArtista() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 0, 0));
        inicializarDatos();
        construirUI();
    }

    // ─── Datos iniciales ────────────────────────────────────────────────────────

    private void inicializarDatos() {
        artistasData.add(new Object[]{"001", "Bad Bunny",  "Reggaeton",  "Puerto Rico", 48, "Activo"});
        artistasData.add(new Object[]{"002", "Karol G",    "Urbano",     "Colombia",    62, "Activo"});
        artistasData.add(new Object[]{"003", "Shakira",    "Pop / Rock", "Colombia",    87, "Activo"});
    }

    // ─── Construcción de la UI ───────────────────────────────────────────────────

    private void construirUI() {
        add(crearTopPanel(), BorderLayout.NORTH);
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

        JLabel titulo = label("Artistas", new Font("Segoe UI", Font.BOLD, 26), TEXT_PRIMARY);
        JLabel sub    = label("Gestión de artistas, bandas y colaboraciones",
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
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar artista...");
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrarTabla(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrarTabla(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarTabla(); }
        });

        ModernUI.RoundedButton btnNuevo = new ModernUI.RoundedButton("+ Nuevo artista", true);
        btnNuevo.setPreferredSize(new Dimension(160, 36));
        btnNuevo.addActionListener(e -> abrirFormulario(null));

        toolbar.add(txtBuscar, BorderLayout.WEST);
        toolbar.add(btnNuevo,  BorderLayout.EAST);
        return toolbar;
    }

    private ModernUI.CardPanel crearCardTabla() {
        tableModel = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        refrescarTabla(artistasData);

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

        ModernUI.RoundedButton btnEditar   = new ModernUI.RoundedButton("Editar", false);
        ModernUI.RoundedButton btnEliminar = new ModernUI.RoundedButton("Eliminar", false);
        btnEliminar.setForeground(new Color(255, 80, 120));

        btnEditar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { toast("Selecciona un artista primero", MainFrame.ToastType.INFO); return; }
            abrirFormulario(fila);
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { toast("Selecciona un artista primero", MainFrame.ToastType.INFO); return; }
            String nombre = (String) tableModel.getValueAt(fila, 1);
            int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a \"" + nombre + "\"?", "Z-One — Confirmar", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) eliminarArtista(fila);
        });

        acciones.add(btnEditar);
        acciones.add(btnEliminar);
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

        int[] anchos = {50, 180, 130, 110, 80, 90};
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
                if (col == 5) {
                    String estado = val == null ? "" : val.toString();
                    lbl.setForeground("Activo".equals(estado) ? new Color(34, 197, 94) : new Color(250, 180, 40));
                    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                }
                if (col == 0) lbl.setForeground(TEXT_MUTED);
                return lbl;
            }
        });
    }

    private void refrescarTabla(List<Object[]> datos) {
        tableModel.setRowCount(0);
        datos.forEach(tableModel::addRow);
    }

    private void filtrarTabla() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        if (texto.isEmpty()) { refrescarTabla(artistasData); return; }
        List<Object[]> filtrado = new ArrayList<>();
        for (Object[] f : artistasData)
            for (Object c : f)
                if (c != null && c.toString().toLowerCase().contains(texto)) { filtrado.add(f); break; }
        refrescarTabla(filtrado);
    }

    private void eliminarArtista(int filaTabla) {
        String id = (String) tableModel.getValueAt(filaTabla, 0);
        artistasData.removeIf(f -> f[0].equals(id));
        refrescarTabla(artistasData);
        toast("Artista eliminado correctamente", MainFrame.ToastType.SUCCESS);
    }

    // ─── Diálogo crear / editar ───────────────────────────────────────────────────

    private void abrirFormulario(Integer filaEditar) {
        boolean esEdicion = (filaEditar != null);

        String idVal      = esEdicion ? (String) tableModel.getValueAt(filaEditar, 0) : "";
        String nombreVal  = esEdicion ? (String) tableModel.getValueAt(filaEditar, 1) : "";
        String generoVal  = esEdicion ? (String) tableModel.getValueAt(filaEditar, 2) : "";
        String paisVal    = esEdicion ? (String) tableModel.getValueAt(filaEditar, 3) : "";
        String cancionVal = esEdicion ? tableModel.getValueAt(filaEditar, 4).toString() : "0";
        String estadoVal  = esEdicion ? (String) tableModel.getValueAt(filaEditar, 5) : "Activo";

        JDialog dlg = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            esEdicion ? "Editar artista" : "Nuevo artista", true);
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

        JLabel lblTitulo = label(esEdicion ? "Editar artista" : "Nuevo artista",
                                 new Font("Segoe UI", Font.BOLD, 20), TEXT_PRIMARY);
        lblTitulo.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(24));

        JTextField fNombre    = campoTexto(nombreVal);
        JTextField fGenero    = campoTexto(generoVal);
        JTextField fPais      = campoTexto(paisVal);
        JTextField fCanciones = campoTexto(cancionVal);

        JComboBox<String> cbEstado = crearCombo(
            new String[]{"Activo", "Inactivo", "En gira", "Hiatus"}, estadoVal);

        panel.add(fila("Nombre artístico *", fNombre));    panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Género musical *",   fGenero));    panel.add(Box.createVerticalStrut(12));
        panel.add(fila("País de origen",     fPais));      panel.add(Box.createVerticalStrut(12));
        panel.add(fila("N.º de canciones",   fCanciones)); panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Estado",             cbEstado));   panel.add(Box.createVerticalStrut(28));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        ModernUI.RoundedButton btnCancelar = new ModernUI.RoundedButton("Cancelar", false);
        ModernUI.RoundedButton btnGuardar  = new ModernUI.RoundedButton(
            esEdicion ? "Guardar cambios" : "Crear artista", true);

        btnCancelar.addActionListener(e -> dlg.dispose());

        btnGuardar.addActionListener(e -> {
            String nombre = fNombre.getText().trim();
            String genero = fGenero.getText().trim();
            String pais   = fPais.getText().trim();
            String canStr = fCanciones.getText().trim();
            String estado = (String) cbEstado.getSelectedItem();

            if (nombre.isEmpty()) { toast("El nombre artístico es obligatorio", MainFrame.ToastType.ERROR); return; }
            if (genero.isEmpty()) { toast("El género musical es obligatorio",    MainFrame.ToastType.ERROR); return; }

            int canciones = 0;
            try { if (!canStr.isEmpty()) canciones = Integer.parseInt(canStr); }
            catch (NumberFormatException ex) {
                toast("N.º de canciones debe ser un número", MainFrame.ToastType.ERROR); return;
            }

            if (esEdicion) {
                for (Object[] f : artistasData)
                    if (f[0].equals(idVal)) {
                        f[1] = nombre; f[2] = genero; f[3] = pais; f[4] = canciones; f[5] = estado; break;
                    }
                toast("Artista actualizado correctamente", MainFrame.ToastType.SUCCESS);
            } else {
                artistasData.add(new Object[]{String.format("%03d", nextId++), nombre, genero, pais, canciones, estado});
                toast("Artista creado: " + nombre, MainFrame.ToastType.SUCCESS);
            }
            refrescarTabla(artistasData);
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

    @SuppressWarnings("unchecked")
    private JComboBox<String> crearCombo(String[] opciones, String seleccion) {
        JComboBox<String> cb = new JComboBox<>(opciones) {
            @Override
            protected void paintComponent(Graphics g) {
                // Pintamos nosotros el fondo — el L&F no toca nada
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_FIELD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(139, 92, 246, 90));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        cb.setSelectedItem(seleccion);
        cb.setEditable(false);
        cb.setOpaque(false);          // paintComponent propio se encarga del fondo
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setForeground(TEXT_PRIMARY);
        cb.setBackground(BG_FIELD);
        cb.setMaximumRowCount(6);
        cb.setFocusable(false);
        cb.setBorder(new EmptyBorder(0, 0, 0, 0));

        // ── Renderer: controla tanto el ítem en la caja (index -1) como el popup ──
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel lbl = new JLabel(value == null ? "" : value.toString());
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lbl.setForeground(TEXT_PRIMARY);
                lbl.setBorder(new EmptyBorder(7, 12, 7, 12));
                lbl.setOpaque(true);

                if (index == -1) {
                    // Ítem visible en la barra cerrada — siempre fondo oscuro
                    lbl.setBackground(BG_FIELD);
                } else {
                    // Ítems dentro del popup desplegado
                    lbl.setBackground(isSelected ? PURPLE : BG_FIELD);
                }
                return lbl;
            }
        });

        // ── Estilizar el popup (JList dentro del JScrollPane) ──
        cb.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                Object popup = cb.getUI().getAccessibleChild(cb, 0);
                if (popup instanceof JComponent jp) {
                    jp.setBorder(BorderFactory.createLineBorder(new Color(139, 92, 246, 120), 1));
                    // Fondo del scroll interior
                    for (Component c : jp.getComponents()) {
                        if (c instanceof JScrollPane sp) {
                            sp.getViewport().setBackground(BG_FIELD);
                            sp.setBorder(BorderFactory.createEmptyBorder());
                        }
                    }
                }
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });

        return cb;
    }

    private void toast(String msg, MainFrame.ToastType type) {
        MainFrame.showToast(msg, type);
    }
}