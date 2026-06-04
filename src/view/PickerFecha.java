package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Campo de fecha con 3 inputs separados DD / MM / AAAA.
 * Al completar DD avanza automáticamente a MM, y de MM a AAAA.
 * Uso: new DatePickerField()  →  getValue() devuelve LocalDate o null.
 */
public class PickerFecha extends JPanel {

    private static final Color BG_FIELD  = new Color(240, 242, 248);
    private static final Color COL_BRD   = new Color(220, 225, 240);
    private static final Color PURPLE    = new Color(99,  91, 255);
    private static final Color TXT_PRI   = new Color(30,  30,  60);
    private static final Color TXT_SEC   = new Color(130, 140, 170);
    private static final Color ERROR_COL = new Color(239, 68,  68);

    private final JTextField fDia;
    private final JTextField fMes;
    private final JTextField fAnio;
    private boolean hasError = false;

    public PickerFecha() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));

        JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                boolean focused = fDia.hasFocus() || fMes.hasFocus() || fAnio.hasFocus();
                Color brd = hasError ? ERROR_COL : focused ? PURPLE : COL_BRD;
                // Fondo
                g2.setColor(focused ? Color.WHITE : BG_FIELD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // Borde
                g2.setColor(brd);
                g2.setStroke(new BasicStroke(focused ? 1.8f : 1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                // Glow
                if (focused) {
                    g2.setColor(new Color(139, 92, 246, 30));
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(0, 8, 0, 8));

        fDia  = crearSegmento(2, "DD");
        fMes  = crearSegmento(2, "MM");
        fAnio = crearSegmento(4, "AAAA");

        // Separadores
        JLabel sep1 = separador();
        JLabel sep2 = separador();

        // Auto-avance DD → MM → AAAA
        fDia.addKeyListener(new SegmentListener(fDia, fMes,  2, 1, 31));
        fMes.addKeyListener(new SegmentListener(fMes, fAnio, 2, 1, 12));
        fAnio.addKeyListener(new SegmentListener(fAnio, null, 4, 1900, 2100));

        // Repintar el panel contenedor al cambiar foco
        FocusAdapter repaintFocus = new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { inner.repaint(); }
            @Override public void focusLost(FocusEvent e)   {
                inner.repaint();
                validar();
            }
        };
        fDia.addFocusListener(repaintFocus);
        fMes.addFocusListener(repaintFocus);
        fAnio.addFocusListener(repaintFocus);

        inner.add(fDia);
        inner.add(sep1);
        inner.add(fMes);
        inner.add(sep2);
        inner.add(fAnio);

        add(inner, BorderLayout.CENTER);
    }

    // ── Crea un segmento numérico (DD, MM o AAAA) ────────────────────
    private JTextField crearSegmento(int columnas, String placeholder) {
        JTextField f = new JTextField(columnas) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Placeholder si está vacío y sin foco
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = g2d(g);
                    g2.setColor(TXT_SEC);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth()  - fm.stringWidth(placeholder)) / 2;
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(placeholder, x, y);
                    g2.dispose();
                }
            }
        };
        f.setFont(new Font("Segoe UI", Font.BOLD, 13));
        f.setForeground(TXT_PRI);
        f.setOpaque(false);
        f.setCaretColor(PURPLE);
        f.setBorder(new EmptyBorder(8, 4, 8, 4));
        f.setHorizontalAlignment(JTextField.CENTER);
        f.setPreferredSize(new Dimension(
            columnas == 4 ? 52 : 32, 34));
        // Solo dígitos
        ((javax.swing.text.PlainDocument) f.getDocument())
            .setDocumentFilter(new javax.swing.text.DocumentFilter() {
                @Override public void insertString(FilterBypass fb, int off,
                        String str, javax.swing.text.AttributeSet a)
                        throws javax.swing.text.BadLocationException {
                    if (str != null && str.matches("\\d+") &&
                        fb.getDocument().getLength() + str.length() <= columnas)
                        super.insertString(fb, off, str, a);
                }
                @Override public void replace(FilterBypass fb, int off, int len,
                        String str, javax.swing.text.AttributeSet a)
                        throws javax.swing.text.BadLocationException {
                    if (str != null && str.matches("\\d*") &&
                        fb.getDocument().getLength() - len + str.length() <= columnas)
                        super.replace(fb, off, len, str, a);
                }
            });
        return f;
    }

    // ── Listener que avanza al siguiente campo al completarse ─────────
    private class SegmentListener extends KeyAdapter {
        private final JTextField actual;
        private final JTextField siguiente;
        private final int maxLen;
        private final int min, max;

        SegmentListener(JTextField actual, JTextField siguiente,
                        int maxLen, int min, int max) {
            this.actual    = actual;
            this.siguiente = siguiente;
            this.maxLen    = maxLen;
            this.min       = min;
            this.max       = max;
        }

        @Override public void keyReleased(KeyEvent e) {
            String txt = actual.getText();
            // Avance automático al completar los dígitos
            if (txt.length() == maxLen && siguiente != null) {
                // Validar rango antes de avanzar
                try {
                    int val = Integer.parseInt(txt);
                    if (val >= min && val <= max) {
                        siguiente.requestFocus();
                        siguiente.selectAll();
                    } else {
                        marcarError(actual);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    // ── Separador " / " ──────────────────────────────────────────────
    private JLabel separador() {
        JLabel l = new JLabel("/");
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(TXT_SEC);
        l.setBorder(new EmptyBorder(0, 2, 0, 2));
        return l;
    }

    // ── Validación visual ────────────────────────────────────────────
    private void marcarError(JTextField f) {
        f.setForeground(ERROR_COL);
        Timer t = new Timer(800, e -> f.setForeground(TXT_PRI));
        t.setRepeats(false);
        t.start();
    }

    private void validar() {
        hasError = false;
        String d = fDia.getText(), m = fMes.getText(), a = fAnio.getText();
        if (!d.isEmpty() && !m.isEmpty() && !a.isEmpty()) {
            try {
                LocalDate.of(Integer.parseInt(a),
                             Integer.parseInt(m),
                             Integer.parseInt(d));
            } catch (Exception ex) {
                hasError = true;
            }
        }
        repaint();
    }

    // ── API pública ──────────────────────────────────────────────────

    /** Devuelve el LocalDate ingresado, o null si está incompleto/inválido. */
    public LocalDate getValue() {
        try {
            int d = Integer.parseInt(fDia.getText());
            int m = Integer.parseInt(fMes.getText());
            int a = Integer.parseInt(fAnio.getText());
            return LocalDate.of(a, m, d);
        } catch (Exception e) {
            return null;
        }
    }

    /** Carga un LocalDate existente en los campos. */
    public void setValue(LocalDate date) {
        if (date == null) { fDia.setText(""); fMes.setText(""); fAnio.setText(""); return; }
        fDia.setText(String.format("%02d", date.getDayOfMonth()));
        fMes.setText(String.format("%02d", date.getMonthValue()));
        fAnio.setText(String.valueOf(date.getYear()));
    }

    /** Devuelve true si los 3 campos están vacíos. */
    public boolean isEmpty() {
        return fDia.getText().isEmpty() && fMes.getText().isEmpty() && fAnio.getText().isEmpty();
    }

    private static Graphics2D g2d(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return g2;
    }
}