package view;

import model.Artista;
import model.Productor;
import model.Sesion;
import service.SesionServicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static view.ModernUI.*;
 
public class formSesion extends JPanel {

    // =========================================================
    //  PALETA
    // =========================================================
    private static final Color C_BG_DARK     = new Color(0x04111F);
    private static final Color C_CARD_BG     = new Color(0x061829);
    private static final Color C_ROW_BG      = new Color(0x071E30);
    private static final Color C_ROW_SEL     = new Color(0x0D3560);
    private static final Color C_PRIMARY     = new Color(0x1A6EBE);
    private static final Color C_ACCENT_BLUE = new Color(0x2196F3);
    private static final Color C_ACCENT_CYAN = new Color(0x00BCD4);
    private static final Color C_TEXT_PRI    = new Color(0xE8EFF7);
    private static final Color C_TEXT_MUT    = new Color(0x5A7A9A);
    private static final Color C_OK          = new Color(0x4CAF50);   // Finalizada
    private static final Color C_WARN        = new Color(0xFFA726);   // En curso
    private static final Color C_ERR         = new Color(0xEF5350);   // Cancelada
    private static final Color C_PROG        = new Color(0x42A5F5);   // Programada
    private static final Color C_BORDER      = new Color(0x0D2A45);

    // =========================================================
    //  CONSTANTES
    // =========================================================
    private static final String[] COLS    = {"ID","Nombre sesión","Fecha","Hora inicio","Hora fin","Artista","Productor","Estado"};
    private static final String[] ESTADOS = Sesion.ESTADOS_VALIDOS;

    private static final Font FT  = new Font("Segoe UI", Font.BOLD,  24);
    private static final Font FS  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FE  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FH  = new Font("Segoe UI", Font.BOLD,  11);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String VISTA_TABLA    = "tabla";
    private static final String VISTA_TARJETAS = "tarjetas";

    // =========================================================
    //  SERVICIO
    // =========================================================
    private final SesionServicio sesionServicio;

    // =========================================================
    //  DATOS LOCALES (catálogos)
    // =========================================================
    private final List<Artista>   artistas    = new ArrayList<>();
    private final List<Productor> productores = new ArrayList<>();
    private final List<String>    cabinas     = new ArrayList<>();
    private final List<Sesion>    sesiones    = new ArrayList<>();

    // =========================================================
    //  COMPONENTES UI
    // =========================================================
    private ModernUI.RoundedTextField busqueda;
    private Sesion                    seleccionada;
    private boolean                   modoTarjetas = false;

    private CardLayout cardLayout;
    private JPanel     vistaCentral;
    private JPanel     gridTarjetas;
    private ModernUI.RoundedButton btnVista;

    // KPIs
    private final JLabel stTotal = new JLabel("0");
    private final JLabel stProg  = new JLabel("0");
    private final JLabel stCurso = new JLabel("0");
    private final JLabel stCosto = new JLabel("$0");

    // Panel lateral
    private JPanel rankingBox;
    private JLabel resDuracion, resCabina, resProductor;

    // Lista principal
    private JScrollPane listaScroll;
    private JPanel      listaCont;

    // =========================================================
    //  CONSTRUCTOR
    // =========================================================

    /**
     * Constructor principal — recibe el servicio inyectado.
     *
     * @param sesionServicio instancia del servicio (no null).
     */
    public formSesion(SesionServicio sesionServicio) {
        this.sesionServicio = sesionServicio;

        setOpaque(false);
        setLayout(new BorderLayout());
        cargarCombos();
        cargarSesionesDesdeServicio();   // ← carga desde Oracle

        JPanel norte = new JPanel();
        norte.setOpaque(false);
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(headerPanel());
        norte.add(statsPanel());
        add(norte, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(13, 0));
        centro.setOpaque(false);
        centro.add(centroVistas(),  BorderLayout.CENTER);
        centro.add(panelLateral(),  BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);

        aplicarFiltro();
    }

    /**
     * Constructor de compatibilidad: instancia el servicio por defecto.
     */
    public formSesion() {
        this(crearServicioPorDefecto());
    }

    private static SesionServicio crearServicioPorDefecto() {
        try {
            return new SesionServicio();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo inicializar SesionServicio", e);
        }
    }

    // =========================================================
    //  INTEGRACIÓN CON SERVICIO  (corregida)
    // =========================================================

    /**
     * Carga todas las sesiones desde Oracle a través del servicio.
     * Método del servicio: listar() — no listarTodos().
     */
    private void cargarSesionesDesdeServicio() {
        sesiones.clear();
        try {
            List<Sesion> lista = sesionServicio.listar();   // ← listar(), no listarTodos()
            if (lista != null) sesiones.addAll(lista);
        } catch (Exception ex) {
            toast("Error al cargar sesiones: " + ex.getMessage(), MainFrame.ToastType.ERROR);
            ex.printStackTrace();
        }
    }

