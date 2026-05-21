package view;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
/**
 * ModernUI — Tema visual y componentes reutilizables para Z-One.
 * Provee constantes de color, fuentes y componentes Swing custom
 * (botones redondeados, campos con placeholder, paneles con gradiente, etc.)
 * Usalo en cualquier formulario nuevo del proyecto para mantener
 * un look consistente y moderno.
 */
public final class ModernUI {
    private ModernUI() {}

    // ===== PALETA DE COLORES =====
    public static final Color BG_DARK        = new Color(13, 13, 30);
    public static final Color BG_GRADIENT_1  = new Color(20, 12, 48);
    public static final Color BG_GRADIENT_2  = new Color(7, 7, 22);
    public static final Color CARD_BG        = new Color(24, 24, 52, 230);
    public static final Color INPUT_BG       = new Color(35, 32, 70);
    public static final Color INPUT_BG_HOVER = new Color(42, 38, 82);
    public static final Color PRIMARY        = new Color(139, 92, 246);
    public static final Color PRIMARY_HOVER  = new Color(167, 124, 255);
    public static final Color PRIMARY_LIGHT  = new Color(196, 181, 253);
    public static final Color ACCENT_CYAN    = new Color(6, 182, 212);
    public static final Color ACCENT_PINK    = new Color(236, 72, 153);
    public static final Color ERROR          = new Color(239, 68, 68);
    public static final Color SUCCESS        = new Color(34, 197, 94);
    public static final Color TEXT_PRIMARY   = new Color(241, 245, 249);
    public static final Color TEXT_SECONDARY = new Color(203, 213, 225);
    public static final Color TEXT_MUTED     = new Color(148, 163, 184);
    public static final Color BORDER         = new Color(79, 70, 229, 80);
    public static final Color BORDER_FOCUS   = new Color(167, 124, 255, 200);
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
            // Círculo decorativo superior izquierdo (morado difuso)
            RadialGradientPaint glow1 = new RadialGradientPaint(
                -50, -50, 280,
                new float[]{0f, 1f},
                new Color[]{new Color(139, 92, 246, 70), new Color(139, 92, 246, 0)});
            g2.setPaint(glow1);
            g2.fillOval(-150, -150, 400, 400);
            // Círculo decorativo inferior derecho (cian difuso)
            RadialGradientPaint glow2 = new RadialGradientPaint(
                getWidth() + 50, getHeight() + 50, 280,
                new float[]{0f, 1f},
                new Color[]{new Color(6, 182, 212, 55), new Color(6, 182, 212, 0)});
            g2.setPaint(glow2);
            g2.fillOval(getWidth() - 150, getHeight() - 150, 400, 400);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    // =================================================================
    // TARJETA CENTRAL — panel redondeado con sombra sutil
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
            g2.setColor(new Color(139, 92, 246, 50));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    // =================================================================
    // LOGO ANIMADO — disco de vinilo dibujado a mano
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
                new Color[]{new Color(40, 30, 80),
                            new Color(20, 15, 50),
                            new Color(10, 10, 30)});
            g2.setPaint(disc);
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            // Surcos (anillos concéntricos)
            g2.setStroke(new BasicStroke(0.8f));
            g2.setColor(new Color(139, 92, 246, 90));
            for (int rr = r - 4; rr > 12; rr -= 3) {
                g2.drawOval(cx - rr, cy - rr, rr * 2, rr * 2);
            }
            // Etiqueta central (color púrpura)
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
    // BOTÓN REDONDEADO CON EFECTO HOVER
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
            this.hoverColor = primary ? PRIMARY_HOVER : new Color(139, 92, 246, 30);
            setFont(FONT_BUTTON);
            setForeground(primary ? Color.WHITE : PRIMARY);
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
                // Sombra de glow morado cuando hover
                if (hover && isEnabled()) {
                    for (int i = 0; i < 6; i++) {
                        g2.setColor(new Color(139, 92, 246, 20 - i * 3));
                        g2.fillRoundRect(-i, -i, getWidth() + i * 2, getHeight() + i * 2,
                                         arc + i, arc + i);
                    }
                }
                // Fondo
                Color c = isEnabled() ? (pressed ? baseColor.darker() : (hover ? hoverColor : baseColor))
                                      : new Color(60, 60, 80);
                // Gradiente sutil del botón
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
            // Borde (más visible cuando hay focus)
            if (hasFocus()) {
                g2.setColor(BORDER_FOCUS);
                g2.setStroke(new BasicStroke(2f));
            } else {
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
            }
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
            // Placeholder si está vacío
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
    // ETIQUETA pequeña para labels arriba de cada input
    // =================================================================
    public static JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
    // =================================================================
    // INDICADOR DE CONEXIÓN con punto de color (verde/rojo)
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