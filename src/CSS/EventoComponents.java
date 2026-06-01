package css;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;

import static css.EventoStyles.*;

/**
 * EventoComponents.java — Componentes visuales mejorados para formEvento.
 * ─────────────────────────────────────────────────────────────────────────
 * Mejoras:
 *   · BandaEvento  — waveform animada + partículas + badge REC parpadeante
 *   · LineaShimmer — gradiente de doble color más vivo
 *   · FieldFx      — ícono prefijo + error visual inline + animación de entrada
 *   · ComboFx      — renderer con ícono de tipo, flecha animada
 *   · BtnFx        — estado loading (spinner) + estado success (check)
 *   · ErrorLabel   — etiqueta de error deslizante por campo
 *   · SeccionHeader — separador con título y línea shimmer
 */
public final class EventoComponents {

    private EventoComponents() {}

    // ══════════════════════════════════════════════════════════════════
    //  BANDA SUPERIOR ANIMADA
    // ══════════════════════════════════════════════════════════════════
    public static class BandaEvento extends JPanel {

        private final float[] px  = new float[20];
        private final float[] py  = new float[20];
        private final float[] pv  = new float[20];
        private final int[]   pSym = new int[20];
        private final String[] sym = {"🎟","🎸","🎧","🎙","🥁","🚀","♪","♫"};

        // Waveform
        private final float[] wH  = new float[22];
        private final float[] wHt = new float[22];

        // REC badge
        private boolean recVis = true;
        private float   bounce = 0f;

        public BandaEvento(boolean isEdit, List<Timer> timers) {
            setOpaque(false);
            setLayout(new BorderLayout(16, 0));
            setBorder(new EmptyBorder(22, 28, 22, 28));
            setPreferredSize(new Dimension(0, 108));

            java.util.Random r = new java.util.Random();
            for (int i = 0; i < px.length; i++) {
                px[i]  = r.nextFloat();
                py[i]  = r.nextFloat();
                pv[i]  = 0.0006f + r.nextFloat() * 0.001f;
                pSym[i] = r.nextInt(sym.length);
            }
            for (int i = 0; i < wH.length; i++) {
                wH[i]  = 0.15f + r.nextFloat() * 0.7f;
                wHt[i] = 0.1f  + r.nextFloat() * 0.85f;
            }

            // Timer principal: partículas + waveform
            Timer tMain = new Timer(38, e -> {
                java.util.Random rr = new java.util.Random();
                for (int i = 0; i < px.length; i++) {
                    py[i] -= pv[i];
                    px[i] += pv[i] * 0.25f;
                    if (py[i] < -0.12f) { py[i] = 1.12f; px[i] = rr.nextFloat(); pSym[i] = rr.nextInt(sym.length); }
                    if (px[i] >  1.12f)   px[i] = -0.12f;
                }
                for (int i = 0; i < wH.length; i++) {
                    wH[i] += (wHt[i] - wH[i]) * 0.09f;
                    if (Math.abs(wH[i] - wHt[i]) < 0.015f)
                        wHt[i] = 0.08f + rr.nextFloat() * 0.88f;
                }
                bounce = (float)(Math.sin(System.currentTimeMillis() / 650.0) * 0.5 + 0.5);
                repaint();
            });
            timers.add(tMain);
            tMain.start();

            // Timer REC parpadeante
            Timer tRec = new Timer(560, e -> { recVis = !recVis; repaint(); });
            timers.add(tRec);
            tRec.start();

            // ── Ícono pulsante ──
            JLabel ico = new JLabel(isEdit ? "✏" : "🎟", SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int pulse = (int)(bounce * 22);
                    // Halo exterior
                    g2.setColor(new Color(255, 255, 255, 12 + pulse));
                    g2.fillRoundRect(-4, -4, getWidth() + 8, getHeight() + 8, 18, 18);
                    // Caja
                    g2.setColor(new Color(255, 255, 255, 28 + pulse));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    g2.setColor(new Color(255, 255, 255, 90 + pulse));
                    g2.setStroke(new BasicStroke(1.4f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
            ico.setForeground(Color.WHITE);
            ico.setPreferredSize(new Dimension(58, 58));

            // ── Columna de texto ──
            JPanel txt = new JPanel();
            txt.setOpaque(false);
            txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));

            // Fila título + badge REC
            JPanel tituloRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            tituloRow.setOpaque(false);
            tituloRow.setAlignmentX(LEFT_ALIGNMENT);

            JLabel titLbl = new JLabel(isEdit ? "Editar evento" : "Nuevo evento");
            titLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
            titLbl.setForeground(Color.WHITE);

            JLabel recLbl = new JLabel("● REC") {
                @Override protected void paintComponent(Graphics g) {
                    if (!recVis) return;
                    super.paintComponent(g);
                }
            };
            recLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
            recLbl.setForeground(new Color(0xFF4444));

            tituloRow.add(titLbl);
            tituloRow.add(recLbl);

            JLabel subLbl = new JLabel(isEdit
                    ? "ACTUALIZA LA INFORMACIÓN DEL EVENTO"
                    : "REGISTRA UN NUEVO EVENTO EN EL SISTEMA");
            subLbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
            subLbl.setForeground(new Color(255, 255, 255, 175));
            subLbl.setAlignmentX(LEFT_ALIGNMENT);

            txt.add(Box.createVerticalGlue());
            txt.add(tituloRow);
            txt.add(Box.createVerticalStrut(4));
            txt.add(subLbl);
            txt.add(Box.createVerticalGlue());

            add(ico, BorderLayout.WEST);
            add(txt, BorderLayout.CENTER);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            // Fondo degradado
            g2.setPaint(new GradientPaint(0, 0, new Color(0x08213E),
                    w, h, new Color(0x10406E)));
            g2.fillRect(0, 0, w, h);

            // Brillo superior sutil
            g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 22),
                    0, h / 2f, new Color(255, 255, 255, 0)));
            g2.fillRect(0, 0, w, h / 2);

