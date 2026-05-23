package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

import static view.ModernUI.*;

public class formArtista extends JPanel {

    // ── Paleta ────────────────────────────────────────────────────────
    private static final Color BG_BASE    = new Color(10,  8,  28);
    private static final Color BG_CARD    = new Color(16, 14, 38);
    private static final Color BG_CARD2   = new Color(20, 18, 48);
    private static final Color PURPLE     = new Color(139, 92, 246);
    private static final Color PURPLE_DIM = new Color(139, 92, 246, 40);
    private static final Color CYAN       = new Color(6, 182, 212);
    private static final Color GREEN      = new Color(34, 197, 94);
    private static final Color PINK       = new Color(236, 72, 153);
    private static final Color AMBER      = new Color(245, 158, 11);
    private static final Color TEXT_PRI   = new Color(241, 245, 249);
    private static final Color TEXT_SEC   = new Color(148, 163, 184);
    private static final Color BORDER_COL = new Color(139, 92, 246, 55);

    private static final String[] COLUMNAS = {"#", "Nombre artístico", "Género", "País", "Canciones", "Estado"};

    private final List<Object[]> artistasData = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable            tabla;
    private JTextField        txtBuscar;
    private int nextId = 4;

    // Stat labels para actualizar
    private JLabel statTotal, statActivos, statPaises, statCanciones;

