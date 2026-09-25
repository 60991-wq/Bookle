package be.esi.prj.bookle.mvp.model;

/**
 * Manages feedback for each row of guesses in the Wordle game.
 * Feedback includes whether letters are correct, present, or absent.
 */
public class GridFeedback {
    private final LetterFeedback[][] feedback;
    private int currentRow;

    /**
     * Constructs an empty {@code GridFeedback} for a 5x5 Wordle grid.
     */
    public GridFeedback() {
        this.feedback = new LetterFeedback[5][5];
        this.currentRow = 0;
    }

    /**
     * Inserts feedback for a guess compared to the secret word.
     * Determines correctness, presence, or absence of each letter.
     *
     * @param guess  the guessed word (must be 5 letters)
     * @param secret the secret word to compare against (must be 5 letters)
     * @throws IllegalArgumentException if either word is null or not 5 characters
     */
    protected void insertFeedback(String guess, String secret) {
        validateInputs(guess, secret);

        LetterFeedback[] rowFeedback = new LetterFeedback[5];
        boolean[] usedInSecret = new boolean[5];

        markCorrectLetters(guess, secret, rowFeedback, usedInSecret);
        markPresentOrAbsentLetters(guess, secret, rowFeedback, usedInSecret);

        feedback[currentRow] = rowFeedback;
        currentRow++;
    }

    /**
     * Removes the last inserted feedback row (used in undo operations).
     */
    protected void removeLastFeedback() {
        if (currentRow == 0) return;

        currentRow--;
        for (int i = 0; i < 5; i++) {
            feedback[currentRow][i] = null;
        }
    }

    /**
     * Checks that both guess and secret are not null and have exactly 5 letters.
     *
     * @param guess  the guessed word
     * @param secret the correct word
     * @throws IllegalArgumentException if guess or secret is invalid
     */
    private void validateInputs(String guess, String secret) {
        if (guess == null || secret == null || guess.length() != 5 || secret.length() != 5) {
            throw new IllegalArgumentException("Words must be exactly 5 characters long.");
        }
    }

    /**
     * Marks letters that are correct (same letter in the same position)
     * in the feedback row.
     *
     * @param guess       the guessed word
     * @param secret      the correct word
     * @param rowFeedback the array to store feedback for each letter
     * @param used        keeps track of letters already matched in the secret word
     */
    private void markCorrectLetters(String guess, String secret, LetterFeedback[] rowFeedback, boolean[] used) {
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == secret.charAt(i)) {
                rowFeedback[i] = LetterFeedback.CORRECT;
                used[i] = true;
            }
        }
    }

    /**
     * Marks the remaining letters that are either present (in the word but wrong place)
     * or absent (not in the word).
     *
     * @param guess       the guessed word
     * @param secret      the correct word
     * @param rowFeedback the array to store feedback for each letter
     * @param used        keeps track of letters already matched in the secret word
     */
    private void markPresentOrAbsentLetters(String guess, String secret, LetterFeedback[] rowFeedback, boolean[] used) {
        for (int i = 0; i < 5; i++) {
            if (rowFeedback[i] != null) continue;

            char guessedChar = guess.charAt(i);
            boolean found = false;

            for (int j = 0; j < 5; j++) {
                if (!used[j] && guessedChar == secret.charAt(j)) {
                    found = true;
                    used[j] = true;
                    break;
                }
            }

            rowFeedback[i] = found ? LetterFeedback.PRESENT : LetterFeedback.ABSENT;
        }
    }

    /**
     * Gets the feedback value (correct, present, or absent)
     * for a specific cell in the feedback grid.
     *
     * @param row the row number (0 to 4)
     * @param col the column number (0 to 4)
     * @return the feedback for that position
     */
    protected LetterFeedback getFeedbackAt(int row, int col) {
        return feedback[row][col];
    }

    /**
     * Clears all feedback from the grid and starts over.
     * Used to reset the game state.
     */
    protected void reset() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                feedback[i][j] = null;
            }
        }
        currentRow = 0;
    }
}
