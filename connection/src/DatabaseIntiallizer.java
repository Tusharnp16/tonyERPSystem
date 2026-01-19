import java.sql.Connection;
import java.sql.Statement;

public final class DatabaseIntiallizer {

    private DatabaseIntiallizer(){};

    public static void intiallizeTable(){
        createProductTable();
        createVariantTable();
        createSupplier();
        createBillTable();
        createStockMasterTable();
        createBillItemTable();
        createInventory();
    }

    private static void createInventory() {

        String sql = """
                    CREATE TABLE IF NOT EXISTS inventory (
                            batch_id BIGINT PRIMARY KEY REFERENCES stock_master(batch_id) ON DELETE CASCADE,
                    variant_id BIGINT NOT NULL REFERENCES variants(variant_id),
                    expire_date DATE NOT NULL,
                    quantity INT NOT NULL CHECK (quantity >= 0)
                );
                
                """;
            try(Connection con=DBConnection.getConnection();
                 Statement stmt=con.createStatement()){
                stmt.execute(sql);
            }catch (Exception e){
                System.out.println(e);
            }
}




private static void createSupplier() {
String sql= """
                    CREATE TABLE IF NOT EXISTS suppliers (
                        supplier_id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 10001) PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        contact VARCHAR(20) NOT NULL,
                        state VARCHAR(20) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                        updated_at TIMESTAMP
                    );
                
                """;
        try(Connection con=DBConnection.getConnection();
            Statement stmt=con.createStatement()){
            stmt.execute(sql);
        }catch (Exception e){
            System.out.println(e);
        }
    }



    private static void createBillTable() {
        String sql= """
                CREATE TABLE IF NOT EXISTS bills (
                      bill_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                      supplier_id BIGINT NOT NULL REFERENCES suppliers(supplier_id) ON DELETE CASCADE,
                      gst_type VARCHAR(10) NOT NULL,           
                      total_amount NUMERIC(12,2) DEFAULT 0,
                      tax_amount NUMERIC(12,2) DEFAULT 0,
                      net_amount NUMERIC(12,2) DEFAULT 0,
                      created_at TIMESTAMP NOT NULL DEFAULT NOW()
                  );
                """;

        try(Connection con=DBConnection.getConnection();
            Statement stmt=con.createStatement()){
            stmt.execute(sql);
        }catch (Exception e){
            System.out.println(e);
        }
    }


    private static void createBillItemTable() {
        String sql= """
                CREATE TABLE IF NOT EXISTS bill_items (
                        bill_item_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        bill_id BIGINT NOT NULL REFERENCES bills(bill_id) ON DELETE CASCADE,
                        batch_id BIGINT NOT NULL REFERENCES stock_master(batch_id) ON DELETE RESTRICT,
                        quantity INT NOT NULL,
                        selling_price NUMERIC(12,2) NOT NULL,
                        mrp NUMERIC(12,2) NOT NULL,
                        tax_amount NUMERIC(12,2) DEFAULT 0,
                        line_total NUMERIC(12,2) DEFAULT 0,
                        created_at TIMESTAMP DEFAULT NOW()
                    );
                    
                """;

        try(Connection con=DBConnection.getConnection();
            Statement stmt=con.createStatement()){
            stmt.execute(sql);
        }catch (Exception e){
            System.out.println(e);
        }
    }



    private static void createStockMasterTable() {
        String sql =
                """
                CREATE TABLE IF NOT EXISTS stock_master (
                    batch_id BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 101) PRIMARY KEY,
                    variant_id BIGINT NOT NULL REFERENCES variants(variant_id) ON DELETE CASCADE,
                    quantity INT NOT NULL CHECK(quantity >= 0),
                    expire_date DATE NOT NULL,
                    mrp NUMERIC(10,2) NOT NULL,
                    selling_price NUMERIC(10,2) NOT NULL CHECK (selling_price <= mrp),
                    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                    updated_at TIMESTAMP
                );
                    """;

        try(Connection con=DBConnection.getConnection();
            Statement stmt=con.createStatement()){
            stmt.execute(sql);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private static void createVariantTable() {
        String sql =
                """
                  CREATE TABLE IF NOT EXISTS variants(
                        variant_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        product_id BIGINT NOT NULL REFERENCES products(product_id) ON DELETE CASCADE,
                        color VARCHAR(50) NOT NULL,
                        size VARCHAR(50) NOT NULL              
                  );
                        """;

        try(Connection con=DBConnection.getConnection();
            Statement stmt=con.createStatement()){
            stmt.execute(sql);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private static void createProductTable(){
        String sql = """
            CREATE TABLE IF NOT EXISTS products (
                          product_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          item_code VARCHAR(50) NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          modified_at TIMESTAMP
                      );
        """;

        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
