package view;

import dao.CancionDao;
import model.Cancion;
import services.CancionService;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class formCancion extends JPanel {

    // ══════════════════════════════════════════════════════════════════
    //  PALETA — tema claro
    // ══════════════════════════════════════════════════════════════════
    static final Color BG_CARD   = new Color(255, 255, 255);
    static final Color BG_FIELD  = new Color(240, 242, 248);
    static final Color BG_ROW_A  = new Color(255, 255, 255);
    static final Color BG_ROW_B  = new Color(248, 249, 253);
    static final Color BG_SOFT   = new Color(241, 243, 247);
    static final Color COL_BRD   = new Color(220, 225, 240);
    static final Color PURPLE    = new Color(139,  92, 246);
    static final Color PURPLE_LT = new Color(167, 139, 250);
    static final Color CYAN      = new Color(6,  182, 212);
    static final Color GREEN     = new Color(16, 185, 129);
    static final Color AMBER     = new Color(245, 158,  11);
    static final Color PINK      = new Color(236,  72, 153);
    static final Color TXT_PRI   = new Color( 30,  30,  60);
    static final Color TXT_SEC   = new Color(130, 140, 170);
    static final Color SEL_BG    = new Color( 99,  91, 255,  50);
    static final Color ORO       = new Color(234, 179,   8);
    static final Color PLATA     = new Color(148, 163, 184);
    static final Color BRONCE    = new Color(180, 120,  60);

    static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  26);
    static final Font F_SUB    = new Font("Segoe UI", Font.BOLD,   9);
    static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_BOLD   = new Font("Segoe UI", Font.BOLD,  13);
    static final Font F_MONO   = new Font("Consolas", Font.PLAIN, 11);
    static final Font F_MONO_B = new Font("Consolas", Font.BOLD,  11);

    static final String[] COLS = { "ID", "Título", "Género", "BPM", "Estado", "Fecha" };
    static final int COL_ID=0, COL_TITULO=1, COL_GENERO=2,
                     COL_BPM=3, COL_ESTADO=4, COL_FECHA=5;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ══════════════════════════════════════════════════════════════════
    //  ESTADO
    // ══════════════════════════════════════════════════════════════════
    private final CancionService  svc  = new CancionService();
    DefaultTableModel             modeloTabla;
    JTable                        tabla;
    private JTextField            campoBusqueda;

    private JLabel lblTotal, lblGeneros, lblBpmProm, lblPublicadas;
    private JPanel rankingContainer;
    GraficoGenero graficoGenero;
    private JLabel lblResTotal, lblResGen, lblResTop;

    private float tableAlpha = 0f;
    private javax.swing.Timer fadeTimer;

    // ── REPRODUCTOR ──
    private List<Cancion> playlist = new ArrayList<>();
    private int indiceActual = 0;
    private Clip clipActual = null;
    private boolean reproduciendo = false;
    private JLabel lblTituloRep, lblArtistaRep, lblTiempo;
    private JButton btnPlayPause;
    private JProgressBar barraProgreso;
    private Timer timerProgreso;
    private JPanel coverRep;
    private float coverRot = 0f;
    private Timer coverSpinTimer;

    public formCancion() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(24, 24, 24, 24));
        construirUI();
        cargarCanciones();
    }

    private void construirUI() {
        JPanel izq = new JPanel(new BorderLayout(0, 0));
        izq.setOpaque(false);
        izq.add(encabezado(), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.add(Box.createVerticalStrut(14));
        cuerpo.add(filaStats());
        cuerpo.add(Box.createVerticalStrut(14));
        cuerpo.add(reproductorSpotify());      // ⬅ REPRODUCTOR AQUÍ
        cuerpo.add(Box.createVerticalStrut(14));
        cuerpo.add(panelTabla());
        izq.add(cuerpo, BorderLayout.CENTER);

        JPanel der = new JPanel(new BorderLayout(0, 10));
        der.setOpaque(false);
        der.setBorder(new EmptyBorder(0, 14, 0, 0));
        der.setPreferredSize(new Dimension(275, 0));
        JPanel rank = panelRanking();
        rank.setPreferredSize(new Dimension(275, 280));
        JPanel res = panelResumen();
        res.setPreferredSize(new Dimension(275, 138));
        der.add(rank,           BorderLayout.NORTH);
        der.add(panelGrafico(), BorderLayout.CENTER);
        der.add(res,            BorderLayout.SOUTH);

        add(izq, BorderLayout.CENTER);
        add(der, BorderLayout.EAST);
    }

    // ══════════════════════════════════════════════════════════════════
    //  🎵 MINI REPRODUCTOR ESTILO SPOTIFY
    // ══════════════════════════════════════════════════════════════════
    private JPanel reproductorSpotify() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(0, 3, getWidth(), getHeight()-3, 16, 16);
                GradientPaint gp = new GradientPaint(0, 0,
                    new Color(0xFFFFFF), getWidth(), 0,
                    new Color(0xF5F0FF));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight()-3, 16, 16);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-4, 16, 16);
                g2.setColor(PURPLE);
                g2.fillRoundRect(0, 0, 4, getHeight()-3, 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(14, 0));
        card.setBorder(new EmptyBorder(12, 18, 12, 18));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setPreferredSize(new Dimension(0, 80));

        // ─── IZQUIERDA: Cover giratorio + Info ───
        JPanel info = new JPanel(new BorderLayout(12, 0));
        info.setOpaque(false);

        coverRep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                int cx = getWidth()/2, cy = getHeight()/2;
                int r = Math.min(cx, cy) - 2;
                // Sombra
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillOval(cx-r+1, cy-r+2, r*2, r*2);
                // Disco con gradiente girando
                g2.rotate(Math.toRadians(coverRot), cx, cy);
                GradientPaint gp = new GradientPaint(cx-r, cy-r, PURPLE, cx+r, cy+r, CYAN);
                g2.setPaint(gp);
                g2.fillOval(cx-r, cy-r, r*2, r*2);
                // Surcos
                g2.setStroke(new BasicStroke(0.8f));
                g2.setColor(new Color(255, 255, 255, 40));
                for (int rr = r-3; rr > 6; rr -= 3) {
                    g2.drawOval(cx-rr, cy-rr, rr*2, rr*2);
                }
                g2.rotate(-Math.toRadians(coverRot), cx, cy);
                // Centro
                g2.setColor(Color.WHITE);
                g2.fillOval(cx-7, cy-7, 14, 14);
                g2.setColor(PURPLE);
                g2.fillOval(cx-3, cy-3, 6, 6);
                // Brillo
                g2.setColor(new Color(255, 255, 255, 100));
                g2.fillArc(cx-r+3, cy-r+3, (r-3)*2, (r-3)*2, 40, 60);
                g2.dispose();
            }
        };
        coverRep.setOpaque(false);
        coverRep.setPreferredSize(new Dimension(52, 52));

        // Timer rotación del disco
        coverSpinTimer = new Timer(40, e -> {
            if (reproduciendo) {
                coverRot = (coverRot + 2f) % 360f;
                coverRep.repaint();
            }
        });
        coverSpinTimer.start();

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));

        lblTituloRep = mkLabel("Selecciona una canción", new Font("Segoe UI", Font.BOLD, 14), TXT_PRI);
        lblArtistaRep = mkLabel("Doble click en la tabla para reproducir",
                new Font("Segoe UI", Font.PLAIN, 11), TXT_SEC);
        lblTituloRep.setAlignmentX(LEFT_ALIGNMENT);
        lblArtistaRep.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(Box.createVerticalGlue());
        txt.add(lblTituloRep);
        txt.add(Box.createVerticalStrut(2));
        txt.add(lblArtistaRep);
        txt.add(Box.createVerticalGlue());

        info.add(coverRep, BorderLayout.WEST);
        info.add(txt, BorderLayout.CENTER);
        info.setPreferredSize(new Dimension(260, 0));

        // ─── CENTRO: Controles + Barra ───
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

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

        lblTiempo = mkLabel("0:00 / 0:00", new Font("Consolas", Font.PLAIN, 10), TXT_SEC);

        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setValue(0);
        barraProgreso.setOpaque(false);
        barraProgreso.setBorderPainted(false);
        barraProgreso.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = g2d(g);
                int w = barraProgreso.getWidth(), h = 5;
                int y = (barraProgreso.getHeight() - h) / 2;
                g2.setColor(BG_SOFT);
                g2.fillRoundRect(0, y, w, h, h, h);
                int progW = (int)(w * (barraProgreso.getValue() / 100.0));
                GradientPaint gp = new GradientPaint(0, y, PURPLE, progW, y, CYAN);
                g2.setPaint(gp);
                g2.fillRoundRect(0, y, progW, h, h, h);
                if (progW > 0) {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(progW-6, y-3, 12, 12);
                    g2.setColor(PURPLE);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(progW-6, y-3, 12, 12);
                }
                g2.dispose();
            }
        });
        barraProgreso.setPreferredSize(new Dimension(0, 16));

        // Click en barra para saltar
        barraProgreso.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (clipActual == null) return;
                double ratio = (double) e.getX() / barraProgreso.getWidth();
                long totalMicros = clipActual.getMicrosecondLength();
                long nuevaPos = (long)(totalMicros * ratio);
                clipActual.setMicrosecondPosition(nuevaPos);
            }
        });
        barraProgreso.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        barraBox.add(lblTiempo, BorderLayout.EAST);
        barraBox.add(barraProgreso, BorderLayout.CENTER);

        controles.setAlignmentX(CENTER_ALIGNMENT);
        barraBox.setAlignmentX(CENTER_ALIGNMENT);
        center.add(controles);
        center.add(barraBox);

        // ─── DERECHA: Contador ───
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        JLabel rep = mkLabel("🎵 REPRODUCTOR", new Font("Segoe UI", Font.BOLD, 10), PURPLE);
        right.add(rep);
        right.setPreferredSize(new Dimension(120, 0));

        card.add(info, BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        return card;
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

    // ══════════════════════════════════════════════════════════════════
    //  🎵 LÓGICA DEL REPRODUCTOR
    // ══════════════════════════════════════════════════════════════════
    private void cargarPlaylist() {
        try {
            playlist = new CancionDao().listarConArchivo();
        } catch (Exception ex) {
            playlist = new ArrayList<>();
        }
    }

    private void togglePlay() {
        if (playlist.isEmpty()) cargarPlaylist();
        if (playlist.isEmpty()) {
            MainFrame.showToast("No hay canciones con audio disponibles",
                    MainFrame.ToastType.INFO);
            return;
        }
        if (reproduciendo) {
            pausar();
        } else {
            if (clipActual != null) {
                // Reanudar pausa
                clipActual.start();
                reproduciendo = true;
                btnPlayPause.setText("⏸");
                if (timerProgreso != null) timerProgreso.start();
            } else {
                reproducir();
            }
        }
    }

    private void reproducir() {
        if (playlist.isEmpty()) return;
        Cancion c = playlist.get(indiceActual);
        String ruta = c.getRutaArchivo();
        if (ruta == null || ruta.isBlank()) {
            MainFrame.showToast("Esta canción no tiene archivo de audio",
                    MainFrame.ToastType.ERROR);
            return;
        }

        File archivo = new File(ruta);
        if (!archivo.exists()) {
            MainFrame.showToast("Archivo no encontrado: " + archivo.getName(),
                    MainFrame.ToastType.ERROR);
            return;
        }

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
                if (event.getType() == LineEvent.Type.STOP) {
                    long pos = clipActual.getMicrosecondPosition();
                    long len = clipActual.getMicrosecondLength();
                    if (pos >= len - 100000 && reproduciendo) {
                        SwingUtilities.invokeLater(this::siguiente);
                    }
                }
            });

            actualizarInfoCancion();
        } catch (UnsupportedAudioFileException ex) {
            MainFrame.showToast("Formato no soportado. Usa archivos .wav",
                    MainFrame.ToastType.ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
            MainFrame.showToast("Error al reproducir: " + ex.getMessage(),
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
        reproducir();
    }

    private void anterior() {
        if (playlist.isEmpty()) return;
        if (clipActual != null) {
            clipActual.stop();
            clipActual.close();
            clipActual = null;
        }
        indiceActual = (indiceActual - 1 + playlist.size()) % playlist.size();
        reproducir();
    }

    private void reproducirPorId(int idCancion) {
        if (playlist.isEmpty()) cargarPlaylist();
        for (int i = 0; i < playlist.size(); i++) {
            if (playlist.get(i).getIdCancion() == idCancion) {
                indiceActual = i;
                if (clipActual != null) {
                    clipActual.stop();
                    clipActual.close();
                    clipActual = null;
                }
                reproducir();
                return;
            }
        }
        MainFrame.showToast("Esta canción no tiene archivo de audio",
                MainFrame.ToastType.INFO);
    }

    private void actualizarInfoCancion() {
        if (playlist.isEmpty()) return;
        Cancion c = playlist.get(indiceActual);
        lblTituloRep.setText(recortar(c.getTitulo(), 28));
        String prod = c.getNombreProductor() != null ? c.getNombreProductor() : "Sin productor";
        String gen = c.getNombreGenero() != null ? " · " + c.getNombreGenero() : "";
        lblArtistaRep.setText(prod + gen);
    }

    private void iniciarTimerProgreso() {
        if (timerProgreso != null) timerProgreso.stop();
        if (clipActual == null) return;
        long totalMicros = clipActual.getMicrosecondLength();
        long totalSegs = totalMicros / 1_000_000;
        timerProgreso = new Timer(300, e -> {
            if (clipActual != null && clipActual.isRunning()) {
                long actMicros = clipActual.getMicrosecondPosition();
                long actSegs = actMicros / 1_000_000;
                int progreso = (int)((actMicros * 100) / totalMicros);
                barraProgreso.setValue(Math.min(100, progreso));
                lblTiempo.setText(formatTiempo(actSegs) + " / " + formatTiempo(totalSegs));
            }
        });
        timerProgreso.start();
    }

    private String formatTiempo(long segs) {
        return String.format("%d:%02d", segs / 60, segs % 60);
    }

    // ══════════════════════════════════════════════════════════════════
    //  ENCABEZADO
    // ══════════════════════════════════════════════════════════════════
    private JPanel encabezado() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setColor(CYAN);
                g2.fillRect(0, 12, 4, getHeight()-24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(14, 20, 14, 20));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JPanel icoBox = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(6, 182, 212, 22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icoBox.setOpaque(false);
        icoBox.setPreferredSize(new Dimension(42, 42));
        icoBox.add(mkLabel("🎵", new Font("Segoe UI Emoji", Font.PLAIN, 20), CYAN));

        JPanel txtCol = new JPanel();
        txtCol.setOpaque(false);
        txtCol.setLayout(new BoxLayout(txtCol, BoxLayout.Y_AXIS));

        JLabel title = mkLabel("Canciones", F_TITLE, TXT_PRI);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = mkLabel("GESTIÓN DE CANCIONES · GÉNEROS · ESTADOS DE PRODUCCIÓN", F_SUB, TXT_SEC);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        chips.setOpaque(false);
        chips.setAlignmentX(LEFT_ALIGNMENT);
        chips.add(mkChip("● Conectado",    new Color(16,185,129,25), GREEN));
        chips.add(mkChip("🎵  canciones",  new Color(6,182,212,18),  CYAN));
        chips.add(mkChip("🎸  géneros",    new Color(99,91,255,18),  PURPLE));

        txtCol.add(title);
        txtCol.add(Box.createVerticalStrut(1));
        txtCol.add(sub);
        txtCol.add(Box.createVerticalStrut(4));
        txtCol.add(chips);

        left.add(icoBox);
        left.add(txtCol);

        JPanel acc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acc.setOpaque(false);
        campoBusqueda = mkTextField("🔍  Buscar canción...");
        campoBusqueda.setPreferredSize(new Dimension(210, 36));
        campoBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { buscar(); }
        });
        ZBtn btnNuevo = new ZBtn("＋ Nueva canción", true);
        btnNuevo.setPreferredSize(new Dimension(160, 36));
        btnNuevo.addActionListener(e -> abrirFormulario(null));
        acc.add(campoBusqueda);
        acc.add(btnNuevo);

        card.add(left, BorderLayout.WEST);
        card.add(acc,  BorderLayout.EAST);
        return card;
    }

    private JLabel mkChip(String texto, Color bg, Color fg) {
        JLabel chip = new JLabel(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chip.setForeground(fg);
        chip.setBorder(new EmptyBorder(3, 9, 3, 9));
        chip.setOpaque(false);
        return chip;
    }

    // ══════════════════════════════════════════════════════════════════
    //  STAT CARDS
    // ══════════════════════════════════════════════════════════════════
    private JPanel filaStats() {
        lblTotal      = new JLabel("0");
        lblGeneros    = new JLabel("0");
        lblBpmProm    = new JLabel("0");
        lblPublicadas = new JLabel("0");

        JPanel p = new JPanel(new GridLayout(1, 4, 12, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.add(statCard("TOTAL CANCIONES",  lblTotal,      CYAN,   "🎵"));
        p.add(statCard("GÉNEROS",           lblGeneros,   PURPLE, "🎸"));
        p.add(statCard("BPM PROMEDIO",      lblBpmProm,   GREEN,  "🥁"));
        p.add(statCard("PUBLICADAS",        lblPublicadas, AMBER,  "⭐"));
        return p;
    }

    private JPanel statCard(String titulo, JLabel valor, Color ac, String emoji) {
        final boolean[] hov = {false};
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                if (hov[0]) {
                    g2.setColor(new Color(ac.getRed(),ac.getGreen(),ac.getBlue(),18));
                    g2.fillRoundRect(-3,3,getWidth()+6,getHeight()+2,14,14);
                }
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(hov[0] ? new Color(ac.getRed(),ac.getGreen(),ac.getBlue(),120) : COL_BRD);
                g2.setStroke(new BasicStroke(hov[0]?1.5f:1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.setColor(ac); g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(14,1,getWidth()-14,1);
                g2.dispose(); super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(8,0));
        card.setBorder(new EmptyBorder(12,14,12,14));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter(){
            @Override public void mouseEntered(MouseEvent e){hov[0]=true; card.repaint(); valor.setForeground(ac.brighter());}
            @Override public void mouseExited(MouseEvent e) {hov[0]=false;card.repaint(); valor.setForeground(ac);}
        });
        JLabel emo = mkLabel(emoji, new Font("Segoe UI Emoji",Font.PLAIN,20), ac);
        JPanel txt = new JPanel(); txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt,BoxLayout.Y_AXIS));
        JLabel lTit = mkLabel(titulo, F_SUB, TXT_SEC); lTit.setAlignmentX(LEFT_ALIGNMENT);
        valor.setFont(new Font("Segoe UI",Font.BOLD,26)); valor.setForeground(ac);
        valor.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(lTit); txt.add(valor);
        card.add(emo,BorderLayout.WEST); card.add(txt,BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL TABLA
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelTabla() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(COL_BRD); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.dispose(); super.paintComponent(g);
            }
        };
        card.setOpaque(false); card.setLayout(new BorderLayout());
        card.setAlignmentX(LEFT_ALIGNMENT);

        modeloTabla = new DefaultTableModel(COLS, 0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        tabla = new JTable(modeloTabla);
        estilizarTabla();

        // ⬇ DOBLE CLICK PARA REPRODUCIR
        tabla.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabla.getSelectedRow();
                    if (row < 0) return;
                    int id = (int) modeloTabla.getValueAt(row, COL_ID);
                    reproducirPorId(id);
                }
            }
        });

        JPanel fadeWrap = new JPanel(new BorderLayout()){
            @Override protected void paintChildren(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,tableAlpha));
                super.paintChildren(g2); g2.dispose();
            }
        };
        fadeWrap.setOpaque(false);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(0,0,0,0));
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        fadeWrap.add(scroll);

        JPanel head = new JPanel(new BorderLayout()); head.setOpaque(false);
        head.setBorder(new EmptyBorder(14,18,10,18));
        JPanel hl = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0)); hl.setOpaque(false);
        hl.add(mkLabel("Lista de canciones", F_BOLD, TXT_PRI));
        hl.add(mkLabel("doble click para reproducir 🎵",
            new Font("Segoe UI",Font.PLAIN,10), PURPLE));
        head.add(hl, BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        btnRow.setOpaque(false); btnRow.setBorder(new EmptyBorder(10,14,14,14));
        ZBtn btnReproducir = new ZBtn("▶ Reproducir", true);
        ZBtn btnEditar   = new ZBtn("✏  Editar",   false);
        ZBtn btnEliminar = new ZBtn("🗑  Eliminar", false);
        btnEliminar.setForeground(PINK);
        btnReproducir.addActionListener(e -> accionReproducir());
        btnEditar.addActionListener(  e -> accionEditar());
        btnEliminar.addActionListener(e -> accionEliminar());
        btnRow.add(btnReproducir);
        btnRow.add(btnEditar);
        btnRow.add(btnEliminar);

        card.add(head,    BorderLayout.NORTH);
        card.add(fadeWrap,BorderLayout.CENTER);
        card.add(btnRow,  BorderLayout.SOUTH);
        return card;
    }

    private void accionReproducir() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            MainFrame.showToast("Selecciona una canción primero", MainFrame.ToastType.INFO);
            return;
        }
        int id = (int) modeloTabla.getValueAt(row, COL_ID);
        reproducirPorId(id);
    }

    private void estilizarTabla() {
        tabla.setOpaque(false); tabla.setBackground(BG_CARD);
        tabla.setForeground(TXT_PRI); tabla.setFont(F_BODY);
        tabla.setRowHeight(42); tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0,0));
        tabla.setSelectionBackground(SEL_BG); tabla.setSelectionForeground(TXT_PRI);
        tabla.setFocusable(false); tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader th = tabla.getTableHeader();
        th.setBackground(new Color(248,249,253)); th.setForeground(TXT_SEC);
        th.setFont(new Font("Segoe UI",Font.BOLD,9));
        th.setBorder(BorderFactory.createMatteBorder(0,0,1,0,COL_BRD));
        th.setReorderingAllowed(false); th.setPreferredSize(new Dimension(0,34));
        th.setDefaultRenderer(new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(
                    JTable t,Object v,boolean s,boolean f,int r,int c){
                JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c);
                l.setBackground(new Color(248,249,253)); l.setForeground(TXT_SEC);
                l.setFont(new Font("Segoe UI",Font.BOLD,9));
                l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0,0,1,0,COL_BRD),
                    new EmptyBorder(0,16,0,16)));
                l.setOpaque(true); return l;
            }
        });
        int[] w = {52, 180, 110, 70, 110, 90};
        for (int i=0;i<w.length;i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        tabla.setDefaultRenderer(Object.class, new CeldaRenderer());
    }

    // ══════════════════════════════════════════════════════════════════
    //  RANKING LATERAL (sin cambios)
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelRanking() {
        JPanel inner = new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g); g2.setColor(BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(COL_BRD); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.dispose(); super.paintComponent(g);
            }
        };
        inner.setOpaque(false); inner.setLayout(new BorderLayout());

        JPanel cab = new JPanel(new BorderLayout()){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g); g2.setColor(new Color(255,251,235));
                g2.fillRoundRect(0,0,getWidth(),getHeight()+12,12,12);
                g2.dispose(); super.paintComponent(g);
            }
        };
        cab.setOpaque(false); cab.setBorder(new EmptyBorder(11,14,11,14));
        JLabel t=new JLabel("🏆  TOP BPM");
        t.setFont(new Font("Segoe UI",Font.BOLD,13)); t.setForeground(ORO);
        cab.add(t,BorderLayout.WEST);
        cab.add(mkLabel("canciones más rápidas",new Font("Segoe UI",Font.PLAIN,9),TXT_SEC),BorderLayout.EAST);

        JPanel sep=new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g);
                g2.setPaint(new GradientPaint(0,0,ORO,getWidth()*0.6f,0,new Color(0,0,0,0)));
                g2.fillRect(0,0,getWidth(),1); g2.dispose();
            }
        };
        sep.setOpaque(false); sep.setPreferredSize(new Dimension(0,1));

        JPanel ts=new JPanel(new BorderLayout()); ts.setOpaque(false);
        ts.add(cab,BorderLayout.CENTER); ts.add(sep,BorderLayout.SOUTH);

        rankingContainer=new JPanel(); rankingContainer.setOpaque(false);
        rankingContainer.setLayout(new BoxLayout(rankingContainer,BoxLayout.Y_AXIS));
        rankingContainer.setBorder(new EmptyBorder(10,10,10,10));

        JScrollPane scroll=new JScrollPane(rankingContainer);
        scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(0,0,0,0));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4,0));

        inner.add(ts,    BorderLayout.NORTH);
        inner.add(scroll,BorderLayout.CENTER);
        return inner;
    }

    void actualizarRanking(List<Cancion> lista) {
        rankingContainer.removeAll();
        List<Cancion> orden = new ArrayList<>(lista);
        orden.removeIf(c -> c.getBpm()==null);
        orden.sort(Comparator.comparingInt(Cancion::getBpm).reversed());

        if (orden.isEmpty()) {
            rankingContainer.add(mkLabel("Sin datos de BPM",F_MONO.deriveFont(10f),TXT_SEC));
        } else {
            for (int i=0;i<orden.size();i++){
                boolean podio=i<3;
                JPanel fila=filaRanking(i+1, orden.get(i), podio);
                fila.setVisible(false);
                final JPanel f=fila;
                int delay=i*60;
                new javax.swing.Timer(delay,e->{
                    f.setVisible(true);((javax.swing.Timer)e.getSource()).stop();
                }).start();
                rankingContainer.add(fila);
                rankingContainer.add(Box.createVerticalStrut(podio?6:4));
            }
        }
        rankingContainer.revalidate(); rankingContainer.repaint();
    }

    private JPanel filaRanking(int puesto, Cancion c, boolean podio){
        Color ac=puesto==1?ORO:puesto==2?PLATA:puesto==3?BRONCE:PURPLE_LT;
        String med=puesto==1?"🥇":puesto==2?"🥈":puesto==3?"🥉":"#"+puesto;
        final boolean[] hov={false};

        JPanel fila=new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g);
                Color bg=hov[0]?new Color(ac.getRed(),ac.getGreen(),ac.getBlue(),22)
                        :(podio?new Color(ac.getRed(),ac.getGreen(),ac.getBlue(),10):BG_ROW_B);
                g2.setColor(bg); g2.fillRoundRect(0,0,getWidth(),getHeight(),9,9);
                if(podio||hov[0]){
                    g2.setColor(new Color(ac.getRed(),ac.getGreen(),ac.getBlue(),hov[0]?90:50));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,9,9);
                }
                g2.dispose(); super.paintComponent(g);
            }
        };
        fila.setOpaque(false); fila.setLayout(new BorderLayout(8,0));
        fila.setBorder(new EmptyBorder(podio?8:5,10,podio?8:5,10));
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE,podio?50:38));
        fila.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fila.addMouseListener(new MouseAdapter(){
            @Override public void mouseEntered(MouseEvent e){hov[0]=true; fila.repaint();}
            @Override public void mouseExited(MouseEvent e) {hov[0]=false;fila.repaint();}
        });

        JLabel lblM=new JLabel(med,SwingConstants.CENTER);
        lblM.setFont(podio?new Font("Segoe UI Emoji",Font.PLAIN,19)
                         :new Font("Consolas",Font.BOLD,12));
        lblM.setForeground(ac); lblM.setPreferredSize(new Dimension(28,0));

        JPanel txt=new JPanel(); txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt,BoxLayout.Y_AXIS));
        JLabel nom=mkLabel(recortar(c.getTitulo(),podio?16:18),
            new Font("Segoe UI",Font.BOLD,podio?12:11),TXT_PRI);
        nom.setAlignmentX(LEFT_ALIGNMENT); txt.add(nom);
        if(podio && c.getNombreGenero()!=null){
            JLabel gen=mkLabel(recortar(c.getNombreGenero(),18),F_MONO.deriveFont(8.5f),TXT_SEC);
            gen.setAlignmentX(LEFT_ALIGNMENT);
            txt.add(Box.createVerticalStrut(1)); txt.add(gen);
        }

        String bpmStr=c.getBpm()!=null ? c.getBpm()+" BPM" : "—";
        JLabel monto=mkLabel(bpmStr,new Font("Consolas",Font.BOLD,podio?14:11),ac);

        fila.add(lblM,BorderLayout.WEST);
        fila.add(txt,BorderLayout.CENTER);
        fila.add(monto,BorderLayout.EAST);
        return fila;
    }

    // ══════════════════════════════════════════════════════════════════
    //  GRÁFICO (sin cambios)
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelGrafico() {
        JPanel inner=new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g); g2.setColor(BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(COL_BRD); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.dispose(); super.paintComponent(g);
            }
        };
        inner.setOpaque(false); inner.setLayout(new BorderLayout());

        JPanel cab=new JPanel(new BorderLayout()){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g); g2.setColor(new Color(240,252,255));
                g2.fillRoundRect(0,0,getWidth(),getHeight()+12,12,12);
                g2.dispose(); super.paintComponent(g);
            }
        };
        cab.setOpaque(false); cab.setBorder(new EmptyBorder(11,14,11,14));
        JLabel tit=new JLabel("⊙  CANCIONES POR GÉNERO");
        tit.setFont(new Font("Segoe UI",Font.BOLD,12)); tit.setForeground(CYAN);
        JPanel tp=new JPanel(); tp.setOpaque(false);
        tp.setLayout(new BoxLayout(tp,BoxLayout.Y_AXIS));
        tp.add(tit); tp.add(Box.createVerticalStrut(2));
        tp.add(mkLabel("CANTIDAD POR GÉNERO MUSICAL",F_SUB,TXT_SEC));
        cab.add(tp,BorderLayout.WEST);

        JPanel sepC=new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g);
                g2.setPaint(new GradientPaint(0,0,CYAN,getWidth()*0.6f,0,new Color(0,0,0,0)));
                g2.fillRect(0,0,getWidth(),1); g2.dispose();
            }
        };
        sepC.setOpaque(false); sepC.setPreferredSize(new Dimension(0,1));

        JPanel ts=new JPanel(new BorderLayout()); ts.setOpaque(false);
        ts.add(cab,BorderLayout.CENTER); ts.add(sepC,BorderLayout.SOUTH);

        graficoGenero=new GraficoGenero();
        graficoGenero.setOpaque(false);
        graficoGenero.setBorder(new EmptyBorder(10,14,14,14));

        inner.add(ts,          BorderLayout.NORTH);
        inner.add(graficoGenero,BorderLayout.CENTER);
        return inner;
    }

    class GraficoGenero extends JPanel {
        private String[] labels = {};
        private int[]    valores= {};
        private float    animPct= 0f;
        private javax.swing.Timer growTimer;
        private final Color[] BARES={CYAN,PURPLE,GREEN,AMBER,PINK,PURPLE_LT,
            new Color(99,91,255),new Color(6,182,212)};

        void setDatos(List<Cancion> lista){
            java.util.Map<String,Integer> map=new java.util.LinkedHashMap<>();
            for(Cancion c:lista){
                String g=c.getNombreGenero()!=null?c.getNombreGenero():"Sin género";
                map.merge(g,1,Integer::sum);
            }
            List<java.util.Map.Entry<String,Integer>> entries=
                new ArrayList<>(map.entrySet());
            entries.sort((a,b)->b.getValue()-a.getValue());
            int n=Math.min(entries.size(),8);
            labels=new String[n]; valores=new int[n];
            for(int i=0;i<n;i++){
                labels[i]=entries.get(i).getKey();
                valores[i]=entries.get(i).getValue();
            }
            animPct=0f;
            if(growTimer!=null) growTimer.stop();
            growTimer=new javax.swing.Timer(12,e->{
                animPct+=0.07f;
                if(animPct>=1f){animPct=1f;((javax.swing.Timer)e.getSource()).stop();}
                repaint();
            });
            growTimer.start();
        }

        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if(labels.length==0) return;
            Graphics2D g2=g2d(g);
            int W=getWidth(),H=getHeight(),n=labels.length;
            int maxV=0; for(int v:valores) if(v>maxV) maxV=v;
            if(maxV==0){g2.dispose();return;}
            int mt=28,mb=42,aH=H-mt-mb;
            if(aH<10){g2.dispose();return;}
            int bW=Math.min(32,(W-20)/n-8);
            int totW=n*(bW+8)-8,sX=(W-totW)/2;

            g2.setColor(COL_BRD);
            g2.setStroke(new BasicStroke(0.7f,BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,1,new float[]{4,4},0));
            for(int r=1;r<=3;r++){int y=mt+(aH*r/4);g2.drawLine(10,y,W-10,y);}

            for(int i=0;i<n;i++){
                double ratio=(double)valores[i]/maxV;
                int bHfull=(int)(aH*ratio);
                int bH=(int)(bHfull*animPct);
                int bX=sX+i*(bW+8),bY=mt+aH-bH;
                Color c=BARES[i%BARES.length];
                g2.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),20));
                g2.fillRoundRect(bX-2,bY-2,bW+4,bH+4,8,8);
                g2.setPaint(new GradientPaint(bX,bY,c,bX,bY+bH,
                    new Color(c.getRed(),c.getGreen(),c.getBlue(),160)));
                g2.setStroke(new BasicStroke(1f));
                g2.fillRoundRect(bX,bY,bW,bH,6,6);
                if(animPct>=0.85f){
                    String val=String.valueOf(valores[i]);
                    g2.setFont(new Font("Consolas",Font.BOLD,9));
                    g2.setColor(c); FontMetrics fm=g2.getFontMetrics();
                    g2.drawString(val,bX+(bW-fm.stringWidth(val))/2,bY-5);
                }
                String nom=abrev(labels[i],7);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,9));
                g2.setColor(TXT_SEC); FontMetrics fm2=g2.getFontMetrics();
                g2.drawString(nom,bX+(bW-fm2.stringWidth(nom))/2,mt+aH+14);
                g2.setColor(c); g2.fillOval(bX+bW/2-3,mt+aH+22,6,6);
            }
            g2.dispose();
        }
        private String abrev(String s,int max){
            if(s==null||s.isEmpty())return "";
            String f=s.trim().split("\\s+")[0];
            return f.length()>max?f.substring(0,max):f;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL RESUMEN (sin cambios)
    // ══════════════════════════════════════════════════════════════════
    private JPanel panelResumen(){
        JPanel inner=new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g); g2.setColor(BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(COL_BRD); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.dispose(); super.paintComponent(g);
            }
        };
        inner.setOpaque(false); inner.setLayout(new BorderLayout());

        JPanel cab=new JPanel(new BorderLayout()){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g); g2.setColor(new Color(240,252,255));
                g2.fillRoundRect(0,0,getWidth(),getHeight()+12,12,12);
                g2.dispose(); super.paintComponent(g);
            }
        };
        cab.setOpaque(false); cab.setBorder(new EmptyBorder(11,14,11,14));
        JLabel tl=new JLabel("⊙  RESUMEN");
        tl.setFont(new Font("Segoe UI",Font.BOLD,12)); tl.setForeground(CYAN);
        cab.add(tl,BorderLayout.WEST);

        JPanel sepC=new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g);
                g2.setPaint(new GradientPaint(0,0,CYAN,getWidth()*0.6f,0,new Color(0,0,0,0)));
                g2.fillRect(0,0,getWidth(),1); g2.dispose();
            }
        };
        sepC.setOpaque(false); sepC.setPreferredSize(new Dimension(0,1));

        JPanel top=new JPanel(new BorderLayout()); top.setOpaque(false);
        top.add(cab,BorderLayout.CENTER); top.add(sepC,BorderLayout.SOUTH);

        lblResTotal=new JLabel("0"); lblResGen=new JLabel("0"); lblResTop=new JLabel("—");

        JPanel filas=new JPanel(); filas.setOpaque(false);
        filas.setLayout(new BoxLayout(filas,BoxLayout.Y_AXIS));
        filas.setBorder(new EmptyBorder(8,14,8,14));
        filas.add(filaRes("Total canciones",  lblResTotal, CYAN));
        filas.add(Box.createVerticalStrut(2)); sepH(filas); filas.add(Box.createVerticalStrut(2));
        filas.add(filaRes("Géneros únicos",   lblResGen,   PURPLE));
        filas.add(Box.createVerticalStrut(2)); sepH(filas); filas.add(Box.createVerticalStrut(2));
        filas.add(filaRes("Mayor BPM",        lblResTop,   ORO));

        inner.add(top,  BorderLayout.NORTH);
        inner.add(filas,BorderLayout.CENTER);
        return inner;
    }

    private JPanel filaRes(String lbl,JLabel val,Color ac){
        JPanel row=new JPanel(new BorderLayout()); row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE,26));
        row.add(mkLabel(lbl,new Font("Segoe UI",Font.PLAIN,11),TXT_SEC),BorderLayout.WEST);
        val.setFont(new Font("Segoe UI",Font.BOLD,12)); val.setForeground(ac);
        row.add(val,BorderLayout.EAST); return row;
    }

    private void sepH(JPanel p){
        JPanel s=new JPanel(){@Override protected void paintComponent(Graphics g){
            g.setColor(COL_BRD);g.fillRect(0,0,getWidth(),1);}};
        s.setOpaque(false); s.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        s.setAlignmentX(LEFT_ALIGNMENT); p.add(s);
    }

    // ══════════════════════════════════════════════════════════════════
    //  RENDERER CELDAS
    // ══════════════════════════════════════════════════════════════════
    private class CeldaRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(
                JTable t,Object val,boolean sel,boolean foc,int row,int col){
            JLabel c=(JLabel)super.getTableCellRendererComponent(t,val,sel,foc,row,col);
            c.setBorder(new EmptyBorder(0,16,0,16));
            c.setOpaque(true); c.setIcon(null);
            c.setBackground(sel?SEL_BG:(row%2==0?BG_ROW_A:BG_ROW_B));
            c.setForeground(TXT_PRI); c.setFont(F_BODY);
            if(col==COL_ID)    {c.setForeground(PURPLE);c.setFont(F_MONO_B);}
            if(col==COL_TITULO){c.setFont(F_BOLD);}
            if(col==COL_GENERO&&val!=null){c.setForeground(CYAN);c.setFont(F_BOLD);c.setText("● "+val);}
            if(col==COL_BPM   &&val!=null){c.setForeground(PURPLE_LT);c.setFont(F_MONO_B);}
            if(col==COL_ESTADO&&val!=null){
                String est=val.toString();
                Color ce="Publicada".equals(est)?GREEN
                        :"Grabando".equals(est)||"Mezcla".equals(est)?CYAN
                        :"Master".equals(est)?AMBER
                        :"Archivada".equals(est)?PINK:TXT_SEC;
                c.setForeground(ce);c.setFont(F_BOLD);c.setText("● "+est);
            }
            return c;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  CARGA / BÚSQUEDA / ACCIONES
    // ══════════════════════════════════════════════════════════════════
    private void cargarCanciones(){
        worker(()->svc.listar(), lista -> {
            poblar(lista);
            cargarPlaylist();   // ⬅ Recargar playlist también
        }, "Error al cargar");
    }

    private void buscar(){
        String q=campoBusqueda.getText().trim().toLowerCase();
        worker(()->{
            List<Cancion> todas=svc.listar();
            if(q.isEmpty()) return todas;
            List<Cancion> fil=new ArrayList<>();
            for(Cancion c:todas)
                if(c.getTitulo().toLowerCase().contains(q) ||
                   (c.getNombreGenero()!=null&&c.getNombreGenero().toLowerCase().contains(q)))
                    fil.add(c);
            return fil;
        }, this::poblar, "Error al buscar");
    }

    void poblar(List<Cancion> lista){
        modeloTabla.setRowCount(0);
        for(Cancion c:lista){
            modeloTabla.addRow(new Object[]{
                c.getIdCancion(),
                c.getTitulo(),
                c.getNombreGenero()!=null?c.getNombreGenero():"",
                c.getBpm()!=null?c.getBpm()+"":"-",
                c.getNombreEstado()!=null?c.getNombreEstado():"En composicion",
                c.getFechaCompilacion()!=null?c.getFechaCompilacion().format(FMT):""
            });
        }
        tableAlpha=0f;
        if(fadeTimer!=null) fadeTimer.stop();
        fadeTimer=new javax.swing.Timer(16,e->{
            tableAlpha+=0.08f;
            if(tableAlpha>=1f){tableAlpha=1f;((javax.swing.Timer)e.getSource()).stop();}
            repaint();
        });
        fadeTimer.start();

        long generos=lista.stream().map(Cancion::getNombreGenero).distinct().count();
        double bpmProm=lista.stream().filter(c->c.getBpm()!=null)
            .mapToInt(Cancion::getBpm).average().orElse(0);
        long publicadas=lista.stream()
            .filter(c->"Publicada".equals(c.getNombreEstado())).count();

        animarContador(lblTotal,    lista.size());
        animarContador(lblGeneros,  (int)generos);
        lblBpmProm.setText(bpmProm>0?String.format("%.0f",bpmProm):"—");
        animarContador(lblPublicadas,(int)publicadas);

        actualizarRanking(lista);
        if(graficoGenero!=null) graficoGenero.setDatos(lista);

        lblResTotal.setText(String.valueOf(lista.size()));
        lblResGen.setText(String.valueOf(generos));
        if(!lista.isEmpty()){
            Cancion top=lista.stream().filter(c->c.getBpm()!=null)
                .max(Comparator.comparingInt(Cancion::getBpm)).orElse(null);
            lblResTop.setText(top!=null?recortar(top.getTitulo(),15)+" ("+top.getBpm()+")"  :"—");
        } else lblResTop.setText("—");
    }

    private void animarContador(JLabel lbl,int target){
        final int[] cur={0};
        new javax.swing.Timer(20,e->{
            cur[0]+=Math.max(1,(target-cur[0])/4);
            if(cur[0]>=target){cur[0]=target;((javax.swing.Timer)e.getSource()).stop();}
            lbl.setText(String.valueOf(cur[0]));
        }).start();
    }

    private void accionEditar(){
        int row=tabla.getSelectedRow();
        if(row<0){MainFrame.showToast("Selecciona una canción primero",MainFrame.ToastType.INFO);return;}
        int id=(int)modeloTabla.getValueAt(row,COL_ID);
        try{
            Cancion c=svc.listar().stream().filter(x->x.getIdCancion()==id).findFirst().orElse(null);
            if(c!=null) abrirFormulario(c);
        }catch(Exception ex){MainFrame.showToast("Error: "+ex.getMessage(),MainFrame.ToastType.ERROR);}
    }

    private void accionEliminar(){
        int row=tabla.getSelectedRow();
        if(row<0){MainFrame.showToast("Selecciona una canción primero",MainFrame.ToastType.INFO);return;}
        String tit=modeloTabla.getValueAt(row,COL_TITULO).toString();
        int id=(int)modeloTabla.getValueAt(row,COL_ID);
        if(JOptionPane.showConfirmDialog(this,"¿Eliminar \""+tit+"\"?",
                "Z-One — Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            worker(()->{svc.eliminar(id);return svc.listar();},lista->{
                poblar(lista);MainFrame.showToast("Canción eliminada",MainFrame.ToastType.SUCCESS);
            },"Error al eliminar");
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  FORMULARIO ALTA / EDICIÓN (sin cambios)
    // ══════════════════════════════════════════════════════════════════
    private void abrirFormulario(Cancion orig){
        boolean esEd=orig!=null;
        JDialog dlg=new JDialog(
            (Frame)SwingUtilities.getWindowAncestor(this),
            esEd?"Editar canción":"Nueva canción", true);

        JPanel root=new JPanel(new BorderLayout());
        root.setBackground(BG_CARD);

        JPanel dHead=new JPanel(new BorderLayout()){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g); g2.setColor(new Color(240,252,255));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(COL_BRD); g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1);
                g2.setColor(CYAN); g2.setStroke(new BasicStroke(3f));
                g2.drawLine(0,0,getWidth(),0);
                g2.dispose(); super.paintComponent(g);
            }
        };
        dHead.setOpaque(false); dHead.setBorder(new EmptyBorder(16,20,16,20));
        JLabel dTit=new JLabel((esEd?"✎  Editar":"＋  Nueva")+" canción");
        dTit.setFont(new Font("Segoe UI",Font.BOLD,16)); dTit.setForeground(TXT_PRI);
        dHead.add(dTit,BorderLayout.WEST);

        JTextField fTitulo=dlgField(esEd?orig.getTitulo():"");
        JTextField fBpm   =dlgField(esEd&&orig.getBpm()!=null?String.valueOf(orig.getBpm()):"");
        JTextField fProd  =dlgField(esEd&&orig.getIdProductor()!=null?String.valueOf(orig.getIdProductor()):"");
        JTextField fFecha =dlgField(esEd&&orig.getFechaCompilacion()!=null
            ?orig.getFechaCompilacion().format(FMT):LocalDate.now().format(FMT));

        String[] opGenero={"Reggaeton","Pop","Rock","Vallenato","Salsa","Bachata","Trap","Hip-hop","Electronica","Jazz"};
        String[] opIdioma={"Espanol","Ingles","Portugues","Frances","Italiano"};
        String[] opEstado={"En composicion","Grabando","Mezcla","Master","Publicada","Archivada"};
        JComboBox<String> cbGen=dlgCombo(opGenero);
        JComboBox<String> cbIdi=dlgCombo(opIdioma);
        JComboBox<String> cbEst=dlgCombo(opEstado);
        if(esEd){
            if(orig.getNombreGenero()!=null) cbGen.setSelectedItem(orig.getNombreGenero());
            if(orig.getNombreIdioma()!=null) cbIdi.setSelectedItem(orig.getNombreIdioma());
            if(orig.getNombreEstado()!=null) cbEst.setSelectedItem(orig.getNombreEstado());
        }

        JPanel form=new JPanel(new GridLayout(0,2,10,10));
        form.setBackground(BG_CARD); form.setBorder(new EmptyBorder(20,20,10,20));
        form.add(dlgLabel("Título *"));          form.add(fTitulo);
        form.add(dlgLabel("BPM"));               form.add(fBpm);
        form.add(dlgLabel("ID Productor *"));    form.add(fProd);
        form.add(dlgLabel("Fecha compilación")); form.add(fFecha);
        form.add(dlgLabel("Género musical"));    form.add(cbGen);
        form.add(dlgLabel("Idioma"));            form.add(cbIdi);
        form.add(dlgLabel("Estado"));            form.add(cbEst);

        JPanel bRow=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        bRow.setBackground(BG_CARD); bRow.setBorder(new EmptyBorder(0,20,18,20));
        ZBtn bCan=new ZBtn("Cancelar",false); bCan.addActionListener(e->dlg.dispose());
        ZBtn bGua=new ZBtn(esEd?"💾 Guardar":"✦ Crear",true);
        bGua.addActionListener(e->guardar(dlg,orig,esEd,fTitulo,fBpm,fProd,fFecha,cbGen,cbIdi,cbEst));
        bRow.add(bCan); bRow.add(bGua);

        root.add(dHead,BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(bRow, BorderLayout.SOUTH);
        dlg.setContentPane(root);
        dlg.setSize(520,430);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private JTextField dlgField(String val){
        JTextField f=new JTextField(val){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g);
                g2.setColor(hasFocus()?new Color(240,252,255):BG_FIELD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(hasFocus()?CYAN:COL_BRD);
                g2.setStroke(new BasicStroke(hasFocus()?1.5f:1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.dispose(); super.paintComponent(g);
            }
        };
        f.setFont(F_BODY); f.setForeground(TXT_PRI); f.setOpaque(false);
        f.setCaretColor(CYAN); f.setBorder(new EmptyBorder(6,10,6,10));
        f.addFocusListener(new FocusAdapter(){
            @Override public void focusGained(FocusEvent e){f.repaint();}
            @Override public void focusLost(FocusEvent e)  {f.repaint();}
        });
        return f;
    }

    private JComboBox<String> dlgCombo(String[] items){
        JComboBox<String> c=new JComboBox<>(items);
        c.setFont(F_BODY); c.setBackground(BG_FIELD); c.setForeground(TXT_PRI);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COL_BRD),
            new EmptyBorder(4,8,4,8)));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return c;
    }

    private JLabel dlgLabel(String txt){
        JLabel l=mkLabel(txt,new Font("Segoe UI",Font.BOLD,11),TXT_SEC);
        l.setBorder(new EmptyBorder(0,0,0,8)); return l;
    }

    private void guardar(JDialog dlg,Cancion orig,boolean esEd,
            JTextField fT,JTextField fB,JTextField fP,JTextField fF,
            JComboBox<String> cbG,JComboBox<String> cbI,JComboBox<String> cbE){
        try{
            if(fT.getText().isBlank()) throw new IllegalArgumentException("El título es obligatorio");
            if(fP.getText().isBlank()) throw new IllegalArgumentException("El ID de productor es obligatorio");
            Cancion c=esEd?orig:new Cancion();
            c.setTitulo(fT.getText().trim());
            c.setBpm(fB.getText().isBlank()?null:Integer.parseInt(fB.getText().trim()));
            c.setIdProductor(Integer.parseInt(fP.getText().trim()));
            c.setFechaCompilacion(fF.getText().isBlank()?null:LocalDate.parse(fF.getText().trim(),FMT));
            c.setFechaComposicion(LocalDateTime.now());
            c.setNombreGenero((String)cbG.getSelectedItem());
            c.setNombreIdioma((String)cbI.getSelectedItem());
            c.setNombreEstado((String)cbE.getSelectedItem());
            if(esEd) svc.actualizar(c); else svc.crear(c);
            MainFrame.showToast(esEd?"Canción actualizada ✓":"Canción creada ✓",MainFrame.ToastType.SUCCESS);
            cargarCanciones(); dlg.dispose();
        }catch(NumberFormatException ex){
            MainFrame.showToast("BPM e ID Productor deben ser números",MainFrame.ToastType.ERROR);
        }catch(Exception ex){
            MainFrame.showToast(ex.getMessage(),MainFrame.ToastType.ERROR);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  UTILIDADES
    // ══════════════════════════════════════════════════════════════════
    static JLabel mkLabel(String txt,Font f,Color c){
        JLabel l=new JLabel(txt);l.setFont(f);l.setForeground(c);return l;}

    JTextField mkTextField(String ph){
        JTextField f=new JTextField(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=g2d(g);
                g2.setColor(BG_FIELD);g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(COL_BRD);g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose();super.paintComponent(g);
            }
        };
        f.putClientProperty("JTextField.placeholderText",ph);
        f.setFont(F_BODY);f.setForeground(TXT_PRI);f.setOpaque(false);
        f.setCaretColor(CYAN);f.setBorder(new EmptyBorder(0,14,0,14));
        return f;
    }

    static Graphics2D g2d(Graphics g){
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        return g2;
    }

    String recortar(String s,int max){
        if(s==null)return "";
        return s.length()>max?s.substring(0,max-1)+"…":s;
    }

    void worker(java.util.concurrent.Callable<List<Cancion>> tarea,
                java.util.function.Consumer<List<Cancion>> fin,String err){
        new SwingWorker<List<Cancion>,Void>(){
            @Override protected List<Cancion> doInBackground() throws Exception{return tarea.call();}
            @Override protected void done(){
                try{fin.accept(get());}
                catch(Exception ex){MainFrame.showToast(err+": "+ex.getMessage(),MainFrame.ToastType.ERROR);}
            }
        }.execute();
    }

    // ══════════════════════════════════════════════════════════════════
    //  ZBtn
    // ══════════════════════════════════════════════════════════════════
    static class ZBtn extends JButton {
        private final boolean primary;
        ZBtn(String text,boolean primary){
            super(text);this.primary=primary;
            setFont(new Font("Segoe UI",Font.BOLD,12));
            setForeground(primary?Color.WHITE:TXT_PRI);
            setOpaque(false);setContentAreaFilled(false);setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8,18,8,18));
        }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=g2d(g);
            if(primary){
                Color base=new Color(6,182,212);
                g2.setColor(getModel().isPressed()?base.darker():base);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                if(!getModel().isPressed()){
                    g2.setPaint(new GradientPaint(0,0,new Color(255,255,255,40),
                        0,getHeight()/2f,new Color(0,0,0,0)));
                    g2.fillRoundRect(0,0,getWidth(),getHeight()/2,10,10);
                }
            }else{
                g2.setColor(getModel().isRollover()?new Color(240,252,255):BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(COL_BRD);g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
            }
            g2.dispose();super.paintComponent(g);
        }
        private static Graphics2D g2d(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            return g2;
        }
    }
}