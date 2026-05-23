package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static view.ModernUI.*;

/**
 * Panel del catálogo musical.
 * Adaptado al modelo entidad-relación:
 *   cancion (id_cancion, titulo, genero, bpm, fecha_composicion,
 *            fecha_compilacion, idiomas, estado_cancion)
 *   perfil_artista (Id_artista, nombre_artista, genero_musical, ...)
 *   formato (id_formato, titulo, año, tipo_proyecto)
 *
 * Permite explorar canciones filtradas por género y búsqueda de texto,
 * y agregar nuevos géneros al combo desplegable en tiempo de ejecución.
 */
public class formCatalogo extends JPanel {

    // ── Columnas de tabla ─────────────────────────────────────────────
    private static final String[] COLUMNAS_TABLA = {"ID", "Título", "Artista", "Género", "BPM", "Idioma", "Estado"};
    private static final int[]    ANCHOS_COLUMNAS = {50, 200, 160, 120, 60, 100, 90};
    private static final int      COL_GENERO = 3;

    // ── Layout ────────────────────────────────────────────────────────
    private static final int FILA_ALTURA        = 40;
    private static final int ANCHO_BUSCADOR     = 220;
    private static final int ALTO_CONTROL       = 36;
    private static final int ANCHO_COMBO_GENERO = 160;

    // ── Colores ───────────────────────────────────────────────────────
    private static final Color COLOR_FONDO        = new Color(18, 18, 40);
    private static final Color COLOR_FONDO_CAMPO  = new Color(24, 24, 52);
    private static final Color COLOR_MORADO       = new Color(139, 92, 246);
    private static final Color COLOR_EXITO        = new Color(34, 197, 94);
    private static final Color COLOR_ADVERTENCIA  = new Color(250, 180, 40);
    private static final Color COLOR_INFO         = new Color(6, 182, 212);
    private static final Color COLOR_FILA_IMPAR   = new Color(22, 22, 50);
    private static final Color COLOR_SELECCION    = new Color(139, 92, 246, 60);
    private static final Color COLOR_ENCABEZADO   = new Color(13, 13, 30);
    private static final Color COLOR_BORDE_TABLA  = new Color(139, 92, 246, 50);
    private static final Color COLOR_BORDE_BUS    = new Color(139, 92, 246, 80);
    private static final Color COLOR_BORDE_POPUP  = new Color(139, 92, 246, 120);
    private static final Color COLOR_COMBO_BORDE  = new Color(139, 92, 246, 90);
    private static final Color COLOR_FONDO_BUS    = new Color(20, 20, 45);

    // ── Fuentes ───────────────────────────────────────────────────────
    private static final Font FUENTE_TITULO     = new Font("Segoe UI", Font.BOLD,  26);
    private static final Font FUENTE_SUBTITULO  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_CUERPO     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_ETIQUETA   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FUENTE_ENCABEZADO = new Font("Segoe UI", Font.BOLD,  11);

    // ── Estado ────────────────────────────────────────────────────────
    /** Géneros cargados en runtime (se pueden agregar desde la UI) */
    private final List<String>         generosDisponibles = new ArrayList<>();
    /** Filas mostradas en la tabla: cada Object[] = {id, titulo, artista, genero, bpm, idioma, estado} */
    private final List<Object[]>       filasActuales      = new ArrayList<>();

    private DefaultTableModel          modeloTabla;
    private JTable                     tabla;
    private JTextField                 campoBusqueda;
    private JComboBox<String>          comboGenero;
    private JLabel                     etiquetaConteo;
    private JPanel                     panelChips;

    // ── Géneros fijos iniciales ───────────────────────────────────────
    private static final String GENERO_TODOS = "[ Todos ]";

    // ── Constructor ───────────────────────────────────────────────────
    public formCatalogo() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        // Géneros base
        generosDisponibles.add(GENERO_TODOS);
        generosDisponibles.addAll(List.of("Vallenato", "Pop", "Reggaeton", "Rock", "Urbano", "Salsa", "Cumbia"));

        add(construirEncabezado(),   BorderLayout.NORTH);
        add(construirTarjetaTabla(), BorderLayout.CENTER);

