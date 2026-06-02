package view;

import services.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * RegistroDialog — Versión unificada y corregida.
 *
 * CAMBIOS RESPECTO AL CÓDIGO ANTERIOR:
 *  - Se mantiene la UI moderna con animaciones, mascota y panel doble.
 *  - Se corrige el orden/tipo de parámetros de service.registrar() para
 *    que coincida con la firma original: registrar(username, pass, correo, nombre, rolId).
 *  - RolItem se recupera del código viejo para pasar el id (int) al servicio.
 *  - Se añaden guards de campos vacíos.
 *  - Se eliminan referencias a constantes de ModernUI que pueden no existir
 *    (ACCENT_PINK, ModernUI.SUCCESS) y se definen localmente para evitar errores de
 *    compilación si ModernUI no las exporta.
 *  - El resto de la UI (MascotPanel, RoundedTextField, etc.) sigue dependiendo
 *    de ModernUI — asegúrate de que esas clases existen en tu proyecto.
 */
public class RegistroDialog extends JDialog {

    // Colores usados directamente desde ModernUI

    // ── Campos del formulario ──────────────────────────────────────────
    private ModernUI.RoundedTextField     txtNombre;
    private ModernUI.RoundedTextField     txtUsername;
    private ModernUI.RoundedTextField     txtCorreo;
    private ModernUI.RoundedPasswordField txtPassword;
    private ModernUI.RoundedPasswordField txtConfirmar;
    private JComboBox<RolItem>            cmbRol;
    private JLabel                        lblEstado;
    private JLabel                        lblStrength;
    private JPanel                        strengthBar;

    // ── Animación de entrada ───────────────────────────────────────────
    private float animProgress = 0f;
    private Timer animTimer;

    // ── Mascota ────────────────────────────────────────────────────────
    private MascotPanel mascotPanel;

    private final UsuarioService service;

    // ── RolItem: muestra nombre, guarda id (igual que en el código original) ──
    private static class RolItem {
        final int    id;
        final String nombre;
        RolItem(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override public String toString() { return nombre; }
    }

    // =================================================================
    // CONSTRUCTOR
    // =================================================================
    public RegistroDialog(Frame parent, UsuarioService service) {
        super(parent, "Crear cuenta — Z-One", true);
        this.service = service;
        inicializarUI();
        iniciarAnimacion();
    }

