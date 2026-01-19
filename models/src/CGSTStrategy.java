import java.math.BigDecimal;

public class CGSTStrategy implements TaxStrategy {

    private static final BigDecimal CGST = new BigDecimal("0.09");
    private static final BigDecimal SGST = new BigDecimal("0.09");
    private static final String appliedGST = "CGST SGST";

    @Override
    public Money calulateGST(Money amount) {
        BigDecimal taxRate = CGST.add(SGST);
        return new Money(amount.getPrice() * taxRate.doubleValue());
    }

    @Override
    public String getGST(){
        return appliedGST;
    }
}
