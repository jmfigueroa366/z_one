package view;

import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static view.ModernUI.*;

public class MainFrame extends JFrame {

    public enum ToastType { SUCCESS, INFO, ERROR }

    private final Usuario usuarioActual;
    private JPanel        contentPanel;
    private CardLayout    cardLayout;
    private SidebarItem   activeItem;
    

    private static MainFrame instance;

    // ── Paleta sidebar blanco ─────────────────────────────────────────
    private static final Color SB_BG        = new Color(255, 255, 255);
    private static final Color SB_BG2       = new Color(248, 249, 252);
    private static final Color SB_BORDER    = new Color(226, 232, 240);
    private static final Color SB_ACCENT    = new Color(109, 40, 217);
    private static final Color SB_ACCENT2   = new Color(37,  99, 235);
    private static final Color SB_GREEN     = new Color(22, 163,  74);
    private static final Color SB_TEXT      = new Color( 30,  41,  59);
    private static final Color SB_MUTED     = new Color(148, 163, 184);
    private static final Color SB_ACTIVE_BG = new Color(237, 233, 254);
    private static final Color SB_ACTIVE_FG = new Color(109,  40, 217);
    private static final Color SB_HOVER_BG  = new Color(241, 245, 249);
    private static final Color SB_RED       = new Color(220,  38,  38);

