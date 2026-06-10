package view;

import model.Productor;
import services.ProductorService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  26);
    static final Font F_SUB    = new Font("Segoe UI", Font.BOLD,   9);
    static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_BOLD   = new Font("Segoe UI", Font.BOLD,  13);
    static final Font F_MONO   = new Font("Consolas", Font.PLAIN, 11);
    static final Font F_MONO_B = new Font("Consolas", Font.BOLD,  11);

    static final String[] COLS = {
        "ID", "Nombre", "Especialidad", "Nacionalidad", "Estado"
    };
    static final int COL_ID=0, COL_NOMBRE=1, COL_ESPECIALIDAD=2,
                     COL_NACIONALIDAD=3, COL_ESTADO=4;

    // ══════════════════════════════════════════════════════════════════
    //  ESTADO
    // ══════════════════════════════════════════════════════════════════
    final ProductorService svc = new ProductorService();
    DefaultTableModel modeloTabla;
    JTable            tabla;
    private JTextField campoBusqueda;
    private JLabel     lblTotal, lblEspecialidades, lblEstados, lblNacionalidades;
    private DistribPanel distEstado, distEspecialidad, distNacionalidad;

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

        JPanel der = new JPanel(new GridLayout(3, 1, 0, 12));
        der.setOpaque(false);
        der.setBorder(new EmptyBorder(0, 14, 0, 0));
        der.setPreferredSize(new Dimension(275, 0));

        distEstado       = new DistribPanel("POR ESTADO",        "distribución de productores", CYAN);
        distEspecialidad = new DistribPanel("POR ESPECIALIDAD",  "equipo técnico",              PURPLE_LT);
        distNacionalidad = new DistribPanel("POR NACIONALIDAD",  "origen del equipo",           GREEN);

        der.add(distEstado.contenedor());
        der.add(distEspecialidad.contenedor());
        der.add(distNacionalidad.contenedor());

        add(izq, BorderLayout.CENTER);
        add(der, BorderLayout.EAST);
    }

    // ══════════════════════════════════════════════════════════════════
    //  ENCABEZADO
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
                g2.fillRect(0, 12, 4, getHeight()-24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(14, 20, 14, 20));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        // Icono ecualizador animado (barras que suben y bajan)
        final float[] eqFase = {0f};
        JPanel icoBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(99, 91, 255, 22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // 4 barras estilo ecualizador
                int n = 4, bw = 4, gap = 4;
                int totW = n*bw + (n-1)*gap;
                int sx = (getWidth()-totW)/2;
                int baseY = getHeight()-13, maxH = 18;
                for (int i=0;i<n;i++) {
                    double ph = eqFase[0] + i*0.7;
                    int h = (int)(maxH*(0.35 + 0.65*Math.abs(Math.sin(ph))));
                    int x = sx + i*(bw+gap);
                    g2.setColor(PURPLE);
                    g2.fillRoundRect(x, baseY-h, bw, h, 3, 3);
                }
                g2.dispose();
            }
        };
        icoBox.setOpaque(false);
        icoBox.setPreferredSize(new Dimension(42, 42));
        new javax.swing.Timer(60, e -> { eqFase[0] += 0.18f; icoBox.repaint(); }).start();

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
        chips.add(mkChip("● Conectado",     new Color(16,185,129,25), GREEN));
        chips.add(mkChip("productores",      new Color(99, 91,255,18), PURPLE));
        chips.add(mkChip("especialidades",   new Color(6, 182,212,18), CYAN));

        txtCol.add(title);
        txtCol.add(Box.createVerticalStrut(1));
        txtCol.add(sub);
        txtCol.add(Box.createVerticalStrut(4));
        txtCol.add(chips);

        left.add(icoBox);
        left.add(txtCol);

        JPanel acc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acc.setOpaque(false);
        campoBusqueda = mkTextField("Buscar productor...");
        campoBusqueda.setPreferredSize(new Dimension(210, 36));
        campoBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { buscar(); }
        });
        ZBtn btnNuevo = new ZBtn("+  Nuevo productor", true);
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
    //  STAT CARDS
    // ══════════════════════════════════════════════════════════════════
    private JPanel filaStats() {
        lblTotal          = new JLabel("0");
        lblEspecialidades = new JLabel("0");
        lblEstados        = new JLabel("0");
        lblNacionalidades = new JLabel("0");

        JPanel p = new JPanel(new GridLayout(1, 4, 12, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.add(statCard("TOTAL PRODUCTORES", lblTotal,          PURPLE, ""));
        p.add(statCard("ESPECIALIDADES",    lblEspecialidades, CYAN,   ""));
        p.add(statCard("ESTADOS",           lblEstados,        GREEN,  ""));
        p.add(statCard("NACIONALIDADES",    lblNacionalidades, AMBER,  ""));
        return p;
    }

    private JPanel statCard(String titulo, JLabel valor, Color acento, String emoji) {
        final boolean[] hov = {false};
        final JPanel[] emoRef = {null};

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
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

        card.addMouseListener(new MouseAdapter() {
            javax.swing.Timer t;
            @Override public void mouseEntered(MouseEvent e) {
                hov[0] = true; card.repaint();
                if (emoRef[0]!=null) emoRef[0].repaint();
                animateValue(valor, acento.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                hov[0] = false; card.repaint();
                if (emoRef[0]!=null) emoRef[0].repaint();
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

        // Icono dibujado: disco con anillo que crece al hacer hover
        JPanel emo = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                int cx = getWidth()/2, cy = getHeight()/2;
                int ring = hov[0] ? 16 : 13;
                g2.setColor(new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),40));
                g2.fillOval(cx-ring, cy-ring, ring*2, ring*2);
                g2.setColor(acento);
                g2.fillOval(cx-6, cy-6, 12, 12);
                g2.setColor(BG_CARD);
                g2.fillOval(cx-2, cy-2, 4, 4);
                g2.dispose();
            }
        };
        emo.setOpaque(false);
        emo.setPreferredSize(new Dimension(38, 38));
        emoRef[0] = emo;
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
        ZBtn btnEditar   = new ZBtn("Editar",   false);
        ZBtn btnEliminar = new ZBtn("Eliminar", false);
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
        int[] w = {52,180,150,130,100};
        for (int i=0;i<w.length;i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        tabla.setDefaultRenderer(Object.class, new CeldaRenderer());
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL DE DISTRIBUCIÓN (análisis) — reutilizable
    // ══════════════════════════════════════════════════════════════════
    class DistribPanel {
        private final JPanel inner;
        private final JPanel listaContainer;
        private final Color acento;
        private float pulsoNivel = 0f;   // 0..1 para el punto pulsante

        DistribPanel(String titulo, String subtitulo, Color acento) {
            this.acento = acento;

            inner = new JPanel() {
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

            JPanel cab = new JPanel(new BorderLayout(8,0)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = g2d(g);
                    g2.setColor(new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),14));
                    g2.fillRoundRect(0,0,getWidth(),getHeight()+12,12,12);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            cab.setOpaque(false);
            cab.setBorder(new EmptyBorder(10,14,10,14));

            // Punto pulsante animado (reemplaza al emoji)
            JPanel pulso = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = g2d(g);
                    int cx = getWidth()/2, cy = getHeight()/2;
                    // halo que late
                    int halo = (int)(5 + 4*pulsoNivel);
                    g2.setColor(new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),(int)(60*(1-pulsoNivel))+15));
                    g2.fillOval(cx-halo, cy-halo, halo*2, halo*2);
                    // núcleo
                    g2.setColor(acento);
                    g2.fillOval(cx-3, cy-3, 6, 6);
                    g2.dispose();
                }
            };
            pulso.setOpaque(false);
            pulso.setPreferredSize(new Dimension(18, 18));

            JLabel t = new JLabel(titulo);
            t.setFont(new Font("Segoe UI",Font.BOLD,12));
            t.setForeground(acento);

            JPanel tituloRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            tituloRow.setOpaque(false);
            tituloRow.add(pulso);
            tituloRow.add(t);

            cab.add(tituloRow, BorderLayout.WEST);
            cab.add(mkLabel(subtitulo, new Font("Segoe UI",Font.PLAIN,9), TXT_SEC), BorderLayout.EAST);

            // Timer del pulso continuo
            final float[] dir = {0.06f};
            new javax.swing.Timer(40, e -> {
                pulsoNivel += dir[0];
                if (pulsoNivel >= 1f) { pulsoNivel = 1f; dir[0] = -dir[0]; }
                if (pulsoNivel <= 0f) { pulsoNivel = 0f; dir[0] = -dir[0]; }
                pulso.repaint();
            }).start();

            JPanel sep = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = g2d(g);
                    g2.setPaint(new GradientPaint(0,0,acento,getWidth()*0.6f,0,new Color(0,0,0,0)));
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

            listaContainer = new JPanel();
            listaContainer.setOpaque(false);
            listaContainer.setLayout(new BoxLayout(listaContainer, BoxLayout.Y_AXIS));
            listaContainer.setBorder(new EmptyBorder(8,10,8,10));

            JScrollPane scroll = new JScrollPane(listaContainer);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.getViewport().setBackground(new Color(0,0,0,0));
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4,0));

            inner.add(topSect, BorderLayout.NORTH);
            inner.add(scroll,  BorderLayout.CENTER);
        }

        JPanel contenedor() { return inner; }

        /** Recibe conteos ya agrupados (etiqueta → cantidad) */
        void setDatos(Map<String,Integer> conteos, int total) {
            listaContainer.removeAll();
            if (conteos.isEmpty() || total<=0) {
                listaContainer.add(mkLabel("Sin datos", F_MONO.deriveFont(10f), TXT_SEC));
            } else {
                int max = conteos.values().stream().mapToInt(Integer::intValue).max().orElse(1);
                int idx = 0;
                for (Map.Entry<String,Integer> e : conteos.entrySet()) {
                    JPanel fila = filaBarra(e.getKey(), e.getValue(), max, total);
                    fila.setVisible(false);
                    final JPanel f = fila;
                    new javax.swing.Timer(idx*55, ev -> {
                        f.setVisible(true); ((javax.swing.Timer)ev.getSource()).stop();
                    }).start();
                    listaContainer.add(fila);
                    listaContainer.add(Box.createVerticalStrut(5));
                    idx++;
                }
            }
            listaContainer.revalidate();
            listaContainer.repaint();
        }

        private JPanel filaBarra(String etiqueta, int cantidad, int max, int total) {
            final float pct = total>0 ? (cantidad*100f/total) : 0;
            final float ratio = max>0 ? (cantidad/(float)max) : 0;

            // hov=hover · glow=intensidad hover · grow=crecimiento de carga 0..1
            final boolean[] hov  = {false};
            final float[]   glow = {0f};
            final float[]   grow = {0f};

            JPanel fila = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = g2d(g);
                    int alpha = (int)(26 * glow[0]);
                    if (alpha > 0) {
                        g2.setColor(new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),alpha));
                        g2.fillRoundRect(-6, -2, getWidth()+12, getHeight()+4, 9, 9);
                        g2.setColor(new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),(int)(70*glow[0])));
                        g2.setStroke(new BasicStroke(1f));
                        g2.drawRoundRect(-6, -2, getWidth()+11, getHeight()+3, 9, 9);
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            fila.setOpaque(false);
            fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
            fila.setAlignmentX(LEFT_ALIGNMENT);
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            fila.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Fila superior: etiqueta + conteo
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.setAlignmentX(LEFT_ALIGNMENT);
            top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
            JLabel lbl = mkLabel(recortar(etiqueta, 20),
                new Font("Segoe UI",Font.BOLD,11), TXT_PRI);
            final JLabel cnt = mkLabel(cantidad + "  ·  " + String.format("%.0f%%", pct),
                new Font("Consolas",Font.BOLD,10), acento);
            top.add(lbl, BorderLayout.WEST);
            top.add(cnt, BorderLayout.EAST);

            // Barra de progreso (crece al cargar, brilla y muestra destello)
            JPanel barra = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = g2d(g);
                    int w = getWidth(), h = getHeight();
                    // pista de fondo
                    g2.setColor(new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),18));
                    g2.fillRoundRect(0,0,w,h,h,h);
                    // ancho con easing (out-cubic) y crecimiento
                    float eased = 1f - (float)Math.pow(1 - grow[0], 3);
                    int bw = (int)(w * ratio * eased);
                    if (bw < h && ratio > 0) bw = h;
                    int endAlpha = Math.min((int)(150 + 80*glow[0]), 255);
                    g2.setPaint(new GradientPaint(0,0,acento, bw,0,
                        new Color(acento.getRed(),acento.getGreen(),acento.getBlue(),endAlpha)));
                    g2.fillRoundRect(0,0,bw,h,h,h);
                    // destello que recorre la barra mientras crece
                    if (grow[0] < 1f && bw > h) {
                        Shape clip = new java.awt.geom.RoundRectangle2D.Float(0,0,bw,h,h,h);
                        g2.setClip(clip);
                        int sx = (int)(bw * grow[0]);
                        g2.setPaint(new GradientPaint(sx-12,0,new Color(255,255,255,0),
                                                      sx,0,new Color(255,255,255,150)));
                        g2.fillRect(sx-12,0,12,h);
                        g2.setPaint(new GradientPaint(sx,0,new Color(255,255,255,150),
                                                      sx+12,0,new Color(255,255,255,0)));
                        g2.fillRect(sx,0,12,h);
                    }
                    g2.dispose();
                }
            };
            barra.setOpaque(false);
            barra.setAlignmentX(LEFT_ALIGNMENT);
            barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 7));
            barra.setPreferredSize(new Dimension(0, 7));

            // Crecimiento al aparecer la fila
            final javax.swing.Timer growT = new javax.swing.Timer(16, null);
            growT.addActionListener(ev -> {
                grow[0] += 0.045f;
                if (grow[0] >= 1f) { grow[0] = 1f; growT.stop(); }
                barra.repaint();
            });
            fila.addHierarchyListener(ev -> {
                if (fila.isShowing() && grow[0] == 0f && !growT.isRunning()) growT.start();
            });

            // Hover: glow sube/baja suave; etiqueta colorea y conteo agranda
            final javax.swing.Timer[] anim = {null};
            fila.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hov[0]=true;  iniciar(); }
                @Override public void mouseExited (MouseEvent e) { hov[0]=false; iniciar(); }
                private void iniciar() {
                    if (anim[0]!=null) anim[0].stop();
                    anim[0] = new javax.swing.Timer(16, ev -> {
                        glow[0] += hov[0] ? 0.15f : -0.15f;
                        if (glow[0] >= 1f) { glow[0]=1f; ((javax.swing.Timer)ev.getSource()).stop(); }
                        if (glow[0] <= 0f) { glow[0]=0f; ((javax.swing.Timer)ev.getSource()).stop(); }
                        lbl.setForeground(mezclar(TXT_PRI, acento, glow[0]));
                        cnt.setFont(new Font("Consolas",Font.BOLD, Math.round(10 + glow[0])));
                        fila.repaint();
                        barra.repaint();
                    });
                    anim[0].start();
                }
            });

            fila.add(top);
            fila.add(Box.createVerticalStrut(4));
            fila.add(barra);
            return fila;
        }
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
                p.getIdProductor(), p.getNombre(),
                p.getEspecialidad()!=null?p.getEspecialidad():"",
                p.getNacionalidad()!=null?p.getNacionalidad():"",
                p.getEstado()!=null?p.getEstado():"Disponible"
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

        // Agrupaciones de conteo
        Map<String,Integer> porEstado        = agrupar(lista, p -> p.getEstado()!=null?p.getEstado():"Disponible");
        Map<String,Integer> porEspecialidad  = agrupar(lista, p -> vacioSi(p.getEspecialidad()));
        Map<String,Integer> porNacionalidad  = agrupar(lista, p -> vacioSi(p.getNacionalidad()));

        int total = lista.size();
        animarContador(lblTotal,          total);
        animarContador(lblEspecialidades, porEspecialidad.size());
        animarContador(lblEstados,        porEstado.size());
        animarContador(lblNacionalidades, porNacionalidad.size());

        distEstado.setDatos(porEstado, total);
        distEspecialidad.setDatos(porEspecialidad, total);
        distNacionalidad.setDatos(porNacionalidad, total);
    }

    private String vacioSi(String s) { return (s==null||s.trim().isEmpty())?"Sin definir":s; }

    /** Agrupa la lista por una clave y cuenta, ordenado de mayor a menor */
    private Map<String,Integer> agrupar(List<Productor> lista,
                                        java.util.function.Function<Productor,String> clave) {
        Map<String,Integer> tmp = new LinkedHashMap<>();
        for (Productor p : lista) {
            String k = clave.apply(p);
            tmp.merge(k, 1, Integer::sum);
        }
        Map<String,Integer> ord = new LinkedHashMap<>();
        tmp.entrySet().stream()
           .sorted((a,b)->Integer.compare(b.getValue(),a.getValue()))
           .forEach(e->ord.put(e.getKey(),e.getValue()));
        return ord;
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
    /** Interpola entre dos colores: t=0 → a, t=1 → b */
    static Color mezclar(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g = (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl= (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        return new Color(r, g, bl);
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