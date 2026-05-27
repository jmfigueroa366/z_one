package view;

import model.Artista;
import model.Productor;
import model.Sesion;

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

    // --- Constantes ---

    private static final String[] COLS    = {"ID","Nombre sesion","Fecha","Hora inicio","Hora fin","Artista","Productor","Estado"};
    private static final String[] ESTADOS = Sesion.ESTADOS_VALIDOS;

    // Paleta azul: tomada de ModernUI (re-skin global)
    private static final Color CARD_INNER = new Color(10, 50, 92);     // fondo filas/inputs
    private static final Color ROW_BG     = new Color(8, 40, 76);      // fila normal
    private static final Color ROW_SEL    = new Color(14, 80, 137);    // fila seleccionada
    private static final Color OK_COLOR   = new Color(79, 232, 210);   // turquesa (finalizada/exito)
    private static final Color CURSO_COL  = new Color(86, 194, 232);   // celeste (en curso)
    private static final Color PROG_COL   = new Color(151, 202, 219);  // celeste claro (programada)

    private static final Font FT = new Font("Segoe UI",Font.BOLD,24);
    private static final Font FS = new Font("Segoe UI",Font.PLAIN,13);
    private static final Font FE = new Font("Segoe UI",Font.PLAIN,11);
    private static final Font FH = new Font("Segoe UI",Font.BOLD,11);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String VISTA_TABLA    = "tabla";
    private static final String VISTA_TARJETAS = "tarjetas";

    // --- Estado ---

    private final List<Artista>   artistas    = new ArrayList<>();
    private final List<Productor> productores = new ArrayList<>();
    private final List<String>    cabinas     = new ArrayList<>();
    private final List<Sesion>    sesiones    = new ArrayList<>();

    private DefaultTableModel modelo;
    private JTable            tabla;
    private ModernUI.RoundedTextField busqueda;
    private int               nextId = 4;

    private Sesion            seleccionada;
    private boolean           modoTarjetas = false;

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
    private JPanel  rankingBox;
    private JLabel  resDuracion, resCabina, resProductor;

    // --- Constructor ---

    public formSesion() {
        setOpaque(false);
        setLayout(new BorderLayout());
        cargarCombos();
        sesiones.add(new Sesion(1,artistas.get(0),productores.get(0),1,"Grabacion Titi",
                LocalDate.of(2025,3,10),"09:00","13:00",4.0,Sesion.ESTADO_FINALIZADA,""));
        sesiones.add(new Sesion(2,artistas.get(1),productores.get(2),2,"Sesion TQG",
                LocalDate.of(2025,4,22),"14:00","19:30",5.5,Sesion.ESTADO_PROGRAMADA,""));
        sesiones.add(new Sesion(3,artistas.get(2),productores.get(1),1,"Sesion Pop",
                LocalDate.of(2025,5,1),"10:00","12:00",2.0,Sesion.ESTADO_EN_CURSO,""));

        JPanel norte = new JPanel();
        norte.setOpaque(false);
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(headerPanel());
        norte.add(statsPanel());
        add(norte, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(13, 0));
        centro.setOpaque(false);
        centro.add(centroVistas(), BorderLayout.CENTER);
        centro.add(panelLateral(), BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);

        aplicarFiltro();
    }

    // --- API publica ---

    public void guardar() { /* SesionServicio.guardar(sesion) - pendiente */ }

    public void calcularYMostrarCosto(JComboBox<Productor> cp, JTextField cd, JLabel lbl) {
        Productor p = (Productor) cp.getSelectedItem();
        double h = parseDouble(cd.getText()).orElse(-1.0);
        if (p == null || h <= 0) { lbl.setText("Costo estimado: -"); lbl.setForeground(TEXT_MUTED); return; }
        lbl.setText(String.format("Costo estimado: $%.2f  (%.1f h x $%.0f/h)", h*p.getTarifaHora(), h, p.getTarifaHora()));
        lbl.setForeground(OK_COLOR);
    }

    public void cargarCombos() {
        artistas.clear(); productores.clear(); cabinas.clear();
        artistas.add(new Artista(1, null, "Bad Bunny", "Benito Martinez",
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
        cabinas.add("Cabina A"); cabinas.add("Cabina B"); cabinas.add("Cabina C - Mastering");
    }

    // --- Encabezado ---

  // =====================================================================
//  REEMPLAZA el método headerPanel() en tu formSesion.java
//  Solo este método cambia — todo lo demás queda igual
// =====================================================================

private JPanel headerPanel() {
    // Panel raíz: título a la izquierda, acciones a la derecha — UNA sola fila
    JPanel p = new JPanel(new BorderLayout(16, 0));
    p.setOpaque(false);
    p.setAlignmentX(LEFT_ALIGNMENT);
    p.setBorder(new EmptyBorder(0, 0, 14, 0));  // espacio abajo antes de los KPI

    // ── Lado izquierdo: título + subtítulo ───────────────────────────
    JPanel izq = new JPanel();
    izq.setOpaque(false);
    izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

    JLabel titulo    = lbl("Sesiones",              FT,      TEXT_PRIMARY);
    JLabel subtitulo = lbl("GRABACIÓN  ·  CABINAS  ·  AGENDA", FE, TEXT_MUTED);
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    subtitulo.setAlignmentX(LEFT_ALIGNMENT);

    izq.add(titulo);
    izq.add(Box.createVerticalStrut(4));
    izq.add(subtitulo);

    // ── Lado derecho: buscador + botones ─────────────────────────────
    JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    der.setOpaque(false);

    busqueda = new ModernUI.RoundedTextField("Buscar sesion...");
    busqueda.setPreferredSize(new Dimension(210, 38));
    busqueda.getDocument().addDocumentListener(docListener(this::aplicarFiltro));

    ModernUI.RoundedButton bRefr  = btn("↺  Refrescar",   false, 130);
    btnVista                       = btn("Ver tarjetas",   false, 130);
    ModernUI.RoundedButton bNueva  = btn("＋ Nueva sesion", true,  158);

    bRefr .addActionListener(e -> { busqueda.setText(""); aplicarFiltro(); toast("Lista actualizada", MainFrame.ToastType.INFO); });
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

    // --- Tarjetas KPI ---

    private JPanel statsPanel() {
        JPanel p = new JPanel(new GridLayout(1, 4, 12, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setBorder(new EmptyBorder(0, 0, 14, 0));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        p.add(statCard("Sesiones totales", stTotal, PRIMARY,      "totales"));
        p.add(statCard("Programadas",      stProg,  PROG_COL,     "en agenda"));
        p.add(statCard("En curso",         stCurso, CURSO_COL,    "activas"));
        p.add(statCard("Costo estimado",   stCosto, OK_COLOR,     "acumulado"));
        return p;
    }

    private JPanel statCard(String titulo, JLabel valor, Color acento, String sub) {
        ModernUI.StatCard c = new ModernUI.StatCard(13, acento);
        c.setLayout(new BorderLayout());
        c.setBorder(new EmptyBorder(15, 17, 13, 17));
        JLabel t = new JLabel(titulo.toUpperCase());
        t.setFont(new Font("Segoe UI", Font.BOLD, 9));
        t.setForeground(TEXT_MUTED);
        valor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valor.setForeground(acento);
        JLabel sb = new JLabel(sub);
        sb.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        sb.setForeground(new Color(60, 110, 150));
        JPanel centro = new JPanel(); centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        t.setAlignmentX(LEFT_ALIGNMENT); valor.setAlignmentX(LEFT_ALIGNMENT); sb.setAlignmentX(LEFT_ALIGNMENT);
        centro.add(t); centro.add(Box.createVerticalStrut(3)); centro.add(valor);
        centro.add(Box.createVerticalStrut(2)); centro.add(sb);
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

    // --- Vista central (tabla / tarjetas) ---

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

    // --- Vista TABLA (lista de filas-tarjeta) ---

    private ModernUI.CardPanel tablaCard() {
        ModernUI.CardPanel card = new ModernUI.CardPanel(16);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel cab = new JPanel(new BorderLayout()); cab.setOpaque(false);
        JLabel tit = new JLabel("Lista de sesiones");
        tit.setFont(new Font("Segoe UI",Font.BOLD,14)); tit.setForeground(TEXT_PRIMARY);
        cab.add(tit, BorderLayout.WEST);
        card.add(cab, BorderLayout.NORTH);

        // La "tabla" real es una lista vertical de filas tipo tarjeta
        gridTarjetas = null; // no se usa aqui
        JScrollPane sc = construirListaScroll();
        card.add(sc, BorderLayout.CENTER);
        return card;
    }

    private JScrollPane listaScroll;
    private JPanel      listaCont;

    private JScrollPane construirListaScroll() {
        listaCont = new JPanel();
        listaCont.setOpaque(false);
        listaCont.setLayout(new BoxLayout(listaCont, BoxLayout.Y_AXIS));
        JPanel wrap = new JPanel(new BorderLayout()); wrap.setOpaque(false);
        wrap.add(listaCont, BorderLayout.NORTH);
        listaScroll = new JScrollPane(wrap);
        listaScroll.setBorder(BorderFactory.createEmptyBorder());
        listaScroll.setOpaque(false);
        listaScroll.getViewport().setOpaque(false);
        listaScroll.getVerticalScrollBar().setUnitIncrement(18);
        return listaScroll;
    }

    /** Mantengo modelo/tabla por compatibilidad, pero la vista usa filas-tarjeta. */
    private void styleTable() { }

  // ✅ OPCIÓN 2 — definir el color directamente
private Color stateColor(String s) {
    return switch(s) {
        case "Finalizada" -> OK_COLOR;
        case "En curso"   -> CURSO_COL;
        case "Cancelada"  -> new Color(255, 80, 120);  // ← rojo directo
        default           -> PROG_COL;
    };
}

    /** Iniciales del artista para el avatar (ej: "Bad Bunny" -> "BB"). */
    private String iniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "?";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length == 1) return partes[0].substring(0,1).toUpperCase();
        return (partes[0].charAt(0) + "" + partes[1].charAt(0)).toUpperCase();
    }

    /** Avatar circular con iniciales y color de acento. */
    private JComponent avatar(String txt, Color color) {
        JLabel a = new JLabel(txt, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        a.setFont(new Font("Segoe UI",Font.BOLD,13));
        a.setForeground(Color.WHITE);
        a.setOpaque(false);
        a.setPreferredSize(new Dimension(38,38));
        return a;
    }

    /** Construye una fila-tarjeta para una sesion. */
    private JComponent filaSesion(Sesion s) {
        boolean activa = s == seleccionada;
        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(activa ? ROW_SEL : ROW_BG);
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.setColor(activa ? PRIMARY_HOVER : new Color(26,72,120,110));
                g2.setStroke(new BasicStroke(activa?2f:1f));
                g2.drawRoundRect(1,1,getWidth()-3,getHeight()-3,12,12);
                if (activa) {
                    g2.setColor(ACCENT_CYAN);
                    g2.fillRoundRect(0,9,4,getHeight()-19,4,4);
                }
                g2.dispose();
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(12,0));
        fila.setBorder(new EmptyBorder(11,14,11,14));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        fila.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color ac = stateColor(s.getEstadoSesion());
        fila.add(avatar(iniciales(s.getArtista().getNombreArtista()), ac), BorderLayout.WEST);

        JPanel centro = new JPanel(); centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        JLabel nom = new JLabel(s.getNombreSesion());
        nom.setFont(new Font("Segoe UI",Font.BOLD,13)); nom.setForeground(TEXT_PRIMARY);
        nom.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel(s.getArtista().getNombreArtista() + "  -  " + s.getProductor().getNombre());
        sub.setFont(new Font("Segoe UI",Font.PLAIN,10)); sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        centro.add(nom); centro.add(Box.createVerticalStrut(2)); centro.add(sub);
        fila.add(centro, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT,14,0)); der.setOpaque(false);

        JPanel fechaBox = new JPanel(); fechaBox.setOpaque(false);
        fechaBox.setLayout(new BoxLayout(fechaBox, BoxLayout.Y_AXIS));
        JLabel fec = new JLabel(s.getFecha().format(FMT));
        fec.setFont(new Font("Segoe UI",Font.BOLD,11)); fec.setForeground(TEXT_SECONDARY);
        fec.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel hor = new JLabel(s.getHoraInicio()+"-"+s.getHoraFin());
        hor.setFont(new Font("Segoe UI",Font.PLAIN,9)); hor.setForeground(TEXT_MUTED);
        hor.setAlignmentX(Component.CENTER_ALIGNMENT);
        fechaBox.add(fec); fechaBox.add(hor);
        der.add(fechaBox);

        JLabel costo = new JLabel(String.format("$%,.0f", s.getCostoTotal()));
        costo.setFont(new Font("Segoe UI",Font.BOLD,13)); costo.setForeground(OK_COLOR);
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
            listaCont.add(Box.createVerticalStrut(8));
        }
        if (data.isEmpty()) {
            JLabel vacio = new JLabel("No hay sesiones para mostrar");
            vacio.setFont(FS); vacio.setForeground(TEXT_MUTED);
            vacio.setBorder(new EmptyBorder(30,10,30,10));
            listaCont.add(vacio);
        }
        listaCont.revalidate();
        listaCont.repaint();
    }

    // --- Vista TARJETAS ---

    private JScrollPane tarjetasCard() {
        gridTarjetas = new JPanel(new GridLayout(0, 3, 14, 14));
        gridTarjetas.setOpaque(false);
        gridTarjetas.setBorder(new EmptyBorder(2,2,2,2));

        JPanel cont = new JPanel(new BorderLayout());
        cont.setOpaque(false);
        cont.add(gridTarjetas, BorderLayout.NORTH);

        JScrollPane sc = new JScrollPane(cont);
        sc.setBorder(BorderFactory.createEmptyBorder()); sc.setOpaque(false);
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
            BorderFactory.createLineBorder(activa ? PRIMARY_HOVER : new Color(26,72,120,90), activa ? 2 : 1, true),
            new EmptyBorder(14,16,14,16)));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel top = new JPanel(new BorderLayout(8,0)); top.setOpaque(false);
        JPanel idar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0)); idar.setOpaque(false);
        idar.add(avatar(iniciales(s.getArtista().getNombreArtista()), ac));
        JLabel nom = new JLabel(s.getNombreSesion());
        nom.setFont(new Font("Segoe UI",Font.BOLD,14)); nom.setForeground(TEXT_PRIMARY);
        idar.add(nom);
        ModernUI.Pildora pill = new ModernUI.Pildora();
        pill.setText(s.getEstadoSesion()); pill.setAcento(ac);
        top.add(idar, BorderLayout.CENTER); top.add(pill, BorderLayout.EAST);

        JPanel info = new JPanel(); info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(filaTarjeta(s.getFecha().format(FMT) + "   " + s.getHoraInicio() + "-" + s.getHoraFin()));
        info.add(Box.createVerticalStrut(4));
        info.add(filaTarjeta("Artista:  " + s.getArtista().getNombreArtista()));
        info.add(Box.createVerticalStrut(4));
        info.add(filaTarjeta("Productor:  " + s.getProductor().getNombre()));

        JLabel costo = new JLabel(String.format("$%,.0f", s.getCostoTotal()));
        costo.setFont(new Font("Segoe UI",Font.BOLD,16)); costo.setForeground(OK_COLOR);

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
        l.setFont(FE); l.setForeground(TEXT_MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    // --- Panel lateral: ranking + resumen ---

    private JComponent panelLateral() {
        ModernUI.CardPanel card = new ModernUI.CardPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(290, 10));
        card.setBorder(new EmptyBorder(18,18,18,18));

        JLabel t1 = new JLabel("PROXIMAS SESIONES");
        t1.setFont(new Font("Segoe UI",Font.BOLD,11)); t1.setForeground(ACCENT_CYAN);
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
        sep.setForeground(new Color(26,72,120)); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        card.add(sep);
        card.add(Box.createVerticalStrut(14));

        JLabel t2 = new JLabel("RESUMEN");
        t2.setFont(new Font("Segoe UI",Font.BOLD,11)); t2.setForeground(ACCENT_CYAN);
        t2.setAlignmentX(LEFT_ALIGNMENT);
        card.add(t2);
        card.add(Box.createVerticalStrut(10));

        resDuracion  = new JLabel("-");
        resCabina    = new JLabel("-");
        resProductor = new JLabel("-");
        card.add(resumenFila("Duracion total",  resDuracion,  ACCENT_CYAN));
        card.add(Box.createVerticalStrut(7));
        card.add(resumenFila("Cabina mas usada", resCabina,   PROG_COL));
        card.add(Box.createVerticalStrut(7));
        card.add(resumenFila("Productor top",    resProductor, OK_COLOR));

        return card;
    }

    private JComponent resumenFila(String etiqueta, JLabel valor, Color color) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(10,55,102));
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,9,9);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10,12,10,12));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
        JLabel et = new JLabel(etiqueta);
        et.setFont(new Font("Segoe UI",Font.PLAIN,10)); et.setForeground(TEXT_MUTED);
        valor.setFont(new Font("Segoe UI",Font.BOLD,14)); valor.setForeground(color);
        valor.setHorizontalAlignment(SwingConstants.RIGHT);
        p.add(et, BorderLayout.WEST); p.add(valor, BorderLayout.EAST);
        return p;
    }

    private JComponent rankingFila(int pos, Sesion s) {
        Color ac = pos==1 ? PRIMARY : pos==2 ? PROG_COL : new Color(26,72,120);
        JPanel p = new JPanel(new BorderLayout(9,0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(10,55,102));
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,9,9);
                g2.setColor(ac);
                g2.fillRoundRect(0,4,3,getHeight()-9,3,3);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(8,11,8,11));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));

        JLabel num = new JLabel(String.valueOf(pos), SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ac);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),7,7);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        num.setFont(new Font("Segoe UI",Font.BOLD,11));
        num.setForeground(pos==2 ? new Color(4,32,63) : Color.WHITE);
        num.setOpaque(false);
        num.setPreferredSize(new Dimension(22,22));
        p.add(num, BorderLayout.WEST);

        JPanel info = new JPanel(); info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel n = new JLabel(s.getNombreSesion());
        n.setFont(new Font("Segoe UI",Font.BOLD,11)); n.setForeground(TEXT_PRIMARY);
        n.setAlignmentX(LEFT_ALIGNMENT);
        JLabel d = new JLabel(s.getArtista().getNombreArtista()+" - "+nombreCabina(s.getIdCabina()));
        d.setFont(new Font("Segoe UI",Font.PLAIN,9)); d.setForeground(TEXT_MUTED);
        d.setAlignmentX(LEFT_ALIGNMENT);
        info.add(n); info.add(d);
        p.add(info, BorderLayout.CENTER);

        JLabel fec = new JLabel(s.getFecha().format(FMT));
        fec.setFont(new Font("Segoe UI",Font.BOLD,10)); fec.setForeground(TEXT_SECONDARY);
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
            .collect(java.util.stream.Collectors.groupingBy(c -> c, java.util.stream.Collectors.counting()))
            .entrySet().stream().max(Comparator.comparingLong(java.util.Map.Entry::getValue))
            .map(java.util.Map.Entry::getKey).orElse("-");
        resCabina.setText(cabina);

        String prod = sesiones.stream()
            .map(s -> s.getProductor().getNombre())
            .collect(java.util.stream.Collectors.groupingBy(c -> c, java.util.stream.Collectors.counting()))
            .entrySet().stream().max(Comparator.comparingLong(java.util.Map.Entry::getValue))
            .map(java.util.Map.Entry::getKey).orElse("-");
        resProductor.setText(prod);
    }

    private String nombreCabina(Integer idCabina) {
        if (idCabina != null && idCabina >= 1 && idCabina <= cabinas.size())
            return cabinas.get(idCabina - 1);
        return "-";
    }

    // --- Operaciones ---

    private List<Sesion> filtrar() {
        String q = busqueda == null ? "" : busqueda.getText().trim().toLowerCase();
        if (q.isEmpty()) return new ArrayList<>(sesiones);
        return sesiones.stream().filter(s ->
            s.getNombreSesion().toLowerCase().contains(q) ||
            s.getArtista().getNombreArtista().toLowerCase().contains(q) ||
            s.getProductor().getNombre().toLowerCase().contains(q) ||
            s.getFecha().format(FMT).contains(q)).toList();
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
        if (seleccionada == null) { toast("Selecciona una sesion primero",MainFrame.ToastType.INFO); return; }
        int id = seleccionada.getIdSesion();
        if (JOptionPane.showConfirmDialog(this,"Eliminar la sesion #"+String.format("%03d",id)+"?",
                "Z-One - Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            sesiones.removeIf(s -> s.getIdSesion() == id);
            seleccionada = null;
            aplicarFiltro();
            toast("Sesion eliminada correctamente",MainFrame.ToastType.SUCCESS);
        }
    }

    // --- Dialogo crear / editar ---

    private void openForm(Sesion se) {
        boolean isEdit = se != null;

        JDialog dlg = new JDialog((java.awt.Frame)SwingUtilities.getWindowAncestor(this), isEdit?"Editar sesion":"Nueva sesion", true);
        JPanel  pnl = darkPanel();

        JTextField fNombre = field(se!=null?se.getNombreSesion():"","Nombre de la sesion");
        JTextField fFecha  = field(se!=null?se.getFecha().format(FMT):"","dd/MM/yyyy");
        JTextField fHIni   = field(se!=null?se.getHoraInicio():"09:00","HH:mm");
        JTextField fHFin   = field(se!=null?se.getHoraFin():"12:00","HH:mm");
        JTextField fDur    = field(se!=null?String.valueOf(se.getDuracion()):"","Horas (ej: 3.5)");
        JTextField fObs    = field(se!=null&&se.getObservaciones()!=null?se.getObservaciones():"","Observaciones opcionales");
        JComboBox<String>   cbEstado  = comboStr(ESTADOS,se!=null?se.getEstadoSesion():Sesion.ESTADO_PROGRAMADA);
        JComboBox<String>   cbCabina  = comboStr(cabinas.toArray(new String[0]),cabinas.get(0));
        if(se!=null && se.getIdCabina()!=null && se.getIdCabina()>=1 && se.getIdCabina()<=cabinas.size())
            cbCabina.setSelectedIndex(se.getIdCabina()-1);
        JComboBox<Artista>  cbArt = new JComboBox<>(artistas.toArray(new Artista[0]));
        JComboBox<Productor> cbProd = new JComboBox<>(productores.toArray(new Productor[0]));
        cbArt.setRenderer(objRenderer(v->v instanceof Artista a?a.getNombreArtista():"")); styleCombo(cbArt);
        cbProd.setRenderer(objRenderer(v->v instanceof Productor p?p.getNombre():"")); styleCombo(cbProd);
        if(se!=null){ cbArt.setSelectedItem(se.getArtista()); cbProd.setSelectedItem(se.getProductor()); }

        JLabel lblCosto = new JLabel(se!=null?String.format("Costo estimado: $%.2f",se.getCostoTotal()):"Costo estimado: -");
        lblCosto.setFont(new Font("Segoe UI",Font.BOLD,13)); lblCosto.setForeground(se!=null?OK_COLOR:TEXT_MUTED); lblCosto.setAlignmentX(LEFT_ALIGNMENT);

        fDur.getDocument().addDocumentListener(docListener(()->calcularYMostrarCosto(cbProd,fDur,lblCosto)));
        cbProd.addActionListener(e->calcularYMostrarCosto(cbProd,fDur,lblCosto));

        int sp=12;
        for(Object[] r : new Object[][]{
            {"Nombre de sesion *",fNombre},{"Fecha * (dd/MM/yyyy)",fFecha},{"Hora inicio (HH:mm)",fHIni},
            {"Hora fin (HH:mm)",fHFin},{"Duracion (h) *",fDur},{"Estado sesion",cbEstado},
            {"Cabina",cbCabina},{"Artista *",cbArt},{"Productor *",cbProd},{"Observaciones",fObs}
        }){ pnl.add(formRow((String)r[0],(JComponent)r[1])); pnl.add(Box.createVerticalStrut(sp)); }
        pnl.add(lblCosto); pnl.add(Box.createVerticalStrut(24));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); btns.setOpaque(false); btns.setAlignmentX(LEFT_ALIGNMENT);
        ModernUI.RoundedButton bCancel=btn("Cancelar",false,0), bSave=btn(isEdit?"Guardar cambios":"Crear sesion",true,0);
        bCancel.addActionListener(e->dlg.dispose());
        bSave.addActionListener(e->{
            String nm=fNombre.getText().trim(), fd=fFecha.getText().trim(), dr=fDur.getText().trim();
            String hi=fHIni.getText().trim(), hf=fHFin.getText().trim(), obs=fObs.getText().trim();
            String estado=(String)cbEstado.getSelectedItem();
            Integer idCab=cbCabina.getSelectedIndex()+1;
            Artista art=(Artista)cbArt.getSelectedItem(); Productor prod=(Productor)cbProd.getSelectedItem();
            if(nm.isEmpty()){toast("El nombre de sesion es obligatorio",MainFrame.ToastType.ERROR);return;}
            if(fd.isEmpty()){toast("La fecha es obligatoria",MainFrame.ToastType.ERROR);return;}
            if(dr.isEmpty()){toast("La duracion es obligatoria",MainFrame.ToastType.ERROR);return;}
            if(art==null){toast("Selecciona un artista",MainFrame.ToastType.ERROR);return;}
            if(prod==null){toast("Selecciona un productor",MainFrame.ToastType.ERROR);return;}
            LocalDate fecha; try{ fecha=LocalDate.parse(fd,FMT); }catch(DateTimeParseException ex){toast("Formato de fecha invalido (dd/MM/yyyy)",MainFrame.ToastType.ERROR);return;}
            double dur=parseDouble(dr).orElse(-1.0);
            if(dur<=0){toast("La duracion debe ser un numero positivo",MainFrame.ToastType.ERROR);return;}
            if(isEdit){
                se.setNombreSesion(nm); se.setFecha(fecha); se.setHoraInicio(hi); se.setHoraFin(hf);
                se.setDuracion(dur); se.setArtista(art); se.setProductor(prod);
                se.setIdCabina(idCab); se.setEstadoSesion(estado); se.setObservaciones(obs);
                toast("Sesion actualizada correctamente",MainFrame.ToastType.SUCCESS);
            } else {
                Sesion nueva = new Sesion(nextId++,art,prod,idCab,nm,fecha,hi,hf,dur,estado,obs);
                sesiones.add(nueva);
                seleccionada = nueva;
                toast("Sesion creada correctamente",MainFrame.ToastType.SUCCESS);
            }
            aplicarFiltro();
            dlg.dispose();
        });
        btns.add(bCancel); btns.add(bSave); pnl.add(btns);
        dlg.setContentPane(pnl); dlg.pack(); dlg.setMinimumSize(new Dimension(480,dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this); dlg.setVisible(true);
    }

    // --- Helpers de componentes ---

    private JLabel lbl(String t,Font f,Color c){ JLabel l=new JLabel(t); l.setFont(f); l.setForeground(c); return l; }

    private JTextField field(String val,String ph){
        ModernUI.RoundedTextField f = new ModernUI.RoundedTextField(ph);
        f.setText(val);
        f.setPreferredSize(new Dimension(10,40));
        return f;
    }

    private ModernUI.RoundedButton btn(String t,boolean p,int w){
        ModernUI.RoundedButton b=new ModernUI.RoundedButton(t,p); if(w>0) b.setPreferredSize(new Dimension(w,38)); return b;
    }

    private JPanel formRow(String label,JComponent comp){
        JPanel r=new JPanel(new BorderLayout(0,4)); r.setOpaque(false); r.setAlignmentX(LEFT_ALIGNMENT); r.setMaximumSize(new Dimension(Integer.MAX_VALUE,74));
        JLabel l=new JLabel(label); l.setFont(FE); l.setForeground(TEXT_MUTED); r.add(l,BorderLayout.NORTH); r.add(comp,BorderLayout.CENTER); return r;
    }

    private JComboBox<String> comboStr(String[] opts,String sel){
        JComboBox<String> cb = ModernUI.roundedCombo(opts);
        cb.setSelectedItem(sel);
        cb.setRenderer(new DefaultListCellRenderer(){ @Override public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){
            JLabel c=new JLabel(v==null?"":v.toString()); c.setBackground(i==-1?INPUT_BG:s?PRIMARY:INPUT_BG); c.setForeground(TEXT_PRIMARY); c.setBorder(new EmptyBorder(7,12,7,12)); c.setOpaque(true); return c;}});
        return cb;
    }

    private <T> DefaultListCellRenderer objRenderer(Function<Object,String> fn){
        return new DefaultListCellRenderer(){ @Override public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){
            JLabel c=new JLabel(fn.apply(v)); c.setBackground(s&&i!=-1?PRIMARY:INPUT_BG); c.setForeground(TEXT_PRIMARY); c.setBorder(new EmptyBorder(7,12,7,12)); c.setOpaque(true); return c;}};
    }

    private <T> void styleCombo(JComboBox<T> cb){
        cb.setEditable(false); cb.setFont(FS); cb.setForeground(TEXT_PRIMARY); cb.setBackground(INPUT_BG); cb.setOpaque(false);
        cb.setMaximumRowCount(6); cb.setFocusable(false);
        cb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER,1,true),new EmptyBorder(6,8,6,8)));
    }

    private JPanel darkPanel(){
        JPanel p=new JPanel(){ @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BG_DARK); g2.fillRect(0,0,getWidth(),getHeight()); g2.dispose();}};
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); p.setBorder(new EmptyBorder(28,32,28,32)); return p;
    }

    // --- Helpers utilitarios ---

    private DocumentListener docListener(Runnable r){
        return new DocumentListener(){
            public void insertUpdate(javax.swing.event.DocumentEvent e){r.run();}
            public void removeUpdate(javax.swing.event.DocumentEvent e){r.run();}
            public void changedUpdate(javax.swing.event.DocumentEvent e){r.run();}
        };
    }

    private Optional<Double> parseDouble(String s){ try{return Optional.of(Double.parseDouble(s.trim()));}catch(NumberFormatException e){return Optional.empty();} }

    private void toast(String msg,MainFrame.ToastType t){ MainFrame.showToast(msg,t); }
}