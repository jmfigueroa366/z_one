package view;

import model.Artista;
import services.ArtistaService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class formArtista extends JPanel {

    // ══════════════════════════════════════════════════════════════════
    //  PALETA
    // ══════════════════════════════════════════════════════════════════
    static final Color BG_DEEP   = new Color(235, 237, 245);
    static final Color BG_CARD   = new Color(255, 255, 255);
    static final Color BG_FIELD  = new Color(244, 245, 247);
    static final Color BG_ROW_A  = new Color(255, 255, 255);
    static final Color BG_ROW_B  = new Color(250, 251, 252);
    static final Color COL_BRD   = new Color(224, 226, 232);
    static final Color PURPLE    = new Color(108,  99, 255);
    static final Color PURPLE_LT = new Color( 91,  82, 212);
    static final Color CYAN      = new Color(  8, 178, 212);
    static final Color GREEN     = new Color(  5, 150, 105);
    static final Color AMBER     = new Color(217, 119,   6);
    static final Color PINK      = new Color(236,  72, 153);
    static final Color TXT_PRI   = new Color( 26,  29,  46);
    static final Color TXT_SEC   = new Color(107, 114, 128);
    static final Color SEL_BG    = new Color(108,  99, 255,  40);
    static final Color ORO       = new Color(161, 130,   0);
    static final Color PLATA     = new Color(100, 110, 125);
    static final Color BRONCE    = new Color(145,  90,  30);

    static final Color[] BAR_COLORS = {
        new Color(108,  99, 255),
        new Color(  6, 182, 212),
        new Color(236,  72, 153),
        new Color( 59, 130, 246),
        new Color(  5, 150, 105),
        new Color(245, 158,  11),
        new Color(239,  68,  68),
        new Color(139,  92, 246),
    };

    static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  26);
    static final Font F_SUB    = new Font("Segoe UI", Font.BOLD,   9);
    static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_BOLD   = new Font("Segoe UI", Font.BOLD,  13);
    static final Font F_MONO   = new Font("Consolas", Font.PLAIN, 11);
    static final Font F_MONO_B = new Font("Consolas", Font.BOLD,  11);

    private static final String[] COLS = {
        "ID", "Nombre artístico", "Género musical", "Nacionalidad", "Tipo", "Estado"
    };
    private static final int COL_ID=0, COL_NOMBRE=1, COL_GENERO=2,
                              COL_PAIS=3, COL_TIPO=4, COL_ESTADO=5;

    // ══════════════════════════════════════════════════════════════════
    //  ANIMACIONES — variables globales
    // ══════════════════════════════════════════════════════════════════
    private float alphaPanel   = 0f;
    private float donaProgress = 0f;
    private float barProgress  = 0f;
    private float rowFadeProgress = 0f;
    private float pulseDona = 0f;
    private boolean pulseUp = true;

    private javax.swing.Timer timerFade, timerDona, timerBar, timerRowFade, timerPulse;

    // ══════════════════════════════════════════════════════════════════
    //  ESTADO
    // ══════════════════════════════════════════════════════════════════
    private final ArtistaService svc = new ArtistaService();
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextField campoBusqueda;

    private JLabel badgeArtistas, badgeGeneros;

    private JLabel lblTotal, lblActivos, lblPaises, lblTipos;
    private JPanel statsContainer;
    private JLabel lblStatTotal, lblStatArtistas, lblStatProm, lblStatLider;

    private Map<String, Integer> datosGenero = new LinkedHashMap<>();
    private PanelDona panelDona;
    private JPanel leyendaGenero;

    private List<Artista> listaActual = new ArrayList<>();

    // ══════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ══════════════════════════════════════════════════════════════════
    public formArtista() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        construirUI();
        cargarArtistas();
        iniciarAnimacionFade();
    }

    // ── Fade-in del panel completo ────────────────────────────────────
    private void iniciarAnimacionFade() {
        alphaPanel = 0f;
        timerFade = new javax.swing.Timer(16, null);
        timerFade.addActionListener(e -> {
            alphaPanel = Math.min(1f, alphaPanel + 0.04f);
            repaint();
            if (alphaPanel >= 1f) timerFade.stop();
        });
        timerFade.start();
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaPanel));
        super.paintComponent(g2);
        g2.dispose();
    }

    // ══════════════════════════════════════════════════════════════════
    //  CONSTRUCCIÓN UI
    // ══════════════════════════════════════════════════════════════════
    private void construirUI() {
        JPanel izq = new JPanel(new BorderLayout(0, 0));
        izq.setOpaque(false);
        izq.add(encabezado(), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.add(Box.createVerticalStrut(12));
        cuerpo.add(filaStats());
        cuerpo.add(Box.createVerticalStrut(12));
        cuerpo.add(panelTabla());
        izq.add(cuerpo, BorderLayout.CENTER);

        JPanel der = new JPanel(new BorderLayout(0, 10));
        der.setOpaque(false);
        der.setBorder(new EmptyBorder(0, 10, 0, 0));
        der.setPreferredSize(new Dimension(270, 0));

        JPanel panTop = panelTopArtistas();
        panTop.setPreferredSize(new Dimension(270, 390));
        der.add(panTop, BorderLayout.NORTH);
        der.add(panelDistribucion(), BorderLayout.CENTER);

        add(izq, BorderLayout.CENTER);
        add(der, BorderLayout.EAST);
    }

    // ══════════════════════════════════════════════════════════════════
    //  ENCABEZADO MINIMALISTA — blanco limpio, acento izquierdo morado
    // ══════════════════════════════════════════════════════════════════
    private JPanel encabezado() {
        JPanel grad = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                int w = getWidth(), h = getHeight();
                // Fondo blanco puro
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w, h, 14, 14);
                // Borde gris sutil
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w-1, h-1, 14, 14);
                // Acento morado fino en borde izquierdo
                g2.setColor(PURPLE);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(0, 18, 0, h-18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        grad.setOpaque(false);
        grad.setBorder(new EmptyBorder(18, 24, 18, 24));

        // ── Izquierda ───────────────────────────────────────────────
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JPanel icoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        icoRow.setOpaque(false);

        // Caja del icono — fondo morado suave
        JPanel icoCaja = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(108, 99, 255, 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(108, 99, 255, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icoCaja.setOpaque(false);
        icoCaja.setPreferredSize(new Dimension(42, 42));
        JLabel icoEmoji = mkLabel("🎤", new Font("Segoe UI Emoji", Font.PLAIN, 18), PURPLE);
        icoEmoji.setHorizontalAlignment(SwingConstants.CENTER);
        icoCaja.add(icoEmoji, BorderLayout.CENTER);

        JLabel title = new JLabel("Artistas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TXT_PRI);

        icoRow.add(icoCaja);
        icoRow.add(title);
        icoRow.setAlignmentX(LEFT_ALIGNMENT);

        // Subtítulo
        JLabel sub = mkLabel("Gestión de artistas  ·  Bandas  ·  Colaboraciones",
                new Font("Segoe UI", Font.PLAIN, 12), TXT_SEC);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        // ── Badges pill ──────────────────────────────────────────────
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(LEFT_ALIGNMENT);

        // Badge "Conectado" — verde suave con pulso
        JLabel badgeConn = new JLabel("  ● Conectado  ") {
            private float pulso = 0f;
            private javax.swing.Timer t = null;
            {
                t = new javax.swing.Timer(60, e -> {
                    pulso = (pulso + 0.12f) % (2f * (float)Math.PI);
                    repaint();
                });
                t.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(5, 150, 105, 15));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(5, 150, 105, 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                float alpha = 0.4f + 0.3f * (float)Math.sin(pulso);
                g2.setColor(new Color(5, 150, 105, (int)(alpha * 255)));
                g2.fillOval(7, getHeight()/2-4, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badgeConn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badgeConn.setForeground(new Color(5, 150, 105));
        badgeConn.setOpaque(false);
        badgeConn.setBorder(new EmptyBorder(3, 4, 3, 4));

        badgeArtistas = mkBadgePill("♪  0 artistas",
                PURPLE,
                new Color(108, 99, 255, 12),
                new Color(108, 99, 255, 50));
        badgeGeneros = mkBadgePill("◈  0 géneros",
                new Color(8, 178, 212),
                new Color(8, 178, 212, 12),
                new Color(8, 178, 212, 50));

        badgeRow.add(badgeConn);
        badgeRow.add(badgeArtistas);
        badgeRow.add(badgeGeneros);

        left.add(icoRow);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);
        left.add(Box.createVerticalStrut(10));
        left.add(badgeRow);

        // ── Derecha: búsqueda + botón ────────────────────────────────
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JPanel rightRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightRow.setOpaque(false);

        // Campo búsqueda limpio
        campoBusqueda = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                boolean foco = isFocusOwner();
                g2.setColor(foco ? new Color(238, 236, 255) : BG_FIELD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(foco ? PURPLE : COL_BRD);
                g2.setStroke(new BasicStroke(foco ? 1.5f : 1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                // Lupa
                g2.setColor(foco ? PURPLE : TXT_SEC);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(9, 11, 12, 12);
                g2.drawLine(19, 21, 25, 27);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        campoBusqueda.putClientProperty("JTextField.placeholderText", "Buscar artista...");
        campoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoBusqueda.setForeground(TXT_PRI);
        campoBusqueda.setOpaque(false);
        campoBusqueda.setCaretColor(PURPLE);
        campoBusqueda.setBorder(new EmptyBorder(0, 36, 0, 12));
        campoBusqueda.setPreferredSize(new Dimension(215, 38));
        campoBusqueda.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { campoBusqueda.repaint(); }
            public void focusLost(FocusEvent e)   { campoBusqueda.repaint(); }
        });
        campoBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { buscar(); }
        });

        // Botón morado sólido con texto blanco
        JButton btnNuevo = new JButton("＋  Nuevo artista") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(getModel().isPressed() ? PURPLE_LT : PURPLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (!getModel().isPressed()) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(255,255,255,20),
                            0, getHeight()/2f, new Color(0,0,0,0)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight()/2, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnNuevo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setOpaque(false); btnNuevo.setContentAreaFilled(false);
        btnNuevo.setBorderPainted(false); btnNuevo.setFocusPainted(false);
        btnNuevo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNuevo.setBorder(new EmptyBorder(0, 20, 0, 20));
        btnNuevo.setPreferredSize(new Dimension(170, 38));
        btnNuevo.addActionListener(e -> dialogFormulario(null));

        rightRow.add(campoBusqueda);
        rightRow.add(btnNuevo);

        right.add(Box.createVerticalGlue());
        right.add(rightRow);
        right.add(Box.createVerticalGlue());

        grad.add(left,  BorderLayout.WEST);
        grad.add(right, BorderLayout.EAST);
        return grad;
    }

    /** Badge tipo pill con texto, fondo y borde independientes */
    private JLabel mkBadgePill(String texto, Color colorTxt, Color colorFondo, Color colorBorde) {
        JLabel b = new JLabel("  " + texto + "  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(colorFondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(colorBorde);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        b.setForeground(colorTxt);
        b.setOpaque(false);
        b.setBorder(new EmptyBorder(3, 4, 3, 4));
        return b;
    }

    // ══════════════════════════════════════════════════════════════════
    //  STAT CARDS
    // ══════════════════════════════════════════════════════════════════
    private JPanel filaStats() {
        lblTotal   = new JLabel("0");
        lblActivos = new JLabel("0");
        lblPaises  = new JLabel("0");
        lblTipos   = new JLabel("0");

        JPanel p = new JPanel(new GridLayout(1, 4, 10, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 82));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.add(statCard("TOTAL ARTISTAS", lblTotal,   PURPLE, "🎤"));
        p.add(statCard("ACTIVOS",        lblActivos, GREEN,  "✅"));
        p.add(statCard("PAÍSES",         lblPaises,  CYAN,   "🌍"));
        p.add(statCard("SOLISTAS",       lblTipos,   PINK,   "🎵"));
        return p;
    }

    private JPanel statCard(String titulo, JLabel valor, Color acento, String emoji) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(acento);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(14, 1, getWidth()-14, 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(8, 0));
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel emo = mkLabel(emoji, new Font("Segoe UI Emoji", Font.PLAIN, 18), TXT_PRI);
        JPanel iconCircle = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(34, 34));
        iconCircle.add(emo, BorderLayout.CENTER);

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel lTit = mkLabel(titulo, F_SUB, TXT_SEC);
        lTit.setAlignmentX(LEFT_ALIGNMENT);
        valor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valor.setForeground(TXT_PRI);
        valor.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(lTit);
        txt.add(Box.createVerticalStrut(1));
        txt.add(valor);

        card.add(iconCircle, BorderLayout.WEST);
        card.add(txt,        BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL TABLA
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelTabla() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(PURPLE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, 18, 0, getHeight()-18);
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
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.setBorder(new EmptyBorder(12, 16, 10, 16));
        JLabel lTit = mkLabel("Lista de artistas", F_BOLD, TXT_PRI);
        JLabel lSub = mkLabel("  datos en tiempo real desde Oracle", F_MONO.deriveFont(9f), TXT_SEC);
        JPanel hl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hl.setOpaque(false);
        hl.add(lTit); hl.add(lSub);
        head.add(hl, BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(8, 12, 12, 12));
        ZBtn btnEditar   = new ZBtn("✏  Editar",   false);
        ZBtn btnEliminar = new ZBtn("🗑  Eliminar", false);
        btnEliminar.setForeground(PINK);
        btnEditar.setPreferredSize(new Dimension(100, 34));
        btnEliminar.setPreferredSize(new Dimension(110, 34));
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
        tabla.setOpaque(true);
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(TXT_PRI);
        tabla.setFont(F_BODY);
        tabla.setRowHeight(40);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(SEL_BG);
        tabla.setSelectionForeground(TXT_PRI);
        tabla.setFocusable(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader th = tabla.getTableHeader();
        th.setBackground(new Color(250, 251, 252));
        th.setForeground(TXT_SEC);
        th.setFont(new Font("Segoe UI", Font.BOLD, 9));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COL_BRD));
        th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(0, 32));
        th.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                l.setBackground(new Color(250, 251, 252));
                l.setForeground(TXT_SEC);
                l.setFont(new Font("Segoe UI", Font.BOLD, 9));
                l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, COL_BRD),
                    new EmptyBorder(0, 14, 0, 14)));
                l.setOpaque(true);
                return l;
            }
        });

        int[] w = {44, 170, 120, 110, 90, 105};
        for (int i = 0; i < w.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        tabla.setDefaultRenderer(Object.class, new CeldaRenderer());
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL TOP ARTISTAS
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelTopArtistas() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setColor(PURPLE);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(16, 0, getWidth()-16, 0);
                g2.setPaint(new GradientPaint(
                    getWidth()-60, 0, new Color(108, 99, 255, 18),
                    getWidth(), 50,   new Color(108, 99, 255, 0)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setLayout(new BorderLayout(0, 0));

        JPanel cab = new JPanel(new BorderLayout(6, 0));
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(13, 14, 11, 14));
        JLabel titulo = mkLabel("📊  Top artistas", new Font("Segoe UI", Font.BOLD, 13), TXT_PRI);
        JLabel sub    = mkLabel("por género musical", F_MONO.deriveFont(9f), TXT_SEC);
        cab.add(titulo, BorderLayout.WEST);
        cab.add(sub,    BorderLayout.EAST);

        JPanel topSect = new JPanel(new BorderLayout());
        topSect.setOpaque(false);
        topSect.add(cab,       BorderLayout.CENTER);
        topSect.add(sepLine(), BorderLayout.SOUTH);

        lblStatTotal    = mkLabel("0", new Font("Segoe UI", Font.BOLD, 20), TXT_PRI);
        lblStatArtistas = mkLabel("0", new Font("Segoe UI", Font.BOLD, 20), TXT_PRI);
        lblStatProm     = mkLabel("0", new Font("Segoe UI", Font.BOLD, 20), TXT_PRI);
        lblStatLider    = mkLabel("—", new Font("Segoe UI", Font.BOLD, 11), PURPLE);

        JPanel miniGrid = new JPanel(new GridLayout(2, 2, 6, 6));
        miniGrid.setOpaque(false);
        miniGrid.setBorder(new EmptyBorder(8, 10, 8, 10));
        miniGrid.add(miniCard("TOTAL",    lblStatTotal,    PURPLE));
        miniGrid.add(miniCard("ARTISTAS", lblStatArtistas, CYAN));
        miniGrid.add(miniCard("GÉNEROS",  lblStatProm,     new Color(108, 99, 255)));
        miniGrid.add(miniCard("LÍDER",    lblStatLider,    AMBER));

        statsContainer = new JPanel();
        statsContainer.setOpaque(false);
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setBorder(new EmptyBorder(4, 8, 8, 8));

        JScrollPane scroll = mkScroll(statsContainer);

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
                g2.setColor(new Color(249, 250, 253));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 120));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, 6, 0, getHeight()-6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        c.setOpaque(false);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(new EmptyBorder(7, 11, 7, 9));
        JLabel lbl = mkLabel(label, F_SUB, TXT_SEC);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        valor.setAlignmentX(LEFT_ALIGNMENT);
        c.add(lbl);
        c.add(Box.createVerticalStrut(2));
        c.add(valor);
        return c;
    }

    // ── Animación fade+slide de filas ────────────────────────────────
    private void iniciarAnimacionFilas() {
        rowFadeProgress = 0f;
        if (timerRowFade != null) timerRowFade.stop();
        timerRowFade = new javax.swing.Timer(16, null);
        timerRowFade.addActionListener(e -> {
            rowFadeProgress = Math.min(1f, rowFadeProgress + 0.055f);
            statsContainer.repaint();
            if (rowFadeProgress >= 1f) timerRowFade.stop();
        });
        timerRowFade.start();
    }

    private void iniciarAnimacionBarras() {
        barProgress = 0f;
        if (timerBar != null) timerBar.stop();
        timerBar = new javax.swing.Timer(14, null);
        timerBar.addActionListener(e -> {
            barProgress = Math.min(1f, barProgress + 0.045f);
            statsContainer.repaint();
            leyendaGenero.repaint();
            if (barProgress >= 1f) timerBar.stop();
        });
        timerBar.start();
    }

    private void actualizarEstadisticas(List<Artista> lista) {
        statsContainer.removeAll();

        long generos = lista.stream()
                .map(Artista::getGeneroMusical)
                .filter(g -> g != null && !g.isEmpty())
                .distinct().count();
        String lider = lista.isEmpty() ? "—" :
                lista.stream().findFirst()
                        .map(a -> recortar(a.getNombreArtista(), 12))
                        .orElse("—");

        lblStatTotal   .setText(String.valueOf(lista.size()));
        lblStatArtistas.setText(String.valueOf(lista.size()));
        lblStatProm    .setText(String.valueOf(generos));
        lblStatLider   .setText(lider);

        if (lista.isEmpty()) {
            statsContainer.add(mkLabel("Sin artistas registrados", F_MONO.deriveFont(10f), TXT_SEC));
            statsContainer.revalidate();
            statsContainer.repaint();
            return;
        }

        List<Artista> ord = new ArrayList<>(lista);
        ord.sort(Comparator.comparing(a -> a.getNombreArtista() != null ? a.getNombreArtista() : ""));

        for (int i = 0; i < ord.size(); i++) {
            Artista a    = ord.get(i);
            Color acento = BAR_COLORS[i % BAR_COLORS.length];
            double ratio = 1.0 - (i / (double) Math.max(ord.size(), 1));
            boolean esPodio = i < 3;
            final int idx = i;
            statsContainer.add(filaArtista(i+1, a, acento, ratio, esPodio, idx));
            statsContainer.add(Box.createVerticalStrut(esPodio ? 6 : 4));
        }

        statsContainer.revalidate();
        statsContainer.repaint();
    }

    private JPanel filaArtista(int pos, Artista a, Color acento, double ratioMax,
                                boolean esPodio, int idx) {
        final Color colorPos = switch (pos) {
            case 1 -> ORO; case 2 -> PLATA; case 3 -> BRONCE; default -> acento;
        };
        final float delay = idx * 0.12f;
        final float duration = 0.35f;

        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                float localT = Math.max(0f, (rowFadeProgress - delay) / duration);
                localT = Math.min(1f, localT);
                float ease = 1f - (float)Math.pow(1f - localT, 3);

                Graphics2D g2 = g2d(g);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ease));

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                int borderAlpha = esPodio ? 65 : 40;
                Color brd = esPodio ? colorPos : acento;
                g2.setColor(new Color(brd.getRed(), brd.getGreen(), brd.getBlue(), borderAlpha));
                g2.setStroke(new BasicStroke(esPodio ? 1.3f : 1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);

                double progAnimado = ratioMax * barProgress;
                int barW = (int)((getWidth()-14) * progAnimado);
                int barY = getHeight() - 4;
                g2.setColor(new Color(228, 230, 238));
                g2.fillRoundRect(7, barY, getWidth()-14, 3, 3, 3);
                if (barW > 0) {
                    g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 200));
                    g2.fillRoundRect(7, barY, barW, 3, 3, 3);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(6, 0));
        fila.setBorder(new EmptyBorder(esPodio ? 8 : 6, 8, esPodio ? 12 : 9, 8));
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, esPodio ? 54 : 40));

        String iniciales = getIniciales(a.getNombreArtista());
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 100));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(acento);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(iniciales,
                    getWidth()/2 - fm.stringWidth(iniciales)/2,
                    getHeight()/2 + fm.getAscent()/2 - 1);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(28, 28));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel lblNom = mkLabel(recortar(a.getNombreArtista(), esPodio ? 16 : 18),
                new Font("Segoe UI", Font.BOLD, esPodio ? 11 : 10), TXT_PRI);
        lblNom.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(lblNom);
        if (esPodio && a.getGeneroMusical() != null && !a.getGeneroMusical().isEmpty()) {
            JLabel lblGen = mkLabel(recortar(a.getGeneroMusical(), 18), F_MONO.deriveFont(8f), TXT_SEC);
            lblGen.setAlignmentX(LEFT_ALIGNMENT);
            txt.add(Box.createVerticalStrut(1));
            txt.add(lblGen);
        }

        String tipoTxt = a.getTipoArtista() != null ? a.getTipoArtista() : "—";
        JLabel lblTipo = new JLabel(tipoTxt) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 15));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, esPodio ? 10 : 9));
        lblTipo.setForeground(esPodio ? colorPos : acento);
        lblTipo.setOpaque(false);
        lblTipo.setBorder(new EmptyBorder(2, 5, 2, 5));

        fila.add(avatar,  BorderLayout.WEST);
        fila.add(txt,     BorderLayout.CENTER);
        fila.add(lblTipo, BorderLayout.EAST);
        return fila;
    }

    private String getIniciales(String nombre) {
        if (nombre == null || nombre.isEmpty()) return "?";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length == 1) return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        return (partes[0].charAt(0) + "" + partes[1].charAt(0)).toUpperCase();
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL DISTRIBUCIÓN — BLANCO + dona animada + pulso número central
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelDistribucion() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setColor(CYAN);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(16, 0, getWidth()-16, 0);
                g2.setPaint(new GradientPaint(
                    getWidth()-60, 0, new Color(8, 178, 212, 16),
                    getWidth(), 50,   new Color(8, 178, 212, 0)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setLayout(new BorderLayout(0, 0));

        JPanel cab = new JPanel(new BorderLayout(6, 0));
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(11, 14, 10, 14));
        JLabel titulo = mkLabel("◎  Distribución", new Font("Segoe UI", Font.BOLD, 13), TXT_PRI);
        JLabel sub    = mkLabel("por género musical", F_MONO.deriveFont(9f), TXT_SEC);
        cab.add(titulo, BorderLayout.WEST);
        cab.add(sub,    BorderLayout.EAST);

        JPanel topSect = new JPanel(new BorderLayout());
        topSect.setOpaque(false);
        topSect.add(cab,       BorderLayout.CENTER);
        topSect.add(sepLine(), BorderLayout.SOUTH);

        panelDona = new PanelDona();
        panelDona.setOpaque(false);
        panelDona.setPreferredSize(new Dimension(0, 168));
        panelDona.setBorder(new EmptyBorder(10, 8, 6, 8));

        leyendaGenero = new JPanel();
        leyendaGenero.setOpaque(false);
        leyendaGenero.setLayout(new BoxLayout(leyendaGenero, BoxLayout.Y_AXIS));
        leyendaGenero.setBorder(new EmptyBorder(4, 8, 8, 8));

        JScrollPane scroll = mkScroll(leyendaGenero);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(panelDona, BorderLayout.NORTH);
        body.add(scroll,    BorderLayout.CENTER);

        inner.add(topSect, BorderLayout.NORTH);
        inner.add(body,    BorderLayout.CENTER);
        return inner;
    }

    // ── Dona animada ──────────────────────────────────────────────────
    private class PanelDona extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = g2d(g);

            int total = datosGenero.values().stream().mapToInt(Integer::intValue).sum();
            int w = getWidth(), h = getHeight();
            int diam   = Math.min(w, h) - 22;
            int grosor = 26;
            int x = (w - diam) / 2;
            int y = (h - diam) / 2;

            if (total == 0) {
                g2.setColor(new Color(228, 230, 238));
                g2.setStroke(new BasicStroke(grosor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                g2.drawOval(x+grosor/2, y+grosor/2, diam-grosor, diam-grosor);
                g2.setFont(F_MONO.deriveFont(10f));
                g2.setColor(TXT_SEC);
                String s = "Sin datos";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(s, w/2 - fm.stringWidth(s)/2, h/2 + 4);
                g2.dispose();
                return;
            }

            g2.setColor(new Color(228, 230, 238));
            g2.setStroke(new BasicStroke(grosor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.drawOval(x+grosor/2, y+grosor/2, diam-grosor, diam-grosor);

            double totalAngulo = 360.0 * donaProgress;
            double ang = 90;
            double angRestante = totalAngulo;
            int i = 0;
            for (Map.Entry<String, Integer> e : datosGenero.entrySet()) {
                if (angRestante <= 0) break;
                double extFull = -360.0 * e.getValue() / total;
                double ext = Math.max(extFull, -angRestante) * Math.signum(extFull);
                Color c = BAR_COLORS[i % BAR_COLORS.length];
                float grosAct = (i == 0) ? grosor + 4 : grosor;
                g2.setColor(c);
                g2.setStroke(new BasicStroke(grosAct, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                int offset = (i == 0) ? 2 : grosor/2;
                g2.drawArc(x+offset, y+offset, diam-offset*2, diam-offset*2,
                        (int)Math.round(ang), (int)Math.round(ext));
                ang += ext;
                angRestante -= Math.abs(ext);
                i++;
            }

            g2.setColor(Color.WHITE);
            int holeD = diam - grosor * 2 - 6;
            g2.fillOval(x + grosor + 3, y + grosor + 3, holeD, holeD);

            float pulsoEscala = 1f + 0.02f * pulseDona;
            int cx = w/2, cy = h/2;
            g2.translate(cx, cy);
            g2.scale(pulsoEscala, pulsoEscala);
            g2.translate(-cx, -cy);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
            g2.setColor(TXT_PRI);
            String tot = String.valueOf(total);
            FontMetrics fm1 = g2.getFontMetrics();
            g2.drawString(tot, w/2 - fm1.stringWidth(tot)/2, h/2 + 5);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
            g2.setColor(TXT_SEC);
            FontMetrics fm2 = g2.getFontMetrics();
            g2.drawString("ARTISTAS", w/2 - fm2.stringWidth("ARTISTAS")/2, h/2 + 18);

            g2.dispose();
        }
    }

    private void iniciarAnimacionDona() {
        donaProgress = 0f;
        if (timerDona != null) timerDona.stop();
        timerDona = new javax.swing.Timer(14, null);
        timerDona.addActionListener(e -> {
            donaProgress = Math.min(1f, donaProgress + 0.04f);
            panelDona.repaint();
            if (donaProgress >= 1f) {
                timerDona.stop();
                iniciarPulseDona();
            }
        });
        timerDona.start();
    }

    private void iniciarPulseDona() {
        if (timerPulse != null) timerPulse.stop();
        timerPulse = new javax.swing.Timer(30, null);
        timerPulse.addActionListener(e -> {
            if (pulseUp) {
                pulseDona += 0.05f;
                if (pulseDona >= 1f) { pulseDona = 1f; pulseUp = false; }
            } else {
                pulseDona -= 0.05f;
                if (pulseDona <= 0f) { pulseDona = 0f; pulseUp = true; }
            }
            panelDona.repaint();
        });
        timerPulse.start();
    }

    private void actualizarGrafica(List<Artista> lista) {
        Map<String, Integer> conteo = new HashMap<>();
        for (Artista a : lista) {
            String gen = a.getGeneroMusical();
            if (gen == null || gen.trim().isEmpty()) gen = "Sin género";
            conteo.merge(gen, 1, Integer::sum);
        }

        List<Map.Entry<String, Integer>> entradas = new ArrayList<>(conteo.entrySet());
        entradas.sort((e1, e2) -> e2.getValue() - e1.getValue());
        datosGenero.clear();
        for (Map.Entry<String, Integer> e : entradas) datosGenero.put(e.getKey(), e.getValue());

        leyendaGenero.removeAll();
        int total = lista.size();

        if (datosGenero.isEmpty()) {
            leyendaGenero.add(mkLabel("Sin géneros registrados", F_MONO.deriveFont(10f), TXT_SEC));
        } else {
            int idx = 0;
            for (Map.Entry<String, Integer> e : datosGenero.entrySet()) {
                Color c = BAR_COLORS[idx % BAR_COLORS.length];
                leyendaGenero.add(filaLeyenda(e.getKey(), e.getValue(), total, c));
                leyendaGenero.add(Box.createVerticalStrut(4));
                idx++;
            }
        }
        leyendaGenero.revalidate();
        leyendaGenero.repaint();

        iniciarAnimacionDona();
        iniciarAnimacionBarras();
    }

    private JPanel filaLeyenda(String nombre, int cant, int total, Color color) {
        final double ratio = total > 0 ? cant / (double) total : 0;
        final double pct   = ratio * 100;

        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 35));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 7, 7);
                double progAnimado = ratio * barProgress;
                int barW = (int)((getWidth()-12) * progAnimado);
                int barY = getHeight() - 4;
                g2.setColor(new Color(228, 230, 238));
                g2.fillRoundRect(6, barY, getWidth()-12, 3, 3, 3);
                if (barW > 0) {
                    g2.setColor(color);
                    g2.fillRoundRect(6, barY, barW, 3, 3, 3);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(6, 0));
        fila.setBorder(new EmptyBorder(5, 8, 8, 8));
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JPanel punto = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(color);
                g2.fillRoundRect(0, getHeight()/2-4, 9, 9, 3, 3);
                g2.dispose();
            }
        };
        punto.setOpaque(false);
        punto.setPreferredSize(new Dimension(12, 0));

        JLabel lblNom = mkLabel(recortar(nombre, 14), F_BODY.deriveFont(11f), TXT_PRI);
        JLabel lblVal = mkLabel(cant + "  ·  " + String.format("%.0f%%", pct),
                F_MONO_B.deriveFont(10f), color);

        fila.add(punto,  BorderLayout.WEST);
        fila.add(lblNom, BorderLayout.CENTER);
        fila.add(lblVal, BorderLayout.EAST);
        return fila;
    }

    // ══════════════════════════════════════════════════════════════════
    //  RENDERER CELDAS
    // ══════════════════════════════════════════════════════════════════
    private class CeldaRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            c.setBorder(new EmptyBorder(0, 14, 0, 14));
            c.setOpaque(true); c.setIcon(null);
            c.setBackground(sel ? SEL_BG : (row%2==0 ? BG_ROW_A : BG_ROW_B));
            c.setForeground(TXT_PRI); c.setFont(F_BODY);

            if (col == COL_ID) { c.setForeground(PURPLE); c.setFont(new Font("Consolas", Font.BOLD, 11)); }
            if (col == COL_GENERO && val != null) { c.setForeground(CYAN); }
            if (col == COL_PAIS   && val != null) { c.setForeground(new Color(59, 130, 246)); }
            if (col == COL_ESTADO && val != null) {
                String s = val.toString();
                Color color = switch (s) {
                    case Artista.ESTADO_ACTIVO   -> GREEN;
                    case Artista.ESTADO_EN_PAUSA -> AMBER;
                    default                      -> PINK;
                };
                c.setForeground(color); c.setFont(F_BOLD); c.setText("● " + s);
            }
            if (col == COL_TIPO && val != null) {
                c.setForeground(TXT_SEC); c.setFont(F_MONO_B.deriveFont(11f));
            }
            return c;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  CARGA Y ACCIONES
    // ══════════════════════════════════════════════════════════════════
    private void cargarArtistas() {
        worker(() -> svc.obtenerTodos(), lista -> {
            listaActual = lista; poblar(lista);
        }, "Error al cargar");
    }

    private void buscar() {
        String q = campoBusqueda.getText().trim();
        worker(() -> svc.buscar(q), lista -> {
            listaActual = lista; poblar(lista);
        }, "Error al buscar");
    }

    private void poblar(List<Artista> lista) {
        modeloTabla.setRowCount(0);
        for (Artista a : lista) {
            modeloTabla.addRow(new Object[]{
                a.getIdArtista(), a.getNombreArtista(), a.getGeneroMusical(),
                a.getNacionalidad(), a.getTipoArtista(), a.getEstadoArtista()
            });
        }
        long act      = lista.stream().filter(a -> Artista.ESTADO_ACTIVO.equals(a.getEstadoArtista())).count();
        long paises   = lista.stream().map(Artista::getNacionalidad).filter(Objects::nonNull).distinct().count();
        long solistas = lista.stream().filter(a -> Artista.TIPO_SOLISTA.equals(a.getTipoArtista())).count();
        long generos  = lista.stream().map(Artista::getGeneroMusical).filter(g -> g != null && !g.isEmpty()).distinct().count();

        lblTotal  .setText(String.valueOf(lista.size()));
        lblActivos.setText(String.valueOf(act));
        lblPaises .setText(String.valueOf(paises));
        lblTipos  .setText(String.valueOf(solistas));

        badgeArtistas.setText("  ♪  " + lista.size() + " artistas  ");
        badgeGeneros .setText("  ◈  " + generos + " géneros  ");

        actualizarEstadisticas(lista);
        actualizarGrafica(lista);
        iniciarAnimacionFilas();
    }

    private void accionEditar() {
        int row = tabla.getSelectedRow();
        if (row < 0) { toast("Selecciona un artista primero", MainFrame.ToastType.INFO); return; }
        dialogFormulario(row);
    }

    private void accionEliminar() {
        int row = tabla.getSelectedRow();
        if (row < 0) { toast("Selecciona un artista primero", MainFrame.ToastType.INFO); return; }
        String nombre = modeloTabla.getValueAt(row, COL_NOMBRE).toString();
        int    id     = (int) modeloTabla.getValueAt(row, COL_ID);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar a \""+nombre+"\"?",
                "Z-One — Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            worker(() -> { svc.darDeBaja(id); return svc.obtenerTodos(); },
                lista -> { poblar(lista); toast("Artista eliminado", MainFrame.ToastType.SUCCESS); },
                "Error al eliminar");
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  DIÁLOGO CREAR / EDITAR
    // ══════════════════════════════════════════════════════════════════
    private void dialogFormulario(Integer filaEditar) {
        boolean esEdit = filaEditar != null;
        int    id  = esEdit ? (int)    modeloTabla.getValueAt(filaEditar, COL_ID)     : 0;
        String nom = esEdit ? (String) modeloTabla.getValueAt(filaEditar, COL_NOMBRE) : "";
        String gen = esEdit ? (String) modeloTabla.getValueAt(filaEditar, COL_GENERO) : "";
        String pai = esEdit ? (String) modeloTabla.getValueAt(filaEditar, COL_PAIS)   : "";
        String tip = esEdit ? (String) modeloTabla.getValueAt(filaEditar, COL_TIPO)   : Artista.TIPO_SOLISTA;
        String est = esEdit ? (String) modeloTabla.getValueAt(filaEditar, COL_ESTADO) : Artista.ESTADO_ACTIVO;

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                esEdit ? "Editar artista" : "Nuevo artista", true);
        dlg.setResizable(false);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g); g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight()); g2.dispose();
                super.paintComponent(g);
            }
        };
        root.add(bandaCabecera(esEdit), BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(22, 28, 22, 28));

        JTextField        fNom = dlgField(nom);
        JTextField        fGen = dlgField(gen);
        JTextField        fPai = dlgField(pai != null ? pai : "");
        JComboBox<String> cTip = dlgCombo(tip, Artista.TIPOS_VALIDOS);
        JComboBox<String> cEst = dlgCombo(est, Artista.ESTADOS_VALIDOS);

        main.add(dlgFilaDoble("NOMBRE ARTÍSTICO *", fNom, "GÉNERO MUSICAL *", fGen));
        main.add(Box.createVerticalStrut(14));
        main.add(dlgFilaDoble("NACIONALIDAD", fPai, "TIPO DE ARTISTA", cTip));
        main.add(Box.createVerticalStrut(14));
        main.add(dlgFilaCampo("ESTADO", cEst));
        main.add(Box.createVerticalStrut(24));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        ZBtn btnCanc = new ZBtn("Cancelar", false);
        ZBtn btnSave = new ZBtn(esEdit ? "💾  Guardar cambios" : "✦  Crear artista", true);
        btnCanc.setPreferredSize(new Dimension(108, 38));
        btnSave.setPreferredSize(new Dimension(170, 38));
        btnCanc.addActionListener(e -> dlg.dispose());
        btnSave.addActionListener(e -> guardar(esEdit, id, fNom, fGen, fPai, cTip, cEst, dlg));
        btnRow.add(btnCanc); btnRow.add(btnSave);
        main.add(btnRow);

        root.add(main, BorderLayout.CENTER);
        dlg.setContentPane(root);
        dlg.getRootPane().setDefaultButton(btnSave);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(540, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private JPanel bandaCabecera(boolean esEdit) {
        JPanel band = new JPanel(new BorderLayout(14, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0, 0, new Color(88,77,238), getWidth(), 0, new Color(45,95,230)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0,0,new Color(255,255,255,22),0,getHeight(),new Color(255,255,255,0)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose(); super.paintComponent(g);
            }
        };
        band.setOpaque(false);
        band.setBorder(new EmptyBorder(18, 24, 18, 24));
        band.setPreferredSize(new Dimension(0, 82));

        JLabel ico = new JLabel(esEdit ? "✏" : "🎤", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(255,255,255,32)); g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(new Color(255,255,255,85)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.dispose(); super.paintComponent(g);
            }
        };
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        ico.setForeground(Color.WHITE);
        ico.setPreferredSize(new Dimension(46, 46));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel t = mkLabel(esEdit ? "Editar artista" : "Nuevo artista",
                new Font("Segoe UI", Font.BOLD, 19), Color.WHITE);
        JLabel s = mkLabel(esEdit ? "ACTUALIZA LA INFORMACIÓN DEL ARTISTA"
                                  : "REGISTRA UN NUEVO ARTISTA EN Z-ONE",
                F_SUB, new Color(255,255,255,180));
        t.setAlignmentX(LEFT_ALIGNMENT); s.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(Box.createVerticalGlue()); txt.add(t);
        txt.add(Box.createVerticalStrut(3)); txt.add(s);
        txt.add(Box.createVerticalGlue());

        band.add(ico, BorderLayout.WEST);
        band.add(txt, BorderLayout.CENTER);
        return band;
    }

    private void guardar(boolean esEdit, int id,
            JTextField fNom, JTextField fGen, JTextField fPai,
            JComboBox<String> cTip, JComboBox<String> cEst, JDialog dlg) {
        String nom = fNom.getText().trim(), gen = fGen.getText().trim();
        String pai = fPai.getText().trim(), tip = (String) cTip.getSelectedItem();
        String est = (String) cEst.getSelectedItem();
        worker(() -> {
            if (esEdit) svc.modificar(id, nom, null, null, null, pai, gen, null, null, est, tip);
            else        svc.registrar(nom, null, null, null, pai, gen, null, null, est, tip);
            return svc.obtenerTodos();
        }, lista -> {
            poblar(lista);
            toast(esEdit ? "Artista actualizado" : "Artista creado: "+nom, MainFrame.ToastType.SUCCESS);
            dlg.dispose();
        }, "Error al guardar");
    }

    // ── Helpers diálogo ───────────────────────────────────────────────
    private JTextField dlgField(String val) {
        JTextField f = new JTextField(val) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                boolean foco = isFocusOwner();
                g2.setColor(foco ? new Color(238,236,255) : BG_FIELD);
                g2.fillRoundRect(2,2,getWidth()-5,getHeight()-5,10,10);
                g2.setColor(foco ? PURPLE : COL_BRD);
                g2.setStroke(new BasicStroke(foco ? 1.8f : 1f));
                g2.drawRoundRect(2,2,getWidth()-6,getHeight()-6,10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(TXT_PRI); f.setOpaque(false);
        f.setCaretColor(PURPLE);
        f.setBorder(new EmptyBorder(0, 14, 0, 14));
        f.setPreferredSize(new Dimension(200, 42));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { f.repaint(); }
            public void focusLost  (FocusEvent e) { f.repaint(); }
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
                g2.dispose(); super.paintComponent(g);
            }
        };
        cb.setSelectedItem(sel); cb.setOpaque(false);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setForeground(TXT_PRI); cb.setBackground(BG_FIELD);
        cb.setBorder(new EmptyBorder(0, 12, 0, 0));
        cb.setPreferredSize(new Dimension(200, 42));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean s, boolean f) {
                JLabel lb = new JLabel(v == null ? "" : " " + v);
                lb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                lb.setForeground(s ? Color.WHITE : TXT_PRI);
                lb.setBorder(new EmptyBorder(8,12,8,12)); lb.setOpaque(true);
                lb.setBackground(s ? PURPLE : BG_FIELD);
                return lb;
            }
        });
        return cb;
    }

    private JPanel dlgFilaCampo(String label, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false); p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        p.add(mkLabel(label, new Font("Segoe UI", Font.BOLD, 10), TXT_SEC), BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private JPanel dlgFilaDoble(String l1, JComponent c1, String l2, JComponent c2) {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 0));
        p.setOpaque(false); p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        p.add(dlgFilaCampo(l1, c1)); p.add(dlgFilaCampo(l2, c2));
        return p;
    }

    // ══════════════════════════════════════════════════════════════════
    //  UTILIDADES
    // ══════════════════════════════════════════════════════════════════
    private JPanel sepLine() {
        JPanel s = new JPanel(); s.setOpaque(true);
        s.setBackground(COL_BRD); s.setPreferredSize(new Dimension(0, 1)); return s;
    }

    private JScrollPane mkScroll(JComponent comp) {
        JScrollPane s = new JScrollPane(comp);
        s.setOpaque(false); s.getViewport().setOpaque(false);
        s.getViewport().setBackground(Color.WHITE);
        s.setBorder(BorderFactory.createEmptyBorder());
        s.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        s.getVerticalScrollBar().setUnitIncrement(14);
        return s;
    }

    private static JLabel mkLabel(String txt, Font f, Color c) {
        JLabel l = new JLabel(txt); l.setFont(f); l.setForeground(c); return l;
    }

    private static Graphics2D g2d(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        return g2;
    }

    private String recortar(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max-1) + "…" : s;
    }

    private void worker(java.util.concurrent.Callable<List<Artista>> tarea,
            java.util.function.Consumer<List<Artista>> fin, String err) {
        new SwingWorker<List<Artista>, Void>() {
            @Override protected List<Artista> doInBackground() throws Exception { return tarea.call(); }
            @Override protected void done() {
                try { fin.accept(get()); }
                catch (Exception ex) { toast(err + ": " + ex.getMessage(), MainFrame.ToastType.ERROR); }
            }
        }.execute();
    }

    private void toast(String msg, MainFrame.ToastType tipo) { MainFrame.showToast(msg, tipo); }

    // ══════════════════════════════════════════════════════════════════
    //  ZBtn
    // ══════════════════════════════════════════════════════════════════
    static class ZBtn extends JButton {
        private boolean primary;
        ZBtn(String text, boolean primary) {
            super(text); this.primary = primary;
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(primary ? Color.WHITE : TXT_PRI);
            setOpaque(false); setContentAreaFilled(false);
            setBorderPainted(false); setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 16, 8, 16));
        }
        void setPrimary(boolean p) { this.primary = p; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = g2d(g);
            if (primary) {
                g2.setColor(getModel().isPressed() ? PURPLE_LT : PURPLE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                if (!getModel().isPressed()) {
                    g2.setPaint(new GradientPaint(0,0,new Color(255,255,255,25),0,getHeight()/2f,new Color(0,0,0,0)));
                    g2.fillRoundRect(0,0,getWidth(),getHeight()/2,10,10);
                }
            } else {
                g2.setColor(getModel().isRollover() ? new Color(238,236,255) : BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
            }
            g2.dispose(); super.paintComponent(g);
        }
        private static Graphics2D g2d(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            return g2;
        }
    }
}