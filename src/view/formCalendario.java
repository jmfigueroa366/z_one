package view;

import services.CalendarioService;
import services.CalendarioService.ItemCalendario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Calendario semanal con vista por hora.
 * Muestra sesiones (azul), eventos (ambar) y colaboraciones (rosa).
 */
public class formCalendario extends JPanel {

    // ── PALETA ──
    private static final Color BG_DEEP   = new Color(0x04111F);
    private static final Color BG_CARD   = new Color(0x061829);
    private static final Color BG_HOUR   = new Color(0x071E30);
    private static final Color BG_TODAY  = new Color(0x0D2A4D);
    private static final Color C_PRIMARY = new Color(0x1A6EBE);
    private static final Color C_CYAN    = new Color(0x00BCD4);
    private static final Color C_AMBER   = new Color(0xFFA726);
    private static final Color C_PINK    = new Color(0xEC4899);
    private static final Color C_BLUE    = new Color(0x42A5F5);
    private static final Color TXT_PRI   = new Color(0xE8EFF7);
    private static final Color TXT_SEC   = new Color(0x6B89A8);
    private static final Color COL_BRD   = new Color(0x0D2A45);

    private static final DateTimeFormatter FMT_DIA    = DateTimeFormatter.ofPattern("d");
    private static final DateTimeFormatter FMT_RANGO  = DateTimeFormatter.ofPattern("d 'de' MMMM", new Locale("es", "ES"));

    // Horario laboral
    private static final int HORA_INICIO = 7;   // 7am
    private static final int HORA_FIN    = 22;  // 10pm
    private static final int ALTO_HORA   = 50;  // pixeles por hora

    // ── ESTADO ──
    private LocalDate lunesActual;
    private List<ItemCalendario> items;
    private final CalendarioService servicio = new CalendarioService();

    // ── UI ──
    private JLabel lblRango;
    private JPanel cuerpo;

    public formCalendario() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Inicializa en la semana actual (lunes)
        lunesActual = LocalDate.now().with(DayOfWeek.MONDAY);

