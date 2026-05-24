package view;

import model.Artista;
import services.ArtistaService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * formArtista — Estilo Z-One rediseñado.
 *
 * Layout:
 *   IZQUIERDA  → encabezado + stat cards + tabla de artistas
 *   DERECHA    → panel estadísticas de canciones (top artistas, barras,
 *                donut de géneros) + SQL log
 */
public class formArtista extends JPanel {

    // ══════════════════════════════════════════════════════════════════
    //  PALETA
    // ══════════════════════════════════════════════════════════════════
    static final Color BG_DEEP   = new Color(5,   3,  14);
    static final Color BG_CARD   = new Color(11,  9,  27);
    static final Color BG_FIELD  = new Color(18, 14,  44);
    static final Color BG_ROW_A  = new Color(11,  9,  27);
    static final Color BG_ROW_B  = new Color(15, 12,  38);
    static final Color COL_BRD   = new Color(35, 26,  80);
    static final Color PURPLE    = new Color(124, 58, 237);
    static final Color PURPLE_LT = new Color(167,139, 250);
    static final Color CYAN      = new Color(6,  182, 212);
    static final Color GREEN     = new Color(52, 211, 153);
    static final Color AMBER     = new Color(251,191,  36);
    static final Color PINK      = new Color(244,114, 182);
    static final Color ORANGE    = new Color(251,146,  60);
    static final Color TXT_PRI   = new Color(237,233, 254);
    static final Color TXT_SEC   = new Color(120,105, 175);
    static final Color SEL_BG    = new Color(124, 58, 237, 70);
    static final Color ORO       = new Color(251,191,  36);
    static final Color PLATA     = new Color(203,213, 225);
    static final Color BRONCE    = new Color(217,119,  66);

    // Colores rotativos para barras de artistas
    static final Color[] BAR_COLORS = {
        CYAN, PURPLE_LT, GREEN, AMBER, PINK, ORANGE,
        new Color(99,179,237), new Color(129,230,217)
    };