    /**
     * Inserta una nueva sesión en Oracle.
     * Devuelve el ID generado por la secuencia (> 0) o -1 si falló.
     */
    private int crearEnServicio(Sesion sesion) {
        try {
            return sesionServicio.crear(sesion);            // ← INSERT, devuelve ID Oracle
        } catch (IllegalArgumentException ex) {
            // El servicio ya valida; mostramos su mensaje directamente
            toast(ex.getMessage(), MainFrame.ToastType.ERROR);
            return -1;
        } catch (Exception ex) {
            toast("Error al crear: " + ex.getMessage(), MainFrame.ToastType.ERROR);
            ex.printStackTrace();
            return -1;
        }
    }

    /**
     * Actualiza una sesión existente en Oracle.
     * Devuelve true si la operación fue exitosa.
     */
    private boolean actualizarEnServicio(Sesion sesion) {
        try {
            return sesionServicio.actualizar(sesion);       // ← UPDATE
        } catch (IllegalArgumentException ex) {
            toast(ex.getMessage(), MainFrame.ToastType.ERROR);
            return false;
        } catch (Exception ex) {
            toast("Error al actualizar: " + ex.getMessage(), MainFrame.ToastType.ERROR);
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una sesión por ID en Oracle.
     * Devuelve true si fue exitosa.
     */
    private boolean eliminarEnServicio(int idSesion) {
        try {
            return sesionServicio.eliminar(idSesion);       // ← DELETE
        } catch (Exception ex) {
            toast("Error al eliminar: " + ex.getMessage(), MainFrame.ToastType.ERROR);
            ex.printStackTrace();
            return false;
        }
    }

    // =========================================================
    //  CATÁLOGOS
    // =========================================================

    public void cargarCombos() {
        artistas.clear(); productores.clear(); cabinas.clear();
        artistas.add(new Artista(1, null, "Bad Bunny",  "Benito Martinez",
                LocalDate.of(1994,3,10), "M", "Puerto Rico", "Reggaeton",
                "@badbunny", LocalDate.of(2016,1,1),
                Artista.ESTADO_ACTIVO, Artista.TIPO_SOLISTA));
        artistas.add(new Artista(2, null, "Karol G", "Carolina Giraldo",
                LocalDate.of(1991,2,14), "F", "Colombia", "Reggaeton",
                "@karolg", LocalDate.of(2017,1,1),
                Artista.ESTADO_ACTIVO, Artista.TIPO_SOLISTA));
        artistas.add(new Artista(3, null, "Shakira", "Shakira Mebarak",
                LocalDate.of(1977,2,2), "F", "Colombia", "Pop / Rock",
                "@shakira", LocalDate.of(2010,1,1),
                Artista.ESTADO_EN_PAUSA, Artista.TIPO_SOLISTA));
        productores.add(new Productor(1, "Carlos Vives",     "cvives@mail.com",  "3001234567", "Mezcla",        120.0));
        productores.add(new Productor(2, "Andres Torres",    "atorres@mail.com", "3109876543", "Masterizacion",  95.0));
        productores.add(new Productor(3, "Mauricio Rengifo", "mrengifo@mail.com","3154561234", "Composicion",   150.0));
        cabinas.add("Cabina A");
        cabinas.add("Cabina B");
        cabinas.add("Cabina C - Mastering");
    }

    // =========================================================
    //  CABECERA
    // =========================================================

    private JPanel headerPanel() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        JLabel titulo    = lbl("Sesiones",                           FT, C_TEXT_PRI);
        JLabel subtitulo = lbl("GRABACIÓN  ·  CABINAS  ·  AGENDA",  FE, C_TEXT_MUT);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);
        izq.add(titulo);
        izq.add(Box.createVerticalStrut(4));
        izq.add(subtitulo);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        der.setOpaque(false);

        busqueda = new ModernUI.RoundedTextField("Buscar sesión...");
        busqueda.setPreferredSize(new Dimension(210, 38));
        busqueda.getDocument().addDocumentListener(docListener(this::aplicarFiltro));

        ModernUI.RoundedButton bRefr  = btn("↺  Refrescar",   false, 130);
        btnVista                       = btn("Ver tarjetas",   false, 130);
        ModernUI.RoundedButton bNueva  = btn("＋ Nueva sesión", true,  158);

        bRefr.addActionListener(e -> {
            busqueda.setText("");
            cargarSesionesDesdeServicio();   // recarga desde Oracle
            aplicarFiltro();
            toast("Lista actualizada", MainFrame.ToastType.INFO);
        });
        btnVista.addActionListener(e -> alternarVista());
        bNueva.addActionListener(e -> openForm(null));

        der.add(busqueda);
        der.add(btnVista);
        der.add(bRefr);
        der.add(bNueva);

        p.add(izq, BorderLayout.WEST);
        p.add(der, BorderLayout.EAST);
        return p;
    }

    // =========================================================
    //  KPI CARDS
    // =========================================================

