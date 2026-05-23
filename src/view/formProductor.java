package view;

import model.Productor;
import services.ProductorService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * formProductor — Estilo "Solo Leveling / Z-One". Columna derecha con
 * ranking de tarifas (podio) arriba y panel SQL LOG abajo.
 * Conectado a Oracle vía ProductorService.
 */
public class formProductor extends JPanel {

    // ══════════════════════════════════════════════════════════════════
    //  PALETA
    // ══════════════════════════════════════════════════════════════════
    static final Color BG_DEEP = new Color(5, 3, 14);
    static final Color BG_CARD = new Color(11, 9, 27);
    static final Color BG_FIELD = new Color(18, 14, 44);
    static final Color BG_ROW_A = new Color(11, 9, 27);
    static final Color BG_ROW_B = new Color(15, 12, 35);
    static final Color COL_BRD = new Color(35, 26, 80);
    static final Color PURPLE = new Color(124, 58, 237);
    static final Color PURPLE_LT = new Color(167, 139, 250);
    static final Color CYAN = new Color(6, 182, 212);
    static final Color GREEN = new Color(52, 211, 153);
    static final Color AMBER = new Color(251, 191, 36);
    static final Color PINK = new Color(244, 114, 182);
    static final Color TXT_PRI = new Color(237, 233, 254);
    static final Color TXT_SEC = new Color(100, 88, 160);
    static final Color SEL_BG = new Color(124, 58, 237, 60);

    // Colores del podio
    static final Color ORO = new Color(251, 191, 36);
    static final Color PLATA = new Color(203, 213, 225);
    static final Color BRONCE = new Color(217, 119, 66);

    // ── Fuentes ───────────────────────────────────────────────────────
    static final Font F_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    static final Font F_SUB = new Font("Segoe UI", Font.BOLD, 9);
    static final Font F_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    static final Font F_MONO = new Font("Consolas", Font.PLAIN, 11);
    static final Font F_MONO_B = new Font("Consolas", Font.BOLD, 11);

    // ══════════════════════════════════════════════════════════════════
    //  COLUMNAS
    // ══════════════════════════════════════════════════════════════════
    private static final String[] COLS = {"ID", "Nombre", "Especialidad", "Correo", "Teléfono", "Tarifa/h"};
    private static final int COL_ID = 0, COL_NOMBRE = 1, COL_ESPECIALIDAD = 2,
            COL_CORREO = 3, COL_TELEFONO = 4, COL_TARIFA = 5;

    // ══════════════════════════════════════════════════════════════════
    //  COMPONENTES DE ESTADO
    // ══════════════════════════════════════════════════════════════════
    private final ProductorService svc = new ProductorService();
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextField campoBusqueda;
    private JLabel lblTotal, lblEspecialidades, lblTarifaProm, lblTarifaMax;

    // Panel SQL lateral
    private JPanel logContainer;
    private JLabel lblLogCount;
    private int logCount = 0;

    // Panel ranking
    private JPanel rankingContainer;

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

