import java.sql.*;

public class VariantRepository {
    public static void save(Variant variant) throws Exception {

        String sql="INSERT INTO variants(product_id,color,size)  values(?,?,?)";

        try(Connection con=DBConnection.getConnection()){
            PreparedStatement pstmt=con.prepareStatement(sql);
            pstmt.setInt(1,variant.getProduct().getProductId());
            pstmt.setString(2,variant.getSize());
            pstmt.setString(3,variant.getColour());
            int ans=pstmt.executeUpdate();

            if(ans>0){
                System.out.println("Data Inserted");
            }

        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static Variant findById(int id) {
        String sql = "SELECT * FROM variant WHERE variant_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            String color=rs.getString("color");
            String size=rs.getString("size");

            Variant v=new Variant(color,size,null);

            return v;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void display(){
        String sql="SELECT v.variant_id,p.name as product_name,v.size,v.color FROM variants v JOIN products p on v.variant_id=p.product_id";

        try(Connection con=DBConnection.getConnection()){
            Statement stmt=con.createStatement();
            ResultSet rs= stmt.executeQuery(sql);

            while (rs.next()){
                System.out.println(rs.getInt("variant_id") + " | "
                        + rs.getString("product_name") + " | " +
                        rs.getString("size") + " | "
                        + rs.getString("color"));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}