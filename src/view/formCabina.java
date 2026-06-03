package view;

import css.CabinaStyles;
import css.CabinaStyles.*;
import model.Cabina;
import services.CabinaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * formCabina.java — Módulo Cabinas (solo lógica y layout)
 * ────────────────────────────────────────────────────────
 * Todo lo visual está en css/CabinaStyles.java (tema Zircon Blue).
 */
public class formCabina extends JPanel {

    // ── Datos ─────────────────────────────────────────────────
    private final CabinaService servicio = new CabinaService();
    private final List<Cabina>  cabinas  = new ArrayList<>();
    private Cabina seleccionada;

    // ── Labels de stats ───────────────────────────────────────
    private final JLabel stTotal   = new JLabel("0");
    private final JLabel stDispo   = new JLabel("0");
    private final JLabel stOcupada = new JLabel("0");
    private final JLabel stMant    = new JLabel("0");

    // ── Áreas actualizables ───────────────────────────────────
    private JPanel gridCards;
    private JPanel rankingBox;
    private JLabel resTotal, resTopEstado, resUltima;

    // ═════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ═════════════════════════════════════════════════════════
    public formCabina() {
        setOpaque(true);
        setBackground(CabinaStyles.BG_MAIN);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 26, 24, 26));

        // Norte: header + stats
        JPanel norte = new JPanel();
        norte.setOpaque(false);
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(buildHeader());
        norte.add(Box.createVerticalStrut(18));
        norte.add(buildStats());
        norte.add(Box.createVerticalStrut(18));
        add(norte, BorderLayout.NORTH);

        // Centro: grid + lateral
        JPanel centro = new JPanel(new BorderLayout(16, 0));
        centro.setOpaque(false);
        centro.add(buildGridPanel(),    BorderLayout.CENTER);
        centro.add(buildLateralPanel(), BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);

        recargar();
    }

    // ═════════════════════════════════════════════════════════
    //  HEADER
    // ═════════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);

        // Izquierda
        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = CabinaStyles.lbl("Cabinas de Estudio", 22, true, CabinaStyles.TEXT_PRI);
        JLabel sub    = CabinaStyles.lbl("Gestión de disponibilidad y estado", 12, false, CabinaStyles.TEXT_SEC);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        HeaderDecorativo deco = new HeaderDecorativo();
        deco.setAlignmentX(LEFT_ALIGNMENT);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(3));
        izq.add(sub);
        izq.add(Box.createVerticalStrut(8));
        izq.add(deco);

        // Derecha: botones
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        BtnCabina bEditar = CabinaStyles.btnAccion("✎  Editar",    false, 110, CabinaStyles.SKY);
        BtnCabina bElim   = CabinaStyles.btnAccion("✖  Eliminar",  false, 110, CabinaStyles.RED);
        BtnCabina bRefr   = CabinaStyles.btnAccion("↺  Refrescar", false, 120, CabinaStyles.COBALT);
        BtnCabina bNueva  = CabinaStyles.btnAccion("＋  Nueva",     true,  140);

        bEditar.addActionListener(e -> {
            if (seleccionada != null) openForm(seleccionada);
            else toast("Selecciona una cabina", MainFrame.ToastType.INFO);
        });
        bElim.addActionListener(e  -> eliminar());
        bRefr.addActionListener(e  -> { recargar(); toast("Lista actualizada", MainFrame.ToastType.INFO); });
        bNueva.addActionListener(e -> openForm(null));

        der.add(bEditar); der.add(bElim); der.add(bRefr); der.add(bNueva);

        p.add(izq, BorderLayout.WEST);
        p.add(der, BorderLayout.EAST);
        return p;
    }

    // ═════════════════════════════════════════════════════════
    //  STATS
    // ═════════════════════════════════════════════════════════
    private JPanel buildStats() {
        JPanel p = new JPanel(new GridLayout(1, 4, 12, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        p.add(new StatCardAnimada("Total cabinas",  stTotal,   CabinaStyles.INDIGO, "registradas"));
        p.add(new StatCardAnimada("Disponibles",    stDispo,   CabinaStyles.GREEN,  "listas"));
        p.add(new StatCardAnimada("Ocupadas",       stOcupada, CabinaStyles.AMBER,  "en uso"));
        p.add(new StatCardAnimada("Mantenimiento",  stMant,    CabinaStyles.RED,    "no disp."));
        return p;
    }

    // ═════════════════════════════════════════════════════════
    //  GRID DE TARJETAS
    // ═════════════════════════════════════════════════════════
    private JComponent buildGridPanel() {
        GlassPanel card = new GlassPanel(16, true, CabinaStyles.INDIGO);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel tit = CabinaStyles.lbl("Lista de cabinas", 13, true, CabinaStyles.TEXT_PRI);
        card.add(tit, BorderLayout.NORTH);

        gridCards = new JPanel(new GridLayout(0, 3, 14, 14));
        gridCards.setOpaque(false);

        JPanel cont = new JPanel(new BorderLayout());
        cont.setOpaque(false);
        cont.add(gridCards, BorderLayout.NORTH);

        JScrollPane sc = new JScrollPane(cont);
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.setOpaque(false);
        sc.getViewport().setOpaque(false);
        sc.getVerticalScrollBar().setUnitIncrement(16);
        card.add(sc, BorderLayout.CENTER);
        return card;
    }

    // ═════════════════════════════════════════════════════════
    //  TARJETA INDIVIDUAL DE CABINA
    // ═════════════════════════════════════════════════════════
    private JComponent buildCabinaCard(Cabina c) {
        boolean activa = (c == seleccionada);
        Color   acento = CabinaStyles.colorEstado(c.getNombreEstado());

        CabinaCard card = new CabinaCard(activa, acento);

        // Top: icono + nombre + estado
        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);

        JLabel ico = CabinaStyles.lbl("🎙", 22, false, CabinaStyles.TEXT_PRI);

        JPanel nomBox = new JPanel();
        nomBox.setOpaque(false);
        nomBox.setLayout(new BoxLayout(nomBox, BoxLayout.Y_AXIS));
        JLabel nom = CabinaStyles.lbl(c.getNombreCabina(), 14, true, CabinaStyles.TEXT_PRI);
        JLabel id  = CabinaStyles.lbl("Cabina #" + String.format("%03d", c.getIdCabina()),
                                       10, false, CabinaStyles.TEXT_MUT);
        nom.setAlignmentX(LEFT_ALIGNMENT);
        id.setAlignmentX(LEFT_ALIGNMENT);
        nomBox.add(nom);
        nomBox.add(Box.createVerticalStrut(2));
        nomBox.add(id);

        JPanel ladoIzq = new JPanel(new BorderLayout(10, 0));
        ladoIzq.setOpaque(false);
        ladoIzq.add(ico,    BorderLayout.WEST);
        ladoIzq.add(nomBox, BorderLayout.CENTER);

        PildoraEstado pill = new PildoraEstado(c.getNombreEstado());
        top.add(ladoIzq, BorderLayout.CENTER);
        top.add(pill,    BorderLayout.EAST);

        // Cuerpo: barra + descripción
        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.add(Box.createVerticalStrut(10));

        BarraProgreso barra = new BarraProgreso(c.getNombreEstado());
        barra.setAlignmentX(LEFT_ALIGNMENT);
        cuerpo.add(barra);
        cuerpo.add(Box.createVerticalStrut(8));

        JLabel desc = CabinaStyles.lbl(CabinaStyles.descEstado(c.getNombreEstado()),
                                        11, false, CabinaStyles.TEXT_MUT);
        desc.setAlignmentX(LEFT_ALIGNMENT);
        cuerpo.add(desc);

        card.add(top,    BorderLayout.NORTH);
        card.add(cuerpo, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e)  { card.startHover(); }
            @Override public void mouseExited(MouseEvent e)   { card.stopHover(); }
            @Override public void mouseClicked(MouseEvent e) {
                seleccionar(c);
                if (e.getClickCount() == 2) openForm(c);
            }
        });
        return card;
    }

    // ═════════════════════════════════════════════════════════
    //  PANEL LATERAL
    // ═════════════════════════════════════════════════════════
    private JComponent buildLateralPanel() {
        GlassPanel card = new GlassPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(280, 10));
        card.setBorder(new EmptyBorder(20, 18, 20, 18));

        // Ranking
        JLabel t1 = CabinaStyles.lbl("RANKING DE ESTADO", 10, true, CabinaStyles.INDIGO);
        t1.setAlignmentX(LEFT_ALIGNMENT);
        card.add(t1);
        card.add(Box.createVerticalStrut(10));

        rankingBox = new JPanel();
        rankingBox.setOpaque(false);
        rankingBox.setLayout(new BoxLayout(rankingBox, BoxLayout.Y_AXIS));
        rankingBox.setAlignmentX(LEFT_ALIGNMENT);
        card.add(rankingBox);

        // Separador
        card.add(Box.createVerticalStrut(14));
        JSeparator sep = new JSeparator();
        sep.setForeground(CabinaStyles.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);
        card.add(Box.createVerticalStrut(14));

        // Resumen
        JLabel t2 = CabinaStyles.lbl("RESUMEN GENERAL", 10, true, CabinaStyles.INDIGO);
        t2.setAlignmentX(LEFT_ALIGNMENT);
        card.add(t2);
        card.add(Box.createVerticalStrut(10));

        resTotal     = new JLabel("—");
        resTopEstado = new JLabel("—");
        resUltima    = new JLabel("—");

        card.add(buildResumenFila("Total cabinas",     resTotal,     CabinaStyles.INDIGO));
        card.add(Box.createVerticalStrut(7));
        card.add(buildResumenFila("Estado más común",  resTopEstado, CabinaStyles.GREEN));
        card.add(Box.createVerticalStrut(7));
        card.add(buildResumenFila("Última registrada", resUltima,    CabinaStyles.VIOLET));
        return card;
    }

    // ═════════════════════════════════════════════════════════
    //  FILA RESUMEN
    // ═════════════════════════════════════════════════════════
    private JComponent buildResumenFila(String etiqueta, JLabel valor, Color color) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CabinaStyles.BG_CARD_ALT);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(CabinaStyles.BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 12, 10, 12));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel et = CabinaStyles.lbl(etiqueta, 10, false, CabinaStyles.TEXT_SEC);
        valor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valor.setForeground(color);
        valor.setHorizontalAlignment(SwingConstants.RIGHT);

        p.add(et,    BorderLayout.WEST);
        p.add(valor, BorderLayout.EAST);
        return p;
    }

    // ═════════════════════════════════════════════════════════
    //  LÓGICA
    // ═════════════════════════════════════════════════════════
    private void recargar() {
        try {
            cabinas.clear();
            List<Cabina> all = servicio.listar();
            if (all != null) cabinas.addAll(all);
            actualizarVista();
        } catch (Exception ex) {
            toast("Error al cargar: " + ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    private void seleccionar(Cabina c) {
        seleccionada = c;
        actualizarVista();
    }

    private void actualizarVista() {
        // Grid
        gridCards.removeAll();
        for (Cabina c : cabinas) gridCards.add(buildCabinaCard(c));
        gridCards.revalidate();
        gridCards.repaint();

        // Stats
        long dispo = cabinas.stream().filter(c -> "Disponible".equals(c.getNombreEstado())).count();
        long ocup  = cabinas.stream().filter(c -> "Ocupada".equals(c.getNombreEstado())).count();
        long mant  = cabinas.stream().filter(c -> "Mantenimiento".equals(c.getNombreEstado())).count();
        stTotal.setText(String.valueOf(cabinas.size()));
        stDispo.setText(String.valueOf(dispo));
        stOcupada.setText(String.valueOf(ocup));
        stMant.setText(String.valueOf(mant));

        // Ranking con animación escalonada
        rankingBox.removeAll();
        Map<String, Long> agrup = new TreeMap<>();
        for (Cabina c : cabinas) {
            String est = c.getNombreEstado() != null ? c.getNombreEstado() : "Sin estado";
            agrup.merge(est, 1L, Long::sum);
        }
        int pos = 1, delay = 0;
        for (var entry : agrup.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()).toList()) {
            if (pos > 4) break;
            rankingBox.add(new RankingRow(pos, entry.getKey(), entry.getValue(), delay));
            rankingBox.add(Box.createVerticalStrut(6));
            pos++; delay += 80;
        }
        rankingBox.revalidate();
        rankingBox.repaint();

        // Resumen
        resTotal.setText(String.valueOf(cabinas.size()));
        String topEstado = agrup.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("—");
        resTopEstado.setText(topEstado);
        String ultima = cabinas.stream()
                .max(Comparator.comparing(Cabina::getIdCabina))
                .map(Cabina::getNombreCabina).orElse("—");
        resUltima.setText(ultima);
    }

    private void eliminar() {
        if (seleccionada == null) { toast("Selecciona una cabina", MainFrame.ToastType.INFO); return; }
        int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar \"" + seleccionada.getNombreCabina() + "\"?",
                "Z-One Studio", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            try {
                if (servicio.eliminar(seleccionada.getIdCabina())) {
                    toast("Cabina eliminada", MainFrame.ToastType.SUCCESS);
                    seleccionada = null;
                    recargar();
                }
            } catch (Exception ex) { toast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR); }
        }
    }

    // ═════════════════════════════════════════════════════════


