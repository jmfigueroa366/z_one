package util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Grabador de audio simple usando javax.sound.sampled.
 * Captura desde el micrófono del sistema y guarda como .wav
 */
public class AudioRecorder {

    // Formato: 16-bit, 44.1 kHz, mono, PCM signed
    private static final AudioFormat FORMATO = new AudioFormat(
        44100.0f,
        16,
        2,         // ← canales (2 = estéreo) FOCUSRITE necesita estéreo
        true,
        false    // big-endian
    );

    private TargetDataLine linea;
    private Thread hiloGrabacion;
    private File archivoSalida;
    private long inicioMillis;
    private boolean grabando = false;

    /** Inicia la grabación hacia un archivo. */
public void iniciar(File archivo) throws LineUnavailableException {
    if (grabando) throw new IllegalStateException("Ya hay una grabacion en curso");
    archivoSalida = archivo;
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMATO);

    // Buscar Focusrite
    Mixer mixerFocusrite = null;
    for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
        if (mi.getName().toLowerCase().contains("focusrite") ||
            mi.getName().toLowerCase().contains("analogue")) {
            Mixer m = AudioSystem.getMixer(mi);
            System.out.println("Probando mixer: " + mi.getName());
            System.out.println("  Soporta línea: " + m.isLineSupported(info));
            if (m.isLineSupported(info)) {
                mixerFocusrite = m;
                System.out.println("  ✅ Usando este mixer");
                break;
            }
        }
    }

    try {
        if (mixerFocusrite != null) {
            linea = (TargetDataLine) mixerFocusrite.getLine(info);
            System.out.println("Línea obtenida del mixer Focusrite");
        } else {
            System.out.println("⚠️ Usando dispositivo por defecto");
            linea = (TargetDataLine) AudioSystem.getLine(info);
        }

        linea.open(FORMATO);
        System.out.println("✅ Línea abierta correctamente");
        linea.start();
        System.out.println("✅ Grabación iniciada");

    } catch (Exception e) {
        System.out.println("❌ ERROR: " + e.getClass().getName() + ": " + e.getMessage());
        throw e;
    }

    inicioMillis = System.currentTimeMillis();
    grabando = true;

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
public static void listarDispositivos() {
    System.out.println("=== DISPOSITIVOS DE AUDIO DISPONIBLES ===");
    for (Mixer.Info info : AudioSystem.getMixerInfo()) {
        Mixer m = AudioSystem.getMixer(info);
        DataLine.Info lineaInfo = new DataLine.Info(TargetDataLine.class, FORMATO);
        boolean soporta = m.isLineSupported(lineaInfo);
        System.out.println((soporta ? "✅ " : "❌ ") + info.getName() + " | " + info.getDescription());
    }
    System.out.println("=========================================");
}

    /** Detiene la grabación y devuelve la duración en segundos. */
    public int detener() {
        if (!grabando) return 0;
        int duracion = (int) ((System.currentTimeMillis() - inicioMillis) / 1000);
        linea.stop();
        linea.close();
        grabando = false;
        try {
            if (hiloGrabacion != null) hiloGrabacion.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return duracion;
    }

    public boolean estaGrabando() {
        return grabando;
    }

    public int segundosTranscurridos() {
        if (!grabando) return 0;
        return (int) ((System.currentTimeMillis() - inicioMillis) / 1000);
    }

    /** Verifica si hay micrófono disponible. */
    public static boolean hayMicrofono() {
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMATO);
            return AudioSystem.isLineSupported(info);
        } catch (Exception e) {
            return false;
        }
    }
}