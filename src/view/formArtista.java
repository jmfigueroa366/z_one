package view;

import model.Artista;
import services.ArtistaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
public class formArtista extends JPanel {
    // ── Paleta de colores ────────────────────────────────────────────
    private static final Color COLOR_FONDO_CARD    = new Color(16, 14, 38);
    private static final Color COLOR_FONDO_CAMPO   = new Color(20, 18, 48);
    private static final Color COLOR_MORADO        = new Color(139, 92, 246);
    private static final Color COLOR_CYAN          = new Color(6, 182, 212);
    private static final Color COLOR_VERDE         = new Color(34, 197, 94);
    private static final Color COLOR_ROSA          = new Color(236, 72, 153);
    private static final Color COLOR_AMBAR         = new Color(245, 158, 11);
    private static final Color COLOR_TEXTO_PRIM    = new Color(241, 245, 249);
    private static final Color COLOR_TEXTO_SEC     = new Color(148, 163, 184);
    private static final Color COLOR_BORDE         = new Color(139, 92, 246, 55);

    // ── Columnas de la tabla ─────────────────────────────────────────
    private static final String[] COLUMNAS = {"ID", "Nombre artístico", "Género", "País", "Canciones", "Estado"};
    private static final int      COL_ID        = 0;
    private static final int      COL_NOMBRE    = 1;
    private static final int      COL_GENERO    = 2;
    private static final int      COL_PAIS      = 3;
    private static final int      COL_CANCIONES = 4;
    private static final int      COL_ESTADO    = 5;

    // ── Dependencias ─────────────────────────────────────────────────
    private final ArtistaService artistaService;

    // ── Componentes de estado ─────────────────────────────────────────
    private DefaultTableModel modeloTabla;
    private JTable            tabla;
    private JTextField        campoBusqueda;
    private JLabel            lblTotalArtistas;
    private JLabel            lblArtistaActivos;
    private JLabel            lblPaisesDistintos;
    private JLabel            lblTotalCanciones;

    // ── Constructor ──────────────────────────────────────────────────
    public formArtista() {
        this.artistaService = new ArtistaService();
        configurarPanel();
        construirUI();
        cargarArtistasDesdeServicio();
    }

