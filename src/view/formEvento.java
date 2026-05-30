package view;

import model.Evento;
import services.EventoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class formEvento extends JPanel {

    private final EventoService servicio = new EventoService();
    private DefaultListModel<Evento> modelo = new DefaultListModel<>();
    private JList<Evento> lista;
    private static final DateTimeFormatter FMT  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FHOR = DateTimeFormatter.ofPattern("HH:mm");

    public formEvento() {
        setOpaque(false);
        setLayout(new BorderLayout(10, 10));
        construirUI();
        recargar();
    }

    private void construirUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titulo = new JLabel("🎟  Eventos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(0xE8EFF7));
        header.add(titulo, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(crearBtn("＋ Nuevo",     true,  e -> openForm(null)));
        acciones.add(crearBtn("✎ Editar",    false, e -> { Evento s = lista.getSelectedValue(); if (s != null) openForm(s); }));
        acciones.add(crearBtn("✖ Eliminar",  false, e -> eliminar()));
        acciones.add(crearBtn("↺ Refrescar", false, e -> recargar()));
        header.add(acciones, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        lista = new JList<>(modelo);
        lista.setBackground(new Color(0x061829));
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lista.setCellRenderer((l, e, i, sel, foc) -> {
            String fechaStr = e.getFecha() != null ? e.getFecha().format(FMT) : "—";
            JLabel item = new JLabel("  🎟  " + (e.getDescripcion() != null ? e.getDescripcion() : "(sin descripción)")
                    + "    ·    " + fechaStr
                    + (e.getNombreTipoEvento() != null ? "    ·    " + e.getNombreTipoEvento() : ""));
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
            List<Evento> all = servicio.listar();
            for (Evento e : all) modelo.addElement(e);
        } catch (Exception ex) {
            MainFrame.showToast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    private void eliminar() {
        Evento s = lista.getSelectedValue();
        if (s == null) { MainFrame.showToast("Selecciona un evento", MainFrame.ToastType.INFO); return; }
        int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el evento?", "Z-One", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            try {
                if (servicio.eliminar(s.getIdEvento())) {
                    MainFrame.showToast("Evento eliminado", MainFrame.ToastType.SUCCESS);
                    recargar();
                }
            } catch (Exception ex) {
                MainFrame.showToast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR);
            }
        }
    }

    private void openForm(Evento e) {
        boolean isEdit = e != null;
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Editar evento" : "Nuevo evento", true);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(new Color(0x04111F));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));

        JTextField fDesc  = campo(isEdit && e.getDescripcion() != null ? e.getDescripcion() : "");
        JTextField fFecha = campo(isEdit && e.getFecha() != null ? e.getFecha().format(FMT) : LocalDate.now().format(FMT));
        JTextField fHIni  = campo(isEdit && e.getHoraInicio() != null ? e.getHoraInicio().format(FHOR) : "19:00");
        JTextField fHFin  = campo(isEdit && e.getHoraFin() != null ? e.getHoraFin().format(FHOR) : "21:00");
        JTextField fArt   = campo(isEdit && e.getIdArtista() != null ? String.valueOf(e.getIdArtista()) : "");
        JTextField fProd  = campo(isEdit && e.getIdProductor() != null ? String.valueOf(e.getIdProductor()) : "");

        JComboBox<String> cbTipo = combo(new String[]{"Concierto", "Sesion", "Lanzamiento", "Entrevista", "Ensayo"});
        if (isEdit && e.getNombreTipoEvento() != null) cbTipo.setSelectedItem(e.getNombreTipoEvento());

        form.add(label("Descripción *"));   form.add(fDesc);
        form.add(label("Fecha (dd/MM/yyyy) *")); form.add(fFecha);
        form.add(label("Hora inicio"));     form.add(fHIni);
        form.add(label("Hora fin"));        form.add(fHFin);
        form.add(label("ID Artista"));      form.add(fArt);
        form.add(label("ID Productor"));    form.add(fProd);
        form.add(label("Tipo de evento"));  form.add(cbTipo);

        JButton bSave = crearBtn(isEdit ? "💾 Guardar" : "✦ Crear", true, ev -> {
            try {
                Evento n = isEdit ? e : new Evento();
                if (fDesc.getText().isBlank()) throw new IllegalArgumentException("Descripción obligatoria");
                n.setDescripcion(fDesc.getText().trim());
                n.setFecha(LocalDate.parse(fFecha.getText().trim(), FMT));
                if (!fHIni.getText().isBlank()) n.setHoraInicio(LocalTime.parse(fHIni.getText().trim(), FHOR));
                if (!fHFin.getText().isBlank()) n.setHoraFin(LocalTime.parse(fHFin.getText().trim(), FHOR));
                n.setIdArtista(fArt.getText().isBlank() ? null : Integer.parseInt(fArt.getText().trim()));
                n.setIdProductor(fProd.getText().isBlank() ? null : Integer.parseInt(fProd.getText().trim()));
                n.setNombreTipoEvento((String) cbTipo.getSelectedItem());
                n.setTipoEvento((String) cbTipo.getSelectedItem());

                if (isEdit) servicio.actualizar(n);
                else servicio.crear(n);

                MainFrame.showToast(isEdit ? "Evento actualizado" : "Evento creado", MainFrame.ToastType.SUCCESS);
                recargar();
                dlg.dispose();
            } catch (Exception ex) {
                MainFrame.showToast(ex.getMessage(), MainFrame.ToastType.ERROR);
            }
        });
        JButton bCancel = crearBtn("Cancelar", false, ev -> dlg.dispose());

        form.add(bCancel); form.add(bSave);
        dlg.setContentPane(form);
        dlg.setSize(500, 400);
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