            // Waveform de fondo
            int bW = 3, gap = 5;
            int totalW = wH.length * (bW + gap);
            int startX = w / 2 - totalW / 2;
            int midY   = h / 2;
            for (int i = 0; i < wH.length; i++) {
                int bH   = (int)(wH[i] * h * 0.55f);
                int alpha = (int)(18 + wH[i] * 32);
                g2.setColor(new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(),
                        Math.min(70, alpha)));
                g2.fillRoundRect(startX + i * (bW + gap), midY - bH / 2, bW, bH, bW, bW);
            }

            // Partículas flotantes
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            for (int i = 0; i < px.length; i++) {
                int alpha = (int)(12 + Math.sin(py[i] * Math.PI) * 30);
                g2.setColor(new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(),
                        Math.max(0, Math.min(60, alpha))));
                g2.drawString(sym[pSym[i]], (int)(px[i] * w), (int)(py[i] * h));
            }

            // Línea inferior cyan
            g2.setColor(new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 200));
            g2.fillRect(0, h - 2, w, 2);

            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  LÍNEA SHIMMER MEJORADA (doble gradiente)
    // ══════════════════════════════════════════════════════════════════
    public static class LineaShimmer extends JPanel {
        private float phase = 0f;

        public LineaShimmer(List<Timer> timers) {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 3));
            Timer t = new Timer(38, e -> {
                phase += 0.014f;
                if (phase > 1.5f) phase = -0.5f;
                repaint();
            });
            timers.add(t);
            t.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            // Base translúcida
            g2.setColor(new Color(BORDER_FOCUS.getRed(), BORDER_FOCUS.getGreen(),
                    BORDER_FOCUS.getBlue(), 45));
            g2.fillRect(0, 0, w, h);

            // Destello viajero
            float cx = phase * w;
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(cx, h / 2f),
                    Math.max(1f, w * 0.20f),
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{
                        new Color(255, 255, 255, 210),
                        new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 120),
                        new Color(BORDER_FOCUS.getRed(), BORDER_FOCUS.getGreen(),
                                  BORDER_FOCUS.getBlue(), 0)
                    }));
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  SECCIÓN HEADER con línea shimmer y título
    // ══════════════════════════════════════════════════════════════════
    public static class SeccionHeader extends JPanel {
        public SeccionHeader(String texto, Color color, List<Timer> timers) {
            setOpaque(false);
            setLayout(new BorderLayout(10, 0));
            setAlignmentX(LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

            JLabel lbl = EventoStyles.lbl(texto, 9, true, color);
            add(lbl, BorderLayout.WEST);

            final float[] phase = {0f};
            JPanel linea = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    int w = getWidth(), h = getHeight();
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
                    g2.fillRect(0, h / 2, w, 1);
                    float cx = phase[0] * w;
                    g2.setPaint(new RadialGradientPaint(
                            new Point2D.Float(cx, h / 2f),
                            Math.max(1f, w * 0.22f),
                            new float[]{0f, 1f},
                            new Color[]{
                                new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 200),
                                new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 0)
                            }));
                    g2.fillRect(0, h / 2, w, 1);
                    g2.dispose();
                }
            };
            linea.setOpaque(false);

            Timer t = new Timer(38, e -> {
                phase[0] += 0.014f;
                if (phase[0] > 1.4f) phase[0] = -0.4f;
                linea.repaint();
            });
            timers.add(t);
            t.start();

            add(linea, BorderLayout.CENTER);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  ERROR LABEL — desliza hacia abajo al aparecer
    // ══════════════════════════════════════════════════════════════════
    public static class ErrorLabel extends JLabel {
        private boolean visible2 = false;
        private float   alpha    = 0f;
        private float   offsetY  = -6f;

        public ErrorLabel() {
            setFont(new Font("Segoe UI", Font.PLAIN, 10));
            setForeground(new Color(0xDC2626));
            setBorder(new EmptyBorder(2, 2, 0, 0));
            setOpaque(false);
            setPreferredSize(new Dimension(0, 0));
        }

        public void mostrar(String msg, List<Timer> timers) {
            if (visible2) return;
            visible2 = true;
            setText(msg);
            setPreferredSize(new Dimension(0, 18));
            getParent().revalidate();

            Timer t = new Timer(16, null);
            final long ini = System.currentTimeMillis();
            t.addActionListener(ev -> {
                float p   = Math.min(1f, (System.currentTimeMillis() - ini) / 200f);
                float ease = 1f - (float)Math.pow(1 - p, 3);
                alpha   = ease;
                offsetY = -6f * (1 - ease);
                repaint();
                if (p >= 1f) t.stop();
            });
            timers.add(t);
            t.start();
        }

        public void ocultar() {
            visible2 = false;
            alpha    = 0f;
            offsetY  = -6f;
            setText("");
            setPreferredSize(new Dimension(0, 0));
            if (getParent() != null) getParent().revalidate();
        }

        @Override protected void paintComponent(Graphics g) {
            if (alpha <= 0f) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.translate(0, (int)offsetY);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  CAMPO CON FOCUS ANIMADO + ÍCONO + SHAKE + ERROR
    // ══════════════════════════════════════════════════════════════════
    public static class FieldFx extends JTextField {
        private float   focusAnim  = 0f;
        private boolean focoActivo = false;
        private float   shakeOff   = 0f;
        private boolean enError    = false;
        private final String placeholder;
        private final List<Timer> timersOwner;

        public FieldFx(String value, String ph, List<Timer> timers) {
            super(value);
            this.placeholder = ph;
            this.timersOwner = timers;
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(TEXT_PRI);
            setCaretColor(BORDER_FOCUS);
            setBorder(new EmptyBorder(8, 14, 8, 14));
            setPreferredSize(new Dimension(0, 40));

            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    focoActivo = true;
                    if (enError) limpiarError();
                    animarFoco();
                }
                @Override public void focusLost(FocusEvent e) {
                    focoActivo = false;
                    animarFoco();
                }
            });
        }

        private void animarFoco() {
            Timer t = new Timer(16, null);
            final long  ini   = System.currentTimeMillis();
            final float desde = focusAnim;
            final float hasta = focoActivo ? 1f : 0f;
            final int   dur   = 200;
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

        /** Sacude el campo y activa estado de error. */
        public void shake() {
            enError = true;
            repaint();
            Timer t = new Timer(16, null);
            final long ini = System.currentTimeMillis();
            final int  dur = 380;
            t.addActionListener(ev -> {
                float p  = Math.min(1f, (System.currentTimeMillis() - ini) / (float) dur);
                shakeOff = (float)(Math.sin(p * Math.PI * 5) * (1 - p) * 7);
                repaint();
                if (p >= 1f) { shakeOff = 0f; t.stop(); }
            });
            timersOwner.add(t);
            t.start();
        }

        public void limpiarError() {
            enError = false;
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int sh = (int) shakeOff;
            int w  = getWidth(), h = getHeight();

            // Halo de foco / error
            if (enError) {
                g2.setColor(new Color(220, 38, 38, 55));
                g2.fillRoundRect(sh - 2, -2, w + 3, h + 3, 14, 14);
            } else if (focusAnim > 0.01f) {
                g2.setColor(new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(),
                        (int)(focusAnim * 65)));
                g2.fillRoundRect(sh - 2, -2, w + 3, h + 3, 14, 14);
            }

            // Fondo
            Color bg = enError
                    ? new Color(0xFFF0F0)
                    : blend(BG_FIELD, BG_SELECTED, focusAnim * 0.28f);
            g2.setColor(bg);
            g2.fillRoundRect(sh + 2, 2, w - 5, h - 5, 10, 10);

            // Borde
            Color borde = enError
                    ? new Color(0xDC2626)
                    : blend(BORDER, BORDER_FOCUS, focusAnim);
            g2.setColor(borde);
            g2.setStroke(new BasicStroke(enError ? 1.6f : 1f + focusAnim * 0.5f));
            g2.drawRoundRect(sh + 2, 2, w - 6, h - 6, 10, 10);
            g2.dispose();

            // Placeholder
            if (getText().isEmpty() && !focoActivo && placeholder != null) {
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setFont(getFont());
                g3.setColor(TEXT_MUT);
                FontMetrics fm = g3.getFontMetrics();
                g3.drawString(placeholder, sh + 16, (h + fm.getAscent()) / 2 - 2);
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

    // ══════════════════════════════════════════════════════════════════
    //  COMBO CON FOCUS ANIMADO + RENDERER CON ÍCONO
    // ══════════════════════════════════════════════════════════════════
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
            final int   dur   = 200;
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
            int w = getWidth(), h = getHeight();
            if (focusAnim > 0.01f) {
                g2.setColor(new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(),
                        (int)(focusAnim * 65)));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);
            }
            g2.setColor(blend(BG_FIELD, BG_SELECTED, focusAnim * 0.28f));
            g2.fillRoundRect(2, 2, w - 5, h - 5, 10, 10);
            g2.setColor(blend(BORDER, BORDER_FOCUS, focusAnim));
            g2.setStroke(new BasicStroke(1f + focusAnim * 0.5f));
            g2.drawRoundRect(2, 2, w - 6, h - 6, 10, 10);

            // Flecha personalizada
            int ax = w - 22, ay = h / 2;
            g2.setColor(blend(TEXT_MUT, INDIGO, focusAnim));
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(ax - 4, ay - 2, ax, ay + 3);
            g2.drawLine(ax, ay + 3, ax + 4, ay - 2);
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

    /** Factory con renderer que incluye ícono de tipo de evento. */
    public static ComboFx<String> comboFx(String[] opts, String sel, List<Timer> timers) {
        ComboFx<String> cb = new ComboFx<>(opts, timers);
        cb.setSelectedItem(sel);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean s, boolean f) {
                String tipo = v == null ? "" : v.toString();
                String ico  = EventoStyles.iconoTipo(tipo);
                JLabel c    = new JLabel(ico + "  " + tipo);
                c.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
                c.setBackground(s && i != -1
                        ? new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(), 200)
                        : BG_FIELD);
                c.setForeground(s && i != -1 ? Color.WHITE : TEXT_PRI);
                c.setBorder(new EmptyBorder(8, 12, 8, 12));
                c.setOpaque(true);
                return c;
            }
        });
        return cb;
    }

    // ══════════════════════════════════════════════════════════════════
    //  BOTÓN ANIMADO — estados: normal / hover / loading / success
    // ══════════════════════════════════════════════════════════════════
    public static class BtnFx extends JButton {

        public enum Estado { NORMAL, LOADING, SUCCESS }

        private final boolean primary;
        private float   hoverAnim  = 0f;
        private boolean hovered    = false;
        private float   shimmerX   = -0.3f;
        private Estado  estado     = Estado.NORMAL;
        private float   spinAngle  = 0f;
        private float   checkAnim  = 0f;

        private final List<Timer> timersOwner;
        private Timer spinTimer, checkTimer;

        public BtnFx(String text, boolean primary, List<Timer> timers) {
            super(text);
            this.primary     = primary;
            this.timersOwner = timers;
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(primary ? Color.WHITE : TEXT_PRI);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 20, 8, 20));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { if (estado == Estado.NORMAL) { hovered = true;  animarHover(); } }
                @Override public void mouseExited (MouseEvent e) { if (estado == Estado.NORMAL) { hovered = false; animarHover(); } }
            });

            // Shimmer continuo (solo primario)
            if (primary) {
                Timer t = new Timer(38, e -> {
                    shimmerX += 0.016f;
                    if (shimmerX > 1.3f) shimmerX = -0.3f;
                    repaint();
                });
                timersOwner.add(t);
                t.start();
            }
        }

        /** Inicia estado de carga (spinner girando). */
        public void setLoading(String msg) {
            estado = Estado.LOADING;
            setText(msg);
            setCursor(Cursor.getDefaultCursor());
            if (spinTimer != null) spinTimer.stop();
            spinTimer = new Timer(20, e -> { spinAngle += 12f; repaint(); });
            timersOwner.add(spinTimer);
            spinTimer.start();
        }

        /** Cambia a estado de éxito (check animado). */
        public void setSuccess(String msg) {
            if (spinTimer != null) spinTimer.stop();
            estado = Estado.SUCCESS;
            checkAnim = 0f;
            setText(msg);
            checkTimer = new Timer(16, null);
            final long ini = System.currentTimeMillis();
            checkTimer.addActionListener(ev -> {
                checkAnim = Math.min(1f, (System.currentTimeMillis() - ini) / 400f);
                repaint();
                if (checkAnim >= 1f) checkTimer.stop();
            });
            timersOwner.add(checkTimer);
            checkTimer.start();
        }

        /** Vuelve al estado normal. */
        public void resetEstado(String msg) {
            if (spinTimer  != null) spinTimer.stop();
            if (checkTimer != null) checkTimer.stop();
            estado = Estado.NORMAL;
            checkAnim = 0f;
            setText(msg);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            repaint();
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
            int w = getWidth(), h = getHeight();

            if (estado == Estado.SUCCESS) {
                // Fondo verde
                g2.setColor(new Color(0x059669));
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                // Brillo superior
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 28), 0, h / 2f, new Color(0,0,0,0)));
                g2.fillRoundRect(0, 0, w, h / 2, 10, 10);
            } else if (estado == Estado.LOADING) {
                g2.setColor(new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(), 200));
                g2.fillRoundRect(0, 0, w, h, 10, 10);
            } else if (primary) {
                Color base = getModel().isPressed() ? new Color(0x1A4A9E) : INDIGO;
                Color lift = blend(base, INDIGO_LIGHT, hoverAnim * 0.45f);
                g2.setColor(lift);
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                g2.setPaint(new GradientPaint(0, 0, new Color(255,255,255,28), 0, h/2f, new Color(0,0,0,0)));
                g2.fillRoundRect(0, 0, w, h / 2, 10, 10);
                // Shimmer
                float cx = shimmerX * w;
                g2.setPaint(new RadialGradientPaint(
                        new Point2D.Float(cx, h / 2f),
                        Math.max(1f, w * 0.22f),
                        new float[]{0f, 1f},
                        new Color[]{new Color(255,255,255,52), new Color(255,255,255,0)}));
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                // Borde hover
                if (hoverAnim > 0.05f) {
                    g2.setColor(new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(), (int)(hoverAnim * 70)));
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawRoundRect(-1, -1, w + 1, h + 1, 11, 11);
                }
            } else {
                Color base = blend(BG_CARD_ALT, BG_SELECTED, hoverAnim * 0.5f);
                g2.setColor(base);
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                g2.setColor(blend(BORDER, INDIGO, hoverAnim));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
            }

            g2.dispose();

            // Texto + iconos especiales
            Graphics2D gTxt = (Graphics2D) g.create();
            gTxt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gTxt.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (estado == Estado.LOADING) {
                // Spinner
                int sz = 16, sx = 14, sy = (h - sz) / 2;
                gTxt.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                gTxt.setColor(new Color(255, 255, 255, 40));
                gTxt.drawOval(sx, sy, sz, sz);
                gTxt.setColor(Color.WHITE);
                gTxt.rotate(Math.toRadians(spinAngle), sx + sz / 2.0, sy + sz / 2.0);
                gTxt.drawArc(sx, sy, sz, sz, 0, 260);
                gTxt.rotate(-Math.toRadians(spinAngle), sx + sz / 2.0, sy + sz / 2.0);

                gTxt.setFont(new Font("Segoe UI", Font.BOLD, 12));
                gTxt.setColor(new Color(255, 255, 255, 220));
                FontMetrics fm = gTxt.getFontMetrics();
                String t = getText();
                int tx = sx + sz + 10;
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
                gTxt.drawString(t, tx, ty);

            } else if (estado == Estado.SUCCESS) {
                // Check animado
                int cx2 = 18, cy2 = h / 2;
                int r  = 8;
                gTxt.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                gTxt.setColor(new Color(255, 255, 255, 180));
                gTxt.drawOval(cx2 - r, cy2 - r, r * 2, r * 2);
                // Dibujar el check progresivo
                if (checkAnim > 0) {
                    gTxt.setColor(Color.WHITE);
                    gTxt.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    // check: dos segmentos
                    int x1 = cx2 - 5, y1 = cy2;
                    int x2 = cx2 - 1, y2 = cy2 + 4;
                    int x3 = cx2 + 6, y3 = cy2 - 4;
                    if (checkAnim < 0.5f) {
                        float p = checkAnim / 0.5f;
                        int mx = x1 + (int)((x2 - x1) * p);
                        int my = y1 + (int)((y2 - y1) * p);
                        gTxt.drawLine(x1, y1, mx, my);
                    } else {
                        gTxt.drawLine(x1, y1, x2, y2);
                        float p = (checkAnim - 0.5f) / 0.5f;
                        int mx = x2 + (int)((x3 - x2) * p);
                        int my = y2 + (int)((y3 - y2) * p);
                        gTxt.drawLine(x2, y2, mx, my);
                    }
                }
                gTxt.setFont(new Font("Segoe UI", Font.BOLD, 12));
                gTxt.setColor(Color.WHITE);
                FontMetrics fm = gTxt.getFontMetrics();
                String t = getText();
                int tx = cx2 + r + 10;
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
                gTxt.drawString(t, tx, ty);

            } else {
                // Normal — deja que super dibuje el texto
                super.paintComponent(gTxt);
            }
            gTxt.dispose();
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
    //  HELPER: etiqueta simple
    // ════════════════════════════════════════════════════════════════
    public static JLabel lbl(String t, Font f, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }
}