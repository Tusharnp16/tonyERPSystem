//import java.math.BigDecimal;
//
//public class IGSTStrategy implements TaxStrategy {
//    private static final BigDecimal IGST =new BigDecimal("0.18");
//
//    @Override
//    public Money calulateGST(Money amount) {
//        BigDecimal tax=amount.toBigDecimal().multiply(IGST);
//        return new Money(tax);
//        //return new Money(amount.getPrice()*IGST.doubleValue());
//    }
//}
