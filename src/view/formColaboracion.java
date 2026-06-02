package view;

import model.Colaboracion;
import services.ColaboracionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class formColaboracion extends JPanel {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final Color BG_PAGE    = new Color(0xF0F2F8);
    private static final Color BG_CARD    = Color.WHITE;
    private static final Color BG_INPUT   = new Color(0xF9FAFB);
    private static final Color BG_HOVER   = new Color(0xF5F8FF);
    private static final Color BG_SEL     = new Color(0xEFF6FF);

    private static final Color C_BLUE     = new Color(0x2563EB);
    private static final Color C_PURPLE   = new Color(0x7C3AED);
    private static final Color C_GREEN    = new Color(0x059669);
    private static final Color C_AMBER    = new Color(0xD97706);
    private static final Color C_RED      = new Color(0xDC2626);

    private static final Color TXT_DARK   = new Color(0x111827);
    private static final Color TXT_MID    = new Color(0x374151);
    private static final Color TXT_MUTED  = new Color(0x9CA3AF);
    private static final Color TXT_LIGHT  = new Color(0xC4CDD8);

    private static final Color BORDER     = new Color(0xE8EDF6);
    private static final Color BORDER_INPUT = new Color(0xE5E7EB);

    // Colores de badge/acento por tipo
    private static final Color[] TIPO_ACCENT = {C_GREEN, C_AMBER, C_PURPLE, C_BLUE};
    private static final Color[] TIPO_BG     = {
        new Color(0xECFDF5), new Color(0xFFFBEB),
        new Color(0xF5F3FF), new Color(0xEFF6FF)
    };
    private static final Color[] TIPO_FG     = {
        new Color(0x065F46), new Color(0x92400E),
        new Color(0x4C1D95), new Color(0x1E40AF)
    };
    private static final String[] TIPO_NOMBRES = {"Featuring", "Lanzamiento", "Producción", "Remix"};
    private static final String[] TIPO_ICONOS  = {"🎤", "🚀", "🎧", "🎸"};

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Estado ─────────────────────────────────────────────────────────────────
    private final ColaboracionService servicio = new ColaboracionService();
    private final DefaultListModel<Colaboracion> modelo = new DefaultListModel<>();
    private JList<Colaboracion> lista;

    // Stats labels
    private JLabel lblTotal, lblArtistas, lblCanciones, lblUltimo;

    // Panel de detalle
    private JPanel detailPanel;
    private JLabel detAvatar, detNombre, detCancion, detFecha, detTipo, detId;
    private JLabel detEmptyLabel;
    private JPanel detContent;

    // ══════════════════════════════════════════════════════════════════════════
    public formColaboracion() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_PAGE);
        construirUI();
        recargar();
    }

    // ── Construcción de UI ────────────────────────────────────────────────────
    private void construirUI() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(28, 32, 28, 32));

        root.add(buildTopBar(),   BorderLayout.NORTH);
        root.add(buildCenter(),   BorderLayout.CENTER);

        add(root, BorderLayout.CENTER);
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 18, 0));

        // Título
        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("  Colaboraciones");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TXT_DARK);
        title.setIcon(buildGradientIcon());

        JLabel sub = new JLabel("GESTIÓN DE ARTISTAS  ·  CANCIONES  ·  PARTICIPACIONES");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sub.setForeground(TXT_MUTED);
        sub.setBorder(new EmptyBorder(4, 2, 0, 0));

        titleBlock.add(title);
        titleBlock.add(sub);

        // Botones
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(buildBtn("↺  Refrescar", false, false, e -> recargar()));
        btnRow.add(buildBtn("✎  Editar",    false, false, e -> { Colaboracion s = lista.getSelectedValue(); if (s != null) openForm(s); else toast("Selecciona una colaboración", false); }));
        btnRow.add(buildBtn("✖  Eliminar",  false, true,  e -> eliminar()));
        btnRow.add(buildBtn("＋  Nueva colaboración", true, false, e -> openForm(null)));

        p.add(titleBlock, BorderLayout.WEST);
        p.add(btnRow,     BorderLayout.EAST);
        return p;
    }

    // Ícono degradado simulado con un círculo de color
    private Icon buildGradientIcon() {
        return new Icon() {
            public int getIconWidth()  { return 36; }
            public int getIconHeight() { return 36; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(x, y, C_BLUE, x + 36, y + 36, C_PURPLE);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(x, y, 36, 36, 10, 10));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                g2.drawString("🤝", x + 9, y + 24);
                g2.dispose();
            }
        };
    }

    // ── Centro: stats + lista + detalle ───────────────────────────────────────
    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);
        p.add(buildStatsRow(), BorderLayout.NORTH);
        p.add(buildMainGrid(), BorderLayout.CENTER);
        return p;
    }

    // ── Stats row ─────────────────────────────────────────────────────────────
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 95));

        lblTotal    = new JLabel("0");
        lblArtistas = new JLabel("0");
        lblCanciones= new JLabel("0");
        lblUltimo   = new JLabel("—");

        row.add(buildStatCard("TOTAL COLABORACIONES", lblTotal,    "registradas",  C_BLUE,   new Color(0x2563EB)));
        row.add(buildStatCard("ARTISTAS ÚNICOS",      lblArtistas, "participantes",C_PURPLE, new Color(0x7C3AED)));
        row.add(buildStatCard("CANCIONES",            lblCanciones,"involucradas", C_GREEN,  new Color(0x059669)));
        row.add(buildStatCard("ÚLTIMO REGISTRO",      lblUltimo,   "más reciente", C_AMBER,  new Color(0xD97706)));
        return row;
    }

    private JPanel buildStatCard(String lbl, JLabel valLabel, String sub, Color accent, Color topColor) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 3, getWidth(), getHeight() - 3, 14, 14));
                g2.setColor(topColor);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel lblTitle = new JLabel(lbl);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitle.setForeground(TXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valLabel.setForeground(accent);

        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(TXT_LIGHT);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.add(lblTitle);
        inner.add(Box.createVerticalStrut(6));
        inner.add(valLabel);
        inner.add(Box.createVerticalStrut(2));
        inner.add(lblSub);

        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    // ── Main grid: lista + detalle ────────────────────────────────────────────
    private JPanel buildMainGrid() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);
        p.add(buildListPanel(),   BorderLayout.CENTER);
        p.add(buildDetailPanel(), BorderLayout.EAST);
        return p;
    }

    // ── Panel de lista ────────────────────────────────────────────────────────
    private JPanel buildListPanel() {
        JPanel card = buildCard();
        card.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(12, 18, 12, 18));
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(12, 18, 12, 18)
        ));

        JLabel hLbl = new JLabel("☰  Lista de colaboraciones");
        hLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hLbl.setForeground(new Color(0x6B7280));

        JLabel countBadge = new JLabel("0 registros");
        countBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        countBadge.setForeground(new Color(0x4F46E5));
        countBadge.setOpaque(true);
        countBadge.setBackground(new Color(0xEEF2FF));
        countBadge.setBorder(new EmptyBorder(3, 10, 3, 10));

        header.add(hLbl,        BorderLayout.WEST);
        header.add(countBadge,  BorderLayout.EAST);

        // Lista
        lista = new JList<>(modelo);
        lista.setBackground(BG_CARD);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFixedCellHeight(72);
        lista.setCellRenderer(new ColabCellRenderer());

        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarDetalle(lista.getSelectedValue());
        });

        JScrollPane sp = new JScrollPane(lista);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_CARD);

        card.add(header, BorderLayout.NORTH);
        card.add(sp,     BorderLayout.CENTER);

        // Guardamos referencia al badge para actualizar count
        lista.getModel().addListDataListener(new javax.swing.event.ListDataListener() {
            public void intervalAdded(javax.swing.event.ListDataEvent e)   { countBadge.setText(modelo.size() + " registros"); }
            public void intervalRemoved(javax.swing.event.ListDataEvent e) { countBadge.setText(modelo.size() + " registros"); }
            public void contentsChanged(javax.swing.event.ListDataEvent e) { countBadge.setText(modelo.size() + " registros"); }
        });

        return card;
    }

    // ── Cell renderer mejorado ────────────────────────────────────────────────
    private class ColabCellRenderer implements ListCellRenderer<Colaboracion> {
        @Override
        public Component getListCellRendererComponent(JList<? extends Colaboracion> list,
                Colaboracion c, int index, boolean selected, boolean focused) {

            int tipoIdx = getTipoIndex(c);
            Color accent = TIPO_ACCENT[tipoIdx];
            Color tipoBg = TIPO_BG[tipoIdx];
            Color tipoFg = TIPO_FG[tipoIdx];
            String icono = TIPO_ICONOS[tipoIdx];
            String tipoNombre = TIPO_NOMBRES[tipoIdx];

            JPanel row = new JPanel(new BorderLayout(14, 0)) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(accent);
                    g2.fillRect(0, 0, 4, getHeight());
                    g2.dispose();
                }
            };
            row.setOpaque(true);
            row.setBackground(selected ? BG_SEL : BG_CARD);
            row.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(0xF0F3FA)),
                new EmptyBorder(14, 20, 14, 18)
            ));

            // Ícono box
            JLabel iconBox = new JLabel(icono, SwingConstants.CENTER);
            iconBox.setOpaque(true);
            iconBox.setBackground(tipoBg);
            iconBox.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            iconBox.setPreferredSize(new Dimension(42, 42));
            iconBox.setBorder(BorderFactory.createEmptyBorder());

            // Info
            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setOpaque(false);

           String artista = c.getNombreColaborador() != null ? c.getNombreColaborador() : "—";
            String cancion = c.getNombreCancion() != null ? c.getNombreCancion() : "Sin canción";
            String fecha   = c.getFechaColaboracion() != null ? c.getFechaColaboracion().format(FMT) : "—";

            JLabel lNombre = new JLabel(artista);
            lNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lNombre.setForeground(TXT_DARK);

            JLabel lCancion = new JLabel("♪  " + cancion);
            lCancion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lCancion.setForeground(TXT_MUTED);

            JLabel lFecha = new JLabel(fecha);
            lFecha.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lFecha.setForeground(TXT_LIGHT);

            info.add(lNombre);
            info.add(Box.createVerticalStrut(2));
            info.add(lCancion);
            info.add(Box.createVerticalStrut(2));
            info.add(lFecha);

            // Badge de tipo
            JLabel badge = new JLabel("● " + tipoNombre);
            badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            badge.setForeground(tipoFg);
            badge.setOpaque(true);
            badge.setBackground(tipoBg);
            badge.setBorder(new EmptyBorder(5, 12, 5, 12));

            row.add(iconBox, BorderLayout.WEST);
            row.add(info,    BorderLayout.CENTER);
            row.add(badge,   BorderLayout.EAST);

            return row;
        }
    }

    private int getTipoIndex(Colaboracion c) {
       if (c == null || c.getNombreColaborador() == null) return 3;
        // Si el modelo tiene campo tipo puedes usarlo; aquí lo inferimos del artista como demo
        return 3; // default Remix — ajusta según tu campo real
    }

    // ── Panel de detalle ──────────────────────────────────────────────────────
    private JPanel buildDetailPanel() {
        detailPanel = buildCard();
        detailPanel.setLayout(new BorderLayout());
        detailPanel.setPreferredSize(new Dimension(260, 0));

        // Estado vacío
        detEmptyLabel = new JLabel("<html><center>Selecciona una<br>colaboración<br>para ver el detalle</center></html>", SwingConstants.CENTER);
        detEmptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detEmptyLabel.setForeground(new Color(0xC4CDD8));

        // Contenido de detalle
        detContent = new JPanel();
        detContent.setLayout(new BoxLayout(detContent, BoxLayout.Y_AXIS));
        detContent.setOpaque(false);
        detContent.setBorder(new EmptyBorder(20, 20, 20, 20));
        detContent.setVisible(false);

        detAvatar = new JLabel("??", SwingConstants.CENTER);
        detAvatar.setFont(new Font("Segoe UI", Font.BOLD, 17));
        detAvatar.setOpaque(true);
        detAvatar.setPreferredSize(new Dimension(52, 52));
        detAvatar.setMaximumSize(new Dimension(52, 52));
        detAvatar.setMinimumSize(new Dimension(52, 52));

        detNombre  = detLabel("", 16, Font.BOLD,  TXT_DARK);
        detCancion = detLabel("", 13, Font.PLAIN, TXT_MUTED);

        JPanel topInfo = new JPanel(new BorderLayout(14, 0));
        topInfo.setOpaque(false);
        topInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel avatarWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        avatarWrap.setOpaque(false);

        JPanel roundAv = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(detAvatar.getBackground());
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        roundAv.setOpaque(false);
        roundAv.setPreferredSize(new Dimension(52, 52));
        roundAv.add(detAvatar, BorderLayout.CENTER);

        JPanel infoRight = new JPanel();
        infoRight.setLayout(new BoxLayout(infoRight, BoxLayout.Y_AXIS));
        infoRight.setOpaque(false);
        infoRight.add(detNombre);
        infoRight.add(Box.createVerticalStrut(4));
        infoRight.add(detCancion);

        topInfo.add(roundAv,   BorderLayout.WEST);
        topInfo.add(infoRight, BorderLayout.CENTER);

        detFecha = detLabel("", 13, Font.BOLD, TXT_MID);
        detTipo  = detLabel("", 13, Font.BOLD, TXT_MID);
        detId    = detLabel("", 13, Font.BOLD, TXT_MID);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0xF1F5FB));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        detContent.add(topInfo);
        detContent.add(Box.createVerticalStrut(16));
        detContent.add(sep);
        detContent.add(Box.createVerticalStrut(14));
        detContent.add(buildFieldRow("Fecha",        detFecha));
        detContent.add(Box.createVerticalStrut(10));
        detContent.add(buildFieldRow("Tipo",         detTipo));
        detContent.add(Box.createVerticalStrut(10));
        detContent.add(buildFieldRow("ID Canción",   detId));
        detContent.add(Box.createVerticalStrut(20));

        // Botones de acción dentro del detalle
        JPanel detBtns = new JPanel(new GridLayout(1, 2, 8, 0));
        detBtns.setOpaque(false);
        detBtns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        detBtns.add(buildBtn("✎ Editar",   false, false, e -> { Colaboracion s = lista.getSelectedValue(); if (s != null) openForm(s); }));
        detBtns.add(buildBtn("✖ Eliminar", false, true,  e -> eliminar()));
        detContent.add(detBtns);

        detailPanel.add(detEmptyLabel, BorderLayout.CENTER);
        detailPanel.add(detContent,    BorderLayout.NORTH);

        return detailPanel;
    }

    private JPanel buildFieldRow(String key, JLabel valLabel) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel k = new JLabel(key.toUpperCase());
        k.setFont(new Font("Segoe UI", Font.BOLD, 10));
        k.setForeground(new Color(0xB0BAD0));
        p.add(k);
        p.add(Box.createVerticalStrut(2));
        p.add(valLabel);
        return p;
    }

    private JLabel detLabel(String txt, int size, int style, Color fg) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(fg);
        return l;
    }

    private void actualizarDetalle(Colaboracion c) {
        if (c == null) {
            detEmptyLabel.setVisible(true);
            detContent.setVisible(false);
            return;
        }
        int tipoIdx = getTipoIndex(c);
        detAvatar.setText(iniciales(c.getNombreColaborador()));
        detAvatar.setBackground(TIPO_BG[tipoIdx]);
        detAvatar.setForeground(TIPO_FG[tipoIdx]);
        detNombre.setText(c.getNombreColaborador() != null ? c.getNombreColaborador() : "—");
        detCancion.setText("♪  " + (c.getNombreCancion() != null ? c.getNombreCancion() : "Sin canción"));
        detFecha.setText(c.getFechaColaboracion() != null ? c.getFechaColaboracion().format(FMT) : "—");
        detTipo.setText(TIPO_NOMBRES[tipoIdx]);
        detId.setText(c.getIdCancion() != null ? "#" + c.getIdCancion() : "—");
        detEmptyLabel.setVisible(false);
        detContent.setVisible(true);
        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private String iniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "??";
        String[] parts = nombre.trim().split("\\s+");
        if (parts.length >= 2) return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        return nombre.substring(0, Math.min(2, nombre.length())).toUpperCase();
    }

    // ── Acciones ──────────────────────────────────────────────────────────────
    private void recargar() {
        try {
            modelo.clear();
            List<Colaboracion> all = servicio.listar();
            for (Colaboracion c : all) modelo.addElement(c);
            actualizarStats(all);
            actualizarDetalle(null);
        } catch (Exception ex) {
            MainFrame.showToast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    private void actualizarStats(List<Colaboracion> all) {
        lblTotal.setText(String.valueOf(all.size()));
        long artUnicos = all.stream()
            .map(Colaboracion::getNombreColaborador)
            .filter(a -> a != null)
            .distinct().count();
        lblArtistas.setText(String.valueOf(artUnicos));
        long cancionesUnicas = all.stream()
            .map(Colaboracion::getIdCancion)
            .filter(id -> id != null)
            .distinct().count();
        lblCanciones.setText(String.valueOf(cancionesUnicas));
        all.stream()
            .map(Colaboracion::getFechaColaboracion)
            .filter(f -> f != null)
            .max(LocalDate::compareTo)
            .ifPresentOrElse(
                f -> lblUltimo.setText(f.format(DateTimeFormatter.ofPattern("dd/MM/yy"))),
                () -> lblUltimo.setText("—")
            );
    }

    private void eliminar() {
        Colaboracion s = lista.getSelectedValue();
        if (s == null) { MainFrame.showToast("Selecciona una colaboración", MainFrame.ToastType.INFO); return; }
        int op = JOptionPane.showConfirmDialog(this,
            "¿Eliminar la colaboración de " + s.getNombreColaborador() + "?",
            "Z-One", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            try {
                if (servicio.eliminar(s.getIdColaboracion())) {
                    MainFrame.showToast("Colaboración eliminada", MainFrame.ToastType.SUCCESS);
                    recargar();
                }
            } catch (Exception ex) {
                MainFrame.showToast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR);
            }
        }
    }

    // ── Formulario de creación / edición ──────────────────────────────────────
// ══════════════════════════════════════════════════════════════════════════════
//  openForm — reemplaza el método openForm() en formColaboracion.java
//  Requiere los métodos auxiliares al final: abrirConFade, cerrarConFade,
//  buildFieldAnimado, buildSeccionHeader, buildErrorLabel, buildBtnAnimado
// ══════════════════════════════════════════════════════════════════════════════

private void openForm(Colaboracion c) {
    boolean isEdit = c != null;
    java.util.List<Timer> dlgTimers = new java.util.ArrayList<>();

    JDialog dlg = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Editar colaboración" : "Nueva colaboración", true);
    dlg.setResizable(false);
    dlg.setUndecorated(false);

    // ── ROOT ──────────────────────────────────────────────────────────────────
    JPanel root = new JPanel(new BorderLayout()) {
        @Override protected void paintComponent(Graphics g) {
            g.setColor(BG_PAGE);
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
    };
    root.setOpaque(false);

    // ══════════════════════════════════════════════════════════════════════════
    //  BANDA SUPERIOR ANIMADA
    // ══════════════════════════════════════════════════════════════════════════
    final float[] px  = new float[18]; final float[] py  = new float[18];
    final float[] pv  = new float[18]; final int[]   pSym = new int[18];
    final float[] wH  = new float[20]; final float[] wHt = new float[20];
    final float[] bounce  = {0f};
    final boolean[] recVis = {true};
    final boolean[] inited = {false};
    final String[]  syms   = {"🤝","🎤","🎸","🎧","🚀","♪","✦"};

    JPanel banda = new JPanel(new BorderLayout(16, 0)) {
        @Override protected void paintComponent(Graphics g) {
            if (!inited[0]) {
                inited[0] = true;
                java.util.Random r = new java.util.Random();
                for (int i = 0; i < px.length; i++) {
                    px[i]  = r.nextFloat(); py[i]  = r.nextFloat();
                    pv[i]  = 0.0006f + r.nextFloat() * 0.001f;
                    pSym[i] = r.nextInt(syms.length);
                }
                for (int i = 0; i < wH.length; i++) {
                    wH[i]  = 0.15f + r.nextFloat() * 0.7f;
                    wHt[i] = 0.1f  + r.nextFloat() * 0.85f;
                }
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            // Fondo degradado azul→violeta
            g2.setPaint(new GradientPaint(0, 0, new Color(0x1A1060),
                    w, h, new Color(0x3B1A8E)));
            g2.fillRect(0, 0, w, h);

            // Brillo superior
            g2.setPaint(new GradientPaint(0, 0, new Color(255,255,255,24),
                    0, h/2f, new Color(255,255,255,0)));
            g2.fillRect(0, 0, w, h/2);

            // Waveform de fondo
            int bW = 3, gap = 5, totalW2 = wH.length * (bW + gap);
            int sx2 = w/2 - totalW2/2, midY = h/2;
            for (int i = 0; i < wH.length; i++) {
                int bH   = (int)(wH[i] * h * 0.52f);
                int alpha = (int)(16 + wH[i] * 30);
                g2.setColor(new Color(C_PURPLE.getRed(), C_PURPLE.getGreen(),
                        C_PURPLE.getBlue(), Math.min(65, alpha)));
                g2.fillRoundRect(sx2 + i*(bW+gap), midY - bH/2, bW, bH, bW, bW);
            }

            // Partículas flotantes
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            for (int i = 0; i < px.length; i++) {
                int alpha = (int)(10 + Math.sin(py[i] * Math.PI) * 28);
                g2.setColor(new Color(C_BLUE.getRed(), C_BLUE.getGreen(),
                        C_BLUE.getBlue(), Math.max(0, Math.min(55, alpha))));
                g2.drawString(syms[pSym[i]], (int)(px[i]*w), (int)(py[i]*h));
            }

            // Línea inferior
            g2.setColor(new Color(C_BLUE.getRed(), C_BLUE.getGreen(), C_BLUE.getBlue(), 180));
            g2.fillRect(0, h-2, w, 2);
            g2.dispose();
        }
    };
    banda.setOpaque(false);
    banda.setPreferredSize(new Dimension(0, 105));
    banda.setBorder(new EmptyBorder(22, 26, 22, 26));

    // Timer: partículas + waveform
    Timer tAnim = new Timer(38, ev -> {
        java.util.Random r = new java.util.Random();
        for (int i = 0; i < px.length; i++) {
            py[i] -= pv[i]; px[i] += pv[i] * 0.22f;
            if (py[i] < -0.12f) { py[i] = 1.12f; px[i] = r.nextFloat(); pSym[i] = r.nextInt(syms.length); }
            if (px[i] >  1.12f)   px[i] = -0.12f;
        }
        for (int i = 0; i < wH.length; i++) {
            wH[i] += (wHt[i] - wH[i]) * 0.09f;
            if (Math.abs(wH[i] - wHt[i]) < 0.015f)
                wHt[i] = 0.08f + r.nextFloat() * 0.88f;
        }
        bounce[0] = (float)(Math.sin(System.currentTimeMillis() / 640.0) * 0.5 + 0.5);
        banda.repaint();
    });
    dlgTimers.add(tAnim); tAnim.start();

    // Timer REC
    Timer tRec = new Timer(550, ev -> { recVis[0] = !recVis[0]; banda.repaint(); });
    dlgTimers.add(tRec); tRec.start();

    // ── Ícono pulsante ────────────────────────────────────────────────────────
    JPanel iconBox = new JPanel(new GridBagLayout()) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int pulse = (int)(bounce[0] * 20);
            g2.setColor(new Color(255,255,255, 10 + pulse));
            g2.fillRoundRect(-4,-4, getWidth()+8, getHeight()+8, 18, 18);
            g2.setColor(new Color(255,255,255, 25 + pulse));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.setColor(new Color(255,255,255, 85 + pulse));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
            g2.dispose();
        }
    };
    iconBox.setOpaque(false);
    iconBox.setPreferredSize(new Dimension(56, 56));
    JLabel icoLbl = new JLabel(isEdit ? "✎" : "🤝");
    icoLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
    icoLbl.setForeground(Color.WHITE);
    iconBox.add(icoLbl);

    // ── Textos + REC ──────────────────────────────────────────────────────────
    JPanel txtCol = new JPanel();
    txtCol.setOpaque(false);
    txtCol.setLayout(new BoxLayout(txtCol, BoxLayout.Y_AXIS));

    JPanel titRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    titRow.setOpaque(false); titRow.setAlignmentX(LEFT_ALIGNMENT);

    JLabel titLbl = new JLabel(isEdit ? "Editar colaboración" : "Nueva colaboración");
    titLbl.setFont(new Font("Segoe UI", Font.BOLD, 21));
    titLbl.setForeground(Color.WHITE);

    JLabel recLbl = new JLabel("● REC") {
        @Override protected void paintComponent(Graphics g) {
            if (!recVis[0]) return;
            super.paintComponent(g);
        }
    };
    recLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
    recLbl.setForeground(new Color(0xFF4455));
    titRow.add(titLbl); titRow.add(recLbl);

    JLabel subLbl = new JLabel(isEdit
            ? "MODIFICA LOS DATOS DE LA COLABORACIÓN"
            : "REGISTRA UNA NUEVA COLABORACIÓN ARTÍSTICA");
    subLbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
    subLbl.setForeground(new Color(255,255,255,168));
    subLbl.setAlignmentX(LEFT_ALIGNMENT);

    txtCol.add(Box.createVerticalGlue());
    txtCol.add(titRow);
    txtCol.add(Box.createVerticalStrut(4));
    txtCol.add(subLbl);
    txtCol.add(Box.createVerticalGlue());

    banda.add(iconBox, BorderLayout.WEST);
    banda.add(txtCol,  BorderLayout.CENTER);
    root.add(banda,    BorderLayout.NORTH);

    // ══════════════════════════════════════════════════════════════════════════
    //  LÍNEA SHIMMER
    // ══════════════════════════════════════════════════════════════════════════
    final float[] shimPhase = {0f};
    JPanel shimmer = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h2 = getHeight();
            g2.setColor(new Color(C_BLUE.getRed(), C_BLUE.getGreen(), C_BLUE.getBlue(), 40));
            g2.fillRect(0, 0, w, h2);
            float cx2 = shimPhase[0] * w;
            g2.setPaint(new java.awt.RadialGradientPaint(
                    new java.awt.geom.Point2D.Float(cx2, h2/2f),
                    Math.max(1f, w * 0.20f),
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{
                        new Color(255,255,255,200),
                        new Color(C_PURPLE.getRed(), C_PURPLE.getGreen(), C_PURPLE.getBlue(), 100),
                        new Color(C_BLUE.getRed(), C_BLUE.getGreen(), C_BLUE.getBlue(), 0)
                    }));
            g2.fillRect(0, 0, w, h2);
            g2.dispose();
        }
    };
    shimmer.setOpaque(false);
    shimmer.setPreferredSize(new Dimension(0, 3));
    Timer tShim = new Timer(38, ev -> {
        shimPhase[0] += 0.014f;
        if (shimPhase[0] > 1.5f) shimPhase[0] = -0.5f;
        shimmer.repaint();
    });
    dlgTimers.add(tShim); tShim.start();

    // ══════════════════════════════════════════════════════════════════════════
    //  BODY — formulario
    // ══════════════════════════════════════════════════════════════════════════
    JPanel body = new JPanel();
    body.setOpaque(false);
    body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
    body.setBorder(new EmptyBorder(20, 26, 8, 26));

    // ── Sección: Artista ──────────────────────────────────────────────────────
    body.add(buildSeccionHeader("INFORMACIÓN DE LA COLABORACIÓN", C_BLUE, dlgTimers));
    body.add(Box.createVerticalStrut(14));

    // Artista + ID Canción en fila
    JTextField fArt = buildFieldAnimado(
            isEdit && c.getNombreColaborador() != null ? c.getNombreColaborador():"",
            "Ej. J Balvin", dlgTimers);
    JTextField fCan = buildFieldAnimado(
            isEdit && c.getIdCancion() != null ? String.valueOf(c.getIdCancion()) : "",
            "Ej. 3", dlgTimers);

    JLabel errArt = buildErrorLabel();
    JLabel errCan = buildErrorLabel();

    JPanel rowArtCan = new JPanel(new GridLayout(1, 2, 14, 0));
    rowArtCan.setOpaque(false);
    rowArtCan.setAlignmentX(LEFT_ALIGNMENT);
    rowArtCan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
    rowArtCan.add(envolverCampo("ARTISTA COLABORADOR  *", fArt));
    rowArtCan.add(envolverCampo("ID CANCIÓN  *", fCan));
    body.add(rowArtCan);

    JPanel rowErrArtCan = new JPanel(new GridLayout(1, 2, 14, 0));
    rowErrArtCan.setOpaque(false);
    rowErrArtCan.setAlignmentX(LEFT_ALIGNMENT);
    rowErrArtCan.add(errArt);
    rowErrArtCan.add(errCan);
    body.add(rowErrArtCan);
    body.add(Box.createVerticalStrut(14));

    // ── Sección: Tipo ─────────────────────────────────────────────────────────
    body.add(buildSeccionHeader("TIPO DE COLABORACIÓN", C_PURPLE, dlgTimers));
    body.add(Box.createVerticalStrut(12));

    // Pills de tipo animadas
    final int[] tipoSel = {0};
    JPanel tipoGrid = new JPanel(new GridLayout(2, 2, 8, 8));
    tipoGrid.setOpaque(false);
    tipoGrid.setAlignmentX(LEFT_ALIGNMENT);
    tipoGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

    JPanel[] pillPanels = new JPanel[4];
    JLabel[] pillLabels = new JLabel[4];

    Runnable refreshPills = () -> {
        for (int i = 0; i < 4; i++) {
            final boolean sel = (i == tipoSel[0]);
            Color fg = sel ? TIPO_FG[i] : TXT_MUTED;
            pillLabels[i].setText(TIPO_ICONOS[i] + "  " + (sel ? "● " : "") + TIPO_NOMBRES[i]);
            pillLabels[i].setForeground(fg);
            pillPanels[i].repaint();
        }
    };

    for (int i = 0; i < 4; i++) {
        final int idx = i;
        Color accent = TIPO_ACCENT[i];
        Color bg2    = TIPO_BG[i];

        JLabel pill = new JLabel("", SwingConstants.CENTER);
        pill.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        pillLabels[i] = pill;

        // hover animation
        final float[] hoverT = {0f};
        final Timer[] hTimer = {null};

        JPanel pillPanel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean sel = (idx == tipoSel[0]);
                if (sel) {
                    g2.setColor(bg2);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(accent);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                } else {
                    Color base = new Color(
                        (int)(BG_CARD.getRed()   + (bg2.getRed()   - BG_CARD.getRed())   * hoverT[0] * 0.5f),
                        (int)(BG_CARD.getGreen() + (bg2.getGreen() - BG_CARD.getGreen()) * hoverT[0] * 0.5f),
                        (int)(BG_CARD.getBlue()  + (bg2.getBlue()  - BG_CARD.getBlue())  * hoverT[0] * 0.5f));
                    g2.setColor(base);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(new Color(
                        BORDER_INPUT.getRed(), BORDER_INPUT.getGreen(), BORDER_INPUT.getBlue(),
                        (int)(180 + hoverT[0] * 75)));
                    g2.setStroke(new BasicStroke(1f + hoverT[0] * 0.6f));
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pillPanel.setOpaque(false);
        pillPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pillPanel.add(pill, BorderLayout.CENTER);
        pillPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        pillPanel.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (hTimer[0] != null) hTimer[0].stop();
                hTimer[0] = new Timer(12, null);
                hTimer[0].addActionListener(ev -> {
                    hoverT[0] = Math.min(1f, hoverT[0] + 0.1f);
                    pillPanel.repaint();
                    if (hoverT[0] >= 1f) ((Timer)ev.getSource()).stop();
                });
                hTimer[0].start();
            }
            @Override public void mouseExited(MouseEvent e) {
                if (hTimer[0] != null) hTimer[0].stop();
                hTimer[0] = new Timer(12, null);
                hTimer[0].addActionListener(ev -> {
                    hoverT[0] = Math.max(0f, hoverT[0] - 0.1f);
                    pillPanel.repaint();
                    if (hoverT[0] <= 0f) ((Timer)ev.getSource()).stop();
                });
                hTimer[0].start();
            }
            @Override public void mouseClicked(MouseEvent e) {
                tipoSel[0] = idx;
                // Pequeño pop de escala
                Timer tPop = new Timer(12, null);
                final float[] scale = {1f};
                final boolean[] goingUp = {true};
                tPop.addActionListener(ev -> {
                    if (goingUp[0]) { scale[0] += 0.04f; if (scale[0] >= 1.08f) goingUp[0] = false; }
                    else            { scale[0] -= 0.04f; if (scale[0] <= 1f)    { scale[0] = 1f; tPop.stop(); } }
                    pillPanel.repaint();
                });
                dlgTimers.add(tPop); tPop.start();
                refreshPills.run();
            }
        });

        pillPanels[i] = pillPanel;
        tipoGrid.add(pillPanel);
    }
    refreshPills.run();
    body.add(tipoGrid);
    body.add(Box.createVerticalStrut(14));

    // ── Sección: Fecha ────────────────────────────────────────────────────────
    body.add(buildSeccionHeader("FECHA DE COLABORACIÓN", C_GREEN, dlgTimers));
    body.add(Box.createVerticalStrut(12));

    JTextField fFecha = buildFieldAnimado(
            isEdit && c.getFechaColaboracion() != null
                    ? c.getFechaColaboracion().format(FMT)
                    : LocalDate.now().format(FMT),
            "dd/MM/yyyy", dlgTimers);
    JLabel errFecha = buildErrorLabel();

    body.add(envolverCampo("FECHA  * (dd/MM/yyyy)", fFecha));
    body.add(errFecha);
    body.add(Box.createVerticalStrut(22));

    // ══════════════════════════════════════════════════════════════════════════
    //  FOOTER — botones
    // ══════════════════════════════════════════════════════════════════════════
    JPanel footer = new JPanel(new BorderLayout()) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(BG_CARD);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(BORDER);
            g2.drawLine(0, 0, getWidth(), 0);
            g2.dispose();
        }
    };
    footer.setOpaque(false);
    footer.setBorder(new EmptyBorder(12, 26, 16, 26));

    JPanel bRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    bRow.setOpaque(false);

    // Botón Cancelar
    JButton bCancel = buildBtnAnimado("Cancelar", false, false, dlgTimers);
    bCancel.setPreferredSize(new Dimension(125, 40));

    // Botón Guardar con estados
    final String labelGuardar = isEdit ? "💾  Guardar cambios" : "🤝  Crear colaboración";
    JButton bSave = buildBtnAnimado(labelGuardar, true, false, dlgTimers);
    bSave.setPreferredSize(new Dimension(200, 40));

    bCancel.addActionListener(e -> cerrarConFade(dlg, dlgTimers));

    bSave.addActionListener(e -> {
        // ── Limpiar errores ───────────────────────────────────────────────────
        ocultarError(errArt);  limpiarErrorCampo(fArt);
        ocultarError(errCan);  limpiarErrorCampo(fCan);
        ocultarError(errFecha); limpiarErrorCampo(fFecha);

        String artTxt   = fArt  .getText().trim();
        String canTxt   = fCan  .getText().trim();
        String fechaTxt = fFecha.getText().trim();
        boolean ok = true;

        if (artTxt.isBlank()) {
            shakeCampo(fArt, dlgTimers);
            mostrarError(errArt, "El artista es obligatorio", dlgTimers);
            ok = false;
        }
        if (canTxt.isBlank()) {
            shakeCampo(fCan, dlgTimers);
            mostrarError(errCan, "El ID de canción es obligatorio", dlgTimers);
            ok = false;
        } else {
            try { Integer.parseInt(canTxt); }
            catch (NumberFormatException ex) {
                shakeCampo(fCan, dlgTimers);
                mostrarError(errCan, "Debe ser un número", dlgTimers);
                ok = false;
            }
        }
        LocalDate fechaP = null;
        if (fechaTxt.isBlank()) {
            shakeCampo(fFecha, dlgTimers);
            mostrarError(errFecha, "La fecha es obligatoria", dlgTimers);
            ok = false;
        } else {
            try { fechaP = LocalDate.parse(fechaTxt, FMT); }
            catch (Exception ex) {
                shakeCampo(fFecha, dlgTimers);
                mostrarError(errFecha, "Formato inválido (dd/MM/yyyy)", dlgTimers);
                ok = false;
            }
        }
        if (!ok) return;

        // ── Estado loading ────────────────────────────────────────────────────
        bSave.setEnabled(false);
        bCancel.setEnabled(false);
        setLoadingBtn(bSave, "Guardando...", dlgTimers);

        final LocalDate fechaFinal = fechaP;
        Timer tGuardar = new Timer(750, done -> {
            try {
                Colaboracion n = isEdit ? c : new Colaboracion();
                n.setNombreColaborador(artTxt);
                n.setIdCancion(Integer.parseInt(canTxt));
                n.setFechaColaboracion(fechaFinal);
                // n.setTipo(TIPO_NOMBRES[tipoSel[0]]); // descomenta si tu modelo lo tiene

                if (isEdit) servicio.actualizar(n);
                else        servicio.crear(n);

                // ── Estado success ────────────────────────────────────────────
                setSuccessBtn(bSave, isEdit ? "¡Actualizado!" : "¡Creado!", dlgTimers);
                MainFrame.showToast(
                    isEdit ? "Colaboración actualizada" : "Colaboración creada: " + artTxt,
                    MainFrame.ToastType.SUCCESS);

                Timer tCerrar = new Timer(850, close -> {
                    dlgTimers.forEach(Timer::stop);
                    recargar();
                    dlg.dispose();
                });
                tCerrar.setRepeats(false);
                dlgTimers.add(tCerrar);
                tCerrar.start();

            } catch (Exception ex) {
                resetBtn(bSave, labelGuardar);
                bSave.setEnabled(true);
                bCancel.setEnabled(true);
                MainFrame.showToast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR);
            }
        });
        tGuardar.setRepeats(false);
        dlgTimers.add(tGuardar);
        tGuardar.start();
    });

    bRow.add(bCancel);
    bRow.add(bSave);
    footer.add(bRow, BorderLayout.EAST);

    // ── Ensamblar ─────────────────────────────────────────────────────────────
    JPanel wrap = new JPanel(new BorderLayout());
    wrap.setOpaque(false);
    wrap.add(shimmer, BorderLayout.NORTH);
    wrap.add(body,    BorderLayout.CENTER);
    root.add(wrap,   BorderLayout.CENTER);
    root.add(footer, BorderLayout.SOUTH);

    dlg.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override public void windowClosed(java.awt.event.WindowEvent e) {
            dlgTimers.forEach(Timer::stop);
            dlgTimers.clear();
        }
    });

    dlg.setContentPane(root);
    dlg.pack();
    dlg.setMinimumSize(new Dimension(560, dlg.getPreferredSize().height));
    dlg.setLocationRelativeTo(this);
    abrirConFade(dlg);
    dlg.setVisible(true);
}

