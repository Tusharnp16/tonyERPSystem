import java.sql.*;

public class VariantRepository {
    public static void save(Variant variant) throws Exception {

        String sql="INSERT INTO variants(product_id,color,size) values(?,?,?) RETURNING variant_id";

        try(Connection con=DBConnection.getConnection()){
            PreparedStatement pstmt=con.prepareStatement(sql);
            pstmt.setInt(1,variant.getProduct().getProductId());
            pstmt.setString(2,variant.getSize());
            pstmt.setString(3,variant.getColour());

            ResultSet rs= pstmt.executeQuery();
            if(rs.next()){
                int id=rs.getInt("variant_id");
                variant.setVariantId(id);
            }

        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static Variant findById(int id) {
        String sql = "SELECT * FROM variants WHERE variant_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {

                String color = rs.getString("color");
                String size = rs.getString("size");

                int prdId=rs.getInt("product_id");
                Product product=ProductRepository.findById(prdId);

                Variant v = new Variant(color, size, product);
                v.setVariantId(rs.getInt("variant_id"));

                return v;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void display(){
        String sql="SELECT v.variant_id,p.name as product_name,v.size,v.color FROM variants v LEFT JOIN products p on v.product_id=p.product_id";

        try(Connection con=DBConnection.getConnection()){
            Statement stmt=con.createStatement();
            ResultSet rs= stmt.executeQuery(sql);

            System.out.printf("%-10s %-15s %-12s %-10s%n","VariantId", "ProductName", "Size", "Colour");

            while (rs.next()){
                System.out.printf("%-10d %-15s %-12s %-10s%n",
                                    rs.getInt("variant_id"),
                                    rs.getString("product_name"),
                                    rs.getString("size"),
                                    rs.getString("color")
                );
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}