    public formArtista() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(28, 28, 28, 28));
        inicializarDatos();
        construirUI();
    }

    // ─── Datos ──────────────────────────────────────────────────────────────────
    private void inicializarDatos() {
        artistasData.add(new Object[]{"001", "Bad Bunny", "Reggaeton",  "Puerto Rico", 48, "Activo"});
        artistasData.add(new Object[]{"002", "Karol G",   "Urbano",     "Colombia",    62, "Activo"});
        artistasData.add(new Object[]{"003", "Shakira",   "Pop / Rock", "Colombia",    87, "Activo"});
    }

    // ─── UI principal ────────────────────────────────────────────────────────────
    private void construirUI() {
        // Header
        add(crearHeader(), BorderLayout.NORTH);

        // Cuerpo: stats arriba + tabla abajo
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(Box.createVerticalStrut(20));
        body.add(crearStatsRow());
        body.add(Box.createVerticalStrut(20));
        body.add(crearTablaPanel());

        add(body, BorderLayout.CENTER);
    }

    // ─── Header ─────────────────────────────────────────────────────────────────
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);

        // Título + subtítulo
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel ico = new JLabel("🎤");
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        ico.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Artistas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(TEXT_PRI);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Gestión de artistas, bandas y colaboraciones");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_SEC);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        titles.add(ico);
        titles.add(Box.createVerticalStrut(4));
        titles.add(titulo);
        titles.add(Box.createVerticalStrut(2));
        titles.add(sub);

        // Barra de búsqueda + botón
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        txtBuscar = crearSearchField();
        ModernUI.RoundedButton btnNuevo = new ModernUI.RoundedButton("＋ Nuevo artista", true);
        btnNuevo.setPreferredSize(new Dimension(170, 38));
        btnNuevo.addActionListener(e -> abrirFormulario(null));

        actions.add(txtBuscar);
        actions.add(btnNuevo);

        header.add(titles,  BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JTextField crearSearchField() {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER_COL);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setPreferredSize(new Dimension(240, 38));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(TEXT_PRI);
        tf.setOpaque(false);
        tf.setCaretColor(TEXT_PRI);
        tf.setBorder(new EmptyBorder(0, 14, 0, 14));
        tf.putClientProperty("JTextField.placeholderText", "🔍  Buscar artista...");
        tf.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrarTabla(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrarTabla(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarTabla(); }
        });
        return tf;
    }

    // ─── Stats row ──────────────────────────────────────────────────────────────
    private JPanel crearStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        row.setAlignmentX(LEFT_ALIGNMENT);

        statTotal     = new JLabel();
        statActivos   = new JLabel();
        statPaises    = new JLabel();
        statCanciones = new JLabel();

        actualizarStats();

        row.add(crearStatCard("TOTAL ARTISTAS",  statTotal,     PURPLE, "🎤"));
        row.add(crearStatCard("ACTIVOS",          statActivos,   GREEN,  "✅"));
        row.add(crearStatCard("PAÍSES",           statPaises,    CYAN,   "🌍"));
        row.add(crearStatCard("CANCIONES",        statCanciones, AMBER,  "🎵"));
        return row;
    }

    private JPanel crearStatCard(String titulo, JLabel valorLabel, Color accentColor, String emoji) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Fondo
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Borde con glow del color accent
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 60));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                // Glow superior
                GradientPaint topGlow = new GradientPaint(
                    0, 0, new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30),
                    0, getHeight()/2f, new Color(0,0,0,0));
                g2.setPaint(topGlow);
                g2.fillRoundRect(0, 0, getWidth(), getHeight()/2, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(8, 0));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Emoji badge
        JLabel emojiLbl = new JLabel(emoji);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        // Texto
        JPanel txtPanel = new JPanel();
        txtPanel.setOpaque(false);
        txtPanel.setLayout(new BoxLayout(txtPanel, BoxLayout.Y_AXIS));

        JLabel tituloLbl = new JLabel(titulo);
        tituloLbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        tituloLbl.setForeground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 200));
        tituloLbl.setAlignmentX(LEFT_ALIGNMENT);

        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valorLabel.setForeground(TEXT_PRI);
        valorLabel.setAlignmentX(LEFT_ALIGNMENT);

        txtPanel.add(tituloLbl);
        txtPanel.add(valorLabel);

        card.add(emojiLbl, BorderLayout.WEST);
        card.add(txtPanel, BorderLayout.CENTER);
        return card;
    }

    private void actualizarStats() {
        statTotal.setText(String.valueOf(artistasData.size()));
        long activos = artistasData.stream().filter(f -> "Activo".equals(f[5])).count();
        statActivos.setText(String.valueOf(activos));
        long paises = artistasData.stream().map(f -> f[3]).distinct().count();
        statPaises.setText(String.valueOf(paises));
        int canciones = artistasData.stream().mapToInt(f -> (f[4] instanceof Integer i) ? i : 0).sum();
        statCanciones.setText(String.valueOf(canciones));
    }

    // ─── Panel tabla ─────────────────────────────────────────────────────────────
    private JPanel crearTablaPanel() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));
        card.setAlignmentX(LEFT_ALIGNMENT);

        // Tabla
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
        scroll.getViewport().setBackground(new Color(0,0,0,0));
        scroll.setBackground(new Color(0,0,0,0));

        // Footer con botones
        JPanel footer = crearFooter();

        card.add(crearTablaHeader(), BorderLayout.NORTH);
        card.add(scroll,            BorderLayout.CENTER);
        card.add(footer,            BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearTablaHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.setBorder(new EmptyBorder(16, 18, 10, 18));

        JLabel lbl = new JLabel("Lista de artistas");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_PRI);

        JLabel count = new JLabel(artistasData.size() + " registros");
        count.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        count.setForeground(TEXT_SEC);

        h.add(lbl,   BorderLayout.WEST);
        h.add(count, BorderLayout.EAST);
        return h;
    }

    private JPanel crearFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(12, 14, 14, 14));

        ModernUI.RoundedButton btnEditar   = new ModernUI.RoundedButton("✏  Editar", false);
        ModernUI.RoundedButton btnEliminar = new ModernUI.RoundedButton("🗑  Eliminar", false);
        btnEliminar.setForeground(PINK);

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

        footer.add(btnEditar);
        footer.add(btnEliminar);
        return footer;
    }

    // ─── Tabla config ─────────────────────────────────────────────────────────────
    private void configurarTabla() {
        tabla.setOpaque(false);
        tabla.setBackground(new Color(0,0,0,0));
        tabla.setForeground(TEXT_PRI);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(44);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(new Color(139, 92, 246, 50));
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFocusable(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader head = tabla.getTableHeader();
        head.setBackground(new Color(12, 10, 30));
        head.setForeground(new Color(139, 92, 246));
        head.setFont(new Font("Segoe UI", Font.BOLD, 10));
        head.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(139, 92, 246, 60)));
        head.setReorderingAllowed(false);
        head.setPreferredSize(new Dimension(0, 36));

        int[] anchos = {52, 190, 130, 120, 90, 100};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                lbl.setBorder(new EmptyBorder(0, 16, 0, 16));
                lbl.setOpaque(true);

                Color base = (row % 2 == 0) ? new Color(16,14,38) : new Color(19,17,44);
                lbl.setBackground(sel ? new Color(139, 92, 246, 50) : base);
                lbl.setForeground(TEXT_PRI);

                // Columna ID
                if (col == 0) {
                    lbl.setForeground(new Color(139, 92, 246));
                    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 11f));
                }

                // Columna Estado — badge de color
                if (col == 5 && val != null) {
                    String estado = val.toString();
                    Color c = switch (estado) {
                        case "Activo"   -> GREEN;
                        case "En gira"  -> CYAN;
                        case "Hiatus"   -> AMBER;
                        default         -> PINK;
                    };
                    lbl.setForeground(c);
                    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                    // Prefijo burbuja visual
                    lbl.setText("● " + estado);
                }

                // Columna canciones — mini barra de progreso pintada sobre el label
                if (col == 4 && val != null) {
                    int canciones = Integer.parseInt(val.toString());
                    lbl.setText(String.valueOf(canciones));
                    // Dibujamos una pequeña barra debajo del número
                    lbl.setIcon(crearMiniBar(canciones, 120, 3, PURPLE));
                    lbl.setHorizontalTextPosition(JLabel.LEFT);
                    lbl.setIconTextGap(6);
                }

                return lbl;
            }
        });
    }

    /** Mini barra de progreso como Icon */
    private Icon crearMiniBar(int valor, int max, int height, Color color) {
        return new Icon() {
            @Override public int getIconWidth()  { return 54; }
            @Override public int getIconHeight() { return height + 4; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Fondo
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillRoundRect(x, y + 2, getIconWidth(), height, height, height);
                // Relleno
                int fill = (int)(getIconWidth() * Math.min(valor / (float) max, 1f));
                g2.setColor(color);
                if (fill > 0)
                    g2.fillRoundRect(x, y + 2, fill, height, height, height);
                g2.dispose();
            }
        };
    }

    private void refrescarTabla(List<Object[]> datos) {
        tableModel.setRowCount(0);
        datos.forEach(tableModel::addRow);
        actualizarStats();
        // Actualizar contador en el header (si ya está construido)
        repaint();
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

        JDialog dlg = new JDialog(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            esEdicion ? "Editar artista" : "Nuevo artista", true);
        dlg.setResizable(false);
        dlg.setUndecorated(false);

        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Fondo degradado
                GradientPaint gp = new GradientPaint(0, 0, new Color(10,8,28), getWidth(), getHeight(), new Color(20,16,50));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Glow morado arriba
                RadialGradientPaint glow = new RadialGradientPaint(
                    getWidth() * 0.5f, 0, getWidth() * 0.7f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(139,92,246,40), new Color(139,92,246,0)});
                g2.setPaint(glow);
                g2.fillRect(0, 0, getWidth(), getHeight()/2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(28, 32, 28, 32));

        // Título con ícono
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(LEFT_ALIGNMENT);
        JLabel icoLbl = new JLabel(esEdicion ? "✏" : "🎤");
        icoLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        JLabel lblTitulo = new JLabel(esEdicion ? "Editar artista" : "Nuevo artista");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(TEXT_PRI);
        titleRow.add(icoLbl);
        titleRow.add(lblTitulo);

        // Divider
        JPanel div = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0,0,new Color(139,92,246,180),getWidth()*0.6f,0,new Color(139,92,246,0));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),2); g2.dispose();
            }
        };
        div.setOpaque(false);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        div.setPreferredSize(new Dimension(0, 2));
        div.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(titleRow);
        panel.add(Box.createVerticalStrut(10));
        panel.add(div);
        panel.add(Box.createVerticalStrut(22));

        JTextField fNombre    = campoTexto(nombreVal);
        JTextField fGenero    = campoTexto(generoVal);
        JTextField fPais      = campoTexto(paisVal);
        JTextField fCanciones = campoTexto(cancionVal);
        JComboBox<String> cbEstado = crearCombo(
            new String[]{"Activo", "Inactivo", "En gira", "Hiatus"}, estadoVal);

        // Fila doble: Nombre + Género
        panel.add(filaDoble("Nombre artístico *", fNombre, "Género musical *", fGenero));
        panel.add(Box.createVerticalStrut(14));
        // Fila doble: País + Canciones
        panel.add(filaDoble("País de origen", fPais, "N.º de canciones", fCanciones));
        panel.add(Box.createVerticalStrut(14));
        panel.add(fila("Estado", cbEstado));
        panel.add(Box.createVerticalStrut(28));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        ModernUI.RoundedButton btnCancelar = new ModernUI.RoundedButton("Cancelar", false);
        ModernUI.RoundedButton btnGuardar  = new ModernUI.RoundedButton(esEdicion ? "Guardar cambios" : "Crear artista", true);

        btnCancelar.addActionListener(e -> dlg.dispose());
        btnGuardar.addActionListener(e -> {
            String nombre  = fNombre.getText().trim();
            String genero  = fGenero.getText().trim();
            String pais    = fPais.getText().trim();
            String canStr  = fCanciones.getText().trim();
            String estado  = (String) cbEstado.getSelectedItem();

            if (nombre.isEmpty()) { toast("El nombre artístico es obligatorio", MainFrame.ToastType.ERROR); return; }
            if (genero.isEmpty()) { toast("El género musical es obligatorio",    MainFrame.ToastType.ERROR); return; }
            int canciones = 0;
            try { if (!canStr.isEmpty()) canciones = Integer.parseInt(canStr); }
            catch (NumberFormatException ex) { toast("N.º de canciones debe ser un número", MainFrame.ToastType.ERROR); return; }

            if (esEdicion) {
                for (Object[] f : artistasData)
                    if (f[0].equals(idVal)) { f[1]=nombre; f[2]=genero; f[3]=pais; f[4]=canciones; f[5]=estado; break; }
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
        dlg.setMinimumSize(new Dimension(520, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ─── Helpers UI ──────────────────────────────────────────────────────────────

    private JTextField campoTexto(String valor) {
        JTextField tf = new JTextField(valor) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD2);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_COL);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(TEXT_PRI);
        tf.setOpaque(false);
        tf.setCaretColor(TEXT_PRI);
        tf.setBorder(new EmptyBorder(0, 12, 0, 12));
        tf.setPreferredSize(new Dimension(200, 38));
        return tf;
    }

    private JPanel fila(String etiqueta, JComponent campo) {
        JPanel row = new JPanel(new BorderLayout(0, 6));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(139, 92, 246, 200));
        row.add(lbl,   BorderLayout.NORTH);
        row.add(campo, BorderLayout.CENTER);
        return row;
    }

    private JPanel filaDoble(String lbl1, JComponent c1, String lbl2, JComponent c2) {
        JPanel p = new JPanel(new GridLayout(1, 2, 14, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        p.add(fila(lbl1, c1));
        p.add(fila(lbl2, c2));
        return p;
    }

    @SuppressWarnings("unchecked")
    private JComboBox<String> crearCombo(String[] opciones, String seleccion) {
        JComboBox<String> cb = new JComboBox<>(opciones) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD2);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_COL);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cb.setSelectedItem(seleccion);
        cb.setOpaque(false);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setForeground(TEXT_PRI);
        cb.setBackground(BG_CARD2);
        cb.setBorder(new EmptyBorder(0, 0, 0, 0));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean sel, boolean foc) {
                JLabel lbl = new JLabel(value == null ? "" : value.toString());
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lbl.setForeground(TEXT_PRI);
                lbl.setBorder(new EmptyBorder(8, 14, 8, 14));
                lbl.setOpaque(true);
                lbl.setBackground(sel ? PURPLE : BG_CARD2);
                return lbl;
            }
        });
        cb.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                Object popup = cb.getUI().getAccessibleChild(cb, 0);
                if (popup instanceof JComponent jp) {
                    jp.setBorder(BorderFactory.createLineBorder(new Color(139,92,246,120), 1));
                    for (Component c : jp.getComponents())
                        if (c instanceof JScrollPane sp) {
                            sp.getViewport().setBackground(BG_CARD2);
                            sp.setBorder(BorderFactory.createEmptyBorder());
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