// ══════════════════════════════════════════════════════════════════════════════
//  MÉTODOS AUXILIARES — agrégalos al final de formColaboracion.java
// ══════════════════════════════════════════════════════════════════════════════

// ── Fade abrir / cerrar ───────────────────────────────────────────────────────
private void abrirConFade(JDialog dlg) {
    try { dlg.setOpacity(0f); } catch (Exception ignore) { return; }
    Timer t = new Timer(14, null);
    final long ini = System.currentTimeMillis();
    t.addActionListener(ev -> {
        float p = Math.min(1f, (System.currentTimeMillis() - ini) / 220f);
        float e = 1f - (float) Math.pow(1 - p, 3);
        try { dlg.setOpacity(e); } catch (Exception ignore) {}
        if (p >= 1f) t.stop();
    });
    SwingUtilities.invokeLater(t::start);
}

private void cerrarConFade(JDialog dlg, java.util.List<Timer> timers) {
    try { dlg.setOpacity(1f); } catch (Exception ignore) { dlg.dispose(); return; }
    Timer t = new Timer(14, null);
    final long ini = System.currentTimeMillis();
    t.addActionListener(ev -> {
        float p = Math.min(1f, (System.currentTimeMillis() - ini) / 170f);
        try { dlg.setOpacity(1f - p); } catch (Exception ignore) {}
        if (p >= 1f) {
            t.stop();
            timers.forEach(Timer::stop);
            timers.clear();
            dlg.dispose();
        }
    });
    t.start();
}

