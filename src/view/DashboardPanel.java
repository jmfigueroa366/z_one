package view;

import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static view.formProductor.*;

/**
 * DashboardPanel — Rediseñado con el mismo estilo oscuro de formProductor.
 * Paleta: fondo casi negro, tarjetas oscuras, bordes morados sutiles,
 * acentos cyan/verde/ámbar/rosa.
 */
public class DashboardPanel extends JPanel {

    private final Usuario usuario;

    // ── Paleta (reutiliza la de formProductor) ────────────────────────
    // BG_DEEP, BG_CARD, BG_FIELD, PURPLE, PURPLE_LT, CYAN, GREEN, AMBER, PINK
    // TXT_PRI, TXT_SEC, COL_BRD, ORO, PLATA, BRONCE — ya definidas en formProductor

    public DashboardPanel(Usuario usuario) {
        this.usuario = usuario;
        construirUI();
    }

    // ── CONSTRUCCIÓN ─────────────────────────────────────────────────
    private void construirUI() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel izq = new JPanel(new BorderLayout(0, 18));
        izq.setOpaque(false);
        izq.add(encabezado(),       BorderLayout.NORTH);
        izq.add(cuerpoIzquierdo(),  BorderLayout.CENTER);

        JPanel der = new JPanel(new BorderLayout(0, 14));
        der.setOpaque(false);
        der.setBorder(new EmptyBorder(0, 14, 0, 0));
        der.add(panelAcciones(),    BorderLayout.NORTH);
        der.add(panelActividad(),   BorderLayout.CENTER);
        der.setPreferredSize(new Dimension(280, 0));

