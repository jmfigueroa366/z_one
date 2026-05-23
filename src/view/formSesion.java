package view;

import model.Artista;
import model.Productor;
import model.Sesion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static view.ModernUI.*;

/**
 * formSesion — gestión de session_grabacion.
 *
 * Columnas DB → campo UI:
 *   id_session        → ID (autoincremental)
 *   fecha             → campoFecha         (DATE  dd/MM/yyyy)
 *   hora_inicio       → campoHoraInicio    (VARCHAR2 HH:mm)
 *   hora_fin          → campoHoraFin       (VARCHAR2 HH:mm)
 *   duracion          → calculado (hora_fin - hora_inicio) — etiquetaCosto
 *   estado_session    → comboEstado        (Programada / En curso / Finalizada / Cancelada)
 *   nombre_sesion     → campoNombre
 *   observaciones     → campoObservaciones
 *   artista_FKv5      → comboArtista       (id_artista)
 *   productos_FK      → comboProductor     (id_productor)
 *   session_grabacion_cabina_FK → comboCabina (id_cabina)
 */
public class formSesion extends JPanel {

    // Columnas visibles en la tabla — reflejan los campos más relevantes de session_grabacion
    private static final String[] COLUMNAS = {
        "ID", "Nombre sesión", "Fecha", "Hora inicio", "Hora fin", "Artista", "Productor", "Estado"
    };

    private static final Color             BG_DARK  = new Color(18, 18, 40);
    private static final Color             BG_FIELD = new Color(24, 24, 52);
    private static final Color             PURPLE   = new Color(139, 92, 246);
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Listas de soporte (se llenarán desde el servicio cuando haya backend)
    private final List<Artista>   artistas    = new ArrayList<>();
    private final List<Productor> productores = new ArrayList<>();
    private final List<String>    cabinas     = new ArrayList<>();   // nombre_cabina de tabla cabina
    private final List<Sesion>    sesionesData = new ArrayList<>();

    private DefaultTableModel tableModel;
    private JTable            tabla;
    private JTextField        txtBuscar;
    private int               nextId = 4;

