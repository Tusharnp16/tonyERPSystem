import java.time.LocalDate;
import java.util.*;
import java.sql.*;

public class StoreInventory {

    private final Map<Integer, List<StockMaster>> inventory = new HashMap<>();

    public StoreInventory() {
        loadInventoryFromDB();
    }

    private void loadInventoryFromDB() {
        String sql = "SELECT s.batch_id,\n" +
                "       s.variant_id,\n" +
                "       v.color,\n" +
                "       v.size,\n" +
                "       s.quantity,\n" +
                "       s.expire_date,\n" +
                "       s.mrp,\n" +
                "       s.selling_price\n" +
                "FROM stock_master s\n" +
                "JOIN variants v ON s.variant_id = v.variant_id\n" +
                "WHERE s.quantity > 0;\n";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int batchId = rs.getInt("batch_id");
                int quantity = rs.getInt("quantity");
                LocalDate expiry = rs.getDate("expire_date").toLocalDate();
                Money mrp = new Money(rs.getDouble("mrp"));
                Money sellingPrice = new Money(rs.getDouble("selling_price"));

                Variant variant = new Variant(rs.getInt("variant_id"));
                variant.setColour(rs.getString("color"));
                variant.setSize(rs.getString("size"));

                StockMaster batch = new StockMaster(quantity, expiry, variant, mrp, sellingPrice);
                batch.setBatchId(batchId);

                addStock(batch);
            }

        } catch (Exception e) {
            System.out.println("Error loading inventory: " + e);
        }
    }

    public void addStock(StockMaster batch) {
        int variantId = batch.getVariant().getVariantId();
        inventory.computeIfAbsent(variantId, k -> new ArrayList<>()).add(batch);
    }

    public boolean deductStockFEFO(int variantId, int askingQty) {
        List<StockMaster> batches = inventory.get(variantId);
        if (batches == null || batches.isEmpty()) {
            System.out.println("Either variant is sold out or no such product in inventory");
            return false;
        }

        batches.sort(Comparator.comparing(StockMaster::getExpireDate));

        int remaining = askingQty;

        for (StockMaster batch : batches) {
            if (remaining <= 0) break;

            int available = batch.getQuantity();
            if (available <= 0) continue;

            int deduct = Math.min(available, remaining);
            int newQty = available - deduct;

            batch.setQuantity(newQty);
            updateDBQuantity(batch.getBatchId(), newQty);

            remaining -= deduct;
        }

        if (remaining > 0) {
            throw new InventoryException("StockShortageException: Not enough stocks");
        }

        return true;
    }

    private void updateDBQuantity(int batchId, int qty) {
        String sql = "UPDATE stock_master SET quantity = ? WHERE batch_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, qty);
            pstmt.setInt(2, batchId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error updating batch qty: " + e);
        }
    }

    public StockMaster getFirstBatch(int variantId) {
        List<StockMaster> batches = inventory.get(variantId);
        if (batches == null || batches.isEmpty()) return null;
        batches.sort(Comparator.comparing(StockMaster::getExpireDate));
        return batches.get(0);
    }

    public void printInventory() {
        for (Map.Entry<Integer, List<StockMaster>> entry : inventory.entrySet()) {
            for (StockMaster batch : entry.getValue()) {
                System.out.println("  " + batch);
            }
            System.out.println("-----");
        }
    }
}
