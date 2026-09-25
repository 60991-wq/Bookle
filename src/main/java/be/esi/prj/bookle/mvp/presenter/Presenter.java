package be.esi.prj.bookle.mvp.presenter;

import be.esi.prj.bookle.mvp.model.Model;
import be.esi.prj.bookle.mvp.model.dto.Player;
import be.esi.prj.bookle.mvp.model.dto.WordKey;
import be.esi.prj.bookle.mvp.view.*;
import be.esi.prj.bookle.observer.Observable;
import be.esi.prj.bookle.observer.Observer;

import java.util.List;
import java.util.Optional;


public class Presenter implements Observer {

    private final Model model;
    private ConfigController configView;
    private PlayController playView;


    /**
     * Creates a new Presenter and links it to the given model.
     *
     * @param model the game model
     */

    public Presenter(Model model) {
        this.model = model;
    }


    /**
     * Sets the configuration view and links it to the presenter.
     *
     * @param view the configuration screen controller
     */
    public void setConfigView(ConfigController view) {
        this.configView = view;
        configView.setPresenter(this);
    }

    /**
     * Sets the user configuration view and links it to the presenter.
     *
     * @param view the user configuration screen controller
     */
    public void setConfigUserView(ConfigUserController view) {
        view.setPresenter(this);
    }

    /**
     * Sets the page selection view and links it to the presenter.
     *
     * @param view the page selection screen controller
     */
    public void setChoosePageView(ChoosePageController view) {
        view.setPresenter(this);
    }

    /**
     * Sets the play view and links it to the presenter.
     *
     * @param view the main game screen controller
     */
    public void setPlayView(PlayController view) {
        this.playView = view;
        playView.setPresenter(this);
    }

    /**
     * Initializes the game model, usually called at startup.
     */
    public void initialize() {
        model.initialize();
    }


    /**
     * Loads all players from the model.
     *
     * @return a list of players
     */
    public List<Player> loadPlayers() {
        return model.getAllPlayers();
    }

    public void selectPlayer(Player player) {
        model.setCurrentPlayer(player);
    }

    /**
     * Tries to insert a word into the game grid.
     *
     * @param word the guessed word
     * @return true if the word is valid and added, false otherwise
     */
    public boolean insertWord(String word) {
        return model.insertWordToGrid(word);
    }

    public boolean isGameWon() {
        return model.hasWon();
    }

    public boolean isGameLost() {
        return model.hasLost();
    }

    public String getSecretWord() {
        return model.getSecretWord();
    }

    /**
     * Finds all WordKey entries for a given book title and optional page.
     *
     * @param title the book title
     * @param page  the page number (optional)
     * @return list of WordKey objects
     */
    public List<WordKey> findPagesByTitle(String title, Optional<Integer> page) {
        return model.findBooks(title, page);
    }

    /**
     * Finds all 5-letter words for a specific title and page.
     *
     * @param title the book title
     * @param page  the page number
     * @return list of valid words
     */
    public List<String> findWordsForPage(String title, int page) {
        return model.findWordsForTitleAndPage(title, page);
    }

    public void chooseNewSecretWord(List<String> words) {
        model.setNewSecretWord(words);
    }

    /**
     * Called when the model updates.
     * Refreshes the game grid and feedback grid in the play view.
     *
     * @param observable the object that sent the update
     * @param arg        optional update data (not used)
     */
    @Override
    public void update(Observable observable, Object arg) {
        if (playView != null) {
            playView.updateGameGrid();
        }
    }

    public char getLetterAt(int row, int col) {
        return model.getLetterAt(row, col);
    }

    public String getFeedbackAt(int row, int col) {
        return model.getFeedbackAt(row, col);
    }

    public int getCurrentScore() {
        return model.getCurrentPlayer().score();
    }

    /**
     * Resets the game to start a new round.
     */
    public void playAgain() {
        model.playAgain();
    }

    public String getCurrentPlayerName() {
        return model.getCurrentPlayer().name();
    }

    public void addPlayer(Player newPlayer) {
        model.addPlayerToDB(newPlayer);
    }

    // Méthodes pour le thread
    public void startNewGame() throws Exception {
        model.scanPageAndPrepareGame(configView.getImagePath());
    }

    /**
     * Opens the play screen using the current title and page from the model.
     */
    public void showPlayScreen() {
        if (configView != null) {
            configView.openPlayScreen(model.getCurrentTitle(), model.getCurrentPage());
        }
    }
}
