package view;

import model.Usuario;
import services.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private static final Color BG_DARK       = new Color(15, 15, 35);
    private static final Color BG_INPUT      = new Color(26, 26, 62);
    private static final Color COLOR_PRIMARY = new Color(124, 58, 237);
    private static final Color COLOR_ACCENT  = new Color(233, 69, 96);
    private static final Color TEXT_PRIMARY  = new Color(241, 245, 249);
    private static final Color TEXT_MUTED    = new Color(148, 163, 184);
    private static final Color BORDER_COLOR  = new Color(79, 70, 229, 100);

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin;
    private JButton        btnRegistrar;
    private JLabel         lblEstado;
    private JLabel         lblConexion;

    private final UsuarioService usuarioService = new UsuarioService();

    public LoginFrame() {
        inicializarUI();
        verificarConexion();
    }

    private void inicializarUI() {
        setTitle("Z-One Music — Login");
        setSize(420, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setBackground(BG_DARK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(36, 44, 24, 44));

        // Titulo
        JLabel titulo = new JLabel("🎵  Z-ONE MUSIC");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(COLOR_PRIMARY);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Inicia sesion para continuar");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(TEXT_MUTED);
        subtitulo.setAlignmentX(CENTER_ALIGNMENT);

        // Campos
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        estilizar(txtUsername);
        estilizar(txtPassword);

        // Botones
        btnLogin     = botonPrimario("Iniciar Sesion");
        btnRegistrar = botonLink("No tienes cuenta? Registrate");

        // Estado
        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(COLOR_ACCENT);
        lblEstado.setAlignmentX(CENTER_ALIGNMENT);

        // Indicador conexion Oracle
        lblConexion = new JLabel("Verificando conexion...");
        lblConexion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblConexion.setForeground(TEXT_MUTED);
        lblConexion.setAlignmentX(CENTER_ALIGNMENT);

        // Ensamblar
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitulo);
        panel.add(Box.createVerticalStrut(32));
        panel.add(etiqueta("Usuario"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtUsername);
        panel.add(Box.createVerticalStrut(16));
        panel.add(etiqueta("Contrasena"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtPassword);
        panel.add(Box.createVerticalStrut(26));
        panel.add(btnLogin);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnRegistrar);
        panel.add(Box.createVerticalStrut(18));
        panel.add(lblEstado);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblConexion);

        setContentPane(panel);

        // Eventos
        btnLogin.addActionListener(e -> accionLogin());
        btnRegistrar.addActionListener(e ->
            new RegistroDialog(this, usuarioService).setVisible(true));

        KeyAdapter enterKey = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) accionLogin();
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);
    }

    private void accionLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            mostrarEstado("Completa todos los campos.", COLOR_ACCENT);
            return;
        }

        mostrarEstado("Verificando...", TEXT_MUTED);
        btnLogin.setEnabled(false);

        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            protected Usuario doInBackground() throws Exception {
                return usuarioService.login(user, pass);
            }
            protected void done() {
                btnLogin.setEnabled(true);
                try {
                    Usuario u = get();
                    mostrarEstado("Bienvenido, " + u.getNombre() + "!", new Color(34, 197, 94));
                    Timer t = new Timer(800, ev -> {
                        dispose();
                        // ==================================================
                        // AQUI ABRE TU MENU PRINCIPAL cuando lo tengas listo:
                        // new MenuPrincipal(u).setVisible(true);
                        // ==================================================
                        JOptionPane.showMessageDialog(null,
                            "Login exitoso!\n\nUsuario : " + u.getUsername() +
                            "\nNombre  : " + u.getNombre() +
                            "\nRol     : " + u.getRol(),
                            "Z-One — Bienvenido",
                            JOptionPane.INFORMATION_MESSAGE);
                    });
                    t.setRepeats(false);
                    t.start();
                } catch (Exception ex) {
                    String msg = ex.getCause() != null
                        ? ex.getCause().getMessage() : ex.getMessage();
                    mostrarEstado(msg, COLOR_ACCENT);
                    txtPassword.setText("");
                    txtPassword.requestFocus();
                }
            }
        };
        worker.execute();
    }

    private void verificarConexion() {
        new SwingWorker<Boolean, Void>() {
            protected Boolean doInBackground() {
                return usuarioService.hayConexion();
            }
            protected void done() {
                try {
                    if (get()) {
                        lblConexion.setText("Oracle conectado correctamente");
                        lblConexion.setForeground(new Color(34, 197, 94));
                    } else {
                        lblConexion.setText("Sin conexion — revisa ConexionDB.java");
                        lblConexion.setForeground(COLOR_ACCENT);
                    }
                } catch (Exception e) {
                    lblConexion.setText("Error de conexion");
                    lblConexion.setForeground(COLOR_ACCENT);
                }
            }
        }.execute();
    }

    private void mostrarEstado(String msg, Color color) {
        lblEstado.setText(msg);
        lblEstado.setForeground(color);
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private void estilizar(JTextField c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.setForeground(TEXT_PRIMARY);
        c.setBackground(BG_INPUT);
        c.setCaretColor(COLOR_PRIMARY);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        c.setMaximumSize(new Dimension(340, 44));
        c.setAlignmentX(LEFT_ALIGNMENT);
    }

    private JButton botonPrimario(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(COLOR_PRIMARY);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        b.setMaximumSize(new Dimension(340, 46));
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton botonLink(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setForeground(COLOR_PRIMARY);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
