package Utils;

import java.time.ZonedDateTime;

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
        return "Price: " + this.price + ", Timestamp: " + this.timestamp;
    }
    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

}