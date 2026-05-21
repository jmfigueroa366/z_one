package view;

import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static view.ModernUI.*;

/**
 * DashboardPanel — Panel principal de inicio.
 *
 * Aplica los patrones GRASP:
 *   - Information Expert: cada componente (StatCard, ActivityItem, QuickAction)
 *     conoce y maneja sus propios datos.
 *   - High Cohesion: clase enfocada solo en mostrar el resumen del sistema.
 *   - Low Coupling: depende solo de Usuario y ModernUI.
 *   - Pure Fabrication: las clases internas (StatCard, IconRenderer)
 *     son creaciones puras para encapsular comportamiento visual.
 *   - Polymorphism: cada IconType se dibuja distinto pero por la misma interfaz.
 */
public class DashboardPanel extends JPanel {

    private final Usuario usuario;

    public DashboardPanel(Usuario usuario) {
        this.usuario = usuario;
        construirUI();
    }

    // =================================================================
    // CONSTRUCCIÓN DE LA INTERFAZ
    // =================================================================
    private void construirUI() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));

        add(construirHeader(),     BorderLayout.NORTH);
        add(construirContenido(),  BorderLayout.CENTER);
    }

    private JPanel construirHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Bienvenido, " + usuario.getNombre());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(TEXT_PRIMARY);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Aquí tienes el resumen de la actividad de tu productora");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(TEXT_MUTED);
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);

        header.add(titulo);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitulo);
        return header;
    }

    private JComponent construirContenido() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(construirStatsRow());
        content.add(Box.createVerticalStrut(24));
        content.add(construirMiddleRow());
        content.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // ===== Fila de Stats =====
    private JPanel construirStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        row.setPreferredSize(new Dimension(0, 140));

        row.add(new StatCard("ARTISTAS",    "24", "+3 este mes",  new Color(59, 130, 246), IconType.PERSON));
        row.add(new StatCard("PRODUCTORES", "8",  "todos activos", new Color(139, 92, 246), IconType.MIC));
        row.add(new StatCard("SESIONES",    "12", "este mes",      new Color(245, 158, 11), IconType.CALENDAR));
        row.add(new StatCard("CANCIONES",   "87", "+5 publicadas", new Color(236, 72, 153), IconType.MUSIC));
        return row;
    }

    // ===== Fila de Actividad + Acciones =====
    private JPanel construirMiddleRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 20, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));
        row.setPreferredSize(new Dimension(0, 380));

        row.add(construirCardActividad());
        row.add(construirCardAcciones());
        return row;
    }

    private JPanel construirCardActividad() {
        ModernUI.CardPanel card = new ModernUI.CardPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel titulo = new JLabel("Actividad reciente");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(TEXT_PRIMARY);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Últimos eventos del sistema");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        card.add(titulo);
        card.add(Box.createVerticalStrut(2));
        card.add(sub);
        card.add(Box.createVerticalStrut(18));
        card.add(new ActivityItem("CV", "Carlos Vives grabó Pa Mayte",        "hace 2 horas", SUCCESS));
        card.add(Box.createVerticalStrut(4));
        card.add(new ActivityItem("SH", "Shakira publicó Hips Don't Lie",     "hace 5 horas", ACCENT_CYAN));
        card.add(Box.createVerticalStrut(4));
        card.add(new ActivityItem("JB", "Sesión programada con J Balvin",     "mañana 14:00", PRIMARY));
        card.add(Box.createVerticalStrut(4));
        card.add(new ActivityItem("MA", "Nuevo productor: Maluma",            "hace 1 día",   PRIMARY_LIGHT));
        card.add(Box.createVerticalStrut(4));
        card.add(new ActivityItem("OR", "Álbum Oral Fixation Vol. 2 listo",   "hace 2 días",  ACCENT_PINK));
        return card;
    }
    private JPanel construirCardAcciones() {
        ModernUI.CardPanel card = new ModernUI.CardPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel titulo = new JLabel("Acciones rápidas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(TEXT_PRIMARY);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Tareas comunes con un solo clic");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        card.add(titulo);
        card.add(Box.createVerticalStrut(2));
        card.add(sub);
        card.add(Box.createVerticalStrut(18));

        card.add(new QuickAction("Nuevo artista",    "Registrar un artista nuevo",   IconType.PERSON,   new Color(59, 130, 246)));
        card.add(Box.createVerticalStrut(8));
        card.add(new QuickAction("Nueva canción",    "Agregar al catálogo musical",  IconType.MUSIC,    new Color(236, 72, 153)));
        card.add(Box.createVerticalStrut(8));
        card.add(new QuickAction("Programar sesión", "Reservar cabina y agendar",    IconType.CALENDAR, new Color(245, 158, 11)));
        card.add(Box.createVerticalStrut(8));
        card.add(new QuickAction("Ver estadísticas", "Top canciones y métricas",     IconType.CHART,    new Color(139, 92, 246)));
        return card;
    }
    // =================================================================
    // ENUM de iconos — Polymorphism: cada icono se dibuja distinto
    // =================================================================
    private enum IconType { PERSON, MIC, MUSIC, CALENDAR, CHART }
    // =================================================================
    // Pure Fabrication: dibuja iconos vectoriales con Graphics2D
    // =================================================================
    private static class IconRenderer {
        static void draw(Graphics2D g2, IconType type, int x, int y, int size, Color color) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(size / 12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            switch (type) {
                case PERSON:
                    int hr = size / 4;
                    g2.fillOval(x + size/2 - hr, y + size/6, hr*2, hr*2);
                    g2.fillRoundRect(x + size/6, y + size/2 + size/8, size - size/3, size/3, size/6, size/6);
                    break;
                case MIC:
                    int mw = size / 3;
                    g2.fillRoundRect(x + size/2 - mw/2, y + size/8, mw, size*2/3, mw, mw);
                    g2.drawArc(x + size/4, y + size/2, size/2, size/3, 0, -180);
                    g2.drawLine(x + size/2, y + size*4/5, x + size/2, y + size - 2);
                    break;
                case MUSIC:
                    g2.setStroke(new BasicStroke(size / 10f));
                    g2.drawLine(x + size/3, y + size/5, x + size/3, y + size*4/5);
                    g2.drawLine(x + size*2/3, y + size/8, x + size*2/3, y + size*3/4);
                    g2.drawLine(x + size/3, y + size/5, x + size*2/3, y + size/8);
                    g2.fillOval(x + size/8, y + size*2/3, size/4, size/5);
                    g2.fillOval(x + size/2, y + size*3/5, size/4, size/5);
                    break;
                case CALENDAR:
                    int p = size / 8;
                    g2.drawRoundRect(x + p, y + p*2, size - p*2, size - p*3, p, p);
                    g2.fillRect(x + p, y + p*2, size - p*2, p*2);
                    g2.setColor(color);
                    g2.drawLine(x + p*3, y + p, x + p*3, y + p*3);
                    g2.drawLine(x + size - p*3, y + p, x + size - p*3, y + p*3);
                    break;
                case CHART:
                    int bw = size / 5;
                    int bx = x + size/6;
                    g2.fillRoundRect(bx,           y + size*3/5, bw, size*2/5 - 2, 2, 2);
                    g2.fillRoundRect(bx + bw + 4,  y + size*2/5, bw, size*3/5 - 2, 2, 2);
                    g2.fillRoundRect(bx + bw*2+ 8, y + size/4,   bw, size*3/4 - 2, 2, 2);
                    break;
            }
        }
    }
    // =================================================================
    // STAT CARD — Information Expert: conoce y dibuja sus propios datos
    // =================================================================
    private static class StatCard extends JPanel {
        private final String titulo, valor, detalle;
        private final Color  accent;
        private final IconType icon;
        private boolean hover = false;

        public StatCard(String titulo, String valor, String detalle, Color accent, IconType icon) {
            this.titulo  = titulo;
            this.valor   = valor;
            this.detalle = detalle;
            this.accent  = accent;
            this.icon    = icon;
            setOpaque(false);
            setPreferredSize(new Dimension(0, 130));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) {
                    MainFrame.showToast("Ver detalles de " + titulo, MainFrame.ToastType.INFO);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            // Sombra suave
            for (int i = 0; i < 6; i++) {
                g2.setColor(new Color(0, 0, 0, 6));
                g2.fillRoundRect(i, i + 2, w - i*2, h - i*2, 18, 18);
            }
            // Fondo
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, w - 4, h - 4, 18, 18);
            // Borde
            g2.setColor(hover ? accent : new Color(139, 92, 246, 40));
            g2.setStroke(new BasicStroke(hover ? 2f : 1f));
            g2.drawRoundRect(0, 0, w - 5, h - 5, 18, 18);
            // Barra acento izquierda
            g2.setColor(accent);
            g2.fillRoundRect(0, 16, 3, h - 36, 3, 3);

            // Icono grande arriba derecha con halo difuso
            int iconSize = 36;
            int ix = w - iconSize - 24;
            int iy = 20;
            // Halo
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
            g2.fillOval(ix - 8, iy - 8, iconSize + 16, iconSize + 16);
            // Icono
            IconRenderer.draw(g2, icon, ix, iy, iconSize,
                              new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 200));

            // Título
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(TEXT_MUTED);
            g2.drawString(titulo, 20, 32);

            // Valor grande
            g2.setFont(new Font("Segoe UI", Font.BOLD, 38));
            g2.setColor(TEXT_PRIMARY);
            g2.drawString(valor, 20, 78);

            // Detalle
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(accent);
            g2.drawString(detalle, 20, h - 22);

            g2.dispose();
        }
    }

    // =================================================================
    // ACTIVITY ITEM — High Cohesion: solo muestra un evento
    // =================================================================
    private static class ActivityItem extends JPanel {
        private final String iniciales, texto, tiempo;
        private final Color color;

        public ActivityItem(String iniciales, String texto, String tiempo, Color color) {
            this.iniciales = iniciales;
            this.texto = texto;
            this.tiempo = tiempo;
            this.color = color;
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setPreferredSize(new Dimension(0, 46));
            setAlignmentX(LEFT_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            int h = getHeight();
            int w = getWidth();

            // Avatar circular con iniciales
            int av = 32;
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
            g2.fillOval(4, (h - av)/2, av, av);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(4, (h - av)/2, av, av);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(iniciales);
            g2.drawString(iniciales,
                          4 + (av - tw)/2,
                          (h + fm.getAscent() - fm.getDescent())/2);

            // Texto
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g2.setColor(TEXT_SECONDARY);
            int textY = (h + fm.getAscent() - fm.getDescent())/2 + 1;
            g2.drawString(texto, 4 + av + 14, textY);

            // Tiempo a la derecha
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(TEXT_MUTED);
            FontMetrics fm2 = g2.getFontMetrics();
            int tiempoW = fm2.stringWidth(tiempo);
            g2.drawString(tiempo, w - tiempoW - 4, textY);

            g2.dispose();
        }
    }

    // =================================================================
    // QUICK ACTION — clic dispara feedback (toast)
    // =================================================================
    private static class QuickAction extends JPanel {
        private final String titulo, descripcion;
        private final IconType icon;
        private final Color accent;
        private boolean hover = false;

        public QuickAction(String titulo, String descripcion, IconType icon, Color accent) {
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.icon = icon;
            this.accent = accent;
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
            setPreferredSize(new Dimension(0, 56));
            setAlignmentX(LEFT_ALIGNMENT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) {
                    MainFrame.showToast("Acción: " + titulo, MainFrame.ToastType.SUCCESS);
                }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            // Fondo
            if (hover) {
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
                g2.fillRoundRect(0, 0, w, h, 12, 12);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
            } else {
                g2.setColor(new Color(35, 32, 70, 80));
                g2.fillRoundRect(0, 0, w, h, 12, 12);
            }

            // Icono cuadrado a la izquierda con fondo de color
            int boxSize = 36;
            int bx = 10;
            int by = (h - boxSize)/2;
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 50));
            g2.fillRoundRect(bx, by, boxSize, boxSize, 8, 8);
            IconRenderer.draw(g2, icon, bx + 8, by + 8, boxSize - 16, accent);

            // Título
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(TEXT_PRIMARY);
            g2.drawString(titulo, bx + boxSize + 14, by + 16);

            // Descripción
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(TEXT_MUTED);
            g2.drawString(descripcion, bx + boxSize + 14, by + 32);

            // Flecha derecha
            g2.setColor(hover ? accent : TEXT_MUTED);
            g2.setStroke(new BasicStroke(1.5f));
            int ax = w - 22;
            int ay = h / 2;
            g2.drawLine(ax, ay - 4, ax + 4, ay);
            g2.drawLine(ax, ay + 4, ax + 4, ay);

            g2.dispose();
        }
    }
}