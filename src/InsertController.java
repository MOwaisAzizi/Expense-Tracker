import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertController {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost:3306/expense_tracker";
        String user = "root";
        String dbPassword = "";

        String sql = "INSERT INTO note (name, price, amount, date) VALUES (?, ?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcURL, user, dbPassword);

            PreparedStatement statement = connection.prepareStatement(sql);

            // Set parameters correctly
            statement.setString(1, "John");
            statement.setInt(2, 100); // price
            statement.setInt(3, 2);   // amount

            // Current date
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            statement.setDate(4, currentDate); // date

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("A new note was inserted successfully!");
            }

            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
