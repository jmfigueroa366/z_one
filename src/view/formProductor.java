package view;

import model.Productor;
import services.ProductorService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class formProductor extends JPanel {

    // ══════════════════════════════════════════════════════════════════
    //  PALETA
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
    static final Color AMBER     = new Color(186,230, 253);
    static final Color PINK      = new Color(244,114, 182);
    static final Color TXT_PRI   = new Color(226,232, 255);
    static final Color TXT_SEC   = new Color(71, 100, 160);
    static final Color SEL_BG    = new Color(37,  99, 235, 60);
    static final Color ORO       = new Color(224,242, 254);
    static final Color PLATA     = new Color(203,213, 225);
    static final Color BRONCE    = new Color(125,211, 252);

    // ── Fuentes ───────────────────────────────────────────────────────
    static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  26);
    static final Font F_SUB    = new Font("Segoe UI", Font.BOLD,   9);
    static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_BOLD   = new Font("Segoe UI", Font.BOLD,  13);
    static final Font F_MONO   = new Font("Consolas", Font.PLAIN, 11);
    static final Font F_MONO_B = new Font("Consolas", Font.BOLD,  11);

    // ══════════════════════════════════════════════════════════════════
    //  COLUMNAS
    // ══════════════════════════════════════════════════════════════════
    static final String[] COLS = {
        "ID", "Nombre", "Especialidad", "Nacionalidad", "Tarifa/h", "Estado"
    };
    static final int
        COL_ID           = 0,
        COL_NOMBRE       = 1,
        COL_ESPECIALIDAD = 2,
        COL_NACIONALIDAD = 3,
        COL_TARIFA       = 4,
        COL_ESTADO       = 5;

    // ══════════════════════════════════════════════════════════════════
    //  ESTADO
    // ══════════════════════════════════════════════════════════════════
    final ProductorService svc = new ProductorService();
    DefaultTableModel modeloTabla;
    JTable            tabla;
    private JTextField campoBusqueda;
    private JLabel     lblTotal, lblEspecialidades, lblTarifaProm, lblTarifaMax;
    private JPanel     rankingContainer;
    GraficoBarras      graficoBarras;
    private JLabel     lblResTotal, lblResEsp, lblResTop;

    // ══════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ══════════════════════════════════════════════════════════════════
    public formProductor() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 24, 24, 24));
        construirUI();
        cargarProductores();
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

        JPanel der = new JPanel(new BorderLayout(0, 10));
        der.setOpaque(false);
        der.setBorder(new EmptyBorder(0, 14, 0, 0));
        der.setPreferredSize(new Dimension(275, 0));

        JPanel rank = panelRanking();
        rank.setPreferredSize(new Dimension(275, 280));
        JPanel graf = panelGrafico();
        JPanel res  = panelResumen();
        res.setPreferredSize(new Dimension(275, 138));

        der.add(rank, BorderLayout.NORTH);
        der.add(graf, BorderLayout.CENTER);
        der.add(res,  BorderLayout.SOUTH);

        add(izq, BorderLayout.CENTER);
        add(der, BorderLayout.EAST);
    }

    // ─── Encabezado ───────────────────────────────────────────────────
    private JPanel encabezado() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        JLabel ico   = mkLabel("🎚", new Font("Segoe UI Emoji", Font.PLAIN, 20), TXT_PRI);
        JLabel title = mkLabel("Productores", F_TITLE, TXT_PRI);
        JLabel sub   = mkLabel("GESTIÓN DE PRODUCTORES · EQUIPO TÉCNICO · ESPECIALIDADES",
                                F_SUB, TXT_SEC);
        for (JLabel l : new JLabel[]{ico, title, sub}) l.setAlignmentX(LEFT_ALIGNMENT);
        titulos.add(ico);
        titulos.add(Box.createVerticalStrut(3));
        titulos.add(title);
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(sub);

        JPanel acc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acc.setOpaque(false);
        campoBusqueda = mkTextField("🔍  Buscar productor...");
        campoBusqueda.setPreferredSize(new Dimension(210, 38));
        campoBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { buscar(); }
        });
        ZBtn btnNuevo = new ZBtn("＋ Nuevo productor", true);
        btnNuevo.setPreferredSize(new Dimension(178, 38));
        btnNuevo.addActionListener(e -> new Formproductordialog(this, null).setVisible(true));
        acc.add(campoBusqueda);
        acc.add(btnNuevo);

        p.add(titulos, BorderLayout.WEST);
        p.add(acc,     BorderLayout.EAST);
        return p;
    }

    // ─── Fila estadísticas ────────────────────────────────────────────
    private JPanel filaStats() {
        lblTotal          = new JLabel("0");
        lblEspecialidades = new JLabel("0");
        lblTarifaProm     = new JLabel("$0");
        lblTarifaMax      = new JLabel("$0");

        JPanel p = new JPanel(new GridLayout(1, 4, 12, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.add(statCard("TOTAL PRODUCTORES", lblTotal,          PURPLE, "🎚"));
        p.add(statCard("ESPECIALIDADES",    lblEspecialidades, CYAN,   "🎛"));
        p.add(statCard("TARIFA PROMEDIO",   lblTarifaProm,     GREEN,  "💵"));
        p.add(statCard("TARIFA MÁXIMA",     lblTarifaMax,      AMBER,  "⭐"));
        return p;
    }

    private JPanel statCard(String titulo, JLabel valor, Color acento, String emoji) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(acento);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(14, 1, getWidth()-14, 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(8, 0));
        card.setBorder(new EmptyBorder(12, 14, 12, 14));
        JLabel emo = mkLabel(emoji, new Font("Segoe UI Emoji", Font.PLAIN, 20), TXT_PRI);
        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel lTit = mkLabel(titulo, F_SUB,
            new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 200));
        lTit.setAlignmentX(LEFT_ALIGNMENT);
        valor.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valor.setForeground(acento);
        valor.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(lTit);
        txt.add(valor);
        card.add(emo, BorderLayout.WEST);
        card.add(txt, BorderLayout.CENTER);
        return card;
    }

    // ─── Panel tabla ──────────────────────────────────────────────────
    private JPanel panelTabla() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
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
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.setBorder(new EmptyBorder(14, 18, 10, 18));
        head.add(mkLabel("Lista de productores", F_BOLD, TXT_PRI), BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(10, 14, 14, 14));
        ZBtn btnEditar   = new ZBtn("✏  Editar",   false);
        ZBtn btnEliminar = new ZBtn("🗑  Eliminar", false);
        btnEliminar.setForeground(PINK);
        btnEditar.addActionListener(  e -> accionEditar());
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
        tabla.setRowHeight(42);
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
        th.setBorder(BorderFactory.createMatteBorder(0,0,1,0, COL_BRD));
        th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(0, 34));
        th.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                l.setBackground(new Color(5, 12, 30));
                l.setForeground(PURPLE_LT);
                l.setFont(new Font("Segoe UI", Font.BOLD, 9));
                l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0,0,1,0, COL_BRD),
                    new EmptyBorder(0,16,0,16)));
                l.setOpaque(true);
                return l;
            }
        });

        int[] w = {52, 155, 130, 110, 90, 90};
        for (int i = 0; i < w.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        tabla.setDefaultRenderer(Object.class, new CeldaRenderer());
    }

    // ══════════════════════════════════════════════════════════════════
    //  RANKING
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelRanking() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(7, 5, 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setLayout(new BorderLayout());

        JPanel cab = new JPanel(new BorderLayout(6, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(10, 8, 24));
                g2.fillRoundRect(0, 0, getWidth(), getHeight()+12, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(11, 14, 11, 14));
        JLabel titulo = new JLabel("🏆  TOP TARIFAS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(ORO);

        JPanel cabDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        cabDer.setOpaque(false);
        JLabel lblMas = mkLabel("más", F_MONO.deriveFont(9f), TXT_SEC);
        JLabel lblOpc = mkLabel("···", new Font("Segoe UI", Font.BOLD, 13), TXT_SEC);
        lblOpc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cabDer.add(lblMas);
        cabDer.add(lblOpc);
        cab.add(titulo, BorderLayout.WEST);
        cab.add(cabDer, BorderLayout.EAST);

        JPanel sepOro = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0, ORO, getWidth()*0.6f, 0, new Color(0,0,0,0)));
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        sepOro.setOpaque(false);
        sepOro.setPreferredSize(new Dimension(0, 1));

        JPanel topSect = new JPanel(new BorderLayout());
        topSect.setOpaque(false);
        topSect.add(cab,    BorderLayout.CENTER);
        topSect.add(sepOro, BorderLayout.SOUTH);

        rankingContainer = new JPanel();
        rankingContainer.setOpaque(false);
        rankingContainer.setLayout(new BoxLayout(rankingContainer, BoxLayout.Y_AXIS));
        rankingContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(rankingContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(0,0,0,0));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        inner.add(topSect, BorderLayout.NORTH);
        inner.add(scroll,  BorderLayout.CENTER);
        return inner;
    }

    void actualizarRanking(List<Productor> lista) {
        rankingContainer.removeAll();
        List<Productor> orden = new ArrayList<>(lista);
        orden.sort(Comparator.comparingDouble(Productor::getTarifaHora).reversed());

        if (orden.isEmpty()) {
            JLabel vacio = mkLabel("Sin productores", F_MONO.deriveFont(10f), TXT_SEC);
            vacio.setAlignmentX(LEFT_ALIGNMENT);
            rankingContainer.add(vacio);
        } else {
            double maxTarifa = orden.get(0).getTarifaHora();
            if (maxTarifa <= 0) maxTarifa = 1;
            for (int i = 0; i < orden.size(); i++) {
                boolean esPodio = i < 3;
                rankingContainer.add(filaRanking(i+1, orden.get(i), maxTarifa, esPodio));
                rankingContainer.add(Box.createVerticalStrut(esPodio ? 6 : 4));
            }
        }
        rankingContainer.revalidate();
        rankingContainer.repaint();
    }

    private JPanel filaRanking(int puesto, Productor p, double maxTarifa, boolean esPodio) {
        Color acento  = puesto == 1 ? ORO : puesto == 2 ? PLATA : puesto == 3 ? BRONCE : PURPLE_LT;
        String medalla = puesto == 1 ? "🥇" : puesto == 2 ? "🥈" : puesto == 3 ? "🥉" : "#"+puesto;
        final Color ac = acento;

        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(esPodio ? new Color(20, 16, 44) : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                if (esPodio) {
                    g2.setColor(new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 110));
                    g2.setStroke(new BasicStroke(1.4f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 9, 9);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(8, 0));
        fila.setBorder(new EmptyBorder(esPodio ? 8 : 5, 10, esPodio ? 8 : 5, 10));
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, esPodio ? 50 : 38));

        JLabel lblMed = new JLabel(medalla, SwingConstants.CENTER);
        lblMed.setFont(esPodio
            ? new Font("Segoe UI Emoji", Font.PLAIN, 19)
            : new Font("Consolas", Font.BOLD, 12));
        lblMed.setForeground(acento);
        lblMed.setPreferredSize(new Dimension(28, 0));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel lblNom = mkLabel(recortar(p.getNombre(), esPodio ? 16 : 18),
            new Font("Segoe UI", Font.BOLD, esPodio ? 12 : 11), TXT_PRI);
        lblNom.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(lblNom);
        if (esPodio && p.getEspecialidad() != null) {
            JLabel lblEsp = mkLabel(recortar(p.getEspecialidad(), 18),
                F_MONO.deriveFont(8.5f), TXT_SEC);
            lblEsp.setAlignmentX(LEFT_ALIGNMENT);
            txt.add(Box.createVerticalStrut(1));
            txt.add(lblEsp);
        }

        JLabel lblMonto = mkLabel(String.format("$%.0f", p.getTarifaHora()),
            new Font("Consolas", Font.BOLD, esPodio ? 14 : 11), acento);

        fila.add(lblMed,   BorderLayout.WEST);
        fila.add(txt,      BorderLayout.CENTER);
        fila.add(lblMonto, BorderLayout.EAST);
        return fila;
    }

    // ══════════════════════════════════════════════════════════════════
    //  GRÁFICO DE BARRAS
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelGrafico() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(7, 5, 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setLayout(new BorderLayout());

        JPanel cab = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(10, 8, 24));
                g2.fillRoundRect(0, 0, getWidth(), getHeight()+12, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(11, 14, 11, 14));
        JLabel tit = new JLabel("⊙  TARIFA POR PRODUCTOR");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tit.setForeground(CYAN);
        JLabel sub = mkLabel("TARIFA/HORA (USD)", F_SUB, TXT_SEC);
        JPanel titPanel = new JPanel();
        titPanel.setOpaque(false);
        titPanel.setLayout(new BoxLayout(titPanel, BoxLayout.Y_AXIS));
        titPanel.add(tit);
        titPanel.add(Box.createVerticalStrut(2));
        titPanel.add(sub);
        cab.add(titPanel, BorderLayout.WEST);

        JPanel sepCyan = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0, CYAN, getWidth()*0.6f, 0, new Color(0,0,0,0)));
                g2.fillRect(0,0,getWidth(),1);
                g2.dispose();
            }
        };
        sepCyan.setOpaque(false);
        sepCyan.setPreferredSize(new Dimension(0, 1));

        JPanel topSect = new JPanel(new BorderLayout());
        topSect.setOpaque(false);
        topSect.add(cab,     BorderLayout.CENTER);
        topSect.add(sepCyan, BorderLayout.SOUTH);

        graficoBarras = new GraficoBarras();
        graficoBarras.setOpaque(false);
        graficoBarras.setBorder(new EmptyBorder(10, 14, 14, 14));

        inner.add(topSect,       BorderLayout.NORTH);
        inner.add(graficoBarras, BorderLayout.CENTER);
        return inner;
    }

    class GraficoBarras extends JPanel {
        private List<Productor> datos = new ArrayList<>();
        private final Color[] BARES = {
            new Color(56,189,248), new Color(6,182,212), new Color(129,140,248),
            new Color(186,230,253), new Color(244,114,182),
            PURPLE, PURPLE_LT, GREEN, AMBER, PINK
        };

        void setDatos(List<Productor> lista) {
            List<Productor> orden = new ArrayList<>(lista);
            orden.sort(Comparator.comparingDouble(Productor::getTarifaHora).reversed());
            this.datos = orden;
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (datos.isEmpty()) return;
            Graphics2D g2 = g2d(g);

            int W = getWidth(), H = getHeight();
            int n = Math.min(datos.size(), 8);
            double maxTar = datos.stream().mapToDouble(Productor::getTarifaHora).max().orElse(1);
            if (maxTar <= 0) maxTar = 1;
            int marginTop = 28, marginBot = 42;
            int areaH = H - marginTop - marginBot;
            if (areaH < 10) { g2.dispose(); return; }
            int barW   = Math.min(32, (W - 20) / n - 8);
            int totalW = n * (barW + 8) - 8;
            int startX = (W - totalW) / 2;

            g2.setColor(new Color(35, 26, 80, 120));
            g2.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1, new float[]{4,4}, 0));
            for (int row = 1; row <= 3; row++) {
                int y = marginTop + (areaH * row / 4);
                g2.drawLine(10, y, W-10, y);
            }

            for (int i = 0; i < n; i++) {
                Productor p = datos.get(i);
                double ratio = p.getTarifaHora() / maxTar;
                int bH = (int)(areaH * ratio);
                int bX = startX + i * (barW + 8);
                int bY = marginTop + areaH - bH;
                Color c = BARES[i % BARES.length];

                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 35));
                g2.fillRoundRect(bX-2, bY-2, barW+4, bH+4, 8, 8);
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(c);
                g2.fillRoundRect(bX, bY, barW, bH, 6, 6);

                String val = "$" + (int)p.getTarifaHora();
                g2.setFont(new Font("Consolas", Font.BOLD, 9));
                g2.setColor(c);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(val, bX + (barW - fm.stringWidth(val)) / 2, bY - 5);

                String nombre = abreviar(p.getNombre(), 7);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2.setColor(TXT_SEC);
                FontMetrics fm2 = g2.getFontMetrics();
                g2.drawString(nombre, bX + (barW - fm2.stringWidth(nombre)) / 2,
                    marginTop + areaH + 14);
                g2.setColor(c);
                g2.fillOval(bX + barW/2 - 3, marginTop + areaH + 22, 6, 6);
            }
            g2.dispose();
        }

        private String abreviar(String s, int max) {
            if (s == null || s.isEmpty()) return "";
            String first = s.trim().split("\\s+")[0];
            return first.length() > max ? first.substring(0, max) : first;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL RESUMEN
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelResumen() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(7, 5, 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setLayout(new BorderLayout());

        JPanel cab = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(10, 8, 24));
                g2.fillRoundRect(0, 0, getWidth(), getHeight()+12, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(11, 14, 11, 14));
        JLabel titLbl = new JLabel("⊙  RESUMEN");
        titLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titLbl.setForeground(PURPLE_LT);
        cab.add(titLbl, BorderLayout.WEST);

        JPanel sepPurple = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0, PURPLE_LT, getWidth()*0.6f, 0, new Color(0,0,0,0)));
                g2.fillRect(0,0,getWidth(),1);
                g2.dispose();
            }
        };
        sepPurple.setOpaque(false);
        sepPurple.setPreferredSize(new Dimension(0, 1));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(cab,       BorderLayout.CENTER);
        top.add(sepPurple, BorderLayout.SOUTH);

        lblResTotal = new JLabel("0");
        lblResEsp   = new JLabel("0");
        lblResTop   = new JLabel("—");

        JPanel filas = new JPanel();
        filas.setOpaque(false);
        filas.setLayout(new BoxLayout(filas, BoxLayout.Y_AXIS));
        filas.setBorder(new EmptyBorder(8, 14, 8, 14));
        filas.add(filaResumen("Total productores",    lblResTotal, PURPLE_LT));
        filas.add(Box.createVerticalStrut(2));
        sepH(filas);
        filas.add(Box.createVerticalStrut(2));
        filas.add(filaResumen("Especialidades únicas", lblResEsp, CYAN));
        filas.add(Box.createVerticalStrut(2));
        sepH(filas);
        filas.add(Box.createVerticalStrut(2));
        filas.add(filaResumen("Productor top",         lblResTop, ORO));

        inner.add(top,   BorderLayout.NORTH);
        inner.add(filas, BorderLayout.CENTER);
        return inner;
    }

    private JPanel filaResumen(String label, JLabel valor, Color acento) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        JLabel lbl = mkLabel(label, new Font("Segoe UI", Font.PLAIN, 11), TXT_SEC);
        valor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        valor.setForeground(acento);
        row.add(lbl,   BorderLayout.WEST);
        row.add(valor, BorderLayout.EAST);
        return row;
    }

    private void sepH(JPanel parent) {
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(COL_BRD);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        sep.setOpaque(false);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        parent.add(sep);
    }

    // ══════════════════════════════════════════════════════════════════
    //  RENDERER DE CELDAS
    // ══════════════════════════════════════════════════════════════════
    private class CeldaRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(t,val,sel,foc,row,col);
            c.setBorder(new EmptyBorder(0, 16, 0, 16));
            c.setOpaque(true);
            c.setIcon(null);
            c.setBackground(sel ? SEL_BG : (row % 2 == 0 ? BG_ROW_A : BG_ROW_B));
            c.setForeground(TXT_PRI);
            c.setFont(F_BODY);
            if (col == COL_ID)           { c.setForeground(PURPLE_LT); c.setFont(F_MONO_B); }
            if (col == COL_ESPECIALIDAD && val != null) {
                c.setForeground(CYAN); c.setFont(F_BOLD); c.setText("● " + val);
            }
            if (col == COL_TARIFA && val != null) { c.setForeground(GREEN); c.setFont(F_BOLD); }
            if (col == COL_ESTADO && val != null) {
                String estado = val.toString();
                Color colorEstado = "Disponible".equals(estado)  ? GREEN
                                  : "En proyecto".equals(estado) ? CYAN
                                  : "Ocupado".equals(estado)     ? AMBER
                                  : PINK;
                c.setForeground(colorEstado);
                c.setFont(F_BOLD);
                c.setText("● " + estado);
            }
            return c;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  CARGA, BÚSQUEDA Y ACCIONES
    // ══════════════════════════════════════════════════════════════════
    private void cargarProductores() {
        worker(() -> svc.obtenerTodos(), this::poblar, "Error al cargar");
    }

    private void buscar() {
        String q = campoBusqueda.getText().trim();
        worker(() -> svc.buscar(q), this::poblar, "Error al buscar");
    }

    void poblar(List<Productor> lista) {
        modeloTabla.setRowCount(0);
        for (Productor p : lista) {
            modeloTabla.addRow(new Object[]{
                p.getIdProductor(),
                p.getNombre(),
                p.getEspecialidad()  != null ? p.getEspecialidad()  : "",
                p.getNacionalidad()  != null ? p.getNacionalidad()  : "",
                String.format("$%.0f", p.getTarifaHora()),
                p.getEstado()        != null ? p.getEstado()        : "Disponible"
            });
        }

        long   esp  = lista.stream().map(Productor::getEspecialidad).distinct().count();
        double prom = lista.stream().mapToDouble(Productor::getTarifaHora).average().orElse(0);
        double max  = lista.stream().mapToDouble(Productor::getTarifaHora).max().orElse(0);

        lblTotal.setText(String.valueOf(lista.size()));
        lblEspecialidades.setText(String.valueOf(esp));
        lblTarifaProm.setText(String.format("$%.0f", prom));
        lblTarifaMax.setText(String.format("$%.0f", max));

        actualizarRanking(lista);
        if (graficoBarras != null) graficoBarras.setDatos(lista);

        lblResTotal.setText(String.valueOf(lista.size()));
        lblResEsp.setText(String.valueOf(esp));
        if (!lista.isEmpty()) {
            Productor top = lista.stream()
                .max(Comparator.comparingDouble(Productor::getTarifaHora)).orElse(null);
            lblResTop.setText(top != null ? recortar(top.getNombre(), 18) : "—");
        } else {
            lblResTop.setText("—");
        }
    }

    private void accionEditar() {
        int row = tabla.getSelectedRow();
        if (row < 0) { toast("Selecciona un productor primero", MainFrame.ToastType.INFO); return; }
        new Formproductordialog(this, row).setVisible(true);
    }

    private void accionEliminar() {
        int row = tabla.getSelectedRow();
        if (row < 0) { toast("Selecciona un productor primero", MainFrame.ToastType.INFO); return; }
        String nombre = modeloTabla.getValueAt(row, COL_NOMBRE).toString();
        int    id     = (int) modeloTabla.getValueAt(row, COL_ID);
        if (JOptionPane.showConfirmDialog(this,
                "¿Eliminar a \"" + nombre + "\"?",
                "Z-One — Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            worker(() -> { svc.darDeBaja(id); return svc.obtenerTodos(); }, lista -> {
                poblar(lista);
                toast("Productor eliminado", MainFrame.ToastType.SUCCESS);
            }, "Error al eliminar");
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  DIÁLOGO CREAR / EDITAR
    // ══════════════════════════════════════════════════════════════════
    private void dialogFormulario(Integer filaEditar) {
        boolean esEdit = filaEditar != null;
        int    id  = esEdit ? (int)    modeloTabla.getValueAt(filaEditar, COL_ID)           : 0;
        String nom = esEdit ? (String) modeloTabla.getValueAt(filaEditar, COL_NOMBRE)       : "";
        String esp = esEdit ? (String) modeloTabla.getValueAt(filaEditar, COL_ESPECIALIDAD) : "";
        String nac = esEdit ? (String) modeloTabla.getValueAt(filaEditar, COL_NACIONALIDAD) : "";
        String tar = esEdit
            ? modeloTabla.getValueAt(filaEditar, COL_TARIFA).toString().replace("$", "")
            : "0";
        String est = esEdit ? (String) modeloTabla.getValueAt(filaEditar, COL_ESTADO) : "Disponible";

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            esEdit ? "Editar productor" : "Nuevo productor", true);
        dlg.setResizable(false);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(10, 8, 24));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        root.add(bandaCabecera(esEdit), BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(24, 30, 24, 30));

        JTextField fNom = dlgField(nom);
        JTextField fEsp = dlgField(esp);
        JTextField fNac = dlgField(nac);
        JTextField fTar = dlgField(tar);
        JTextField fEst = dlgField(est);

        main.add(dlgFilaDoble("NOMBRE COMPLETO *", fNom, "ESPECIALIDAD *", fEsp));
        main.add(Box.createVerticalStrut(15));
        main.add(dlgFilaDoble("NACIONALIDAD",      fNac, "TARIFA POR HORA ($)", fTar));
        main.add(Box.createVerticalStrut(15));
        main.add(dlgFilaCampo("ESTADO",            fEst));
        main.add(Box.createVerticalStrut(26));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        ZBtn btnCanc = new ZBtn("Cancelar", false);
        ZBtn btnSave = new ZBtn(esEdit ? "💾  Guardar cambios" : "✦  Crear productor", true);
        btnCanc.setPreferredSize(new Dimension(112, 40));
        btnSave.setPreferredSize(new Dimension(186, 40));
        btnCanc.addActionListener(e -> dlg.dispose());
        btnSave.addActionListener(e -> guardar(esEdit, id, fNom, fEsp, fNac, fTar, fEst, dlg));
        btnRow.add(btnCanc);
        btnRow.add(btnSave);
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
        JPanel band = new JPanel(new BorderLayout(14, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0, 0, new Color(37, 99, 235),
                    getWidth(), getHeight(), new Color(14, 50, 140)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, 0, new Color(255,255,255,45),
                    0, getHeight(), new Color(255,255,255,0)));
                g2.fillRect(0, 0, getWidth(), getHeight()/2);
                g2.setColor(CYAN);
                g2.fillRect(0, getHeight()-2, getWidth(), 2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        band.setOpaque(false);
        band.setBorder(new EmptyBorder(20, 26, 20, 26));
        band.setPreferredSize(new Dimension(0, 90));

        JLabel ico = new JLabel(esEdit ? "✏" : "🎚", SwingConstants.CENTER) {
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
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        ico.setForeground(Color.WHITE);
        ico.setPreferredSize(new Dimension(50, 50));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel t = mkLabel(esEdit ? "Editar productor" : "Nuevo productor",
            new Font("Segoe UI", Font.BOLD, 21), Color.WHITE);
        JLabel s = mkLabel(esEdit
            ? "ACTUALIZA LA INFORMACIÓN DEL PRODUCTOR"
            : "REGISTRA UN NUEVO PRODUCTOR EN Z-ONE",
            F_SUB, new Color(255,255,255,185));
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

    private void guardar(boolean esEdit, int id,
            JTextField fNom, JTextField fEsp,
            JTextField fNac, JTextField fTar, JTextField fEst,
            JDialog dlg) {
        String nom = fNom.getText().trim();
        String esp = fEsp.getText().trim();
        String nac = fNac.getText().trim();
        String est = fEst.getText().trim().isEmpty() ? "Disponible" : fEst.getText().trim();
        double tarifa;
        try {
            tarifa = fTar.getText().trim().isEmpty()
                ? 0 : Double.parseDouble(fTar.getText().trim());
        } catch (NumberFormatException ex) {
            toast("La tarifa debe ser un número", MainFrame.ToastType.ERROR);
            return;
        }

        worker(() -> {
            if (esEdit) {
                svc.modificar(id, nom, esp, tarifa, nac, est);
            } else {
                svc.registrar(nom, esp, tarifa, nac);
            }
            return svc.obtenerTodos();
        }, lista -> {
            poblar(lista);
            toast(esEdit ? "Productor actualizado" : "Productor creado: " + nom,
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
                g2.setStroke(new BasicStroke(foco ? 1.8f : 1f));
                g2.drawRoundRect(2,2,getWidth()-6,getHeight()-6,10,10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(TXT_PRI);
        f.setOpaque(false);
        f.setCaretColor(PURPLE_LT);
        f.setBorder(new EmptyBorder(0, 14, 0, 14));
        f.setPreferredSize(new Dimension(200, 44));
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { f.repaint(); }
            public void focusLost (java.awt.event.FocusEvent e) { f.repaint(); }
        });
        return f;
    }

    private JPanel dlgFilaCampo(String label, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 7));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        JLabel l = mkLabel(label, new Font("Segoe UI", Font.BOLD, 10), PURPLE_LT);
        p.add(l,     BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private JPanel dlgFilaDoble(String l1, JComponent c1, String l2, JComponent c2) {
        JPanel p = new JPanel(new GridLayout(1, 2, 14, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        p.add(dlgFilaCampo(l1, c1));
        p.add(dlgFilaCampo(l2, c2));
        return p;
    }

    // ══════════════════════════════════════════════════════════════════
    //  UTILIDADES
    // ══════════════════════════════════════════════════════════════════
    static JLabel mkLabel(String txt, Font f, Color c) {
        JLabel l = new JLabel(txt);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    JTextField mkTextField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(COL_BRD);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.putClientProperty("JTextField.placeholderText", placeholder);
        f.setFont(F_BODY);
        f.setForeground(TXT_PRI);
        f.setOpaque(false);
        f.setCaretColor(TXT_PRI);
        f.setBorder(new EmptyBorder(0, 14, 0, 14));
        return f;
    }

    static Graphics2D g2d(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return g2;
    }

    String recortar(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max-1) + "…" : s;
    }

    void worker(java.util.concurrent.Callable<List<Productor>> tarea,
                java.util.function.Consumer<List<Productor>> fin, String err) {
        new SwingWorker<List<Productor>, Void>() {
            @Override protected List<Productor> doInBackground() throws Exception { return tarea.call(); }
            @Override protected void done() {
                try { fin.accept(get()); }
                catch (Exception ex) { toast(err + ": " + ex.getMessage(), MainFrame.ToastType.ERROR); }
            }
        }.execute();
    }

    void toast(String msg, MainFrame.ToastType tipo) {
        MainFrame.showToast(msg, tipo);
    }

    // ══════════════════════════════════════════════════════════════════
    //  ZBtn
    // ══════════════════════════════════════════════════════════════════
    static class ZBtn extends JButton {
        private final boolean primary;
        ZBtn(String text, boolean primary) {
            super(text);
            this.primary = primary;
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(primary ? Color.WHITE : TXT_PRI);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 18, 8, 18));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = g2d(g);
            if (primary) {
                g2.setColor(getModel().isPressed() ? new Color(29,78,216) : PURPLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (!getModel().isPressed()) {
                    g2.setPaint(new GradientPaint(0,0,new Color(255,255,255,28),
                        0, getHeight()/2f, new Color(0,0,0,0)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight()/2, 10, 10);
                }
            } else {
                g2.setColor(getModel().isRollover() ? new Color(14,34,80) : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
        private static Graphics2D g2d(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            return g2;
        }
    }
}