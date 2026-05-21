package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MascotPanel — muñequito Z-One dibujado en Java2D.
 * Imita el personaje 3D: chico con hoodie oscuro, audífonos cyan,
 * pantallas holográficas y notas musicales flotantes.
 *
 * Uso: simplemente añade una instancia de MascotPanel a tu layout.
 * Llama a triggerExcited() cuando quieras animar al personaje.
 */
public class MascotPanel extends JPanel {

    // ── Animación ──────────────────────────────────────────────────────
    private float floatY      = 0f;
    private float wobble      = 0f;
    private float scale       = 1f;
    private float scaleTarget = 1f;
    private int   tick        = 0;

    private boolean excited     = false;
    private int     exciteTicks = 0;
    private int     noteCool    = 0;

    // ── Notas musicales ────────────────────────────────────────────────
    private static class Note {
        float x, y, vy, alpha, size;
        String g;
        Note(float x, float y) {
            this.x = x; this.y = y;
            vy = -(0.7f + (float) Math.random() * 1.1f);
            alpha = 1f;
            size = 10 + (float) Math.random() * 8;
            String[] s = {"♪","♫","♬","♩"};
            g = s[(int)(Math.random() * s.length)];
        }
        boolean tick() {
            y += vy; x += (float) Math.sin(y * 0.05) * 0.6f;
            alpha -= 0.014f;
            return alpha > 0;
        }
    }
    private final List<Note> notes = new ArrayList<>();

    // ── Paleta interna ─────────────────────────────────────────────────
    private static final Color SKIN       = new Color(255, 210, 170);
    private static final Color SKIN_DARK  = new Color(220, 170, 130);
    private static final Color HAIR       = new Color(90, 55, 30);
    private static final Color HAIR_DARK  = new Color(55, 30, 10);
    private static final Color HOODIE     = new Color(50, 50, 70);
    private static final Color HOODIE_D   = new Color(30, 30, 50);
    private static final Color CYAN       = new Color(0, 220, 255);
    private static final Color CYAN_DARK  = new Color(0, 160, 200);
    private static final Color HOLO_BLUE  = new Color(30, 180, 255, 180);
    private static final Color HOLO_GREEN = new Color(0, 240, 180, 160);
    private static final Color HOLO_PINK  = new Color(255, 80, 180, 140);
    private static final Color PURPLE     = new Color(139, 92, 246);
    private static final Color NOTE_COL   = new Color(180, 140, 255);

    // ── Glifo musical en el hoodie ─────────────────────────────────────
    private static final String CLEF = "<{}>";   // simplificado

