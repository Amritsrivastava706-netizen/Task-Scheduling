import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connection {
    public static Connection getConnection(){
        Connection co = null;
        try{
            String url = "jdbc:postgresql://localhost:5432/promanage_db";
            String user = "postgres";
            String password = "Greed";

            co = DriverManager.getConnection(url, user, password);

            System.out.println("PostgreSQL Connection!");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return co;
    }
}
