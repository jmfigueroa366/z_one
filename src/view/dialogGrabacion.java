package view;

import model.Grabacion;
import services.GrabacionService;
import util.AudioRecorder;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

public class dialogGrabacion extends JDialog {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final Color BG      = new Color(0xEFF3F8);
    private static final Color CARD    = new Color(0xFFFFFF);
    private static final Color FTR     = new Color(0xF5F8FC);
    private static final Color C_BLU   = new Color(0x1565C0);
    private static final Color C_CYN   = new Color(0x00838F);
    private static final Color C_RED   = new Color(0xE53935);
    private static final Color C_GRN   = new Color(0x1DB954);
    private static final Color C_AMB   = new Color(0xF57C00);
    private static final Color C_BRD   = new Color(0xD0DCE8);
    private static final Color TXT_PRI = new Color(0x0F1923);
    private static final Color TXT_SEC = new Color(0x5A7080);
    private static final Color VU_LO   = new Color(0x1DB954);
    private static final Color VU_MID  = new Color(0xF57C00);
    private static final Color VU_HI   = new Color(0xE53935);

    // ── Estado ────────────────────────────────────────────────────────────────
    private final int    idSesion;
    private final String nombreSesion;
    private final AudioRecorder    recorder = new AudioRecorder();
    private final GrabacionService servicio = new GrabacionService();
    private Runnable onGrabacionGuardada;
    private File archivoActual;

    // ── Componentes UI ────────────────────────────────────────────────────────
    private JLabel         lblTimer, lblEstado;
    private JButton        btnGrabar, btnDetener;
    private Timer          timerUI;
    private WaveformPanel  wavePanel;
    private SpectrumPanel  specPanel;
    private VUMeterPanel   vuLeft, vuRight;
    private OrbitalPanel   orbitalPanel;
    private ParticleCanvas particleCanvas;
    private RulerPanel     rulerPanel;
    private DotWidget      dotWidget;
    private JLabel         dbLabelL, dbLabelR, clipLight;
    private JLabel         peakPillL, peakPillR;
    private ShimmerCard    timerCard;
    private HeaderScanPanel headerScan;

    // ── Datos de audio ────────────────────────────────────────────────────────
    private LevelMonitor levelMonitor;
    private static final int WPTS  = 300;
    private static final int FBINS = 60;
    private final Deque<Float> wdL    = new ArrayDeque<>();
    private final Deque<Float> wdR    = new ArrayDeque<>();
    private final float[]      fBins  = new float[FBINS];
    private final float[]      fPeaks = new float[FBINS];
    private final int[]        fHold  = new int[FBINS];
    private float pkL, pkR, dspL, dspR;
    private int   holdL, holdR;

    // ── Tick state ────────────────────────────────────────────────────────────
    private long    startMs;
    private boolean isRecording;

    // ══════════════════════════════════════════════════════════════════════════
    //  Constructor
    // ══════════════════════════════════════════════════════════════════════════
    public dialogGrabacion(Frame owner, int idSesion, String nombreSesion) {
        super(owner, "Z-One  ·  " + nombreSesion, true);
        this.idSesion     = idSesion;
        this.nombreSesion = nombreSesion;
        for (int i = 0; i < WPTS; i++) {
            wdL.addLast(0f);
            wdR.addLast(0f);
        }
        construirUI();
        AudioRecorder.listarDispositivos();
    }

