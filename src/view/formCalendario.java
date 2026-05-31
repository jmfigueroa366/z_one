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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class formCalendario extends JPanel {

    // ── PALETA CLARA ──────────────────────────────────────────────────
    private static final Color BG_PAGE    = new Color(247, 248, 252);
    private static final Color BG_CARD    = new Color(255, 255, 255);
    private static final Color BG_TODAY   = new Color(235, 232, 255);
    private static final Color BG_HOY_HDR = new Color(108,  99, 255);
    private static final Color COL_BRD    = new Color(226, 228, 235);
    private static final Color COL_GRID   = new Color(238, 240, 245);
    private static final Color TXT_PRI    = new Color( 26,  29,  46);
    private static final Color TXT_SEC    = new Color(120, 128, 148);
    private static final Color PURPLE     = new Color(108,  99, 255);
    private static final Color PURPLE_LT  = new Color( 91,  82, 212);

    // Colores de categoría
    private static final Color C_SESION   = new Color( 66, 165, 245);   // azul
    private static final Color C_EVENTO   = new Color(251, 192,  45);   // ámbar
    private static final Color C_COLAB    = new Color(236,  72, 153);   // rosa
    private static final Color C_LANZ     = new Color( 52, 199, 142);   // verde
    private static final Color C_CONC     = new Color(251, 146,  60);   // naranja

    private static final DateTimeFormatter FMT_RANGO =
            DateTimeFormatter.ofPattern("d 'de' MMMM", new Locale("es", "ES"));

    private static final int HORA_INICIO = 7;
    private static final int HORA_FIN    = 22;
    private static final int ALTO_HORA   = 56;   // px por hora
    private static final int ANCHO_HORAS = 60;   // columna de horas

    // ── ESTADO ────────────────────────────────────────────────────────
    private LocalDate        lunesActual;
    private List<ItemCalendario> items;
    private final CalendarioService servicio = new CalendarioService();

    // ── UI ────────────────────────────────────────────────────────────
    private JLabel  lblRango;
    private JPanel  cuerpo;

    // ── ANIMACIÓN fade-in ─────────────────────────────────────────────
    private float alpha = 0f;
    private javax.swing.Timer timerFade;

    public formCalendario() {
        setOpaque(false);
        setBackground(BG_PAGE);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        lunesActual = LocalDate.now().with(DayOfWeek.MONDAY);
        construirUI();
        recargar();
        iniciarFade();
    }

    private void iniciarFade() {
        alpha = 0f;
        timerFade = new javax.swing.Timer(16, null);
        timerFade.addActionListener(e -> {
            alpha = Math.min(1f, alpha + 0.05f);
            repaint();
            if (alpha >= 1f) timerFade.stop();
        });
        timerFade.start();
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(BG_PAGE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }

    // ══════════════════════════════════════════════════════════════════
    //  UI PRINCIPAL
    // ══════════════════════════════════════════════════════════════════
    private void construirUI() {
        removeAll();
        add(headerPanel(), BorderLayout.NORTH);
        cuerpo = new JPanel(new BorderLayout());
        cuerpo.setOpaque(false);
        add(cuerpo, BorderLayout.CENTER);
        add(leyenda(), BorderLayout.SOUTH);
    }

    // ── HEADER ───────────────────────────────────────────────────────
    private JPanel headerPanel() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);

        // Izquierda: icono + título + rango
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setOpaque(false);

        // Caja icono morada
        JPanel icoCaja = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(108, 99, 255, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(108, 99, 255, 70));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icoCaja.setOpaque(false);
        icoCaja.setPreferredSize(new Dimension(40, 40));
        JLabel icoLbl = new JLabel("📅", SwingConstants.CENTER);
        icoLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
        icoCaja.add(icoLbl, BorderLayout.CENTER);

        JPanel titStack = new JPanel();
        titStack.setOpaque(false);
        titStack.setLayout(new BoxLayout(titStack, BoxLayout.Y_AXIS));
        JLabel tit = new JLabel("Calendario semanal");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 22));
        tit.setForeground(TXT_PRI);
        tit.setAlignmentX(LEFT_ALIGNMENT);
        lblRango = new JLabel("—");
        lblRango.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRango.setForeground(PURPLE);
        lblRango.setAlignmentX(LEFT_ALIGNMENT);
        titStack.add(tit);
        titStack.add(Box.createVerticalStrut(2));
        titStack.add(lblRango);

        izq.add(icoCaja);
        izq.add(titStack);

        // Derecha: botones navegación
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        der.setOpaque(false);
        der.add(navBtn("‹ Anterior", false, e -> { lunesActual = lunesActual.minusWeeks(1); recargar(); }));
        der.add(navBtn("  Hoy  ",   true,  e -> { lunesActual = LocalDate.now().with(DayOfWeek.MONDAY); recargar(); }));
        der.add(navBtn("Siguiente ›", false, e -> { lunesActual = lunesActual.plusWeeks(1); recargar(); }));
        der.add(navBtn("↺ Refrescar", false, e -> recargar()));

        p.add(izq, BorderLayout.WEST);
        p.add(der, BorderLayout.EAST);
        return p;
    }

    // ── GRID PRINCIPAL ────────────────────────────────────────────────
    private JComponent calendarioGrid() {
        // Card contenedora con sombra suave
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                // Sombra
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(3, 4, getWidth()-3, getHeight()-3, 14, 14);
                // Fondo blanco
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                // Borde
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        // ── Cabecera con días ───────────────────────────────────────
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COL_BRD));

        // Celda vacía sobre la columna de horas
        JPanel celdaVacia = new JPanel();
        celdaVacia.setOpaque(false);
        celdaVacia.setPreferredSize(new Dimension(ANCHO_HORAS, 52));

        JPanel diasHdr = new JPanel(new GridLayout(1, 7, 1, 0));
        diasHdr.setOpaque(false);
        diasHdr.setBorder(new EmptyBorder(0, 0, 0, 0));
        for (int i = 0; i < 7; i++) {
            diasHdr.add(headerDia(lunesActual.plusDays(i)));
        }

        cabecera.add(celdaVacia, BorderLayout.WEST);
        cabecera.add(diasHdr,   BorderLayout.CENTER);

        // ── Cuerpo scrollable ───────────────────────────────────────
        JPanel gridBody = new JPanel(new BorderLayout());
        gridBody.setOpaque(false);

        // Columna de horas (fija a la izquierda)
        JPanel colHoras = columnaHoras();

        // Columnas de días
        JPanel colsDias = new JPanel(new GridLayout(1, 7, 1, 0));
        colsDias.setOpaque(true);
        colsDias.setBackground(BG_CARD);
        colsDias.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, COL_BRD));
        for (int i = 0; i < 7; i++) {
            colsDias.add(columnaDia(lunesActual.plusDays(i)));
        }

        // Separador vertical entre horas y días
        JPanel sep = new JPanel();
        sep.setOpaque(true);
        sep.setBackground(COL_BRD);
        sep.setPreferredSize(new Dimension(1, 0));

        gridBody.add(colHoras, BorderLayout.WEST);
        gridBody.add(sep,      BorderLayout.AFTER_LAST_LINE);
        gridBody.add(colsDias, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(gridBody);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));

        // Scroll inicial a las 8am
        SwingUtilities.invokeLater(() -> {
            int scrollY = (8 - HORA_INICIO) * ALTO_HORA;
            scroll.getVerticalScrollBar().setValue(scrollY);
        });

        card.add(cabecera, BorderLayout.NORTH);
        card.add(scroll,   BorderLayout.CENTER);
        return card;
    }

    // ── HEADER DÍA ───────────────────────────────────────────────────
    private JComponent headerDia(LocalDate fecha) {
        boolean esHoy = fecha.equals(LocalDate.now());

        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(0, 52));
        p.setBorder(new EmptyBorder(8, 0, 8, 0));

        String nomDia = fecha.getDayOfWeek()
                .getDisplayName(TextStyle.SHORT, new Locale("es", "ES"))
                .toUpperCase();

        JLabel lblNom = new JLabel(nomDia, SwingConstants.CENTER);
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblNom.setForeground(esHoy ? PURPLE : TXT_SEC);
        lblNom.setAlignmentX(CENTER_ALIGNMENT);

        // Número del día — círculo relleno si es hoy
        JLabel lblNum = new JLabel(String.valueOf(fecha.getDayOfMonth()), SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                if (esHoy) {
                    Graphics2D g2 = g2d(g);
                    int d = Math.min(getWidth(), getHeight()) - 4;
                    int x = (getWidth()-d)/2, y = (getHeight()-d)/2;
                    g2.setColor(BG_HOY_HDR);
                    g2.fillOval(x, y, d, d);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNum.setForeground(esHoy ? Color.WHITE : TXT_PRI);
        lblNum.setAlignmentX(CENTER_ALIGNMENT);
        lblNum.setPreferredSize(new Dimension(32, 32));
        lblNum.setMaximumSize(new Dimension(32, 32));
        lblNum.setHorizontalAlignment(SwingConstants.CENTER);

        p.add(lblNom);
        p.add(Box.createVerticalStrut(2));
        p.add(lblNum);
        return p;
    }

    // ── COLUMNA DE HORAS ─────────────────────────────────────────────
    private JPanel columnaHoras() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(ANCHO_HORAS, (HORA_FIN - HORA_INICIO) * ALTO_HORA));

        for (int h = HORA_INICIO; h < HORA_FIN; h++) {
            JPanel celda = new JPanel(new BorderLayout());
            celda.setOpaque(false);
            celda.setPreferredSize(new Dimension(ANCHO_HORAS, ALTO_HORA));
            celda.setMaximumSize(new Dimension(ANCHO_HORAS, ALTO_HORA));
            celda.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COL_GRID));

            JLabel lbl = new JLabel(String.format("%02d:00", h));
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lbl.setForeground(TXT_SEC);
            lbl.setBorder(new EmptyBorder(4, 10, 0, 8));
            celda.add(lbl, BorderLayout.NORTH);
            p.add(celda);
        }
        return p;
    }

    // ── COLUMNA DÍA ──────────────────────────────────────────────────
    private JComponent columnaDia(LocalDate fecha) {
        boolean esHoy = fecha.equals(LocalDate.now());

        JPanel p = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);

                // Fondo
                g2.setColor(esHoy ? new Color(245, 243, 255) : BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Líneas de hora
                g2.setColor(COL_GRID);
                g2.setStroke(new BasicStroke(1f));
                for (int h = 0; h <= (HORA_FIN - HORA_INICIO); h++) {
                    int y = h * ALTO_HORA;
                    g2.drawLine(0, y, getWidth(), y);
                }

                // Separador izquierdo
                g2.setColor(COL_BRD);
                g2.drawLine(0, 0, 0, getHeight());

                // Línea "ahora"
                if (esHoy) {
                    LocalTime ahora = LocalTime.now();
                    if (ahora.getHour() >= HORA_INICIO && ahora.getHour() < HORA_FIN) {
                        int y = (ahora.getHour() - HORA_INICIO) * ALTO_HORA
                              + (ahora.getMinute() * ALTO_HORA / 60);
                        g2.setColor(PURPLE);
                        g2.setStroke(new BasicStroke(2f));
                        g2.fillOval(-5, y - 5, 10, 10);
                        g2.drawLine(0, y, getWidth(), y);
                    }
                }
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(0, (HORA_FIN - HORA_INICIO) * ALTO_HORA));

        // Bloques de eventos del día
        if (items != null) {
            List<ItemCalendario> delDia = items.stream()
                    .filter(it -> it.fecha.equals(fecha))
                    .collect(Collectors.toList());

            for (int i = 0; i < delDia.size(); i++) {
                ItemCalendario it = delDia.get(i);
                JComponent bloque = bloqueItem(it, delDia.size(), i);

                int y = (it.horaInicio.getHour() - HORA_INICIO) * ALTO_HORA
                      + (it.horaInicio.getMinute() * ALTO_HORA / 60);
                int dur = (it.horaFin.getHour() - it.horaInicio.getHour()) * 60
                        + (it.horaFin.getMinute()  - it.horaInicio.getMinute());
                int h = Math.max(32, dur * ALTO_HORA / 60 - 2);

                // Si hay solapamiento, apilar desplazando
                int totalCols = delDia.size();
                int col = i;

                bloque.setBounds(
                    4 + col * 6,          // pequeño offset para cada evento
                    y + 1,
                    getWidth() - 8 - col * 6,
                    h
                );
                p.add(bloque);
            }
        }
        return p;
    }

    // ── BLOQUE EVENTO ─────────────────────────────────────────────────
    private JComponent bloqueItem(ItemCalendario it, int total, int idx) {
        Color c = colorDe(it.tipo);
        Color cLight = new Color(c.getRed(), c.getGreen(), c.getBlue(), 35);
        Color cBorde = new Color(c.getRed(), c.getGreen(), c.getBlue(), 180);

        // Hover state
        boolean[] over = {false};

        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                // Fondo
                g2.setColor(over[0]
                    ? new Color(c.getRed(), c.getGreen(), c.getBlue(), 55)
                    : cLight);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 7, 7);
                // Franja izquierda
                g2.setColor(c);
                g2.fillRoundRect(0, 0, 4, getHeight()-1, 4, 4);
                // Borde
                g2.setColor(cBorde);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, 7, 7);
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

        JLabel tit = new JLabel(truncar(it.titulo, 20));
        tit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tit.setForeground(TXT_PRI);
        tit.setAlignmentX(LEFT_ALIGNMENT);

        JLabel hora = new JLabel(it.horaInicio + " - " + it.horaFin);
        hora.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        hora.setForeground(TXT_SEC);
        hora.setAlignmentX(LEFT_ALIGNMENT);

        txt.add(tit);
        txt.add(hora);

        if (it.subtitulo != null && !it.subtitulo.isBlank()) {
            JLabel sub = new JLabel(truncar(it.subtitulo, 22));
            sub.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            sub.setForeground(TXT_SEC);
            sub.setAlignmentX(LEFT_ALIGNMENT);
            txt.add(sub);
        }

        p.add(txt, BorderLayout.NORTH);

        // Hover
        p.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { over[0] = true;  p.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { over[0] = false; p.repaint(); }
            @Override public void mouseClicked(MouseEvent e) { mostrarDetalle(it); }
        });

        p.setToolTipText("<html><b>" + it.tipo + "</b><br>"
            + it.titulo + "<br>"
            + it.horaInicio + " – " + it.horaFin
            + (it.subtitulo != null ? "<br>" + it.subtitulo : "")
            + "</html>");

        return p;
    }

    // ── LEYENDA INFERIOR ──────────────────────────────────────────────
    private JPanel leyenda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        p.setOpaque(false);

        p.add(chipLeyenda("Sesiones",       C_SESION));
        p.add(chipLeyenda("Eventos",        C_EVENTO));
        p.add(chipLeyenda("Colaboraciones", C_COLAB));
        p.add(chipLeyenda("Lanzamientos",   C_LANZ));
        p.add(chipLeyenda("Conciertos",     C_CONC));

        if (items != null && !items.isEmpty()) {
            long ses  = items.stream().filter(i -> "SESION".equals(i.tipo)).count();
            long ev   = items.stream().filter(i -> "EVENTO".equals(i.tipo)).count();
            long col  = items.stream().filter(i -> "COLABORACION".equals(i.tipo)).count();
            long lanz = items.stream().filter(i -> "LANZAMIENTO".equals(i.tipo)).count();
            long conc = items.stream().filter(i -> "CONCIERTO".equals(i.tipo)).count();

            StringBuilder sb = new StringBuilder("  Total: " + items.size() + " (");
            if (ses  > 0) sb.append(ses).append(" sesiones · ");
            if (ev   > 0) sb.append(ev).append(" eventos · ");
            if (col  > 0) sb.append(col).append(" colaboraciones · ");
            if (lanz > 0) sb.append(lanz).append(" lanzamientos · ");
            if (conc > 0) sb.append(conc).append(" conciertos · ");
            String txt = sb.toString().replaceAll(" · $", "") + ")";

            JLabel resumen = new JLabel(txt);
            resumen.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            resumen.setForeground(TXT_SEC);
            p.add(resumen);
        }
        return p;
    }

    private JComponent chipLeyenda(String texto, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);

        JPanel punto = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(color);
                g2.fillOval(0, (getHeight()-10)/2, 10, 10);
                g2.dispose();
            }
        };
        punto.setOpaque(false);
        punto.setPreferredSize(new Dimension(10, 16));

        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TXT_PRI);

        p.add(punto);
        p.add(lbl);
        return p;
    }

    // ── DETALLE ───────────────────────────────────────────────────────
    private void mostrarDetalle(ItemCalendario it) {
        String msg = "<html>"
            + "<b style='font-size:13'>" + it.titulo + "</b><br><br>"
            + "<b>Tipo:</b> " + it.tipo + "<br>"
            + "<b>Fecha:</b> " + it.fecha + "<br>"
            + "<b>Hora:</b> " + it.horaInicio + " – " + it.horaFin + "<br>"
            + (it.subtitulo != null && !it.subtitulo.isBlank()
                ? "<b>Detalle:</b> " + it.subtitulo : "")
            + "</html>";
        JOptionPane.showMessageDialog(this, msg, "Z-One — Detalle",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ── CARGA ─────────────────────────────────────────────────────────
    private void recargar() {
        try {
            items = servicio.itemsDeSemana(lunesActual);
        } catch (Exception ex) {
            ex.printStackTrace();
            MainFrame.showToast("Error al cargar calendario", MainFrame.ToastType.ERROR);
            items = java.util.Collections.emptyList();
        }

        LocalDate domingo = lunesActual.plusDays(6);
        String rango = lunesActual.format(FMT_RANGO).toUpperCase()
                + "  →  " + domingo.format(FMT_RANGO).toUpperCase();

        // Reconstruir UI
        construirUI();
        lblRango.setText(rango);

        cuerpo.removeAll();
        cuerpo.add(calendarioGrid(), BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    // ── HELPERS ───────────────────────────────────────────────────────
    private Color colorDe(String tipo) {
        if (tipo == null) return C_SESION;
        return switch (tipo.toUpperCase()) {
            case "SESION"       -> C_SESION;
            case "EVENTO"       -> C_EVENTO;
            case "COLABORACION" -> C_COLAB;
            case "LANZAMIENTO"  -> C_LANZ;
            case "CONCIERTO"    -> C_CONC;
            default             -> C_SESION;
        };
    }

    private JButton navBtn(String txt, boolean primary, java.awt.event.ActionListener a) {
        JButton b = new JButton(txt) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                if (primary) {
                    g2.setColor(getModel().isPressed() ? PURPLE_LT : PURPLE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else {
                    g2.setColor(getModel().isRollover()
                        ? new Color(108, 99, 255, 15) : BG_CARD);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(COL_BRD);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setForeground(primary ? Color.WHITE : TXT_PRI);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(7, 14, 7, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(a);
        return b;
    }

    private String truncar(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max-1) + "…" : s;
    }

    private static Graphics2D g2d(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        return g2;
    }
}