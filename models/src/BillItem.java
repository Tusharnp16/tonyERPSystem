//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//public class BillItem {
//
//    private String billNo;
//    private final LocalDate date;
//    private final List<Bill> sBill=new ArrayList<>();
//
//    public BillItem(String billNo) {
//        this.date = LocalDate.now();
//        this.billNo = billNo;
//    }
//
//    public void addBill(int qty,Variant varint){
//        sBill.add(new Bill(varint,qty));
//    }
//
//    public double total(){
//        double sum=0;
//
//        for(Bill bill:sBill){
//          sum+= bill.quantity*bill.variant.getPrice().getPrice();
//
//        }
//        return sum;
//    }
//
//    @Override
//    public String toString() {
//        return "BillItem{" +
//                "billNo='" + billNo + '\'' +
//                ", date=" + date +
//                ", Total =" + total() +
//                '}';
//    }
//
//    static class Bill{
//        Variant variant;
//        int quantity;
//
//        Bill(Variant variant,int quantity){
//            this.variant=variant;
//            this.quantity=quantity;
//        }
//    }
//}
