package view;

import model.Productor;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static view.formProductor.*;

/**
 * Diálogo para crear o editar un Productor.
 * Separado de formProductor para mantener cada clase enfocada.
 *
 * Uso:
 *   new formProductorDialog(panelPadre, null).setVisible(true);   // crear
 *   new formProductorDialog(panelPadre, filaSeleccionada).setVisible(true); // editar
 */
public class Formproductordialog extends JDialog {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final formProductor parent;
    private final Integer       filaEditar; // null = crear, Integer = editar

    // Campos del formulario
    private JTextField fNom, fEsp, fNac, fTar, fEst, fNumId, fFechaFirma, fFechaNac;

    public Formproductordialog(formProductor parent, Integer filaEditar) {
        super((Frame) SwingUtilities.getWindowAncestor(parent),
              filaEditar != null ? "Editar productor" : "Nuevo productor", true);
        this.parent     = parent;
        this.filaEditar = filaEditar;
        construirUI();
        pack();
        setMinimumSize(new Dimension(600, getPreferredSize().height));
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    // ══════════════════════════════════════════════════════════════════
    //  VALORES INICIALES (al editar, se leen de la fila seleccionada)
    // ══════════════════════════════════════════════════════════════════
    private String val(int col) {
        if (filaEditar == null) return "";
        Object o = parent.modeloTabla.getValueAt(filaEditar, col);
        return o != null ? o.toString() : "";
    }

    // ══════════════════════════════════════════════════════════════════
    //  CONSTRUCCIÓN UI
    // ══════════════════════════════════════════════════════════════════
    private void construirUI() {
        boolean esEdit = filaEditar != null;

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(10, 8, 24));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };

        root.add(bandaCabecera(esEdit), BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(24, 30, 24, 30));

        // Pre-cargar valores si es edición
        String nomVal  = val(COL_NOMBRE);
        String espVal  = val(COL_ESPECIALIDAD);
        String nacVal  = val(COL_NACIONALIDAD);
        String tarVal  = val(COL_TARIFA).replace("$","").trim();
        String estVal  = val(COL_ESTADO).replace("● ","").trim();
        if (estVal.isEmpty()) estVal = "Disponible";

        // Campos
        fNom      = dlgField(nomVal);
        fEsp      = dlgField(espVal);
        fNac      = dlgField(nacVal);
        fTar      = dlgField(tarVal.isEmpty() ? "0" : tarVal);
        fEst      = dlgField(estVal);
        fNumId    = dlgField("");
        fFechaFirma = dlgField("dd/MM/yyyy");
        fFechaNac   = dlgField("dd/MM/yyyy");

        // Si es edición, intentar cargar datos completos del servicio
        if (esEdit) {
            try {
                int id = (int) parent.modeloTabla.getValueAt(filaEditar, COL_ID);
                Productor p = parent.svc.buscarPorId(id);
                if (p != null) {
                    if (p.getNumIdentificacion() != null) fNumId.setText(p.getNumIdentificacion());
                    if (p.getFechaFirma()       != null) fFechaFirma.setText(p.getFechaFirma().format(FMT));
                    if (p.getFechaNacimiento()  != null) fFechaNac.setText(p.getFechaNacimiento().format(FMT));
                }
            } catch (Exception ignored) {}
        }

        main.add(filaDos("NOMBRE COMPLETO *", fNom,       "ESPECIALIDAD *",      fEsp));
        main.add(Box.createVerticalStrut(15));
        main.add(filaDos("NACIONALIDAD",       fNac,       "TARIFA POR HORA ($)", fTar));
        main.add(Box.createVerticalStrut(15));
        main.add(filaDos("NUM. IDENTIFICACIÓN", fNumId,    "ESTADO",              fEst));
        main.add(Box.createVerticalStrut(15));
        main.add(filaDos("FECHA FIRMA (dd/MM/yyyy)", fFechaFirma,
                          "FECHA NACIMIENTO (dd/MM/yyyy)", fFechaNac));
        main.add(Box.createVerticalStrut(26));

