package css;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * EventoStyles.java — Tema Azul Claro "Zircon" (mismo que CabinaStyles)
 * ────────────────────────────────────────────────────────────────────────
 * Paleta idéntica a Cabinas. Todo lo visual del módulo Eventos vive aquí.
 * Componentes base: StatCardEvento, BtnEvento, GlassPanel, CampoElegante.
 */
public class EventoStyles {

    // ══════════════════════════════════════════════════════════════════
    //  PALETA — igual que CabinaStyles para coherencia visual
    // ══════════════════════════════════════════════════════════════════

    // Fondos
    public static final Color BG_MAIN       = new Color(0xEDF2FB);
    public static final Color BG_CARD       = new Color(0xFFFFFF);
    public static final Color BG_CARD_ALT   = new Color(0xF4F8FF);
    public static final Color BG_FIELD      = new Color(0xF0F5FF);
    public static final Color BG_SELECTED   = new Color(0xDCEAFF);

    // Bordes
    public static final Color BORDER        = new Color(0xC8D9F0);
    public static final Color BORDER_FOCUS  = new Color(0x5B9BF8);
    public static final Color BORDER_STRONG = new Color(0x94B8E8);

    // Texto
    public static final Color TEXT_PRI      = new Color(0x0D1B3E);
    public static final Color TEXT_SEC      = new Color(0x3A5A8C);
    public static final Color TEXT_MUT      = new Color(0x8AAFD4);

    // Acentos
    public static final Color INDIGO        = new Color(0x3B6EF8);
    public static final Color INDIGO_LIGHT  = new Color(0x6D99FB);
    public static final Color INDIGO_PALE   = new Color(0xDBE8FF);
    public static final Color SKY           = new Color(0x0EA5E9);
    public static final Color SKY_PALE      = new Color(0xE0F5FF);

    // Colores por tipo de evento
    public static final Color COLOR_CONCIERTO   = new Color(0x3B6EF8); // azul índigo
    public static final Color COLOR_SESION      = new Color(0x059669); // verde esmeralda
    public static final Color COLOR_LANZAMIENTO = new Color(0xD97706); // ámbar
    public static final Color COLOR_ENTREVISTA  = new Color(0x7C3AED); // violeta
    public static final Color COLOR_ENSAYO      = new Color(0xDC2626); // rojo

    public static final Color PALE_CONCIERTO    = new Color(0xDBE8FF);
    public static final Color PALE_SESION       = new Color(0xD1FAE5);
    public static final Color PALE_LANZAMIENTO  = new Color(0xFEF3C7);
    public static final Color PALE_ENTREVISTA   = new Color(0xEDE9FE);
    public static final Color PALE_ENSAYO       = new Color(0xFEE2E2);

    // ══════════════════════════════════════════════════════════════════
    //  MAPEO POR TIPO DE EVENTO
    // ══════════════════════════════════════════════════════════════════
    public static Color colorTipo(String tipo) {
        if (tipo == null) return TEXT_MUT;
        return switch (tipo) {
            case "Concierto"   -> COLOR_CONCIERTO;
            case "Sesion"      -> COLOR_SESION;
            case "Lanzamiento" -> COLOR_LANZAMIENTO;
            case "Entrevista"  -> COLOR_ENTREVISTA;
            case "Ensayo"      -> COLOR_ENSAYO;
            default            -> TEXT_MUT;
        };
    }

    public static Color paleBgTipo(String tipo) {
        if (tipo == null) return BG_FIELD;
        return switch (tipo) {
            case "Concierto"   -> PALE_CONCIERTO;
            case "Sesion"      -> PALE_SESION;
            case "Lanzamiento" -> PALE_LANZAMIENTO;
            case "Entrevista"  -> PALE_ENTREVISTA;
            case "Ensayo"      -> PALE_ENSAYO;
            default            -> BG_FIELD;
        };
    }

    public static String iconoTipo(String tipo) {
        if (tipo == null) return "🎟";
        return switch (tipo) {
            case "Concierto"   -> "🎸";
            case "Sesion"      -> "🎧";
            case "Lanzamiento" -> "🚀";
            case "Entrevista"  -> "🎙";
            case "Ensayo"      -> "🥁";
            default            -> "🎟";
        };
    }

