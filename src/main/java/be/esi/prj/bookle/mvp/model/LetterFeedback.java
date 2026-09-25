package be.esi.prj.bookle.mvp.model;

/**
 * Represents the feedback for a single letter in a Wordle guess.
 * This shows if the letter is correct, present in the word, or absent.
 */
public enum LetterFeedback {
    CORRECT,   // Green (well-placed)
    PRESENT,   // Yellow (exists but not in the right place)
    ABSENT
}

