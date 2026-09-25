package be.esi.prj.bookle.mvp.model.scanner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class GoogleTranslatorTest {

    @Test
    void testDetectLanguage_successful() {
        // This test is limited because detectLanguage() uses real HTTP request.
        // In practice, we would extract HTTP logic into a helper or inject a mockable client.
        String lang = GoogleTranslator.detectLanguage("Bonjour");
        assertNotNull(lang);
        assertTrue(lang.matches("[a-z]{2}|und")); // could be "fr", "en", or "und"
    }

    @Test
    void testDetectLanguage_withInvalidText() {
        String lang = GoogleTranslator.detectLanguage("");
        assertEquals("und", lang); // undefined for empty input
    }

    @Test
    void testDetectAndTranslate_withEmptyText() {
        String translated = GoogleTranslator.detectAndTranslate("", "en");
        assertEquals("", translated); // returns original text
    }

    @Test
    void testDetectAndTranslate_withInvalidLanguageCode() {
        String result = GoogleTranslator.detectAndTranslate("Bonjour", "xx"); // invalid target language
        assertTrue(result.equalsIgnoreCase("Bonjour") || result.isEmpty());
    }
}
