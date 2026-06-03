package view;

import dao.GrabacionDAO;
import model.Grabacion;
import model.Usuario;
import services.EstadisticasService;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

<<<<<<< HEAD
import static view.formProductor.*;

/**
 * DashboardPanel — Rediseñado con el mismo estilo oscuro de formProductor.
 * Paleta: fondo casi negro, tarjetas oscuras, bordes morados sutiles,
 * acentos cyan/verde/ámbar/rosa.
 */
=======
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
public class DashboardPanel extends JPanel {

    // ════════════════════════════════════════════════════════════════
    //  🎨 PALETA
    // ════════════════════════════════════════════════════════════════
    private static final Color BG_MAIN   = new Color(0xF7F8FA);
    private static final Color BG_CARD   = new Color(0xFFFFFF);
    private static final Color BG_SOFT   = new Color(0xF1F3F7);
    private static final Color TXT_PRI   = new Color(0x1A1D29);
    private static final Color TXT_SEC   = new Color(0x8B92A5);
    private static final Color TXT_MUT   = new Color(0xB4BACA);
    private static final Color COL_BRD   = new Color(0xE5E8EE);
    private static final Color BLUE      = new Color(0x4F7DF7);
    private static final Color CYAN      = new Color(0x06B6D4);
    private static final Color PURPLE    = new Color(0x8B5CF6);
    private static final Color PURPLE_LT = new Color(0xA78BFA);
    private static final Color PINK      = new Color(0xEC4899);
    private static final Color AMBER     = new Color(0xF59E0B);
    private static final Color GREEN     = new Color(0x10B981);

    private static final Font F_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font F_SUB   = new Font("Segoe UI", Font.BOLD, 10);
    private static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN, 12);

    private final Usuario usuario;
    private final EstadisticasService stats = new EstadisticasService();

    // Stats
    private int totalArtistas, totalProductores, totalSesiones, totalCabinas, totalCanciones;
    private int artistasNuevos, cabinasDisp, sesionesMes, productoresActivos, cancionesPubMes;
    private int totalSesionesGlobal, totalCancionesGlobal, totalArtistasActivos;
    private int[] datosSesiones  = new int[7];
    private int[] datosCanciones = new int[7];
    private int[] datosArtistas  = new int[7];
    private List<String[]> actividad;

<<<<<<< HEAD
    // ── Paleta (reutiliza la de formProductor) ────────────────────────
    // BG_DEEP, BG_CARD, BG_FIELD, PURPLE, PURPLE_LT, CYAN, GREEN, AMBER, PINK
    // TXT_PRI, TXT_SEC, COL_BRD, ORO, PLATA, BRONCE — ya definidas en formProductor
