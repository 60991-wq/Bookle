package be.esi.prj.bookle.mvp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GridFeedbackTest {

    @Test
    void testInsertFeedbackCorrect() {
        GridFeedback gf = new GridFeedback();
        gf.insertFeedback("apple", "apple");
        for (int i = 0; i < 5; i++)
            assertEquals(LetterFeedback.CORRECT, gf.getFeedbackAt(0, i));
    }

    @Test
    void testInsertFeedbackPresentAndAbsent() {
        GridFeedback gf = new GridFeedback();
        gf.insertFeedback("plane", "apple");
        assertEquals(LetterFeedback.PRESENT, gf.getFeedbackAt(0, 0)); // Presenter is in apple
        assertEquals(LetterFeedback.PRESENT, gf.getFeedbackAt(0, 1)); // L is in apple
        assertEquals(LetterFeedback.PRESENT, gf.getFeedbackAt(0, 2)); // A is in apple
        assertEquals(LetterFeedback.ABSENT, gf.getFeedbackAt(0, 3));  // N not in apple
        assertEquals(LetterFeedback.CORRECT, gf.getFeedbackAt(0, 4)); // E is correct
    }


    @Test
    void testReset() {
        GridFeedback gf = new GridFeedback();
        gf.insertFeedback("apple", "apple");
        gf.reset();
        assertNull(gf.getFeedbackAt(0, 0));
    }

    @Test
    void testRemoveLastFeedback() {
        GridFeedback gf = new GridFeedback();
        gf.insertFeedback("apple", "apple");
        gf.removeLastFeedback();
        assertNull(gf.getFeedbackAt(0, 0));
    }

    @Test
    void testInvalidInsertFeedback() {
        GridFeedback gf = new GridFeedback();
        assertThrows(IllegalArgumentException.class, () -> gf.insertFeedback("abc", "apple"));
    }
}
