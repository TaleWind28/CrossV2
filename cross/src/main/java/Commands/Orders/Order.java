package Commands.Orders;

public abstract class Order {
    private String user;
    private int orderId;
    
    public String getUser(){
        return this.user;
    }

    public void setUser(String user){
        this.user = user;
    }

    public int getOrderID(){
        return this.orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public abstract String getExchangeType();
    public abstract int getPrice();
    public abstract int getSize();
    
    
}