    private JPanel statsPanel() {
        JPanel p = new JPanel(new GridLayout(1, 4, 12, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setBorder(new EmptyBorder(0, 0, 14, 0));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        p.add(statCard("Sesiones totales", stTotal, C_ACCENT_BLUE, "totales"));
        p.add(statCard("Programadas",      stProg,  C_PROG,        "en agenda"));
        p.add(statCard("En curso",         stCurso, C_WARN,        "activas"));
        p.add(statCard("Costo estimado",   stCosto, C_OK,          "acumulado"));
        return p;
    }

    private JPanel statCard(String titulo, JLabel valor, Color acento, String sub) {
        ModernUI.StatCard c = new ModernUI.StatCard(13, acento);
        c.setLayout(new BorderLayout());
        c.setBorder(new EmptyBorder(15, 17, 13, 17));
        JLabel t  = new JLabel(titulo.toUpperCase());
        t.setFont(new Font("Segoe UI", Font.BOLD, 9));
        t.setForeground(C_TEXT_MUT);
        valor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valor.setForeground(acento);
        JLabel sb = new JLabel(sub);
        sb.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        sb.setForeground(new Color(0x2A4A65));
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
        long prog  = sesiones.stream().filter(s -> Sesion.ESTADO_PROGRAMADA.equals(s.getEstadoSesion())).count();
        long curso = sesiones.stream().filter(s -> Sesion.ESTADO_EN_CURSO.equals(s.getEstadoSesion())).count();
        double costo = sesiones.stream().mapToDouble(Sesion::getCostoTotal).sum();
        stTotal.setText(String.valueOf(sesiones.size()));
        stProg.setText(String.valueOf(prog));
        stCurso.setText(String.valueOf(curso));
        stCosto.setText(String.format("$%,.0f", costo));
    }

    // =========================================================
    //  VISTA CENTRAL (tabla / tarjetas)
    // =========================================================

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

    // --- Vista TABLA (filas-tarjeta) ---

    private ModernUI.CardPanel tablaCard() {
        ModernUI.CardPanel card = new ModernUI.CardPanel(16);
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
        bEditar  .setForeground(C_ACCENT_CYAN);
        bEliminar.setForeground(C_ERR);
        bEditar  .addActionListener(e -> {
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

    // --- Estado → color (paleta unificada) ---

    private Color stateColor(String s) {
        if (s == null) return C_PROG;
        return switch (s) {
            case "Finalizada" -> C_OK;
            case "En curso"   -> C_WARN;
            case "Cancelada"  -> C_ERR;
            default           -> C_PROG;
        };
    }

    // --- Utilidades de avatar ---

    private String iniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "?";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length == 1) return partes[0].substring(0, 1).toUpperCase();
        return (partes[0].charAt(0) + "" + partes[1].charAt(0)).toUpperCase();
    }

    private JComponent avatar(String txt, Color color) {
        JLabel a = new JLabel(txt, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        a.setFont(new Font("Segoe UI", Font.BOLD, 13));
        a.setForeground(Color.WHITE);
        a.setOpaque(false);
        a.setPreferredSize(new Dimension(38, 38));
        return a;
    }

    // --- Fila-tarjeta ---

    private JComponent filaSesion(Sesion s) {
        boolean activa = s == seleccionada;
        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(activa ? C_ROW_SEL : C_ROW_BG);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(activa ? C_PRIMARY : C_BORDER);
                g2.setStroke(new BasicStroke(activa ? 2f : 1f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 10, 10);
                if (activa) {
                    g2.setColor(C_ACCENT_CYAN);
                    g2.fillRoundRect(0, 9, 4, getHeight()-19, 4, 4);
                }
                g2.dispose();
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(12, 0));
        fila.setBorder(new EmptyBorder(11, 14, 11, 14));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        fila.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color ac = stateColor(s.getEstadoSesion());
        fila.add(avatar(iniciales(s.getArtista().getNombreArtista()), ac), BorderLayout.WEST);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        JLabel nom = new JLabel(s.getNombreSesion());
        nom.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nom.setForeground(C_TEXT_PRI);
        nom.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel(s.getArtista().getNombreArtista() + "  ·  " + s.getProductor().getNombre());
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
        JLabel fec = new JLabel(s.getFecha().format(FMT));
        fec.setFont(new Font("Segoe UI", Font.BOLD, 11));
        fec.setForeground(new Color(0xB0C8E0));
        fec.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel hor = new JLabel(s.getHoraInicio() + "-" + s.getHoraFin());
        hor.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        hor.setForeground(C_TEXT_MUT);
        hor.setAlignmentX(Component.CENTER_ALIGNMENT);
        fechaBox.add(fec);
        fechaBox.add(hor);
        der.add(fechaBox);

        JLabel costo = new JLabel(String.format("$%,.0f", s.getCostoTotal()));
        costo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        costo.setForeground(C_OK);
        der.add(costo);

        ModernUI.Pildora pill = new ModernUI.Pildora();
        pill.setText(s.getEstadoSesion());
        pill.setAcento(ac);
        der.add(pill);

        fila.add(der, BorderLayout.EAST);

        fila.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                seleccionar(s);
                if (e.getClickCount() == 2) openForm(s);
            }
        });
        return fila;
    }

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

    // =========================================================
    //  VISTA TARJETAS
    // =========================================================

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
        boolean activa = s == seleccionada;
        Color ac = stateColor(s.getEstadoSesion());
        ModernUI.CardPanel c = new ModernUI.CardPanel(14);
        c.setLayout(new BorderLayout(0, 10));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(activa ? C_PRIMARY : C_BORDER, activa ? 2 : 1, true),
                new EmptyBorder(14, 16, 14, 16)));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);
        JPanel idar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        idar.setOpaque(false);
        idar.add(avatar(iniciales(s.getArtista().getNombreArtista()), ac));
        JLabel nom = new JLabel(s.getNombreSesion());
        nom.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nom.setForeground(C_TEXT_PRI);
        idar.add(nom);
        ModernUI.Pildora pill = new ModernUI.Pildora();
        pill.setText(s.getEstadoSesion());
        pill.setAcento(ac);
        top.add(idar, BorderLayout.CENTER);
        top.add(pill, BorderLayout.EAST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(filaTarjeta(s.getFecha().format(FMT) + "   " + s.getHoraInicio() + "-" + s.getHoraFin()));
        info.add(Box.createVerticalStrut(4));
        info.add(filaTarjeta("Artista:   " + s.getArtista().getNombreArtista()));
        info.add(Box.createVerticalStrut(4));
        info.add(filaTarjeta("Productor: " + s.getProductor().getNombre()));

        JLabel costo = new JLabel(String.format("$%,.0f", s.getCostoTotal()));
        costo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        costo.setForeground(C_OK);

        c.add(top,   BorderLayout.NORTH);
        c.add(info,  BorderLayout.CENTER);
        c.add(costo, BorderLayout.SOUTH);

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

    // =========================================================
    //  PANEL LATERAL
    // =========================================================

    private JComponent panelLateral() {
        ModernUI.CardPanel card = new ModernUI.CardPanel(16);
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
        JSeparator sep = new JSeparator();
        sep.setForeground(C_BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);
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
        card.add(resumenFila("Duración total",   resDuracion,  C_ACCENT_BLUE));
        card.add(Box.createVerticalStrut(7));
        card.add(resumenFila("Cabina más usada", resCabina,    C_PROG));
        card.add(Box.createVerticalStrut(7));
        card.add(resumenFila("Productor top",    resProductor, C_OK));

        return card;
    }

    private JComponent resumenFila(String etiqueta, JLabel valor, Color color) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_ROW_BG);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 9, 9);
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

    private JComponent rankingFila(int pos, Sesion s) {
        Color ac = pos == 1 ? C_ACCENT_BLUE : pos == 2 ? C_PROG : C_BORDER;
        JPanel p = new JPanel(new BorderLayout(9, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_ROW_BG);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 9, 9);
                g2.setColor(ac);
                g2.fillRoundRect(0, 4, 3, getHeight()-9, 3, 3);
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
                g2.setColor(ac);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        num.setFont(new Font("Segoe UI", Font.BOLD, 11));
        num.setForeground(Color.WHITE);
        num.setOpaque(false);
        num.setPreferredSize(new Dimension(22, 22));
        p.add(num, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel n = new JLabel(s.getNombreSesion());
        n.setFont(new Font("Segoe UI", Font.BOLD, 11));
        n.setForeground(C_TEXT_PRI);
        n.setAlignmentX(LEFT_ALIGNMENT);
        JLabel d = new JLabel(s.getArtista().getNombreArtista() + " · " + nombreCabina(s.getIdCabina()));
        d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        d.setForeground(C_TEXT_MUT);
        d.setAlignmentX(LEFT_ALIGNMENT);
        info.add(n);
        info.add(d);
        p.add(info, BorderLayout.CENTER);

        JLabel fec = new JLabel(s.getFecha().format(FMT));
        fec.setFont(new Font("Segoe UI", Font.BOLD, 10));
        fec.setForeground(new Color(0xB0C8E0));
        p.add(fec, BorderLayout.EAST);
        return p;
    }

    private void actualizarPanelLateral() {
        if (rankingBox == null) return;
        rankingBox.removeAll();
        List<Sesion> orden = new ArrayList<>(sesiones);
        orden.sort(Comparator.comparing(Sesion::getFecha));
        int pos = 1;
        for (Sesion s : orden) {
            if (pos > 3) break;
            rankingBox.add(rankingFila(pos, s));
            rankingBox.add(Box.createVerticalStrut(6));
            pos++;
        }
        rankingBox.revalidate();
        rankingBox.repaint();

        double durTotal = sesiones.stream().mapToDouble(Sesion::getDuracion).sum();
        resDuracion.setText(String.format("%.1f h", durTotal));

        String cabina = sesiones.stream()
                .map(s -> nombreCabina(s.getIdCabina()))
                .collect(java.util.stream.Collectors.groupingBy(cb -> cb, java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparingLong(java.util.Map.Entry::getValue))
                .map(java.util.Map.Entry::getKey).orElse("-");
        resCabina.setText(cabina);

        String prod = sesiones.stream()
                .map(s -> s.getProductor().getNombre())
                .collect(java.util.stream.Collectors.groupingBy(pr -> pr, java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparingLong(java.util.Map.Entry::getValue))
                .map(java.util.Map.Entry::getKey).orElse("-");
        resProductor.setText(prod);
    }

    private String nombreCabina(Integer idCabina) {
        if (idCabina != null && idCabina >= 1 && idCabina <= cabinas.size())
            return cabinas.get(idCabina - 1);
        return "-";
    }

    // =========================================================
    //  OPERACIONES CRUD
    // =========================================================

    private List<Sesion> filtrar() {
        String q = busqueda == null ? "" : busqueda.getText().trim().toLowerCase();
        if (q.isEmpty()) return new ArrayList<>(sesiones);
        return sesiones.stream().filter(s ->
                s.getNombreSesion().toLowerCase().contains(q) ||
                s.getArtista().getNombreArtista().toLowerCase().contains(q) ||
                s.getProductor().getNombre().toLowerCase().contains(q) ||
                s.getFecha().format(FMT).contains(q)).toList();
    }

    private void calcularYMostrarCosto(
        JComboBox<Productor> cbProd,
        JTextField fDur,
        JLabel lblCosto) {

    try {

        Productor prod =
                (Productor) cbProd.getSelectedItem();

        if (prod == null ||
            fDur.getText().trim().isEmpty()) {

            lblCosto.setText("Costo estimado: -");
            return;
        }

        double duracion =
                Double.parseDouble(
                        fDur.getText().trim());

        Sesion temp = new Sesion(
                null,                // artista
                prod,                // productor
                null,                // cabina
                "",                  // nombre
                null,                // fecha
                "",                  // hora inicio
                "",                  // hora fin
                duracion,
                Sesion.ESTADO_PROGRAMADA,
                ""
        );

        lblCosto.setText(
                String.format(
                        "Costo estimado: $%,.2f",
                        temp.getCostoTotal()));

        lblCosto.setForeground(
                new Color(22,163,74));

    }
    catch(NumberFormatException ex){

        lblCosto.setText(
                "Costo estimado: valor inválido");

        lblCosto.setForeground(Color.RED);
    }
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
    }

    private void eliminarSeleccionada() {
        if (seleccionada == null) {
            toast("Selecciona una sesión primero", MainFrame.ToastType.INFO);
            return;
        }
        int id = seleccionada.getIdSesion();
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar la sesión #" + String.format("%03d", id) + "?",
                "Z-One — Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (eliminarEnServicio(id)) {           // ← DELETE en Oracle
                sesiones.removeIf(s -> s.getIdSesion() == id);
                seleccionada = null;
                aplicarFiltro();
                toast("Sesión eliminada correctamente", MainFrame.ToastType.SUCCESS);
            }
        }
    }

private void openForm(Sesion se) {
    final boolean isEdit = se != null;

    JDialog dlg = new JDialog(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Editar sesión" : "Nueva sesión", true);
    dlg.setResizable(false);

    // ── Root ──
    JPanel root = new JPanel(new BorderLayout()) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(C_BG_DARK);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    };

    // ── Banda cabecera ──
    root.add(bandaCabeceraSesion(isEdit), BorderLayout.NORTH);

    // ── Cuerpo ──
    JPanel body = new JPanel();
    body.setOpaque(false);
    body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
    body.setBorder(new EmptyBorder(20, 26, 18, 26));

    // ─── Campos ───
    JTextField fNombre = field(isEdit ? se.getNombreSesion()              : "", "Nombre de la sesión");
    JTextField fFecha  = field(isEdit ? se.getFecha().format(FMT)         : "", "dd/MM/yyyy");
    JTextField fHIni   = field(isEdit ? se.getHoraInicio()                : "09:00", "HH:mm");
    JTextField fHFin   = field(isEdit ? se.getHoraFin()                   : "12:00", "HH:mm");
    JTextField fDur    = field(isEdit ? String.valueOf(se.getDuracion())  : "", "Horas (ej: 3.5)");
    JTextField fObs    = field(isEdit && se.getObservaciones() != null
                                       ? se.getObservaciones() : "", "Observaciones opcionales");

    JComboBox<String>    cbEstado = comboStr(ESTADOS, isEdit ? se.getEstadoSesion() : Sesion.ESTADO_PROGRAMADA);
    JComboBox<String>    cbCabina = comboStr(cabinas.toArray(new String[0]), cabinas.get(0));
    if (isEdit && se.getIdCabina() != null && se.getIdCabina() >= 1 && se.getIdCabina() <= cabinas.size())
        cbCabina.setSelectedIndex(se.getIdCabina() - 1);

    JComboBox<Artista>   cbArt  = new JComboBox<>(artistas.toArray(new Artista[0]));
    JComboBox<Productor> cbProd = new JComboBox<>(productores.toArray(new Productor[0]));
    cbArt .setRenderer(objRenderer(v -> v instanceof Artista   ? ((Artista) v).getNombreArtista() : ""));
    cbProd.setRenderer(objRenderer(v -> v instanceof Productor ? ((Productor) v).getNombre()     : ""));
    styleCombo(cbArt);
    styleCombo(cbProd);
    if (isEdit) {
        cbArt .setSelectedItem(se.getArtista());
        cbProd.setSelectedItem(se.getProductor());
    }

    // ─── Sección INFORMACIÓN GENERAL ───
    body.add(seccionTitulo("INFORMACIÓN GENERAL"));
    body.add(Box.createVerticalStrut(14));
    body.add(filaDoble("NOMBRE DE SESIÓN *", fNombre, "FECHA *", fFecha));
    body.add(Box.createVerticalStrut(14));
    body.add(filaDoble("HORA INICIO", fHIni, "HORA FIN", fHFin));
    body.add(Box.createVerticalStrut(14));
    body.add(filaSimple("DURACIÓN (h) *", fDur));
    body.add(Box.createVerticalStrut(22));

    // ─── Sección DETALLES ───
    body.add(seccionTitulo("DETALLES"));
    body.add(Box.createVerticalStrut(14));
    body.add(filaDoble("ESTADO SESIÓN", cbEstado, "CABINA", cbCabina));
    body.add(Box.createVerticalStrut(14));
    body.add(filaDoble("ARTISTA *", cbArt, "PRODUCTOR *", cbProd));
    body.add(Box.createVerticalStrut(14));
    body.add(filaSimple("OBSERVACIONES", fObs));
    body.add(Box.createVerticalStrut(20));

    // ─── Card de costo estimado ───
    JLabel lblCostoValor = new JLabel(isEdit
            ? String.format("$%,.2f", se.getCostoTotal())
            : "$0.00");
    JLabel lblCostoMeta = new JLabel(isEdit
            ? String.format("%.1f h  ·  $%,.0f /h tarifa productor",
                  se.getDuracion(), se.getProductor().getTarifaHora())
            : "—");
    body.add(cardCostoEstimado(lblCostoValor, lblCostoMeta));
    body.add(Box.createVerticalStrut(20));

    // Recalcular costo en vivo
    Runnable recalcular = () -> {
        try {
            Productor pr = (Productor) cbProd.getSelectedItem();
            if (pr == null || fDur.getText().trim().isEmpty()) {
                lblCostoValor.setText("$0.00");
                lblCostoMeta .setText("—");
                return;
            }
            double dur = Double.parseDouble(fDur.getText().trim());
            Sesion temp = new Sesion(null, pr, null, "", null, "", "", dur,
                                     Sesion.ESTADO_PROGRAMADA, "");
            lblCostoValor.setText(String.format("$%,.2f", temp.getCostoTotal()));
            lblCostoMeta .setText(String.format("%.1f h  ·  $%,.0f /h tarifa productor",
                                                dur, pr.getTarifaHora()));
        } catch (NumberFormatException ex) {
            lblCostoValor.setText("—");
            lblCostoMeta .setText("Duración inválida");
        }
    };
    fDur.getDocument().addDocumentListener(docListener(recalcular));
    cbProd.addActionListener(e -> recalcular.run());

    // ─── Botones ───
    JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    btns.setOpaque(false);
    btns.setAlignmentX(LEFT_ALIGNMENT);
    btns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    ModernUI.RoundedButton bCancel = new ModernUI.RoundedButton("Cancelar", false);
    ModernUI.RoundedButton bSave   = new ModernUI.RoundedButton(
            isEdit ? "💾  Guardar cambios" : "✦  Crear sesión", true);
    bCancel.setPreferredSize(new Dimension(120, 40));
    bSave  .setPreferredSize(new Dimension(180, 40));
    bCancel.addActionListener(e -> dlg.dispose());

    bSave.addActionListener(e -> {
        String nm     = fNombre.getText().trim();
        String fd     = fFecha.getText().trim();
        String dr     = fDur.getText().trim();
        String hi     = fHIni.getText().trim();
        String hf     = fHFin.getText().trim();
        String obs    = fObs.getText().trim();
        String estado = (String) cbEstado.getSelectedItem();
        Integer idCab = cbCabina.getSelectedIndex() + 1;
        Artista   art  = (Artista)   cbArt .getSelectedItem();
        Productor prod = (Productor) cbProd.getSelectedItem();

        if (nm.isEmpty()) { toast("El nombre de sesión es obligatorio", MainFrame.ToastType.ERROR); return; }
        if (fd.isEmpty()) { toast("La fecha es obligatoria",            MainFrame.ToastType.ERROR); return; }
        if (dr.isEmpty()) { toast("La duración es obligatoria",         MainFrame.ToastType.ERROR); return; }
        if (art  == null) { toast("Selecciona un artista",              MainFrame.ToastType.ERROR); return; }
        if (prod == null) { toast("Selecciona un productor",            MainFrame.ToastType.ERROR); return; }

        LocalDate fecha;
        try { fecha = LocalDate.parse(fd, FMT); }
        catch (DateTimeParseException ex) {
            toast("Formato de fecha inválido (dd/MM/yyyy)", MainFrame.ToastType.ERROR);
            return;
        }
        double dur = parseDouble(dr).orElse(-1.0);
        if (dur <= 0) { toast("La duración debe ser un número positivo", MainFrame.ToastType.ERROR); return; }

        if (isEdit) {
            se.setNombreSesion(nm); se.setFecha(fecha);
            se.setHoraInicio(hi);   se.setHoraFin(hf);
            se.setDuracion(dur);    se.setArtista(art);
            se.setProductor(prod);  se.setIdCabina(idCab);
            se.setEstadoSesion(estado); se.setObservaciones(obs);

            if (actualizarEnServicio(se)) {
                toast("Sesión actualizada correctamente", MainFrame.ToastType.SUCCESS);
                aplicarFiltro();
                dlg.dispose();
            }
        } else {
            Sesion nueva = new Sesion(0, art, prod, idCab, nm, fecha, hi, hf, dur, estado, obs);
            int idGenerado = crearEnServicio(nueva);
            if (idGenerado > 0) {
                nueva.setIdSesion(idGenerado);
                sesiones.add(nueva);
                seleccionada = nueva;
                toast("Sesión creada correctamente", MainFrame.ToastType.SUCCESS);
                aplicarFiltro();
                dlg.dispose();
            }
        }
    });
    btns.add(bCancel);
    btns.add(bSave);
    body.add(btns);

    // Scroll por si la pantalla es pequeña
    JScrollPane scroll = new JScrollPane(body);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getViewport().setBackground(new Color(0, 0, 0, 0));
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    scroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
    root.add(scroll, BorderLayout.CENTER);

    dlg.setContentPane(root);
    dlg.getRootPane().setDefaultButton(bSave);
    dlg.setSize(640, 720);
    dlg.setLocationRelativeTo(this);
    dlg.setVisible(true);
}
    // =========================================================
    //  HELPERS DE COMPONENTES
    // =========================================================
// =========================================================
//  HELPERS DEL DIÁLOGO REDISEÑADO
// =========================================================

private JPanel bandaCabeceraSesion(boolean isEdit) {
    JPanel band = new JPanel(new BorderLayout(14, 0)) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, new Color(0x0A2A4F),
                    getWidth(), getHeight(), new Color(0x14467E)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 30),
                    0, getHeight(), new Color(255, 255, 255, 0)));
            g2.fillRect(0, 0, getWidth(), getHeight() / 2);
            g2.setColor(C_ACCENT_CYAN);
            g2.fillRect(0, getHeight() - 2, getWidth(), 2);
            g2.dispose();
            super.paintComponent(g);
        }
    };
    band.setOpaque(false);
    band.setBorder(new EmptyBorder(20, 26, 20, 26));
    band.setPreferredSize(new Dimension(0, 90));

    JLabel ico = new JLabel(isEdit ? "✏" : "📅", SwingConstants.CENTER) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 13, 13);
            g2.setColor(new Color(255, 255, 255, 95));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 13, 13);
            g2.dispose();
            super.paintComponent(g);
        }
    };
    ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
    ico.setForeground(Color.WHITE);
    ico.setPreferredSize(new Dimension(50, 50));

    JPanel txt = new JPanel();
    txt.setOpaque(false);
    txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
    JLabel t = lbl(isEdit ? "Editar sesión" : "Nueva sesión",
            new Font("Segoe UI", Font.BOLD, 22), Color.WHITE);
    JLabel s = lbl(isEdit ? "ACTUALIZA LA INFORMACIÓN DE LA SESIÓN"
                          : "REGISTRA UNA NUEVA SESIÓN DE GRABACIÓN",
            new Font("Segoe UI", Font.BOLD, 9), new Color(255, 255, 255, 185));
    t.setAlignmentX(LEFT_ALIGNMENT);
    s.setAlignmentX(LEFT_ALIGNMENT);
    txt.add(Box.createVerticalGlue());
    txt.add(t);
    txt.add(Box.createVerticalStrut(3));
    txt.add(s);
    txt.add(Box.createVerticalGlue());

    band.add(ico, BorderLayout.WEST);
    band.add(txt, BorderLayout.CENTER);
    return band;
}

