package view;

import util.Chatcontroller;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class Zbotchatview extends JPanel {

    private JPanel      messagesPanel;
    private JScrollPane scrollPane;
    private JTextField  inputField;
    private JButton     sendButton;
    private Chatcontroller controller;

    // ── PALETA (alineada con mockup) ──────────────────────────────────
    private static final Color PURPLE_HEADER  = new Color(108,  92, 231); // #6C5CE7
    private static final Color PURPLE_DARK    = new Color( 83,  74, 183); // #534AB7
    private static final Color PURPLE_LIGHT   = new Color(196, 186, 247); // #C4BAF7
    private static final Color PURPLE_XLIT    = new Color(238, 237, 254); // #EEEDFE
    private static final Color CHIP_HOVER     = new Color(206, 203, 246); // #CECBF6
    private static final Color GREEN_DOT      = new Color( 74, 222, 128); // #00E676
    private static final Color BG_CHAT        = new Color(249, 249, 255);
    private static final Color BG_INPUT       = new Color(244, 243, 255);
    private static final Color BUBBLE_BOT_BDR = new Color(228, 226, 252);
    private static final Color TEXT_PRI       = new Color( 38,  33,  92);
    private static final Color TEXT_SEC       = new Color(160, 155, 210);
    private static final Color WHITE          = Color.WHITE;

    // ── Fade-in ───────────────────────────────────────────────────────
    private float alpha = 0f;
    private javax.swing.Timer timerFade;

    public Zbotchatview() {
        setLayout(new BorderLayout());
        setOpaque(false);
        controller = new Chatcontroller(this);
        construirUI();
        iniciarFade();
    }

    // ══════════════════════════════════════════════════════════════════
    //  FADE-IN
    // ══════════════════════════════════════════════════════════════════
    private void iniciarFade() {
        alpha = 0f;
        timerFade = new javax.swing.Timer(16, null);
        timerFade.addActionListener(e -> {
            alpha = Math.min(1f, alpha + 0.05f);
            repaint();
            if (alpha >= 1f) timerFade.stop();
        });
        timerFade.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = g2d(g);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(BG_CHAT);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }

    // ══════════════════════════════════════════════════════════════════
    //  UI PRINCIPAL
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
        JPanel header = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(PURPLE_HEADER);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // separador inferior sutil
                g2.setColor(new Color(0, 0, 0, 28));
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(14, 18, 14, 18));
        header.setPreferredSize(new Dimension(0, 76));

        // ── Avatar circular con punto online ──
        JPanel avatarPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                int d = 46;
                // Fondo circular semitransparente
                g2.setColor(new Color(255, 255, 255, 45));
                g2.fillOval(0, 0, d, d);
                // Borde blanco suave
                g2.setColor(new Color(255, 255, 255, 120));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(0, 0, d - 1, d - 1);
                // Muñequito
                drawMunequito(g2, 4, 4);
                // Punto verde online
                g2.setColor(GREEN_DOT);
                g2.fillOval(d - 12, d - 12, 11, 11);
                g2.setColor(PURPLE_HEADER);
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawOval(d - 12, d - 12, 11, 11);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(48, 48); }
        };
        avatarPanel.setOpaque(false);

        // ── Info del bot ──
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel name = new JLabel("Z-BOT");
        name.setFont(new Font("Segoe UI", Font.BOLD, 16));
        name.setForeground(WHITE);
        name.setAlignmentX(LEFT_ALIGNMENT);

        JLabel status = new JLabel("Asistente musical  \u2022  En linea");
        status.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        status.setForeground(new Color(255, 255, 255, 175));
        status.setAlignmentX(LEFT_ALIGNMENT);

        info.add(name);
        info.add(Box.createVerticalStrut(3));
        info.add(status);

        // ── Botones de acción ──
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btns.setOpaque(false);
        btns.add(makeHeaderBtn('\u2315', "Buscar"));   // ⌕
        btns.add(makeHeaderBtn('\u22ef', "Opciones")); // ⋯

        JPanel east = new JPanel(new BorderLayout());
        east.setOpaque(false);
        east.add(btns, BorderLayout.NORTH);

        header.add(avatarPanel, BorderLayout.WEST);
        header.add(info,        BorderLayout.CENTER);
        header.add(east,        BorderLayout.EAST);
        return header;
    }

    private JButton makeHeaderBtn(char icon, String tip) {
        JButton b = new JButton(String.valueOf(icon)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                int a = getModel().isRollover() ? 65 : 30;
                g2.setColor(new Color(255, 255, 255, a));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(new Color(255, 255, 255, 210));
        b.setPreferredSize(new Dimension(32, 32));
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

        // Chips bar
        JPanel chipsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        chipsBar.setBackground(WHITE);
        chipsBar.setOpaque(true);
        chipsBar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BUBBLE_BOT_BDR),
            new EmptyBorder(0, 12, 0, 12)
        ));

        // { label, prompt, bgNormal, bgHover, borde, texto }
        Object[][] chips = {
            {"Recomiendame algo", "Recomiendame una cancion para hoy",
                new Color(238,237,254), new Color(214,210,250),
                new Color(180,172,240), new Color(83,74,183)},
            {"Top reggaeton", "Cual es el mejor artista de reggaeton ahora?",
                new Color(255,237,245), new Color(252,210,230),
                new Color(240,160,195), new Color(175,35,95)},
            {"Para estudiar", "Que musica me recomiendas para estudiar?",
                new Color(230,248,238), new Color(195,235,212),
                new Color(130,205,162), new Color(25,125,70)},
            {"Dato curioso", "Cuentame un dato curioso de musica",
                new Color(255,246,220), new Color(252,228,165),
                new Color(238,196,100), new Color(155,105,10)}
        };

        for (Object[] chip : chips) {
            final String prompt    = (String) chip[1];
            final Color  bgNormal  = (Color)  chip[2];
            final Color  bgHover   = (Color)  chip[3];
            final Color  borde     = (Color)  chip[4];
            final Color  txtColor  = (Color)  chip[5];
            chipsBar.add(makeChip((String) chip[0], prompt, bgNormal, bgHover, borde, txtColor));
        }

        // Panel de mensajes
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(BG_CHAT);
        messagesPanel.setOpaque(true);
        messagesPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Bienvenida inicial del bot
        mostrarMensaje("Hola! Soy Z-BOT, tu asistente musical.\nPreguntame sobre canciones, artistas y recomendaciones!", false);

        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setBackground(BG_CHAT);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        wrap.add(chipsBar,   BorderLayout.NORTH);
        wrap.add(scrollPane, BorderLayout.CENTER);
        return wrap;
    }

    private JButton makeChip(String label, String prompt,
                              Color bgNormal, Color bgHover, Color borde, Color txtColor) {
        JButton chip = new JButton(label) {
            boolean hov = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hov = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(hov ? bgHover : bgNormal);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.setColor(borde);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(new Font("Segoe UI", Font.BOLD, 11));
        chip.setForeground(txtColor);
        chip.setOpaque(false); chip.setContentAreaFilled(false);
        chip.setBorderPainted(false); chip.setFocusPainted(false);
        chip.setBorder(new EmptyBorder(6, 14, 6, 14));
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chip.addActionListener(e -> { inputField.setText(prompt); enviar(); });
        return chip;
    }

    // ── INPUT ─────────────────────────────────────────────────────────
    private JPanel buildInput() {
        JPanel panel = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BUBBLE_BOT_BDR);
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 16, 14, 16));

        // Campo de texto
        final String PH = "Escribele a Jesusito...";
        inputField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                boolean foco = isFocusOwner();
                g2.setColor(foco ? PURPLE_XLIT : BG_INPUT);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.setColor(foco ? PURPLE_HEADER : PURPLE_LIGHT);
                g2.setStroke(new BasicStroke(foco ? 1.5f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inputField.setText(PH);
        inputField.setForeground(TEXT_SEC);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputField.setOpaque(false);
        inputField.setCaretColor(PURPLE_HEADER);
        inputField.setBorder(new EmptyBorder(10, 18, 10, 18));
        inputField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (inputField.getText().equals(PH)) {
                    inputField.setText("");
                    inputField.setForeground(TEXT_PRI);
                }
            }
            public void focusLost(FocusEvent e) {
                if (inputField.getText().isEmpty()) {
                    inputField.setText(PH);
                    inputField.setForeground(TEXT_SEC);
                }
            }
        });
        inputField.addActionListener(e -> enviar());

        // Botón enviar circular
        sendButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                boolean hov = getModel().isRollover();
                g2.setColor(hov ? PURPLE_DARK : PURPLE_HEADER);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                // Brillo superior
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillArc(3, 3, getWidth() - 7, (getHeight() - 6) / 2, 0, 180);
                // Flecha hacia la derecha
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.setColor(WHITE);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx - 7, cy, cx + 6, cy);
                int[] ax = {cx + 2, cx + 7, cx + 2};
                int[] ay = {cy - 4, cy,     cy + 4};
                g2.fillPolygon(ax, ay, 3);
                g2.dispose();
            }
        };
        sendButton.setPreferredSize(new Dimension(44, 44));
        sendButton.setOpaque(false); sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false); sendButton.setFocusPainted(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.setToolTipText("Enviar");
        sendButton.addActionListener(e -> enviar());

        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════
    //  LÓGICA PÚBLICA
    // ══════════════════════════════════════════════════════════════════
    private void enviar() {
        String ph    = "Escribele a Jesusito...";
        String texto = inputField.getText().trim();
        if (texto.isEmpty() || texto.equals(ph)) return;
        inputField.setText("");
        controller.enviarMensaje(texto);
    }

    public void mostrarMensaje(String texto, boolean esUsuario) {
        JPanel row = new JPanel(new FlowLayout(
            esUsuario ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 2));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        row.setAlignmentX(LEFT_ALIGNMENT);

        if (!esUsuario) {
            // Mini avatar bot
            JPanel mini = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = g2d(g);
                    g2.setColor(PURPLE_XLIT);
                    g2.fillOval(0, 0, 32, 32);
                    g2.setColor(PURPLE_LIGHT);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawOval(0, 0, 31, 31);
                    drawMunequitoMini(g2, 4, 4);
                    g2.dispose();
                }
                @Override public Dimension getPreferredSize() { return new Dimension(34, 34); }
            };
            mini.setOpaque(false);

            JPanel col = new JPanel();
            col.setOpaque(false);
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

            JLabel who = new JLabel("Jesusito");
            who.setFont(new Font("Segoe UI", Font.BOLD, 10));
            who.setForeground(TEXT_SEC);
            who.setAlignmentX(LEFT_ALIGNMENT);
            who.setBorder(new EmptyBorder(0, 6, 3, 0));

            col.add(who);
            col.add(makeBurbuja(texto, false));

            JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            fila.setOpaque(false);
            fila.add(mini);
            fila.add(col);
            row.add(fila);
        } else {
            JPanel col = new JPanel();
            col.setOpaque(false);
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
            col.add(makeBurbuja(texto, true));
            row.add(col);
        }

        messagesPanel.add(row);
        messagesPanel.add(Box.createVerticalStrut(10));
        messagesPanel.revalidate();
        messagesPanel.repaint();
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    private JLabel makeBurbuja(String texto, boolean esUsuario) {
        JLabel lbl = new JLabel(
            "<html><body style='width:270px;font-family:Segoe UI;font-size:11pt'>"
            + escHtml(texto) + "</body></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                if (esUsuario) {
                    // Burbuja usuario: violeta sólido, esquina inf-derecha plana
                    g2.setColor(PURPLE_HEADER);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                    g2.setColor(PURPLE_HEADER);
                    g2.fillRect(getWidth() - 18, getHeight() - 18, 18, 18);
                } else {
                    // Burbuja bot: blanco con borde suave, esquina inf-izq plana
                    g2.setColor(WHITE);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                    g2.setColor(WHITE);
                    g2.fillRect(0, getHeight() - 18, 18, 18);
                    g2.setColor(BUBBLE_BOT_BDR);
                    g2.setStroke(new BasicStroke(0.8f));
                    g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 18, 18);
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

    private JLabel typingLabel = null;

    /** Muestra/oculta indicador de "escribiendo..." en el panel de mensajes. */
    public void setBubbleTexto(String texto) {
        SwingUtilities.invokeLater(() -> {
            // Quitar indicador anterior si existe
            if (typingLabel != null) {
                Container parent = typingLabel.getParent();
                if (parent != null) {
                    parent.remove(typingLabel);
                    // también quitar el strut que sigue
                }
                typingLabel = null;
                messagesPanel.revalidate();
                messagesPanel.repaint();
            }
            if (texto == null || texto.isEmpty()) return;

            // Crear nuevo indicador de estado (gris itálica, alineado a la izquierda)
            typingLabel = new JLabel("<html><i style='color:#A09BD2;font-family:Segoe UI;font-size:10pt'>"
                + escHtml(texto) + "</i></html>");
            typingLabel.setAlignmentX(LEFT_ALIGNMENT);
            typingLabel.setBorder(new EmptyBorder(2, 46, 4, 0)); // indent = mini avatar

            messagesPanel.add(typingLabel);
            messagesPanel.add(Box.createVerticalStrut(4));
            messagesPanel.revalidate();
            messagesPanel.repaint();
            SwingUtilities.invokeLater(() -> {
                JScrollBar bar = scrollPane.getVerticalScrollBar();
                bar.setValue(bar.getMaximum());
            });
        });
    }

    public void setInputHabilitado(boolean habilitado) {
        inputField.setEnabled(habilitado);
        sendButton.setEnabled(habilitado);
        if (habilitado) inputField.requestFocus();
    }

    // ══════════════════════════════════════════════════════════════════
    //  DIBUJADO DEL MUÑEQUITO
    // ══════════════════════════════════════════════════════════════════
    /** Versión completa para el header (38x38 px). */
    private void drawMunequito(Graphics2D g2, int x, int y) {
        g2 = (Graphics2D) g2.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);
        // Cuerpo
        g2.setColor(WHITE);
        g2.fillRoundRect(5, 20, 28, 18, 10, 10);
        g2.setColor(PURPLE_LIGHT);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(5, 20, 28, 18, 10, 10);
        // Nota musical
        g2.setColor(new Color(162, 89, 247));
        g2.setFont(new Font("SansSerif", Font.BOLD, 9));
        g2.drawString("\u266B", 10, 33);
        // Brazos
        g2.setColor(new Color(220, 210, 252));
        g2.fillRoundRect(1, 22, 6, 10, 4, 4);
        g2.fillRoundRect(31, 22, 6, 10, 4, 4);
        // Cara
        g2.setColor(new Color(253, 232, 200));
        g2.fillOval(5, 4, 28, 20);
        // Gorro
        g2.setColor(new Color(162, 89, 247));
        g2.fillRoundRect(4, 8, 30, 6, 4, 4);
        g2.fillOval(2,  7, 8, 8);
        g2.fillOval(28, 7, 8, 8);
        // Ojos con lentes
        g2.setColor(new Color(124, 58, 237));
        g2.fillOval(3,  8, 5, 6);
        g2.fillOval(30, 8, 5, 6);
        g2.setColor(WHITE);
        g2.fillOval(8,  10, 7, 8);
        g2.fillOval(23, 10, 7, 8);
        g2.setColor(new Color(60, 52, 137));
        g2.fillOval(10, 12, 4, 4);
        g2.fillOval(25, 12, 4, 4);
        g2.setColor(new Color(255, 255, 255, 190));
        g2.fillOval(11, 13, 2, 2);
        g2.fillOval(26, 13, 2, 2);
        // Sonrisa
        g2.setColor(new Color(200, 100, 50));
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(11, 17, 12, 6, 0, -180);
        g2.dispose();
    }

    /** Versión mini para las burbujas del bot (22x22 px). */
    private void drawMunequitoMini(Graphics2D g2, int x, int y) {
        g2 = (Graphics2D) g2.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);
        g2.setColor(new Color(253, 232, 200));
        g2.fillOval(5, 2, 14, 11);
        g2.setColor(new Color(162, 89, 247));
        g2.fillRoundRect(4, 5, 16, 4, 3, 3);
        g2.fillOval(2, 4, 5, 5);
        g2.fillOval(17, 4, 5, 5);
        g2.setColor(WHITE);
        g2.fillOval(6, 6, 5, 5);
        g2.fillOval(13, 6, 5, 5);
        g2.setColor(new Color(60, 52, 137));
        g2.fillOval(8, 7, 2, 2);
        g2.fillOval(15, 7, 2, 2);
        g2.setColor(WHITE);
        g2.fillRoundRect(5, 14, 14, 9, 6, 6);
        g2.setColor(PURPLE_LIGHT);
        g2.setStroke(new BasicStroke(0.8f));
        g2.drawRoundRect(5, 14, 14, 9, 6, 6);
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

    private String escHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");
    }
}