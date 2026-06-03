package view;

import css.SesionComponents;
import css.SesionComponents.BandaAnimada;
import css.SesionComponents.BtnFx;
<<<<<<< HEAD
import css.SesionComponents.CardCostoFx;
=======
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
import css.SesionComponents.ComboFx;
import css.SesionComponents.FieldFx;
import css.SesionComponents.LineaShimmer;
import css.SesionComponents.VuMeter;
<<<<<<< HEAD
import model.Artista;
import model.Productor;
=======
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
import model.Sesion;
import service.SesionServicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
<<<<<<< HEAD
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
=======
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d

import static css.SesionStyles.*;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

public class formSesion extends JPanel {

<<<<<<< HEAD
    private static final String[] ESTADOS = Sesion.ESTADOS_VALIDOS;

    // ── SERVICIO Y DATOS ────────────────────────────────────────────
    private final SesionServicio  sesionServicio;
    private final List<Artista>   artistas    = new ArrayList<>();
    private final List<Productor> productores = new ArrayList<>();
    private final List<String>    cabinas     = new ArrayList<>();
    private final List<Sesion>    sesiones    = new ArrayList<>();
=======
    // ── CATÁLOGOS EN MEMORIA (ID → Nombre) ─────────────────────────
    private final Map<Integer, String> mapaArtistas    = new LinkedHashMap<>();
    private final Map<Integer, String> mapaProductores = new LinkedHashMap<>();
    private final Map<Integer, String> mapaCabinas     = new LinkedHashMap<>();
    private final Map<Integer, String> mapaFases       = new LinkedHashMap<>();
    private final Map<Integer, String> mapaEstados     = new LinkedHashMap<>();
    private final Map<Integer, String> mapaCanciones   = new LinkedHashMap<>();

    // ── SERVICIO Y DATOS ────────────────────────────────────────────
    private final SesionServicio  sesionServicio;
    private final List<Sesion>    sesiones = new ArrayList<>();
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d

    // ── UI ──────────────────────────────────────────────────────────
    private ModernUI.RoundedTextField busqueda;
    private Sesion seleccionada;
    private boolean modoTarjetas = false;
    private CardLayout cardLayout;
    private JPanel vistaCentral;
    private JPanel grabacionesBox;
    private JPanel gridTarjetas;
    private ModernUI.RoundedButton btnVista;
    private final JLabel stTotal = new JLabel("0");
    private final JLabel stProg  = new JLabel("0");
    private final JLabel stCurso = new JLabel("0");
    private final JLabel stCosto = new JLabel("$0");
    private JPanel rankingBox;
    private JLabel resDuracion, resCabina, resProductor;
    private JScrollPane listaScroll;
    private JPanel listaCont;

    private Timer recTimer;
    private JLabel recLabel;

<<<<<<< HEAD
    // ── CONSTRUCTORES ───────────────────────────────────────────────
=======
    // ── CONSTRUCTOR ─────────────────────────────────────────────────
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
    public formSesion(SesionServicio sesionServicio) {
        this.sesionServicio = sesionServicio;
        setOpaque(true);
        setBackground(C_BG_DARK);
        setLayout(new BorderLayout());
<<<<<<< HEAD
        cargarCombos();
=======

        inicializarCatalogos();
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        cargarSesionesDesdeServicio();

        JPanel norte = new JPanel();
        norte.setOpaque(false);
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(headerPanel());
        norte.add(statsPanel());
        add(norte, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(13, 0));
        centro.setOpaque(false);
        centro.add(centroVistas(),  BorderLayout.CENTER);
        centro.add(panelLateral(), BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);
        aplicarFiltro();
    }

    public formSesion() { this(crearServicioPorDefecto()); }

