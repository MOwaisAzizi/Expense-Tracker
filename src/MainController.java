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
    private Note selectedNote = null;

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
    selectedNote = getTableView().getItems().get(getIndex());

    fName.setText(selectedNote.getName());
    price.setText(String.valueOf(selectedNote.getPrice()));
    amount.setText(String.valueOf(selectedNote.getAmount()));
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

public void addOrUpdateData(ActionEvent event) {
    System.out.println("clickkkkkkkkkkkkkkkkk");
    String name = fName.getText();
    String priceValue = price.getText();
    String amountValue = amount.getText();
    String date = LocalDate.now().toString();

    if (name.isEmpty() || priceValue.isEmpty() || amountValue.isEmpty()) {
        showAlert("Input Error", "All fields must be filled!");
        return;
    }

    try {
        int priceInt = Integer.parseInt(priceValue);
        int amountInt = Integer.parseInt(amountValue);

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        if (selectedNote == null) {
            // INSERT
            String sql = "INSERT INTO note (name, price, amount, date) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.setInt(2, priceInt);
            stmt.setInt(3, amountInt);
            stmt.setString(4, date);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    notes.add(new Note(newId, name, priceInt, amountInt, date));
                }
                showAlert("Success", "Note added successfully!");
            } else {
                showAlert("Error", "Insert failed.");
            }

            stmt.close();
        } else {
            // UPDATE
            String sql = "UPDATE note SET name = ?, price = ?, amount = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setInt(2, priceInt);
            stmt.setInt(3, amountInt);
            stmt.setInt(4, selectedNote.getId());

            int rows = stmt.executeUpdate();
            stmt.close();

            if (rows > 0) {
                selectedNote.setName(name);
                selectedNote.setPrice(priceInt);
                selectedNote.setAmount(amountInt);
                tableView.refresh(); // reflect changes in UI
                showAlert("Updated", "Note updated successfully!");
            } else {
                showAlert("Error", "Update failed.");
            }

            // Clear selected note after update
            selectedNote = null;
        }

        connection.close();
        fName.clear();
        price.clear();
        amount.clear();

    } catch (ClassNotFoundException e) {
        showAlert("Driver Error", "JDBC driver not found.");
    } catch (SQLException e) {
        showAlert("Database Error", "Error: " + e.getMessage());
    } catch (NumberFormatException e) {
        showAlert("Input Error", "Price and amount must be numbers.");
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
