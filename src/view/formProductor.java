package view;

import model.Productor;
import services.ProductorService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class formProductor extends JPanel {

    // ══════════════════════════════════════════════════════════════════
    //  PALETA — tema claro igual que formArtista
    // ══════════════════════════════════════════════════════════════════
    static final Color BG_DEEP   = new Color(245, 246, 250);
    static final Color BG_CARD   = new Color(255, 255, 255);
    static final Color BG_FIELD  = new Color(240, 242, 248);
    static final Color BG_ROW_A  = new Color(255, 255, 255);
    static final Color BG_ROW_B  = new Color(248, 249, 253);
    static final Color COL_BRD   = new Color(220, 225, 240);
    static final Color PURPLE    = new Color(99,  91, 255);
    static final Color PURPLE_LT = new Color(130, 122, 255);
    static final Color CYAN      = new Color(6,  182, 212);
    static final Color GREEN     = new Color(16, 185, 129);
    static final Color AMBER     = new Color(245, 158,  11);
    static final Color PINK      = new Color(236,  72, 153);
    static final Color TXT_PRI   = new Color( 30,  30,  60);
    static final Color TXT_SEC   = new Color(130, 140, 170);
    static final Color SEL_BG    = new Color( 99,  91, 255,  50);
    static final Color ORO       = new Color(234, 179,   8);
    static final Color PLATA     = new Color(148, 163, 184);
    static final Color BRONCE    = new Color(180, 120,  60);

    static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  26);
    static final Font F_SUB    = new Font("Segoe UI", Font.BOLD,   9);
    static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_BOLD   = new Font("Segoe UI", Font.BOLD,  13);
    static final Font F_MONO   = new Font("Consolas", Font.PLAIN, 11);
    static final Font F_MONO_B = new Font("Consolas", Font.BOLD,  11);

<<<<<<< HEAD
    // ══════════════════════════════════════════════════════════════════
    //  COLUMNAS
    // ══════════════════════════════════════════════════════════════════
