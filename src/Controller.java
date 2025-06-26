import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {
    @FXML
    private TextField fName;

    private Stage mainWindow;

    public void setMainWindow(Stage mainWindow) { // must be public
        this.mainWindow = mainWindow;
    }

    @FXML
    private void onCallback(ActionEvent event) {
        String title = fName.getText();
        mainWindow.setTitle(title);
    }
}
