import java.util.*;

class Product implements AutoCloseable {

    private final int productId;
    private String productName;
    private static int autoGenrateId=100;
    private static int autoItemGenrateId=1001;
    private String itemCode;
    private Date createdAt;
    private Date modifiedAt;
    private List<Variant> variants=new ArrayList<>();

    Product(String name) {
        this.productId= autoGenrateId++;
        this.itemCode="IT" + autoItemGenrateId++;
        this.productName = name;
        this.createdAt=new Date();
        this.modifiedAt=new Date();
    }

    public int getProductId(){
        return productId;
    }

    public String getProductName(){
        return productName;
    }

    public void addVariant(Variant variant){
        variants.add(variant);
    }

    public List<Variant> getVariant(){
        return Collections.unmodifiableList(variants);
    }

    @Override
    public String toString() {
        return "\n  Product ID   : " + productId + "\n" +
                "  Name          : " + productName + "\n" +
                "  Created At    : " + createdAt + "\n" +
                "  Item Code     : " + itemCode + "\n" ;
    }

    @Override
    public void close() throws Exception {
        throw  new Exception("Product does not Exists");
    }
}


