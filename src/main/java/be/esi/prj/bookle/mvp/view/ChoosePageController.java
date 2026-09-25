package be.esi.prj.bookle.mvp.view;

import be.esi.prj.bookle.mvp.model.dto.WordKey;
import be.esi.prj.bookle.mvp.presenter.Presenter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ChoosePageController {

    @FXML
    private TextField titleField;
    @FXML
    private TextField pageField;
    @FXML
    private ListView<String> resultsList;

    private Presenter presenter;
    private final ObservableList<String> results = FXCollections.observableArrayList();
    private String selectedTitle;
    private int selectedPage;
    private final ShowAlert alert = new ShowAlert();

    /**
     * Sets the presenter for this view and loads all scanned pages.
     *
     * @param presenter the presenter connected to the model
     */
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        loadAllPages();
    }

    /**
     * Initializes the controller after the FXML is loaded.
     * Sets up the results list and click listener.
     */
    @FXML
    private void initialize() {
        resultsList.setItems(results);
        resultsList.setOnMouseClicked(this::handleResultClick);
    }

    /**
     * Loads all scanned pages and displays them in the list.
     * If no pages are found, shows a message.
     */
    private void loadAllPages() {
        List<WordKey> allPages = presenter.findPagesByTitle("", Optional.empty());
        results.clear();

        if (allPages.isEmpty()) {
            results.add("Aucune page scannée.");
        } else {
            Set<String> uniqueEntries = allPages.stream().map(key -> String.format("Titre: %s | Page: %d", key.bookTitle(), key.page())).collect(Collectors.toSet());

            results.addAll(uniqueEntries);
        }
    }

    /**
     * Searches for pages based on the book title and optional page number.
     * Displays the results or an error message.
     */
    @FXML
    private void handleSearch() {
        results.clear();
        String title = titleField.getText().trim();
        String pageText = pageField.getText().trim();

        if (title.isEmpty()) {
            results.add("Merci de saisir un titre de livre.");
            return;
        }

        try {
            Optional<Integer> page = pageText.isEmpty() ? Optional.empty() : Optional.of(Integer.parseInt(pageText));
            List<WordKey> foundWords = presenter.findPagesByTitle(title, page);

            if (foundWords.isEmpty()) {
                results.add("Aucun résultat trouvé.");
            } else {
                Set<String> uniqueResults = foundWords.stream().map(wk -> "Titre: " + wk.bookTitle() + " | Page: " + wk.page()).collect(Collectors.toSet());
                results.addAll(uniqueResults);
            }
        } catch (NumberFormatException e) {
            results.add("Numéro de page invalide.");
        } catch (Exception e) {
            results.add("Erreur de recherche.");
        }
    }

    /**
     * Handles double-click on a search result.
     * Extracts the book title and page, then starts the game if valid.
     *
     * @param event the mouse event triggered by user interaction
     */
    private void handleResultClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String selected = resultsList.getSelectionModel().getSelectedItem();
            if (selected != null && !selected.startsWith("Erreur") && !selected.startsWith("Aucun")) {
                extractTitleAndPage(selected);
                startGame();
            }
        }
    }

    /**
     * Extracts the book title and page number from the selected string in the list.
     *
     * @param selected the selected result string
     */
    private void extractTitleAndPage(String selected) {
        try {
            String[] parts = selected.split("\\|");
            selectedTitle = parts[0].replace("Titre:", "").trim();
            selectedPage = Integer.parseInt(parts[1].replace("Page:", "").trim());
        } catch (Exception e) {
            alert.showAlert("Erreur", "Impossible d'extraire le titre/la page.");
        }
    }

    /**
     * Tries to start a new game using the selected title and page.
     * Chooses a random secret word and opens the game screen.
     * If no valid words are found, shows an error.
     */
    private void startGame() {
        try {
            List<String> words = presenter.findWordsForPage(selectedTitle, selectedPage);

            if (words.isEmpty()) {
                alert.showAlert("Erreur", "Aucun mot disponible pour cette page.");
                return;
            }

            presenter.chooseNewSecretWord(words);
            openPlayScreen(selectedTitle, selectedPage);
        } catch (Exception e) {
            alert.showAlert("Erreur", "Impossible de démarrer la partie.");
        }
    }

    /**
     * Opens the play screen with the selected title and page.
     *
     * @param title the selected book title
     * @param page  the selected page number
     */
    private void openPlayScreen(String title, int page) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/play.fxml"));
            Parent root = loader.load();
            PlayController playController = loader.getController();

            playController.setPresenter(presenter);
            playController.setGameInfo(title, page);

            Stage stage = (Stage) resultsList.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            alert.showAlert("Erreur", "Impossible d'ouvrir l'écran de jeu.");
        }
    }

    /**
     * Returns to the main configuration screen.
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/config.fxml"));
            Parent root = loader.load();
            ConfigController configController = loader.getController();

            configController.setPresenter(presenter);

            Stage stage = (Stage) resultsList.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            alert.showAlert("Erreur", "Impossible de retourner au menu principal.");
        }
    }
}
