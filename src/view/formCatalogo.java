package view;

import model.Album;
import model.Artista;
import model.Cancion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static view.ModernUI.*;

/**
 * Panel del catálogo musical.
 * Permite explorar canciones filtradas por género y búsqueda de texto.
 *
 * Adaptado a los cambios del merge:
 *  - Artista: nuevo constructor completo (12 parámetros), getter renombrado
 *    getNombre() → getNombreArtista(), genero musical en getGeneroMusical().
 *  - ModernUI: paleta actualizada de morado a azul marino; se reemplazaron
 *    los colores locales por las constantes vigentes de ModernUI.
 */
public class formCatalogo extends JPanel {

    // ── Columnas de tabla ─────────────────────────────────────────────

    private static final String[] COLUMNAS_TABLA = {"Título", "Artista", "Álbum", "Género", "Duración"};
    private static final int[]    ANCHOS_COLUMNAS = {200, 160, 200, 120, 80};
    private static final int      COL_GENERO      = 3;

    // ── Géneros disponibles ───────────────────────────────────────────

    private static final String   GENERO_TODOS = "[ Todos ]";
    private static final String[] GENEROS = {
        GENERO_TODOS, "Vallenato", "Pop", "Reggaeton", "Rock", "Urbano", "Salsa", "Cumbia"
    };

    // ── Layout ────────────────────────────────────────────────────────

    private static final int FILA_ALTURA        = 40;
    private static final int ANCHO_BUSCADOR     = 260;
    private static final int ALTO_CONTROL       = 36;
    private static final int ANCHO_COMBO_GENERO = 160;

    // ── Colores (usando la paleta actualizada de ModernUI) ────────────

    // Fondos
    private static final Color COLOR_FONDO       = BG_DARK;           // azul muy oscuro
    private static final Color COLOR_FONDO_CAMPO = new Color(6, 38, 74);  // INPUT_BG de ModernUI
    private static final Color COLOR_FONDO_BUS   = new Color(4, 28, 58);

    // Acentos de género (adaptados a la nueva paleta azul)
    private static final Color COLOR_ACENTO      = PRIMARY;            // azul brillante
    private static final Color COLOR_EXITO       = SUCCESS;            // turquesa
    private static final Color COLOR_INFO        = ACCENT_CYAN;        // celeste eléctrico
    private static final Color COLOR_ADVERTENCIA = new Color(250, 180, 40); // amarillo (sin equivalente en ModernUI)
    private static final Color COLOR_ROCK        = ACCENT_PINK;        // turquesa rosado

    // Tabla
    private static final Color COLOR_FILA_IMPAR  = new Color(3, 22, 52);
    private static final Color COLOR_SELECCION   = new Color(1, 138, 190, 60);  // PRIMARY con alpha
    private static final Color COLOR_ENCABEZADO  = new Color(2, 15, 38);
    private static final Color COLOR_BORDE_TABLA = new Color(26, 72, 120, 80);  // BORDER más suave

    // Bordes de controles
    private static final Color COLOR_BORDE_BUS   = new Color(1, 138, 190, 100);
    private static final Color COLOR_BORDE_POPUP = new Color(1, 138, 190, 150);
    private static final Color COLOR_COMBO_BORDE = new Color(1, 138, 190, 110);

    // ── Fuentes ───────────────────────────────────────────────────────

    private static final Font FUENTE_TITULO     = FONT_TITLE;
    private static final Font FUENTE_SUBTITULO  = FONT_SUBTITLE;
    private static final Font FUENTE_CUERPO     = FONT_LABEL;
    private static final Font FUENTE_ETIQUETA   = FONT_SMALL;
    private static final Font FUENTE_ENCABEZADO = new Font("Segoe UI", Font.BOLD, 11);

    // ── Estado ────────────────────────────────────────────────────────

    private final List<Artista> artistas  = new ArrayList<>();
    private final List<Album>   albums    = new ArrayList<>();
    private final List<Cancion> canciones = new ArrayList<>();

    private DefaultTableModel  modeloTabla;
    private JTable             tabla;
    private JTextField         campoBusqueda;
    private JComboBox<String>  comboGenero;
    private JLabel             etiquetaConteo;

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

