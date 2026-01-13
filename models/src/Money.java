import java.math.BigDecimal;
import java.math.RoundingMode;

public class Money {

    private final BigDecimal price;

    public Money(double prices){
//        if(prices<=0){
//            throw new IllegalArgumentException("Price cant be less than 0");
//        }
        this.price=BigDecimal.valueOf(prices).setScale(2, RoundingMode.HALF_UP);
    }


    public BigDecimal toBigDecimal(){
        return price;
    }

    public Money(BigDecimal prices){
//        if(prices==null){
//            throw new IllegalArgumentException("Price cant be less than 0");
//        }
        this.price=prices.setScale(2, RoundingMode.HALF_UP);
    }


    public Money add(Money amount){
        return new Money(this.price.add(amount.price));
    }

    public Money substract(Money amount){
        return new Money(this.price.subtract(amount.price));
    }

    public Money mutiply(int qty){
        return new Money(this.price.multiply(BigDecimal.valueOf(qty)));
    }

    public Double getPrice() {
        return price.doubleValue();
    }

    @Override
    public String toString() {
        return " " + price;
    }
}
