package css;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static css.EventoStyles.*;

/**
 * EventoComponents.java — Componentes visuales reutilizables para formEvento.
 * ─────────────────────────────────────────────────────────────────────────────
 * Sigue el mismo patrón que SesionComponents.
 * Incluye: BandaEvento, LineaShimmer, FieldFx, ComboFx, BtnFx.
 */
public final class EventoComponents {

    private EventoComponents() {}

    // ════════════════════════════════════════════════════════════════
    //  BANDA SUPERIOR ANIMADA (partículas de eventos)
    // ════════════════════════════════════════════════════════════════
    public static class BandaEvento extends JPanel {
        private final float[] px = new float[16], py = new float[16];
        private final float[] pv = new float[16];
        private final String[] sym = {"🎟", "🎸", "🎧", "🎙", "🥁", "🚀"};
        private final int[]   pSym = new int[16];
        private float bounce = 0f;

        public BandaEvento(boolean isEdit, List<Timer> timers) {
            setOpaque(false);
            setLayout(new BorderLayout(14, 0));
            setBorder(new EmptyBorder(20, 26, 20, 26));
            setPreferredSize(new Dimension(0, 100));

            java.util.Random r = new java.util.Random();
            for (int i = 0; i < px.length; i++) {
                px[i] = r.nextFloat();
                py[i] = r.nextFloat();
                pv[i] = 0.0008f + r.nextFloat() * 0.0012f;
                pSym[i] = r.nextInt(sym.length);
            }

            Timer t = new Timer(40, e -> {
                for (int i = 0; i < px.length; i++) {
                    py[i] -= pv[i];
                    px[i] += pv[i] * 0.3f;
                    if (py[i] < -0.1f) { py[i] = 1.1f; px[i] = (float) Math.random(); }
                    if (px[i] >  1.1f)  px[i] = -0.1f;
                }
                bounce = (float)(Math.sin(System.currentTimeMillis() / 700.0) * 0.5 + 0.5);
                repaint();
            });
            timers.add(t);
            t.start();

            // Ícono pulsante
            JLabel ico = new JLabel(isEdit ? "✏" : "🎟", SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int pulse = (int)(bounce * 25);
                    g2.setColor(new Color(255, 255, 255, 30 + pulse));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 13, 13);
                    g2.setColor(new Color(255, 255, 255, 80 + pulse));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 13, 13);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            ico.setForeground(Color.WHITE);
            ico.setPreferredSize(new Dimension(54, 54));

            JPanel txt = new JPanel();
            txt.setOpaque(false);
            txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
            JLabel t1 = lbl(isEdit ? "Editar evento" : "Nuevo evento",
                    new Font("Segoe UI", Font.BOLD, 22), Color.WHITE);
            JLabel t2 = lbl(isEdit ? "ACTUALIZA LA INFORMACIÓN DEL EVENTO"
                                   : "REGISTRA UN NUEVO EVENTO",
                    new Font("Segoe UI", Font.BOLD, 9), new Color(255, 255, 255, 185));
            t1.setAlignmentX(LEFT_ALIGNMENT);
            t2.setAlignmentX(LEFT_ALIGNMENT);
            txt.add(Box.createVerticalGlue());
            txt.add(t1);
            txt.add(Box.createVerticalStrut(3));
            txt.add(t2);
            txt.add(Box.createVerticalGlue());

            add(ico, BorderLayout.WEST);
            add(txt, BorderLayout.CENTER);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo degradado azul (igual que Sesion)
            g2.setPaint(new GradientPaint(0, 0, new Color(0x0A2A4F),
                    getWidth(), getHeight(), new Color(0x14467E)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Brillo superior
            g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 30),
                    0, getHeight(), new Color(255, 255, 255, 0)));
            g2.fillRect(0, 0, getWidth(), getHeight() / 2);

