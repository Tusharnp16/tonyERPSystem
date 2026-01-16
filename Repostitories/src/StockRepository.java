import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class StockRepository {

    public static void save(StockMaster master) throws Exception {

        String sql="INSERT INTO stock_master(variant_id,quantity,expire_date,mrp,selling_price) values(?,?,?,?,?) RETURNING batch_id";

        try(Connection con=DBConnection.getConnection()){
            PreparedStatement pstmt=con.prepareStatement(sql);
            pstmt.setInt(1,master.getVariant().getVariantId());
            pstmt.setInt(2,master.getQuantity());
            pstmt.setDate(3, java.sql.Date.valueOf(master.getExpireDate()));
            pstmt.setDouble(4, master.getMrp().getPrice());
            pstmt.setDouble(5, master.getSellingPrice().getPrice());

            ResultSet rs= pstmt.executeQuery();
            if(rs.next()){
                int id=rs.getInt("batch_id");
                master.setBatchId(id);
            }

        }catch (Exception e){
            System.out.println(e);
        }
    }

//    public static Product findById(int id) {
//        String sql = "SELECT * FROM products WHERE product_id = ?";
//        try (Connection con = DBConnection.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//
//            ps.setInt(1, id);
//            ResultSet rs = ps.executeQuery();
//
//            if (rs.next()) {
//                Product p = new Product(
//                        rs.getString("name")
//                );
//                p.setProductId(rs.getInt("product_id"));
//                p.setItemCode(rs.getString("item_code"));
//                p.setCreatedAt(rs.getTimestamp("created_at"));
//                return p;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static void display(){
//        String sql="SELECT * FROM products";
//
//        try(Connection con=DBConnection.getConnection()){
//            Statement stmt=con.createStatement();
//            ResultSet rs= stmt.executeQuery(sql);
//
//            while (rs.next()){
//                System.out.println(rs.getInt("product_id") + " | " + rs.getString("name") + " | "
//                        + rs.getString("item_code") + " | " + rs.getString("created_at")
//                        + " | " + rs.getString("modified_at"));
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//    }

}
