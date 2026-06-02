package view;

import model.Cabina;
import services.CabinaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class formCabina extends JPanel {

    private final CabinaService servicio = new CabinaService();
    private DefaultListModel<Cabina> modelo = new DefaultListModel<>();
    private JList<Cabina> lista;

    public formCabina() {
        setOpaque(false);
        setLayout(new BorderLayout(10, 10));
        construirUI();
        recargar();
    }

    private void construirUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titulo = new JLabel("🎙  Cabinas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(0xE8EFF7));
        header.add(titulo, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(crearBtn("＋ Nueva",     true,  e -> openForm(null)));
        acciones.add(crearBtn("✎ Editar",    false, e -> { Cabina s = lista.getSelectedValue(); if (s != null) openForm(s); }));
        acciones.add(crearBtn("✖ Eliminar",  false, e -> eliminar()));
        acciones.add(crearBtn("↺ Refrescar", false, e -> recargar()));
        header.add(acciones, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        lista = new JList<>(modelo);
        lista.setBackground(new Color(0x061829));
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lista.setCellRenderer((l, c, i, sel, foc) -> {
            Color colEst = colorEstado(c.getNombreEstado());
            JLabel item = new JLabel("  🎙  " + c.getNombreCabina()
                    + "    ·    Estado: " + (c.getNombreEstado() != null ? c.getNombreEstado() : "—"));
            item.setOpaque(true);
            item.setBackground(sel ? new Color(0x0D3560) : new Color(0x061829));
            item.setForeground(sel ? Color.WHITE : colEst);
            item.setBorder(new EmptyBorder(12, 16, 12, 16));
            return item;
        });
        JScrollPane sp = new JScrollPane(lista);
        sp.setBorder(BorderFactory.createLineBorder(new Color(0x0D2A45), 1));
        sp.getViewport().setBackground(new Color(0x061829));
        add(sp, BorderLayout.CENTER);
    }

    private Color colorEstado(String e) {
        if (e == null) return new Color(0xE8EFF7);
        return switch (e) {
            case "Disponible"    -> new Color(0x22C55E);
            case "Ocupada"       -> new Color(0xFFA726);
            case "Mantenimiento" -> new Color(0xEF5350);
            case "Reservada"     -> new Color(0x42A5F5);
            default              -> new Color(0xE8EFF7);
        };
    }

    private void recargar() {
        try {
            modelo.clear();
            List<Cabina> all = servicio.listar();
            for (Cabina c : all) modelo.addElement(c);
        } catch (Exception ex) {
            MainFrame.showToast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    private void eliminar() {
        Cabina s = lista.getSelectedValue();
        if (s == null) { MainFrame.showToast("Selecciona una cabina", MainFrame.ToastType.INFO); return; }
        int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la cabina \"" + s.getNombreCabina() + "\"?", "Z-One", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            try {
                if (servicio.eliminar(s.getIdCabina())) {
                    MainFrame.showToast("Cabina eliminada", MainFrame.ToastType.SUCCESS);
                    recargar();
                }
            } catch (Exception ex) {
                MainFrame.showToast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR);
            }
        }
    }

    private void openForm(Cabina c) {
        boolean isEdit = c != null;
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Editar cabina" : "Nueva cabina", true);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(new Color(0x04111F));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));

        JTextField fNombre = campo(isEdit ? c.getNombreCabina() : "");
        JComboBox<String> cbEstado = combo(new String[]{"Disponible", "Ocupada", "Mantenimiento", "Reservada"});
        if (isEdit && c.getNombreEstado() != null) cbEstado.setSelectedItem(c.getNombreEstado());

        form.add(label("Nombre de cabina *"));  form.add(fNombre);
        form.add(label("Estado"));              form.add(cbEstado);

        JButton bSave = crearBtn(isEdit ? "💾 Guardar" : "✦ Crear", true, e -> {
            try {
                Cabina n = isEdit ? c : new Cabina();
                if (fNombre.getText().isBlank()) throw new IllegalArgumentException("Nombre obligatorio");
                n.setNombreCabina(fNombre.getText().trim());
                n.setNombreEstado((String) cbEstado.getSelectedItem());

                if (isEdit) servicio.actualizar(n);
                else servicio.crear(n);

                MainFrame.showToast(isEdit ? "Cabina actualizada" : "Cabina creada", MainFrame.ToastType.SUCCESS);
                recargar();
                dlg.dispose();
            } catch (Exception ex) {
                MainFrame.showToast(ex.getMessage(), MainFrame.ToastType.ERROR);
            }
        });
        JButton bCancel = crearBtn("Cancelar", false, e -> dlg.dispose());

        form.add(bCancel); form.add(bSave);
        dlg.setContentPane(form);
        dlg.setSize(440, 220);
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
    private JComboBox<String> combo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setBackground(new Color(0x0A1F36));
        c.setForeground(new Color(0xE8EFF7));
        return c;
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