private void openForm(Cabina c) {
    boolean isEdit = (c != null);
    final List<javax.swing.Timer> timers = new ArrayList<>();

    JDialog dlg = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Editar cabina" : "Nueva cabina", true);
    dlg.setResizable(false);

    // ── ROOT ──────────────────────────────────────────────────────────
    JPanel root = new JPanel(new BorderLayout()) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(CabinaStyles.BG_MAIN);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    };
    root.setOpaque(false);

    // ── BANDA SUPERIOR ANIMADA ────────────────────────────────────────
    // Arrays para la animación (accesibles desde el Timer externo)
    final float[] wx  = new float[24];
    final float[] wy  = new float[24];
    final float[] wsp = new float[24];
    final float[] wH  = new float[20];
    final float[] wHt = new float[20];
    final boolean[] bandaInited = {false};

    JPanel banda = new JPanel(new BorderLayout(14, 0)) {
        @Override protected void paintComponent(Graphics g) {
            if (!bandaInited[0]) {
                bandaInited[0] = true;
                java.util.Random r = new java.util.Random();
                for (int i = 0; i < wx.length; i++) {
                    wx[i]  = r.nextFloat();
                    wy[i]  = r.nextFloat();
                    wsp[i] = 0.0005f + r.nextFloat() * 0.001f;
                }
                for (int i = 0; i < wH.length; i++) {
                    wH[i]  = 0.15f + r.nextFloat() * 0.7f;
                    wHt[i] = 0.1f  + r.nextFloat() * 0.85f;
                }
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo blanco → índigo suave
            g2.setPaint(new java.awt.GradientPaint(
                    0, 0, Color.WHITE,
                    getWidth(), getHeight(), new Color(0xEBF0FF)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Franja translúcida arriba
            g2.setColor(new Color(59, 110, 248, 14));
            g2.fillRect(0, 0, getWidth(), getHeight() / 2);

            // Waveform animada de fondo
            int barW = 3, gap = 4;
            int sx   = getWidth() / 2 - (wH.length * (barW + gap)) / 2;
            int midY = getHeight() / 2;
            for (int i = 0; i < wH.length; i++) {
                int h = (int)(wH[i] * (getHeight() * 0.52f));
                g2.setColor(new Color(CabinaStyles.INDIGO.getRed(),
                        CabinaStyles.INDIGO.getGreen(),
                        CabinaStyles.INDIGO.getBlue(),
                        (int)(20 + wH[i] * 30)));
                g2.fillRoundRect(sx + i * (barW + gap), midY - h / 2, barW, h, barW, barW);
            }

            // Partículas musicales flotantes
            String[] syms = {"♪", "♫", "♬", "♩"};
            g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 11));
            for (int i = 0; i < wx.length; i++) {
                int alpha = (int)(15 + Math.sin(wy[i] * Math.PI) * 28);
                g2.setColor(new Color(CabinaStyles.SKY.getRed(),
                        CabinaStyles.SKY.getGreen(),
                        CabinaStyles.SKY.getBlue(),
                        Math.max(0, Math.min(50, alpha))));
                g2.drawString(syms[i % syms.length],
                        (int)(wx[i] * getWidth()),
                        (int)(wy[i] * getHeight()));
            }

            // Línea índigo abajo
            g2.setColor(CabinaStyles.INDIGO);
            g2.fillRect(0, getHeight() - 2, getWidth(), 2);
            g2.dispose();
            super.paintComponent(g);
        }
    };
    banda.setOpaque(false);
    banda.setPreferredSize(new Dimension(0, 88));
    banda.setBorder(new EmptyBorder(18, 22, 18, 22));

    // Timer de animación: mueve partículas y waveform, llama repaint
    javax.swing.Timer tAnim = new javax.swing.Timer(38, ev -> {
        java.util.Random r = new java.util.Random();
        for (int i = 0; i < wx.length; i++) {
            wy[i] -= wsp[i];
            wx[i] += wsp[i] * 0.2f;
            if (wy[i] < -0.1f) { wy[i] = 1.1f; wx[i] = r.nextFloat(); }
            if (wx[i] >  1.1f)   wx[i] = -0.1f;
        }
        for (int i = 0; i < wH.length; i++) {
            wH[i] += (wHt[i] - wH[i]) * 0.1f;
            if (Math.abs(wH[i] - wHt[i]) < 0.01f)
                wHt[i] = 0.1f + r.nextFloat() * 0.85f;
        }
        banda.repaint();
    });
    timers.add(tAnim);
    tAnim.start();

    // ── Ícono en caja redondeada ──────────────────────────────────────
    JPanel iconBox = new JPanel(new GridBagLayout()) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CabinaStyles.INDIGO_PALE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.setColor(new Color(CabinaStyles.INDIGO.getRed(),
                    CabinaStyles.INDIGO.getGreen(),
                    CabinaStyles.INDIGO.getBlue(), 130));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
        }
    };
    iconBox.setOpaque(false);
    iconBox.setPreferredSize(new Dimension(52, 52));
    JLabel icoLbl = new JLabel("🎙");
    icoLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
    iconBox.add(icoLbl);

    // ── Columna de textos + badge REC ─────────────────────────────────
    JPanel titleCol = new JPanel();
    titleCol.setOpaque(false);
    titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));

    JPanel tituloRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    tituloRow.setOpaque(false);
    tituloRow.setAlignmentX(LEFT_ALIGNMENT);

    JLabel titLbl = CabinaStyles.lbl(
            isEdit ? "Editar cabina" : "Nueva cabina",
            16, true, CabinaStyles.TEXT_PRI);

    // Badge REC parpadeante
    JLabel recLbl = new JLabel("● REC");
    recLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
    recLbl.setForeground(new Color(0xDC2626));

    javax.swing.Timer tRec = new javax.swing.Timer(580, null);
    final boolean[] recVis = {true};
    tRec.addActionListener(e -> {
        recVis[0] = !recVis[0];
        recLbl.setForeground(recVis[0]
                ? new Color(0xDC2626)
                : new Color(0, 0, 0, 0));
    });
    timers.add(tRec);
    tRec.start();

    tituloRow.add(titLbl);
    tituloRow.add(recLbl);

    JLabel subLbl = CabinaStyles.lbl(
            isEdit ? "Modifica los datos del estudio"
                   : "Registra un nuevo estudio al sistema",
            11, false, CabinaStyles.TEXT_SEC);
    subLbl.setAlignmentX(LEFT_ALIGNMENT);

    titleCol.add(tituloRow);
    titleCol.add(Box.createVerticalStrut(3));
    titleCol.add(subLbl);

    banda.add(iconBox,  BorderLayout.WEST);
    banda.add(titleCol, BorderLayout.CENTER);
    root.add(banda, BorderLayout.NORTH);

    // ── BODY ──────────────────────────────────────────────────────────
    JPanel body = new JPanel();
    body.setOpaque(false);
    body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
    body.setBorder(new EmptyBorder(22, 24, 12, 24));

    // Sección INFORMACIÓN
    body.add(buildSeccionHeader("INFORMACIÓN DE CABINA", timers));
    body.add(Box.createVerticalStrut(14));

    // Campo nombre
    JLabel lblNom = CabinaStyles.lbl("NOMBRE DE CABINA  *", 9, true, CabinaStyles.INDIGO);
    lblNom.setAlignmentX(LEFT_ALIGNMENT);
    body.add(lblNom);
    body.add(Box.createVerticalStrut(5));

    CabinaStyles.CampoElegante fNombre =
            new CabinaStyles.CampoElegante("Ej: Cabina Norte A", CabinaStyles.INDIGO);
    fNombre.setAlignmentX(LEFT_ALIGNMENT);
    fNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    if (isEdit) fNombre.setText(c.getNombreCabina());
    body.add(fNombre);
    body.add(Box.createVerticalStrut(18));

    // Sección ESTADO
    body.add(buildSeccionHeader("ESTADO DEL ESTUDIO", timers));
    body.add(Box.createVerticalStrut(14));

    // Pills de estado
    String[] estados = {"Disponible", "Ocupada", "Mantenimiento", "Reservada"};
    final String[] estadoSel = {
        isEdit && c.getNombreEstado() != null ? c.getNombreEstado() : "Disponible"
    };

    JPanel estadoGrid = new JPanel(new GridLayout(1, 4, 8, 0));
    estadoGrid.setOpaque(false);
    estadoGrid.setAlignmentX(LEFT_ALIGNMENT);
    estadoGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

    // Preview labels (se actualizan al cambiar pill)
    JLabel previewDot  = new JLabel("●");
    previewDot.setFont(new Font("Segoe UI", Font.BOLD, 10));
    JLabel previewNom  = new JLabel();
    previewNom.setFont(new Font("Segoe UI", Font.BOLD, 11));
    JLabel previewSep  = new JLabel("—");
    previewSep.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    previewSep.setForeground(CabinaStyles.TEXT_MUT);
    JLabel previewDesc = new JLabel();
    previewDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    previewDesc.setForeground(CabinaStyles.TEXT_SEC);

    // Mapa de pill panels para redibujar al cambiar selección
    java.util.Map<String, JPanel>  pillMap  = new java.util.LinkedHashMap<>();
    java.util.Map<String, JLabel>  labelMap = new java.util.LinkedHashMap<>();

    Runnable refreshAll = () -> {
        for (String est : estados) {
            JPanel pill  = pillMap.get(est);
            JLabel lbl   = labelMap.get(est);
            if (pill == null || lbl == null) continue;
            boolean sel = est.equals(estadoSel[0]);
            Color fg = CabinaStyles.colorEstado(est);
            lbl.setText(sel ? "● " + est : est);
            lbl.setForeground(sel ? fg : CabinaStyles.TEXT_MUT);
            pill.repaint();
        }
        // Actualizar preview
        String est = estadoSel[0];
        Color col  = CabinaStyles.colorEstado(est);
        previewDot.setForeground(col);
        previewNom.setText(est);
        previewNom.setForeground(col);
        previewDesc.setText(CabinaStyles.descEstado(est));
    };

    for (String est : estados) {
        final Color fg  = CabinaStyles.colorEstado(est);
        final Color bg  = CabinaStyles.paleBgEstado(est);
        final String e  = est;

        JLabel pillLbl = new JLabel(est, SwingConstants.CENTER);
        pillLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pillLbl.setForeground(CabinaStyles.TEXT_MUT);
        labelMap.put(est, pillLbl);

        JPanel pill = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean sel = e.equals(estadoSel[0]);
                if (sel) {
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(fg);
                    g2.setStroke(new BasicStroke(1.8f));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                } else {
                    g2.setColor(new Color(
                            CabinaStyles.BG_CARD_ALT.getRed(),
                            CabinaStyles.BG_CARD_ALT.getGreen(),
                            CabinaStyles.BG_CARD_ALT.getBlue()));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(CabinaStyles.BORDER);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setOpaque(false);
        pill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pill.add(pillLbl, BorderLayout.CENTER);
        pill.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent ev) {
                estadoSel[0] = e;
                refreshAll.run();
            }
        });
        pillMap.put(est, pill);
        estadoGrid.add(pill);
    }
    body.add(estadoGrid);
    body.add(Box.createVerticalStrut(10));

    // Panel preview del estado
    JPanel previewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CabinaStyles.BG_CARD);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.setColor(CabinaStyles.BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
        }
    };
    previewPanel.setOpaque(false);
    previewPanel.setAlignmentX(LEFT_ALIGNMENT);
    previewPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    previewPanel.setBorder(new EmptyBorder(4, 10, 4, 10));
    previewPanel.add(previewDot);
    previewPanel.add(previewNom);
    previewPanel.add(previewSep);
    previewPanel.add(previewDesc);

    // Inicializar preview con el estado actual
    refreshAll.run();
    body.add(previewPanel);

    root.add(body, BorderLayout.CENTER);

    // ── FOOTER ────────────────────────────────────────────────────────
    JPanel footer = new JPanel(new BorderLayout()) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(CabinaStyles.BG_CARD);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(CabinaStyles.BORDER);
            g2.drawLine(0, 0, getWidth(), 0);
            g2.dispose();
        }
    };
    footer.setOpaque(false);
    footer.setBorder(new EmptyBorder(14, 24, 16, 24));

    JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    btnRow.setOpaque(false);

    CabinaStyles.BtnCabina bCancel = CabinaStyles.btnAccion(
            "Cancelar", false, 120, CabinaStyles.BORDER_STRONG);
    CabinaStyles.BtnCabina bSave   = CabinaStyles.btnAccion(
            isEdit ? "💾  Guardar cambios" : "＋  Crear cabina", true, 175);

    bCancel.addActionListener(e -> cerrarFormConFade(dlg, timers));

    bSave.addActionListener(e -> {
        try {
            String nombre = fNombre.getText().trim();
            if (nombre.isBlank())
                throw new IllegalArgumentException("El nombre de cabina es obligatorio");
            Cabina n = isEdit ? c : new Cabina();
            n.setNombreCabina(nombre);
            n.setNombreEstado(estadoSel[0]);
            if (isEdit) servicio.actualizar(n);
            else        servicio.crear(n);
            toast(isEdit ? "Cabina actualizada" : "Cabina creada", MainFrame.ToastType.SUCCESS);
            recargar();
            cerrarFormConFade(dlg, timers);
        } catch (Exception ex) {
            toast(ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    });

    btnRow.add(bCancel);
    btnRow.add(bSave);
    footer.add(btnRow, BorderLayout.EAST);
    root.add(footer, BorderLayout.SOUTH);

    // ── Mostrar con fade ──────────────────────────────────────────────
    dlg.setContentPane(root);
    dlg.setSize(480, 430);
    dlg.setLocationRelativeTo(this);
    dlg.setResizable(false);

    dlg.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override public void windowClosed(java.awt.event.WindowEvent e) {
            timers.forEach(javax.swing.Timer::stop);
            timers.clear();
        }
    });

    abrirFormConFade(dlg);
    dlg.setVisible(true);
}