    public MainFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        instance = this;
        inicializarUI();
    }

    // =================================================================
    //  UI PRINCIPAL
    // =================================================================
    private void inicializarUI() {
        setTitle("Z-One Music — " + usuarioActual.getNombreCompleto());
        setSize(1280, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1100, 680));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(SB_BG);

        root.add(construirSidebar(), BorderLayout.WEST);

        // ── FONDO ANIMADO CON ONDAS MUSICALES + ESTRELLAS + GLOWS ──
        JPanel contenidoFondo = new FondoAnimado();
        contenidoFondo.setLayout(new BorderLayout());

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(24, 24, 24, 24));
        contentPanel.add(new DashboardPanel(usuarioActual),    "dashboard");
        contentPanel.add(new formArtista(),                    "artistas");
        contentPanel.add(new formProductor(),                  "productores");
        contentPanel.add(new formCancion(),                    "canciones");
        contentPanel.add(new formSesion(),                     "sesiones");
        contentPanel.add(new formCabina(),                     "cabinas");
        contentPanel.add(new formEvento(),                     "eventos");
        contentPanel.add(new formColaboracion(),               "colaboraciones");
        contentPanel.add(new formCalendario(),                 "calendario");
        contentPanel.add(new Jesusitochatview(),               "asistente");
        contentPanel.add(new formConfiguracion(usuarioActual), "configuracion");

        contenidoFondo.add(contentPanel,         BorderLayout.CENTER);
        contenidoFondo.add(construirStatusBar(), BorderLayout.SOUTH);

        root.add(contenidoFondo, BorderLayout.CENTER);
        setContentPane(root);
    }

    // =================================================================
    //  🌊 CLASE INTERNA: FONDO ANIMADO
    // =================================================================
    private static class FondoAnimado extends JPanel {
        private float tiempo = 0f;
        private final Random rnd = new Random(42);
        // Estrellas precomputadas
        private final float[] estrellasX = new float[40];
        private final float[] estrellasY = new float[40];
        private final float[] estrellasFase = new float[40];
        private final int[]   estrellasTipo = new int[40];   // 0=punto, 1=estrella, 2=nota

        public FondoAnimado() {
            setOpaque(false);
            // Inicializar estrellas/notas
            for (int i = 0; i < 40; i++) {
                estrellasX[i] = rnd.nextFloat();
                estrellasY[i] = rnd.nextFloat();
                estrellasFase[i] = rnd.nextFloat() * (float)(Math.PI * 2);
                estrellasTipo[i] = rnd.nextInt(3);
            }
            // Timer animación
            Timer animTimer = new Timer(40, e -> {
                tiempo += 0.015f;
                repaint();
            });
            animTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // ── 1. FONDO BASE GRIS CLARO ──
            g2.setColor(new Color(0xF7F8FA));
            g2.fillRect(0, 0, w, h);

            // ── 2. GLOWS GRANDES (manchas suaves de color) ──
            // Glow violeta arriba-derecha
            float glowX = w * 0.85f + (float)(Math.sin(tiempo * 0.5f) * 25);
            float glowY = h * 0.18f + (float)(Math.cos(tiempo * 0.5f) * 20);
            RadialGradientPaint glow = new RadialGradientPaint(
                glowX, glowY, 220,
                new float[]{0f, 1f},
                new Color[]{
                    new Color(139, 92, 246, 28),
                    new Color(139, 92, 246, 0)
                }
            );
            g2.setPaint(glow);
            g2.fillOval((int)(glowX - 220), (int)(glowY - 220), 440, 440);

            // Glow cyan abajo-izquierda
            float glow2X = w * 0.10f + (float)(Math.cos(tiempo * 0.4f) * 20);
            float glow2Y = h * 0.85f + (float)(Math.sin(tiempo * 0.4f) * 25);
            RadialGradientPaint glow2 = new RadialGradientPaint(
                glow2X, glow2Y, 270,
                new float[]{0f, 1f},
                new Color[]{
                    new Color(6, 182, 212, 22),
                    new Color(6, 182, 212, 0)
                }
            );
            g2.setPaint(glow2);
            g2.fillOval((int)(glow2X - 270), (int)(glow2Y - 270), 540, 540);

            // Glow rosa centro-arriba
            float glow3X = w * 0.50f + (float)(Math.sin(tiempo * 0.3f) * 30);
            float glow3Y = h * 0.35f + (float)(Math.cos(tiempo * 0.3f) * 20);
            RadialGradientPaint glow3 = new RadialGradientPaint(
                glow3X, glow3Y, 180,
                new float[]{0f, 1f},
                new Color[]{
                    new Color(236, 72, 153, 15),
                    new Color(236, 72, 153, 0)
                }
            );
            g2.setPaint(glow3);
            g2.fillOval((int)(glow3X - 180), (int)(glow3Y - 180), 360, 360);

            // ── 3. ESTRELLAS / NOTAS / PUNTOS PARPADEANTES ──
            String[] notas = {"♪", "♫", "♬", "♩"};
            for (int i = 0; i < 40; i++) {
                float x = estrellasX[i] * w;
                // Movimiento vertical lento (notas suben)
                float y = (estrellasY[i] * h + tiempo * 6f * (1 + (i % 3) * 0.3f)) % (h + 30) - 15;

                // Parpadeo
                float bright = (float)(0.5f + 0.5f * Math.sin(tiempo * 1.5f + estrellasFase[i]));
                int alpha = (int)(15 + bright * 20);

                Color col;
                if (i % 3 == 0) col = new Color(139, 92, 246, alpha);   // violeta
                else if (i % 3 == 1) col = new Color(6, 182, 212, alpha); // cyan
                else col = new Color(236, 72, 153, alpha);                 // rosa
                g2.setColor(col);

                if (estrellasTipo[i] == 0) {
                    // Punto pequeño
                    int size = 3 + (int)(bright * 2);
                    g2.fillOval((int)x, (int)y, size, size);
                } else if (estrellasTipo[i] == 1) {
                    // Estrella 4 puntas
                    int s = 4 + (int)(bright * 3);
                    g2.fillOval((int)(x - s/2), (int)y, s, 2);
                    g2.fillOval((int)x, (int)(y - s/2), 2, s);
                } else {
                    // Nota musical
                    g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12 + (i % 3) * 3));
                    g2.drawString(notas[i % notas.length], x, y);
                }
            }

            // ── 4. ECUALIZADOR ANIMADO ARRIBA (línea sutil) ──
            int eqY = 80;
            int barras = 60;
            int barW = w / barras;
            for (int b = 0; b < barras; b++) {
                float altura = (float)(Math.sin(tiempo * 2 + b * 0.3f) * 0.5f + 0.5f) * 8f
                             + (float)(Math.sin(tiempo * 3.5f + b * 0.5f) * 0.5f + 0.5f) * 4f;
                int alpha = 18;
                Color cb = b % 3 == 0 ? new Color(139, 92, 246, alpha)
                         : b % 3 == 1 ? new Color(6, 182, 212, alpha)
                         : new Color(236, 72, 153, alpha);
                g2.setColor(cb);
                int bx = b * barW + 2;
                int bh = Math.max(2, (int)altura);
                g2.fillRoundRect(bx, eqY - bh, barW - 4, bh, 2, 2);
            }

            // ── 5. ONDAS MUSICALES INFERIORES ──
            Color[] colores = {
                new Color(0x8B5CF6),   // violeta
                new Color(0x06B6D4),   // cyan
                new Color(0xEC4899)    // rosa
            };
            float[] fases = {0f, 2.0f, 4.0f};
            float[] amplitudes = {28f, 22f, 18f};
            float[] frecuencias = {0.008f, 0.012f, 0.010f};
            float[] velocidades = {1.0f, 1.5f, 0.8f};
            int[] alphas = {22, 18, 14};
            int[] desplY = {h - 180, h - 120, h - 60};

            for (int i = 0; i < 3; i++) {
                int dy = desplY[i];
                Color c = colores[i];

                Path2D.Float path = new Path2D.Float();
                path.moveTo(0, h);
                for (int x = 0; x <= w; x += 4) {
                    float y = dy
                            + (float)(Math.sin(x * frecuencias[i] + tiempo * velocidades[i] + fases[i])
                                * amplitudes[i])
                            + (float)(Math.sin(x * frecuencias[i] * 2 + tiempo * velocidades[i] * 1.3f)
                                * amplitudes[i] * 0.3f);
                    path.lineTo(x, y);
                }
                path.lineTo(w, h);
                path.closePath();

                GradientPaint gp = new GradientPaint(
                    0, dy - 40,
                    new Color(c.getRed(), c.getGreen(), c.getBlue(), alphas[i]),
                    0, h,
                    new Color(c.getRed(), c.getGreen(), c.getBlue(), 0)
                );
                g2.setPaint(gp);
                g2.fill(path);
            }

            // ── 6. PARTÍCULAS BRILLANTES OCASIONALES ──
            for (int i = 0; i < 6; i++) {
                float px = (float)((Math.sin(tiempo * 0.4f + i * 1.3f) + 1) / 2 * w);
                float py = (float)((Math.cos(tiempo * 0.3f + i * 1.7f) + 1) / 2 * h);
                float brightP = (float)(0.5f + 0.5f * Math.sin(tiempo * 2f + i));
                int sa = (int)(brightP * 40);

                RadialGradientPaint pgp = new RadialGradientPaint(
                    px, py, 12,
                    new float[]{0f, 1f},
                    new Color[]{
                        new Color(167, 139, 250, sa),
                        new Color(167, 139, 250, 0)
                    }
                );
                g2.setPaint(pgp);
                g2.fillOval((int)(px - 12), (int)(py - 12), 24, 24);
            }

            g2.dispose();
        }
    }

    // =================================================================
    //  SIDEBAR
    // =================================================================
    private JPanel construirSidebar() {

        JPanel sidebar = new JPanel() {
            private float shimmerX = -1f;
            private final Timer shimmer = new Timer(30, e -> {
                shimmerX += 0.008f;
                if (shimmerX > 2f) shimmerX = -1f;
                repaint();
            });
            { shimmer.start(); }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setPaint(new GradientPaint(0, 0, SB_BG, 0, getHeight(), SB_BG2));
                g2.fillRect(0, 0, getWidth(), getHeight());

                float y0 = shimmerX * getHeight();
                g2.setPaint(new GradientPaint(
                    0, y0 - 60, new Color(109, 40, 217, 0),
                    0, y0,      new Color(109, 40, 217, 8)
                ));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setPaint(new GradientPaint(
                    0, 0,            new Color(109, 40, 217, 120),
                    0, getHeight(),  new Color(37,  99, 235, 80)
                ));
                g2.fillRect(getWidth() - 1, 0, 1, getHeight());

                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(24, 0, 16, 0));

        sidebar.add(construirLogo());
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(construirSeparador());
        sidebar.add(Box.createVerticalStrut(14));

 Map<String, String[]> items = new LinkedHashMap<>();
items.put("Dashboard",      new String[]{"dashboard",      "▦"});
items.put("Artistas",       new String[]{"artistas",       "♪"});
items.put("Productores",    new String[]{"productores",    "♬"});
items.put("Canciones",      new String[]{"canciones",      "♫"});
items.put("Sesiones",       new String[]{"sesiones",       "◉"});
items.put("Cabinas",        new String[]{"cabinas",        "▣"});
items.put("Eventos",        new String[]{"eventos",        "★"});
items.put("Colaboraciones", new String[]{"colaboraciones", "⇄"});
items.put("Calendario",     new String[]{"calendario",     "▤"});
items.put("Asistente",      new String[]{"asistente",      "✦"});
items.put("Configuración",  new String[]{"configuracion",  "⚙"});

        for (Map.Entry<String, String[]> e : items.entrySet()) {
            SidebarItem item = new SidebarItem(e.getKey(), e.getValue()[1]);
            if (e.getValue()[0].equals("dashboard")) {
                item.setActive(true);
                activeItem = item;
            }
            item.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent ev) {
                    if (activeItem != null) activeItem.setActive(false);
                    item.setActive(true);
                    activeItem = item;
                    cardLayout.show(contentPanel, e.getValue()[0]);
                }
            });
            sidebar.add(item);
            sidebar.add(Box.createVerticalStrut(2));
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(construirSeparador());
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(construirPanelUsuario());
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(construirBtnLogout());
        sidebar.add(Box.createVerticalStrut(4));

        return sidebar;
    }

    // ── Logo con disco animado ────────────────────────────────────────
    private JPanel construirLogo() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(210, 48));
        p.setBorder(new EmptyBorder(0, 16, 0, 0));

        JPanel disco = new JPanel() {
            private float rot = 0f;
            private final Timer spin = new Timer(40, e -> { rot = (rot + 2f) % 360f; repaint(); });
            { spin.start(); }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                int r  = Math.min(cx, cy) - 2;

                g2.setPaint(new RadialGradientPaint(cx, cy + 2, r + 4,
                    new float[]{0.6f, 1f},
                    new Color[]{new Color(109, 40, 217, 40), new Color(109, 40, 217, 0)}));
                g2.fillOval(cx - r - 4, cy - r - 2, (r + 4) * 2, (r + 4) * 2);

                g2.setColor(new Color(245, 243, 255));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);

                g2.rotate(Math.toRadians(rot), cx, cy);
                g2.setStroke(new BasicStroke(0.8f));
                for (int rr = r - 3; rr > 7; rr -= 4) {
                    int alpha = Math.max(20, 100 - (r - rr) * 8);
                    g2.setColor(new Color(109, 40, 217, alpha));
                    g2.drawOval(cx - rr, cy - rr, rr * 2, rr * 2);
                }
                g2.rotate(-Math.toRadians(rot), cx, cy);

                g2.setPaint(new GradientPaint(cx - 8, cy - 8, SB_ACCENT, cx + 8, cy + 8, SB_ACCENT2));
                g2.fillOval(cx - 8, cy - 8, 16, 16);

                g2.setColor(SB_BG);
                g2.fillOval(cx - 3, cy - 3, 6, 6);

                g2.setColor(new Color(255, 255, 255, 120));
                g2.fillArc(cx - r + 5, cy - r + 5, (r - 5) * 2, (r - 5) * 2, 40, 65);

                g2.setColor(new Color(109, 40, 217, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);

                g2.dispose();
            }
        };
        disco.setOpaque(false);
        disco.setPreferredSize(new Dimension(36, 36));

        JLabel txt = new JLabel("z_one") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(getFont());
                g2.setPaint(new GradientPaint(0, 0, SB_ACCENT, getWidth(), 0, SB_ACCENT2));
                g2.drawString(getText(), 0, getFont().getSize());
                g2.dispose();
            }
        };
        txt.setFont(new Font("Consolas", Font.BOLD, 22));
        txt.setPreferredSize(new Dimension(80, 30));

        p.add(disco);
        p.add(txt);
        return p;
    }

    // ── Separador con degradado ───────────────────────────────────────
    private JPanel construirSeparador() {
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int mid = getWidth() / 2;
                g2.setPaint(new GradientPaint(14, 0, new Color(109, 40, 217, 0),
                                              mid,  0, new Color(109, 40, 217, 60)));
                g2.fillRect(14, 0, mid - 14, 1);
                g2.setPaint(new GradientPaint(mid,  0, new Color(37, 99, 235, 60),
                                              getWidth() - 14f, 0, new Color(37, 99, 235, 0)));
                g2.fillRect(mid, 0, getWidth() / 2 - 14, 1);
                g2.dispose();
            }
        };
        sep.setOpaque(false);
        sep.setMaximumSize(new Dimension(210, 1));
        sep.setPreferredSize(new Dimension(0, 1));
        return sep;
    }

    // ── Panel usuario con avatar pulsante ────────────────────────────
    private JPanel construirPanelUsuario() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(210, 52));
        p.setBorder(new EmptyBorder(0, 14, 0, 14));

        JPanel avatar = new JPanel() {
            private float pulse = 0f;
            private final Timer t = new Timer(60, e -> { pulse = (float)((pulse + 0.08f) % (Math.PI * 2)); repaint(); });
            { t.start(); }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                int r  = Math.min(cx, cy) - 3;

                float glowA = 0.18f + 0.10f * (float) Math.sin(pulse);
                g2.setPaint(new RadialGradientPaint(cx, cy, r + 7,
                    new float[]{0.5f, 1f},
                    new Color[]{new Color(109, 40, 217, (int)(glowA * 255)),
                                new Color(109, 40, 217, 0)}));
                g2.fillOval(cx - r - 7, cy - r - 7, (r + 7) * 2, (r + 7) * 2);

                g2.setStroke(new BasicStroke(2f));
                g2.setPaint(new GradientPaint(cx - r, cy - r, SB_ACCENT, cx + r, cy + r, SB_ACCENT2));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);

                g2.setPaint(new GradientPaint(cx - r, cy - r, SB_ACCENT, cx + r, cy + r, SB_ACCENT2));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
