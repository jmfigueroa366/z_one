package view;

import css.EventoComponents;
import css.EventoComponents.*;
import css.EventoStyles;
import css.EventoStyles.*;
import model.Evento;
import services.EventoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * formEvento — Vista rediseñada usando EventoStyles + EventoComponents.
 * Layout:
 *   NORTE  → encabezado + 4 stat cards
 *   CENTRO → lista de eventos (izq) + panel lateral (der)
 */
public class formEvento extends JPanel {

    // ── Formato fechas ────────────────────────────────────────────────
    private static final DateTimeFormatter FMT  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FHOR = DateTimeFormatter.ofPattern("HH:mm");

    // ── Tipos de evento ───────────────────────────────────────────────
    private static final String[] TIPOS = {
        "Concierto", "Sesion", "Lanzamiento", "Entrevista", "Ensayo"
    };

    // ── Servicio ──────────────────────────────────────────────────────
    private final EventoService servicio = new EventoService();

    // ── Timers para animaciones ───────────────────────────────────────
    private final List<Timer> timers = new ArrayList<>();

    // ── Componentes de estado ─────────────────────────────────────────
    private final List<Evento> listaEventos = new ArrayList<>();
    private Evento eventoSeleccionado = null;

    // Stat labels
    private final JLabel lblTotal      = new JLabel("0");
    private final JLabel lblConciertos = new JLabel("0");
    private final JLabel lblProximos   = new JLabel("0");
    private final JLabel lblHoy        = new JLabel("0");

    // Panel lista de eventos
    private JPanel listaContainer;

    // Panel detalle lateral
    private JPanel panelDetalleSwitch;
    private JPanel panelDetalleVacio;
    private JPanel panelDetalleContenido;
    private JLabel dTitulo, dTipo, dFecha, dHorario, dArtista, dProductor, dDesc;

    // ══════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ══════════════════════════════════════════════════════════════════
    public formEvento() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(0, 0, 0, 0));
        construirUI();
        recargar();
    }

    // ══════════════════════════════════════════════════════════════════
    //  CONSTRUCCIÓN UI
    // ══════════════════════════════════════════════════════════════════
