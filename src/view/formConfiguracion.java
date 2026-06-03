package view;

import model.Usuario;
import services.ConfiguracionService;
import util.SesionUsuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class formConfiguracion extends JPanel {

    // ── PALETA CLARA ──
    private static final Color BG_PAGE   = new Color(0xF0F2F8);
    private static final Color BG_CARD   = new Color(0xFFFFFF);
    private static final Color BG_FIELD  = new Color(0xFFFFFF);
    private static final Color COL_BRD   = new Color(0xE2E8F0);

    private static final Color C_BLUE    = new Color(0x3B82F6);
    private static final Color C_PINK    = new Color(0xEC4899);
    private static final Color C_PURPLE  = new Color(0x8B5CF6);
    private static final Color C_TEAL    = new Color(0x06B6D4);
    private static final Color C_AMBER   = new Color(0xD97706);
    private static final Color C_INDIGO  = new Color(0x6366F1);

    private static final Color TXT_PRI   = new Color(0x1E293B);
    private static final Color TXT_SEC   = new Color(0x64748B);
    private static final Color TXT_MUTED = new Color(0x94A3B8);

    private static final Font F_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font F_SUB   = new Font("Segoe UI", Font.BOLD, 10);
    private static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_LBL   = new Font("Segoe UI", Font.BOLD, 9);
    private static final Font F_CARD  = new Font("Segoe UI", Font.BOLD, 14);

    private final ConfiguracionService servicio = new ConfiguracionService();
    private Usuario usuario;

    private JTextField fNombreCompleto;
    private JTextField fCorreo;
    private JPasswordField fPassActual;
    private JPasswordField fPassNueva;
    private JPasswordField fPassConfirmar;

    public formConfiguracion() {
        this(SesionUsuario.get());
    }

    public formConfiguracion(Usuario usuario) {
        this.usuario = usuario != null ? usuario : SesionUsuario.get();
        if (this.usuario == null) {
            this.usuario = new Usuario(0, "—", "", "—", "Sin sesión",
                    Usuario.ROL_USUARIO, "USUARIO", false,
                    java.time.LocalDate.now(), null);
        }
        setOpaque(false);
        setBackground(BG_PAGE);
        setLayout(new BorderLayout());
        construirUI();
    }

    private void construirUI() {
        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        root.add(headerPanel());
        root.add(Box.createVerticalStrut(20));

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);

        grid.add(cardPerfil());
        grid.add(cardPassword());
        grid.add(cardTema());
        grid.add(cardBackup());

        root.add(grid);
        root.add(Box.createVerticalGlue());

        JScrollPane sp = new JScrollPane(root);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        add(sp, BorderLayout.CENTER);
    }

    // ── HEADER ──
    private JPanel headerPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(LEFT_ALIGNMENT);

        JLabel tit = mk("⚙  Configuración", F_TITLE, TXT_PRI);
        tit.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = mk("PERFIL  ·  SEGURIDAD  ·  TEMA  ·  BACKUP", F_SUB, C_TEAL);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        p.add(tit);
        p.add(Box.createVerticalStrut(4));
        p.add(sub);
        return p;
    }

    // ════════════════════════════════════════════════════════════════
    //  CARD 1: PERFIL
    // ════════════════════════════════════════════════════════════════
    private JComponent cardPerfil() {
        JPanel card = cardBase(C_BLUE);
        card.add(headerCard("👤  Perfil de usuario",
                "Actualiza tus datos personales", C_BLUE), BorderLayout.NORTH);

        JPanel body = bodyPanel();

        body.add(labelCampo("USERNAME", C_BLUE));
        body.add(Box.createVerticalStrut(4));
        JTextField fUser = campo(usuario.getUsername());
        fUser.setEditable(false);
        fUser.setForeground(TXT_MUTED);
        body.add(fUser);
        body.add(Box.createVerticalStrut(12));

        body.add(labelCampo("NOMBRE COMPLETO", C_BLUE));
        body.add(Box.createVerticalStrut(4));
        fNombreCompleto = campo(usuario.getNombreCompleto());
        body.add(fNombreCompleto);
        body.add(Box.createVerticalStrut(12));

        body.add(labelCampo("CORREO", C_BLUE));
        body.add(Box.createVerticalStrut(4));
        fCorreo = campo(usuario.getCorreo());
        body.add(fCorreo);
        body.add(Box.createVerticalStrut(14));

        JButton bGuardar = btnPrimary("💾  Guardar cambios", C_BLUE, e -> guardarPerfil());
        bGuardar.setAlignmentX(LEFT_ALIGNMENT);
        body.add(bGuardar);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private void guardarPerfil() {
        String nombre = fNombreCompleto.getText().trim();
        String correo = fCorreo.getText().trim();
        if (nombre.isBlank()) { MainFrame.showToast("Nombre obligatorio", MainFrame.ToastType.ERROR); return; }
        if (correo.isBlank() || !correo.contains("@")) { MainFrame.showToast("Correo inválido", MainFrame.ToastType.ERROR); return; }
        try {
            if (servicio.actualizarPerfil(usuario.getIdUsuario(), correo, nombre)) {
                usuario.setNombreCompleto(nombre);
                usuario.setCorreo(correo);
                MainFrame.showToast("Perfil actualizado correctamente", MainFrame.ToastType.SUCCESS);
            }
        } catch (Exception ex) {
            MainFrame.showToast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  CARD 2: PASSWORD
    // ════════════════════════════════════════════════════════════════
    private JComponent cardPassword() {
        JPanel card = cardBase(C_PINK);
        card.add(headerCard("🔒  Cambiar contraseña",
                "Actualiza tu contraseña de acceso", C_PINK), BorderLayout.NORTH);

        JPanel body = bodyPanel();

        body.add(labelCampo("CONTRASEÑA ACTUAL", C_PINK));
        body.add(Box.createVerticalStrut(4));
        fPassActual = passField();
        body.add(fPassActual);
        body.add(Box.createVerticalStrut(12));

        body.add(labelCampo("CONTRASEÑA NUEVA", C_PINK));
        body.add(Box.createVerticalStrut(4));
        fPassNueva = passField();
        body.add(fPassNueva);
        body.add(Box.createVerticalStrut(12));

        body.add(labelCampo("CONFIRMAR CONTRASEÑA", C_PINK));
        body.add(Box.createVerticalStrut(4));
        fPassConfirmar = passField();
        body.add(fPassConfirmar);
        body.add(Box.createVerticalStrut(14));

        JButton bCambiar = btnPrimary("🔐  Cambiar contraseña", C_PINK, e -> cambiarPassword());
        bCambiar.setAlignmentX(LEFT_ALIGNMENT);
        body.add(bCambiar);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private void cambiarPassword() {
        String actual = new String(fPassActual.getPassword());
        String nueva  = new String(fPassNueva.getPassword());
        String conf   = new String(fPassConfirmar.getPassword());
        if (actual.isBlank() || nueva.isBlank() || conf.isBlank()) { MainFrame.showToast("Todos los campos son obligatorios", MainFrame.ToastType.ERROR); return; }
        if (nueva.length() < 6) { MainFrame.showToast("La nueva contraseña debe tener al menos 6 caracteres", MainFrame.ToastType.ERROR); return; }
        if (!nueva.equals(conf)) { MainFrame.showToast("Las contraseñas no coinciden", MainFrame.ToastType.ERROR); return; }
        try {
            if (servicio.cambiarPassword(usuario.getIdUsuario(), actual, nueva)) {
                MainFrame.showToast("Contraseña actualizada correctamente", MainFrame.ToastType.SUCCESS);
                fPassActual.setText(""); fPassNueva.setText(""); fPassConfirmar.setText("");
            }
        } catch (Exception ex) {
            MainFrame.showToast(ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  CARD 3: TEMA
    // ════════════════════════════════════════════════════════════════
    private JComponent cardTema() {
        JPanel card = cardBase(C_PURPLE);
        card.add(headerCard("🎨  Tema de la interfaz",
                "Personaliza la apariencia visual", C_PURPLE), BorderLayout.NORTH);

        JPanel body = bodyPanel();

        body.add(labelCampo("TEMA ACTUAL", C_PURPLE));
        body.add(Box.createVerticalStrut(8));

        String temaActual = servicio.temaActual();
        ButtonGroup grupo = new ButtonGroup();
        JRadioButton rbOscuro = radio("🌙  Tema oscuro (recomendado)", "oscuro".equals(temaActual));
        JRadioButton rbClaro  = radio("☀  Tema claro",                 "claro".equals(temaActual));
        JRadioButton rbAuto   = radio("🔄  Automático (según hora)",    "auto".equals(temaActual));
        grupo.add(rbOscuro); grupo.add(rbClaro); grupo.add(rbAuto);

        for (JRadioButton rb : new JRadioButton[]{rbOscuro, rbClaro, rbAuto}) {
            rb.setAlignmentX(LEFT_ALIGNMENT);
            body.add(rb);
            body.add(Box.createVerticalStrut(6));
        }

        body.add(Box.createVerticalStrut(6));
        JLabel info = mk("La aplicación está optimizada para el tema oscuro.",
                new Font("Segoe UI", Font.ITALIC, 10), TXT_MUTED);
        info.setAlignmentX(LEFT_ALIGNMENT);
        body.add(info);
        body.add(Box.createVerticalStrut(10));

        JButton bAplicar = btnPrimary("✓  Aplicar tema", C_PURPLE, e -> {
            String t = rbOscuro.isSelected() ? "oscuro" : rbClaro.isSelected() ? "claro" : "auto";
            servicio.guardarTema(t);
            MainFrame.showToast("claro".equals(t)
                    ? "Tema claro guardado. Reinicia para aplicar."
                    : "Tema " + t + " aplicado",
                    "claro".equals(t) ? MainFrame.ToastType.INFO : MainFrame.ToastType.SUCCESS);
        });
        bAplicar.setAlignmentX(LEFT_ALIGNMENT);
        body.add(bAplicar);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // ════════════════════════════════════════════════════════════════
    //  CARD 4: BACKUP
    // ════════════════════════════════════════════════════════════════
    private JComponent cardBackup() {
        JPanel card = cardBase(C_TEAL);
        card.add(headerCard("💾  Backup y exportación",
                "Exporta los datos de Oracle", C_TEAL), BorderLayout.NORTH);

        JPanel body = bodyPanel();

        // CSV
        JLabel l1 = mk("📊  EXPORTAR A CSV", F_LBL, C_TEAL);
        l1.setAlignmentX(LEFT_ALIGNMENT);
        body.add(l1);
        body.add(Box.createVerticalStrut(4));
        JLabel d1 = mk("Genera un archivo .csv por cada tabla (abrible en Excel)",
                new Font("Segoe UI", Font.PLAIN, 11), TXT_SEC);
        d1.setAlignmentX(LEFT_ALIGNMENT);
        body.add(d1);
        body.add(Box.createVerticalStrut(8));
        JButton bCSV = btnOutline("📁  Exportar CSV", C_TEAL, e -> exportarCSV());
        bCSV.setAlignmentX(LEFT_ALIGNMENT);
        body.add(bCSV);

        body.add(Box.createVerticalStrut(16));

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(COL_BRD);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        body.add(sep);
        body.add(Box.createVerticalStrut(16));

        // HTML/PDF
        JLabel l2 = mk("📄  INFORME PDF/HTML", F_LBL, C_AMBER);
        l2.setAlignmentX(LEFT_ALIGNMENT);
        body.add(l2);
        body.add(Box.createVerticalStrut(4));
        JLabel d2 = mk("Informe profesional con estadísticas y tablas",
                new Font("Segoe UI", Font.PLAIN, 11), TXT_SEC);
        d2.setAlignmentX(LEFT_ALIGNMENT);
        body.add(d2);
        body.add(Box.createVerticalStrut(8));
        JButton bHTML = btnOutline("📋  Generar informe", C_AMBER, e -> exportarHTML());
        bHTML.setAlignmentX(LEFT_ALIGNMENT);
        body.add(bHTML);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private void exportarCSV() {
        new Thread(() -> {
            try {
                String ruta = servicio.exportarCSV();
                SwingUtilities.invokeLater(() -> { MainFrame.showToast("CSV exportados correctamente", MainFrame.ToastType.SUCCESS); abrirCarpeta(ruta); });
            } catch (Exception ex) { ex.printStackTrace(); SwingUtilities.invokeLater(() -> MainFrame.showToast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR)); }
        }).start();
    }

    private void exportarHTML() {
        new Thread(() -> {
            try {
                String ruta = servicio.exportarInformeHTML();
                SwingUtilities.invokeLater(() -> { MainFrame.showToast("Informe HTML generado", MainFrame.ToastType.SUCCESS); abrirArchivo(ruta); });
            } catch (Exception ex) { ex.printStackTrace(); SwingUtilities.invokeLater(() -> MainFrame.showToast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR)); }
        }).start();
    }

    private void abrirCarpeta(String ruta) {
        try { Desktop.getDesktop().open(new File(ruta)); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Archivos guardados en:\n" + ruta, "Z-One", JOptionPane.INFORMATION_MESSAGE); }
    }

    private void abrirArchivo(String ruta) {
        try { Desktop.getDesktop().browse(new File(ruta).toURI()); }
        catch (Exception ex) {
            try { Desktop.getDesktop().open(new File(ruta)); }
            catch (Exception ex2) { JOptionPane.showMessageDialog(this, "Informe guardado en:\n" + ruta, "Z-One", JOptionPane.INFORMATION_MESSAGE); }
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS DE UI
    // ════════════════════════════════════════════════════════════════

    private JPanel cardBase(Color acento) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                // Fondo blanco
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Borde suave
                g2.setColor(COL_BRD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                // Línea de acento superior
                g2.setColor(acento);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(14, 1, getWidth() - 14, 1);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    private JComponent headerCard(String titulo, String sub, Color color) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(14, 18, 8, 18));

        JLabel tit = mk(titulo, F_CARD, TXT_PRI);
        tit.setAlignmentX(LEFT_ALIGNMENT);

        JLabel s = mk(sub.toUpperCase(), new Font("Segoe UI", Font.BOLD, 9), color);
        s.setAlignmentX(LEFT_ALIGNMENT);

        p.add(tit);
        p.add(Box.createVerticalStrut(2));
        p.add(s);

        // Separador de color
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        sep.setOpaque(false);
        sep.setPreferredSize(new Dimension(0, 1));

        JPanel w = new JPanel(new BorderLayout());
        w.setOpaque(false);
        w.add(p, BorderLayout.CENTER);
        w.add(sep, BorderLayout.SOUTH);
        return w;
    }

    private JPanel bodyPanel() {
        JPanel b = new JPanel();
        b.setOpaque(false);
        b.setLayout(new BoxLayout(b, BoxLayout.Y_AXIS));
        b.setBorder(new EmptyBorder(14, 18, 14, 18));
        return b;
    }

    private JLabel labelCampo(String texto, Color color) {
        JLabel l = mk(texto, F_LBL, color);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JTextField campo(String valor) {
        JTextField f = new JTextField(valor);
        f.setBackground(BG_FIELD);
        f.setForeground(TXT_PRI);
        f.setCaretColor(C_INDIGO);
        f.setFont(F_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COL_BRD, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(LEFT_ALIGNMENT);
        return f;
    }

    private JPasswordField passField() {
        JPasswordField f = new JPasswordField();
        f.setBackground(BG_FIELD);
        f.setForeground(TXT_PRI);
        f.setCaretColor(C_INDIGO);
        f.setFont(F_BODY);
        f.setEchoChar('●');
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COL_BRD, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(LEFT_ALIGNMENT);
        return f;
    }

    private JRadioButton radio(String texto, boolean selected) {
        JRadioButton r = new JRadioButton(texto, selected);
        r.setOpaque(true);
        r.setBackground(BG_FIELD);
        r.setForeground(TXT_PRI);
        r.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        r.setFocusPainted(false);
        r.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COL_BRD, 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return r;
    }

    /** Botón sólido (guardar, cambiar) */
    private JButton btnPrimary(String txt, Color color, java.awt.event.ActionListener a) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(a);
        return b;
    }

    /** Botón con borde de color (exportar, generar) */
    private JButton btnOutline(String txt, Color color, java.awt.event.ActionListener a) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(color);
        b.setBackground(BG_FIELD);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 120), 1),
                BorderFactory.createEmptyBorder(7, 15, 7, 15)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(a);
        return b;
    }

    private static JLabel mk(String t, Font f, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    private static Graphics2D g2d(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }
}
