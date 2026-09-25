package be.esi.prj.bookle.mvp.model;

import be.esi.prj.bookle.mvp.model.dataBaseRepository.RepositoryException;
import be.esi.prj.bookle.observer.Observable;
import be.esi.prj.bookle.mvp.model.dto.Player;
import be.esi.prj.bookle.mvp.model.dto.WordKey;
import be.esi.prj.bookle.mvp.model.dataBaseRepository.PlayerRepository;
import be.esi.prj.bookle.mvp.model.dataBaseRepository.WordKeyRepository;
import be.esi.prj.bookle.mvp.model.dataAccessObject.ConnectionManager;
import be.esi.prj.bookle.mvp.model.dataAccessObject.PlayerDao;
import be.esi.prj.bookle.mvp.model.dataAccessObject.WordKeyDao;
import be.esi.prj.bookle.mvp.model.scanner.OcrScanner;

import java.util.*;
import java.sql.Connection;

/**
 * Main model class that handles game logic, data interaction, and state management.
 * It manages players, word keys, grid status, OCR scanning, and game progression.
 */
public class Model extends Observable {

    private final PlayerRepository playerRepo;
    private final WordKeyRepository wordRepo;
    private final Grid grid;
    private final GridFeedback feedbackGrid;
    private Player currentPlayer;
    private String secretWord;
    private String currentTitle;
    private int currentPage;

    /**
     * Constructs the model, initializing repositories, game grid, and feedback grid.
     */
    public Model() {
        Connection connection = ConnectionManager.getConnection();
        this.playerRepo = new PlayerRepository(new PlayerDao(connection));
        this.wordRepo = new WordKeyRepository(new WordKeyDao(connection));
        this.grid = new Grid();
        this.feedbackGrid = new GridFeedback();
    }

    /**
     * Initializes the model state on application start.
     * Selects the first available player if any are present.
     */
    public void initialize() {
        List<Player> players = getAllPlayers();
        if (!players.isEmpty()) {
            currentPlayer = players.getFirst();
        }
    }

