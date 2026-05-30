package css;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Function;

import static css.SesionStyles.*;

/**
 * Componentes visuales animados reutilizables para formSesion.
 * Banda animada, campos con focus, combos, botones, card de costo y línea shimmer.
 */
public final class SesionComponents {

    private SesionComponents() {}

    // ════════════════════════════════════════════════════════════════
    //  AVATAR REDONDEADO
    // ════════════════════════════════════════════════════════════════
    public static JComponent avatar(String txt, Color color) {
        JLabel a = new JLabel(txt, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        a.setFont(new Font("Segoe UI", Font.BOLD, 13));
        a.setForeground(Color.WHITE);
        a.setOpaque(false);
        a.setPreferredSize(new Dimension(38, 38));
        return a;
    }

    // ════════════════════════════════════════════════════════════════
    //  BANDA SUPERIOR ANIMADA (partículas musicales)
    // ════════════════════════════════════════════════════════════════
    public static class BandaAnimada extends JPanel {
        private final float[] px = new float[18], py = new float[18];
        private final float[] pv = new float[18];
        private final String[] sym = {"♪", "♫", "♬", "♩"};
        private final int[] pSym = new int[18];
        private float bounce = 0f;

        public BandaAnimada(boolean isEdit, List<Timer> timers) {
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
                    if (px[i] >  1.1f) { px[i] = -0.1f; }
                }
                bounce = (float) (Math.sin(System.currentTimeMillis() / 700.0) * 0.5 + 0.5);
                repaint();
            });
            timers.add(t);
            t.start();