=======
    // ── REPRODUCTOR ──
    private List<Grabacion> playlist = new ArrayList<>();
    private int indiceActual = 0;
    private Clip clipActual = null;
    private boolean reproduciendo = false;
    private JLabel lblTituloRep, lblArtistaRep, lblTiempo;
    private JButton btnPlayPause;
    private JProgressBar barraProgreso;
    private Timer timerProgreso;

    // ── ANIMACIÓN DEL REPRODUCTOR ──
    private Timer timerAnim;
    private float fasePulso = 0f;
    private float[] ondas = new float[32];
    private JPanel coverAnimado;
    private JPanel ondasPanel;
    private JPanel listaCancionesBox;   // lista central de grabaciones
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d

    public DashboardPanel(Usuario usuario) {
        this.usuario = usuario;
        cargarDatosReales();
        cargarPlaylist();
        construirUI();
    }

    private void cargarDatosReales() {
        try {
            totalArtistas    = stats.totalArtistas();
            totalProductores = stats.totalProductores();
            totalSesiones    = stats.totalSesiones();
            totalCabinas     = stats.totalCabinas();
            totalCanciones   = stats.totalCanciones();
            artistasNuevos      = stats.artistasNuevosEsteMes();
            productoresActivos  = stats.productoresActivos();
            sesionesMes         = stats.sesionesEsteMes();
            cabinasDisp         = stats.cabinasDisponibles();
            cancionesPubMes     = stats.cancionesPublicadasEsteMes();
<<<<<<< HEAD

            totalSesionesGlobal  = totalSesiones;
            totalCancionesGlobal = totalCanciones;
            totalArtistasActivos = stats.artistasActivos();

=======
            totalSesionesGlobal   = totalSesiones;
            totalCancionesGlobal  = totalCanciones;
            totalArtistasActivos  = stats.artistasActivos();
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
            Map<String, int[]> sem = stats.actividadSemanal();
            datosSesiones  = sem.get("sesiones");
            datosCanciones = sem.get("canciones");
            datosArtistas  = sem.get("artistas");
            actividad = stats.actividadReciente();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

<<<<<<< HEAD
    // ── CONSTRUCCIÓN ─────────────────────────────────────────────────
    private void construirUI() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel izq = new JPanel(new BorderLayout(0, 18));
        izq.setOpaque(false);
        izq.add(encabezado(),      BorderLayout.NORTH);
        izq.add(cuerpoIzquierdo(), BorderLayout.CENTER);

        JPanel der = new JPanel(new BorderLayout(0, 14));
        der.setOpaque(false);
        der.setBorder(new EmptyBorder(0, 14, 0, 0));
        der.add(panelAcciones(),  BorderLayout.NORTH);
        der.add(panelActividad(), BorderLayout.CENTER);
        der.setPreferredSize(new Dimension(280, 0));

        add(izq, BorderLayout.CENTER);
        add(der, BorderLayout.EAST);
=======
    private void cargarPlaylist() {
        try {
            playlist = new GrabacionDAO().listarTodos();
            if (playlist == null) playlist = new ArrayList<>();
        } catch (Exception ex) {
            ex.printStackTrace();
            playlist = new ArrayList<>();
        }
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
    }

    private void construirUI() {
        setBackground(BG_MAIN);
        setOpaque(true);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 28, 8, 28));

        JPanel contenidoCentral = new JPanel(new BorderLayout(0, 0));
        contenidoCentral.setOpaque(false);

        // ── COLUMNA IZQUIERDA (centro) ──
        JPanel izq = new JPanel(new BorderLayout(0, 18));
        izq.setOpaque(false);
        izq.add(encabezado(), BorderLayout.NORTH);
        izq.add(cuerpoIzquierdo(), BorderLayout.CENTER);

        // ── COLUMNA DERECHA ──
        JPanel der = new JPanel(new BorderLayout(0, 14));
        der.setOpaque(false);
        der.setBorder(new EmptyBorder(0, 16, 0, 0));
        der.add(panelAcciones(), BorderLayout.NORTH);
        der.add(panelActividad(), BorderLayout.CENTER);
        der.setPreferredSize(new Dimension(290, 0));

        contenidoCentral.add(izq, BorderLayout.CENTER);
        contenidoCentral.add(der, BorderLayout.EAST);

        add(contenidoCentral, BorderLayout.CENTER);
        // Gráfica semanal COMPACTA abajo, ancho completo
        add(graficaCompactaAbajo(), BorderLayout.SOUTH);
    }

    // ════════════════════════════════════════════════════════════════
    //  CUERPO IZQUIERDO: stats + REPRODUCTOR + LISTA de grabaciones
    // ════════════════════════════════════════════════════════════════
    private JPanel cuerpoIzquierdo() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(filaStats());
        p.add(Box.createVerticalStrut(18));
        JComponent rep = reproductorGrande();
        rep.setAlignmentX(LEFT_ALIGNMENT);
        p.add(rep);
        p.add(Box.createVerticalStrut(16));
        JComponent lista = panelListaGrabaciones();
        lista.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lista);
        return p;
    }

    // ════════════════════════════════════════════════════════════════
    //  🎵 REPRODUCTOR GRANDE Y ANIMADO
    // ════════════════════════════════════════════════════════════════
    private JComponent reproductorGrande() {
        for (int i = 0; i < ondas.length; i++) ondas[i] = 0.2f + (float) Math.random() * 0.3f;

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                // Sombra
                g2.setColor(new Color(139, 92, 246, 22));
                g2.fillRoundRect(0, 6, getWidth(), getHeight() - 6, 22, 22);
                // Fondo gradiente lila → blanco → cyan
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(0xF1EAFF), getWidth(), getHeight(),
                        new Color(0xE7F4FF));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() - 6, 22, 22);
                // Borde
                g2.setColor(new Color(0xDFD3F5));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 7, 22, 22);
                // Barra lateral con glow
                GradientPaint side = new GradientPaint(0, 0, PURPLE, 0, getHeight(), CYAN);
                g2.setPaint(side);
                g2.fillRoundRect(0, 0, 6, getHeight() - 6, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(20, 0));
        card.setBorder(new EmptyBorder(18, 26, 18, 26));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setPreferredSize(new Dimension(0, 130));

        // ─── IZQUIERDA: Cover animado + Info ───
        JPanel info = new JPanel(new BorderLayout(16, 0));
        info.setOpaque(false);

        coverAnimado = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                int pad = reproduciendo ? (int) (4 * Math.abs(Math.sin(fasePulso))) : 0;
                int w = getWidth() - pad * 2, h = getHeight() - pad * 2;
                if (reproduciendo) {
                    g2.setColor(new Color(139, 92, 246, 45));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                }
                GradientPaint gp = new GradientPaint(pad, pad, PURPLE, pad + w, pad + h, CYAN);
                g2.setPaint(gp);
                g2.fillRoundRect(pad, pad, w, h, 16, 16);
                g2.setColor(new Color(255, 255, 255, 55));
                g2.fillRoundRect(pad, pad, w, h / 2, 16, 16);
                g2.setColor(new Color(255, 255, 255, 240));
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
                FontMetrics fm = g2.getFontMetrics();
                String emo = reproduciendo ? "🎶" : "🎵";
                g2.drawString(emo, (getWidth() - fm.stringWidth(emo)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        coverAnimado.setOpaque(false);
        coverAnimado.setPreferredSize(new Dimension(76, 76));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));

        String tituloInit  = playlist.isEmpty() ? "Sin grabaciones disponibles" : playlist.get(0).getNombreArchivo();
        String artistaInit = playlist.isEmpty() ? "Graba audio desde una sesión" : infoGrabacion(playlist.get(0));

        JLabel etiqueta = mk("♫  REPRODUCIENDO AHORA", new Font("Segoe UI", Font.BOLD, 9), PURPLE);
        lblTituloRep  = mk(tituloInit, new Font("Segoe UI", Font.BOLD, 17), TXT_PRI);
        lblArtistaRep = mk(artistaInit, new Font("Segoe UI", Font.PLAIN, 11), TXT_SEC);
        etiqueta.setAlignmentX(LEFT_ALIGNMENT);
        lblTituloRep.setAlignmentX(LEFT_ALIGNMENT);
        lblArtistaRep.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(Box.createVerticalGlue());
        txt.add(etiqueta);
        txt.add(Box.createVerticalStrut(4));
        txt.add(lblTituloRep);
        txt.add(Box.createVerticalStrut(2));
        txt.add(lblArtistaRep);
        txt.add(Box.createVerticalGlue());

        info.add(coverAnimado, BorderLayout.WEST);
        info.add(txt, BorderLayout.CENTER);
        info.setPreferredSize(new Dimension(300, 0));

        // ─── CENTRO: Ondas + Controles + Barra ───
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        ondasPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                int n = ondas.length;
                int bw = 3, gap = 4;
                int totalW = n * (bw + gap);
                int startX = (getWidth() - totalW) / 2;
                int midY = getHeight() / 2;
                for (int i = 0; i < n; i++) {
                    int bh = (int) (ondas[i] * (getHeight() - 4));
                    int x = startX + i * (bw + gap);
                    float t = i / (float) n;
                    Color c = mezclar(PURPLE, CYAN, t);
                    g2.setColor(reproduciendo ? c : new Color(0xD8D0E8));
                    g2.fillRoundRect(x, midY - bh / 2, bw, bh, bw, bw);
                }
                g2.dispose();
            }
        };
        ondasPanel.setOpaque(false);
        ondasPanel.setPreferredSize(new Dimension(0, 30));
        ondasPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        controles.setOpaque(false);
        JButton btnPrev = btnRedondo("⏮", 38, false);
        btnPrev.addActionListener(e -> anterior());
        btnPlayPause = btnRedondo("▶", 52, true);
        btnPlayPause.addActionListener(e -> togglePlay());
        JButton btnNext = btnRedondo("⏭", 38, false);
        btnNext.addActionListener(e -> siguiente());
        controles.add(btnPrev);
        controles.add(btnPlayPause);
        controles.add(btnNext);

        JPanel barraBox = new JPanel(new BorderLayout(10, 0));
        barraBox.setOpaque(false);
        barraBox.setBorder(new EmptyBorder(4, 50, 0, 50));
        lblTiempo = mk("0:00 / 0:00", new Font("Consolas", Font.PLAIN, 10), TXT_SEC);
        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setValue(0);
        barraProgreso.setOpaque(false);
        barraProgreso.setBorderPainted(false);
        barraProgreso.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = g2d(g);
                int w = barraProgreso.getWidth(), h = 5;
                int y = (barraProgreso.getHeight() - h) / 2;
                g2.setColor(new Color(0xE0D8F0));
                g2.fillRoundRect(0, y, w, h, h, h);
                int progW = (int) (w * (barraProgreso.getValue() / 100.0));
                GradientPaint gp = new GradientPaint(0, y, PURPLE, progW, y, CYAN);
                g2.setPaint(gp);
                g2.fillRoundRect(0, y, progW, h, h, h);
                if (progW > 0) {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(progW - 6, y - 3, 12, 12);
                    g2.setColor(PURPLE);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(progW - 6, y - 3, 12, 12);
                }
                g2.dispose();
            }
        });
        barraProgreso.setPreferredSize(new Dimension(0, 16));
        barraBox.add(lblTiempo, BorderLayout.EAST);
        barraBox.add(barraProgreso, BorderLayout.CENTER);

        ondasPanel.setAlignmentX(CENTER_ALIGNMENT);
        controles.setAlignmentX(CENTER_ALIGNMENT);
        barraBox.setAlignmentX(CENTER_ALIGNMENT);
        center.add(ondasPanel);
        center.add(Box.createVerticalStrut(4));
        center.add(controles);
        center.add(barraBox);

        // ─── DERECHA: contador ───
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel disp = mk(String.valueOf(playlist.size()), new Font("Segoe UI", Font.BOLD, 30), PURPLE);
        JLabel dispTxt = mk("audio" + (playlist.size() != 1 ? "s" : ""),
                new Font("Segoe UI", Font.PLAIN, 9), TXT_SEC);
        disp.setAlignmentX(RIGHT_ALIGNMENT);
        dispTxt.setAlignmentX(RIGHT_ALIGNMENT);
        right.add(Box.createVerticalGlue());
        right.add(disp);
        right.add(dispTxt);
        right.add(Box.createVerticalGlue());
        right.setPreferredSize(new Dimension(90, 0));

        card.add(info, BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        // Timer de animación
        timerAnim = new Timer(60, e -> {
            fasePulso += 0.25f;
            if (reproduciendo) {
                for (int i = 0; i < ondas.length; i++) {
                    ondas[i] += (float) (Math.random() - 0.5) * 0.4f;
                    if (ondas[i] < 0.15f) ondas[i] = 0.15f;
                    if (ondas[i] > 1.0f)  ondas[i] = 1.0f;
                }
            }
            if (coverAnimado != null) coverAnimado.repaint();
            if (ondasPanel != null)   ondasPanel.repaint();
        });
        timerAnim.start();

        return card;
    }

    private JButton btnRedondo(String text, int size, boolean primary) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                if (primary) {
                    if (reproduciendo) {
                        g2.setColor(new Color(139, 92, 246, 50));
                        g2.fillOval(-3, -3, getWidth() + 6, getHeight() + 6);
                    }
                    GradientPaint gp = new GradientPaint(0, 0, PURPLE, getWidth(), getHeight(), CYAN);
                    g2.setPaint(gp);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.setColor(new Color(255, 255, 255, 70));
                    g2.fillArc(0, 0, getWidth(), getHeight() / 2, 0, 180);
                } else {
                    g2.setColor(getModel().isRollover() ? BG_SOFT : Color.WHITE);
                    g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                    g2.setColor(COL_BRD);
                    g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, primary ? 16 : 12));
        b.setForeground(primary ? Color.WHITE : TXT_PRI);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(size, size));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ════════════════════════════════════════════════════════════════
    //  📃 LISTA DE GRABACIONES (donde antes estaba la gráfica)
    // ════════════════════════════════════════════════════════════════
    private JComponent panelListaGrabaciones() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(0, 2, getWidth(), getHeight() - 2, 16, 16);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() - 2, 16, 16);
                g2.setColor(COL_BRD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(0, 0));

        JPanel cab = new JPanel(new BorderLayout(8, 0));
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(16, 20, 12, 20));
        JLabel titulo = mk("🎼  Biblioteca de audios", new Font("Segoe UI", Font.BOLD, 14), TXT_PRI);
        JLabel sub    = mk("Toca play para escuchar", new Font("Segoe UI", Font.PLAIN, 10), TXT_SEC);
        cab.add(titulo, BorderLayout.WEST);
        cab.add(sub, BorderLayout.EAST);

        listaCancionesBox = new JPanel();
        listaCancionesBox.setOpaque(false);
        listaCancionesBox.setLayout(new BoxLayout(listaCancionesBox, BoxLayout.Y_AXIS));
        listaCancionesBox.setBorder(new EmptyBorder(6, 14, 14, 14));
        construirListaGrabaciones();

        JScrollPane scroll = new JScrollPane(listaCancionesBox);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));

        card.add(cab, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(0, 230));
        return card;
    }

    private void construirListaGrabaciones() {
        listaCancionesBox.removeAll();
        if (playlist.isEmpty()) {
            JLabel vacio = mk("No hay audios. Graba uno desde una sesión.",
                    new Font("Segoe UI", Font.PLAIN, 11), TXT_SEC);
            vacio.setBorder(new EmptyBorder(24, 10, 24, 10));
            listaCancionesBox.add(vacio);
        } else {
            for (int i = 0; i < playlist.size(); i++) {
                listaCancionesBox.add(filaGrabacion(i));
                listaCancionesBox.add(Box.createVerticalStrut(7));
            }
        }
        listaCancionesBox.revalidate();
        listaCancionesBox.repaint();
    }

    private JComponent filaGrabacion(int idx) {
        Grabacion g = playlist.get(idx);
        boolean activa = (idx == indiceActual);

        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics gr) {
                Graphics2D g2 = g2d(gr);
                if (activa) {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(0xF1EAFF),
                            getWidth(), 0, new Color(0xEAF6FF));
                    g2.setPaint(gp);
                } else {
                    g2.setColor(BG_SOFT);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 11, 11);
                if (activa) {
                    g2.setColor(PURPLE);
                    g2.fillRoundRect(0, 6, 3, getHeight() - 12, 3, 3);
                }
                g2.dispose();
                super.paintComponent(gr);
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(12, 0));
        fila.setBorder(new EmptyBorder(9, 13, 9, 13));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Mini cover
        JPanel mini = new JPanel() {
            @Override protected void paintComponent(Graphics gr) {
                Graphics2D g2 = g2d(gr);
                GradientPaint gp = new GradientPaint(0, 0, PURPLE, getWidth(), getHeight(), CYAN);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
                FontMetrics fm = g2.getFontMetrics();
                String e = (activa && reproduciendo) ? "🎶" : "♪";
                g2.drawString(e, (getWidth() - fm.stringWidth(e)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        mini.setOpaque(false);
        mini.setPreferredSize(new Dimension(34, 34));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel n = mk(g.getNombreArchivo(), new Font("Segoe UI", Font.BOLD, 12), TXT_PRI);
        JLabel d = mk(infoGrabacion(g), new Font("Segoe UI", Font.PLAIN, 10), TXT_SEC);
        n.setAlignmentX(LEFT_ALIGNMENT);
        d.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(n);
        txt.add(d);

        JButton play = btnRedondo(activa && reproduciendo ? "⏸" : "▶", 30, false);
        play.setForeground(PURPLE);
        play.addActionListener(e -> {
            if (idx == indiceActual && reproduciendo) {
                pausar();
            } else {
                indiceActual = idx;
                actualizarInfoCancion();
                if (clipActual != null) { clipActual.stop(); clipActual.close(); clipActual = null; }
                reproducir();
            }
            construirListaGrabaciones();
        });

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        der.setOpaque(false);
        der.add(play);

        fila.add(mini, BorderLayout.WEST);
        fila.add(txt, BorderLayout.CENTER);
        fila.add(der, BorderLayout.EAST);

        fila.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                indiceActual = idx;
                actualizarInfoCancion();
                construirListaGrabaciones();
            }
        });
        return fila;
    }

    // ════════════════════════════════════════════════════════════════
    //  🎵 LÓGICA DEL REPRODUCTOR
    // ════════════════════════════════════════════════════════════════
    private void togglePlay() {
        if (playlist.isEmpty()) {
            MainFrame.showToast("No hay audios disponibles", MainFrame.ToastType.INFO);
            return;
        }
        if (reproduciendo) pausar();
        else reproducir();
        construirListaGrabaciones();
    }

    private void reproducir() {
        Grabacion c = playlist.get(indiceActual);
        String ruta = c.getRutaArchivo();
        if (ruta == null || ruta.isBlank()) {
            MainFrame.showToast("Este audio no tiene archivo", MainFrame.ToastType.ERROR);
            return;
        }
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            MainFrame.showToast("Archivo no encontrado: " + archivo.getName(), MainFrame.ToastType.ERROR);
            return;
        }
        String ext = ruta.toLowerCase();
        if (ext.endsWith(".wav") || ext.endsWith(".au") || ext.endsWith(".aiff")) {
            reproducirInterno(archivo);
        } else {
            reproducirExterno(archivo);
        }
    }

    private void reproducirInterno(File archivo) {
        try {
            if (clipActual != null) {
                clipActual.stop();
                clipActual.close();
            }
            AudioInputStream stream = AudioSystem.getAudioInputStream(archivo);
            clipActual = AudioSystem.getClip();
            clipActual.open(stream);
            clipActual.start();
            reproduciendo = true;
            btnPlayPause.setText("⏸");
            iniciarTimerProgreso();
            clipActual.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP && reproduciendo) {
                    SwingUtilities.invokeLater(this::siguiente);
                }
            });
            actualizarInfoCancion();
            construirListaGrabaciones();
            MainFrame.showToast("▶ " + playlist.get(indiceActual).getNombreArchivo(),
                    MainFrame.ToastType.SUCCESS);
        } catch (Exception ex) {
            ex.printStackTrace();
            MainFrame.showToast("Error al reproducir: " + ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    private void reproducirExterno(File archivo) {
        try {
            Desktop.getDesktop().open(archivo);
            reproduciendo = true;
            btnPlayPause.setText("⏸");
            actualizarInfoCancion();
            MainFrame.showToast("🎬 Reproduciendo externamente", MainFrame.ToastType.SUCCESS);
        } catch (Exception ex) {
            MainFrame.showToast("Error al abrir archivo: " + ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    private void pausar() {
        if (clipActual != null && clipActual.isRunning()) {
            clipActual.stop();
        }
        reproduciendo = false;
        btnPlayPause.setText("▶");
        if (timerProgreso != null) timerProgreso.stop();
    }

    private void siguiente() {
        if (playlist.isEmpty()) return;
        if (clipActual != null) {
            clipActual.stop();
            clipActual.close();
            clipActual = null;
        }
        indiceActual = (indiceActual + 1) % playlist.size();
        actualizarInfoCancion();
        construirListaGrabaciones();
        if (reproduciendo) reproducir();
    }

    private void anterior() {
        if (playlist.isEmpty()) return;
        if (clipActual != null) {
            clipActual.stop();
            clipActual.close();
            clipActual = null;
        }
        indiceActual = (indiceActual - 1 + playlist.size()) % playlist.size();
        actualizarInfoCancion();
        construirListaGrabaciones();
        if (reproduciendo) reproducir();
    }

    private void actualizarInfoCancion() {
        if (playlist.isEmpty()) return;
        Grabacion c = playlist.get(indiceActual);
        lblTituloRep.setText(c.getNombreArchivo());
        lblArtistaRep.setText(infoGrabacion(c));
    }

    private String infoGrabacion(Grabacion g) {
        String sesion = g.getNombreSesion() != null ? g.getNombreSesion() : "Sesión";
        return sesion + "  ·  " + g.getDuracionSegundos() + "s  ·  " + g.getTamanoKb() + " KB";
    }

    private void iniciarTimerProgreso() {
        if (timerProgreso != null) timerProgreso.stop();
        if (clipActual == null) return;
        long totalMicros = clipActual.getMicrosecondLength();
        long totalSegs = totalMicros / 1_000_000;
        timerProgreso = new Timer(500, e -> {
            if (clipActual != null && clipActual.isRunning()) {
                long actMicros = clipActual.getMicrosecondPosition();
                long actSegs = actMicros / 1_000_000;
                int progreso = totalMicros > 0 ? (int) ((actMicros * 100) / totalMicros) : 0;
                barraProgreso.setValue(progreso);
                lblTiempo.setText(formatTiempo(actSegs) + " / " + formatTiempo(totalSegs));
            }
        });
        timerProgreso.start();
    }

    private String formatTiempo(long segs) {
        return String.format("%d:%02d", segs / 60, segs % 60);
    }

    // ════════════════════════════════════════════════════════════════
    //  ENCABEZADO
    // ════════════════════════════════════════════════════════════════
    private JPanel encabezado() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel title = mk("Dashboard", F_TITLE, TXT_PRI);
        JLabel sub   = mk("Resumen general · Actividad · Métricas rápidas", F_BODY, TXT_SEC);
        title.setAlignmentX(LEFT_ALIGNMENT);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        titulos.add(title);
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(sub);

        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COL_BRD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setLayout(new BoxLayout(badge, BoxLayout.Y_AXIS));
        badge.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel bienvenido = mk("Bienvenido de vuelta,", new Font("Segoe UI", Font.PLAIN, 10), TXT_SEC);
        JLabel nombreUser = mk(usuario.getNombreCompleto() + "  ✨",
                new Font("Segoe UI", Font.BOLD, 14), PURPLE);
        JLabel rolUser = mk(usuario.getNombreRol() != null
                ? usuario.getNombreRol().toUpperCase() : "USUARIO",
                new Font("Segoe UI", Font.BOLD, 9), BLUE);
        bienvenido.setAlignmentX(RIGHT_ALIGNMENT);
        nombreUser.setAlignmentX(RIGHT_ALIGNMENT);
        rolUser.setAlignmentX(RIGHT_ALIGNMENT);
        badge.add(bienvenido);
        badge.add(Box.createVerticalStrut(2));
        badge.add(nombreUser);
        badge.add(Box.createVerticalStrut(2));
        badge.add(rolUser);

        p.add(titulos, BorderLayout.WEST);
        p.add(badge,   BorderLayout.EAST);
        return p;
    }

    // ════════════════════════════════════════════════════════════════
    //  STATS
    // ════════════════════════════════════════════════════════════════
    private JPanel filaStats() {
        JPanel p = new JPanel(new GridLayout(1, 5, 14, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        p.setAlignmentX(LEFT_ALIGNMENT);

        p.add(statCard("ARTISTAS", String.valueOf(totalArtistas),
                artistasNuevos > 0 ? "+" + artistasNuevos + " este mes" : "registrados",
                BLUE, "🎤"));
        p.add(statCard("PRODUCTORES", String.valueOf(totalProductores),
                productoresActivos + " activos", PURPLE, "🎚"));
        p.add(statCard("SESIONES", String.valueOf(totalSesiones),
                sesionesMes + " este mes", AMBER, "📅"));
        p.add(statCard("CABINAS", String.valueOf(totalCabinas),
                cabinasDisp + " disponibles", CYAN, "🎙"));
        p.add(statCard("CANCIONES", String.valueOf(totalCanciones),
                cancionesPubMes > 0 ? "+" + cancionesPubMes + " publicadas" : "en catálogo",
                PINK, "🎵"));
        return p;
    }

    private JPanel statCard(String titulo, String valor, String sub, Color acento, String emoji) {
        JPanel card = new JPanel() {
            boolean hover = false;
            {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) {
                        MainFrame.showToast("Ver detalles de " + titulo, MainFrame.ToastType.INFO);
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(0, 0, 0, hover ? 12 : 6));
                g2.fillRoundRect(0, 2, getWidth(), getHeight()-2, 14, 14);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight()-2, 14, 14);
                g2.setColor(hover ? acento : COL_BRD);
                g2.setStroke(new BasicStroke(hover ? 1.5f : 1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-3, 14, 14);
                g2.setColor(acento);
                g2.fillRoundRect(0, 0, getWidth(), 3, 14, 14);
                if (hover) {
                    g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 25));
                    g2.fillOval(getWidth()-52, 8, 40, 40);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(6, 0));
        card.setBorder(new EmptyBorder(16, 18, 14, 18));

        JLabel emo = mk(emoji, new Font("Segoe UI Emoji", Font.PLAIN, 22), TXT_PRI);
        emo.setHorizontalAlignment(SwingConstants.RIGHT);
        emo.setVerticalAlignment(SwingConstants.TOP);

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel lTit = mk(titulo, F_SUB, TXT_SEC);
        JLabel lVal = mk(valor, new Font("Segoe UI", Font.BOLD, 28), acento);
        JLabel lSub = mk(sub,   new Font("Segoe UI", Font.PLAIN, 10), TXT_SEC);
        lTit.setAlignmentX(LEFT_ALIGNMENT);
        lVal.setAlignmentX(LEFT_ALIGNMENT);
        lSub.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(lTit);
        txt.add(Box.createVerticalStrut(4));
        txt.add(lVal);
        txt.add(Box.createVerticalStrut(2));
        txt.add(lSub);

        card.add(txt, BorderLayout.CENTER);
        card.add(emo, BorderLayout.EAST);
        return card;
    }

    // ════════════════════════════════════════════════════════════════
    //  📊 GRÁFICA COMPACTA ABAJO (ancho completo, baja)
    // ════════════════════════════════════════════════════════════════
    private JComponent graficaCompactaAbajo() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(0, 2, getWidth(), getHeight()-2, 14, 14);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight()-2, 14, 14);
                g2.setColor(COL_BRD);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-3, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(12, 22, 12, 22));

        // Izquierda: título + totales
        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        JLabel titulo = mk("📈  Actividad semanal", new Font("Segoe UI", Font.BOLD, 13), TXT_PRI);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        izq.add(titulo);
        izq.add(Box.createVerticalStrut(8));
        Object[][] tot = {
            {String.valueOf(totalSesionesGlobal),  "Sesiones",  CYAN},
            {String.valueOf(totalCancionesGlobal), "Canciones", PINK},
            {String.valueOf(totalArtistasActivos), "Artistas",  BLUE}
        };
        for (Object[] it : tot) {
            JPanel fi = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            fi.setOpaque(false);
            fi.setAlignmentX(LEFT_ALIGNMENT);
            fi.setMaximumSize(new Dimension(160, 24));
            JLabel v = mk((String) it[0], new Font("Segoe UI", Font.BOLD, 15), (Color) it[2]);
            JLabel t = mk((String) it[1], new Font("Segoe UI", Font.PLAIN, 10), TXT_SEC);
            fi.add(v);
            fi.add(t);
            izq.add(fi);
            izq.add(Box.createVerticalStrut(2));
        }
        izq.setPreferredSize(new Dimension(160, 0));

        // Centro: gráfica de barras compacta
        JPanel grafico = new JPanel() {
            final String[] dias = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
            final Color[] colores = {CYAN, PINK, BLUE};
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                int n = dias.length;
                int padL = 30, padR = 14, padT = 10, padB = 22;
                int chartW = getWidth() - padL - padR;
                int chartH = getHeight() - padT - padB;
                int maxVal = 1;
                for (int v : datosSesiones)  if (v > maxVal) maxVal = v;
                for (int v : datosCanciones) if (v > maxVal) maxVal = v;
                for (int v : datosArtistas)  if (v > maxVal) maxVal = v;
                maxVal = Math.max(5, ((maxVal + 4) / 5) * 5);
                g2.setStroke(new BasicStroke(0.6f));
                for (int i = 0; i <= 5; i++) {
                    int y = padT + chartH - (int) (chartH * i / 5.0);
                    g2.setColor(new Color(0, 0, 0, 12));
                    g2.drawLine(padL, y, padL + chartW, y);
                }
                int groupW = chartW / n;
                int barCount = 3;
                int barW = Math.max(4, (groupW - 10) / barCount);
                int gap = 2;
                int[][] datos = {datosSesiones, datosCanciones, datosArtistas};
                for (int d = 0; d < n; d++) {
                    int gx = padL + d * groupW + 5;
                    for (int b = 0; b < barCount; b++) {
                        int bh = (int) (chartH * datos[b][d] / (double) maxVal);
                        int bx = gx + b * (barW + gap);
                        int by = padT + chartH - bh;
                        Color c = colores[b];
                        GradientPaint gp = new GradientPaint(bx, by, c,
                                bx, by + bh, new Color(c.getRed(), c.getGreen(), c.getBlue(), 120));
                        g2.setPaint(gp);
                        g2.fillRoundRect(bx, by, barW, bh, 4, 4);
                    }
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    g2.setColor(TXT_SEC);
                    FontMetrics fm = g2.getFontMetrics();
                    int lx = gx + (groupW - 10) / 2 - fm.stringWidth(dias[d]) / 2;
                    g2.drawString(dias[d], lx, padT + chartH + 14);
                }
                g2.dispose();
            }
        };
        grafico.setOpaque(false);

        // Derecha: leyenda
        JPanel leyenda = new JPanel();
        leyenda.setOpaque(false);
        leyenda.setLayout(new BoxLayout(leyenda, BoxLayout.Y_AXIS));
        for (Object[] it : new Object[][]{{"Sesiones", CYAN}, {"Canciones", PINK}, {"Artistas", BLUE}}) {
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            item.setOpaque(false);
            item.setAlignmentX(LEFT_ALIGNMENT);
            item.setMaximumSize(new Dimension(110, 18));
            JPanel dot = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = g2d(g);
                    g2.setColor((Color) it[1]);
                    g2.fillOval(0, 3, 8, 8);
                    g2.dispose();
                }
            };
            dot.setOpaque(false);
            dot.setPreferredSize(new Dimension(8, 14));
            item.add(dot);
            item.add(mk((String) it[0], new Font("Segoe UI", Font.PLAIN, 10), TXT_SEC));
            leyenda.add(item);
            leyenda.add(Box.createVerticalStrut(4));
        }
        leyenda.setPreferredSize(new Dimension(100, 0));

