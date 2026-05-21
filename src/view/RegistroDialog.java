package view;

import services.UsuarioService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import static view.ModernUI.*;
/**
 * RegistroDialog — Formulario de creación de cuenta moderno.
 * Mantiene el mismo tema visual que LoginFrame (gradiente, tarjeta,
 * inputs redondeados, botón con hover).
 */
public class RegistroDialog extends JDialog {
    private ModernUI.RoundedTextField     txtNombre;
    private ModernUI.RoundedTextField     txtUsername;
    private ModernUI.RoundedTextField     txtCorreo;
    private ModernUI.RoundedPasswordField txtPassword;
    private ModernUI.RoundedPasswordField txtConfirmar;
    private JComboBox<String>             cmbRol;
    private JLabel                        lblEstado;

    private final UsuarioService service;

    public RegistroDialog(Frame parent, UsuarioService service) {
        super(parent, "Crear cuenta — Z-One", true);
        this.service = service;
        inicializarUI();
    }

    private void inicializarUI() {
        setSize(480, 760);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // ===== Panel raíz con gradiente =====
        ModernUI.GradientPanel root = new ModernUI.GradientPanel();
        root.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        // ===== Tarjeta central =====
        ModernUI.CardPanel card = new ModernUI.CardPanel(24);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(32, 40, 24, 40));
        card.setPreferredSize(new Dimension(400, 700));
        card.setMaximumSize(new Dimension(400, 700));

        // --- Encabezado ---
        JLabel titulo = new JLabel("Crear cuenta nueva");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXT_PRIMARY);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Únete a la familia Z-One");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(TEXT_MUTED);
        subtitulo.setAlignmentX(CENTER_ALIGNMENT);

        // --- Campos ---
        txtNombre    = nuevoCampo("Tu nombre completo");
        txtUsername  = nuevoCampo("Nombre de usuario único");
        txtCorreo    = nuevoCampo("correo@ejemplo.com");
        txtPassword  = nuevoPass("Mínimo 4 caracteres");
        txtConfirmar = nuevoPass("Repite tu contraseña");

        cmbRol = ModernUI.roundedCombo(new String[]{"ARTISTA", "PRODUCTOR", "USUARIO"});
        cmbRol.setMaximumSize(new Dimension(320, 44));
        cmbRol.setAlignmentX(LEFT_ALIGNMENT);

        // --- Botones ---
        ModernUI.RoundedButton btnGuardar  = new ModernUI.RoundedButton("Crear cuenta", true);
        btnGuardar.setMaximumSize(new Dimension(320, 48));
        btnGuardar.setAlignmentX(CENTER_ALIGNMENT);
        btnGuardar.addActionListener(e -> registrar());

        ModernUI.RoundedButton btnCancelar = new ModernUI.RoundedButton("Cancelar", false);
        btnCancelar.setMaximumSize(new Dimension(320, 36));
        btnCancelar.setAlignmentX(CENTER_ALIGNMENT);
        btnCancelar.addActionListener(e -> dispose());

        // --- Mensaje de estado ---
        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(ACCENT_PINK);
        lblEstado.setAlignmentX(CENTER_ALIGNMENT);
        // --- Ensamblado ---
        card.add(titulo);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitulo);
        card.add(Box.createVerticalStrut(22));
        agregarCampo(card, "NOMBRE COMPLETO", txtNombre);
        agregarCampo(card, "USUARIO",         txtUsername);
        agregarCampo(card, "CORREO",          txtCorreo);
        agregarCampo(card, "CONTRASEÑA",      txtPassword);
        agregarCampo(card, "CONFIRMAR",       txtConfirmar);

        card.add(formLabel("ROL EN LA PLATAFORMA"));
        card.add(Box.createVerticalStrut(8));
        card.add(cmbRol);
        card.add(Box.createVerticalStrut(24));

        card.add(btnGuardar);
        card.add(Box.createVerticalStrut(8));
        card.add(btnCancelar);
        card.add(Box.createVerticalStrut(12));
        card.add(lblEstado);

        // Scroll por si la pantalla del usuario es pequeña
        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        root.add(scroll, gbc);
        setContentPane(root);
    }

    // -------- helpers --------
    private ModernUI.RoundedTextField nuevoCampo(String placeholder) {
        ModernUI.RoundedTextField c = new ModernUI.RoundedTextField(placeholder);
        c.setMaximumSize(new Dimension(320, 44));
        c.setAlignmentX(LEFT_ALIGNMENT);
        return c;
    }
    private ModernUI.RoundedPasswordField nuevoPass(String placeholder) {
        ModernUI.RoundedPasswordField c = new ModernUI.RoundedPasswordField(placeholder);
        c.setMaximumSize(new Dimension(320, 44));
        c.setAlignmentX(LEFT_ALIGNMENT);
        return c;
    }
    private void agregarCampo(JPanel p, String label, JComponent input) {
        p.add(formLabel(label));
        p.add(Box.createVerticalStrut(8));
        p.add(input);
        p.add(Box.createVerticalStrut(14));
    }
    private void registrar() {
        String nombre    = txtNombre.getText().trim();
        String username  = txtUsername.getText().trim();
        String correo    = txtCorreo.getText().trim();
        String pass      = new String(txtPassword.getPassword());
        String confirmar = new String(txtConfirmar.getPassword());
        String rol       = (String) cmbRol.getSelectedItem();
        if (!pass.equals(confirmar)) {
            mostrarEstado("Las contraseñas no coinciden.", ACCENT_PINK);
            return;
        }
        mostrarEstado("Registrando usuario...", TEXT_MUTED);
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                service.registrar(username, pass, nombre, correo, rol);
                return null;
            }
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(RegistroDialog.this,
                        "¡Cuenta creada exitosamente!\nYa puedes iniciar sesión.",
                        "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    String msg = ex.getCause() != null
                        ? ex.getCause().getMessage() : ex.getMessage();
                    mostrarEstado(msg, ACCENT_PINK);
                }
            }
        }.execute();
    }

    private void mostrarEstado(String msg, Color color) {
        lblEstado.setText(msg);
        lblEstado.setForeground(color);
    }
}