    public MascotPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(230, 310));

        Timer t = new Timer(16, e -> {
            tick++;
            floatY = (float)(Math.sin(tick * 0.04) * 5.5);
            wobble = (float)(Math.sin(tick * 0.03) * 2.2);
            scale += (scaleTarget - scale) * 0.12f;

            notes.removeIf(n -> !n.tick());

            if (excited) {
                exciteTicks--;
                if (exciteTicks <= 0) excited = false;
                if (noteCool <= 0) {
                    notes.add(new Note(60 + (float)Math.random() * 110, 60 + (float)Math.random() * 40));
                    noteCool = 6;
                }
            } else {
                if (noteCool <= 0 && Math.random() < 0.018) {
                    notes.add(new Note(75 + (float)Math.random() * 80, 70 + (float)Math.random() * 30));
                    noteCool = 38;
                }
            }
            if (noteCool > 0) noteCool--;
            repaint();
        });
        t.start();

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { scaleTarget = 1.05f; triggerExcited(); }
            @Override public void mouseExited(MouseEvent e)  { scaleTarget = 1f; }
            @Override public void mouseClicked(MouseEvent e) { triggerExcited(); }
        });
    }

    /** Activa la animación de emoción (notas + globo) */
    public void triggerExcited() {
        excited = true;
        exciteTicks = 65;
    }

    // ── Pintura principal ──────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        // Aplicar transform: flotar + wobble + scale
        AffineTransform base = g2.getTransform();
        g2.translate(cx, cy + floatY);
        g2.rotate(Math.toRadians(wobble));
        g2.scale(scale, scale);
        g2.translate(-cx, -cy);

        drawShadow(g2, cx);
        drawHoloPanels(g2, cx, cy);
        drawBody(g2, cx, cy);
        drawHead(g2, cx, cy);
        drawHeadphones(g2, cx, cy);
        drawArms(g2, cx, cy);

        g2.setTransform(base);  // restaurar

        // Notas y globo (sin transform para que queden quietas en parte)
        drawNotes(g2);
        if (excited && exciteTicks > 28) drawBubble(g2, cx, (int)(cy + floatY));
    }

    // ── Sombra ─────────────────────────────────────────────────────────
    private void drawShadow(Graphics2D g2, int cx) {
        int sw = (int)(90 * scale);
        RadialGradientPaint sh = new RadialGradientPaint(
            cx, getHeight() - 18, sw / 2f,
            new float[]{0f, 1f},
            new Color[]{new Color(0,0,0,50), new Color(0,0,0,0)});
        g2.setPaint(sh);
        g2.fillOval(cx - sw/2, getHeight() - 26, sw, 16);
    }

    // ── Pantallas holográficas ─────────────────────────────────────────
    private void drawHoloPanels(Graphics2D g2, int cx, int cy) {
        // Panel izquierdo (código)
        drawHoloPanel(g2,
            cx - 95, cy + 10, 70, 52,
            -18, HOLO_BLUE,
            new String[]{"if func(){", " func(){", "  play"}
        );
        // Panel derecho (teclado/ecualizador)
        drawHoloPanel(g2,
            cx + 28, cy + 25, 65, 38,
            12, HOLO_GREEN,
            null
        );
        // Panel arriba-izquierdo (gráfico)
        drawHoloPanel(g2,
            cx - 80, cy - 30, 50, 38,
            -10, HOLO_PINK,
            null
        );
        drawKeyboard(g2, cx + 30, cy + 28, 62, 35);
        drawBarChart(g2, cx - 77, cy - 27, 46, 32);
    }

    private void drawHoloPanel(Graphics2D g2, int x, int y, int w, int h,
                                int angle, Color col, String[] lines) {
        AffineTransform t = g2.getTransform();
        g2.rotate(Math.toRadians(angle), x + w/2.0, y + h/2.0);
        // Fondo semi-transparente
        g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 50));
        g2.fillRoundRect(x, y, w, h, 6, 6);
        // Borde brillante
        g2.setColor(col);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(x, y, w, h, 6, 6);
        // Líneas de código si aplica
        if (lines != null) {
            g2.setFont(new Font("Courier New", Font.PLAIN, 8));
            g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 210));
            for (int i = 0; i < lines.length; i++) {
                g2.drawString(lines[i], x + 4, y + 12 + i * 12);
            }
        }
        g2.setTransform(t);
    }

    private void drawKeyboard(Graphics2D g2, int x, int y, int w, int h) {
        AffineTransform t = g2.getTransform();
        g2.rotate(Math.toRadians(12), x + w/2.0, y + h/2.0);
        int rows = 3, cols = 8;
        int kw = (w - 4) / cols, kh = (h - 4) / rows;
        g2.setColor(new Color(0, 240, 180, 120));
        g2.setStroke(new BasicStroke(0.8f));
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g2.drawRoundRect(x + 2 + c * kw, y + 2 + r * kh, kw - 1, kh - 1, 2, 2);
            }
        }
        g2.setTransform(t);
    }

    private void drawBarChart(Graphics2D g2, int x, int y, int w, int h) {
        AffineTransform t = g2.getTransform();
        g2.rotate(Math.toRadians(-10), x + w/2.0, y + h/2.0);
        int[] heights = {10, 18, 12, 22, 16, 20};
        int barW = w / heights.length;
        Color[] cols = {HOLO_PINK, HOLO_PINK, CYAN, CYAN, HOLO_GREEN, HOLO_GREEN};
        for (int i = 0; i < heights.length; i++) {
            int bh = heights[i];
            g2.setColor(cols[i]);
            g2.fillRoundRect(x + i * barW + 1, y + h - bh, barW - 2, bh, 2, 2);
        }
        g2.setTransform(t);
    }

    // ── Cuerpo / Hoodie ────────────────────────────────────────────────
    private void drawBody(Graphics2D g2, int cx, int cy) {
        // Torso principal
        int bx = cx - 38, by = cy + 30, bw = 76, bh = 85;

        // Gradiente del hoodie
        GradientPaint gp = new GradientPaint(bx, by, HOODIE, bx + bw, by + bh, HOODIE_D);
        g2.setPaint(gp);
        // Forma redondeada del cuerpo
        g2.fillRoundRect(bx, by, bw, bh, 22, 22);

        // Pliegues/sombra del hoodie
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillRoundRect(bx + 8, by + 20, 10, bh - 25, 5, 5);
        g2.fillRoundRect(bx + bw - 18, by + 20, 10, bh - 25, 5, 5);

        // Símbolo musical en el pecho (clef simplificado)
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.setColor(CYAN);
        FontMetrics fm = g2.getFontMetrics();
        String sym = "<{}>";
        g2.drawString(sym, cx - fm.stringWidth(sym)/2, by + 48);

        // Ribete / capucha
        g2.setColor(new Color(HOODIE.getRed() + 20, HOODIE.getGreen() + 20, HOODIE.getBlue() + 30));
        g2.setStroke(new BasicStroke(1.5f));
        // línea central del cierre
        g2.drawLine(cx, by + 10, cx, by + bh - 5);

        // Bolsillo frontal
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRoundRect(cx - 18, by + bh - 28, 36, 22, 10, 10);
        g2.setColor(new Color(HOODIE.getRed() + 15, HOODIE.getGreen() + 15, HOODIE.getBlue() + 25));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(cx - 18, by + bh - 28, 36, 22, 10, 10);
    }

    // ── Cabeza ─────────────────────────────────────────────────────────
    private void drawHead(Graphics2D g2, int cx, int cy) {
        int hx = cx - 28, hy = cy - 42, hw = 56, hh = 52;

        // Cuello
        g2.setColor(SKIN_DARK);
        g2.fillRoundRect(cx - 10, hy + hh - 6, 20, 20, 6, 6);
        g2.setColor(SKIN);
        g2.fillRoundRect(cx - 9, hy + hh - 8, 18, 18, 6, 6);

        // Cara
        GradientPaint face = new GradientPaint(hx, hy, SKIN, hx + hw, hy + hh, SKIN_DARK);
        g2.setPaint(face);
        g2.fillRoundRect(hx, hy, hw, hh, 22, 22);

        // Sombra sutil en mejillas
        g2.setColor(new Color(220, 150, 120, 60));
        g2.fillOval(hx + 4, hy + hh/2, 14, 10);
        g2.fillOval(hx + hw - 18, hy + hh/2, 14, 10);

        // Ojos
        g2.setColor(new Color(50, 30, 20));
        g2.fillOval(cx - 16, hy + 18, 10, 11);
        g2.fillOval(cx + 6, hy + 18, 10, 11);
        // Brillo en ojos
        g2.setColor(Color.WHITE);
        g2.fillOval(cx - 13, hy + 19, 4, 4);
        g2.fillOval(cx + 9, hy + 19, 4, 4);

        // Nariz
        g2.setColor(SKIN_DARK);
        g2.fillOval(cx - 3, hy + 30, 6, 5);

        // Sonrisa
        g2.setColor(new Color(180, 100, 80));
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Arc2D smile = new Arc2D.Float(cx - 12, hy + 34, 24, 12, 200, 140, Arc2D.OPEN);
        g2.draw(smile);

        // Cabello
        drawHair(g2, cx, hx, hy, hw, hh);
    }

    private void drawHair(Graphics2D g2, int cx, int hx, int hy, int hw, int hh) {
        // Base del cabello (cobertura superior)
        g2.setColor(HAIR);
        // Capa superior
        g2.fillRoundRect(hx - 2, hy - 6, hw + 4, 28, 20, 20);
        // Mechones laterales
        g2.fillOval(hx - 4, hy, 14, 26);
        g2.fillOval(hx + hw - 10, hy, 14, 26);
        // Flequillo hacia adelante (mechones)
        g2.setColor(HAIR_DARK);
        // mechón central
        g2.fillOval(cx - 8, hy - 8, 16, 18);
        // mechones laterales del flequillo
        g2.fillOval(cx - 20, hy - 4, 14, 14);
        g2.fillOval(cx + 6, hy - 4, 14, 14);
        // Línea de detalle del cabello
        g2.setColor(new Color(HAIR.getRed() - 20, HAIR.getGreen() - 10, HAIR.getBlue() - 5));
        g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(cx - 15, hy - 5, 30, 20, 30, 120);
    }

    // ── Audífonos ──────────────────────────────────────────────────────
    private void drawHeadphones(Graphics2D g2, int cx, int cy) {
        int hy = cy - 42;

        // Arco de la diadema
        g2.setColor(CYAN_DARK);
        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Arc2D arc = new Arc2D.Float(cx - 32, hy - 12, 64, 50, 20, 140, Arc2D.OPEN);
        g2.draw(arc);
        // Brillo en el arco
        g2.setColor(CYAN);
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Arc2D arcH = new Arc2D.Float(cx - 28, hy - 8, 56, 42, 25, 130, Arc2D.OPEN);
        g2.draw(arcH);

        // Copa izquierda
        drawCup(g2, cx - 38, hy + 14, true);
        // Copa derecha
        drawCup(g2, cx + 26, hy + 14, false);
    }

    private void drawCup(Graphics2D g2, int x, int y, boolean left) {
        // Cuerpo de la copa
        g2.setColor(CYAN_DARK);
        g2.fillRoundRect(x, y, 14, 22, 6, 6);
        // Detalle interior brillante
        GradientPaint gp = new GradientPaint(
            x, y, CYAN,
            x + 14, y + 22, new Color(0, 100, 160));
        g2.setPaint(gp);
        g2.fillRoundRect(x + 2, y + 3, 10, 16, 4, 4);
        // Indicador LED (pequeño punto brillante)
        g2.setColor(Color.WHITE);
        g2.fillOval(x + (left ? 8 : 4), y + 6, 3, 3);
        // Ondas sonoras (3 arcos pequeños)
        g2.setColor(new Color(CYAN.getRed(), CYAN.getGreen(), CYAN.getBlue(), 160));
        g2.setStroke(new BasicStroke(1f));
        int ox = left ? x - 6 : x + 14;
        for (int i = 1; i <= 3; i++) {
            int r = i * 5;
            if (left) {
                g2.drawArc(ox - r, y + 11 - r/2, r * 2, r, 270, 180);
            } else {
                g2.drawArc(ox - r, y + 11 - r/2, r * 2, r, 90, 180);
            }
        }
    }

    // ── Brazos ─────────────────────────────────────────────────────────
    private void drawArms(Graphics2D g2, int cx, int cy) {
        // Brazo izquierdo (sosteniendo pluma/stylus)
        drawArm(g2, cx - 38, cy + 38, cx - 68, cy + 18, true);
        // Brazo derecho (sobre el teclado)
        drawArm(g2, cx + 38, cy + 38, cx + 65, cy + 35, false);

        // Mano izquierda con stylus
        g2.setColor(SKIN);
        g2.fillOval(cx - 76, cy + 12, 16, 18);
        // Stylus
        g2.setColor(new Color(200, 200, 220));
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - 68, cy + 8, cx - 80, cy - 8);
        g2.setColor(CYAN);
        g2.fillOval(cx - 82, cy - 11, 5, 5);

        // Mano derecha
        g2.setColor(SKIN);
        g2.fillOval(cx + 60, cy + 28, 16, 18);
    }

    private void drawArm(Graphics2D g2, int x1, int y1, int x2, int y2, boolean left) {
        // Manga del hoodie
        g2.setColor(HOODIE);
        g2.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x1, y1, x2, y2);
        // Borde oscuro para profundidad
        g2.setColor(HOODIE_D);
        g2.setStroke(new BasicStroke(14f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // sombra en el borde
        g2.drawLine(x1 + (left ? -2 : 2), y1, x2 + (left ? -2 : 2), y2);
    }

    // ── Notas musicales ────────────────────────────────────────────────
    private void drawNotes(Graphics2D g2) {
        for (Note n : notes) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, (int) n.size));
            g2.setColor(new Color(NOTE_COL.getRed(), NOTE_COL.getGreen(),
                NOTE_COL.getBlue(), (int)(n.alpha * 220)));
            g2.drawString(n.g, (int) n.x, (int) n.y);
        }
    }

    // ── Globo de saludo ────────────────────────────────────────────────
    private void drawBubble(Graphics2D g2, int cx, int baseY) {
        float alpha = Math.min(1f, (exciteTicks - 28) / 30f);
        int bx = cx + 28, by = baseY - 80;
        // Fondo
        g2.setColor(new Color(PURPLE.getRed(), PURPLE.getGreen(), PURPLE.getBlue(),
            (int)(alpha * 190)));
        g2.fillRoundRect(bx, by, 82, 28, 12, 12);
        // Colita
        int[] px = {bx + 6, bx - 4, bx + 14};
        int[] py = {by + 26, by + 34, by + 26};
        g2.fillPolygon(px, py, 3);
        // Texto
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(new Color(255, 255, 255, (int)(alpha * 230)));
        g2.drawString("¡Hola! ♪", bx + 10, by + 18);
    }


}