    private static SesionServicio crearServicioPorDefecto() {
        try { return new SesionServicio(); }
        catch (Exception e) { throw new RuntimeException("No se pudo inicializar SesionServicio", e); }
    }

<<<<<<< HEAD
=======
    // ── CATÁLOGOS ───────────────────────────────────────────────────
    /**
     * Carga los catálogos desde la BD usando los DAOs reales.
     * Si algún catálogo falla, muestra un toast pero no rompe el formulario.
     */
    private void inicializarCatalogos() {
        // Artistas (ID_ARTISTA → NOMBRE_ARTISTA)
        try {
            for (model.Artista a : new dao.ArtistaDAO().listarTodos())
                mapaArtistas.put(a.getIdArtista(), a.getNombreArtista());
        } catch (Exception e) {
            toast("Error al cargar artistas: " + e.getMessage(), MainFrame.ToastType.ERROR);
        }

        // Productores (ID_PRODUCTOR → NOMBRE)
        try {
            for (model.Productor p : new dao.ProductorDAO().listarTodos())
                mapaProductores.put(p.getIdProductor(), p.getNombre());
        } catch (Exception e) {
            toast("Error al cargar productores: " + e.getMessage(), MainFrame.ToastType.ERROR);
        }

        // Cabinas (ID_CABINA → NOMBRE_CABINA)
        try {
            for (model.Cabina c : new dao.CabinaDAO().listarTodos())
                mapaCabinas.put(c.getIdCabina(), c.getNombreCabina());
        } catch (Exception e) {
            toast("Error al cargar cabinas: " + e.getMessage(), MainFrame.ToastType.ERROR);
        }

        // Canciones (ID_CANCION → TITULO)
        try {
            for (model.Cancion c : new dao.CancionDao().listarTodos())
                mapaCanciones.put(c.getIdCancion(), c.getTitulo());
        } catch (Exception e) {
            toast("Error al cargar canciones: " + e.getMessage(), MainFrame.ToastType.ERROR);
        }

        // Fases (ID_FASE → etiqueta) — opción A: todas las fases
        try {
            mapaFases.putAll(new dao.FaseDAO().listarMapa());
        } catch (Exception e) {
            toast("Error al cargar fases: " + e.getMessage(), MainFrame.ToastType.ERROR);
        }

        // Estados de grabación (ID_ESTADO_GRABACION → NOMBRE)
        try {
            mapaEstados.putAll(new dao.EstadoGrabacionDAO().listarMapa());
        } catch (Exception e) {
            toast("Error al cargar estados: " + e.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    // ── HELPERS DE CATÁLOGO ─────────────────────────────────────────
    private String nombreArtista(Integer id) {
        return id != null ? mapaArtistas.getOrDefault(id, "Artista #" + id) : "-";
    }
    private String nombreProductor(Integer id) {
        return id != null ? mapaProductores.getOrDefault(id, "Productor #" + id) : "-";
    }
    private String nombreCabina(Integer id) {
        return id != null ? mapaCabinas.getOrDefault(id, "Cabina #" + id) : "-";
    }
    private String nombreEstado(Integer id) {
        return id != null ? mapaEstados.getOrDefault(id, "Estado #" + id) : "-";
    }
    private String nombreFase(Integer id) {
        return id != null ? mapaFases.getOrDefault(id, "Fase #" + id) : "-";
    }
    private String nombreCancion(Integer id) {
        return id != null ? mapaCanciones.getOrDefault(id, "Canción #" + id) : "-";
    }

    /**
     * Calcula la duración en horas entre horaInicio y horaFin.
     * Devuelve 0 si alguno es null.
     */
    private double duracion(Sesion s) {
        if (s.getHoraInicio() == null || s.getHoraFin() == null) return 0;
        long minutos = ChronoUnit.MINUTES.between(s.getHoraInicio(), s.getHoraFin());
        return Math.max(0, minutos / 60.0);
    }

    /** Costo estimado: la tarifa fue eliminada, así que siempre es 0. */
    private double costoEstimado(Sesion s) {
        return 0.0;
    }

>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
    // ── INTEGRACIÓN SERVICIO ────────────────────────────────────────
    private void cargarSesionesDesdeServicio() {
        sesiones.clear();
        try {
            List<Sesion> lista = sesionServicio.listar();
            if (lista != null) sesiones.addAll(lista);
        } catch (Exception ex) {
            toast("Error al cargar sesiones: " + ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    private int crearEnServicio(Sesion s) {
        try { return sesionServicio.crear(s); }
        catch (IllegalArgumentException ex) { toast(ex.getMessage(), MainFrame.ToastType.ERROR); return -1; }
        catch (Exception ex) { toast("Error al crear: " + ex.getMessage(), MainFrame.ToastType.ERROR); return -1; }
    }

    private boolean actualizarEnServicio(Sesion s) {
        try { return sesionServicio.actualizar(s); }
        catch (IllegalArgumentException ex) { toast(ex.getMessage(), MainFrame.ToastType.ERROR); return false; }
        catch (Exception ex) { toast("Error al actualizar: " + ex.getMessage(), MainFrame.ToastType.ERROR); return false; }
    }

    private boolean eliminarEnServicio(int id) {
        try { return sesionServicio.eliminar(id); }
        catch (Exception ex) { toast("Error al eliminar: " + ex.getMessage(), MainFrame.ToastType.ERROR); return false; }
    }

<<<<<<< HEAD
    // ── CATÁLOGOS ───────────────────────────────────────────────────
    public void cargarCombos() {
        artistas.clear(); productores.clear(); cabinas.clear();
        artistas.add(new Artista(1, null, "Bad Bunny", "Benito Martinez",
                LocalDate.of(1994, 3, 10), "M", "Puerto Rico", "Reggaeton",
                "@badbunny", LocalDate.of(2016, 1, 1),
                Artista.ESTADO_ACTIVO, Artista.TIPO_SOLISTA));
        artistas.add(new Artista(2, null, "Karol G", "Carolina Giraldo",
                LocalDate.of(1991, 2, 14), "F", "Colombia", "Reggaeton",
                "@karolg", LocalDate.of(2017, 1, 1),
                Artista.ESTADO_ACTIVO, Artista.TIPO_SOLISTA));
        artistas.add(new Artista(3, null, "Shakira", "Shakira Mebarak",
                LocalDate.of(1977, 2, 2), "F", "Colombia", "Pop / Rock",
                "@shakira", LocalDate.of(2010, 1, 1),
                Artista.ESTADO_EN_PAUSA, Artista.TIPO_SOLISTA));
        productores.add(new Productor(1, "Carlos Vives",     "cvives@mail.com",  "3001234567", "Mezcla",        120.0));
        productores.add(new Productor(2, "Andres Torres",    "atorres@mail.com", "3109876543", "Masterizacion",  95.0));
        productores.add(new Productor(3, "Mauricio Rengifo", "mrengifo@mail.com","3154561234", "Composicion",   150.0));
        cabinas.add("Cabina A");
        cabinas.add("Cabina B");
        cabinas.add("Cabina C - Mastering");
    }

=======
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
    // ── HEADER ──────────────────────────────────────────────────────
    private JPanel headerPanel() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JPanel tituloRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tituloRow.setOpaque(false);
        tituloRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titulo = SesionComponents.lbl("Sesiones", FT, C_TEXT_PRI);

        recLabel = new JLabel("  ● REC");
        recLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        recLabel.setForeground(C_REC);

        final boolean[] recVis = {true};
        recTimer = new Timer(600, e -> {
            recVis[0] = !recVis[0];
            recLabel.setForeground(recVis[0] ? C_REC : new Color(0, 0, 0, 0));
        });
        recTimer.start();

        tituloRow.add(titulo);
        tituloRow.add(recLabel);

        JLabel subtitulo = SesionComponents.lbl("GRABACIÓN  ·  CABINAS  ·  AGENDA", FE, C_TEXT_MUT);
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);

        izq.add(tituloRow);
        izq.add(Box.createVerticalStrut(4));
        izq.add(subtitulo);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        der.setOpaque(false);

<<<<<<< HEAD
        // Campo de búsqueda con fondo blanco
=======
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        busqueda = new ModernUI.RoundedTextField("Buscar sesión...") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (hasFocus()) {
                    g2.setColor(new Color(26, 167, 224, 210));
                    g2.setStroke(new BasicStroke(2f));
                } else {
                    g2.setColor(new Color(0xD1D5DB));
                    g2.setStroke(new BasicStroke(1f));
                }
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
                if (getText().isEmpty() && !hasFocus()) {
                    g2.setColor(new Color(0x9CA3AF));
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("Buscar sesión...", 16,
                            (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                }
                g2.dispose();
                getUI().paint(g, this);
            }
        };
        busqueda.setForeground(new Color(0x1F2937));
        busqueda.setCaretColor(new Color(0x1F2937));
        busqueda.setPreferredSize(new Dimension(210, 38));
        busqueda.getDocument().addDocumentListener(docListener(this::aplicarFiltro));

        ModernUI.RoundedButton bGrabar   = btn("🎙  Grabar",      false, 120);
<<<<<<< HEAD
        ModernUI.RoundedButton bFacturar = btn("💳  Facturar",    false, 125);
=======
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        ModernUI.RoundedButton bRefr     = btn("↺  Refrescar",    false, 130);
        btnVista                         = btn("Ver tarjetas",    false, 130);
        ModernUI.RoundedButton bNueva    = btn("＋ Nueva sesión", true,  158);

        bGrabar.setForeground(C_ACCENT_CYAN);
<<<<<<< HEAD
        bFacturar.setForeground(C_OK);
        bRefr.setForeground(C_TEXT_MUT);

        bGrabar.addActionListener(e -> abrirGrabacion());
        bFacturar.addActionListener(e -> facturarSesionSeleccionada());
=======
        bRefr.setForeground(C_TEXT_MUT);

        bGrabar.addActionListener(e -> abrirGrabacion());
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        bRefr.addActionListener(e -> {
            busqueda.setText("");
            cargarSesionesDesdeServicio();
            aplicarFiltro();
            toast("Lista actualizada", MainFrame.ToastType.INFO);
        });
        btnVista.addActionListener(e -> alternarVista());
        bNueva.addActionListener(e -> openForm(null));

        der.add(busqueda);
        der.add(btnVista);
        der.add(bGrabar);
<<<<<<< HEAD
        der.add(bFacturar);
=======
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        der.add(bRefr);
        der.add(bNueva);

        p.add(izq, BorderLayout.WEST);
        p.add(der, BorderLayout.EAST);
        return p;
    }

<<<<<<< HEAD
    // ── ABRIR GRABACIÓN ─────────────────────────────────────────────
=======
    // ── GRABACIÓN ───────────────────────────────────────────────────
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
    private void abrirGrabacion() {
        if (seleccionada == null) {
            toast("Selecciona una sesión para grabar audio", MainFrame.ToastType.INFO);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
<<<<<<< HEAD
        dialogGrabacion dlg = new dialogGrabacion(owner, seleccionada.getIdSesion(),
=======
        dialogGrabacion dlg = new dialogGrabacion(owner,
                seleccionada.getIdGrabacion(),
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
                seleccionada.getNombreSesion());
        dlg.setOnGrabacionGuardada(this::actualizarGrabaciones);
        dlg.setVisible(true);
        actualizarGrabaciones();
    }

    // ── KPI CARDS ───────────────────────────────────────────────────
    private JPanel statsPanel() {
        JPanel p = new JPanel(new GridLayout(1, 4, 12, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setBorder(new EmptyBorder(0, 0, 14, 0));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        p.add(statCard("Sesiones totales", stTotal, C_PRIMARY,           "totales"));
        p.add(statCard("Programadas",      stProg,  C_PROG,              "en agenda"));
        p.add(statCard("En curso",         stCurso, new Color(0xBA7517), "activas"));
        p.add(statCard("Costo estimado",   stCosto, C_OK,                "acumulado"));
        return p;
    }

    private JPanel statCard(String titulo, JLabel valor, Color acento, String sub) {
        JPanel c = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 13, 13);
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 13, 13);
                g2.setColor(acento);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        c.setOpaque(false);
        c.setLayout(new BorderLayout());
        c.setBorder(new EmptyBorder(15, 20, 13, 17));

        JLabel t = new JLabel(titulo.toUpperCase());
        t.setFont(new Font("Segoe UI", Font.BOLD, 9));
        t.setForeground(C_TEXT_MUT);

        valor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valor.setForeground(acento);

        JLabel sb = new JLabel(sub);
        sb.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        sb.setForeground(C_TEXT_MUT);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        t.setAlignmentX(LEFT_ALIGNMENT);
        valor.setAlignmentX(LEFT_ALIGNMENT);
        sb.setAlignmentX(LEFT_ALIGNMENT);
        centro.add(t);
        centro.add(Box.createVerticalStrut(3));
        centro.add(valor);
        centro.add(Box.createVerticalStrut(2));
        centro.add(sb);
        c.add(centro, BorderLayout.WEST);
        return c;
    }

    private void actualizarStats() {
<<<<<<< HEAD
        long prog  = sesiones.stream().filter(s -> Sesion.ESTADO_PROGRAMADA.equals(s.getEstadoSesion())).count();
        long curso = sesiones.stream().filter(s -> Sesion.ESTADO_EN_CURSO.equals(s.getEstadoSesion())).count();
        double costo = sesiones.stream().mapToDouble(Sesion::getCostoTotal).sum();
=======
        // "Programada" = idEstadoGrabacion == 1  |  "En curso" = 2
        long prog  = sesiones.stream().filter(s -> Integer.valueOf(1).equals(s.getIdEstadoGrabacion())).count();
        long curso = sesiones.stream().filter(s -> Integer.valueOf(2).equals(s.getIdEstadoGrabacion())).count();
        double costo = sesiones.stream().mapToDouble(this::costoEstimado).sum();
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        stTotal.setText(String.valueOf(sesiones.size()));
        stProg.setText(String.valueOf(prog));
        stCurso.setText(String.valueOf(curso));
        stCosto.setText(String.format("$%,.0f", costo));
    }

    // ── VISTA CENTRAL ───────────────────────────────────────────────
    private JPanel centroVistas() {
        cardLayout   = new CardLayout();
        vistaCentral = new JPanel(cardLayout);
        vistaCentral.setOpaque(false);
        vistaCentral.add(tablaCard(),    VISTA_TABLA);
        vistaCentral.add(tarjetasCard(), VISTA_TARJETAS);
        return vistaCentral;
    }

    private void alternarVista() {
        modoTarjetas = !modoTarjetas;
        cardLayout.show(vistaCentral, modoTarjetas ? VISTA_TARJETAS : VISTA_TABLA);
        btnVista.setText(modoTarjetas ? "Ver tabla" : "Ver tarjetas");
    }

    private JPanel tablaCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        JLabel tit = new JLabel("Lista de sesiones");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tit.setForeground(C_TEXT_PRI);
        cab.add(tit, BorderLayout.WEST);

        ModernUI.RoundedButton bEditar   = btn("✎ Editar",   false, 110);
        ModernUI.RoundedButton bEliminar = btn("✖ Eliminar", false, 110);
        bEditar.setForeground(C_PRIMARY);
        bEliminar.setForeground(C_ERR);
        bEditar.addActionListener(e -> {
            if (seleccionada != null) openForm(seleccionada);
            else toast("Selecciona una sesión primero", MainFrame.ToastType.INFO);
        });
        bEliminar.addActionListener(e -> eliminarSeleccionada());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(bEditar);
        acciones.add(bEliminar);
        cab.add(acciones, BorderLayout.EAST);
        card.add(cab, BorderLayout.NORTH);
        card.add(construirListaScroll(), BorderLayout.CENTER);
        return card;
    }

    private JScrollPane construirListaScroll() {
        listaCont = new JPanel();
        listaCont.setOpaque(false);
        listaCont.setLayout(new BoxLayout(listaCont, BoxLayout.Y_AXIS));
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(listaCont, BorderLayout.NORTH);
        listaScroll = new JScrollPane(wrap);
        listaScroll.setBorder(BorderFactory.createEmptyBorder());
        listaScroll.setOpaque(false);
        listaScroll.getViewport().setOpaque(false);
        listaScroll.getVerticalScrollBar().setUnitIncrement(18);
        return listaScroll;
    }

    private JComponent filaSesion(Sesion s) {
        boolean activa = s == seleccionada;
<<<<<<< HEAD
        Color accentColor = colorEstadoAccent(s.getEstadoSesion());
=======
        Color accentColor = colorEstadoAccentById(s.getIdEstadoGrabacion());
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d

        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(activa ? C_ROW_SEL : C_ROW_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(activa ? C_PRIMARY : C_BORDER);
                g2.setStroke(new BasicStroke(activa ? 1.5f : 1f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
                if (activa) {
                    g2.setColor(C_ACCENT_CYAN);
                    g2.fillRoundRect(0, 9, 4, getHeight() - 19, 4, 4);
                }
                g2.dispose();
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(12, 0));
        fila.setBorder(new EmptyBorder(11, 14, 11, 14));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        fila.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

<<<<<<< HEAD
        fila.add(SesionComponents.avatar(iniciales(s.getArtista().getNombreArtista()), accentColor), BorderLayout.WEST);
=======
        String artNombre = s.getNombreArtista() != null ? s.getNombreArtista()
                         : nombreArtista(s.getIdArtista());
        fila.add(SesionComponents.avatar(iniciales(artNombre), accentColor), BorderLayout.WEST);
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
<<<<<<< HEAD
        JLabel nom = new JLabel(s.getNombreSesion());
        nom.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nom.setForeground(C_TEXT_PRI);
        nom.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel(s.getArtista().getNombreArtista() + "  ·  " + s.getProductor().getNombre());
=======
        JLabel nom = new JLabel(s.getNombreSesion() != null ? s.getNombreSesion() : "(sin nombre)");
        nom.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nom.setForeground(C_TEXT_PRI);
        nom.setAlignmentX(LEFT_ALIGNMENT);

        String prodNombre = s.getNombreProductor() != null ? s.getNombreProductor()
                          : nombreProductor(s.getIdProductor());
        JLabel sub = new JLabel(artNombre + "  ·  " + prodNombre);
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sub.setForeground(C_TEXT_MUT);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        centro.add(nom);
        centro.add(Box.createVerticalStrut(2));
        centro.add(sub);
        fila.add(centro, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        der.setOpaque(false);

        JPanel fechaBox = new JPanel();
        fechaBox.setOpaque(false);
        fechaBox.setLayout(new BoxLayout(fechaBox, BoxLayout.Y_AXIS));
<<<<<<< HEAD
        JLabel fec = new JLabel(s.getFecha().format(FMT));
        fec.setFont(new Font("Segoe UI", Font.BOLD, 11));
        fec.setForeground(C_TEXT_SEC());
        fec.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel hor = new JLabel(s.getHoraInicio() + "-" + s.getHoraFin());
=======
        String fechaStr = s.getFechaGrabacion() != null ? s.getFechaGrabacion().format(FMT) : "-";
        String horaStr  = horaStr(s);
        JLabel fec = new JLabel(fechaStr);
        fec.setFont(new Font("Segoe UI", Font.BOLD, 11));
        fec.setForeground(new Color(0x374151));
        fec.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel hor = new JLabel(horaStr);
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        hor.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        hor.setForeground(C_TEXT_MUT);
        hor.setAlignmentX(Component.CENTER_ALIGNMENT);
        fechaBox.add(fec);
        fechaBox.add(hor);
        der.add(fechaBox);

<<<<<<< HEAD
        JLabel costo = new JLabel(String.format("$%,.0f", s.getCostoTotal()));
=======
        JLabel costo = new JLabel(String.format("$%,.0f", costoEstimado(s)));
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        costo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        costo.setForeground(C_OK);
        der.add(costo);

<<<<<<< HEAD
        der.add(pillEstado(s.getEstadoSesion()));
=======
        der.add(pillEstado(s.getIdEstadoGrabacion()));
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d

        fila.add(der, BorderLayout.EAST);
        fila.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                seleccionar(s);
                if (e.getClickCount() == 2) openForm(s);
            }
        });
        return fila;
    }

<<<<<<< HEAD
    private static Color C_TEXT_SEC() {
        return new Color(0x374151);
    }

    private JPanel pillEstado(String estado) {
        Color bgColor  = colorEstado(estado);
        Color fgColor  = colorEstadoFg(estado);
        Color dotColor = colorEstadoAccent(estado);
=======
    /** Formatea el rango de horas de una sesión para mostrar en la lista. */
    private String horaStr(Sesion s) {
        if (s.getHoraInicio() == null) return "-";
        String hi = s.getHoraInicio().toLocalTime().toString().substring(0, 5);
        if (s.getHoraFin() == null) return hi;
        String hf = s.getHoraFin().toLocalTime().toString().substring(0, 5);
        return hi + "-" + hf;
    }

    private JPanel pillEstado(Integer idEstado) {
        String label = nombreEstado(idEstado);
        Color bgColor  = colorEstadoById(idEstado);
        Color fgColor  = colorEstadoFgById(idEstado);
        Color dotColor = colorEstadoAccentById(idEstado);
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d

        JPanel pill = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 99, 99);
                g2.dispose();
            }
        };
        pill.setOpaque(false);
        pill.setBorder(new EmptyBorder(3, 8, 3, 8));

        JLabel dot = new JLabel("●");
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 8));
        dot.setForeground(dotColor);

<<<<<<< HEAD
        JLabel txt = new JLabel(estado != null ? estado : "");
=======
        JLabel txt = new JLabel(label);
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        txt.setFont(new Font("Segoe UI", Font.BOLD, 10));
        txt.setForeground(fgColor);

        pill.add(dot);
        pill.add(txt);
        return pill;
    }

<<<<<<< HEAD
=======
    // ── Mapeo de colores por ID de estado ───────────────────────────
    private Color colorEstadoById(Integer id) {
        if (id == null) return new Color(0xF3F4F6);
        return switch (id) {
            case 1 -> new Color(0xEFF6FF);
            case 2 -> new Color(0xFFFBEB);
            case 3 -> new Color(0xF0FDF4);
            case 4 -> new Color(0xFEF2F2);
            default -> new Color(0xF3F4F6);
        };
    }
    private Color colorEstadoFgById(Integer id) {
        if (id == null) return new Color(0x6B7280);
        return switch (id) {
            case 1 -> new Color(0x1D4ED8);
            case 2 -> new Color(0x92400E);
            case 3 -> new Color(0x166534);
            case 4 -> new Color(0x991B1B);
            default -> new Color(0x6B7280);
        };
    }
    private Color colorEstadoAccentById(Integer id) {
        if (id == null) return C_BORDER;
        return switch (id) {
            case 1 -> C_PRIMARY;
            case 2 -> new Color(0xF59E0B);
            case 3 -> C_OK;
            case 4 -> C_ERR;
            default -> C_BORDER;
        };
    }

>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
    private void construirLista(List<Sesion> data) {
        if (listaCont == null) return;
        listaCont.removeAll();
        for (Sesion s : data) {
            listaCont.add(filaSesion(s));
            listaCont.add(Box.createVerticalStrut(7));
        }
        if (data.isEmpty()) {
            JLabel vacio = new JLabel("No hay sesiones para mostrar");
            vacio.setFont(FS);
            vacio.setForeground(C_TEXT_MUT);
            vacio.setBorder(new EmptyBorder(30, 10, 30, 10));
            listaCont.add(vacio);
        }
        listaCont.revalidate();
        listaCont.repaint();
    }

    // ── VISTA TARJETAS ──────────────────────────────────────────────
    private JScrollPane tarjetasCard() {
        gridTarjetas = new JPanel(new GridLayout(0, 3, 14, 14));
        gridTarjetas.setOpaque(false);
        gridTarjetas.setBorder(new EmptyBorder(2, 2, 2, 2));
        JPanel cont = new JPanel(new BorderLayout());
        cont.setOpaque(false);
        cont.add(gridTarjetas, BorderLayout.NORTH);
        JScrollPane sc = new JScrollPane(cont);
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.setOpaque(false);
        sc.getViewport().setOpaque(false);
        sc.getVerticalScrollBar().setUnitIncrement(16);
        return sc;
    }

    private void construirTarjetas(List<Sesion> data) {
        if (gridTarjetas == null) return;
        gridTarjetas.removeAll();
        for (Sesion s : data) gridTarjetas.add(crearTarjeta(s));
        gridTarjetas.revalidate();
        gridTarjetas.repaint();
    }

    private JComponent crearTarjeta(Sesion s) {
        boolean activa      = s == seleccionada;
<<<<<<< HEAD
        Color   accentColor = colorEstadoAccent(s.getEstadoSesion());
=======
        Color   accentColor = colorEstadoAccentById(s.getIdEstadoGrabacion());
        String  artNombre   = s.getNombreArtista() != null ? s.getNombreArtista()
                            : nombreArtista(s.getIdArtista());
        String  prodNombre  = s.getNombreProductor() != null ? s.getNombreProductor()
                            : nombreProductor(s.getIdProductor());
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d

        JPanel c = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(activa ? C_PRIMARY : C_BORDER);
                g2.setStroke(new BasicStroke(activa ? 2f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        c.setOpaque(false);
        c.setLayout(new BorderLayout(0, 10));
        c.setBorder(new EmptyBorder(14, 16, 14, 16));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);
        JPanel idar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        idar.setOpaque(false);
<<<<<<< HEAD
        idar.add(SesionComponents.avatar(iniciales(s.getArtista().getNombreArtista()), accentColor));
        JLabel nom = new JLabel(s.getNombreSesion());
=======
        idar.add(SesionComponents.avatar(iniciales(artNombre), accentColor));
        JLabel nom = new JLabel(s.getNombreSesion() != null ? s.getNombreSesion() : "(sin nombre)");
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        nom.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nom.setForeground(C_TEXT_PRI);
        idar.add(nom);
        top.add(idar, BorderLayout.CENTER);
<<<<<<< HEAD
        top.add(pillEstado(s.getEstadoSesion()), BorderLayout.EAST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(filaTarjeta(s.getFecha().format(FMT) + "   " + s.getHoraInicio() + "-" + s.getHoraFin()));
        info.add(Box.createVerticalStrut(4));
        info.add(filaTarjeta("Artista:   " + s.getArtista().getNombreArtista()));
        info.add(Box.createVerticalStrut(4));
        info.add(filaTarjeta("Productor: " + s.getProductor().getNombre()));

        JLabel costoLbl = new JLabel(String.format("$%,.0f", s.getCostoTotal()));
=======
        top.add(pillEstado(s.getIdEstadoGrabacion()), BorderLayout.EAST);

        String fechaStr = s.getFechaGrabacion() != null ? s.getFechaGrabacion().format(FMT) : "-";
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(filaTarjeta(fechaStr + "   " + horaStr(s)));
        info.add(Box.createVerticalStrut(4));
        info.add(filaTarjeta("Artista:   " + artNombre));
        info.add(Box.createVerticalStrut(4));
        info.add(filaTarjeta("Productor: " + prodNombre));
        info.add(Box.createVerticalStrut(4));
        info.add(filaTarjeta("Fase: " + nombreFase(s.getIdFase())));

        JLabel costoLbl = new JLabel(String.format("$%,.0f", costoEstimado(s)));
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        costoLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        costoLbl.setForeground(C_OK);

        c.add(top,      BorderLayout.NORTH);
        c.add(info,     BorderLayout.CENTER);
        c.add(costoLbl, BorderLayout.SOUTH);
        c.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                seleccionar(s);
                if (e.getClickCount() == 2) openForm(s);
            }
        });
        return c;
    }

