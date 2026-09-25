package be.esi.prj.bookle.mvp.view;

import be.esi.prj.bookle.mvp.presenter.Presenter;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ConfigController {

    @FXML
    private Button btnChooseImage;
    @FXML
    private Button btnUser;
    @FXML
    private Label labelRules;
    @FXML
    private ProgressIndicator loadingSpinner;
    @FXML
    private Label loadingLabel;

    private Presenter presenter;
    private boolean isImageLoaded = false;
    private String imagePath;
    private final ShowAlert alert = new ShowAlert();

    /**
     * Sets the presenter for this controller.
     *
     * @param presenter the presenter that manages the game logic
     */
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Called automatically when the FXML is loaded.
     * Sets up the button to open the user configuration screen.
     */
    @FXML
    private void initialize() {
        btnUser.setOnAction(event -> openUserConfig());
    }

    /**
     * Opens a file chooser dialog to let the user select an image.
     * Stores the image path if a file is selected.
     * Hides the button after selection.
     */
    @FXML
    private void handleChooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        Stage stage = (Stage) btnChooseImage.getScene().getWindow();
        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            imagePath = file.getAbsolutePath();
            isImageLoaded = true;
            btnChooseImage.setVisible(false);
        } else {
            alert.showAlert("Info", "Aucun fichier sélectionné.");
        }
    }

    /**
     * Returns the path of the selected image.
     *
     * @return the path to the image
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Starts the game after verifying that an image has been selected.
     * Shows a loading animation and runs the scan task in a separate thread.
     */
    @FXML
    private void handleStartGame() {
        if (!isImageLoaded) {
            alert.showAlert("Erreur", "Veuillez d'abord sélectionner une image avant de commencer le jeu.");
            return;
        }
        showLoadingAnimation(true);

        Task<Void> scanTask = createScanTask();
        configureTaskEvents(scanTask);
        startScanTask(scanTask);
    }

    /**
     * Creates a background task to scan the selected image and prepare the game.
     *
     * @return the scan task
     */
    private Task<Void> createScanTask() {
        return new Task<>() {
            @Override
            protected Void call() {
                try {
                    presenter.startNewGame();
                } catch (Exception e) {
                    Platform.runLater(() -> alert.showAlert("Erreur", "Erreur pendant l'analyse de l'image: " + e.getMessage()));
                }
                return null;
            }
        };
    }

    /**
     * Sets what happens when the scan task finishes or fails.
     *
     * @param task the scan task to configure
     */
    private void configureTaskEvents(Task<Void> task) {
        task.setOnSucceeded(event -> {
            showLoadingAnimation(false);
            presenter.showPlayScreen();
        });

        task.setOnFailed(event -> {
            showLoadingAnimation(false);
            alert.showAlert("Erreur", "Échec de la préparation du jeu.");
        });
    }

    /**
     * Starts the scan task in a background thread.
     *
     * @param task the scan task to run
     */
    private void startScanTask(Task<Void> task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Shows or hides the loading animation and label.
     *
     * @param show true to show loading, false to hide
     */
    private void showLoadingAnimation(boolean show) {
        loadingSpinner.setVisible(show);
        loadingLabel.setText(show ? "Chargement de la page..." : "");
        loadingLabel.setVisible(show);
    }

    /**
     * Shows or hides the game rules label when the button is clicked.
     */
    @FXML
    private void handleShowRules() {
        labelRules.setVisible(!labelRules.isVisible());
    }

    /**
     * Closes the application window.
     */
    @FXML
    private void handleExit() {
        Stage stage = (Stage) btnChooseImage.getScene().getWindow();
        stage.close();
    }

    /**
     * Opens the screen to choose a page manually from previously scanned books.
     */
    @FXML
    private void handleChoosePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/choosePage.fxml"));
            Parent root = loader.load();
            ChoosePageController choosePageController = loader.getController();

            presenter.setChoosePageView(choosePageController);

            Stage stage = (Stage) btnUser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            alert.showAlert("Erreur", "Impossible d'ouvrir l'écran de choix de page.");
        }
    }

    /**
     * Opens the screen where the user can select or manage players.
     */
    private void openUserConfig() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/configUser.fxml"));
            Parent root = loader.load();
            ConfigUserController configUserController = loader.getController();

            presenter.setConfigUserView(configUserController);
            configUserController.refreshPlayers();

            Stage stage = (Stage) btnUser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            alert.showAlert("Erreur", "Impossible d'ouvrir la configuration utilisateur.");
        }
    }

    /**
     * Opens the game screen and sets the book title and page.
     *
     * @param title the selected book title
     * @param page  the selected page number
     */
    public void openPlayScreen(String title, int page) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/play.fxml"));
            Parent root = loader.load();
            PlayController playController = loader.getController();

            presenter.setPlayView(playController);
            playController.setGameInfo(title, page);

            Stage stage = (Stage) btnUser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            alert.showAlert("Erreur", "Impossible d'ouvrir l'écran de jeu.");
        }
    }
}
