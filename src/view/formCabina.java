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
    //  DIÁLOGO CREAR / EDITAR
    // ═════════════════════════════════════════════════════════
  // ═════════════════════════════════════════════════════════
//  DIÁLOGO CREAR / EDITAR  —  versión rediseñada
// ═════════════════════════════════════════════════════════
private void openForm(Cabina c) {
    boolean isEdit = (c != null);

    JDialog dlg = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Editar cabina" : "Nueva cabina", true);

    // ── Root ─────────────────────────────────────────────
    JPanel root = new JPanel(new BorderLayout()) {
        @Override protected void paintComponent(Graphics g) {
            g.setColor(CabinaStyles.BG_MAIN);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    };

    // ══════════════════════════════════════════════════════
    //  HEADER: ícono + título + subtítulo
    // ══════════════════════════════════════════════════════
    JPanel header = new JPanel(new BorderLayout(14, 0)) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CabinaStyles.BG_CARD);
            g2.fillRect(0, 0, getWidth(), getHeight());
            // franja índigo arriba
            g2.setColor(CabinaStyles.INDIGO);
            g2.fillRect(0, 0, getWidth(), 3);
            // borde inferior
            g2.setColor(CabinaStyles.BORDER);
            g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            g2.dispose();
        }
    };
    header.setOpaque(false);
    header.setBorder(new EmptyBorder(16, 22, 16, 22));

    // Círculo con ícono
    JPanel iconCircle = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(CabinaStyles.INDIGO.getRed(),
                                  CabinaStyles.INDIGO.getGreen(),
                                  CabinaStyles.INDIGO.getBlue(), 28));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
        }
    };
    iconCircle.setOpaque(false);
    iconCircle.setPreferredSize(new Dimension(42, 42));
    iconCircle.setLayout(new GridBagLayout());
    JLabel iconLbl = new JLabel("🎙");
    iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
    iconCircle.add(iconLbl);

    // Textos del header
    JPanel titleCol = new JPanel();
    titleCol.setOpaque(false);
    titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));

    JLabel titLbl = CabinaStyles.lbl(
            isEdit ? "Editar cabina" : "Nueva cabina",
            15, true, CabinaStyles.TEXT_PRI);
    JLabel subLbl = CabinaStyles.lbl(
            isEdit ? "Modifica los datos del estudio"
                   : "Agrega un nuevo estudio al sistema",
            11, false, CabinaStyles.TEXT_SEC);
    titLbl.setAlignmentX(LEFT_ALIGNMENT);
    subLbl.setAlignmentX(LEFT_ALIGNMENT);
    titleCol.add(titLbl);
    titleCol.add(Box.createVerticalStrut(2));
    titleCol.add(subLbl);

    header.add(iconCircle, BorderLayout.WEST);
    header.add(titleCol,   BorderLayout.CENTER);
    root.add(header, BorderLayout.NORTH);

    // ══════════════════════════════════════════════════════
    //  BODY
    // ══════════════════════════════════════════════════════
    JPanel body = new JPanel();
    body.setOpaque(false);
    body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
    body.setBorder(new EmptyBorder(20, 24, 16, 24));

    // ── Campo Nombre ──────────────────────────────────────
    JLabel lblNombre = CabinaStyles.lbl("NOMBRE DE CABINA  *", 9, true, CabinaStyles.INDIGO);
    lblNombre.setAlignmentX(LEFT_ALIGNMENT);
    body.add(lblNombre);
    body.add(Box.createVerticalStrut(6));

    CampoElegante fNombre = new CampoElegante("Ej: Cabina Norte A", CabinaStyles.INDIGO);
    fNombre.setAlignmentX(LEFT_ALIGNMENT);
    fNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    if (isEdit) fNombre.setText(c.getNombreCabina());
    body.add(fNombre);
    body.add(Box.createVerticalStrut(16));

    // ── Campo Estado ──────────────────────────────────────
    JLabel lblEstado = CabinaStyles.lbl("ESTADO", 9, true, CabinaStyles.INDIGO);
    lblEstado.setAlignmentX(LEFT_ALIGNMENT);
    body.add(lblEstado);
    body.add(Box.createVerticalStrut(6));

    String[] estados = {"Disponible", "Ocupada", "Mantenimiento", "Reservada"};
    JComboBox<String> cbEstado = new JComboBox<>(estados);
    cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    cbEstado.setForeground(CabinaStyles.TEXT_PRI);
    cbEstado.setBackground(CabinaStyles.BG_FIELD);
    cbEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    cbEstado.setAlignmentX(LEFT_ALIGNMENT);
    if (isEdit && c.getNombreEstado() != null)
        cbEstado.setSelectedItem(c.getNombreEstado());
    body.add(cbEstado);
    body.add(Box.createVerticalStrut(8));

    // ── Badge de estado (dinámico) ─────────────────────────
    JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    badgePanel.setOpaque(false);
    badgePanel.setAlignmentX(LEFT_ALIGNMENT);
    badgePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

    JLabel badgeLbl = new JLabel();
    badgeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    badgeLbl.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(20, CabinaStyles.colorEstado(
                    (String) cbEstado.getSelectedItem())),
            new EmptyBorder(3, 10, 3, 10)));
    badgeLbl.setOpaque(false);

    // Actualiza el badge según estado seleccionado
    Runnable refreshBadge = () -> {
        String est = (String) cbEstado.getSelectedItem();
        Color col = CabinaStyles.colorEstado(est);
        String desc = CabinaStyles.descEstado(est);
        badgeLbl.setText("● " + est + "  —  " + desc);
        badgeLbl.setForeground(col);
        badgeLbl.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(20, new Color(col.getRed(),
                                             col.getGreen(),
                                             col.getBlue(), 60)),
                new EmptyBorder(3, 10, 3, 10)));
    };
    refreshBadge.run();
    cbEstado.addActionListener(e -> refreshBadge.run());

    badgePanel.add(badgeLbl);
    body.add(badgePanel);

    root.add(body, BorderLayout.CENTER);

    // ══════════════════════════════════════════════════════
    //  FOOTER: botones
    // ══════════════════════════════════════════════════════
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

    BtnCabina bCancel = CabinaStyles.btnAccion("Cancelar",
            false, 120, CabinaStyles.BORDER_STRONG);
    BtnCabina bSave   = CabinaStyles.btnAccion(
            isEdit ? "💾  Guardar" : "＋  Crear cabina", true, 160);

    bCancel.addActionListener(e -> dlg.dispose());
    bSave.addActionListener(e -> {
        try {
            if (fNombre.getText().isBlank())
                throw new IllegalArgumentException("El nombre es obligatorio");
            Cabina n = isEdit ? c : new Cabina();
            n.setNombreCabina(fNombre.getText().trim());
            n.setNombreEstado((String) cbEstado.getSelectedItem());
            if (isEdit) servicio.actualizar(n);
            else        servicio.crear(n);
            toast(isEdit ? "Cabina actualizada" : "Cabina creada",
                  MainFrame.ToastType.SUCCESS);
            recargar();
            dlg.dispose();
        } catch (Exception ex) {
            toast(ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    });

    btnRow.add(bCancel);
    btnRow.add(bSave);
    footer.add(btnRow, BorderLayout.EAST);
    root.add(footer, BorderLayout.SOUTH);

    // ── Mostrar ───────────────────────────────────────────
    dlg.setContentPane(root);
    dlg.setSize(460, 340);
    dlg.setLocationRelativeTo(this);
    dlg.setResizable(false);
    dlg.setVisible(true);
}


// ─────────────────────────────────────────────────────────
//  Clase auxiliar RoundBorder  (agrégala al final del archivo
//  o como clase interna estática)
// ─────────────────────────────────────────────────────────
private static class RoundBorder implements javax.swing.border.Border {
    private final int   radius;
    private final Color color;

    RoundBorder(int radius, Color color) {
        this.radius = radius;
        this.color  = color;
    }

    @Override
    public void paintBorder(Component c, Graphics g,
                            int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
        g2.dispose();
    }

    @Override public Insets getBorderInsets(Component c) { return new Insets(2,2,2,2); }
    @Override public boolean isBorderOpaque()            { return false; }
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