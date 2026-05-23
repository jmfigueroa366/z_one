package view;

import model.Album;
import model.Artista;
import model.Cancion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static view.ModernUI.*;

/**
 * Panel del catálogo musical.
 * Permite explorar canciones filtradas por género y búsqueda de texto.
 */
public class formCatalogo extends JPanel {

    // ── Columnas de tabla ─────────────────────────────────────────────

    private static final String[] COLUMNAS_TABLA = {"Título", "Artista", "Álbum", "Género", "Duración"};
    private static final int[]    ANCHOS_COLUMNAS = {200, 160, 200, 120, 80};
    private static final int      COL_GENERO      = 3;

    // ── Géneros disponibles ───────────────────────────────────────────

    private static final String GENERO_TODOS = "[ Todos ]";
    private static final String[] GENEROS = {
        GENERO_TODOS, "Vallenato", "Pop", "Reggaeton", "Rock", "Urbano", "Salsa", "Cumbia"
    };

    // ── Layout ────────────────────────────────────────────────────────

    private static final int FILA_ALTURA       = 40;
    private static final int ANCHO_BUSCADOR    = 260;
    private static final int ALTO_CONTROL      = 36;
    private static final int ANCHO_COMBO_GENERO = 160;

    // ── Colores ───────────────────────────────────────────────────────

    private static final Color COLOR_FONDO       = new Color(18, 18, 40);
    private static final Color COLOR_FONDO_CAMPO = new Color(24, 24, 52);
    private static final Color COLOR_MORADO      = new Color(139, 92, 246);
    private static final Color COLOR_EXITO       = new Color(34, 197, 94);
    private static final Color COLOR_ADVERTENCIA = new Color(250, 180, 40);
    private static final Color COLOR_INFO        = new Color(6, 182, 212);
    private static final Color COLOR_FILA_IMPAR  = new Color(22, 22, 50);
    private static final Color COLOR_SELECCION   = new Color(139, 92, 246, 60);
    private static final Color COLOR_ENCABEZADO  = new Color(13, 13, 30);
    private static final Color COLOR_BORDE_TABLA = new Color(139, 92, 246, 50);
    private static final Color COLOR_BORDE_CAMPO = new Color(139, 92, 246, 70);
    private static final Color COLOR_BORDE_BUS   = new Color(139, 92, 246, 80);
    private static final Color COLOR_BORDE_POPUP = new Color(139, 92, 246, 120);
    private static final Color COLOR_COMBO_BORDE = new Color(139, 92, 246, 90);
    private static final Color COLOR_FONDO_BUS   = new Color(20, 20, 45);

    // ── Fuentes ───────────────────────────────────────────────────────

    private static final Font FUENTE_TITULO     = new Font("Segoe UI", Font.BOLD,  26);
    private static final Font FUENTE_SUBTITULO  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_CUERPO     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_ETIQUETA   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FUENTE_ENCABEZADO = new Font("Segoe UI", Font.BOLD,  11);

    // ── Estado ────────────────────────────────────────────────────────

    private final List<Artista>  artistas  = new ArrayList<>();
    private final List<Album>    albums    = new ArrayList<>();
    private final List<Cancion>  canciones = new ArrayList<>();

    private DefaultTableModel    modeloTabla;
    private JTable               tabla;
    private JTextField           campoBusqueda;
    private JComboBox<String>    comboGenero;
    private JLabel               etiquetaConteo;

    // ── Constructor ───────────────────────────────────────────────────

