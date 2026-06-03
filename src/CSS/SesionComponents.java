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
 * Paleta clara inspirada en el módulo de Canciones.
 * Animaciones temáticas: REC badge, waveform, VU meters.
 */
public final class SesionComponents {

    private SesionComponents() {}

    // ════════════════════════════════════════════════════════════════
    //  AVATAR REDONDEADO
    // ════════════════════════════════════════════════════════════════
    public static JComponent avatar(String txt, Color bgColor) {
        JLabel a = new JLabel(txt, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Fondo suave derivado del color del estado
                g2.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // Borde del color del estado
                g2.setColor(bgColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        a.setFont(new Font("Segoe UI", Font.BOLD, 13));
        a.setForeground(bgColor);
        a.setOpaque(false);
        a.setPreferredSize(new Dimension(38, 38));
        return a;
    }

    // ════════════════════════════════════════════════════════════════
    //  BANDA SUPERIOR ANIMADA — TEMA ESTUDIO DE GRABACIÓN
    //  Reemplaza el fondo azul oscuro por gradiente claro violeta/cyan
    //  con partículas musicales y línea waveform animada al fondo.
    // ════════════════════════════════════════════════════════════════
    public static class BandaAnimada extends JPanel {

        // Partículas musicales flotantes
        private final float[] px = new float[16], py = new float[16];
        private final float[] pv = new float[16];
        private final String[] sym = {"♪", "♫", "♬", "♩"};
        private final int[]   pSym = new int[16];

        // Waveform animada (barras)
        private final float[] waveH  = new float[32];
        private final float[] waveTarget = new float[32];
        private float bounce = 0f;

        // Badge REC: fase de parpadeo
        private boolean recVisible = true;
        private final JLabel recDot = new JLabel("●");

        public BandaAnimada(boolean isEdit, List<Timer> timers) {
            setOpaque(false);
            setLayout(new BorderLayout(14, 0));
            setBorder(new EmptyBorder(20, 26, 20, 26));
            setPreferredSize(new Dimension(0, 100));

            java.util.Random r = new java.util.Random();
            for (int i = 0; i < px.length; i++) {
                px[i]   = r.nextFloat();
                py[i]   = r.nextFloat();
                pv[i]   = 0.0006f + r.nextFloat() * 0.001f;
                pSym[i] = r.nextInt(sym.length);
            }
            for (int i = 0; i < waveH.length; i++) {
                waveH[i]      = 0.2f + r.nextFloat() * 0.6f;
                waveTarget[i] = 0.1f + r.nextFloat() * 0.8f;
            }

            // Timer principal: partículas + waveform
            Timer tAnim = new Timer(40, e -> {
                for (int i = 0; i < px.length; i++) {
                    py[i] -= pv[i];
                    px[i] += pv[i] * 0.25f;
                    if (py[i] < -0.1f) { py[i] = 1.1f; px[i] = (float) Math.random(); }
                    if (px[i] >  1.1f) { px[i] = -0.1f; }
                }
                for (int i = 0; i < waveH.length; i++) {
                    waveH[i] += (waveTarget[i] - waveH[i]) * 0.12f;
                    if (Math.abs(waveH[i] - waveTarget[i]) < 0.01f)
                        waveTarget[i] = 0.1f + (float) Math.random() * 0.85f;
                }
                bounce = (float) (Math.sin(System.currentTimeMillis() / 700.0) * 0.5 + 0.5);
                repaint();
            });
            timers.add(tAnim);
            tAnim.start();

            // Timer REC: parpadeo del punto rojo
            Timer tRec = new Timer(600, e -> {
                recVisible = !recVisible;
                recDot.setForeground(recVisible ? C_REC : new Color(0, 0, 0, 0));
            });
            timers.add(tRec);
            tRec.start();

            // ── Icono del diálogo ──
            JLabel ico = new JLabel(isEdit ? "✏" : "🎙", SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int pulse = (int) (bounce * 20);
                    // Fondo violeta suave
                    g2.setColor(new Color(0xEEEDFE));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 13, 13);
                    g2.setColor(new Color(C_PRIMARY.getRed(), C_PRIMARY.getGreen(),
                            C_PRIMARY.getBlue(), 100 + pulse));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 13, 13);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
            ico.setForeground(C_PRIMARY);
            ico.setPreferredSize(new Dimension(56, 56));

            // ── Texto del diálogo ──
            JPanel textPanel = new JPanel();
            textPanel.setOpaque(false);
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

            JLabel t1 = lbl(isEdit ? "Editar sesión" : "Nueva sesión",
                    new Font("Segoe UI", Font.BOLD, 22), C_TEXT_PRI);
            JLabel t2 = lbl(isEdit
                            ? "ACTUALIZA LA INFORMACIÓN DE LA SESIÓN"
                            : "REGISTRA UNA NUEVA SESIÓN DE GRABACIÓN",
                    new Font("Segoe UI", Font.BOLD, 9), C_TEXT_MUT);
            t1.setAlignmentX(LEFT_ALIGNMENT);
            t2.setAlignmentX(LEFT_ALIGNMENT);

            // Badge REC
            JPanel recBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            recBadge.setOpaque(false);
            recBadge.setAlignmentX(LEFT_ALIGNMENT);
            recDot.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            recDot.setForeground(C_REC);
            JLabel recText = new JLabel("REC");
            recText.setFont(new Font("Segoe UI", Font.BOLD, 10));
            recText.setForeground(C_REC);
            recBadge.add(recDot);
            recBadge.add(recText);

            textPanel.add(Box.createVerticalGlue());
            textPanel.add(t1);
            textPanel.add(Box.createVerticalStrut(3));
            textPanel.add(t2);
            textPanel.add(Box.createVerticalStrut(4));
            textPanel.add(recBadge);
            textPanel.add(Box.createVerticalGlue());

            add(ico,       BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo claro: blanco a violeta muy suave
            g2.setPaint(new GradientPaint(0, 0, new Color(0xFFFFFF),
                    getWidth(), getHeight(), new Color(0xF0EFFE)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Franja superior con acento violeta translúcido
            g2.setPaint(new GradientPaint(0, 0, new Color(0x534AB7, false) {
                { /* alpha override */ }
                @Override public int getAlpha() { return 18; }
            }, 0, getHeight() / 2, new Color(0, 0, 0, 0)));
            g2.fillRect(0, 0, getWidth(), getHeight() / 2);

            // Waveform animada al fondo (barras verticales centradas)
            int barW = 3, gap = 4;
            int total = waveH.length;
            int startX = getWidth() / 2 - (total * (barW + gap)) / 2;
            int midY   = getHeight() / 2;
            for (int i = 0; i < total; i++) {
                int h = (int) (waveH[i] * (getHeight() * 0.55f));
                int alpha = 25 + (int) (waveH[i] * 35);
                g2.setColor(new Color(C_PRIMARY.getRed(), C_PRIMARY.getGreen(),
                        C_PRIMARY.getBlue(), alpha));
                g2.fillRoundRect(startX + i * (barW + gap), midY - h / 2, barW, h, barW, barW);
            }

            // Partículas musicales flotantes (color violeta suave)
            g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 13));
            for (int i = 0; i < px.length; i++) {
                int alpha = 20 + (int) (Math.sin(py[i] * Math.PI) * 30);
                g2.setColor(new Color(C_ACCENT_CYAN.getRed(), C_ACCENT_CYAN.getGreen(),
                        C_ACCENT_CYAN.getBlue(), Math.max(0, Math.min(60, alpha))));
                g2.drawString(sym[pSym[i]],
                        (int) (px[i] * getWidth()),
                        (int) (py[i] * getHeight()));
            }

            // Línea inferior de acento violeta
            g2.setColor(C_PRIMARY);
            g2.fillRect(0, getHeight() - 2, getWidth(), 2);
            g2.dispose();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  LÍNEA SHIMMER — versión paleta clara
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
            // Línea base violeta suave
            g2.setColor(new Color(C_PRIMARY.getRed(), C_PRIMARY.getGreen(),
                    C_PRIMARY.getBlue(), 60));
            g2.fillRect(0, h - 2, w, 2);
            // Punto brillante que recorre la línea
            float cx = phase * w;
            g2.setPaint(new java.awt.RadialGradientPaint(
                    new java.awt.geom.Point2D.Float(cx, h - 1),
                    Math.max(1f, w * 0.18f),
                    new float[]{0f, 1f},
                    new Color[]{
                        new Color(C_ACCENT_CYAN.getRed(), C_ACCENT_CYAN.getGreen(),
                                C_ACCENT_CYAN.getBlue(), 220),
                        new Color(C_ACCENT_CYAN.getRed(), C_ACCENT_CYAN.getGreen(),
                                C_ACCENT_CYAN.getBlue(), 0)
                    }));
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
            this.placeholder  = ph;
            this.timersOwner  = timers;
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(C_TEXT_PRI);
            setCaretColor(C_PRIMARY);
            setBorder(new EmptyBorder(8, 14, 8, 14));
            setPreferredSize(new Dimension(0, 40));

            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override public void focusGained(java.awt.event.FocusEvent e) { focoActivo = true;  animarFoco(); }
                @Override public void focusLost  (java.awt.event.FocusEvent e) { focoActivo = false; animarFoco(); }
            });
        }

        private void animarFoco() {
            Timer t = new Timer(16, null);
            final long ini   = System.currentTimeMillis();
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

        public void shake() {
            Timer t = new Timer(16, null);
            final long ini = System.currentTimeMillis();
            final int  dur = 360;
            t.addActionListener(ev -> {
                float p  = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
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

            // Halo de foco violeta suave
            if (focusAnim > 0.01f) {
                int alpha = (int) (focusAnim * 50);
                g2.setColor(new Color(C_PRIMARY.getRed(), C_PRIMARY.getGreen(),
                        C_PRIMARY.getBlue(), alpha));
                g2.fillRoundRect(sh, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }

            // Fondo del campo
            Color bg = blend(C_FIELD_BG, C_FIELD_BG_FOC, focusAnim);
            g2.setColor(bg);
            g2.fillRoundRect(sh + 2, 2, getWidth() - 5, getHeight() - 5, 10, 10);

            // Borde: gris → violeta al enfocar
            Color borde = blend(C_BORDER, C_PRIMARY, focusAnim);
            g2.setColor(borde);
            g2.setStroke(new BasicStroke(1f + focusAnim * 0.7f));
            g2.drawRoundRect(sh + 2, 2, getWidth() - 6, getHeight() - 6, 10, 10);
            g2.dispose();

            // Placeholder
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
                g2.setColor(new Color(C_PRIMARY.getRed(), C_PRIMARY.getGreen(),
                        C_PRIMARY.getBlue(), (int) (focusAnim * 50)));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
            g2.setColor(blend(C_FIELD_BG, C_FIELD_BG_FOC, focusAnim));
            g2.fillRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 10, 10);
            g2.setColor(blend(C_BORDER, C_PRIMARY, focusAnim));
            g2.setStroke(new BasicStroke(1f + focusAnim * 0.7f));
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
                c.setBackground(s && i != -1 ? C_ROW_SEL : Color.WHITE);
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
                c.setBackground(s && i != -1 ? C_ROW_SEL : Color.WHITE);
                c.setForeground(C_TEXT_PRI);
                c.setBorder(new EmptyBorder(8, 12, 8, 12));
                c.setOpaque(true);
                return c;
            }
        });
        return cb;
    }

    // ════════════════════════════════════════════════════════════════
    //  CARD DE COSTO ANIMADA — paleta clara
    // ════════════════════════════════════════════════════════════════
    public static class CardCostoFx extends JPanel {
        private double valorMostrado = 0;
        private double valorObjetivo = 0;
        private float  flash         = 0f;
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
            ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
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
            final int  dur = 380;
            t.addActionListener(ev -> {
                float p     = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
                float eased = 1f - (float) Math.pow(1 - p, 3);
                valorMostrado = desde + delta * eased;
                lblValor.setText(String.format("$%,.2f", valorMostrado));
                if (p >= 1f) { valorMostrado = valorObjetivo; t.stop(); }
            });
            timersOwner.add(t);
            t.start();

            // Flash del borde
            Timer tf = new Timer(16, null);
            final long iniF = System.currentTimeMillis();
            final int  durF = 420;
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

            // Fondo: blanco a violeta suave con flash en cyan
            Color bgLeft  = blend(new Color(0xF0EFFE), new Color(0xE1F5EE), flash);
            Color bgRight = new Color(0xFFFFFF);
            g2.setPaint(new GradientPaint(0, 0, bgLeft, getWidth(), 0, bgRight));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

            // Borde: violeta con pulso cyan al cambiar valor
            Color borde = blend(C_BORDER,
                    new Color(C_ACCENT_CYAN.getRed(), C_ACCENT_CYAN.getGreen(),
                            C_ACCENT_CYAN.getBlue(), 180),
                    flash);
            g2.setColor(borde);
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

            // Acento lateral violeta
            g2.setColor(C_PRIMARY);
            g2.fillRoundRect(0, 10, 4, getHeight() - 20, 4, 4);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  BOTÓN ANIMADO CON SHIMMER — paleta clara
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
                    shimmerX += 0.016f;
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
                // Violeta sólido, más oscuro al presionar
                Color base = getModel().isPressed()
                        ? new Color(0x3C3489)
                        : blend(C_PRIMARY, new Color(0x7F77DD), hoverAnim);
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // Shimmer blanco
                float cx = shimmerX * getWidth();
                g2.setPaint(new java.awt.RadialGradientPaint(
                        new java.awt.geom.Point2D.Float(cx, getHeight() / 2f),
                        Math.max(1f, getWidth() * 0.22f),
                        new float[]{0f, 1f},
                        new Color[]{new Color(255, 255, 255, 50), new Color(255, 255, 255, 0)}));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            } else {
                // Ghost: fondo blanco con borde, hover violeta muy suave
                Color bg = blend(Color.WHITE, new Color(0xEEEDFE), hoverAnim);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(blend(C_BORDER, C_PRIMARY, hoverAnim));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  VU METER — componente de adorno para la banda
    //  Dibuja un medidor de nivel vertical animado (L o R).
    // ════════════════════════════════════════════════════════════════
    public static class VuMeter extends JPanel {
        private static final int SEGS = 10;
        private int nivel = 0;

        public VuMeter(String canal, List<Timer> timers) {
            setOpaque(false);
            setPreferredSize(new Dimension(14, 56));
            setToolTipText(canal);

            Timer t = new Timer(120, e -> {
                nivel = (int) (Math.random() * SEGS);
                repaint();
            });
            timers.add(t);
            t.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int segH = (getHeight() - 2) / SEGS;
            for (int i = 0; i < SEGS; i++) {
                int y    = getHeight() - (i + 1) * segH;
                boolean  lit = i < nivel;
                Color col;
                if (!lit) {
                    col = new Color(C_BORDER.getRed(), C_BORDER.getGreen(),
                            C_BORDER.getBlue(), 120);
                } else if (i < 6) {
                    col = C_OK;
                } else if (i < 8) {
                    col = new Color(0xEF9F27);
                } else {
                    col = C_REC;
                }
                g2.setColor(col);
                g2.fillRoundRect(2, y + 1, getWidth() - 4, segH - 2, 2, 2);
            }
            g2.dispose();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════

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