        construirUI();
        recargar();
    }

    private void construirUI() {
        add(headerPanel(), BorderLayout.NORTH);

        cuerpo = new JPanel(new BorderLayout());
        cuerpo.setOpaque(false);
        add(cuerpo, BorderLayout.CENTER);

        add(leyenda(), BorderLayout.SOUTH);
    }

    // ── HEADER ──
    private JPanel headerPanel() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);

        // Izquierda: título + rango
        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel tit = new JLabel("📅  Calendario semanal");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tit.setForeground(TXT_PRI);
        tit.setAlignmentX(LEFT_ALIGNMENT);

        lblRango = new JLabel("—");
        lblRango.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRango.setForeground(C_CYAN);
        lblRango.setAlignmentX(LEFT_ALIGNMENT);

        izq.add(tit);
        izq.add(Box.createVerticalStrut(4));
        izq.add(lblRango);

        // Derecha: navegación
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        der.setOpaque(false);
        der.add(btn("‹ Anterior", false, e -> {
            lunesActual = lunesActual.minusWeeks(1);
            recargar();
        }));
        der.add(btn("Hoy", true, e -> {
            lunesActual = LocalDate.now().with(DayOfWeek.MONDAY);
            recargar();
        }));
        der.add(btn("Siguiente ›", false, e -> {
            lunesActual = lunesActual.plusWeeks(1);
            recargar();
        }));
        der.add(btn("↺ Refrescar", false, e -> recargar()));

        p.add(izq, BorderLayout.WEST);
        p.add(der, BorderLayout.EAST);
        return p;
    }

    // ── CALENDARIO PRINCIPAL ──
    private JComponent calendarioGrid() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(COL_BRD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Header con días
        JPanel cabeceraDias = new JPanel(new GridLayout(1, 8, 1, 0));
        cabeceraDias.setOpaque(false);
        cabeceraDias.add(headerColumnaHoras());
        for (int i = 0; i < 7; i++) {
            cabeceraDias.add(headerDia(lunesActual.plusDays(i)));
        }

        // Grid de horas
        JPanel grid = new JPanel(new GridLayout(1, 8, 1, 0));
        grid.setOpaque(false);
        grid.add(columnaHoras());
        for (int i = 0; i < 7; i++) {
            grid.add(columnaDia(lunesActual.plusDays(i)));
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        card.add(cabeceraDias, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JComponent headerColumnaHoras() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(56, 38));
        return p;
    }

    private JComponent headerDia(LocalDate fecha) {
        boolean esHoy = fecha.equals(LocalDate.now());
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(esHoy ? BG_TODAY : BG_HOUR);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                if (esHoy) {
                    g2.setColor(C_CYAN);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 8, 8);
                }
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(0, 38));

        String nomDia = fecha.getDayOfWeek().getDisplayName(TextStyle.SHORT,
                new Locale("es", "ES")).toUpperCase();
        JLabel lblDia = new JLabel(nomDia, SwingConstants.CENTER);
        lblDia.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblDia.setForeground(esHoy ? C_CYAN : TXT_SEC);
        lblDia.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblNum = new JLabel(fecha.format(FMT_DIA), SwingConstants.CENTER);
        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNum.setForeground(esHoy ? Color.WHITE : TXT_PRI);
        lblNum.setAlignmentX(CENTER_ALIGNMENT);

        p.add(Box.createVerticalStrut(4));
        p.add(lblDia);
        p.add(lblNum);
        return p;
    }

    private JComponent columnaHoras() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(56, (HORA_FIN - HORA_INICIO) * ALTO_HORA));

        for (int h = HORA_INICIO; h < HORA_FIN; h++) {
            JLabel l = new JLabel(String.format("%02d:00", h));
            l.setFont(new Font("Consolas", Font.PLAIN, 10));
            l.setForeground(TXT_SEC);
            l.setBorder(new EmptyBorder(2, 8, 0, 0));
            l.setAlignmentX(LEFT_ALIGNMENT);
            l.setPreferredSize(new Dimension(56, ALTO_HORA));
            l.setMaximumSize(new Dimension(56, ALTO_HORA));
            p.add(l);
        }
        return p;
    }

    private JComponent columnaDia(LocalDate fecha) {
        boolean esHoy = fecha.equals(LocalDate.now());

        JPanel p = new JPanel(null) {  // null layout para posicionar items absolutos
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(esHoy ? new Color(BG_TODAY.getRed(), BG_TODAY.getGreen(),
                        BG_TODAY.getBlue(), 80) : BG_HOUR);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Lineas horarias
                g2.setColor(COL_BRD);
                for (int h = 0; h <= (HORA_FIN - HORA_INICIO); h++) {
                    int y = h * ALTO_HORA;
                    g2.drawLine(0, y, getWidth(), y);
                }

                // Linea de "ahora" si es hoy
                if (esHoy) {
                    java.time.LocalTime ahora = java.time.LocalTime.now();
                    if (ahora.getHour() >= HORA_INICIO && ahora.getHour() < HORA_FIN) {
                        int y = (ahora.getHour() - HORA_INICIO) * ALTO_HORA
                                + (ahora.getMinute() * ALTO_HORA / 60);
                        g2.setColor(C_AMBER);
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.fillOval(-4, y - 4, 8, 8);
                        g2.drawLine(0, y, getWidth(), y);
                    }
                }
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(0, (HORA_FIN - HORA_INICIO) * ALTO_HORA));

        // Items del día (sesiones/eventos/colaboraciones)
        if (items != null) {
            List<ItemCalendario> delDia = items.stream()
                    .filter(it -> it.fecha.equals(fecha))
                    .collect(Collectors.toList());
            int colWidth = 130;  // se ajusta dinámicamente al render
            for (int i = 0; i < delDia.size(); i++) {
                ItemCalendario it = delDia.get(i);
                JComponent comp = bloqueItem(it);
                int y = (it.horaInicio.getHour() - HORA_INICIO) * ALTO_HORA
                        + (it.horaInicio.getMinute() * ALTO_HORA / 60);
                int dur = (it.horaFin.getHour() - it.horaInicio.getHour()) * 60
                        + (it.horaFin.getMinute() - it.horaInicio.getMinute());
                int h = Math.max(28, dur * ALTO_HORA / 60);
                comp.setBounds(4 + i * 2, y, colWidth, h);
                p.add(comp);
                p.setComponentZOrder(comp, 0);
            }
        }
        return p;
    }

    private JComponent bloqueItem(ItemCalendario it) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                Color c = it.color;
                // Fondo con transparencia
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                // Borde izquierdo grueso del color
                g2.setColor(c);
                g2.fillRoundRect(0, 0, 4, getHeight() - 1, 4, 4);
                // Borde general
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 150));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 8, 8);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(4, 8, 4, 6));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));

        JLabel tit = new JLabel(truncar(it.titulo, 18));
        tit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tit.setForeground(Color.WHITE);
        tit.setAlignmentX(LEFT_ALIGNMENT);

        JLabel hora = new JLabel(it.horaInicio + " - " + it.horaFin);
        hora.setFont(new Font("Consolas", Font.PLAIN, 9));
        hora.setForeground(new Color(255, 255, 255, 200));
        hora.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel(truncar(it.subtitulo, 22));
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        sub.setForeground(new Color(255, 255, 255, 180));
        sub.setAlignmentX(LEFT_ALIGNMENT);

        txt.add(tit);
        txt.add(hora);
        if (it.subtitulo != null && !it.subtitulo.isBlank()) txt.add(sub);
        p.add(txt, BorderLayout.NORTH);

        // Tooltip al hover con info completa
        p.setToolTipText("<html><b>" + it.tipo + "</b><br>"
                + it.titulo + "<br>"
                + it.horaInicio + " - " + it.horaFin + "<br>"
                + (it.subtitulo != null ? it.subtitulo : "") + "</html>");

        p.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                mostrarDetalle(it);
            }
        });
        return p;
    }

    // ── LEYENDA ──
    private JPanel leyenda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 4));
        p.setOpaque(false);
        p.add(chipLeyenda("Sesiones", C_BLUE));
        p.add(chipLeyenda("Eventos", C_AMBER));
        p.add(chipLeyenda("Colaboraciones", C_PINK));

        // Resumen de la semana
        if (items != null) {
            long ses = items.stream().filter(i -> "SESION".equals(i.tipo)).count();
            long ev  = items.stream().filter(i -> "EVENTO".equals(i.tipo)).count();
            long col = items.stream().filter(i -> "COLABORACION".equals(i.tipo)).count();

            JLabel resumen = new JLabel("  ·  Total: " + items.size()
                    + " (" + ses + " sesiones · " + ev + " eventos · " + col + " colaboraciones)");
            resumen.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            resumen.setForeground(TXT_SEC);
            p.add(resumen);
        }
        return p;
    }

    private JComponent chipLeyenda(String texto, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JPanel cuadrito = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(color);
                g2.fillRoundRect(0, 2, 14, 14, 4, 4);
                g2.dispose();
            }
        };
        cuadrito.setOpaque(false);
        cuadrito.setPreferredSize(new Dimension(14, 18));
        p.add(cuadrito);
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(TXT_PRI);
        p.add(l);
        return p;
    }

    // ── DETALLE AL CLICK ──
    private void mostrarDetalle(ItemCalendario it) {
        String msg = "<html>"
                + "<b style='font-size:14'>" + it.titulo + "</b><br><br>"
                + "<b>Tipo:</b> " + it.tipo + "<br>"
                + "<b>Fecha:</b> " + it.fecha + "<br>"
                + "<b>Hora:</b> " + it.horaInicio + " - " + it.horaFin + "<br>"
                + (it.subtitulo != null ? "<b>Detalle:</b> " + it.subtitulo : "")
                + "</html>";
        JOptionPane.showMessageDialog(this, msg, "Z-One — Detalle",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ── CARGA ──
    private void recargar() {
        try {
            items = servicio.itemsDeSemana(lunesActual);
        } catch (Exception ex) {
            ex.printStackTrace();
            MainFrame.showToast("Error al cargar calendario", MainFrame.ToastType.ERROR);
        }

        LocalDate domingo = lunesActual.plusDays(6);
        lblRango.setText(lunesActual.format(FMT_RANGO).toUpperCase()
                + "  →  " + domingo.format(FMT_RANGO).toUpperCase());

        cuerpo.removeAll();
        cuerpo.add(calendarioGrid(), BorderLayout.CENTER);
        cuerpo.revalidate();
        cuerpo.repaint();

        // Refresca leyenda
        removeAll();
        construirUI();
        add(headerPanel(), BorderLayout.NORTH);
        cuerpo = new JPanel(new BorderLayout());
        cuerpo.setOpaque(false);
        cuerpo.add(calendarioGrid(), BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);
        add(leyenda(), BorderLayout.SOUTH);

        LocalDate dom2 = lunesActual.plusDays(6);
        lblRango.setText(lunesActual.format(FMT_RANGO).toUpperCase()
                + "  →  " + dom2.format(FMT_RANGO).toUpperCase());

        revalidate();
        repaint();
    }

    // ── HELPERS ──
    private JButton btn(String txt, boolean primary, java.awt.event.ActionListener a) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setForeground(primary ? Color.WHITE : TXT_PRI);
        b.setBackground(primary ? C_PRIMARY : new Color(0x0A1F36));
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(6, 14, 6, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(a);
        return b;
    }

    private String truncar(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }

    private static Graphics2D g2d(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }
}