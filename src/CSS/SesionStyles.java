package css;

import java.awt.Color;
import java.awt.Font;
import java.time.format.DateTimeFormatter;

/**
 * Constantes visuales para formSesion: paleta, fuentes, formatos.
 * Paleta rediseñada: fondo claro, acentos violeta/cyan/ámbar/coral
 * inspirada en el módulo de Canciones, con tema de estudio de grabación.
 */
public final class SesionStyles {

    private SesionStyles() {}

    // ── PALETA BASE ─────────────────────────────────────────────────
    /** Fondo general de la pantalla */
    public static final Color C_BG_DARK      = new Color(0xF4F6FB);
    /** Fondo de tarjetas/paneles */
    public static final Color C_CARD_BG      = new Color(0xFFFFFF);
    /** Fondo de filas normales */
    public static final Color C_ROW_BG       = new Color(0xF0F4FF);
    /** Fondo de fila seleccionada */
    public static final Color C_ROW_SEL      = new Color(0xEEEDFE);
    /** Color primario (violeta — botón principal, borde activo) */
    public static final Color C_PRIMARY      = new Color(0x534AB7);
    /** Acento azul-violeta para labels y detalles */
    public static final Color C_ACCENT_BLUE  = new Color(0x7F77DD);
    /** Acento cyan/verde para highlights y separadores */
    public static final Color C_ACCENT_CYAN  = new Color(0x1D9E75);
    /** Texto principal (oscuro, legible sobre fondo claro) */
    public static final Color C_TEXT_PRI     = new Color(0x1A1F36);
    /** Texto secundario/muted */
    public static final Color C_TEXT_MUT     = new Color(0x6B7280);
    /** Color de éxito / costo (verde) */
    public static final Color C_OK           = new Color(0x1D9E75);
    /** Color de advertencia / en curso (ámbar) */
    public static final Color C_WARN         = new Color(0xBA7517);
    /** Color de error / cancelada (coral) */
    public static final Color C_ERR          = new Color(0xD85A30);
    /** Color de estado programada (violeta suave) */
    public static final Color C_PROG         = new Color(0x534AB7);
    /** Borde general */
    public static final Color C_BORDER       = new Color(0xE8EAF0);
    /** Fondo de campos de texto */
    public static final Color C_FIELD_BG     = new Color(0xF4F6FB);
    /** Fondo de campo con foco */
    public static final Color C_FIELD_BG_FOC = new Color(0xEEEDFE);

    // ── PALETA SEMÁNTICA EXTRA (para pills y badges) ────────────────
    /** Fondo pill "Programada" */
    public static final Color C_PILL_PROG_BG = new Color(0xE1F5EE);
    /** Texto pill "Programada" */
    public static final Color C_PILL_PROG_FG = new Color(0x0F6E56);
    /** Fondo pill "En curso" */
    public static final Color C_PILL_CURSO_BG = new Color(0xFAEEDA);
    /** Texto pill "En curso" */
    public static final Color C_PILL_CURSO_FG = new Color(0x854F0B);
    /** Fondo pill "Finalizada" */
    public static final Color C_PILL_FIN_BG  = new Color(0xF1EFE8);
    /** Texto pill "Finalizada" */
    public static final Color C_PILL_FIN_FG  = new Color(0x5F5E5A);
    /** Fondo pill "Cancelada" */
    public static final Color C_PILL_CANCEL_BG = new Color(0xFAECE7);
    /** Texto pill "Cancelada" */
    public static final Color C_PILL_CANCEL_FG = new Color(0x993C1D);

    // ── COLOR REC (badge de grabación) ──────────────────────────────
    /** Rojo REC para el badge parpadeante */
    public static final Color C_REC          = new Color(0xE24B4A);

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

    /**
     * Color de píldora según estado: devuelve el color de FONDO del pill.
     * Para el texto usar {@link #colorEstadoFg(String)}.
     */
    public static Color colorEstado(String estado) {
        if (estado == null) return C_PILL_PROG_BG;
        return switch (estado) {
            case "Finalizada" -> C_PILL_FIN_BG;
            case "En curso"   -> C_PILL_CURSO_BG;
            case "Cancelada"  -> C_PILL_CANCEL_BG;
            default           -> C_PILL_PROG_BG;
        };
    }

    /**
     * Color de texto de píldora según estado.
     */
    public static Color colorEstadoFg(String estado) {
        if (estado == null) return C_PILL_PROG_FG;
        return switch (estado) {
            case "Finalizada" -> C_PILL_FIN_FG;
            case "En curso"   -> C_PILL_CURSO_FG;
            case "Cancelada"  -> C_PILL_CANCEL_FG;
            default           -> C_PILL_PROG_FG;
        };
    }

    /**
     * Color de acento para el avatar/borde del estado.
     * Mantiene compatibilidad con el uso en avatares y filas.
     */
    public static Color colorEstadoAccent(String estado) {
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