        // Botones
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        ZBtn btnCanc = new ZBtn("Cancelar", false);
        ZBtn btnSave = new ZBtn(esEdit ? "💾  Guardar cambios" : "✦  Crear productor", true);
        btnCanc.setPreferredSize(new Dimension(112, 40));
        btnSave.setPreferredSize(new Dimension(186, 40));
        btnCanc.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> guardar());
        btnRow.add(btnCanc);
        btnRow.add(btnSave);
        main.add(btnRow);

        root.add(main, BorderLayout.CENTER);
        setContentPane(root);
        getRootPane().setDefaultButton(btnSave);
    }

    // ══════════════════════════════════════════════════════════════════
    //  GUARDAR — llama al servicio con los parámetros correctos
    // ══════════════════════════════════════════════════════════════════
    private void guardar() {
        String nom   = fNom.getText().trim();
        String esp   = fEsp.getText().trim();
        String nac   = fNac.getText().trim();
        String numId = fNumId.getText().trim();
        String est   = fEst.getText().trim().isEmpty() ? "Disponible" : fEst.getText().trim();

        double tarifa;
        try {
            tarifa = fTar.getText().trim().isEmpty() ? 0
                   : Double.parseDouble(fTar.getText().trim());
        } catch (NumberFormatException ex) {
            parent.toast("La tarifa debe ser un número", MainFrame.ToastType.ERROR);
            return;
        }

        LocalDate fechaFirma      = parseFecha(fFechaFirma.getText().trim());
        LocalDate fechaNacimiento = parseFecha(fFechaNac.getText().trim());

        boolean esEdit = filaEditar != null;

        parent.worker(() -> {
            if (esEdit) {
                int id = (int) parent.modeloTabla.getValueAt(filaEditar, COL_ID);
                // ✔ firma correcta: modificar(id, nom, esp, tarifa, fechaFirma,
                //                             fechaNac, numId, nac, generoPersona,
                //                             generoMusical, estado)
                parent.svc.modificar(id, nom, esp, tarifa,
                        fechaFirma, fechaNacimiento, numId,
                        nac, null, null, est);
            } else {
                // ✔ firma correcta: registrar(nom, esp, tarifa, fechaFirma,
                //                             fechaNac, numId, nac, generoPersona,
                //                             generoMusical, estado, idUsuario)
                parent.svc.registrar(nom, esp, tarifa,
                        fechaFirma, fechaNacimiento, numId,
                        nac, null, null, est, null);
            }
            return parent.svc.obtenerTodos();
        }, lista -> {
            parent.poblar(lista);
            parent.toast(
                esEdit ? "Productor actualizado" : "Productor creado: " + nom,
                MainFrame.ToastType.SUCCESS);
            dispose();
        }, "Error al guardar");
    }

    // ══════════════════════════════════════════════════════════════════
    //  HELPERS UI
    // ══════════════════════════════════════════════════════════════════
    private JPanel bandaCabecera(boolean esEdit) {
        JPanel band = new JPanel(new BorderLayout(14, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setPaint(new GradientPaint(0, 0, new Color(37,99,235),
                    getWidth(), getHeight(), new Color(14,50,140)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, 0, new Color(255,255,255,45),
                    0, getHeight(), new Color(255,255,255,0)));
                g2.fillRect(0, 0, getWidth(), getHeight()/2);
                g2.setColor(CYAN);
                g2.fillRect(0, getHeight()-2, getWidth(), 2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        band.setOpaque(false);
        band.setBorder(new EmptyBorder(20, 26, 20, 26));
        band.setPreferredSize(new Dimension(0, 90));

        JLabel ico = new JLabel(esEdit ? "✏" : "🎚", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                g2.setColor(new Color(255,255,255,40));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),13,13);
                g2.setColor(new Color(255,255,255,95));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,13,13);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        ico.setForeground(Color.WHITE);
        ico.setPreferredSize(new Dimension(50, 50));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel t = mkLabel(esEdit ? "Editar productor" : "Nuevo productor",
            new Font("Segoe UI", Font.BOLD, 21), Color.WHITE);
        JLabel s = mkLabel(esEdit
            ? "ACTUALIZA LA INFORMACIÓN DEL PRODUCTOR"
            : "REGISTRA UN NUEVO PRODUCTOR EN Z-ONE",
            F_SUB, new Color(255,255,255,185));
        t.setAlignmentX(LEFT_ALIGNMENT);
        s.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(Box.createVerticalGlue());
        txt.add(t);
        txt.add(Box.createVerticalStrut(3));
        txt.add(s);
        txt.add(Box.createVerticalGlue());

        band.add(ico, BorderLayout.WEST);
        band.add(txt, BorderLayout.CENTER);
        return band;
    }

    private JTextField dlgField(String val) {
        JTextField f = new JTextField(val) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = g2d(g);
                boolean foco = isFocusOwner();
                if (foco) {
                    g2.setColor(new Color(37,99,235,60));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                }
                g2.setColor(foco ? new Color(10,22,60) : BG_FIELD);
                g2.fillRoundRect(2,2,getWidth()-5,getHeight()-5,10,10);
                g2.setColor(foco ? PURPLE : COL_BRD);
                g2.setStroke(new BasicStroke(foco ? 1.8f : 1f));
                g2.drawRoundRect(2,2,getWidth()-6,getHeight()-6,10,10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(TXT_PRI);
        f.setOpaque(false);
        f.setCaretColor(PURPLE_LT);
        f.setBorder(new EmptyBorder(0, 14, 0, 14));
        f.setPreferredSize(new Dimension(200, 44));
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { f.repaint(); }
            public void focusLost (java.awt.event.FocusEvent e) { f.repaint(); }
        });
        return f;
    }

    private JPanel filaCampo(String label, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 7));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        p.add(mkLabel(label, new Font("Segoe UI", Font.BOLD, 10), PURPLE_LT), BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private JPanel filaDos(String l1, JComponent c1, String l2, JComponent c2) {
        JPanel p = new JPanel(new GridLayout(1, 2, 14, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        p.add(filaCampo(l1, c1));
        p.add(filaCampo(l2, c2));
        return p;
    }

    private LocalDate parseFecha(String texto) {
        if (texto == null || texto.isBlank() || texto.equals("dd/MM/yyyy")) return null;
        try {
            return LocalDate.parse(texto, FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}