=======
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
    static final String[] COLS = {
        "ID", "Nombre", "Especialidad", "Nacionalidad", "Tarifa/h", "Estado"
    };
    static final int COL_ID=0, COL_NOMBRE=1, COL_ESPECIALIDAD=2,
                     COL_NACIONALIDAD=3, COL_TARIFA=4, COL_ESTADO=5;

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

    // Animación — fade-in al cargar filas
    private float tableAlpha = 0f;
    private javax.swing.Timer fadeTimer;

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
    //  LAYOUT PRINCIPAL
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
        JPanel res = panelResumen();
        res.setPreferredSize(new Dimension(275, 138));
        der.add(rank,         BorderLayout.NORTH);
        der.add(panelGrafico(),BorderLayout.CENTER);
        der.add(res,          BorderLayout.SOUTH);

        add(izq, BorderLayout.CENTER);
        add(der, BorderLayout.EAST);
    }

    // ══════════════════════════════════════════════════════════════════
    //  ENCABEZADO — compacto, mismas proporciones que Artistas
    // ══════════════════════════════════════════════════════════════════
    private JPanel encabezado() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setColor(PURPLE);
                g2.fillRect(0, 12, 4, getHeight()-24);  // franja izquierda
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(12, 0));
        // ↓ padding compacto: igual al de formArtista
        card.setBorder(new EmptyBorder(14, 20, 14, 20));

        // — Icono + texto en una sola fila horizontal —
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        // Icono pequeño en caja redondeada
        JPanel icoBox = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(99, 91, 255, 22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icoBox.setOpaque(false);
        icoBox.setPreferredSize(new Dimension(42, 42));
        icoBox.add(mkLabel("🎚", new Font("Segoe UI Emoji", Font.PLAIN, 20), PURPLE));

        // Texto: título + subtítulo + chips apilados
        JPanel txtCol = new JPanel();
        txtCol.setOpaque(false);
        txtCol.setLayout(new BoxLayout(txtCol, BoxLayout.Y_AXIS));

        JLabel title = mkLabel("Productores", F_TITLE, TXT_PRI);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = mkLabel("GESTIÓN DE PRODUCTORES · EQUIPO TÉCNICO · ESPECIALIDADES",
                              F_SUB, TXT_SEC);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        chips.setOpaque(false);
        chips.setAlignmentX(LEFT_ALIGNMENT);
        chips.add(mkChip("● Conectado",      new Color(16,185,129,25), GREEN));
        chips.add(mkChip("🎚  productores",   new Color(99, 91,255,18), PURPLE));
        chips.add(mkChip("🎛  especialidades",new Color(6, 182,212,18), CYAN));

        txtCol.add(title);
        txtCol.add(Box.createVerticalStrut(1));
        txtCol.add(sub);
        txtCol.add(Box.createVerticalStrut(4));
        txtCol.add(chips);

        left.add(icoBox);
        left.add(txtCol);

        // — Acciones derecha —
        JPanel acc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acc.setOpaque(false);
        campoBusqueda = mkTextField("🔍  Buscar productor...");
        campoBusqueda.setPreferredSize(new Dimension(210, 36));
        campoBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { buscar(); }
        });
        ZBtn btnNuevo = new ZBtn("＋ Nuevo productor", true);
        btnNuevo.setPreferredSize(new Dimension(170, 36));
        btnNuevo.addActionListener(e -> new Formproductordialog(this, null).setVisible(true));
        acc.add(campoBusqueda);
        acc.add(btnNuevo);

        card.add(left, BorderLayout.WEST);
        card.add(acc,  BorderLayout.EAST);
        return card;
    }

    private JLabel mkChip(String texto, Color bg, Color fg) {
        JLabel chip = new JLabel(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chip.setForeground(fg);
        chip.setBorder(new EmptyBorder(3, 9, 3, 9));
        chip.setOpaque(false);
        return chip;
    }

    // ══════════════════════════════════════════════════════════════════
    //  STAT CARDS — con animación hover
    // ══════════════════════════════════════════════════════════════════
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
        // hover state
        final boolean[] hov = {false};
        final float[] scale = {1f};

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                // sombra suave cuando hover
                if (hov[0]) {
                    g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 18));
                    g2.fillRoundRect(-3, 3, getWidth()+6, getHeight()+2, 14, 14);
                }
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                Color border = hov[0] ? new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 120) : COL_BRD;
                g2.setColor(border);
                g2.setStroke(new BasicStroke(hov[0] ? 1.5f : 1f));
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
        card.setBorder(new EmptyBorder(12, 14, 12, 14));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover animation
        card.addMouseListener(new MouseAdapter() {
            javax.swing.Timer t;
            @Override public void mouseEntered(MouseEvent e) {
                hov[0] = true; card.repaint();
                animateValue(valor, acento.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                hov[0] = false; card.repaint();
                valor.setForeground(acento);
            }
            private void animateValue(JLabel lbl, Color bright) {
                if (t != null) t.stop();
                final int[] step = {0};
                t = new javax.swing.Timer(16, ev -> {
                    step[0]++;
                    float frac = Math.min(step[0] / 8f, 1f);
                    int r = (int)(acento.getRed()   + (bright.getRed()   - acento.getRed())   * frac);
                    int g2 = (int)(acento.getGreen() + (bright.getGreen() - acento.getGreen()) * frac);
                    int b  = (int)(acento.getBlue()  + (bright.getBlue()  - acento.getBlue())  * frac);
                    lbl.setForeground(new Color(Math.min(r,255), Math.min(g2,255), Math.min(b,255)));
                    if (step[0] >= 8) ((javax.swing.Timer)ev.getSource()).stop();
                });
                t.start();
            }
        });

        JLabel emo = mkLabel(emoji, new Font("Segoe UI Emoji", Font.PLAIN, 20), acento);
        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel lTit = mkLabel(titulo, F_SUB, TXT_SEC);
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

    // ══════════════════════════════════════════════════════════════════
    //  PANEL TABLA
    // ══════════════════════════════════════════════════════════════════
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

        // Wrap con fade-in al pintar
        JPanel fadeWrap = new JPanel(new BorderLayout()) {
            @Override protected void paintChildren(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tableAlpha));
                super.paintChildren(g2);
                g2.dispose();
            }
        };
        fadeWrap.setOpaque(false);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(0,0,0,0));
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        fadeWrap.add(scroll);

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.setBorder(new EmptyBorder(14, 18, 10, 18));
        JPanel headLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headLeft.setOpaque(false);
        headLeft.add(mkLabel("Lista de productores", F_BOLD, TXT_PRI));
        headLeft.add(mkLabel("datos en tiempo real desde Oracle",
            new Font("Segoe UI", Font.PLAIN, 10), TXT_SEC));
        head.add(headLeft, BorderLayout.WEST);

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

        card.add(head,    BorderLayout.NORTH);
        card.add(fadeWrap,BorderLayout.CENTER);
        card.add(btnRow,  BorderLayout.SOUTH);
        return card;
    }

    private void estilizarTabla() {
        tabla.setOpaque(false);
        tabla.setBackground(BG_CARD);
        tabla.setForeground(TXT_PRI);
        tabla.setFont(F_BODY);
        tabla.setRowHeight(42);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0,0));
        tabla.setSelectionBackground(SEL_BG);
        tabla.setSelectionForeground(TXT_PRI);
        tabla.setFocusable(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader th = tabla.getTableHeader();
        th.setBackground(new Color(248, 249, 253));
        th.setForeground(TXT_SEC);
        th.setFont(new Font("Segoe UI", Font.BOLD, 9));
        th.setBorder(BorderFactory.createMatteBorder(0,0,1,0, COL_BRD));
        th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(0, 34));
        th.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,s,f,r,c);
                l.setBackground(new Color(248,249,253));
                l.setForeground(TXT_SEC);
                l.setFont(new Font("Segoe UI", Font.BOLD, 9));
                l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0,0,1,0,COL_BRD),
                    new EmptyBorder(0,16,0,16)));
                l.setOpaque(true);
                return l;
            }
        });
