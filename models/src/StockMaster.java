import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class StockMaster {

    private final Variant variant;
    private int batchId;


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

        this.variant = variant;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.mrp=mrp;
        this.sellingPrice=sellingPrice;

        variant.addStock(this);
    }

    public int getBatchId() {
        return batchId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setMrp(Money mrp) {
        this.mrp = mrp;
    }

    public void setSellingPrice(Money sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Supplier getSupplier() {
        return supplier;
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


