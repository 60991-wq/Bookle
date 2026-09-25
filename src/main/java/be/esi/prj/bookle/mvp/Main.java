package be.esi.prj.bookle.mvp;

import be.esi.prj.bookle.mvp.model.Model;
import be.esi.prj.bookle.mvp.presenter.Presenter;
import be.esi.prj.bookle.mvp.view.ConfigController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Model model = new Model();
        Presenter presenter = new Presenter(model);

        // 2. Load the first view (Config)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/config.fxml"));
        Parent root = loader.load();
        ConfigController configController = loader.getController();

        // 3. Link the view with the presenter
        presenter.setConfigView(configController);

        // 4.  Model observes the Presenter
        model.addObserver(presenter);

        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Bookle - Configuration");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // 5. Initialize the Presenter (it initializes the view)
        presenter.initialize();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