<<<<<<< HEAD

        int[] w = {52, 155, 130, 110, 90, 90};
        for (int i = 0; i < w.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

=======
        int[] w = {52,155,130,110,90,90};
        for (int i=0;i<w.length;i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        tabla.setDefaultRenderer(Object.class, new CeldaRenderer());
    }

    // ══════════════════════════════════════════════════════════════════
    //  RANKING
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelRanking() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
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

        JPanel cab = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(255,251,235));
                g2.fillRoundRect(0,0,getWidth(),getHeight()+12,12,12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cab.setOpaque(false);
<<<<<<< HEAD
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
=======
        cab.setBorder(new EmptyBorder(11,14,11,14));
        JLabel t = new JLabel("🏆  TOP TARIFAS");
        t.setFont(new Font("Segoe UI",Font.BOLD,13));
        t.setForeground(ORO);
        cab.add(t, BorderLayout.WEST);
        cab.add(mkLabel("por tarifa/hora", new Font("Segoe UI",Font.PLAIN,9), TXT_SEC), BorderLayout.EAST);
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d

        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0,ORO,getWidth()*0.6f,0,new Color(0,0,0,0)));
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

        rankingContainer = new JPanel();
        rankingContainer.setOpaque(false);
        rankingContainer.setLayout(new BoxLayout(rankingContainer, BoxLayout.Y_AXIS));
        rankingContainer.setBorder(new EmptyBorder(10,10,10,10));

        JScrollPane scroll = new JScrollPane(rankingContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(0,0,0,0));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4,0));

        inner.add(topSect, BorderLayout.NORTH);
        inner.add(scroll,  BorderLayout.CENTER);
        return inner;
    }

    void actualizarRanking(List<Productor> lista) {
        rankingContainer.removeAll();
        List<Productor> orden = new ArrayList<>(lista);
        orden.sort(Comparator.comparingDouble(Productor::getTarifaHora).reversed());
        if (orden.isEmpty()) {
            rankingContainer.add(mkLabel("Sin productores", F_MONO.deriveFont(10f), TXT_SEC));
        } else {
            double max = orden.get(0).getTarifaHora();
            if (max<=0) max=1;
            for (int i=0;i<orden.size();i++) {
                boolean podio = i<3;
                JPanel fila = filaRanking(i+1, orden.get(i), max, podio);
                // fade-in escalonado por fila
                fila.setVisible(false);
                final JPanel f = fila;
                int delay = i * 60;
                new javax.swing.Timer(delay, e -> {
                    f.setVisible(true); ((javax.swing.Timer)e.getSource()).stop();
                }).start();
                rankingContainer.add(fila);
                rankingContainer.add(Box.createVerticalStrut(podio?6:4));
            }
        }
        rankingContainer.revalidate();
        rankingContainer.repaint();
    }