    private JLabel filaTarjeta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FE);
        l.setForeground(C_TEXT_MUT);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    // ── PANEL LATERAL ───────────────────────────────────────────────
    private JComponent panelLateral() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(290, 10));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel t1 = new JLabel("PRÓXIMAS SESIONES");
        t1.setFont(new Font("Segoe UI", Font.BOLD, 11));
        t1.setForeground(C_ACCENT_CYAN);
        t1.setAlignmentX(LEFT_ALIGNMENT);
        card.add(t1);
        card.add(Box.createVerticalStrut(10));

        rankingBox = new JPanel();
        rankingBox.setOpaque(false);
        rankingBox.setLayout(new BoxLayout(rankingBox, BoxLayout.Y_AXIS));
        rankingBox.setAlignmentX(LEFT_ALIGNMENT);
        card.add(rankingBox);

        card.add(Box.createVerticalStrut(8));
        card.add(separador());
        card.add(Box.createVerticalStrut(14));

        JLabel t2 = new JLabel("RESUMEN");
        t2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        t2.setForeground(C_ACCENT_CYAN);
        t2.setAlignmentX(LEFT_ALIGNMENT);
        card.add(t2);
        card.add(Box.createVerticalStrut(10));

        resDuracion  = new JLabel("-");
        resCabina    = new JLabel("-");
        resProductor = new JLabel("-");
        card.add(resumenFila("Duración total",   resDuracion,  C_PRIMARY));
        card.add(Box.createVerticalStrut(7));
        card.add(resumenFila("Cabina más usada", resCabina,    C_ACCENT_CYAN));
        card.add(Box.createVerticalStrut(7));
        card.add(resumenFila("Productor top",    resProductor, new Color(0xBA7517)));
        card.add(Box.createVerticalStrut(14));

        card.add(separador());
        card.add(Box.createVerticalStrut(14));

        JLabel t3 = new JLabel("GRABACIONES");
        t3.setFont(new Font("Segoe UI", Font.BOLD, 11));
        t3.setForeground(C_ACCENT_CYAN);
        t3.setAlignmentX(LEFT_ALIGNMENT);
        card.add(t3);
        card.add(Box.createVerticalStrut(10));

        grabacionesBox = new JPanel();
        grabacionesBox.setOpaque(false);
        grabacionesBox.setLayout(new BoxLayout(grabacionesBox, BoxLayout.Y_AXIS));
        grabacionesBox.setAlignmentX(LEFT_ALIGNMENT);
        card.add(grabacionesBox);

        card.add(Box.createVerticalStrut(14));
        card.add(separador());
        card.add(Box.createVerticalStrut(10));

        JLabel tVu = new JLabel("SEÑAL EN VIVO");
        tVu.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tVu.setForeground(C_ACCENT_CYAN);
        tVu.setAlignmentX(LEFT_ALIGNMENT);
        card.add(tVu);
        card.add(Box.createVerticalStrut(8));

        JPanel vuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        vuPanel.setOpaque(false);
        vuPanel.setAlignmentX(LEFT_ALIGNMENT);
        List<Timer> vuTimers = new ArrayList<>();
        vuPanel.add(new VuMeter("L", vuTimers));
        vuPanel.add(new VuMeter("R", vuTimers));
        JLabel vuLabel = new JLabel("L        R");
        vuLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        vuLabel.setForeground(C_TEXT_MUT);
        vuPanel.add(vuLabel);
        card.add(vuPanel);

        return card;
    }

    private JSeparator separador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(C_BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private JComponent resumenFila(String etiqueta, JLabel valor, Color color) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_ROW_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 9, 9);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 12, 10, 12));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        JLabel et = new JLabel(etiqueta);
        et.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        et.setForeground(C_TEXT_MUT);
        valor.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valor.setForeground(color);
        valor.setHorizontalAlignment(SwingConstants.RIGHT);
        p.add(et,    BorderLayout.WEST);
        p.add(valor, BorderLayout.EAST);
        return p;
    }

    private void actualizarGrabaciones() {
        if (grabacionesBox == null) return;
        grabacionesBox.removeAll();

        if (seleccionada == null) {
            JLabel lbl = new JLabel("Selecciona una sesión");
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lbl.setForeground(C_TEXT_MUT);
            lbl.setAlignmentX(LEFT_ALIGNMENT);
            grabacionesBox.add(lbl);
            grabacionesBox.revalidate();
            grabacionesBox.repaint();
            return;
        }

        try {
            services.GrabacionService svc = new services.GrabacionService();
<<<<<<< HEAD
            List<model.Grabacion> lista = svc.listarPorSesion(seleccionada.getIdSesion());
=======
            List<model.Grabacion> lista = svc.listarPorSesion(seleccionada.getIdGrabacion());
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d

            if (lista.isEmpty()) {
                JLabel lbl = new JLabel("Sin grabaciones aún");
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                lbl.setForeground(C_TEXT_MUT);
                lbl.setAlignmentX(LEFT_ALIGNMENT);
                grabacionesBox.add(lbl);
            } else {
                for (model.Grabacion g : lista) {
                    JPanel fila = new JPanel(new BorderLayout(6, 0));
                    fila.setOpaque(false);
                    fila.setAlignmentX(LEFT_ALIGNMENT);
                    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

                    JPanel info = new JPanel();
                    info.setOpaque(false);
                    info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
                    JLabel nombre = new JLabel(g.getNombreArchivo());
                    nombre.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    nombre.setForeground(C_TEXT_PRI);
                    nombre.setAlignmentX(LEFT_ALIGNMENT);
                    JLabel meta = new JLabel(g.getDuracionSegundos() + "s  ·  " + g.getTamanoKb() + " KB");
                    meta.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                    meta.setForeground(C_TEXT_MUT);
                    meta.setAlignmentX(LEFT_ALIGNMENT);
                    info.add(nombre);
                    info.add(meta);

                    JButton btnPlay     = buildIconBtn("▶", C_ACCENT_CYAN);
                    JButton btnEliminar = buildIconBtn("✖", C_ERR);
                    btnPlay.addActionListener(e -> reproducir(g.getRutaArchivo(), btnPlay));
                    btnEliminar.addActionListener(e -> eliminarGrabacion(g.getIdGrabacion()));

                    JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
                    acciones.setOpaque(false);
                    acciones.add(btnPlay);
                    acciones.add(btnEliminar);

                    JLabel musicIco = new JLabel("♪");
                    musicIco.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    musicIco.setForeground(C_PRIMARY);

                    fila.add(musicIco, BorderLayout.WEST);
                    fila.add(info,     BorderLayout.CENTER);
                    fila.add(acciones, BorderLayout.EAST);
                    grabacionesBox.add(fila);
                    grabacionesBox.add(Box.createVerticalStrut(6));
                }
            }
        } catch (Exception ex) {
            JLabel err = new JLabel("Error: " + ex.getMessage());
            err.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            err.setForeground(C_ERR);
            err.setAlignmentX(LEFT_ALIGNMENT);
            grabacionesBox.add(err);
        }

        grabacionesBox.revalidate();
        grabacionesBox.repaint();
    }

    private JButton buildIconBtn(String icon, Color fg) {
        JButton b = new JButton(icon) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_ROW_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setForeground(fg);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(28, 28));
        return b;
    }

    private void eliminarGrabacion(int idGrabacion) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar esta grabación?",
                "Z-One — Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            services.GrabacionService svc = new services.GrabacionService();
            if (svc.eliminar(idGrabacion)) {
                toast("Grabación eliminada", MainFrame.ToastType.SUCCESS);
                actualizarGrabaciones();
            }
        } catch (Exception ex) {
            toast("Error al eliminar: " + ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    private Clip clipActual = null;

    private void reproducir(String ruta, JButton btn) {
        try {
            if (clipActual != null && clipActual.isRunning()) {
                clipActual.stop();
                clipActual.close();
                clipActual = null;
                btn.setText("▶");
                return;
            }
            File archivo = new File(ruta);
            if (!archivo.exists()) {
                toast("Archivo no encontrado: " + ruta, MainFrame.ToastType.ERROR);
                return;
            }
            AudioInputStream stream = AudioSystem.getAudioInputStream(archivo);
            clipActual = AudioSystem.getClip();
            clipActual.open(stream);
            clipActual.start();
            btn.setText("⏹");
            clipActual.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP)
                    SwingUtilities.invokeLater(() -> btn.setText("▶"));
            });
        } catch (Exception ex) {
            toast("Error al reproducir: " + ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    private JComponent rankingFila(int pos, Sesion s) {
        Color ac = pos == 1 ? C_PRIMARY : pos == 2 ? C_ACCENT_CYAN : C_BORDER;
        JPanel p = new JPanel(new BorderLayout(9, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_ROW_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 9, 9);
                g2.setColor(ac);
                g2.fillRoundRect(0, 4, 3, getHeight() - 9, 3, 3);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(8, 11, 8, 11));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JLabel num = new JLabel(String.valueOf(pos), SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                g2.setColor(ac);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        num.setFont(new Font("Segoe UI", Font.BOLD, 11));
        num.setForeground(ac);
        num.setOpaque(false);
        num.setPreferredSize(new Dimension(22, 22));
        p.add(num, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
<<<<<<< HEAD
        JLabel n = new JLabel(s.getNombreSesion());
        n.setFont(new Font("Segoe UI", Font.BOLD, 11));
        n.setForeground(C_TEXT_PRI);
        n.setAlignmentX(LEFT_ALIGNMENT);
        JLabel d = new JLabel(s.getArtista().getNombreArtista() + " · " + nombreCabina(s.getIdCabina()));
=======
        JLabel n = new JLabel(s.getNombreSesion() != null ? s.getNombreSesion() : "(sin nombre)");
        n.setFont(new Font("Segoe UI", Font.BOLD, 11));
        n.setForeground(C_TEXT_PRI);
        n.setAlignmentX(LEFT_ALIGNMENT);
        String artNombre = s.getNombreArtista() != null ? s.getNombreArtista()
                         : nombreArtista(s.getIdArtista());
        JLabel d = new JLabel(artNombre + " · " + nombreCabina(s.getIdCabina()));
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        d.setForeground(C_TEXT_MUT);
        d.setAlignmentX(LEFT_ALIGNMENT);
        info.add(n);
        info.add(d);
        p.add(info, BorderLayout.CENTER);

<<<<<<< HEAD
        JLabel fec = new JLabel(s.getFecha().format(FMT));
=======
        String fechaStr = s.getFechaGrabacion() != null ? s.getFechaGrabacion().format(FMT) : "-";
        JLabel fec = new JLabel(fechaStr);
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        fec.setFont(new Font("Segoe UI", Font.BOLD, 10));
        fec.setForeground(C_TEXT_MUT);
        p.add(fec, BorderLayout.EAST);
        return p;
    }

    private void actualizarPanelLateral() {
        if (rankingBox == null) return;
        rankingBox.removeAll();
        List<Sesion> orden = new ArrayList<>(sesiones);
<<<<<<< HEAD
        orden.sort(Comparator.comparing(Sesion::getFecha));
=======
        orden.sort(Comparator.comparing(
                s -> s.getFechaGrabacion() != null ? s.getFechaGrabacion() : LocalDate.MAX));
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        int pos = 1;
        for (Sesion s : orden) {
            if (pos > 3) break;
            rankingBox.add(rankingFila(pos, s));
            rankingBox.add(Box.createVerticalStrut(6));
            pos++;
        }
        rankingBox.revalidate();
        rankingBox.repaint();

<<<<<<< HEAD
        double durTotal = sesiones.stream().mapToDouble(Sesion::getDuracion).sum();
=======
        double durTotal = sesiones.stream().mapToDouble(this::duracion).sum();
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        resDuracion.setText(String.format("%.1f h", durTotal));

        String cabina = sesiones.stream()
                .map(s -> nombreCabina(s.getIdCabina()))
<<<<<<< HEAD
                .collect(java.util.stream.Collectors.groupingBy(cb -> cb, java.util.stream.Collectors.counting()))
=======
                .collect(java.util.stream.Collectors.groupingBy(cb -> cb,
                         java.util.stream.Collectors.counting()))
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
                .entrySet().stream()
                .max(Comparator.comparingLong(java.util.Map.Entry::getValue))
                .map(java.util.Map.Entry::getKey).orElse("-");
        resCabina.setText(cabina);

        String prod = sesiones.stream()
<<<<<<< HEAD
                .map(s -> s.getProductor().getNombre())
                .collect(java.util.stream.Collectors.groupingBy(pr -> pr, java.util.stream.Collectors.counting()))
=======
                .map(s -> {
                    String n = s.getNombreProductor();
                    return (n != null && !n.isBlank()) ? n : nombreProductor(s.getIdProductor());
                })
                .collect(java.util.stream.Collectors.groupingBy(pr -> pr,
                         java.util.stream.Collectors.counting()))
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
                .entrySet().stream()
                .max(Comparator.comparingLong(java.util.Map.Entry::getValue))
                .map(java.util.Map.Entry::getKey).orElse("-");
        resProductor.setText(prod);
    }

<<<<<<< HEAD
    private String nombreCabina(Integer idCabina) {
        if (idCabina != null && idCabina >= 1 && idCabina <= cabinas.size())
            return cabinas.get(idCabina - 1);
        return "-";
    }

    // ── CRUD ────────────────────────────────────────────────────────
    private List<Sesion> filtrar() {
        String q = busqueda == null ? "" : busqueda.getText().trim().toLowerCase();
        if (q.isEmpty()) return new ArrayList<>(sesiones);
        return sesiones.stream().filter(s ->
                s.getNombreSesion().toLowerCase().contains(q) ||
                s.getArtista().getNombreArtista().toLowerCase().contains(q) ||
                s.getProductor().getNombre().toLowerCase().contains(q) ||
                s.getFecha().format(FMT).contains(q)).toList();
=======
    // ── FILTRO Y SELECCIÓN ──────────────────────────────────────────
    private List<Sesion> filtrar() {
        String q = busqueda == null ? "" : busqueda.getText().trim().toLowerCase();
        if (q.isEmpty()) return new ArrayList<>(sesiones);
        return sesiones.stream().filter(s -> {
            String nom  = s.getNombreSesion() != null ? s.getNombreSesion().toLowerCase() : "";
            String art  = s.getNombreArtista() != null ? s.getNombreArtista().toLowerCase()
                        : nombreArtista(s.getIdArtista()).toLowerCase();
            String prod = s.getNombreProductor() != null ? s.getNombreProductor().toLowerCase()
                        : nombreProductor(s.getIdProductor()).toLowerCase();
            String fec  = s.getFechaGrabacion() != null ? s.getFechaGrabacion().format(FMT) : "";
            return nom.contains(q) || art.contains(q) || prod.contains(q) || fec.contains(q);
        }).toList();
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
    }

    private void aplicarFiltro() {
        List<Sesion> data = filtrar();
        construirLista(data);
        construirTarjetas(data);
        actualizarStats();
        actualizarPanelLateral();
    }

    private void seleccionar(Sesion s) {
        seleccionada = s;
        construirLista(filtrar());
        if (modoTarjetas) construirTarjetas(filtrar());
        actualizarGrabaciones();
    }

    private void eliminarSeleccionada() {
        if (seleccionada == null) {
            toast("Selecciona una sesión primero", MainFrame.ToastType.INFO);
            return;
        }
<<<<<<< HEAD
        int id = seleccionada.getIdSesion();
=======
        int id = seleccionada.getIdGrabacion();
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la sesión #" + String.format("%03d", id) + "?",
                "Z-One — Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (eliminarEnServicio(id)) {
<<<<<<< HEAD
                sesiones.removeIf(s -> s.getIdSesion() == id);
=======
                sesiones.removeIf(s -> s.getIdGrabacion() == id);
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
                seleccionada = null;
                aplicarFiltro();
                toast("Sesión eliminada correctamente", MainFrame.ToastType.SUCCESS);
            }
        }
    }

<<<<<<< HEAD
    // ── DIÁLOGO CREAR/EDITAR ────────────────────────────────────────
=======
    // ── DIÁLOGO CREAR / EDITAR ──────────────────────────────────────
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
    private void openForm(Sesion se) {
        final boolean isEdit = se != null;
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Editar sesión" : "Nueva sesión", true);
        dlg.setResizable(false);
        final List<Timer> timersDlg = new ArrayList<>();

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(C_BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        root.add(new BandaAnimada(isEdit, timersDlg), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(20, 26, 18, 26));

<<<<<<< HEAD
        FieldFx fNombre = new FieldFx(isEdit ? se.getNombreSesion()             : "", "Nombre de la sesión", timersDlg);
        FieldFx fFecha  = new FieldFx(isEdit ? se.getFecha().format(FMT)        : "", "dd/MM/yyyy",          timersDlg);
        FieldFx fHIni   = new FieldFx(isEdit ? se.getHoraInicio()               : "09:00", "HH:mm",          timersDlg);
        FieldFx fHFin   = new FieldFx(isEdit ? se.getHoraFin()                  : "12:00", "HH:mm",          timersDlg);
        FieldFx fDur    = new FieldFx(isEdit ? String.valueOf(se.getDuracion()) : "", "Horas (ej: 3.5)",     timersDlg);
        FieldFx fObs    = new FieldFx(isEdit && se.getObservaciones() != null
                                             ? se.getObservaciones() : "", "Observaciones opcionales",       timersDlg);

        ComboFx<String> cbEstado = SesionComponents.comboFx(
                ESTADOS, isEdit ? se.getEstadoSesion() : Sesion.ESTADO_PROGRAMADA, timersDlg);
        ComboFx<String> cbCabina = SesionComponents.comboFx(
                cabinas.toArray(new String[0]), cabinas.get(0), timersDlg);
        if (isEdit && se.getIdCabina() != null && se.getIdCabina() >= 1 && se.getIdCabina() <= cabinas.size())
            cbCabina.setSelectedIndex(se.getIdCabina() - 1);

        // ── Combos de Artista y Productor (una sola declaración cada uno) ──
        ComboFx<Artista> cbArt = SesionComponents.comboFxObj(
                artistas.toArray(new Artista[0]),
                v -> (v instanceof Artista) ? ((Artista) v).getNombreArtista() : "",
                timersDlg);
        if (isEdit) cbArt.setSelectedItem(se.getArtista());

        ComboFx<Productor> cbProd = SesionComponents.comboFxObj(
                productores.toArray(new Productor[0]),
                v -> (v instanceof Productor) ? ((Productor) v).getNombre() : "",
                timersDlg);
        if (isEdit) cbProd.setSelectedItem(se.getProductor());

        List<JComponent> filasFx = new ArrayList<>();
        filasFx.add(seccionTituloFx("INFORMACIÓN GENERAL", timersDlg));
        filasFx.add(filaDoble("NOMBRE DE SESIÓN *", fNombre, "FECHA *", fFecha));
        filasFx.add(filaDoble("HORA INICIO", fHIni, "HORA FIN", fHFin));
        filasFx.add(filaSimple("DURACIÓN (h) *", fDur));
        filasFx.add(seccionTituloFx("DETALLES", timersDlg));
        filasFx.add(filaDoble("ESTADO SESIÓN", cbEstado, "CABINA", cbCabina));
        filasFx.add(filaDoble("ARTISTA *", cbArt, "PRODUCTOR *", cbProd));
        filasFx.add(filaSimple("OBSERVACIONES", fObs));

        CardCostoFx cardCosto = new CardCostoFx(timersDlg);
        if (isEdit) {
            cardCosto.setMeta(String.format("%.1f h  ·  $%,.0f /h tarifa productor",
                    se.getDuracion(), se.getProductor().getTarifaHora()));
            cardCosto.setValor(se.getCostoTotal(), false);
        }
        filasFx.add(cardCosto);

        body.add(filasFx.get(0)); body.add(Box.createVerticalStrut(14));
        body.add(filasFx.get(1)); body.add(Box.createVerticalStrut(14));
        body.add(filasFx.get(2)); body.add(Box.createVerticalStrut(14));
        body.add(filasFx.get(3)); body.add(Box.createVerticalStrut(22));
        body.add(filasFx.get(4)); body.add(Box.createVerticalStrut(14));
        body.add(filasFx.get(5)); body.add(Box.createVerticalStrut(14));
        body.add(filasFx.get(6)); body.add(Box.createVerticalStrut(14));
        body.add(filasFx.get(7)); body.add(Box.createVerticalStrut(20));
        body.add(filasFx.get(8)); body.add(Box.createVerticalStrut(20));

        Runnable recalcular = () -> {
            try {
                Productor pr = (Productor) cbProd.getSelectedItem();
                if (pr == null || fDur.getText().trim().isEmpty()) {
                    cardCosto.setValor(0, true);
                    cardCosto.setMeta("—");
                    return;
                }
                double dur = Double.parseDouble(fDur.getText().trim());
                Sesion temp = new Sesion();
                temp.setProductor(pr);
                temp.setDuracion(dur);
                cardCosto.setValor(temp.getCostoTotal(), true);
                cardCosto.setMeta(String.format("%.1f h  ·  $%,.0f /h tarifa productor",
                        dur, pr.getTarifaHora()));
            } catch (NumberFormatException ex) {
                cardCosto.setMeta("Duración inválida");
            }
        };
        fDur.getDocument().addDocumentListener(docListener(recalcular));
        cbProd.addActionListener(e -> recalcular.run());

=======
        // ── Campos de texto ──────────────────────────────────────────
        FieldFx fNombre = new FieldFx(
                isEdit && se.getNombreSesion() != null ? se.getNombreSesion() : "",
                "Nombre de la sesión", timersDlg);

        FieldFx fFecha = new FieldFx(
                isEdit && se.getFechaGrabacion() != null ? se.getFechaGrabacion().format(FMT) : "",
                "dd/MM/yyyy", timersDlg);

        String hiStr = (isEdit && se.getHoraInicio() != null)
                ? se.getHoraInicio().toLocalTime().toString().substring(0, 5) : "09:00";
        String hfStr = (isEdit && se.getHoraFin() != null)
                ? se.getHoraFin().toLocalTime().toString().substring(0, 5) : "12:00";
        FieldFx fHIni = new FieldFx(hiStr, "HH:mm", timersDlg);
        FieldFx fHFin = new FieldFx(hfStr, "HH:mm", timersDlg);

        FieldFx fNumSesion = new FieldFx(
                isEdit && se.getNumeroSesion() != null ? se.getNumeroSesion().toString() : "",
                "Número (opcional)", timersDlg);

        FieldFx fObs = new FieldFx(
                isEdit && se.getNotas() != null ? se.getNotas() : "",
                "Notas / observaciones", timersDlg);

        // ── Combos de catálogo (Integer ID → nombre) ─────────────────
        Integer[] idsEstados     = mapaEstados.keySet().toArray(new Integer[0]);
        Integer[] idsCabinas     = mapaCabinas.keySet().toArray(new Integer[0]);
        Integer[] idsFases       = mapaFases.keySet().toArray(new Integer[0]);
        Integer[] idsCanciones   = mapaCanciones.keySet().toArray(new Integer[0]);
        Integer[] idsArtistas    = mapaArtistas.keySet().toArray(new Integer[0]);
        Integer[] idsProductores = mapaProductores.keySet().toArray(new Integer[0]);

        ComboFx<Integer> cbEstado = SesionComponents.comboFxObj(idsEstados,
                id -> mapaEstados.getOrDefault(id, "Estado " + id), timersDlg);
        if (isEdit && se.getIdEstadoGrabacion() != null)
            cbEstado.setSelectedItem(se.getIdEstadoGrabacion());
        else if (idsEstados.length > 0) cbEstado.setSelectedIndex(0);

        ComboFx<Integer> cbCabina = SesionComponents.comboFxObj(idsCabinas,
                id -> mapaCabinas.getOrDefault(id, "Cabina " + id), timersDlg);
        if (isEdit && se.getIdCabina() != null) cbCabina.setSelectedItem(se.getIdCabina());
        else if (idsCabinas.length > 0) cbCabina.setSelectedIndex(0);

        ComboFx<Integer> cbFase = SesionComponents.comboFxObj(idsFases,
                id -> mapaFases.getOrDefault(id, "Fase " + id), timersDlg);
        if (isEdit && se.getIdFase() != null) cbFase.setSelectedItem(se.getIdFase());
        else if (idsFases.length > 0) cbFase.setSelectedIndex(0);

        ComboFx<Integer> cbCancion = SesionComponents.comboFxObj(idsCanciones,
                id -> mapaCanciones.getOrDefault(id, "Canción " + id), timersDlg);
        if (isEdit && se.getIdCancion() != null) cbCancion.setSelectedItem(se.getIdCancion());
        else if (idsCanciones.length > 0) cbCancion.setSelectedIndex(0);

        ComboFx<Integer> cbArt = SesionComponents.comboFxObj(idsArtistas,
                id -> mapaArtistas.getOrDefault(id, "Artista " + id), timersDlg);
        if (isEdit && se.getIdArtista() != null) cbArt.setSelectedItem(se.getIdArtista());
        else if (idsArtistas.length > 0) cbArt.setSelectedIndex(0);

        ComboFx<Integer> cbProd = SesionComponents.comboFxObj(idsProductores,
                id -> mapaProductores.getOrDefault(id, "Productor " + id), timersDlg);
        if (isEdit && se.getIdProductor() != null) cbProd.setSelectedItem(se.getIdProductor());
        else if (idsProductores.length > 0) cbProd.setSelectedIndex(0);

        // ── Ensamblar filas ──────────────────────────────────────────
        List<JComponent> filasFx = new ArrayList<>();
        filasFx.add(seccionTituloFx("INFORMACIÓN GENERAL", timersDlg));        // 0
        filasFx.add(filaDoble("NOMBRE DE SESIÓN *", fNombre,
                              "FECHA *",            fFecha));                   // 1
        filasFx.add(filaDoble("HORA INICIO",  fHIni,
                              "HORA FIN",     fHFin));                          // 2
        filasFx.add(filaDoble("N° SESIÓN",    fNumSesion,
                              "FASE *",       cbFase));                         // 3
        filasFx.add(seccionTituloFx("DETALLES", timersDlg));                   // 4
        filasFx.add(filaDoble("ESTADO *",     cbEstado,
                              "CABINA *",     cbCabina));                       // 5
        filasFx.add(filaDoble("ARTISTA *",    cbArt,
                              "PRODUCTOR *",  cbProd));                         // 6
        filasFx.add(filaSimple("CANCIÓN *",   cbCancion));                      // 7
        filasFx.add(filaSimple("NOTAS",       fObs));                           // 8

        for (int i = 0; i < filasFx.size(); i++) {
            body.add(filasFx.get(i));
            body.add(Box.createVerticalStrut(i == 3 || i == 8 ? 22 : 14));
        }

        // ── Botones ──────────────────────────────────────────────────
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);
        btns.setAlignmentX(LEFT_ALIGNMENT);
        btns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        BtnFx bCancel = new BtnFx("Cancelar", false, timersDlg);
<<<<<<< HEAD
        BtnFx bSave   = new BtnFx(isEdit ? "💾  Guardar cambios" : "✦  Crear sesión", true, timersDlg);
=======
        BtnFx bSave   = new BtnFx(isEdit ? "💾  Guardar cambios" : "✦  Crear sesión",
                                  true, timersDlg);
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        bCancel.setPreferredSize(new Dimension(120, 40));
        bSave.setPreferredSize(new Dimension(180, 40));
        bCancel.addActionListener(e -> cerrarConFade(dlg));

        bSave.addActionListener(e -> {
<<<<<<< HEAD
            String nm     = fNombre.getText().trim();
            String fd     = fFecha.getText().trim();
            String dr     = fDur.getText().trim();
            String hi     = fHIni.getText().trim();
            String hf     = fHFin.getText().trim();
            String obs    = fObs.getText().trim();
            String estado = (String)    cbEstado.getSelectedItem();
            Integer idCab = cbCabina.getSelectedIndex() + 1;
            Artista   art  = (Artista)   cbArt.getSelectedItem();
            Productor prod = (Productor) cbProd.getSelectedItem();

            if (nm.isEmpty())  { fNombre.shake(); toast("El nombre de sesión es obligatorio", MainFrame.ToastType.ERROR); return; }
            if (fd.isEmpty())  { fFecha.shake();  toast("La fecha es obligatoria",            MainFrame.ToastType.ERROR); return; }
            if (dr.isEmpty())  { fDur.shake();    toast("La duración es obligatoria",          MainFrame.ToastType.ERROR); return; }
            if (art  == null)  { toast("Selecciona un artista",   MainFrame.ToastType.ERROR); return; }
            if (prod == null)  { toast("Selecciona un productor", MainFrame.ToastType.ERROR); return; }
=======
            String  nm       = fNombre.getText().trim();
            String  fd       = fFecha.getText().trim();
            String  hiT      = fHIni.getText().trim();
            String  hfT      = fHFin.getText().trim();
            String  obs      = fObs.getText().trim();
            String  numStr   = fNumSesion.getText().trim();
            Integer idEstado = (Integer) cbEstado.getSelectedItem();
            Integer idCab    = (Integer) cbCabina.getSelectedItem();
            Integer idFase   = (Integer) cbFase.getSelectedItem();
            Integer idCancion= (Integer) cbCancion.getSelectedItem();
            Integer idArt    = (Integer) cbArt.getSelectedItem();
            Integer idProd   = (Integer) cbProd.getSelectedItem();

            // ── Validaciones básicas ──
            if (nm.isEmpty())     { fNombre.shake(); toast("El nombre de sesión es obligatorio", MainFrame.ToastType.ERROR); return; }
            if (fd.isEmpty())     { fFecha.shake();  toast("La fecha es obligatoria",             MainFrame.ToastType.ERROR); return; }
            if (idArt  == null)   { toast("Selecciona un artista",    MainFrame.ToastType.ERROR); return; }
            if (idProd == null)   { toast("Selecciona un productor",  MainFrame.ToastType.ERROR); return; }
            if (idFase == null)   { toast("Selecciona una fase",      MainFrame.ToastType.ERROR); return; }
            if (idCancion == null){ toast("Selecciona una canción",   MainFrame.ToastType.ERROR); return; }
            if (idEstado == null) { toast("Selecciona un estado",     MainFrame.ToastType.ERROR); return; }
            if (idCab == null)    { toast("Selecciona una cabina",    MainFrame.ToastType.ERROR); return; }
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d

            LocalDate fecha;
            try { fecha = LocalDate.parse(fd, FMT); }
            catch (DateTimeParseException ex) {
                fFecha.shake();
                toast("Formato de fecha inválido (dd/MM/yyyy)", MainFrame.ToastType.ERROR);
                return;
            }
<<<<<<< HEAD
            double dur = parseDouble(dr).orElse(-1.0);
            if (dur <= 0) { fDur.shake(); toast("La duración debe ser un número positivo", MainFrame.ToastType.ERROR); return; }

            if (isEdit) {
                se.setNombreSesion(nm);  se.setFecha(fecha);
                se.setHoraInicio(hi);    se.setHoraFin(hf);
                se.setDuracion(dur);     se.setArtista(art);
                se.setProductor(prod);   se.setIdCabina(idCab);
                se.setEstadoSesion(estado); se.setObservaciones(obs);
=======

            LocalDateTime horaIni = null, horaFin = null;
            try {
                if (!hiT.isEmpty())
                    horaIni = LocalDateTime.of(fecha, java.time.LocalTime.parse(hiT));
                if (!hfT.isEmpty())
                    horaFin = LocalDateTime.of(fecha, java.time.LocalTime.parse(hfT));
            } catch (Exception ex) {
                toast("Formato de hora inválido (HH:mm)", MainFrame.ToastType.ERROR);
                return;
            }
            if (horaIni != null && horaFin != null && !horaFin.isAfter(horaIni)) {
                toast("La hora de fin debe ser posterior a la de inicio", MainFrame.ToastType.ERROR);
                return;
            }

            Integer numSesion = null;
            if (!numStr.isEmpty()) {
                try { numSesion = Integer.parseInt(numStr); }
                catch (NumberFormatException ex) {
                    fNumSesion.shake();
                    toast("El número de sesión debe ser entero", MainFrame.ToastType.ERROR);
                    return;
                }
            }

            if (isEdit) {
                se.setNombreSesion(nm);
                se.setFechaGrabacion(fecha);
                se.setHoraInicio(horaIni);
                se.setHoraFin(horaFin);
                se.setNumeroSesion(numSesion);
                se.setIdArtista(idArt);
                se.setIdProductor(idProd);
                se.setIdCabina(idCab);
                se.setIdFase(idFase);
                se.setIdCancion(idCancion);
                se.setIdEstadoGrabacion(idEstado);
                se.setNotas(obs.isEmpty() ? null : obs);
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
                if (actualizarEnServicio(se)) {
                    toast("Sesión actualizada correctamente", MainFrame.ToastType.SUCCESS);
                    aplicarFiltro();
                    cerrarConFade(dlg);
                }
            } else {
<<<<<<< HEAD
                Sesion nueva = new Sesion(0, art, prod, idCab, nm, fecha, hi, hf, dur, estado, obs);
                int idGenerado = crearEnServicio(nueva);
                if (idGenerado > 0) {
                    nueva.setIdSesion(idGenerado);
=======
                Sesion nueva = new Sesion();
                nueva.setNombreSesion(nm);
                nueva.setFechaGrabacion(fecha);
                nueva.setHoraInicio(horaIni);
                nueva.setHoraFin(horaFin);
                nueva.setNumeroSesion(numSesion);
                nueva.setIdArtista(idArt);
                nueva.setIdProductor(idProd);
                nueva.setIdCabina(idCab);
                nueva.setIdFase(idFase);
                nueva.setIdCancion(idCancion);
                nueva.setIdEstadoGrabacion(idEstado);
                nueva.setNotas(obs.isEmpty() ? null : obs);
                nueva.setNombreArtista(mapaArtistas.get(idArt));
                nueva.setNombreProductor(mapaProductores.get(idProd));

                int idGenerado = crearEnServicio(nueva);
                if (idGenerado > 0) {
                    nueva.setIdGrabacion(idGenerado);
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
                    sesiones.add(nueva);
                    seleccionada = nueva;
                    toast("Sesión creada correctamente", MainFrame.ToastType.SUCCESS);
                    aplicarFiltro();
                    cerrarConFade(dlg);
<<<<<<< HEAD
                    generarFacturaParaSesion(nueva);
                }
            }
        });
        btns.add(bCancel);
        btns.add(bSave);
        body.add(btns);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(C_BG_DARK);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        root.add(scroll, BorderLayout.CENTER);

        dlg.setContentPane(root);
        dlg.getRootPane().setDefaultButton(bSave);
        dlg.setSize(640, 730);
        dlg.setLocationRelativeTo(this);

        dlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                for (Timer t : timersDlg) t.stop();
                timersDlg.clear();
            }
        });

=======
                }
            }
        });

        btns.add(bCancel);
        btns.add(bSave);
        body.add(btns);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(C_BG_DARK);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        root.add(scroll, BorderLayout.CENTER);

        dlg.setContentPane(root);
        dlg.getRootPane().setDefaultButton(bSave);
        dlg.setSize(640, 760);
        dlg.setLocationRelativeTo(this);
        dlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                for (Timer t : timersDlg) t.stop();
                timersDlg.clear();
            }
        });
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        abrirConFade(dlg);
        animarEntradaFilas(filasFx, timersDlg);
        dlg.setVisible(true);
    }
