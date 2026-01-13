import java.util.*;

class Product {

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
        return  "Id : " + productId +
                ",Product Name : " + productName +
                ", Date : " +createdAt +
                ", Item Code : " + itemCode;
    }
}


