package be.esi.prj.bookle.mvp.model.dto;

/**
 * Represents a key that identifies a word in a book at a specific page.
 *
 * @param word      the word
 * @param bookTitle the title of the book
 * @param page      the page number where the word appears
 */
public record WordKey(String word, String bookTitle, int page) {
}
