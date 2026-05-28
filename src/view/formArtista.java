package view;

import model.Artista;
import services.ArtistaService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class formArtista extends JPanel {

    // ══════════════════════════════════════════════════════════════════
    //  PALETA — misma que formProductor (azul)
    // ══════════════════════════════════════════════════════════════════
    static final Color BG_DEEP   = new Color(3,   8,  20);
    static final Color BG_CARD   = new Color(8,  14,  32);
    static final Color BG_FIELD  = new Color(12, 22,  52);
    static final Color BG_ROW_A  = new Color(8,  14,  32);
    static final Color BG_ROW_B  = new Color(11, 18,  40);
    static final Color COL_BRD   = new Color(22, 48, 100);
    static final Color PURPLE    = new Color(37,  99, 235);
    static final Color PURPLE_LT = new Color(96, 165, 250);
    static final Color CYAN      = new Color(6,  182, 212);
    static final Color GREEN     = new Color(56, 189, 248);
    static final Color AMBER     = new Color(186, 230, 253);
    static final Color PINK      = new Color(244, 114, 182);
    static final Color ORANGE    = new Color(129, 140, 248);
    static final Color TXT_PRI   = new Color(226, 232, 255);
    static final Color TXT_SEC   = new Color(71,  100, 160);
    static final Color SEL_BG    = new Color(37,   99, 235, 60);
    static final Color ORO       = new Color(224, 242, 254);
    static final Color PLATA     = new Color(203, 213, 225);
    static final Color BRONCE    = new Color(125, 211, 252);

    static final Color[] BAR_COLORS = {
        new Color(56,  189, 248),
        new Color(6,   182, 212),
        new Color(96,  165, 250),
        new Color(129, 140, 248),
        new Color(125, 211, 252),
        new Color(186, 230, 253),
        new Color(37,   99, 235),
        new Color(224, 242, 254),
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

    private JLabel lblTotal, lblActivos, lblPaises, lblTipos;
    private JPanel  statsContainer;
    private JLabel  lblStatTotal, lblStatArtistas, lblStatProm, lblStatLider;
    private List<Artista> listaActual = new ArrayList<>();

    // ── Gráfica de género ─────────────────────────────────────────────
    private Map<String,Integer> datosGenero = new LinkedHashMap<>();
    private PanelDona panelDona;
    private JPanel    leyendaGenero;

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

        JPanel der = new JPanel(new BorderLayout(0, 14));
        der.setOpaque(false);
        der.setBorder(new EmptyBorder(0, 14, 0, 0));
        der.setPreferredSize(new Dimension(295, 0));

        JPanel panStats = panelEstadisticasCanciones();
        panStats.setPreferredSize(new Dimension(295, 430));
        der.add(panStats, BorderLayout.NORTH);
        der.add(panelGraficaGeneros(), BorderLayout.CENTER);

        add(izq, BorderLayout.CENTER);
        add(der, BorderLayout.EAST);
    }

    // ══════════════════════════════════════════════════════════════════
    //  ENCABEZADO
    // ══════════════════════════════════════════════════════════════════
    private JPanel encabezado() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);

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

        JLabel badge = new JLabel("  ● ORACLE  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(56,189,248,30));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                g2.setColor(new Color(56,189,248,120));
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
    //  FILA STAT CARDS
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
        p.add(statCard("TOTAL ARTISTAS", lblTotal,   PURPLE,   "🎤"));
        p.add(statCard("ACTIVOS",        lblActivos, GREEN,    "✅"));
        p.add(statCard("PAÍSES",         lblPaises,  CYAN,     "🌍"));
        p.add(statCard("SOLISTAS",       lblTipos,   AMBER,    "🎵"));
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
    //  PANEL TABLA
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
        th.setBackground(new Color(5, 12, 30));
        th.setForeground(PURPLE_LT);
        th.setFont(new Font("Segoe UI", Font.BOLD, 9));
        th.setBorder(BorderFactory.createMatteBorder(0,0,1,0,COL_BRD));
        th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(0,36));
        th.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                l.setBackground(new Color(5, 12, 30));
                l.setForeground(PURPLE_LT);
                l.setFont(new Font("Segoe UI", Font.BOLD, 9));
                l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0,0,1,0,COL_BRD),
                    new EmptyBorder(0,16,0,16)));
                l.setOpaque(true);
                return l;
            }
        });

        int[] w = {50,185,130,115,100,110};
        for (int i=0; i<w.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        tabla.setDefaultRenderer(Object.class, new CeldaRenderer());
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL ESTADÍSTICAS (columna derecha, arriba)
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelEstadisticasCanciones() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(5, 12, 30));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.setColor(PURPLE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(16,0,getWidth()-16,0);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setLayout(new BorderLayout(0,0));

        JPanel cab = new JPanel(new BorderLayout(6,0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0,new Color(37,99,235,25),
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
        titulo.setForeground(PURPLE_LT);

        JLabel sub = new JLabel("por género musical");
        sub.setFont(F_MONO.deriveFont(9f));
        sub.setForeground(TXT_SEC);

        cab.add(titulo, BorderLayout.WEST);
        cab.add(sub,    BorderLayout.EAST);

        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0,PURPLE_LT,getWidth()*0.7f,0,new Color(0,0,0,0)));
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

        lblStatTotal    = mkLabel("0", new Font("Segoe UI",Font.BOLD,22), PURPLE_LT);
        lblStatArtistas = mkLabel("0", new Font("Segoe UI",Font.BOLD,22), GREEN);
        lblStatProm     = mkLabel("0", new Font("Segoe UI",Font.BOLD,22), CYAN);
        lblStatLider    = mkLabel("—", new Font("Segoe UI",Font.BOLD,11), ORO);

        JPanel miniGrid = new JPanel(new GridLayout(2,2,8,8));
        miniGrid.setOpaque(false);
        miniGrid.setBorder(new EmptyBorder(10,10,10,10));
        miniGrid.add(miniCard("TOTAL",    lblStatTotal,    PURPLE_LT));
        miniGrid.add(miniCard("ARTISTAS", lblStatArtistas, GREEN));
        miniGrid.add(miniCard("GÉNEROS",  lblStatProm,     CYAN));
        miniGrid.add(miniCard("LÍDER",    lblStatLider,    AMBER));

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
            statsContainer.add(filaArtista(i+1, a, acento, ratio, esPodio));
            statsContainer.add(Box.createVerticalStrut(esPodio ? 8 : 5));
        }

        statsContainer.revalidate();
        statsContainer.repaint();
    }

    private JPanel filaArtista(int pos, Artista a, Color acento, double ratio, boolean esPodio) {
        final String medalla = switch (pos) {
            case 1 -> "🥇"; case 2 -> "🥈"; case 3 -> "🥉"; default -> null;
        };
        final Color colorPos = switch (pos) {
            case 1 -> ORO; case 2 -> PLATA; case 3 -> BRONCE; default -> acento;
        };

        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                if (esPodio) {
                    g2.setPaint(new GradientPaint(0,0,
                            new Color(colorPos.getRed(),colorPos.getGreen(),colorPos.getBlue(),18),
                            getWidth(),0, new Color(5,12,30)));
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
                g2.setColor(new Color(22,48,100,150));
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

        JLabel lblNom = mkLabel(recortar(a.getNombreArtista(), esPodio?18:20),
                new Font("Segoe UI",Font.BOLD, esPodio?12:11),
                esPodio ? TXT_PRI : PURPLE_LT);
        lblNom.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(lblNom);

        if (esPodio && a.getGeneroMusical() != null && !a.getGeneroMusical().isEmpty()) {
            JLabel lblGen = mkLabel(recortar(a.getGeneroMusical(),20), F_MONO.deriveFont(8.5f), TXT_SEC);
            lblGen.setAlignmentX(LEFT_ALIGNMENT);
            txt.add(Box.createVerticalStrut(1));
            txt.add(lblGen);
        }

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
    //  PANEL GRÁFICA DE DONA — DISTRIBUCIÓN POR GÉNERO
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelGraficaGeneros() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(5, 12, 30));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.setColor(PURPLE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(16,0,getWidth()-16,0);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setLayout(new BorderLayout());

        // ── Cabecera ──
        JPanel cab = new JPanel(new BorderLayout(6,0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0,new Color(37,99,235,25),
                        0,getHeight(),new Color(0,0,0,0)));
                g2.fillRoundRect(0,0,getWidth(),getHeight()+12,12,12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(11,14,11,14));

        JLabel titulo = new JLabel("📊  DISTRIBUCIÓN");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(PURPLE_LT);

        JLabel sub = new JLabel("por género musical");
        sub.setFont(F_MONO.deriveFont(9f));
        sub.setForeground(TXT_SEC);

        cab.add(titulo, BorderLayout.WEST);
        cab.add(sub,    BorderLayout.EAST);

        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0,PURPLE_LT,getWidth()*0.6f,0,new Color(0,0,0,0)));
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

        // ── Dona ──
        panelDona = new PanelDona();
        panelDona.setOpaque(false);
        panelDona.setPreferredSize(new Dimension(0, 175));
        panelDona.setBorder(new EmptyBorder(10,10,4,10));

        // ── Leyenda con scroll ──
        leyendaGenero = new JPanel();
        leyendaGenero.setOpaque(false);
        leyendaGenero.setLayout(new BoxLayout(leyendaGenero, BoxLayout.Y_AXIS));
        leyendaGenero.setBorder(new EmptyBorder(4,10,10,10));

        JScrollPane scroll = new JScrollPane(leyendaGenero);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(0,0,0,0));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4,0));
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(panelDona, BorderLayout.NORTH);
        body.add(scroll,    BorderLayout.CENTER);

        inner.add(topSect, BorderLayout.NORTH);
        inner.add(body,    BorderLayout.CENTER);
        return inner;
    }

    // ── Componente que pinta la dona ──────────────────────────────────
    private class PanelDona extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = g2d(g);

            int total = 0;
            for (int v : datosGenero.values()) total += v;

            int w = getWidth(), h = getHeight();
            int diam   = Math.min(w, h) - 24;
            int grosor = 24;
            int x = (w - diam) / 2;
            int y = (h - diam) / 2;

            if (total == 0) {
                g2.setColor(COL_BRD);
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

            double ang = 90;
            int i = 0;
            for (Map.Entry<String,Integer> e : datosGenero.entrySet()) {
                double ext = -360.0 * e.getValue() / total;
                Color c = BAR_COLORS[i % BAR_COLORS.length];
                g2.setColor(c);
                g2.setStroke(new BasicStroke(grosor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                g2.drawArc(x+grosor/2, y+grosor/2, diam-grosor, diam-grosor,
                           (int)Math.round(ang), (int)Math.round(ext));
                ang += ext;
                i++;
            }

            // Centro: total
            g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
            g2.setColor(TXT_PRI);
            String tot = String.valueOf(total);
            FontMetrics fm1 = g2.getFontMetrics();
            g2.drawString(tot, w/2 - fm1.stringWidth(tot)/2, h/2 + 6);

            g2.setFont(F_SUB);
            g2.setColor(TXT_SEC);
            FontMetrics fm2 = g2.getFontMetrics();
            g2.drawString("ARTISTAS", w/2 - fm2.stringWidth("ARTISTAS")/2, h/2 + 22);

            g2.dispose();
        }
    }

    private void actualizarGrafica(List<Artista> lista) {
        Map<String,Integer> conteo = new HashMap<>();
        for (Artista a : lista) {
            String gen = a.getGeneroMusical();
            if (gen == null || gen.trim().isEmpty()) gen = "Sin género";
            conteo.merge(gen, 1, Integer::sum);
        }

        List<Map.Entry<String,Integer>> entradas = new ArrayList<>(conteo.entrySet());
        entradas.sort((e1, e2) -> e2.getValue() - e1.getValue());

        datosGenero.clear();
        for (Map.Entry<String,Integer> e : entradas)
            datosGenero.put(e.getKey(), e.getValue());

        leyendaGenero.removeAll();
        int total = lista.size();
        if (datosGenero.isEmpty()) {
            leyendaGenero.add(mkLabel("Sin géneros registrados",
                    F_MONO.deriveFont(10f), TXT_SEC));
        } else {
            int i = 0;
            for (Map.Entry<String,Integer> e : datosGenero.entrySet()) {
                Color c = BAR_COLORS[i % BAR_COLORS.length];
                leyendaGenero.add(filaLeyenda(e.getKey(), e.getValue(), total, c));
                leyendaGenero.add(Box.createVerticalStrut(5));
                i++;
            }
        }

        leyendaGenero.revalidate();
        leyendaGenero.repaint();
        panelDona.repaint();
    }

    private JPanel filaLeyenda(String nombre, int cant, int total, Color color) {
        final double ratio = total > 0 ? cant / (double) total : 0;
        final double pct   = ratio * 100;

        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),55));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                int barY = getHeight()-5;
                int barW = (int)((getWidth()-16) * ratio);
                g2.setColor(new Color(22,48,100,150));
                g2.fillRoundRect(8,barY,getWidth()-16,3,3,3);
                if (barW > 0) {
                    g2.setColor(color);
                    g2.fillRoundRect(8,barY,barW,3,3,3);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(8,0));
        fila.setBorder(new EmptyBorder(6,10,9,10));
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel punto = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(color);
                g2.fillRoundRect(0, getHeight()/2-5, 11, 11, 4, 4);
                g2.dispose();
            }
        };
        punto.setOpaque(false);
        punto.setPreferredSize(new Dimension(14,0));

        JLabel lblNom = mkLabel(recortar(nombre, 15), F_BODY.deriveFont(11f), TXT_PRI);

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
                c.setForeground(GREEN);
            }
            if (col == COL_ESTADO && val != null) {
                String s = val.toString();
                Color color = switch (s) {
                    case Artista.ESTADO_ACTIVO   -> GREEN;
                    case Artista.ESTADO_EN_PAUSA -> AMBER;
                    default                      -> PINK;
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
        }, "Error al cargar");
    }

    private void buscar() {
        String q = campoBusqueda.getText().trim();
        worker(() -> svc.buscar(q), lista -> {
            listaActual = lista;
            poblar(lista);
        }, "Error al buscar");
    }

    private void poblar(List<Artista> lista) {
        modeloTabla.setRowCount(0);
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
        actualizarGrafica(lista);
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
                toast("Artista eliminado", MainFrame.ToastType.SUCCESS);
            },"Error al eliminar");
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  DIÁLOGO CREAR / EDITAR
    // ══════════════════════════════════════════════════════════════════
    private void dialogFormulario(Integer filaEditar) {
        boolean esEdit = filaEditar != null;
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
                g2.setColor(new Color(5, 12, 30));
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
                g2.setPaint(new GradientPaint(0,0,new Color(37,99,235),
                        getWidth(),getHeight(),new Color(14,50,140)));
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

        worker(()->{
            if (esEdit)
                svc.modificar(id, nom, null, null, null, pai, gen, null, null, est, tip);
            else
                svc.registrar(nom, null, null, null, pai, gen, null, null, est, tip);
            return svc.obtenerTodos();
        }, lista -> {
            poblar(lista);
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
                if (foco) {
                    g2.setColor(new Color(37,99,235,60));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                }
                g2.setColor(foco ? new Color(10,22,60) : BG_FIELD);
                g2.fillRoundRect(2,2,getWidth()-5,getHeight()-5,10,10);
                g2.setColor(foco ? PURPLE : COL_BRD);
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
                lb.setBackground(s ? PURPLE : BG_FIELD);
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
    //  ZBtn
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
                g2.setColor(getModel().isPressed() ? new Color(29,78,216) : PURPLE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                if(!getModel().isPressed()){
                    g2.setPaint(new GradientPaint(0,0,new Color(255,255,255,28),0,getHeight()/2f,new Color(0,0,0,0)));
                    g2.fillRoundRect(0,0,getWidth(),getHeight()/2,10,10);
                }
            } else {
                g2.setColor(getModel().isRollover() ? new Color(14,34,80) : BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
            }
            g2.dispose(); super.paintComponent(g);
        }
        private static Graphics2D g2d(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            return g2;
        }
    }
}