import javax.imageio.stream.ImageInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class SupplierRepository {


    /*

     CREATE TABLE IF NOT EXISTS suppliers (
                        supplier_id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 10001) PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        contact VARCHAR(20) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                        updated_at TIMESTAMP
     */


    public static void save(Supplier supplier) throws Exception {

        String sql="INSERT INTO suppliers(name,contact,state) values(?,?,?) RETURNING supplier_id";

        try(Connection con=DBConnection.getConnection()){
            PreparedStatement pstmt=con.prepareStatement(sql);
            pstmt.setString(1, supplier.getContact());
            pstmt.setString(2, supplier.getContactNumber());
            pstmt.setString(3,supplier.getState());

            ResultSet rs= pstmt.executeQuery();
            if(rs.next()){
                int id=rs.getInt("supplier_id");
                supplier.setSupplierId(id);
            }

        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static Supplier findById(int id) {
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Supplier s = new Supplier(
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("state")
                );
                s.setSupplierId(rs.getInt("supplier_id"));
                return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void display(){
        String sql="SELECT * FROM suppliers";

        try(Connection con=DBConnection.getConnection()){
            Statement stmt=con.createStatement();
            ResultSet rs= stmt.executeQuery(sql);


            System.out.printf("%-10s %-10s %-12s %-15s%n",
                    "SellerID", "Name", "Contact","State");

            while (rs.next()){
                System.out.printf("%-10d %-10s %-12s %-15s%n",
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("state"));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
