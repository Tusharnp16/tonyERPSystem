import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillItemRepository {

    private static final String INSERT_SQL = """
        INSERT INTO bill_items 
        (bill_id, batch_id, quantity, selling_price, mrp, line_total) 
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM bill_items WHERE bill_item_id = ?";

    private static final String FIND_BY_BILL_SQL =
            "SELECT * FROM bill_items WHERE bill_id = ?";

    private static final String DISPLAY_SQL =
            "SELECT * FROM bill_items";


    public static void save(int billId, Bill.PurchaseItem item) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, billId);
            pstmt.setInt(2, item.getBatchId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getSellingPrice().getPrice());
            pstmt.setDouble(5, item.getMrp().getPrice());
            pstmt.setDouble(6, item.getNetAmount().getPrice());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                item.setPurchaseId(rs.getInt(1));
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error saving bill item", ex);
        }
    }

    public static Bill.PurchaseItem findById(int id) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(FIND_BY_ID_SQL)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) return null;

            return mapRowToPurchaseItem(rs);

        } catch (Exception ex) {
            throw new RuntimeException("Error fetching bill item", ex);
        }
    }


    public static List<Bill.PurchaseItem> getItemsForBill(int billId) {

        List<Bill.PurchaseItem> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(FIND_BY_BILL_SQL)) {

            pstmt.setInt(1, billId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapRowToPurchaseItem(rs));
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error fetching items for bill id " + billId, ex);
        }

        return list;
    }
    public static void display() {

        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(DISPLAY_SQL)) {

            while (rs.next()) {
                Bill.PurchaseItem item = mapRowToPurchaseItem(rs);
                System.out.println(item);
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error displaying bill items", ex);
        }
    }

    private static Bill.PurchaseItem mapRowToPurchaseItem(ResultSet rs) throws Exception {

        int batchId = rs.getInt("batch_id");

        StockMaster stockMaster = StockRepository.findById(batchId);

        Bill.PurchaseItem item = new Bill.PurchaseItem(stockMaster,rs.getInt("quantity"));

        item.setPurchaseId(rs.getInt("bill_item_id"));
        item.setBatchId(rs.getInt("batch_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setSellingPrice(new Money(rs.getDouble("selling_price")));
        item.setMrp(new Money(rs.getDouble("mrp")));
        item.setNetAmount(new Money(rs.getDouble("line_total")));

        return item;
    }
}
