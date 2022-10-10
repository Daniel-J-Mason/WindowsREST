package application;

import controller.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class SearchBar extends Application{
    
    /**
     * @param window
     * @throws IOException
     * @throws URISyntaxException
     * Creates a window with some basic settings and uses mainView.fxml for window configuration
     */
    @Override
    public void start(Stage window) throws IOException, URISyntaxException {
    
        window.setTitle("REV Engineering Search Tool");
        window.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainView.fxml"));
        Parent root = loader.load();
        
        MainViewController controller = loader.getController();
        controller.setGetHostController(getHostServices());
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.getIcons().add(new Image(Objects.requireNonNull(SearchBar.class.getResourceAsStream("/images/REST-ICO-128.png"))));
        window.show();
    }
}
