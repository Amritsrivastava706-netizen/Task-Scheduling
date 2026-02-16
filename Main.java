import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Main {
    public static void main(String[] args) {

        try{
            Connection co = connection.getConnection();
            PreparedStatement ps = co.prepareStatement("insert into projects values(?,?,?,?)");
            ps.setInt(1, 3);
            ps.setString(2,"BMW");
            ps.setInt(3, 6);
            ps.setDouble(4, 100000);
            ps.executeUpdate();
            PreparedStatement ps1 = co.prepareStatement("select * from projects");
            ResultSet val = ps1.executeQuery();
            while(val.next()){
                System.out.println(val.getInt(1));
                System.out.println(val.getString(2));
                System.out.println(val.getInt(3));
                System.out.println(val.getDouble(4));
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}