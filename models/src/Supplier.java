import java.util.ArrayList;
import java.util.List;

public class Supplier {

    private final int supplierId;
    private static int autoGenrateId=1;
    private final String contact;
    private final String contactNumber;
    private final List<StockMaster> batches=new ArrayList<>();

    public Supplier(String name, String contactNumber) {
        this.supplierId = autoGenrateId++;
        this.contact = name;
        this.contactNumber = contactNumber;
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
