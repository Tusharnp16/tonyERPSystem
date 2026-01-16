import java.sql.*;

public class ProductRepository {
        public static void save(Product product) throws Exception {

            String sql="INSERT INTO products(name,item_code,created_at,modified_at) values(?,?,?,?) RETURNING product_id";

            try(Connection con=DBConnection.getConnection()){
                PreparedStatement pstmt=con.prepareStatement(sql);
                pstmt.setString(1,product.getProductName());
                pstmt.setString(2,product.getItemCode());
                pstmt.setTimestamp(3,new java.sql.Timestamp(product.getCreatedAt().getTime()));
                pstmt.setTimestamp(4,new java.sql.Timestamp(product.getModifiedAt().getTime()));

                ResultSet rs= pstmt.executeQuery();
                if(rs.next()){
                    int id=rs.getInt("product_id");
                    product.setProductId(id);
                }

            }catch (Exception e){
                System.out.println(e);
            }
        }

        public static Product findById(int id) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Product p = new Product(
                        rs.getString("name")
                );
                p.setProductId(rs.getInt("product_id"));
                p.setItemCode(rs.getString("item_code"));
                p.setCreatedAt(rs.getTimestamp("created_at"));
                return p;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

        public static void display(){
            String sql="SELECT * FROM products";

            try(Connection con=DBConnection.getConnection()){
                Statement stmt=con.createStatement();
                ResultSet rs= stmt.executeQuery(sql);

                while (rs.next()){
                    System.out.println(rs.getInt("product_id") + " | " + rs.getString("name") + " | "
                            + rs.getString("item_code") + " | " + rs.getString("created_at")
                            + " | " + rs.getString("modified_at"));
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
}