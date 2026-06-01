package view;

import dao.CancionDao;
import model.Cancion;
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
    private int[] datosSesiones   = new int[7];
    private int[] datosCanciones  = new int[7];
    private int[] datosArtistas   = new int[7];
    private List<String[]> actividad;

    // ── REPRODUCTOR ──
    private List<Cancion> playlist = new ArrayList<>();
    private int indiceActual = 0;
    private Clip clipActual = null;
    private boolean reproduciendo = false;
    private JLabel lblTituloRep, lblArtistaRep, lblTiempo;
    private JButton btnPlayPause;
    private JProgressBar barraProgreso;
    private Timer timerProgreso;

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
            totalSesionesGlobal   = totalSesiones;
            totalCancionesGlobal  = totalCanciones;
            totalArtistasActivos  = stats.artistasActivos();
            Map<String, int[]> sem = stats.actividadSemanal();
            datosSesiones  = sem.get("sesiones");
            datosCanciones = sem.get("canciones");
            datosArtistas  = sem.get("artistas");
            actividad = stats.actividadReciente();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarPlaylist() {
        try {
            playlist = new CancionDao().listarConArchivo();
        } catch (Exception ex) {
            ex.printStackTrace();
            playlist = new ArrayList<>();
        }
    }

    private void construirUI() {
        setBackground(BG_MAIN);
        setOpaque(true);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 28, 8, 28));

        JPanel contenidoCentral = new JPanel(new BorderLayout(0, 0));
        contenidoCentral.setOpaque(false);

        JPanel izq = new JPanel(new BorderLayout(0, 18));
        izq.setOpaque(false);
        izq.add(encabezado(), BorderLayout.NORTH);
        izq.add(cuerpoIzquierdo(), BorderLayout.CENTER);

        JPanel der = new JPanel(new BorderLayout(0, 14));
        der.setOpaque(false);
        der.setBorder(new EmptyBorder(0, 16, 0, 0));
        der.add(panelAcciones(), BorderLayout.NORTH);
        der.add(panelActividad(), BorderLayout.CENTER);
        der.setPreferredSize(new Dimension(290, 0));

        contenidoCentral.add(izq, BorderLayout.CENTER);
        contenidoCentral.add(der, BorderLayout.EAST);

        add(contenidoCentral, BorderLayout.CENTER);
        add(reproductorSpotify(), BorderLayout.SOUTH);   // ⬅ REPRODUCTOR ABAJO
    }

    // ════════════════════════════════════════════════════════════════
    //  🎵 REPRODUCTOR ESTILO SPOTIFY
    // ════════════════════════════════════════════════════════════════
    private JPanel reproductorSpotify() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                // Sombra
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(0, 3, getWidth(), getHeight()-3, 16, 16);
                // Fondo con gradiente sutil violeta→cyan
                GradientPaint gp = new GradientPaint(0, 0,
                    new Color(0xFFFFFF), getWidth(), 0,
                    new Color(0xF5F0FF));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight()-3, 16, 16);
                // Borde
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-4, 16, 16);
                // Línea izquierda violeta
                g2.setColor(PURPLE);
                g2.fillRoundRect(0, 0, 4, getHeight()-3, 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(14, 0));
        card.setBorder(new EmptyBorder(12, 18, 12, 18));
        card.setPreferredSize(new Dimension(0, 76));

        // ─── IZQUIERDA: Cover + Info ───
        JPanel info = new JPanel(new BorderLayout(12, 0));
        info.setOpaque(false);

        JPanel cover = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                GradientPaint gp = new GradientPaint(0, 0, PURPLE, getWidth(), getHeight(), CYAN);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(255, 255, 255, 220));
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
                FontMetrics fm = g2.getFontMetrics();
                String emo = "🎵";
                g2.drawString(emo, (getWidth()-fm.stringWidth(emo))/2,
                            (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        cover.setOpaque(false);
        cover.setPreferredSize(new Dimension(48, 48));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));

        String tituloInit = playlist.isEmpty() ? "Sin canciones disponibles" : playlist.get(0).getTitulo();
        String artistaInit = playlist.isEmpty() ? "Agrega canciones con ruta de audio" : nombreArtista(playlist.get(0));

        lblTituloRep = mk(tituloInit, new Font("Segoe UI", Font.BOLD, 13), TXT_PRI);
        lblArtistaRep = mk(artistaInit, new Font("Segoe UI", Font.PLAIN, 10), TXT_SEC);
        lblTituloRep.setAlignmentX(LEFT_ALIGNMENT);
        lblArtistaRep.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(Box.createVerticalGlue());
        txt.add(lblTituloRep);
        txt.add(Box.createVerticalStrut(2));
        txt.add(lblArtistaRep);
        txt.add(Box.createVerticalGlue());

        info.add(cover, BorderLayout.WEST);
        info.add(txt, BorderLayout.CENTER);
        info.setPreferredSize(new Dimension(220, 0));

        // ─── CENTRO: Controles + Barra ───
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        // Controles play
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        controles.setOpaque(false);

        JButton btnPrev = btnRedondo("⏮", 32, false);
        btnPrev.addActionListener(e -> anterior());

        btnPlayPause = btnRedondo("▶", 40, true);
        btnPlayPause.addActionListener(e -> togglePlay());

        JButton btnNext = btnRedondo("⏭", 32, false);
        btnNext.addActionListener(e -> siguiente());

        controles.add(btnPrev);
        controles.add(btnPlayPause);
        controles.add(btnNext);

        // Barra de progreso
        JPanel barraBox = new JPanel(new BorderLayout(8, 0));
        barraBox.setOpaque(false);
        barraBox.setBorder(new EmptyBorder(2, 0, 0, 0));

        lblTiempo = mk("0:00 / 0:00", new Font("Consolas", Font.PLAIN, 9), TXT_SEC);

        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setValue(0);
        barraProgreso.setOpaque(false);
        barraProgreso.setBorderPainted(false);
        barraProgreso.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = g2d(g);
                int w = barraProgreso.getWidth(), h = 4;
                int y = (barraProgreso.getHeight() - h) / 2;
                // Track
                g2.setColor(BG_SOFT);
                g2.fillRoundRect(0, y, w, h, h, h);
                // Progress
                int progW = (int)(w * (barraProgreso.getValue() / 100.0));
                GradientPaint gp = new GradientPaint(0, y, PURPLE, progW, y, CYAN);
                g2.setPaint(gp);
                g2.fillRoundRect(0, y, progW, h, h, h);
                // Thumb
                if (progW > 0) {
                    g2.setColor(PURPLE);
                    g2.fillOval(progW-5, y-3, 10, 10);
                }
                g2.dispose();
            }
        });
        barraProgreso.setPreferredSize(new Dimension(0, 14));

        barraBox.add(lblTiempo, BorderLayout.EAST);
        barraBox.add(barraProgreso, BorderLayout.CENTER);

        controles.setAlignmentX(CENTER_ALIGNMENT);
        barraBox.setAlignmentX(CENTER_ALIGNMENT);
        center.add(controles);
        center.add(barraBox);

        // ─── DERECHA: Info adicional ───
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JLabel cancionesDisp = mk("🎶 " + playlist.size() + " disponible" + (playlist.size() != 1 ? "s" : ""),
                new Font("Segoe UI", Font.BOLD, 10), PURPLE);
        right.add(cancionesDisp);
        right.setPreferredSize(new Dimension(140, 0));

        card.add(info, BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        // Wrapper con padding
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(8, 0, 16, 0));
        wrap.add(card, BorderLayout.CENTER);
        return wrap;
    }

    private JButton btnRedondo(String text, int size, boolean primary) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                if (primary) {
                    GradientPaint gp = new GradientPaint(0, 0, PURPLE, getWidth(), getHeight(), CYAN);
                    g2.setPaint(gp);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.setColor(new Color(255, 255, 255, 60));
                    g2.fillArc(0, 0, getWidth(), getHeight()/2, 0, 180);
                } else {
                    g2.setColor(getModel().isRollover() ? BG_SOFT : Color.WHITE);
                    g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                    g2.setColor(COL_BRD);
                    g2.drawOval(0, 0, getWidth()-1, getHeight()-1);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, primary ? 14 : 11));
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
    //  🎵 LÓGICA DEL REPRODUCTOR
    // ════════════════════════════════════════════════════════════════
    private void togglePlay() {
        if (playlist.isEmpty()) {
            MainFrame.showToast("No hay canciones con audio disponibles",
                    MainFrame.ToastType.INFO);
            return;
        }
        if (reproduciendo) {
            pausar();
        } else {
            reproducir();
        }
    }

    private void reproducir() {
        Cancion c = playlist.get(indiceActual);
        String ruta = c.getRutaArchivo();
        if (ruta == null || ruta.isBlank()) {
            MainFrame.showToast("Esta canción no tiene archivo",
                    MainFrame.ToastType.ERROR);
            return;
        }

        File archivo = new File(ruta);
        if (!archivo.exists()) {
            MainFrame.showToast("Archivo no encontrado: " + archivo.getName(),
                    MainFrame.ToastType.ERROR);
            return;
        }

        String ext = ruta.toLowerCase();

        // Si es .wav → reproducir DENTRO de la app
        if (ext.endsWith(".wav") || ext.endsWith(".au") || ext.endsWith(".aiff")) {
            reproducirInterno(archivo);
        }
        // Si es otro formato (mp4, mp3, etc) → abrir con Windows Media Player
        else {
            reproducirExterno(archivo);
        }
    }

    private void reproducirInterno(File archivo) {
        try {
            // Si ya hay algo sonando, detenerlo
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

            // Iniciar timer de progreso
            iniciarTimerProgreso();

            // Auto-skip al terminar
            clipActual.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP && reproduciendo) {
                    SwingUtilities.invokeLater(this::siguiente);
                }
            });

            actualizarInfoCancion();
            MainFrame.showToast("▶ " + playlist.get(indiceActual).getTitulo(),
                    MainFrame.ToastType.SUCCESS);
        } catch (Exception ex) {
            ex.printStackTrace();
            MainFrame.showToast("Error al reproducir: " + ex.getMessage(),
                    MainFrame.ToastType.ERROR);
        }
    }

    private void reproducirExterno(File archivo) {
        try {
            Desktop.getDesktop().open(archivo);
            reproduciendo = true;
            btnPlayPause.setText("⏸");
            actualizarInfoCancion();
            MainFrame.showToast("🎬 Reproduciendo en Windows Media Player",
                    MainFrame.ToastType.SUCCESS);
        } catch (Exception ex) {
            MainFrame.showToast("Error al abrir archivo: " + ex.getMessage(),
                    MainFrame.ToastType.ERROR);
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
        if (reproduciendo) reproducir();
    }

    private void actualizarInfoCancion() {
        if (playlist.isEmpty()) return;
        Cancion c = playlist.get(indiceActual);
        lblTituloRep.setText(c.getTitulo());
        lblArtistaRep.setText(nombreArtista(c));
    }

    private String nombreArtista(Cancion c) {
        String prod = c.getNombreProductor() != null ? c.getNombreProductor() : "Sin productor";
        String gen = c.getNombreGenero() != null ? " · " + c.getNombreGenero() : "";
        return prod + gen;
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
                int progreso = (int)((actMicros * 100) / totalMicros);
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
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
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
    //  CUERPO IZQUIERDO + STATS + GRÁFICO (SIN CAMBIOS)
    // ════════════════════════════════════════════════════════════════
    private JPanel cuerpoIzquierdo() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(filaStats());
        p.add(Box.createVerticalStrut(20));
        p.add(panelGrafico());
        return p;
    }

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

    private JPanel panelGrafico() {
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
        card.setLayout(new BorderLayout(0, 0));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JPanel cab = new JPanel(new BorderLayout(8, 0));
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(16, 20, 12, 20));

       JLabel titulo = mk("📈  Actividad semanal", new Font("Segoe UI", Font.BOLD, 14), TXT_PRI);
        JLabel sub    = mk("Sesiones · Canciones · Artistas (últimos 7 días)",
                new Font("Segoe UI", Font.PLAIN, 10), TXT_SEC);

        JPanel cabTxt = new JPanel();
        cabTxt.setOpaque(false);
        cabTxt.setLayout(new BoxLayout(cabTxt, BoxLayout.Y_AXIS));
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        cabTxt.add(titulo);
        cabTxt.add(Box.createVerticalStrut(2));
        cabTxt.add(sub);

        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        leyenda.setOpaque(false);
        for (Object[] it : new Object[][]{
            {"Sesiones", CYAN}, {"Canciones", PINK}, {"Artistas", BLUE}
        }) {
            JPanel dot = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = g2d(g);
                    g2.setColor((Color)it[1]);
                    g2.fillOval(0, 3, 8, 8);
                    g2.dispose();
                }
            };
            dot.setOpaque(false);
            dot.setPreferredSize(new Dimension(8, 14));
            JLabel lbl = mk((String)it[0], new Font("Segoe UI", Font.PLAIN, 10), TXT_SEC);
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            item.setOpaque(false);
            item.add(dot);
            item.add(lbl);
            leyenda.add(item);
        }

        cab.add(cabTxt,  BorderLayout.WEST);
        cab.add(leyenda, BorderLayout.EAST);

        JPanel grafico = new JPanel() {
            final String[] dias = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
            final Color[] colores = {CYAN, PINK, BLUE};

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());

                int n = dias.length;
                int padL = 44, padR = 20, padT = 20, padB = 36;
                int chartW = getWidth()  - padL - padR;
                int chartH = getHeight() - padT - padB;

                int maxVal = 1;
                for (int v : datosSesiones)  if (v > maxVal) maxVal = v;
                for (int v : datosCanciones) if (v > maxVal) maxVal = v;
                for (int v : datosArtistas)  if (v > maxVal) maxVal = v;
                maxVal = Math.max(5, ((maxVal + 4) / 5) * 5);

                g2.setStroke(new BasicStroke(0.6f));
                for (int i = 0; i <= 5; i++) {
                    int y = padT + chartH - (int)(chartH * i / 5.0);
                    g2.setColor(new Color(0, 0, 0, 14));
                    g2.drawLine(padL, y, padL + chartW, y);
                    g2.setFont(new Font("Consolas", Font.PLAIN, 9));
                    g2.setColor(TXT_MUT);
                    g2.drawString(String.valueOf(maxVal * i / 5), padL-26, y+4);
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
                            bx, by + bh, new Color(c.getRed(), c.getGreen(), c.getBlue(), 120));
                        g2.setPaint(gp);
                        g2.fillRoundRect(bx, by, barW, bh, 5, 5);
                        if (bh > 18) {
                            g2.setColor(c.darker());
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

                g2.setColor(new Color(0, 0, 0, 20));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(padL, padT, padL, padT + chartH);

                g2.dispose();
            }
        };
        grafico.setOpaque(false);
        grafico.setPreferredSize(new Dimension(0, 220));

        JPanel footer = new JPanel(new GridLayout(1, 3, 0, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(12, 20, 16, 20));
        Object[][] tot = {
            {String.valueOf(totalSesionesGlobal),  "Total sesiones",   CYAN},
            {String.valueOf(totalCancionesGlobal), "Canciones creadas", PINK},
            {String.valueOf(totalArtistasActivos), "Artistas activos",  BLUE}
        };
        for (Object[] it : tot) {
            JPanel fi = new JPanel();
            fi.setOpaque(false);
            fi.setLayout(new BoxLayout(fi, BoxLayout.Y_AXIS));
            JLabel v = mk((String)it[0], new Font("Segoe UI", Font.BOLD, 20), (Color)it[2]);
            JLabel t = mk((String)it[1], new Font("Segoe UI", Font.PLAIN, 10), TXT_SEC);
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

        card.add(cab,        BorderLayout.NORTH);
        card.add(grafico,    BorderLayout.CENTER);
        card.add(footerFull, BorderLayout.SOUTH);
        return card;
    }

    // ════════════════════════════════════════════════════════════════
    //  PANELES LATERALES (SIN CAMBIOS)
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
            // ⬅ NAVEGAR A LA VISTA CORRESPONDIENTE
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

    private static Graphics2D g2d(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }
}