    public void setOnGrabacionGuardada(Runnable cb) {
        this.onGrabacionGuardada = cb;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UI
    // ══════════════════════════════════════════════════════════════════════════
    private void construirUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        setContentPane(root);
        setSize(680, 580);
        setMinimumSize(new Dimension(580, 500));
        setLocationRelativeTo(getOwner());
        if (!AudioRecorder.hayMicrofono()) {
            lblEstado.setText("SIN MICRÓFONO");
            lblEstado.setForeground(C_RED);
            btnGrabar.setEnabled(false);
        }
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        headerScan = new HeaderScanPanel();

        JPanel outer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                headerScan.paintScan((Graphics2D) g, getWidth(), getHeight());
            }
        };
        outer.setPreferredSize(new Dimension(0, 56));
        outer.setBackground(CARD);

        // barra azul izquierda
        JPanel accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(5, 56));
        accentBar.setBackground(C_BLU);
        outer.add(accentBar, BorderLayout.WEST);

        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        dotWidget = new DotWidget();
        dotWidget.setPreferredSize(new Dimension(20, 20));

        JLabel title = new JLabel("Z-ONE RECORDER");
        title.setFont(new Font("Consolas", Font.BOLD, 13));
        title.setForeground(TXT_PRI);

        JLabel sep = new JLabel("/");
        sep.setFont(new Font("Consolas", Font.PLAIN, 13));
        sep.setForeground(C_BRD);

        JLabel sesLbl = new JLabel(nombreSesion.toUpperCase());
        sesLbl.setFont(new Font("Consolas", Font.BOLD, 11));
        sesLbl.setForeground(C_CYN);

        left.add(dotWidget);
        left.add(title);
        left.add(sep);
        left.add(sesLbl);

        lblEstado = buildBadge("STANDBY", new Color(0xEEF2F8), TXT_SEC);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);
        right.add(lblEstado);

        inner.add(left,  BorderLayout.CENTER);
        inner.add(right, BorderLayout.EAST);

        outer.add(inner, BorderLayout.CENTER);
        outer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BRD));
        return outer;
    }

    private JLabel buildBadge(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Consolas", Font.BOLD, 10));
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(false);
        lbl.setBorder(new EmptyBorder(4, 12, 4, 12));
        return lbl;
    }

    // ── Centro ────────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(14, 16, 0, 16));

        // Fila 1: timer + VU
        JPanel row1 = new JPanel(new BorderLayout(12, 0));
        row1.setOpaque(false);

        timerCard = new ShimmerCard();
        timerCard.setLayout(new BoxLayout(timerCard, BoxLayout.Y_AXIS));
        timerCard.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel durLbl = new JLabel("TIEMPO TRANSCURRIDO");
        durLbl.setFont(new Font("Consolas", Font.PLAIN, 9));
        durLbl.setForeground(TXT_SEC);
        durLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTimer = new JLabel("00:00.0");
        lblTimer.setFont(new Font("Consolas", Font.BOLD, 52));
        lblTimer.setForeground(TXT_PRI);
        lblTimer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        metaRow.setOpaque(false);
        metaRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        peakPillL = buildPill("L  — dB", new Color(0xE8F2FC), new Color(0x1565C0));
        peakPillR = buildPill("R  — dB", new Color(0xE0F5F5), new Color(0x006064));
        clipLight = new JLabel("●");
        clipLight.setFont(new Font("Monospaced", Font.BOLD, 10));
        clipLight.setForeground(C_BRD);

        metaRow.add(peakPillL);
        metaRow.add(peakPillR);
        metaRow.add(clipLight);

        timerCard.add(durLbl);
        timerCard.add(Box.createVerticalStrut(4));
        timerCard.add(lblTimer);
        timerCard.add(Box.createVerticalStrut(6));
        timerCard.add(metaRow);

        // VU group
        JPanel vuGroup = new JPanel();
        vuGroup.setLayout(new BoxLayout(vuGroup, BoxLayout.Y_AXIS));
        vuGroup.setOpaque(false);

        JPanel vuMeters = new JPanel(new GridLayout(1, 2, 6, 0));
        vuMeters.setOpaque(false);
        vuLeft  = new VUMeterPanel("L");
        vuRight = new VUMeterPanel("R");
        vuMeters.add(vuLeft);
        vuMeters.add(vuRight);

        JPanel dbRow = new JPanel(new GridLayout(1, 2, 6, 0));
        dbRow.setOpaque(false);
        dbLabelL = tinyLabel("—");
        dbLabelR = tinyLabel("—");
        dbRow.add(dbLabelL);
        dbRow.add(dbLabelR);

        vuGroup.add(vuMeters);
        vuGroup.add(Box.createVerticalStrut(3));
        vuGroup.add(dbRow);

        row1.add(timerCard, BorderLayout.CENTER);
        row1.add(vuGroup,   BorderLayout.EAST);

        // Wave + particle overlay
        JLayeredPane waveLayer = new JLayeredPane();
        waveLayer.setPreferredSize(new Dimension(0, 190));
        wavePanel      = new WaveformPanel();
        particleCanvas = new ParticleCanvas();

        waveLayer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension d = waveLayer.getSize();
                wavePanel.setBounds(0, 0, d.width, d.height);
                particleCanvas.setBounds(0, 0, d.width, d.height);
            }
        });
        waveLayer.add(wavePanel,      JLayeredPane.DEFAULT_LAYER);
        waveLayer.add(particleCanvas, JLayeredPane.PALETTE_LAYER);

        // Fila inferior: orbital + spectrum
        JPanel bottomRow = new JPanel(new BorderLayout(10, 0));
        bottomRow.setOpaque(false);

        orbitalPanel = new OrbitalPanel();
        orbitalPanel.setPreferredSize(new Dimension(120, 120));

        specPanel = new SpectrumPanel();

        bottomRow.add(orbitalPanel, BorderLayout.WEST);
        bottomRow.add(specPanel,    BorderLayout.CENTER);

        rulerPanel = new RulerPanel();
        rulerPanel.setPreferredSize(new Dimension(0, 22));

        p.add(row1,      BorderLayout.NORTH);
        p.add(waveLayer, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(0, 8));
        south.setOpaque(false);
        south.add(bottomRow,  BorderLayout.CENTER);
        south.add(rulerPanel, BorderLayout.SOUTH);
        p.add(south, BorderLayout.SOUTH);

        return p;
    }

    private JLabel tinyLabel(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setFont(new Font("Consolas", Font.PLAIN, 9));
        l.setForeground(TXT_SEC);
        return l;
    }

    private JLabel buildPill(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(new Font("Consolas", Font.BOLD, 9));
        l.setForeground(fg);
        l.setBackground(bg);
        l.setOpaque(false);
        l.setBorder(new EmptyBorder(3, 9, 3, 9));
        return l;
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(FTR);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, C_BRD));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 13));
        btns.setOpaque(false);

        btnGrabar  = dawBtn("⏺  REC",    C_RED,                    Color.WHITE);
        btnDetener = dawBtn("⏹  STOP",   C_GRN,                    Color.WHITE);
        JButton btnCerrar = dawBtn("✕  CERRAR", new Color(0xD6E0EC), new Color(0x334455));

        btnGrabar.addActionListener(e  -> iniciarGrabacion());
        btnDetener.addActionListener(e -> detenerGrabacion());
        btnCerrar.addActionListener(e  -> dispose());

        btnDetener.setEnabled(false);

        btns.add(btnGrabar);
        btns.add(btnDetener);
        btns.add(btnCerrar);
        p.add(btns, BorderLayout.CENTER);

        JLabel fmt = new JLabel("PCM · 44100 Hz · 16 bit · Stereo   ");
        fmt.setFont(new Font("Consolas", Font.PLAIN, 9));
        fmt.setForeground(TXT_SEC);
        p.add(fmt, BorderLayout.EAST);
        return p;
    }

    private JButton dawBtn(String txt, Color bg, Color fg) {
        JButton b = new JButton(txt) {
            private Point ripple;
            private float rippleR;
            private float rippleA;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        ripple  = e.getPoint();
                        rippleR = 0;
                        rippleA = 0.35f;
                        Timer rt = new Timer(16, null);
                        rt.addActionListener(ev -> {
                            rippleR += 8;
                            rippleA -= 0.015f;
                            if (rippleA <= 0) { rippleA = 0; rt.stop(); }
                            repaint();
                        });
                        rt.start();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = isEnabled() ? bg : new Color(0xCFD8E3);
                if      (getModel().isPressed())   base = base.darker();
                else if (getModel().isRollover())  base = base.brighter();
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                if (ripple != null && rippleA > 0) {
                    g2.setColor(new Color(1f, 1f, 1f, rippleA));
                    g2.fillOval((int) (ripple.x - rippleR),
                                (int) (ripple.y - rippleR),
                                (int) (rippleR * 2),
                                (int) (rippleR * 2));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Consolas", Font.BOLD, 12));
        b.setForeground(fg);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(140, 40));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Lógica de grabación
    // ══════════════════════════════════════════════════════════════════════════
    private void iniciarGrabacion() {
        try {
            File carpeta = new File("grabaciones");
            if (!carpeta.exists()) carpeta.mkdirs();
            String ts = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            archivoActual = new File(carpeta,
                    "sesion_" + idSesion + "_" + ts + ".wav");
            recorder.iniciar(archivoActual);

            isRecording = true;
            startMs     = System.currentTimeMillis();
            updateBadge("● REC", new Color(0xFEE8E8), new Color(0xB71C1C));
            btnGrabar.setEnabled(false);
            btnDetener.setEnabled(true);

            wdL.clear();
            wdR.clear();
            for (int i = 0; i < WPTS; i++) {
                wdL.addLast(0f);
                wdR.addLast(0f);
            }

            particleCanvas.init();
            timerCard.startShimmer();
            headerScan.setActive(true);

            levelMonitor = new LevelMonitor(recorder);
            levelMonitor.start();

            timerUI = new Timer(50, ev -> tick());
            timerUI.start();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al iniciar: " + ex.getMessage(),
                    "Z-One", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tick() {
        long elapsed = System.currentTimeMillis() - startMs;
        long s  = elapsed / 1000;
        long d  = (elapsed % 1000) / 100;
        long m  = s / 60;
        long rs = s % 60;
        lblTimer.setText(String.format("%02d:%02d.%d", m, rs, d));

        if (levelMonitor != null) {
            float rawL = levelMonitor.getPeakL();
            float rawR = levelMonitor.getPeakR();
            pkL = pkL * 0.68f + rawL * 0.32f;
            pkR = pkR * 0.68f + rawR * 0.32f;

            if (pkL > dspL) { dspL = pkL; holdL = 28; }
            else if (holdL-- > 0) { /* mantener */ }
            else dspL = Math.max(0, dspL - 0.018f);

            if (pkR > dspR) { dspR = pkR; holdR = 28; }
            else if (holdR-- > 0) { /* mantener */ }
            else dspR = Math.max(0, dspR - 0.018f);

            wdL.addLast(pkL); wdL.pollFirst();
            wdR.addLast(pkR); wdR.pollFirst();

            updateSpectrum(elapsed / 1000f);

            String dbL = String.format("%.1f", dB(pkL));
            String dbR = String.format("%.1f", dB(pkR));
            peakPillL.setText("L  " + dbL + " dB");
            peakPillR.setText("R  " + dbR + " dB");
            dbLabelL.setText(dbL);
            dbLabelR.setText(dbR);

            boolean clip = pkL > 0.95f || pkR > 0.95f;
            clipLight.setForeground(clip ? C_RED : pkL > 0.70f ? C_AMB : C_GRN);
        }

        dotWidget.tick(true);
        headerScan.tick();
        timerCard.tick();
        particleCanvas.setLevel((pkL + pkR) / 2f);

        wavePanel.repaint();
        specPanel.repaint();
        vuLeft.setLevel(pkL, dspL);   vuLeft.repaint();
        vuRight.setLevel(pkR, dspR);  vuRight.repaint();
        orbitalPanel.setLevel((pkL + pkR) / 2f);
        orbitalPanel.repaint();
        particleCanvas.repaint();
        rulerPanel.setProgress(elapsed);
        rulerPanel.repaint();

        // Repintar el panel que contiene lblEstado para mostrar el scan del header
        Container parent = lblEstado.getParent();
        if (parent != null) {
            Container grandParent = parent.getParent();
            if (grandParent instanceof JComponent) {
                ((JComponent) grandParent).repaint();
            }
        }
    }

    private void updateSpectrum(float t) {
        for (int i = 0; i < FBINS; i++) {
            float f = (float) i / FBINS;
            float v;
            if      (f < .08f) v = 0.85f * Math.abs((float) Math.sin(t * 0.9  + i * .4))  * (1 - f * 8);
            else if (f < .20f) v = 0.65f * Math.abs((float) Math.sin(t * 1.5  + i * .25));
            else if (f < .45f) v = 0.45f * Math.abs((float) Math.sin(t * 2.4  + i * .18));
            else if (f < .70f) v = 0.28f * Math.abs((float) Math.sin(t * 3.8  + i * .14));
            else               v = 0.14f * Math.abs((float) Math.sin(t * 6.0  + i * .10));
            v *= (1 - f * 0.35f);
            fBins[i] = fBins[i] * 0.72f + v * 0.28f;

            if (fBins[i] > fPeaks[i]) {
                fPeaks[i] = fBins[i];
                fHold[i]  = 22;
            } else if (fHold[i]-- > 0) {
                /* mantener peak */
            } else {
                fPeaks[i] = Math.max(0, fPeaks[i] - 0.018f);
            }
        }
    }

    private float dB(float v) {
        return v < 0.00001f ? -60f : (float) (20 * Math.log10(v));
    }

    private void detenerGrabacion() {
        if (timerUI     != null) timerUI.stop();
        if (levelMonitor != null) levelMonitor.stopMonitor();
        int duracion = recorder.detener();
        isRecording  = false;

        updateBadge("STOPPED", new Color(0xFFF3E0), new Color(0xE65100));
        dotWidget.tick(false);
        headerScan.setActive(false);
        timerCard.stopShimmer();
        particleCanvas.setLevel(0);
        btnGrabar.setEnabled(true);
        btnDetener.setEnabled(false);
        clipLight.setForeground(C_BRD);
        peakPillL.setText("L  — dB");
        peakPillR.setText("R  — dB");
        dbLabelL.setText("—");
        dbLabelR.setText("—");
        vuLeft.setLevel(0, 0);   vuLeft.repaint();
        vuRight.setLevel(0, 0);  vuRight.repaint();

        long tamanoKb = archivoActual.length() / 1024;
        String obs = JOptionPane.showInputDialog(this,
                "Grabación lista  ·  " + duracion + "s  ·  " + tamanoKb + " KB\n"
                + "Observaciones (opcional):",
                "Z-One – Guardar", JOptionPane.PLAIN_MESSAGE);

        if (obs == null) {
            archivoActual.delete();
            resetUI();
            return;
        }

        try {
            Grabacion g = new Grabacion();
            g.setIdSesion(idSesion);
            g.setNombreArchivo(archivoActual.getName());
            g.setRutaArchivo(archivoActual.getAbsolutePath());
            g.setDuracionSegundos(duracion);
            g.setTamanoKb(tamanoKb);
            g.setObservaciones(obs.isBlank() ? null : obs);
            int id = servicio.crear(g);
            JOptionPane.showMessageDialog(this,
                    "✓  Guardado  ·  ID " + id + "\n" + archivoActual.getName(),
                    "Z-One", JOptionPane.INFORMATION_MESSAGE);
            if (onGrabacionGuardada != null) onGrabacionGuardada.run();
            resetUI();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al guardar: " + ex.getMessage(),
                    "Z-One", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetUI() {
        lblTimer.setText("00:00.0");
        updateBadge("STANDBY", new Color(0xEEF2F8), TXT_SEC);
        wdL.clear();
        wdR.clear();
        for (int i = 0; i < WPTS; i++) {
            wdL.addLast(0f);
            wdR.addLast(0f);
        }
        wavePanel.repaint();
        specPanel.repaint();
        rulerPanel.repaint();
    }

    private void updateBadge(String txt, Color bg, Color fg) {
        lblEstado.setText(txt);
        lblEstado.setBackground(bg);
        lblEstado.setForeground(fg);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DotWidget – anillo pulsante animado
    // ══════════════════════════════════════════════════════════════════════════
    private static class DotWidget extends JComponent {
        private float   alpha     = 0f;
        private float   ringScale = 1f;
        private boolean dir       = false;

        void tick(boolean rec) {
            if (!rec) { alpha = 0; repaint(); return; }
            alpha += dir ? 0.04f : -0.04f;
            if (alpha <= 0.12f) { alpha = 0.12f; dir = true; }
            if (alpha >= 1.00f) { alpha = 1.00f; dir = false; }
            ringScale = 0.8f + alpha * 0.4f;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int cx = getWidth() / 2, cy = getHeight() / 2, r = 5;
            int rr = (int) (r * ringScale * 1.8);
            g2.setColor(new Color(229, 57, 53, (int) (alpha * 140)));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(cx - rr, cy - rr, rr * 2, rr * 2);
            g2.setColor(alpha > 0.1f
                    ? new Color(229, 57, 53, (int) (alpha * 255))
                    : new Color(0x5A7080));
            g2.fillOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ShimmerCard – fondo blanco con brillo que recorre la tarjeta
    // ══════════════════════════════════════════════════════════════════════════
    private static class ShimmerCard extends JPanel {
        private float shimmerX = -1f;
        private Timer shimmerTimer;

        ShimmerCard() { setOpaque(false); }

        void startShimmer() {
            shimmerX = -0.2f;
            shimmerTimer = new Timer(30, e -> {
                shimmerX += 0.02f;
                if (shimmerX > 1.2f) shimmerX = -0.2f;
                repaint();
            });
            shimmerTimer.start();
        }

        void stopShimmer() {
            if (shimmerTimer != null) shimmerTimer.stop();
            shimmerX = -1f;
            repaint();
        }

        void tick() { repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(CARD);
            g2.fillRoundRect(0, 0, w, h, 12, 12);
            g2.setColor(C_BRD);
            g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
            // borde izquierdo azul
            g2.setColor(C_BLU);
            g2.fillRoundRect(0, 0, 4, h, 4, 4);
            // shimmer
            if (shimmerX >= 0) {
                float sx = shimmerX * w;
                float x0 = sx - 40f, x2 = sx + 40f;
                Color c0 = new Color(21, 101, 192, 0);
                Color c1 = new Color(21, 101, 192, 18);
                LinearGradientPaint gp = new LinearGradientPaint(
                        x0, 0f, x2, 0f,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{c0, c1, c0});
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, 12, 12);
            }
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HeaderScanPanel – luz que barre el header
    // ══════════════════════════════════════════════════════════════════════════
    private static class HeaderScanPanel {
        private float   pos    = -0.3f;
        private boolean active = false;

        void setActive(boolean a) {
            active = a;
            if (!a) pos = -0.3f;
        }

        void tick() {
            if (active) {
                pos += 0.012f;
                if (pos > 1.3f) pos = -0.3f;
            }
        }

        void paintScan(Graphics2D g2, int w, int h) {
            if (!active) return;
            float sx = pos * w;
            float x0 = sx - 60f, x2 = sx + 60f;
            Color c0 = new Color(21, 101, 192, 0);
            Color c1 = new Color(21, 101, 192, 14);
            LinearGradientPaint gp = new LinearGradientPaint(
                    x0, 0f, x2, 0f,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{c0, c1, c0});
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  WaveformPanel
    // ══════════════════════════════════════════════════════════════════════════
    private class WaveformPanel extends JPanel {
        WaveformPanel() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            g2.setColor(CARD);
            g2.fillRoundRect(0, 0, w, h, 12, 12);
            g2.setColor(C_BRD);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

            // grid horizontal
            g2.setColor(new Color(208, 220, 232, 80));
            g2.setStroke(new BasicStroke(0.5f));
            for (int i = 1; i < 4; i++) {
                g2.drawLine(0, h * i / 4, w, h * i / 4);
            }
            g2.setColor(new Color(190, 205, 220, 180));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(0, h / 2, w, h / 2);

            Float[] sL = wdL.toArray(new Float[0]);
            Float[] sR = wdR.toArray(new Float[0]);

            boolean hasSignal = false;
            for (float v : sL) {
                if (v > 0.002f) { hasSignal = true; break; }
            }

            if (!hasSignal) {
                g2.setColor(TXT_SEC);
                g2.setFont(new Font("Consolas", Font.PLAIN, 11));
                String m = "EN ESPERA DE SEÑAL...";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(m, (w - fm.stringWidth(m)) / 2, h / 2 + 4);
                g2.dispose();
                return;
            }

            drawChannel(g2, sL, w, h, new int[]{21, 101, 192}, true);
            drawChannel(g2, sR, w, h, new int[]{0, 131, 143},  false);

            // Etiquetas de canal
            g2.setFont(new Font("Consolas", Font.BOLD, 9));
            g2.setColor(new Color(21, 101, 192, 120));
            g2.drawString("L", 9, 15);
            g2.setColor(new Color(0, 131, 143, 120));
            g2.drawString("R", 9, h - 6);

            // scan flash si hay clipping
            float avg = (pkL + pkR) / 2f;
            if (avg > 0.65f && isRecording) {
                long  t     = System.currentTimeMillis();
                float scanX = ((t % 3000) / 3000f) * w;
                float x0    = scanX - 30f, x2 = scanX + 30f;
                Color ca0   = new Color(229, 57, 53, 0);
                Color ca1   = new Color(229, 57, 53, (int) ((avg - .65f) * 60));
                LinearGradientPaint gp = new LinearGradientPaint(
                        x0, 0f, x2, 0f,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{ca0, ca1, ca0});
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);
            }
            g2.dispose();
        }

        private void drawChannel(Graphics2D g2, Float[] s, int w, int h,
                                  int[] rgb, boolean upper) {
            if (s.length < 2) return;
            int mid = h / 2, hH = h / 2 - 8;
            int r = rgb[0], gv = rgb[1], b = rgb[2];

            Path2D fill = new Path2D.Float();
            fill.moveTo(0, mid);
            for (int i = 0; i < s.length; i++) {
                float x   = (float) w * i / s.length;
                float amp = Math.min(s[i], 1f) * hH;
                float y   = upper ? mid - amp : mid + amp;
                if (i == 0) {
                    fill.moveTo(x, y);
                } else {
                    float px   = (float) w * (i - 1) / s.length;
                    float pAmp = Math.min(s[i - 1], 1f) * hH;
                    float py   = upper ? mid - pAmp : mid + pAmp;
                    float cx   = (px + x) / 2f;
                    fill.quadTo(cx, py, x, y);
                }
            }
            fill.lineTo(w, mid);
            fill.lineTo(0, mid);
            fill.closePath();

            GradientPaint gp = upper
                    ? new GradientPaint(0f, (float)(mid - hH), new Color(r, gv, b, 130),
                                        0f, (float) mid,        new Color(r, gv, b, 8))
                    : new GradientPaint(0f, (float) mid,        new Color(r, gv, b, 8),
                                        0f, (float)(mid + hH), new Color(r, gv, b, 130));
            g2.setPaint(gp);
            g2.fill(fill);

            // contorno nítido
            Path2D line = new Path2D.Float();
            for (int i = 0; i < s.length; i++) {
                float x   = (float) w * i / s.length;
                float amp = Math.min(s[i], 1f) * hH;
                float y   = upper ? mid - amp : mid + amp;
                if (i == 0) {
                    line.moveTo(x, y);
                } else {
                    float px   = (float) w * (i - 1) / s.length;
                    float pAmp = Math.min(s[i - 1], 1f) * hH;
                    float py   = upper ? mid - pAmp : mid + pAmp;
                    float cx   = (px + x) / 2f;
                    line.quadTo(cx, py, x, y);
                }
            }
            g2.setColor(new Color(r, gv, b, 230));
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(line);
            // glow exterior
            g2.setColor(new Color(r, gv, b, 45));
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(line);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SpectrumPanel
    // ══════════════════════════════════════════════════════════════════════════
    private class SpectrumPanel extends JPanel {
        SpectrumPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 120));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            g2.setColor(CARD);
            g2.fillRoundRect(0, 0, w, h, 10, 10);
            g2.setColor(C_BRD);
            g2.setStroke(new BasicStroke(.5f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);

            float bw = (float) (w - 4) / FBINS; 
            for (int i = 0; i < FBINS; i++) {
                float f  = (float) i / FBINS;
                float v  = fBins[i];
                float pv = fPeaks[i];
                float bh = Math.max(v * (h - 10), 0);
                int ri  = (int) (21  + f * 208);
                int gv  = (int) (101 - f * 44);
                int bi  = (int) (192 - f * 180);

                float x = 2 + i * bw;
                float y = h - bh - 4;
                g2.setColor(new Color(ri, gv, bi, 210));
                g2.fill(new RoundRectangle2D.Float(x, y, bw - .8f, bh, 2, 2));

                if (pv > 0.04f) {
                    float py = h - pv * (h - 10) - 4;
                    g2.setColor(new Color(ri, gv, bi, 130));
                    g2.fillRect((int) x, (int) py, (int) (bw - .8f), 2);
                }
            }

            g2.setFont(new Font("Consolas", Font.PLAIN, 9));
            g2.setColor(new Color(90, 112, 128, 130));
            g2.drawString("ESPECTRO", w - 74, 15);

            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  OrbitalPanel – medidor circular reactivo
    // ══════════════════════════════════════════════════════════════════════════
    private static class OrbitalPanel extends JPanel {
        private float level = 0f;

        OrbitalPanel() { setOpaque(false); }

        void setLevel(float l) { this.level = Math.min(l, 1f); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w  = getWidth(), h = getHeight();
            int cx = w / 2, cy = h / 2;
            int R  = Math.min(w, h) / 2 - 10;

            // fondo
            g2.setColor(CARD);
            g2.fillOval(cx - R - 6, cy - R - 6, (R + 6) * 2, (R + 6) * 2);
            g2.setColor(C_BRD);
            g2.setStroke(new BasicStroke(.8f));
            g2.drawOval(cx - R - 6, cy - R - 6, (R + 6) * 2, (R + 6) * 2);

            // pista base
            g2.setColor(new Color(0xE8EFF6));
            g2.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(cx - R, cy - R, R * 2, R * 2);

            // arco de nivel
            Color arcColor = level > 0.85f ? C_RED : level > 0.65f ? C_AMB : C_BLU;
            g2.setColor(arcColor);
            g2.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int sweepAngle = -(int) (level * 360);
            g2.drawArc(cx - R, cy - R, R * 2, R * 2, 90, sweepAngle);

            // puntos orbitales internos
            int innerR = R - 14;
            for (int i = 0; i < 8; i++) {
                float ang       = (float) (-Math.PI / 2 + i * Math.PI * 2 / 8);
                float intensity = Math.max(0, level - (float) i / 8 * 0.5f);
                if (intensity < 0.02f) continue;
                int dx = (int) (Math.cos(ang) * innerR);
                int dy = (int) (Math.sin(ang) * innerR);
                g2.setColor(new Color(21, 101, 192, (int) (intensity * 70)));
                int pr = (int) (2 + intensity * 4);
                g2.fillOval(cx + dx - pr, cy + dy - pr, pr * 2, pr * 2);
            }

            // texto porcentaje
            String pct = Math.round(level * 100) + "%";
            g2.setFont(new Font("Consolas", Font.BOLD, 15));
            g2.setColor(arcColor);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(pct, cx - fm.stringWidth(pct) / 2, cy + 5);
            g2.setFont(new Font("Consolas", Font.PLAIN, 8));
            g2.setColor(TXT_SEC);
            g2.drawString("NIVEL", cx - 14, cy + 15);

            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ParticleCanvas
    // ══════════════════════════════════════════════════════════════════════════
    private static class ParticleCanvas extends JPanel {
        private static class P {
            float x, y, vx, vy, r, a, life, max;
        }

        private final List<P> particles = new ArrayList<>();
        private final Random  rng       = new Random();
        private float level = 0f;

        ParticleCanvas() { setOpaque(false); }

        void setLevel(float l) { this.level = l; }

        void init() {
            particles.clear();
            int w = getWidth()  > 0 ? getWidth()  : 600;
            int h = getHeight() > 0 ? getHeight() : 190;
            for (int i = 0; i < 55; i++) particles.add(spawn(w, h));
        }

        private P spawn(int w, int h) {
            P p  = new P();
            p.x  = rng.nextFloat() * w;
            p.y  = rng.nextFloat() * h;
            p.vx = (rng.nextFloat() - .5f) * 1.4f;
            p.vy = (rng.nextFloat() - .5f) * 1.4f;
            p.r  = rng.nextFloat() * 1.8f + .5f;
            p.a  = rng.nextFloat() * .5f + .15f;
            p.life = 0;
            p.max  = 100 + rng.nextFloat() * 140;
            return p;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (level < 0.01f) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int   w     = getWidth(), h = getHeight();
            float boost = 1f + level * 4f;

            for (int i = 0; i < particles.size(); i++) {
                P p = particles.get(i);
                p.life++;
                if (p.life > p.max) { particles.set(i, spawn(w, h)); continue; }
                p.x += p.vx * boost;
                p.y += p.vy * boost;
                if (p.x < 0) p.x = w;
                if (p.x > w) p.x = 0;
                if (p.y < 0) p.y = h;
                if (p.y > h) p.y = 0;

                float fade  = Math.min(p.life / 20f, 1f) * Math.min((p.max - p.life) / 20f, 1f);
                float alpha = p.a * fade * (0.3f + level * 1.8f);
                int   a     = Math.min(255, (int) (alpha * 255));

                Color pColor = level > 0.7f ? new Color(229, 57,  53,  a)
                             : level > 0.4f ? new Color(245, 124, 0,   a)
                             :                new Color(21,  101, 192, a);
                g2.setColor(pColor);
                float pr = p.r * (1f + level * .8f);
                g2.fillOval((int) (p.x - pr), (int) (p.y - pr),
                            (int) (pr * 2),   (int) (pr * 2));

                if (level > 0.5f) {
                    for (int j = i + 1; j < Math.min(i + 8, particles.size()); j++) {
                        P q    = particles.get(j);
                        float dx   = q.x - p.x;
                        float dy   = q.y - p.y;
                        float dist = (float) Math.sqrt(dx * dx + dy * dy);
                        if (dist < 50) {
                            int la = (int) ((1f - dist / 50f) * alpha * 0.4f * 255);
                            g2.setColor(new Color(21, 101, 192,
                                    Math.max(0, Math.min(255, la))));
                            g2.setStroke(new BasicStroke(.5f));
                            g2.drawLine((int) p.x, (int) p.y, (int) q.x, (int) q.y);
                        }
                    }
                }
            }
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  VUMeterPanel
    // ══════════════════════════════════════════════════════════════════════════
    private static class VUMeterPanel extends JPanel {
        private float  level, peak;
        private final String ch;

        VUMeterPanel(String ch) {
            this.ch = ch;
            setOpaque(false);
            setPreferredSize(new Dimension(26, 84));
        }

        void setLevel(float l, float p) {
            level = Math.min(l, 1f);
            peak  = Math.min(p, 1f);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight() - 14;
            final int SEGS = 22, gap = 1;
            float segH = (float) (h - 4) / (SEGS + gap * SEGS);

            g2.setColor(new Color(0xEAF0F6));
            g2.fillRoundRect(0, 0, w, h, 5, 5);
            g2.setColor(C_BRD);
            g2.setStroke(new BasicStroke(.5f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 5, 5);

            for (int i = 0; i < SEGS; i++) {
                float sl = (float) (SEGS - i) / SEGS;
                float y  = 2 + i * (segH + gap);
                boolean lit = level >= sl;
                Color c;
                if      (sl > 0.86f) c = lit ? VU_HI  : new Color(0xFCE8E8);
                else if (sl > 0.68f) c = lit ? VU_MID : new Color(0xFDF1E0);
                else                 c = lit ? VU_LO  : new Color(0xE0F5E8);
                g2.setColor(c);
                g2.fillRect(2, (int) y, w - 4, (int) segH);
            }

            if (peak > 0.01f) {
                int py = 2 + (int) ((1f - peak) * (h - 4));
                g2.setColor(peak > 0.86f ? VU_HI : peak > 0.68f ? VU_MID : VU_LO);
                g2.fillRect(2, py, w - 4, 2);
            }

            g2.setFont(new Font("Consolas", Font.BOLD, 9));
            g2.setColor(TXT_SEC);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(ch, (w - fm.stringWidth(ch)) / 2, h + 11);
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  RulerPanel
    // ══════════════════════════════════════════════════════════════════════════
    private static class RulerPanel extends JPanel {
        private long elapsedMs;

        RulerPanel() { setOpaque(false); }

        void setProgress(long ms) { this.elapsedMs = ms; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            g2.setColor(FTR);
            g2.fillRect(0, 0, w, h);

            // barra de progreso (máx 30s)
            float pct = Math.min(elapsedMs / 30000f, 1f);
            int   px  = (int) (pct * w);
            g2.setColor(new Color(21, 101, 192, 180));
            g2.fillRect(0, 0, px, 3);
            if (px > 0) {
                g2.setColor(C_BLU);
                g2.fillRect(px - 1, 0, 2, 7);
            }

            // marcas de tiempo
            g2.setFont(new Font("Consolas", Font.PLAIN, 8));
            int n = 12;
            for (int i = 0; i <= n; i++) {
                int x     = w * i / n;
                boolean major = i % 2 == 0;
                g2.setColor(major ? new Color(0xB0BEC5) : new Color(0xD0DCE8));
                g2.fillRect(x, 0, 1, major ? 7 : 4);
                if (major) {
                    g2.setColor(new Color(0x8090A0));
                    String lbl = (i * 5 / 2) + "s";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(lbl, x - fm.stringWidth(lbl) / 2, h - 2);
                }
            }
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LevelMonitor
    // ══════════════════════════════════════════════════════════════════════════
    private static class LevelMonitor extends Thread {
        private final AudioRecorder    rec;
        private volatile float         peakL, peakR;
        private volatile boolean       running = true;

        LevelMonitor(AudioRecorder rec) {
            super("LevelMonitor");
            setDaemon(true);
            this.rec = rec;
        }

        @Override
        public void run() {
            AudioFormat    fmt  = new AudioFormat(44100f, 16, 2, true, false);
            DataLine.Info  info = new DataLine.Info(TargetDataLine.class, fmt);
            Mixer          target = null;

            for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
                String n = mi.getName().toLowerCase();
                if (n.contains("behringer") || n.contains("umc")) {
                    Mixer m = AudioSystem.getMixer(mi);
                    if (m.isLineSupported(info)) { target = m; break; }
                }
            }

            TargetDataLine line = null;
            try {
                line = target != null
                        ? (TargetDataLine) target.getLine(info)
                        : (TargetDataLine) AudioSystem.getLine(info);
                line.open(fmt, 4096);
                line.start();
                byte[] buf = new byte[1024];
                while (running) {
                    int read = line.read(buf, 0, buf.length);
                    float mL = 0, mR = 0;
                    for (int i = 0; i < read - 3; i += 4) {
                        short sL = (short) ((buf[i + 1] << 8) | (buf[i]     & 0xFF));
                        short sR = (short) ((buf[i + 3] << 8) | (buf[i + 2] & 0xFF));
                        mL = Math.max(mL, Math.abs(sL) / 32768f);
                        mR = Math.max(mR, Math.abs(sR) / 32768f);
                    }
                    peakL = peakL * 0.70f + mL * 0.30f;
                    peakR = peakR * 0.70f + mR * 0.30f;
                }
            } catch (Exception ignored) {
            } finally {
                if (line != null) { line.stop(); line.close(); }
            }
        }

        float getPeakL()  { return peakL; }
        float getPeakR()  { return peakR; }
        void  stopMonitor() { running = false; }
    }
}