package be.esi.prj.bookle.mvp.model.scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Utility class to interact with Google Translate API.
 * Provides methods to detect the language of a given text and translate it.
 */
public class GoogleTranslator {

    private static final String API_KEY = "AIzaSyCCPg-lZUGyJz0GGX_ck_d3HYloZZNm8ug";

    /**
     * Detects the language of the given text using the Google Translate API.
     *
     * @param text The input text whose language is to be detected.
     * @return A string representing the ISO 639-1 language code ( "en", "es").
     *         Returns "und" (undefined) if detection fails or an error occurs.
     */
    public static String detectLanguage(String text) {
        try {
            String urlStr = "https://translation.googleapis.com/language/translate/v2/detect?key=" + API_KEY;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            String requestBody = String.format("{\"q\": \"%s\"}", text);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray detections = json.getAsJsonObject("data")
                        .getAsJsonArray("detections")
                        .get(0).getAsJsonArray();
                return detections.get(0).getAsJsonObject().get("language").getAsString();
            }
        } catch (Exception e) {
            return "und";
        }
    }

    /**
     * Automatically detects the source language of the given text and translates it to the target language.
     *
     * @param text       The input text to translate.
     * @param targetLang The target language ISO 639-1 code (e.g., "en" for English, "fr" for French).
     * @return The translated text if successful, otherwise the original text.
     */
    public static String detectAndTranslate(String text, String targetLang) {
        try {
            String urlStr = "https://translation.googleapis.com/language/translate/v2?key=" + API_KEY;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            String requestBody = String.format("""
                {
                  "q": "%s",
                  "target": "%s",
                  "format": "text"
                }
                """, text, targetLang);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray translations = json.getAsJsonObject("data").getAsJsonArray("translations");
                return translations.get(0).getAsJsonObject().get("translatedText").getAsString();
            }
        } catch (Exception e) {
            return text;
        }
    }
}
