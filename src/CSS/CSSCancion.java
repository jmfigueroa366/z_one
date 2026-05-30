package CSS;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * CSSCancion — Hoja de estilos centralizada para el módulo Canciones.
 * Aquí viven TODOS los colores, fuentes, bordes y fábricas de componentes
 * visuales. formCancion.java solo importa esta clase y pide componentes,
 * sin hardcodear ningún valor de diseño.
 */
public final class CSSCancion {

    private CSSCancion() {}

    // =========================================================
    // PALETA
    // =========================================================
    public static final Color BG_PANEL        = new Color(0x04111F);
    public static final Color BG_LIST         = new Color(0x061829);
    public static final Color BG_LIST_SEL     = new Color(0x0D3560);
    public static final Color BG_FIELD        = new Color(0x0A1F36);
    public static final Color BG_FIELD_HOVER  = new Color(0x0E2B4A);
    public static final Color BG_DIALOG       = new Color(0x04111F);

    public static final Color ACCENT_PRIMARY  = new Color(0x018ABE);   // azul brillante
    public static final Color ACCENT_HOVER    = new Color(0x1AA7E0);   // azul hover
    public static final Color ACCENT_SUCCESS  = new Color(0x4FE8D2);   // turquesa éxito
    public static final Color ACCENT_DANGER   = new Color(0xEF6363);   // rojo suave
    public static final Color ACCENT_NEUTRAL  = new Color(0x1A4878);   // gris-azul botón secundario

    public static final Color BORDER_DEFAULT  = new Color(0x0D2A45);
    public static final Color BORDER_FOCUS    = new Color(0x1AA7E0, true);  // con alpha
    public static final Color BORDER_LIST     = new Color(0x0D2A45);

    public static final Color TEXT_PRIMARY    = new Color(0xE8EFF7);
    public static final Color TEXT_LABEL      = new Color(0x42A5F5);
    public static final Color TEXT_MUTED      = new Color(0x6BA3C4);
    public static final Color TEXT_LIST_ITEM  = new Color(0xC8DCF0);

