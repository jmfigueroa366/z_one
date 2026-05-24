package view;

import model.Artista;
import model.Productor;
import model.Sesion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static view.ModernUI.*;

public class formSesion extends JPanel {

    // ─── Constantes ───────────────────────────────────────────────────────────────

    private static final String[] COLS   = {"ID","Nombre sesión","Fecha","Hora inicio","Hora fin","Artista","Productor","Estado"};
    private static final String[] ESTADOS = Sesion.ESTADOS_VALIDOS;

    private static final Color CF  = new Color(18,18,40);
    private static final Color CFD = new Color(24,24,52);
    private static final Color CM  = new Color(139,92,246);
    private static final Color COK = new Color(34,197,94);
    private static final Color CER = new Color(255,80,120);
    private static final Color CAD = new Color(250,180,40);
    private static final Color CI  = new Color(6,182,212);

    private static final Font FT = new Font("Segoe UI",Font.BOLD,26);
    private static final Font FS = new Font("Segoe UI",Font.PLAIN,13);
    private static final Font FE = new Font("Segoe UI",Font.PLAIN,11);
    private static final Font FH = new Font("Segoe UI",Font.BOLD,11);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─── Estado ───────────────────────────────────────────────────────────────────

    private final List<Artista>   artistas    = new ArrayList<>();
    private final List<Productor> productores = new ArrayList<>();
    private final List<String>    cabinas     = new ArrayList<>();
    private final List<Sesion>    sesiones    = new ArrayList<>();

    private DefaultTableModel modelo;
    private JTable            tabla;
    private JTextField        busqueda;
    private int               nextId = 4;

    // ─── Constructor ──────────────────────────────────────────────────────────────

