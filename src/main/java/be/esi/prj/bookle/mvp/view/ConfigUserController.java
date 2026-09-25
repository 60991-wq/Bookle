package be.esi.prj.bookle.mvp.view;

import be.esi.prj.bookle.mvp.model.dto.Player;
import be.esi.prj.bookle.mvp.presenter.Presenter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ConfigUserController {

    @FXML
    private VBox playerListVBox;

    private Presenter presenter;
    private final ShowAlert alert = new ShowAlert();

    /**
     * Sets the presenter that connects this view to the model.
     *
     * @param presenter the presenter to set
     */
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Reloads and displays the list of all players from the model.
     */
    public void refreshPlayers() {
        List<Player> players = presenter.loadPlayers();
        displayPlayers(players);
    }

    /**
     * Displays the list of players as buttons in the VBox.
     *
     * @param players list of players to show
     */
    private void displayPlayers(List<Player> players) {
        playerListVBox.getChildren().clear();
        for (Player player : players) {
            Button playerButton = createPlayerButton(player);
            playerListVBox.getChildren().add(playerButton);
        }
    }

    /**
     * Creates a button for a player. When clicked, the player is selected,
     * and the screen returns to the main config view.
     *
     * @param player the player to create a button for
     * @return the created button
     */
    private Button createPlayerButton(Player player) {
        Button button = new Button(player.name() + " - Score: " + player.score());
        button.setPrefWidth(580);
        button.setStyle("""
                -fx-background-color: #4FC3F7;
                -fx-text-fill: white;
                -fx-font-size: 16px;
                -fx-background-radius: 20;
                -fx-padding: 10 20;
                """);

        button.setOnAction(e -> {
            presenter.selectPlayer(player);
            returnToConfig();
        });
        return button;
    }

    /**
     * Handles the "Add Player" action.
     * Displays a text field and button to create and save a new player.
     * Adds the player to the database and selects them.
     */
    @FXML
    private void handleAddPlayer() {
        HBox inputBox = new HBox(10);
        inputBox.setStyle("-fx-padding: 20;");
        inputBox.setPrefWidth(600);

        TextField nameField = new TextField();
        nameField.setPromptText("Enter player name");
        nameField.setPrefWidth(520);

        Button validateButton = new Button("⏎");
        validateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        validateButton.setOnAction(e -> {
            String name = nameField.getText();
            if (name != null && !name.trim().isEmpty()) {
                Player newPlayer = new Player(name.trim(), 0);
                presenter.addPlayer(newPlayer);  // add to the data base
                presenter.selectPlayer(newPlayer); // select to play with
                refreshPlayers(); // recharge list
                returnToConfig();
            }
        });

        inputBox.getChildren().addAll(nameField, validateButton);
        playerListVBox.getChildren().add(inputBox);
    }

    /**
     * Handles the action to return to the main configuration screen.
     */
    @FXML
    private void handleReturnToConfig() {
        returnToConfig();
    }

    /**
     * Loads and displays the main configuration screen (config.fxml).
     * Called when the user selects a player or wants to go back.
     */
    private void returnToConfig() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/config.fxml"));
            Parent root = loader.load();
            ConfigController configController = loader.getController();

            presenter.setConfigView(configController);

            Stage stage = (Stage) playerListVBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            alert.showAlert("Erreur de navigation", "Impossible de revenir à l'écran de configuration.");
        }
    }
}