<<<<<<< HEAD
        cab.add(cabTxt,  BorderLayout.WEST);
        cab.add(leyenda, BorderLayout.EAST);

        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0, PURPLE, getWidth()*0.6f,0, new Color(0,0,0,0)));
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        sep.setOpaque(false);
        sep.setPreferredSize(new Dimension(0, 1));

        JPanel cabFull = new JPanel(new BorderLayout());
        cabFull.setOpaque(false);
        cabFull.add(cab, BorderLayout.CENTER);
        cabFull.add(sep, BorderLayout.SOUTH);

        // Área del gráfico — usa datos reales
        JPanel grafico = new JPanel() {
            final String[] dias   = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
            final Color[] colores = {CYAN, PINK, new Color(59,130,246)};

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());

                int n = dias.length;
                int padL = 40, padR = 20, padT = 20, padB = 36;
                int chartW = getWidth()  - padL - padR;
                int chartH = getHeight() - padT - padB;

                // Calcular maxVal dinámicamente
                int maxVal = 1;
                for (int v : datosSesiones)  if (v > maxVal) maxVal = v;
                for (int v : datosCanciones) if (v > maxVal) maxVal = v;
                for (int v : datosArtistas)  if (v > maxVal) maxVal = v;
                // Redondear arriba al múltiplo de 5 más cercano
                maxVal = Math.max(5, ((maxVal + 4) / 5) * 5);

                // Líneas guía
                g2.setStroke(new BasicStroke(0.6f));
                for (int i = 0; i <= 5; i++) {
                    int y = padT + chartH - (int)(chartH * i / 5.0);
                    g2.setColor(new Color(255,255,255,15));
                    g2.drawLine(padL, y, padL + chartW, y);
                    g2.setFont(new Font("Consolas", Font.PLAIN, 9));
                    g2.setColor(TXT_SEC);
                    g2.drawString(String.valueOf(maxVal * i / 5), padL-22, y+4);
                }

                int groupW   = chartW / n;
                int barCount = 3;
                int barW     = Math.max(6, (groupW - 12) / barCount);
                int gap      = 3;

                int[][] datos = {datosSesiones, datosCanciones, datosArtistas};

                for (int d = 0; d < n; d++) {
                    int gx = padL + d * groupW + 6;
                    for (int b = 0; b < barCount; b++) {
                        int bh = (int)(chartH * datos[b][d] / (double)maxVal);
                        int bx = gx + b * (barW + gap);
                        int by = padT + chartH - bh;
                        Color c = colores[b];
                        GradientPaint gp = new GradientPaint(
                            bx, by, c,
                            bx, by + bh, new Color(c.getRed(), c.getGreen(), c.getBlue(), 80));
                        g2.setPaint(gp);
                        g2.fillRoundRect(bx, by, barW, bh, 4, 4);
                        if (bh > 18) {
                            g2.setColor(Color.WHITE);
                            g2.setFont(new Font("Consolas", Font.BOLD, 8));
                            g2.drawString(String.valueOf(datos[b][d]), bx+2, by-3);
                        }
                    }
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    g2.setColor(TXT_SEC);
                    FontMetrics fm = g2.getFontMetrics();
                    int lx = gx + (groupW - 12)/2 - fm.stringWidth(dias[d])/2;
                    g2.drawString(dias[d], lx, padT + chartH + 18);
                }

                g2.setColor(new Color(255,255,255,20));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(padL, padT, padL, padT + chartH);

                g2.dispose();
            }
        };
        grafico.setOpaque(false);
        grafico.setPreferredSize(new Dimension(0, 200));

        card.add(cabFull, BorderLayout.NORTH);
