package be.esi.prj.bookle.mvp.model;

/**
 * Represents the 5x5 grid used in the Wordle game.
 * Each row corresponds to an attempt, and each cell holds a single letter.
 */
public class Grid {
    private final char[][] grid;
    private int currentRow;

    /**
     * Constructs an empty 5x5 Wordle grid.
     * Each cell is initialized to a space character ' '.
     */
    public Grid() {
        this.grid = new char[5][5];
        this.currentRow = 0;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                grid[i][j] = ' ';
            }
        }
    }

    /**
     * Retrieves a character at a specific position in the grid.
     *
     * @param row the row index (0-based)
     * @param col the column index (0-based)
     * @return the character at the specified cell
     */
    protected char getLetterAt(int row, int col) {
        return grid[row][col];
    }

    /**
     * Returns the current row index (i.e., number of attempts made).
     *
     * @return the current row
     */
    protected int getCurrentRow() {
        return currentRow;
    }

    /**
     * Checks if the grid is full (i.e., 5 guesses have been made).
     *
     * @return true if full, false otherwise
     */
    protected boolean isFull() {
        return currentRow >= 5;
    }

    /**
     * Resets the grid to an empty state.
     */
    protected void reset() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                grid[i][j] = ' ';
            }
        }
        currentRow = 0;
    }

    /**
     * Sets a letter at the specified position in the grid.
     *
     * @param row    the row index
     * @param col    the column index
     * @param letter the character to insert
     */
    protected void setLetterAt(int row, int col, char letter) {
        grid[row][col] = letter;
    }

    /**
     * Advances to the next row (i.e., after a word is entered).
     */
    protected void advanceRow() {
        currentRow++;
    }

    /**
     * Inserts a full word into the current row and moves to the next row.
     *
     * @param word the 5-letter word to insert
     */
    protected void insertWord(String word) {
        for (int i = 0; i < 5; i++) {
            setLetterAt(currentRow, i, word.charAt(i));
        }
        currentRow++;
    }
}
