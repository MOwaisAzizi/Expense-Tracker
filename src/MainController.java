import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MainController {

@FXML
private TextField fName;

@FXML
private TextField price;

@FXML
private TextField amount;

public void buttonClick(ActionEvent event) { 
    LocalDate currentDate = LocalDate.now();
System.out.println("Current date: " + currentDate);

    String name = fName.getText();
    String priceValue = price.getText();
    String amountValue = amount.getText();
System.out.println("Name:"+name);   
System.out.println("price:"+priceValue);   
System.out.println("amount:"+amountValue);   
 }
}