private void construirUI() {
    JPanel fondo = new JPanel(new BorderLayout(0, 0)) {
        @Override protected void paintComponent(Graphics g) {
            g.setColor(EventoStyles.BG_MAIN);
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
    };
    fondo.setOpaque(false);

    // ── Encabezado ──
    JPanel encabezado = new JPanel(new BorderLayout());
    encabezado.setOpaque(false);
    encabezado.setBorder(new EmptyBorder(24, 28, 0, 28));
    encabezado.add(construirEncabezado(), BorderLayout.CENTER);

    // ── Stats ──
    JPanel statsWrap = new JPanel(new BorderLayout());
    statsWrap.setOpaque(false);
    statsWrap.setBorder(new EmptyBorder(18, 28, 10, 28));
    statsWrap.add(construirStatsRow(), BorderLayout.CENTER);

    // ── Norte = encabezado + stats apilados ──
    JPanel norte = new JPanel(new BorderLayout());
    norte.setOpaque(false);
    norte.add(encabezado, BorderLayout.NORTH);
    norte.add(statsWrap,  BorderLayout.CENTER);

    // ── Centro ──
    JPanel centro = new JPanel(new BorderLayout(14, 0));
    centro.setOpaque(false);
    centro.setBorder(new EmptyBorder(0, 28, 28, 28));
    centro.add(construirPanelLista(),   BorderLayout.CENTER);
    centro.add(construirPanelDetalle(), BorderLayout.EAST);

    fondo.add(norte,  BorderLayout.NORTH);
    fondo.add(centro, BorderLayout.CENTER);
    add(fondo, BorderLayout.CENTER);
}
// ══════════════════════════════════════════════════════════════════
//  ENCABEZADO
// ══════════════════════════════════════════════════════════════════
private JPanel construirEncabezado() {
    JPanel p = new JPanel(new BorderLayout(16, 0));
    p.setOpaque(false);

    // Título + subtítulo
    JPanel izq = new JPanel();
    izq.setOpaque(false);
    izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

    JLabel title = EventoStyles.lbl("Eventos Agendados", 26, true, EventoStyles.TEXT_PRI);
    JLabel sub   = EventoStyles.lbl(
        "GESTIÓN DE CONCIERTOS  ·  SESIONES  ·  LANZAMIENTOS",
        9, true, EventoStyles.TEXT_MUT);
    title.setAlignmentX(LEFT_ALIGNMENT);
    sub  .setAlignmentX(LEFT_ALIGNMENT);
    izq.add(title);
    izq.add(Box.createVerticalStrut(5));
    izq.add(sub);

    // Acciones
    JPanel acc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    acc.setOpaque(false);

    BtnEvento btnNuevo   = EventoStyles.btnAccion("＋ Nuevo evento", true, 155);
    BtnEvento btnEditar  = EventoStyles.btnAccion("✏  Editar",       false, 110);
    BtnEvento btnElim    = EventoStyles.btnAccion("🗑  Eliminar",     false, 115,
                                EventoStyles.COLOR_ENSAYO);
    BtnEvento btnRefresh = EventoStyles.btnAccion("↺  Refrescar",    false, 120);

    btnNuevo  .addActionListener(e -> abrirFormulario(null));
    btnEditar .addActionListener(e -> {
        if (eventoSeleccionado != null) abrirFormulario(eventoSeleccionado);
        else toast("Selecciona un evento primero", MainFrame.ToastType.INFO);
    });
    btnElim   .addActionListener(e -> eliminar());
    btnRefresh.addActionListener(e -> recargar());

    acc.add(btnRefresh);
    acc.add(btnEditar);
    acc.add(btnElim);
    acc.add(btnNuevo);

    p.add(izq, BorderLayout.WEST);
    p.add(acc, BorderLayout.EAST);
    return p;
}

    // ══════════════════════════════════════════════════════════════════
    //  STAT CARDS
    // ══════════════════════════════════════════════════════════════════
private JPanel construirStatsRow() {
    JPanel p = new JPanel(new GridLayout(1, 4, 12, 0));
    p.setOpaque(false);
    p.setPreferredSize(new Dimension(0, 100)); // ← solo altura, ancho lo maneja BorderLayout
    p.add(new StatCardEvento("TOTAL EVENTOS",   lblTotal,      EventoStyles.INDIGO,           "eventos registrados"));
    p.add(new StatCardEvento("CONCIERTOS",      lblConciertos, EventoStyles.COLOR_CONCIERTO,  "tipo concierto"));
    p.add(new StatCardEvento("PRÓXIMOS",        lblProximos,   EventoStyles.COLOR_SESION,     "desde hoy"));
    p.add(new StatCardEvento("HOY",             lblHoy,        EventoStyles.COLOR_LANZAMIENTO,"programados hoy"));
    return p;
}

    private void actualizarStats() {
        LocalDate hoy = LocalDate.now();
        long conciertos = listaEventos.stream()
            .filter(e -> "Concierto".equals(e.getNombreTipoEvento())).count();
        long proximos = listaEventos.stream()
            .filter(e -> e.getFecha() != null && !e.getFecha().isBefore(hoy)).count();
        long hoyCount = listaEventos.stream()
            .filter(e -> hoy.equals(e.getFecha())).count();

        lblTotal     .setText(String.valueOf(listaEventos.size()));
        lblConciertos.setText(String.valueOf(conciertos));
        lblProximos  .setText(String.valueOf(proximos));
        lblHoy       .setText(String.valueOf(hoyCount));
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL LISTA DE EVENTOS
    // ══════════════════════════════════════════════════════════════════
    private JPanel construirPanelLista() {
        GlassPanel card = new GlassPanel(16, true, EventoStyles.INDIGO);
        card.setLayout(new BorderLayout());

        // Cabecera de la lista
        JPanel cabLista = new JPanel(new BorderLayout());
        cabLista.setOpaque(false);
        cabLista.setBorder(new EmptyBorder(14, 18, 10, 18));
        JLabel lTit = EventoStyles.lbl("Lista de eventos", 14, true, EventoStyles.TEXT_PRI);
        cabLista.add(lTit, BorderLayout.WEST);

        // Contenedor de filas
        listaContainer = new JPanel();
        listaContainer.setOpaque(false);
        listaContainer.setLayout(new BoxLayout(listaContainer, BoxLayout.Y_AXIS));
        listaContainer.setBorder(new EmptyBorder(4, 10, 10, 10));

        JPanel wrapLista = new JPanel(new BorderLayout());
        wrapLista.setOpaque(false);
        wrapLista.add(listaContainer, BorderLayout.NORTH);

        JScrollPane sc = new JScrollPane(wrapLista);
        sc.setOpaque(false);
        sc.getViewport().setOpaque(false);
        sc.getViewport().setBackground(new Color(0, 0, 0, 0));
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.getVerticalScrollBar().setUnitIncrement(16);
        sc.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));

        card.add(cabLista, BorderLayout.NORTH);
        card.add(sc,       BorderLayout.CENTER);
        return card;
    }

    private void poblarLista() {
        listaContainer.removeAll();

        if (listaEventos.isEmpty()) {
            JLabel vacio = EventoStyles.lbl("No hay eventos registrados", 13, false, EventoStyles.TEXT_MUT);
            vacio.setBorder(new EmptyBorder(30, 20, 30, 20));
            vacio.setAlignmentX(LEFT_ALIGNMENT);
            listaContainer.add(vacio);
        } else {
            for (Evento e : listaEventos) {
                listaContainer.add(crearFilaEvento(e));
                listaContainer.add(Box.createVerticalStrut(8));
            }
        }
        listaContainer.revalidate();
        listaContainer.repaint();
    }

    private JPanel crearFilaEvento(Evento e) {
        boolean activo = e == eventoSeleccionado;
        Color acento   = EventoStyles.colorTipo(e.getNombreTipoEvento());

        EventoCard card = new EventoCard(activo, acento);
        card.setLayout(new BorderLayout(12, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        card.setAlignmentX(LEFT_ALIGNMENT);

        // Ícono tipo
        JLabel icoTipo = new JLabel(EventoStyles.iconoTipo(e.getNombreTipoEvento()),
            SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(EventoStyles.paleBgTipo(e.getNombreTipoEvento()));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icoTipo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        icoTipo.setPreferredSize(new Dimension(42, 42));
        icoTipo.setOpaque(false);

        // Info central
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        String desc = e.getDescripcion() != null ? e.getDescripcion() : "(sin descripción)";
        JLabel lDesc = EventoStyles.lbl(recortar(desc, 35), 13, true, EventoStyles.TEXT_PRI);
        lDesc.setAlignmentX(LEFT_ALIGNMENT);

        String fechaStr = e.getFecha() != null ? e.getFecha().format(FMT) : "—";
        String horaStr  = (e.getHoraInicio() != null ? e.getHoraInicio().format(FHOR) : "") +
                          (e.getHoraFin() != null ? " – " + e.getHoraFin().format(FHOR) : "");
        JLabel lSub = EventoStyles.lbl(fechaStr + (horaStr.isBlank() ? "" : "   " + horaStr),
            10, false, EventoStyles.TEXT_MUT);
        lSub.setAlignmentX(LEFT_ALIGNMENT);

        info.add(lDesc);
        info.add(Box.createVerticalStrut(3));
        info.add(lSub);

        // Píldora tipo (derecha)
        PildoraTipo pill = new PildoraTipo(e.getNombreTipoEvento());

        card.add(icoTipo, BorderLayout.WEST);
        card.add(info,    BorderLayout.CENTER);
        card.add(pill,    BorderLayout.EAST);

        // Hover + click
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent ev)  { card.startHover(); }
            @Override public void mouseExited (java.awt.event.MouseEvent ev)  { card.stopHover(); }
            @Override public void mouseClicked(java.awt.event.MouseEvent ev) {
                seleccionar(e);
                if (ev.getClickCount() == 2) abrirFormulario(e);
            }
        });

        return card;
    }

    // ══════════════════════════════════════════════════════════════════
    //  PANEL DETALLE LATERAL
    // ══════════════════════════════════════════════════════════════════
    private JPanel construirPanelDetalle() {
        panelDetalleSwitch = new GlassPanel(16, true, EventoStyles.SKY);
        panelDetalleSwitch.setLayout(new BorderLayout());
        panelDetalleSwitch.setPreferredSize(new Dimension(275, 0));

        // Estado vacío
        panelDetalleVacio = new JPanel(new GridBagLayout());
        panelDetalleVacio.setOpaque(false);
        JPanel vi = new JPanel();
        vi.setOpaque(false);
        vi.setLayout(new BoxLayout(vi, BoxLayout.Y_AXIS));
        JLabel viI = new JLabel("🎟", SwingConstants.CENTER);
        viI.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        viI.setForeground(EventoStyles.TEXT_MUT);
        viI.setAlignmentX(CENTER_ALIGNMENT);
        JLabel viM = new JLabel("<html><center>Selecciona un evento<br>para ver el detalle</center></html>");
        viM.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        viM.setForeground(EventoStyles.TEXT_MUT);
        viM.setAlignmentX(CENTER_ALIGNMENT);
        vi.add(viI); vi.add(Box.createVerticalStrut(10)); vi.add(viM);
        panelDetalleVacio.add(vi);

        // Estado con contenido
        panelDetalleContenido = new JPanel();
        panelDetalleContenido.setOpaque(false);
        panelDetalleContenido.setLayout(new BoxLayout(panelDetalleContenido, BoxLayout.Y_AXIS));
        panelDetalleContenido.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel encab = EventoStyles.lbl("DETALLE DE EVENTO", 9, true, EventoStyles.SKY);
        encab.setAlignmentX(LEFT_ALIGNMENT);

        dTitulo    = mkDetalle(14, true);
        dTipo      = mkDetalle(11, false);
        dFecha     = mkDetalle(12, false);
        dHorario   = mkDetalle(11, false);
        dArtista   = mkDetalle(11, false);
        dProductor = mkDetalle(11, false);
        dDesc      = mkDetalle(11, false);

        panelDetalleContenido.add(encab);
        panelDetalleContenido.add(Box.createVerticalStrut(6));
        panelDetalleContenido.add(dTitulo);
        panelDetalleContenido.add(Box.createVerticalStrut(4));
        panelDetalleContenido.add(dTipo);
        panelDetalleContenido.add(Box.createVerticalStrut(12));
        panelDetalleContenido.add(sepDetalle());
        panelDetalleContenido.add(Box.createVerticalStrut(10));
        panelDetalleContenido.add(filaDetalle("📅  Fecha",     dFecha));
        panelDetalleContenido.add(filaDetalle("⏰  Horario",   dHorario));
        panelDetalleContenido.add(filaDetalle("🎤  Artista",   dArtista));
        panelDetalleContenido.add(filaDetalle("🎛  Productor", dProductor));
        panelDetalleContenido.add(filaDetalle("📝  Descripción", dDesc));
        panelDetalleContenido.add(Box.createVerticalStrut(16));

        // Botones acción
        JPanel bRow = new JPanel(new GridLayout(1, 2, 10, 0));
        bRow.setOpaque(false);
        bRow.setAlignmentX(LEFT_ALIGNMENT);
        bRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        BtnEvento bEd = EventoStyles.btnAccion("✏  Editar",   true,  115);
        BtnEvento bEl = EventoStyles.btnAccion("🗑  Eliminar", false, 115,
                            EventoStyles.COLOR_ENSAYO);
        bEd.addActionListener(e -> { if (eventoSeleccionado!=null) abrirFormulario(eventoSeleccionado); });
        bEl.addActionListener(e -> eliminar());
        bRow.add(bEd); bRow.add(bEl);
        panelDetalleContenido.add(bRow);

        panelDetalleSwitch.add(panelDetalleVacio, BorderLayout.CENTER);
        return panelDetalleSwitch;
    }

    private JLabel mkDetalle(int size, boolean bold) {
        JLabel l = EventoStyles.lbl("—", size, bold, EventoStyles.TEXT_PRI);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JPanel filaDetalle(String etiqueta, JLabel valor) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setBorder(new EmptyBorder(0, 0, 8, 0));
        JLabel et = EventoStyles.lbl(etiqueta, 9, true, EventoStyles.TEXT_MUT);
        et.setAlignmentX(LEFT_ALIGNMENT);
        p.add(et); p.add(Box.createVerticalStrut(2)); p.add(valor);
        return p;
    }

    private JPanel sepDetalle() {
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(EventoStyles.BORDER);
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        sep.setOpaque(false);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setPreferredSize(new Dimension(0, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        return sep;
    }

    private void refrescarDetalle() {
        panelDetalleSwitch.removeAll();
        if (eventoSeleccionado == null) {
            panelDetalleSwitch.add(panelDetalleVacio, BorderLayout.CENTER);
        } else {
            Evento e = eventoSeleccionado;
            Color ac = EventoStyles.colorTipo(e.getNombreTipoEvento());

            dTitulo.setText(e.getDescripcion() != null ? e.getDescripcion() : "—");
            dTitulo.setForeground(EventoStyles.TEXT_PRI);

            String tipo = e.getNombreTipoEvento() != null
                ? EventoStyles.iconoTipo(e.getNombreTipoEvento()) + "  " + e.getNombreTipoEvento()
                : "—";
            dTipo.setText(tipo);
            dTipo.setForeground(ac);
            dTipo.setFont(new Font("Segoe UI", Font.BOLD, 11));

            dFecha    .setText(e.getFecha() != null ? e.getFecha().format(FMT) : "—");
            dHorario  .setText(
                (e.getHoraInicio()!=null ? e.getHoraInicio().format(FHOR) : "—") +
                (e.getHoraFin()   !=null ? " — " + e.getHoraFin().format(FHOR) : ""));
            dArtista  .setText(e.getIdArtista()   != null ? "ID: " + e.getIdArtista()   : "—");
            dProductor.setText(e.getIdProductor() != null ? "ID: " + e.getIdProductor() : "—");
            dDesc     .setText(recortar(e.getDescripcion() != null ? e.getDescripcion() : "—", 40));

            panelDetalleSwitch.add(panelDetalleContenido, BorderLayout.CENTER);
        }
        panelDetalleSwitch.revalidate();
        panelDetalleSwitch.repaint();
    }

    // ══════════════════════════════════════════════════════════════════
    //  LÓGICA
    // ══════════════════════════════════════════════════════════════════
    private void recargar() {
        try {
            listaEventos.clear();
            listaEventos.addAll(servicio.listar());
        } catch (Exception ex) {
            // Sin conexión — lista vacía, no muestra error crítico
        }
        poblarLista();
        actualizarStats();
        eventoSeleccionado = null;
        refrescarDetalle();
    }

    private void seleccionar(Evento e) {
        eventoSeleccionado = e;
        poblarLista();
        refrescarDetalle();
    }

    private void eliminar() {
        if (eventoSeleccionado == null) {
            toast("Selecciona un evento primero", MainFrame.ToastType.INFO);
            return;
        }
        int op = JOptionPane.showConfirmDialog(this,
            "¿Eliminar \"" + eventoSeleccionado.getDescripcion() + "\"?",
            "Z-One — Confirmar", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            try {
                servicio.eliminar(eventoSeleccionado.getIdEvento());
                toast("Evento eliminado", MainFrame.ToastType.SUCCESS);
                recargar();
            } catch (Exception ex) {
                toast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  DIÁLOGO CREAR / EDITAR
    // ══════════════════════════════════════════════════════════════════
    private void abrirFormulario(Evento ev) {
        boolean isEdit = ev != null;
        List<Timer> dlgTimers = new ArrayList<>();

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Editar evento" : "Nuevo evento", true);
        dlg.setResizable(false);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(EventoStyles.BG_MAIN);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };

        // Banda superior animada
        BandaEvento banda = new BandaEvento(isEdit, dlgTimers);
        root.add(banda, BorderLayout.NORTH);

        // Shimmer
        LineaShimmer shimmer = new LineaShimmer(dlgTimers);
        shimmer.setPreferredSize(new Dimension(0, 3));

        // Formulario
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(22, 28, 22, 28));

        // Campos con animación
        FieldFx fDesc  = new FieldFx(isEdit && ev.getDescripcion()  != null ? ev.getDescripcion()  : "",
            "Descripción o nombre del evento", dlgTimers);
        FieldFx fFecha = new FieldFx(isEdit && ev.getFecha()        != null ? ev.getFecha().format(FMT) : LocalDate.now().format(FMT),
            "dd/MM/yyyy", dlgTimers);
        FieldFx fHIni  = new FieldFx(isEdit && ev.getHoraInicio()   != null ? ev.getHoraInicio().format(FHOR) : "19:00",
            "HH:mm", dlgTimers);
        FieldFx fHFin  = new FieldFx(isEdit && ev.getHoraFin()      != null ? ev.getHoraFin().format(FHOR) : "21:00",
            "HH:mm", dlgTimers);
        FieldFx fArt   = new FieldFx(isEdit && ev.getIdArtista()    != null ? String.valueOf(ev.getIdArtista())   : "", "ID del artista (opcional)", dlgTimers);
        FieldFx fProd  = new FieldFx(isEdit && ev.getIdProductor()  != null ? String.valueOf(ev.getIdProductor()) : "", "ID del productor (opcional)", dlgTimers);

        ComboFx<String> cbTipo = EventoComponents.comboFx(TIPOS,
            isEdit && ev.getNombreTipoEvento() != null ? ev.getNombreTipoEvento() : TIPOS[0],
            dlgTimers);

        // Filas del formulario
        form.add(filaDlg("DESCRIPCIÓN *",         fDesc,  "TIPO DE EVENTO",        cbTipo));
        form.add(Box.createVerticalStrut(14));
        form.add(filaDlg("FECHA * (dd/MM/yyyy)",   fFecha, "HORA INICIO (HH:mm)",   fHIni));
        form.add(Box.createVerticalStrut(14));
        form.add(filaDlg("HORA FIN (HH:mm)",       fHFin,  "ID ARTISTA (opcional)", fArt));
        form.add(Box.createVerticalStrut(14));
        form.add(envolver("ID PRODUCTOR (opcional)", fProd));
        form.add(Box.createVerticalStrut(24));

        // Botones
        JPanel bRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bRow.setOpaque(false);
        bRow.setAlignmentX(LEFT_ALIGNMENT);

        BtnFx bCancel = new BtnFx("Cancelar", false, dlgTimers);
        BtnFx bSave   = new BtnFx(isEdit ? "💾  Guardar cambios" : "🎟  Crear evento", true, dlgTimers);
        bCancel.setPreferredSize(new Dimension(130, 40));
        bSave  .setPreferredSize(new Dimension(185, 40));

        bCancel.addActionListener(e -> {
            dlgTimers.forEach(Timer::stop);
            dlg.dispose();
        });

        bSave.addActionListener(e -> {
            String desc  = fDesc .getText().trim();
            String fecha = fFecha.getText().trim();
            String hIni  = fHIni .getText().trim();
            String hFin  = fHFin .getText().trim();
            String art   = fArt  .getText().trim();
            String prod  = fProd .getText().trim();
            String tipo  = (String) cbTipo.getSelectedItem();

            if (desc.isEmpty()) {
                fDesc.shake();
                toast("La descripción es obligatoria", MainFrame.ToastType.ERROR);
                return;
            }
            LocalDate fechaP;
            try { fechaP = LocalDate.parse(fecha, FMT); }
            catch (DateTimeParseException ex) {
                fFecha.shake();
                toast("Fecha inválida (dd/MM/yyyy)", MainFrame.ToastType.ERROR);
                return;
            }
            LocalTime hiP = null, hfP = null;
            try { if (!hIni.isEmpty()) hiP = LocalTime.parse(hIni, FHOR); }
            catch (DateTimeParseException ex) { fHIni.shake(); toast("Hora inicio inválida", MainFrame.ToastType.ERROR); return; }
            try { if (!hFin.isEmpty()) hfP = LocalTime.parse(hFin, FHOR); }
            catch (DateTimeParseException ex) { fHFin.shake(); toast("Hora fin inválida",    MainFrame.ToastType.ERROR); return; }

            Evento n = isEdit ? ev : new Evento();
            n.setDescripcion(desc);
            n.setFecha(fechaP);
            n.setHoraInicio(hiP);
            n.setHoraFin(hfP);
            n.setIdArtista(art.isEmpty()  ? null : Integer.parseInt(art));
            n.setIdProductor(prod.isEmpty() ? null : Integer.parseInt(prod));
            n.setNombreTipoEvento(tipo);
            n.setTipoEvento(tipo);

            try {
                if (isEdit) servicio.actualizar(n);
                else        servicio.crear(n);
                toast(isEdit ? "Evento actualizado" : "Evento creado: " + desc,
                    MainFrame.ToastType.SUCCESS);
                dlgTimers.forEach(Timer::stop);
                recargar();
                dlg.dispose();
            } catch (Exception ex) {
                toast("Error: " + ex.getMessage(), MainFrame.ToastType.ERROR);
            }
        });

        bRow.add(bCancel);
        bRow.add(bSave);
        form.add(bRow);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(shimmer, BorderLayout.NORTH);
        wrap.add(form,    BorderLayout.CENTER);
        root.add(wrap, BorderLayout.CENTER);

        dlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                dlgTimers.forEach(Timer::stop);
            }
        });

        dlg.setContentPane(root);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(600, dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════
    //  HELPERS UI
    // ══════════════════════════════════════════════════════════════════
    private JPanel filaDlg(String l1, JComponent c1, String l2, JComponent c2) {
        JPanel p = new JPanel(new GridLayout(1, 2, 14, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        p.add(envolver(l1, c1));
        p.add(envolver(l2, c2));
        return p;
    }

    private JPanel envolver(String etq, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = EventoStyles.lbl(etq, 10, true, EventoStyles.INDIGO_LIGHT);
        p.add(l,     BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private JPanel envolver(String etq, FieldFx campo) {
        return envolver(etq, (JComponent) campo);
    }

    private String recortar(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }

    private void toast(String msg, MainFrame.ToastType t) {
        MainFrame.showToast(msg, t);
    }
}