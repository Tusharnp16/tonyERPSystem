import org.jetbrains.annotations.Nullable;

import javax.lang.model.type.NullType;
import java.rmi.MarshalledObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Main {

    private static final Scanner sc = new Scanner(System.in);

    private static List<Product> productList = new ArrayList<>();
    private static List<Variant> variantList = new ArrayList<>();
    private static List<StockMaster> stockList = new ArrayList<>();
    private static List<Supplier> supplierList = new ArrayList<>();
    private static List<Bill> billList = new ArrayList<>();
    private static StoreInventory storeInventory = new StoreInventory();


    public static void createProduct() {
        sc.nextLine();
        System.out.println("Enter Product Name: ");
        String prd = sc.nextLine();

        if (prd.isBlank()) {
            System.out.println("Please specify product name");
            return;
        }

        Product newPrd = new Product(prd);
        productList.add(newPrd);

        System.out.println("Product Added : " + newPrd);

        System.out.println("Wants to create variant of it? (Y/N)");
        String s = sc.next();

        if (s.toUpperCase().equals("Y")) {
            createVariant(newPrd);
        }
    }

    public static void createVariant(Product prd) {
        sc.nextLine();

        if (productList.isEmpty()) {
            System.out.println("There is no product please add product first");
            return;
        }

        Product selectedProduct = prd;
        int enterId;

        if (selectedProduct == null) {
            System.out.println("Select ProductId for variant : ");
            for (Product p : productList) {
                System.out.println("Product Id : " + p.getProductId() + " Product Name : " + p.getProductName());
            }

            System.out.println("Enter Product Id : ");
            enterId = safeInt();

            selectedProduct=productList.stream()
                    .filter(p -> p.getProductId()==enterId)
                    .findFirst()
                    .orElse(null);

            if (selectedProduct == null) {
                System.out.println("No such product available.");
                return;
            }
        }

        System.out.println("Enter Product Colour: ");
        String color = sc.nextLine();

        System.out.println("Enter Product Size: ");
        String size = sc.nextLine();

        Variant newVariant = new Variant(color, size, selectedProduct);
        variantList.add(newVariant);

        System.out.println("New Variant Added : " + newVariant);

        System.out.println("Wants to create Stock of it? (Y/N)");
        String s = sc.next();

        if (s.toUpperCase().equals("Y")) {
            createStock(newVariant);
        }

    }

    public static void createStock(Variant variant) {
        sc.nextLine();

        if (variantList.isEmpty()) {
            System.out.println("There is no variant avilable please add variant first");
            return;
        }

        Variant selectedVariant = variant;
        int variantId;

        if(selectedVariant==null) {
            System.out.println("Select variant Id for Stock Generation : ");

            for (Variant v : variantList) {
                System.out.println(v);
            }

            variantId = safeInt();

            selectedVariant=variantList.stream()
                    .filter(v-> v.getVariantId()==variantId)
                    .findFirst()
                    .orElse(null);

            if(selectedVariant==null){
                System.out.println("Variant does not exitsts");
                return;
            }
        }

        System.out.println("Enter Product Quantity: ");
        int qty = sc.nextInt();

        sc.nextLine();

        LocalDate today = LocalDate.now();
        LocalDate exDate = today.plusDays(15);


        System.out.println("Enter Product MRP: ");
        Double mrp = sc.nextDouble();

        System.out.println("Enter Product Selling price: ");
        Double sellingPrice = sc.nextDouble();

        if (sellingPrice > mrp) {

            System.out.println("Selling price cant more than actual MRP");
            return;
        }

        Money newMrp = new Money(mrp);
        Money newSellingPrice = new Money(sellingPrice);

        try {
            StockMaster newStock = new StockMaster(qty, exDate, selectedVariant, newMrp, newSellingPrice);
            stockList.add(newStock);
            storeInventory.addStock(newStock);
            System.out.println("Inventory Updated : " + newStock);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void createBill() {
        sc.nextLine();

        //    Supplier supplier = supplierList.get(0);

        for (Supplier s : supplierList) {
            System.out.println(s);
        }

        System.out.println("Select Supplier : ");
        int index = safeInt() - 1;

        if (index < 0 || index >= supplierList.size()) {
            System.out.println("Invalid Supplier");
            return;
        }

        System.out.println("Select GST Method : ");
        System.out.println("1.IGST \n2.CGST ");
        int gst = sc.nextInt();

        TaxStrategy strategy;

        if (gst == 1) {
            strategy = new IGSTStrategy();
        } else {
            strategy = new CGSTStrategy();
        }

        Supplier s = supplierList.get(index);

        Bill bill = new Bill(s, strategy);
        billList.add(bill);

        addItemToBill();
    }

    public static void addItemToBill() {
        sc.nextLine();

        if (billList.isEmpty()) {
            System.out.println("Bill list is empty");
            return;
        }

        System.out.println("Available Bills");

        for (Bill b : billList) {
            System.out.println("Bill Id : " + b.getBillId() + " Supplier Name : " + b.getSupplierName() + " Supplier Contact : " + b.getSupplieContact());
        }

        System.out.print("Choose Bill ID: ");
        int bIndex = safeInt() - 1;

        if (bIndex < 0 || bIndex >= billList.size()) {
            System.out.println("Invalid Bill.");
            return;
        }

        Bill bill = billList.get(bIndex);

        System.out.println("Available Stock:");
        for (int i = 0; i < stockList.size(); i++) {
            System.out.println((i + 1) + ". " + stockList.get(i));
        }

        System.out.println("Enter Variant Id : ");
        int variantId = sc.nextInt();

        System.out.println("Enter Quantity : ");
        int quantity = sc.nextInt();

        addItemToBill(bill, variantId, quantity);
    }

    public static void addItemToBill(Bill bill, int variantId, int quantity) {

        try {
            boolean ans = storeInventory.deductStockFEFO(variantId, quantity);

            if (!ans) {
                System.out.println("Insufficent Stock");
                return;
            }

            StockMaster firstBatch = storeInventory.getFirstBatch(variantId);
            if (firstBatch == null) {
                System.out.println("No Stock avilable");
                return;
            }

            StockMaster finalBatch = new StockMaster(firstBatch.getQuantity(), firstBatch.getExpireDate(), firstBatch.getVariant(), firstBatch.getMrp(), firstBatch.getSellingPrice());

            bill.addItem(finalBatch);
            System.out.println("Bill Generated");
        } catch (Exception e) {
            System.out.println(e);
        }

        showBill();

    }

    public static void showBill() {
        sc.nextLine();

        if (billList.isEmpty()) {
            System.out.println("No bills found.");
            return;
        }

        System.out.println("Available Bills:");
        for (int i = 0; i < billList.size(); i++) {
            System.out.println((i + 1) + ". Bill " + billList.get(i).display());
        }

//        System.out.print("Choose Bill to Display: ");
//        int index = safeInt() - 1;
//
//        if (index < 0 || index >= billList.size()) {
//            System.out.println("Invalid selection.");
//            return;
//        }
//
//        System.out.println(billList.get(index).display());

    }

    public static void displayAll() {

        sc.nextLine();

        System.out.println("1.Product List");
        System.out.println("2.Variant List");
        System.out.println("3.Stock Master List");
        System.out.println("4.Bill List");
        System.out.println("3.Inventory List");

        System.out.println("Enter Choice : ");
        int ch = sc.nextInt();

        if (ch == 1) {
            if (productList.isEmpty()) {
                System.out.println("No Product Avilable");
            } else {
                for (Product p : productList) {
                    System.out.println(p);
                }
            }
        } else if (ch == 2) {
            if (variantList.isEmpty()) {
                System.out.println("No Variant Avilable");
            } else {
                for (Variant v : variantList) {
                    System.out.println(v);
                }
            }
        } else if (ch == 3) {
            if (productList.isEmpty()) {
                System.out.println("No Stock Avilable");
            } else {
                for (StockMaster s : stockList) {
                    System.out.println(s);
                }
            }
        } else if (ch == 4) {
            if (billList.isEmpty()) {
                System.out.println("No Bill Avilable");
            } else {
                for (Bill b : billList) {
                    System.out.println(b.display());
                }
            }
        } else if (ch == 5) {
            storeInventory.printInventory();
        }

    }

    public static void searchItems() {

        sc.nextLine();

        System.out.println("1.Product Find");
        System.out.println("2.Variant Find");
        System.out.println("3.Stock Find");

        System.out.println("Enter Choice : ");
        int ch = sc.nextInt();

        int id = safeInt();

        if (ch == 1) {
            try (Product p = findProductById(id);) {
                System.out.println(p);
            } catch (Exception e) {
                System.out.println("Product Does not Exists");
            }
        }
        if (ch == 2) {

            Variant v=findVariantById(id);
            if(v!=null){
                System.out.println(v);
            }else{
                System.out.println("Variant Does not exists");
            }

        }
        if (ch == 3) {
           StockMaster s=findStockById(id);
            if(s!=null){
                System.out.println(s);
            }else{
                System.out.println("Stock Does not exists");
            }
        }
    }

    private static Product findProductById(int id) throws Exception {
        for (Product p : productList) {
            if (p.getProductId() == id) {
                return p;
            }
        }
        throw new Exception("Product does not exists");
    }

    private static Variant findVariantById(int id){
        for (Variant v : variantList) {
            if (v.getVariantId() == id) {
                return v;
            }
        }
        return null;
    }

    @Nullable
    private static StockMaster findStockById(int id){
        for (StockMaster s : stockList) {
            if (s.getStockId() == id) {
                return s;
            }
        }
        return null;
    }

    public static int safeInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.print("Enter valid number: ");
            }
        }
    }

    public static void main(String[] args) {

        supplierList.add(new Supplier("Tony Textile", "7984569840"));
        supplierList.add(new Supplier("Hardik Raw Menu factoring Unit", "1234567890"));

        int ch;

        while (true) {
            System.out.println("Tony ERP System");
            System.out.println("1. Create Product");
            System.out.println("2. Create Variant");
            System.out.println("3. Create Stock Batch");
            System.out.println("4. Create Bill");
            System.out.println("5. Add Item to Bill");
            System.out.println("7. Display All");
            System.out.println("8. Search Item");
            System.out.println("9. Exit");
            System.out.print("Choose option: ");
            ch = sc.nextInt();
            switch (ch) {
                case 1 -> createProduct();
                case 2 -> createVariant(null);
                case 3 -> createStock(null);
                case 4 -> createBill();
                case 5 -> addItemToBill();
                case 6 -> showBill();
                case 7 -> displayAll();
                case 8 ->searchItems();

                case 9 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option.");

            }
        }

//        Product shirt = new Product("Linen");
//        Product shirt2 = new Product("Lycra");
//        System.out.println(shirt);
////
//      Variant blackShirt=new Variant("Black","M",shirt);
//        System.out.println(blackShirt);
//        Variant whiteShirt=new Variant("White","XL",shirt2);
//
//        StockMaster batch1=new StockMaster(50, "30-01-2026",blackShirt,new Money(50),new Money(20));
//        StockMaster batch2=new StockMaster(100, "30-01-2026",whiteShirt,new Money(50),new Money(20));
//
//
//        Supplier supplier=new Supplier("Tony Traders","7984569840");
//        Supplier supplier2=new Supplier("MItul Traders","7984569840");
//        supplier.addSuppliedBatch(batch1);
//        supplier.addSuppliedBatch(batch2);
//
//
//        Bill bill=new Bill(supplier);
//        bill.addItem(batch1);
//        bill.addItem(batch2);
//
//     //   Bill bill2=new Bill(supplier);
////
//        bill.display();
//        System.out.println(bill.display());


//            BillItem item=new BillItem("Tony-101");
////        item.addBill(2,blackShirt);
////        item.addBill(1,whiteShirt);
//
//        System.out.println("\nProduct");
////        System.out.println(shirt);
//
//        System.out.println("\nVariants");
////        System.out.println(blackShirt);
////        System.out.println(whiteShirt);
//
//       System.out.println("\nStock Batches");
////        System.out.println(batch1);
////        System.out.println(batch2);
//
//        System.out.println("\nBill");
//        System.out.println(item);


    }
}