// ══════════════════════════════════════════════════════════════════════
//  MÉTODOS AUXILIARES
//  Agrégalos al final de formCabina.java (fuera de openForm)
// ══════════════════════════════════════════════════════════════════════

private void abrirFormConFade(JDialog dlg) {
    try { dlg.setOpacity(0f); } catch (Exception ignore) { return; }
    javax.swing.Timer t = new javax.swing.Timer(14, null);
    final long ini = System.currentTimeMillis();
    final int  dur = 200;
    t.addActionListener(ev -> {
        float p = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
        float e = 1f - (float) Math.pow(1 - p, 3);
        try { dlg.setOpacity(e); } catch (Exception ignore) {}
        if (p >= 1f) t.stop();
    });
    SwingUtilities.invokeLater(t::start);
}

private void cerrarFormConFade(JDialog dlg, List<javax.swing.Timer> timers) {
    try { dlg.setOpacity(1f); } catch (Exception ignore) { dlg.dispose(); return; }
    javax.swing.Timer t = new javax.swing.Timer(14, null);
    final long ini = System.currentTimeMillis();
    final int  dur = 150;
    t.addActionListener(ev -> {
        float p = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
        try { dlg.setOpacity(1f - p); } catch (Exception ignore) {}
        if (p >= 1f) {
            t.stop();
            timers.forEach(javax.swing.Timer::stop);
            timers.clear();
            dlg.dispose();
        }
    });
    t.start();
}