    /**
     * Retrieves all registered players from the database.
     *
     * @return a list of all players, or an empty list if an error occurs.
     */
    public List<Player> getAllPlayers() {
        try {
            return playerRepo.findAll();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Updates the current player's score based on the number of attempts after a win.
     */
    protected void updateScoreOnWin() {
        if (currentPlayer != null) {
            int attempts = grid.getCurrentRow();
            int points;
            switch (attempts) {
                case 1 -> points = 50;
                case 2 -> points = 40;
                case 3 -> points = 30;
                case 4 -> points = 20;
                case 5 -> points = 10;
                default -> points = 0;
            }

            int newScore = currentPlayer.score() + points;
            currentPlayer = new Player(currentPlayer.name(), newScore);

            try {
                playerRepo.save(currentPlayer);
            } catch (RepositoryException e) {
                System.err.println("Erreur lors de la mise à jour du score: " + e.getMessage());
            }
        }
    }

    /**
     * Sets the current active player.
     *
     * @param player the player to set as current.
     */
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
        notifyObservers();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Inserts a list of 5-letter words into the database, associated with a book title and page number.
     *
     * @param words list of words to insert.
     * @param title title of the book.
     * @param page  page number of the book.
     */
    protected void insertWordsToDataBase(List<String> words, String title, int page) {
        try {
            for (String word : words) {
                if (word.length() == 5) {
                    wordRepo.save(new WordKey(word.toUpperCase(), title, page));
                }
            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds all word entries for a specific book title and optional page number.
     *
     * @param title the book title.
     * @param page  optional page number.
     * @return list of matching WordKey entries.
     */
    public List<WordKey> findBooks(String title, Optional<Integer> page) {
        try {
            return wordRepo.findByTitleAndPage(title, page);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves all 5-letter words associated with a specific title and page.
     *
     * @param title book title.
     * @param page  page number.
     * @return list of 5-letter words.
     */
    public List<String> findWordsForTitleAndPage(String title, int page) {
        try {
            List<WordKey> allWords = wordRepo.findByTitleAndPage(title, Optional.of(page));
            List<String> words = new ArrayList<>();
            for (WordKey key : allWords) {
                if (key.word().length() == 5) {
                    words.add(key.word());
                }
            }
            return words;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Inserts a word into the game grid, updates feedback, and checks for win condition.
     *
     * @param word the word to insert.
     * @return true if the word was inserted, false otherwise.
     */
    public boolean insertWordToGrid(String word) {
        if (word.length() != 5) return false;
        if (grid.isFull()) return false;

        grid.insertWord(word);
        feedbackGrid.insertFeedback(word, secretWord);
        if (hasWon()) {
            updateScoreOnWin();
        }
        notifyObservers(this);
        return true;
    }

    /**
     * Checks if the current word matches the secret word, indicating a win.
     *
     * @return true if the player has won, false otherwise.
     */
    public boolean hasWon() {
        if (grid.getCurrentRow() == 0) return false;
        StringBuilder lastWord = new StringBuilder();
        for (int col = 0; col < 5; col++) {
            lastWord.append(grid.getLetterAt(grid.getCurrentRow() - 1, col));
        }
        return lastWord.toString().equalsIgnoreCase(secretWord);
    }

    /**
     * Checks if the player has lost the game (grid full and not won).
     *
     * @return true if the player has lost, false otherwise.
     */
    public boolean hasLost() {
        if (grid.isFull() && !hasWon()) {
            grid.reset();
            return true;
        } else {
            return false;
        }
    }

    public String getSecretWord() {
        return secretWord;
    }

    public Grid getGrid() {
        return grid;
    }

    public GridFeedback getFeedbackGrid() {
        return feedbackGrid;
    }

    public char getLetterAt(int row, int col) {
        return getGrid().getLetterAt(row, col);
    }

    public String getFeedbackAt(int row, int col) {
        return getFeedbackGrid().getFeedbackAt(row, col).toString();
    }

    /**
     * Resets the game grid and feedback grid to start a new game.
     */
    public void playAgain() {
        grid.reset();
        feedbackGrid.reset();
    }

    public void setNewSecretWord(List<String> words) {
        int randomIndex = (int) (Math.random() * words.size());
        setSecretWord(words.get(randomIndex));
    }

    public void setSecretWord(String word) {
        this.secretWord = word.toUpperCase();
    }

    /**
     * Chooses a random 5-letter word from the database based on a title and page.
     *
     * @param title book title.
     * @param page  page number.
     * @throws RepositoryException if no matching word is found.
     */
    private void chooseRandomSecretWord(String title, int page) throws RepositoryException {
        List<WordKey> words = wordRepo.findAll();
        List<String> filteredWords = new ArrayList<>();

        for (WordKey wordKey : words) {
            if (wordKey.bookTitle().equalsIgnoreCase(title) && wordKey.page() == page && wordKey.word().length() == 5) {
                filteredWords.add(wordKey.word());
            }
        }

        if (filteredWords.isEmpty()) {
            throw new RepositoryException("Aucun mot de 5 lettres trouvé pour ce livre et cette page.");
        }

        int randomIndex = (int) (Math.random() * filteredWords.size());
        String randomWord = filteredWords.get(randomIndex);

        setSecretWord(randomWord);
    }

    /**
     * Performs OCR scanning on a page image, extracts data, saves valid words to the database,
     * and initializes a new game with a randomly chosen secret word.
     *
     * @param imagePath the file path of the image to scan.
     * @throws Exception if OCR fails or data is incomplete.
     */
    public void scanPageAndPrepareGame(String imagePath) throws Exception {
        OcrScanner scanner = new OcrScanner();
        scanner.setFileToScan(imagePath);
        Map<String, Object> result = scanner.scan();

        List<String> words = (List<String>) result.get("words");
        List<String> fiveLetterWords = words.stream().filter(word -> word.length() == 5).map(String::toUpperCase).toList();
        currentTitle = (String) result.get("title");
        currentPage = Integer.parseInt((String) result.get("page"));

        if (currentTitle.isEmpty()) {
            throw new Exception("Titre introuvable après OCR.");
        }

        insertWordsToDataBase(fiveLetterWords, currentTitle, currentPage);
        chooseRandomSecretWord(currentTitle, currentPage);
    }

    /**
     * Adds a new player to the database.
     *
     * @param newPlayer the player to add.
     */
    public void addPlayerToDB(Player newPlayer) {
        try {
            playerRepo.save(newPlayer);
        } catch (RepositoryException e) {
            System.err.println("Erreur lors de l'ajout du joueur: " + e.getMessage());
        }
        notifyObservers();
    }
}
