    package view;

    import model.Usuario;

    import javax.swing.*;
    import javax.swing.border.EmptyBorder;
    import java.awt.*;
    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;
    import java.util.LinkedHashMap;
    import java.util.Map;

    import static view.ModernUI.*;

    public class MainFrame extends JFrame {

        public enum ToastType { SUCCESS, INFO, ERROR }

        private final Usuario usuarioActual;
        private JPanel        contentPanel;
        private CardLayout    cardLayout;
        private SidebarItem   activeItem;
        private JLayeredPane  layeredPane;

        // Referencia estática para que cualquier panel pueda mostrar toasts
        private static MainFrame instance;

        public MainFrame(Usuario usuario) {
            this.usuarioActual = usuario;
            instance = this;
            inicializarUI();
        }

        // =================================================================
        // UI PRINCIPAL
        // =================================================================
        private void inicializarUI() {
            setTitle("Z-One Music — " + usuarioActual.getNombreCompleto());
            setSize(1280, 780);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setMinimumSize(new Dimension(1100, 680));

            layeredPane = new JLayeredPane();
            layeredPane.setLayout(new BorderLayout());

            ModernUI.GradientPanel root = new ModernUI.GradientPanel();
            root.setLayout(new BorderLayout());

            // Sidebar
            root.add(construirSidebar(), BorderLayout.WEST);

            // Área de contenido con CardLayout
            cardLayout   = new CardLayout();
            contentPanel = new JPanel(cardLayout);
            contentPanel.setOpaque(false);
            contentPanel.setBorder(new EmptyBorder(24, 24, 24, 24));
contentPanel.add(new DashboardPanel(usuarioActual), "dashboard");
contentPanel.add(new formArtista(),                 "artistas");
contentPanel.add(new formProductor(),               "productores");
contentPanel.add(new formCancion(),                 "canciones");
contentPanel.add(new formSesion(),                  "sesiones");
contentPanel.add(new formCabina(),                  "cabinas");
contentPanel.add(new formEvento(),                  "eventos");
contentPanel.add(new formColaboracion(),            "colaboraciones");
contentPanel.add(new formCalendario(),              "calendario");
contentPanel.add(new Jesusitochatview(),            "asistente");
contentPanel.add(new formConfiguracion(usuarioActual), "configuracion");
            root.add(contentPanel,         BorderLayout.CENTER);
            root.add(construirStatusBar(), BorderLayout.SOUTH);

            setContentPane(root);
        }

        // =================================================================
        // SIDEBAR
        // =================================================================
        private JPanel construirSidebar() {
            JPanel sidebar = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(13, 13, 30, 220));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(new Color(139, 92, 246, 60));
                    g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                    g2.dispose();
                }
            };
            sidebar.setOpaque(false);
            sidebar.setPreferredSize(new Dimension(240, 0));
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBorder(new EmptyBorder(28, 0, 20, 0));

            // ---- Logo ----
            JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            logoPanel.setOpaque(false);
            logoPanel.setMaximumSize(new Dimension(240, 60));
            logoPanel.setBorder(new EmptyBorder(0, 24, 0, 0));

            ModernUI.LogoPanel logo = new ModernUI.LogoPanel();
            logo.setPreferredSize(new Dimension(34, 34));

            JLabel txtLogo = new JLabel("z_one");
            txtLogo.setFont(new Font("Consolas", Font.BOLD, 22));
            txtLogo.setForeground(new Color(34, 197, 94));

            logoPanel.add(logo);
            logoPanel.add(txtLogo);
            sidebar.add(logoPanel);
            sidebar.add(Box.createVerticalStrut(36));

            // ---- Items del menú ----