// ── Campo con animación de foco y shake ───────────────────────────────────────
private JTextField buildFieldAnimado(String valor, String placeholder, java.util.List<Timer> timers) {
    final float[] focusT = {0f};
    final boolean[] focused = {false};
    final float[] shakeX   = {0f};
    final boolean[] isError = {false};

    JTextField f = new JTextField(valor) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int sh = (int) shakeX[0];
            int w  = getWidth(), h = getHeight();

            // Halo
            if (isError[0]) {
                g2.setColor(new Color(220, 38, 38, 50));
                g2.fillRoundRect(sh-2, -2, w+4, h+4, 14, 14);
            } else if (focusT[0] > 0.01f) {
                Color hc = C_BLUE;
                g2.setColor(new Color(hc.getRed(), hc.getGreen(), hc.getBlue(), (int)(focusT[0]*60)));
                g2.fillRoundRect(sh-2, -2, w+4, h+4, 14, 14);
            }
            // Fondo
            g2.setColor(isError[0] ? new Color(0xFFF0F0) :
                    blend(BG_INPUT, new Color(0xEFF6FF), focusT[0] * 0.3f));
            g2.fillRoundRect(sh+2, 2, w-5, h-5, 10, 10);
            // Borde
            g2.setColor(isError[0] ? C_RED :
                    blend(BORDER_INPUT, C_BLUE, focusT[0]));
            g2.setStroke(new BasicStroke(isError[0] ? 1.7f : 1f + focusT[0]*0.5f));
            g2.drawRoundRect(sh+2, 2, w-6, h-6, 10, 10);
            g2.dispose();

            // Placeholder
            if (getText().isEmpty() && !focused[0]) {
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setFont(getFont().deriveFont(Font.ITALIC));
                g3.setColor(TXT_MUTED);
                FontMetrics fm = g3.getFontMetrics();
                g3.drawString(placeholder, sh+16, (h + fm.getAscent())/2 - 2);
                g3.dispose();
            }
            super.paintComponent(g);
        }
    };
    f.setOpaque(false);
    f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    f.setForeground(TXT_DARK);
    f.setCaretColor(C_BLUE);
    f.setBorder(new EmptyBorder(8, 14, 8, 14));
    f.setPreferredSize(new Dimension(0, 40));

    f.addFocusListener(new FocusAdapter() {
        @Override public void focusGained(FocusEvent e) {
            focused[0] = true; isError[0] = false;
            animField(f, focusT, true, timers);
        }
        @Override public void focusLost(FocusEvent e) {
            focused[0] = false;
            animField(f, focusT, false, timers);
        }
    });

    // Guardamos estado de error en el campo para acceso externo
    f.putClientProperty("isError", isError);
    f.putClientProperty("shakeX",  shakeX);
    return f;
}

