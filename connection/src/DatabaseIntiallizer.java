import java.sql.Connection;
import java.sql.Statement;

public final class DatabaseIntiallizer {

    private DatabaseIntiallizer(){};

    public static void intiallizeTable(){
        createProductTable();
        createVariantTable();
        createStockMasterTable();
        createBillTable();
    }

    private static void createBillTable() {
        String sql= """
                CREATE TABLE IF NOT EXISTS bills (
                    bill_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    gst_type VARCHAR(10) NOT NULL,      -- IGST or CGST                  
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