=======
        card.add(izq, BorderLayout.WEST);
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
        card.add(grafico, BorderLayout.CENTER);
        card.add(leyenda, BorderLayout.EAST);

<<<<<<< HEAD
        // Footer con totales reales
        JPanel footer = new JPanel(new GridLayout(1, 3, 0, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 14, 14, 14));
        Object[][] tot = {
            {String.valueOf(totalSesionesGlobal),  "Total sesiones",    CYAN},
            {String.valueOf(totalCancionesGlobal), "Canciones creadas", PINK},
            {String.valueOf(totalArtistasActivos), "Artistas activos",  new Color(59,130,246)}
        };
        for (Object[] it : tot) {
            JPanel fi = new JPanel();
            fi.setOpaque(false);
            fi.setLayout(new BoxLayout(fi, BoxLayout.Y_AXIS));
            JLabel v = mk((String)it[0], new Font("Segoe UI", Font.BOLD, 18), (Color)it[2]);
            JLabel t = mk((String)it[1], new Font("Segoe UI", Font.PLAIN, 9), TXT_SEC);
            v.setAlignmentX(LEFT_ALIGNMENT);
            t.setAlignmentX(LEFT_ALIGNMENT);
            fi.add(v); fi.add(t);
            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setOpaque(false);
            wrap.add(fi, BorderLayout.CENTER);
            footer.add(wrap);
        }

        JPanel sepFooter = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(COL_BRD);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        sepFooter.setOpaque(false);
        sepFooter.setPreferredSize(new Dimension(0, 1));

        JPanel footerFull = new JPanel(new BorderLayout());
        footerFull.setOpaque(false);
        footerFull.add(sepFooter, BorderLayout.NORTH);
        footerFull.add(footer,    BorderLayout.CENTER);

        card.add(footerFull, BorderLayout.SOUTH);
        return card;
