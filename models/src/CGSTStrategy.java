//import java.math.BigDecimal;
//
//public class CGSTStrategy implements TaxStrategy {
//
//    private static final BigDecimal CGST = new BigDecimal("0.09");
//    private static final BigDecimal SGST = new BigDecimal("0.09");
//
//    @Override
//    public Money calulateGST(Money amount) {
//        BigDecimal taxRate = CGST.add(SGST);
//        return new Money(amount.getPrice() * taxRate.doubleValue());
//    }
//}