    public formSesion() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 0, 0));
        cargarCombos();
        inicializarDatos();
        construirUI();
    }

    // ─── Métodos del diagrama de clase ────────────────────────────────────────────

    /** Envía los datos al servicio (INSERT / UPDATE en session_grabacion). Pendiente de backend. */
    public void guardar() { /* SesionServicio.guardar(sesion) — pendiente */ }

    /**
     * Recalcula y muestra el costo estimado usando tarifa_hora del productor
     * y la duración derivada de hora_inicio / hora_fin.
     * Mapea a: duracion (DATE) y tarifa_hora (NUMBER) de productorv1.
     */
    public void calcularYMostrarCosto(JComboBox<Productor> comboProductor,
                                      JTextField campoDuracion,
                                      JLabel etiquetaCosto) {
        try {
            Productor p = (Productor) comboProductor.getSelectedItem();
            double horas = Double.parseDouble(campoDuracion.getText().trim());
            if (p != null && horas > 0) {
                double costo = horas * p.getTarifaHora();
                etiquetaCosto.setText(String.format("Costo estimado: $%.2f  (%.1f h × $%.0f/h)",
                    costo, horas, p.getTarifaHora()));
                etiquetaCosto.setForeground(new Color(34, 197, 94));
            }
        } catch (NumberFormatException e) {
            etiquetaCosto.setText("Costo estimado: —");
            etiquetaCosto.setForeground(TEXT_MUTED);
        }
    }

    /**
     * Llena comboArtista, comboProductor y comboCabina con datos del servicio.
     * Mapea a: perfil_artista, productorv1, cabina.
     */
    public void cargarCombos() {
        artistas.clear();
        productores.clear();
        cabinas.clear();

        // perfil_artista — datos de ejemplo hasta tener backend
        artistas.add(new Artista("Reggaeton",  1, "Bad Bunny", "badbunny@mail.com",  "0000000001"));
        artistas.add(new Artista("Urbano",     2, "Karol G",   "karolg@mail.com",    "0000000002"));
        artistas.add(new Artista("Pop / Rock", 3, "Shakira",   "shakira@mail.com",   "0000000003"));

        // productorv1
        productores.add(new Productor("Mezcla",        120.0, 1, "Carlos Vives",     "cvives@mail.com",   "3001234567"));
        productores.add(new Productor("Masterización", 95.0,  2, "Andrés Torres",    "atorres@mail.com",  "3109876543"));
        productores.add(new Productor("Composición",   150.0, 3, "Mauricio Rengifo", "mrengifo@mail.com", "3154561234"));

        // cabina (nombre_cabina)
        cabinas.add("Cabina A");
        cabinas.add("Cabina B");
        cabinas.add("Cabina C — Mastering");
    }

    private void inicializarDatos() {
        sesionesData.add(new Sesion(1, LocalDate.of(2025, 3, 10), 3.0, artistas.get(0), productores.get(0), 360.0));
        sesionesData.add(new Sesion(2, LocalDate.of(2025, 4, 22), 5.5, artistas.get(1), productores.get(2), 825.0));
        sesionesData.add(new Sesion(3, LocalDate.of(2025, 5,  1), 2.0, artistas.get(2), productores.get(1), 190.0));
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

        JLabel titulo = label("Sesiones", new Font("Segoe UI", Font.BOLD, 26), TEXT_PRIMARY);
        JLabel sub    = label("Sesiones de grabación, cabinas y agenda",
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
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar sesión...");
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrarTabla(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrarTabla(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarTabla(); }
        });

        ModernUI.RoundedButton btnNuevo = new ModernUI.RoundedButton("+ Nueva sesión", true);
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
        refrescarTabla(sesionesData);

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

        ModernUI.RoundedButton btnEditar   = new ModernUI.RoundedButton("Editar",      false);
        ModernUI.RoundedButton btnEliminar = new ModernUI.RoundedButton("Eliminar",    false);
        ModernUI.RoundedButton btnRefresh  = new ModernUI.RoundedButton("↻ Refrescar", false);
        btnEliminar.setForeground(new Color(255, 80, 120));

        btnEditar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { toast("Selecciona una sesión primero", MainFrame.ToastType.INFO); return; }
            abrirFormulario(fila);
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { toast("Selecciona una sesión primero", MainFrame.ToastType.INFO); return; }
            String id = (String) tableModel.getValueAt(fila, 0);
            int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la sesión #" + id + "?", "Z-One — Confirmar", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) eliminarSesion(fila);
        });

        btnRefresh.addActionListener(e -> {
            txtBuscar.setText("");
            refrescarTabla(sesionesData);
            toast("Lista actualizada", MainFrame.ToastType.INFO);
        });

        acciones.add(btnEditar);
        acciones.add(btnEliminar);
        acciones.add(btnRefresh);
        return acciones;
    }

    // ─── Tabla ────────────────────────────────────────────────────────────────────

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

        // ID | Nombre sesión | Fecha | H.inicio | H.fin | Artista | Productor | Estado
        int[] anchos = {45, 140, 90, 75, 75, 130, 130, 90};
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
                if (col == 0) lbl.setForeground(TEXT_MUTED);
                // estado_session con color según valor
                if (col == 7) {
                    String est = val == null ? "" : val.toString();
                    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                    lbl.setForeground(switch (est) {
                        case "Finalizada"  -> new Color(34, 197, 94);
                        case "En curso"    -> new Color(250, 180, 40);
                        case "Cancelada"   -> new Color(255, 80, 120);
                        default            -> new Color(6, 182, 212);   // Programada
                    });
                }
                return lbl;
            }
        });
    }

    /**
     * Rellena el DefaultTableModel con los campos de session_grabacion.
     * Los campos hora_inicio / hora_fin se almacenan como String (VARCHAR2 en DB).
     * duracion se deriva localmente para mostrar; en DB es DATE.
     */
    private void refrescarTabla(List<Sesion> datos) {
        tableModel.setRowCount(0);
        for (Sesion s : datos) {
            tableModel.addRow(new Object[]{
                String.format("%03d", s.getIdentificador()),   // id_session
                "Sesión #" + s.getIdentificador(),             // nombre_sesion (placeholder hasta backend)
                s.getFechaRealizacion().format(FMT_DATE),      // fecha
                "09:00",                                       // hora_inicio (placeholder)
                "12:00",                                       // hora_fin    (placeholder)
                s.getArtista().getNombre(),                    // artista_FKv5
                s.getProductor().getNombre(),                  // productos_FK
                "Programada"                                   // estado_session (placeholder)
            });
        }
    }

    private void filtrarTabla() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        if (texto.isEmpty()) { refrescarTabla(sesionesData); return; }
        List<Sesion> filtrado = new ArrayList<>();
        for (Sesion s : sesionesData)
            if (s.getArtista().getNombre().toLowerCase().contains(texto)
                || s.getProductor().getNombre().toLowerCase().contains(texto)
                || s.getFechaRealizacion().format(FMT_DATE).contains(texto))
                filtrado.add(s);
        refrescarTabla(filtrado);
    }

    private void eliminarSesion(int filaTabla) {
        int id = Integer.parseInt((String) tableModel.getValueAt(filaTabla, 0));
        sesionesData.removeIf(s -> s.getIdentificador() == id);
        refrescarTabla(sesionesData);
        toast("Sesión eliminada correctamente", MainFrame.ToastType.SUCCESS);
    }

    // ─── Diálogo crear / editar ──────────────────────────────────────────────────

    private void abrirFormulario(Integer filaEditar) {
        boolean esEdicion = (filaEditar != null);
        Sesion se = null;
        if (esEdicion) {
            int id = Integer.parseInt((String) tableModel.getValueAt(filaEditar, 0));
            se = sesionesData.stream().filter(s -> s.getIdentificador() == id).findFirst().orElse(null);
        }
        final Sesion sesionEdit = se;

        JDialog dlg = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            esEdicion ? "Editar sesión" : "Nueva sesión", true);
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

        JLabel lblTitulo = label(esEdicion ? "Editar sesión" : "Nueva sesión",
                                 new Font("Segoe UI", Font.BOLD, 20), TEXT_PRIMARY);
        lblTitulo.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(24));

        // ── nombre_sesion ──
        JTextField campoNombre = campoTexto(sesionEdit != null ? "Sesión #" + sesionEdit.getIdentificador() : "");
        campoNombre.putClientProperty("JTextField.placeholderText", "Nombre de la sesión");

        // ── fecha (DATE) ──
        JTextField campoFecha = campoTexto(sesionEdit != null
            ? sesionEdit.getFechaRealizacion().format(FMT_DATE) : "");
        campoFecha.putClientProperty("JTextField.placeholderText", "dd/MM/yyyy");

        // ── hora_inicio / hora_fin (VARCHAR2) ──
        JTextField campoHoraInicio = campoTexto("09:00");
        JTextField campoHoraFin    = campoTexto("12:00");
        campoHoraInicio.putClientProperty("JTextField.placeholderText", "HH:mm");
        campoHoraFin.putClientProperty("JTextField.placeholderText",    "HH:mm");

        // ── duracion derivada (para calcularCosto) ──
        JTextField campoDuracion = campoTexto(sesionEdit != null
            ? String.valueOf(sesionEdit.getDuracionHoras()) : "");
        campoDuracion.putClientProperty("JTextField.placeholderText", "Horas (ej: 3.5)");

        // ── observaciones (VARCHAR2 100) ──
        JTextField campoObs = campoTexto("");
        campoObs.putClientProperty("JTextField.placeholderText", "Observaciones opcionales");

        // ── estado_session (combo) ──
        JComboBox<String> comboEstado = crearComboString(
            new String[]{"Programada", "En curso", "Finalizada", "Cancelada"}, "Programada");

        // ── session_grabacion_cabina_FK (combo cabina) ──
        JComboBox<String> comboCabina = crearComboString(
            cabinas.toArray(new String[0]), cabinas.get(0));

        // ── artista_FKv5 (combo Artista) ──
        JComboBox<Artista> comboArtista = new JComboBox<>(artistas.toArray(new Artista[0]));
        comboArtista.setRenderer(comboRenderer(a -> a instanceof Artista ar ? ar.getNombre() : ""));
        estilizarComboBox(comboArtista);
        if (sesionEdit != null) comboArtista.setSelectedItem(sesionEdit.getArtista());

        // ── productos_FK (combo Productor) ──
        JComboBox<Productor> comboProductor = new JComboBox<>(productores.toArray(new Productor[0]));
        comboProductor.setRenderer(comboRenderer(p -> p instanceof Productor pr ? pr.getNombre() : ""));
        estilizarComboBox(comboProductor);
        if (sesionEdit != null) comboProductor.setSelectedItem(sesionEdit.getProductor());

        // ── etiquetaCosto (muestra duracion × tarifa_hora) ──
        JLabel etiquetaCosto = new JLabel("Costo estimado: —");
        etiquetaCosto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        etiquetaCosto.setForeground(TEXT_MUTED);
        etiquetaCosto.setAlignmentX(LEFT_ALIGNMENT);
        if (sesionEdit != null)
            etiquetaCosto.setText(String.format("Costo estimado: $%.2f", sesionEdit.getCostoTotal()));

        // Auto-recalcular al cambiar duración o productor
        campoDuracion.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { calcularYMostrarCosto(comboProductor, campoDuracion, etiquetaCosto); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { calcularYMostrarCosto(comboProductor, campoDuracion, etiquetaCosto); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calcularYMostrarCosto(comboProductor, campoDuracion, etiquetaCosto); }
        });
        comboProductor.addActionListener(e -> calcularYMostrarCosto(comboProductor, campoDuracion, etiquetaCosto));

        // ── Agregar campos al panel (orden = columnas de session_grabacion) ──
        panel.add(fila("Nombre de sesión *",        campoNombre));      panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Fecha * (dd/MM/yyyy)",       campoFecha));       panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Hora inicio (HH:mm)",        campoHoraInicio));  panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Hora fin (HH:mm)",           campoHoraFin));     panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Duración (h) *",             campoDuracion));    panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Estado sesión",              comboEstado));      panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Cabina",                     comboCabina));      panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Artista *",                  comboArtista));     panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Productor *",                comboProductor));   panel.add(Box.createVerticalStrut(12));
        panel.add(fila("Observaciones",              campoObs));         panel.add(Box.createVerticalStrut(16));
        panel.add(etiquetaCosto);                                        panel.add(Box.createVerticalStrut(24));

        // ── Botones ──
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        ModernUI.RoundedButton btnCancelar = new ModernUI.RoundedButton("Cancelar", false);
        ModernUI.RoundedButton btnGuardar  = new ModernUI.RoundedButton(
            esEdicion ? "Guardar cambios" : "Crear sesión", true);

        btnCancelar.addActionListener(e -> dlg.dispose());

        btnGuardar.addActionListener(e -> {
            String    nombre    = campoNombre.getText().trim();
            String    fechaStr  = campoFecha.getText().trim();
            String    durStr    = campoDuracion.getText().trim();
            Artista   artista   = (Artista)   comboArtista.getSelectedItem();
            Productor productor = (Productor) comboProductor.getSelectedItem();

            if (nombre.isEmpty())    { toast("El nombre de sesión es obligatorio", MainFrame.ToastType.ERROR); return; }
            if (fechaStr.isEmpty())  { toast("La fecha es obligatoria",            MainFrame.ToastType.ERROR); return; }
            if (durStr.isEmpty())    { toast("La duración es obligatoria",         MainFrame.ToastType.ERROR); return; }
            if (artista == null)     { toast("Selecciona un artista",              MainFrame.ToastType.ERROR); return; }
            if (productor == null)   { toast("Selecciona un productor",            MainFrame.ToastType.ERROR); return; }

            LocalDate fecha;
            try   { fecha = LocalDate.parse(fechaStr, FMT_DATE); }
            catch (DateTimeParseException ex) {
                toast("Formato de fecha inválido (dd/MM/yyyy)", MainFrame.ToastType.ERROR); return;
            }

            double duracion;
            try   { duracion = Double.parseDouble(durStr); }
            catch (NumberFormatException ex) {
                toast("La duración debe ser un número", MainFrame.ToastType.ERROR); return;
            }

            double costo = duracion * productor.getTarifaHora();

            if (esEdicion && sesionEdit != null) {
                sesionEdit.setFechaRealizacion(fecha);
                sesionEdit.setDuracionHoras(duracion);
                sesionEdit.setArtista(artista);
                sesionEdit.setProductor(productor);
                sesionEdit.setCostoTotal(costo);
                toast("Sesión actualizada correctamente", MainFrame.ToastType.SUCCESS);
            } else {
                sesionesData.add(new Sesion(nextId++, fecha, duracion, artista, productor, costo));
                toast("Sesión creada correctamente", MainFrame.ToastType.SUCCESS);
            }

            refrescarTabla(sesionesData);
            dlg.dispose();
        });

        btnRow.add(btnCancelar);
        btnRow.add(btnGuardar);
        panel.add(btnRow);

        dlg.setContentPane(panel);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(480, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ─── Helpers de UI ───────────────────────────────────────────────────────────

    /** Renderer genérico para combos de objetos de dominio. */
    private <T> DefaultListCellRenderer comboRenderer(java.util.function.Function<Object, String> textFn) {
        return new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean sel, boolean foc) {
                JLabel lbl = new JLabel(textFn.apply(v));
                lbl.setBackground(sel && i != -1 ? PURPLE : BG_FIELD);
                lbl.setForeground(TEXT_PRIMARY);
                lbl.setBorder(new EmptyBorder(7, 12, 7, 12));
                lbl.setOpaque(true);
                return lbl;
            }
        };
    }

    /** Combo de Strings con estilo dark. */
    private JComboBox<String> crearComboString(String[] opciones, String seleccion) {
        JComboBox<String> cb = new JComboBox<>(opciones) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
        cb.setOpaque(false);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setForeground(TEXT_PRIMARY);
        cb.setBackground(BG_FIELD);
        cb.setMaximumRowCount(6);
        cb.setFocusable(false);
        cb.setBorder(new EmptyBorder(0, 0, 0, 0));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean sel, boolean foc) {
                JLabel lbl = new JLabel(v == null ? "" : v.toString());
                lbl.setBackground(i == -1 ? BG_FIELD : (sel ? PURPLE : BG_FIELD));
                lbl.setForeground(TEXT_PRIMARY);
                lbl.setBorder(new EmptyBorder(7, 12, 7, 12));
                lbl.setOpaque(true);
                return lbl;
            }
        });
        estilizarPopup(cb);
        return cb;
    }

    private <T> void estilizarComboBox(JComboBox<T> cb) {
        cb.setEditable(false);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setForeground(TEXT_PRIMARY);
        cb.setBackground(BG_FIELD);
        cb.setOpaque(false);
        cb.setMaximumRowCount(6);
        cb.setFocusable(false);
        cb.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 92, 246, 70), 1, true),
            new EmptyBorder(6, 4, 6, 4)
        ));
        estilizarPopup(cb);
    }

    private void estilizarPopup(JComboBox<?> cb) {
        cb.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                Object popup = cb.getUI().getAccessibleChild(cb, 0);
                if (popup instanceof JComponent jp) {
                    jp.setBorder(BorderFactory.createLineBorder(new Color(139, 92, 246, 120), 1));
                    for (Component c : jp.getComponents())
                        if (c instanceof JScrollPane sp) {
                            sp.getViewport().setBackground(BG_FIELD);
                            sp.setBorder(BorderFactory.createEmptyBorder());
                        }
                }
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });
    }

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