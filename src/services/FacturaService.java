package services;

import dao.FacturaDAO;
import model.Factura;
import model.Sesion;
import util.FacturaPDFGenerator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio que orquesta todo el flujo de facturacion:
 * generar numero -> crear PDF -> guardar BD -> enviar email.
 */
public class FacturaService {

    private final FacturaDAO dao = new FacturaDAO();

    public List<Factura> listar() throws SQLException {
        return dao.listarTodos();
    }

    /**
     * Genera factura completa: PDF + BD + envio automatico.
     * @param sesion sesion creada
     * @param correoCliente correo del artista
     * @return la factura creada
     */
    public Factura generarYEnviar(Sesion sesion, String correoCliente) throws Exception {
        // 1. Crear carpeta facturas si no existe
        Path carpeta = Paths.get("facturas");
        Files.createDirectories(carpeta);

        // 2. Generar numero
        String numero = dao.generarNumeroFactura();

        // 3. Crear modelo Factura
        Factura f = new Factura();
        f.setNumeroFactura(numero);
        f.setIdSesion(sesion.getIdSesion());
        f.setCorreoDestino(correoCliente);
        f.setMontoTotal(sesion.getCostoTotal() * 1.19);   // con IVA
        f.setEstado("EMITIDA");
        f.setFechaEmision(java.time.LocalDateTime.now());

        // 4. Generar PDF
        EmailService emailSvc = new EmailService();
        String fechaStr = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File pdf = carpeta.resolve(numero + "_" + fechaStr + ".pdf").toFile();

        FacturaPDFGenerator.generar(f, sesion, pdf.getAbsolutePath(),
                emailSvc.getEmpresaNombre(), emailSvc.getEmpresaDireccion(),
                emailSvc.getEmpresaNit(), emailSvc.getEmpresaTelefono());

        f.setRutaPdf(pdf.getAbsolutePath());

        // 5. Guardar en BD
        int idGenerado = dao.crear(f);
        f.setIdFactura(idGenerado);

        // 6. Enviar por correo (cliente + admin)
        String destinatarios = correoCliente + ", " + emailSvc.getAdminCorreo();
        String html = construirEmailHtml(f, sesion, emailSvc);
        String asunto = "🎵 Factura " + numero + " - " + emailSvc.getEmpresaNombre();

        try {
            emailSvc.enviarConAdjunto(destinatarios, asunto, html, pdf);
            dao.marcarEnviada(idGenerado);
            f.setEstado("ENVIADA");
        } catch (Exception ex) {
            System.err.println("Error enviando email: " + ex.getMessage());
            ex.printStackTrace();
            // No relanzamos: la factura se guardo aunque el email falle
            f.setObservaciones("Error envio: " + ex.getMessage());
        }

        return f;
    }

    /**
     * Reenvía una factura existente.
     */
    public void reenviar(int idFactura) throws Exception {
        Factura f = dao.buscarPorId(idFactura);
        if (f == null) throw new IllegalArgumentException("Factura no encontrada");

        File pdf = new File(f.getRutaPdf());
        if (!pdf.exists())
            throw new IllegalStateException("El PDF de la factura ya no existe");

        EmailService emailSvc = new EmailService();
        String destinatarios = f.getCorreoDestino() + ", " + emailSvc.getAdminCorreo();
        String asunto = "🎵 Factura " + f.getNumeroFactura() + " - " + emailSvc.getEmpresaNombre();

        String html = "<html><body style='font-family:Arial;color:#222;'>"
                + "<h2 style='color:#1A6EBE;'>Reenvío de factura</h2>"
                + "<p>Factura <b>" + f.getNumeroFactura() + "</b> por <b>$"
                + String.format("%,.2f", f.getMontoTotal()) + "</b></p>"
                + "<p>Adjunto encontrarás el documento en PDF.</p>"
                + "<p style='color:#888;'>— " + emailSvc.getEmpresaNombre() + "</p>"
                + "</body></html>";

        emailSvc.enviarConAdjunto(destinatarios, asunto, html, pdf);
        dao.marcarEnviada(idFactura);
    }

    private String construirEmailHtml(Factura f, Sesion s, EmailService es) {
        return "<!DOCTYPE html><html><body style='font-family:Arial,sans-serif;"
                + "color:#222;max-width:600px;margin:0 auto;'>"
                + "<div style='background:#1A6EBE;color:white;padding:24px;text-align:center;'>"
                + "<h1 style='margin:0;font-size:24px;'>🎵 " + es.getEmpresaNombre() + "</h1>"
                + "<p style='margin:8px 0 0;font-size:12px;letter-spacing:2px;'>FACTURA ELECTRÓNICA</p>"
                + "</div>"
                + "<div style='padding:24px;background:#f9fbff;'>"
                + "<h2 style='color:#1A6EBE;margin:0 0 16px;'>¡Hola"
                + (s.getArtista() != null ? " " + s.getArtista().getNombreArtista() : "") + "!</h2>"
                + "<p>Te enviamos la factura correspondiente a tu sesión de grabación:</p>"
                + "<table style='width:100%;border-collapse:collapse;margin:16px 0;'>"
                + "<tr><td style='padding:8px;border-bottom:1px solid #ddd;'><b>Número:</b></td>"
                + "<td style='padding:8px;border-bottom:1px solid #ddd;'>" + f.getNumeroFactura() + "</td></tr>"
                + "<tr><td style='padding:8px;border-bottom:1px solid #ddd;'><b>Sesión:</b></td>"
                + "<td style='padding:8px;border-bottom:1px solid #ddd;'>" + s.getNombreSesion() + "</td></tr>"
                + "<tr><td style='padding:8px;border-bottom:1px solid #ddd;'><b>Fecha:</b></td>"
                + "<td style='padding:8px;border-bottom:1px solid #ddd;'>" + s.getFecha() + "</td></tr>"
                + "<tr><td style='padding:8px;border-bottom:1px solid #ddd;'><b>Duración:</b></td>"
                + "<td style='padding:8px;border-bottom:1px solid #ddd;'>" + s.getDuracion() + " h</td></tr>"
                + "<tr><td style='padding:8px;background:#1A6EBE;color:white;'><b>TOTAL:</b></td>"
                + "<td style='padding:8px;background:#1A6EBE;color:white;font-size:18px;'><b>$"
                + String.format("%,.2f", f.getMontoTotal()) + "</b></td></tr>"
                + "</table>"
                + "<p>📎 <b>Adjunto encontrarás la factura en PDF.</b></p>"
                + "<p>Gracias por confiar en nosotros.</p>"
                + "</div>"
                + "<div style='background:#04111F;color:#7a9;padding:14px;text-align:center;font-size:11px;'>"
                + es.getEmpresaDireccion() + "  ·  Tel: " + es.getEmpresaTelefono()
                + "<br>NIT: " + es.getEmpresaNit()
                + "</div></body></html>";
    }
}