String ini = usuarioActual.getNombreCompleto() != null 
    ? usuarioActual.getNombreCompleto().substring(0, 1).toUpperCase()
    : usuarioActual.getUsername().substring(0, 1).toUpperCase();
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(36, 36));

        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));

        JLabel nombre = new JLabel(usuarioActual.getNombreCompleto());
        nombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nombre.setForeground(SB_TEXT);

        JLabel rol = new JLabel("● " + usuarioActual.getNombreRol());
        rol.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        rol.setForeground(SB_ACCENT);

        texts.add(nombre);
        texts.add(Box.createVerticalStrut(2));
        texts.add(rol);

        p.add(avatar);
        p.add(texts);
        return p;
    }

    // ── Botón cerrar sesión animado ───────────────────────────────────
    private JPanel construirBtnLogout() {
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        wrap.setOpaque(false);
        wrap.setMaximumSize(new Dimension(210, 36));

        JButton btn = new JButton("⮐  Cerrar sesión") {
            private float ha = 0f;
            private boolean ov = false;
            private final Timer t = new Timer(16, e -> {
                float target = ov ? 1f : 0f;
                if (Math.abs(ha - target) > 0.01f) { ha += (target - ha) * 0.15f; repaint(); }
                else ((Timer)e.getSource()).stop();
            });
            {
                setOpaque(false); setContentAreaFilled(false);
                setBorderPainted(false); setFocusPainted(false);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setForeground(SB_RED);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { ov = true;  t.start(); }
                    @Override public void mouseExited(MouseEvent e)  { ov = false; t.start(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                if (ha > 0.01f) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(220, 38, 38, (int)(ha * 20)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(new Color(220, 38, 38, (int)(ha * 80)));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(178, 32));
        btn.addActionListener(ev -> {
            int op = JOptionPane.showConfirmDialog(this,
                "¿Cerrar sesión actual?", "Z-One", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });
        wrap.add(btn);
        return wrap;
    }

    // =================================================================
    //  STATUS BAR
    // =================================================================
    private JPanel construirStatusBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(0xE5E8EE));
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        bar.setOpaque(true);
        bar.setBackground(Color.WHITE);
        bar.setPreferredSize(new Dimension(0, 32));
        bar.setBorder(new EmptyBorder(0, 24, 0, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        left.setOpaque(false);
        ModernUI.StatusDot dot = new ModernUI.StatusDot();
        dot.setColor(new Color(0x10B981));
        JLabel txt = new JLabel("Oracle conectado · Z-One v1.0");
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txt.setForeground(new Color(0x8B92A5));
        left.add(dot);
        left.add(txt);

        JLabel right = new JLabel("Sesión activa · " + usuarioActual.getUsername());
        right.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        right.setForeground(new Color(0x8B92A5));
        right.setBorder(new EmptyBorder(6, 0, 0, 0));

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // =================================================================
    //  STUB PANEL
    // =================================================================
    private JPanel stubPanel(String titulo, String descripcion) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel lbl1 = new JLabel(titulo);
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbl1.setForeground(TEXT_PRIMARY);
        lbl1.setAlignmentX(LEFT_ALIGNMENT);
        ModernUI.CardPanel card = new ModernUI.CardPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(48, 48, 48, 48));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(900, 320));
        ModernUI.RoundedButton btn = new ModernUI.RoundedButton("Probar acción", true);
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.addActionListener(e -> showToast("Módulo " + titulo, ToastType.SUCCESS));
        card.add(btn);
        p.add(lbl1); p.add(Box.createVerticalStrut(24)); p.add(card); p.add(Box.createVerticalGlue());
        return p;
    }

    // =================================================================
    //  TOAST SYSTEM
    // =================================================================
    public static void showToast(String mensaje, ToastType tipo) {
        if (instance == null) return;
        instance.mostrarToastInterno(mensaje, tipo);
    }
    /** Cambia la vista mostrada en el contenido principal. */
public static void navegarA(String vista) {
    if (instance == null) return;
    instance.cambiarVista(vista);
}

private void cambiarVista(String vista) {
    // Cambia al panel
    cardLayout.show(contentPanel, vista);
    
    // Actualiza el ítem activo del sidebar
    Component[] componentes = ((JPanel)getContentPane().getComponent(0)).getComponents();
    for (Component c : componentes) {
        if (c instanceof JPanel) {
            for (Component item : ((JPanel)c).getComponents()) {
                if (item instanceof SidebarItem) {
                    SidebarItem si = (SidebarItem) item;
                    String label = si.getLabelLower();
                    boolean match = label.equals(vista) 
                                  || (vista.equals("configuracion") && label.equals("configuración"));
                    si.setActive(match);
                    if (match) activeItem = si;
                }
            }
        }
    }
}
  private void mostrarToastInterno(String mensaje, ToastType tipo) {
    // Si la ventana no está visible todavía, ignorar el toast
    if (!isShowing()) return;
    
    Color color; String icono;
    switch (tipo) {
        case SUCCESS: color = SUCCESS;     icono = "✓"; break;
        case ERROR:   color = ACCENT_PINK; icono = "✗"; break;
        default:      color = ACCENT_CYAN; icono = "i"; break;
    }
    JWindow toast = new JWindow(this);
    JPanel panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(24, 24, 52, 245));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
            g2.dispose();
        }
    };
    panel.setOpaque(false);
    panel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));
    panel.setBorder(new EmptyBorder(8, 16, 8, 20));
    JLabel ico = new JLabel(icono);
    ico.setFont(new Font("Segoe UI", Font.BOLD, 16));
    ico.setForeground(color);
    JLabel txt = new JLabel(mensaje);
    txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    txt.setForeground(TEXT_PRIMARY);
    panel.add(ico); panel.add(txt);
    toast.setContentPane(panel);
    toast.pack();
    
    try {
        Point loc = getLocationOnScreen();
        toast.setLocation(loc.x + getWidth() - toast.getWidth() - 24,
                          loc.y + getHeight() - toast.getHeight() - 48);
        toast.setBackground(new Color(0, 0, 0, 0));
        toast.setVisible(true);
        Timer t = new Timer(2500, e -> toast.dispose());
        t.setRepeats(false); t.start();
    } catch (IllegalComponentStateException ex) {
        toast.dispose(); // Si falla, simplemente ignorar
    }
}

    // =================================================================
    //  SIDEBAR ITEM
    // =================================================================
    private static class SidebarItem extends JPanel {
        private final String label;
        private final String icono;
        private       boolean active     = false;
        private       float   hoverAlpha = 0f;
        private       float   activeAlpha = 0f;
        private final Timer   anim;

        public SidebarItem(String label, String icono) {
            this.label = label;
            this.icono = icono;
            setOpaque(false);
            setPreferredSize(new Dimension(210, 40));
            setMaximumSize(new Dimension(210, 40));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            anim = new Timer(16, e -> {
                boolean isHover = getMousePosition() != null;
                float dh = isHover ? 0.15f : -0.15f;
                float da = active  ? 0.12f : -0.12f;
                boolean changed = false;
                float nh = Math.max(0f, Math.min(1f, hoverAlpha  + dh));
                float na = Math.max(0f, Math.min(1f, activeAlpha + da));
                if (nh != hoverAlpha)  { hoverAlpha  = nh; changed = true; }
                if (na != activeAlpha) { activeAlpha = na; changed = true; }
                if (changed) repaint();
            });
            anim.start();
        }

        public void setActive(boolean a) { this.active = a; repaint(); }

        public String getLabelLower() {
    // Normaliza el label a la clave de cardLayout
    String l = label.toLowerCase();
    if (l.equals("configuración")) return "configuracion";
    return l;
}
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int rx = 8, ry = 2, rw = getWidth() - 16, rh = getHeight() - 4;

            if (hoverAlpha > 0f && activeAlpha < 0.5f) {
                g2.setColor(new Color(
                    SB_HOVER_BG.getRed(), SB_HOVER_BG.getGreen(), SB_HOVER_BG.getBlue(),
                    (int)(hoverAlpha * 255)));
                g2.fill(new RoundRectangle2D.Float(rx, ry, rw, rh, 10, 10));
            }

            if (activeAlpha > 0f) {
                g2.setColor(new Color(
                    SB_ACTIVE_BG.getRed(), SB_ACTIVE_BG.getGreen(), SB_ACTIVE_BG.getBlue(),
                    (int)(activeAlpha * 255)));
                g2.fill(new RoundRectangle2D.Float(rx, ry, rw, rh, 10, 10));

                g2.setPaint(new GradientPaint(
                    0, ry,      new Color(109, 40, 217, (int)(activeAlpha * 255)),
                    0, ry + rh, new Color(37,  99, 235, (int)(activeAlpha * 200))
                ));
                g2.fill(new RoundRectangle2D.Float(0, ry + 4, 3, rh - 8, 3, 3));
            }

            Color icoColor = activeAlpha > 0.5f ? SB_ACTIVE_FG
                           : blend(SB_MUTED, SB_TEXT, hoverAlpha);
g2.setColor(icoColor);
g2.setFont(new Font("Segoe UI Symbol", Font.BOLD, 16));
g2.drawString(icono, 22, (getHeight() + 10) / 2);

            Color txtColor = activeAlpha > 0.5f ? SB_ACTIVE_FG
                           : blend(SB_MUTED, SB_TEXT, Math.max(hoverAlpha, activeAlpha));
            g2.setColor(txtColor);
            g2.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 12));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, 46, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);

            g2.dispose();
        }

        private Color blend(Color a, Color b, float t) {
            t = Math.max(0f, Math.min(1f, t));
            return new Color(
                (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t),
                (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t)
            );
        }
    }

    // =================================================================
    //  MAIN
    // =================================================================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ex) { ex.printStackTrace(); }

        Usuario test = new Usuario(
            1, "jesus", "hash123", "jesus@zonemusic.com",
            "Jesús Pérez", Usuario.ROL_PRODUCTOR, "PRODUCTOR",
            true, java.time.LocalDate.now(), null
        );
        SwingUtilities.invokeLater(() -> new MainFrame(test).setVisible(true));
    }
}