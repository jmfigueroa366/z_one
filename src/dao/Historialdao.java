package dao;

import model.Mensaje;
import java.util.ArrayList;
import java.util.List;

public class Historialdao {
    private List<Mensaje> historial = new ArrayList<>();

    public void agregarMensaje(Mensaje m) {
        historial.add(m);
    }

    public List<Mensaje> getHistorial() {
        return historial;
    }

    public void limpiar() {
        historial.clear();
    }

    // Convierte el historial a JSON para la API de Gemini
    public String toJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < historial.size(); i++) {
            Mensaje m = historial.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"role\":\"").append(m.getRol()).append("\",")
              .append("\"parts\":[{\"text\":\"")
              .append(m.getTexto().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n"))
              .append("\"}]}");
        }
        sb.append("]");
        return sb.toString();
    }
}