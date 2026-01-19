import java.util.*;

public class Variant {

    private int variantId;
    private String colour;
    private String size;
    private Product product;
    private List<StockMaster> batches =new ArrayList<>();

    public void setSize(String size) {
        this.size = size;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setBatches(List<StockMaster> batches) {
        this.batches = batches;
    }

    public Variant(String colour, String size, Product product) {
        if(product==null){
           throw new IllegalArgumentException("Variant should have aligned product");
        }
        this.colour = colour;
        this.size = size;
        this.product = product;
//        this.mrp=price;
        product.addVariant(this);
    }

    public Variant(int variantId) {
        this.variantId = variantId;
    }


    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public String getColour() {
        return colour;
    }

    public String getSize() {
        return size;
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
        return  product==null ? " " : product + "\n" +
                "  Variant Id  : " + variantId + "\n" +
                "  Colour      : " + colour + "\n" +
                "  Size        : " + size + "\n";
    }

}
