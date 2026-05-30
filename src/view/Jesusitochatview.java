package view;

import util.Chatcontroller;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class Jesusitochatview extends JPanel {

    private JPanel messagesPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton sendButton;
    private JLabel bubbleLabel;
    private Chatcontroller controller;

    private static final Color BG_TOP      = new Color(243, 238, 255);
    private static final Color BG_BOT      = new Color(232, 220, 255);
    private static final Color PURPLE_DARK = new Color(124, 58, 237);
    private static final Color PURPLE_MID  = new Color(162, 89, 247);
    private static final Color PURPLE_LIGHT= new Color(212, 186, 255);
    private static final Color TEXT_DARK   = new Color(74, 40, 128);
    private static final Color WHITE       = Color.WHITE;

    public Jesusitochatview() {
        setLayout(new BorderLayout());
        setOpaque(false);
        controller = new Chatcontroller(this);

        JPanel main = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        main.setOpaque(false);

        // ---- HEADER ----
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 180));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        header.setPreferredSize(new Dimension(0, 110));

        JPanel avatarPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMunequito((Graphics2D) g, 5, 5);
            }
            @Override public Dimension getPreferredSize() { return new Dimension(75, 90); }
        };
        avatarPanel.setOpaque(false);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Jesusito");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setForeground(new Color(167, 143, 208));

        bubbleLabel = new JLabel("<html><body style='width:300px'>Hola! Soy Jesusito, tu asistente musical. Preguntame sobre canciones, artistas y recomendaciones!</body></html>");
        bubbleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bubbleLabel.setForeground(TEXT_DARK);
        bubbleLabel.setBackground(WHITE);
        bubbleLabel.setOpaque(true);
        bubbleLabel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(PURPLE_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(bubbleLabel);
        header.add(avatarPanel, BorderLayout.WEST);
        header.add(infoPanel, BorderLayout.CENTER);

        // ---- CHIPS ----
        JPanel chipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        chipsPanel.setOpaque(false);
        chipsPanel.setBorder(BorderFactory.createEmptyBorder(4, 10, 0, 10));
        String[] chips   = {"Recomiendame algo", "Top reggaeton", "Para estudiar", "Dato curioso"};
        String[] prompts = {
            "Recomiendame una cancion para hoy",
            "Cual es el mejor artista de reggaeton ahora?",
            "Que musica me recomiendas para estudiar?",
            "Cuentame un dato curioso de musica"
        };
        for (int i = 0; i < chips.length; i++) {
            final String p = prompts[i];
            JButton chip = new JButton(chips[i]);
            chip.setFont(new Font("SansSerif", Font.PLAIN, 11));
            chip.setForeground(PURPLE_DARK);
            chip.setBackground(WHITE);
            chip.setBorder(new LineBorder(PURPLE_LIGHT, 1, true));
            chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            chip.setFocusPainted(false);
            chip.addActionListener(e -> { inputField.setText(p); enviar(); });
            chipsPanel.add(chip);
        }

        // ---- MENSAJES ----
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setOpaque(false);
        messagesPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // ---- INPUT ----
        JPanel inputPanel = new JPanel(new BorderLayout(8, 0)) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 160));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, PURPLE_LIGHT),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        inputField = new JTextField();
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputField.setForeground(TEXT_DARK);
        inputField.setBackground(WHITE);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(PURPLE_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        inputField.addActionListener(e -> enviar());

        sendButton = new JButton("➤") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PURPLE_MID, 0, getHeight(), PURPLE_DARK);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "➤";
                g2.drawString(txt, (getWidth()-fm.stringWidth(txt))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        sendButton.setPreferredSize(new Dimension(40, 40));
        sendButton.setBorderPainted(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> enviar());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(header, BorderLayout.NORTH);
        topSection.add(chipsPanel, BorderLayout.SOUTH);

        main.add(topSection, BorderLayout.NORTH);
        main.add(scrollPane, BorderLayout.CENTER);
        main.add(inputPanel, BorderLayout.SOUTH);

        add(main, BorderLayout.CENTER);
    }

    private void enviar() {
        String texto = inputField.getText().trim();
        if (texto.isEmpty()) return;
        inputField.setText("");
        controller.enviarMensaje(texto);
    }

    public void mostrarMensaje(String texto, boolean esUsuario) {
        JPanel row = new JPanel(new FlowLayout(esUsuario ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 2));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(700, Integer.MAX_VALUE));

        JLabel lbl = new JLabel("<html><body style='width:300px'>" + escapeHtml(texto) + "</body></html>");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(esUsuario ? PURPLE_DARK : PURPLE_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        lbl.setBackground(esUsuario ? PURPLE_DARK : WHITE);
        lbl.setForeground(esUsuario ? WHITE : TEXT_DARK);
        lbl.setOpaque(true);

        row.add(lbl);
        messagesPanel.add(row);
        messagesPanel.add(Box.createVerticalStrut(4));
        messagesPanel.revalidate();
        messagesPanel.repaint();
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    public void setBubbleTexto(String texto) {
        bubbleLabel.setText("<html><body style='width:300px'>" + escapeHtml(texto) + "</body></html>");
    }

    public void setInputHabilitado(boolean habilitado) {
        inputField.setEnabled(habilitado);
        sendButton.setEnabled(habilitado);
        if (habilitado) inputField.requestFocus();
    }

    private String escapeHtml(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\n","<br>");
    }

    private void drawMunequito(Graphics2D g2, int x, int y) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(WHITE);
        g2.fillRoundRect(x+14, y+48, 30, 26, 12, 12);
        g2.setColor(PURPLE_LIGHT);
        g2.drawRoundRect(x+14, y+48, 30, 26, 12, 12);
        g2.setColor(PURPLE_MID);
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.drawString("\u266B", x+22, y+65);
        g2.setColor(new Color(224, 208, 255));
        g2.fillRoundRect(x+8, y+52, 8, 14, 6, 6);
        g2.fillRoundRect(x+42, y+52, 8, 14, 6, 6);
        g2.setColor(new Color(253, 232, 200));
        g2.fillOval(x+12, y+22, 34, 30);
        g2.setColor(PURPLE_MID);
        g2.fillRoundRect(x+10, y+26, 34, 7, 4, 4);
        g2.fillOval(x+8, y+25, 10, 11);
        g2.fillOval(x+40, y+25, 10, 11);
        g2.setColor(PURPLE_DARK);
        g2.fillOval(x+10, y+27, 6, 7);
        g2.fillOval(x+42, y+27, 6, 7);
        g2.setColor(WHITE);
        g2.fillOval(x+18, y+30, 8, 9);
        g2.fillOval(x+32, y+30, 8, 9);
        g2.setColor(TEXT_DARK);
        g2.fillOval(x+20, y+32, 5, 5);
        g2.fillOval(x+34, y+32, 5, 5);
        g2.setColor(new Color(200, 100, 50));
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(x+22, y+40, 14, 7, 0, -180);
    }
}