package css;

import java.awt.Color;
import java.awt.Font;
import java.time.format.DateTimeFormatter;

/**
 * Constantes visuales para formSesion: paleta, fuentes, formatos.
 * Centraliza el "CSS" del módulo de sesiones.
 */
public final class SesionStyles {

    private SesionStyles() {} // No instanciable

    // ── PALETA ──────────────────────────────────────────────────────
    public static final Color C_BG_DARK     = new Color(0x04111F);
    public static final Color C_CARD_BG     = new Color(0x061829);
    public static final Color C_ROW_BG      = new Color(0x071E30);
    public static final Color C_ROW_SEL     = new Color(0x0D3560);
    public static final Color C_PRIMARY     = new Color(0x1A6EBE);
    public static final Color C_ACCENT_BLUE = new Color(0x2196F3);
    public static final Color C_ACCENT_CYAN = new Color(0x00BCD4);
    public static final Color C_TEXT_PRI    = new Color(0xE8EFF7);
    public static final Color C_TEXT_MUT    = new Color(0x5A7A9A);
    public static final Color C_OK          = new Color(0x4CAF50);
    public static final Color C_WARN        = new Color(0xFFA726);
    public static final Color C_ERR         = new Color(0xEF5350);
    public static final Color C_PROG        = new Color(0x42A5F5);
    public static final Color C_BORDER      = new Color(0x0D2A45);
    public static final Color C_FIELD_BG    = new Color(0x0A1F36);
    public static final Color C_FIELD_BG_FOC= new Color(0x0E2C4E);

    // ── FUENTES ─────────────────────────────────────────────────────
    public static final Font FT = new Font("Segoe UI", Font.BOLD,  24);
    public static final Font FS = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FE = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FH = new Font("Segoe UI", Font.BOLD,  11);

    // ── FORMATOS ────────────────────────────────────────────────────
    public static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── CLAVES PARA VISTAS ──────────────────────────────────────────
    public static final String VISTA_TABLA    = "tabla";
    public static final String VISTA_TARJETAS = "tarjetas";

    // ── UTILIDADES DE COLOR ─────────────────────────────────────────

    /** Color según estado de sesión. */
    public static Color colorEstado(String estado) {
        if (estado == null) return C_PROG;
        return switch (estado) {
            case "Finalizada" -> C_OK;
            case "En curso"   -> C_WARN;
            case "Cancelada"  -> C_ERR;
            default           -> C_PROG;
        };
    }

    /** Mezcla lineal entre dos colores. */
    public static Color blend(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r  = (int) (a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g  = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        return new Color(r, g, bl);
    }

    /** Calcula las iniciales de un nombre (para avatares). */
    public static String iniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "?";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length == 1) return partes[0].substring(0, 1).toUpperCase();
        return (partes[0].charAt(0) + "" + partes[1].charAt(0)).toUpperCase();
    }
}