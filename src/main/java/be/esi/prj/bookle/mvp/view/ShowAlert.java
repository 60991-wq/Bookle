package be.esi.prj.bookle.mvp.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public class ShowAlert {

    /**
     * Shows a warning alert dialog with a custom title and message.
     * The alert is styled before being displayed.
     *
     * @param title   the title of the alert window
     * @param message the message to display in the alert
     */
    public void showAlert(String title, String message) {
        Alert alert = createAlert(title, message);
        styleAlert(alert);
        alert.showAndWait();
    }

    /**
     * Creates a basic warning alert with the given title and message content.
     *
     * @param title   the title of the alert
     * @param content the message shown in the alert
     * @return a configured Alert object
     */
    private Alert createAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert;
    }

    /**
     * Applies custom styles to the alert dialog and its buttons.
     *
     * @param alert the alert to style
     */
    private void styleAlert(Alert alert) {
        DialogPane pane = alert.getDialogPane();
        styleDialogPane(pane);
        styleButtons(pane);
        renameCancelButton(pane);
    }

    /**
     * Renames the "Cancel" button in the alert dialog to "Cancel" (in English),
     * if it is present.
     *
     * @param pane the dialog pane containing the buttons
     */
    private void renameCancelButton(DialogPane pane) {
        Button cancelButton = (Button) pane.lookupButton(ButtonType.CANCEL);
        if (cancelButton != null) {
            cancelButton.setText("Cancel");
        }
    }

    /**
     * Sets custom styles for the dialog pane background, border, and text.
     *
     * @param pane the dialog pane to style
     */
    private void styleDialogPane(DialogPane pane) {
        pane.setStyle("-fx-background-color: skyblue;" + "-fx-border-color: white;" + "-fx-border-width: 2px;" + "-fx-font-size: 14px;" + "-fx-font-family: 'System';" + "-fx-text-fill: white;");
    }

    /**
     * Applies custom styles to all buttons in the alert dialog.
     *
     * @param pane the dialog pane containing the buttons
     */
    private void styleButtons(DialogPane pane) {
        for (ButtonType type : pane.getButtonTypes()) {
            Button button = (Button) pane.lookupButton(type);
            if (button != null) {
                button.setStyle("-fx-background-color: #0074D9;" + " -fx-text-fill: white;" + " -fx-font-weight: bold;");
            }
        }
    }
}
