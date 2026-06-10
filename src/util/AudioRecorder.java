package util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Grabador de audio que usa EXCLUSIVAMENTE la interfaz Behringer/UMC22.
 * Si el dispositivo no está disponible, reintenta hasta que aparezca.
 * Nunca hace fallback a otro micrófono.
 */
public class AudioRecorder {

    // ── Palabras clave para identificar el Behringer ──────────────────────────
    private static final String[] PALABRAS_BEHRINGER = {
        "behringer", "umc22", "umc-22", "umc", "usb wdm audio"
    };

    // ── Formatos en orden de preferencia ─────────────────────────────────────
    private static final AudioFormat[] FORMATOS_CANDIDATOS = {
        new AudioFormat(48000.0f, 16, 2, true, false),  // 48k estéreo  ← más estable USB
        new AudioFormat(44100.0f, 16, 2, true, false),  // 44.1k estéreo
        new AudioFormat(48000.0f, 16, 1, true, false),  // 48k mono
        new AudioFormat(44100.0f, 16, 1, true, false),  // 44.1k mono
        new AudioFormat(48000.0f, 24, 2, true, false),  // 48k estéreo 24bit
        new AudioFormat(44100.0f, 24, 2, true, false),  // 44.1k estéreo 24bit
    };

    // ── Configuración de reintentos ───────────────────────────────────────────
    private static final int ESPERA_REINTENTO_MS   = 2_000;  // pausa entre intentos
    private static final int INTENTOS_MAX          = 0;      // 0 = infinito

    private AudioFormat formatoActivo;
    private TargetDataLine linea;
    private Thread hiloGrabacion;
    private File archivoSalida;
    private long inicioMillis;
    private volatile boolean grabando = false;
    private volatile boolean detenerSolicitado = false;