private JPanel buildSeccionHeader(String texto, List<javax.swing.Timer> timers) {
    JPanel p = new JPanel(new BorderLayout(8, 0));
    p.setOpaque(false);
    p.setAlignmentX(LEFT_ALIGNMENT);
    p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

    JLabel lbl = CabinaStyles.lbl(texto, 10, true, CabinaStyles.INDIGO);
    p.add(lbl, BorderLayout.WEST);

    // Línea shimmer
    final float[] phase = {0f};
    JPanel linea = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(CabinaStyles.INDIGO.getRed(),
                    CabinaStyles.INDIGO.getGreen(),
                    CabinaStyles.INDIGO.getBlue(), 55));
            g2.fillRect(0, h / 2, w, 1);
            float cx = phase[0] * w;
            g2.setPaint(new java.awt.RadialGradientPaint(
                    new java.awt.geom.Point2D.Float(cx, h / 2f),
                    Math.max(1f, w * 0.2f),
                    new float[]{0f, 1f},
                    new Color[]{
                        new Color(CabinaStyles.SKY.getRed(),
                                CabinaStyles.SKY.getGreen(),
                                CabinaStyles.SKY.getBlue(), 230),
                        new Color(CabinaStyles.SKY.getRed(),
                                CabinaStyles.SKY.getGreen(),
                                CabinaStyles.SKY.getBlue(), 0)
                    }));
            g2.fillRect(0, h / 2, w, 1);
            g2.dispose();
        }
    };
    linea.setOpaque(false);
    linea.setPreferredSize(new Dimension(0, 18));

    javax.swing.Timer tShimmer = new javax.swing.Timer(38, e -> {
        phase[0] += 0.013f;
        if (phase[0] > 1.4f) phase[0] = -0.4f;
        linea.repaint();
    });
    timers.add(tShimmer);
    tShimmer.start();

    p.add(linea, BorderLayout.CENTER);
    return p;
}

    // ═════════════════════════════════════════════════════════
    //  HELPERS
    // ═════════════════════════════════════════════════════════
    private JPanel buildFieldRow(String label, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel l = CabinaStyles.lbl(label, 9, true, CabinaStyles.INDIGO);
        campo.setPreferredSize(new Dimension(0, 42));
        p.add(l,     BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private void toast(String msg, MainFrame.ToastType t) {
        MainFrame.showToast(msg, t);
    }
}