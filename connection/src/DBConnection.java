import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/tony_erp";
    private static final String USER = "postgres";
    private static final String PASS = "Tushar";

    private DBConnection() { }

    public static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
