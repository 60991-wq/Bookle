package be.esi.prj.bookle.mvp.model.dto;

/**
 * Represents a player with a name and a score.
 *
 * @param name  the name of the player
 * @param score the score of the player
 */
public record Player(String name, int score) {
}
