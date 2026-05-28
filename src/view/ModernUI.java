package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class ModernUI {
    private ModernUI() {}

    // ===== PALETA DE COLORES (AZUL) =====
    public static final Color BG_DARK        = new Color(0, 17, 46);     // #00112E fondo base
    public static final Color BG_GRADIENT_1  = new Color(0, 27, 72);     // #001B48
    public static final Color BG_GRADIENT_2  = new Color(0, 12, 32);     // marino muy oscuro
    public static final Color CARD_BG        = new Color(4, 32, 63, 235);// #04203F tarjetas
    public static final Color INPUT_BG       = new Color(6, 38, 74);     // campos
    public static final Color INPUT_BG_HOVER = new Color(10, 50, 92);    // campos hover
    public static final Color PRIMARY        = new Color(1, 138, 190);   // #018ABE azul brillante
    public static final Color PRIMARY_HOVER  = new Color(26, 167, 224);  // #1AA7E0
    public static final Color PRIMARY_LIGHT  = new Color(151, 202, 219); // #97CADB celeste
    public static final Color ACCENT_CYAN    = new Color(86, 194, 232);  // celeste electrico
    public static final Color ACCENT_PINK    = new Color(54, 224, 200);  // turquesa (valor/costo)
    public static final Color ERROR          = new Color(239, 99, 99);   // rojo suave
    public static final Color SUCCESS        = new Color(79, 232, 210);  // turquesa exito
    public static final Color TEXT_PRIMARY   = new Color(234, 244, 248); // #EAF4F8
    public static final Color TEXT_SECONDARY = new Color(151, 202, 219); // #97CADB
    public static final Color TEXT_MUTED     = new Color(107, 163, 196); // azul-gris apagado
    public static final Color BORDER         = new Color(26, 72, 120, 130);
    public static final Color BORDER_FOCUS   = new Color(26, 167, 224, 210);

    // ===== FUENTES =====
    public static final Font FONT_TITLE      = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_SUBTITLE   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL      = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_INPUT      = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON     = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL      = new Font("Segoe UI", Font.PLAIN, 11);

    // =================================================================
    // PANEL CON GRADIENTE Y ACENTOS DECORATIVOS (fondo de las ventanas)
    // =================================================================
    public static class GradientPanel extends JPanel {
        public GradientPanel() {
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            // Gradiente vertical de fondo
            GradientPaint gp = new GradientPaint(
                0, 0, BG_GRADIENT_1,
                0, getHeight(), BG_GRADIENT_2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            // Circulo decorativo superior izquierdo (azul brillante difuso)
            RadialGradientPaint glow1 = new RadialGradientPaint(
                -50, -50, 280,
                new float[]{0f, 1f},
                new Color[]{new Color(1, 138, 190, 75), new Color(1, 138, 190, 0)});
            g2.setPaint(glow1);
            g2.fillOval(-150, -150, 400, 400);
            // Circulo decorativo inferior derecho (celeste difuso)
            RadialGradientPaint glow2 = new RadialGradientPaint(
                getWidth() + 50, getHeight() + 50, 280,
                new float[]{0f, 1f},
                new Color[]{new Color(86, 194, 232, 55), new Color(86, 194, 232, 0)});
            g2.setPaint(glow2);
            g2.fillOval(getWidth() - 150, getHeight() - 150, 400, 400);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =================================================================
    // TARJETA CENTRAL - panel redondeado con sombra sutil
    // =================================================================
    public static class CardPanel extends JPanel {
        private final int arc;
        public CardPanel(int arc) {
            this.arc = arc;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            // Sombra
            for (int i = 0; i < 8; i++) {
                g2.setColor(new Color(0, 0, 0, 5));
                g2.fillRoundRect(i, i + 2, getWidth() - i * 2, getHeight() - i * 2,
                                 arc, arc);
            }
            // Fondo de la tarjeta
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            // Borde sutil
            g2.setColor(new Color(26, 72, 120, 90));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =================================================================
    // TARJETA KPI - panel con gradiente diagonal y barra de acento arriba
    // Usala para las tarjetas de estadisticas (Sesiones totales, etc.)
    //   new ModernUI.StatCard(14, ModernUI.ACCENT_CYAN);
    // =================================================================
    public static class StatCard extends JPanel {
        private final int arc;
        private final Color acento;
        public StatCard(int arc, Color acento) {
            this.arc = arc;
            this.acento = acento;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            // Gradiente diagonal del fondo de la tarjeta
            GradientPaint gp = new GradientPaint(
                0, 0, new Color(10, 55, 102),
                0, getHeight(), new Color(4, 32, 63));
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            // Barra de acento superior
            g2.setColor(acento);
            g2.fillRoundRect(0, 0, getWidth() - 1, 6, arc, arc);
            g2.fillRect(0, 3, getWidth() - 1, 3);
            // Borde sutil
            g2.setColor(new Color(26, 72, 120, 110));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =================================================================
    // LOGO ANIMADO - disco de vinilo dibujado a mano
    // =================================================================
    public static class LogoPanel extends JPanel {
        public LogoPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(80, 80));
            setMaximumSize(new Dimension(80, 80));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int r = Math.min(getWidth(), getHeight()) / 2 - 4;
            // Disco principal
            RadialGradientPaint disc = new RadialGradientPaint(
                cx, cy, r,
                new float[]{0f, 0.6f, 1f},
                new Color[]{new Color(10, 60, 105),
                            new Color(4, 35, 70),
                            new Color(0, 17, 46)});
            g2.setPaint(disc);
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            // Surcos (anillos concentricos)
            g2.setStroke(new BasicStroke(0.8f));
            g2.setColor(new Color(86, 194, 232, 90));
            for (int rr = r - 4; rr > 12; rr -= 3) {
                g2.drawOval(cx - rr, cy - rr, rr * 2, rr * 2);
            }
            // Etiqueta central (azul brillante)
            g2.setColor(PRIMARY);
            g2.fillOval(cx - 12, cy - 12, 24, 24);
            // Hueco central
            g2.setColor(BG_DARK);
            g2.fillOval(cx - 3, cy - 3, 6, 6);
            // Reflejo brillante
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillArc(cx - r + 4, cy - r + 4, (r - 4) * 2, (r - 4) * 2, 45, 60);
            g2.dispose();
        }
    }

    // =================================================================
    // BOTON REDONDEADO CON EFECTO HOVER
    // =================================================================
    public static class RoundedButton extends JButton {
        private final int arc;
        private boolean hover = false;
        private boolean pressed = false;
        private Color baseColor;
        private Color hoverColor;
        private boolean primary;
        public RoundedButton(String text, boolean primary) {
            super(text);
            this.primary = primary;
            this.arc = 14;
            this.baseColor = primary ? PRIMARY : new Color(0, 0, 0, 0);
            this.hoverColor = primary ? PRIMARY_HOVER : new Color(1, 138, 190, 38);
            setFont(FONT_BUTTON);
            setForeground(primary ? Color.WHITE : PRIMARY_LIGHT);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; pressed = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) { pressed = true;  repaint(); }
                @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            if (primary) {
                // Glow azul cuando hover
                if (hover && isEnabled()) {
                    for (int i = 0; i < 6; i++) {
                        g2.setColor(new Color(26, 167, 224, 22 - i * 3));
                        g2.fillRoundRect(-i, -i, getWidth() + i * 2, getHeight() + i * 2,
                                         arc + i, arc + i);
                    }
                }
                // Fondo
                Color c = isEnabled() ? (pressed ? baseColor.darker() : (hover ? hoverColor : baseColor))
                                      : new Color(40, 55, 75);
                // Gradiente sutil del boton
                GradientPaint gp = new GradientPaint(
                    0, 0, c.brighter(),
                    0, getHeight(), c);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            } else {
                if (hover) {
                    g2.setColor(hoverColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                }
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =================================================================
    // CAMPO DE TEXTO REDONDEADO CON PLACEHOLDER
    // =================================================================
    public static class RoundedTextField extends JTextField {
        private final String placeholder;
        private boolean hover = false;
        public RoundedTextField(String placeholder) {
            super();
            this.placeholder = placeholder;
            setFont(FONT_INPUT);
            setForeground(TEXT_PRIMARY);
            setBackground(INPUT_BG);
            setCaretColor(PRIMARY);
            setOpaque(false);
            setBorder(new EmptyBorder(12, 16, 12, 16));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            });
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { repaint(); }
                @Override public void focusLost(FocusEvent e)   { repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            // Fondo
            g2.setColor(hover && !hasFocus() ? INPUT_BG_HOVER : INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            // Borde (mas visible cuando hay focus)
            if (hasFocus()) {
                g2.setColor(BORDER_FOCUS);
                g2.setStroke(new BasicStroke(2f));
            } else {
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
            }
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
            // Placeholder si esta vacio
            if (getText().isEmpty() && !hasFocus()) {
                g2.setColor(TEXT_MUTED);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(placeholder, 16,
                              (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =================================================================
    // CAMPO PASSWORD REDONDEADO CON PLACEHOLDER
    // =================================================================
    public static class RoundedPasswordField extends JPasswordField {
        private final String placeholder;
        private boolean hover = false;
        public RoundedPasswordField(String placeholder) {
            super();
            this.placeholder = placeholder;
            setFont(FONT_INPUT);
            setForeground(TEXT_PRIMARY);
            setBackground(INPUT_BG);
            setCaretColor(PRIMARY);
            setOpaque(false);
            setBorder(new EmptyBorder(12, 16, 12, 16));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            });
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { repaint(); }
                @Override public void focusLost(FocusEvent e)   { repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hover && !hasFocus() ? INPUT_BG_HOVER : INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            if (hasFocus()) {
                g2.setColor(BORDER_FOCUS);
                g2.setStroke(new BasicStroke(2f));
            } else {
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
            }
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
            if (getPassword().length == 0 && !hasFocus()) {
                g2.setColor(TEXT_MUTED);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(placeholder, 16,
                              (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =================================================================
    // COMBOBOX REDONDEADO
    // =================================================================
    public static JComboBox<String> roundedCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        combo.setFont(FONT_INPUT);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(INPUT_BG);
        combo.setOpaque(false);
        combo.setBorder(new EmptyBorder(8, 12, 8, 12));
        combo.setFocusable(false);
        combo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return combo;
    }

    // =================================================================
    // ETIQUETA pequena para labels arriba de cada input
    // =================================================================
    public static JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // =================================================================
    // PILDORA DE ESTADO - etiqueta redondeada con color de acento.
    // Usala para mostrar estados (Activo, Finalizada, En curso, etc.)
    // =================================================================
    public static class Pildora extends JLabel {
        private Color acento = ACCENT_CYAN;
        public Pildora() {
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(new EmptyBorder(4, 12, 4, 12));
            setOpaque(false);
            setHorizontalAlignment(CENTER);
        }
        public void setAcento(Color c) { this.acento = c; repaint(); }
        @Override public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, 26);
        }
        @Override public Dimension getMaximumSize() { return getPreferredSize(); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 42));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            setForeground(acento);
            super.paintComponent(g);
        }
    }

    // =================================================================
    // INDICADOR DE CONEXION con punto de color (verde/rojo)
    // =================================================================
    public static class StatusDot extends JComponent {
        private Color color = TEXT_MUTED;
        public StatusDot() { setPreferredSize(new Dimension(10, 10)); }
        public void setColor(Color c) { this.color = c; repaint(); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            // Glow exterior
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
            g2.fillOval(0, 1, 10, 10);
            // Punto principal
            g2.setColor(color);
            g2.fillOval(2, 3, 6, 6);
            g2.dispose();
        }
    }
}