public interface TaxStrategy {
    Money calulateGST(Money amount);
    String getGST();
}
