// import java.time.LocalDate;

// import javafx.event.ActionEvent;
// import javafx.fxml.FXML;
// import javafx.scene.control.TextField;

// public class MainController {

// @FXML
// private TextField fName;

// @FXML
// private TextField price;

// @FXML
// private TextField amount;

// public void buttonClick(ActionEvent event) { 
//     LocalDate currentDate = LocalDate.now();
// System.out.println("Current date: " + currentDate);

//     String name = fName.getText();
//     String priceValue = price.getText();
//     String amountValue = amount.getText();
// System.out.println("Name:"+name);   
// System.out.println("price:"+priceValue);   
// System.out.println("amount:"+amountValue);   
//  }
// }

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class MainController {

    @FXML
    private TextField fName;

    @FXML
    private TextField price;

    @FXML
    private TextField amount;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expense_tracker";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Empty password as per your InsertController

    public void buttonClick(ActionEvent event) {
        LocalDate currentDate = LocalDate.now();
        System.out.println("Current date: " + currentDate);

        String name = fName.getText();
        String priceValue = price.getText();
        String amountValue = amount.getText();
        
        System.out.println("Name: " + name);   
        System.out.println("Price: " + priceValue);   
        System.out.println("Amount: " + amountValue);

        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // 2. Create SQL statement
            String sql = "INSERT INTO note (name, price, amount, date) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            
            // 3. Set parameters
            statement.setString(1, name);
            statement.setInt(2, Integer.parseInt(priceValue));
            statement.setInt(3, Integer.parseInt(amountValue));
            statement.setString(4, currentDate.toString());
            
            // 4. Execute update
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Success", "Data inserted successfully!");
                System.out.println("A new note was inserted successfully!");
            }
            
            // 5. Clean up
            statement.close();
            connection.close();
            
            // 6. Clear fields after successful insertion
            fName.clear();
            price.clear();
            amount.clear();
            
        } catch (SQLException e) {
            showAlert("Database Error", "Error inserting data: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for price and amount");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}