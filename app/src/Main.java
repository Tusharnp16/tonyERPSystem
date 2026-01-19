import java.time.LocalDate;
import java.util.Scanner;

class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static StoreInventory storeInventory = new StoreInventory();

    public static void createProduct() throws Exception {
        sc.nextLine();
        System.out.println("Enter Product Name: ");
        String prd = sc.nextLine();

        if (prd.isBlank()) {
            System.out.println("Please specify product name");
            return;
        }

        Product newPrd = new Product(prd);
        ProductRepository.save(newPrd);

        System.out.println("Product Added: " + newPrd);

        System.out.println("Create variant of this product? (Y/N)");
        if (sc.next().equalsIgnoreCase("Y")) {
            createVariant(newPrd);
        }
    }

    public static void createVariant(Product product) throws Exception {
        sc.nextLine();

        Product selectedProduct = product;

        if (product == null) {
            ProductRepository.display();
            System.out.println("Select Product Id: ");
            int id = safeInt();
            selectedProduct = ProductRepository.findById(id);

            if (selectedProduct == null) {
                System.out.println("Product not found.");
                return;
            }
        }

        System.out.println("Enter Product Colour: ");
        String color = sc.nextLine();

        System.out.println("Enter Product Size: ");
        String size = sc.nextLine();

        Variant newVariant = new Variant(color, size, selectedProduct);
        VariantRepository.save(newVariant);

        System.out.println("Variant Added: " + newVariant);

        System.out.println("Create stock for this variant? (Y/N)");
        if (sc.next().equalsIgnoreCase("Y")) {
            createStock(newVariant);
        }
    }

    public static void createStock(Variant variant) throws Exception {
        sc.nextLine();

        Variant selectedVariant = variant;

        if (selectedVariant == null) {
            VariantRepository.display();
            System.out.println("Select Variant Id: ");
            int id = safeInt();
            selectedVariant = VariantRepository.findById(id);

            if (selectedVariant == null) {
                System.out.println("Variant not found.");
                return;
            }
        }

        System.out.println("Enter Quantity: ");
        int qty = sc.nextInt();
        sc.nextLine();

        LocalDate exDate = LocalDate.now().plusDays(15);

        System.out.println("Enter MRP: ");
        double mrp = sc.nextDouble();

        System.out.println("Enter Selling Price: ");
        double sellingPrice = sc.nextDouble();

        if (sellingPrice > mrp) {
            System.out.println("Selling price cannot exceed MRP.");
            return;
        }

        StockMaster stock = new StockMaster(qty, exDate, selectedVariant, new Money(mrp), new Money(sellingPrice));
        StockRepository.save(stock);
        storeInventory.addStock(stock);

        System.out.println("Stock batch created: " + stock);
    }

    public static void createSupplier() throws Exception {
        sc.nextLine();

        System.out.println("Enter Supplier Name: ");
        String name = sc.nextLine();

        System.out.println("Enter Supplier State: ");
        String state = sc.nextLine();

        System.out.println("Enter Supplier contact number: ");
        String number = sc.nextLine();

        Supplier supplier = new Supplier(name, number,state);
        SupplierRepository.save(supplier);

        System.out.println("Supplier Added: " + supplier);
    }

    public static void createBill() throws Exception {
        sc.nextLine();

        SupplierRepository.display();
        System.out.println("Select Supplier ID: ");
        int supplierId = safeInt();

        Supplier supplier = SupplierRepository.findById(supplierId);

        if (supplier == null) {
            System.out.println("Supplier does not exist.");
            return;
        }


        String supplierState= supplier.getState();

        TaxStrategy tax = (supplierState.equalsIgnoreCase("GUJARAT") || supplierState.equalsIgnoreCase("GJ")) ? new CGSTStrategy() : new IGSTStrategy();

        Bill bill = new Bill(supplier, tax);
        BillRepository.save(bill);

        System.out.println("Bill Created: " + bill.getBillId());

        addItemToBill(bill);

    }

    public static void addItemToBill(Bill bill) throws Exception {

        System.out.println("DEBUG — Bill ID before saving item: " + bill.getBillId());


        StockRepository.display();

        System.out.println("Enter Variant ID: ");
        int variantId = sc.nextInt();

        System.out.println("Enter Quantity: ");
        int quantity = sc.nextInt();

        boolean ok = storeInventory.deductStockFEFO(variantId, quantity);
        if (!ok) {
            System.out.println("Insufficient stock.");
            return;
        }

        StockMaster batch = storeInventory.getFirstBatch(variantId);
        StockMaster fullBatch = StockRepository.findById(batch.getBatchId());

        bill.addItem(fullBatch, quantity);


        Bill.PurchaseItem item = bill.getPurchaseItemList().get(bill.getPurchaseItemList().size() - 1);


        BillItemRepository.save(bill.getBillId(), item);
        bill.recalculateTotals();
        BillRepository.updateTotals(bill);
        bill.display();
        System.out.println("Item added to bill.");
        Bill finalBill=BillRepository.findById(bill.getBillId());
        System.out.println(finalBill.display());

    }

    public static void showBill() throws Exception {
        sc.nextLine();

        BillRepository.display();
    }

    public static void displayAll() {
        System.out.println("1. Products");
        System.out.println("2. Variants");
        System.out.println("3. Stock");
        System.out.println("4. Suppliers");
        System.out.println("5. Bills");
        System.out.println("6. Bill Items");
        System.out.println("7. Inventory");

        System.out.println("Enter Choice: ");
        int ch = sc.nextInt();

        switch (ch) {
            case 1 -> ProductRepository.display();
            case 2 -> VariantRepository.display();
            case 3 -> StockRepository.display();
            case 4 -> SupplierRepository.display();
            case 5 -> BillRepository.display();
            case 6 -> BillItemRepository.display();
            case 7 -> storeInventory.printInventory();
        }
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


    public static void main(String[] args) throws Exception {

        DatabaseIntiallizer.intiallizeTable();

        while (true) {
            System.out.println("\nTony ERP System");
            System.out.println("1. Create Product");
            System.out.println("2. Create Variant");
            System.out.println("3. Create Stock Batch");
            System.out.println("4. Create Supplier");
            System.out.println("5. Create Bill");
            System.out.println("6. Display All");
            System.out.println("7. Exit");
            System.out.print("Choose: ");

            int ch = sc.nextInt();

            switch (ch) {
                case 1 -> createProduct();
                case 2 -> createVariant(null);
                case 3 -> createStock(null);
                case 4 -> createSupplier();
                case 5 -> createBill();
                case 6 -> displayAll();
                case 7 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
