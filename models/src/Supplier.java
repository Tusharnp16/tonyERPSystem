import java.util.ArrayList;
import java.util.List;

public class Supplier {

    private int supplierId;
    private  String contact;
    private  String contactNumber;
    private final List<StockMaster> batches=new ArrayList<>();
    private String state;
   // private StockMaster master;

    public Supplier(String name, String contactNumber,String state) {
        this.contact = name;
        this.contactNumber = contactNumber;
        this.state=state;
    }


    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

//    public void setMaster(StockMaster master){
//        this.master=master;
//    }
//
//    public StockMaster getMaster(){
//        return master;
//    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getContact() {
        return contact;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void addSuppliedBatch(StockMaster batch){
        batches.add(batch);
    }

    @Override
    public String toString() {
        return ", Supplier ID : " + supplierId +
                ", Supplier Name : " + contact +
                 ", Contact Number : " + contactNumber;
    }
}