private JPanel seccionTitulo(String texto) {
    JPanel p = new JPanel(new BorderLayout(0, 4));
    p.setOpaque(false);
    p.setAlignmentX(LEFT_ALIGNMENT);
    p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

    JLabel l = new JLabel(texto);
    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
    l.setForeground(C_ACCENT_CYAN);

    JPanel linea = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, C_ACCENT_CYAN,
                    getWidth() * 0.8f, 0, new Color(0, 0, 0, 0)));
            g2.fillRect(0, getHeight() - 2, getWidth(), 2);
            g2.dispose();
        }
    };
    linea.setOpaque(false);
    linea.setPreferredSize(new Dimension(0, 18));

    p.add(l,     BorderLayout.WEST);
    p.add(linea, BorderLayout.CENTER);
    return p;
}

private JPanel filaCampo(String label, JComponent campo) {
    JPanel p = new JPanel(new BorderLayout(0, 6));
    p.setOpaque(false);
    p.setAlignmentX(LEFT_ALIGNMENT);
    p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

    JLabel l = new JLabel(label);
    l.setFont(new Font("Segoe UI", Font.BOLD, 10));
    l.setForeground(C_ACCENT_BLUE);

    if (campo instanceof JTextField)
        campo.setPreferredSize(new Dimension(0, 40));
    if (campo instanceof JComboBox)
        campo.setPreferredSize(new Dimension(0, 40));

    p.add(l,     BorderLayout.NORTH);
    p.add(campo, BorderLayout.CENTER);
    return p;
}

