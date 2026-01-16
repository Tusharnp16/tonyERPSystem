import java.util.*;

public class StoreInventory {

    private Map<Integer, List<StockMaster>> inventory=new HashMap<>();

    public void addStock(StockMaster batches){
        int variantId = batches.getVariant().getVariantId();

        inventory.putIfAbsent(variantId,new ArrayList<>());
        inventory.get(variantId).add(batches);

        System.out.println("Stock Added to Inventory with variant with : " + variantId);
    }

    public boolean deductStockFEFO(int variantId,int askingqty){

        if(!inventory.containsKey(variantId)){
            System.out.println("Either variant is sold out or no such product in inventory");
            return false;
        }

        List<StockMaster> batches=inventory.get(variantId);

        batches.sort(Comparator.comparing(StockMaster::getExpireDate));

        int remaining=askingqty;

        Iterator<StockMaster> it = batches.iterator();

        while (it.hasNext() && remaining>0){
            StockMaster batch=it.next();

            int avilable=batch.getQuantity();

            if(avilable<=remaining){
                remaining-=avilable;
                it.remove();
            }else{
                batch.reduceQuantity(remaining);
                remaining=0;
            }
        }

        if(remaining>0){
            throw new InventoryException("StockShortageException : " +
                    "Not enough stocks");
        }

        return true;

    }

    public boolean sellItem(int variantId,int qty){
            return deductStockFEFO(variantId,qty);
    }

    public StockMaster getFirstBatch(int variantId) {
        if (!inventory.containsKey(variantId)) return null;

        List<StockMaster> batches = inventory.get(variantId);
        if (batches.isEmpty()) return null;

        batches.sort(Comparator.comparing(StockMaster::getExpireDate));
        return batches.get(0);
    }


    public void printInventory(){
        System.out.println("Inventory ");
        for(Map.Entry<Integer,List<StockMaster>> entry : inventory.entrySet()){
            System.out.println("Variant Id : " + entry.getKey());
           for(StockMaster batch : entry.getValue()){
               System.out.println(" " + batch);
           }
            System.out.println("-----");
        }
    }
}
