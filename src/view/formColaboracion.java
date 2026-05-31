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

            String artista = c.getColaboracionArtista() != null ? c.getColaboracionArtista() : "—";
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
        if (c == null || c.getColaboracionArtista() == null) return 3;
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
        detAvatar.setText(iniciales(c.getColaboracionArtista()));
        detAvatar.setBackground(TIPO_BG[tipoIdx]);
        detAvatar.setForeground(TIPO_FG[tipoIdx]);
        detNombre.setText(c.getColaboracionArtista() != null ? c.getColaboracionArtista() : "—");
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
            .map(Colaboracion::getColaboracionArtista)
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
            "¿Eliminar la colaboración de " + s.getColaboracionArtista() + "?",
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
    private void openForm(Colaboracion c) {
        boolean isEdit = c != null;
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Editar colaboración" : "Nueva colaboración", true);
        dlg.setUndecorated(false);

        // ── Contenedor principal del diálogo
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_CARD);

        // ── Header con degradado
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, C_BLUE, getWidth(), 0, C_PURPLE);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(new EmptyBorder(0, 20, 0, 16));

        JLabel hTitle = new JLabel((isEdit ? "✎  Editar" : "＋  Nueva") + " colaboración");
        hTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        hTitle.setForeground(Color.WHITE);

        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(new Color(255, 255, 255, 40));
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setPreferredSize(new Dimension(30, 30));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dlg.dispose());

        header.add(hTitle,   BorderLayout.WEST);
        header.add(closeBtn, BorderLayout.EAST);

        // ── Body del formulario
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(BG_CARD);
        body.setBorder(new EmptyBorder(22, 24, 10, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 14, 0);

        JTextField fArt   = buildInput(isEdit && c.getColaboracionArtista() != null ? c.getColaboracionArtista() : "", "Ej. J Balvin");
        JTextField fCan   = buildInput(isEdit && c.getIdCancion() != null ? String.valueOf(c.getIdCancion()) : "", "Ej. 3");
        JTextField fFecha = buildInput(isEdit && c.getFechaColaboracion() != null
            ? c.getFechaColaboracion().format(FMT) : LocalDate.now().format(FMT), "dd/mm/aaaa");

        int row = 0;
        addFormRow(body, gbc, row++, "ARTISTA COLABORADOR  *", fArt);
        addFormRow(body, gbc, row++, "ID CANCIÓN  *",          fCan);
        addFormRow(body, gbc, row++, "FECHA DE COLABORACIÓN  *", fFecha);

        // Selector de tipo
        gbc.gridy = row++;
        gbc.gridx = 0; gbc.gridwidth = 2;
        JLabel tipoLbl = new JLabel("TIPO DE COLABORACIÓN");
        tipoLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tipoLbl.setForeground(new Color(0x6B7280));
        body.add(tipoLbl, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 18, 0);
        JPanel tipoGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        tipoGrid.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        JToggleButton[] tipoBtns = new JToggleButton[4];
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            tipoBtns[i] = buildTipoBtn(TIPO_NOMBRES[i], TIPO_ACCENT[i], TIPO_BG[i]);
            bg.add(tipoBtns[i]);
            tipoGrid.add(tipoBtns[i]);
        }
        tipoBtns[0].setSelected(true);
        body.add(tipoGrid, gbc);

        // ── Footer con botones
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        footer.setBackground(BG_CARD);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER));

        JButton bCancel = buildBtn("Cancelar", false, false, e -> dlg.dispose());
        JButton bSave   = buildBtn(isEdit ? "💾  Guardar cambios" : "✦  Crear colaboración", true, false, e -> {
            try {
                if (fArt.getText().isBlank()) throw new IllegalArgumentException("El artista es obligatorio");
                if (fCan.getText().isBlank()) throw new IllegalArgumentException("El ID de canción es obligatorio");

                Colaboracion n = isEdit ? c : new Colaboracion();
                n.setColaboracionArtista(fArt.getText().trim());
                n.setIdCancion(Integer.parseInt(fCan.getText().trim()));
                n.setFechaColaboracion(LocalDate.parse(fFecha.getText().trim(), FMT));

                // Tipo seleccionado
                for (int i = 0; i < tipoBtns.length; i++) {
                    if (tipoBtns[i].isSelected()) {
                        // n.setTipo(TIPO_NOMBRES[i]); // descomenta si tu modelo tiene campo tipo
                        break;
                    }
                }

                if (isEdit) servicio.actualizar(n);
                else        servicio.crear(n);

                MainFrame.showToast(isEdit ? "Colaboración actualizada" : "Colaboración creada", MainFrame.ToastType.SUCCESS);
                recargar();
                dlg.dispose();
            } catch (Exception ex) {
                MainFrame.showToast(ex.getMessage(), MainFrame.ToastType.ERROR);
            }
        });

        footer.add(bCancel);
        footer.add(bSave);

        root.add(header, BorderLayout.NORTH);
        root.add(body,   BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        dlg.setContentPane(root);
        dlg.setSize(480, 490);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
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