    // =========================================================
    // FUENTES
    // =========================================================
    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD,  24);
    public static final Font FONT_LABEL    = new Font("Segoe UI", Font.BOLD,  11);
    public static final Font FONT_FIELD    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LIST     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BTN      = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font FONT_BTN_ICON = new Font("Segoe UI", Font.BOLD,  13);

    // =========================================================
    // DIMENSIONES
    // =========================================================
    public static final int BTN_W  = 120;
    public static final int BTN_H  = 34;
    public static final int ARC    = 10;   // radio de redondeo general
    public static final int ARC_SM = 6;

    // =========================================================
    // FÁBRICAS DE COMPONENTES
    // =========================================================

    /** Etiqueta de título del panel (ej. "🎵  Canciones") */
    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FONT_TITLE);
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    /** Etiqueta de campo en formularios */
    public static JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_LABEL);
        return l;
    }

    /**
     * Campo de texto estilizado con hover + focus animado.
     * @param valorInicial texto inicial (puede ser "")
     */
    public static JTextField campo(String valorInicial) {
        JTextField f = new JTextField(valorInicial) {
            boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
                addFocusListener(new java.awt.event.FocusAdapter() {
                    @Override public void focusGained(java.awt.event.FocusEvent e) { repaint(); }
                    @Override public void focusLost(java.awt.event.FocusEvent e)   { repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? BG_FIELD_HOVER : BG_FIELD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
                if (hasFocus()) {
                    g2.setColor(new Color(0x1AA7E0));
                    g2.setStroke(new BasicStroke(2f));
                } else {
                    g2.setColor(BORDER_DEFAULT);
                    g2.setStroke(new BasicStroke(1f));
                }
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, ARC, ARC);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setFont(FONT_FIELD);
        f.setForeground(TEXT_PRIMARY);
        f.setBackground(BG_FIELD);
        f.setCaretColor(ACCENT_HOVER);
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(6, 10, 6, 10));
        return f;
    }

    /**
     * ComboBox estilizado.
     * @param items arreglo de opciones
     */
    public static JComboBox<String> combo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(FONT_FIELD);
        c.setForeground(TEXT_PRIMARY);
        c.setBackground(BG_FIELD);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DEFAULT),
                new EmptyBorder(4, 8, 4, 8)));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return c;
    }

    /**
     * Botón animado con hover y efecto de presión.
     * @param texto    texto del botón
     * @param tipo     "primary" | "danger" | "success" | "secondary"
     * @param accion   ActionListener
     */
    public static JButton boton(String texto, String tipo, java.awt.event.ActionListener accion) {
        Color[] colores = resolverColoresBoton(tipo);
        Color base  = colores[0];
        Color hover = colores[1];

        JButton b = new JButton(texto) {
            boolean isHover   = false;
            boolean isPressed = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { isHover = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { isHover = false; isPressed = false; repaint(); }
                    @Override public void mousePressed(MouseEvent e) { isPressed = true;  repaint(); }
                    @Override public void mouseReleased(MouseEvent e){ isPressed = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glow sutil al hacer hover
                if (isHover && isEnabled()) {
                    g2.setColor(new Color(hover.getRed(), hover.getGreen(), hover.getBlue(), 40));
                    g2.fillRoundRect(-3, -3, getWidth() + 6, getHeight() + 6, ARC + 4, ARC + 4);
                }
                Color bg = isPressed ? base.darker() : (isHover ? hover : base);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(FONT_BTN);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(BTN_W, BTN_H));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (accion != null) b.addActionListener(accion);
        return b;
    }

    private static Color[] resolverColoresBoton(String tipo) {
        switch (tipo) {
            case "primary":   return new Color[]{ACCENT_PRIMARY, ACCENT_HOVER};
            case "danger":    return new Color[]{new Color(0x8B2020), ACCENT_DANGER};
            case "success":   return new Color[]{new Color(0x0F6E56), ACCENT_SUCCESS};
            default:          return new Color[]{ACCENT_NEUTRAL, new Color(0x215A8A)};
        }
    }

    /**
     * Renderer para celdas de JList<Cancion>.
     * Devuelve un JLabel listo para usar como renderer.
     */
    public static JLabel celdaCancion(String texto, boolean seleccionada) {
        JLabel item = new JLabel("  ♪  " + texto);
        item.setOpaque(true);
        item.setBackground(seleccionada ? BG_LIST_SEL : BG_LIST);
        item.setForeground(seleccionada ? TEXT_PRIMARY : TEXT_LIST_ITEM);
        item.setFont(FONT_LIST);
        item.setBorder(new EmptyBorder(11, 16, 11, 16));
        return item;
    }

    /**
     * Aplica el estilo base al JList de canciones.
     */
    public static void estilizarLista(JList<?> lista) {
        lista.setBackground(BG_LIST);
        lista.setForeground(TEXT_PRIMARY);
        lista.setFont(FONT_LIST);
        lista.setSelectionBackground(BG_LIST_SEL);
        lista.setSelectionForeground(TEXT_PRIMARY);
        lista.setFixedCellHeight(44);
    }

    /**
     * Aplica el estilo al JScrollPane que envuelve la lista.
     */
    public static void estilizarScroll(JScrollPane sp) {
        sp.setBorder(BorderFactory.createLineBorder(BORDER_LIST, 1));
        sp.getViewport().setBackground(BG_LIST);
        sp.getVerticalScrollBar().setBackground(BG_LIST);
    }

    /**
     * Panel de formulario (fondo del JDialog).
     */
    public static JPanel panelFormulario() {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setBackground(BG_DIALOG);
        p.setBorder(new EmptyBorder(24, 28, 24, 28));
        return p;
    }

    /**
     * Border estándar para separar el header del resto.
     */
    public static Border bordeHeader() {
        return new EmptyBorder(0, 0, 12, 0);
    }
}