private void animField(JTextField f, float[] focusT, boolean in, java.util.List<Timer> timers) {
    Timer t = new Timer(16, null);
    final long ini = System.currentTimeMillis();
    final float desde = focusT[0], hasta = in ? 1f : 0f;
    t.addActionListener(ev -> {
        float p = Math.min(1f, (System.currentTimeMillis() - ini) / 200f);
        focusT[0] = desde + (hasta - desde) * (1f - (float)Math.pow(1 - p, 3));
        f.repaint();
        if (p >= 1f) t.stop();
    });
    timers.add(t); t.start();
}

@SuppressWarnings("unchecked")
private void shakeCampo(JTextField f, java.util.List<Timer> timers) {
    boolean[] isErr = (boolean[]) f.getClientProperty("isError");
    float[]   shX   = (float[])  f.getClientProperty("shakeX");
    if (isErr != null) isErr[0] = true;
    if (shX == null) return;
    Timer t = new Timer(16, null);
    final long ini = System.currentTimeMillis();
    t.addActionListener(ev -> {
        float p = Math.min(1f, (System.currentTimeMillis() - ini) / 380f);
        shX[0]  = (float)(Math.sin(p * Math.PI * 5) * (1 - p) * 7);
        f.repaint();
        if (p >= 1f) { shX[0] = 0f; t.stop(); }
    });
    timers.add(t); t.start();
}