Map<String, String> items = new LinkedHashMap<>();
items.put("Dashboard",      "dashboard");
items.put("Artistas",       "artistas");
items.put("Productores",    "productores");
items.put("Canciones",      "canciones");
items.put("Sesiones",       "sesiones");
items.put("Cabinas",        "cabinas");
items.put("Eventos",        "eventos");
items.put("Colaboraciones", "colaboraciones");
items.put("Calendario",     "calendario");
items.put("Asistente",      "asistente");
items.put("Configuración",  "configuracion");
            for (Map.Entry<String, String> e : items.entrySet()) {
                SidebarItem item = new SidebarItem(e.getKey(), e.getValue());
                if (e.getValue().equals("dashboard")) {
                    item.setActive(true);
                    activeItem = item;
                }
                item.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent ev) {
                        if (activeItem != null) activeItem.setActive(false);
                        item.setActive(true);
                        activeItem = item;
                        cardLayout.show(contentPanel, e.getValue());
                    }
                });
                sidebar.add(item);
                sidebar.add(Box.createVerticalStrut(4));
            }

            sidebar.add(Box.createVerticalGlue());

            // ---- Info usuario ----
            JPanel userInfo = new JPanel();
            userInfo.setOpaque(false);
            userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
            userInfo.setBorder(new EmptyBorder(12, 24, 12, 24));
            userInfo.setMaximumSize(new Dimension(240, 80));

            JLabel lblNombre = new JLabel(usuarioActual.getNombreCompleto());
            lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblNombre.setForeground(TEXT_PRIMARY);
            lblNombre.setAlignmentX(LEFT_ALIGNMENT);

            JLabel lblRol    = new JLabel(usuarioActual.getNombreRol());
            lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblRol.setForeground(PRIMARY_LIGHT);
            lblRol.setAlignmentX(LEFT_ALIGNMENT);

            userInfo.add(lblNombre);
            userInfo.add(lblRol);
            sidebar.add(userInfo);

            // ---- Botón cerrar sesión ----
            ModernUI.RoundedButton btnLogout = new ModernUI.RoundedButton("Cerrar sesión", false);
            btnLogout.setMaximumSize(new Dimension(200, 36));
            btnLogout.setAlignmentX(CENTER_ALIGNMENT);
            btnLogout.addActionListener(ev -> {
                int op = JOptionPane.showConfirmDialog(this,
                    "¿Cerrar sesión actual?", "Z-One",
                    JOptionPane.YES_NO_OPTION);
                if (op == JOptionPane.YES_OPTION) {
                    dispose();
                    SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
                }
            });
            sidebar.add(btnLogout);
            sidebar.add(Box.createVerticalStrut(8));

            return sidebar;
        }

        // =================================================================
        // STATUS BAR
        // =================================================================
        private JPanel construirStatusBar() {
            JPanel bar = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0, 0, 0, 100));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(new Color(139, 92, 246, 50));
                    g2.fillRect(0, 0, getWidth(), 1);
                    g2.dispose();
                }
            };
            bar.setOpaque(false);
            bar.setPreferredSize(new Dimension(0, 28));
            bar.setBorder(new EmptyBorder(0, 24, 0, 24));

            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            left.setOpaque(false);

            ModernUI.StatusDot dot = new ModernUI.StatusDot();
            dot.setColor(SUCCESS);

            JLabel txt = new JLabel("Oracle conectado · Z-One v1.0");
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            txt.setForeground(TEXT_MUTED);

            left.add(dot);
            left.add(txt);
    JLabel right = new JLabel("Sesión activa · " + usuarioActual.getUsername());
            right.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            right.setForeground(TEXT_MUTED);
            right.setBorder(new EmptyBorder(6, 0, 0, 0));

            bar.add(left,  BorderLayout.WEST);
            bar.add(right, BorderLayout.EAST);
            return bar;
        }

        // =================================================================
        // STUB PANEL
        // =================================================================
        private JPanel stubPanel(String titulo, String descripcion) {
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

            JLabel lbl1 = new JLabel(titulo);
            lbl1.setFont(new Font("Segoe UI", Font.BOLD, 26));
            lbl1.setForeground(TEXT_PRIMARY);
            lbl1.setAlignmentX(LEFT_ALIGNMENT);

            JLabel lbl2 = new JLabel(descripcion);
            lbl2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl2.setForeground(TEXT_MUTED);
            lbl2.setAlignmentX(LEFT_ALIGNMENT);

            ModernUI.CardPanel card = new ModernUI.CardPanel(16);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(new EmptyBorder(48, 48, 48, 48));
            card.setAlignmentX(LEFT_ALIGNMENT);
            card.setMaximumSize(new Dimension(900, 320));

            JLabel placeholder = new JLabel("Próximamente");
            placeholder.setFont(new Font("Segoe UI", Font.BOLD, 22));
            placeholder.setForeground(PRIMARY);
            placeholder.setAlignmentX(LEFT_ALIGNMENT);

            JLabel detalle = new JLabel("<html>Este módulo será implementado por el integrante asignado.<br>"
                    + "Reemplaza este stub por <b>" + titulo + "Panel.java</b> en MainFrame.</html>");
            detalle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            detalle.setForeground(TEXT_MUTED);
            detalle.setAlignmentX(LEFT_ALIGNMENT);

            ModernUI.RoundedButton btn = new ModernUI.RoundedButton("Probar acción de ejemplo", true);
            btn.setMaximumSize(new Dimension(240, 42));
            btn.setAlignmentX(LEFT_ALIGNMENT);
            btn.addActionListener(e -> showToast("Botón presionado en módulo " + titulo, ToastType.SUCCESS));

            card.add(placeholder);
            card.add(Box.createVerticalStrut(8));
            card.add(detalle);
            card.add(Box.createVerticalStrut(24));
            card.add(btn);

            p.add(lbl1);
            p.add(Box.createVerticalStrut(6));
            p.add(lbl2);
            p.add(Box.createVerticalStrut(24));
            p.add(card);
            p.add(Box.createVerticalGlue());
            return p;
        }

        // =================================================================
        // TOAST SYSTEM
        // =================================================================
        public static void showToast(String mensaje, ToastType tipo) {
            if (instance == null) return;
            instance.mostrarToastInterno(mensaje, tipo);
        }

        private void mostrarToastInterno(String mensaje, ToastType tipo) {
            Color  color;
            String icono;
            switch (tipo) {
                case SUCCESS: color = SUCCESS;     icono = "✓"; break;
                case ERROR:   color = ACCENT_PINK; icono = "✗"; break;
                default:      color = ACCENT_CYAN; icono = "i"; break;
            }

            JWindow toast = new JWindow(this);

            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(24, 24, 52, 245));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    g2.setColor(color);
                    g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                    g2.dispose();
                }
            };
            panel.setOpaque(false);
            panel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));
            panel.setBorder(new EmptyBorder(8, 16, 8, 20));

            JLabel ico = new JLabel(icono);
            ico.setFont(new Font("Segoe UI", Font.BOLD, 16));
            ico.setForeground(color);

            JLabel txt = new JLabel(mensaje);
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txt.setForeground(TEXT_PRIMARY);

            panel.add(ico);
            panel.add(txt);

            toast.setContentPane(panel);
            toast.pack();

            // ── Posicionar abajo-derecha de la ventana principal ──────────
            Point loc    = getLocationOnScreen();
            int   toastX = loc.x + getWidth()  - toast.getWidth()  - 24;
            int   toastY = loc.y + getHeight() - toast.getHeight() - 48;
            toast.setLocation(toastX, toastY);
            toast.setBackground(new Color(0, 0, 0, 0));
            toast.setVisible(true);

            Timer t = new Timer(2500, e -> toast.dispose());
            t.setRepeats(false);
            t.start();
        }

        // =================================================================
        // SIDEBAR ITEM
        // =================================================================
        private static class SidebarItem extends JPanel {
            private final String  label;
            private       boolean active = false;
            private       boolean hover  = false;

            public SidebarItem(String label, String key) {
                this.label = label;
                setOpaque(false);
                setPreferredSize(new Dimension(240, 44));
                setMaximumSize(new Dimension(240, 44));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }

            public void setActive(boolean active) {
                this.active = active;
                repaint();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);

                if (active) {
                    g2.setColor(new Color(139, 92, 246, 35));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(new Color(34, 197, 94));
                    g2.fillRect(0, 8, 3, getHeight() - 16);
                } else if (hover) {
                    g2.setColor(new Color(255, 255, 255, 12));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                g2.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
                g2.setColor(active ? TEXT_PRIMARY : (hover ? TEXT_PRIMARY : TEXT_SECONDARY));
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(label, 28, y);
                g2.dispose();
            }
        }

        // =================================================================
        // MAIN — prueba sin pasar por el login
        // =================================================================
        public static void main(String[] args) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Constructor: (int id, String username, String pass, String correo, String rol, boolean activo)
            Usuario test = new Usuario(
        1,                        // idUsuario
        "jesus",                  // username
        "hash123",                // passwordHash
        "jesus@zonemusic.com",    // correo
        "Jesús Pérez",            // nombreCompleto  ← faltaba este campo
        Usuario.ROL_PRODUCTOR,    // idRol (3)
        "PRODUCTOR",              // nombreRol
        true,                     // activo
        java.time.LocalDate.now(),// fechaRegistro
        null                      // ultimoLogin
    );

            SwingUtilities.invokeLater(() -> new MainFrame(test).setVisible(true));
        }
    }