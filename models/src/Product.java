import java.util.*;

class Product implements AutoCloseable {

    private int productId;
    private String productName;
  //  private static int autoGenrateId=100;
    private static int autoItemGenrateId=1001;
    private String itemCode;

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public static void setAutoGenrateId(int autoGenrateId) {
    //    Product.autoGenrateId = autoGenrateId;
    }

    public static void setAutoItemGenrateId(int autoItemGenrateId) {
        Product.autoItemGenrateId = autoItemGenrateId;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }

    private Date createdAt;
    private Date modifiedAt;
    private List<Variant> variants=new ArrayList<>();

    Product(String name) {
      //  this.productId= autoGenrateId++;
        this.itemCode="IT" + autoItemGenrateId++;
        this.productName = name;
        this.createdAt=new Date();
        this.modifiedAt=new Date();
    }

    public int getProductId(){
        return productId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public String getItemCode(){
        return itemCode;
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


