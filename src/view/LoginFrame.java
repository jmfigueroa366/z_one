package view;

import model.Usuario;
import services.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.AlphaComposite;
import java.awt.event.*;

import static view.ModernUI.*;

public class LoginFrame extends JFrame {

    private ModernUI.RoundedTextField     txtUsername;
    private ModernUI.RoundedPasswordField txtPassword;
    private ModernUI.RoundedButton        btnLogin;
    private ModernUI.RoundedButton        btnRegistrar;
    private JLabel                        lblEstado;
    private JLabel                        lblConexion;
    private ModernUI.StatusDot            dotConexion;
    private MascotPanel                   mascotPanel;

    private final UsuarioService usuarioService = new UsuarioService();

    public LoginFrame() {
        inicializarUI();
        verificarConexion();
    }

    // =================================================================
    // UI PRINCIPAL
    // =================================================================
    private void inicializarUI() {
        setTitle("Z-One Music — Entra en tu sonido");
        setSize(900, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridLayout(1, 2, 0, 0));
        root.setBackground(BG_DARK);

        // ── MITAD IZQUIERDA ──────────────────────────────────────────
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint bg = new GradientPaint(
                    0, 0,          new Color(10, 6, 30),
                    getWidth(), 0, new Color(22, 14, 55));
                g2.setPaint(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());

                RadialGradientPaint glow1 = new RadialGradientPaint(
                    getWidth() * 0.5f, -40, getWidth() * 0.85f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(100, 60, 220, 80),
                                new Color(100, 60, 220, 0)});
                g2.setPaint(glow1);
                g2.fillRect(0, 0, getWidth(), getHeight() * 2 / 3);

