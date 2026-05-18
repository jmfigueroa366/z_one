package view;

import services.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class RegistroDialog extends JDialog {

    private static final Color BG_DARK       = new Color(15, 15, 35);
    private static final Color BG_INPUT      = new Color(26, 26, 62);
    private static final Color COLOR_PRIMARY = new Color(124, 58, 237);
    private static final Color COLOR_ACCENT  = new Color(233, 69, 96);
    private static final Color TEXT_PRIMARY  = new Color(241, 245, 249);
    private static final Color TEXT_MUTED    = new Color(148, 163, 184);
    private static final Color BORDER_COLOR  = new Color(79, 70, 229, 100);

    private JTextField     txtNombre;
    private JTextField     txtUsername;
    private JTextField     txtCorreo;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmar;
    private JComboBox<String> cmbRol;
    private JLabel         lblEstado;

    private final UsuarioService service;

    public RegistroDialog(Frame parent, UsuarioService service) {
        super(parent, "Registro — Z-One", true);
        this.service = service;
        inicializarUI();
    }

    private void inicializarUI() {
        setSize(400, 560);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setBackground(BG_DARK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(28, 36, 20, 36));

        // Titulo
        JLabel titulo = new JLabel("Crear cuenta nueva");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(TEXT_PRIMARY);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(22));

        // Campos
        txtNombre    = agregarCampo(panel, "Nombre completo");
        txtUsername  = agregarCampo(panel, "Nombre de usuario");
        txtCorreo    = agregarCampo(panel, "Correo electronico");
        txtPassword  = agregarCampoPass(panel, "Contrasena (minimo 4 caracteres)");
        txtConfirmar = agregarCampoPass(panel, "Confirmar contrasena");

        // Rol
        panel.add(etiqueta("Rol en la plataforma"));
        panel.add(Box.createVerticalStrut(6));
        cmbRol = new JComboBox<>(new String[]{"ARTISTA", "PRODUCTOR", "USUARIO"});
        cmbRol.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbRol.setBackground(BG_INPUT);
        cmbRol.setForeground(TEXT_PRIMARY);
        cmbRol.setMaximumSize(new Dimension(328, 42));
        cmbRol.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(cmbRol);
        panel.add(Box.createVerticalStrut(22));

        // Boton guardar
        JButton btnGuardar = new JButton("Crear cuenta");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(COLOR_PRIMARY);
        btnGuardar.setOpaque(true);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnGuardar.setMaximumSize(new Dimension(328, 46));
        btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> registrar());
        panel.add(btnGuardar);
        panel.add(Box.createVerticalStrut(12));

        // Boton cancelar
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancelar.setForeground(TEXT_MUTED);
        btnCancelar.setOpaque(false);
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setAlignmentX(LEFT_ALIGNMENT);
        btnCancelar.addActionListener(e -> dispose());
        panel.add(btnCancelar);
        panel.add(Box.createVerticalStrut(10));

        // Estado
        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(COLOR_ACCENT);
        lblEstado.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblEstado);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_DARK);
        setContentPane(scroll);
    }

    private void registrar() {
        String nombre    = txtNombre.getText().trim();
        String username  = txtUsername.getText().trim();
        String correo    = txtCorreo.getText().trim();
        String pass      = new String(txtPassword.getPassword());
        String confirmar = new String(txtConfirmar.getPassword());
        String rol       = (String) cmbRol.getSelectedItem();

        if (!pass.equals(confirmar)) {
            lblEstado.setText("Las contrasenas no coinciden.");
            lblEstado.setForeground(COLOR_ACCENT);
            return;
        }

        lblEstado.setText("Registrando usuario...");
        lblEstado.setForeground(TEXT_MUTED);

        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                service.registrar(username, pass, nombre, correo, rol);
                return null;
            }
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(RegistroDialog.this,
                        "Cuenta creada exitosamente!\nYa puedes iniciar sesion.",
                        "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    String msg = ex.getCause() != null
                        ? ex.getCause().getMessage() : ex.getMessage();
                    lblEstado.setText(msg);
                    lblEstado.setForeground(COLOR_ACCENT);
                }
            }
        }.execute();
    }

    private JTextField agregarCampo(JPanel p, String label) {
        p.add(etiqueta(label));
        p.add(Box.createVerticalStrut(6));
        JTextField c = new JTextField();
        estilizar(c);
        p.add(c);
        p.add(Box.createVerticalStrut(14));
        return c;
    }

    private JPasswordField agregarCampoPass(JPanel p, String label) {
        p.add(etiqueta(label));
        p.add(Box.createVerticalStrut(6));
        JPasswordField c = new JPasswordField();
        estilizar(c);
        p.add(c);
        p.add(Box.createVerticalStrut(14));
        return c;
    }

    private void estilizar(JTextField c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setForeground(TEXT_PRIMARY);
        c.setBackground(BG_INPUT);
        c.setCaretColor(COLOR_PRIMARY);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        c.setMaximumSize(new Dimension(328, 42));
        c.setAlignmentX(LEFT_ALIGNMENT);
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
}