        add(izq, BorderLayout.CENTER);
        add(der, BorderLayout.EAST);
    }

    // ── ENCABEZADO ───────────────────────────────────────────────────
    private JPanel encabezado() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel ico   = mk("🎵", new Font("Segoe UI Emoji", Font.PLAIN, 18), TXT_PRI);
        JLabel title = mk("Dashboard", F_TITLE, TXT_PRI);
        JLabel sub   = mk("RESUMEN GENERAL · ACTIVIDAD · MÉTRICAS RÁPIDAS", F_SUB, TXT_SEC);

        for (JLabel l : new JLabel[]{ico, title, sub}) l.setAlignmentX(LEFT_ALIGNMENT);
        titulos.add(ico);
        titulos.add(Box.createVerticalStrut(3));
        titulos.add(title);
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(sub);

        // Badge de bienvenida
        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(PURPLE.getRed(), PURPLE.getGreen(), PURPLE.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(PURPLE.getRed(), PURPLE.getGreen(), PURPLE.getBlue(), 100));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setLayout(new BoxLayout(badge, BoxLayout.Y_AXIS));
        badge.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel bienvenido = mk("Bienvenido de vuelta,", F_BODY, TXT_SEC);
        JLabel nombreUser = mk(usuario.getNombre() + "  ✦",
                new Font("Segoe UI", Font.BOLD, 16), PURPLE_LT);
        JLabel rolUser    = mk(usuario.getRol().toUpperCase(),
                new Font("Segoe UI", Font.BOLD, 9), CYAN);
        bienvenido.setAlignmentX(RIGHT_ALIGNMENT);
        nombreUser.setAlignmentX(RIGHT_ALIGNMENT);
        rolUser.setAlignmentX(RIGHT_ALIGNMENT);
        badge.add(bienvenido);
        badge.add(Box.createVerticalStrut(2));
        badge.add(nombreUser);
        badge.add(Box.createVerticalStrut(2));
        badge.add(rolUser);

        p.add(titulos, BorderLayout.WEST);
        p.add(badge,   BorderLayout.EAST);
        return p;
    }

    // ── CUERPO IZQUIERDO ─────────────────────────────────────────────
    private JPanel cuerpoIzquierdo() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(filaStats());
        p.add(Box.createVerticalStrut(18));
        p.add(panelGrafico());
        return p;
    }

    // ── FILA STATS ───────────────────────────────────────────────────
    private JPanel filaStats() {
        JPanel p = new JPanel(new GridLayout(1, 5, 12, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        p.setAlignmentX(LEFT_ALIGNMENT);

        p.add(statCard("ARTISTAS",    "24", "+3 este mes",    new Color(59,130,246), "🎤"));
        p.add(statCard("PRODUCTORES", "8",  "todos activos",  PURPLE,               "🎚"));
        p.add(statCard("SESIONES",    "12", "este mes",       AMBER,                "📅"));
        p.add(statCard("CABINAS",     "3",  "disponibles",    CYAN,                 "🎙"));
        p.add(statCard("CANCIONES",   "87", "+5 publicadas",  PINK,                 "🎵"));
        return p;
    }

    private JPanel statCard(String titulo, String valor, String sub, Color acento, String emoji) {
        JPanel card = new JPanel() {
            boolean hover = false;
            {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) {
                        MainFrame.showToast("Ver detalles de " + titulo, MainFrame.ToastType.INFO);
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                // borde
                g2.setColor(hover
                    ? new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 180)
                    : new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 60));
                g2.setStroke(new BasicStroke(hover ? 1.5f : 1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                // línea de acento superior
                g2.setColor(acento);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(14, 1, getWidth()-14, 1);
                // halo del emoji
                if (hover) {
                    g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 20));
                    g2.fillOval(getWidth()-46, 8, 38, 38);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(6, 0));
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel emo = mk(emoji, new Font("Segoe UI Emoji", Font.PLAIN, 18), TXT_PRI);
        emo.setHorizontalAlignment(SwingConstants.RIGHT);
        emo.setVerticalAlignment(SwingConstants.TOP);

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel lTit = mk(titulo, F_SUB,
            new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 200));
        JLabel lVal = mk(valor, new Font("Segoe UI", Font.BOLD, 26), acento);
        JLabel lSub = mk(sub,   new Font("Segoe UI", Font.PLAIN, 9), TXT_SEC);
        lTit.setAlignmentX(LEFT_ALIGNMENT);
        lVal.setAlignmentX(LEFT_ALIGNMENT);
        lSub.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(lTit);
        txt.add(lVal);
        txt.add(lSub);

        card.add(txt, BorderLayout.CENTER);
        card.add(emo, BorderLayout.EAST);
        return card;
    }

    // ── GRÁFICO DE BARRAS ─────────────────────────────────────────────
    private JPanel panelGrafico() {
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
        card.setLayout(new BorderLayout(0, 0));
        card.setAlignmentX(LEFT_ALIGNMENT);

        // Cabecera del gráfico
        JPanel cab = new JPanel(new BorderLayout(8, 0));
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(14, 18, 10, 18));

        JLabel titulo = mk("📊  ACTIVIDAD SEMANAL", new Font("Segoe UI", Font.BOLD, 12), TXT_PRI);
        JLabel sub    = mk("sesiones · canciones · artistas", F_SUB, TXT_SEC);

        JPanel cabTxt = new JPanel();
        cabTxt.setOpaque(false);
        cabTxt.setLayout(new BoxLayout(cabTxt, BoxLayout.Y_AXIS));
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        cabTxt.add(titulo);
        cabTxt.add(Box.createVerticalStrut(2));
        cabTxt.add(sub);

        // Leyenda
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        leyenda.setOpaque(false);
        for (Object[] it : new Object[][]{
            {"Sesiones", CYAN}, {"Canciones", PINK}, {"Artistas", new Color(59,130,246)}
        }) {
            JPanel dot = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = g2d(g);
                    g2.setColor((Color)it[1]);
                    g2.fillOval(0, 3, 8, 8);
                    g2.dispose();
                }
            };
            dot.setOpaque(false);
            dot.setPreferredSize(new Dimension(8, 14));
            JLabel lbl = mk((String)it[0], new Font("Segoe UI", Font.PLAIN, 10), TXT_SEC);
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            item.setOpaque(false);
            item.add(dot);
            item.add(lbl);
            leyenda.add(item);
        }

        cab.add(cabTxt,  BorderLayout.WEST);
        cab.add(leyenda, BorderLayout.EAST);

        // Separador
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0, PURPLE, getWidth()*0.6f,0, new Color(0,0,0,0)));
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        sep.setOpaque(false);
        sep.setPreferredSize(new Dimension(0, 1));

        JPanel cabFull = new JPanel(new BorderLayout());
        cabFull.setOpaque(false);
        cabFull.add(cab, BorderLayout.CENTER);
        cabFull.add(sep, BorderLayout.SOUTH);

        // Área del gráfico
        JPanel grafico = new JPanel() {
            // Datos simulados por día de la semana
            final String[] dias    = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
            final int[]    ses     = {3, 5, 2, 6, 4, 7, 1};
            final int[]    canc    = {5, 3, 7, 4, 6, 8, 2};
            final int[]    art     = {2, 4, 3, 5, 3, 6, 1};
            final Color[]  colores = {CYAN, PINK, new Color(59,130,246)};

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());

                int n = dias.length;
                int padL = 40, padR = 20, padT = 20, padB = 36;
                int chartW = getWidth()  - padL - padR;
                int chartH = getHeight() - padT - padB;
                int maxVal = 10;

                // Líneas horizontales de guía
                g2.setStroke(new BasicStroke(0.6f));
                for (int i = 0; i <= 5; i++) {
                    int y = padT + chartH - (int)(chartH * i / 5.0);
                    g2.setColor(new Color(255,255,255,15));
                    g2.drawLine(padL, y, padL + chartW, y);
                    g2.setFont(new Font("Consolas", Font.PLAIN, 9));
                    g2.setColor(TXT_SEC);
                    g2.drawString(String.valueOf(i*2), padL-22, y+4);
                }

                // Grupos de barras
                int groupW   = chartW / n;
                int barCount = 3;
                int barW     = Math.max(6, (groupW - 12) / barCount);
                int gap      = 3;

                int[][] datos = {ses, canc, art};

                for (int d = 0; d < n; d++) {
                    int gx = padL + d * groupW + 6;
                    for (int b = 0; b < barCount; b++) {
                        int bh = (int)(chartH * datos[b][d] / (double)maxVal);
                        int bx = gx + b * (barW + gap);
                        int by = padT + chartH - bh;
                        // Barra con gradiente
                        Color c = colores[b];
                        GradientPaint gp = new GradientPaint(
                            bx, by, c,
                            bx, by + bh, new Color(c.getRed(), c.getGreen(), c.getBlue(), 80));
                        g2.setPaint(gp);
                        g2.fillRoundRect(bx, by, barW, bh, 4, 4);
                        // Valor encima
                        if (bh > 18) {
                            g2.setColor(Color.WHITE);
                            g2.setFont(new Font("Consolas", Font.BOLD, 8));
                            g2.drawString(String.valueOf(datos[b][d]), bx+2, by-3);
                        }
                    }
                    // Etiqueta del día
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    g2.setColor(TXT_SEC);
                    FontMetrics fm = g2.getFontMetrics();
                    int lx = gx + (groupW - 12)/2 - fm.stringWidth(dias[d])/2;
                    g2.drawString(dias[d], lx, padT + chartH + 18);
                }

                // Eje Y
                g2.setColor(new Color(255,255,255,20));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(padL, padT, padL, padT + chartH);

                g2.dispose();
            }
        };
        grafico.setOpaque(false);
        grafico.setPreferredSize(new Dimension(0, 200));

        card.add(cabFull, BorderLayout.NORTH);
        card.add(grafico, BorderLayout.CENTER);

        // Footer con totales de la semana
        JPanel footer = new JPanel(new GridLayout(1, 3, 0, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 14, 14, 14));
        for (Object[] it : new Object[][]{
            {"28", "Total sesiones", CYAN},
            {"35", "Canciones creadas", PINK},
            {"24", "Artistas activos", new Color(59,130,246)}
        }) {
            JPanel fi = new JPanel();
            fi.setOpaque(false);
            fi.setLayout(new BoxLayout(fi, BoxLayout.Y_AXIS));
            JLabel v = mk((String)it[0], new Font("Segoe UI", Font.BOLD, 18), (Color)it[2]);
            JLabel t = mk((String)it[1], new Font("Segoe UI", Font.PLAIN, 9), TXT_SEC);
            v.setAlignmentX(LEFT_ALIGNMENT);
            t.setAlignmentX(LEFT_ALIGNMENT);
            fi.add(v); fi.add(t);

            // Separador vertical excepto último
            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setOpaque(false);
            wrap.add(fi, BorderLayout.CENTER);
            footer.add(wrap);
        }

        JPanel sepFooter = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(COL_BRD);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        sepFooter.setOpaque(false);
        sepFooter.setPreferredSize(new Dimension(0, 1));

        JPanel footerFull = new JPanel(new BorderLayout());
        footerFull.setOpaque(false);
        footerFull.add(sepFooter, BorderLayout.NORTH);
        footerFull.add(footer,    BorderLayout.CENTER);

        card.add(footerFull, BorderLayout.SOUTH);
        return card;
    }

    // ── PANEL ACCIONES RÁPIDAS (derecha arriba) ───────────────────────
    private JPanel panelAcciones() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(7, 5, 18));
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
        inner.setPreferredSize(new Dimension(280, 240));

        // Cabecera
        JPanel cab = cabeceraDer("⚡  ACCIONES RÁPIDAS", "un solo clic", AMBER);
        inner.add(cab, BorderLayout.NORTH);

        // Acciones
        JPanel acciones = new JPanel();
        acciones.setOpaque(false);
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));
        acciones.setBorder(new EmptyBorder(10, 10, 10, 10));

        Object[][] items = {
            {"🎤", "Nuevo artista",     "Registrar artista",      new Color(59,130,246)},
            {"🎵", "Nueva canción",     "Agregar al catálogo",    PINK},
            {"📅", "Programar sesión",  "Reservar cabina",        AMBER},
            {"📊", "Ver estadísticas",  "Top canciones",          PURPLE},
        };
        for (Object[] it : items) {
            acciones.add(filaAccion(
                (String)it[0], (String)it[1], (String)it[2], (Color)it[3]));
            acciones.add(Box.createVerticalStrut(6));
        }

        JScrollPane scroll = new JScrollPane(acciones);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        inner.add(scroll, BorderLayout.CENTER);
        return inner;
    }

    private JPanel filaAccion(String emoji, String titulo, String desc, Color acento) {
        final boolean[] hover = {false};
        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(hover[0] ? new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 25) : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                if (hover[0]) {
                    g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 100));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 9, 9);
                }
                // Barra izquierda
                g2.setColor(acento);
                g2.fillRoundRect(0, 6, 3, getHeight()-12, 3, 3);
                // Flecha derecha
                g2.setColor(hover[0] ? acento : TXT_SEC);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int ax = getWidth()-14, ay = getHeight()/2;
                g2.drawLine(ax, ay-4, ax+5, ay);
                g2.drawLine(ax, ay+4, ax+5, ay);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(10, 0));
        fila.setBorder(new EmptyBorder(8, 12, 8, 22));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        fila.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fila.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover[0]=true;  fila.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hover[0]=false; fila.repaint(); }
            @Override public void mouseClicked(MouseEvent e) {
                MainFrame.showToast("Acción: " + titulo, MainFrame.ToastType.SUCCESS);
            }
        });

        JLabel emo = mk(emoji, new Font("Segoe UI Emoji", Font.PLAIN, 16), TXT_PRI);
        emo.setPreferredSize(new Dimension(24, 0));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel t = mk(titulo, new Font("Segoe UI", Font.BOLD, 12), TXT_PRI);
        JLabel d = mk(desc,   new Font("Segoe UI", Font.PLAIN, 9),  TXT_SEC);
        t.setAlignmentX(LEFT_ALIGNMENT);
        d.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(t); txt.add(d);

        fila.add(emo, BorderLayout.WEST);
        fila.add(txt, BorderLayout.CENTER);
        return fila;
    }

    // ── PANEL ACTIVIDAD RECIENTE (derecha abajo) ──────────────────────
    private JPanel panelActividad() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(7, 5, 18));
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

        JPanel cab = cabeceraDer("⬡  ACTIVIDAD RECIENTE", "últimos eventos", GREEN);
        inner.add(cab, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setOpaque(false);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBorder(new EmptyBorder(10, 10, 10, 10));

        Object[][] eventos = {
            {"CV", "Carlos Vives grabó Pa Mayte",       "hace 2h",  GREEN},
            {"SH", "Shakira publicó Hips Don't Lie",    "hace 5h",  CYAN},
            {"JB", "Sesión programada con J Balvin",    "mañana",   PURPLE_LT},
            {"MA", "Nuevo productor: Maluma",           "hace 1d",  AMBER},
            {"OR", "Álbum Oral Fixation Vol. 2 listo",  "hace 2d",  PINK},
            {"BB", "Bad Bunny agendó cabina B",         "hace 3d",  new Color(59,130,246)},
        };

        for (Object[] ev : eventos) {
            lista.add(filaActividad(
                (String)ev[0], (String)ev[1], (String)ev[2], (Color)ev[3]));
            lista.add(Box.createVerticalStrut(5));
        }

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        inner.add(scroll, BorderLayout.CENTER);
        return inner;
    }

    private JPanel filaActividad(String iniciales, String texto, String tiempo, Color acento) {
        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                g2.setColor(acento);
                g2.fillRoundRect(0, 4, 3, getHeight()-8, 3, 3);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(8, 0));
        fila.setBorder(new EmptyBorder(7, 12, 7, 10));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        fila.setAlignmentX(LEFT_ALIGNMENT);

        // Avatar con iniciales
        JLabel avatar = new JLabel(iniciales, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 50));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 150));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        avatar.setForeground(acento);
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(28, 28));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        // Truncar texto si es muy largo
        String textoCorto = texto.length() > 28 ? texto.substring(0, 27) + "…" : texto;
        JLabel t = mk(textoCorto, new Font("Segoe UI", Font.PLAIN, 11), TXT_PRI);
        JLabel d = mk(tiempo,     new Font("Consolas", Font.PLAIN, 9),  TXT_SEC);
        t.setAlignmentX(LEFT_ALIGNMENT);
        d.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(t); txt.add(d);

        fila.add(avatar, BorderLayout.WEST);
        fila.add(txt,    BorderLayout.CENTER);
        return fila;
    }

    // ── CABECERA REUTILIZABLE PANEL DERECHO ───────────────────────────
    private JPanel cabeceraDer(String titulo, String sub, Color acento) {
        JPanel cab = new JPanel(new BorderLayout(6, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(10, 8, 24));
                g2.fillRoundRect(0, 0, getWidth(), getHeight()+12, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(11, 14, 11, 14));

        JLabel t = mk(titulo, new Font("Segoe UI", Font.BOLD, 12), acento);
        JLabel s = mk(sub, F_SUB, TXT_SEC);
        cab.add(t, BorderLayout.WEST);
        cab.add(s, BorderLayout.EAST);

        JPanel sepLine = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0, acento, getWidth()*0.6f,0, new Color(0,0,0,0)));
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        sepLine.setOpaque(false);
        sepLine.setPreferredSize(new Dimension(0, 1));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(cab,     BorderLayout.CENTER);
        wrapper.add(sepLine, BorderLayout.SOUTH);
        return wrapper;
    }

    // ── HELPERS ───────────────────────────────────────────────────────
    private static JLabel mk(String txt, Font f, Color c) {
        JLabel l = new JLabel(txt);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    private static Graphics2D g2d(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }
}