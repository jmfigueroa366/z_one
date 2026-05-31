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
    //  PALETA — misma que formArtista (tema claro)
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
        // Card blanca con borde redondeado — igual que Artistas
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                // Franja izquierda morada
                g2.setColor(PURPLE);
                g2.fillRoundRect(0, 0, 5, getHeight(), 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(18, 22, 18, 22));

        // — Icono + títulos —
        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        // Icono redondeado (igual al de Artistas)
        JPanel icoWrap = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(99, 91, 255, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icoWrap.setOpaque(false);
        icoWrap.setPreferredSize(new Dimension(46, 46));
        icoWrap.setMaximumSize(new Dimension(46, 46));
        icoWrap.setLayout(new GridBagLayout());
        JLabel ico = mkLabel("🎚", new Font("Segoe UI Emoji", Font.PLAIN, 22), PURPLE);
        icoWrap.add(ico);
        icoWrap.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = mkLabel("Productores", F_TITLE, TXT_PRI);
        JLabel sub   = mkLabel("GESTIÓN DE PRODUCTORES · EQUIPO TÉCNICO · ESPECIALIDADES",
                                F_SUB, TXT_SEC);
        title.setAlignmentX(LEFT_ALIGNMENT);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        // Chips de estado — igual que Artistas ("Conectado", "X productores", etc.)
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.setAlignmentX(LEFT_ALIGNMENT);

        JLabel chipConectado = mkChip("● Conectado",   new Color(16,185,129,30), GREEN);
        chips.add(chipConectado);
        // Los chips de conteo se actualizan dinámicamente vía lblChipProd / lblChipEsp
        chips.add(mkChip("🎚  productores", new Color(99,91,255,20), PURPLE));
        chips.add(mkChip("🎛  especialidades", new Color(6,182,212,20), CYAN));

        titulos.add(icoWrap);
        titulos.add(Box.createVerticalStrut(8));
        titulos.add(title);
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(sub);
        titulos.add(Box.createVerticalStrut(6));
        titulos.add(chips);

        // — Acciones: buscar + nuevo —
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

        card.add(titulos, BorderLayout.WEST);
        card.add(acc,     BorderLayout.EAST);
        return card;
    }

    /** Chip de estado estilo Artistas */
    private JLabel mkChip(String texto, Color bgColor, Color fgColor) {
        JLabel chip = new JLabel(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chip.setForeground(fgColor);
        chip.setBorder(new EmptyBorder(4, 10, 4, 10));
        chip.setOpaque(false);
        return chip;
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
        p.add(statCard("ESPECIALIDADES",     lblEspecialidades, CYAN,   "🎛"));
        p.add(statCard("TARIFA PROMEDIO",    lblTarifaProm,     GREEN,  "💵"));
        p.add(statCard("TARIFA MÁXIMA",      lblTarifaMax,      AMBER,  "⭐"));
        return p;
    }

    private JPanel statCard(String titulo, JLabel valor, Color acento, String emoji) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                // Línea superior de color
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

        JPanel headLeft = new JPanel();
        headLeft.setOpaque(false);
        headLeft.setLayout(new BoxLayout(headLeft, BoxLayout.X_AXIS));
        headLeft.add(mkLabel("Lista de productores", F_BOLD, TXT_PRI));
        headLeft.add(Box.createHorizontalStrut(10));
        JLabel subTag = mkLabel("datos en tiempo real desde Oracle",
            new Font("Segoe UI", Font.PLAIN, 10), TXT_SEC);
        headLeft.add(subTag);
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

        card.add(head,   BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);
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
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                l.setBackground(new Color(248, 249, 253));
                l.setForeground(TXT_SEC);
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
                g2.setColor(BG_CARD);
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
                g2.setColor(new Color(253, 251, 240));
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
        cab.add(titulo, BorderLayout.WEST);

        JLabel subRight = mkLabel("por tarifa/hora", new Font("Segoe UI", Font.PLAIN, 9), TXT_SEC);
        cab.add(subRight, BorderLayout.EAST);

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
        Color acento = puesto == 1 ? ORO : puesto == 2 ? PLATA : puesto == 3 ? BRONCE : PURPLE_LT;
        String medalla = puesto == 1 ? "🥇" : puesto == 2 ? "🥈" : puesto == 3 ? "🥉" : "#"+puesto;
        final Color ac = acento;

        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(esPodio ? new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 12) : BG_ROW_B);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                if (esPodio) {
                    g2.setColor(new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 60));
                    g2.setStroke(new BasicStroke(1.2f));
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
                g2.setColor(BG_CARD);
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
                g2.setColor(new Color(240, 252, 255));
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
            new Color(99, 91, 255), new Color(6,182,212), new Color(16,185,129),
            new Color(245,158, 11), new Color(236, 72,153),
            PURPLE_LT, CYAN, GREEN, AMBER, PINK
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
            // Líneas de cuadrícula claras
            g2.setColor(new Color(220, 225, 240));
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
                // Sombra suave
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 30));
                g2.fillRoundRect(bX-2, bY-2, barW+4, bH+4, 8, 8);
                g2.setStroke(new BasicStroke(1f));
                // Barra con gradiente
                g2.setPaint(new GradientPaint(bX, bY, c,
                    bX, bY + bH, new Color(c.getRed(), c.getGreen(), c.getBlue(), 160)));
                g2.fillRoundRect(bX, bY, barW, bH, 6, 6);
                // Valor encima
                String val = "$" + (int)p.getTarifaHora();
                g2.setFont(new Font("Consolas", Font.BOLD, 9));
                g2.setColor(c);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(val, bX + (barW - fm.stringWidth(val)) / 2, bY - 5);
                // Nombre debajo
                String nombre = abreviar(p.getNombre(), 7);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2.setColor(TXT_SEC);
                FontMetrics fm2 = g2.getFontMetrics();
                g2.drawString(nombre, bX + (barW - fm2.stringWidth(nombre)) / 2,
                    marginTop + areaH + 14);
                // Punto de color
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
                g2.setColor(BG_CARD);
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
                g2.setColor(new Color(246, 245, 255));
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
            if (col == COL_ID)           { c.setForeground(PURPLE); c.setFont(F_MONO_B); }
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
                p.getEspecialidad()    != null ? p.getEspecialidad()    : "",
                p.getNacionalidad()    != null ? p.getNacionalidad()     : "",
                String.format("$%.0f", p.getTarifaHora()),
                p.getEstado()          != null ? p.getEstado()           : "Disponible"
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
                g2.setColor(BG_FIELD);
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
                g2.setColor(getModel().isPressed() ? new Color(79, 70, 229) : PURPLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (!getModel().isPressed()) {
                    g2.setPaint(new GradientPaint(0,0,new Color(255,255,255,40),
                        0, getHeight()/2f, new Color(0,0,0,0)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight()/2, 10, 10);
                }
            } else {
                g2.setColor(getModel().isRollover() ? new Color(240, 242, 255) : BG_CARD);
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