<<<<<<< HEAD
    private JPanel filaRanking(int puesto, Productor p, double maxTarifa, boolean esPodio) {
        Color acento  = puesto == 1 ? ORO : puesto == 2 ? PLATA : puesto == 3 ? BRONCE : PURPLE_LT;
        String medalla = puesto == 1 ? "🥇" : puesto == 2 ? "🥈" : puesto == 3 ? "🥉" : "#"+puesto;
        final Color ac = acento;
=======
    private JPanel filaRanking(int puesto, Productor p, double maxT, boolean podio) {
        Color ac = puesto==1?ORO : puesto==2?PLATA : puesto==3?BRONCE : PURPLE_LT;
        String med = puesto==1?"🥇":puesto==2?"🥈":puesto==3?"🥉":"#"+puesto;
        final boolean[] hov = {false};
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d

        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                Color bg = hov[0]
                    ? new Color(ac.getRed(),ac.getGreen(),ac.getBlue(),22)
                    : (podio ? new Color(ac.getRed(),ac.getGreen(),ac.getBlue(),10) : BG_ROW_B);
                g2.setColor(bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),9,9);
                if (podio || hov[0]) {
                    g2.setColor(new Color(ac.getRed(),ac.getGreen(),ac.getBlue(), hov[0]?90:50));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,9,9);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(8,0));
        fila.setBorder(new EmptyBorder(podio?8:5,10,podio?8:5,10));
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE,podio?50:38));
        fila.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fila.addMouseListener(new MouseAdapter(){
            @Override public void mouseEntered(MouseEvent e){hov[0]=true; fila.repaint();}
            @Override public void mouseExited(MouseEvent e) {hov[0]=false;fila.repaint();}
        });

        JLabel lblM = new JLabel(med, SwingConstants.CENTER);
        lblM.setFont(podio ? new Font("Segoe UI Emoji",Font.PLAIN,19)
                           : new Font("Consolas",Font.BOLD,12));
        lblM.setForeground(ac);
        lblM.setPreferredSize(new Dimension(28,0));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt,BoxLayout.Y_AXIS));
        JLabel nom = mkLabel(recortar(p.getNombre(),podio?16:18),
            new Font("Segoe UI",Font.BOLD,podio?12:11), TXT_PRI);
        nom.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(nom);
        if (podio && p.getEspecialidad()!=null) {
            JLabel esp=mkLabel(recortar(p.getEspecialidad(),18),F_MONO.deriveFont(8.5f),TXT_SEC);
            esp.setAlignmentX(LEFT_ALIGNMENT);
            txt.add(Box.createVerticalStrut(1));
            txt.add(esp);
        }

        JLabel monto = mkLabel(String.format("$%.0f",p.getTarifaHora()),
            new Font("Consolas",Font.BOLD,podio?14:11), ac);

        fila.add(lblM,  BorderLayout.WEST);
        fila.add(txt,   BorderLayout.CENTER);
        fila.add(monto, BorderLayout.EAST);
        return fila;
    }

    // ══════════════════════════════════════════════════════════════════
    //  GRÁFICO BARRAS — con animación de crecimiento
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelGrafico() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
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

        JPanel cab = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(240,252,255));
                g2.fillRoundRect(0,0,getWidth(),getHeight()+12,12,12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(11,14,11,14));
        JLabel tit = new JLabel("⊙  TARIFA POR PRODUCTOR");
        tit.setFont(new Font("Segoe UI",Font.BOLD,12));
        tit.setForeground(CYAN);
        JPanel tp=new JPanel(); tp.setOpaque(false);
        tp.setLayout(new BoxLayout(tp,BoxLayout.Y_AXIS));
        tp.add(tit);
        tp.add(Box.createVerticalStrut(2));
        tp.add(mkLabel("TARIFA/HORA (USD)",F_SUB,TXT_SEC));
        cab.add(tp, BorderLayout.WEST);

        JPanel sepC = new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g);
                g2.setPaint(new GradientPaint(0,0,CYAN,getWidth()*0.6f,0,new Color(0,0,0,0)));
                g2.fillRect(0,0,getWidth(),1); g2.dispose();
            }
        };
        sepC.setOpaque(false); sepC.setPreferredSize(new Dimension(0,1));

        JPanel ts=new JPanel(new BorderLayout()); ts.setOpaque(false);
        ts.add(cab,BorderLayout.CENTER); ts.add(sepC,BorderLayout.SOUTH);

        graficoBarras = new GraficoBarras();
        graficoBarras.setOpaque(false);
        graficoBarras.setBorder(new EmptyBorder(10,14,14,14));

        inner.add(ts,          BorderLayout.NORTH);
        inner.add(graficoBarras,BorderLayout.CENTER);
        return inner;
    }

    class GraficoBarras extends JPanel {
        private List<Productor> datos = new ArrayList<>();
        private float animPct = 0f;   // 0..1 para la animación de crecimiento
        private javax.swing.Timer growTimer;
        private final Color[] BARES = {
            PURPLE, CYAN, GREEN, AMBER, PINK, PURPLE_LT,
            new Color(99,91,255), new Color(6,182,212)
        };

        void setDatos(List<Productor> lista) {
            List<Productor> ord = new ArrayList<>(lista);
            ord.sort(Comparator.comparingDouble(Productor::getTarifaHora).reversed());
            datos = ord;
            animPct = 0f;
            if (growTimer!=null) growTimer.stop();
            growTimer = new javax.swing.Timer(12, e -> {
                animPct += 0.07f;
                if (animPct >= 1f) { animPct=1f; ((javax.swing.Timer)e.getSource()).stop(); }
                repaint();
            });
            growTimer.start();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (datos.isEmpty()) return;
            Graphics2D g2 = g2d(g);
<<<<<<< HEAD

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
=======
            int W=getWidth(), H=getHeight();
            int n=Math.min(datos.size(),8);
            double maxT=datos.stream().mapToDouble(Productor::getTarifaHora).max().orElse(1);
            if (maxT<=0) maxT=1;
            int mt=28, mb=42, aH=H-mt-mb;
            if (aH<10){g2.dispose();return;}
            int bW=Math.min(32,(W-20)/n-8);
            int totW=n*(bW+8)-8, sX=(W-totW)/2;

            g2.setColor(COL_BRD);
            g2.setStroke(new BasicStroke(0.7f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,1,new float[]{4,4},0));
            for (int r=1;r<=3;r++) { int y=mt+(aH*r/4); g2.drawLine(10,y,W-10,y); }

            for (int i=0;i<n;i++) {
                Productor p=datos.get(i);
                double ratio=p.getTarifaHora()/maxT;
                int bHfull=(int)(aH*ratio);
                int bH=(int)(bHfull*animPct);  // animado
                int bX=sX+i*(bW+8);
                int bY=mt+aH-bH;
                Color c=BARES[i%BARES.length];

                // Sombra
                g2.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),20));
                g2.fillRoundRect(bX-2,bY-2,bW+4,bH+4,8,8);
                // Barra con gradiente
                g2.setPaint(new GradientPaint(bX,bY,c, bX,bY+bH, new Color(c.getRed(),c.getGreen(),c.getBlue(),160)));
                g2.setStroke(new BasicStroke(1f));
                g2.fillRoundRect(bX,bY,bW,bH,6,6);
                // Valor
                if (animPct>=0.85f) {
                    String val="$"+(int)p.getTarifaHora();
                    g2.setFont(new Font("Consolas",Font.BOLD,9));
                    g2.setColor(c); FontMetrics fm=g2.getFontMetrics();
                    g2.drawString(val,bX+(bW-fm.stringWidth(val))/2,bY-5);
                }
                // Nombre
                String nom=abrev(p.getNombre(),7);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,9));
                g2.setColor(TXT_SEC); FontMetrics fm2=g2.getFontMetrics();
                g2.drawString(nom,bX+(bW-fm2.stringWidth(nom))/2,mt+aH+14);
                g2.setColor(c); g2.fillOval(bX+bW/2-3,mt+aH+22,6,6);
            }
            g2.dispose();
        }
        private String abrev(String s, int max) {
            if (s==null||s.isEmpty()) return "";
            String f=s.trim().split("\\s+")[0];
            return f.length()>max?f.substring(0,max):f;
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL RESUMEN
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelResumen() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
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

        JPanel cab = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=g2d(g);
                g2.setColor(new Color(246,245,255));
                g2.fillRoundRect(0,0,getWidth(),getHeight()+12,12,12);
                g2.dispose(); super.paintComponent(g);
            }
        };
        cab.setOpaque(false); cab.setBorder(new EmptyBorder(11,14,11,14));
        JLabel tl=new JLabel("⊙  RESUMEN");
        tl.setFont(new Font("Segoe UI",Font.BOLD,12)); tl.setForeground(PURPLE_LT);
        cab.add(tl,BorderLayout.WEST);

        JPanel sepP=new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g);
                g2.setPaint(new GradientPaint(0,0,PURPLE_LT,getWidth()*0.6f,0,new Color(0,0,0,0)));
                g2.fillRect(0,0,getWidth(),1); g2.dispose();
            }
        };
        sepP.setOpaque(false); sepP.setPreferredSize(new Dimension(0,1));

        JPanel top=new JPanel(new BorderLayout()); top.setOpaque(false);
        top.add(cab,BorderLayout.CENTER); top.add(sepP,BorderLayout.SOUTH);

        lblResTotal=new JLabel("0"); lblResEsp=new JLabel("0"); lblResTop=new JLabel("—");

        JPanel filas=new JPanel(); filas.setOpaque(false);
        filas.setLayout(new BoxLayout(filas,BoxLayout.Y_AXIS));
        filas.setBorder(new EmptyBorder(8,14,8,14));
        filas.add(filaRes("Total productores",    lblResTotal,PURPLE_LT));
        filas.add(Box.createVerticalStrut(2)); sepH(filas); filas.add(Box.createVerticalStrut(2));
        filas.add(filaRes("Especialidades únicas",lblResEsp,CYAN));
        filas.add(Box.createVerticalStrut(2)); sepH(filas); filas.add(Box.createVerticalStrut(2));
        filas.add(filaRes("Productor top",        lblResTop,ORO));

        inner.add(top,  BorderLayout.NORTH);
        inner.add(filas,BorderLayout.CENTER);
        return inner;
    }

    private JPanel filaRes(String lbl, JLabel val, Color ac) {
        JPanel row=new JPanel(new BorderLayout()); row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE,26));
        JLabel l=mkLabel(lbl,new Font("Segoe UI",Font.PLAIN,11),TXT_SEC);
        val.setFont(new Font("Segoe UI",Font.BOLD,12)); val.setForeground(ac);
        row.add(l,BorderLayout.WEST); row.add(val,BorderLayout.EAST);
        return row;
    }

    private void sepH(JPanel parent) {
        JPanel s=new JPanel(){@Override protected void paintComponent(Graphics g){
            g.setColor(COL_BRD); g.fillRect(0,0,getWidth(),1);}};
        s.setOpaque(false); s.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        s.setAlignmentX(LEFT_ALIGNMENT); parent.add(s);
    }

    // ══════════════════════════════════════════════════════════════════
    //  RENDERER CELDAS
    // ══════════════════════════════════════════════════════════════════
    private class CeldaRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {
            JLabel c=(JLabel)super.getTableCellRendererComponent(t,val,sel,foc,row,col);
            c.setBorder(new EmptyBorder(0,16,0,16));
            c.setOpaque(true); c.setIcon(null);
            c.setBackground(sel?SEL_BG:(row%2==0?BG_ROW_A:BG_ROW_B));
            c.setForeground(TXT_PRI); c.setFont(F_BODY);
            if (col==COL_ID)           {c.setForeground(PURPLE);c.setFont(F_MONO_B);}
            if (col==COL_ESPECIALIDAD&&val!=null){c.setForeground(CYAN);c.setFont(F_BOLD);c.setText("● "+val);}
            if (col==COL_TARIFA&&val!=null)      {c.setForeground(GREEN);c.setFont(F_BOLD);}
            if (col==COL_ESTADO&&val!=null){
                String est=val.toString();
                Color ce="Disponible".equals(est)?GREEN:"En proyecto".equals(est)?CYAN
                         :"Ocupado".equals(est)?AMBER:PINK;
                c.setForeground(ce);c.setFont(F_BOLD);c.setText("● "+est);
            }
            return c;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  CARGA / BÚSQUEDA / ACCIONES
    // ══════════════════════════════════════════════════════════════════
    private void cargarProductores() { worker(()->svc.obtenerTodos(), this::poblar,"Error al cargar"); }
    private void buscar() {
        String q=campoBusqueda.getText().trim();
        worker(()->svc.buscar(q),this::poblar,"Error al buscar");
    }

    void poblar(List<Productor> lista) {
        modeloTabla.setRowCount(0);
        for (Productor p:lista) {
            modeloTabla.addRow(new Object[]{
<<<<<<< HEAD
                p.getIdProductor(),
                p.getNombre(),
                p.getEspecialidad()  != null ? p.getEspecialidad()  : "",
                p.getNacionalidad()  != null ? p.getNacionalidad()  : "",
                String.format("$%.0f", p.getTarifaHora()),
                p.getEstado()        != null ? p.getEstado()        : "Disponible"
=======
                p.getIdProductor(), p.getNombre(),
                p.getEspecialidad()!=null?p.getEspecialidad():"",
                p.getNacionalidad()!=null?p.getNacionalidad():"",
                String.format("$%.0f",p.getTarifaHora()),
                p.getEstado()!=null?p.getEstado():"Disponible"
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
            });
        }
        // Fade-in tabla
        tableAlpha=0f;
        if (fadeTimer!=null) fadeTimer.stop();
        fadeTimer=new javax.swing.Timer(16,e->{
            tableAlpha+=0.08f;
            if(tableAlpha>=1f){tableAlpha=1f;((javax.swing.Timer)e.getSource()).stop();}
            repaint();
        });
        fadeTimer.start();

        long   esp =lista.stream().map(Productor::getEspecialidad).distinct().count();
        double prom=lista.stream().mapToDouble(Productor::getTarifaHora).average().orElse(0);
        double max =lista.stream().mapToDouble(Productor::getTarifaHora).max().orElse(0);
        animarContador(lblTotal,          lista.size());
        animarContadorDouble(lblEspecialidades,(int)esp,null);
        lblTarifaProm.setText(String.format("$%.0f",prom));
        lblTarifaMax.setText( String.format("$%.0f",max));

        actualizarRanking(lista);
        if (graficoBarras!=null) graficoBarras.setDatos(lista);

        lblResTotal.setText(String.valueOf(lista.size()));
        lblResEsp.setText(String.valueOf(esp));
        if (!lista.isEmpty()) {
            Productor top=lista.stream().max(Comparator.comparingDouble(Productor::getTarifaHora)).orElse(null);
            lblResTop.setText(top!=null?recortar(top.getNombre(),18):"—");
        } else lblResTop.setText("—");
    }

    /** Anima un contador numérico de 0 → target */
    private void animarContador(JLabel lbl, int target) {
        final int[] cur={0};
        new javax.swing.Timer(20, e->{
            cur[0]+= Math.max(1,(target-cur[0])/4);
            if(cur[0]>=target){cur[0]=target;((javax.swing.Timer)e.getSource()).stop();}
            lbl.setText(String.valueOf(cur[0]));
        }).start();
    }
    private void animarContadorDouble(JLabel lbl, int target, String unused) {
        animarContador(lbl, target);
    }

    private void accionEditar() {
        int row=tabla.getSelectedRow();
        if(row<0){toast("Selecciona un productor primero",MainFrame.ToastType.INFO);return;}
        new Formproductordialog(this,row).setVisible(true);
    }
    private void accionEliminar() {
        int row=tabla.getSelectedRow();
        if(row<0){toast("Selecciona un productor primero",MainFrame.ToastType.INFO);return;}
        String nom=modeloTabla.getValueAt(row,COL_NOMBRE).toString();
        int id=(int)modeloTabla.getValueAt(row,COL_ID);
        if(JOptionPane.showConfirmDialog(this,"¿Eliminar a \""+nom+"\"?",
                "Z-One — Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            worker(()->{svc.darDeBaja(id);return svc.obtenerTodos();},lista->{
                poblar(lista); toast("Productor eliminado",MainFrame.ToastType.SUCCESS);
            },"Error al eliminar");
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
        JLabel l=new JLabel(txt); l.setFont(f); l.setForeground(c); return l;
    }
    JTextField mkTextField(String ph) {
        JTextField f=new JTextField(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g);
                g2.setColor(BG_FIELD); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(COL_BRD); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        f.putClientProperty("JTextField.placeholderText",ph);
        f.setFont(F_BODY); f.setForeground(TXT_PRI); f.setOpaque(false);
        f.setCaretColor(TXT_PRI); f.setBorder(new EmptyBorder(0,14,0,14));
        return f;
    }
    static Graphics2D g2d(Graphics g) {
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        return g2;
    }
    String recortar(String s,int max){
        if(s==null)return "";
        return s.length()>max?s.substring(0,max-1)+"…":s;
    }
    void worker(java.util.concurrent.Callable<List<Productor>> tarea,
                java.util.function.Consumer<List<Productor>> fin, String err) {
        new SwingWorker<List<Productor>,Void>(){
            @Override protected List<Productor> doInBackground() throws Exception{return tarea.call();}
            @Override protected void done(){
                try{fin.accept(get());}
                catch(Exception ex){toast(err+": "+ex.getMessage(),MainFrame.ToastType.ERROR);}
            }
        }.execute();
    }
    void toast(String msg,MainFrame.ToastType tipo){MainFrame.showToast(msg,tipo);}

    // ══════════════════════════════════════════════════════════════════
    //  ZBtn
    // ══════════════════════════════════════════════════════════════════
    static class ZBtn extends JButton {
        private final boolean primary;
        ZBtn(String text,boolean primary){
            super(text); this.primary=primary;
            setFont(new Font("Segoe UI",Font.BOLD,12));
            setForeground(primary?Color.WHITE:TXT_PRI);
            setOpaque(false); setContentAreaFilled(false); setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8,18,8,18));
        }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=g2d(g);
            if(primary){
                g2.setColor(getModel().isPressed()?new Color(79,70,229):PURPLE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                if(!getModel().isPressed()){
                    g2.setPaint(new GradientPaint(0,0,new Color(255,255,255,40),0,getHeight()/2f,new Color(0,0,0,0)));
                    g2.fillRoundRect(0,0,getWidth(),getHeight()/2,10,10);
                }
            } else {
                g2.setColor(getModel().isRollover()?new Color(240,242,255):BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(COL_BRD); g2.setStroke(new BasicStroke(1f));
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