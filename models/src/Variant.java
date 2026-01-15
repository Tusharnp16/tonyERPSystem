import java.util.*;

public class Variant {

    private final int variantId;
    private static int autoGenrateId=1001;
    private final String colour;
    private final String size;
    private final Product product;
    private List<StockMaster> batches =new ArrayList<>();

    public Variant(String colour, String size, Product product) {
        if(product==null){
           throw new IllegalArgumentException("Variant should have aligned product");
        }
        this.variantId = autoGenrateId++;
        this.colour = colour;
        this.size = size;
        this.product = product;
//        this.mrp=price;

        product.addVariant(this);
    }

    public int getVariantId(){
        return variantId;
    }

    public void addStock(StockMaster stockBatch){
        batches.add(stockBatch);
    }

    public List<StockMaster> getStock(){
        return Collections.unmodifiableList(batches);
    }

    public Product getProduct(){
        return product;
    }

    @Override
    public String toString() {
        return  product + "\n" +
                "  Variant Id  : " + variantId + "\n" +
                "  Colour      : " + colour + "\n" +
                "  Size        : " + size + "\n";
    }

}
