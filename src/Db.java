import java.sql.Connection;
import java.sql.Statement; // ✅ Correct import
import utils.Helper;

public class Db {
public static void main(String args[]){
    createTable();
}
    public static void createTable() {
        try {
            Connection conn = Helper.getConnection();
            Statement stmt = conn.createStatement();

            // Drop the table if it exists
            stmt.executeUpdate("DROP TABLE IF EXISTS note");

            // Create a new table
            String createTableSQL = """
                CREATE TABLE note (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    price INT NOT NULL,
                    amount INT NOT NULL,
                    date DATE NOT NULL,
                    description VARCHAR(1000)
                )
            """;
            stmt.executeUpdate(createTableSQL);

            stmt.close();
            conn.close();
            System.out.println("Table dropped and created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
