import java.util.*;
import java.sql.*;

public class StoreInventory {

    private Map<Integer, List<StockMaster>> inventory = new HashMap<>();


    // *************** 1. LOAD INVENTORY FROM DATABASE ***************
    public StoreInventory() {
        loadInventoryFromDB();
    }

    private void loadInventoryFromDB() {
        String sql = "SELECT * FROM stock_master WHERE quantity > 0";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                StockMaster sm = new StockMaster(
                        rs.getInt("quantity"),
                        rs.getInt("varinant_id"),
                        rs.getDate("expire_date").toLocalDate(),
                        (new Money(rs.getDouble("mrp"),
                        new Money(rs.getDouble("selling_price")


                );
                sm.setBatchId(rs.getInt("batch_id"));

                Variant v = new Variant();
                v.setVariantId(rs.getInt("variant_id"));
                sm.setVariant(v);

                addStock(sm);  // load into memory map
            }

        } catch (Exception e) {
            System.out.println("Error loading inventory: " + e);
        }
    }


    // *************** 2. ADD STOCK ***************
    public void addStock(StockMaster batch) {
        int variantId = batch.getVariant().getVariantId();

        inventory.putIfAbsent(variantId, new ArrayList<>());
        inventory.get(variantId).add(batch);

        System.out.println("Stock Added to Inventory with variant : " + variantId);
    }


    // *************** 3. FEFO DEDUCT (Updates DB also) ***************
    public boolean deductStockFEFO(int variantId, int askingQty) {

        if (!inventory.containsKey(variantId)) {
            System.out.println("Either variant is sold out or no such product in inventory");
            return false;
        }

        List<StockMaster> batches = inventory.get(variantId);

        batches.sort(Comparator.comparing(StockMaster::getExpireDate));

        int remaining = askingQty;

        Iterator<StockMaster> it = batches.iterator();

        while (it.hasNext() && remaining > 0) {

            StockMaster batch = it.next();
            int available = batch.getQuantity();

            if (available <= remaining) {
                // consume full batch
                remaining -= available;

                updateDBDeleteBatch(batch.getBatchId());  // DB DELETE
                it.remove(); // memory remove

            } else {
                // partial consumption
                int newQty = available - remaining;
                remaining = 0;

                batch.setQuantity(newQty);

                updateDBQuantity(batch.getBatchId(), newQty); // DB UPDATE
            }
        }

        if (remaining > 0) {
            throw new InventoryException("StockShortageException: Not enough stocks");
        }

        return true;
    }


    // *************** DB UPDATE HELPERS ***************
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

    private void updateDBDeleteBatch(int batchId) {
        String sql = "DELETE FROM stock_master WHERE batch_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, batchId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error deleting batch: " + e);
        }
    }


    // *************** 4. GET FIRST BATCH ***************
    public StockMaster getFirstBatch(int variantId) {
        if (!inventory.containsKey(variantId)) return null;

        List<StockMaster> batches = inventory.get(variantId);
        if (batches.isEmpty()) return null;

        batches.sort(Comparator.comparing(StockMaster::getExpireDate));
        return batches.get(0);
    }


    // *************** 5. PRINT INVENTORY ***************
    public void printInventory() {
        System.out.println("Inventory:");
        for (Map.Entry<Integer, List<StockMaster>> entry : inventory.entrySet()) {
            System.out.println("Variant Id : " + entry.getKey());
            for (StockMaster batch : entry.getValue()) {
                System.out.println("  " + batch);
            }
            System.out.println("-----");
        }
    }
}

