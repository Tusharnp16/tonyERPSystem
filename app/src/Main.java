import org.jetbrains.annotations.Nullable;
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

        System.out.println("Enter Supplier contact number: ");
        String number = sc.nextLine();

        Supplier supplier = new Supplier(name, number);
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

            System.out.println("Select GST: 1. IGST  |  2. CGST");
            int g = sc.nextInt();

            TaxStrategy tax = (g == 1) ? new IGSTStrategy() : new CGSTStrategy();

            Bill bill = new Bill(supplier, tax);
            BillRepository.save(bill);

            System.out.println("Bill Created: " + bill);

            addItemToBill(bill);

    }

    public static void addItemToBill(Bill bill) throws Exception {
        sc.nextLine();

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
        if (batch == null) {
            System.out.println("No batch available.");
            return;
        }

        StockMaster fullBatch = StockRepository.findById(batch.getBatchId());

        // Convert Item
        Bill.PurchaseItem purchaseItem = convertToPurchaseItem(fullBatch, quantity);

        // Save in object model
        bill.addItem(fullBatch);

        // Save in DB
        BillItemRepository.save(bill.getBillId(), purchaseItem);

        System.out.println("Item added to bill.");
        showBill();
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
        System.out.println("6. Inventory");

        System.out.println("Enter Choice: ");
        int ch = sc.nextInt();

        switch (ch) {
            case 1 -> ProductRepository.display();
            case 2 -> VariantRepository.display();
            case 3 -> StockRepository.display();
            case 4 -> SupplierRepository.display();
            case 5 -> BillRepository.display();
            case 6 -> storeInventory.printInventory();
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

    private static Bill.PurchaseItem convertToPurchaseItem(StockMaster stock, int quantity) {

        // Call your required constructor
        Bill.PurchaseItem item = new Bill.PurchaseItem(stock);

        // Fill additional fields
        item.setQuantity(quantity);
        item.setSellingPrice(stock.getSellingPrice());
        item.setMrp(stock.getMrp());

        double total = stock.getSellingPrice().getPrice() * quantity;
        item.setNetAmount(new Money(total));

        return item;
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