@SuppressWarnings("unchecked")
private void limpiarErrorCampo(JTextField f) {
    boolean[] isErr = (boolean[]) f.getClientProperty("isError");
    if (isErr != null) { isErr[0] = false; f.repaint(); }
}

// ── Error label deslizante ────────────────────────────────────────────────────
private JLabel buildErrorLabel() {
    JLabel l = new JLabel(" ") {
        float alpha = 0f; float offY = -5f;
        @Override protected void paintComponent(Graphics g) {
            if (alpha <= 0f) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.translate(0, (int)offY);
            super.paintComponent(g2);
            g2.dispose();
        }
        // Permite acceder a alpha/offY desde fuera vía putClientProperty
    };
    l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
    l.setForeground(C_RED);
    l.setBorder(new EmptyBorder(2, 2, 0, 0));
    l.setPreferredSize(new Dimension(0, 0));
    l.putClientProperty("alpha", new float[]{0f});
    l.putClientProperty("offY",  new float[]{-5f});
    return l;
}

@SuppressWarnings("unchecked")
private void mostrarError(JLabel lbl, String msg, java.util.List<Timer> timers) {
    lbl.setText(msg);
    lbl.setPreferredSize(new Dimension(0, 18));
    if (lbl.getParent() != null) lbl.getParent().revalidate();
    float[] alpha = (float[]) lbl.getClientProperty("alpha");
    float[] offY  = (float[]) lbl.getClientProperty("offY");
    Timer t = new Timer(16, null);
    final long ini = System.currentTimeMillis();
    t.addActionListener(ev -> {
        float p = Math.min(1f, (System.currentTimeMillis() - ini) / 200f);
        float ease = 1f - (float)Math.pow(1 - p, 3);
        if (alpha != null) alpha[0] = ease;
        if (offY  != null) offY[0]  = -5f * (1 - ease);
        lbl.repaint();
        if (p >= 1f) t.stop();
    });
    timers.add(t); t.start();
}

