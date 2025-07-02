import java.sql.Connection;
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
import utils.Helper;

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
    
    String name = fName.getText().trim();
    String priceValue = price.getText().trim();
    String amountValue = amount.getText().trim();
    String date = LocalDate.now().toString();

// 3. Validate name
if (!name.matches("[a-zA-Z0-9\\s]{2,30}")) {
    Helper.showAlert("Input Error", "Name must be 2–30 characters (letters, numbers, spaces only).");
    return;
}

if (name.isEmpty() || priceValue.isEmpty() || amountValue.isEmpty()) {
        Helper.showAlert("Input Error", "All fields must be filled!");
        return;
    }

try {
    int priceInt = Integer.parseInt(priceValue);
    int amountInt = Integer.parseInt(amountValue);
    System.out.println(priceInt);
    System.out.println(amountInt);

        if (priceInt <= 0 || amountInt <= 0) {
        Helper.showAlert("Input Error", "Price and Amount must be positive numbers.");
        return;
    }


        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = Helper.getConnection();

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
                Helper.showAlert("Success", "Note added successfully!");
            } else {
                Helper.showAlert("Error", "Insert failed.");
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
                tableView.refresh(); 
                Helper.showAlert("Updated", "Note updated successfully!");
            } else {
                Helper.showAlert("Error", "Update failed.");
            }

            selectedNote = null;
        }

        connection.close();
        fName.clear();
        price.clear();
        amount.clear();

    } catch (ClassNotFoundException e) {
        Helper.showAlert("Driver Error", "JDBC driver not found.");
    } catch (SQLException e) {
        Helper.showAlert("Database Error", "Error: " + e.getMessage());
    } catch (NumberFormatException e) {
        Helper.showAlert("Input Error", "Price and amount must be numbers.");
    }
}

    private void loadNotesFromDatabase() {
        notes.clear();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = Helper.getConnection();

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
            Helper.showAlert("Load Error", "Could not load notes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteNoteFromDatabase(int id) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = Helper.getConnection();

            String query = "DELETE FROM note WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            statement.close();
            connection.close();

            if (rowsDeleted == 0) {
                Helper.showAlert("Delete Error", "Note not found or could not be deleted.");
            } else {
                Helper.showAlert("Deleted", "Note deleted successfully.");
            }
        } catch (Exception e) {
            Helper.showAlert("Delete Error", "Error deleting note: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
