package view;

import model.Colaboracion;
import services.ColaboracionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class formColaboracion extends JPanel {

    private final ColaboracionService servicio = new ColaboracionService();
    private DefaultListModel<Colaboracion> modelo = new DefaultListModel<>();
    private JList<Colaboracion> lista;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public formColaboracion() {
        setOpaque(false);
        setLayout(new BorderLayout(10, 10));
        construirUI();
        recargar();
    }

    private void construirUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titulo = new JLabel("🤝  Colaboraciones");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(0xE8EFF7));
        header.add(titulo, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(crearBtn("＋ Nueva",     true,  e -> openForm(null)));
        acciones.add(crearBtn("✎ Editar",    false, e -> { Colaboracion s = lista.getSelectedValue(); if (s != null) openForm(s); }));
        acciones.add(crearBtn("✖ Eliminar",  false, e -> eliminar()));
        acciones.add(crearBtn("↺ Refrescar", false, e -> recargar()));
        header.add(acciones, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        lista = new JList<>(modelo);
        lista.setBackground(new Color(0x061829));
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lista.setCellRenderer((l, c, i, sel, foc) -> {
            String fechaStr = c.getFechaColaboracion() != null ? c.getFechaColaboracion().format(FMT) : "—";
            JLabel item = new JLabel("  🤝  " + (c.getColaboracionArtista() != null ? c.getColaboracionArtista() : "—")
                    + "    ·    " + (c.getNombreCancion() != null ? c.getNombreCancion() : "Sin canción")
                    + "    ·    " + fechaStr);
            item.setOpaque(true);
            item.setBackground(sel ? new Color(0x0D3560) : new Color(0x061829));
            item.setForeground(new Color(0xE8EFF7));
            item.setBorder(new EmptyBorder(12, 16, 12, 16));
            return item;
        });
        JScrollPane sp = new JScrollPane(lista);
        sp.setBorder(BorderFactory.createLineBorder(new Color(0x0D2A45), 1));
        sp.getViewport().setBackground(new Color(0x061829));
        add(sp, BorderLayout.CENTER);
    }

    private void recargar() {
        try {
            modelo.clear();
            List<Colaboracion> all = servicio.listar();
            for (Colaboracion c : all) modelo.addElement(c);
        } catch (Exception ex) {
            MainFrame.showToast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    private void eliminar() {
        Colaboracion s = lista.getSelectedValue();
        if (s == null) { MainFrame.showToast("Selecciona una colaboración", MainFrame.ToastType.INFO); return; }
        int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la colaboración?", "Z-One", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            try {
                if (servicio.eliminar(s.getIdColaboracion())) {
                    MainFrame.showToast("Colaboración eliminada", MainFrame.ToastType.SUCCESS);
                    recargar();
                }
            } catch (Exception ex) {
                MainFrame.showToast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR);
            }
        }
    }

    private void openForm(Colaboracion c) {
        boolean isEdit = c != null;
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Editar colaboración" : "Nueva colaboración", true);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(new Color(0x04111F));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));

        JTextField fArt    = campo(isEdit && c.getColaboracionArtista() != null ? c.getColaboracionArtista() : "");
        JTextField fCan    = campo(isEdit && c.getIdCancion() != null ? String.valueOf(c.getIdCancion()) : "");
        JTextField fFecha  = campo(isEdit && c.getFechaColaboracion() != null
                ? c.getFechaColaboracion().format(FMT) : LocalDate.now().format(FMT));

        form.add(label("Artista colaborador *"));  form.add(fArt);
        form.add(label("ID Canción *"));            form.add(fCan);
        form.add(label("Fecha colaboración *"));   form.add(fFecha);

        JButton bSave = crearBtn(isEdit ? "💾 Guardar" : "✦ Crear", true, e -> {
            try {
                Colaboracion n = isEdit ? c : new Colaboracion();
                if (fArt.getText().isBlank()) throw new IllegalArgumentException("Artista obligatorio");
                if (fCan.getText().isBlank()) throw new IllegalArgumentException("ID canción obligatorio");
                n.setColaboracionArtista(fArt.getText().trim());
                n.setIdCancion(Integer.parseInt(fCan.getText().trim()));
                n.setFechaColaboracion(LocalDate.parse(fFecha.getText().trim(), FMT));

                if (isEdit) servicio.actualizar(n);
                else servicio.crear(n);

                MainFrame.showToast(isEdit ? "Colaboración actualizada" : "Colaboración creada", MainFrame.ToastType.SUCCESS);
                recargar();
                dlg.dispose();
            } catch (Exception ex) {
                MainFrame.showToast(ex.getMessage(), MainFrame.ToastType.ERROR);
            }
        });
        JButton bCancel = crearBtn("Cancelar", false, e -> dlg.dispose());

        form.add(bCancel); form.add(bSave);
        dlg.setContentPane(form);
        dlg.setSize(460, 260);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private JTextField campo(String v) {
        JTextField f = new JTextField(v);
        f.setBackground(new Color(0x0A1F36));
        f.setForeground(new Color(0xE8EFF7));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x0D2A45)),
                new EmptyBorder(6, 10, 6, 10)));
        return f;
    }
    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(new Color(0x42A5F5));
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return l;
    }
    private JButton crearBtn(String t, boolean primary, java.awt.event.ActionListener a) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(primary ? Color.WHITE : new Color(0xE8EFF7));
        b.setBackground(primary ? new Color(0x1A6EBE) : new Color(0x0A1F36));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(110, 34));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(a);
        return b;
    }
}