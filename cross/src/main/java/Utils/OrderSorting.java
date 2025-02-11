package Utils;

import java.time.ZonedDateTime;
import java.util.Comparator;

public class OrderSorting implements Comparable<OrderSorting>{
    private String timestamp;
    private int price;
    private int orderId;

    public OrderSorting(String timestamp, int price, int orderId){
        this.price = price;
        this.timestamp = timestamp;
        this.orderId = orderId;
    }

    @Override
    public int compareTo(OrderSorting ord) {
        //confronto i prezzi
        int cmp = Integer.compare(this.price, ord.price);
        //se sono diversi uso questo
        if(cmp != 0)return cmp;
        //altrimenti controllo le date
        ZonedDateTime t1 = ZonedDateTime.parse(this.timestamp);
        ZonedDateTime t2 = ZonedDateTime.parse(ord.timestamp);
        //ritorno il confronto sulle date
        return t1.compareTo(t2);        
    }
    public int getPrice() {
        return price;
    }
    public String getTimestamp() {
        return timestamp;
    }
    @Override
    public String toString() {
        // Converte l'oggetto OrderSorting in una rappresentazione stringa, per esempio
        return "Price: " + this.price + ", Timestamp: " + this.timestamp+ ", OrderId: " +this.orderId;
    }
    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public static final Comparator<OrderSorting> PRICE_DESCENDING = Comparator
            .comparing(OrderSorting::getPrice, Comparator.reverseOrder()) // Prezzo decrescente
            .thenComparing(o -> ZonedDateTime.parse(o.getTimestamp()));  // Timestamp crescente

    public static final Comparator<OrderSorting> PRICE_ASCENDING = Comparator
            .comparing(OrderSorting::getPrice) // Prezzo crescente
            .thenComparing(o -> ZonedDateTime.parse(o.getTimestamp()));  // Timestamp crescente


}