=======
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(10, 0, 12, 0));
        wrap.add(card, BorderLayout.CENTER);
        wrap.setPreferredSize(new Dimension(0, 130));
        return wrap;
>>>>>>> d739312be638ca602b5beb6bdb70b272567f437d
    }

    // ════════════════════════════════════════════════════════════════
    //  PANELES LATERALES
    // ════════════════════════════════════════════════════════════════
    private JPanel panelAcciones() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(0, 0, 0, 6));
                g2.fillRoundRect(0, 2, getWidth(), getHeight()-2, 14, 14);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight()-2, 14, 14);
                g2.setColor(COL_BRD);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-3, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setLayout(new BorderLayout());
        inner.setPreferredSize(new Dimension(280, 260));

        JPanel cab = cabeceraDer("⚡  Acciones rápidas", "un solo clic", AMBER);
        inner.add(cab, BorderLayout.NORTH);

        JPanel acciones = new JPanel();
        acciones.setOpaque(false);
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));
        acciones.setBorder(new EmptyBorder(10, 12, 10, 12));
        Object[][] items = {
            {"🎤", "Nuevo artista",    "Registrar artista",   BLUE,   "artistas"},
            {"🎵", "Nueva canción",    "Agregar al catálogo", PINK,   "canciones"},
            {"📅", "Programar sesión", "Reservar cabina",     AMBER,  "sesiones"},
            {"📊", "Ver estadísticas", "Top canciones",       PURPLE, "canciones"},
        };
        for (Object[] it : items) {
            acciones.add(filaAccion(
                (String)it[0], (String)it[1], (String)it[2],
                (Color)it[3], (String)it[4]));
            acciones.add(Box.createVerticalStrut(6));
        }

        JScrollPane scroll = new JScrollPane(acciones);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        inner.add(scroll, BorderLayout.CENTER);
        return inner;
    }

    private JPanel filaAccion(String emoji, String titulo, String desc, Color acento, String vistaDestino) {
        final boolean[] hover = {false};
        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(hover[0]
                    ? new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 18)
                    : BG_SOFT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (hover[0]) {
                    g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 100));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                }
                g2.setColor(acento);
                g2.fillRoundRect(0, 6, 3, getHeight()-12, 3, 3);
                g2.setColor(hover[0] ? acento : TXT_MUT);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int ax = getWidth()-14, ay = getHeight()/2;
                g2.drawLine(ax, ay-4, ax+5, ay);
                g2.drawLine(ax, ay+4, ax+5, ay);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(10, 0));
        fila.setBorder(new EmptyBorder(10, 14, 10, 24));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        fila.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fila.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover[0]=true;  fila.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hover[0]=false; fila.repaint(); }
            @Override public void mouseClicked(MouseEvent e) {
                MainFrame.navegarA(vistaDestino);
                MainFrame.showToast("→ " + titulo, MainFrame.ToastType.INFO);
            }
        });

        JLabel emo = mk(emoji, new Font("Segoe UI Emoji", Font.PLAIN, 18), TXT_PRI);
        emo.setPreferredSize(new Dimension(28, 0));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel t = mk(titulo, new Font("Segoe UI", Font.BOLD, 12), TXT_PRI);
        JLabel d = mk(desc,   new Font("Segoe UI", Font.PLAIN, 10),  TXT_SEC);
        t.setAlignmentX(LEFT_ALIGNMENT);
        d.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(t); txt.add(d);

        fila.add(emo, BorderLayout.WEST);
        fila.add(txt, BorderLayout.CENTER);
        return fila;
    }

    private JPanel panelActividad() {
        JPanel inner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(0, 0, 0, 6));
                g2.fillRoundRect(0, 2, getWidth(), getHeight()-2, 14, 14);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight()-2, 14, 14);
                g2.setColor(COL_BRD);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-3, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inner.setOpaque(false);
        inner.setLayout(new BorderLayout());

        JPanel cab = cabeceraDer("⬡  Actividad reciente", "últimos eventos", GREEN);
        inner.add(cab, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setOpaque(false);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBorder(new EmptyBorder(10, 12, 10, 12));

        Color[] paleta = {GREEN, CYAN, PURPLE, AMBER, PINK, BLUE};
        if (actividad != null && !actividad.isEmpty()) {
            int i = 0;
            for (String[] ev : actividad) {
                Color c = paleta[i % paleta.length];
                lista.add(filaActividad(ev[0], ev[1], ev[2], c));
                lista.add(Box.createVerticalStrut(6));
                i++;
            }
        } else {
            JLabel vacio = mk("No hay actividad reciente",
                    new Font("Segoe UI", Font.PLAIN, 11), TXT_SEC);
            vacio.setBorder(new EmptyBorder(20, 12, 20, 12));
            lista.add(vacio);
        }

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        inner.add(scroll, BorderLayout.CENTER);
        return inner;
    }

    private JPanel filaActividad(String iniciales, String texto, String tiempo, Color acento) {
        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_SOFT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(acento);
                g2.fillRoundRect(0, 4, 3, getHeight()-8, 3, 3);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(8, 0));
        fila.setBorder(new EmptyBorder(8, 14, 8, 12));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        fila.setAlignmentX(LEFT_ALIGNMENT);

        JLabel avatar = new JLabel(iniciales, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        avatar.setForeground(acento.darker());
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(30, 30));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        String textoCorto = texto.length() > 28 ? texto.substring(0, 27) + "…" : texto;
        JLabel t = mk(textoCorto, new Font("Segoe UI", Font.PLAIN, 11), TXT_PRI);
        JLabel d = mk(tiempo,     new Font("Consolas", Font.PLAIN, 9),  TXT_SEC);
        t.setAlignmentX(LEFT_ALIGNMENT);
        d.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(t); txt.add(d);

        fila.add(avatar, BorderLayout.WEST);
        fila.add(txt,    BorderLayout.CENTER);
        return fila;
    }

    private JPanel cabeceraDer(String titulo, String sub, Color acento) {
        JPanel cab = new JPanel(new BorderLayout(6, 0));
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(14, 16, 12, 16));

        JLabel t = mk(titulo, new Font("Segoe UI", Font.BOLD, 13), TXT_PRI);
        JLabel s = mk(sub, new Font("Segoe UI", Font.PLAIN, 10), TXT_SEC);
        cab.add(t, BorderLayout.WEST);
        cab.add(s, BorderLayout.EAST);

        JPanel sepLine = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0,0, acento,
                        getWidth()*0.6f, 0, new Color(0,0,0,0)));
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        sepLine.setOpaque(false);
        sepLine.setPreferredSize(new Dimension(0, 1));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(cab,     BorderLayout.CENTER);
        wrapper.add(sepLine, BorderLayout.SOUTH);
        return wrapper;
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════
    private static JLabel mk(String txt, Font f, Color c) {
        JLabel l = new JLabel(txt);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    private static Color mezclar(Color a, Color b, float t) {
        int r = (int) (a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        return new Color(r, g, bl);
    }

    private static Graphics2D g2d(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }
}