    // ─────────────────────────────────────────────────────────────────────────
    //  INICIO DE GRABACIÓN
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Inicia la grabación hacia un archivo.
     * Bloquea hasta que el Behringer esté disponible y abierto.
     *
     * @throws InterruptedException si el hilo es interrumpido mientras espera
     * @throws LineUnavailableException si se agotaron los intentos (solo si INTENTOS_MAX > 0)
     */
    public void iniciar(File archivo) throws LineUnavailableException, InterruptedException {
        if (grabando) throw new IllegalStateException("Ya hay una grabación en curso");

        detenerSolicitado = false;
        archivoSalida = archivo;

        // ── Esperar y abrir el Behringer ──────────────────────────────────────
        linea = esperarYAbrirBehringer();
        if (linea == null) {
            throw new LineUnavailableException(
                "No se pudo abrir el Behringer tras " + INTENTOS_MAX + " intentos.");
        }

        inicioMillis = System.currentTimeMillis();
        grabando = true;

        // ── Hilo de grabación ─────────────────────────────────────────────────
        hiloGrabacion = new Thread(() -> {
            try (AudioInputStream ais = new AudioInputStream(linea)) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, archivoSalida);
                System.out.println("✅ Archivo guardado: " + archivoSalida.getName());
            } catch (IOException e) {
                System.out.println("❌ Error guardando archivo: " + e.getMessage());
                e.printStackTrace();
            }
        }, "AudioRecorder-Thread");

        hiloGrabacion.start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  ESPERAR Y ABRIR BEHRINGER (con reintentos)
    // ─────────────────────────────────────────────────────────────────────────

    private TargetDataLine esperarYAbrirBehringer() throws InterruptedException {
        int intento = 0;

        while (!detenerSolicitado) {
            intento++;
            if (INTENTOS_MAX > 0 && intento > INTENTOS_MAX) {
                System.out.println("❌ Behringer no encontrado tras " + INTENTOS_MAX + " intentos. Abortando.");
                return null;
            }

            System.out.println("🔍 Buscando Behringer (intento " + intento + ")...");

            Mixer mixerBehringer = buscarMixerBehringer();

            if (mixerBehringer == null) {
                System.out.println("⚠️  Behringer no detectado. Reintentando en "
                    + (ESPERA_REINTENTO_MS / 1000) + "s...");
                Thread.sleep(ESPERA_REINTENTO_MS);
                continue;
            }

            // Mixer encontrado → intentar abrir línea
            TargetDataLine lineaAbierta = intentarAbrirLinea(mixerBehringer);

            if (lineaAbierta != null) {
                return lineaAbierta;
            }

            System.out.println("⚠️  Behringer encontrado pero no aceptó ningún formato. "
                + "Reintentando en " + (ESPERA_REINTENTO_MS / 1000) + "s...");
            Thread.sleep(ESPERA_REINTENTO_MS);
        }

        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  BUSCAR MIXER BEHRINGER
    // ─────────────────────────────────────────────────────────────────────────

    private Mixer buscarMixerBehringer() {
        for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
            String nombre = mi.getName().toLowerCase();
            for (String clave : PALABRAS_BEHRINGER) {
                if (nombre.contains(clave)) {
                    Mixer m = AudioSystem.getMixer(mi);
                    // Verificar que soporte captura (TargetDataLine)
                    for (AudioFormat fmt : FORMATOS_CANDIDATOS) {
                        DataLine.Info info = new DataLine.Info(TargetDataLine.class, fmt);
                        if (m.isLineSupported(info)) {
                            System.out.println("✅ Mixer Behringer encontrado: " + mi.getName());
                            return m;
                        }
                    }
                }
            }
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  INTENTAR ABRIR LÍNEA EN UN MIXER
    // ─────────────────────────────────────────────────────────────────────────

    private TargetDataLine intentarAbrirLinea(Mixer mixer) {
        for (AudioFormat fmt : FORMATOS_CANDIDATOS) {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, fmt);
            if (!mixer.isLineSupported(info)) continue;
            try {
                TargetDataLine candidato = (TargetDataLine) mixer.getLine(info);
                System.out.println("   Probando formato: " + fmt);
                candidato.open(fmt);
                candidato.start();
                formatoActivo = fmt;
                System.out.println("✅ Formato aceptado: " + fmt);
                return candidato;
            } catch (Exception e) {
                System.out.println("   ❌ Rechazado (" + fmt + "): " + e.getMessage());
            }
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DETENER
    // ─────────────────────────────────────────────────────────────────────────

    /** Detiene la grabación y devuelve la duración en segundos. */
    public int detener() {
        if (!grabando) return 0;

        detenerSolicitado = true;
        int duracion = (int) ((System.currentTimeMillis() - inicioMillis) / 1000);

        if (linea != null) {
            linea.stop();
            linea.close();
        }

        grabando = false;

        try {
            if (hiloGrabacion != null) hiloGrabacion.join(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return duracion;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  UTILIDADES
    // ─────────────────────────────────────────────────────────────────────────

    public static void listarDispositivos() {
        System.out.println("=== DISPOSITIVOS DE AUDIO DISPONIBLES ===");
        AudioFormat fmt = FORMATOS_CANDIDATOS[0];
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            Mixer m = AudioSystem.getMixer(info);
            DataLine.Info lineaInfo = new DataLine.Info(TargetDataLine.class, fmt);
            boolean soporta = m.isLineSupported(lineaInfo);
            System.out.println((soporta ? "✅ " : "❌ ") + info.getName() + " | " + info.getDescription());
        }
        System.out.println("=========================================");
    }

    public static boolean hayMicrofono() {
        for (AudioFormat fmt : FORMATOS_CANDIDATOS) {
            try {
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, fmt);
                if (AudioSystem.isLineSupported(info)) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    public boolean estaGrabando()       { return grabando; }
    public AudioFormat getFormatoActivo() { return formatoActivo; }

    public int segundosTranscurridos() {
        if (!grabando) return 0;
        return (int) ((System.currentTimeMillis() - inicioMillis) / 1000);
    }
}