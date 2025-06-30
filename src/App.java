import java.sql.*;

public class App {
    public static void main(String[] args) {
        try {
            
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Open a connection
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/expense-tracker", "root", "");

            // SQL query to fetch all notes
            String query = "SELECT * FROM notes";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Display notes
            System.out.println("All Notes:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("name"); // or "title" if that's the correct column
                String description = rs.getString("description");
                double amount = rs.getDouble("amount"); // adjust as per your table schema

                System.out.println("| " + id + " || " + title + " || " + description + " || " + amount + " |");
            }

            // Clean-up
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
