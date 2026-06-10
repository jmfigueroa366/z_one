package css;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;

import static css.SesionStyles.*;

/**
 * Línea de tiempo horizontal de producción musical.
 * Animaciones: halo pulsante en nodo activo + línea de progreso animada.
 */
public class CancionTimelinePanel extends JPanel {

    public static final String[] FASES = {
        "Composición", "Pre-prod.", "Grabación", "Mezcla", "Master.", "Lanzamiento"
    };
    private static final String[] ICONOS = { "✍", "♪", "🎙", "🎚", "✦", "★" };
    private static final Color[] COLORES = {
        new Color(0x6366F1),
        new Color(0xF59E0B),
        new Color(0x1AA7E0),
        new Color(0x10B981),
        new Color(0xEC4899),
        new Color(0xF97316),
    };

    private int  faseActual   = -1;
    private String nombreCancion = "";

    // Animación pulso (halo)
    private float pulseRadius = 0f;
    private float pulseAlpha  = 0f;
    private boolean pulseGrow = true;
    private Timer pulseTimer;

    // Animación línea de progreso (fill)
    private float lineProgress = 0f;   // 0..1 fracción de la línea pintada
    private float lineTarget   = 0f;
    private Timer lineTimer;

    public CancionTimelinePanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(0, 100));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        setBorder(new EmptyBorder(6, 10, 6, 10));

        // Timer pulso: expande y desvanece el halo del nodo activo
        pulseTimer = new Timer(22, e -> {
            if (pulseGrow) {
                pulseRadius += 0.6f;
                pulseAlpha   = Math.max(0f, 0.55f - pulseRadius / 22f);
                if (pulseRadius >= 14f) { pulseRadius = 0f; pulseAlpha = 0.55f; }
            }
            repaint();
        });
        pulseTimer.setRepeats(true);

        // Timer línea: interpola suavemente hacia lineTarget
        lineTimer = new Timer(16, e -> {
            float diff = lineTarget - lineProgress;
            if (Math.abs(diff) < 0.002f) {
                lineProgress = lineTarget;
                ((Timer) e.getSource()).stop();
            } else {
                lineProgress += diff * 0.08f;
            }
            repaint();
        });
    }

    /** faseBD: 1-based (coincide con ID de BD). -1 o 0 = sin iniciar. */
    public void setFaseActual(int faseBD) {
        this.faseActual = faseBD - 1; // 0-based internamente
        // calcular target de la línea: fracción hasta el nodo activo
        int n = FASES.length;
        if (faseActual < 0) {
            lineTarget = 0f;
        } else {
            lineTarget = (float) faseActual / (n - 1);
        }
        // arrancar animaciones
        lineTimer.restart();
        if (faseActual >= 0) {
            pulseRadius = 0f; pulseAlpha = 0.55f;
            pulseTimer.restart();
        } else {
            pulseTimer.stop();
        }
        repaint();
    }

    public void setNombreCancion(String nombre) {
        this.nombreCancion = nombre != null ? nombre : "";
        repaint();
    }

    public void stopTimers() {
        pulseTimer.stop();
        lineTimer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

        int W  = getWidth();
        int H  = getHeight();
        int n  = FASES.length;
        int padX  = 44;
        int nodeY = H / 2 - 2;
        int nodeR = 15;

        // ── Fondo redondeado ────────────────────────────────────────
        g2.setColor(new Color(0xF8FAFC));
        g2.fillRoundRect(0, 0, W - 1, H - 1, 16, 16);
        g2.setColor(new Color(0xE2E8F0));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, W - 1, H - 1, 16, 16);

        // ── Nombre de canción ───────────────────────────────────────
        if (!nombreCancion.isBlank()) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
            g2.setColor(new Color(0x94A3B8));
            g2.drawString("♪  " + nombreCancion.toUpperCase(), 14, 13);
        }

        // ── Posiciones X de cada nodo ───────────────────────────────
        float step = (float)(W - padX * 2) / (n - 1);
        int[] cx = new int[n];
        for (int i = 0; i < n; i++) cx[i] = padX + Math.round(i * step);

        // ── Línea base gris ─────────────────────────────────────────
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(0xDDE3EE));
        g2.drawLine(cx[0], nodeY, cx[n - 1], nodeY);

        // ── Línea de progreso animada ───────────────────────────────
        if (lineProgress > 0f && faseActual >= 0) {
            Color col = COLORES[Math.min(faseActual, COLORES.length - 1)];
            int endX  = cx[0] + Math.round(lineProgress * (cx[n - 1] - cx[0]));
            GradientPaint gp = new GradientPaint(cx[0], nodeY, col.darker(), endX, nodeY, col);
            g2.setPaint(gp);
            g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx[0], nodeY, endX, nodeY);
        }

        // ── Nodos ───────────────────────────────────────────────────
        for (int i = 0; i < n; i++) {
            boolean done   = faseActual >= 0 && i < faseActual;
            boolean active = i == faseActual;
            boolean future = !done && !active;

            Color col = COLORES[i];

            // Halo pulsante en nodo activo
            if (active && pulseAlpha > 0f) {
                int hr = nodeR + (int) pulseRadius;
                g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(),
                        Math.round(pulseAlpha * 255)));
                g2.fillOval(cx[i] - hr, nodeY - hr, hr * 2, hr * 2);
            }

            // Sombra
            if (!future) {
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillOval(cx[i] - nodeR + 1, nodeY - nodeR + 3, nodeR * 2, nodeR * 2);
            }

            // Fondo nodo
            if (active) {
                // Gradiente radial simulado con dos capas
                g2.setColor(col.brighter());
                g2.fillOval(cx[i] - nodeR, nodeY - nodeR, nodeR * 2, nodeR * 2);
                g2.setColor(col);
                g2.fillOval(cx[i] - nodeR + 2, nodeY - nodeR + 2, nodeR * 2 - 4, nodeR * 2 - 4);
            } else if (done) {
                g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 200));
                g2.fillOval(cx[i] - nodeR, nodeY - nodeR, nodeR * 2, nodeR * 2);
            } else {
                g2.setColor(new Color(0xEEF2F7));
                g2.fillOval(cx[i] - nodeR, nodeY - nodeR, nodeR * 2, nodeR * 2);
            }

            // Borde nodo
            g2.setStroke(new BasicStroke(active ? 2.5f : 1.5f));
            if (active)       g2.setColor(col.darker());
            else if (done)    g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 160));
            else              g2.setColor(new Color(0xCBD5E1));
            g2.drawOval(cx[i] - nodeR, nodeY - nodeR, nodeR * 2, nodeR * 2);

            // Contenido del nodo
            if (done) {
                // Checkmark
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int bx = cx[i] - 5, by = nodeY;
                g2.drawLine(bx, by + 2,     bx + 4, by + 6);
                g2.drawLine(bx + 4, by + 6, bx + 10, by - 2);
            } else {
                // Icono texto
                Font iconFont = new Font("Segoe UI", Font.BOLD, active ? 11 : 9);
                g2.setFont(iconFont);
                g2.setColor(active ? Color.WHITE : new Color(0xA0AABA));
                FontMetrics fm = g2.getFontMetrics();
                String ico = ICONOS[i];
                g2.drawString(ico, cx[i] - fm.stringWidth(ico) / 2,
                              nodeY + fm.getAscent() / 2 - 1);
            }

            // Etiqueta debajo
            Font lblFont = new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, active ? 9 : 8);
            g2.setFont(lblFont);
            g2.setColor(active ? col.darker() : done ? new Color(0x64748B) : new Color(0xB0BAC8));
            FontMetrics fm = g2.getFontMetrics();
            String lbl = FASES[i];
            g2.drawString(lbl, cx[i] - fm.stringWidth(lbl) / 2, nodeY + nodeR + 14);
        }

        g2.dispose();
    }
}