    // ── Configuración base del panel ─────────────────────────────────
    private void configurarPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(28, 28, 28, 28));
    }

    // ── Construcción de la UI ─────────────────────────────────────────
    private void construirUI() {
        add(construirEncabezado(), BorderLayout.NORTH);
        add(construirCuerpo(),     BorderLayout.CENTER);
    }

    private JPanel construirCuerpo() {
        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.add(Box.createVerticalStrut(20));
        cuerpo.add(construirFilaEstadisticas());
        cuerpo.add(Box.createVerticalStrut(20));
        cuerpo.add(construirPanelTabla());
        return cuerpo;
    }

    // ── Encabezado con título y barra de acciones ─────────────────────
    private JPanel construirEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout(16, 0));
        encabezado.setOpaque(false);
        encabezado.add(construirTitulos(),  BorderLayout.WEST);
        encabezado.add(construirAcciones(), BorderLayout.EAST);
        return encabezado;
    }

    private JPanel construirTitulos() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel icono    = etiquetaConFuente("🎤", new Font("Segoe UI Emoji", Font.PLAIN, 22), COLOR_TEXTO_PRIM);
        JLabel titulo   = etiquetaConFuente("Artistas", new Font("Segoe UI", Font.BOLD, 28), COLOR_TEXTO_PRIM);
        JLabel subtitulo = etiquetaConFuente("Gestión de artistas, bandas y colaboraciones",
                            new Font("Segoe UI", Font.PLAIN, 13), COLOR_TEXTO_SEC);

        for (JLabel lbl : new JLabel[]{icono, titulo, subtitulo}) {
            lbl.setAlignmentX(LEFT_ALIGNMENT);
        }

        panel.add(icono);
        panel.add(Box.createVerticalStrut(4));
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(2));
        panel.add(subtitulo);
        return panel;
    }

    private JPanel construirAcciones() {
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);

        campoBusqueda = construirCampoBusqueda();
        ModernUI.RoundedButton btnNuevo = new ModernUI.RoundedButton("＋ Nuevo artista", true);
        btnNuevo.setPreferredSize(new Dimension(170, 38));
        btnNuevo.addActionListener(e -> abrirDialogCrear());

        acciones.add(campoBusqueda);
        acciones.add(btnNuevo);
        return acciones;
    }

    private JTextField construirCampoBusqueda() {
        JTextField campo = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                pintarFondoRedondeado(this,g, COLOR_FONDO_CARD, COLOR_BORDE, 10);
                super.paintComponent(g);
            }
        };
        campo.setPreferredSize(new Dimension(240, 38));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setForeground(COLOR_TEXTO_PRIM);
        campo.setOpaque(false);
        campo.setCaretColor(COLOR_TEXTO_PRIM);
        campo.setBorder(new EmptyBorder(0, 14, 0, 14));
        campo.putClientProperty("JTextField.placeholderText", "🔍  Buscar artista...");
        campo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { ejecutarBusqueda(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { ejecutarBusqueda(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { ejecutarBusqueda(); }
        });
        return campo;
    }

    // ── Fila de estadísticas ─────────────────────────────────────────
    private JPanel construirFilaEstadisticas() {
        lblTotalArtistas  = new JLabel("0");
        lblArtistaActivos = new JLabel("0");
        lblPaisesDistintos = new JLabel("0");
        lblTotalCanciones = new JLabel("0");

        JPanel fila = new JPanel(new GridLayout(1, 4, 14, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.add(construirTarjetaEstadistica("TOTAL ARTISTAS", lblTotalArtistas,  COLOR_MORADO, "🎤"));
        fila.add(construirTarjetaEstadistica("ACTIVOS",         lblArtistaActivos, COLOR_VERDE,  "✅"));
        fila.add(construirTarjetaEstadistica("PAÍSES",          lblPaisesDistintos,COLOR_CYAN,   "🌍"));
        fila.add(construirTarjetaEstadistica("CANCIONES",       lblTotalCanciones, COLOR_AMBAR,  "🎵"));
        return fila;
    }

    private JPanel construirTarjetaEstadistica(String titulo, JLabel lblValor,
                                                Color colorAcento, String emoji) {
        JPanel tarjeta = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_FONDO_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 60));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                GradientPaint glow = new GradientPaint(
                    0, 0, new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 30),
                    0, getHeight() / 2f, new Color(0, 0, 0, 0));
                g2.setPaint(glow);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setLayout(new BorderLayout(8, 0));
        tarjeta.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblEmoji = etiquetaConFuente(emoji, new Font("Segoe UI Emoji", Font.PLAIN, 22), COLOR_TEXTO_PRIM);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel lblTitulo = etiquetaConFuente(titulo, new Font("Segoe UI", Font.BOLD, 9),
            new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 200));
        lblTitulo.setAlignmentX(LEFT_ALIGNMENT);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValor.setForeground(COLOR_TEXTO_PRIM);
        lblValor.setAlignmentX(LEFT_ALIGNMENT);

        textos.add(lblTitulo);
        textos.add(lblValor);
        tarjeta.add(lblEmoji, BorderLayout.WEST);
        tarjeta.add(textos,   BorderLayout.CENTER);
        return tarjeta;
    }

    // ── Panel de tabla ───────────────────────────────────────────────
    private JPanel construirPanelTabla() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_FONDO_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(COLOR_BORDE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setAlignmentX(LEFT_ALIGNMENT);

        inicializarModeloTabla();
        tabla = new JTable(modeloTabla);
        configurarEstiloTabla();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(0, 0, 0, 0));

        card.add(construirEncabezadoTabla(), BorderLayout.NORTH);
        card.add(scroll,                    BorderLayout.CENTER);
        card.add(construirBotonesAccion(),   BorderLayout.SOUTH);
        return card;
    }

    private void inicializarModeloTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int fila, int col) { return false; }
        };
    }

    private JPanel construirEncabezadoTabla() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(16, 18, 10, 18));
        encabezado.add(etiquetaConFuente("Lista de artistas",
            new Font("Segoe UI", Font.BOLD, 14), COLOR_TEXTO_PRIM), BorderLayout.WEST);
        return encabezado;
    }

    private JPanel construirBotonesAccion() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 14, 14, 14));

        ModernUI.RoundedButton btnEditar   = new ModernUI.RoundedButton("✏  Editar",   false);
        ModernUI.RoundedButton btnEliminar = new ModernUI.RoundedButton("🗑  Eliminar", false);
        btnEliminar.setForeground(COLOR_ROSA);

        btnEditar.addActionListener(  e -> accionEditar());
        btnEliminar.addActionListener(e -> accionEliminar());

        panel.add(btnEditar);
        panel.add(btnEliminar);
        return panel;
    }

    // ── Estilo de la tabla ───────────────────────────────────────────
    private void configurarEstiloTabla() {
        tabla.setOpaque(false);
        tabla.setBackground(new Color(0, 0, 0, 0));
        tabla.setForeground(COLOR_TEXTO_PRIM);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(44);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(new Color(139, 92, 246, 50));
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFocusable(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configurarEncabezadoTabla();
        configurarAnchoColumnas();
        tabla.setDefaultRenderer(Object.class, new RenderCeldaArtista());
    }

    private void configurarEncabezadoTabla() {
        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setBackground(new Color(12, 10, 30));
        encabezado.setForeground(COLOR_MORADO);
        encabezado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        encabezado.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
            new Color(139, 92, 246, 60)));
        encabezado.setReorderingAllowed(false);
        encabezado.setPreferredSize(new Dimension(0, 36));
    }

    private void configurarAnchoColumnas() {
        int[] anchos = {52, 190, 130, 120, 130, 110};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    // ── Renderer de celdas (clase separada — SRP) ────────────────────
    private class RenderCeldaArtista extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object valor, boolean seleccionada,
                boolean enfocada, int fila, int columna) {

            JLabel celda = (JLabel) super.getTableCellRendererComponent(
                t, valor, seleccionada, enfocada, fila, columna);

            celda.setBorder(new EmptyBorder(0, 16, 0, 16));
            celda.setOpaque(true);
            celda.setIcon(null);

            Color colorFila = (fila % 2 == 0) ? new Color(16, 14, 38) : new Color(19, 17, 44);
            celda.setBackground(seleccionada ? new Color(139, 92, 246, 50) : colorFila);
            celda.setForeground(COLOR_TEXTO_PRIM);

            if (columna == COL_ID)        aplicarEstiloId(celda);
            if (columna == COL_ESTADO)    aplicarEstiloEstado(celda, valor);
            if (columna == COL_CANCIONES) aplicarEstiloCanciones(celda, valor);

            return celda;
        }

        private void aplicarEstiloId(JLabel celda) {
            celda.setForeground(COLOR_MORADO);
            celda.setFont(celda.getFont().deriveFont(Font.BOLD, 11f));
        }

        private void aplicarEstiloEstado(JLabel celda, Object valor) {
            if (valor == null) return;
            String estado = valor.toString();
            Color color;
            if      (Artista.ESTADO_ACTIVO.equals(estado))   color = COLOR_VERDE;
            else if (Artista.ESTADO_EN_GIRA.equals(estado))  color = COLOR_CYAN;
            else if (Artista.ESTADO_HIATUS.equals(estado))   color = COLOR_AMBAR;
            else                                              color = COLOR_ROSA;
            celda.setForeground(color);
            celda.setFont(celda.getFont().deriveFont(Font.BOLD));
            celda.setText("● " + estado);
        }

        private void aplicarEstiloCanciones(JLabel celda, Object valor) {
            if (valor == null) return;
            int cantidad = 0;
            try { cantidad = Integer.parseInt(valor.toString()); }
            catch (NumberFormatException ignored) {}
            celda.setText(String.valueOf(cantidad));
            celda.setIcon(crearBarraProgreso(cantidad, 120, 3, COLOR_MORADO));
            celda.setHorizontalTextPosition(JLabel.LEFT);
            celda.setIconTextGap(6);
        }
    }

    private Icon crearBarraProgreso(int valor, int maximo, int alto, Color color) {
        return new Icon() {
            @Override public int getIconWidth()  { return 54; }
            @Override public int getIconHeight() { return alto + 4; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillRoundRect(x, y + 2, getIconWidth(), alto, alto, alto);
                int relleno = (int)(getIconWidth() * Math.min(valor / (float) maximo, 1f));
                if (relleno > 0) {
                    g2.setColor(color);
                    g2.fillRoundRect(x, y + 2, relleno, alto, alto, alto);
                }
                g2.dispose();
            }
        };
    }

    // ── Carga y actualización de datos ───────────────────────────────

    private void cargarArtistasDesdeServicio() {
        ejecutarEnHiloSegundo(
            () -> artistaService.obtenerTodos(),
            this::poblarTablaConArtistas,
            "Error al cargar artistas"
        );
    }

    private void ejecutarBusqueda() {
        String texto = campoBusqueda.getText().trim();
        ejecutarEnHiloSegundo(
            () -> artistaService.buscar(texto),
            this::poblarTablaConArtistas,
            "Error al buscar artistas"
        );
    }

    /**
     * Ejecuta la consulta en un SwingWorker para no bloquear el EDT.
     * Principio 3 — función pequeña que abstrae el patrón SwingWorker.
     */
    private void ejecutarEnHiloSegundo(java.util.concurrent.Callable<List<Artista>> consulta,
                                        java.util.function.Consumer<List<Artista>> alTerminar,
                                        String mensajeError) {
        new SwingWorker<List<Artista>, Void>() {
            @Override protected List<Artista> doInBackground() throws Exception {
                return consulta.call();
            }
            @Override protected void done() {
                try {
                    alTerminar.accept(get());
                } catch (Exception ex) {
                    mostrarError(mensajeError + ": " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void poblarTablaConArtistas(List<Artista> artistas) {
        modeloTabla.setRowCount(0);
        for (Artista a : artistas) {
            modeloTabla.addRow(new Object[]{
                a.getIdentificacion(),
                a.getNombre(),
                a.getGenero(),
                a.getPais(),
                a.getCantidadCanciones(),
                a.getEstado()
            });
        }
        actualizarEstadisticas(artistas);
    }

    private void actualizarEstadisticas(List<Artista> artistas) {
        long activos  = artistas.stream().filter(a -> Artista.ESTADO_ACTIVO.equals(a.getEstado())).count();
        long paises   = artistas.stream().map(Artista::getPais).distinct().count();
        int canciones = artistas.stream().mapToInt(Artista::getCantidadCanciones).sum();

        lblTotalArtistas.setText(String.valueOf(artistas.size()));
        lblArtistaActivos.setText(String.valueOf(activos));
        lblPaisesDistintos.setText(String.valueOf(paises));
        lblTotalCanciones.setText(String.valueOf(canciones));
    }

    // ── Acciones de botones ──────────────────────────────────────────

    private void accionEditar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            toast("Selecciona un artista primero", MainFrame.ToastType.INFO);
            return;
        }
        abrirDialogEditar(fila);
    }

    private void accionEliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            toast("Selecciona un artista primero", MainFrame.ToastType.INFO);
            return;
        }
        String nombre = modeloTabla.getValueAt(fila, COL_NOMBRE).toString();
        int id        = (int) modeloTabla.getValueAt(fila, COL_ID);

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar a \"" + nombre + "\"?", "Z-One — Confirmar",
            JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            ejecutarEnHiloSegundo(
                () -> { artistaService.darDeBaja(id); return artistaService.obtenerTodos(); },
                artistas -> {
                    poblarTablaConArtistas(artistas);
                    toast("Artista eliminado correctamente", MainFrame.ToastType.SUCCESS);
                },
                "Error al eliminar artista"
            );
        }
    }

    // ── Diálogos de crear / editar ───────────────────────────────────

    private void abrirDialogCrear() {
        abrirDialogFormulario(null);
    }

    private void abrirDialogEditar(int filaSeleccionada) {
        abrirDialogFormulario(filaSeleccionada);
    }

    private void abrirDialogFormulario(Integer filaEditar) {
        boolean esEdicion = (filaEditar != null);

        // Extraer valores actuales si es edición
        int    idActual       = esEdicion ? (int)    modeloTabla.getValueAt(filaEditar, COL_ID)        : 0;
        String nombreActual   = esEdicion ? (String) modeloTabla.getValueAt(filaEditar, COL_NOMBRE)    : "";
        String generoActual   = esEdicion ? (String) modeloTabla.getValueAt(filaEditar, COL_GENERO)    : "";
        String paisActual     = esEdicion ? (String) modeloTabla.getValueAt(filaEditar, COL_PAIS)      : "";
        String cancionesActual= esEdicion ? modeloTabla.getValueAt(filaEditar, COL_CANCIONES).toString(): "0";
        String estadoActual   = esEdicion ? (String) modeloTabla.getValueAt(filaEditar, COL_ESTADO)    : Artista.ESTADO_ACTIVO;

        JDialog dialogo = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            esEdicion ? "Editar artista" : "Nuevo artista", true);
        dialogo.setResizable(false);

        JPanel contenido = construirContenidoDialogo(
            esEdicion, idActual,
            nombreActual, generoActual, paisActual,
            cancionesActual, estadoActual, dialogo);

        dialogo.setContentPane(contenido);
        dialogo.pack();
        dialogo.setMinimumSize(new Dimension(520, dialogo.getPreferredSize().height));
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    private JPanel construirContenidoDialogo(
            boolean esEdicion, int id,
            String nombreActual, String generoActual, String paisActual,
            String cancionesActual, String estadoActual, JDialog dialogo) {

        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint fondoDeg = new GradientPaint(
                    0, 0, new Color(10, 8, 28), getWidth(), getHeight(), new Color(20, 16, 50));
                g2.setPaint(fondoDeg);
                g2.fillRect(0, 0, getWidth(), getHeight());
                RadialGradientPaint glow = new RadialGradientPaint(
                    getWidth() * 0.5f, 0, getWidth() * 0.7f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(139, 92, 246, 40), new Color(139, 92, 246, 0)});
                g2.setPaint(glow);
                g2.fillRect(0, 0, getWidth(), getHeight() / 2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(28, 32, 28, 32));

        // Título del diálogo
        panel.add(construirTituloDialogo(esEdicion));
        panel.add(Box.createVerticalStrut(10));
        panel.add(construirDividerMorado());
        panel.add(Box.createVerticalStrut(22));

        // Campos del formulario
        JTextField campoNombre    = construirCampoTexto(nombreActual);
        JTextField campoGenero    = construirCampoTexto(generoActual);
        JTextField campoPais      = construirCampoTexto(paisActual);
        JTextField campoCanciones = construirCampoTexto(cancionesActual);
        JComboBox<String> comboEstado = construirComboEstado(estadoActual);

        panel.add(construirFilaDoble("Nombre artístico *", campoNombre, "Género musical *", campoGenero));
        panel.add(Box.createVerticalStrut(14));
        panel.add(construirFilaDoble("País de origen", campoPais, "N.º de canciones", campoCanciones));
        panel.add(Box.createVerticalStrut(14));
        panel.add(construirFilaCampo("Estado", comboEstado));
        panel.add(Box.createVerticalStrut(28));

        // Botones
        panel.add(construirBotonesDialogo(
            esEdicion, id,
            campoNombre, campoGenero, campoPais,
            campoCanciones, comboEstado, dialogo));

        return panel;
    }

    private JPanel construirTituloDialogo(boolean esEdicion) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.add(etiquetaConFuente(esEdicion ? "✏" : "🎤",
            new Font("Segoe UI Emoji", Font.PLAIN, 20), COLOR_TEXTO_PRIM));
        fila.add(etiquetaConFuente(esEdicion ? "Editar artista" : "Nuevo artista",
            new Font("Segoe UI", Font.BOLD, 20), COLOR_TEXTO_PRIM));
        return fila;
    }

    private JPanel construirDividerMorado() {
        JPanel div = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(139, 92, 246, 180),
                    getWidth() * 0.6f, 0, new Color(139, 92, 246, 0)));
                g2.fillRect(0, 0, getWidth(), 2);
                g2.dispose();
            }
        };
        div.setOpaque(false);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        div.setPreferredSize(new Dimension(0, 2));
        div.setAlignmentX(LEFT_ALIGNMENT);
        return div;
    }

    private JPanel construirBotonesDialogo(
            boolean esEdicion, int id,
            JTextField campoNombre, JTextField campoGenero,
            JTextField campoPais, JTextField campoCanciones,
            JComboBox<String> comboEstado, JDialog dialogo) {

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);

        ModernUI.RoundedButton btnCancelar = new ModernUI.RoundedButton("Cancelar", false);
        ModernUI.RoundedButton btnGuardar  = new ModernUI.RoundedButton(
            esEdicion ? "Guardar cambios" : "Crear artista", true);

        btnCancelar.addActionListener(e -> dialogo.dispose());
        btnGuardar.addActionListener(e ->
            accionGuardar(esEdicion, id, campoNombre, campoGenero,
                campoPais, campoCanciones, comboEstado, dialogo));

        fila.add(btnCancelar);
        fila.add(btnGuardar);
        return fila;
    }

    private void accionGuardar(boolean esEdicion, int id,
                                JTextField campoNombre, JTextField campoGenero,
                                JTextField campoPais, JTextField campoCanciones,
                                JComboBox<String> comboEstado, JDialog dialogo) {
        String nombre    = campoNombre.getText().trim();
        String genero    = campoGenero.getText().trim();
        String pais      = campoPais.getText().trim();
        String estado    = (String) comboEstado.getSelectedItem();
        int    canciones;

        try {
            canciones = campoCanciones.getText().trim().isEmpty()
                ? 0 : Integer.parseInt(campoCanciones.getText().trim());
        } catch (NumberFormatException ex) {
            toast("N.º de canciones debe ser un número entero", MainFrame.ToastType.ERROR);
            return;
        }

        String correo   = "";   // extensión futura
        String telefono = "";   // extensión futura

        ejecutarEnHiloSegundo(
            () -> {
                if (esEdicion) {
                    artistaService.modificar(id, nombre, correo, telefono, genero, pais, canciones, estado);
                } else {
                    artistaService.registrar(nombre, correo, telefono, genero, pais, canciones, estado);
                }
                return artistaService.obtenerTodos();
            },
            artistas -> {
                poblarTablaConArtistas(artistas);
                toast(esEdicion ? "Artista actualizado" : "Artista creado: " + nombre,
                    MainFrame.ToastType.SUCCESS);
                dialogo.dispose();
            },
            "Error al guardar artista"
        );
    }

    // ── Helpers de construcción de UI ────────────────────────────────

    private JTextField construirCampoTexto(String valorInicial) {
        JTextField campo = new JTextField(valorInicial) {
            @Override protected void paintComponent(Graphics g) {
                pintarFondoRedondeado(this,g, COLOR_FONDO_CAMPO, COLOR_BORDE, 8);
                super.paintComponent(g);
            }
        };
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setForeground(COLOR_TEXTO_PRIM);
        campo.setOpaque(false);
        campo.setCaretColor(COLOR_TEXTO_PRIM);
        campo.setBorder(new EmptyBorder(0, 12, 0, 12));
        campo.setPreferredSize(new Dimension(200, 38));
        return campo;
    }

    @SuppressWarnings("unchecked")
    private JComboBox<String> construirComboEstado(String seleccionActual) {
        JComboBox<String> combo = new JComboBox<>(Artista.ESTADOS_VALIDOS) {
            @Override protected void paintComponent(Graphics g) {
              pintarFondoRedondeado(this, g, COLOR_FONDO_CARD, COLOR_BORDE, 10);
                super.paintComponent(g);
            }
        };
        combo.setSelectedItem(seleccionActual);
        combo.setOpaque(false);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setForeground(COLOR_TEXTO_PRIM);
        combo.setBackground(COLOR_FONDO_CAMPO);
        combo.setBorder(new EmptyBorder(0, 0, 0, 0));
        combo.setRenderer(new RendererComboEstado());
        combo.addPopupMenuListener(new EstilizadorPopupCombo(combo));
        return combo;
    }

    private JPanel construirFilaCampo(String etiqueta, JComponent campo) {
        JPanel fila = new JPanel(new BorderLayout(0, 6));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        JLabel lbl = etiquetaConFuente(etiqueta, new Font("Segoe UI", Font.BOLD, 10),
            new Color(139, 92, 246, 200));
        fila.add(lbl,   BorderLayout.NORTH);
        fila.add(campo, BorderLayout.CENTER);
        return fila;
    }

    private JPanel construirFilaDoble(String lbl1, JComponent campo1,
                                       String lbl2, JComponent campo2) {
        JPanel fila = new JPanel(new GridLayout(1, 2, 14, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        fila.add(construirFilaCampo(lbl1, campo1));
        fila.add(construirFilaCampo(lbl2, campo2));
        return fila;
    }

    // ── Helpers visuales reutilizables ───────────────────────────────

    /** Pinta fondo redondeado en un componente custom. */
private void pintarFondoRedondeado(JComponent comp, Graphics g,
                                   Color fondo, Color borde, int radio) {

    Graphics2D g2 = (Graphics2D) g.create();

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

    int width  = comp.getWidth();
    int height = comp.getHeight();

    g2.setColor(fondo);
    g2.fillRoundRect(0, 0, width, height, radio, radio);

    g2.setColor(borde);
    g2.drawRoundRect(0, 0, width - 1, height - 1, radio, radio);

    g2.dispose();
}

    private JLabel etiquetaConFuente(String texto, Font fuente, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente);
        lbl.setForeground(color);
        return lbl;
    }

    private void mostrarError(String mensaje) {
        toast(mensaje, MainFrame.ToastType.ERROR);
    }

    private void toast(String mensaje, MainFrame.ToastType tipo) {
        MainFrame.showToast(mensaje, tipo);
    }

    // ── Clases internas de estilo ────────────────────────────────────

    /** Renderer para items del combo de estado. */
    private class RendererComboEstado extends DefaultListCellRenderer {
        @Override public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean sel, boolean foc) {
            JLabel lbl = new JLabel(value == null ? "" : value.toString());
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(COLOR_TEXTO_PRIM);
            lbl.setBorder(new EmptyBorder(8, 14, 8, 14));
            lbl.setOpaque(true);
            lbl.setBackground(sel ? COLOR_MORADO : COLOR_FONDO_CAMPO);
            return lbl;
        }
    }

    /** Estiliza el popup del JComboBox al abrirse. */
    private static class EstilizadorPopupCombo implements javax.swing.event.PopupMenuListener {
        private final JComboBox<?> combo;
        EstilizadorPopupCombo(JComboBox<?> combo) { this.combo = combo; }

        @Override public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
            Object popup = combo.getUI().getAccessibleChild(combo, 0);
            if (popup instanceof JComponent jp) {
                jp.setBorder(BorderFactory.createLineBorder(new Color(139, 92, 246, 120), 1));
                for (Component c : jp.getComponents()) {
                    if (c instanceof JScrollPane sp) {
                        sp.getViewport().setBackground(new Color(20, 18, 48));
                        sp.setBorder(BorderFactory.createEmptyBorder());
                    }
                }
            }
        }
        @Override public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
        @Override public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
    }
}