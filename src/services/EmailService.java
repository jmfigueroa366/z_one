package services;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Servicio de envio de emails con adjuntos (facturas PDF).
 * Usa SMTP con autenticacion via App Password de Gmail.
 *
 * Intenta leer config/email.properties; si no lo encuentra (por ejemplo
 * cuando el archivo esta en la nube de OneDrive), usa una configuracion
 * hardcoded como fallback.
 */
public class EmailService {

    private final Properties props;
    private final String     usuario;
    private final String     password;

    public EmailService() throws IOException {
        props = new Properties();

        // Intenta varias ubicaciones donde puede estar el archivo
        String[] rutasPosibles = {
            "config/email.properties",
            "./config/email.properties",
            System.getProperty("user.dir") + "/config/email.properties",
            new File("").getAbsolutePath() + "/config/email.properties"
        };

        File config = null;
        for (String ruta : rutasPosibles) {
            File f = new File(ruta);
            if (f.exists()) {
                config = f;
                break;
            }
        }

        if (config != null) {
            System.out.println("Email config cargado desde: " + config.getAbsolutePath());
            try (FileInputStream fis = new FileInputStream(config)) {
                props.load(fis);
            }
        } else {
            System.err.println("AVISO: config/email.properties no encontrado. "
                    + "Usando configuracion hardcoded.");
            cargarConfiguracionHardcoded();
        }

        usuario  = props.getProperty("email.from");
        password = props.getProperty("email.password");
    }

    /**
     * Configuracion hardcoded como fallback cuando no se encuentra el archivo.
     * IMPORTANTE: cambia estos valores por los tuyos antes de compilar.
     */
    private void cargarConfiguracionHardcoded() {
        props.setProperty("email.from",         "jesusreyfigueroa@gmail.com");
        props.setProperty("email.password",     "drsg zsyg omuu efyc");
        props.setProperty("email.smtp.host",    "smtp.gmail.com");
        props.setProperty("email.smtp.port",    "587");
        props.setProperty("email.admin",        "jesusreyfigueroa@gmail.com");
        props.setProperty("empresa.nombre",     "Z-One Music Studios");
        props.setProperty("empresa.direccion",  "Calle 12 # 34-56, Valledupar, Colombia");
        props.setProperty("empresa.nit",        "900.123.456-7");
        props.setProperty("empresa.telefono",   "+57 300 123 4567");
    }

    // ════════════════════════════════════════════════════════════════
    //  GETTERS DE CONFIGURACION
    // ════════════════════════════════════════════════════════════════

    public String getAdminCorreo() {
        return props.getProperty("email.admin");
    }

    public String getEmpresaNombre() {
        return props.getProperty("empresa.nombre");
    }

    public String getEmpresaDireccion() {
        return props.getProperty("empresa.direccion");
    }

    public String getEmpresaNit() {
        return props.getProperty("empresa.nit");
    }

    public String getEmpresaTelefono() {
        return props.getProperty("empresa.telefono");
    }

    // ════════════════════════════════════════════════════════════════
    //  ENVIO DE EMAILS
    // ════════════════════════════════════════════════════════════════

    /**
     * Envia un email con adjunto PDF.
     * @param destinatarios uno o varios correos separados por coma
     * @param asunto asunto del email
     * @param mensajeHtml cuerpo HTML del email
     * @param adjunto archivo a adjuntar (puede ser null)
     */
    public void enviarConAdjunto(String destinatarios, String asunto,
                                  String mensajeHtml, File adjunto)
            throws MessagingException {

        // ── Configuracion SMTP ──
        Properties smtp = new Properties();
        smtp.put("mail.smtp.auth",            "true");
        smtp.put("mail.smtp.starttls.enable", "true");
        smtp.put("mail.smtp.host",            props.getProperty("email.smtp.host"));
        smtp.put("mail.smtp.port",            props.getProperty("email.smtp.port"));

        Session session = Session.getInstance(smtp, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, password);
            }
        });

        // ── Crear mensaje ──
        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(usuario, getEmpresaNombre()));
        } catch (java.io.UnsupportedEncodingException ex) {
            msg.setFrom(new InternetAddress(usuario));
        }

        // Multiples destinatarios separados por coma
        for (String dest : destinatarios.split(",")) {
            String d = dest.trim();
            if (!d.isEmpty())
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(d));
        }

        msg.setSubject(asunto);

        // ── Cuerpo HTML + adjunto ──
        Multipart mp = new MimeMultipart();

        // Parte 1: cuerpo HTML
        MimeBodyPart cuerpo = new MimeBodyPart();
        cuerpo.setContent(mensajeHtml, "text/html; charset=UTF-8");
        mp.addBodyPart(cuerpo);

        // Parte 2: PDF adjunto (si existe)
        if (adjunto != null && adjunto.exists()) {
            MimeBodyPart adj = new MimeBodyPart();
            try {
                adj.attachFile(adjunto);
                mp.addBodyPart(adj);
            } catch (IOException ex) {
                System.err.println("Error adjuntando archivo: " + ex.getMessage());
            }
        }

        msg.setContent(mp);
        Transport.send(msg);

        System.out.println("Email enviado a: " + destinatarios);
    }

    /**
     * Envia un email simple sin adjunto (texto plano o HTML).
     */
    public void enviar(String destinatario, String asunto, String mensajeHtml)
            throws MessagingException {
        enviarConAdjunto(destinatario, asunto, mensajeHtml, null);
    }
}