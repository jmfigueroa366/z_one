package services;

import dao.Historialdao;
import model.Mensaje;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Geminiservice {

   private static final String API_KEY = "AQ.Ab8RN6IWZKGyOjupqkQC006__TxHfoTSJvTBg6CjnhBC3y20fw";
   private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + API_KEY;
    private static final String SYSTEM_PROMPT = "Eres Z-BOT, un asistente personal experto en musica de todos los generos: reggaeton, pop, rock, electronica, clasica, jazz, salsa, cumbia, metal y mas. Respondes en espanol, eres amigable, entusiasta y das recomendaciones personalizadas. Cuando alguien pide recomendaciones, sugiere artistas, canciones y albums concretos. Eres breve y directo pero siempre util.";

    private Historialdao historialDAO;

    public Geminiservice(Historialdao historialDAO) {
        this.historialDAO = historialDAO;
    }

    public String enviarMensaje(String textoUsuario) throws Exception {
        historialDAO.agregarMensaje(new Mensaje("user", textoUsuario));
        String respuesta = llamarAPI(historialDAO.toJson());
        historialDAO.agregarMensaje(new Mensaje("model", respuesta));
        return respuesta;
    }

    private String llamarAPI(String contentsJson) throws Exception {
        System.setProperty("https.protocols", "TLSv1.2");
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);

        String body = "{\"system_instruction\":{\"parts\":[{\"text\":\"" + SYSTEM_PROMPT + "\"}]},\"contents\":" + contentsJson + "}";
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            InputStream errStream = conn.getErrorStream();
            if (errStream != null) {
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(errStream, StandardCharsets.UTF_8));
                StringBuilder err = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) err.append(line);
                throw new Exception("HTTP " + responseCode + ": " + err.toString());
            } else {
                throw new Exception("HTTP " + responseCode + ": sin detalle");
            }
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }

        JSONObject json = new JSONObject(sb.toString());
        return json.getJSONArray("candidates")
                   .getJSONObject(0)
                   .getJSONObject("content")
                   .getJSONArray("parts")
                   .getJSONObject(0)
                   .getString("text");
    }
}