private JPanel filaDoble(String l1, JComponent c1, String l2, JComponent c2) {
    JPanel p = new JPanel(new GridLayout(1, 2, 16, 0));
    p.setOpaque(false);
    p.setAlignmentX(LEFT_ALIGNMENT);
    p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
    p.add(filaCampo(l1, c1));
    p.add(filaCampo(l2, c2));
    return p;
}

private JPanel filaSimple(String label, JComponent campo) {
    JPanel p = new JPanel(new BorderLayout());
    p.setOpaque(false);
    p.setAlignmentX(LEFT_ALIGNMENT);
    p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
    p.add(filaCampo(label, campo), BorderLayout.CENTER);
    return p;
}

private JPanel cardCostoEstimado(JLabel valor, JLabel meta) {
    JPanel card = new JPanel(new BorderLayout(14, 0)) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0,
                    new Color(C_PRIMARY.getRed(), C_PRIMARY.getGreen(), C_PRIMARY.getBlue(), 55),
                    getWidth(), 0, new Color(0x061829)));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.setColor(new Color(C_ACCENT_CYAN.getRed(), C_ACCENT_CYAN.getGreen(),
                    C_ACCENT_CYAN.getBlue(), 120));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setColor(C_ACCENT_CYAN);
            g2.fillRoundRect(0, 14, 4, getHeight() - 28, 4, 4);
            g2.dispose();
            super.paintComponent(g);
        }
    };
    card.setOpaque(false);
    card.setBorder(new EmptyBorder(16, 20, 16, 20));
    card.setAlignmentX(LEFT_ALIGNMENT);
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 82));

    JLabel ico = new JLabel("💰", SwingConstants.CENTER);
    ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
    ico.setPreferredSize(new Dimension(40, 40));

    JPanel txt = new JPanel();
    txt.setOpaque(false);
    txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
    JLabel lTit = lbl("COSTO ESTIMADO",
            new Font("Segoe UI", Font.BOLD, 10), C_ACCENT_CYAN);
    lTit.setAlignmentX(LEFT_ALIGNMENT);
    meta.setFont(new Font("Segoe UI", Font.PLAIN, 10));
    meta.setForeground(C_TEXT_MUT);
    meta.setAlignmentX(LEFT_ALIGNMENT);
    txt.add(lTit);
    txt.add(Box.createVerticalStrut(3));
    txt.add(meta);

    valor.setFont(new Font("Segoe UI", Font.BOLD, 26));
    valor.setForeground(C_OK);
    valor.setHorizontalAlignment(SwingConstants.RIGHT);

    card.add(ico,   BorderLayout.WEST);
    card.add(txt,   BorderLayout.CENTER);
    card.add(valor, BorderLayout.EAST);
    return card;
}

    private JLabel lbl(String t, Font f, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    private JTextField field(String val, String ph) {
        ModernUI.RoundedTextField f = new ModernUI.RoundedTextField(ph);
        f.setText(val);
        f.setPreferredSize(new Dimension(10, 40));
        return f;
    }

    private ModernUI.RoundedButton btn(String t, boolean primary, int w) {
        ModernUI.RoundedButton b = new ModernUI.RoundedButton(t, primary);
        if (w > 0) b.setPreferredSize(new Dimension(w, 38));
        return b;
    }

    private JPanel formRow(String label, JComponent comp) {
        JPanel r = new JPanel(new BorderLayout(0, 4));
        r.setOpaque(false);
        r.setAlignmentX(LEFT_ALIGNMENT);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        JLabel l = new JLabel(label);
        l.setFont(FE);
        l.setForeground(C_TEXT_MUT);
        r.add(l,    BorderLayout.NORTH);
        r.add(comp, BorderLayout.CENTER);
        return r;
    }

    private JComboBox<String> comboStr(String[] opts, String sel) {
        JComboBox<String> cb = ModernUI.roundedCombo(opts);
        cb.setSelectedItem(sel);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean s, boolean f) {
                JLabel c = new JLabel(v == null ? "" : v.toString());
                c.setBackground(i == -1 ? INPUT_BG : s ? C_PRIMARY : INPUT_BG);
                c.setForeground(C_TEXT_PRI);
                c.setBorder(new EmptyBorder(7, 12, 7, 12));
                c.setOpaque(true);
                return c;
            }
        });
        return cb;
    }

    private <T> DefaultListCellRenderer objRenderer(Function<Object, String> fn) {
        return new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean s, boolean f) {
                JLabel c = new JLabel(fn.apply(v));
                c.setBackground(s && i != -1 ? C_PRIMARY : INPUT_BG);
                c.setForeground(C_TEXT_PRI);
                c.setBorder(new EmptyBorder(7, 12, 7, 12));
                c.setOpaque(true);
                return c;
            }
        };
    }

    private <T> void styleCombo(JComboBox<T> cb) {
        cb.setEditable(false);
        cb.setFont(FS);
        cb.setForeground(C_TEXT_PRI);
        cb.setBackground(INPUT_BG);
        cb.setOpaque(false);
        cb.setMaximumRowCount(6);
        cb.setFocusable(false);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8)));
    }

    private JPanel darkPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(28, 32, 28, 32));
        return p;
    }

    // =========================================================
    //  UTILIDADES
    // =========================================================

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

    private void toast(String msg, MainFrame.ToastType t) {
        MainFrame.showToast(msg, t);
    }
}