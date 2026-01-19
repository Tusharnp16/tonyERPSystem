import java.time.LocalDate;
import java.util.*;
import java.sql.*;

public class StoreInventory {

    private Map<Integer, List<StockMaster>> inventory = new HashMap<>();

    public StoreInventory() {
        loadInventoryFromDB();
    }

    private void loadInventoryFromDB() {

        String sql = "SELECT batch_id, variant_id, quantity, expire_date, mrp, selling_price " +
                "FROM stock_master WHERE quantity > 0";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                int batchId = rs.getInt("batch_id");
                int quantity = rs.getInt("quantity");
                LocalDate expiry = rs.getDate("expire_date").toLocalDate();
                Money mrp = new Money(rs.getDouble("mrp"));
                Money sellingPrice = new Money(rs.getDouble("selling_price"));

                Variant v = new Variant(rs.getInt("variant_id"));
                v.setVariantId(rs.getInt("variant_id"));

                // CREATE StockMaster using the REAL constructor
                StockMaster sm = new StockMaster(
                        quantity,
                        expiry,
                        v,
                        mrp,
                        sellingPrice
                );

                // set batch id
                sm.setBatchId(batchId);

                // Load into in-memory Map
                addStock(sm);
            }

        } catch (Exception e) {
            System.out.println("Error loading inventory: " + e);
        }
    }



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

                updateDBDeleteBatch(batch.getBatchId(),0);  // DB DELETE
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

    private void updateDBDeleteBatch(int batchId,int qty) {
        String sql = "UPDATE stock_master SET quantity = ? WHERE batch_id = ?\n";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, qty);
            pstmt.setInt(2, batchId);
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

