import java.io.IOException;

import javax.xml.transform.Source;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class UserController {
         @FXML
    private void createAccount(ActionEvent event) {
        // Your code here. For example:
        System.out.println("Create account button clicked!");
        try {
            Parent mainView = FXMLLoader.load(getClass().getResource("main.fxml"));
            Scene mainScene = new Scene(mainView);
            Node source = (Node)event.getSource();
            Stage stage = (Stage)source.getScene().getWindow();
            stage.setScene(mainScene);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("fail to load MainView");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

      @FXML
    private void login(ActionEvent event) {
        // Your code here. For example:
        System.out.println("login button clicked!");
        try {
            Parent mainView = FXMLLoader.load(getClass().getResource("main.fxml"));
            Scene mainScene = new Scene(mainView);
            Node source = (Node)event.getSource();
            Stage stage = (Stage)source.getScene().getWindow();
            stage.setScene(mainScene);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("fail to load MainView");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
