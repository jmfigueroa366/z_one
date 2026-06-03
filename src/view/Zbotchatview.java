package view;

import util.Chatcontroller;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class Zbotchatview extends JPanel {

    private JPanel      messagesPanel;
    private JScrollPane scrollPane;
    private JTextField  inputField;
    private JButton     sendButton;
    private JLabel      bubbleLabel;
    private Chatcontroller controller;

    // ── PALETA ────────────────────────────────────────────────────────
    private static final Color BG_CHAT      = new Color(250, 249, 255);
    private static final Color PURPLE_GRAD1 = new Color(127, 119, 221);
    private static final Color PURPLE_GRAD2 = new Color( 79,  70, 200);
    private static final Color PURPLE_DARK  = new Color(124,  58, 237);
    private static final Color PURPLE_MID   = new Color(162,  89, 247);
    private static final Color PURPLE_LIGHT = new Color(206, 203, 246);
    private static final Color PURPLE_XLIT  = new Color(238, 237, 254);
    private static final Color TEXT_DARK    = new Color( 60,  52, 137);
    private static final Color TEXT_PRI     = new Color( 38,  33,  92);
    private static final Color TEXT_SEC     = new Color(175, 169, 236);
    private static final Color WHITE        = Color.WHITE;
    private static final Color BUBBLE_BOT_BG= WHITE;
    private static final Color BUBBLE_BOT_BDR = new Color(230, 228, 250);
    private static final Color GREEN_DOT    = new Color( 74, 222, 128);
    private static final Color INPUT_BG     = new Color(245, 244, 255);

    // ── ANIMACIÓN ─────────────────────────────────────────────────────
    private float avatarY     = 0f;
    private float avatarDelta = 0.12f;
    private javax.swing.Timer timerFloat;
    private float alpha = 0f;
    private javax.swing.Timer timerFade;

    public Zbotchatview() {
        setLayout(new BorderLayout());
        setOpaque(false);
        controller = new Chatcontroller(this);
        construirUI();
        iniciarAnimaciones();
    }

    // ══════════════════════════════════════════════════════════════════
    //  ANIMACIONES
    // ══════════════════════════════════════════════════════════════════
    private void iniciarAnimaciones() {
        alpha = 0f;
        timerFade = new javax.swing.Timer(16, null);
        timerFade.addActionListener(e -> {
            alpha = Math.min(1f, alpha + 0.045f);
            repaint();
            if (alpha >= 1f) timerFade.stop();
        });
        timerFade.start();

        timerFloat = new javax.swing.Timer(18, null);
        timerFloat.addActionListener(e -> {
            avatarY += avatarDelta;
            if (avatarY > 5f || avatarY < -5f) avatarDelta = -avatarDelta;
            repaint();
        });
        timerFloat.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(BG_CHAT);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }

    // ══════════════════════════════════════════════════════════════════
    //  UI
    // ══════════════════════════════════════════════════════════════════
    private void construirUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.add(buildHeader(), BorderLayout.NORTH);
        main.add(buildChat(),   BorderLayout.CENTER);
        main.add(buildInput(),  BorderLayout.SOUTH);
        add(main, BorderLayout.CENTER);
    }

    // ── HEADER ────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0, 0, PURPLE_GRAD1, getWidth(), getHeight(), PURPLE_GRAD2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fillOval(getWidth() - 100, -30, 130, 130);
                g2.setColor(new Color(255, 255, 255, 8));
                g2.fillOval(getWidth() - 50, 20, 90, 90);
                // Línea separadora inferior
                g2.setColor(new Color(0, 0, 0, 20));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(14, 18, 14, 18));
        header.setPreferredSize(new Dimension(0, 108));

        // Avatar con muñequito flotante
        JPanel avatarWrap = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                int r = 28;
                g2.setColor(new Color(255, 255, 255, 55));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(cx-r-4, (int)(cy-r-4+avatarY), (r+4)*2, (r+4)*2);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(cx-r, (int)(cy-r+avatarY), r*2, r*2);
                g2.setColor(new Color(255, 255, 255, 120));
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawOval(cx-r, (int)(cy-r+avatarY), r*2, r*2);
                drawMunequito(g2, cx-22, (int)(cy-32+avatarY), 1.0f);
                g2.setColor(GREEN_DOT);
                g2.fillOval(cx+r-6, (int)(cy+r-6+avatarY), 11, 11);
                g2.setColor(PURPLE_GRAD2);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx+r-6, (int)(cy+r-6+avatarY), 11, 11);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(72, 72); }
        };
        avatarWrap.setOpaque(false);

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Z-BOT");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        nameLabel.setForeground(WHITE);
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);

        // Sin emoji para evitar cuadros □ — icono dibujado con texto seguro
        JLabel statusLabel = new JLabel("  Asistente musical · En linea");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(255, 255, 255, 180));
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);

        // Burbuja de bienvenida compacta
        bubbleLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(255, 255, 255, 210));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setColor(new Color(255, 255, 255, 140));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bubbleLabel.setText(wrapHtml("Hola! Soy Z-BOT, tu asistente musical. "
                + "Preguntame sobre canciones, artistas y recomendaciones!"));
        bubbleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bubbleLabel.setForeground(TEXT_PRI);
        bubbleLabel.setOpaque(false);
        bubbleLabel.setBorder(new EmptyBorder(7, 12, 7, 12));
        bubbleLabel.setAlignmentX(LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(statusLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(bubbleLabel);

        // Botones header — ahora con texto ASCII limpio, sin emoji
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setOpaque(false);
        btnRow.add(iconBtn("x", "Limpiar chat"));
        btnRow.add(iconBtn("...", "Opciones"));

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.add(btnRow, BorderLayout.NORTH);

        header.add(avatarWrap, BorderLayout.WEST);
        header.add(infoPanel,  BorderLayout.CENTER);
        header.add(right,      BorderLayout.EAST);
        return header;
    }

    private JButton iconBtn(String txt, String tip) {
        JButton b = new JButton(txt) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                int a = getModel().isRollover() ? 60 : 30;
                g2.setColor(new Color(255, 255, 255, a));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(0, 0, getWidth()-2, getHeight()-2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setForeground(new Color(255, 255, 255, 210));
        b.setPreferredSize(new Dimension(34, 34));
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setToolTipText(tip);
        return b;
    }

    // ── CHIPS + MENSAJES ──────────────────────────────────────────────
    private JPanel buildChat() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);

        JPanel chipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 9));
        chipsPanel.setOpaque(true);
        chipsPanel.setBackground(BG_CHAT);
        chipsPanel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BUBBLE_BOT_BDR),
            new EmptyBorder(2, 12, 2, 12)
        ));

        // Labels sin emoji — solo texto claro
        String[] chips   = {"Recomiendame algo", "Top reggaeton", "Para estudiar", "Dato curioso"};
        // Prefijos dibujados como íconos musicales ASCII
        String[] prompts = {
            "Recomiendame una cancion para hoy",
            "Cual es el mejor artista de reggaeton ahora?",
            "Que musica me recomiendas para estudiar?",
            "Cuentame un dato curioso de musica"
        };
        for (int i = 0; i < chips.length; i++) {
            final String p = prompts[i];
            final String label = chips[i];
            JButton chip = new JButton(label) {
                boolean hov = false;
                { addMouseListener(new MouseAdapter(){
                    public void mouseEntered(MouseEvent e){hov=true; repaint();}
                    public void mouseExited (MouseEvent e){hov=false;repaint();}
                }); }
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = g2d(g);
                    g2.setColor(hov ? PURPLE_LIGHT : PURPLE_XLIT);
                    g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 22, 22);
                    g2.setColor(hov ? new Color(162,155,220) : PURPLE_LIGHT);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, 22, 22);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            chip.setFont(new Font("Segoe UI", Font.BOLD, 11));
            chip.setForeground(new Color(83, 74, 183));
            chip.setOpaque(false); chip.setContentAreaFilled(false);
            chip.setBorderPainted(false); chip.setFocusPainted(false);
            chip.setBorder(new EmptyBorder(6, 14, 6, 14));
            chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            chip.addActionListener(e -> { inputField.setText(p); enviar(); });
            chipsPanel.add(chip);
        }

        // Panel de mensajes
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setOpaque(true);
        messagesPanel.setBackground(BG_CHAT);
        messagesPanel.setBorder(new EmptyBorder(14, 14, 14, 14));

        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(BG_CHAT);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        wrap.add(chipsPanel, BorderLayout.NORTH);
        wrap.add(scrollPane, BorderLayout.CENTER);
        return wrap;
    }

    // ── INPUT ─────────────────────────────────────────────────────────
    private JPanel buildInput() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BUBBLE_BOT_BDR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(12, 16, 14, 16));

        // Campo con placeholder real
        inputField = new JTextField() {
            private static final String PH = "Escribele a Jesusito...";
            {
                setText(PH);
                setForeground(TEXT_SEC);
                addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        if (getText().equals(PH)) { setText(""); setForeground(TEXT_PRI); }
                        repaint();
                    }
                    public void focusLost(FocusEvent e) {
                        if (getText().isEmpty()) { setText(PH); setForeground(TEXT_SEC); }
                        repaint();
                    }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                boolean foco = isFocusOwner();
                g2.setColor(foco ? PURPLE_XLIT : INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 26, 26);
                g2.setColor(foco ? PURPLE_GRAD1 : PURPLE_LIGHT);
                g2.setStroke(new BasicStroke(foco ? 1.5f : 1f));
                g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, 26, 26);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputField.setOpaque(false);
        inputField.setCaretColor(PURPLE_DARK);
        inputField.setBorder(new EmptyBorder(11, 18, 11, 18));
        inputField.addActionListener(e -> enviar());

        // Botón enviar — flecha dibujada con Graphics2D, sin emoji
        sendButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                boolean hov = getModel().isRollover();
                Color c1 = hov ? new Color(150, 140, 230) : PURPLE_GRAD1;
                Color c2 = hov ? PURPLE_GRAD1 : PURPLE_GRAD2;
                g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                // Brillo superior
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillArc(3, 3, getWidth()-7, getHeight()/2, 0, 180);
                // Flecha dibujada con polígono — no emoji
                int cx = getWidth()/2, cy = getHeight()/2;
                int[] px = {cx-7, cx+8, cx-7};
                int[] py = {cy-5, cy,   cy+5};
                g2.setColor(WHITE);
                g2.fillPolygon(px, py, 3);
                // Línea de la cola
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx-8, cy, cx+7, cy);
                g2.dispose();
            }
        };
        sendButton.setPreferredSize(new Dimension(46, 46));
        sendButton.setOpaque(false); sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false); sendButton.setFocusPainted(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.setToolTipText("Enviar");
        sendButton.addActionListener(e -> enviar());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        return inputPanel;
    }

    // ══════════════════════════════════════════════════════════════════
    //  LÓGICA
    // ══════════════════════════════════════════════════════════════════
    private void enviar() {
        String ph = "Escribele a Jesusito...";
        String texto = inputField.getText().trim();
        if (texto.isEmpty() || texto.equals(ph)) return;
        inputField.setText("");
        controller.enviarMensaje(texto);
    }

    public void mostrarMensaje(String texto, boolean esUsuario) {
        JPanel row = new JPanel(new FlowLayout(esUsuario ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 2));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        row.setAlignmentX(LEFT_ALIGNMENT);

        if (!esUsuario) {
            JPanel miniAvatar = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = g2d(g);
                    g2.setColor(PURPLE_XLIT);
                    g2.fillOval(0, 0, 34, 34);
                    g2.setColor(PURPLE_LIGHT);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawOval(0, 0, 33, 33);
                    drawMunequito(g2, -4, -4, 0.55f);
                    g2.dispose();
                }
                @Override public Dimension getPreferredSize() { return new Dimension(36, 36); }
            };
            miniAvatar.setOpaque(false);

            JPanel col = new JPanel();
            col.setOpaque(false);
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

            JLabel who = new JLabel("Jesusito");
            who.setFont(new Font("Segoe UI", Font.BOLD, 10));
            who.setForeground(TEXT_SEC);
            who.setAlignmentX(LEFT_ALIGNMENT);
            who.setBorder(new EmptyBorder(0, 6, 3, 0));

            col.add(who);
            col.add(buildBurbuja(texto, false));

            JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            fila.setOpaque(false);
            fila.add(miniAvatar);
            fila.add(col);
            row.add(fila);
        } else {
            JPanel col = new JPanel();
            col.setOpaque(false);
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
            col.add(buildBurbuja(texto, true));
            row.add(col);
        }

        messagesPanel.add(row);
        messagesPanel.add(Box.createVerticalStrut(8));
        messagesPanel.revalidate();
        messagesPanel.repaint();
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    private JLabel buildBurbuja(String texto, boolean esUsuario) {
        JLabel lbl = new JLabel("<html><body style='width:280px;font-family:Segoe UI'>"
                + escapeHtml(texto) + "</body></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                if (esUsuario) {
                    g2.setPaint(new GradientPaint(0, 0, PURPLE_GRAD1, 0, getHeight(), PURPLE_GRAD2));
                    g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                    g2.fillRect(getWidth()-18, getHeight()-18, 18, 18);
                } else {
                    g2.setColor(BUBBLE_BOT_BG);
                    g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                    g2.fillRect(0, getHeight()-18, 18, 18);
                    g2.setColor(BUBBLE_BOT_BDR);
                    g2.setStroke(new BasicStroke(0.8f));
                    g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, 18, 18);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(esUsuario ? WHITE : TEXT_PRI);
        lbl.setOpaque(false);
        lbl.setBorder(new EmptyBorder(10, 14, 10, 14));
        return lbl;
    }

    public void setBubbleTexto(String texto) {
        bubbleLabel.setText(wrapHtml(texto));
    }

    public void setInputHabilitado(boolean habilitado) {
        inputField.setEnabled(habilitado);
        sendButton.setEnabled(habilitado);
        if (habilitado) inputField.requestFocus();
    }

    // ══════════════════════════════════════════════════════════════════
    //  MUNEQUITO JESUSITO — igual al original, sin cambios
    // ══════════════════════════════════════════════════════════════════
    private void drawMunequito(Graphics2D g2, int x, int y, float scale) {
        g2 = (Graphics2D) g2.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);
        g2.scale(scale, scale);

        int ox = 0, oy = 0;

        // Cuerpo
        g2.setColor(WHITE);
        g2.fillRoundRect(ox+14, oy+48, 30, 26, 12, 12);
        g2.setColor(PURPLE_LIGHT);
        g2.drawRoundRect(ox+14, oy+48, 30, 26, 12, 12);
        // Nota musical (caracter Unicode estable en SansSerif)
        g2.setColor(PURPLE_MID);
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.drawString("\u266B", ox+22, oy+65);
        // Brazos
        g2.setColor(new Color(224, 208, 255));
        g2.fillRoundRect(ox+8,  oy+52, 8, 14, 6, 6);
        g2.fillRoundRect(ox+42, oy+52, 8, 14, 6, 6);
        // Cara
        g2.setColor(new Color(253, 232, 200));
        g2.fillOval(ox+12, oy+22, 34, 30);
        // Gorro morado
        g2.setColor(PURPLE_MID);
        g2.fillRoundRect(ox+10, oy+26, 34, 7, 4, 4);
        g2.fillOval(ox+8,  oy+25, 10, 11);
        g2.fillOval(ox+40, oy+25, 10, 11);
        // Ojos con lentes
        g2.setColor(PURPLE_DARK);
        g2.fillOval(ox+10, oy+27, 6, 7);
        g2.fillOval(ox+42, oy+27, 6, 7);
        g2.setColor(WHITE);
        g2.fillOval(ox+18, oy+30, 8, 9);
        g2.fillOval(ox+32, oy+30, 8, 9);
        g2.setColor(TEXT_DARK);
        g2.fillOval(ox+20, oy+32, 5, 5);
        g2.fillOval(ox+34, oy+32, 5, 5);
        // Brillo en ojos
        g2.setColor(new Color(255, 255, 255, 190));
        g2.fillOval(ox+22, oy+33, 2, 2);
        g2.fillOval(ox+36, oy+33, 2, 2);
        // Sonrisa
        g2.setColor(new Color(200, 100, 50));
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(ox+22, oy+40, 14, 7, 0, -180);

        g2.dispose();
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private static Graphics2D g2d(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        return g2;
    }

    private String escapeHtml(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\n","<br>");
    }

    private String wrapHtml(String s) {
        return "<html><body style='width:270px;font-family:Segoe UI;font-size:11pt'>"
                + escapeHtml(s) + "</body></html>";
    }
}