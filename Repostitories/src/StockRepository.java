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
                insertIntoInventory(master, con);
            }

        }catch (Exception e){
            System.out.println(e);
        }
    }

    private static void insertIntoInventory(StockMaster master, Connection con) throws Exception {
        String sql = "INSERT INTO inventory(batch_id, variant_id, expire_date, quantity) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = con.prepareStatement(sql);

        pstmt.setInt(1, master.getBatchId());
        pstmt.setInt(2, master.getVariant().getVariantId());
        pstmt.setDate(3, java.sql.Date.valueOf(master.getExpireDate()));
        pstmt.setInt(4, master.getQuantity());

        pstmt.executeUpdate();
    }


    public static StockMaster findById(int batchId) {

        String sql = "SELECT * FROM stock_master WHERE batch_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, batchId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            int variantId = rs.getInt("variant_id");
            Variant variant = VariantRepository.findById(variantId);


            StockMaster master = new StockMaster(
                    rs.getInt("quantity"),
                    rs.getDate("expire_date").toLocalDate(),
                    variant,
                    new Money(rs.getDouble("mrp")),
                    new Money(rs.getDouble("selling_price"))
            );

            master.setBatchId(rs.getInt("batch_id"));
            return master;

        } catch (Exception e) {
            throw new RuntimeException("Error fetching StockMaster with batch_id " + batchId, e);
        }
    }


    public static void display(){
        String sql="SELECT * FROM stock_master";

        try(Connection con=DBConnection.getConnection()){
            Statement stmt=con.createStatement();
            ResultSet rs= stmt.executeQuery(sql);

            while (rs.next()){
                System.out.println(rs.getInt("batch_id") + " | " +
                        rs.getInt("variant_id") + " | " +
                        rs.getString("expire_date")
                        + " | " + rs.getString("quantity")
                        + " | " + rs.getDouble("mrp")
                        + " | " + rs.getDouble("selling_price"));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
