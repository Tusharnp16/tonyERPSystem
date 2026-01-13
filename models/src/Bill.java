import com.sun.source.doctree.AuthorTree;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Bill {

    private int billId;
    private static int autoGenrateId = 1;

    private Money total;

    private Date createdAt;
    private Date modidfiedDate;

    private String supplierName;
    private String supplieContact;
    private Money taxAmount=new Money(0.0);
    private Money finalAmout=new Money(0.0);

    TaxStrategy strategy;

    private List<PurchaseItem> purchaseItemList = new ArrayList<>();


    public Bill(Supplier supplier,TaxStrategy strategy) {
        this.billId = autoGenrateId++;
        this.createdAt = new Date();
        this.modidfiedDate = new Date();
        this.total = new Money(0.0);
        this.supplierName=supplier.getContact();
        this.supplieContact=supplier.getContactNumber();
        this.strategy=strategy;

    }

    public void addItem(StockMaster stockMaster) {
        PurchaseItem purchaseItem = new PurchaseItem(stockMaster);
        purchaseItemList.add(purchaseItem);
        this.total = this.total.add(purchaseItem.netAmount);

        TaxCalulator calulator=new TaxCalulator(strategy);
        this.taxAmount=calulator.applyTax(total);
        this.finalAmout=this.total.add(taxAmount);

        this.modidfiedDate = new Date();
    }

    public Money getTotalamount() {
        return total;
    }

//    @Override
//    public String toString() {
//        return "\n" + " Bill Id = " + billId +
//                "\n Supplier = " + supplier +
//                "\n Stock = " + stockBatch +
//                //    "\n Variant = " + variant +
//                "\n Quantity = " + stockBatch.getQuantity() +
//                "\n Total = " + total;

    public String getSupplieContact() {
        return supplieContact;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public int getBillId() {
        return billId;
    }

    /// /                "\n GST = " + taxAmount +
    /// /                "\n Total Bill = " + finalAmout;
//
//    }



    public String display(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n================ BILL ================\n");
        sb.append("Bill ID        : ").append(billId).append("\n");
        sb.append("Supplier Name : ").append(supplierName).append("\n");
        sb.append("Supplier Contact No. : ").append(supplieContact).append("\n");
        sb.append("Created At     : ").append(createdAt).append("\n");
        sb.append("Modified At    : ").append(modidfiedDate).append("\n");
        sb.append("--------------------------------------\n");
        sb.append("Items:\n");

        for (PurchaseItem item : purchaseItemList) {
            sb.append(item).append("\n");
        }

        sb.append("--------------------------------------\n");
        sb.append("Total Amount   : ").append(total).append("\n");
        sb.append("Tax Amount (").append(strategy.getGST()).append(") : ").append(taxAmount).append("\n");
        sb.append("Net Amount   : ").append(finalAmout).append("\n");

        sb.append("======================================\n");

        return sb.toString();

    }



    public class PurchaseItem {

        private int purchaseId;
        private static int autoGenrateId = 100;
        private int quantity;
        private Money sellingPrice;
        private Money mrp;
        private Product product;
        private Variant variant;
        private LocalDate expireDate;
        private Money netAmount;

        public PurchaseItem(StockMaster stockMaster) {
            this.purchaseId = autoGenrateId++;
            this.quantity = stockMaster.getQuantity();
            this.sellingPrice = stockMaster.getSellingPrice();
            this.product = stockMaster.getVariant().getProduct();
            this.expireDate = stockMaster.getExpireDate();
            this.variant = stockMaster.getVariant();
            this.mrp=stockMaster.getMrp();
            this.netAmount = sellingPrice.mutiply(quantity);
        }

        @Override
        public String toString() {
            return  "Item:\n" +
                    "  Purchase ID : " + purchaseId + "\n" +
                    "  Qty         : " + quantity + "\n" +
                    "  Price       : " + sellingPrice+ "\n" +
                    "  MRP         : " + mrp  + "\n" +
                    "  Net Amount  : " + netAmount + "\n" +
                    "  Product Id  : " + product.getProductId() + "\n" +
                    "  Variant     : " + product.getVariant() + "\n" +
                    "  Expiry      : " + expireDate + "\n";
        }


    }
}
