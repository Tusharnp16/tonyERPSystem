import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class StockMaster {

    private final Variant variant;
//    private int variantId;
//    private int producrId;
    private final int batchId;
    private static int autoGenrateId=100;
    private final int quantity;
    private Money mrp;
    private Money sellingPrice;
    private LocalDate expiryDate;
    private Supplier supplier;

    public StockMaster(int quantity, LocalDate expiryDate,Variant variant,Money mrp,Money sellingPrice) {

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

    public Variant getVariant() {
        return variant;
    }

    public Money getSellingPrice(){
        return sellingPrice;
    }

    public Money getMrp(){
        return mrp;
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
        return " BatchId : " + batchId +
                ", ExpiryDate : " + expiryDate +
                ", Quantity : " + quantity +
                ", Selling Price : " + sellingPrice +
                ", MRP : " + mrp +
                "\n Variant : " + variant;
        //    ", Supplier : " + supplier;
    }
}