    public formSesion() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0,0,0,0));
        cargarCombos();
        sesiones.add(new Sesion(1,artistas.get(0),productores.get(0),1,"Grabación Titi",
                LocalDate.of(2025,3,10),"09:00","13:00",4.0,Sesion.ESTADO_FINALIZADA,""));
        sesiones.add(new Sesion(2,artistas.get(1),productores.get(2),2,"Sesión TQG",
                LocalDate.of(2025,4,22),"14:00","19:30",5.5,Sesion.ESTADO_PROGRAMADA,""));
        sesiones.add(new Sesion(3,artistas.get(2),productores.get(1),1,"Sesión Pop",
                LocalDate.of(2025,5,1),"10:00","12:00",2.0,Sesion.ESTADO_EN_CURSO,""));
        add(headerPanel(), BorderLayout.NORTH);
        add(tableCard(),   BorderLayout.CENTER);
    }

    // ─── API pública ──────────────────────────────────────────────────────────────

    public void guardar() { /* SesionServicio.guardar(sesion) — pendiente */ }

    public void calcularYMostrarCosto(JComboBox<Productor> cp, JTextField cd, JLabel lbl) {
        Productor p = (Productor) cp.getSelectedItem();
        double h = parseDouble(cd.getText()).orElse(-1.0);
        if (p == null || h <= 0) { lbl.setText("Costo estimado: —"); lbl.setForeground(TEXT_MUTED); return; }
        lbl.setText(String.format("Costo estimado: $%.2f  (%.1f h × $%.0f/h)", h*p.getTarifaHora(), h, p.getTarifaHora()));
        lbl.setForeground(COK);
    }

    public void cargarCombos() {
        artistas.clear(); productores.clear(); cabinas.clear();
        artistas.add(new Artista(1, null, "Bad Bunny", "Benito Martínez",
                LocalDate.of(1994,3,10), "M", "Puerto Rico", "Reggaeton",
                "@badbunny", LocalDate.of(2016,1,1),
                Artista.ESTADO_ACTIVO, Artista.TIPO_SOLISTA));
        artistas.add(new Artista(2, null, "Karol G", "Carolina Giraldo",
                LocalDate.of(1991,2,14), "F", "Colombia", "Reggaeton",
                "@karolg", LocalDate.of(2017,1,1),
                Artista.ESTADO_ACTIVO, Artista.TIPO_SOLISTA));
        artistas.add(new Artista(3, null, "Shakira", "Shakira Mebarak",
                LocalDate.of(1977,2,2), "F", "Colombia", "Pop / Rock",
                "@shakira", LocalDate.of(2010,1,1),
                Artista.ESTADO_EN_PAUSA, Artista.TIPO_SOLISTA));
        productores.add(new Productor(1, "Carlos Vives",     "cvives@mail.com",  "3001234567", "Mezcla",        120.0));
        productores.add(new Productor(2, "Andrés Torres",    "atorres@mail.com", "3109876543", "Masterización",  95.0));
        productores.add(new Productor(3, "Mauricio Rengifo", "mrengifo@mail.com","3154561234", "Composición",   150.0));
        cabinas.add("Cabina A"); cabinas.add("Cabina B"); cabinas.add("Cabina C — Mastering");
    }

    // ─── UI principal ─────────────────────────────────────────────────────────────

    private JPanel headerPanel() {
        JPanel p = new JPanel(); p.setOpaque(false); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));

        JPanel enc = new JPanel(); enc.setOpaque(false); enc.setLayout(new BoxLayout(enc,BoxLayout.Y_AXIS));
        JLabel t = lbl("Sesiones",FT,TEXT_PRIMARY); t.setAlignmentX(LEFT_ALIGNMENT);
        JLabel s = lbl("Sesiones de grabación, cabinas y agenda",FS,TEXT_MUTED); s.setAlignmentX(LEFT_ALIGNMENT);
        enc.add(t); enc.add(Box.createVerticalStrut(4)); enc.add(s);

        JPanel bar = new JPanel(new BorderLayout(12,0)); bar.setOpaque(false); bar.setBorder(new EmptyBorder(16,0,12,0));
        busqueda = new JTextField();
        busqueda.setPreferredSize(new Dimension(280,36));
        busqueda.setFont(FS); busqueda.setForeground(TEXT_PRIMARY);
        busqueda.setBackground(new Color(20,20,45)); busqueda.setCaretColor(TEXT_PRIMARY);
        busqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139,92,246,80),1,true), new EmptyBorder(6,12,6,12)));
        busqueda.putClientProperty("JTextField.placeholderText","Buscar sesión...");
        busqueda.getDocument().addDocumentListener(docListener(() -> filtrarTabla()));
        ModernUI.RoundedButton bn = btn("+ Nueva sesión",true,160); bn.addActionListener(e -> openForm(null));
        bar.add(busqueda,BorderLayout.WEST); bar.add(bn,BorderLayout.EAST);

        p.add(enc); p.add(bar); return p;
    }

    private ModernUI.CardPanel tableCard() {
        modelo = new DefaultTableModel(COLS,0){ @Override public boolean isCellEditable(int r,int c){return false;} };
        refreshTable(sesiones);
        tabla = new JTable(modelo);
        styleTable();
        JScrollPane sc = new JScrollPane(tabla);
        sc.setBorder(BorderFactory.createEmptyBorder()); sc.setOpaque(false);
        sc.getViewport().setOpaque(false); sc.getViewport().setBackground(CF); sc.setBackground(CF);

        JPanel acc = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0)); acc.setOpaque(false); acc.setBorder(new EmptyBorder(12,0,0,0));
        ModernUI.RoundedButton be = btn("Editar",false,0), bd = btn("Eliminar",false,0), br = btn("↻ Refrescar",false,0);
        bd.setForeground(CER);
        be.addActionListener(e -> { int r=tabla.getSelectedRow(); if(r<0){toast("Selecciona una sesión primero",MainFrame.ToastType.INFO);}else openForm(r); });
        bd.addActionListener(e -> { int r=tabla.getSelectedRow(); if(r<0){toast("Selecciona una sesión primero",MainFrame.ToastType.INFO);return;}
            if(JOptionPane.showConfirmDialog(this,"¿Eliminar la sesión #"+(String)modelo.getValueAt(r,0)+"?","Z-One — Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                int id=Integer.parseInt((String)modelo.getValueAt(r,0)); sesiones.removeIf(s->s.getIdSesion()==id); refreshTable(sesiones); toast("Sesión eliminada correctamente",MainFrame.ToastType.SUCCESS);} });
        br.addActionListener(e -> { busqueda.setText(""); refreshTable(sesiones); toast("Lista actualizada",MainFrame.ToastType.INFO); });
        acc.add(be); acc.add(bd); acc.add(br);

        ModernUI.CardPanel card = new ModernUI.CardPanel(16); card.setLayout(new BorderLayout());
        card.add(sc,BorderLayout.CENTER); card.add(acc,BorderLayout.SOUTH); return card;
    }

    // ─── Tabla ────────────────────────────────────────────────────────────────────

    private void styleTable() {
        tabla.setOpaque(false); tabla.setBackground(CF); tabla.setForeground(TEXT_PRIMARY);
        tabla.setFont(FS); tabla.setRowHeight(40); tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0,0));
        tabla.setSelectionBackground(new Color(139,92,246,60)); tabla.setSelectionForeground(Color.WHITE);
        tabla.setFocusable(false); tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader h = tabla.getTableHeader();
        h.setBackground(new Color(13,13,30)); h.setForeground(TEXT_MUTED); h.setFont(FH);
        h.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(139,92,246,50))); h.setReorderingAllowed(false);
        int[] w={45,140,90,75,75,130,130,90};
        for(int i=0;i<w.length;i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                JLabel cl=(JLabel)super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                cl.setBorder(new EmptyBorder(0,14,0,14)); cl.setOpaque(true);
                cl.setBackground(sel?new Color(139,92,246,60):(r%2==0?CF:new Color(22,22,50)));
                cl.setForeground(c==0?TEXT_MUTED:c==7?stateColor(v==null?"":v.toString()):TEXT_PRIMARY);
                if(c==7) cl.setFont(cl.getFont().deriveFont(Font.BOLD));
                return cl;
            }
        });
    }

    private Color stateColor(String s){ return switch(s){ case "Finalizada"->COK; case "En curso"->CAD; case "Cancelada"->CER; default->CI; }; }

    private void refreshTable(List<Sesion> data) {
        modelo.setRowCount(0);
        data.forEach(s -> modelo.addRow(new Object[]{
            String.format("%03d",s.getIdSesion()), s.getNombreSesion(),
            s.getFecha().format(FMT), s.getHoraInicio(), s.getHoraFin(),
            s.getArtista().getNombreArtista(), s.getProductor().getNombre(), s.getEstadoSesion()
        }));
    }

    private void filtrarTabla() {
        String q = busqueda.getText().trim().toLowerCase();
        refreshTable(q.isEmpty() ? sesiones : sesiones.stream().filter(s ->
            s.getNombreSesion().toLowerCase().contains(q) ||
            s.getArtista().getNombreArtista().toLowerCase().contains(q) ||
            s.getProductor().getNombre().toLowerCase().contains(q) ||
            s.getFecha().format(FMT).contains(q)).toList());
    }

    // ─── Diálogo crear / editar ──────────────────────────────────────────────────

    private void openForm(Integer editRow) {
        boolean isEdit = editRow != null;
        Sesion se = isEdit ? sesiones.stream().filter(s->s.getIdSesion()==Integer.parseInt((String)modelo.getValueAt(editRow,0))).findFirst().orElse(null) : null;

        JDialog dlg = new JDialog((java.awt.Frame)SwingUtilities.getWindowAncestor(this), isEdit?"Editar sesión":"Nueva sesión", true);
        JPanel  pnl = darkPanel();

        JTextField fNombre = field(se!=null?se.getNombreSesion():"","Nombre de la sesión");
        JTextField fFecha  = field(se!=null?se.getFecha().format(FMT):"","dd/MM/yyyy");
        JTextField fHIni   = field(se!=null?se.getHoraInicio():"09:00","HH:mm");
        JTextField fHFin   = field(se!=null?se.getHoraFin():"12:00","HH:mm");
        JTextField fDur    = field(se!=null?String.valueOf(se.getDuracion()):"","Horas (ej: 3.5)");
        JTextField fObs    = field(se!=null&&se.getObservaciones()!=null?se.getObservaciones():"","Observaciones opcionales");
        JComboBox<String>   cbEstado  = comboStr(ESTADOS,se!=null?se.getEstadoSesion():Sesion.ESTADO_PROGRAMADA);
        JComboBox<String>   cbCabina  = comboStr(cabinas.toArray(new String[0]),cabinas.get(0));
        if(se!=null && se.getIdCabina()!=null && se.getIdCabina()>=1 && se.getIdCabina()<=cabinas.size())
            cbCabina.setSelectedIndex(se.getIdCabina()-1);
        JComboBox<Artista>  cbArt = new JComboBox<>(artistas.toArray(new Artista[0]));
        JComboBox<Productor> cbProd = new JComboBox<>(productores.toArray(new Productor[0]));
        cbArt.setRenderer(objRenderer(v->v instanceof Artista a?a.getNombreArtista():"")); styleCombo(cbArt);
        cbProd.setRenderer(objRenderer(v->v instanceof Productor p?p.getNombre():"")); styleCombo(cbProd);
        if(se!=null){ cbArt.setSelectedItem(se.getArtista()); cbProd.setSelectedItem(se.getProductor()); }

        JLabel lblCosto = new JLabel(se!=null?String.format("Costo estimado: $%.2f",se.getCostoTotal()):"Costo estimado: —");
        lblCosto.setFont(new Font("Segoe UI",Font.BOLD,13)); lblCosto.setForeground(se!=null?COK:TEXT_MUTED); lblCosto.setAlignmentX(LEFT_ALIGNMENT);

        fDur.getDocument().addDocumentListener(docListener(()->calcularYMostrarCosto(cbProd,fDur,lblCosto)));
        cbProd.addActionListener(e->calcularYMostrarCosto(cbProd,fDur,lblCosto));

        int sp=12;
        for(Object[] r : new Object[][]{
            {"Nombre de sesión *",fNombre},{"Fecha * (dd/MM/yyyy)",fFecha},{"Hora inicio (HH:mm)",fHIni},
            {"Hora fin (HH:mm)",fHFin},{"Duración (h) *",fDur},{"Estado sesión",cbEstado},
            {"Cabina",cbCabina},{"Artista *",cbArt},{"Productor *",cbProd},{"Observaciones",fObs}
        }){ pnl.add(formRow((String)r[0],(JComponent)r[1])); pnl.add(Box.createVerticalStrut(sp)); }
        pnl.add(lblCosto); pnl.add(Box.createVerticalStrut(24));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); btns.setOpaque(false); btns.setAlignmentX(LEFT_ALIGNMENT);
        ModernUI.RoundedButton bCancel=btn("Cancelar",false,0), bSave=btn(isEdit?"Guardar cambios":"Crear sesión",true,0);
        bCancel.addActionListener(e->dlg.dispose());
        bSave.addActionListener(e->{
            String nm=fNombre.getText().trim(), fd=fFecha.getText().trim(), dr=fDur.getText().trim();
            String hi=fHIni.getText().trim(), hf=fHFin.getText().trim(), obs=fObs.getText().trim();
            String estado=(String)cbEstado.getSelectedItem();
            Integer idCab=cbCabina.getSelectedIndex()+1;
            Artista art=(Artista)cbArt.getSelectedItem(); Productor prod=(Productor)cbProd.getSelectedItem();
            if(nm.isEmpty()){toast("El nombre de sesión es obligatorio",MainFrame.ToastType.ERROR);return;}
            if(fd.isEmpty()){toast("La fecha es obligatoria",MainFrame.ToastType.ERROR);return;}
            if(dr.isEmpty()){toast("La duración es obligatoria",MainFrame.ToastType.ERROR);return;}
            if(art==null){toast("Selecciona un artista",MainFrame.ToastType.ERROR);return;}
            if(prod==null){toast("Selecciona un productor",MainFrame.ToastType.ERROR);return;}
            LocalDate fecha; try{ fecha=LocalDate.parse(fd,FMT); }catch(DateTimeParseException ex){toast("Formato de fecha inválido (dd/MM/yyyy)",MainFrame.ToastType.ERROR);return;}
            double dur=parseDouble(dr).orElse(-1.0);
            if(dur<=0){toast("La duración debe ser un número positivo",MainFrame.ToastType.ERROR);return;}
            if(isEdit&&se!=null){
                se.setNombreSesion(nm); se.setFecha(fecha); se.setHoraInicio(hi); se.setHoraFin(hf);
                se.setDuracion(dur); se.setArtista(art); se.setProductor(prod);
                se.setIdCabina(idCab); se.setEstadoSesion(estado); se.setObservaciones(obs);
                toast("Sesión actualizada correctamente",MainFrame.ToastType.SUCCESS);
            } else {
                sesiones.add(new Sesion(nextId++,art,prod,idCab,nm,fecha,hi,hf,dur,estado,obs));
                toast("Sesión creada correctamente",MainFrame.ToastType.SUCCESS);
            }
            refreshTable(sesiones); dlg.dispose();
        });
        btns.add(bCancel); btns.add(bSave); pnl.add(btns);
        dlg.setContentPane(pnl); dlg.pack(); dlg.setMinimumSize(new Dimension(480,dlg.getPreferredSize().height));
        dlg.setLocationRelativeTo(this); dlg.setVisible(true);
    }

    // ─── Helpers de componentes ───────────────────────────────────────────────────

    private JLabel lbl(String t,Font f,Color c){ JLabel l=new JLabel(t); l.setFont(f); l.setForeground(c); return l; }

    private JTextField field(String val,String ph){
        JTextField f=new JTextField(val); f.setFont(FS); f.setForeground(TEXT_PRIMARY); f.setBackground(CFD); f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(139,92,246,70),1,true),new EmptyBorder(7,12,7,12)));
        f.putClientProperty("JTextField.placeholderText",ph); return f;
    }

    private ModernUI.RoundedButton btn(String t,boolean p,int w){
        ModernUI.RoundedButton b=new ModernUI.RoundedButton(t,p); if(w>0) b.setPreferredSize(new Dimension(w,36)); return b;
    }

    private JPanel formRow(String label,JComponent comp){
        JPanel r=new JPanel(new BorderLayout(0,4)); r.setOpaque(false); r.setAlignmentX(LEFT_ALIGNMENT); r.setMaximumSize(new Dimension(Integer.MAX_VALUE,70));
        JLabel l=new JLabel(label); l.setFont(FE); l.setForeground(TEXT_MUTED); r.add(l,BorderLayout.NORTH); r.add(comp,BorderLayout.CENTER); return r;
    }

    private JComboBox<String> comboStr(String[] opts,String sel){
        JComboBox<String> cb=new JComboBox<>(opts){ @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CFD); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
            g2.setColor(new Color(139,92,246,90)); g2.setStroke(new BasicStroke(1f)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10); g2.dispose(); super.paintComponent(g);}};
        cb.setSelectedItem(sel); cb.setEditable(false); cb.setOpaque(false); cb.setFont(FS); cb.setForeground(TEXT_PRIMARY);
        cb.setBackground(CFD); cb.setMaximumRowCount(6); cb.setFocusable(false); cb.setBorder(new EmptyBorder(0,0,0,0));
        cb.setRenderer(new DefaultListCellRenderer(){ @Override public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){
            JLabel c=new JLabel(v==null?"":v.toString()); c.setBackground(i==-1?CFD:s?CM:CFD); c.setForeground(TEXT_PRIMARY); c.setBorder(new EmptyBorder(7,12,7,12)); c.setOpaque(true); return c;}});
        stylePopup(cb); return cb;
    }

    private <T> DefaultListCellRenderer objRenderer(Function<Object,String> fn){
        return new DefaultListCellRenderer(){ @Override public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){
            JLabel c=new JLabel(fn.apply(v)); c.setBackground(s&&i!=-1?CM:CFD); c.setForeground(TEXT_PRIMARY); c.setBorder(new EmptyBorder(7,12,7,12)); c.setOpaque(true); return c;}};
    }

    private <T> void styleCombo(JComboBox<T> cb){
        cb.setEditable(false); cb.setFont(FS); cb.setForeground(TEXT_PRIMARY); cb.setBackground(CFD); cb.setOpaque(false);
        cb.setMaximumRowCount(6); cb.setFocusable(false);
        cb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(139,92,246,70),1,true),new EmptyBorder(6,4,6,4)));
        stylePopup(cb);
    }

    private void stylePopup(JComboBox<?> cb){
        cb.addPopupMenuListener(new javax.swing.event.PopupMenuListener(){
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e){
                Object pop=cb.getUI().getAccessibleChild(cb,0); if(!(pop instanceof JComponent jp)) return;
                jp.setBorder(BorderFactory.createLineBorder(new Color(139,92,246,120),1));
                for(Component c:jp.getComponents()) if(c instanceof JScrollPane sp){ sp.getViewport().setBackground(CFD); sp.setBorder(BorderFactory.createEmptyBorder()); }
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e){}
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e){}
        });
    }

    private JPanel darkPanel(){
        JPanel p=new JPanel(){ @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CF); g2.fillRect(0,0,getWidth(),getHeight()); g2.dispose();}};
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); p.setBorder(new EmptyBorder(28,32,28,32)); return p;
    }

    // ─── Helpers utilitarios ──────────────────────────────────────────────────────

    private DocumentListener docListener(Runnable r){
        return new DocumentListener(){
            public void insertUpdate(javax.swing.event.DocumentEvent e){r.run();}
            public void removeUpdate(javax.swing.event.DocumentEvent e){r.run();}
            public void changedUpdate(javax.swing.event.DocumentEvent e){r.run();}
        };
    }

    private Optional<Double> parseDouble(String s){ try{return Optional.of(Double.parseDouble(s.trim()));}catch(NumberFormatException e){return Optional.empty();} }

    private void toast(String msg,MainFrame.ToastType t){ MainFrame.showToast(msg,t); }
}