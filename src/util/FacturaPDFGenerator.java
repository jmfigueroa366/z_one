package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import model.Factura;
import model.Sesion;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Generador de facturas en PDF con iText.
 * Diseño profesional con header colorido, tabla de items y totales.
 */
public class FacturaPDFGenerator {

    private static final BaseColor C_PRIMARY = new BaseColor(26, 110, 190);
    private static final BaseColor C_CYAN    = new BaseColor(0, 188, 212);
    private static final BaseColor C_DARK    = new BaseColor(4, 17, 31);
    private static final BaseColor C_GRAY    = new BaseColor(120, 130, 145);

    public static void generar(Factura f, Sesion s, String rutaSalida,
                                String empresaNom, String empresaDir,
                                String empresaNit, String empresaTel)
            throws DocumentException, IOException {

        Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
        PdfWriter.getInstance(doc, new FileOutputStream(rutaSalida));
        doc.open();

        // ── HEADER ──
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{2, 1});

        PdfPCell celdaEmpresa = new PdfPCell();
        celdaEmpresa.setBackgroundColor(C_PRIMARY);
        celdaEmpresa.setBorder(Rectangle.NO_BORDER);
        celdaEmpresa.setPadding(20);
        celdaEmpresa.addElement(par(empresaNom,
                FontFactory.getFont("Helvetica", 22, Font.BOLD, BaseColor.WHITE)));
        celdaEmpresa.addElement(par("ESTUDIO DE GRABACION PROFESIONAL",
                FontFactory.getFont("Helvetica", 9, Font.BOLD, BaseColor.WHITE)));
        celdaEmpresa.addElement(par(empresaDir,
                FontFactory.getFont("Helvetica", 9, Font.NORMAL, BaseColor.WHITE)));
        celdaEmpresa.addElement(par("NIT: " + empresaNit + "  -  Tel: " + empresaTel,
                FontFactory.getFont("Helvetica", 9, Font.NORMAL, BaseColor.WHITE)));
        header.addCell(celdaEmpresa);

        PdfPCell celdaNum = new PdfPCell();
        celdaNum.setBackgroundColor(C_DARK);
        celdaNum.setBorder(Rectangle.NO_BORDER);
        celdaNum.setPadding(20);
        celdaNum.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaNum.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celdaNum.addElement(parRight("FACTURA",
                FontFactory.getFont("Helvetica", 10, Font.BOLD, C_CYAN)));
        celdaNum.addElement(parRight(f.getNumeroFactura(),
                FontFactory.getFont("Helvetica", 18, Font.BOLD, BaseColor.WHITE)));
        celdaNum.addElement(parRight("Emitida: " + f.getFechaEmision()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                FontFactory.getFont("Helvetica", 9, Font.NORMAL, BaseColor.WHITE)));
        header.addCell(celdaNum);

        doc.add(header);
        doc.add(espacio(18));

        // ── CLIENTE ──
        PdfPTable cliente = new PdfPTable(1);
        cliente.setWidthPercentage(100);
        PdfPCell celCliente = new PdfPCell();
        celCliente.setBackgroundColor(new BaseColor(240, 245, 250));
        celCliente.setBorder(Rectangle.NO_BORDER);
        celCliente.setPadding(14);
        celCliente.addElement(par("FACTURAR A",
                FontFactory.getFont("Helvetica", 9, Font.BOLD, C_PRIMARY)));
        celCliente.addElement(par(
                s.getArtista() != null ? s.getArtista().getNombreArtista() : "Cliente",
                FontFactory.getFont("Helvetica", 14, Font.BOLD, C_DARK)));
        celCliente.addElement(par("Correo: " + f.getCorreoDestino(),
                FontFactory.getFont("Helvetica", 10, Font.NORMAL, C_GRAY)));
        cliente.addCell(celCliente);
        doc.add(cliente);
        doc.add(espacio(20));

