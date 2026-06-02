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
            44100.0f,  // sample rate
            16,        // bits por sample
            1,         // canales (1 = mono)
            true,      // signed
            false      // big-endian
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
        if (!AudioSystem.isLineSupported(info))
            throw new LineUnavailableException("El microfono no es compatible con este formato");

        linea = (TargetDataLine) AudioSystem.getLine(info);
        linea.open(FORMATO);
        linea.start();
        inicioMillis = System.currentTimeMillis();
        grabando = true;

        hiloGrabacion = new Thread(() -> {
            try (AudioInputStream ais = new AudioInputStream(linea)) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, archivoSalida);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "AudioRecorder-Thread");
        hiloGrabacion.start();
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