private void ocultarError(JLabel lbl) {
    lbl.setText(" ");
    lbl.setPreferredSize(new Dimension(0, 0));
    float[] alpha = (float[]) lbl.getClientProperty("alpha");
    float[] offY  = (float[]) lbl.getClientProperty("offY");
    if (alpha != null) alpha[0] = 0f;
    if (offY  != null) offY[0]  = -5f;
    lbl.repaint();
    if (lbl.getParent() != null) lbl.getParent().revalidate();
}

// ── Sección header con línea shimmer ─────────────────────────────────────────
private JPanel buildSeccionHeader(String texto, Color color, java.util.List<Timer> timers) {
    JPanel p = new JPanel(new BorderLayout(10, 0));
    p.setOpaque(false);
    p.setAlignmentX(LEFT_ALIGNMENT);
    p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

    JLabel lbl = new JLabel(texto);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
    lbl.setForeground(color);
    p.add(lbl, BorderLayout.WEST);

    final float[] phase = {0f};
    JPanel linea = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 38));
            g2.fillRect(0, h/2, w, 1);
            float cx2 = phase[0] * w;
            g2.setPaint(new java.awt.RadialGradientPaint(
                    new java.awt.geom.Point2D.Float(cx2, h/2f),
                    Math.max(1f, w*0.22f),
                    new float[]{0f, 1f},
                    new Color[]{
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 190),
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)
                    }));
            g2.fillRect(0, h/2, w, 1);
            g2.dispose();
        }
    };
    linea.setOpaque(false);

    Timer t = new Timer(38, ev -> {
        phase[0] += 0.014f;
        if (phase[0] > 1.4f) phase[0] = -0.4f;
        linea.repaint();
    });
    timers.add(t); t.start();
    p.add(linea, BorderLayout.CENTER);
    return p;
}