    // =================================================================
    // ANIMACIÓN DE ENTRADA
    // =================================================================
    private void iniciarAnimacion() {
        animProgress = 0f;
        animTimer = new Timer(12, e -> {
            animProgress += 0.045f;
            if (animProgress >= 1f) {
                animProgress = 1f;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        animTimer.start();
    }

    // =================================================================
    // UI PRINCIPAL
    // =================================================================
    private void inicializarUI() {
        setMinimumSize(new Dimension(860, 600));
        setPreferredSize(new Dimension(980, 660));
        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel root = new JPanel(new GridLayout(1, 2, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                float ease = easeOutCubic(animProgress);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ease));
                g2.translate((int)((1f - ease) * 40), 0);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        root.setBackground(ModernUI.BG_DARK);

        root.add(construirPanelIzquierdo());
        root.add(construirPanelDerecho());

        setContentPane(root);
    }

    private float easeOutCubic(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    // =================================================================
    // PANEL IZQUIERDO — mascota + beneficios
    // =================================================================
    private JPanel construirPanelIzquierdo() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint bg = new GradientPaint(
                    0, 0,          new Color(8, 4, 26),
                    getWidth(), getHeight(), new Color(28, 16, 60));
                g2.setPaint(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());

                RadialGradientPaint glow1 = new RadialGradientPaint(
                    getWidth() * 0.5f, 0, getWidth() * 0.9f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(139, 92, 246, 70),
                                new Color(139, 92, 246, 0)});
                g2.setPaint(glow1);
                g2.fillRect(0, 0, getWidth(), getHeight() * 2 / 3);

                RadialGradientPaint glow2 = new RadialGradientPaint(
                    getWidth() * 0.4f, (float) getHeight(), getWidth() * 0.65f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(6, 182, 212, 50),
                                new Color(6, 182, 212, 0)});
                g2.setPaint(glow2);
                g2.fillRect(0, getHeight() / 2, getWidth(), getHeight() / 2);

                g2.setColor(new Color(139, 92, 246, 65));
                int[][] dots = {{22,30},{55,110},{18,210},{260,70},{280,190},
                                {38,440},{270,410},{140,520},{60,560}};
                for (int[] d : dots) g2.fillOval(d[0], d[1], 3, 3);
                g2.setColor(new Color(6, 182, 212, 45));
                int[][] dots2 = {{80,290},{240,340},{110,490},{320,470},{14,370}};
                for (int[] d : dots2) g2.fillOval(d[0], d[1], 2, 2);

                GradientPaint sep = new GradientPaint(
                    0, getHeight() * 0.15f, new Color(139, 92, 246, 0),
                    0, getHeight() * 0.5f,  new Color(139, 92, 246, 70));
                g2.setPaint(sep);
                g2.fillRect(getWidth() - 1, 0, 1, getHeight() / 2);
                GradientPaint sep2 = new GradientPaint(
                    0, getHeight() * 0.5f,  new Color(139, 92, 246, 70),
                    0, getHeight() * 0.85f, new Color(139, 92, 246, 0));
                g2.setPaint(sep2);
                g2.fillRect(getWidth() - 1, getHeight() / 2, 1, getHeight() / 2);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        mascotPanel = new MascotPanel();
        mascotPanel.setPreferredSize(new Dimension(420, 320));
        mascotPanel.setMinimumSize(new Dimension(300, 260));

        JPanel mascotWrapper = new JPanel(new GridBagLayout());
        mascotWrapper.setOpaque(false);
        mascotWrapper.setBorder(new EmptyBorder(30, 0, 0, 0));
        mascotWrapper.add(mascotPanel);

        JPanel beneficiosPanel = new JPanel();
        beneficiosPanel.setOpaque(false);
        beneficiosPanel.setLayout(new BoxLayout(beneficiosPanel, BoxLayout.Y_AXIS));
        beneficiosPanel.setBorder(new EmptyBorder(0, 28, 28, 28));

        JLabel titulo = new JLabel("ÚNETE A Z-ONE");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Tu estudio musical en un solo lugar");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(148, 163, 184));
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);

        beneficiosPanel.add(titulo);
        beneficiosPanel.add(Box.createVerticalStrut(6));
        beneficiosPanel.add(subtitulo);
        beneficiosPanel.add(Box.createVerticalStrut(18));
        beneficiosPanel.add(beneficioItem("🎵", "Gestiona tus artistas y productores"));
        beneficiosPanel.add(Box.createVerticalStrut(10));
        beneficiosPanel.add(beneficioItem("🎙", "Reserva sesiones de grabación"));
        beneficiosPanel.add(Box.createVerticalStrut(10));
        beneficiosPanel.add(beneficioItem("📊", "Estadísticas y rankings en tiempo real"));
        beneficiosPanel.add(Box.createVerticalStrut(10));
        beneficiosPanel.add(beneficioItem("🔐", "Tu información siempre segura"));

        panel.add(mascotWrapper,   BorderLayout.CENTER);
        panel.add(beneficiosPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel beneficioItem(String icon, String texto) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        p.setAlignmentX(LEFT_ALIGNMENT);

        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel txt = new JLabel(texto);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setForeground(new Color(203, 213, 225));

        p.add(ico);
        p.add(txt);
        return p;
    }

    // =================================================================
    // PANEL DERECHO — formulario
    // =================================================================
    private JPanel construirPanelDerecho() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(16, 14, 38));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(28, 36, 28, 36));

        // ── Encabezado ─────────────────────────────────────────────────
        JLabel titulo = new JLabel("Crear cuenta");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(ModernUI.TEXT_PRIMARY);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Completa el formulario para registrarte");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(ModernUI.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(139, 92, 246, 180),
                    getWidth() * 0.6f, 0, new Color(139, 92, 246, 0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        divider.setOpaque(false);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        divider.setPreferredSize(new Dimension(0, 2));
        divider.setAlignmentX(LEFT_ALIGNMENT);

        // ── Campos ─────────────────────────────────────────────────────
        txtNombre   = nuevoCampo("Tu nombre completo");
        txtUsername = nuevoCampo("Nombre de usuario único");
        JPanel fila1 = filaDos("NOMBRE COMPLETO", txtNombre, "USUARIO", txtUsername);

        txtCorreo = nuevoCampo("correo@ejemplo.com");
        txtCorreo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        txtCorreo.setAlignmentX(LEFT_ALIGNMENT);

        txtPassword  = nuevoPass("Mínimo 4 caracteres");
        txtConfirmar = nuevoPass("Repite tu contraseña");
        JPanel fila3 = filaDos("CONTRASEÑA", txtPassword, "CONFIRMAR", txtConfirmar);

        // ── Fortaleza de contraseña ────────────────────────────────────
        JPanel strengthWrapper = new JPanel();
        strengthWrapper.setOpaque(false);
        strengthWrapper.setLayout(new BoxLayout(strengthWrapper, BoxLayout.Y_AXIS));
        strengthWrapper.setAlignmentX(LEFT_ALIGNMENT);
        strengthWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JPanel barRow = new JPanel(new GridLayout(1, 4, 4, 0));
        barRow.setOpaque(false);
        barRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        barRow.setPreferredSize(new Dimension(0, 4));
        barRow.setAlignmentX(LEFT_ALIGNMENT);

        lblStrength = new JLabel(" ");
        lblStrength.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStrength.setForeground(ModernUI.TEXT_MUTED);
        lblStrength.setAlignmentX(LEFT_ALIGNMENT);

        strengthBar = barRow;
        actualizarFortaleza(0);

        strengthWrapper.add(barRow);
        strengthWrapper.add(Box.createVerticalStrut(4));
        strengthWrapper.add(lblStrength);

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String p = new String(txtPassword.getPassword());
                actualizarFortaleza(calcularFortaleza(p));
                mascotPanel.triggerExcited();
            }
        });

        // ── Combo de rol — usa RolItem igual que el código original ───