            // Partículas flotantes
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            for (int i = 0; i < px.length; i++) {
                int alpha = 25 + (int)(Math.sin(py[i] * Math.PI) * 35);
                g2.setColor(new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(),
                        Math.max(0, Math.min(70, alpha))));
                int x = (int)(px[i] * getWidth());
                int y = (int)(py[i] * getHeight());
                g2.drawString(sym[pSym[i]], x, y);
            }

            // Línea cyan inferior
            g2.setColor(SKY);
            g2.fillRect(0, getHeight() - 2, getWidth(), 2);
            g2.dispose();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  LÍNEA SHIMMER — igual que SesionComponents
    // ════════════════════════════════════════════════════════════════
    public static class LineaShimmer extends JPanel {
        private float phase = 0f;

        public LineaShimmer(List<Timer> timers) {
            setOpaque(false);
            Timer t = new Timer(40, e -> {
                phase += 0.012f;
                if (phase > 1.4f) phase = -0.4f;
                repaint();
            });
            timers.add(t);
            t.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(BORDER_FOCUS.getRed(), BORDER_FOCUS.getGreen(),
                    BORDER_FOCUS.getBlue(), 50));
            g2.fillRect(0, h - 2, w, 2);
            float cx = phase * w;
            g2.setPaint(new java.awt.RadialGradientPaint(
                    new java.awt.geom.Point2D.Float(cx, h - 1),
                    Math.max(1f, w * 0.18f),
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 255, 255, 200),
                            new Color(BORDER_FOCUS.getRed(), BORDER_FOCUS.getGreen(),
                                    BORDER_FOCUS.getBlue(), 0)}));
            g2.fillRect(0, h - 2, w, 2);
            g2.dispose();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  CAMPO CON FOCUS ANIMADO + PLACEHOLDER + SHAKE
    // ════════════════════════════════════════════════════════════════
    public static class FieldFx extends JTextField {
        private float   focusAnim   = 0f;
        private boolean focoActivo  = false;
        private float   shakeOff    = 0f;
        private final String placeholder;
        private final List<Timer> timersOwner;

        public FieldFx(String value, String ph, List<Timer> timers) {
            super(value);
            this.placeholder  = ph;
            this.timersOwner  = timers;
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(TEXT_PRI);
            setCaretColor(BORDER_FOCUS);
            setBorder(new EmptyBorder(8, 14, 8, 14));
            setPreferredSize(new Dimension(0, 40));

            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focoActivo = true;  animarFoco(); }
                @Override public void focusLost  (FocusEvent e) { focoActivo = false; animarFoco(); }
            });
        }

        private void animarFoco() {
            Timer t = new Timer(16, null);
            final long  ini   = System.currentTimeMillis();
            final float desde = focusAnim;
            final float hasta = focoActivo ? 1f : 0f;
            final int   dur   = 180;
            t.addActionListener(ev -> {
                float p     = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
                float eased = 1f - (float) Math.pow(1 - p, 3);
                focusAnim   = desde + (hasta - desde) * eased;
                repaint();
                if (p >= 1f) t.stop();
            });
            timersOwner.add(t);
            t.start();
        }

        /** Sacude el campo (validación fallida). */
        public void shake() {
            Timer t = new Timer(16, null);
            final long ini = System.currentTimeMillis();
            final int  dur = 360;
            t.addActionListener(ev -> {
                float p   = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
                shakeOff  = (float)(Math.sin(p * Math.PI * 5) * (1 - p) * 6);
                repaint();
                if (p >= 1f) { shakeOff = 0f; t.stop(); }
            });
            timersOwner.add(t);
            t.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int sh = (int) shakeOff;

            if (focusAnim > 0.01f) {
                g2.setColor(new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(),
                        (int)(focusAnim * 70)));
                g2.fillRoundRect(sh, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
            Color bg = blend(BG_FIELD, BG_SELECTED, focusAnim * 0.3f);
            g2.setColor(bg);
            g2.fillRoundRect(sh + 2, 2, getWidth() - 5, getHeight() - 5, 10, 10);
            Color borde = blend(BORDER, BORDER_FOCUS, focusAnim);
            g2.setColor(borde);
            g2.setStroke(new BasicStroke(1f + focusAnim * 0.6f));
            g2.drawRoundRect(sh + 2, 2, getWidth() - 6, getHeight() - 6, 10, 10);
            g2.dispose();

            if (getText().isEmpty() && !focoActivo && placeholder != null) {
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setFont(getFont());
                g3.setColor(TEXT_MUT);
                FontMetrics fm = g3.getFontMetrics();
                g3.drawString(placeholder, sh + 16, (getHeight() + fm.getAscent()) / 2 - 2);
                g3.dispose();
            }
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

    // ════════════════════════════════════════════════════════════════
    //  COMBO CON FOCUS ANIMADO
    // ════════════════════════════════════════════════════════════════
    public static class ComboFx<T> extends JComboBox<T> {
        private float   focusAnim  = 0f;
        private boolean focoActivo = false;
        private final List<Timer> timersOwner;

        public ComboFx(T[] items, List<Timer> timers) {
            super(items);
            this.timersOwner = timers;
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(TEXT_PRI);
            setBackground(BG_FIELD);
            setBorder(new EmptyBorder(0, 12, 0, 0));
            setPreferredSize(new Dimension(0, 40));
            setFocusable(false);
            setMaximumRowCount(6);

            addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
                public void popupMenuWillBecomeVisible  (javax.swing.event.PopupMenuEvent e) { focoActivo = true;  animarFoco(); }
                public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) { focoActivo = false; animarFoco(); }
                public void popupMenuCanceled           (javax.swing.event.PopupMenuEvent e) { focoActivo = false; animarFoco(); }
            });
        }

        private void animarFoco() {
            Timer t = new Timer(16, null);
            final long  ini   = System.currentTimeMillis();
            final float desde = focusAnim;
            final float hasta = focoActivo ? 1f : 0f;
            final int   dur   = 180;
            t.addActionListener(ev -> {
                float p     = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
                float eased = 1f - (float) Math.pow(1 - p, 3);
                focusAnim   = desde + (hasta - desde) * eased;
                repaint();
                if (p >= 1f) t.stop();
            });
            timersOwner.add(t);
            t.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (focusAnim > 0.01f) {
                g2.setColor(new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(),
                        (int)(focusAnim * 70)));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
            g2.setColor(blend(BG_FIELD, BG_SELECTED, focusAnim * 0.3f));
            g2.fillRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 10, 10);
            g2.setColor(blend(BORDER, BORDER_FOCUS, focusAnim));
            g2.setStroke(new BasicStroke(1f + focusAnim * 0.6f));
            g2.drawRoundRect(2, 2, getWidth() - 6, getHeight() - 6, 10, 10);
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

    // ── Factory combo con renderer personalizado ──────────────────
    public static ComboFx<String> comboFx(String[] opts, String sel, List<Timer> timers) {
        ComboFx<String> cb = new ComboFx<>(opts, timers);
        cb.setSelectedItem(sel);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean s, boolean f) {
                JLabel c = new JLabel(v == null ? "" : v.toString());
                c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                c.setBackground(s && i != -1 ? INDIGO : BG_FIELD);
                c.setForeground(s && i != -1 ? Color.WHITE : TEXT_PRI);
                c.setBorder(new EmptyBorder(8, 12, 8, 12));
                c.setOpaque(true);
                return c;
            }
        });
        return cb;
    }

    // ════════════════════════════════════════════════════════════════
    //  BOTÓN ANIMADO CON SHIMMER
    // ════════════════════════════════════════════════════════════════
    public static class BtnFx extends JButton {
        private final boolean primary;
        private float hoverAnim = 0f;
        private boolean hovered = false;
        private float shimmerX  = -0.3f;
        private final List<Timer> timersOwner;

        public BtnFx(String text, boolean primary, List<Timer> timers) {
            super(text);
            this.primary      = primary;
            this.timersOwner  = timers;
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(primary ? Color.WHITE : TEXT_PRI);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 18, 8, 18));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  animarHover(); }
                @Override public void mouseExited (MouseEvent e) { hovered = false; animarHover(); }
            });

            if (primary) {
                Timer t = new Timer(40, e -> {
                    shimmerX += 0.018f;
                    if (shimmerX > 1.3f) shimmerX = -0.3f;
                    repaint();
                });
                timersOwner.add(t);
                t.start();
            }
        }

        private void animarHover() {
            Timer t = new Timer(16, null);
            final long  ini   = System.currentTimeMillis();
            final float desde = hoverAnim;
            final float hasta = hovered ? 1f : 0f;
            final int   dur   = 160;
            t.addActionListener(ev -> {
                float p     = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
                float eased = 1f - (float) Math.pow(1 - p, 3);
                hoverAnim   = desde + (hasta - desde) * eased;
                repaint();
                if (p >= 1f) t.stop();
            });
            timersOwner.add(t);
            t.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (primary) {
                Color base = getModel().isPressed() ? new Color(0x1A4A9E) : INDIGO;
                Color lift = blend(base, INDIGO_LIGHT, hoverAnim * 0.45f);
                g2.setColor(lift);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 32),
                        0, getHeight() / 2f, new Color(0, 0, 0, 0)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 10, 10);
                float cx = shimmerX * getWidth();
                g2.setPaint(new java.awt.RadialGradientPaint(
                        new java.awt.geom.Point2D.Float(cx, getHeight() / 2f),
                        Math.max(1f, getWidth() * 0.25f),
                        new float[]{0f, 1f},
                        new Color[]{new Color(255, 255, 255, 55), new Color(255, 255, 255, 0)}));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            } else {
                Color base = blend(BG_CARD_ALT, BG_SELECTED, hoverAnim * 0.5f);
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(blend(BORDER, INDIGO, hoverAnim));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
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

    // ════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════
    public static JLabel lbl(String t, Font f, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }
}