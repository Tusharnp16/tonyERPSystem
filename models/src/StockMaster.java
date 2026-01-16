import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class StockMaster {

    private final Variant variant;
    private int batchId;
    private static int autoGenrateId=100;

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    private int quantity;
    private Money mrp;
    private Money sellingPrice;
    private LocalDate expiryDate;
    private Supplier supplier;

    public StockMaster(int quantity, LocalDate expiryDate,Variant variant,Money mrp,Money sellingPrice) {

        if(quantity<=0){
            throw new IllegalArgumentException("negative stock initialization 0");
        }

//        if (variant == null) {
//            throw new IllegalArgumentException("Should have variant");
//        }

          this.variant = variant;
//        this.variantId=variantId;
//        this.producrId=productId;
        this.batchId = autoGenrateId++;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.mrp=mrp;
        this.sellingPrice=sellingPrice;

        variant.addStock(this);
    }

    public int getQuantity() {
        return quantity;
    }

    public int getStockId() {
        return batchId;
    }

    public Variant getVariant() {
        return variant;
    }

    public Money getSellingPrice(){
        return sellingPrice;
    }

    public Money getMrp(){
        return mrp;
    }

    public void reduceQuantity(int q){
        this.quantity-=q;
    }

    public LocalDate getExpireDate(){
        return expiryDate;
    }
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
        supplier.addSuppliedBatch(this);
    }

    @Override
    public String toString() {
        return variant + "\n" +
                "  Batch ID       : " + batchId + "\n" +
                "  Expiry Date    : " + expiryDate + "\n" +
                "  Quantity       : " + quantity + "\n" +
                "  Selling Price  : " + sellingPrice + "\n" +
                "  MRP            : " + mrp + "\n";
    }

}