        // ── TABLA DETALLE ──
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{4, 1.2f, 1.4f, 1.4f});

        addHeader(tabla, "DESCRIPCION");
        addHeader(tabla, "CANT.");
        addHeader(tabla, "VALOR UNIT.");
        addHeader(tabla, "TOTAL");

        double tarifa = s.getProductor() != null ? s.getProductor().getTarifaHora() : 0;
        addCell(tabla, "Sesion de grabacion: " + s.getNombreSesion()
                + "\nProductor: " + (s.getProductor() != null ? s.getProductor().getNombre() : "-")
                + "\nFecha: " + s.getFecha()
                + "  -  Horario: " + s.getHoraInicio() + " - " + s.getHoraFin(), false);
        addCell(tabla, String.format("%.1f h", s.getDuracion()), true);
        addCell(tabla, String.format("$ %,.2f", tarifa), true);
        addCell(tabla, String.format("$ %,.2f", s.getDuracion() * tarifa), true);

        doc.add(tabla);

        // ── TOTALES ──
        doc.add(espacio(10));
        PdfPTable totales = new PdfPTable(2);
        totales.setWidthPercentage(45);
        totales.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totales.setWidths(new float[]{1.5f, 1});

        double subtotal = f.getMontoTotal() / 1.19;
        double iva      = f.getMontoTotal() - subtotal;

        addTotal(totales, "Subtotal:",  String.format("$ %,.2f", subtotal), false);
        addTotal(totales, "IVA (19%):", String.format("$ %,.2f", iva), false);
        addTotal(totales, "TOTAL:",     String.format("$ %,.2f", f.getMontoTotal()), true);

        doc.add(totales);

        // ── FOOTER ──
        doc.add(espacio(35));
        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(100);
        PdfPCell celFooter = new PdfPCell();
        celFooter.setBackgroundColor(C_DARK);
        celFooter.setBorder(Rectangle.NO_BORDER);
        celFooter.setPadding(14);
        celFooter.addElement(parCenter("Gracias por confiar en " + empresaNom,
                FontFactory.getFont("Helvetica", 11, Font.BOLD, C_CYAN)));
        celFooter.addElement(parCenter(
                "Esta factura electronica fue generada automaticamente por Z-One Music",
                FontFactory.getFont("Helvetica", 8, Font.ITALIC, BaseColor.WHITE)));
        footer.addCell(celFooter);
        doc.add(footer);

        doc.close();
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════

    private static Paragraph par(String texto, Font font) {
        return new Paragraph(texto, font);
    }

    private static Paragraph parRight(String texto, Font font) {
        Paragraph p = new Paragraph(texto, font);
        p.setAlignment(Element.ALIGN_RIGHT);
        return p;
    }

    private static Paragraph parCenter(String texto, Font font) {
        Paragraph p = new Paragraph(texto, font);
        p.setAlignment(Element.ALIGN_CENTER);
        return p;
    }

    private static Paragraph espacio(int alto) {
        Paragraph p = new Paragraph(" ");
        p.setSpacingAfter(alto);
        return p;
    }

    private static void addHeader(PdfPTable t, String texto) {
        PdfPCell c = new PdfPCell(new Phrase(texto,
                FontFactory.getFont("Helvetica", 9, Font.BOLD, BaseColor.WHITE)));
        c.setBackgroundColor(C_PRIMARY);
        c.setPadding(10);
        c.setBorder(Rectangle.NO_BORDER);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        t.addCell(c);
    }

    private static void addCell(PdfPTable t, String texto, boolean alignRight) {
        PdfPCell c = new PdfPCell(new Phrase(texto,
                FontFactory.getFont("Helvetica", 10, Font.NORMAL, C_DARK)));
        c.setPadding(10);
        c.setBorder(Rectangle.BOX);
        c.setBorderColor(new BaseColor(220, 225, 230));
        if (alignRight) c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(c);
    }

    private static void addTotal(PdfPTable t, String etq, String val, boolean negrita) {
        Font f = FontFactory.getFont("Helvetica",
                negrita ? 13 : 10, negrita ? Font.BOLD : Font.NORMAL,
                negrita ? BaseColor.WHITE : C_DARK);

        PdfPCell c1 = new PdfPCell(new Phrase(etq, f));
        c1.setPadding(8);
        c1.setBackgroundColor(negrita ? C_PRIMARY : new BaseColor(245, 248, 252));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase(val, f));
        c2.setPadding(8);
        c2.setBackgroundColor(negrita ? C_PRIMARY : new BaseColor(245, 248, 252));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(c2);
    }
}