// ── Envolver campo con etiqueta ───────────────────────────────────────────────
private JPanel envolverCampo(String etiqueta, JComponent campo) {
    JPanel p = new JPanel(new BorderLayout(0, 5));
    p.setOpaque(false);
    JLabel l = new JLabel(etiqueta);
    l.setFont(new Font("Segoe UI", Font.BOLD, 9));
    l.setForeground(C_BLUE);
    p.add(l,     BorderLayout.NORTH);
    p.add(campo, BorderLayout.CENTER);
    return p;
}

// ── Botón animado con hover + ripple ──────────────────────────────────────────
private JButton buildBtnAnimado(String texto, boolean primary, boolean danger,
                                java.util.List<Timer> timers) {
    final float[] hoverT  = {0f};
    final float[] shimX   = {-0.3f};
    final float[] spinA   = {0f};
    final float[] checkP  = {0f};
    final int[]   estado  = {0}; // 0=normal 1=loading 2=success
    final float[] ripA    = {0f};
    final float[] ripR    = {0f};
    final int[]   ripXY   = {0, 0};

    JButton b = new JButton(texto) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            if (primary || estado[0] == 2) {
                Color base = estado[0] == 2 ? new Color(0x059669)
                           : blend(C_BLUE, new Color(0x1E40AF), hoverT[0]*0.5f);
                if (primary && estado[0] == 0) {
                    g2.setPaint(new GradientPaint(0, 0, base, w, 0,
                            blend(C_PURPLE, new Color(0x5B21B6), hoverT[0]*0.4f)));
                } else {
                    g2.setColor(base);
                }
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                g2.setPaint(new GradientPaint(0, 0, new Color(255,255,255,28),
                        0, h/2f, new Color(0,0,0,0)));
                g2.fillRoundRect(0, 0, w, h/2, 10, 10);
                if (primary && estado[0] == 0) {
                    float cx2 = shimX[0] * w;
                    g2.setPaint(new java.awt.RadialGradientPaint(
                            new java.awt.geom.Point2D.Float(cx2, h/2f),
                            Math.max(1f, w*0.22f),
                            new float[]{0f, 1f},
                            new Color[]{new Color(255,255,255,50), new Color(255,255,255,0)}));
                    g2.fillRoundRect(0, 0, w, h, 10, 10);
                }
            } else if (danger) {
                g2.setColor(new Color(C_RED.getRed(), C_RED.getGreen(), C_RED.getBlue(),
                        (int)(hoverT[0]*30)));
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                g2.setColor(new Color(C_RED.getRed(), C_RED.getGreen(), C_RED.getBlue(),
                        (int)(80 + hoverT[0]*120)));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w-1, h-1, 10, 10);
            } else {
                g2.setColor(blend(BG_CARD, new Color(0xEFF6FF), hoverT[0]*0.6f));
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                g2.setColor(blend(BORDER, C_BLUE, hoverT[0]*0.7f));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w-1, h-1, 10, 10);
            }

            // Ripple
            if (ripA[0] > 0f) {
                g2.setColor(new Color(255,255,255,(int)(ripA[0]*100)));
                g2.fillOval(ripXY[0]-(int)ripR[0], ripXY[1]-(int)ripR[0],
                        (int)ripR[0]*2, (int)ripR[0]*2);
            }
            g2.dispose();

            // Texto / spinner / check
            if (estado[0] == 1) {
                // Spinner
                Graphics2D gs = (Graphics2D) g.create();
                gs.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int sz = 15, sx2 = 14, sy2 = (h-sz)/2;
                gs.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                gs.setColor(new Color(255,255,255,40));
                gs.drawOval(sx2, sy2, sz, sz);
                gs.setColor(Color.WHITE);
                gs.rotate(Math.toRadians(spinA[0]), sx2+sz/2.0, sy2+sz/2.0);
                gs.drawArc(sx2, sy2, sz, sz, 0, 255);
                gs.rotate(-Math.toRadians(spinA[0]), sx2+sz/2.0, sy2+sz/2.0);
                gs.setFont(new Font("Segoe UI", Font.BOLD, 12));
                gs.setColor(new Color(255,255,255,210));
                FontMetrics fm = gs.getFontMetrics();
                String t2 = getText();
                gs.drawString(t2, sx2+sz+10, (h+fm.getAscent()-fm.getDescent())/2);
                gs.dispose();
            } else if (estado[0] == 2) {
                // Check
                Graphics2D gc = (Graphics2D) g.create();
                gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int r2 = 8, cx2 = 18, cy2 = h/2;
                gc.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                gc.setColor(new Color(255,255,255,160)); gc.drawOval(cx2-r2, cy2-r2, r2*2, r2*2);
                if (checkP[0] > 0) {
                    gc.setColor(Color.WHITE);
                    int x1=cx2-5,y1=cy2,x2=cx2-1,y2=cy2+4,x3=cx2+6,y3=cy2-4;
                    if (checkP[0] < 0.5f) {
                        float pp=(float)checkP[0]/0.5f;
                        gc.drawLine(x1,y1,(int)(x1+(x2-x1)*pp),(int)(y1+(y2-y1)*pp));
                    } else {
                        gc.drawLine(x1,y1,x2,y2);
                        float pp=(checkP[0]-0.5f)/0.5f;
                        gc.drawLine(x2,y2,(int)(x2+(x3-x2)*pp),(int)(y2+(y3-y2)*pp));
                    }
                }
                gc.setFont(new Font("Segoe UI", Font.BOLD, 12));
                gc.setColor(Color.WHITE);
                FontMetrics fm = gc.getFontMetrics();
                gc.drawString(getText(), cx2+r2+10, (h+fm.getAscent()-fm.getDescent())/2);
                gc.dispose();
            } else {
                super.paintComponent(g);
            }
        }
    };
    b.setFont(new Font("Segoe UI", Font.BOLD, 12));
    b.setForeground(primary ? Color.WHITE : danger ? C_RED : TXT_MID);
    b.setOpaque(false); b.setContentAreaFilled(false);
    b.setBorderPainted(false); b.setFocusPainted(false);
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    b.setBorder(new EmptyBorder(8, 18, 8, 18));

    b.addMouseListener(new MouseAdapter() {
        @Override public void mouseEntered(MouseEvent e) { animHoverBtn(hoverT, true, b, timers); }
        @Override public void mouseExited (MouseEvent e) { animHoverBtn(hoverT, false, b, timers); }
        @Override public void mousePressed(MouseEvent e) {
            ripXY[0]=e.getX(); ripXY[1]=e.getY(); ripR[0]=0f; ripA[0]=0.4f;
            Timer tr = new Timer(12, null);
            tr.addActionListener(ev -> { ripR[0]+=10f; ripA[0]-=0.03f; b.repaint(); if (ripA[0]<=0) tr.stop(); });
            timers.add(tr); tr.start();
        }
    });

    // Shimmer continuo (solo primario)
    if (primary) {
        Timer ts = new Timer(38, e -> { shimX[0]+=0.016f; if(shimX[0]>1.3f) shimX[0]=-0.3f; b.repaint(); });
        timers.add(ts); ts.start();
    }

    // Guardar arrays en el botón para acceso externo
    b.putClientProperty("estado", estado);
    b.putClientProperty("spinA",  spinA);
    b.putClientProperty("checkP", checkP);
    b.putClientProperty("timers", timers);
    return b;
}

