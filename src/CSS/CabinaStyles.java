package css;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;

/**
 * CabinaStyles.java — Tema Azul Claro "Zircon"
 * ─────────────────────────────────────────────
 * Paleta: blancos azulados, azules cielo, acentos índigo y cyan profundo.
 * TODO lo visual del módulo Cabinas vive aquí.
 */
public class CabinaStyles {

    // ══════════════════════════════════════════════════════════════════
    //  PALETA — Tema "Zircon Blue" (claro/azulado)
    // ══════════════════════════════════════════════════════════════════

    // Fondos
    public static final Color BG_MAIN        = new Color(0xEDF2FB); // azul leche suave
    public static final Color BG_CARD        = new Color(0xFFFFFF); // blanco puro
    public static final Color BG_CARD_ALT    = new Color(0xF4F8FF); // blanco azulado
    public static final Color BG_FIELD       = new Color(0xF0F5FF); // campo input
    public static final Color BG_SELECTED    = new Color(0xDCEAFF); // card seleccionada

    // Bordes
    public static final Color BORDER         = new Color(0xC8D9F0); // borde suave
    public static final Color BORDER_FOCUS   = new Color(0x5B9BF8); // borde en focus
    public static final Color BORDER_STRONG  = new Color(0x94B8E8); // borde notable

    // Texto
    public static final Color TEXT_PRI       = new Color(0x0D1B3E); // azul muy oscuro
    public static final Color TEXT_SEC       = new Color(0x3A5A8C); // azul medio
    public static final Color TEXT_MUT       = new Color(0x8AAFD4); // azul claro muted

    // Acentos principales
    public static final Color INDIGO         = new Color(0x3B6EF8); // azul índigo vibrante
    public static final Color INDIGO_LIGHT   = new Color(0x6D99FB); // índigo suave
    public static final Color INDIGO_PALE    = new Color(0xDBE8FF); // índigo muy claro

    public static final Color SKY           = new Color(0x0EA5E9); // azul cielo
    public static final Color SKY_LIGHT     = new Color(0x7DD3FC); // cielo suave
    public static final Color SKY_PALE      = new Color(0xE0F5FF); // cielo pálido

    public static final Color COBALT        = new Color(0x1D4ED8); // azul cobalto
    public static final Color NAVY          = new Color(0x1E3A70); // azul marino

    // Estados semánticos (todos en tono azulado)
    public static final Color GREEN         = new Color(0x059669); // verde esmeralda
    public static final Color GREEN_PALE    = new Color(0xD1FAE5);
    public static final Color AMBER         = new Color(0xD97706); // ámbar cálido
    public static final Color AMBER_PALE    = new Color(0xFEF3C7);
    public static final Color RED           = new Color(0xDC2626); // rojo coral
    public static final Color RED_PALE      = new Color(0xFEE2E2);
    public static final Color VIOLET        = new Color(0x7C3AED); // violeta para reservada
    public static final Color VIOLET_PALE   = new Color(0xEDE9FE);

    // Sombra
    public static final Color SHADOW        = new Color(0x3B6EF8, true); // sombra azulada

    // ══════════════════════════════════════════════════════════════════
    //  MAPEO POR ESTADO
    // ══════════════════════════════════════════════════════════════════
    public static Color colorEstado(String estado) {
        if (estado == null) return TEXT_MUT;
        return switch (estado) {
            case "Disponible"    -> GREEN;
            case "Ocupada"       -> AMBER;
            case "Mantenimiento" -> RED;
            case "Reservada"     -> VIOLET;
            default              -> TEXT_MUT;
        };
    }

    public static Color paleBgEstado(String estado) {
        if (estado == null) return BG_FIELD;
        return switch (estado) {
            case "Disponible"    -> GREEN_PALE;
            case "Ocupada"       -> AMBER_PALE;
            case "Mantenimiento" -> RED_PALE;
            case "Reservada"     -> VIOLET_PALE;
            default              -> BG_FIELD;
        };
    }

