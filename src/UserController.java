import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.Helper;

public class UserController {
 @FXML private TextField accountName;
@FXML private TextField accountPassword;

@FXML
private void createAccount(ActionEvent event) {
    String userName = accountName.getText();
    String password = accountPassword.getText();

//input validations
if (userName.isEmpty() || !userName.matches("[a-zA-Z\\s]{2,50}")) {
    Helper.showAlert("Error","Enter a valid userName (letters only, 2–50 chars)");
   return;
}

if (password.length() < 6 || !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
     Helper.showAlert("Error","Password must be at least 6 characters and contain letters and numbers.");
     return;
    }

    if (userName.isEmpty() || password.isEmpty()) {
        Helper.showAlert("Missing Input", "Username and password cannot be empty.");
        return;
    }

    String insertQuery = "INSERT INTO user (name, password) VALUES (?, ?)";

    try (Connection con = Helper.getConnection();
         java.sql.PreparedStatement stmt = con.prepareStatement(insertQuery)) {

        stmt.setString(1, userName);
        stmt.setString(2, password);

        int result = stmt.executeUpdate();
        if (result > 0) {
            Parent mainView = FXMLLoader.load(getClass().getResource("main.fxml"));
            Scene mainScene = new Scene(mainView);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
        } else {
            throw new SQLException("Insert failed, no rows affected.");
        }

    } catch (Exception e) {
        e.printStackTrace();
        Helper.showAlert("Error", e.getMessage());
    }
}


@FXML private TextField userName;
@FXML private TextField userPassword;

@FXML
private void login(ActionEvent event) throws ClassNotFoundException {
    String userNameValue = userName.getText();
    String userPasswordValue = userPassword.getText();

    
if (userNameValue.isEmpty() || userPasswordValue.isEmpty() ) {
    Helper.showAlert("Error","Please enter your name and password");
   return;
}

    String query = "SELECT * FROM user WHERE name = ? AND password = ?";

    try (
        Connection con = Helper.getConnection();
         java.sql.PreparedStatement stmt = con.prepareStatement(query)
         ) {

        stmt.setString(1, userNameValue);
        stmt.setString(2, userPasswordValue);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Parent mainView = FXMLLoader.load(getClass().getResource("main.fxml"));
            Scene mainScene = new Scene(mainView);
            Node source = (Node)event.getSource();
            Stage stage = (Stage)source.getScene().getWindow();
            stage.setScene(mainScene);
        } else {
            Helper.showAlert("Login Failed", "Invalid Username or Password");
        }

    } catch (SQLException | IOException e) {
        e.printStackTrace();
        Helper.showAlert("Database Error", e.getMessage());
    }
}

    public void goToSignUp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("account.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml")); // make sure path is correct
            Parent root = loader.load();

            Stage stage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}