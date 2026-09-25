package be.esi.prj.bookle.mvp.model.scanner;

import be.esi.prj.bookle.mvp.view.ShowAlert;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.*;

/**
 * A class that performs OCR (Optical Character Recognition) on an image file
 * and extracts text, metadata (title, page), and a list of filtered words.
 * <p>
 * Uses the Tess4J library for OCR and GoogleTranslator for language detection and translation.
 */
public class OcrScanner {
    private Path fileToScan;
    private final Tesseract tesseract;
    private final ShowAlert alert;

    /**
     * Creates an {@code OcrScanner} and initializes the Tesseract instance with multiple languages.
     */
    public OcrScanner() {
        this.tesseract = new Tesseract();
        tesseract.setLanguage("fra+eng+nld+ron+cat");
        this.alert = new ShowAlert();
    }

    /**
     * Sets the image file to be scanned.
     *
     * @param imageName the path to the image file
     */
    public void setFileToScan(String imageName) {
        fileToScan = Paths.get(imageName);
    }

    /**
     * Performs the OCR scan, processes the text, and returns extracted data.
     *
     * @return a map containing title, page number, list of words, translated text, and language
     * @throws TesseractException if OCR fails
     * @throws IOException        if file reading or configuration fails
     */
    public Map<String, Object> scan() throws TesseractException, IOException {
        configureTesseract();

        String rawText = readRawText();
        String cleanedText = cleanRawText(rawText);

        String[] lines = cleanedText.split("\\r?\\n");
        String title = extractTitle(lines);
        String page = extractPage(lines);

        String content = cleanContent(lines);

        String detectedLang = GoogleTranslator.detectLanguage(content);

        String translatedText = content;
        if (!"fr".equals(detectedLang)) {
            translatedText = GoogleTranslator.detectAndTranslate(content, "fr");
            title = GoogleTranslator.detectAndTranslate(title, "fr");
        }
        List<String> words = extractWords(translatedText);

        if (words.isEmpty()) {
            alert.showAlert("Erreur", "Aucun mot détecté dans l'image.");
        }

        return createResultMap(title, page, words, translatedText, detectedLang);
    }

    /**
     * Configures the Tesseract engine with the path to the trained data.
     *
     * @throws IOException if the tessdata directory is not found
     */
    private void configureTesseract() throws IOException {
        String dataDirectory = "src/main/resources/tessdata";
        if (!Files.exists(Paths.get(dataDirectory))) {
            throw new IOException("Le dossier tessdata est introuvable.");
        }
        System.setProperty("TESSDATA_PREFIX", dataDirectory);
        tesseract.setDatapath(dataDirectory);
    }

    /**
     * Runs Tesseract OCR on the set image file.
     *
     * @return the raw OCR result as a string
     * @throws TesseractException if OCR processing fails
     */
    private String readRawText() throws TesseractException {
        return tesseract.doOCR(fileToScan.toFile());
    }

    /**
     * Cleans and normalizes the raw OCR text.
     *
     * @param rawText the raw text from OCR
     * @return the cleaned and normalized text
     */
    String cleanRawText(String rawText) {
        rawText = rawText.replaceAll("[’‘`´]", "'");
        rawText = removeAccents(rawText);
        return rawText.toLowerCase();
    }

    /**
     * Extracts the book title from lines of OCR text.
     *
     * @param lines the lines of OCR output
     * @return the extracted title, or an empty string if not found
     */
    String extractTitle(String[] lines) {
        for (String line : lines) {
            if (line.trim().matches("[a-z ]{3,30}")) {
                return line.trim();
            }
        }
        return "";
    }

    /**
     * Extracts the page number from lines of OCR text.
     *
     * @param lines the lines of OCR output
     * @return the extracted page number, or an empty string if not found
     */
    String extractPage(String[] lines) {
        for (String line : lines) {
            if (line.trim().matches("\\d{1,4}")) {
                return line.trim();
            }
        }
        return "";
    }

    /**
     * Cleans the OCR content, removing title/page and unwanted characters.
     *
     * @param lines the lines of OCR output
     * @return a cleaned text string
     */
    String cleanContent(String[] lines) {
        StringBuilder contentBuilder = new StringBuilder();
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.matches("[a-z ]{3,30}") && !trimmed.matches("\\d{1,4}")) {
                contentBuilder.append(trimmed).append(" ");
            }
        }
        return contentBuilder.toString().replaceAll("[^\\p{L}\\p{N}' ]", " ").replaceAll("\\s+", " ").trim();
    }

    /**
     * Extracts a list of valid French-like words from the cleaned content.
     *
     * @param content the cleaned OCR content
     * @return a list of unique valid words
     */
    List<String> extractWords(String content) {
        Set<String> uniqueWords = new HashSet<>();

        Arrays.stream(content.split(" ")).flatMap(word -> word.contains("'") ? Arrays.stream(word.split("'")) : Arrays.stream(new String[]{word})).map(String::trim).map(this::removeAccents).map(String::toLowerCase).filter(w -> !w.isBlank()).filter(w -> w.matches("[a-z]{2,}")).filter(this::isLikelyFrenchWord).forEach(uniqueWords::add);


        return new ArrayList<>(uniqueWords);
    }

    /**
     * Determines if a word is likely a valid French word using basic heuristics.
     *
     * @param word the word to check
     * @return {@code true} if the word is likely French, otherwise {@code false}
     */
    boolean isLikelyFrenchWord(String word) {
        word = word.toLowerCase();
        if (word.matches(".*[bcdfghjklmnpqrstvwxz]{4,}.*")) {
            return false;
        }

        if (!word.matches(".*[aeiouy].*")) {
            return false;
        }
        String[] invalidPatterns = {"zk", "xq", "qj", "vx", "jx", "zx", "qx", "sr", "zkn"};
        for (String pattern : invalidPatterns) {
            if (word.contains(pattern)) {
                return false;
            }
        }
        if (word.matches("^(sr|zk|zg|zd|fk|kz).*")) {
            return false;
        }
        if (word.length() == 5) {
            int vowelCount = word.replaceAll("[^aeiouy]", "").length();
            return vowelCount >= 2;
        }
        return true;
    }

    /**
     * Builds a result map from extracted data.
     *
     * @param title          the book title
     * @param page           the page number
     * @param words          the list of words
     * @param translatedText the translated text
     * @param detectedLang   the original detected language
     * @return a map with all extracted and processed data
     */
    private Map<String, Object> createResultMap(String title, String page, List<String> words, String translatedText, String detectedLang) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("title", title);
        resultMap.put("page", page);
        resultMap.put("words", words);
        resultMap.put("translatedText", translatedText);
        resultMap.put("language", detectedLang);

        return resultMap;
    }

    /**
     * Removes accents from a given string.
     *
     * @param text the input string with possible accents
     * @return a new string with all accents removed
     */
    String removeAccents(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }
}
