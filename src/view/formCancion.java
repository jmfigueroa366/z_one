package view;

import CSS.CSSCancion;
import model.Cancion;
import services.CancionService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * formCancion — Vista del módulo Canciones.
 *
 * RESPONSABILIDAD: estructura, lógica de navegación y datos.
 * DISEÑO: delegado completamente a CSS.CSSCancion.
 */
public class formCancion extends JPanel {

    // =========================================================
    // ESTADO
    // =========================================================
    private final CancionService           servicio = new CancionService();
    private final DefaultListModel<Cancion> modelo  = new DefaultListModel<>();
    private       JList<Cancion>            lista;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public formCancion() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        construirUI();
        recargar();
    }

    // =========================================================
    // CONSTRUCCIÓN DE LA UI
    // =========================================================
    private void construirUI() {
        add(construirHeader(),  BorderLayout.NORTH);
        add(construirLista(),   BorderLayout.CENTER);
    }

    /** Barra superior: título + botones de acción */
    private JPanel construirHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(CSSCancion.bordeHeader());

        header.add(CSSCancion.titulo("🎵  Canciones"), BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(CSSCancion.boton("＋ Nueva",     "primary",   e -> abrirFormulario(null)));
        acciones.add(CSSCancion.boton("✎ Editar",    "secondary", e -> accionEditar()));
        acciones.add(CSSCancion.boton("✖ Eliminar",  "danger",    e -> accionEliminar()));
        acciones.add(CSSCancion.boton("↺ Refrescar", "secondary", e -> recargar()));
        header.add(acciones, BorderLayout.EAST);

        return header;
    }

    /** Panel central con la lista de canciones */
    private JScrollPane construirLista() {
        lista = new JList<>(modelo);

        // Renderer delegado a CSSCancion
        lista.setCellRenderer((l, cancion, i, sel, foc) -> {
            String texto = cancion.getTitulo()
                    + "    ·    " + (cancion.getNombreGenero() != null ? cancion.getNombreGenero() : "Sin género")
                    + (cancion.getBpm() != null ? "    ·    " + cancion.getBpm() + " BPM" : "");
            return CSSCancion.celdaCancion(texto, sel);
        });

        CSSCancion.estilizarLista(lista);

        JScrollPane sp = new JScrollPane(lista);
        CSSCancion.estilizarScroll(sp);
        return sp;
    }

    // =========================================================
    // ACCIONES DEL HEADER
    // =========================================================
    private void accionEditar() {
        Cancion seleccionada = lista.getSelectedValue();
        if (seleccionada == null) {
            MainFrame.showToast("Selecciona una canción para editar", MainFrame.ToastType.INFO);
            return;
        }
        abrirFormulario(seleccionada);
    }

    private void accionEliminar() {
        Cancion seleccionada = lista.getSelectedValue();
        if (seleccionada == null) {
            MainFrame.showToast("Selecciona una canción para eliminar", MainFrame.ToastType.INFO);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar \"" + seleccionada.getTitulo() + "\"?",
                "Z-One", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            eliminar(seleccionada);
        }
    }

    // =========================================================
    // OPERACIONES DE DATOS
    // =========================================================
    private void recargar() {
        try {
            modelo.clear();
            List<Cancion> canciones = servicio.listar();
            canciones.forEach(modelo::addElement);
        } catch (Exception ex) {
            MainFrame.showToast("Error al cargar: " + ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    private void eliminar(Cancion cancion) {
        try {
            if (servicio.eliminar(cancion.getIdCancion())) {
                MainFrame.showToast("Canción eliminada", MainFrame.ToastType.SUCCESS);
                recargar();
            }
        } catch (Exception ex) {
            MainFrame.showToast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }

    // =========================================================
    // FORMULARIO DE ALTA / EDICIÓN
    // =========================================================
    private void abrirFormulario(Cancion cancionExistente) {
        boolean esEdicion = cancionExistente != null;

        JDialog dlg = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                esEdicion ? "Editar canción" : "Nueva canción",
                true);

        // ── Campos de texto ──────────────────────────────────
        JTextField fTitulo = CSSCancion.campo(esEdicion ? cancionExistente.getTitulo() : "");
        JTextField fBpm    = CSSCancion.campo(
                esEdicion && cancionExistente.getBpm() != null
                        ? String.valueOf(cancionExistente.getBpm()) : "");
        JTextField fProd   = CSSCancion.campo(
                esEdicion && cancionExistente.getIdProductor() != null
                        ? String.valueOf(cancionExistente.getIdProductor()) : "");
        JTextField fFecha  = CSSCancion.campo(
                esEdicion && cancionExistente.getFechaCompilacion() != null
                        ? cancionExistente.getFechaCompilacion().format(FMT)
                        : LocalDate.now().format(FMT));

        // ── ComboBoxes ───────────────────────────────────────
        JComboBox<String> cbGenero = CSSCancion.combo(
                new String[]{"Reggaeton","Pop","Rock","Vallenato","Salsa","Bachata","Trap","Hip-hop","Electronica","Jazz"});
        JComboBox<String> cbIdioma = CSSCancion.combo(
                new String[]{"Espanol","Ingles","Portugues","Frances","Italiano"});
        JComboBox<String> cbEstado = CSSCancion.combo(
                new String[]{"En composicion","Grabando","Mezcla","Master","Publicada","Archivada"});

        if (esEdicion) {
            if (cancionExistente.getNombreGenero() != null) cbGenero.setSelectedItem(cancionExistente.getNombreGenero());
            if (cancionExistente.getNombreIdioma() != null) cbIdioma.setSelectedItem(cancionExistente.getNombreIdioma());
            if (cancionExistente.getNombreEstado() != null) cbEstado.setSelectedItem(cancionExistente.getNombreEstado());
        }

        // ── Layout del formulario ────────────────────────────
        JPanel form = CSSCancion.panelFormulario();
        form.add(CSSCancion.label("Título *"));          form.add(fTitulo);
        form.add(CSSCancion.label("BPM"));               form.add(fBpm);
        form.add(CSSCancion.label("ID Productor *"));    form.add(fProd);
        form.add(CSSCancion.label("Fecha compilación")); form.add(fFecha);
        form.add(CSSCancion.label("Género musical"));    form.add(cbGenero);
        form.add(CSSCancion.label("Idioma"));            form.add(cbIdioma);
        form.add(CSSCancion.label("Estado"));            form.add(cbEstado);

        // ── Botones del diálogo ──────────────────────────────
        JButton bGuardar  = CSSCancion.boton(esEdicion ? "💾 Guardar" : "✦ Crear", "primary",
                e -> guardarDesdeFormulario(dlg, cancionExistente, esEdicion,
                        fTitulo, fBpm, fProd, fFecha, cbGenero, cbIdioma, cbEstado));
        JButton bCancelar = CSSCancion.boton("Cancelar", "secondary", e -> dlg.dispose());

        form.add(bCancelar);
        form.add(bGuardar);

        dlg.setContentPane(form);
        dlg.setSize(520, 400);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    /** Lógica de validación y persistencia al guardar el formulario */
    private void guardarDesdeFormulario(
            JDialog dlg,
            Cancion original,
            boolean esEdicion,
            JTextField fTitulo,
            JTextField fBpm,
            JTextField fProd,
            JTextField fFecha,
            JComboBox<String> cbGenero,
            JComboBox<String> cbIdioma,
            JComboBox<String> cbEstado) {
        try {
            // Validaciones
            if (fTitulo.getText().isBlank()) throw new IllegalArgumentException("El título es obligatorio");
            if (fProd.getText().isBlank())   throw new IllegalArgumentException("El ID de productor es obligatorio");

            Cancion cancion = esEdicion ? original : new Cancion();
            cancion.setTitulo(fTitulo.getText().trim());
            cancion.setBpm(fBpm.getText().isBlank() ? null : Integer.parseInt(fBpm.getText().trim()));
            cancion.setIdProductor(Integer.parseInt(fProd.getText().trim()));
            cancion.setFechaCompilacion(
                    fFecha.getText().isBlank() ? null : LocalDate.parse(fFecha.getText().trim(), FMT));
            cancion.setFechaComposicion(LocalDateTime.now());
            cancion.setNombreGenero((String) cbGenero.getSelectedItem());
            cancion.setNombreIdioma((String) cbIdioma.getSelectedItem());
            cancion.setNombreEstado((String) cbEstado.getSelectedItem());

            if (esEdicion) servicio.actualizar(cancion);
            else           servicio.crear(cancion);

            MainFrame.showToast(
                    esEdicion ? "Canción actualizada ✓" : "Canción creada ✓",
                    MainFrame.ToastType.SUCCESS);
            recargar();
            dlg.dispose();

        } catch (NumberFormatException ex) {
            MainFrame.showToast("BPM e ID Productor deben ser números", MainFrame.ToastType.ERROR);
        } catch (Exception ex) {
            MainFrame.showToast(ex.getMessage(), MainFrame.ToastType.ERROR);
        }
    }
}
