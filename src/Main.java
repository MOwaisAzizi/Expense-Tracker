import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Expense Tracker");
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.setWidth(800); 
        primaryStage.setMinWidth(800); 
        primaryStage.setMaxWidth(850);
        primaryStage.setResizable(false);
        primaryStage.setHeight(1000);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
