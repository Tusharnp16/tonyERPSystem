import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Bill {

    private int billId;

    private Money total;

    private Date createdAt;
    private Date modidfiedDate;

    private String supplierName;
    private String supplieContact;
    private Money taxAmount=new Money(0.0);
    private Money finalAmout=new Money(0.0);
    private String gstType;
    private int supplier_id;

    TaxStrategy strategy;

    private List<PurchaseItem> purchaseItemList = new ArrayList<>();

    public Bill() {}

    public Bill(Supplier supplier,TaxStrategy strategy) {
        this.createdAt = new Date();
        this.modidfiedDate = new Date();
        this.total = new Money(0.0);
        this.supplierName=supplier.getContact();
        this.supplieContact=supplier.getContactNumber();
        this.supplier_id=supplier.getSupplierId();
        this.strategy=strategy;
        this.gstType=strategy.getGST();
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

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getSupplierId() {
        return supplier_id;
    }

    public Money getTotal() {
        return total;
    }

    public void setTotal(Money total) {
        this.total = total;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModidfiedDate() {
        return modidfiedDate;
    }

    public void setModidfiedDate(Date modidfiedDate) {
        this.modidfiedDate = modidfiedDate;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setSupplieContact(String supplieContact) {
        this.supplieContact = supplieContact;
    }

    public Money getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Money taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Money getFinalAmout() {
        return finalAmout;
    }

    public void setFinalAmout(Money finalAmout) {
        this.finalAmout = finalAmout;
    }

    public String getGstType() {
        return gstType;
    }

    public void setGstType(String gstType) {
        this.gstType = gstType;
    }

    public TaxStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(TaxStrategy strategy) {
        this.strategy = strategy;
    }

    public List<PurchaseItem> getPurchaseItemList() {
        return purchaseItemList;
    }

    public void setPurchaseItemList(List<PurchaseItem> purchaseItemList) {
        this.purchaseItemList = purchaseItemList;
    }

    public Money getTotalamount() {
        return total;
    }

    public String getSupplieContact() {
        return supplieContact;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public int getBillId() {
        return billId;
    }

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



    public static class PurchaseItem {

        private int purchaseId;
        private int batchId;
        private int quantity;
        private Money sellingPrice;
        private Money mrp;
        private Product product;
        private Variant variant;
        private LocalDate expireDate;
        private Money netAmount;

        public PurchaseItem(StockMaster stockMaster) {
            this.batchId = stockMaster.getStockId();
            this.quantity = stockMaster.getQuantity();
            this.sellingPrice = stockMaster.getSellingPrice();
            this.product = stockMaster.getVariant().getProduct();
            this.expireDate = stockMaster.getExpireDate();
            this.variant = stockMaster.getVariant();
            this.mrp=stockMaster.getMrp();
            this.netAmount = sellingPrice.mutiply(quantity);
        }


        public int getPurchaseId() {
            return purchaseId;
        }

        public int getBatchId() {
            return batchId;
        }

        public void setBatchId(int batchId) {
            this.batchId = batchId;
        }

        public void setPurchaseId(int purchaseId) {
            this.purchaseId = purchaseId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public Money getSellingPrice() {
            return sellingPrice;
        }

        public void setSellingPrice(Money sellingPrice) {
            this.sellingPrice = sellingPrice;
        }

        public Money getMrp() {
            return mrp;
        }

        public void setMrp(Money mrp) {
            this.mrp = mrp;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public Variant getVariant() {
            return variant;
        }

        public void setVariant(Variant variant) {
            this.variant = variant;
        }

        public LocalDate getExpireDate() {
            return expireDate;
        }

        public void setExpireDate(LocalDate expireDate) {
            this.expireDate = expireDate;
        }

        public Money getNetAmount() {
            return netAmount;
        }

        public void setNetAmount(Money netAmount) {
            this.netAmount = netAmount;
        }

        @Override
        public String toString() {
            return  "Item:\n" +
                    "  Purchase ID : " + purchaseId + "\n" +
                    "  Variant     : " + product.getVariant() + "\n" +
                    "  Expiry      : " + expireDate + "\n" +
                    "  Qty         : " + quantity + "\n" +
                    "  Price       : " + sellingPrice+ "\n" +
                    "  MRP         : " + mrp  + "\n" +
                    "  Net Amount  : " + netAmount + "\n";
        }
    }
}
