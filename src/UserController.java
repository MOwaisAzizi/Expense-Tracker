import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.transform.Source;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserController {
 @FXML private TextField accountName;
@FXML private TextField accountPassword;


@FXML
private void createAccount(ActionEvent event) {
    String userName = accountName.getText();
    String password = accountPassword.getText();

    if (userName.isEmpty() || password.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Missing Input");
        alert.setHeaderText(null);
        alert.setContentText("Username and password cannot be empty.");
        alert.showAndWait();
        return;
    }

    String jdbcUrl = "jdbc:mysql://localhost:3306/expense_tracker";
    String dbUser = "root";
    String dbPassword = "";

    String insertQuery = "INSERT INTO user (name, password) VALUES (?, ?)";

    try (Connection con = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
         java.sql.PreparedStatement stmt = con.prepareStatement(insertQuery)) {

        stmt.setString(1, userName);
        stmt.setString(2, password); // Use hashed password in real apps

        int result = stmt.executeUpdate();
        if (result > 0) {
            // Switch to main.fxml
            Parent mainView = FXMLLoader.load(getClass().getResource("main.fxml"));
            Scene mainScene = new Scene(mainView);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
        } else {
            throw new SQLException("Insert failed, no rows affected.");
        }

    } catch (Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Account Creation Failed");
        alert.setHeaderText("Could not create account.");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}


@FXML private TextField userName;
@FXML private TextField userPassword;

@FXML
private void login(ActionEvent event) {
    String userNameValue = userName.getText();
    String userPasswordValue = userPassword.getText();

    String jdbcUrl = "jdbc:mysql://localhost:3306/expense_tracker";
    String dbUser = "root";
    String dbPassword = "";

    String query = "SELECT * FROM user WHERE name = ? AND password = ?";

    try (Connection con = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
         java.sql.PreparedStatement stmt = con.prepareStatement(query)) {

        stmt.setString(1, userNameValue);
        stmt.setString(2, userPasswordValue);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            // Login successful
            Parent mainView = FXMLLoader.load(getClass().getResource("main.fxml"));
            Scene mainScene = new Scene(mainView);
            Node source = (Node)event.getSource();
            Stage stage = (Stage)source.getScene().getWindow();
            stage.setScene(mainScene);
        } else {
            // Login failed
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid Username or Password");
            alert.setContentText("Please check your credentials and try again.");
            alert.showAndWait();
        }

    } catch (SQLException | IOException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}



    public void goToSignUp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("account.fxml")); // make sure path is correct
            Parent root = loader.load();

            // Get current stage
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

            // Get current stage
            Stage stage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}