<<<<<<< HEAD

    // ── FACTURACIÓN ─────────────────────────────────────────────────
    private void generarFacturaParaSesion(Sesion sesion) {
        String correo = JOptionPane.showInputDialog(this,
                "Correo del artista para enviar la factura:",
                "Z-One — Facturación", JOptionPane.QUESTION_MESSAGE);
        if (correo == null || correo.isBlank() || !correo.contains("@")) {
            toast("Factura no generada (correo inválido)", MainFrame.ToastType.INFO);
            return;
        }
        final String correoFinal = correo.trim();
        toast("Generando factura...", MainFrame.ToastType.INFO);
        new Thread(() -> {
            try {
                services.FacturaService fs = new services.FacturaService();
                model.Factura f = fs.generarYEnviar(sesion, correoFinal);
                SwingUtilities.invokeLater(() -> {
                    if ("ENVIADA".equals(f.getEstado()))
                        toast("✓ Factura " + f.getNumeroFactura() + " enviada", MainFrame.ToastType.SUCCESS);
                    else
                        toast("Factura generada pero no enviada", MainFrame.ToastType.INFO);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> toast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR));
            }
        }).start();
    }

// Reemplaza el bloque de facturarSesionSeleccionada() con esto:

private void facturarSesionSeleccionada() {
    if (seleccionada == null) { toast("Selecciona una sesión primero", MainFrame.ToastType.INFO); return; }
    if (seleccionada.getProductor() == null) { toast("La sesión debe tener productor asignado", MainFrame.ToastType.ERROR); return; }

    abrirDialogoFactura(seleccionada);
}

