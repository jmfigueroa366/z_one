package view;

import model.Usuario;
import services.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
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

    private void inicializarUI() {
        setTitle("Z-One Music — Entra en tu sonido");
        setSize(860, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Panel raíz con gradiente ──────────────────────────────────
        ModernUI.GradientPanel root = new ModernUI.GradientPanel();
        root.setLayout(new GridBagLayout());

        // ── Wrapper externo: mascota + tarjeta lado a lado ────────────
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH;
        g.insets = new Insets(0, 0, 0, 0);

        // ── Lado izquierdo: panel oscuro con mascota ──────────────────
        JPanel leftPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Fondo ligeramente más claro que el BG para crear contraste sutil
                g2.setColor(new Color(20, 14, 50, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                // Borde izquierdo brillante
                GradientPaint gp = new GradientPaint(
                    0, 0,           new Color(139, 92, 246, 0),
                    0, getHeight()/2f, new Color(139, 92, 246, 120));
                g2.setPaint(gp);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 28, 28);
                // Glow decorativo en la parte superior izquierda
                RadialGradientPaint glow = new RadialGradientPaint(
                    getWidth()/2f, 60, 130,
                    new float[]{0f, 1f},
                    new Color[]{new Color(139, 92, 246, 35), new Color(139, 92, 246, 0)});
                g2.setPaint(glow);
                g2.fillOval(getWidth()/2-130, -70, 260, 260);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setPreferredSize(new Dimension(340, 620));

        // Mascota centrada vertical y horizontalmente
        mascotPanel = new MascotPanel();
        mascotPanel.setPreferredSize(new Dimension(280, 300));
        mascotPanel.setMaximumSize(new Dimension(280, 300));
        mascotPanel.setMinimumSize(new Dimension(280, 300));
        // Texto descriptivo debajo de la mascota
        JPanel leftContent = new JPanel();
        leftContent.setOpaque(false);
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));

        JLabel tagline1 = new JLabel("Tu asistente musical");
        tagline1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tagline1.setForeground(new Color(167, 124, 255));
        tagline1.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tagline2 = new JLabel("siempre contigo");
        tagline2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tagline2.setForeground(new Color(148, 163, 184));
        tagline2.setAlignmentX(CENTER_ALIGNMENT);

        leftContent.add(mascotPanel);
        leftContent.add(Box.createVerticalStrut(6));
        leftContent.add(tagline1);
        leftContent.add(Box.createVerticalStrut(3));
        leftContent.add(tagline2);

        leftPanel.add(leftContent);

        // ── Separador decorativo ──────────────────────────────────────
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int h = getHeight();
                // línea con degradado
                g2.setPaint(new GradientPaint(0, h*0.15f, new Color(139,92,246,0),
                                              0, h*0.5f,  new Color(139,92,246,100)));
                g2.fillRect(1, 0, 1, h/2);
                g2.setPaint(new GradientPaint(0, h*0.5f,  new Color(139,92,246,100),
                                              0, h*0.85f, new Color(139,92,246,0)));
                g2.fillRect(1, h/2, 1, h/2);
                g2.dispose();
            }
        };
        sep.setOpaque(false);
        sep.setPreferredSize(new Dimension(3, 540));

        // ── Lado derecho: tarjeta login ───────────────────────────────
        ModernUI.CardPanel card = new ModernUI.CardPanel(24);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(32, 44, 28, 44));
        card.setPreferredSize(new Dimension(400, 580));
        card.setMaximumSize(new Dimension(400, 580));
        // Logo
        ModernUI.LogoPanel logo = new ModernUI.LogoPanel();
        logo.setAlignmentX(CENTER_ALIGNMENT);
        
        // Título
        JLabel titulo = new JLabel("Z-ONE MUSIC");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(PRIMARY);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        // Subtítulo
        JLabel subtitulo = new JLabel("Entra en tu sonido");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(TEXT_MUTED);
        subtitulo.setAlignmentX(CENTER_ALIGNMENT);

        // Campos
        txtUsername = new ModernUI.RoundedTextField("Tu usuario");
        txtUsername.setMaximumSize(new Dimension(340, 48));
        txtUsername.setAlignmentX(LEFT_ALIGNMENT);

        txtPassword = new ModernUI.RoundedPasswordField("Tu contraseña");
        txtPassword.setMaximumSize(new Dimension(340, 48));
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);

        // Botones
        btnLogin = new ModernUI.RoundedButton("Iniciar sesión", true);
        btnLogin.setMaximumSize(new Dimension(340, 50));
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);

        btnRegistrar = new ModernUI.RoundedButton("¿No tienes cuenta? Regístrate", false);
        btnRegistrar.setMaximumSize(new Dimension(340, 36));
        btnRegistrar.setAlignmentX(CENTER_ALIGNMENT);

        // Estado
        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(ACCENT_PINK);
        lblEstado.setAlignmentX(CENTER_ALIGNMENT);

        // Conexión
        JPanel conexionPanel = new JPanel();
        conexionPanel.setOpaque(false);
        conexionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 0));
        conexionPanel.setAlignmentX(CENTER_ALIGNMENT);
        conexionPanel.setMaximumSize(new Dimension(340, 24));
        dotConexion = new ModernUI.StatusDot();
        lblConexion = new JLabel("Verificando conexión...");
        lblConexion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblConexion.setForeground(TEXT_MUTED);
        conexionPanel.add(dotConexion);
        conexionPanel.add(lblConexion);

        // Ensamblado tarjeta
        card.add(logo);
        card.add(Box.createVerticalStrut(10));
        card.add(titulo);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitulo);
        card.add(Box.createVerticalStrut(10));
        card.add(formLabel("USUARIO"));
        card.add(Box.createVerticalStrut(8));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(18));
        card.add(formLabel("CONTRASEÑA"));
        card.add(Box.createVerticalStrut(8));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(26));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(8));
        card.add(btnRegistrar);
        card.add(Box.createVerticalStrut(14));
        card.add(lblEstado);
        card.add(Box.createVerticalStrut(4));
        card.add(conexionPanel);

        // ── Ensamblado wrapper ────────────────────────────────────────
        g.gridx = 0; g.gridy = 0; g.weightx = 0; g.weighty = 1;
        wrapper.add(leftPanel, g);

        g.gridx = 1; g.weightx = 0;
        wrapper.add(sep, g);

        g.gridx = 2; g.weightx = 0;
        wrapper.add(card, g);

        GridBagConstraints rootGbc = new GridBagConstraints();
        root.add(wrapper, rootGbc);
        setContentPane(root);

        // ── Eventos ───────────────────────────────────────────────────
        btnLogin.addActionListener(e -> accionLogin());
        btnRegistrar.addActionListener(e ->
            new RegistroDialog(this, usuarioService).setVisible(true));

        KeyAdapter teclas = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) accionLogin();
                else mascotPanel.triggerExcited();
            }
        };
        txtUsername.addKeyListener(teclas);
        txtPassword.addKeyListener(teclas);
    }

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
                    mostrarEstado("¡Bienvenido, " + u.getNombre() + "!", SUCCESS);
                    Timer t = new Timer(800, ev -> {
                        try {
                            dispose();
                            new MainFrame(u).setVisible(true);
                        } catch (Exception ex) {
                            ex.printStackTrace();
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