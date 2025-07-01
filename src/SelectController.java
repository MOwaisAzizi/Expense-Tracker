import java.sql.*;

public class SelectController {
    public static void main(String[] args) {
        try {
            
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Open a connection
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/expense_tracker", "root", "");

            String query = "SELECT * FROM note";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Display notes
            System.out.println("All Notes:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("name"); // or "title" if that's the correct column
                String amount = rs.getString("amount");
                int price = rs.getInt("price");
                String date = rs.getString("date");


                System.out.println("| " + id + " || " + title + " || " + price + " || " + amount + " |" + date + " |");
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