private void abrirDialogoFactura(Sesion sesion) {
    JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Generar Factura", true);
    dlg.setResizable(false);
    List<Timer> timers = new ArrayList<>();

    // ── ROOT ──
    JPanel root = new JPanel(new BorderLayout()) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(C_BG_DARK);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    };

    // ── BANDA SUPERIOR ──
    JPanel banda = new JPanel(new BorderLayout(12, 0)) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, new Color(0xFFFFFF),
                    getWidth(), getHeight(), new Color(0xF0EFFE)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(C_PRIMARY);
            g2.fillRect(0, getHeight() - 2, getWidth(), 2);
            g2.dispose();
        }
    };
    banda.setOpaque(false);
    banda.setBorder(new EmptyBorder(18, 22, 18, 22));
    banda.setPreferredSize(new Dimension(0, 78));

    JLabel ico = new JLabel("💳", SwingConstants.CENTER) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0xEEEDFE));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(C_PRIMARY);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    };
    ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
    ico.setPreferredSize(new Dimension(48, 48));

    JPanel bandaTxt = new JPanel();
    bandaTxt.setOpaque(false);
    bandaTxt.setLayout(new BoxLayout(bandaTxt, BoxLayout.Y_AXIS));
    JLabel bTit = SesionComponents.lbl("Generar factura", new Font("Segoe UI", Font.BOLD, 18), C_TEXT_PRI);
    JLabel bSub = SesionComponents.lbl("RESUMEN Y ENVÍO AL ARTISTA",
            new Font("Segoe UI", Font.BOLD, 9), C_TEXT_MUT);
    bTit.setAlignmentX(LEFT_ALIGNMENT);
    bSub.setAlignmentX(LEFT_ALIGNMENT);
    bandaTxt.add(Box.createVerticalGlue());
    bandaTxt.add(bTit);
    bandaTxt.add(Box.createVerticalStrut(3));
    bandaTxt.add(bSub);
    bandaTxt.add(Box.createVerticalGlue());
    banda.add(ico, BorderLayout.WEST);
    banda.add(bandaTxt, BorderLayout.CENTER);
    root.add(banda, BorderLayout.NORTH);

    // ── BODY ──
    JPanel body = new JPanel();
    body.setOpaque(false);
    body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
    body.setBorder(new EmptyBorder(20, 22, 6, 22));

    // Card resumen
    JPanel card = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(C_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            // acento lateral violeta
            g2.setColor(C_PRIMARY);
            g2.fillRoundRect(0, 8, 4, getHeight()-16, 4, 4);
            g2.dispose();
        }
    };
    card.setOpaque(false);
    card.setLayout(new GridLayout(0, 2, 0, 0));
    card.setBorder(new EmptyBorder(14, 18, 14, 18));
    card.setAlignmentX(LEFT_ALIGNMENT);
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

    double subtotal = sesion.getCostoTotal();
    double iva      = subtotal * 0.19;
    double total    = subtotal + iva;

    String[][] filas = {
        {"Sesión",    sesion.getNombreSesion()},
        {"Artista",   sesion.getArtista().getNombreArtista()},
        {"Productor", sesion.getProductor().getNombre()},
        {"Duración",  sesion.getDuracion() + " h"},
        {"Subtotal",  String.format("$%,.2f", subtotal)},
        {"IVA (19%)", String.format("$%,.2f", iva)},
    };
    for (String[] fila : filas) {
        JLabel k = new JLabel(fila[0]);
        k.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        k.setForeground(C_TEXT_MUT);
        k.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel v = new JLabel(fila[1], SwingConstants.RIGHT);
        v.setFont(new Font("Segoe UI", Font.BOLD, 12));
        v.setForeground(C_TEXT_PRI);
        v.setBorder(new EmptyBorder(5, 0, 5, 0));
        card.add(k); card.add(v);
    }
    body.add(card);
    body.add(Box.createVerticalStrut(10));

    // Total destacado
    JPanel totalRow = new JPanel(new BorderLayout()) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0xEEEDFE));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.setColor(C_PRIMARY);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            g2.dispose();
        }
    };
    totalRow.setOpaque(false);
    totalRow.setBorder(new EmptyBorder(10, 16, 10, 16));
    totalRow.setAlignmentX(LEFT_ALIGNMENT);
    totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

    JLabel kTotal = new JLabel("Total con IVA");
    kTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
    kTotal.setForeground(C_PRIMARY);

    JLabel vTotal = new JLabel(String.format("$%,.2f", total), SwingConstants.RIGHT);
    vTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
    vTotal.setForeground(C_OK);

    totalRow.add(kTotal, BorderLayout.WEST);
    totalRow.add(vTotal, BorderLayout.EAST);
    body.add(totalRow);
    body.add(Box.createVerticalStrut(20));

    // Separador sección correo
    JLabel secCorreo = SesionComponents.lbl("ENVIAR A",
            new Font("Segoe UI", Font.BOLD, 10), C_ACCENT_CYAN);
    secCorreo.setAlignmentX(LEFT_ALIGNMENT);
    body.add(secCorreo);
    body.add(Box.createVerticalStrut(8));

    JLabel lblCorreo = new JLabel("Correo del artista");
    lblCorreo.setFont(new Font("Segoe UI", Font.BOLD, 10));
    lblCorreo.setForeground(C_PRIMARY);
    lblCorreo.setAlignmentX(LEFT_ALIGNMENT);
    body.add(lblCorreo);
    body.add(Box.createVerticalStrut(5));

    FieldFx fCorreo = new FieldFx("", "artista@ejemplo.com", timers);
    fCorreo.setAlignmentX(LEFT_ALIGNMENT);
    fCorreo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    body.add(fCorreo);
    body.add(Box.createVerticalStrut(22));

    // Botones
    JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    btns.setOpaque(false);
    btns.setAlignmentX(LEFT_ALIGNMENT);
    btns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

    BtnFx bCancelar = new BtnFx("Cancelar", false, timers);
    BtnFx bEnviar   = new BtnFx("💳  Generar y enviar", true, timers);
    bCancelar.setPreferredSize(new Dimension(110, 40));
    bEnviar.setPreferredSize(new Dimension(180, 40));

    bCancelar.addActionListener(e -> cerrarConFade(dlg));
    bEnviar.addActionListener(e -> {
        String correo = fCorreo.getText().trim();
        if (correo.isBlank() || !correo.contains("@")) {
            fCorreo.shake();
            toast("Correo inválido", MainFrame.ToastType.ERROR);
            return;
        }
        cerrarConFade(dlg);
        toast("📧 Generando y enviando factura...", MainFrame.ToastType.INFO);
        new Thread(() -> {
            try {
                services.FacturaService fs = new services.FacturaService();
                model.Factura f = fs.generarYEnviar(sesion, correo);
                SwingUtilities.invokeLater(() -> {
                    if ("ENVIADA".equals(f.getEstado()))
                        toast("✓ Factura " + f.getNumeroFactura() + " enviada a " + correo, MainFrame.ToastType.SUCCESS);
                    else
                        toast("Factura generada pero no enviada — revisa config/email.properties", MainFrame.ToastType.INFO);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> toast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR));
            }
        }).start();
    });

    btns.add(bCancelar);
    btns.add(bEnviar);
    body.add(btns);
    body.add(Box.createVerticalStrut(10));

    root.add(body, BorderLayout.CENTER);

    dlg.setContentPane(root);
    dlg.getRootPane().setDefaultButton(bEnviar);
    dlg.setSize(420, 560);
    dlg.setLocationRelativeTo(this);
    dlg.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override public void windowClosed(java.awt.event.WindowEvent e) {
            timers.forEach(Timer::stop);
        }
    });
    abrirConFade(dlg);
    dlg.setVisible(true);
}

    // ── ANIMACIONES DEL DIÁLOGO ─────────────────────────────────────
    private void abrirConFade(JDialog dlg) {
        try { dlg.setOpacity(0f); } catch (Exception ignore) { return; }
        Timer t = new Timer(16, null);
        final long ini = System.currentTimeMillis();
        final int  dur = 220;
        t.addActionListener(ev -> {
            float p     = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
            float eased = 1f - (float) Math.pow(1 - p, 3);
            try { dlg.setOpacity(eased); } catch (Exception ignore) {}
            if (p >= 1f) t.stop();
        });
        SwingUtilities.invokeLater(t::start);
    }

