package be.esi.prj.bookle.mvp.model.scanner;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;


class OcrScannerTest {

    private OcrScanner scanner;

    @BeforeEach
    void setup() {
        scanner = new OcrScanner();
    }

    @Test
    void testRemoveAccents() {
        String input = "éèàçêöù";
        String expected = "eeaceou";
        String actual = invokeRemoveAccents(input);
        assertEquals(expected, actual);
    }

    @Test
    void testIsLikelyFrenchWord_validWords() {
        assertTrue(invokeIsLikelyFrenchWord("bonjour"));
        assertTrue(invokeIsLikelyFrenchWord("français"));
        assertTrue(invokeIsLikelyFrenchWord("livre"));
    }

    @Test
    void testIsLikelyFrenchWord_invalidWords() {
        assertFalse(invokeIsLikelyFrenchWord("zqjx"));
        assertFalse(invokeIsLikelyFrenchWord("kzzz"));
        assertFalse(invokeIsLikelyFrenchWord("srzk"));
    }

    @Test
    void testExtractTitle() {
        String[] lines = {"123", "le petit prince", "chapter 1"};
        String title = invokeExtractTitle(lines);
        assertEquals("le petit prince", title);
    }

    @Test
    void testExtractPage() {
        String[] lines = {"le livre", "Chapitre", "47", "some text"};
        String page = invokeExtractPage(lines);
        assertEquals("47", page);
    }

    @Test
    void testCleanRawText() {
        String raw = "L’élève a dit : Bonjour !";
        String cleaned = invokeCleanRawText(raw);
        assertEquals("l'eleve a dit : bonjour !", cleaned);
    }

    @Test
    void testCleanContent() {
        String[] lines = {"le petit prince", "32", "Ceci est un texte avec des mots."};
        String content = invokeCleanContent(lines);
        assertTrue(content.contains("Ceci"));
        assertFalse(content.contains("prince"));
        assertFalse(content.contains("32"));
    }

    @Test
    void testExtractWords() {
        String input = "bonjour ceci est un test. français élève école";
        List<String> words = invokeExtractWords(input);
        assertTrue(words.contains("bonjour"));
        assertTrue(words.contains("ecole"));
        assertFalse(words.contains("zqjx"));
    }

    // === Utility: Access to package-private methods for testing ===

    private String invokeRemoveAccents(String text) {
        return scanner.removeAccents(text);
    }

    private boolean invokeIsLikelyFrenchWord(String word) {
        return scanner.isLikelyFrenchWord(word);
    }

    private String invokeExtractTitle(String[] lines) {
        return scanner.extractTitle(lines);
    }

    private String invokeExtractPage(String[] lines) {
        return scanner.extractPage(lines);
    }

    private String invokeCleanRawText(String raw) {
        return scanner.cleanRawText(raw);
    }

    private String invokeCleanContent(String[] lines) {
        return scanner.cleanContent(lines);
    }

    private List<String> invokeExtractWords(String text) {
        return scanner.extractWords(text);
    }
}