        // Cargar datos (intentar BD, si falla usar demo)
        cargarDatos();
    }

    // ================================================================
    // ENCABEZADO: títulos + barra de filtros + sección agregar género
    // ================================================================
    private JPanel construirEncabezado() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(construirTitulos());
        panel.add(Box.createVerticalStrut(12));
        panel.add(construirBarraFiltros());
        panel.add(Box.createVerticalStrut(8));
        panel.add(construirSeccionAgregarGenero());
        return panel;
    }

    private JPanel construirTitulos() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel titulo    = etiqueta("Catálogo musical", FUENTE_TITULO, TEXT_PRIMARY);
        JLabel subtitulo = etiqueta("Canciones registradas · filtro por género y búsqueda libre", FUENTE_SUBTITULO, TEXT_MUTED);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitulo);
        return panel;
    }

    private JPanel construirBarraFiltros() {
        JPanel barra = new JPanel(new BorderLayout(12, 0));
        barra.setOpaque(false);

        // Izquierda: combo género
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izquierda.setOpaque(false);
        izquierda.add(etiqueta("Filtrar por género:", FUENTE_CUERPO, TEXT_MUTED));
        comboGenero = construirComboGenero();
        izquierda.add(comboGenero);

        // Derecha: buscar + conteo
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derecha.setOpaque(false);
        etiquetaConteo = etiqueta("", FUENTE_ETIQUETA, TEXT_MUTED);
        campoBusqueda  = construirCampoBusqueda();
        derecha.add(etiqueta("Buscar:", FUENTE_CUERPO, TEXT_MUTED));
        derecha.add(campoBusqueda);
        derecha.add(etiquetaConteo);

        barra.add(izquierda, BorderLayout.WEST);
        barra.add(derecha,   BorderLayout.EAST);
        return barra;
    }

    // ================================================================
    // SECCIÓN AGREGAR GÉNERO  ← nueva funcionalidad
    // ================================================================
    private JPanel construirSeccionAgregarGenero() {
        // Contenedor principal con borde suave
        JPanel contenedor = new JPanel();
        contenedor.setOpaque(true);
        contenedor.setBackground(new Color(24, 24, 52, 180));
        contenedor.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 92, 246, 50), 1, true),
            new EmptyBorder(10, 14, 10, 14)));
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setAlignmentX(LEFT_ALIGNMENT);

        // — Fila superior: campo + botón —
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblTitulo = etiqueta("➕  Agregar género musical:", FUENTE_CUERPO, TEXT_SECONDARY);
        JTextField txtNuevoGenero = construirCampoGenero();
        JButton    btnAgregar     = construirBotonAgregar(txtNuevoGenero);

        fila.add(lblTitulo);
        fila.add(txtNuevoGenero);
        fila.add(btnAgregar);

        // — Fila inferior: chips de géneros actuales —
        panelChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panelChips.setOpaque(false);
        panelChips.setAlignmentX(LEFT_ALIGNMENT);
        refrescarChips();

        contenedor.add(fila);
        contenedor.add(Box.createVerticalStrut(6));
        contenedor.add(panelChips);

        // Wrapper para que no se estire al ancho completo
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contenedor, BorderLayout.WEST);
        return wrapper;
    }

    private JTextField construirCampoGenero() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(180, ALTO_CONTROL));
        tf.setFont(FUENTE_CUERPO);
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(COLOR_FONDO_BUS);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_COMBO_BORDE, 1, true),
            new EmptyBorder(6, 10, 6, 10)));
        tf.putClientProperty("JTextField.placeholderText", "Ej: Jazz, Cumbia...");
        return tf;
    }

    private JButton construirBotonAgregar(JTextField txtNuevoGenero) {
        ModernUI.RoundedButton btn = new ModernUI.RoundedButton("Agregar", true);
        btn.setPreferredSize(new Dimension(100, ALTO_CONTROL));

        ActionListener accion = e -> {
            String nuevoGenero = txtNuevoGenero.getText().trim();

            // Validar vacío
            if (nuevoGenero.isEmpty()) {
                MainFrame.showToast("Escribe un nombre de género primero", MainFrame.ToastType.ERROR);
                txtNuevoGenero.requestFocus();
                return;
            }

            // Validar duplicado (ignorando mayúsculas)
            boolean yaExiste = generosDisponibles.stream()
                .anyMatch(g -> g.equalsIgnoreCase(nuevoGenero));
            if (yaExiste) {
                MainFrame.showToast("El género '" + nuevoGenero + "' ya existe", MainFrame.ToastType.ERROR);
                return;
            }

            // Agregar al modelo del combo y a la lista interna
            generosDisponibles.add(nuevoGenero);
            comboGenero.addItem(nuevoGenero);

            // Actualizar chips visuales
            refrescarChips();

            // Seleccionar el género recién agregado en el combo
            comboGenero.setSelectedItem(nuevoGenero);

            txtNuevoGenero.setText("");
            MainFrame.showToast("Género '" + nuevoGenero + "' agregado al filtro", MainFrame.ToastType.SUCCESS);
        };

        btn.addActionListener(accion);

        // También activar con Enter desde el campo de texto
        txtNuevoGenero.addActionListener(accion);

        return btn;
    }

    /** Redibuja los chips de género debajo del campo de agregar */
    private void refrescarChips() {
        panelChips.removeAll();
        JLabel lbl = etiqueta("Géneros activos: ", FUENTE_ETIQUETA, TEXT_MUTED);
        panelChips.add(lbl);

        for (String genero : generosDisponibles) {
            if (genero.equals(GENERO_TODOS)) continue;
            panelChips.add(crearChipGenero(genero));
        }
        panelChips.revalidate();
        panelChips.repaint();
    }

    // ================================================================
    // COMPONENTES DE FILTRO
    // ================================================================
    private JTextField construirCampoBusqueda() {
        JTextField campo = new JTextField();
        campo.setPreferredSize(new Dimension(ANCHO_BUSCADOR, ALTO_CONTROL));
        campo.setFont(FUENTE_CUERPO);
        campo.setForeground(TEXT_PRIMARY);
        campo.setBackground(COLOR_FONDO_BUS);
        campo.setCaretColor(TEXT_PRIMARY);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE_BUS, 1, true),
            new EmptyBorder(6, 12, 6, 12)));
        campo.putClientProperty("JTextField.placeholderText", "Título, artista, género...");
        campo.getDocument().addDocumentListener(docListener(this::aplicarFiltros));
        return campo;
    }

    private JComboBox<String> construirComboGenero() {
        JComboBox<String> combo = new JComboBox<>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_FONDO_CAMPO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_COMBO_BORDE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        // Poblar con géneros iniciales
        for (String g : generosDisponibles) combo.addItem(g);

        combo.setPreferredSize(new Dimension(ANCHO_COMBO_GENERO, ALTO_CONTROL));
        combo.setEditable(false);
        combo.setOpaque(false);
        combo.setFont(FUENTE_CUERPO);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(COLOR_FONDO_CAMPO);
        combo.setMaximumRowCount(12);
        combo.setFocusable(false);
        combo.setBorder(new EmptyBorder(0, 0, 0, 0));
        combo.setRenderer(rendererComboString());
        combo.addActionListener(e -> aplicarFiltros());
        registrarEstilizadorPopup(combo);
        return combo;
    }

    // ================================================================
    // TABLA
    // ================================================================
    private ModernUI.CardPanel construirTarjetaTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS_TABLA, 0) {
            @Override public boolean isCellEditable(int f, int c) { return false; }
        };

        tabla = new JTable(modeloTabla);
        aplicarEstiloTabla();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setBackground(COLOR_FONDO);

        ModernUI.CardPanel tarjeta = new ModernUI.CardPanel(16);
        tarjeta.setLayout(new BorderLayout());
        tarjeta.add(scroll, BorderLayout.CENTER);
        return tarjeta;
    }

    private void aplicarEstiloTabla() {
        tabla.setOpaque(false);
        tabla.setBackground(COLOR_FONDO);
        tabla.setForeground(TEXT_PRIMARY);
        tabla.setFont(FUENTE_CUERPO);
        tabla.setRowHeight(FILA_ALTURA);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(COLOR_SELECCION);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFocusable(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setBackground(COLOR_ENCABEZADO);
        encabezado.setForeground(TEXT_MUTED);
        encabezado.setFont(FUENTE_ENCABEZADO);
        encabezado.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE_TABLA));
        encabezado.setReorderingAllowed(false);

        // Ocultar columna ID
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);

        for (int i = 1; i < ANCHOS_COLUMNAS.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(ANCHOS_COLUMNAS[i]);
        }
        tabla.setDefaultRenderer(Object.class, crearRendererCeldas());
    }

    private DefaultTableCellRenderer crearRendererCeldas() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object valor, boolean sel, boolean foco, int fila, int col) {
                JLabel celda = (JLabel) super.getTableCellRendererComponent(t, valor, sel, foco, fila, col);
                celda.setBorder(new EmptyBorder(0, 14, 0, 14));
                celda.setOpaque(true);
                celda.setBackground(sel ? COLOR_SELECCION
                                       : (fila % 2 == 0 ? COLOR_FONDO : COLOR_FILA_IMPAR));
                if (col == COL_GENERO) {
                    celda.setForeground(colorChip(valor == null ? "" : valor.toString()));
                    celda.setFont(FUENTE_CUERPO.deriveFont(Font.BOLD));
                } else {
                    celda.setForeground(TEXT_PRIMARY);
                    celda.setFont(FUENTE_CUERPO);
                }
                return celda;
            }
        };
    }

    // ================================================================
    // CHIPS DE GÉNERO (barra inferior y sección agregar)
    // ================================================================
    private JLabel crearChipGenero(String genero) {
        Color c = colorChip(genero);
        JLabel chip = new JLabel(genero);
        chip.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chip.setForeground(c);
        chip.setOpaque(true);
        chip.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue(), 25));
        chip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(c.getRed(), c.getGreen(), c.getBlue(), 80), 1, true),
            new EmptyBorder(3, 8, 3, 8)));
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chip.setToolTipText("Filtrar por " + genero);
        chip.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                comboGenero.setSelectedItem(genero);
            }
        });
        return chip;
    }

    private Color colorChip(String genero) {
        return switch (genero.toLowerCase()) {
            case "vallenato" -> COLOR_EXITO;
            case "pop"       -> COLOR_INFO;
            case "reggaeton" -> COLOR_MORADO;
            case "urbano"    -> COLOR_ADVERTENCIA;
            case "rock"      -> new Color(255, 100, 100);
            case "salsa"     -> new Color(255, 165, 0);
            case "cumbia"    -> new Color(180, 100, 255);
            default          -> TEXT_MUTED;
        };
    }

    // ================================================================
    // CARGA DE DATOS (BD → demo si falla)
    // ================================================================
    private void cargarDatos() {
        try {
            cargarDesdeBD();
        } catch (Exception ex) {
            System.err.println("[formCatalogo] BD no disponible, usando demo: " + ex.getMessage());
            cargarDatosDemo();
        }
    }

    /**
     * Consulta las tablas cancion y perfil_artista según el modelo ER.
     * Reemplaza la cadena de conexión con la de tu proyecto.
     */
    private void cargarDesdeBD() throws SQLException {
        // ── Ajusta estos datos según tu configuración ──────────────────
        String url    = "jdbc:oracle:thin:@localhost:1521:xe";
        String user   = "z_one";
        String pass   = "z_one";
        // ──────────────────────────────────────────────────────────────

        String sql = """
            SELECT c.id_cancion,
                   c.titulo,
                   NVL(a.nombre_artista, '—') AS artista,
                   c.genero,
                   c.bpm,
                   c.idiomas,
                   c.estado_cancion
            FROM   cancion c
            LEFT JOIN perfil_artista a ON a.Id_artista = c.Id_artista
            ORDER  BY c.titulo
            """;

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            filasActuales.clear();
            while (rs.next()) {
                String genero = rs.getString("genero");

                // Registrar género en el combo si es nuevo
                if (genero != null && !genero.isBlank()) {
                    boolean nuevo = generosDisponibles.stream()
                        .noneMatch(g -> g.equalsIgnoreCase(genero));
                    if (nuevo) {
                        generosDisponibles.add(genero);
                        comboGenero.addItem(genero);
                    }
                }

                filasActuales.add(new Object[]{
                    rs.getInt("id_cancion"),
                    rs.getString("titulo"),
                    rs.getString("artista"),
                    genero,
                    rs.getString("bpm"),
                    rs.getString("idiomas"),
                    rs.getString("estado_cancion")
                });
            }
        }
        refrescarTabla(filasActuales);
        refrescarChips();
    }

    /** Datos de ejemplo que reflejan la estructura de la BD */
    private void cargarDatosDemo() {
        filasActuales.clear();
        filasActuales.addAll(List.of(
            new Object[]{1,  "Pa' Mayte",          "Carlos Vives",   "Vallenato", "92",  "Español", "ACTIVO"},
            new Object[]{2,  "La Bicicleta",        "Carlos Vives",   "Vallenato", "98",  "Español", "ACTIVO"},
            new Object[]{3,  "Hips Don't Lie",      "Shakira",        "Pop",       "100", "Inglés",  "ACTIVO"},
            new Object[]{4,  "Waka Waka",           "Shakira",        "Pop",       "112", "Inglés",  "ACTIVO"},
            new Object[]{5,  "Me Porto Bonito",     "Bad Bunny",      "Reggaeton", "88",  "Español", "ACTIVO"},
            new Object[]{6,  "Tití Me Preguntó",    "Bad Bunny",      "Reggaeton", "96",  "Español", "ACTIVO"},
            new Object[]{7,  "PROVENZA",            "Karol G",        "Urbano",    "78",  "Español", "ACTIVO"},
            new Object[]{8,  "Cairo",               "Karol G",        "Urbano",    "82",  "Español", "ACTIVO"},
            new Object[]{9,  "Smooth",              "Carlos Santana", "Rock",      "104", "Inglés",  "ACTIVO"},
            new Object[]{10, "Maria Maria",         "Carlos Santana", "Rock",      "95",  "Inglés",  "ACTIVO"}
        ));
        refrescarTabla(filasActuales);
    }

    // ================================================================
    // FILTRADO Y REFRESCO DE TABLA
    // ================================================================
    private void aplicarFiltros() {
        String generoSel = (String) comboGenero.getSelectedItem();
        String texto     = campoBusqueda.getText().trim().toLowerCase();

        List<Object[]> resultado = filasActuales.stream()
            .filter(f -> generoCoincide(f, generoSel))
            .filter(f -> textoCoincide(f, texto))
            .toList();

        refrescarTabla(resultado);
    }

    private boolean generoCoincide(Object[] fila, String filtro) {
        if (filtro == null || filtro.equals(GENERO_TODOS)) return true;
        String generoFila = fila[3] == null ? "" : fila[3].toString();
        return generoFila.equalsIgnoreCase(filtro);
    }

    private boolean textoCoincide(Object[] fila, String texto) {
        if (texto.isEmpty()) return true;
        for (int i = 1; i < fila.length; i++) {
            if (fila[i] != null && fila[i].toString().toLowerCase().contains(texto)) return true;
        }
        return false;
    }

    private void refrescarTabla(List<Object[]> datos) {
        modeloTabla.setRowCount(0);
        datos.forEach(f -> modeloTabla.addRow(f));
        actualizarConteo(datos.size());
    }

    private void actualizarConteo(int total) {
        if (etiquetaConteo != null)
            etiquetaConteo.setText(total + " resultado" + (total != 1 ? "s" : ""));
    }

    // ================================================================
    // HELPERS DE COMPONENTES
    // ================================================================
    private JLabel etiqueta(String texto, Font fuente, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente);
        lbl.setForeground(color);
        return lbl;
    }

    private DefaultListCellRenderer rendererComboString() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista, Object valor, int idx, boolean sel, boolean foco) {
                JLabel celda = new JLabel(valor == null ? "" : valor.toString());
                celda.setBackground(idx == -1 ? COLOR_FONDO_CAMPO : sel ? COLOR_MORADO : COLOR_FONDO_CAMPO);
                celda.setForeground(TEXT_PRIMARY);
                celda.setBorder(new EmptyBorder(7, 12, 7, 12));
                celda.setOpaque(true);
                return celda;
            }
        };
    }

    private void registrarEstilizadorPopup(JComboBox<?> combo) {
        combo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                Object popup = combo.getUI().getAccessibleChild(combo, 0);
                if (!(popup instanceof JComponent jp)) return;
                jp.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_POPUP, 1));
                for (Component c : jp.getComponents()) {
                    if (c instanceof JScrollPane sp) {
                        sp.getViewport().setBackground(COLOR_FONDO_CAMPO);
                        sp.setBorder(BorderFactory.createEmptyBorder());
                    }
                }
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });
    }

    private DocumentListener docListener(Runnable accion) {
        return new DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { accion.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { accion.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { accion.run(); }
        };
    }
}