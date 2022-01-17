package application;

import Controllers.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URISyntaxException;
public class SearchBar extends Application{
    
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
        window.getIcons().add(new Image(SearchBar.class.getResourceAsStream("/REST-ICO.png")));
        window.show();
    }
}