cmbRol = new JComboBox<>(new RolItem[]{
    new RolItem(1, "ADMINISTRADOR")
});
        cmbRol.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbRol.setBackground(ModernUI.INPUT_BG);
        cmbRol.setForeground(ModernUI.TEXT_PRIMARY);
        cmbRol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        cmbRol.setAlignmentX(LEFT_ALIGNMENT);

        // ── Botones ────────────────────────────────────────────────────
        ModernUI.RoundedButton btnGuardar = new ModernUI.RoundedButton("Crear mi cuenta", true);
        btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
        btnGuardar.addActionListener(e -> registrar());

        ModernUI.RoundedButton btnCancelar = new ModernUI.RoundedButton("Ya tengo cuenta — Iniciar sesión", false);
        btnCancelar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnCancelar.setAlignmentX(LEFT_ALIGNMENT);
        btnCancelar.addActionListener(e -> cerrarConAnimacion());

        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(ModernUI.ACCENT_PINK);
        lblEstado.setAlignmentX(LEFT_ALIGNMENT);

        // ── Ensamblado ─────────────────────────────────────────────────
        form.add(titulo);
        form.add(Box.createVerticalStrut(4));
        form.add(sub);
        form.add(Box.createVerticalStrut(12));
        form.add(divider);
        form.add(Box.createVerticalStrut(20));
        form.add(fila1);
        form.add(Box.createVerticalStrut(14));
        form.add(formLabel("CORREO ELECTRÓNICO"));
        form.add(Box.createVerticalStrut(7));
        form.add(txtCorreo);
        form.add(Box.createVerticalStrut(14));
        form.add(fila3);
        form.add(Box.createVerticalStrut(8));
        form.add(strengthWrapper);
        form.add(Box.createVerticalStrut(14));
        form.add(formLabel("ROL EN LA PLATAFORMA"));
        form.add(Box.createVerticalStrut(7));
        form.add(cmbRol);
        form.add(Box.createVerticalStrut(22));
        form.add(btnGuardar);
        form.add(Box.createVerticalStrut(10));
        form.add(btnCancelar);
        form.add(Box.createVerticalStrut(10));
        form.add(lblEstado);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // =================================================================
    // HELPERS
    // =================================================================
    private JPanel filaDos(String lbl1, JComponent c1, String lbl2, JComponent c2) {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        p.setPreferredSize(new Dimension(300, 72));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel l1 = formLabel(lbl1);
        l1.setAlignmentX(LEFT_ALIGNMENT);
        c1.setAlignmentX(LEFT_ALIGNMENT);
        c1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        c1.setPreferredSize(new Dimension(140, 44));
        left.add(l1);
        left.add(Box.createVerticalStrut(6));
        left.add(c1);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel l2 = formLabel(lbl2);
        l2.setAlignmentX(LEFT_ALIGNMENT);
        c2.setAlignmentX(LEFT_ALIGNMENT);
        c2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        c2.setPreferredSize(new Dimension(140, 44));
        right.add(l2);
        right.add(Box.createVerticalStrut(6));
        right.add(c2);

        p.add(left);
        p.add(right);
        return p;
    }

    private JLabel formLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(ModernUI.TEXT_MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private ModernUI.RoundedTextField nuevoCampo(String ph) {
        ModernUI.RoundedTextField c = new ModernUI.RoundedTextField(ph);
        c.setAlignmentX(LEFT_ALIGNMENT);
        return c;
    }

    private ModernUI.RoundedPasswordField nuevoPass(String ph) {
        ModernUI.RoundedPasswordField c = new ModernUI.RoundedPasswordField(ph);
        c.setAlignmentX(LEFT_ALIGNMENT);
        return c;
    }

    // =================================================================
    // FORTALEZA DE CONTRASEÑA
    // =================================================================
    private int calcularFortaleza(String p) {
        if (p.isEmpty()) return 0;
        int score = 0;
        if (p.length() >= 6)  score++;
        if (p.length() >= 10) score++;
        if (p.matches(".*[A-Z].*")) score++;
        if (p.matches(".*[0-9].*")) score++;
        if (p.matches(".*[^a-zA-Z0-9].*")) score++;
        return Math.min(score, 4);
    }

    private void actualizarFortaleza(int score) {
        strengthBar.removeAll();
        Color[] colors = {
            new Color(239, 68, 68),
            new Color(245, 158, 11),
            new Color(234, 179, 8),
            new Color(34, 197, 94)
        };
        String[] labels = {"", "Débil", "Regular", "Buena", "Fuerte"};
        Color dim = new Color(35, 32, 70);

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            JPanel seg = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                       RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(idx < score ? colors[score - 1] : dim);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 3, 3);
                    g2.dispose();
                }
            };
            seg.setOpaque(false);
            strengthBar.add(seg);
        }
        if (score > 0) {
            lblStrength.setText("Contraseña " + labels[score]);
            lblStrength.setForeground(colors[score - 1]);
        } else {
            lblStrength.setText(" ");
        }
        strengthBar.revalidate();
        strengthBar.repaint();
    }

    // =================================================================
    // ANIMACIÓN DE CIERRE
    // =================================================================
    private void cerrarConAnimacion() {
        Timer closeTimer = new Timer(12, null);
        float[] progress = {1f};
        closeTimer.addActionListener(e -> {
            progress[0] -= 0.06f;
            if (progress[0] <= 0) {
                closeTimer.stop();
                dispose();
            }
            repaint();
        });
        closeTimer.start();
    }

    // =================================================================
    // LÓGICA DE REGISTRO — firma corregida: (username, pass, correo, nombre, rolId)
    // =================================================================
    private void registrar() {
        String nombre    = txtNombre.getText().trim();
        String username  = txtUsername.getText().trim();
        String correo    = txtCorreo.getText().trim();
        String pass      = new String(txtPassword.getPassword());
        String confirmar = new String(txtConfirmar.getPassword());
        RolItem rol      = (RolItem) cmbRol.getSelectedItem();

        // ── Validaciones ──────────────────────────────────────────────
        if (nombre.isEmpty() || username.isEmpty() || correo.isEmpty()) {
            mostrarEstado("❌ Todos los campos son obligatorios.", ModernUI.ACCENT_PINK);
            return;
        }
        if (!correo.contains("@")) {
            mostrarEstado("❌ Correo electrónico inválido.", ModernUI.ACCENT_PINK);
            return;
        }
        if (pass.length() < 4) {
            mostrarEstado("❌ La contraseña debe tener al menos 4 caracteres.", ModernUI.ACCENT_PINK);
            return;
        }
        if (!pass.equals(confirmar)) {
            mostrarEstado("❌ Las contraseñas no coinciden.", ModernUI.ACCENT_PINK);
            mascotPanel.triggerExcited();
            return;
        }

        mostrarEstado("⏳ Registrando usuario...", ModernUI.TEXT_MUTED);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Orden correcto igual que en el código original:
                // registrar(username, pass, correo, nombre, rolId)
                service.registrar(username, pass, correo, nombre, rol.id);
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    mostrarEstado("✅ ¡Cuenta creada exitosamente!", ModernUI.SUCCESS);
                    mascotPanel.triggerExcited();
                    Timer t = new Timer(1200, ev -> dispose());
                    t.setRepeats(false);
                    t.start();
                } catch (Exception ex) {
                    String msg = ex.getCause() != null
                        ? ex.getCause().getMessage() : ex.getMessage();
                    mostrarEstado("❌ " + msg, ModernUI.ACCENT_PINK);
                    mascotPanel.triggerExcited();
                }
            }
        }.execute();
    }

    private void mostrarEstado(String msg, Color color) {
        lblEstado.setText(msg);
        lblEstado.setForeground(color);
    }
}