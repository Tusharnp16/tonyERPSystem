import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class BillRepository {


    /*

     CREATE TABLE IF NOT EXISTS bills (
                      bill_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                      supplier_id BIGINT NOT NULL REFERENCES suppliers(supplier_id) ON DELETE CASCADE,
                      gst_type VARCHAR(10) NOT NULL,
                      total_amount NUMERIC(12,2) DEFAULT 0,
                      tax_amount NUMERIC(12,2) DEFAULT 0,
                      net_amount NUMERIC(12,2) DEFAULT 0,
                      created_at TIMESTAMP NOT NULL DEFAULT NOW()

     */
    public static void save(Bill bill) throws Exception {

        String sql="INSERT INTO bills(supplier_id,gst_type,total_amount,tax_amount,net_amount) values(?,?,?,?,?) RETURNING bill_id";

        try(Connection con=DBConnection.getConnection()){
            PreparedStatement pstmt=con.prepareStatement(sql);
            pstmt.setInt(1, bill.getSupplierId());
            pstmt.setString(2, bill.getGstType());
            pstmt.setDouble(3, bill.getTotal().getPrice());
            pstmt.setDouble(4, bill.getTaxAmount().getPrice());
            pstmt.setDouble(5, bill.getFinalAmout().getPrice());

            ResultSet rs= pstmt.executeQuery();
            if(rs.next()){
                int id=rs.getInt("bill_id");
                bill.setBillId(id);
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void updateTotals(Bill bill) throws Exception {
        String sql = "UPDATE bills SET total_amount = ?, tax_amount = ?, net_amount = ? WHERE bill_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setDouble(1, bill.getTotal().getPrice());
            pstmt.setDouble(2, bill.getTaxAmount().getPrice());
            pstmt.setDouble(3, bill.getFinalAmout().getPrice());
            pstmt.setInt(4, bill.getBillId());

            pstmt.executeUpdate();
        }
    }


    public static Bill findById(int billId) {

        Bill bill = null;

        String sql = "SELECT b.bill_id, b.supplier_id, b.gst_type, b.total_amount, b.tax_amount, " +
                "b.net_amount, b.created_at " +
                "FROM bills b WHERE b.bill_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, billId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                bill = new Bill();
                bill.setBillId(rs.getInt("bill_id"));
                bill.setGstType(rs.getString("gst_type"));
                bill.setTotal(new Money(rs.getDouble("total_amount")));
                bill.setTaxAmount(new Money(rs.getDouble("tax_amount")));
                bill.setFinalAmout(new Money(rs.getDouble("net_amount")));
                String gstType=rs.getString("gst_type");
               // TaxStrategy strategy= ;
                bill.setStrategy(gstType=="IGST" ? new IGSTStrategy() :new CGSTStrategy());
                bill.setCreatedAt(rs.getTimestamp("created_at"));

                int supplierId = rs.getInt("supplier_id");

                Supplier supplier = SupplierRepository.findById(supplierId);
                bill.setSupplierName(supplier.getContact());
                bill.setSupplieContact(supplier.getContactNumber());

                List<Bill.PurchaseItem> items = BillItemRepository.getItemsForBill((int) billId);
                bill.setPurchaseItemList(items);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return bill;
    }

    public static void display() {
        String sql = "SELECT b.bill_id, b.gst_type, b.total_amount, b.tax_amount, b.net_amount, " +
                "s.supplier_id, s.name, s.contact " +
                "FROM bills b " +
                "JOIN suppliers s ON b.supplier_id = s.supplier_id";

        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {


            System.out.printf("%-7s %-15s %-12s %-10s %-10s %-10s %-10s%n",
                    "BillID", "SupplierName", "Contact", "GST", "Total", "Tax", "Net");

            while (rs.next()) {
                System.out.printf("%-7d %-15s %-12s %-10s %-10.2f %-10.2f %-10.2f%n",
                        rs.getInt("bill_id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("gst_type"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("tax_amount"),
                        rs.getDouble("net_amount"));
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