=======

    // ── ANIMACIONES ─────────────────────────────────────────────────
    private void abrirConFade(JDialog dlg) {
        try { dlg.setOpacity(0f); } catch (Exception ignore) { return; }
        Timer t = new Timer(16, null);
        final long ini = System.currentTimeMillis();
        t.addActionListener(ev -> {
            float p = Math.min(1f, (System.currentTimeMillis() - ini) / 220f);
            try { dlg.setOpacity(1f - (float) Math.pow(1 - p, 3)); }
            catch (Exception ignore) {}
            if (p >= 1f) t.stop();
        });
        SwingUtilities.invokeLater(t::start);
    }

>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
    private void cerrarConFade(JDialog dlg) {
        try { dlg.setOpacity(1f); } catch (Exception ignore) { dlg.dispose(); return; }
        Timer t = new Timer(16, null);
        final long ini = System.currentTimeMillis();
<<<<<<< HEAD
        final int  dur = 160;
        t.addActionListener(ev -> {
            float p = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
=======
        t.addActionListener(ev -> {
            float p = Math.min(1f, (System.currentTimeMillis() - ini) / 160f);
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
            try { dlg.setOpacity(1f - p); } catch (Exception ignore) {}
            if (p >= 1f) { t.stop(); dlg.dispose(); }
        });
        t.start();
    }

    private void animarEntradaFilas(List<JComponent> filas, List<Timer> timersDlg) {
        for (int i = 0; i < filas.size(); i++) {
            JComponent c = filas.get(i);
            c.putClientProperty("fx_appear", 0f);
<<<<<<< HEAD
            final int idx = i;
            Timer t = new Timer(16, null);
            final long ini = System.currentTimeMillis() + idx * 55L;
            final int  dur = 320;
            t.addActionListener(ev -> {
                long now = System.currentTimeMillis();
                if (now < ini) return;
                float p     = Math.min(1f, (now - ini) / (float) dur);
                float eased = 1f - (float) Math.pow(1 - p, 3);
                c.putClientProperty("fx_appear", eased);
=======
            final long ini = System.currentTimeMillis() + i * 55L;
            Timer t = new Timer(16, null);
            t.addActionListener(ev -> {
                long now = System.currentTimeMillis();
                if (now < ini) return;
                float p = Math.min(1f, (now - ini) / 320f);
                c.putClientProperty("fx_appear", 1f - (float) Math.pow(1 - p, 3));
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
                c.repaint();
                if (p >= 1f) t.stop();
            });
            timersDlg.add(t);
            t.start();
        }
    }

<<<<<<< HEAD
    // ── CONSTRUCTORES DE FILAS DEL FORM ─────────────────────────────
=======
    // ── HELPERS DE FORMULARIO ────────────────────────────────────────
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
    private JPanel filaCampo(String label, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(C_PRIMARY);
        if (campo instanceof JTextField) campo.setPreferredSize(new Dimension(0, 40));
        if (campo instanceof JComboBox)  campo.setPreferredSize(new Dimension(0, 40));
        p.add(l,     BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private JPanel filaDoble(String l1, JComponent c1, String l2, JComponent c2) {
        JPanel p = new JPanel(new GridLayout(1, 2, 16, 0)) {
            @Override protected void paintComponent(Graphics g) {
                float a = SesionComponents.appearAlpha(this);
                if (a < 1f) ((Graphics2D) g).setComposite(
                        java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, a));
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        p.add(filaCampo(l1, c1));
        p.add(filaCampo(l2, c2));
        return p;
    }

    private JPanel filaSimple(String label, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                float a = SesionComponents.appearAlpha(this);
                if (a < 1f) ((Graphics2D) g).setComposite(
                        java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, a));
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        p.add(filaCampo(label, campo), BorderLayout.CENTER);
        return p;
    }

    private JComponent seccionTituloFx(String texto, List<Timer> timersDlg) {
        JPanel p = new JPanel(new BorderLayout(8, 0)) {
            @Override protected void paintComponent(Graphics g) {
                float a = SesionComponents.appearAlpha(this);
                if (a < 1f) ((Graphics2D) g).setComposite(
                        java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, a));
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
<<<<<<< HEAD

        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(C_ACCENT_CYAN);

        LineaShimmer linea = new LineaShimmer(timersDlg);
        linea.setPreferredSize(new Dimension(0, 18));

        p.add(l,     BorderLayout.WEST);
        p.add(linea, BorderLayout.CENTER);
        return p;
    }

    // ── HELPERS ─────────────────────────────────────────────────────
    private ModernUI.RoundedButton btn(String t, boolean primary, int w) {
        ModernUI.RoundedButton b = new ModernUI.RoundedButton(t, primary);
        if (w > 0) b.setPreferredSize(new Dimension(w, 38));
        return b;
    }

    private DocumentListener docListener(Runnable r) {
        return new DocumentListener() {
            public void insertUpdate (javax.swing.event.DocumentEvent e) { r.run(); }
            public void removeUpdate (javax.swing.event.DocumentEvent e) { r.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { r.run(); }
        };
    }

    private Optional<Double> parseDouble(String s) {
        try { return Optional.of(Double.parseDouble(s.trim())); }
        catch (NumberFormatException e) { return Optional.empty(); }
    }

=======
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(C_ACCENT_CYAN);
        LineaShimmer linea = new LineaShimmer(timersDlg);
        linea.setPreferredSize(new Dimension(0, 18));
        p.add(l,     BorderLayout.WEST);
        p.add(linea, BorderLayout.CENTER);
        return p;
    }

    // ── HELPERS GENERALES ───────────────────────────────────────────
    private ModernUI.RoundedButton btn(String t, boolean primary, int w) {
        ModernUI.RoundedButton b = new ModernUI.RoundedButton(t, primary);
        if (w > 0) b.setPreferredSize(new Dimension(w, 38));
        return b;
    }

    private DocumentListener docListener(Runnable r) {
        return new DocumentListener() {
            public void insertUpdate (javax.swing.event.DocumentEvent e) { r.run(); }
            public void removeUpdate (javax.swing.event.DocumentEvent e) { r.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { r.run(); }
        };
    }

    private String iniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "??";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length == 1) return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        return (partes[0].charAt(0) + "" + partes[partes.length - 1].charAt(0)).toUpperCase();
    }

>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
    private void toast(String msg, MainFrame.ToastType t) {
        MainFrame.showToast(msg, t);
    }
}