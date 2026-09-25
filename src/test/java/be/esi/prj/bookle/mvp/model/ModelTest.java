package be.esi.prj.bookle.mvp.model;

import be.esi.prj.bookle.mvp.model.dto.Player;
import be.esi.prj.bookle.mvp.model.dto.WordKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    private Model model;

    @BeforeEach
    void setUp() {
        model = new Model();
        model.setCurrentPlayer(new Player("TestPlayer", 0));
    }

    @Test
    void testSetNewSecretWordPicksRandomWord() {
        model.setNewSecretWord(List.of("apple", "brave", "zebra"));
        String secret = model.getSecretWord();
        assertTrue(List.of("APPLE", "BRAVE", "ZEBRA").contains(secret));
    }


    @Test
    void testAddPlayerToDB() {
        Player player = new Player("NewGuy", 0);
        model.addPlayerToDB(player);
        List<Player> players = model.getAllPlayers();
        assertTrue(players.stream().anyMatch(p -> p.name().equals("NewGuy")));
    }

    @Test
    void testFindWordsForTitleAndPageReturnsOnlyFiveLetterWords() {
        model.insertWordsToDataBase(List.of("house", "small", "gigantic"), "SomeBook", 42);
        List<String> words = model.findWordsForTitleAndPage("SomeBook", 42);
        assertTrue(words.contains("HOUSE"));
        assertTrue(words.contains("SMALL"));
        assertFalse(words.contains("GIGANTIC")); // 8 lettres
    }


    @Test
    void testInsertWordsToDataBaseInsertsOnlyFiveLetterWords() {
        model.insertWordsToDataBase(List.of("apple", "tree", "brave", "funny", "tool"), "BookTitle", 1);
        List<String> result = model.findWordsForTitleAndPage("BookTitle", 1);
        assertTrue(result.contains("APPLE"));
        assertFalse(result.contains("TREE")); // 4 lettres
    }


    @Test
    void testInitializeSetsFirstPlayerIfExists() {
        model.addPlayerToDB(new Player("InitPlayer", 0));
        model.initialize();
        assertNotNull(model.getCurrentPlayer());
    }


    @Test
    void testGetAllPlayersNotNull() {
        List<Player> players = model.getAllPlayers();
        assertNotNull(players);
    }

    @Test
    void testUpdateScoreOnWin_AllAttemptsCases() {
        model.setSecretWord("HELLO");
        model.insertWordToGrid("HELLO");
        assertTrue(model.getCurrentPlayer().score() > 0);
    }

    @Test
    void testUpdateScoreOnWin_CatchRepositoryException() {
        // can't force RepositoryException easily without a mock -> only verifying no crash
        model.updateScoreOnWin();
        assertTrue(model.getCurrentPlayer().score() >= 0);
    }

    @Test
    void testFindBooksEmpty() {
        List<WordKey> books = model.findBooks("unknown", Optional.empty());
        assertTrue(books.isEmpty());
    }


    @Test
    void testSetAndGetCurrentPlayer() {
        Player player = new Player("AnotherPlayer", 0);
        model.setCurrentPlayer(player);

        assertEquals("AnotherPlayer", model.getCurrentPlayer().name());
    }

    @Test
    void testInsertWordToGrid() {
        model.setSecretWord("HELLO");

        boolean result = model.insertWordToGrid("HELLO");
        assertTrue(result);
    }

    @Test
    void testHasWonAfterCorrectWord() {
        model.setSecretWord("HELLO");
        model.insertWordToGrid("HELLO");

        assertTrue(model.hasWon());
    }

    @Test
    void testHasLostWhenGridIsFull() {
        model.setSecretWord("HELLO");

        for (int i = 0; i < 6; i++) {
            model.insertWordToGrid("WORLD");
        }

        assertTrue(model.hasLost());
    }

    @Test
    void testUpdateScoreOnWin() {
        model.setSecretWord("HELLO");
        model.insertWordToGrid("HELLO");

        int scoreAfterWin = model.getCurrentPlayer().score();
        assertTrue(scoreAfterWin > 0);
    }

    @Test
    void testPlayAgainResetsGrid() {
        model.setSecretWord("HELLO");
        model.insertWordToGrid("HELLO");

        model.playAgain();

        assertEquals(0, model.getGrid().getCurrentRow());
    }

    @Test
    void testSetSecretWord() {
        model.setSecretWord("world");
        assertEquals("WORLD", model.getSecretWord());
    }

    @Test
    void testGridAndFeedbackGridNotNull() {
        assertNotNull(model.getGrid());
        assertNotNull(model.getFeedbackGrid());
    }

    @Test
    void testInsertInvalidWordLength() {
        boolean result = model.insertWordToGrid("ABCD");
        assertFalse(result);
    }

    @Test
    void testGetLetterAndFeedback() {
        model.setSecretWord("HELLO");
        model.insertWordToGrid("HELLO");

        char letter = model.getLetterAt(0, 0);
        String feedback = model.getFeedbackAt(0, 0);

        assertEquals('H', letter);
        assertNotNull(feedback);
    }
}