        // ── Columna derecha: ranking arriba + SQL log abajo ──
        JPanel der = new JPanel(new BorderLayout(0, 14));
        der.setOpaque(false);
        der.setBorder(new EmptyBorder(0, 14, 0, 0));
        JPanel rank = panelRanking();
        rank.setPreferredSize(new Dimension(275, 300));
        der.add(rank, BorderLayout.NORTH);
        der.add(panelSqlLog(), BorderLayout.CENTER);
        der.setPreferredSize(new Dimension(275, 0));

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
        JLabel ico = mkLabel("🎚", new Font("Segoe UI Emoji", Font.PLAIN, 20), TXT_PRI);
        JLabel title = mkLabel("Productores", F_TITLE, TXT_PRI);
        JLabel sub = mkLabel("GESTIÓN DE PRODUCTORES · EQUIPO TÉCNICO · ESPECIALIDADES", F_SUB, TXT_SEC);
        for (JLabel l : new JLabel[]{ico, title, sub}) {
            l.setAlignmentX(LEFT_ALIGNMENT);
        }
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
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                buscar();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                buscar();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                buscar();
            }
        });

        ZBtn btnNuevo = new ZBtn("＋ Nuevo productor", true);
        btnNuevo.setPreferredSize(new Dimension(178, 38));
        btnNuevo.addActionListener(e -> dialogFormulario(null));

        acc.add(campoBusqueda);
        acc.add(btnNuevo);

        p.add(titulos, BorderLayout.WEST);
        p.add(acc, BorderLayout.EAST);
        return p;
    }

    // ─── Fila estadísticas ────────────────────────────────────────────
    private JPanel filaStats() {
        lblTotal = new JLabel("0");
        lblEspecialidades = new JLabel("0");
        lblTarifaProm = new JLabel("$0");
        lblTarifaMax = new JLabel("$0");

        JPanel p = new JPanel(new GridLayout(1, 4, 12, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.add(statCard("TOTAL PRODUCTORES", lblTotal, PURPLE, "🎚"));
        p.add(statCard("ESPECIALIDADES", lblEspecialidades, CYAN, "🎛"));
        p.add(statCard("TARIFA PROMEDIO", lblTarifaProm, GREEN, "💵"));
        p.add(statCard("TARIFA MÁXIMA", lblTarifaMax, AMBER, "⭐"));
        return p;
    }

    private JPanel statCard(String titulo, JLabel valor, Color acento, String emoji) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(acento);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(14, 1, getWidth() - 14, 1);
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
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setAlignmentX(LEFT_ALIGNMENT);

        modeloTabla = new DefaultTableModel(COLS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        estilizarTabla();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(0, 0, 0, 0));
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.setBorder(new EmptyBorder(14, 18, 10, 18));
        head.add(mkLabel("Lista de productores", F_BOLD, TXT_PRI), BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(10, 14, 14, 14));
        ZBtn btnEditar = new ZBtn("✏  Editar", false);
        ZBtn btnEliminar = new ZBtn("🗑  Eliminar", false);
        btnEliminar.setForeground(PINK);
        btnEditar.addActionListener(e -> accionEditar());
        btnEliminar.addActionListener(e -> accionEliminar());
        btnRow.add(btnEditar);
        btnRow.add(btnEliminar);

        card.add(head, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private void estilizarTabla() {
        tabla.setOpaque(false);
        tabla.setBackground(new Color(0, 0, 0, 0));
        tabla.setForeground(TXT_PRI);
        tabla.setFont(F_BODY);
        tabla.setRowHeight(42);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(SEL_BG);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFocusable(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader th = tabla.getTableHeader();
        th.setBackground(new Color(7, 5, 15));
        th.setForeground(PURPLE_LT);
        th.setFont(new Font("Segoe UI", Font.BOLD, 9));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COL_BRD));
        th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(0, 34));

        int[] w = {52, 160, 130, 175, 110, 95};
        for (int i = 0; i < w.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }
        tabla.setDefaultRenderer(Object.class, new CeldaRenderer());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                int id = (int) modeloTabla.getValueAt(tabla.getSelectedRow(), COL_ID);
                addLog("SELECT", "SELECT * FROM productores WHERE id = " + id,
                        "1 fila seleccionada", CYAN);
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL RANKING (podio de tarifas) — columna derecha, arriba
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelRanking() {
        JPanel inner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(7, 5, 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setLayout(new BorderLayout());

        // ── cabecera ──
        JPanel cab = new JPanel(new BorderLayout(6, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(10, 8, 24));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 12, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(11, 14, 11, 14));

        JLabel titulo = new JLabel("🏆  TOP TARIFAS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(ORO);

        JLabel sub = new JLabel("más caros");
        sub.setFont(F_MONO.deriveFont(9f));
        sub.setForeground(TXT_SEC);

        cab.add(titulo, BorderLayout.WEST);
        cab.add(sub, BorderLayout.EAST);

        JPanel sepOro = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0, 0, ORO, getWidth() * 0.6f, 0, new Color(0, 0, 0, 0)));
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        sepOro.setOpaque(false);
        sepOro.setPreferredSize(new Dimension(0, 1));

        JPanel topSect = new JPanel(new BorderLayout());
        topSect.setOpaque(false);
        topSect.add(cab, BorderLayout.CENTER);
        topSect.add(sepOro, BorderLayout.SOUTH);

        // ── contenedor del ranking ──
        rankingContainer = new JPanel();
        rankingContainer.setOpaque(false);
        rankingContainer.setLayout(new BoxLayout(rankingContainer, BoxLayout.Y_AXIS));
        rankingContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(rankingContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(0, 0, 0, 0));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        inner.add(topSect, BorderLayout.NORTH);
        inner.add(scroll, BorderLayout.CENTER);
        return inner;
    }

    /** Reconstruye el ranking ordenando los productores por tarifa desc. */
    private void actualizarRanking(List<Productor> lista) {
        rankingContainer.removeAll();

        List<Productor> orden = new ArrayList<>(lista);
        orden.sort(Comparator.comparingDouble(Productor::getTarifaHora).reversed());

        if (orden.isEmpty()) {
            JLabel vacio = mkLabel("Sin productores", F_MONO.deriveFont(10f), TXT_SEC);
            vacio.setAlignmentX(LEFT_ALIGNMENT);
            rankingContainer.add(vacio);
        } else {
            double maxTarifa = orden.get(0).getTarifaHora();
            if (maxTarifa <= 0) {
                maxTarifa = 1;
            }
            for (int i = 0; i < orden.size(); i++) {
                Productor p = orden.get(i);
                boolean esPodio = i < 3;
                rankingContainer.add(filaRanking(i + 1, p, maxTarifa, esPodio));
                rankingContainer.add(Box.createVerticalStrut(esPodio ? 8 : 5));
            }
        }
        rankingContainer.revalidate();
        rankingContainer.repaint();
    }

    /** Una fila del ranking. Las 3 primeras se dibujan destacadas (podio). */
    private JPanel filaRanking(int puesto, Productor p, double maxTarifa, boolean esPodio) {
        final Color acento = switch (puesto) {
            case 1 -> ORO;
            case 2 -> PLATA;
            case 3 -> BRONCE;
            default -> PURPLE_LT;
        };
        final String medalla = switch (puesto) {
            case 1 -> "🥇";
            case 2 -> "🥈";
            case 3 -> "🥉";
            default -> "#" + puesto;
        };
        final double ratio = Math.min(p.getTarifaHora() / maxTarifa, 1.0);

        JPanel fila = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                // fondo
                g2.setColor(esPodio ? new Color(20, 16, 44) : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                // borde para el podio
                if (esPodio) {
                    g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 110));
                    g2.setStroke(new BasicStroke(1.4f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 9, 9);
                }
                // barra de tarifa al fondo
                int barW = (int) ((getWidth() - 8) * ratio);
                int barY = getHeight() - 9;
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 30));
                g2.fillRoundRect(4, barY, getWidth() - 8, 4, 3, 3);
                if (barW > 0) {
                    g2.setColor(acento);
                    g2.fillRoundRect(4, barY, barW, 4, 3, 3);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(8, 0));
        fila.setBorder(new EmptyBorder(esPodio ? 9 : 6, 10, esPodio ? 13 : 10, 10));
        fila.setAlignmentX(LEFT_ALIGNMENT);
        int alto = esPodio ? 52 : 40;
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, alto));

        // medalla / puesto
        JLabel lblMed = new JLabel(medalla, SwingConstants.CENTER);
        lblMed.setFont(esPodio
                ? new Font("Segoe UI Emoji", Font.PLAIN, 19)
                : new Font("Consolas", Font.BOLD, 12));
        lblMed.setForeground(acento);
        lblMed.setPreferredSize(new Dimension(28, 0));

        // nombre + especialidad
        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel lblNom = mkLabel(recortar(p.getNombre(), esPodio ? 16 : 18),
                new Font("Segoe UI", Font.BOLD, esPodio ? 12 : 11), TXT_PRI);
        lblNom.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(lblNom);
        if (esPodio) {
            JLabel lblEsp = mkLabel(recortar(p.getEspecialidad(), 18),
                    F_MONO.deriveFont(8.5f), TXT_SEC);
            lblEsp.setAlignmentX(LEFT_ALIGNMENT);
            txt.add(Box.createVerticalStrut(1));
            txt.add(lblEsp);
        }

        // monto
        JLabel lblMonto = mkLabel(String.format("$%.0f", p.getTarifaHora()),
                new Font("Consolas", Font.BOLD, esPodio ? 14 : 11), acento);

        fila.add(lblMed, BorderLayout.WEST);
        fila.add(txt, BorderLayout.CENTER);
        fila.add(lblMonto, BorderLayout.EAST);
        return fila;
    }

    private String recortar(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL SQL LOG (columna derecha, abajo)
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelSqlLog() {
        JPanel inner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(7, 5, 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setLayout(new BorderLayout());

        // ── cabecera ──
        JPanel cab = new JPanel(new BorderLayout(6, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(10, 8, 24));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 12, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(11, 14, 11, 14));

        JLabel titulo = new JLabel("⬡  SQL LOG");
        titulo.setFont(F_MONO_B.deriveFont(13f));
        titulo.setForeground(GREEN);

        JLabel live = new JLabel("● LIVE") {
            float a = 1f;
            boolean d = false;

            {
                Timer t = new Timer(700, ev -> {
                    a = d ? a + 0.08f : a - 0.08f;
                    if (a <= 0.3f) {
                        a = 0.3f;
                        d = true;
                    }
                    if (a >= 1f) {
                        a = 1f;
                        d = false;
                    }
                    setForeground(new Color(52, 211, 153, (int) (a * 255)));
                });
                t.start();
            }
        };
        live.setFont(F_MONO_B.deriveFont(9f));
        live.setForeground(GREEN);

        lblLogCount = new JLabel("0 entradas");
        lblLogCount.setFont(F_MONO.deriveFont(9f));
        lblLogCount.setForeground(TXT_SEC);

        ZBtn btnLimpiar = new ZBtn("Limpiar", false);
        btnLimpiar.setFont(F_BODY.deriveFont(10f));
        btnLimpiar.setPreferredSize(new Dimension(64, 24));
        btnLimpiar.addActionListener(e -> limpiarLog());

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightBar.setOpaque(false);
        rightBar.add(lblLogCount);
        rightBar.add(live);
        rightBar.add(btnLimpiar);

        cab.add(titulo, BorderLayout.WEST);
        cab.add(rightBar, BorderLayout.EAST);

        JPanel sepVerde = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0, 0, GREEN, getWidth() * 0.6f, 0, new Color(0, 0, 0, 0)));
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        sepVerde.setOpaque(false);
        sepVerde.setPreferredSize(new Dimension(0, 1));

        JPanel topSect = new JPanel(new BorderLayout());
        topSect.setOpaque(false);
        topSect.add(cab, BorderLayout.CENTER);
        topSect.add(sepVerde, BorderLayout.SOUTH);

        logContainer = new JPanel();
        logContainer.setOpaque(false);
        logContainer.setLayout(new BoxLayout(logContainer, BoxLayout.Y_AXIS));
        logContainer.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(logContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(0, 0, 0, 0));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        JPanel leyenda = new JPanel(new GridLayout(3, 2, 4, 3)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(10, 8, 24));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 12, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        leyenda.setOpaque(false);
        leyenda.setBorder(new EmptyBorder(8, 12, 10, 12));
        for (Object[] it : new Object[][]{
            {"● INSERT", GREEN}, {"● SELECT", CYAN},
            {"● UPDATE", AMBER}, {"● DELETE", PINK},
            {"● ERROR", new Color(248, 113, 113)}, {"", TXT_SEC}
        }) {
            JLabel l = new JLabel((String) it[0]);
            l.setFont(F_MONO_B.deriveFont(9f));
            l.setForeground((Color) it[1]);
            leyenda.add(l);
        }

        JPanel sepGray = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(COL_BRD);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        sepGray.setOpaque(false);
        sepGray.setPreferredSize(new Dimension(0, 1));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(sepGray, BorderLayout.NORTH);
        bottom.add(leyenda, BorderLayout.CENTER);

        inner.add(topSect, BorderLayout.NORTH);
        inner.add(scroll, BorderLayout.CENTER);
        inner.add(bottom, BorderLayout.SOUTH);
        return inner;
    }

    /** Añade una entrada al log. Hilo-seguro. */
    private void addLog(String tipo, String sql, String resultado, Color acento) {
        SwingUtilities.invokeLater(() -> {
            logCount++;
            lblLogCount.setText(logCount + " entradas");

            JPanel entry = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = g2d(g);
                    g2.setColor(BG_CARD);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(acento);
                    g2.fillRoundRect(0, 0, 3, getHeight(), 3, 3);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            entry.setOpaque(false);
            entry.setLayout(new BoxLayout(entry, BoxLayout.Y_AXIS));
            entry.setBorder(new EmptyBorder(7, 12, 7, 8));
            entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 9999));
            entry.setAlignmentX(LEFT_ALIGNMENT);

            String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            JLabel meta = mkLabel(hora + "  ·  " + tipo, F_MONO_B.deriveFont(9f),
                    new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 220));
            meta.setAlignmentX(LEFT_ALIGNMENT);

            JTextArea sqlArea = new JTextArea(sql);
            sqlArea.setFont(F_MONO);
            sqlArea.setForeground(PURPLE_LT);
            sqlArea.setOpaque(false);
            sqlArea.setEditable(false);
            sqlArea.setLineWrap(true);
            sqlArea.setWrapStyleWord(true);
            sqlArea.setBorder(new EmptyBorder(3, 0, 3, 0));
            sqlArea.setAlignmentX(LEFT_ALIGNMENT);
            sqlArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 9999));

            JLabel res = mkLabel("✓ " + resultado, F_MONO.deriveFont(9f),
                    new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 170));
            res.setAlignmentX(LEFT_ALIGNMENT);

            entry.add(meta);
            entry.add(sqlArea);
            entry.add(res);

            JPanel gap = new JPanel();
            gap.setOpaque(false);
            gap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
            gap.setAlignmentX(LEFT_ALIGNMENT);

            logContainer.add(entry, 0);
            logContainer.add(gap, 1);
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
    //  RENDERER DE CELDAS
    // ══════════════════════════════════════════════════════════════════
    private class CeldaRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            c.setBorder(new EmptyBorder(0, 16, 0, 16));
            c.setOpaque(true);
            c.setIcon(null);
            c.setBackground(sel ? SEL_BG : (row % 2 == 0 ? BG_ROW_A : BG_ROW_B));
            c.setForeground(TXT_PRI);
            c.setFont(F_BODY);
            if (col == COL_ID) {
                c.setForeground(PURPLE_LT);
                c.setFont(new Font("Consolas", Font.BOLD, 11));
            }
            if (col == COL_ESPECIALIDAD && val != null) {
                c.setForeground(CYAN);
                c.setFont(F_BOLD);
                c.setText("● " + val);
            }
            if (col == COL_TARIFA && val != null) {
                c.setForeground(GREEN);
                c.setFont(F_BOLD);
            }
            return c;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  CARGA Y ACCIONES
    // ══════════════════════════════════════════════════════════════════
    private void cargarProductores() {
        worker(() -> svc.obtenerTodos(), lista -> {
            poblar(lista);
            addLog("SELECT", "SELECT * FROM productores ORDER BY id",
                    lista.size() + " fila(s) cargadas", CYAN);
        }, "Error al cargar");
    }

    private void buscar() {
        String q = campoBusqueda.getText().trim();
        worker(() -> svc.buscar(q), lista -> {
            poblar(lista);
            if (!q.isEmpty()) {
                addLog("SELECT", "SELECT * FROM productores WHERE nombre LIKE '%" + q + "%'",
                        lista.size() + " resultado(s)", CYAN);
            }
        }, "Error al buscar");
    }

    private void poblar(List<Productor> lista) {
        modeloTabla.setRowCount(0);
        for (Productor p : lista) {
            modeloTabla.addRow(new Object[]{
                p.getIdentificacion(), p.getNombre(), p.getEspecialidad(),
                p.getCorreo(), p.getTelefono(),
                String.format("$%.0f", p.getTarifaHora())
            });
        }
        long esp = lista.stream().map(Productor::getEspecialidad).distinct().count();
        double prom = lista.stream().mapToDouble(Productor::getTarifaHora).average().orElse(0);
        double max = lista.stream().mapToDouble(Productor::getTarifaHora).max().orElse(0);
        lblTotal.setText(String.valueOf(lista.size()));
        lblEspecialidades.setText(String.valueOf(esp));
        lblTarifaProm.setText(String.format("$%.0f", prom));
        lblTarifaMax.setText(String.format("$%.0f", max));

        // refrescar el podio de tarifas
        actualizarRanking(lista);
    }

    private void accionEditar() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            toast("Selecciona un productor primero", MainFrame.ToastType.INFO);
            return;
        }
        dialogFormulario(row);
    }

    private void accionEliminar() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            toast("Selecciona un productor primero", MainFrame.ToastType.INFO);
            return;
        }
        String nombre = modeloTabla.getValueAt(row, COL_NOMBRE).toString();
        int id = (int) modeloTabla.getValueAt(row, COL_ID);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar a \"" + nombre + "\"?",
                "Z-One — Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            worker(() -> {
                svc.darDeBaja(id);
                return svc.obtenerTodos();
            }, lista -> {
                poblar(lista);
                addLog("DELETE", "DELETE FROM productores WHERE id = " + id,
                        "Commit OK · \"" + nombre + "\" eliminado", PINK);
                toast("Productor eliminado", MainFrame.ToastType.SUCCESS);
            }, "Error al eliminar");
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  DIÁLOGO CREAR / EDITAR
    // ══════════════════════════════════════════════════════════════════
    private void dialogFormulario(Integer filaEditar) {
        boolean esEdit = filaEditar != null;
        int id = esEdit ? (int) modeloTabla.getValueAt(filaEditar, COL_ID) : 0;
        String nom = esEdit ? (String) modeloTabla.getValueAt(filaEditar, COL_NOMBRE) : "";
        String esp = esEdit ? (String) modeloTabla.getValueAt(filaEditar, COL_ESPECIALIDAD) : "";
        String cor = esEdit ? (String) modeloTabla.getValueAt(filaEditar, COL_CORREO) : "";
        String tel = esEdit ? (String) modeloTabla.getValueAt(filaEditar, COL_TELEFONO) : "";
        String tar = esEdit
                ? modeloTabla.getValueAt(filaEditar, COL_TARIFA).toString().replace("$", "")
                : "0";

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                esEdit ? "Editar productor" : "Nuevo productor", true);
        dlg.setResizable(false);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
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
        JTextField fCor = dlgField(cor);
        JTextField fTel = dlgField(tel);
        JTextField fTar = dlgField(tar);

        main.add(dlgFilaDoble("NOMBRE COMPLETO *", fNom, "ESPECIALIDAD *", fEsp));
        main.add(Box.createVerticalStrut(15));
        main.add(dlgFilaDoble("CORREO ELECTRÓNICO", fCor, "TELÉFONO", fTel));
        main.add(Box.createVerticalStrut(15));
        main.add(dlgFilaCampo("TARIFA POR HORA ($)", fTar));
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
        btnSave.addActionListener(e -> guardar(esEdit, id, fNom, fEsp, fCor, fTel, fTar, dlg));
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

    // ─── Banda de cabecera con degradado ──────────────────────────────
    private JPanel bandaCabecera(boolean esEdit) {
        JPanel band = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0, 0, new Color(124, 58, 237),
                        getWidth(), getHeight(), new Color(67, 24, 140)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 45),
                        0, getHeight(), new Color(255, 255, 255, 0)));
                g2.fillRect(0, 0, getWidth(), getHeight() / 2);
                g2.setColor(CYAN);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        band.setOpaque(false);
        band.setBorder(new EmptyBorder(20, 26, 20, 26));
        band.setPreferredSize(new Dimension(0, 90));

        JLabel ico = new JLabel(esEdit ? "✏" : "🎚", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
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
        JLabel t = mkLabel(esEdit ? "Editar productor" : "Nuevo productor",
                new Font("Segoe UI", Font.BOLD, 21), Color.WHITE);
        JLabel s = mkLabel(esEdit ? "ACTUALIZA LA INFORMACIÓN DEL PRODUCTOR"
                                  : "REGISTRA UN NUEVO PRODUCTOR EN Z-ONE",
                F_SUB, new Color(255, 255, 255, 185));
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
            JTextField fNom, JTextField fEsp, JTextField fCor, JTextField fTel,
            JTextField fTar, JDialog dlg) {
        String nom = fNom.getText().trim(), esp = fEsp.getText().trim(),
                cor = fCor.getText().trim(), tel = fTel.getText().trim();
        double tarifa;
        try {
            tarifa = fTar.getText().trim().isEmpty() ? 0 : Double.parseDouble(fTar.getText().trim());
        } catch (NumberFormatException ex) {
            toast("La tarifa debe ser un número", MainFrame.ToastType.ERROR);
            return;
        }
        worker(() -> {
            if (esEdit) {
                svc.modificar(id, nom, cor, tel, esp, tarifa);
            } else {
                svc.registrar(nom, cor, tel, esp, tarifa);
            }
            return svc.obtenerTodos();
        }, lista -> {
            poblar(lista);
            if (esEdit) {
                addLog("UPDATE",
                        "UPDATE productores SET nombre='" + nom + "', tarifa_hora=" + tarifa + " WHERE id=" + id,
                        "Commit OK · 1 fila actualizada", AMBER);
            } else {
                addLog("INSERT",
                        "INSERT INTO productores (nombre,correo,telefono,especialidad,tarifa_hora) VALUES ('"
                        + nom + "','" + cor + "','" + tel + "','" + esp + "'," + tarifa + ")",
                        "Commit OK · 1 fila insertada", GREEN);
            }
            toast(esEdit ? "Productor actualizado" : "Productor creado: " + nom, MainFrame.ToastType.SUCCESS);
            dlg.dispose();
        }, "Error al guardar");
    }

    // ── Helpers dialog ────────────────────────────────────────────────
    private JTextField dlgField(String val) {
        JTextField f = new JTextField(val) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                boolean foco = isFocusOwner();
                if (foco) {
                    g2.setColor(new Color(124, 58, 237, 60));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.setColor(foco ? new Color(30, 23, 64) : BG_FIELD);
                g2.fillRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 10, 10);
                g2.setColor(foco ? PURPLE : COL_BRD);
                g2.setStroke(new BasicStroke(foco ? 1.8f : 1f));
                g2.drawRoundRect(2, 2, getWidth() - 6, getHeight() - 6, 10, 10);
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
            public void focusLost(java.awt.event.FocusEvent e)   { f.repaint(); }
        });
        return f;
    }

    private JPanel dlgFilaCampo(String label, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 7));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        JLabel l = mkLabel(label, new Font("Segoe UI", Font.BOLD, 10), PURPLE_LT);
        p.add(l, BorderLayout.NORTH);
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
    private static JLabel mkLabel(String txt, Font f, Color c) {
        JLabel l = new JLabel(txt);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    private JTextField mkTextField(String placeholder) {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COL_BRD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
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

    private static Graphics2D g2d(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return g2;
    }

    private void worker(java.util.concurrent.Callable<List<Productor>> tarea,
            java.util.function.Consumer<List<Productor>> fin, String err) {
        new SwingWorker<List<Productor>, Void>() {
            @Override
            protected List<Productor> doInBackground() throws Exception {
                return tarea.call();
            }

            @Override
            protected void done() {
                try {
                    fin.accept(get());
                } catch (Exception ex) {
                    toast(err + ": " + ex.getMessage(), MainFrame.ToastType.ERROR);
                }
            }
        }.execute();
    }

    private void toast(String msg, MainFrame.ToastType tipo) {
        MainFrame.showToast(msg, tipo);
    }

    // ══════════════════════════════════════════════════════════════════
    //  ZBtn — botón estilo Z-One
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

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = g2d(g);
            if (primary) {
                g2.setColor(getModel().isPressed() ? new Color(109, 40, 217) : PURPLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (!getModel().isPressed()) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 28), 0, getHeight() / 2f, new Color(0, 0, 0, 0)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 10, 10);
                }
            } else {
                g2.setColor(getModel().isRollover() ? new Color(28, 20, 66) : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}