    public static int progEstado(String estado) {
        if (estado == null) return 10;
        return switch (estado) {
            case "Disponible"    -> 100;
            case "Ocupada"       -> 72;
            case "Reservada"     -> 48;
            case "Mantenimiento" -> 12;
            default              -> 10;
        };
    }

    public static String descEstado(String estado) {
        if (estado == null) return "Sin estado asignado";
        return switch (estado) {
            case "Disponible"    -> "Lista para usar";
            case "Ocupada"       -> "Actualmente en uso";
            case "Mantenimiento" -> "Fuera de servicio";
            case "Reservada"     -> "Reservada";
            default              -> estado;
        };
    }

    // ══════════════════════════════════════════════════════════════════
    //  STAT CARD — blanca con sombra azulada y acento lateral
    // ══════════════════════════════════════════════════════════════════
    public static class StatCardAnimada extends JPanel {
        private final Color acento;
        private float glowPulse = 0f;
        private boolean glowUp  = true;
        private final Timer pulseTimer;

        public StatCardAnimada(String titulo, JLabel valorLbl, Color acento, String sub) {
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
                if (glowPulse <= 0f) { glowPulse = 0f; glowUp = true;  }
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

            // Borde suave
            g2.setColor(BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

            // Acento izquierdo animado
            int barH = (int)(h * 0.55f);
            int barY = (h - barH) / 2;
            g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(),
                    (int)(180 + glowPulse * 60)));
            g2.fillRoundRect(0, barY, 4, barH, 4, 4);

            // Halo top-left suavísimo
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
    //  CABINA CARD — blanca con hover azulado y selección con sombra
    // ══════════════════════════════════════════════════════════════════
    public static class CabinaCard extends JPanel {
        private float hoverT   = 0f;
        private final boolean seleccionada;
        private final Color   acento;

        public CabinaCard(boolean seleccionada, Color acento) {
            this.seleccionada = seleccionada;
            this.acento = acento;
            setOpaque(false);
            setLayout(new BorderLayout(0, 10));
            setBorder(new EmptyBorder(17, 19, 15, 19));
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
            int shadowAlpha = (int)(8 + t * 28);
            g2.setColor(new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(), shadowAlpha));
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