    // ══════════════════════════════════════════════════════════════════
    //  STAT CARD — blanca con acento lateral animado (igual a Cabinas)
    // ══════════════════════════════════════════════════════════════════
    public static class StatCardEvento extends JPanel {
        private final Color acento;
        private float glowPulse = 0f;
        private boolean glowUp  = true;
        private final Timer pulseTimer;

        public StatCardEvento(String titulo, JLabel valorLbl, Color acento, String sub) {
            this.acento = acento;
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(16, 18, 14, 18));

            JLabel t = lbl(titulo.toUpperCase(), 9, false, TEXT_MUT);
            valorLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
            valorLbl.setForeground(acento);
            JLabel s = lbl(sub, 9, false, TEXT_MUT);

            JPanel col = new JPanel();
            col.setOpaque(false);
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
            t.setAlignmentX(LEFT_ALIGNMENT);
            valorLbl.setAlignmentX(LEFT_ALIGNMENT);
            s.setAlignmentX(LEFT_ALIGNMENT);
            col.add(t);
            col.add(Box.createVerticalStrut(4));
            col.add(valorLbl);
            col.add(s);
            add(col, BorderLayout.WEST);

            pulseTimer = new Timer(40, e -> {
                glowPulse += glowUp ? 0.012f : -0.012f;
                if (glowPulse >= 1f) { glowPulse = 1f; glowUp = false; }
                if (glowPulse <= 0f) { glowPulse = 0f; glowUp = true; }
                repaint();
            });
            pulseTimer.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            // Sombra suave
            g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 18));
            g2.fillRoundRect(2, 4, w - 4, h, 14, 14);

            // Fondo blanco
            g2.setColor(BG_CARD);
            g2.fillRoundRect(0, 0, w, h, 14, 14);

            // Borde
            g2.setColor(BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

            // Acento izquierdo animado
            int barH = (int)(h * 0.55f);
            int barY = (h - barH) / 2;
            g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(),
                    (int)(180 + glowPulse * 60)));
            g2.fillRoundRect(0, barY, 4, barH, 4, 4);

            // Halo suave
            RadialGradientPaint halo = new RadialGradientPaint(
                    new Point2D.Float(0, 0), w * 0.7f,
                    new float[]{0f, 1f},
                    new Color[]{
                            new Color(acento.getRed(), acento.getGreen(), acento.getBlue(),
                                    (int)(glowPulse * 18)),
                            new Color(0, 0, 0, 0)
                    });
            g2.setPaint(halo);
            g2.fillRoundRect(0, 0, w, h, 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }

        public void stopAnimations() { pulseTimer.stop(); }
    }

    // ══════════════════════════════════════════════════════════════════
    //  EVENTO CARD — blanca con hover azulado, franja top coloreada
    // ══════════════════════════════════════════════════════════════════
    public static class EventoCard extends JPanel {
        private float hoverT = 0f;
        private final boolean seleccionada;
        private final Color   acento;

        public EventoCard(boolean seleccionada, Color acento) {
            this.seleccionada = seleccionada;
            this.acento = acento;
            setOpaque(false);
            setLayout(new BorderLayout(0, 8));
            setBorder(new EmptyBorder(16, 18, 14, 18));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (seleccionada) hoverT = 1f;
        }

        public void startHover() { animHover(true); }
        public void stopHover()  { animHover(false); }

        private void animHover(boolean in) {
            new Timer(12, null) {{
                addActionListener(e -> {
                    hoverT += in ? 0.1f : -0.1f;
                    hoverT = Math.max(0f, Math.min(1f, hoverT));
                    repaint();
                    if ((in && hoverT >= 1f) || (!in && hoverT <= 0f)) stop();
                });
                start();
            }};
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            float t = seleccionada ? 1f : hoverT;

            // Sombra dinámica
            g2.setColor(new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(), (int)(8 + t * 28)));
            g2.fillRoundRect(3, 6, w - 6, h - 2, 16, 16);

            // Fondo
            Color bg = seleccionada
                    ? blend(BG_CARD, INDIGO_PALE, 0.45f)
                    : blend(BG_CARD, BG_CARD_ALT, t * 0.5f);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, 16, 16);

            // Borde
            if (seleccionada) {
                g2.setStroke(new BasicStroke(1.8f));
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 200));
            } else {
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(blend(BORDER, BORDER_STRONG, hoverT * 0.6f));
            }
            g2.drawRoundRect(1, 1, w - 2, h - 2, 15, 15);

            // Franja top coloreada
            if (seleccionada || hoverT > 0.1f) {
                int alpha = seleccionada ? 220 : (int)(hoverT * 160);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), alpha));
                g2.fillRoundRect(0, 0, w, 4, 16, 16);
                g2.fillRect(0, 2, w, 4);
            }
            g2.dispose();
            super.paintComponent(g);
        }

        private Color blend(Color a, Color b, float t) {
            t = Math.max(0f, Math.min(1f, t));
            return new Color(
                    (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t),
                    (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                    (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t));
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  PÍLDORA DE TIPO
    // ══════════════════════════════════════════════════════════════════
    public static class PildoraTipo extends JPanel {
        private final String texto;
        private final Color  fg;
        private final Color  bg;

        public PildoraTipo(String tipo) {
            this.texto = tipo != null ? tipo : "—";
            this.fg    = colorTipo(tipo);
            this.bg    = paleBgTipo(tipo);
            setOpaque(false);
            FontMetrics fm = getFontMetrics(new Font("Segoe UI", Font.BOLD, 11));
            setPreferredSize(new Dimension(fm.stringWidth("● " + texto) + 24, 24));
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 100));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            String label = "● " + texto;
            int x = (getWidth() - fm.stringWidth(label)) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.setColor(fg);
            g2.drawString(label, x, y);
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  BOTÓN — mismo estilo que BtnCabina
    // ══════════════════════════════════════════════════════════════════
    public static class BtnEvento extends JPanel {
        private final String texto;
        private final boolean primary;
        private final Color   acento;
        private float hoverT      = 0f;
        private float rippleAlpha = 0f;
        private float rippleR     = 0f;
        private int   rippleX, rippleY;
        private Timer hoverTimer, rippleTimer;
        private final java.util.List<ActionListener> listeners = new java.util.ArrayList<>();

        public BtnEvento(String texto, boolean primary) {
            this(texto, primary, primary ? INDIGO : BORDER_STRONG);
        }

        public BtnEvento(String texto, boolean primary, Color acento) {
            this.texto   = texto;
            this.primary = primary;
            this.acento  = acento;
            setOpaque(false);
            setPreferredSize(new Dimension(130, 38));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { anim(true); }
                @Override public void mouseExited (MouseEvent e) { anim(false); }
                @Override public void mousePressed(MouseEvent e) {
                    rippleX = e.getX(); rippleY = e.getY();
                    rippleR = 0f; rippleAlpha = 0.35f;
                    animRipple();
                }
                @Override public void mouseClicked(MouseEvent e) {
                    ActionEvent ae = new ActionEvent(BtnEvento.this, ActionEvent.ACTION_PERFORMED, "");
                    for (ActionListener l : listeners) l.actionPerformed(ae);
                }
            });
        }

        private void anim(boolean in) {
            if (hoverTimer != null) hoverTimer.stop();
            hoverTimer = new Timer(12, e -> {
                hoverT += in ? 0.1f : -0.1f;
                hoverT = Math.max(0f, Math.min(1f, hoverT));
                repaint();
                if ((in && hoverT >= 1f) || (!in && hoverT <= 0f)) ((Timer)e.getSource()).stop();
            });
            hoverTimer.start();
        }

        private void animRipple() {
            if (rippleTimer != null) rippleTimer.stop();
            rippleTimer = new Timer(12, e -> {
                rippleR     += 10f;
                rippleAlpha -= 0.025f;
                repaint();
                if (rippleAlpha <= 0f) ((Timer)e.getSource()).stop();
            });
            rippleTimer.start();
        }

        public void addActionListener(ActionListener l) { listeners.add(l); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            if (primary) {
                Color base = blend(INDIGO, INDIGO_LIGHT, hoverT * 0.45f);
                g2.setColor(base);
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                if (hoverT > 0) {
                    g2.setColor(new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(), (int)(hoverT * 50)));
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawRoundRect(-1, 1, w + 1, h, 11, 11);
                }
            } else {
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), (int)(hoverT * 30)));
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), (int)(80 + hoverT * 120)));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
            }

            // Ripple
            if (rippleAlpha > 0f) {
                g2.setColor(new Color(255, 255, 255, (int)(rippleAlpha * 120)));
                g2.fillOval(rippleX - (int)rippleR, rippleY - (int)rippleR,
                        (int)rippleR * 2, (int)rippleR * 2);
            }

            // Texto
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(texto)) / 2;
            int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
            g2.setColor(primary ? Color.WHITE
                    : new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), (int)(160 + hoverT * 95)));
            g2.drawString(texto, tx, ty);
            g2.dispose();
        }

        private Color blend(Color a, Color b, float t) {
            t = Math.max(0f, Math.min(1f, t));
            return new Color(
                    (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t),
                    (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                    (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t));
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  GLASS PANEL — blanco con borde azul claro (reutilizable)
    // ══════════════════════════════════════════════════════════════════
    public static class GlassPanel extends JPanel {
        private final int     radius;
        private final boolean topAccent;
        private final Color   topColor;

        public GlassPanel(int radius) { this(radius, false, null); }

        public GlassPanel(int radius, boolean topAccent, Color topColor) {
            this.radius    = radius;
            this.topAccent = topAccent;
            this.topColor  = topColor;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(59, 110, 248, 12));
            g2.fillRoundRect(2, 5, w - 4, h, radius, radius);
            g2.setColor(BG_CARD);
            g2.fillRoundRect(0, 0, w, h, radius, radius);
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
            if (topAccent && topColor != null) {
                g2.setColor(topColor);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(radius / 2, 1, w - radius / 2, 1);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  CAMPO ELEGANTE — blanco con focus azul animado
    // ══════════════════════════════════════════════════════════════════
    public static class CampoElegante extends JPanel {
        private final JTextField field;
        private float focusT = 0f;
        private Timer focusTimer;

        public CampoElegante(String valor) {
            setOpaque(false);
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(0, 42));

            field = new JTextField(valor) {
                @Override protected void paintComponent(Graphics g) {
                    g.setColor(BG_FIELD);
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    super.paintComponent(g);
                }
            };
            field.setOpaque(false);
            field.setBorder(BorderFactory.createEmptyBorder(0, 13, 0, 13));
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            field.setForeground(TEXT_PRI);
            field.setCaretColor(BORDER_FOCUS);
            field.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { animFocus(true); }
                @Override public void focusLost  (FocusEvent e) { animFocus(false); }
            });
            add(field, BorderLayout.CENTER);
        }

        private void animFocus(boolean in) {
            if (focusTimer != null) focusTimer.stop();
            focusTimer = new Timer(12, e -> {
                focusT += in ? 0.1f : -0.1f;
                focusT = Math.max(0f, Math.min(1f, focusT));
                repaint();
                if ((in && focusT >= 1f) || (!in && focusT <= 0f)) ((Timer)e.getSource()).stop();
            });
            focusTimer.start();
        }

        public String getText()       { return field.getText(); }
        public void   setText(String t) { field.setText(t); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BG_FIELD);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            Color bc = focusT > 0
                    ? new Color(BORDER_FOCUS.getRed(), BORDER_FOCUS.getGreen(), BORDER_FOCUS.getBlue(),
                                (int)(60 + focusT * 195))
                    : BORDER;
            g2.setStroke(new BasicStroke(focusT > 0 ? 1.5f : 1f));
            g2.setColor(bc);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            if (focusT > 0) {
                g2.setColor(new Color(BORDER_FOCUS.getRed(), BORDER_FOCUS.getGreen(),
                        BORDER_FOCUS.getBlue(), (int)(focusT * 28)));
                g2.setStroke(new BasicStroke(4f));
                g2.drawRoundRect(-2, -2, getWidth() + 3, getHeight() + 3, 13, 13);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  HELPERS GLOBALES
    // ══════════════════════════════════════════════════════════════════
    public static BtnEvento btnAccion(String texto, boolean primary, int ancho) {
        BtnEvento b = new BtnEvento(texto, primary);
        b.setPreferredSize(new Dimension(ancho, 38));
        return b;
    }

    public static BtnEvento btnAccion(String texto, boolean primary, int ancho, Color acento) {
        BtnEvento b = new BtnEvento(texto, primary, acento);
        b.setPreferredSize(new Dimension(ancho, 38));
        return b;
    }

    public static JLabel lbl(String texto, int size, boolean bold, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(color);
        return l;
    }
}