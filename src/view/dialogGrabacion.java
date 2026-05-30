package view;

import model.Grabacion;
import services.GrabacionService;
import util.AudioRecorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class dialogGrabacion extends JDialog {

    private static final Color BG_DEEP   = new Color(0x04111F);
    private static final Color BG_CARD   = new Color(0x061829);
    private static final Color BG_FIELD  = new Color(0x0A1F36);
    private static final Color C_RED     = new Color(0xEF4444);
    private static final Color C_GREEN   = new Color(0x22C55E);
    private static final Color C_CYAN    = new Color(0x00BCD4);
    private static final Color C_AMBER   = new Color(0xFFA726);
    private static final Color TXT_PRI   = new Color(0xE8EFF7);
    private static final Color TXT_SEC   = new Color(0x6B89A8);
    private static final Color COL_BRD   = new Color(0x0D2A45);

    private final int idSesion;
    private final String nombreSesion;
    private final AudioRecorder recorder = new AudioRecorder();
    private final GrabacionService servicio = new GrabacionService();

    private JLabel lblEstado;
    private JLabel lblTimer;
    private JLabel lblWave;
    private JButton btnGrabar;
    private JButton btnDetener;
    private Timer timerUI;
    private File archivoActual;
    private Runnable onGrabacionGuardada;

    public dialogGrabacion(Frame owner, int idSesion, String nombreSesion) {
        super(owner, "🎙 Grabar audio - " + nombreSesion, true);
        this.idSesion = idSesion;
        this.nombreSesion = nombreSesion;
        
        construirUI();
    }

    public void setOnGrabacionGuardada(Runnable callback) {
    this.onGrabacionGuardada = callback;
}
    private void construirUI() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BG_DEEP);
        root.setBorder(new EmptyBorder(24, 32, 24, 32));

        // Cabecera
        JPanel cab = new JPanel();
        cab.setOpaque(false);
        cab.setLayout(new BoxLayout(cab, BoxLayout.Y_AXIS));
        JLabel tit = new JLabel("🎙  Grabación de audio");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tit.setForeground(TXT_PRI);
        tit.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel("SESIÓN: " + nombreSesion.toUpperCase());
        sub.setFont(new Font("Segoe UI", Font.BOLD, 10));
        sub.setForeground(C_CYAN);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        cab.add(tit);
        cab.add(Box.createVerticalStrut(4));
        cab.add(sub);
        root.add(cab, BorderLayout.NORTH);

        // Centro - estado + timer + onda
        JPanel centro = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(COL_BRD);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(new EmptyBorder(28, 24, 28, 24));

        // Estado
        lblEstado = new JLabel("⬤  LISTO PARA GRABAR", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEstado.setForeground(TXT_SEC);
        lblEstado.setAlignmentX(CENTER_ALIGNMENT);

        // Timer
        lblTimer = new JLabel("00:00", SwingConstants.CENTER);
        lblTimer.setFont(new Font("Consolas", Font.BOLD, 56));
        lblTimer.setForeground(TXT_PRI);
        lblTimer.setAlignmentX(CENTER_ALIGNMENT);

        // Onda animada
        lblWave = new JLabel("▁▂▃▅▇█▇▅▃▂▁", SwingConstants.CENTER);
        lblWave.setFont(new Font("Consolas", Font.PLAIN, 24));
        lblWave.setForeground(new Color(40, 60, 90));
        lblWave.setAlignmentX(CENTER_ALIGNMENT);

        centro.add(lblEstado);
        centro.add(Box.createVerticalStrut(16));
        centro.add(lblTimer);
        centro.add(Box.createVerticalStrut(20));
        centro.add(lblWave);

        root.add(centro, BorderLayout.CENTER);

        // Botones
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btns.setOpaque(false);
        btns.setBorder(new EmptyBorder(16, 0, 0, 0));

        btnGrabar = crearBtn("🔴  Grabar", C_RED, true, e -> iniciarGrabacion());
        btnDetener = crearBtn("⏹  Detener", C_GREEN, false, e -> detenerGrabacion());
        btnDetener.setEnabled(false);

        JButton btnCerrar = crearBtn("✖  Cerrar", BG_FIELD, false, e -> dispose());

        btns.add(btnGrabar);
        btns.add(btnDetener);
        btns.add(btnCerrar);
        root.add(btns, BorderLayout.SOUTH);

        setContentPane(root);
        setSize(480, 420);
        setLocationRelativeTo(getOwner());

        // Verifica si hay micrófono
        if (!AudioRecorder.hayMicrofono()) {
            lblEstado.setText("⚠  NO HAY MICRÓFONO DISPONIBLE");
            lblEstado.setForeground(C_RED);
            btnGrabar.setEnabled(false);
        }
    }

    private void iniciarGrabacion() {
        try {
            // Carpeta de salida
            File carpeta = new File("grabaciones");
            if (!carpeta.exists()) carpeta.mkdirs();

            // Nombre único
            String fechaStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nombreArchivo = "sesion_" + idSesion + "_" + fechaStr + ".wav";
            archivoActual = new File(carpeta, nombreArchivo);

            recorder.iniciar(archivoActual);

            lblEstado.setText("● GRABANDO...");
            lblEstado.setForeground(C_RED);
            btnGrabar.setEnabled(false);
            btnDetener.setEnabled(true);

            // Timer UI que actualiza cada 500ms
            timerUI = new Timer(500, ev -> {
                int s = recorder.segundosTranscurridos();
                int m = s / 60;
                int rs = s % 60;
                lblTimer.setText(String.format("%02d:%02d", m, rs));
                animarWave();
            });
            timerUI.start();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al iniciar grabación: " + ex.getMessage(),
                    "Z-One", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int waveFrame = 0;
    private void animarWave() {
        String[] frames = {
            "▁▂▃▅▇█▇▅▃▂▁",
            "▂▃▅▇█▇▅▃▂▁▂",
            "▃▅▇█▇▅▃▂▁▂▃",
            "▅▇█▇▅▃▂▁▂▃▅"
        };
        lblWave.setText(frames[waveFrame % frames.length]);
        lblWave.setForeground(C_AMBER);
        waveFrame++;
    }

    private void detenerGrabacion() {
        if (timerUI != null) timerUI.stop();

        int duracion = recorder.detener();

        lblEstado.setText("✓  GRABACIÓN COMPLETADA");
        lblEstado.setForeground(C_GREEN);
        lblWave.setText("▁▁▁▁▁▁▁▁▁▁▁");
        lblWave.setForeground(new Color(40, 60, 90));
        btnGrabar.setEnabled(true);
        btnDetener.setEnabled(false);

        // Calcular tamaño en KB
        long tamanoKb = archivoActual.length() / 1024;

        // Pedir observaciones
        String obs = JOptionPane.showInputDialog(this,
                "Grabación lista (" + duracion + "s, " + tamanoKb + " KB).\n" +
                "¿Observaciones? (opcional)",
                "Z-One - Guardar grabación",
                JOptionPane.PLAIN_MESSAGE);

        if (obs == null) {
            // canceló
            archivoActual.delete();
            lblEstado.setText("⬤  LISTO PARA GRABAR");
            lblEstado.setForeground(TXT_SEC);
            lblTimer.setText("00:00");
            return;
        }

        // Guardar en BD
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
                    "✓ Grabación guardada con ID " + id + "\n" +
                    "Archivo: " + archivoActual.getName(),
                    "Z-One", JOptionPane.INFORMATION_MESSAGE);
            if (onGrabacionGuardada != null) onGrabacionGuardada.run();
            // Reset
            lblEstado.setText("⬤  LISTO PARA OTRA GRABACIÓN");
            lblEstado.setForeground(TXT_SEC);
            lblTimer.setText("00:00");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al guardar: " + ex.getMessage(),
                    "Z-One", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton crearBtn(String txt, Color color, boolean filled,
                              java.awt.event.ActionListener a) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(filled ? Color.WHITE : TXT_PRI);
        b.setBackground(color);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(130, 42));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(a);
        return b;
    }
}