            // Franja top coloreada si seleccionada
            if (seleccionada) {
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 220));
                g2.fillRoundRect(0, 0, w, 4, 16, 16);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 220));
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
    //  PÍLDORA DE ESTADO
    // ══════════════════════════════════════════════════════════════════
    public static class PildoraEstado extends JPanel {
        private final String texto;
        private final Color  fg;
        private final Color  bg;

        public PildoraEstado(String estado) {
            this.texto = estado != null ? estado : "—";
            this.fg    = colorEstado(estado);
            this.bg    = paleBgEstado(estado);
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
    //  BARRA DE PROGRESO — azul claro con fill animado
    // ══════════════════════════════════════════════════════════════════
    public static class BarraProgreso extends JPanel {
        private final Color acento;
        private final int   target;
        private float       current = 0f;
        private Timer       fillTimer;

        public BarraProgreso(String estado) {
            this.acento = colorEstado(estado);
            this.target = progEstado(estado);
            setOpaque(false);
            setPreferredSize(new Dimension(0, 5));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));

            fillTimer = new Timer(14, e -> {
                current += (target - current) * 0.09f;
                if (Math.abs(current - target) < 0.4f) { current = target; ((Timer)e.getSource()).stop(); }
                repaint();
            });
            fillTimer.setInitialDelay(200);
            fillTimer.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Track
            g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 25));
            g2.fillRoundRect(0, 0, getWidth(), 5, 5, 5);

            // Fill con gradiente
            int fill = (int)(getWidth() * current / 100f);
            if (fill > 1) {
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 160),
                        fill, 0, acento);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, fill, 5, 5, 5);
            }
            g2.dispose();
        }

        public void stopAnimations() { if (fillTimer != null) fillTimer.stop(); }
    }

    // ══════════════════════════════════════════════════════════════════
    //  BOTÓN MODERNO — tema claro con hover azul y ripple
    // ══════════════════════════════════════════════════════════════════
    public static class BtnCabina extends JPanel {
        private final String texto;
        private final boolean primary;
        private final Color   acento;
        private float hoverT     = 0f;
        private float rippleAlpha = 0f;
        private float rippleR    = 0f;
        private int   rippleX, rippleY;
        private Timer hoverTimer, rippleTimer;
        private final java.util.List<ActionListener> listeners = new java.util.ArrayList<>();

        public BtnCabina(String texto, boolean primary) {
            this(texto, primary, primary ? INDIGO : BORDER_STRONG);
        }

        public BtnCabina(String texto, boolean primary, Color acento) {
            this.texto   = texto;
            this.primary = primary;
            this.acento  = acento;
            setOpaque(false);
            setPreferredSize(new Dimension(130, 38));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { anim(true); }
                @Override public void mouseExited(MouseEvent e)  { anim(false); }
                @Override public void mousePressed(MouseEvent e) {
                    rippleX = e.getX(); rippleY = e.getY();
                    rippleR = 0f; rippleAlpha = 0.35f;
                    animRipple();
                }
                @Override public void mouseClicked(MouseEvent e) {
                    ActionEvent ae = new ActionEvent(BtnCabina.this, ActionEvent.ACTION_PERFORMED, "");
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
                // Fondo índigo sólido que se aclara en hover
                Color base = blend(INDIGO, INDIGO_LIGHT, hoverT * 0.45f);
                g2.setColor(base);
                g2.fillRoundRect(0, 0, w, h, 10, 10);

                // Sombra azul suave
                if (hoverT > 0) {
                    g2.setColor(new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(), (int)(hoverT * 50)));
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawRoundRect(-1, 1, w + 1, h, 11, 11);
                }
            } else {
                // Fondo transparente que se rellena azul pálido en hover
                Color bg = new Color(acento.getRed(), acento.getGreen(), acento.getBlue(),
                        (int)(hoverT * 30));
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, 10, 10);

                Color borderC = new Color(acento.getRed(), acento.getGreen(), acento.getBlue(),
                        (int)(80 + hoverT * 120));
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(borderC);
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
                    : new Color(acento.getRed(), acento.getGreen(), acento.getBlue(),
                                (int)(160 + hoverT * 95)));
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
    //  GLASS PANEL — blanco con borde azul claro
    // ══════════════════════════════════════════════════════════════════
    public static class GlassPanel extends JPanel {
        private final int   radius;
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

            // Sombra suave
            g2.setColor(new Color(0x3B6EF8, true));
            g2.setColor(new Color(59, 110, 248, 12));
            g2.fillRoundRect(2, 5, w - 4, h, radius, radius);

            // Fondo blanco
            g2.setColor(BG_CARD);
            g2.fillRoundRect(0, 0, w, h, radius, radius);

            // Borde
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);

            // Franja top
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
    //  RANKING ROW — fila animada estilo tarjeta plana
    // ══════════════════════════════════════════════════════════════════
    public static class RankingRow extends JPanel {
        private final Color acento;
        private float alpha = 0f;

        public RankingRow(int pos, String estado, long cantidad, int delayMs) {
            this.acento = pos == 1 ? INDIGO : pos == 2 ? SKY : INDIGO_LIGHT;
            setOpaque(false);
            setLayout(new BorderLayout(10, 0));
            setBorder(new EmptyBorder(9, 12, 9, 12));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setAlignmentX(LEFT_ALIGNMENT);

            // Número en círculo
            JLabel numLbl = new JLabel(String.valueOf(pos), SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 22));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(acento);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            numLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            numLbl.setForeground(acento);
            numLbl.setOpaque(false);
            numLbl.setPreferredSize(new Dimension(24, 24));

            JLabel estadoLbl = lbl(estado, 12, true, TEXT_PRI);
            JLabel cntLbl    = lbl(cantidad + " cab.", 10, false, colorEstado(estado));

            add(numLbl,    BorderLayout.WEST);
            add(estadoLbl, BorderLayout.CENTER);
            add(cntLbl,    BorderLayout.EAST);

            // Animación de entrada (fade + slide)
            new Timer(12, null) {{
                setInitialDelay(delayMs);
                addActionListener(e -> {
                    alpha = Math.min(1f, alpha + 0.08f);
                    repaint();
                    if (alpha >= 1f) stop();
                });
                start();
            }};
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(BG_CARD_ALT.getRed(), BG_CARD_ALT.getGreen(),
                    BG_CARD_ALT.getBlue(), (int)(alpha * 255)));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.setColor(new Color(BORDER.getRed(), BORDER.getGreen(), BORDER.getBlue(), (int)(alpha * 200)));
            g2.setStroke(new BasicStroke(0.8f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            // Acento izquierdo
            g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), (int)(alpha * 220)));
            g2.fillRoundRect(0, 5, 3, getHeight() - 10, 3, 3);
            g2.dispose();

            Composite prev = ((Graphics2D)g).getComposite();
            ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paintComponent(g);
            ((Graphics2D)g).setComposite(prev);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  CAMPO ELEGANTE — blanco con focus azul
    // ══════════════════════════════════════════════════════════════════
    public static class CampoElegante extends JPanel {
        private final JTextField field;
        private float focusT = 0f;
        private Timer focusTimer;

        public CampoElegante(String placeholder, Color focusColor) {
            setOpaque(false);
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(0, 42));

            field = new JTextField() {
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
            field.setCaretColor(focusColor);

            field.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { animFocus(true); }
                @Override public void focusLost(FocusEvent e)   { animFocus(false); }
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

        public String getText() { return field.getText(); }
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
    //  HEADER DECORATIVO — línea con puntos azules flotantes
    // ══════════════════════════════════════════════════════════════════
    public static class HeaderDecorativo extends JPanel {
        private final int[]   px, py;
        private final float[] pa, ps;
        private final Timer   t;

        public HeaderDecorativo() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 5));
            int n = 10;
            px = new int[n]; py = new int[n];
            pa = new float[n]; ps = new float[n];
            java.util.Random r = new java.util.Random(42);
            for (int i = 0; i < n; i++) {
                px[i] = r.nextInt(500); py[i] = r.nextInt(3);
                pa[i] = r.nextFloat(); ps[i] = 0.007f + r.nextFloat() * 0.008f;
            }
            t = new Timer(60, e -> {
                for (int i = 0; i < n; i++) {
                    pa[i] += ps[i];
                    if (pa[i] > 1f) { pa[i] = 0f; px[i] = r.nextInt(600); }
                }
                repaint();
            });
            t.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Línea base
            g2.setColor(new Color(BORDER_FOCUS.getRed(), BORDER_FOCUS.getGreen(), BORDER_FOCUS.getBlue(), 60));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(0, 2, getWidth(), 2);
            // Puntos flotantes
            for (int i = 0; i < px.length; i++) {
                int alpha = (int)(pa[i] * 180);
                g2.setColor(new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(), alpha));
                g2.fillOval(px[i] % Math.max(1, getWidth()), py[i], 3, 3);
            }
            g2.dispose();
        }

        public void stopAnimations() { t.stop(); }
    }

    // ══════════════════════════════════════════════════════════════════
    //  HELPERS GLOBALES
    // ══════════════════════════════════════════════════════════════════
    public static BtnCabina btnAccion(String texto, boolean primary, int ancho) {
        BtnCabina b = new BtnCabina(texto, primary);
        b.setPreferredSize(new Dimension(ancho, 38));
        return b;
    }

    public static BtnCabina btnAccion(String texto, boolean primary, int ancho, Color acento) {
        BtnCabina b = new BtnCabina(texto, primary, acento);
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