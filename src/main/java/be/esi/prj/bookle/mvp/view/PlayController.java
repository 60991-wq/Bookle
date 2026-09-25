package be.esi.prj.bookle.mvp.view;

import be.esi.prj.bookle.mvp.presenter.Presenter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PlayController {

    @FXML
    private Label messageLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private TextField inputWord;
    @FXML
    private Button btnEnter;
    @FXML
    private GridPane gridPane;
    @FXML
    private Label bookLabel;

    private Presenter presenter;
    private String bookTitle;
    private int pageNumber;
    private boolean wordSubmitted = false;
    private final ShowAlert alert = new ShowAlert();


    /**
     * Sets the presenter that connects this view to the game logic.
     *
     * @param presenter the presenter to set
     */
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Sets the game information such as book title and page number.
     * Updates labels with the player's name and current score.
     *
     * @param title the book title
     * @param page  the page number
     */
    public void setGameInfo(String title, int page) {
        this.bookTitle = title;
        this.pageNumber = page;
        bookLabel.setText(title + " - page " + page);
        this.scoreLabel.setText("Scorer: "+presenter.getCurrentScore());
        updateMessage("Bienvenue "+presenter.getCurrentPlayerName()+"!");
    }

    /**
     * Initializes the controller and sets up actions and input filters for the input field.
     */
    @FXML
    public void initialize() {
        btnEnter.setOnAction(event -> submitWord());
        inputWord.setOnAction(event -> submitWord());
        inputWord.addEventFilter(KeyEvent.KEY_TYPED, this::filterKeyTyped);
    }

    /**
     * Filters the keyboard input to only allow letters and limit the input to 5 characters.
     *
     * @param event the key event triggered by typing
     */
    private void filterKeyTyped(KeyEvent event) {
        if (wordSubmitted) {
            event.consume();
            return;
        }
        String currentText = inputWord.getText();
        if (event.getCharacter().matches("[a-zA-Z]")) {
            if (currentText.length() >= 5) event.consume();
        } else {
            event.consume();
        }
    }

    /**
     * Validates and submits the player's guessed word.
     * Updates the game state, grid UI, score, and messages.
     */
    private void submitWord() {
        String word = inputWord.getText().toUpperCase();
        if (word.length() != 5) {
            updateMessage("Le mot doit contenir 5 lettres !");
            return;
        }

        boolean accepted = presenter.insertWord(word);
        if (!accepted) {
            updateMessage("Mot invalide ou déjà soumis !");
            return;
        }

        updateGridUI();
        updateScore();
        inputWord.clear();
        updateMessageState();
    }

    /**
     * Updates the message label based on whether the game is won, lost, or ongoing.
     */
    private void updateMessageState() {
        if (presenter.isGameWon()) {
            updateMessage("Bravo, vous avez gagné !");
            disableInput();
        } else if (presenter.isGameLost()) {
            updateMessage("Perdu ! Le mot était : " + presenter.getSecretWord());
            disableInput();
        } else {
            updateMessage("Nouveau mot !");
        }
    }

    /**
     * Updates the entire grid with letters and feedback colors.
     */
    private void updateGridUI() {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                updateCellStyle(row, col);
            }
        }
    }

    /**
     * Updates the style and text of a specific cell in the grid.
     *
     * @param row the row index
     * @param col the column index
     */
    private void updateCellStyle(int row, int col) {
        String labelId = "cell_" + row + "_" + col;
        Label cell = (Label) gridPane.lookup("#" + labelId);
        char letter = presenter.getLetterAt(row, col);

        if (letter != ' ') {
            cell.setText(String.valueOf(letter));
            String feedback = presenter.getFeedbackAt(row,col);
            applyFeedbackStyle(cell, feedback);
        } else {
            clearCell(cell);
        }
    }

    /**
     * Clears the content and style of a grid cell.
     *
     * @param cell the label representing the grid cell
     */
    private void clearCell(Label cell) {
        cell.setText("");
        cell.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-text-fill: white;");
    }

    /**
     * Applies color styling to a cell based on the feedback (CORRECT, PRESENT, ABSENT).
     *
     * @param cell     the label to style
     * @param feedback the feedback value
     */
    private void applyFeedbackStyle(Label cell, String feedback) {
        String style = switch (feedback) {
            case "CORRECT" -> "-fx-background-color: green;";
            case "PRESENT" -> "-fx-background-color: goldenrod;";
            default -> "-fx-background-color: black;";
        };
        cell.setStyle(style + "-fx-border-color: white; -fx-text-fill: white;");
    }


    /**
     * Starts a new game by choosing a new word and resetting the interface.
     */
    @FXML
    private void handleRestart() {
        presenter.chooseNewSecretWord(presenter.findWordsForPage(bookTitle, pageNumber));
        resetGame();
    }

    /**
     * Returns to the configuration screen and resets the game state.
     */
    @FXML
    private void handleReturn() {
        resetGame();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/config.fxml"));
            Parent root = loader.load();
            ConfigController configController = loader.getController();

            configController.setPresenter(presenter);
            presenter.setConfigView(configController);
            Stage stage = (Stage) bookLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            alert.showAlert("Erreur", "Impossible de revenir au menu principal.");
        }
    }

    /**
     * Closes the game window.
     */
    @FXML
    private void handleExit() {
        Stage stage = (Stage) bookLabel.getScene().getWindow();
        stage.close();
    }


    /**
     * Updates the visual game grid using the grid and feedback data from the model.
     *
     */
    public void updateGameGrid() {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                updateCellStyle(row, col);
            }
        }
    }

    /**
     * Updates the message label with the given text.
     *
     * @param msg the message to display
     */
    private void updateMessage(String msg) {
        messageLabel.setText(msg);
    }

    /**
     * Updates the score label with the current player's score.
     */
    private void updateScore() {
        scoreLabel.setText("Score: " + presenter.getCurrentScore());
    }

    /**
     * Disables the input field and enter button.
     * Used after the game ends.
     */
    private void disableInput() {
        inputWord.setDisable(true);
        btnEnter.setDisable(true);
    }

    /**
     * Enables the input field and enter button.
     * Used when starting a new game.
     */
    private void enableInput() {
        inputWord.setDisable(false);
        btnEnter.setDisable(false);
    }

    /**
     * Resets the game interface and state for a new round.
     */
    private void resetGame() {
        inputWord.clear();
        enableInput();
        updateMessage("Nouvelle partie !");
        presenter.playAgain();
        wordSubmitted = false;
        updateScore();
        updateGridUI();
    }
}