    // ── Fuentes ───────────────────────────────────────────────────────
    static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  28);
    static final Font F_SUB    = new Font("Segoe UI", Font.BOLD,   9);
    static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_BOLD   = new Font("Segoe UI", Font.BOLD,  13);
    static final Font F_MONO   = new Font("Consolas", Font.PLAIN, 11);
    static final Font F_MONO_B = new Font("Consolas", Font.BOLD,  11);

    // ══════════════════════════════════════════════════════════════════
    //  COLUMNAS TABLA
    // ══════════════════════════════════════════════════════════════════
    private static final String[] COLS = {
        "ID","Nombre artístico","Género musical","Nacionalidad","Tipo","Estado"
    };
    private static final int COL_ID=0, COL_NOMBRE=1, COL_GENERO=2,
                              COL_PAIS=3, COL_TIPO=4, COL_ESTADO=5;

    // ══════════════════════════════════════════════════════════════════
    //  ESTADO
    // ══════════════════════════════════════════════════════════════════
    private final ArtistaService svc = new ArtistaService();
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextField campoBusqueda;

    // Stat cards izquierda
    private JLabel lblTotal, lblActivos, lblPaises, lblTipos;

    // Panel estadísticas canciones (derecha arriba)
    private JPanel  statsContainer;
    private JLabel  lblStatTotal, lblStatArtistas, lblStatProm, lblStatLider;

    // Lista actual
    private List<Artista> listaActual = new ArrayList<>();

    // SQL log
    private JPanel logContainer;
    private JLabel lblLogCount;
    private int    logCount = 0;

    // ══════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ══════════════════════════════════════════════════════════════════
    public formArtista() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 24, 24, 24));
        construirUI();
        cargarArtistas();
    }

    // ══════════════════════════════════════════════════════════════════
    //  CONSTRUCCIÓN UI
    // ══════════════════════════════════════════════════════════════════
    private void construirUI() {
        // ── Columna izquierda ──────────────────────────────────────────
        JPanel izq = new JPanel(new BorderLayout(0, 0));
        izq.setOpaque(false);
        izq.add(encabezado(), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.add(Box.createVerticalStrut(18));
        cuerpo.add(filaStats());
        cuerpo.add(Box.createVerticalStrut(18));
        cuerpo.add(panelTabla());
        izq.add(cuerpo, BorderLayout.CENTER);

        // ── Columna derecha ────────────────────────────────────────────
        JPanel der = new JPanel(new BorderLayout(0, 14));
        der.setOpaque(false);
        der.setBorder(new EmptyBorder(0, 14, 0, 0));
        der.setPreferredSize(new Dimension(295, 0));

        // Estadísticas de canciones arriba
        JPanel panStats = panelEstadisticasCanciones();
        panStats.setPreferredSize(new Dimension(295, 430));
        der.add(panStats, BorderLayout.NORTH);

        // SQL log abajo
        der.add(panelSqlLog(), BorderLayout.CENTER);

        add(izq,  BorderLayout.CENTER);
        add(der,  BorderLayout.EAST);
    }

    // ══════════════════════════════════════════════════════════════════
    //  ENCABEZADO
    // ══════════════════════════════════════════════════════════════════
    private JPanel encabezado() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);

        // Decoración lateral izquierda
        JPanel acento = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0,PURPLE,0,getHeight(),new Color(6,182,212,0)));
                g2.fillRoundRect(0,0,3,getHeight(),3,3);
                g2.dispose();
            }
        };
        acento.setOpaque(false);
        acento.setPreferredSize(new Dimension(5, 0));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel ico   = mkLabel("🎤", new Font("Segoe UI Emoji", Font.PLAIN, 22), TXT_PRI);
        JLabel title = mkLabel("Artistas", F_TITLE, TXT_PRI);
        JLabel sub   = mkLabel("GESTIÓN DE ARTISTAS  ·  BANDAS  ·  COLABORACIONES", F_SUB, TXT_SEC);

        // Badge "en vivo"
        JLabel badge = new JLabel("  ● ORACLE  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(52,211,153,30));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                g2.setColor(new Color(52,211,153,120));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(F_MONO_B.deriveFont(9f));
        badge.setForeground(GREEN);
        badge.setOpaque(false);

        for (JLabel l : new JLabel[]{ico, title, sub}) l.setAlignmentX(LEFT_ALIGNMENT);
        badge.setAlignmentX(LEFT_ALIGNMENT);

        titulos.add(ico);
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(title);
        titulos.add(Box.createVerticalStrut(4));
        titulos.add(sub);
        titulos.add(Box.createVerticalStrut(6));
        titulos.add(badge);

        JPanel acc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acc.setOpaque(false);
        campoBusqueda = mkTextField("🔍  Buscar artista...");
        campoBusqueda.setPreferredSize(new Dimension(220, 40));
        campoBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { buscar(); }
        });
        ZBtn btnNuevo = new ZBtn("＋ Nuevo artista", true);
        btnNuevo.setPreferredSize(new Dimension(165, 40));
        btnNuevo.addActionListener(e -> dialogFormulario(null));
        acc.add(campoBusqueda);
        acc.add(btnNuevo);

        JPanel left = new JPanel(new BorderLayout(8, 0));
        left.setOpaque(false);
        left.add(acento,  BorderLayout.WEST);
        left.add(titulos, BorderLayout.CENTER);

        p.add(left, BorderLayout.WEST);
        p.add(acc,  BorderLayout.EAST);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════
    //  FILA STAT CARDS (izquierda)
    // ══════════════════════════════════════════════════════════════════
    private JPanel filaStats() {
        lblTotal   = new JLabel("0");
        lblActivos = new JLabel("0");
        lblPaises  = new JLabel("0");
        lblTipos   = new JLabel("0");

        JPanel p = new JPanel(new GridLayout(1, 4, 12, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.add(statCard("TOTAL ARTISTAS", lblTotal,   PURPLE,  "🎤"));
        p.add(statCard("ACTIVOS",        lblActivos, GREEN,   "✅"));
        p.add(statCard("PAÍSES",         lblPaises,  CYAN,    "🌍"));
        p.add(statCard("SOLISTAS",       lblTipos,   AMBER,   "🎵"));
        return p;
    }

    private JPanel statCard(String titulo, JLabel valor, Color acento, String emoji) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0,
                        new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),18),
                        0,getHeight(), BG_CARD));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),100));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.setColor(acento);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(16,1,getWidth()-16,1);
                g2.setPaint(new GradientPaint(0,0,
                        new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),40),
                        0,30,new Color(0,0,0,0)));
                g2.fillRoundRect(0,0,getWidth(),30,14,14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel emo = mkLabel(emoji, new Font("Segoe UI Emoji", Font.PLAIN, 24), TXT_PRI);

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel lTit = mkLabel(titulo, F_SUB,
                new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),210));
        lTit.setAlignmentX(LEFT_ALIGNMENT);
        valor.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valor.setForeground(acento);
        valor.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(lTit);
        txt.add(Box.createVerticalStrut(2));
        txt.add(valor);

        card.add(emo, BorderLayout.WEST);
        card.add(txt, BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL TABLA ARTISTAS
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelTabla() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.setColor(PURPLE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0,20,0,getHeight()-20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setAlignmentX(LEFT_ALIGNMENT);

        modeloTabla = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        estilizarTabla();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(0,0,0,0));
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));

        JPanel head = new JPanel(new BorderLayout(0,0));
        head.setOpaque(false);
        head.setBorder(new EmptyBorder(14,18,12,18));

        JLabel lTit = mkLabel("Lista de artistas", F_BOLD, TXT_PRI);
        JLabel lSub = mkLabel("  datos en tiempo real desde Oracle", F_MONO.deriveFont(9f), TXT_SEC);

        JPanel headLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headLeft.setOpaque(false);
        headLeft.add(lTit); headLeft.add(lSub);
        head.add(headLeft, BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(10, 14, 14, 14));
        ZBtn btnEditar   = new ZBtn("✏  Editar",   false);
        ZBtn btnEliminar = new ZBtn("🗑  Eliminar", false);
        btnEliminar.setForeground(PINK);
        btnEditar.addActionListener(e -> accionEditar());
        btnEliminar.addActionListener(e -> accionEliminar());
        btnRow.add(btnEditar);
        btnRow.add(btnEliminar);

        card.add(head,   BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private void estilizarTabla() {
        tabla.setOpaque(false);
        tabla.setBackground(new Color(0,0,0,0));
        tabla.setForeground(TXT_PRI);
        tabla.setFont(F_BODY);
        tabla.setRowHeight(44);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0,0));
        tabla.setSelectionBackground(SEL_BG);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFocusable(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader th = tabla.getTableHeader();
        th.setBackground(new Color(8,6,20));
        th.setForeground(PURPLE_LT);
        th.setFont(new Font("Segoe UI", Font.BOLD, 9));
        th.setBorder(BorderFactory.createMatteBorder(0,0,1,0,COL_BRD));
        th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(0,36));

        int[] w = {50,185,130,115,100,110};
        for (int i=0; i<w.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        tabla.setDefaultRenderer(Object.class, new CeldaRenderer());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                int id = (int) modeloTabla.getValueAt(tabla.getSelectedRow(), COL_ID);
                addLog("SELECT","SELECT * FROM perfil_artista WHERE id_artista="+id,"1 fila seleccionada",CYAN);
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════
    //  ★ PANEL ESTADÍSTICAS (columna derecha, arriba) ★
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelEstadisticasCanciones() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(7,5,20));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.setColor(CYAN);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(16,0,getWidth()-16,0);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setLayout(new BorderLayout(0,0));

        // ── Cabecera ──────────────────────────────────────────────────
        JPanel cab = new JPanel(new BorderLayout(6,0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0,new Color(6,182,212,25),
                        0,getHeight(),new Color(0,0,0,0)));
                g2.fillRoundRect(0,0,getWidth(),getHeight()+14,14,14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(12,14,12,14));

        JLabel titulo = new JLabel("🎤  TOP ARTISTAS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(CYAN);

        JLabel sub = new JLabel("por género musical");
        sub.setFont(F_MONO.deriveFont(9f));
        sub.setForeground(TXT_SEC);

        cab.add(titulo, BorderLayout.WEST);
        cab.add(sub,    BorderLayout.EAST);

        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0,CYAN,getWidth()*0.7f,0,new Color(0,0,0,0)));
                g2.fillRect(0,0,getWidth(),1);
                g2.dispose();
            }
        };
        sep.setOpaque(false);
        sep.setPreferredSize(new Dimension(0,1));

        JPanel topSect = new JPanel(new BorderLayout());
        topSect.setOpaque(false);
        topSect.add(cab, BorderLayout.CENTER);
        topSect.add(sep, BorderLayout.SOUTH);

        // ── Mini cards resumen ────────────────────────────────────────
        lblStatTotal    = mkLabel("0", new Font("Segoe UI",Font.BOLD,22), CYAN);
        lblStatArtistas = mkLabel("0", new Font("Segoe UI",Font.BOLD,22), PURPLE_LT);
        lblStatProm     = mkLabel("0", new Font("Segoe UI",Font.BOLD,22), GREEN);
        lblStatLider    = mkLabel("—", new Font("Segoe UI",Font.BOLD,11), ORO);

        JPanel miniGrid = new JPanel(new GridLayout(2,2,8,8));
        miniGrid.setOpaque(false);
        miniGrid.setBorder(new EmptyBorder(10,10,10,10));
        miniGrid.add(miniCard("TOTAL",    lblStatTotal,    CYAN));
        miniGrid.add(miniCard("ARTISTAS", lblStatArtistas, PURPLE_LT));
        miniGrid.add(miniCard("GÉNEROS",  lblStatProm,     GREEN));
        miniGrid.add(miniCard("LÍDER",    lblStatLider,    ORO));

        // ── Lista scrollable de barras ────────────────────────────────
        statsContainer = new JPanel();
        statsContainer.setOpaque(false);
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setBorder(new EmptyBorder(4,10,10,10));

        JScrollPane scroll = new JScrollPane(statsContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(0,0,0,0));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4,0));
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(miniGrid, BorderLayout.NORTH);
        body.add(scroll,   BorderLayout.CENTER);

        inner.add(topSect, BorderLayout.NORTH);
        inner.add(body,    BorderLayout.CENTER);
        return inner;
    }

    private JPanel miniCard(String label, JLabel valor, Color acento) {
        JPanel c = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0,
                        new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),20),
                        0,getHeight(), BG_FIELD));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        c.setOpaque(false);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(new EmptyBorder(8,10,8,10));
        JLabel lbl = mkLabel(label, F_SUB,
                new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),200));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        valor.setAlignmentX(LEFT_ALIGNMENT);
        c.add(lbl);
        c.add(Box.createVerticalStrut(3));
        c.add(valor);
        return c;
    }

    /** Reconstruye la lista de barras de progreso. */
    private void actualizarEstadisticas(List<Artista> lista) {
        statsContainer.removeAll();

        // Mini cards — usamos datos reales disponibles en perfil_artista
        long generos = lista.stream()
                            .map(Artista::getGeneroMusical)
                            .filter(g -> g != null && !g.isEmpty())
                            .distinct().count();
        String lider = lista.isEmpty() ? "—" :
                       lista.stream()
                            .findFirst()
                            .map(a -> recortar(a.getNombreArtista(), 12))
                            .orElse("—");

        lblStatTotal   .setText(String.valueOf(lista.size()));
        lblStatArtistas.setText(String.valueOf(lista.size()));
        lblStatProm    .setText(String.valueOf(generos));
        lblStatLider   .setText(lider);

        if (lista.isEmpty()) {
            statsContainer.add(mkLabel("Sin artistas registrados",
                    F_MONO.deriveFont(10f), TXT_SEC));
            statsContainer.revalidate();
            statsContainer.repaint();
            return;
        }

        // Ordenar alfabéticamente por nombre
        List<Artista> ord = new ArrayList<>(lista);
        ord.sort(Comparator.comparing(a -> a.getNombreArtista() != null ? a.getNombreArtista() : ""));

        for (int i = 0; i < ord.size(); i++) {
            Artista a    = ord.get(i);
            Color acento = BAR_COLORS[i % BAR_COLORS.length];
            // Ratio basado en posición para las barras decorativas
            double ratio = 1.0 - (i / (double) Math.max(ord.size(), 1));
            boolean esPodio = i < 3;

            statsContainer.add(filaArtista(i+1, a, acento, ratio, esPodio));
            statsContainer.add(Box.createVerticalStrut(esPodio ? 8 : 5));
        }

        statsContainer.revalidate();
        statsContainer.repaint();
    }

    /** Una fila del ranking con podio para los 3 primeros. */
    private JPanel filaArtista(int pos, Artista a, Color acento, double ratio, boolean esPodio) {
        final String medalla = switch (pos) {
            case 1 -> "🥇"; case 2 -> "🥈"; case 3 -> "🥉";
            default -> null;
        };
        final Color colorPos = switch (pos) {
            case 1 -> ORO; case 2 -> PLATA; case 3 -> BRONCE;
            default -> acento;
        };

        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                if (esPodio) {
                    g2.setPaint(new GradientPaint(0,0,
                            new Color(colorPos.getRed(),colorPos.getGreen(),colorPos.getBlue(),18),
                            getWidth(),0, new Color(7,5,20)));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                    g2.setColor(new Color(colorPos.getRed(),colorPos.getGreen(),colorPos.getBlue(),90));
                    g2.setStroke(new BasicStroke(1.3f));
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                } else {
                    g2.setColor(BG_CARD);
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),9,9);
                    g2.setColor(new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),45));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,9,9);
                }
                int barW = (int)((getWidth()-16)*ratio);
                int barY = getHeight()-6;
                g2.setColor(new Color(35,26,80,150));
                g2.fillRoundRect(8,barY,getWidth()-16,4,4,4);
                if (barW > 0) {
                    g2.setPaint(new GradientPaint(8,0,acento,8+barW,0,
                            new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),100)));
                    g2.fillRoundRect(8,barY,barW,4,4,4);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(8,0));
        fila.setBorder(new EmptyBorder(esPodio?10:7, 10, esPodio?14:11, 10));
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, esPodio?58:44));

        JLabel lblPos;
        if (medalla != null) {
            lblPos = new JLabel(medalla, SwingConstants.CENTER);
            lblPos.setFont(new Font("Segoe UI Emoji", Font.PLAIN, esPodio?20:16));
        } else {
            lblPos = new JLabel("#"+pos, SwingConstants.CENTER);
            lblPos.setFont(F_MONO_B.deriveFont(10f));
            lblPos.setForeground(TXT_SEC);
        }
        lblPos.setPreferredSize(new Dimension(30,0));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));

        // ── CORREGIDO: usa getNombreArtista() ────────────────────────
        JLabel lblNom = mkLabel(recortar(a.getNombreArtista(), esPodio?18:20),
                new Font("Segoe UI",Font.BOLD, esPodio?12:11),
                esPodio ? TXT_PRI : new Color(210,200,240));
        lblNom.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(lblNom);

        // ── CORREGIDO: usa getGeneroMusical() ────────────────────────
        if (esPodio && a.getGeneroMusical() != null && !a.getGeneroMusical().isEmpty()) {
            JLabel lblGen = mkLabel(recortar(a.getGeneroMusical(),20), F_MONO.deriveFont(8.5f), TXT_SEC);
            lblGen.setAlignmentX(LEFT_ALIGNMENT);
            txt.add(Box.createVerticalStrut(1));
            txt.add(lblGen);
        }

        // Pastilla con el tipo de artista (Solista / Grupo)
        String tipoTxt = a.getTipoArtista() != null ? a.getTipoArtista() : "—";
        JLabel lblNum = new JLabel(tipoTxt) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),30));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblNum.setFont(new Font("Consolas",Font.BOLD, esPodio?11:10));
        lblNum.setForeground(esPodio ? colorPos : acento);
        lblNum.setOpaque(false);
        lblNum.setBorder(new EmptyBorder(2,6,2,6));
        lblNum.setHorizontalAlignment(SwingConstants.CENTER);

        fila.add(lblPos, BorderLayout.WEST);
        fila.add(txt,    BorderLayout.CENTER);
        fila.add(lblNum, BorderLayout.EAST);
        return fila;
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL SQL LOG (columna derecha, abajo)
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelSqlLog() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(7,5,18));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setLayout(new BorderLayout());

        JPanel cab = new JPanel(new BorderLayout(6,0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(10,8,24));
                g2.fillRoundRect(0,0,getWidth(),getHeight()+12,12,12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(11,14,11,14));

        JLabel titulo = new JLabel("⬡  SQL LOG");
        titulo.setFont(F_MONO_B.deriveFont(13f));
        titulo.setForeground(GREEN);

        JLabel live = new JLabel("● LIVE");
        live.setFont(F_MONO_B.deriveFont(9f));
        live.setForeground(GREEN);
        final float[] la = {1f}; final boolean[] ld = {false};
        javax.swing.Timer liveTimer = new javax.swing.Timer(700, ev -> {
            la[0] = ld[0] ? la[0]+0.08f : la[0]-0.08f;
            if (la[0]<=0.3f){la[0]=0.3f;ld[0]=true;}
            if (la[0]>=1f)  {la[0]=1f;  ld[0]=false;}
            live.setForeground(new Color(52,211,153,(int)(la[0]*255)));
        });
        liveTimer.start();

        lblLogCount = new JLabel("0 entradas");
        lblLogCount.setFont(F_MONO.deriveFont(9f));
        lblLogCount.setForeground(TXT_SEC);

        ZBtn btnLimpiar = new ZBtn("Limpiar", false);
        btnLimpiar.setFont(F_BODY.deriveFont(10f));
        btnLimpiar.setPreferredSize(new Dimension(64,24));
        btnLimpiar.addActionListener(e -> limpiarLog());

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,0));
        rightBar.setOpaque(false);
        rightBar.add(lblLogCount); rightBar.add(live); rightBar.add(btnLimpiar);
        cab.add(titulo,   BorderLayout.WEST);
        cab.add(rightBar, BorderLayout.EAST);

        JPanel sepVerde = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0,GREEN,getWidth()*0.6f,0,new Color(0,0,0,0)));
                g2.fillRect(0,0,getWidth(),1);
                g2.dispose();
            }
        };
        sepVerde.setOpaque(false);
        sepVerde.setPreferredSize(new Dimension(0,1));

        JPanel topSect = new JPanel(new BorderLayout());
        topSect.setOpaque(false);
        topSect.add(cab,      BorderLayout.CENTER);
        topSect.add(sepVerde, BorderLayout.SOUTH);

        logContainer = new JPanel();
        logContainer.setOpaque(false);
        logContainer.setLayout(new BoxLayout(logContainer, BoxLayout.Y_AXIS));
        logContainer.setBorder(new EmptyBorder(8,8,8,8));

        JScrollPane scroll = new JScrollPane(logContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(0,0,0,0));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4,0));
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        JPanel leyenda = new JPanel(new GridLayout(3,2,4,3)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(10,8,24));
                g2.fillRoundRect(0,0,getWidth(),getHeight()+12,12,12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        leyenda.setOpaque(false);
        leyenda.setBorder(new EmptyBorder(8,12,10,12));
        for (Object[] it : new Object[][]{
            {"● INSERT",GREEN},{"● SELECT",CYAN},
            {"● UPDATE",AMBER},{"● DELETE",PINK},
            {"● ERROR",new Color(248,113,113)},{"",TXT_SEC}
        }) {
            JLabel l = new JLabel((String)it[0]);
            l.setFont(F_MONO_B.deriveFont(9f));
            l.setForeground((Color)it[1]);
            leyenda.add(l);
        }

        JPanel sepGray = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(COL_BRD); g.fillRect(0,0,getWidth(),1);
            }
        };
        sepGray.setOpaque(false);
        sepGray.setPreferredSize(new Dimension(0,1));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(sepGray, BorderLayout.NORTH);
        bottom.add(leyenda, BorderLayout.CENTER);

        inner.add(topSect, BorderLayout.NORTH);
        inner.add(scroll,  BorderLayout.CENTER);
        inner.add(bottom,  BorderLayout.SOUTH);
        return inner;
    }

    private void addLog(String tipo, String sql, String resultado, Color acento) {
        SwingUtilities.invokeLater(() -> {
            logCount++;
            lblLogCount.setText(logCount+" entradas");
            JPanel entry = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = g2d(g);
                    g2.setColor(BG_CARD);
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    g2.setColor(acento);
                    g2.fillRoundRect(0,0,3,getHeight(),3,3);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            entry.setOpaque(false);
            entry.setLayout(new BoxLayout(entry, BoxLayout.Y_AXIS));
            entry.setBorder(new EmptyBorder(7,12,7,8));
            entry.setMaximumSize(new Dimension(Integer.MAX_VALUE,9999));
            entry.setAlignmentX(LEFT_ALIGNMENT);

            String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            JLabel meta = mkLabel(hora+"  ·  "+tipo, F_MONO_B.deriveFont(9f),
                    new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),220));
            meta.setAlignmentX(LEFT_ALIGNMENT);

            JTextArea sqlArea = new JTextArea(sql);
            sqlArea.setFont(F_MONO);
            sqlArea.setForeground(PURPLE_LT);
            sqlArea.setOpaque(false);
            sqlArea.setEditable(false);
            sqlArea.setLineWrap(true);
            sqlArea.setWrapStyleWord(true);
            sqlArea.setBorder(new EmptyBorder(3,0,3,0));
            sqlArea.setAlignmentX(LEFT_ALIGNMENT);
            sqlArea.setMaximumSize(new Dimension(Integer.MAX_VALUE,9999));

            JLabel res = mkLabel("✓ "+resultado, F_MONO.deriveFont(9f),
                    new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),170));
            res.setAlignmentX(LEFT_ALIGNMENT);

            entry.add(meta); entry.add(sqlArea); entry.add(res);
            JPanel gap = new JPanel();
            gap.setOpaque(false);
            gap.setMaximumSize(new Dimension(Integer.MAX_VALUE,6));
            gap.setAlignmentX(LEFT_ALIGNMENT);
            logContainer.add(entry,0);
            logContainer.add(gap,  1);
            logContainer.revalidate();
            logContainer.repaint();
        });
    }

    private void limpiarLog() {
        logContainer.removeAll();
        logCount = 0;
        lblLogCount.setText("0 entradas");
        logContainer.revalidate();
        logContainer.repaint();
    }

    // ══════════════════════════════════════════════════════════════════
    //  RENDERER CELDAS
    // ══════════════════════════════════════════════════════════════════
    private class CeldaRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(t,val,sel,foc,row,col);
            c.setBorder(new EmptyBorder(0,16,0,16));
            c.setOpaque(true); c.setIcon(null);
            c.setBackground(sel ? SEL_BG : (row%2==0 ? BG_ROW_A : BG_ROW_B));
            c.setForeground(TXT_PRI); c.setFont(F_BODY);

            if (col == COL_ID) {
                c.setForeground(PURPLE_LT);
                c.setFont(new Font("Consolas",Font.BOLD,11));
            }
            if (col == COL_GENERO && val != null) {
                c.setForeground(CYAN);
            }
            if (col == COL_PAIS && val != null) {
                c.setForeground(new Color(129,230,217));
            }
            // ── CORREGIDO: estados de perfil_artista ─────────────────
            if (col == COL_ESTADO && val != null) {
                String s = val.toString();
                Color color = switch (s) {
                    case Artista.ESTADO_ACTIVO   -> GREEN;
                    case Artista.ESTADO_EN_PAUSA -> AMBER;
                    default                      -> PINK;   // Inactivo
                };
                c.setForeground(color);
                c.setFont(F_BOLD);
                c.setText("● "+s);
            }
            if (col == COL_TIPO && val != null) {
                c.setForeground(PURPLE_LT);
                c.setFont(F_MONO_B.deriveFont(11f));
            }
            return c;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  CARGA Y ACCIONES
    // ══════════════════════════════════════════════════════════════════
    private void cargarArtistas() {
        worker(() -> svc.obtenerTodos(), lista -> {
            listaActual = lista;
            poblar(lista);
            addLog("SELECT",
                   "SELECT id_artista, nombre_artista, genero_musical, nacionalidad, " +
                   "tipo_artista, estado_artista FROM perfil_artista ORDER BY id_artista",
                   lista.size()+" fila(s) cargadas", CYAN);
        }, "Error al cargar");
    }

    private void buscar() {
        String q = campoBusqueda.getText().trim();
        worker(() -> svc.buscar(q), lista -> {
            listaActual = lista;
            poblar(lista);
            if (!q.isEmpty())
                addLog("SELECT",
                       "SELECT * FROM perfil_artista WHERE LOWER(nombre_artista) " +
                       "LIKE '%"+q+"%' OR LOWER(genero_musical) LIKE '%"+q+"%'",
                       lista.size()+" resultado(s)", CYAN);
        }, "Error al buscar");
    }

    private void poblar(List<Artista> lista) {
        modeloTabla.setRowCount(0);
        // ── CORREGIDO: usa los getters del nuevo modelo ───────────────
        for (Artista a : lista) {
            modeloTabla.addRow(new Object[]{
                a.getIdArtista(),
                a.getNombreArtista(),
                a.getGeneroMusical(),
                a.getNacionalidad(),
                a.getTipoArtista(),
                a.getEstadoArtista()
            });
        }
        // ── CORREGIDO: stat cards con datos reales ────────────────────
        long act     = lista.stream()
                            .filter(a -> Artista.ESTADO_ACTIVO.equals(a.getEstadoArtista()))
                            .count();
        long paises  = lista.stream()
                            .map(Artista::getNacionalidad)
                            .filter(Objects::nonNull)
                            .distinct().count();
        long solistas = lista.stream()
                            .filter(a -> Artista.TIPO_SOLISTA.equals(a.getTipoArtista()))
                            .count();
        lblTotal  .setText(String.valueOf(lista.size()));
        lblActivos.setText(String.valueOf(act));
        lblPaises .setText(String.valueOf(paises));
        lblTipos  .setText(String.valueOf(solistas));
        actualizarEstadisticas(lista);
    }

    private void accionEditar() {
        int row = tabla.getSelectedRow();
        if (row<0) { toast("Selecciona un artista primero", MainFrame.ToastType.INFO); return; }
        dialogFormulario(row);
    }

    private void accionEliminar() {
        int row = tabla.getSelectedRow();
        if (row<0) { toast("Selecciona un artista primero", MainFrame.ToastType.INFO); return; }
        String nombre = modeloTabla.getValueAt(row, COL_NOMBRE).toString();
        int    id     = (int) modeloTabla.getValueAt(row, COL_ID);
        if (JOptionPane.showConfirmDialog(this,"¿Eliminar a \""+nombre+"\"?",
                "Z-One — Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            worker(()->{
                svc.darDeBaja(id);
                return svc.obtenerTodos();
            }, lista->{
                poblar(lista);
                addLog("DELETE",
                       "DELETE FROM perfil_artista WHERE id_artista="+id,
                       "Commit OK · \""+nombre+"\" eliminado", PINK);
                toast("Artista eliminado", MainFrame.ToastType.SUCCESS);
            },"Error al eliminar");
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  DIÁLOGO CREAR / EDITAR
    // ══════════════════════════════════════════════════════════════════
    private void dialogFormulario(Integer filaEditar) {
        boolean esEdit = filaEditar != null;
        // ── CORREGIDO: lee los valores con los índices actualizados ───
        int    id  = esEdit ? (int)   modeloTabla.getValueAt(filaEditar, COL_ID)     : 0;
        String nom = esEdit ? (String)modeloTabla.getValueAt(filaEditar, COL_NOMBRE) : "";
        String gen = esEdit ? (String)modeloTabla.getValueAt(filaEditar, COL_GENERO) : "";
        String pai = esEdit ? (String)modeloTabla.getValueAt(filaEditar, COL_PAIS)   : "";
        String tip = esEdit ? (String)modeloTabla.getValueAt(filaEditar, COL_TIPO)   : Artista.TIPO_SOLISTA;
        String est = esEdit ? (String)modeloTabla.getValueAt(filaEditar, COL_ESTADO) : Artista.ESTADO_ACTIVO;

        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),
                esEdit?"Editar artista":"Nuevo artista",true);
        dlg.setResizable(false);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(10,8,24));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        root.add(bandaCabecera(esEdit), BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main,BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(24,30,24,30));

        JTextField      fNom  = dlgField(nom);
        JTextField      fGen  = dlgField(gen);
        JTextField      fPai  = dlgField(pai != null ? pai : "");
        JComboBox<String> cTip = dlgCombo(tip, Artista.TIPOS_VALIDOS);
        JComboBox<String> cEst = dlgCombo(est, Artista.ESTADOS_VALIDOS);

        main.add(dlgFilaDoble("NOMBRE ARTÍSTICO *", fNom, "GÉNERO MUSICAL *", fGen));
        main.add(Box.createVerticalStrut(15));
        main.add(dlgFilaDoble("NACIONALIDAD", fPai, "TIPO DE ARTISTA", cTip));
        main.add(Box.createVerticalStrut(15));
        main.add(dlgFilaCampo("ESTADO", cEst));
        main.add(Box.createVerticalStrut(26));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
        ZBtn btnCanc = new ZBtn("Cancelar", false);
        ZBtn btnSave = new ZBtn(esEdit?"💾  Guardar cambios":"✦  Crear artista", true);
        btnCanc.setPreferredSize(new Dimension(112,40));
        btnSave.setPreferredSize(new Dimension(178,40));
        btnCanc.addActionListener(e -> dlg.dispose());
        btnSave.addActionListener(e -> guardar(esEdit, id, fNom, fGen, fPai, cTip, cEst, dlg));
        btnRow.add(btnCanc); btnRow.add(btnSave);
        main.add(btnRow);

        root.add(main, BorderLayout.CENTER);
        dlg.setContentPane(root);
        dlg.getRootPane().setDefaultButton(btnSave);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(560, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private JPanel bandaCabecera(boolean esEdit) {
        JPanel band = new JPanel(new BorderLayout(14,0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0,new Color(124,58,237),
                        getWidth(),getHeight(),new Color(6,120,180)));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setPaint(new GradientPaint(0,0,new Color(255,255,255,40),
                        0,getHeight(),new Color(255,255,255,0)));
                g2.fillRect(0,0,getWidth(),getHeight()/2);
                g2.setColor(CYAN);
                g2.fillRect(0,getHeight()-2,getWidth(),2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        band.setOpaque(false);
        band.setBorder(new EmptyBorder(20,26,20,26));
        band.setPreferredSize(new Dimension(0,90));

        JLabel ico = new JLabel(esEdit?"✏":"🎤",SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(255,255,255,40));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),13,13);
                g2.setColor(new Color(255,255,255,95));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,13,13);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        ico.setFont(new Font("Segoe UI Emoji",Font.PLAIN,24));
        ico.setForeground(Color.WHITE);
        ico.setPreferredSize(new Dimension(50,50));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt,BoxLayout.Y_AXIS));
        JLabel t = mkLabel(esEdit?"Editar artista":"Nuevo artista",
                new Font("Segoe UI",Font.BOLD,21),Color.WHITE);
        JLabel s = mkLabel(esEdit?"ACTUALIZA LA INFORMACIÓN DEL ARTISTA"
                                 :"REGISTRA UN NUEVO ARTISTA EN Z-ONE",
                F_SUB,new Color(255,255,255,185));
        t.setAlignmentX(LEFT_ALIGNMENT); s.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(Box.createVerticalGlue());
        txt.add(t); txt.add(Box.createVerticalStrut(3)); txt.add(s);
        txt.add(Box.createVerticalGlue());

        band.add(ico, BorderLayout.WEST);
        band.add(txt, BorderLayout.CENTER);
        return band;
    }

    private void guardar(boolean esEdit, int id,
            JTextField fNom, JTextField fGen, JTextField fPai,
            JComboBox<String> cTip, JComboBox<String> cEst, JDialog dlg) {

        String nom  = fNom.getText().trim();
        String gen  = fGen.getText().trim();
        String pai  = fPai.getText().trim();
        String tip  = (String) cTip.getSelectedItem();
        String est  = (String) cEst.getSelectedItem();

        // ── CORREGIDO: llama al service con la firma nueva ────────────
        worker(()->{
            if (esEdit)
                svc.modificar(id, nom, null,
                              null, null,
                              pai, gen,
                              null, null,
                              est, tip);
            else
                svc.registrar(nom, null,
                              null, null,
                              pai, gen,
                              null, null,
                              est, tip);
            return svc.obtenerTodos();
        }, lista -> {
            poblar(lista);
            if (esEdit)
                addLog("UPDATE",
                       "UPDATE perfil_artista SET nombre_artista='"+nom+
                       "', genero_musical='"+gen+"', estado_artista='"+est+
                       "' WHERE id_artista="+id,
                       "Commit OK · 1 fila actualizada", AMBER);
            else
                addLog("INSERT",
                       "INSERT INTO perfil_artista (nombre_artista, genero_musical, " +
                       "nacionalidad, estado_artista, tipo_artista) VALUES ('"+
                       nom+"','"+gen+"','"+pai+"','"+est+"','"+tip+"')",
                       "Commit OK · 1 fila insertada", GREEN);
            toast(esEdit ? "Artista actualizado" : "Artista creado: "+nom,
                  MainFrame.ToastType.SUCCESS);
            dlg.dispose();
        }, "Error al guardar");
    }

    // ── Helpers dialog ────────────────────────────────────────────────
    private JTextField dlgField(String val) {
        JTextField f = new JTextField(val) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                boolean foco = isFocusOwner();
                if (foco) { g2.setColor(new Color(124,58,237,60)); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12); }
                g2.setColor(foco?new Color(30,23,64):BG_FIELD);
                g2.fillRoundRect(2,2,getWidth()-5,getHeight()-5,10,10);
                g2.setColor(foco?PURPLE:COL_BRD);
                g2.setStroke(new BasicStroke(foco?1.8f:1f));
                g2.drawRoundRect(2,2,getWidth()-6,getHeight()-6,10,10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setFont(new Font("Segoe UI",Font.PLAIN,14));
        f.setForeground(TXT_PRI); f.setOpaque(false);
        f.setCaretColor(PURPLE_LT);
        f.setBorder(new EmptyBorder(0,14,0,14));
        f.setPreferredSize(new Dimension(200,44));
        f.addFocusListener(new java.awt.event.FocusAdapter(){
            public void focusGained(java.awt.event.FocusEvent e){f.repaint();}
            public void focusLost  (java.awt.event.FocusEvent e){f.repaint();}
        });
        return f;
    }

    private JComboBox<String> dlgCombo(String sel, String[] opciones) {
        JComboBox<String> cb = new JComboBox<>(opciones) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_FIELD);
                g2.fillRoundRect(2,2,getWidth()-5,getHeight()-5,10,10);
                g2.setColor(COL_BRD);
                g2.drawRoundRect(2,2,getWidth()-6,getHeight()-6,10,10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cb.setSelectedItem(sel);
        cb.setOpaque(false);
        cb.setFont(new Font("Segoe UI",Font.PLAIN,14));
        cb.setForeground(TXT_PRI);
        cb.setBackground(BG_FIELD);
        cb.setBorder(new EmptyBorder(0,12,0,0));
        cb.setPreferredSize(new Dimension(200,44));
        cb.setRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(
                    JList<?> l,Object v,int i,boolean s,boolean f){
                JLabel lb=new JLabel(v==null?"":" "+v);
                lb.setFont(new Font("Segoe UI",Font.PLAIN,14));
                lb.setForeground(s?Color.WHITE:TXT_PRI);
                lb.setBorder(new EmptyBorder(9,12,9,12));
                lb.setOpaque(true);
                lb.setBackground(s?PURPLE:BG_FIELD);
                return lb;
            }
        });
        return cb;
    }

    private JPanel dlgFilaCampo(String label, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0,7));
        p.setOpaque(false); p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE,80));
        p.add(mkLabel(label,new Font("Segoe UI",Font.BOLD,10),PURPLE_LT),BorderLayout.NORTH);
        p.add(campo,BorderLayout.CENTER);
        return p;
    }

    private JPanel dlgFilaDoble(String l1,JComponent c1,String l2,JComponent c2){
        JPanel p=new JPanel(new GridLayout(1,2,14,0));
        p.setOpaque(false); p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE,80));
        p.add(dlgFilaCampo(l1,c1)); p.add(dlgFilaCampo(l2,c2));
        return p;
    }

    // ══════════════════════════════════════════════════════════════════
    //  UTILIDADES
    // ══════════════════════════════════════════════════════════════════
    private static JLabel mkLabel(String txt,Font f,Color c){
        JLabel l=new JLabel(txt); l.setFont(f); l.setForeground(c); return l;
    }

    private JTextField mkTextField(String placeholder){
        JTextField f=new JTextField(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g);
                g2.setColor(BG_CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(COL_BRD); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        f.putClientProperty("JTextField.placeholderText",placeholder);
        f.setFont(F_BODY); f.setForeground(TXT_PRI); f.setOpaque(false);
        f.setCaretColor(TXT_PRI); f.setBorder(new EmptyBorder(0,14,0,14));
        return f;
    }

    private static Graphics2D g2d(Graphics g){
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        return g2;
    }

    private String recortar(String s,int max){
        if(s==null)return "";
        return s.length()>max?s.substring(0,max-1)+"…":s;
    }

    private void worker(java.util.concurrent.Callable<List<Artista>> tarea,
            java.util.function.Consumer<List<Artista>> fin,String err){
        new SwingWorker<List<Artista>,Void>(){
            @Override protected List<Artista> doInBackground() throws Exception { return tarea.call(); }
            @Override protected void done(){
                try{fin.accept(get());}
                catch(Exception ex){toast(err+": "+ex.getMessage(),MainFrame.ToastType.ERROR);}
            }
        }.execute();
    }

    private void toast(String msg,MainFrame.ToastType tipo){ MainFrame.showToast(msg,tipo); }

    // ══════════════════════════════════════════════════════════════════
    //  ZBtn — botón estilo Z-One
    // ══════════════════════════════════════════════════════════════════
    static class ZBtn extends JButton {
        private final boolean primary;
        ZBtn(String text,boolean primary){
            super(text); this.primary=primary;
            setFont(new Font("Segoe UI",Font.BOLD,12));
            setForeground(primary?Color.WHITE:TXT_PRI);
            setOpaque(false); setContentAreaFilled(false);
            setBorderPainted(false); setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8,18,8,18));
        }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=g2d(g);
            if(primary){
                g2.setColor(getModel().isPressed()?new Color(109,40,217):PURPLE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                if(!getModel().isPressed()){
                    g2.setPaint(new GradientPaint(0,0,new Color(255,255,255,28),0,getHeight()/2f,new Color(0,0,0,0)));
                    g2.fillRoundRect(0,0,getWidth(),getHeight()/2,10,10);
                }
            } else {
                g2.setColor(getModel().isRollover()?new Color(28,20,66):BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
            }
            g2.dispose(); super.paintComponent(g);
        }
    }
}
