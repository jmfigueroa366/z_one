package util;

import dao.Historialdao;
import services.Geminiservice;
import view.Jesusitochatview;

public class Chatcontroller {

    private Geminiservice geminiService;
    private Jesusitochatview vista;

    public Chatcontroller(Jesusitochatview vista) {
        this.vista = vista;
        Historialdao historialdao = new Historialdao();
        this.geminiService = new Geminiservice(historialdao);
    }

    public void enviarMensaje(String texto) {
        if (texto == null || texto.trim().isEmpty()) return;
        vista.mostrarMensaje(texto, true);
        vista.setBubbleTexto("Pensando en la mejor musica para ti...");
        vista.setInputHabilitado(false);

        new Thread(() -> {
            try {
                String respuesta = geminiService.enviarMensaje(texto);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    vista.mostrarMensaje(respuesta, false);
                    String preview = respuesta.length() > 55 ? respuesta.substring(0, 55) + "..." : respuesta;
                    vista.setBubbleTexto(preview);
                    vista.setInputHabilitado(true);
                });
            } catch (Exception e) {
                e.printStackTrace();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    vista.mostrarMensaje("Error: " + e.getMessage(), false);
                    vista.setBubbleTexto("Ups, algo salio mal :(");
                    vista.setInputHabilitado(true);
                });
            }
        }).start();
    }
}