    public formCatalogo() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 0, 0));
        cargarDatosDemo();
        add(construirEncabezado(),   BorderLayout.NORTH);
        add(construirTarjetaTabla(), BorderLayout.CENTER);
    }

    // ── Datos demo ────────────────────────────────────────────────────

    private void cargarDatosDemo() {
        Artista carlosVives  = new Artista(1, "Carlos Vives",  "cvives@mail.com",  "3001234567", "Vallenato", "Colombia",    8, Artista.ESTADO_ACTIVO);
        Artista shakira      = new Artista(2, "Shakira",       "shakira@mail.com", "3009876543", "Pop",       "Colombia",   12, Artista.ESTADO_ACTIVO);
        Artista badBunny     = new Artista(3, "Bad Bunny",     "bb@mail.com",      "3004561234", "Reggaeton", "Puerto Rico", 20, Artista.ESTADO_ACTIVO);
        Artista karolG       = new Artista(4, "Karol G",       "kg@mail.com",      "3007894561", "Urbano",    "Colombia",   15, Artista.ESTADO_ACTIVO);
        Artista carlosSantana= new Artista(5, "Carlos Santana","cs@mail.com",      "3001112222", "Rock",      "México",     10, Artista.ESTADO_ACTIVO);
        artistas.addAll(List.of(carlosVives, shakira, badBunny, karolG, carlosSantana));

        Album laTierra   = new Album(1, "La Tierra de la Música", 2019, carlosVives,  new ArrayList<>());
        Album oralFixation= new Album(2, "Oral Fixation Vol. 2",  2006, shakira,      new ArrayList<>());
        Album un_verano  = new Album(3, "Un Verano Sin Ti",       2022, badBunny,     new ArrayList<>());
        Album mañana     = new Album(4, "Mañana Será Bonito",     2023, karolG,       new ArrayList<>());
        Album supernatural= new Album(5, "Supernatural",          1999, carlosSantana,new ArrayList<>());
        albums.addAll(List.of(laTierra, oralFixation, un_verano, mañana, supernatural));

        canciones.addAll(List.of(
            new Cancion(1,  "Pa' Mayte",             3.5,  "Vallenato", carlosVives),
            new Cancion(2,  "La Bicicleta",          3.8,  "Vallenato", carlosVives),
            new Cancion(3,  "Hips Don't Lie",        3.7,  "Pop",       shakira),
            new Cancion(4,  "Waka Waka",             3.5,  "Pop",       shakira),
            new Cancion(5,  "Me Porto Bonito",       3.2,  "Reggaeton", badBunny),
            new Cancion(6,  "Tití Me Preguntó",      4.1,  "Reggaeton", badBunny),
            new Cancion(7,  "PROVENZA",              3.9,  "Urbano",    karolG),
            new Cancion(8,  "Cairo",                 3.6,  "Urbano",    karolG),
            new Cancion(9,  "Smooth",                4.5,  "Rock",      carlosSantana),
            new Cancion(10, "Maria Maria",           4.3,  "Rock",      carlosSantana)
        ));

        // Asociar canciones a álbumes
        laTierra.agregarCancion(canciones.get(0));    laTierra.agregarCancion(canciones.get(1));
        oralFixation.agregarCancion(canciones.get(2)); oralFixation.agregarCancion(canciones.get(3));
        un_verano.agregarCancion(canciones.get(4));   un_verano.agregarCancion(canciones.get(5));
        mañana.agregarCancion(canciones.get(6));      mañana.agregarCancion(canciones.get(7));
        supernatural.agregarCancion(canciones.get(8)); supernatural.agregarCancion(canciones.get(9));
    }

    // ── Construcción de UI ────────────────────────────────────────────

    private JPanel construirEncabezado() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(construirTitulos());
        panel.add(construirBarraFiltros());
        return panel;
    }

    private JPanel construirTitulos() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titulo    = etiqueta("Catálogo musical", FUENTE_TITULO, TEXT_PRIMARY);
        JLabel subtitulo = etiqueta("Álbumes, canciones y géneros disponibles", FUENTE_SUBTITULO, TEXT_MUTED);
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
        barra.setBorder(new EmptyBorder(16, 0, 12, 0));

        // Lado izquierdo: etiqueta + combo de género
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izquierda.setOpaque(false);

        JLabel lblFiltro = etiqueta("Filtrar por género:", FUENTE_CUERPO, TEXT_MUTED);
        comboGenero = construirComboGenero();
        izquierda.add(lblFiltro);
        izquierda.add(comboGenero);

        // Lado derecho: buscador + etiqueta de conteo
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
        campo.putClientProperty("JTextField.placeholderText", "Título, artista o álbum...");
        campo.getDocument().addDocumentListener(docListener(this::aplicarFiltros));
        return campo;
    }

    private JComboBox<String> construirComboGenero() {
        JComboBox<String> combo = new JComboBox<>(GENEROS) {
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
        combo.setPreferredSize(new Dimension(ANCHO_COMBO_GENERO, ALTO_CONTROL));
        combo.setEditable(false);
        combo.setOpaque(false);
        combo.setFont(FUENTE_CUERPO);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(COLOR_FONDO_CAMPO);
        combo.setMaximumRowCount(8);
        combo.setFocusable(false);
        combo.setBorder(new EmptyBorder(0, 0, 0, 0));
        combo.setRenderer(rendererComboString());
        combo.addActionListener(e -> aplicarFiltros());
        registrarEstilizadorPopup(combo);
        return combo;
    }

    // ── Tarjeta de tabla ──────────────────────────────────────────────

    private ModernUI.CardPanel construirTarjetaTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS_TABLA, 0) {
            @Override public boolean isCellEditable(int fila, int col) { return false; }
        };
        refrescarTabla(canciones);

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
        tarjeta.add(scroll,                  BorderLayout.CENTER);
        tarjeta.add(construirPieTarjeta(),   BorderLayout.SOUTH);
        return tarjeta;
    }

    private JPanel construirPieTarjeta() {
        JPanel pie = new JPanel(new BorderLayout(0, 0));
        pie.setOpaque(false);
        pie.setBorder(new EmptyBorder(12, 4, 4, 4));

        // Chips de resumen por género
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        for (String genero : List.of("Vallenato", "Pop", "Reggaeton", "Urbano", "Rock")) {
            chips.add(crearChipGenero(genero));
        }

        pie.add(chips, BorderLayout.WEST);
        return pie;
    }

    private JLabel crearChipGenero(String genero) {
        long conteo = canciones.stream()
            .filter(c -> c.getGenero().equalsIgnoreCase(genero))
            .count();
        JLabel chip = new JLabel(genero + " (" + conteo + ")");
        chip.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chip.setForeground(colorChipGenero(genero));
        chip.setOpaque(true);
        chip.setBackground(new Color(
            colorChipGenero(genero).getRed(),
            colorChipGenero(genero).getGreen(),
            colorChipGenero(genero).getBlue(), 25));
        chip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(
                colorChipGenero(genero).getRed(),
                colorChipGenero(genero).getGreen(),
                colorChipGenero(genero).getBlue(), 80), 1, true),
            new EmptyBorder(3, 8, 3, 8)));
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chip.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                comboGenero.setSelectedItem(genero);
            }
        });
        return chip;
    }

    private Color colorChipGenero(String genero) {
        return switch (genero) {
            case "Vallenato" -> COLOR_EXITO;
            case "Pop"       -> COLOR_INFO;
            case "Reggaeton" -> COLOR_MORADO;
            case "Urbano"    -> COLOR_ADVERTENCIA;
            default          -> TEXT_MUTED;
        };
    }

    // ── Estilo de tabla ───────────────────────────────────────────────

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

        for (int i = 0; i < ANCHOS_COLUMNAS.length; i++) {
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
                celda.setBackground(sel ? COLOR_SELECCION : (fila % 2 == 0 ? COLOR_FONDO : COLOR_FILA_IMPAR));

                if (col == COL_GENERO) {
                    String genero = valor == null ? "" : valor.toString();
                    celda.setForeground(colorChipGenero(genero));
                    celda.setFont(FUENTE_CUERPO.deriveFont(Font.BOLD));
                } else {
                    celda.setForeground(TEXT_PRIMARY);
                    celda.setFont(FUENTE_CUERPO);
                }
                return celda;
            }
        };
    }

    // ── Datos de tabla ────────────────────────────────────────────────

    private void refrescarTabla(List<Cancion> datos) {
        modeloTabla.setRowCount(0);
        datos.forEach(c -> {
            String albumTitulo = buscarAlbumDeCancion(c);
            String duracion    = formatearDuracion(c.getDuracionMinutos());
            modeloTabla.addRow(new Object[]{
                c.getTitulo(),
                c.getArtista().getNombre(),
                albumTitulo,
                c.getGenero(),
                duracion
            });
        });
        actualizarConteo(datos.size());
    }

    private String buscarAlbumDeCancion(Cancion cancion) {
        return albums.stream()
            .filter(a -> a.getCanciones().contains(cancion))
            .map(Album::getTitulo)
            .findFirst()
            .orElse("—");
    }

    private String formatearDuracion(double minutos) {
        int min = (int) minutos;
        int seg = (int) Math.round((minutos - min) * 60);
        return String.format("%d:%02d", min, seg);
    }

    private void actualizarConteo(int total) {
        if (etiquetaConteo != null) {
            etiquetaConteo.setText(total + " resultado" + (total != 1 ? "s" : ""));
        }
    }

    // ── Filtrado ──────────────────────────────────────────────────────

    private void aplicarFiltros() {
        String generoSeleccionado = (String) comboGenero.getSelectedItem();
        String textoBusqueda      = campoBusqueda.getText().trim().toLowerCase();

        List<Cancion> resultado = canciones.stream()
            .filter(c -> generoCoincide(c, generoSeleccionado))
            .filter(c -> textoCoincide(c, textoBusqueda))
            .toList();

        refrescarTabla(resultado);
    }

    private boolean generoCoincide(Cancion cancion, String generoFiltro) {
        return generoFiltro == null
            || generoFiltro.equals(GENERO_TODOS)
            || cancion.getGenero().equalsIgnoreCase(generoFiltro);
    }

    private boolean textoCoincide(Cancion cancion, String texto) {
        if (texto.isEmpty()) return true;
        String albumTitulo = buscarAlbumDeCancion(cancion).toLowerCase();
        return cancion.getTitulo().toLowerCase().contains(texto)
            || cancion.getArtista().getNombre().toLowerCase().contains(texto)
            || albumTitulo.contains(texto)
            || cancion.getGenero().toLowerCase().contains(texto);
    }

    // ── Helpers de componentes ────────────────────────────────────────

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
                    JList<?> lista, Object valor, int indice, boolean sel, boolean foco) {
                JLabel celda = new JLabel(valor == null ? "" : valor.toString());
                celda.setBackground(indice == -1 ? COLOR_FONDO_CAMPO : sel ? COLOR_MORADO : COLOR_FONDO_CAMPO);
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