    /**
     * Crea artistas de prueba usando el constructor actualizado de Artista
     * (12 parámetros): idArtista, idUsuario, nombreArtista, nombreReal,
     * fechaNacimiento, genero, nacionalidad, generoMusical,
     * redesSociales, fechaFirma, estadoArtista, tipoArtista.
     */
    private void cargarDatosDemo() {
        Artista carlosVives   = new Artista(1, null, "Carlos Vives",   "Carlos Eduardo Vives Restrepo",
                                            LocalDate.of(1961, 8, 7),  "M", "Colombia",   "Vallenato",
                                            "@carlosvives",   LocalDate.of(2010, 1, 1),
                                            Artista.ESTADO_ACTIVO, Artista.TIPO_SOLISTA);

        Artista shakira       = new Artista(2, null, "Shakira",         "Shakira Isabel Mebarak Ripoll",
                                            LocalDate.of(1977, 2, 2),  "F", "Colombia",   "Pop",
                                            "@shakira",       LocalDate.of(2005, 3, 1),
                                            Artista.ESTADO_ACTIVO, Artista.TIPO_SOLISTA);

        Artista badBunny      = new Artista(3, null, "Bad Bunny",       "Benito Antonio Martínez Ocasio",
                                            LocalDate.of(1994, 3, 10), "M", "Puerto Rico", "Reggaeton",
                                            "@badbunny",      LocalDate.of(2018, 6, 1),
                                            Artista.ESTADO_ACTIVO, Artista.TIPO_SOLISTA);

        Artista karolG        = new Artista(4, null, "Karol G",         "Carolina Giraldo Navarro",
                                            LocalDate.of(1991, 2, 14), "F", "Colombia",   "Urbano",
                                            "@karolg",        LocalDate.of(2017, 1, 1),
                                            Artista.ESTADO_ACTIVO, Artista.TIPO_SOLISTA);

        Artista carlosSantana = new Artista(5, null, "Carlos Santana",  "Carlos Augusto Santana Alves",
                                            LocalDate.of(1947, 7, 20), "M", "México",     "Rock",
                                            "@santana",       LocalDate.of(1998, 9, 1),
                                            Artista.ESTADO_ACTIVO, Artista.TIPO_SOLISTA);

        artistas.addAll(List.of(carlosVives, shakira, badBunny, karolG, carlosSantana));

        Album laTierra    = new Album(1, "La Tierra de la Música", 2019, carlosVives,   new ArrayList<>());
        Album oralFixation= new Album(2, "Oral Fixation Vol. 2",  2006, shakira,        new ArrayList<>());
        Album un_verano   = new Album(3, "Un Verano Sin Ti",       2022, badBunny,      new ArrayList<>());
        Album mañana      = new Album(4, "Mañana Será Bonito",     2023, karolG,        new ArrayList<>());
        Album supernatural= new Album(5, "Supernatural",           1999, carlosSantana, new ArrayList<>());
        albums.addAll(List.of(laTierra, oralFixation, un_verano, mañana, supernatural));

        // generoMusical del artista se usa como género de la canción
        canciones.addAll(List.of(
            new Cancion(1,  "Pa' Mayte",          3.5, carlosVives.getGeneroMusical(),   carlosVives),
            new Cancion(2,  "La Bicicleta",        3.8, carlosVives.getGeneroMusical(),   carlosVives),
            new Cancion(3,  "Hips Don't Lie",      3.7, shakira.getGeneroMusical(),       shakira),
            new Cancion(4,  "Waka Waka",           3.5, shakira.getGeneroMusical(),       shakira),
            new Cancion(5,  "Me Porto Bonito",     3.2, badBunny.getGeneroMusical(),      badBunny),
            new Cancion(6,  "Tití Me Preguntó",    4.1, badBunny.getGeneroMusical(),      badBunny),
            new Cancion(7,  "PROVENZA",            3.9, karolG.getGeneroMusical(),        karolG),
            new Cancion(8,  "Cairo",               3.6, karolG.getGeneroMusical(),        karolG),
            new Cancion(9,  "Smooth",              4.5, carlosSantana.getGeneroMusical(), carlosSantana),
            new Cancion(10, "Maria Maria",         4.3, carlosSantana.getGeneroMusical(), carlosSantana)
        ));

        laTierra.agregarCancion(canciones.get(0));     laTierra.agregarCancion(canciones.get(1));
        oralFixation.agregarCancion(canciones.get(2)); oralFixation.agregarCancion(canciones.get(3));
        un_verano.agregarCancion(canciones.get(4));    un_verano.agregarCancion(canciones.get(5));
        mañana.agregarCancion(canciones.get(6));       mañana.agregarCancion(canciones.get(7));
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

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izquierda.setOpaque(false);
        comboGenero = construirComboGenero();
        izquierda.add(etiqueta("Filtrar por género:", FUENTE_CUERPO, TEXT_MUTED));
        izquierda.add(comboGenero);

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
        tarjeta.add(scroll,                BorderLayout.CENTER);
        tarjeta.add(construirPieTarjeta(), BorderLayout.SOUTH);
        return tarjeta;
    }

    private JPanel construirPieTarjeta() {
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setBorder(new EmptyBorder(12, 4, 4, 4));

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
        Color col = colorChipGenero(genero);
        JLabel chip = new JLabel(genero + " (" + conteo + ")");
        chip.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chip.setForeground(col);
        chip.setOpaque(true);
        chip.setBackground(new Color(col.getRed(), col.getGreen(), col.getBlue(), 25));
        chip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(col.getRed(), col.getGreen(), col.getBlue(), 80), 1, true),
            new EmptyBorder(3, 8, 3, 8)));
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chip.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                comboGenero.setSelectedItem(genero);
            }
        });
        return chip;
    }

    /** Asigna un color de acento a cada género, usando la paleta azul de ModernUI. */
    private Color colorChipGenero(String genero) {
        return switch (genero) {
            case "Vallenato" -> COLOR_EXITO;       // turquesa
            case "Pop"       -> COLOR_INFO;        // celeste eléctrico
            case "Reggaeton" -> PRIMARY_LIGHT;     // celeste claro
            case "Urbano"    -> COLOR_ADVERTENCIA; // amarillo
            case "Rock"      -> COLOR_ROCK;        // turquesa rosado
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
                    celda.setForeground(colorChipGenero(valor == null ? "" : valor.toString()));
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
                c.getArtista().getNombreArtista(),   // ← getNombreArtista() (antes getNombre())
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
            || cancion.getArtista().getNombreArtista().toLowerCase().contains(texto)  // ← getNombreArtista()
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
                celda.setBackground(indice == -1 ? COLOR_FONDO_CAMPO : sel ? PRIMARY : COLOR_FONDO_CAMPO);
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