            JLabel ico = new JLabel(isEdit ? "✏" : "📅", SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int pulse = (int) (bounce * 25);
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
            JLabel t1 = lbl(isEdit ? "Editar sesión" : "Nueva sesión",
                    new Font("Segoe UI", Font.BOLD, 22), Color.WHITE);
            JLabel t2 = lbl(isEdit ? "ACTUALIZA LA INFORMACIÓN DE LA SESIÓN"
                                   : "REGISTRA UNA NUEVA SESIÓN DE GRABACIÓN",
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

            g2.setPaint(new GradientPaint(0, 0, new Color(0x0A2A4F),
                    getWidth(), getHeight(), new Color(0x14467E)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 30),
                    0, getHeight(), new Color(255, 255, 255, 0)));
            g2.fillRect(0, 0, getWidth(), getHeight() / 2);

            g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
            for (int i = 0; i < px.length; i++) {
                int alpha = 30 + (int) (Math.sin(py[i] * Math.PI) * 40);
                g2.setColor(new Color(C_ACCENT_CYAN.getRed(), C_ACCENT_CYAN.getGreen(),
                        C_ACCENT_CYAN.getBlue(), Math.max(0, Math.min(80, alpha))));
                int x = (int) (px[i] * getWidth());
                int y = (int) (py[i] * getHeight());
                g2.drawString(sym[pSym[i]], x, y);
            }

            g2.setColor(C_ACCENT_CYAN);
            g2.fillRect(0, getHeight() - 2, getWidth(), 2);
            g2.dispose();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  LÍNEA SHIMMER (separadores de sección)
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
            g2.setColor(new Color(C_ACCENT_CYAN.getRed(), C_ACCENT_CYAN.getGreen(),
                    C_ACCENT_CYAN.getBlue(), 50));
            g2.fillRect(0, h - 2, w, 2);
            float cx = phase * w;
            g2.setPaint(new java.awt.RadialGradientPaint(
                    new java.awt.geom.Point2D.Float(cx, h - 1),
                    Math.max(1f, w * 0.18f),
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 255, 255, 200),
                            new Color(C_ACCENT_CYAN.getRed(), C_ACCENT_CYAN.getGreen(),
                                    C_ACCENT_CYAN.getBlue(), 0)}));
            g2.fillRect(0, h - 2, w, 2);
            g2.dispose();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  CAMPO DE TEXTO CON FOCUS ANIMADO + PLACEHOLDER + SHAKE
    // ════════════════════════════════════════════════════════════════
    public static class FieldFx extends JTextField {
        private float focusAnim = 0f;
        private boolean focoActivo = false;
        private float shakeOff = 0f;
        private final String placeholder;
        private final List<Timer> timersOwner;

        public FieldFx(String value, String ph, List<Timer> timers) {
            super(value);
            this.placeholder = ph;
            this.timersOwner = timers;
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(C_TEXT_PRI);
            setCaretColor(C_ACCENT_CYAN);
            setBorder(new EmptyBorder(8, 14, 8, 14));
            setPreferredSize(new Dimension(0, 40));

            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override public void focusGained(java.awt.event.FocusEvent e) { focoActivo = true;  animarFoco(); }
                @Override public void focusLost  (java.awt.event.FocusEvent e) { focoActivo = false; animarFoco(); }
            });
        }

        private void animarFoco() {
            Timer t = new Timer(16, null);
            final long ini = System.currentTimeMillis();
            final float desde = focusAnim;
            final float hasta = focoActivo ? 1f : 0f;
            final int dur = 180;
            t.addActionListener(ev -> {
                float p = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
                float eased = 1f - (float) Math.pow(1 - p, 3);
                focusAnim = desde + (hasta - desde) * eased;
                repaint();
                if (p >= 1f) t.stop();
            });
            timersOwner.add(t);
            t.start();
        }

        /** Sacude el campo horizontalmente (validación fallida). */
        public void shake() {
            Timer t = new Timer(16, null);
            final long ini = System.currentTimeMillis();
            final int dur = 360;
            t.addActionListener(ev -> {
                float p = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
                shakeOff = (float) (Math.sin(p * Math.PI * 5) * (1 - p) * 6);
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
                int a = (int) (focusAnim * 70);
                g2.setColor(new Color(C_ACCENT_BLUE.getRed(), C_ACCENT_BLUE.getGreen(),
                        C_ACCENT_BLUE.getBlue(), a));
                g2.fillRoundRect(sh, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
            Color bg = blend(C_FIELD_BG, C_FIELD_BG_FOC, focusAnim);
            g2.setColor(bg);
            g2.fillRoundRect(sh + 2, 2, getWidth() - 5, getHeight() - 5, 10, 10);
            Color borde = blend(C_BORDER, C_ACCENT_CYAN, focusAnim);
            g2.setColor(borde);
            g2.setStroke(new BasicStroke(1f + focusAnim * 0.6f));
            g2.drawRoundRect(sh + 2, 2, getWidth() - 6, getHeight() - 6, 10, 10);
            g2.dispose();

            if (getText().isEmpty() && !focoActivo && placeholder != null) {
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g3.setFont(getFont());
                g3.setColor(C_TEXT_MUT);
                FontMetrics fm = g3.getFontMetrics();
                g3.drawString(placeholder, sh + 16, (getHeight() + fm.getAscent()) / 2 - 2);
                g3.dispose();
            }
            super.paintComponent(g);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  COMBO CON ANIMACIÓN DE FOCUS
    // ════════════════════════════════════════════════════════════════
    public static class ComboFx<T> extends JComboBox<T> {
        private float focusAnim = 0f;
        private boolean focoActivo = false;
        private final List<Timer> timersOwner;

        public ComboFx(T[] items, List<Timer> timers) {
            super(items);
            this.timersOwner = timers;
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(C_TEXT_PRI);
            setBackground(C_FIELD_BG);
            setBorder(new EmptyBorder(0, 12, 0, 0));
            setPreferredSize(new Dimension(0, 40));
            setFocusable(false);
            setMaximumRowCount(6);

            addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
                public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e)   { focoActivo = true;  animarFoco(); }
                public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) { focoActivo = false; animarFoco(); }
                public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e)            { focoActivo = false; animarFoco(); }
            });
        }

        private void animarFoco() {
            Timer t = new Timer(16, null);
            final long ini = System.currentTimeMillis();
            final float desde = focusAnim;
            final float hasta = focoActivo ? 1f : 0f;
            final int dur = 180;
            t.addActionListener(ev -> {
                float p = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
                float eased = 1f - (float) Math.pow(1 - p, 3);
                focusAnim = desde + (hasta - desde) * eased;
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
                int a = (int) (focusAnim * 70);
                g2.setColor(new Color(C_ACCENT_BLUE.getRed(), C_ACCENT_BLUE.getGreen(),
                        C_ACCENT_BLUE.getBlue(), a));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
            g2.setColor(blend(C_FIELD_BG, C_FIELD_BG_FOC, focusAnim));
            g2.fillRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 10, 10);
            g2.setColor(blend(C_BORDER, C_ACCENT_CYAN, focusAnim));
            g2.setStroke(new BasicStroke(1f + focusAnim * 0.6f));
            g2.drawRoundRect(2, 2, getWidth() - 6, getHeight() - 6, 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Factories de combos ────────────────────────────────────────
    public static <T> ComboFx<T> comboFxObj(T[] items, Function<Object, String> labelFn, List<Timer> timers) {
        ComboFx<T> cb = new ComboFx<>(items, timers);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean s, boolean f) {
                JLabel c = new JLabel(labelFn.apply(v));
                c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                c.setBackground(s && i != -1 ? C_PRIMARY : C_FIELD_BG);
                c.setForeground(C_TEXT_PRI);
                c.setBorder(new EmptyBorder(8, 12, 8, 12));
                c.setOpaque(true);
                return c;
            }
        });
        return cb;
    }

    public static ComboFx<String> comboFx(String[] opts, String sel, List<Timer> timers) {
        ComboFx<String> cb = new ComboFx<>(opts, timers);
        cb.setSelectedItem(sel);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean s, boolean f) {
                JLabel c = new JLabel(v == null ? "" : v.toString());
                c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                c.setBackground(s && i != -1 ? C_PRIMARY : C_FIELD_BG);
                c.setForeground(C_TEXT_PRI);
                c.setBorder(new EmptyBorder(8, 12, 8, 12));
                c.setOpaque(true);
                return c;
            }
        });
        return cb;
    }

    // ════════════════════════════════════════════════════════════════
    //  CARD DE COSTO ANIMADA
    // ════════════════════════════════════════════════════════════════
    public static class CardCostoFx extends JPanel {
        private double valorMostrado = 0;
        private double valorObjetivo = 0;
        private float flash = 0f;
        private final JLabel lblValor = new JLabel("$0.00");
        private final JLabel lblMeta  = new JLabel("—");
        private final List<Timer> timersOwner;

        public CardCostoFx(List<Timer> timers) {
            this.timersOwner = timers;
            setOpaque(false);
            setLayout(new BorderLayout(14, 0));
            setBorder(new EmptyBorder(16, 20, 16, 20));
            setAlignmentX(LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 82));

            JLabel ico = new JLabel("💰", SwingConstants.CENTER);
            ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            ico.setPreferredSize(new Dimension(40, 40));

            JPanel txt = new JPanel();
            txt.setOpaque(false);
            txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
            JLabel lTit = lbl("COSTO ESTIMADO",
                    new Font("Segoe UI", Font.BOLD, 10), C_ACCENT_CYAN);
            lTit.setAlignmentX(LEFT_ALIGNMENT);
            lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblMeta.setForeground(C_TEXT_MUT);
            lblMeta.setAlignmentX(LEFT_ALIGNMENT);
            txt.add(lTit);
            txt.add(Box.createVerticalStrut(3));
            txt.add(lblMeta);

            lblValor.setFont(new Font("Segoe UI", Font.BOLD, 26));
            lblValor.setForeground(C_OK);
            lblValor.setHorizontalAlignment(SwingConstants.RIGHT);

            add(ico,      BorderLayout.WEST);
            add(txt,      BorderLayout.CENTER);
            add(lblValor, BorderLayout.EAST);
        }

        public void setMeta(String s) { lblMeta.setText(s); }

        public void setValor(double v, boolean animar) {
            valorObjetivo = v;
            if (!animar) {
                valorMostrado = v;
                lblValor.setText(String.format("$%,.2f", v));
                repaint();
                return;
            }
            final double desde = valorMostrado;
            final double delta = v - desde;
            Timer t = new Timer(16, null);
            final long ini = System.currentTimeMillis();
            final int dur = 380;
            t.addActionListener(ev -> {
                float p = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
                float eased = 1f - (float) Math.pow(1 - p, 3);
                valorMostrado = desde + delta * eased;
                lblValor.setText(String.format("$%,.2f", valorMostrado));
                if (p >= 1f) { valorMostrado = valorObjetivo; t.stop(); }
            });
            timersOwner.add(t);
            t.start();

            Timer tf = new Timer(16, null);
            final long iniF = System.currentTimeMillis();
            final int durF = 420;
            tf.addActionListener(ev -> {
                float p = Math.min(1f, (System.currentTimeMillis() - iniF) / (float) durF);
                flash = (float) Math.sin(p * Math.PI);
                repaint();
                if (p >= 1f) { flash = 0f; tf.stop(); }
            });
            timersOwner.add(tf);
            tf.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            float a = appearAlpha(this);
            if (a < 1f) g2.setComposite(
                    java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, a));
            g2.setPaint(new GradientPaint(0, 0,
                    new Color(C_PRIMARY.getRed(), C_PRIMARY.getGreen(), C_PRIMARY.getBlue(),
                            55 + (int) (flash * 60)),
                    getWidth(), 0, new Color(0x061829)));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.setColor(new Color(C_ACCENT_CYAN.getRed(), C_ACCENT_CYAN.getGreen(),
                    C_ACCENT_CYAN.getBlue(), 120 + (int) (flash * 80)));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setColor(C_ACCENT_CYAN);
            g2.fillRoundRect(0, 14, 4, getHeight() - 28, 4, 4);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  BOTÓN ANIMADO CON SHIMMER
    // ════════════════════════════════════════════════════════════════
    public static class BtnFx extends JButton {
        private final boolean primary;
        private float hoverAnim = 0f;
        private boolean hovered = false;
        private float shimmerX = -0.3f;
        private final List<Timer> timersOwner;

        public BtnFx(String text, boolean primary, List<Timer> timers) {
            super(text);
            this.primary = primary;
            this.timersOwner = timers;
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(primary ? Color.WHITE : C_TEXT_PRI);
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
            final long ini = System.currentTimeMillis();
            final float desde = hoverAnim;
            final float hasta = hovered ? 1f : 0f;
            final int dur = 160;
            t.addActionListener(ev -> {
                float p = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
                float eased = 1f - (float) Math.pow(1 - p, 3);
                hoverAnim = desde + (hasta - desde) * eased;
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
                Color base = getModel().isPressed() ? new Color(0x0D4A8E) : C_PRIMARY;
                Color lift = blend(base, new Color(0x2D85D8), hoverAnim);
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
                Color base = blend(new Color(0x071E30), new Color(0x0E3257), hoverAnim);
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(blend(C_BORDER, C_ACCENT_BLUE, hoverAnim));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════

    /** Devuelve el alpha de aparición (1f si no hay animación en curso). */
    public static float appearAlpha(JComponent c) {
        Object o = c.getClientProperty("fx_appear");
        return (o instanceof Float) ? (Float) o : 1f;
    }

    public static JLabel lbl(String t, Font f, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }
}