package Commands.Orders;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public abstract class Order {
    private String user;
    private int orderId;
    private String gmt;
    
    public String getUser(){
        return this.user;
    }

    public void setUser(String user){
        this.user = user;
    }

    public int getOrderId(){
        return this.orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setGmt(ZonedDateTime gmt) {
        this.gmt = gmt.format(DateTimeFormatter.ISO_DATE_TIME);;
    }

    public String getGmt() {
        return gmt;
    }
    
    public abstract String getExchangeType();
    public abstract int getPrice();
    public abstract int getSize();
    
    
}
