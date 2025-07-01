import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class MainController {

    @FXML private TextField fName;
    @FXML private TextField price;
    @FXML private TextField amount;

    @FXML private TableView<Note> tableView;
    @FXML private TableColumn<Note, String> colName;
    @FXML private TableColumn<Note, Integer> colPrice;
    @FXML private TableColumn<Note, Integer> colAmount;
    @FXML private TableColumn<Note, String> colDate;
    @FXML private TableColumn<Note, Integer> colId;
    @FXML private TableColumn<Note, Void> colOpr;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/expense_tracker";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private final ObservableList<Note> notes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colOpr.setCellFactory(column -> new TableCell<>() {
            private final Button btnUpdate = new Button("Update");
            private final Button btnDelete = new Button("Delete");
            private final HBox hbox = new HBox(10, btnUpdate, btnDelete);

            {
                btnUpdate.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                btnDelete.setOnAction(event -> {
                    Note note = getTableView().getItems().get(getIndex());
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Delete Confirmation");
                    confirmAlert.setHeaderText(null);
                    confirmAlert.setContentText("Are you sure you want to delete this note?");
                    confirmAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            deleteNoteFromDatabase(note.getId());
                            notes.remove(note);
                        }
                    });
                });

                btnUpdate.setOnAction(event -> {
                    Note note = getTableView().getItems().get(getIndex());
                    showAlert("Update", "Update clicked for: " + note.getName());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });

        tableView.setItems(notes);
        loadNotesFromDatabase();
    }

    public void buttonClick(ActionEvent event) {
        String name = fName.getText();
        String priceValue = price.getText();
        String amountValue = amount.getText();
        String date = LocalDate.now().toString();

        if (name.isEmpty() || priceValue.isEmpty() || amountValue.isEmpty()) {
            showAlert("Input Error", "All fields must be filled!");
            return;
        }

        Connection connection = null;

        try {
            int priceInt = Integer.parseInt(priceValue);
            int amountInt = Integer.parseInt(amountValue);

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "INSERT INTO note (name, price, amount, date) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.setInt(2, priceInt);
            statement.setInt(3, amountInt);
            statement.setString(4, date);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                showAlert("Database Error", "Failed to add note.");
                return;
            }

            int generatedId = -1;
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedId = generatedKeys.getInt(1);
            }
            statement.close();

            notes.add(new Note(generatedId, name, priceInt, amountInt, date));

            fName.clear();
            price.clear();
            amount.clear();

            showAlert("Success", "Note added successfully!");

        } catch (ClassNotFoundException e) {
            showAlert("Driver Error", "MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            showAlert("Database Error", "Could not insert data: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Price and amount must be numbers.");
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadNotesFromDatabase() {
        notes.clear();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM note");

            while (rs.next()) {
                notes.add(new Note(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("price"),
                    rs.getInt("amount"),
                    rs.getString("date")
                ));
            }

            rs.close();
            stmt.close();
            connection.close();

        } catch (Exception e) {
            showAlert("Load Error", "Could not load notes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteNoteFromDatabase(int id) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "DELETE FROM note WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            statement.close();
            connection.close();

            if (rowsDeleted == 0) {
                showAlert("Delete Error", "Note not found or could not be deleted.");
            } else {
                showAlert("Deleted", "Note deleted successfully.");
            }
        } catch (Exception e) {
            showAlert("Delete Error", "Error deleting note: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