                RadialGradientPaint glow2 = new RadialGradientPaint(
                    getWidth() * 0.3f, (float) getHeight(), getWidth() * 0.7f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(6, 182, 212, 45),
                                new Color(6, 182, 212, 0)});
                g2.setPaint(glow2);
                g2.fillRect(0, getHeight() / 2, getWidth(), getHeight() / 2);

                g2.setColor(new Color(139, 92, 246, 70));
                int[][] dots = {{30,40},{60,120},{20,220},{280,80},{310,200},{40,460},{300,420}};
                for (int[] d : dots) g2.fillOval(d[0], d[1], 3, 3);
                g2.setColor(new Color(6, 182, 212, 50));
                int[][] dots2 = {{80,300},{250,350},{120,500},{330,480},{15,380}};
                for (int[] d : dots2) g2.fillOval(d[0], d[1], 2, 2);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BorderLayout());

        mascotPanel = new MascotPanel();
        mascotPanel.setPreferredSize(new Dimension(450, 360));
        mascotPanel.setMinimumSize(new Dimension(300, 280));

        JPanel sloganPanel = new JPanel();
        sloganPanel.setOpaque(false);
        sloganPanel.setLayout(new BoxLayout(sloganPanel, BoxLayout.Y_AXIS));
        sloganPanel.setBorder(new EmptyBorder(0, 30, 28, 30));

        JLabel line1 = new JLabel("CREA.");
        line1.setFont(new Font("Segoe UI", Font.BOLD, 28));
        line1.setForeground(Color.WHITE);
        line1.setAlignmentX(LEFT_ALIGNMENT);

        JLabel line2 = new JLabel("PRODUCE.");
        line2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        line2.setForeground(new Color(167, 124, 255));
        line2.setAlignmentX(LEFT_ALIGNMENT);

        JLabel line3 = new JLabel("SUENA.");
        line3.setFont(new Font("Segoe UI", Font.BOLD, 28));
        line3.setForeground(new Color(6, 182, 212));
        line3.setAlignmentX(LEFT_ALIGNMENT);

        sloganPanel.add(line1);
        sloganPanel.add(line2);
        sloganPanel.add(line3);

        JPanel mascotWrapper = new JPanel(new GridBagLayout());
        mascotWrapper.setOpaque(false);
        mascotWrapper.setBorder(new EmptyBorder(40, 0, 0, 0));
        mascotWrapper.add(mascotPanel);
        leftPanel.add(mascotWrapper, BorderLayout.CENTER);
        leftPanel.add(sloganPanel,   BorderLayout.SOUTH);

        // ── MITAD DERECHA ────────────────────────────────────────────
        JPanel rightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(16, 14, 38));
                g2.fillRect(0, 0, getWidth(), getHeight());

                GradientPaint sep = new GradientPaint(
                    0, getHeight() * 0.1f, new Color(139, 92, 246, 0),
                    0, getHeight() * 0.5f, new Color(139, 92, 246, 80));
                g2.setPaint(sep);
                g2.fillRect(0, 0, 1, getHeight() / 2);

                GradientPaint sep2 = new GradientPaint(
                    0, getHeight() * 0.5f, new Color(139, 92, 246, 80),
                    0, getHeight() * 0.9f, new Color(139, 92, 246, 0));
                g2.setPaint(sep2);
                g2.fillRect(0, getHeight() / 2, 1, getHeight() / 2);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new GridBagLayout());

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(0, 50, 0, 50));

        ModernUI.LogoPanel logo = new ModernUI.LogoPanel();
        logo.setAlignmentX(CENTER_ALIGNMENT);
        logo.setPreferredSize(new Dimension(60, 60));
        logo.setMaximumSize(new Dimension(60, 60));

        JLabel welcome = new JLabel("BIENVENIDO DE NUEVO");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcome.setForeground(TEXT_PRIMARY);
        welcome.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Ingresa tus credenciales para continuar");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        txtUsername = new ModernUI.RoundedTextField("Tu usuario");
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        txtUsername.setAlignmentX(LEFT_ALIGNMENT);

        txtPassword = new ModernUI.RoundedPasswordField("Tu contraseña");
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);

        btnLogin = new ModernUI.RoundedButton("Iniciar sesión", true);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);

        btnRegistrar = new ModernUI.RoundedButton("¿No tienes cuenta? Regístrate", false);
        btnRegistrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnRegistrar.setAlignmentX(CENTER_ALIGNMENT);

        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(ACCENT_PINK);
        lblEstado.setAlignmentX(CENTER_ALIGNMENT);

        JPanel conexionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        conexionPanel.setOpaque(false);
        conexionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        dotConexion = new ModernUI.StatusDot();
        lblConexion = new JLabel("Verificando conexión...");
        lblConexion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblConexion.setForeground(TEXT_MUTED);
        conexionPanel.add(dotConexion);
        conexionPanel.add(lblConexion);

        // Sección superior
        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setAlignmentX(LEFT_ALIGNMENT);
        topSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        logo.setAlignmentX(CENTER_ALIGNMENT);
        welcome.setAlignmentX(CENTER_ALIGNMENT);
        sub.setAlignmentX(CENTER_ALIGNMENT);
        topSection.add(logo);
        topSection.add(Box.createVerticalStrut(14));
        topSection.add(welcome);
        topSection.add(Box.createVerticalStrut(5));
        topSection.add(sub);

        // Sección campos
        JPanel camposSection = new JPanel();
        camposSection.setOpaque(false);
        camposSection.setLayout(new BoxLayout(camposSection, BoxLayout.Y_AXIS));
        camposSection.setAlignmentX(LEFT_ALIGNMENT);
        camposSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel lblUser = formLabel("USUARIO");
        lblUser.setAlignmentX(LEFT_ALIGNMENT);
        txtUsername.setAlignmentX(LEFT_ALIGNMENT);
        JLabel lblPass = formLabel("CONTRASEÑA");
        lblPass.setAlignmentX(LEFT_ALIGNMENT);
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);

        camposSection.add(lblUser);
        camposSection.add(Box.createVerticalStrut(7));
        camposSection.add(txtUsername);
        camposSection.add(Box.createVerticalStrut(18));
        camposSection.add(lblPass);
        camposSection.add(Box.createVerticalStrut(7));
        camposSection.add(txtPassword);

        // Sección botones
        JPanel botonesSection = new JPanel();
        botonesSection.setOpaque(false);
        botonesSection.setLayout(new BoxLayout(botonesSection, BoxLayout.Y_AXIS));
        botonesSection.setAlignmentX(LEFT_ALIGNMENT);

        btnLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnRegistrar.setAlignmentX(CENTER_ALIGNMENT);
        lblEstado.setAlignmentX(CENTER_ALIGNMENT);
        conexionPanel.setAlignmentX(CENTER_ALIGNMENT);

        botonesSection.add(btnLogin);
        botonesSection.add(Box.createVerticalStrut(10));
        botonesSection.add(btnRegistrar);
        botonesSection.add(Box.createVerticalStrut(14));
        botonesSection.add(lblEstado);
        botonesSection.add(Box.createVerticalStrut(5));
        botonesSection.add(conexionPanel);

        // Ensamblado formulario
        form.add(Box.createVerticalGlue());
        form.add(topSection);
        form.add(Box.createVerticalStrut(30));
        form.add(camposSection);
        form.add(Box.createVerticalStrut(24));
        form.add(botonesSection);
        form.add(Box.createVerticalGlue());

        rightPanel.add(form);

        root.add(leftPanel);
        root.add(rightPanel);
        setContentPane(root);

        // ── Eventos ──────────────────────────────────────────────────
        btnLogin.addActionListener(e -> accionLogin());
        btnRegistrar.addActionListener(e -> abrirRegistroConAnimacion());

        KeyAdapter teclas = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) accionLogin();
                else mascotPanel.triggerExcited();
            }
        };
        txtUsername.addKeyListener(teclas);
        txtPassword.addKeyListener(teclas);
    }

    // =================================================================
    // LOGIN
    // =================================================================
    private void accionLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            mostrarEstado("Completa todos los campos.", ACCENT_PINK);
            return;
        }
        mostrarEstado("Verificando credenciales...", TEXT_MUTED);
        btnLogin.setEnabled(false);
        mascotPanel.triggerExcited();

        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            protected Usuario doInBackground() throws Exception {
                return usuarioService.login(user, pass);
            }
            protected void done() {
                btnLogin.setEnabled(true);
                try {
                    Usuario u = get();
                    // ✔ CORRECCIÓN: getNombre() no existe → getNombreCompleto()
                    mostrarEstado("¡Bienvenido, " + u.getNombreCompleto() + "!", SUCCESS);
                    Timer t = new Timer(800, ev -> {
                        try {
                            dispose();
                            new MainFrame(u).setVisible(true);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                "Error al abrir MainFrame:\n" + ex.getMessage(),
                                "Z-One - Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    t.setRepeats(false);
                    t.start();
                } catch (Exception ex) {
                    String msg = ex.getCause() != null
                        ? ex.getCause().getMessage() : ex.getMessage();
                    mostrarEstado(msg, ACCENT_PINK);
                    txtPassword.setText("");
                    txtPassword.requestFocus();
                }
            }
        };
        worker.execute();
    }

    // =================================================================
    // VERIFICAR CONEXIÓN
    // =================================================================
    private void verificarConexion() {
        new SwingWorker<Boolean, Void>() {
            protected Boolean doInBackground() { return usuarioService.hayConexion(); }
            protected void done() {
                try {
                    if (get()) {
                        lblConexion.setText("Oracle conectado correctamente");
                        lblConexion.setForeground(SUCCESS);
                        dotConexion.setColor(SUCCESS);
                    } else {
                        lblConexion.setText("Sin conexión — revisa ConexionDB.java");
                        lblConexion.setForeground(ACCENT_PINK);
                        dotConexion.setColor(ACCENT_PINK);
                    }
                } catch (Exception e) {
                    lblConexion.setText("Error de conexión");
                    lblConexion.setForeground(ACCENT_PINK);
                    dotConexion.setColor(ACCENT_PINK);
                }
            }
        }.execute();
    }

    // =================================================================
    // ANIMACIÓN CORTINA MORADA → abre RegistroDialog
    // =================================================================
    private static class CurtainPanel extends JPanel {

        private float   progress = 0f;
        private boolean closing  = false;

        public float   getProgress()        { return progress; }
        public void    setProgress(float p) { this.progress = p; repaint(); }
        public boolean isClosing()          { return closing; }
        public void    startClose()         { this.closing = true; }

        private float easeInOutCubic(float t) {
            return t < 0.5f
                ? 4 * t * t * t
                : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int totalW   = getWidth();
            int totalH   = getHeight();
            int curtainW = (int)(totalW * easeInOutCubic(progress));

            // Cuerpo principal
            g2.setColor(new Color(0x5B21B6));
            g2.fillRect(0, 0, curtainW, totalH);

            // Borde de barrido con gradiente
            if (curtainW > 0 && curtainW < totalW) {
                int edgeW = 18;
                g2.setPaint(new GradientPaint(
                    curtainW - edgeW, 0, new Color(0x7C3AED),
                    curtainW,         0, new Color(0x3B0F8C)));
                g2.fillRect(curtainW - edgeW, 0, edgeW, totalH);

                // Línea brillante en el frente
                g2.setColor(new Color(167, 139, 250, 180));
                g2.fillRect(curtainW - 2, 0, 2, totalH);
            }

            // Partículas decorativas
            g2.setColor(new Color(196, 181, 253, 60));
            int[][] sparks = {
                {12, 40}, {28, 120}, {8, 240}, {40, 320}, {20, 420}
            };
            for (int[] s : sparks) {
                if (s[0] < curtainW - 4) {
                    g2.fillOval(s[0], s[1] % totalH, 3, 3);
                }
            }

            g2.dispose();
        }
    }

    private void abrirRegistroConAnimacion() {

        final int   TOTAL_W   = getWidth();
        final int   TOTAL_H   = getHeight();
        final int   TICK_MS   = 10;
        final float SPEED_IN  = 0.07f;
        final float SPEED_OUT = 0.09f;

        CurtainPanel curtain = new CurtainPanel();
        curtain.setOpaque(false);
        curtain.setBounds(0, 0, TOTAL_W, TOTAL_H);

        getLayeredPane().add(curtain, JLayeredPane.POPUP_LAYER);

        final boolean[] dialogOpened = {false};

        Timer timer = new Timer(TICK_MS, null);
        timer.addActionListener(e -> {
            float p = curtain.getProgress();

            if (!curtain.isClosing()) {
                // FASE 1: cortina entra
                p = Math.min(p + SPEED_IN, 1f);
                curtain.setProgress(p);

                if (p >= 1f && !dialogOpened[0]) {
                    dialogOpened[0] = true;

                    Timer openDelay = new Timer(60, ev -> {
                        ((Timer) ev.getSource()).stop();

                        RegistroDialog dlg = new RegistroDialog(
                                LoginFrame.this, usuarioService);

                        dlg.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosed(WindowEvent we) {
                                curtain.startClose();
                            }
                        });

                        dlg.setVisible(true);
                    });
                    openDelay.setRepeats(false);
                    openDelay.start();
                }

            } else {
                // FASE 2: cortina sale
                p = Math.max(p - SPEED_OUT, 0f);
                curtain.setProgress(p);

                if (p <= 0f) {
                    timer.stop();
                    getLayeredPane().remove(curtain);
                    getLayeredPane().repaint();
                }
            }
        });

        timer.start();
    }

    // =================================================================
    // HELPERS
    // =================================================================
    private void mostrarEstado(String msg, Color color) {
        lblEstado.setText(msg);
        lblEstado.setForeground(color);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}