private void animHoverBtn(float[] hoverT, boolean in, JButton b, java.util.List<Timer> timers) {
    Timer t = new Timer(16, null);
    final long ini = System.currentTimeMillis();
    final float desde = hoverT[0], hasta = in ? 1f : 0f;
    t.addActionListener(ev -> {
        float p = Math.min(1f, (System.currentTimeMillis() - ini) / 160f);
        hoverT[0] = desde + (hasta - desde) * (1f - (float)Math.pow(1-p,3));
        b.repaint();
        if (p >= 1f) t.stop();
    });
    timers.add(t); t.start();
}

@SuppressWarnings("unchecked")
private void setLoadingBtn(JButton b, String msg, java.util.List<Timer> timers) {
    int[]   estado = (int[])   b.getClientProperty("estado");
    float[] spinA  = (float[]) b.getClientProperty("spinA");
    if (estado != null) estado[0] = 1;
    b.setText(msg);
    b.setCursor(Cursor.getDefaultCursor());
    Timer ts = new Timer(20, e -> { if(spinA!=null){spinA[0]+=12f;} b.repaint(); });
    timers.add(ts); ts.start();
    b.putClientProperty("spinTimer", ts);
}

@SuppressWarnings("unchecked")
private void setSuccessBtn(JButton b, String msg, java.util.List<Timer> timers) {
    int[]   estado = (int[])   b.getClientProperty("estado");
    float[] checkP = (float[]) b.getClientProperty("checkP");
    Timer   st     = (Timer)   b.getClientProperty("spinTimer");
    if (st != null) st.stop();
    if (estado != null) estado[0] = 2;
    if (checkP != null) checkP[0] = 0f;
    b.setText(msg);
    Timer tc = new Timer(16, null);
    final long ini = System.currentTimeMillis();
    tc.addActionListener(ev -> {
        float p = Math.min(1f, (System.currentTimeMillis() - ini) / 420f);
        if (checkP != null) checkP[0] = p;
        b.repaint();
        if (p >= 1f) tc.stop();
    });
    timers.add(tc); tc.start();
}

@SuppressWarnings("unchecked")
private void resetBtn(JButton b, String msg) {
    int[] estado = (int[]) b.getClientProperty("estado");
    if (estado != null) estado[0] = 0;
    b.setText(msg);
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    b.repaint();
}

// ── Color blend helper ────────────────────────────────────────────────────────
private Color blend(Color a, Color b, float t) {
    t = Math.max(0f, Math.min(1f, t));
    return new Color(
        (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t),
        (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
        (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t));
}

    // ── Helpers de construcción ───────────────────────────────────────────────
    private JPanel buildCard() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 16, 16));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    private JTextField buildInput(String val, String placeholder) {
        JTextField f = new JTextField(val) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(TXT_MUTED);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, getInsets().left + 2, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 1);
                    g2.dispose();
                }
            }
        };
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(TXT_DARK);
        f.setBackground(BG_INPUT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_INPUT, 1, true),
            new EmptyBorder(9, 12, 9, 12)
        ));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBackground(Color.WHITE);
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_BLUE, 2, true),
                    new EmptyBorder(8, 11, 8, 11)
                ));
            }
            public void focusLost(FocusEvent e) {
                f.setBackground(BG_INPUT);
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_INPUT, 1, true),
                    new EmptyBorder(9, 12, 9, 12)
                ));
            }
        });
        return f;
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc, int row, String labelTxt, JComponent field) {
        gbc.insets = new Insets(0, 0, 4, 0);
        gbc.gridy = row * 2; gbc.gridx = 0; gbc.gridwidth = 2;
        JLabel lbl = new JLabel(labelTxt);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(0x6B7280));
        p.add(lbl, gbc);

        gbc.insets = new Insets(0, 0, 14, 0);
        gbc.gridy = row * 2 + 1;
        p.add(field, gbc);
    }

    private JToggleButton buildTipoBtn(String texto, Color accent, Color bg) {
        JToggleButton btn = new JToggleButton("● " + texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? new Color(bg.getRed(), bg.getGreen(), bg.getBlue()) : new Color(0xF9FAFB));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 11, 11));
                g2.setColor(isSelected() ? accent : BORDER_INPUT);
                g2.setStroke(new BasicStroke(isSelected() ? 1.8f : 1f));
                g2.draw(new RoundRectangle2D.Float(0.9f, 0.9f, getWidth()-1.8f, getHeight()-1.8f, 11, 11));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TXT_MID);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 40));
        btn.addChangeListener(e -> {
            btn.setForeground(btn.isSelected() ? accent.darker() : TXT_MID);
            btn.repaint();
        });
        return btn;
    }

    private JButton buildBtn(String texto, boolean primary, boolean danger, ActionListener al) {
        JButton b = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primary) {
                    GradientPaint gp = new GradientPaint(0, 0, C_BLUE, getWidth(), 0, C_PURPLE);
                    g2.setPaint(gp);
                } else if (danger) {
                    g2.setColor(getModel().isRollover() ? new Color(0xFEF2F2) : BG_CARD);
                } else {
                    g2.setColor(getModel().isRollover() ? new Color(0xF8FAFF) : BG_CARD);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 9, 9));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(primary ? Color.WHITE : danger ? C_RED : TXT_MID);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(true);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(primary ? 200 : 110, 36));
        if (!primary) {
            b.setBorder(BorderFactory.createLineBorder(danger ? new Color(0xFECACA) : BORDER, 1, true));
        } else {
            b.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        }
        b.addActionListener(al);
        return b;
    }

    private void toast(String msg, boolean success) {
        MainFrame.showToast(msg, success ? MainFrame.ToastType.SUCCESS : MainFrame.ToastType.INFO);
    }
}